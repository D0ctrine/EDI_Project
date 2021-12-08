package kr.co.nexmore.rfiddaemon.reader.process;

import kr.co.nexmore.rfiddaemon.mes.OneooneManager;
import kr.co.nexmore.rfiddaemon.service.DBManageService;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.RFIDTcpClient;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.manager.ClientLogManager;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType;
import kr.co.nexmore.rfiddaemon.vo.reader.ReaderVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.TagDataVO;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static kr.co.nexmore.rfiddaemon.common.CommonControlCode.*;
import static kr.co.nexmore.rfiddaemon.common.CommonControlCode.LOT_END;

@Slf4j
public class DispatchProcessBiz extends CommonProcessBiz {

    /**
     * 1채널 LOT STATUS
     */
    private boolean ch1IsLotStarted;

    /**
     * 2채널 LOT STATUS
     */
    private boolean ch2IsLotStarted;

    /**
     * 3채널 LOT STATUS
     */
    private boolean ch3IsLotStarted;

    /**
     * 4채널 LOT STATUS
     */
    private boolean ch4IsLotStarted;


    /**
     * 1채널 LOT ID
     */
    private String ch1InProcessLotId;

    /**
     * 2채널 LOT ID
     */
    private String ch2InProcessLotId;

    /**
     * 3채널 LOT ID
     */
    private String ch3InProcessLotId;

    /**
     * 4채널 LOT ID
     */
    private String ch4InProcessLotId;

    public DispatchProcessBiz(DBManageService dbManageService, OneooneManager oneooneManager, ClientLogManager clientLogger, RFIDTcpClient client) {
        super(dbManageService, oneooneManager, clientLogger, client);
    }

    @Override
    public void endEvent(RFIDTcpClient client, TagDataVO tagDataVO) {
        if (TAG_EVENT_FORCE_END.equals(tagDataVO.getEventStatus())) {
            super.setReaderStatus(client, tagDataVO, TAG_EVENT_FORCE_END);
        } else {
            super.setReaderStatus(client, tagDataVO);
        }
        super.endEvent(client, tagDataVO);
    }

    /**
     * TABLE 강제 END 처리
     *
     * @param channel
     */
    public void forceEndEvent(int channel) {
        writeLog(String.format("FORCE END EVENT :: START - CHANNEL: %d", channel));
        TagDataVO tagDataVO = client.getMessageHandler().getCurrentData(channel);

        if (tagDataVO == null) {
//            tagDataVO.setEventStatus(TAG_EVENT_END);
//            tagDataVO.setEndProcess(true);
            writeLog(String.format("%d채널의 현재 데이터가 없습니다. 이전 데이터를 조회합니다.", channel));
            tagDataVO = client.getMessageHandler().getPrevData(channel);
            if (tagDataVO == null) {
                writeLog(String.format("%d채널의 이전 데이터가 없습니다. 빈 값으로 END EVENT 를 실행합니다.", channel));
                tagDataVO = new TagDataVO();
                tagDataVO.setAnt(channel);
            }
        }
        tagDataVO.setEventStatus(TAG_EVENT_FORCE_END);
        tagDataVO.setStartProcess(true);
        tagDataVO.setEndProcess(true);

        endEvent(client, tagDataVO);
        writeLog(String.format("FORCE END EVENT :: END - CHANNEL: %d", channel));
    }


    @Override
    public synchronized void lotInitialize(RFIDTcpClient client) {
        writeLog(client, "LOT STATUS INITIALIZED :: START");
        ReaderVO reader = client.getReaderVO();
        try {
            LinkedHashMap<String, String> map = dbManageService.getLastLotStatus(reader.getReaderMac());

            log.debug(map.toString());

            String ch1LotStatus = map.get("ch1Status");
            String ch2LotStatus = map.get("ch2Status");
            String ch3LotStatus = map.get("ch3Status");
            String ch4LotStatus = map.get("ch4Status");

            String ch1LotId = map.get("ch1LotId");
            String ch2LotId = map.get("ch2LotId");
            String ch3LotId = map.get("ch3LotId");
            String ch4LotId = map.get("ch4LotId");

            if (LOT_START.equals(ch1LotStatus)) {
                ch1IsLotStarted = true;
                ch1InProcessLotId = ch1LotId;
            }
            if (LOT_START.equals(ch2LotStatus)) {
                ch2IsLotStarted = true;
                ch2InProcessLotId = ch2LotId;
            }
            if (LOT_START.equals(ch3LotStatus)) {
                ch3IsLotStarted = true;
                ch3InProcessLotId = ch3LotId;
            }
            if (LOT_START.equals(ch4LotStatus)) {
                ch4IsLotStarted = true;
                ch4InProcessLotId = ch4LotId;
            }

            writeLog("***********   CURRENT LOT STATUS   ***********");
            writeLog(String.format("[CH1] LOT ID: %s", ch1InProcessLotId));
            writeLog(String.format("[CH2] LOT ID: %s", ch2InProcessLotId));
            writeLog(String.format("[CH3] LOT ID: %s", ch3InProcessLotId));
            writeLog(String.format("[CH4] LOT ID: %s", ch4InProcessLotId));
            writeLog("**********************************************");

        } catch (Exception e) {
            log.error(e.toString(), e);
            writeLog(client, String.format("[ERROR] LOT INITIALIZED \n %s", e.toString()));
        }
        writeLog(client, "LOT STATUS INITIALIZED :: END");
    }


    public void setChannelLotStatus(int channel, String lotId, String eventType) {
        if (LOT_START.equals(eventType)) {
            if (channel == 1) {
                ch1IsLotStarted = true;
                ch1InProcessLotId = lotId;
            } else if (channel == 2) {
                ch2IsLotStarted = true;
                ch2InProcessLotId = lotId;
            } else if (channel == 3) {
                ch3IsLotStarted = true;
                ch3InProcessLotId = lotId;
            } else {
                ch4IsLotStarted = true;
                ch4InProcessLotId = lotId;
            }
        } else if (eventType.contains("END")) {
            if (channel == 1) {
                ch1IsLotStarted = false;
                ch1InProcessLotId = lotId;
            } else if (channel == 2) {
                ch2IsLotStarted = false;
                ch2InProcessLotId = lotId;
            } else if (channel == 3) {
                ch3IsLotStarted = false;
                ch3InProcessLotId = lotId;
            } else {
                ch4IsLotStarted = false;
                ch4InProcessLotId = lotId;
            }
        }
    }


    //    public synchronized void replyHandler(HashMap response) {
    public void replyHandler(HashMap response) {
        if (!response.isEmpty()) {
            RFIDTcpClient client = (RFIDTcpClient) response.get("client");
            EISType.RFID_EIS_Reply_Out_Tag replyOutTag = (EISType.RFID_EIS_Reply_Out_Tag) response.get("mesReply");
            TagDataVO tagDataVO = (TagDataVO) response.get("eventData");
            int eventLogId = Integer.parseInt(response.get("eventLogId").toString());
            String eventType = response.get("eventType").toString();
            int eventChannel = tagDataVO.getAnt();

            writeLog(client, "REPLY HANDLER :: START");

            writeLog(client, String.format("MES(EIS) REPLY RESULT: %s", replyOutTag.toString()));

            if (MES_EVENT_SUCCESS == replyOutTag.h_status_value) {
                // eventType: SEND_MES_START(MAGAZINE(태그) 인식 일 경우)
                if (SEND_MES_START.equals(eventType)) {
                    // LOT START 조건 일 경우
                    if (replyOutTag.resv_flag_1 == 'Y' && replyOutTag.resv_field_1 != null && !"".equals(replyOutTag.resv_field_1)) {
                        writeLog(client, "*******************************************************************************");
                        writeLog(client, String.format("                         [LOT START] LOT ID: %s                            ", replyOutTag.resv_field_1));
                        writeLog(client, "*******************************************************************************");

                        setChannelLotStatus(eventChannel, replyOutTag.resv_field_1, LOT_START);
                    }
                } else {
                    // eventType: SEND_MES_END(MAGAZINE(태그) DETACH일 경우)
                    setChannelLotStatus(eventChannel, null, eventType);
                    // LOT_END 조건
                    if (replyOutTag.resv_flag_2 == 'Y' && replyOutTag.resv_field_2 != null && !"".equals(replyOutTag.resv_field_2)) {
                        writeLog(client, "*******************************************************************************");
                        writeLog(client, String.format("                         [LOT END] LOT ID: %s                            ", replyOutTag.resv_field_2));
                        writeLog(client, "*******************************************************************************");

                        setChannelLotStatus(eventChannel, null, LOT_END);
                        tagDataVO.setEventStatus(LOT_END);
                    }
                }
            }

            if (eventChannel == 1) {
                if (ch1IsLotStarted) {
                    tagDataVO.setEventStatus(LOT_START);
                    tagDataVO.setMesLotId1(ch1InProcessLotId);
                }
            } else if (eventChannel == 2) {
                if (ch2IsLotStarted) {
                    tagDataVO.setEventStatus(LOT_START);
                    tagDataVO.setMesLotId2(ch2InProcessLotId);
                }
            } else if (eventChannel == 3) {
                if (ch3IsLotStarted) {
                    tagDataVO.setEventStatus(LOT_START);
                    tagDataVO.setMesLotId3(ch3InProcessLotId);
                }
            } else if (eventChannel == 4) {
                if (ch4IsLotStarted) {
                    tagDataVO.setEventStatus(LOT_START);
                    tagDataVO.setMesLotId4(ch4InProcessLotId);
                }
            }

            setReaderStatus(client, tagDataVO, replyOutTag.h_msg);
            updateMesEventHistory(eventLogId, replyOutTag);
            writeLog(client, "REPLY HANDLER :: END");
        }
    }


}
