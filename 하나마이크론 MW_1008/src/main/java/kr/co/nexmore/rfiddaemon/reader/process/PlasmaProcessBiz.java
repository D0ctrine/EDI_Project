package kr.co.nexmore.rfiddaemon.reader.process;

import ch.qos.logback.core.net.server.Client;
import kr.co.nexmore.rfiddaemon.mes.OneooneManager;
import kr.co.nexmore.rfiddaemon.service.DBManageService;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.RFIDTcpClient;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.manager.ClientLogManager;
import kr.co.nexmore.rfiddaemon.vo.reader.ReaderVO;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.RFID_EIS_Reply_Out_Tag;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.TagDataVO;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static kr.co.nexmore.rfiddaemon.common.CommonControlCode.*;
import static kr.co.nexmore.rfiddaemon.common.CommonControlCode.LOT_END;
import static kr.co.nexmore.rfiddaemon.common.CommonControlCode.LOT_START;

@Slf4j
public class PlasmaProcessBiz extends CommonProcessBiz {

    private boolean isPaired = false;

    private RFIDTcpClient pairClient;
    private ReaderVO pairReader;

    private boolean rail1IsLotStarted;  // LOT_START = true
    private boolean rail2IsLotStarted;

    private boolean rail3IsLotStarted;
    private boolean rail4IsLotStarted;

    private String rail1InProcessLotId; // LOT_ID
    private String rail2InProcessLotId;

    private String rail3InProcessLotId;
    private String rail4InProcessLotId;

    /**
     * 설비 별 로그를 위한 logHeader
     * ex) [EX001] EX001-2C-01(xxx.xxx.xxx.xxx)
     */
    private String pairLogHeaderMessage;

    public PlasmaProcessBiz(DBManageService dbManageService, OneooneManager oneooneManager, ClientLogManager clientLogger, RFIDTcpClient client) {
        super(dbManageService, oneooneManager, clientLogger, client);
        writeLog(this.toString());
    }

    public boolean getPaired() {
        return isPaired;
    }

    public void pairRelease(String readerMac) {
        isPaired = false;
        if (readerMac.equals(this.reader.getReaderMac())) {
            this.reader = this.pairReader;
            this.client = this.pairClient;
            this.logHeaderMessage = this.pairLogHeaderMessage;

            this.pairClient = null;
            this.pairReader = null;
        }
        writeLog("[Pairing Released] 페어링이 해제 되었습니다.");
    }

    @Override
    public synchronized void lotInitialize(RFIDTcpClient client) {
        writeLog(client, "LOT STATUS INITIALIZED :: START");
        ReaderVO reader = client.getReaderVO();
        try {
            LinkedHashMap<String, String> map = dbManageService.getLastLotStatus(reader.getReaderMac());

            String ch1LotStatus = map.get("ch1Status");
            String ch2LotStatus = map.get("ch2Status");
            String ch3LotStatus = map.get("ch3Status");
            String ch4LotStatus = map.get("ch4Status");

            String ch1LotId = map.get("ch1LotId");
            String ch2LotId = map.get("ch2LotId");

            if (LOT_START.equals(ch1LotStatus) || LOT_START.equals(ch3LotStatus)) {
                if (client != pairClient) {
                    rail1IsLotStarted = true;
                    rail1InProcessLotId = ch1LotId;
                } else {
                    rail3IsLotStarted = true;
                    rail3InProcessLotId = ch1LotId;
                }
            }

            if (LOT_START.equals(ch2LotStatus) || LOT_START.equals(ch4LotStatus)) {
                if (client != pairClient) {
                    rail2IsLotStarted = true;
                    rail2InProcessLotId = ch2LotId;
                } else {
                    rail4IsLotStarted = true;
                    rail4InProcessLotId = ch2LotId;
                }
            }

            if (client != pairClient) {
                writeLog(client, "***********   CURRENT LOT STATUS   ***********");
                writeLog(client, String.format("[CH1, CH3] LOT ID: %s", rail1InProcessLotId));
                writeLog(client, String.format("[CH2, CH4] LOT ID: %s", rail2InProcessLotId));
                writeLog(client, "**********************************************");
            } else {
                writeLog(client, "***********   CURRENT LOT STATUS   ***********");
                writeLog(client, String.format("[CH1, CH3] LOT ID: %s", rail3InProcessLotId));
                writeLog(client, String.format("[CH2, CH4] LOT ID: %s", rail4InProcessLotId));
                writeLog(client, "**********************************************");
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
            writeLog(client, String.format("[ERROR] LOT INITIALIZED \n %s", e.toString()));
        }
        writeLog(client, "LOT STATUS INITIALIZED :: END");
    }

    public void setPairClient(RFIDTcpClient pairClient) {
        this.pairClient = pairClient;
        this.pairReader = this.pairClient.getReaderVO();
        this.pairLogHeaderMessage = String.format("[%s] %s(%s)", pairReader.getEquipmentName(), pairReader.getReaderName(), pairReader.getReaderIp());
        this.isPaired = true;
        writeLog(String.format("%s(%s)가 %s(%s)에 페어링 되었습니다.", pairClient.getReaderVO().getReaderName(), pairClient.getReaderVO().getReaderIp(), this.reader.getReaderName(), this.reader.getReaderIp()));
        writeLog(String.format("PROCESS CLASS: %s(%d)", this.toString(), this.hashCode()));
        lotInitialize(this.pairClient);
    }


    @Override
    public void getReaderConfig(RFIDTcpClient client, String readerMac) {
        try {
            ReaderVO readerVO = dbManageService.getReaderInfo(readerMac);
            if (readerVO == null) {
                writeLog(client, String.format("%s 리더기 현재 설정 상태 정보가 없습니다.", readerMac));
            } else {
                if (this.client == client) {
                    this.reader = readerVO;
                } else {
                    this.pairReader = readerVO;
                }
                client.setReaderVO(readerVO);
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
            writeLog(client, e.toString());
        }
    }


    @Override
    public void writeLog(RFIDTcpClient client, String logMessage) {
        if (client == this.pairClient) {
            clientLogger.trace(pairLogHeaderMessage, logMessage, pairReader.getEquipmentName());
        } else {
            super.writeLog(client, logMessage);
        }
    }

    @Override
    public void replyHandler(HashMap response) {
        if (!response.isEmpty()) {
            RFIDTcpClient client = (RFIDTcpClient) response.get("client");
            RFID_EIS_Reply_Out_Tag replyOutTag = (RFID_EIS_Reply_Out_Tag) response.get("mesReply");
            TagDataVO tagDataVO = (TagDataVO) response.get("eventData");
            String eventType = response.get("eventType").toString();
            int eventLogId = Integer.parseInt(response.get("eventLogId").toString());

            int evtChannel = tagDataVO.getAnt();

            writeLog(client, "REPLY HANDLER :: START");

            writeLog(client, String.format("MES(EIS) REPLY RESULT: %s", replyOutTag.toString()));

            if (MES_EVENT_SUCCESS == replyOutTag.h_status_value) {
                // eventType: SEND_MES_START(MAGAZINE(태그) ATTACH일 경우)
                if (SEND_MES_START.equals(eventType)) {
                    // LOT_START 조건
                    if (replyOutTag.resv_flag_1 == 'Y' && replyOutTag.resv_field_1 != null && !"".equals(replyOutTag.resv_field_1)) {
                        writeLog(client, "*******************************************************************************");
                        writeLog(client, String.format("                         [LOT START] LOT ID: %s                            ", replyOutTag.resv_field_1));
                        writeLog(client, "*******************************************************************************");
                        // RAIL 1 일 경우
                        if (evtChannel == 1 || evtChannel == 3) {
                            if (client != pairClient) {
                                rail1IsLotStarted = true;
                                rail1InProcessLotId = replyOutTag.resv_field_1;
                            } else {
                                rail3IsLotStarted = true;
                                rail3InProcessLotId = replyOutTag.resv_field_1;
                            }
                            // RAIL 2 일 경우
                        } else {
                            if (client != pairClient) {
                                rail2IsLotStarted = true;
                                rail2InProcessLotId = replyOutTag.resv_field_1;
                            } else {
                                rail4IsLotStarted = true;
                                rail4InProcessLotId = replyOutTag.resv_field_1;
                            }
                        }
                    }
                    // eventType: SEND_MES_END(MAGAZINE(태그) DETACH일 경우)
                } else {
                    // LOT_END 조건
                    if (replyOutTag.resv_flag_2 == 'Y' && replyOutTag.resv_field_2 != null && !"".equals(replyOutTag.resv_field_2)) {
                        writeLog(client, "*******************************************************************************");
                        writeLog(client, String.format("                         [LOT END] LOT ID: %s                            ", replyOutTag.resv_field_2));
                        writeLog(client, "*******************************************************************************");
                        // RAIL 1 일 경우
                        if (evtChannel == 1 || evtChannel == 3) {
                            if (client != pairClient) {
                                rail1IsLotStarted = false;
                                rail1InProcessLotId = null;
                            } else {
                                rail3IsLotStarted = false;
                                rail3InProcessLotId = null;
                            }
                            // RAIL 2 일 경우
                        } else {
                            if (client != pairClient) {
                                rail2IsLotStarted = false;
                                rail2InProcessLotId = null;
                            } else {
                                rail4IsLotStarted = false;
                                rail4InProcessLotId = null;
                            }
                        }
                        tagDataVO.setEventStatus(LOT_END);
                    }
                }
            }
            // event가 발생한 채널이 1 or 3 일 경우
            if (evtChannel == 1 || evtChannel == 3) {
                if (client != pairClient) {
                    if (rail1IsLotStarted) {    // RAIL1이 LOT_START 일 경우
                        tagDataVO.setEventStatus(LOT_START);
                        tagDataVO.setMesLotId1(rail1InProcessLotId);
                        tagDataVO.setMesLotId3(rail1InProcessLotId);
                    }
                } else {
                    if (rail3IsLotStarted) {
                        tagDataVO.setEventStatus(LOT_START);
                        tagDataVO.setMesLotId1(rail3InProcessLotId);
                        tagDataVO.setMesLotId3(rail3InProcessLotId);
                    }
                }
            } else {
                if (client != pairClient) {
                    if (rail2IsLotStarted) {    // RAIL2가 LOT_START 일 경우
                        tagDataVO.setEventStatus(LOT_START);
                        tagDataVO.setMesLotId2(rail2InProcessLotId);
                        tagDataVO.setMesLotId4(rail2InProcessLotId);
                    }
                } else {
                    if (rail4IsLotStarted) {
                        tagDataVO.setEventStatus(LOT_START);
                        tagDataVO.setMesLotId2(rail4InProcessLotId);
                        tagDataVO.setMesLotId4(rail4InProcessLotId);
                    }
                }
            }
            setReaderStatus(client, tagDataVO, replyOutTag.h_msg);
            updateMesEventHistory(eventLogId, replyOutTag);
            writeLog(client, "REPLY HANDLER :: END");
        }
    }
}
