package kr.co.nexmore.rfiddaemon.mes;

import java.util.*;
import java.util.concurrent.TimeUnit;

import kr.co.nexmore.rfiddaemon.reader.tcp.client.RFIDTcpClient;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.handler.RFIDClientMessageHandler;
import kr.co.nexmore.rfiddaemon.reader.util.TcpSetCommandUtil;
import kr.co.nexmore.rfiddaemon.vo.common.EventHistoryVO;
import kr.co.nexmore.rfiddaemon.vo.reader.ReaderVO;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.RFID_EIS_Change_Resource_Info_In;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.EIS_RFID_Request_In_Tag;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.RFID_EIS_Reply_Out_Tag;
import kr.co.nexmore.rfiddaemon.service.DBManageService;
import kr.co.nexmore.rfiddaemon.vo.mes.OneooneVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.TagDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static kr.co.nexmore.rfiddaemon.common.CommonControlCode.*;
import static kr.co.nexmore.rfiddaemon.reader.tcp.client.manager.ClientManager.rfidClientsMapByIp;

@Slf4j
@Component("oneooneManager")
public class OneooneManager {

    Hashtable<Integer, Oneoone> oneooneMap;

    private final DBManageService dbManageService;

    public OneooneManager(DBManageService dbManageService) {
        this.dbManageService = dbManageService;
        this.init();
    }

    public Hashtable<Integer, Oneoone> getOneooneMap() {
        return oneooneMap;
    }

    /**
     * initialize
     */
    public void init() {
        try {
            oneooneMap = new Hashtable<Integer, Oneoone>();
            List<OneooneVO> oneooneConnectionList = dbManageService.getOneooneConnectionList();
            if (oneooneConnectionList != null && oneooneConnectionList.size() > 0) {
                for (int i = 0; i < oneooneConnectionList.size(); i++) {
                    OneooneVO oneooneVO = oneooneConnectionList.get(i);
                    Oneoone oneoone = new Oneoone(oneooneVO, dbManageService, this);
                    oneooneMap.put(oneoone.getStationXId(), oneoone);
                }
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    public int getTimeout(int stationXId) {
        Oneoone oneoone = oneooneMap.get(stationXId);
        return oneoone.getMesTimeout();
    }

    /**
     * 101 정보 등록
     *
     * @param oneooneVO
     * @return Map
     * @throws Exception
     */
    public Map createOneoone(OneooneVO oneooneVO) throws Exception {

        Map response = new HashMap();
        response.put("success", false);
        response.put("message", null);
        String message = null;

        int result = dbManageService.createOneooneConnection(oneooneVO);

        if (result > 0) {
            int stationXId = dbManageService.getStationXId(oneooneVO);
            oneooneVO.setStationXId(stationXId);
            Oneoone oneoone = new Oneoone(oneooneVO, dbManageService, this);
            oneooneMap.put(oneoone.getStationXId(), oneoone);

            response.put("success", true);
            message = "CREATE SUCCESS";
        } else {
            message = "CREATE FAIL";
        }

        response.put("message", message);
        return response;
    }

    /**
     * 101 정보 업데이트
     *
     * @param oneooneVO
     * @return Map
     * @throws Exception
     */
    public Map updateOneoone(OneooneVO oneooneVO) throws Exception {

        Map response = new HashMap();
        response.put("success", false);
        response.put("message", null);
        String message = null;

        int result = dbManageService.updateOneooneConnection(oneooneVO);
        if (result > 0) {
            Oneoone oneoone = oneooneMap.get(oneooneVO.getStationXId());
            if (oneoone == null) {
                message = "존재하지 않는 MES(101) 연결 정보 입니다.";
            } else {
                oneoone.disconnect();
                oneoone.setOneooneVO(oneooneVO);
                oneoone.start();
                response.put("success", true);
            }
        } else {
            message = "UPDATE FAIL";
        }
        response.put("message", message);
        return response;
    }

    public Map updateTimeOut(OneooneVO requestVO) throws Exception {

        Map response = new HashMap();
        response.put("success", false);
        response.put("message", null);

        String message = null;

        int result = dbManageService.updateOneooneTimeout(requestVO);
        if (result > 0) {
            Oneoone oneoone = oneooneMap.get(requestVO.getStationXId());
            oneoone.setTimeout(requestVO.getMesTimeout());
            response.put("success", true);
        } else {
            message = "TIMEOUT UPDATE FAIL";
            response.put("message", message);
        }
        return response;
    }


    /**
     * 101 정보 삭제
     *
     * @param stationXId
     * @return Map
     * @throws Exception
     */
    public Map deleteOneoone(int stationXId) throws Exception {

        Map response = new HashMap();
        response.put("success", false);
        response.put("message", null);
        String message = null;

        Oneoone oneoone = oneooneMap.get(stationXId);
        if (oneoone == null) {
            message = "존재하지 않는 MES(101) 연결 정보 입니다.";
        } else {
            oneoone.disconnect();
            oneooneMap.remove(stationXId);
            int result = dbManageService.deleteOneooneConnection(stationXId);
            if (result > 0) {
                response.put("success", true);
                message = "DELETE SUCCESS";
            } else {
                message = "DELETE FAIL";
            }
        }

        response.put("message", message);

        return response;
    }


    public RFID_EIS_Reply_Out_Tag sendToEIS(int stationXID, EIS_RFID_Request_In_Tag requestInTag, String eventType) {
        Oneoone oneoone = oneooneMap.get(stationXID);
        if (oneoone != null) {
            return oneoone.sendToEIS(requestInTag, eventType);
        } else {
            String message = String.format("[M/W ERROR]  STATION X ID: %d 에 해당하는 CONNECTION 이 없습니다.", stationXID);
            log.error(message);
            RFID_EIS_Reply_Out_Tag replyOutTag = new RFID_EIS_Reply_Out_Tag();
            replyOutTag.h_status_value = MES_EVENT_FAIL;
            replyOutTag.h_msg = message;
            return replyOutTag;
        }
    }

    public RFID_EIS_Reply_Out_Tag sendToEIS(int stationXID, RFID_EIS_Change_Resource_Info_In requestInReader, String eventType) throws Exception {
        Oneoone oneoone = oneooneMap.get(stationXID);
        if (oneoone != null) {
            return oneoone.sendToEIS(requestInReader, eventType);
        } else {
            String message = String.format("[M/W ERROR]  STATION X ID: %d 에 해당하는 CONNECTION 이 없습니다.", stationXID);
            log.error(message);
            RFID_EIS_Reply_Out_Tag replyOutTag = new RFID_EIS_Reply_Out_Tag();
            replyOutTag.h_status_value = MES_EVENT_FAIL;
            replyOutTag.h_msg = message;
            return replyOutTag;
        }
    }


    /**
     * WIRE BOND 공정 설비 LOT END 처리를 위한 메서드
     * EIS로부터 REQUEST 를 받은 설비의 현재 상태를 LOT_END 로 변경 한다.
     *
     * @param requestTag
     * @return
     */
    public RFID_EIS_Reply_Out_Tag endLotId(EIS_RFID_Request_In_Tag requestTag) {
        log.info("[{}] 설비에 LOT END 명령을 보냅니다.", requestTag.res_id);
        RFIDTcpClient client = null;

        String readerIp = requestTag.reader_ip;
        if (readerIp == null || "".equals(readerIp)) {
            if (rfidClientsMapByIp != null && !rfidClientsMapByIp.isEmpty()) {
                for (String key : rfidClientsMapByIp.keySet()) {
                    if (rfidClientsMapByIp.get(key).getReaderVO().getEquipmentName().toUpperCase().equals(requestTag.res_id)) {
                        client = rfidClientsMapByIp.get(key);
                    }
                }
            }

            if (client == null || !client.isActived()) {
                log.info("[IP: {}] 를 사용하는 리더기는 등록되지 않았거나 연결되어 있지 않습니다.", requestTag.reader_ip);
                RFID_EIS_Reply_Out_Tag replyOutTag = new RFID_EIS_Reply_Out_Tag();
                replyOutTag.h_status_value = '3';
                replyOutTag.antenna_port_1 = requestTag.antenna_port_1;
                replyOutTag.reader_ip = requestTag.reader_ip;
                return replyOutTag;
            }
        } else {
            client = rfidClientsMapByIp.get(readerIp);
        }

        ReaderVO reader = client.getReaderVO();
        log.debug("EIS LOT END REQUEST VALUE: {}", requestTag.toString());
        log.info("[END LOT_ID] READER IP: {}  RES_ID : {}", reader.getReaderIp(), requestTag.res_id);
        log.info("------- ANTENNA1 UID: {} MAGAZINE_ID : {} LOT_ID : {}", requestTag.uid_1, requestTag.magazine_id_1);
        log.info("------- ANTENNA2 UID: {} MAGAZINE_ID : {} LOT_ID : {}", requestTag.uid_2, requestTag.magazine_id_2);
        if (reader.getChannelCnt() == 4) {
            log.info("------- ANTENNA3 UID: {} MAGAZINE_ID : {} LOT_ID : {}", requestTag.uid_3, requestTag.magazine_id_3);
            log.info("------- ANTENNA4 UID: {} MAGAZINE_ID : {} LOT_ID : {}", requestTag.uid_4, requestTag.magazine_id_4);
        }
        log.info("");

        RFID_EIS_Reply_Out_Tag replyOutTag = new RFID_EIS_Reply_Out_Tag(requestTag);

        if (reader.getProcessName().toUpperCase().equals("WIRE BOND")) {
            EventHistoryVO eventHistoryVO = new EventHistoryVO(reader, MAGAZINE_EJECT, requestTag, EVENT_RESPONSE_WAITING);
            log.debug("LOT END EVENT HISTORY PARAMETER: {}", eventHistoryVO.toString());
            int eventLogId = 0;
            try {
                int r = dbManageService.insertMesEventHistory(eventHistoryVO);
                if (r > 0) {
                    eventLogId = eventHistoryVO.getEventLogId();
                }
                if (eventLogId > 0) {
                    RFIDClientMessageHandler handler = client.getMessageHandler();
                    replyOutTag = handler.setLotStatusEnd(requestTag);

                    EventHistoryVO updateHistoryVO = new EventHistoryVO(eventLogId, reader.getReaderName(), requestTag, TAG_EVENT_SUCCESS);
//                    int updateResult = dbManageService.updateEventHistory(updateHistoryVO);
                    dbManageService.updateEventHistory(updateHistoryVO);
                }
            } catch (Exception e) {
                log.error(e.toString(), e);
            }
        }
        return replyOutTag;
    }

    //어댑터에 Write 명령을 보냄
    // TODO 기존 소스는 어댑터를 여러개 분산하여 관리하였기 때문에 어댑터를 찾아 어댑터가 연결하고있는 리더기를 찾아야 했음.
    // TODO 이부분 제거하고 netty 에서 해당 ip를 연결한 channel 을 찾아 write 명령하도록 변경 해야함.
    public RFID_EIS_Reply_Out_Tag writeLotId(EIS_RFID_Request_In_Tag requestTag) {
        log.info("[{}({})]의 리더기에 WRITE 명령을 보냅니다.", requestTag.res_id, requestTag.reader_ip);

        String readerIp = requestTag.reader_ip;
        RFIDTcpClient client = rfidClientsMapByIp.get(readerIp);
        log.debug(client.toString());
        log.debug(client.getReaderVO().toString());

        if (client == null) {
            log.info("[IP: {}] 를 사용하는 리더기는 등록되지 않았거나 연결되어 있지 않습니다.", requestTag.reader_ip);
            RFID_EIS_Reply_Out_Tag eisResponse = new RFID_EIS_Reply_Out_Tag();
            eisResponse.h_status_value = '3';
            eisResponse.antenna_port_1 = requestTag.antenna_port_1;
            eisResponse.reader_ip = requestTag.reader_ip;
            return eisResponse;
        }

        log.info("[WRITE LOT_ID] IP: {} RES_ID : {} EVENT_CHANNEL : {}", requestTag.reader_ip, requestTag.res_id, (int) requestTag.antenna_port_1);
        log.info("------- ANTENNA1 UID: {} MAGAZINE_ID : {} LOT_ID : {}", requestTag.uid_1, requestTag.magazine_id_1, requestTag.lot_id_1);

        try {
            ReaderVO reader = client.getReaderVO();
            int channel = (requestTag.antenna_port_1 - 48);
            EventHistoryVO eventHistoryVO = new EventHistoryVO(reader, WRITE_LOT_ID, requestTag, EVENT_RESPONSE_WAITING);
            eventHistoryVO.setAnt1LotId(requestTag.lot_id_1);

            int eventLogId = 0;

            int r = dbManageService.insertMesEventHistory(eventHistoryVO);
            if (r > 0) {
                eventLogId = eventHistoryVO.getEventLogId();
            }


/*            int r = dbManageService.insertEventHistory(eventHistoryVO);
            if (r > 0) {
                Map map = dbManageService.getLastInsertId(eventHistoryVO);
                if (map != null && !map.isEmpty()) {
                    eventLogId = Integer.parseInt(map.get("eventLogId").toString());
                }
            }*/

            client.sendRequest(TcpSetCommandUtil.setMode(1, reader.getReaderInterval(), reader.getReaderIp())); // trigger mode 로 변경
            // write 명령
            client.sendRequest(TcpSetCommandUtil.setWriteLotId(((requestTag.antenna_port_1 - 48) - 1), requestTag.lot_id_1, client.getReaderVO().getReaderMac()));
            Map result = client.getMessageHandler().getBlockingQueue().poll(1000, TimeUnit.MILLISECONDS);
            // trigger mode -> continue mode 로 다시 변경
            client.sendRequest(TcpSetCommandUtil.setMode(0, reader.getReaderInterval(), reader.getReaderIp()));

            RFID_EIS_Reply_Out_Tag replyOutTag = new RFID_EIS_Reply_Out_Tag();
            replyOutTag.res_id = reader.getEquipmentName();
            replyOutTag.reader_ip = reader.getReaderIp();
            replyOutTag.antenna_port_1 = requestTag.antenna_port_1;
            replyOutTag.uid_1 = requestTag.uid_1;
            replyOutTag.magazine_id_1 = requestTag.magazine_id_1;
            replyOutTag.lot_id_1 = requestTag.lot_id_1;
//            replyOutTag.h_status_value = '1';
            replyOutTag.h_status_value = MES_EVENT_FAIL;
            eventHistoryVO.setRetVal(replyOutTag.h_status_value - 48);

            if (result != null && !result.isEmpty()) {
                log.debug("result :{}", result.toString());
                if (result.get("success") != null && (boolean) result.get("success")) {
                    replyOutTag.h_status_value = (char) MES_EVENT_SUCCESS;
                    eventHistoryVO.setRetVal(replyOutTag.h_status_value - 48);
                    client.getMessageHandler().setLotId(replyOutTag.lot_id_1, channel);
                }
            }

            eventHistoryVO.setEventLogId(eventLogId);
            dbManageService.updateEventHistory(eventHistoryVO);

            log.info("[WRITE LOTID RESPONSE] RESULT:" + replyOutTag.h_status_value + " IP:" + replyOutTag.reader_ip
                    + " RESID:" + replyOutTag.res_id + " EVENT_CHANNEL:" + replyOutTag.antenna_port_1 + " LOTID:" + replyOutTag.lot_id_1);
            log.info("[WRITE LOT ID RESPONSE] RESULT: {}  IP: {}  RES ID: {}  CHANNEL: {}  LOT ID: {}", replyOutTag.h_status_value, replyOutTag.reader_ip
                    , replyOutTag.res_id, replyOutTag.antenna_port_1, replyOutTag.lot_id_1);
            return replyOutTag;

        } catch (Exception e) {
            log.error(e.toString(), e);
            RFID_EIS_Reply_Out_Tag replyOutTag = new RFID_EIS_Reply_Out_Tag();
            replyOutTag.h_status_value = '3';
            replyOutTag.antenna_port_1 = requestTag.antenna_port_1;
            replyOutTag.reader_ip = requestTag.reader_ip;
            return replyOutTag;
        }
    }
}
