package kr.co.nexmore.rfiddaemon.reader.process;

import kr.co.nexmore.rfiddaemon.mes.OneooneManager;
import kr.co.nexmore.rfiddaemon.reader.util.CommonUtil;
import kr.co.nexmore.rfiddaemon.service.DBManageService;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.RFIDTcpClient;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.handler.RFIDClientMessageHandler;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.manager.ClientLogManager;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.EIS_RFID_Request_In_Tag;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.RFID_EIS_Reply_Out_Tag;
import kr.co.nexmore.rfiddaemon.vo.reader.ReaderVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.TagDataVO;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static kr.co.nexmore.rfiddaemon.common.CommonControlCode.*;

@Slf4j
public class WireBondProcessBiz extends CommonProcessBiz {

    private HashMap<String, Map<Integer, String>> lotStartMagazines;

    public WireBondProcessBiz(DBManageService dbManageService, OneooneManager oneooneManager, ClientLogManager clientLogger, RFIDTcpClient client) {
        super(dbManageService, oneooneManager, clientLogger, client);
    }

    public void lotStatusCheck() {
        if (lotStartMagazines != null) {
            if (!lotStartMagazines.isEmpty()) {
                log.debug(logHeaderMessage + lotStartMagazines.toString());
                writeLog(lotStartMagazines.toString());
            } else {
                log.debug(logHeaderMessage + "lotStartMagazines is empty");
            }
        } else {
            log.debug(logHeaderMessage + "lotStartMagazines is null");
        }
    }

    public void forceLotAdd(String lotId, String magazineId, int channel) {
        inProcessLotId = lotId;
        if (!lotStartMagazines.isEmpty()) {
            if (lotStartMagazines.get(lotId) == null) {
                Map<Integer, String> magazines = new HashMap();
                magazines.put(channel, magazineId);
                lotStartMagazines.put(lotId, magazines);
            } else {
                lotStartMagazines.get(lotId).put(channel, magazineId);
            }
        }
    }

    @Override
    public synchronized void lotInitialize(RFIDTcpClient client) {
        writeLog("LOT STATUS INITIALIZED :: START");
        ReaderVO reader = client.getReaderVO();
        try {
            LinkedHashMap<String, String> map = dbManageService.getLastLotStatus(reader.getReaderMac());    // 현재 lot status 가져오기
            writeLog(map.toString());
            // ch1(loader)  lotStatus
            String ch1LotStatus = map.get("ch1Status");
            String ch1LotId = map.get("ch1LotId");
            // ch2(unloader) lotStatus
            String ch2LotStatus = map.get("ch2Status");
            String ch2LotId = map.get("ch2LotId");

            HashMap<String, Map<Integer, String>> initializeLot = new HashMap<>();

//            if (ch1LotStatus != null && ch2LotStatus != null) {
            if (ch1LotStatus != null && LOT_START.equals(ch1LotStatus)) {
//                if(ch1LotStatus.equals("LOT_START") || ch2LotStatus.equals("LOT_START")) {
                isLotStarted = true;
                inProcessLotId = ch1LotId;  // ch1(loader) 인식 시 lot_id 를 내려줌. (가장 최근 lot은 무조건 ch1 data. ch2(unloader) 는 ch1(loader) 값을 이전하도록 되어 있음)
                writeLog(String.format("inProcessLotId : %s", inProcessLotId));

                if (ch2LotStatus != null && LOT_START.equals(ch2LotStatus)) {
                    if (ch1LotId != null && ch2LotId != null && ch1LotId.equals(ch2LotId)) {    // 두개의 M/Z 의 lot id 가 같을 경우

                        Map<Integer, String> magazines = new HashMap();

                        magazines.put(1, map.get("ch1MagazineId"));
                        magazines.put(2, map.get("ch2MagazineId"));
                        initializeLot.put(ch1LotId, magazines);

                    } else {    // 두개의 M/Z 의 lot id 가 다를 경우
                        Map<Integer, String> magazines1 = new HashMap();
                        magazines1.put(1, map.get("ch1MagazineId"));
                        initializeLot.put(ch1LotId, magazines1);

                        Map<Integer, String> magazines2 = new HashMap();
                        magazines2.put(2, map.get("ch2MagazineId"));
                        initializeLot.put(ch2LotId, magazines2);
                    }
                } else {
                    Map<Integer, String> magazines = new HashMap();
                    magazines.put(1, map.get("ch1MagazineId"));
                    initializeLot.put(ch1LotId, magazines);
                }
            }

            lotStartMagazines = initializeLot;

            if (!this.lotStartMagazines.isEmpty()) {
                log.debug("CURRENT LOT STATUS : {}", this.lotStartMagazines.toString());
                writeLog("***********   CURRENT LOT STATUS   ***********");
                for (String lotId : this.lotStartMagazines.keySet()) {
                    writeLog(String.format("[LOT ID: %s]  CH1: %s \t CH2: %s ", lotId, this.lotStartMagazines.get(lotId).get(1), this.lotStartMagazines.get(lotId).get(2)));
                }
                writeLog("**********************************************");
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            writeLog(String.format("[ERROR] LOT STATUS INITIALIZED \n %s", e.toString()));
        }
        writeLog("LOT STATUS INITIALIZED :: END");
    }


    /**
     * W/B 공정 설비 떨어짐 감지 시 현재 데이터 변수를
     * 이전 데이터 변수에 저장 한다.
     */
    public void tagEventHandler(RFIDTcpClient client, TagDataVO receiveDataVO) {

        RFIDClientMessageHandler handler = client.getMessageHandler();

        getReaderConfig(client, client.getReaderVO().getReaderMac());
        int evtChannel = receiveDataVO.getAnt();
        TagDataVO currentDataVO = handler.getCurrentData(evtChannel);
        receiveDataVO.setAttachTime(System.currentTimeMillis());    // ProcessBiz Class에서 현재시간 재세팅

        if (currentDataVO == null || "".equals(currentDataVO.getTagUid())) {
            writeLog(client, String.format("CHANNEL-%d INITIAL TAG ATTACH", evtChannel));
            writeLog(client, String.format("CHANNEL-%d TAG UID: %s, MAGAZINE ID: %s, LOT ID: %s,  ATTACH TIME: %s",
                    evtChannel, receiveDataVO.getTagUid(), receiveDataVO.getData(), receiveDataVO.getLotId(), CommonUtil.getCurrentTimeMillis(receiveDataVO.getAttachTime())));

            TagDataVO prevDataVO = handler.getPrevData(evtChannel);

            if (prevDataVO != null) {
                writeLog(client, String.format("PrevData: %s", prevDataVO.toString()));
            }

            if (isLotStarted) {
                if (prevDataVO != null) {
                    if (prevDataVO.getTagUid().equals(receiveDataVO.getTagUid())) { // 이전 데이터와 현재 데이터가 같으면
                        if (!lotStartMagazines.isEmpty()) { // start 된 magazine map이 비어있지 않으면
                            for (String lotId : lotStartMagazines.keySet()) {
                                if (lotStartMagazines.get(lotId).get(evtChannel) != null) {
                                    if (lotStartMagazines.get(lotId).get(evtChannel).equals(receiveDataVO.getData())) { // start된 lot 에 연결된 M/Z 값이 같은게 있다면
                                        receiveDataVO.setStartProcess(prevDataVO.isStartProcess());
                                        receiveDataVO.setEndProcess(prevDataVO.isEndProcess());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            handler.setCurrentData(receiveDataVO, evtChannel);
            if (!receiveDataVO.isStartProcess()) {
                receiveDataVO.setStartProcess(true);
                startEvent(client, receiveDataVO);
            }

        } else if (currentDataVO.getTagUid().equals(receiveDataVO.getTagUid())) {
            currentDataVO.setAttachTime(receiveDataVO.getAttachTime());
        } else {
            writeLog(client, String.format("CHANNEL-%d NEW TAG ATTACH", evtChannel));
            writeLog(client, String.format("CHANNEL-%d TAG UID: %s, MAGAZINE ID: %s, LOT ID: %s,  ATTACH TIME: %s",
                    evtChannel, receiveDataVO.getTagUid(), receiveDataVO.getData(), receiveDataVO.getLotId(), CommonUtil.getCurrentTimeMillis(receiveDataVO.getAttachTime())));

            handler.setPrevData(currentDataVO, evtChannel);

            TagDataVO prevDataVO = handler.getPrevData(evtChannel);
            if (prevDataVO != null) {
                detachHandler(client, prevDataVO);
            }

            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (Exception e) {
                writeLog(client, e.toString());
            }
            if (isLotStarted) {
                if (!lotStartMagazines.isEmpty()) {
                    for (String lotId : lotStartMagazines.keySet()) {
                        if (lotStartMagazines.get(lotId).get(evtChannel) != null) {
                            if (lotStartMagazines.get(lotId).get(evtChannel).equals(receiveDataVO.getData())) {
                                receiveDataVO.setStartProcess(true);
                                receiveDataVO.setEndProcess(true);
                            }
                        }
                    }
                }
            }

            handler.setCurrentData(receiveDataVO, evtChannel);
            if (!receiveDataVO.isStartProcess()) {
                receiveDataVO.setStartProcess(true);
                startEvent(client, receiveDataVO);
            }
        }
        client.getMessageHandler().detachScheduleStart(evtChannel);
    }


    public void replyHandler(HashMap response) {
        if (!response.isEmpty()) {
            RFIDTcpClient client = (RFIDTcpClient) response.get("client");
            RFID_EIS_Reply_Out_Tag replyOutTag = (RFID_EIS_Reply_Out_Tag) response.get("mesReply");
            TagDataVO tagDataVO = (TagDataVO) response.get("eventData");
            int eventLogId = Integer.parseInt(response.get("eventLogId").toString());
            String eventType = response.get("eventType").toString();

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

                        isLotStarted = true;
                        inProcessLotId = replyOutTag.resv_field_1;

                        if (!lotStartMagazines.isEmpty()) {
                            for (String lotId : lotStartMagazines.keySet()) {
                                if (lotStartMagazines.get(lotId).get(2) != null) {
                                    // 현재 진행중인 lot 중 같은 magazine_id 가 있으면 지금 받은 magazine_id 는 null 로 처리
                                    // 연속 작업 시 1채널, 2채널 동시에 물렸을 때를 대비(진행 중인 lot 에 m/z 가 이미 있는데 새로운 lot 에 같은 m/z 가 setting 되는 것을 막기 위함.)
                                    if (lotStartMagazines.get(lotId).get(2).equals(replyOutTag.magazine_id_2)) {
                                        replyOutTag.magazine_id_2 = null;
                                    }
                                }
                            }
                        }

                        Map<Integer, String> magazines = new HashMap<>();
                        magazines.put(1, replyOutTag.magazine_id_1);    // 1채널 M/Z(TAG) DATA
                        magazines.put(2, replyOutTag.magazine_id_2);    // 2채널 M/Z(TAG) DATA

                        lotStartMagazines.put(inProcessLotId, magazines);

                        writeLog(String.format("inProcessLotId: %s", inProcessLotId));
                        writeLog(lotStartMagazines.toString());

                    } else if (replyOutTag.resv_field_3 != null && !"".equals(replyOutTag.resv_field_3)) {
                        if (lotStartMagazines.get(replyOutTag.resv_field_3) != null) {
                            lotStartMagazines.get(replyOutTag.resv_field_3).put(2, replyOutTag.magazine_id_2);
                            writeLog(String.format("inProcessLotId: %s", inProcessLotId));
                            writeLog(lotStartMagazines.toString());
                        }
                    }
                }
                try {
                    dbManageService.setLotStatus(replyOutTag, reader.getReaderMac());
                } catch (Exception e) {
                    writeLog(String.format("LOT STATUS 변경 실패 => %s", e.getCause()));
                    writeLog(e.toString());
                }
            }
            updateMesEventHistory(eventLogId, replyOutTag);
            writeLog(client, "REPLY HANDLER :: END");
        }
    }


    public void detachHandler(RFIDTcpClient client, TagDataVO tagDataVO) {
        if (tagDataVO.isStartProcess() && !tagDataVO.isEndProcess()) {
            tagDataVO.setEventStatus(TAG_EVENT_END);
            tagDataVO.setEndProcess(true);
            endEvent(client, tagDataVO);
        } else if (tagDataVO.isEndProcess()) {
            detachComplete(client, tagDataVO);
        }
    }


    public RFID_EIS_Reply_Out_Tag setLotStatusEnd(EIS_RFID_Request_In_Tag requestInTag) {
        writeLog(String.format("[MZ_EJECT] MES(EIS) REQUEST: %s", requestInTag.toString()));
        String lotId = requestInTag.resv_field_2;

        RFID_EIS_Reply_Out_Tag replyOutTag = new RFID_EIS_Reply_Out_Tag(requestInTag);

        writeLog("*******************************************************************************");
        writeLog(String.format("                         [LOT END] LOT ID: %s                            ", lotId));
        writeLog("*******************************************************************************");

        if (lotId != null && !"".equals(lotId)) {
            if (!lotStartMagazines.isEmpty()) {
                if (lotStartMagazines.get(lotId) != null) {

                    writeLog("lotStartMagazines remove.");
                    lotStartMagazines.remove(lotId);

                    // EJECT 로 받은 lot id가 마지막 lot id 라면.
                    if (inProcessLotId != null && lotId.equals(inProcessLotId)) {
                        writeLog(String.format("LAST LOT ID: %s", inProcessLotId));
                        writeLog("lotStartMagazines clear.");
                        lotStartMagazines.clear();
                        try {
                            dbManageService.setLotEndForWB(this.client.getReaderVO().getReaderMac());
                        } catch (Exception e) {
                            writeLog(String.format("W/B FINAL LOT END FAILURE =>  %s", e.getCause()));
                            writeLog(e.toString());
                        }
                        isLotStarted = false;
                        inProcessLotId = null;
                    }
                }
            }
        }
        replyOutTag.h_status_value = MES_EVENT_SUCCESS;
        writeLog(String.format("[MZ_EJECT] REPLY PARAMETER TO MES(EIS): %s", replyOutTag.toString()));
        return replyOutTag;
    }
}
