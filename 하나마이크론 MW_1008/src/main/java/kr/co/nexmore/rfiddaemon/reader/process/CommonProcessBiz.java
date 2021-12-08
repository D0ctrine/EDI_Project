package kr.co.nexmore.rfiddaemon.reader.process;

import kr.co.nexmore.rfiddaemon.mes.OneooneManager;
import kr.co.nexmore.rfiddaemon.service.DBManageService;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.RFIDTcpClient;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.handler.RFIDClientMessageHandler;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.manager.ClientLogManager;
import kr.co.nexmore.rfiddaemon.reader.util.CommonUtil;
import kr.co.nexmore.rfiddaemon.reader.util.TcpSetCommandUtil;
import kr.co.nexmore.rfiddaemon.vo.common.EventHistoryVO;
import kr.co.nexmore.rfiddaemon.vo.reader.ReaderVO;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.EIS_RFID_Request_In_Tag;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.RFID_EIS_Reply_Out_Tag;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.SyscontrolResVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.TagDataVO;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static kr.co.nexmore.rfiddaemon.common.CommonControlCode.*;
import static kr.co.nexmore.rfiddaemon.common.RequestCommand.SYS_CONF_BUZZER_ONOFF;
import static kr.co.nexmore.rfiddaemon.common.RequestCommand.SYS_CONF_OPERATNG_MODE;

@Slf4j
public class CommonProcessBiz {

    protected final DBManageService dbManageService;
    protected final OneooneManager oneooneManager;
    protected final ClientLogManager clientLogger;

    protected RFIDTcpClient client;
    protected boolean isLotStarted;
    protected String inProcessLotId = null;


    /**
     * MES(EIS) 전송 할 Request eventQueue
     */
    private final ArrayBlockingQueue<HashMap> eventQueue = new ArrayBlockingQueue<HashMap>(1000);    // request Event Queue (MES 전송)

    /**
     * MES(EIS) 로 부터 받는 reply Queue
     */
    private final ArrayBlockingQueue<HashMap> responseQueue = new ArrayBlockingQueue<HashMap>(1000); // response Event Queue (MES reply response)


    /**
     * readerVO
     */
    protected ReaderVO reader;

    Thread eventPollingThread;

    boolean pollingActived = true;

    boolean pollingResponse = false;

    /**
     * 설비 별 로그를 위한 logHeader
     * ex) [EX001] EX001-2C-01(xxx.xxx.xxx.xxx)
     */
    protected String logHeaderMessage;

    public CommonProcessBiz(DBManageService dbManageService, OneooneManager oneooneManager, ClientLogManager clientLogger, RFIDTcpClient client) {
        this.client = client;
        this.reader = client.getReaderVO();
        this.dbManageService = dbManageService;
        this.oneooneManager = oneooneManager;
        this.clientLogger = clientLogger;
        // logHeader setting
        this.logHeaderMessage = String.format("[%s] %s(%s)", reader.getEquipmentName(), reader.getReaderName(), reader.getReaderIp());
        init();
    }

    public void init() {
        lotInitialize(this.client);
        eventPolling(eventQueue, responseQueue, String.format("EventPolling-%s", reader.getEquipmentName()));
    }

/*    public RFIDTcpClient getClient() {
        return client;
    }*/

    /**
     * polling 상태를 변경한다.
     * event polling 중지 목적으로 사용
     *
     * @param flag
     */
    public void changePolling(boolean flag) {
        this.pollingActived = flag;
        if (this.eventQueue.size() <= 0 && !pollingResponse) {
            this.eventPollingThread.interrupt();
        }
    }

    /**
     * daemon 최초 기동 시 DB 에서 해당 리더기의 data 를 조회하여
     * 현재 현재 진행 중인 lot 이 있는지 체크 후 변수에 저장한다.
     *
     * @param client
     */
    public synchronized void lotInitialize(RFIDTcpClient client) {
        writeLog("LOT STATUS INITIALIZED :: START");
        ReaderVO reader = client.getReaderVO();
        try {
            LinkedHashMap<String, String> map = dbManageService.getLastLotStatus(reader.getReaderMac());
            log.debug(map.toString());
            String ch1LotStatus = map.get("ch1Status");
            String ch2LotStatus = map.get("ch2Status");

            String ch1LotId = map.get("ch1LotId");

            if (LOT_START.equals(ch1LotStatus) || LOT_START.equals(ch2LotStatus)) {
                isLotStarted = true;
                inProcessLotId = ch1LotId;
            }

            writeLog("***********   CURRENT LOT STATUS   ***********");
            writeLog(String.format("[LOT INITIALIZE] LOT ID: %s", inProcessLotId));
            writeLog("**********************************************");

        } catch (Exception e) {
            log.error(e.toString(), e);
            writeLog(String.format("[ERROR] LOT INITIALIZED \n %s", e.toString()));
        }
        writeLog("LOT STATUS INITIALIZED :: END");
    }

    /**
     * MES(EIS) 에 Data 를 전송하기 위해 eventQueue 를 polling 한다.
     * eventQueue 에 data 가 add 될 시 MES(EIS) 에 Data 를 전송한다.
     * Data 를 전송한 후 responseQueue 를 polling 하여 응답을 대기한다.
     */
    public void eventPolling(ArrayBlockingQueue<HashMap> eventQueue, ArrayBlockingQueue<HashMap> responseQueue, String threadName) {
        eventPollingThread = new Thread(() -> {
            Thread.currentThread().setName(threadName);
            while (true) {
                try {
//                    int requestPollTime = oneooneManager.getTimeout(reader.getStationXId());
                    writeLog(client, String.format("[%s] EventQueue Polling...   ", threadName));

                    if (!pollingActived && eventQueue.size() <= 0) {
                        writeLog("EVENT POLLING THREAD BREAK");
                        break;
                    }

                    if (eventQueue.size() > 0) {
                        writeLog(client, String.format("[%s] CURRENT EVENT QUEUE COUNT: %d", threadName, eventQueue.size()));
                    }
                    HashMap event = eventQueue.take();
                    if (event == null) {
                        continue;
                    } else {
                        if (!event.isEmpty() && event.get("client") != null && event.get("eventData") != null
                                && event.get("mesEvent") != null && event.get("historyVO") != null && event.get("eventType") != null) {
                            RFIDTcpClient client = (RFIDTcpClient) event.get("client");
                            String eventType = event.get("eventType").toString();
                            TagDataVO eventData = (TagDataVO) event.get("eventData");
                            EIS_RFID_Request_In_Tag requestInTag = (EIS_RFID_Request_In_Tag) event.get("mesEvent");
                            EventHistoryVO eventHistoryVO = (EventHistoryVO) event.get("historyVO");

                            writeLog(client, String.format("[%s] Event Polled Success", threadName));
                            writeLog(client, String.format("[%s] Event Type => %s", threadName, eventType));
                            writeLog(client, String.format("[%s] Event Data: %s", threadName, event.toString()));

                            sendToEIS(client, eventData, requestInTag, eventHistoryVO, eventType);
                            pollingResponse = true;
                            HashMap response = responseQueue.take();  // response 대기
                            pollingResponse = false;
                            if (response != null) {
                                replyHandler(response);
                            } else {
                                writeLog(client, "MES(EIS) REPLY IS NULL!!");
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(e.toString(), e);
                    writeLog(client, String.format("[%s] EventQueue Polling error: %s", threadName, e.getMessage()));
                }
            }
        });
        eventPollingThread.start();
    }


    /**
     * MES(EIS) 에 전송할 Data 를 DB 에 insert 한다. (eventHistory)
     * MES(EIS) 에 Data 를 전송한다. (SEND_MES_START, SEND_MES_END)
     * Data 를 전송한 후 reply 를 responseQueue 에 add 한다.
     * reply 의 값에 따라 현재 status 를 변경한다. (LOT_START, LOT_END)
     *
     * @param tagDataVO
     * @param requestInTag
     * @param eventHistoryVO
     * @param eventType
     */
    public void sendToEIS(RFIDTcpClient client, TagDataVO tagDataVO, EIS_RFID_Request_In_Tag requestInTag, EventHistoryVO eventHistoryVO, String eventType) {
        new Thread(() -> {
            ReaderVO reader = client.getReaderVO();
            int evtChannel = tagDataVO.getAnt();
            writeLog(client, String.format("sendToEIS CHANNEL-%d [%s] EVENT :: START", evtChannel, eventType));

            int eventLogId = 0;
            String eventTime = null;
            RFID_EIS_Reply_Out_Tag replyOutTag = null;  // MES(EIS) Reply 객체
            try {
                Map map = insertMesEventHistory(client, eventHistoryVO);
                if (map != null && !map.isEmpty()) {
                    eventLogId = Integer.parseInt(map.get("eventLogId").toString());
                    eventTime = map.get("regDate").toString();
                }
                // 발생한 채널의 SEND_MES_START, SEND_MES_END EVENT 에 대한 DB insert 등록 시간 값을 MES 에 보내기 전 eventTime 으로 세팅 한다.
                if (evtChannel == 1) {
                    requestInTag.event_time_1 = eventTime;
                } else if (evtChannel == 2) {
                    requestInTag.event_time_2 = eventTime;
                } else if (evtChannel == 3) {
                    requestInTag.event_time_3 = eventTime;
                } else if (evtChannel == 4) {
                    requestInTag.event_time_4 = eventTime;
                }

                writeLog(client, String.format("EIS SEND PARAMETER: %s", requestInTag.toString()));

                if (eventLogId > 0) {
                    // MES(EIS)에 requestTagIn 전송
                    if (oneooneManager != null) {
                        replyOutTag = oneooneManager.sendToEIS(reader.getStationXId(), requestInTag, eventType);
                        if (replyOutTag != null) {
                            writeLog(client, "MES(EIS) REPLY RECEIVE => SUCCESS");
                        }
                    } else {
                        String message = String.format("[M/W ERROR] SEND TO EIS ERROR => 101 station is null");
                        log.error("### 101 Station is null ###");
                        replyOutTag = new RFID_EIS_Reply_Out_Tag();
                        replyOutTag.h_status_value = MES_EVENT_FAIL;
                        replyOutTag.h_msg = message;
                    }
                } else {
                    String message = String.format("[M/W ERROR] SEND TO EIS ERROR => EVENT_LOG_ID ERROR");
                    replyOutTag = new RFID_EIS_Reply_Out_Tag();
                    replyOutTag.h_status_value = MES_EVENT_FAIL;
                    replyOutTag.h_msg = message;
                }
                addResponseQueue(client, tagDataVO, replyOutTag, eventLogId, eventType);
                writeLog(client, "ADD MES(EIS) REPLY INTO A RESPONSE QUEUE");

            } catch (Exception e) {
                String message = String.format("[M/W ERROR] SEND TO EIS ERROR => %s", e.getMessage());
                log.error(e.toString(), e);
                writeLog(client, message);
                writeLog(client, e.toString());
                replyOutTag = new RFID_EIS_Reply_Out_Tag();
                replyOutTag.h_status_value = MES_EVENT_FAIL;
                replyOutTag.h_msg = message;
                addResponseQueue(client, tagDataVO, replyOutTag, eventLogId, eventType);
            }
            writeLog(client, String.format("sendToEIS [%s] EVENT :: END", eventType));
        }).start();
    }


    /**
     * MES(EIS) 로부터 수신 된 reply를 handling
     *
     * @param response
     */
    //    public synchronized void replyHandler(HashMap response) {
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

                    }
                } else {
                    // eventType: SEND_MES_END(MAGAZINE(태그) DETACH일 경우)
                    // LOT_END 조건
                    if (replyOutTag.resv_flag_2 == 'Y' && replyOutTag.resv_field_2 != null && !"".equals(replyOutTag.resv_field_2)) {
                        writeLog(client, "*******************************************************************************");
                        writeLog(client, String.format("                         [LOT END] LOT ID: %s                            ", replyOutTag.resv_field_2));
                        writeLog(client, "*******************************************************************************");
                        isLotStarted = false;
                        inProcessLotId = null;
                        tagDataVO.setEventStatus(LOT_END);
                    }

                }
            }

            if (isLotStarted) {
                tagDataVO.setEventStatus(LOT_START);
                tagDataVO.setMesLotId1(inProcessLotId);
                tagDataVO.setMesLotId2(inProcessLotId);
            }

            setReaderStatus(client, tagDataVO, replyOutTag.h_msg);
            updateMesEventHistory(eventLogId, replyOutTag);
            writeLog(client, "REPLY HANDLER :: END");
        }
    }

    /**
     * 설비별 로그를 로깅
     *
     * @param logMessage
     */
    public void writeLog(String logMessage) {
        clientLogger.trace(logHeaderMessage, logMessage, reader.getEquipmentName());
    }

    public void writeLog(RFIDTcpClient client, String logMessage) {
        writeLog(logMessage);
    }


    public void setLotStatusEnd(String lotId) {
        if (inProcessLotId != null && lotId != null && inProcessLotId.equals(lotId)) {
            isLotStarted = false;
            inProcessLotId = null;
        }
    }

    /**
     * Reader 의 Buzzer On/Off 설정을 조회하는 command 전송
     * DB 의 BuzzerYn 값과 비교하여 다를 경우 DB 의 값으로
     * Reader 의 Buzzer On/Off 값을 변경하는 command 전송한다.
     */
    public void buzzerSynchronize(RFIDTcpClient client) {
        writeLog(client, "DB&READER BUZZER ON/OFF SYNCHRONIZE :: START");
        RFIDClientMessageHandler handler = client.getMessageHandler();
        ReaderVO readerVO = client.getReaderVO();
        int retry = 0;
        try {
            while (retry != 3) {
                writeLog(client, "REQUEST GET BUZZER ON/OFF :: START");
                client.sendRequest(TcpSetCommandUtil.getRegister(SYS_CONF_BUZZER_ONOFF, readerVO.getReaderMac()));
                Map map = handler.getBlockingQueue().poll(1000, TimeUnit.MILLISECONDS);
                if (map != null && !map.isEmpty() && map.get("result") != null) {
                    SyscontrolResVO syscontrolResVO = (SyscontrolResVO) map.get("result");
                    writeLog(client, String.format("BUZZER ON/OFF REGISTRY RESPONSE VO: %s", syscontrolResVO.toString()));
                    String buzzerYn = syscontrolResVO.getRegisterValue() == 0 ? "N" : "Y";
                    writeLog(client, String.format("DB BUZZER Y/N: %s READER BUZZER Y/N: %s", readerVO.getBuzzerYn(), buzzerYn));
                    handler.getBlockingQueue().clear();
                    if (!readerVO.getBuzzerYn().equals(buzzerYn)) {
                        int updTry = 0;
                        while (updTry != 3) {
                            writeLog(client, String.format("READER BUZZER ON/OFF UPDATE COMMAND :: START"));
                            client.sendRequest(TcpSetCommandUtil.setBuzzer(readerVO.getBuzzerYn().equals("Y") ? 1 : 0, readerVO.getReaderMac()));
                            Map setBuzzer = handler.getBlockingQueue().poll(1000, TimeUnit.MILLISECONDS);
                            if (setBuzzer != null && !setBuzzer.isEmpty() && setBuzzer.get("action") != null && setBuzzer.get("success") != null) {
                                writeLog(client, String.format("\"%s\" SUCCESS: %s", setBuzzer.get("action"), setBuzzer.get("success")));
                                client.sendRequest(TcpSetCommandUtil.setSaveRegistry(readerVO.getReaderMac()));
                                writeLog(client, String.format("READER BUZZER ON/OFF UPDATE COMMAND :: END"));
                                handler.getBlockingQueue().clear();
                                break;
                            } else {
                                updTry++;
                            }
                            writeLog(client, String.format("READER BUZZER ON/OFF UPDATE COMMAND :: END"));
                        }
                    } else {
                        writeLog(client, "DB BUZZER Y/N READER BUZZER Y/N이 일치합니다.");
                        break;
                    }
                    writeLog(client, "REQUEST GET READER BUZZER ON/OFF :: END");
                    break;
                } else {
                    retry++;
                }
            }
            writeLog(client, "DB&READER BUZZER SYNCHRONIZE :: END");
        } catch (Exception e) {
            writeLog(client, "예외 상황 발생");
            log.error(e.toString(), e);
            writeLog(client, e.toString());
            handler.getBlockingQueue().clear();
        }
        handler.getBlockingQueue().clear();
    }


    /**
     * Reader 의 Operating Mode 조회 command 전송 (Mode, TransferTime)
     * DB 의 INTERVAL 과 TransferTime 을 비교하여
     * 다를 경우 DB의 값으로 리더의 TransferTime 을 변경하도록 command 전송한다.
     */
    public void intervalSynchronize(RFIDTcpClient client) {
        writeLog(client, "DB&READER INTERVAL SYNCHRONIZE :: START");
        ReaderVO readerVO = client.getReaderVO();
        int retry = 0;
        try {
            while (retry != 3) {
                writeLog(client, "[REQUEST TO READER] GET READER TRANSFER TIME INTERVAL :: START");
                // Reader 에 Command 전송
                client.sendRequest(TcpSetCommandUtil.getRegister(SYS_CONF_OPERATNG_MODE, readerVO.getReaderMac()));
                Map interval = client.getMessageHandler().getBlockingQueue().poll(1000, TimeUnit.MILLISECONDS);
                if (interval != null && !interval.isEmpty() && interval.get("result") != null) {
                    SyscontrolResVO syscontrolResVO = (SyscontrolResVO) interval.get("result");
                    writeLog(client, String.format("TRANSFER TIME INTERVAL REGISTRY RESPONSE VO: %s", syscontrolResVO.toString()));
                    double transferTime = (syscontrolResVO.getTransferTime() / 10.0);
                    writeLog(client, String.format("DB INTERVAL: %.1f  READER INTERVAL: %.1f", readerVO.getReaderInterval(), transferTime));
                    client.getMessageHandler().getBlockingQueue().clear();
                    if (readerVO.getReaderInterval() != transferTime) {
                        int updTry = 0;
                        while (updTry != 3) {
                            writeLog(client, String.format("[REQUEST TO READER] READER TRANSFER TIME INTERVAL UPDATE COMMAND :: START"));
                            // Reader 에 Command 전송
                            client.sendRequest(TcpSetCommandUtil.setMode(0, readerVO.getReaderInterval(), readerVO.getReaderMac()));
                            Map setMode = client.getMessageHandler().getBlockingQueue().poll(1000, TimeUnit.MILLISECONDS);
                            if (setMode != null && !setMode.isEmpty() && setMode.get("action") != null && setMode.get("success") != null) {
                                writeLog(client, String.format("\"%s\" SUCCESS: %s", setMode.get("action"), setMode.get("success")));
                                client.getMessageHandler().getBlockingQueue().clear();
                                client.sendRequest(TcpSetCommandUtil.setSaveRegistry(readerVO.getReaderMac()));
                                writeLog(client, String.format("[REQUEST TO READER] READER TRANSFER TIME INTERVAL UPDATE COMMAND :: END"));
                                client.getMessageHandler().getBlockingQueue().clear();
                                break;
                            } else {
                                updTry++;
                            }
                            writeLog(client, String.format("[REQUEST TO READER] READER TRANSFER TIME INTERVAL UPDATE COMMAND :: END"));
                        }
                    } else {
                        writeLog(client, "DB INTERVAL과 READER INTERVAL이 일치합니다.");
                        break;
                    }
                    writeLog(client, "[REQUEST TO READER] GET READER TRANSFER TIME INTERVAL :: END");
                    break;
                } else {
                    retry++;
                }
            }
            writeLog(client, "DB&READER INTERVAL SYNCHRONIZE :: END");
        } catch (Exception e) {
            writeLog(client, "예외 상황 발생");
            log.error(e.toString(), e);
            writeLog(client, e.toString());
            client.getMessageHandler().getBlockingQueue().clear();
        }
        client.getMessageHandler().getBlockingQueue().clear();
    }


    /**
     * Reader 의 production 정보 조회 command 전송(HW, Firmware)
     * DB 의 Firmware Version 과 비교하여
     * 다를 경우 현재 Reader 의 Firmware 정보를 DB에 업데이트 한다.
     */
    public void firmwareCheck(RFIDTcpClient client) {
        writeLog(client, "[REQUEST TO READER] FIRMWARE VERSION CHECK :: START");
        ReaderVO readerVO = client.getReaderVO();
        int retry = 0;
        while (retry != 2) {
            try {
                client.sendRequest(TcpSetCommandUtil.getReaderInformation(readerVO.getReaderMac())); // 리더 현재 production 정보 조회 명령(HW, FIRMWARE 정보)
                Map productInfo = client.getMessageHandler().getBlockingQueue().poll(1000, TimeUnit.MILLISECONDS);
                if (productInfo != null && !productInfo.isEmpty() && productInfo.get("result") != null) {
                    writeLog(client, productInfo.toString());
                    String[] readerVersion = ((SyscontrolResVO) productInfo.get("result")).getProductRevision().split(",");
                    writeLog(client, String.format("PRODUCT INFORMATION => %s %s %s", readerVersion[0], readerVersion[1], readerVersion[2]));
                    String firmwareVersion = String.format("V.%s.%s.%s", readerVersion[2].substring(15, 17), readerVersion[2].substring(18, 20), readerVersion[2].substring(21, 23));
                    client.getMessageHandler().getBlockingQueue().clear();
                    if (!firmwareVersion.equals(readerVO.getFirmwareVersion())) {
                        writeLog(client, "FIRMWARE VERSION이 변경되어 DB UPDATE 합니다.");
                        int result = dbManageService.setFirmwareVersion(firmwareVersion, readerVO.getReaderMac());
                        if (result > 0) {
                            writeLog(client, String.format("FIRMWARE VERSION DB UPDATE SUCCESS => BEFORE: %s -> AFTER: %s", readerVO.getFirmwareVersion(), firmwareVersion));
                            readerVO.setFirmwareVersion(firmwareVersion);
                        } else {
                            writeLog(client, "FIRMWARE VERSION DB UPDATE FAIL");
                        }
                    }
                    break;
                } else {
                    retry++;
                }
            } catch (Exception e) {
                log.error(e.toString(), e);
                writeLog(client, e.toString());
                writeLog(client, e.getStackTrace().toString());
                client.getMessageHandler().getBlockingQueue().clear();
            }
        }
        client.getMessageHandler().getBlockingQueue().clear();
        writeLog(client, "[REQUEST TO READER] FIRMWARE VERSION CHECK :: END");
    }


    /**
     * 수신 된 TagData 를 START 처리 하기 위해 Handling 한다.
     * 새로운 TagData 면 startEvent 를 호출한다.
     * 현재 가지고 있는 Data와 수신 된 Data의 TAG UID 가 같으면 데이터 받은 시간만 갱신한다.
     *
     * @param receiveDataVO
     */
    public void tagEventHandler(RFIDTcpClient client, TagDataVO receiveDataVO) {

        RFIDClientMessageHandler handler = client.getMessageHandler();
        getReaderConfig(client, client.getReaderVO().getReaderMac());

        int evtChannel = receiveDataVO.getAnt();
        TagDataVO currentDataVO = handler.getCurrentData(evtChannel);

        receiveDataVO.setAttachTime(System.currentTimeMillis());    // ProcessBiz Class에서 현재시간 재세팅

        if (currentDataVO == null || "".equals(currentDataVO.getTagUid())) {
            writeLog(client, String.format("CHANNEL-%d INITIAL TAG ATTACH", evtChannel));
            writeLog(client, String.format("CHANNEL-%d TAG UID: %s, MAGAZINE ID: %s, LOT ID: %s, ATTACH TIME: %s",
                    evtChannel, receiveDataVO.getTagUid(), receiveDataVO.getData(), receiveDataVO.getLotId(), CommonUtil.getCurrentTimeMillis(receiveDataVO.getAttachTime())));

            TagDataVO prevDataVO = handler.getPrevData(evtChannel);

            if (prevDataVO != null) {
                writeLog(client, String.format("PrevData: %s", prevDataVO.toString()));
            }
            receiveDataVO.setStartProcess(true);
            handler.setCurrentData(receiveDataVO, evtChannel);
            startEvent(client, receiveDataVO);

        } else if (currentDataVO.getTagUid().equals(receiveDataVO.getTagUid())) {
            currentDataVO.setAttachTime(receiveDataVO.getAttachTime());
        } else {
            writeLog(client, String.format("CHANNEL-%d NEW TAG ATTACH", evtChannel));
            writeLog(client, String.format("CHANNEL-%d TAG UID: %s, MAGAZINE ID: %s, LOT ID: %s,  ATTACH TIME: %s",
                    evtChannel, receiveDataVO.getTagUid(), receiveDataVO.getData(), receiveDataVO.getLotId(), CommonUtil.getCurrentTimeMillis(receiveDataVO.getAttachTime())));

            receiveDataVO.setStartProcess(true);

            handler.setPrevData(currentDataVO, evtChannel);
            handler.setCurrentData(receiveDataVO, evtChannel);

            TagDataVO prevDataVO = handler.getPrevData(evtChannel);
            if (prevDataVO != null) {
                detachHandler(client, prevDataVO);
            }

            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (Exception e) {
                writeLog(client, e.toString());
            }
            startEvent(client, receiveDataVO);
        }

        client.getMessageHandler().detachScheduleStart(evtChannel);
    }

    /**
     * MES(EIS) 통신 EVENT 발생 시 Handling 하는 메서드
     * MES 에 보낼 EIS_RFID_Request_In_Tag 데이터를 setting
     * MES 에 보낼 데이터에 대한 historyVO setting
     *
     * @param eventHistoryVO
     * @param requestInTag
     */
    public synchronized void mesEventDataHandler(RFIDTcpClient client, EventHistoryVO eventHistoryVO, EIS_RFID_Request_In_Tag
            requestInTag) {
        RFIDClientMessageHandler handler = client.getMessageHandler();
        TagDataVO ch1CurrentData = handler.getCurrentData(1);
        TagDataVO ch2CurrentData = handler.getCurrentData(2);
        TagDataVO ch3CurrentData = handler.getCurrentData(3);
        TagDataVO ch4CurrentData = handler.getCurrentData(4);
        // EventHistory DB Insert Data Setting
        mesEventInsertDataSetting(ch1CurrentData, ch2CurrentData, ch3CurrentData, ch4CurrentData, eventHistoryVO);
        // MES Send Data Setting
        mesSendDataSetting(ch1CurrentData, ch2CurrentData, ch3CurrentData, ch4CurrentData, requestInTag);
    }

    /**
     * TAG(MAGAZINE) Attach 시 START EVENT 를 발생한다.
     * Data validation check 후 올바른 Data 면
     * DB 에 insert 후 MES(EIS) 로 Data 를 전송하기 위해
     * eventQueue 에 발생한 event 를 add 한다.
     *
     * @param receiveDataVO
     */
    public void startEvent(RFIDTcpClient client, TagDataVO receiveDataVO) {

        writeLog(client, "TAG ATTACH EVENT :: START");

        ReaderVO reader = client.getReaderVO();

        writeLog(client, String.format("READER CURRENT CONFIG STATUS =>  MAC: %s  설비명: %s  공정명: %s  모델명: %s  BuzzerYn: %s  Interval: %.1f  MesYn: %s  stationXId: %d",
                reader.getReaderMac(), reader.getEquipmentName(), reader.getProcessName(), reader.getModelName(), reader.getBuzzerYn(),
                reader.getReaderInterval(), reader.getMesUseYn(), reader.getStationXId()));

        writeLog(client, String.format("RECEIVE TAG DATA =>  TAG UID: %s  MAGAZINE ID: %s  CHANNEL: %d", receiveDataVO.getTagUid(), receiveDataVO.getData(), receiveDataVO.getAnt()));

        String eventType = null;
        String message = null;

        // 1. 상태 값 변경
        receiveDataVO.setEventStatus(TAG_EVENT_START);
        setReaderStatus(client, receiveDataVO); // db값 변경

        // 2. validationCheck
        HashMap<String, String> validMap = CommonUtil.validationTagData(receiveDataVO);
        if (validMap != null && !validMap.isEmpty()) {
            if (validMap.get("eventType") != null && !"".equals(validMap.get("eventType"))) {
                eventType = validMap.get("eventType");
                message = validMap.get("message");
                receiveDataVO.setEventStatus(eventType);
            }
        }

        // 3. START log DB insert
        insertEventHistory(client, receiveDataVO, message);
        // 4. SendToEIS
        if ("Y".equals(reader.getMesUseYn()) && !VALIDATION_FAIL.equals(eventType)) {
            receiveDataVO.setEventStatus(SEND_MES_START);
            addEventQueue(client, receiveDataVO);
        }
        writeLog(client, "TAG ATTACH EVENT :: END");
    }

    /**
     * END EVENT 실행(떨어짐 감지 EVENT)
     *
     * @param receiveDataVO
     */
    public void endEvent(RFIDTcpClient client, TagDataVO receiveDataVO) {

        writeLog(client, "TAG DETACH EVENT :: START");

        ReaderVO reader = client.getReaderVO();

        writeLog(client, String.format("READER CURRENT CONFIG STATUS => MAC: %s  채널 수: %d  설비명: %s  공정명: %s  모델명: %s  BuzzerYn: %s  Interval: %.1f  MesYn: %s  stationXId: %d",
                reader.getReaderMac(), reader.getChannelCnt(), reader.getEquipmentName(), reader.getProcessName(), reader.getModelName(), reader.getBuzzerYn(),
                reader.getReaderInterval(), reader.getMesUseYn(), reader.getStationXId()));
        writeLog(client, String.format("RECEIVE TAG DATA =>  TAG UID: %s  MAGAZINE ID: %s  CHANNEL: %d", receiveDataVO.getTagUid(), receiveDataVO.getData(), receiveDataVO.getAnt()));

        String eventType = null;
        String message = null;

        // 1. validationCheck
        HashMap<String, String> validMap = CommonUtil.validationTagData(receiveDataVO);
        if (validMap != null && !validMap.isEmpty()) {
            if (validMap.get("eventType") != null && !"".equals(validMap.get("eventType"))) {
                eventType = validMap.get("eventType");
                message = validMap.get("message");
                receiveDataVO.setEventStatus(eventType);
            }
        }
        // 2. END log DB insert
        insertEventHistory(client, receiveDataVO, message);

        // 3. SendToEIS
        if ("Y".equals(reader.getMesUseYn()) && !VALIDATION_FAIL.equals(eventType) && receiveDataVO.isStartProcess()) { // mesUseYn, validation, startProcess check
            receiveDataVO.setEventStatus(SEND_MES_END);

            addEventQueue(client, receiveDataVO);

            TagDataVO currentData = client.getMessageHandler().getCurrentData(receiveDataVO.getAnt());
            // 현재 data 와 수신된 data 비교하여 detach 처리
            if (currentData != null && receiveDataVO.getTagUid().equals(currentData.getTagUid())) {
                detachComplete(client, receiveDataVO);
            }
        }
        writeLog(client, "TAG DETACH EVENT :: END");
    }


    /**
     * MES(EIS) 에 보낼 데이터를 eventQueue 에 add 한다.
     *
     * @param tagDataVO
     */
    public void addEventQueue(RFIDTcpClient client, TagDataVO tagDataVO) {
        String eventType = tagDataVO.getEventStatus();
        int evtChannel = tagDataVO.getAnt();
        writeLog(client, String.format("ADD TO QUEUE CHANNEL-%d [%s] EVENT :: START", evtChannel, eventType));
        ReaderVO reader = client.getReaderVO();

        EIS_RFID_Request_In_Tag requestInTag = new EIS_RFID_Request_In_Tag(reader.getEquipmentName(), reader.getReaderIp(), tagDataVO.getAnt(), tagDataVO.getTagUid(), tagDataVO.getData());
        EventHistoryVO eventHistoryVO = new EventHistoryVO(reader, tagDataVO, EVENT_RESPONSE_WAITING, null);

        mesEventDataHandler(client, eventHistoryVO, requestInTag);   // eventHistory insert Data&MES send data Setting

        HashMap event = new HashMap();
        event.put("client", client);
        event.put("eventData", tagDataVO);
        event.put("mesEvent", requestInTag);
        event.put("historyVO", eventHistoryVO);
        event.put("eventType", eventType);

        eventQueue.add(event);
        writeLog(client, String.format("ADD TO QUEUE CHANNEL-%d [%s] EVENT :: END", evtChannel, eventType));
    }



    /**
     * MES(EIS) 의 REPLY 데이터 정보를 responseQueue 에 add 한다.
     *
     * @param client
     * @param tagDataVO
     * @param replyOutTag
     * @param eventLogId
     * @param eventType
     */
    public void addResponseQueue(RFIDTcpClient client, TagDataVO tagDataVO, RFID_EIS_Reply_Out_Tag replyOutTag, int eventLogId, String eventType) {
        HashMap response = new HashMap();
        response.put("client", client);
        response.put("eventData", tagDataVO);
        response.put("mesReply", replyOutTag);
        response.put("eventLogId", eventLogId);
        response.put("eventType", eventType);
        responseQueue.add(response);
    }


    /**
     * 떨어짐 처리를 위한 handler
     *
     * @param client
     * @param tagDataVO
     */
    public void detachHandler(RFIDTcpClient client, TagDataVO tagDataVO) {
        if (tagDataVO.isStartProcess() && !tagDataVO.isEndProcess()) {
            tagDataVO.setEventStatus(TAG_EVENT_END);
            tagDataVO.setEndProcess(true);
            endEvent(client, tagDataVO);
        }
    }


    /**
     * 떨어짐 감지 시 현재 데이터 변수를
     * 이전 데이터 변수에 저장 후 초기화 한다.
     *
     * @param tagDataVO
     */
    public void detachComplete(RFIDTcpClient client, TagDataVO tagDataVO) {
        RFIDClientMessageHandler handler = client.getMessageHandler();
        TagDataVO currentData = handler.getCurrentData(tagDataVO.getAnt());
        handler.setPrevData(tagDataVO, tagDataVO.getAnt());
        if (currentData != null) {
            if (currentData.getTagUid().equals(tagDataVO.getTagUid())) {
                handler.setCurrentData(null, tagDataVO.getAnt());
            }
        }
    }


    /**
     * 리더의 현재 상태를 조회
     * 조회한 값으로 현재 상태를 세팅한다.
     *
     * @param readerMac 리더기 MAC Address
     */
    public void getReaderConfig(RFIDTcpClient client, String readerMac) {
        try {
            ReaderVO readerVO = dbManageService.getReaderInfo(readerMac);
            if (readerVO == null) {
                writeLog(client, String.format("%s 리더기 현재 설정 상태 정보가 없습니다.", readerMac));
            } else {
                this.reader = readerVO;
                client.setReaderVO(readerVO);
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
            writeLog(client, e.toString());
        }
    }


    /**
     * 2ch Reader 의 Channel1(antenna1), Channel2(antenna2) 값
     * 4ch Reader 의 Channel1(antenna1), Channel2(antenna2), Channel3(antenna3), Channel4(antenna4) 값
     * MES(EIS) 전송 Request 에 세팅한다.
     *
     * @param ch1TagDataVO 채널1 tagDataVO
     * @param ch2TagDataVO 채널2 tagDataVO
     * @param ch3TagDataVO 채널3 tagDataVO
     * @param ch4TagDataVO 채널4 tagDataVO
     * @param requestInTag EIS_RFID_Request_In_tag
     */
    public void mesSendDataSetting(TagDataVO ch1TagDataVO, TagDataVO ch2TagDataVO, TagDataVO
            ch3TagDataVO, TagDataVO ch4TagDataVO, EIS_RFID_Request_In_Tag requestInTag) {

        if (requestInTag.resv_flag_5 != '1') {
            if (ch1TagDataVO != null && ch1TagDataVO.isStartProcess() && !VALIDATION_FAIL.equals(ch1TagDataVO.getEventStatus())) {
                requestInTag.antenna_port_1 = (char) (ch1TagDataVO.getAnt() + 48);
                requestInTag.uid_1 = ch1TagDataVO.getTagUid();
                requestInTag.magazine_id_1 = ch1TagDataVO.getData();
                requestInTag.event_time_1 = ch1TagDataVO.getEventTime();
            }
        }
        if (requestInTag.resv_flag_5 != '2') {
            if (ch2TagDataVO != null && ch2TagDataVO.isStartProcess() && !VALIDATION_FAIL.equals(ch2TagDataVO.getEventStatus())) {
                requestInTag.antenna_port_2 = (char) (ch2TagDataVO.getAnt() + 48);
                requestInTag.uid_2 = ch2TagDataVO.getTagUid();
                requestInTag.magazine_id_2 = ch2TagDataVO.getData();
                requestInTag.event_time_2 = ch2TagDataVO.getEventTime();
            }
        }
        if (reader.getChannelCnt() == 4) {
            if (requestInTag.resv_flag_5 != '3') {
                if (ch3TagDataVO != null && ch3TagDataVO.isStartProcess() && !VALIDATION_FAIL.equals(ch3TagDataVO.getEventStatus())) {
                    requestInTag.antenna_port_3 = (char) (ch3TagDataVO.getAnt() + 48);
                    requestInTag.uid_3 = ch3TagDataVO.getTagUid();
                    requestInTag.magazine_id_3 = ch3TagDataVO.getData();
                    requestInTag.event_time_3 = ch3TagDataVO.getEventTime();
                }
            }
            if (requestInTag.resv_flag_5 != '4') {
                if (ch4TagDataVO != null && ch4TagDataVO.isStartProcess() && !VALIDATION_FAIL.equals(ch4TagDataVO.getEventStatus())) {
                    requestInTag.antenna_port_4 = (char) (ch4TagDataVO.getAnt() + 48);
                    requestInTag.uid_4 = ch4TagDataVO.getTagUid();
                    requestInTag.magazine_id_4 = ch4TagDataVO.getData();
                    requestInTag.event_time_4 = ch4TagDataVO.getEventTime();
                }
            }
        }
    }

    /**
     * MES(EIS) 전송 데이터를 DB Insert 하기 위해
     * eventHistoryVO 에 setting 한다.
     *
     * @param ch1TagDataVO   채널1 tagDataVO
     * @param ch2TagDataVO   채널2 tagDataVO
     * @param ch3TagDataVO   채널3 tagDataVO
     * @param ch4TagDataVO   채널4 tagDataVO
     * @param eventHistoryVO EventHistoryVO
     */
    public void mesEventInsertDataSetting(TagDataVO ch1TagDataVO, TagDataVO ch2TagDataVO, TagDataVO
            ch3TagDataVO, TagDataVO ch4TagDataVO, EventHistoryVO eventHistoryVO) {

        if (eventHistoryVO.getAntennaNum() != 1) {
            if (ch1TagDataVO != null && ch1TagDataVO.isStartProcess() && !VALIDATION_FAIL.equals(ch1TagDataVO.getEventStatus())) {
                eventHistoryVO.setAnt1TagUid(ch1TagDataVO.getTagUid());
                eventHistoryVO.setAnt1MagazineId(ch1TagDataVO.getData());
            }
        }

        if (eventHistoryVO.getAntennaNum() != 2) {
            if (ch2TagDataVO != null && ch2TagDataVO.isStartProcess() && !VALIDATION_FAIL.equals(ch2TagDataVO.getEventStatus())) {
                eventHistoryVO.setAnt2TagUid(ch2TagDataVO.getTagUid());
                eventHistoryVO.setAnt2MagazineId(ch2TagDataVO.getData());
            }
        }

        if (reader.getChannelCnt() == 4) {
            if (eventHistoryVO.getAntennaNum() != 3) {
                if (ch3TagDataVO != null && ch3TagDataVO.isStartProcess() && !VALIDATION_FAIL.equals(ch3TagDataVO.getEventStatus())) {
                    eventHistoryVO.setAnt3TagUid(ch3TagDataVO.getTagUid());
                    eventHistoryVO.setAnt3MagazineId(ch3TagDataVO.getData());
                }
            }
            if (eventHistoryVO.getAntennaNum() != 4) {
                if (ch4TagDataVO != null && ch4TagDataVO.isStartProcess() && !VALIDATION_FAIL.equals(ch4TagDataVO.getEventStatus())) {
                    eventHistoryVO.setAnt4TagUid(ch4TagDataVO.getTagUid());
                    eventHistoryVO.setAnt4MagazineId(ch4TagDataVO.getData());
                }
            }
        }
    }


    /**
     * MES(EIS) 통신 후 응답 값(reply) 값을 DB에 업데이트 한다.
     *
     * @param eventLogId
     * @param replyOutTag
     */
    public void updateMesEventHistory(int eventLogId, RFID_EIS_Reply_Out_Tag replyOutTag) {
        EventHistoryVO eventHistoryVO = new EventHistoryVO(eventLogId, replyOutTag);
        try {
            dbManageService.updateEventHistory(eventHistoryVO);
        } catch (Exception e) {
            log.error(e.toString(), e);
            writeLog(client, e.toString());
        }
    }


    /**
     * 리더의 현재 상태를 DB에 업데이트 한다.
     *
     * @param tagDataVO
     */
    public void setReaderStatus(RFIDTcpClient client, TagDataVO tagDataVO) {
        this.setReaderStatus(client, tagDataVO, null);
    }

    public void setReaderStatus(RFIDTcpClient client, TagDataVO tagDataVO, String mesMessage) {
        ReaderVO reader = client.getReaderVO();
        if (TAG_EVENT_FORCE_END.equals(mesMessage)) {
            tagDataVO.setEventStatus(TAG_EVENT_END);
        }
        try {
            if (reader.getChannelCnt() == 2) {
                dbManageService.setCh2ReaderStatus(tagDataVO, mesMessage, reader.getReaderMac(), reader.getProcessName().trim().toUpperCase());
            } else {
                log.info(mesMessage);
                dbManageService.setCh4ReaderStatus(tagDataVO, mesMessage, reader.getReaderMac(), reader.getProcessName().trim().toUpperCase());
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
            writeLog(client, e.toString());
        }
    }

    /**
     * eventType에 따른 message 세팅 후 DB에 eventHistory를 저장한다.
     *
     * @param tagDataVO tagDataVO
     * @Param message message
     */
    public void insertEventHistory(RFIDTcpClient client, TagDataVO tagDataVO, String message) {

        ReaderVO reader = client.getReaderVO();

        int result = TAG_EVENT_SUCCESS;
        if (message != null) {
            result = TAG_EVENT_FAIL;
        }
        EventHistoryVO eventHistoryVO = new EventHistoryVO(reader, tagDataVO, result, message);
        try {
            writeLog(client, String.format("EVENT HISTORY INSERT DATA: %s", eventHistoryVO.toString()));
            int r = dbManageService.insertEventHistory(eventHistoryVO);
            if (r > 0) {
                writeLog(client, "EVENT HISTORY INSERT DATA => SUCCESS");
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
            writeLog(client, e.toString());
        }
    }

    /**
     * MES(EIS) 통신 로그 DB insert
     *
     * @param client
     * @param eventHistoryVO
     * @return Map
     */
    public Map insertMesEventHistory(RFIDTcpClient client, EventHistoryVO eventHistoryVO) {
        Map result = null;
        try {
            writeLog(client, String.format("MES EVENT HISTORY INSERT DATA: %s", eventHistoryVO.toString()));
            int r = dbManageService.insertMesEventHistory(eventHistoryVO);
            if (r > 0) {
                writeLog(client, "MES EVENT HISTORY INSERT DATA => SUCCESS");
                String regDate = dbManageService.getLastInsertTime(eventHistoryVO);
                result = new HashMap();
                result.put("eventLogId", eventHistoryVO.getEventLogId());
                result.put("regDate", regDate);
//                        writeLog(client, String.format("EVENT LOG ID: %d  TRANSFER TIME: %s", eventLogId, eventTime));
                writeLog(client, "GET MES EVENT HISTORY EVENT LOG ID & REG DATE => SUCCESS");
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
            writeLog(client, e.toString());
        }
        return result;
    }

    public void lotStatusCheck() {
        if (inProcessLotId != null) {
            log.debug(inProcessLotId);
        } else {
            log.debug("lot is null");
        }
    }
}
