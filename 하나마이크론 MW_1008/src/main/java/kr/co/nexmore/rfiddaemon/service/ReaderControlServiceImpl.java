package kr.co.nexmore.rfiddaemon.service;

import kr.co.nexmore.rfiddaemon.reader.tcp.client.RFIDTcpClient;
import kr.co.nexmore.rfiddaemon.reader.udp.RFIDUdpBroadcaster;
import kr.co.nexmore.rfiddaemon.reader.util.TcpSetCommandUtil;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.request.RequestHeaderVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.request.RequestVO;
import kr.co.nexmore.rfiddaemon.vo.reader.udp.UdpRequestVO;
import kr.co.nexmore.rfiddaemon.vo.reader.udp.UdpResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static kr.co.nexmore.rfiddaemon.common.PacketConstant.*;

@Slf4j
@Service("readerControlService")
public class ReaderControlServiceImpl implements ReaderControlService {

    private final RFIDUdpBroadcaster rfidUdpBroadcaster;

    ReaderControlServiceImpl(RFIDUdpBroadcaster rfidUdpBroadcaster) {
        this.rfidUdpBroadcaster = rfidUdpBroadcaster;
    }

    public LinkedHashMap sendTcpCommand(RequestVO requestVO, RFIDTcpClient client) throws InterruptedException {
        return this.sendTcpCommand(requestVO, client, true);
    }

    public synchronized LinkedHashMap sendTcpCommand(RequestVO requestVO, RFIDTcpClient client, boolean save) throws InterruptedException {
        LinkedHashMap result = null;
        boolean isSave = false;
        RequestHeaderVO header = requestVO.getHeaderVO();

        result = new LinkedHashMap();
        result.put("readerMac", header.getReaderMac());

        if (!client.isActived()) {
            result.put("success", false);
        } else {
            if(client.getMessageHandler().getBlockingQueue().size() > 0) {
                client.getMessageHandler().getBlockingQueue().clear();
            }
            client.sendRequest(requestVO);
            Map response = client.getMessageHandler().getBlockingQueue().poll(1000, TimeUnit.MILLISECONDS);
            if (response == null) {
                result.put("success", false);
            } else {
                log.debug(response.toString());
                result.putAll(response);
                client.getMessageHandler().getBlockingQueue().clear();
            }
        }
        if (save) {
            if ((boolean) result.get("success")) {
                isSave = saveRegister(header.getReaderMac(), client);
            }
            result.put("isSave", isSave);
        }


//            log.debug("BROADCAST");
//            log.debug(String.valueOf(clientMap.size()));
//            for (String key : clientMap.keySet()) {
//                result = new LinkedHashMap();
//                requestVO.getHeaderVO().setReaderMac(key);
//                result.put("readerMac", requestVO.getHeaderVO().getReaderMac());
//                if (!clientMap.get(requestVO.getHeaderVO().getReaderMac()).isActived()) {
//                    result.put("success", false);
//                } else {
//                    Map response = clientMap.get(requestVO.getHeaderVO().getReaderMac()).sendRequest(requestVO);
//                    if (response == null) {
//                        result.put("success", false);
//                    } else {
//                        result.putAll(response);
//                    }
//                }
//                if ((boolean) result.get("success") && save) {
//                    isSave = saveRegister(header.getReaderMac(), clientMap);
//                }
//                result.put("isSave", isSave);
//                list.add(result);
//            }
//        }
        return result;
    }

    public boolean saveRegister(String readerMac, RFIDTcpClient client) throws InterruptedException {
        log.info("RFIDReader saveRegister Start!!!");
        boolean result = false;
        Thread.sleep(200);
        if(client.getMessageHandler().getBlockingQueue().size() > 0) {
            client.getMessageHandler().getBlockingQueue().clear();
        }
        client.sendRequest(TcpSetCommandUtil.setSaveRegistry(readerMac));
        Map response = client.getMessageHandler().getBlockingQueue().poll(1000, TimeUnit.MILLISECONDS);
        if (response != null && !response.isEmpty()) {
            if (response.get("success") != null) {
                result = (boolean) response.get("success");
            }
            client.getMessageHandler().getBlockingQueue().clear();
        }
        return result;
    }

    public UdpResponseVO searchReader(String readerIp) {
        UdpRequestVO udpSearchReqVO = UdpRequestVO.setSearchCommand(readerIp);
        UdpResponseVO searchResponse = rfidUdpBroadcaster.sendUdpCommand(udpSearchReqVO);
        return searchResponse;
    }

    public UdpResponseVO setReaderIp(String readerMac, String readerIp, String changeIp, String gateway, UdpResponseVO searchResponse) {
        log.debug("READER IP 변경 START");
        UdpRequestVO requestVO = UdpRequestVO.setChangeNetworkCommand(readerMac, readerIp, changeIp, gateway, searchResponse.getResponseBuf());
        UdpResponseVO setNetworkResponse = rfidUdpBroadcaster.sendUdpCommand(requestVO);
        return setNetworkResponse;
    }


    public LinkedHashMap firmwareUpdate(File file, String readerMac, String readerIp) {
        LinkedHashMap response = new LinkedHashMap();
        response.put("readerMac", readerMac);
        UdpResponseVO readyMacVO = downloadReady(UDP_FIRMWARE_READY_REQUEST_COM, readerMac, readerIp);
        if (readyMacVO == null) {
            response.put("ready", "FAIL");
        } else {
            response.put("ready", "OK");
            UdpResponseVO responseVO = updatePacketSend(UDP_FIRMWARE_DOWNLOAD_REQUEST_COM, file, readerIp);
            if (responseVO.getCount() == (byte) (Math.ceil((double) file.length() / 224) + 1)) {
                response.put("update", "SUCCESS");
                response.put("count", responseVO.getCount() & 0xff);
            }
        }
/*
        response.put("ready", "OK");
        UdpResponseVO responseVO = updatePacketSend(UDP_FIRMWARE_DOWNLOAD_REQUEST_COM, file, readerIp);
        if (responseVO.getCount() == (byte) (Math.ceil((double) file.length() / 224) + 1)) {
            response.put("update", "SUCCESS");
            response.put("count", responseVO.getCount() & 0xff);
        }*/
        return response;
    }

    public UdpResponseVO downloadReady(byte command, String readerMac, String readerIp) {

        byte readerCount = (byte) (2 + 15); // F/W 다운로딩을 수행 할 리더기 개수(x2) -> 한대만 하려면 2+15,두대를 동시에 진행하려면 4+15
        StringBuffer buffer = new StringBuffer();
        buffer.append(String.join("", readerMac.split(":")));

        UdpRequestVO requestVO = UdpRequestVO.setDownloadReadyCommand(command, readerCount, buffer.toString(), readerIp);
        UdpResponseVO responseVO = rfidUdpBroadcaster.sendUdpCommand(requestVO);

        if (responseVO == null) {
            int retryCnt = 0;
            while (retryCnt != 2) {
                responseVO = rfidUdpBroadcaster.sendUdpCommand(requestVO);
                if (responseVO != null) retryCnt = 2;
                else retryCnt++;
            }
        }
        return responseVO;
    }

    public UdpResponseVO updatePacketSend(byte command, File file, String readerIp) {

        UdpResponseVO responseVO = null;
        long fileSize = file.length();
        byte totalCount = (byte) (Math.ceil((double) fileSize / 224) + 1);
        byte currnetCount = 0;

        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);
            if (command == UDP_CORE_DOWNLOAD_REQUEST_COM) {
                fis.skip(0xD000);
                totalCount = (byte) (Math.ceil((double) fis.available() / 224) + 1);
            }

            log.debug("[Firmware update] File Size: {}, totalCount: {}", fileSize, totalCount & 0xff);
            byte tmp = (byte) 0xff;

            while (totalCount + 1 != currnetCount) {
                UdpRequestVO requestVO = UdpRequestVO.setFirmwarePacketCommand(command, totalCount, currnetCount, readerIp);
                log.debug("totalCount : {} , currentCount : {}", totalCount & 0xff, currnetCount & 0xff);

                try {
                    byte[] binaryPackets = new byte[224];
                    int dataLength = fis.available();

                    if (dataLength >= binaryPackets.length) {
                        fis.read(binaryPackets);
                    } else if (dataLength < binaryPackets.length) {
                        if (dataLength > 0) {
                            fis.read(binaryPackets);
                        }
                        if (command == UDP_FIRMWARE_DOWNLOAD_REQUEST_COM) {
                            for (int i = 0; i < binaryPackets.length - dataLength; i++) {
                                binaryPackets[dataLength + i] = tmp;
                                tmp++;
                            }
                        } else if (command == UDP_CORE_DOWNLOAD_REQUEST_COM) {
                            for (int i = 0; i < binaryPackets.length - dataLength; i++) {
                                binaryPackets[dataLength + i] = 0x00;
                            }
                        }
                    }
                    requestVO.setFirmwarePackets(binaryPackets);
                } catch (Exception e) {
                    log.error(e.toString(), e);
                }

                responseVO = rfidUdpBroadcaster.sendUdpCommand(requestVO);

                int retryCnt = 0;

                if (responseVO == null) {
                    while (retryCnt != 2) {
                        responseVO = rfidUdpBroadcaster.sendUdpCommand(requestVO);
                        if (responseVO != null) retryCnt = 2;
                        else retryCnt++;
                    }
                }
                currnetCount++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    log.error(e.toString(), e);
                }
            }
        }
        return responseVO;
    }


    /////// ddd 파일 업데이트 시 필요. 현재 사용 안함.
/*
    public void coreUpdate(File binFile, File dddFile, File firmwareFile, String readerMac, String readerIp) {
        try {
            // ddd 영역 수정 가능하도록 하는 bin firmware 다운로드
            searchReader(readerIp);
            log.debug("downloader 영역 수정 가능하도록 bin 파일 다운로드");
            downloadReady(UDP_FIRMWARE_READY_REQUEST_COM, readerMac, readerIp);
            updatePacketSend(UDP_FIRMWARE_DOWNLOAD_REQUEST_COM, binFile, readerIp);
            // ddd 파일 다운로드
//                Thread.sleep(3000);
            searchReader(readerIp);
            log.debug("DDD 파일 다운로드");
            downloadReady(UDP_CORE_READY_REQUEST_COM, readerMac, readerIp);
            updatePacketSend(UDP_CORE_DOWNLOAD_REQUEST_COM, dddFile, readerIp);
            // 최종 펌웨어 파일 다운로드
//                Thread.sleep(3000);
            searchReader(readerIp);
            log.debug("FIRMWARE 파일 다운로드");
            downloadReady(UDP_FIRMWARE_READY_REQUEST_COM, readerMac, readerIp);
            updatePacketSend(UDP_FIRMWARE_DOWNLOAD_REQUEST_COM, firmwareFile, readerIp);

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }
    */

}

