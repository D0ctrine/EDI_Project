package kr.co.nexmore.rfiddaemon.mes;

import com.miracom.oneoone.transceiverx.*;
import com.miracom.oneoone.transceiverx.parser.DeliveryType;
import com.miracom.oneoone.transceiverx.parser.StreamTransformerImpl;
import kr.co.nexmore.rfiddaemon.common.CommonControlCode;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.EIS_RFID_Request_In_Tag;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.RFID_EIS_Reply_Out_Tag;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.RFID_EIS_Change_Resource_Info_In;
import kr.co.nexmore.rfiddaemon.service.DBManageService;
import kr.co.nexmore.rfiddaemon.vo.mes.OneooneVO;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

import static kr.co.nexmore.rfiddaemon.common.CommonControlCode.*;
import static kr.co.nexmore.rfiddaemon.vo.mes.EISType.*;

@Slf4j
public class Oneoone extends OneooneVO {

    private DBManageService dbManageService;
    private OneooneManager oneooneManager;

    private Session ioiSession = null;
    Timer timer = new Timer();
    private Oneoone mirror;

    public Oneoone(OneooneVO oneooneVO, DBManageService dbManageService, OneooneManager oneooneManager) {
        setOneooneVO(oneooneVO);
        this.dbManageService = dbManageService;
        this.oneooneManager = oneooneManager;
        init();
    }

    public void setOneooneVO(OneooneVO oneooneVO) {
        this.setStationXId(oneooneVO.getStationXId());
        this.setStationXIp(oneooneVO.getStationXIp());
        this.setStationXPort(oneooneVO.getStationXPort());
        this.setStationXChannel(oneooneVO.getStationXChannel());
        this.setRfidTuneChannel(oneooneVO.getRfidTuneChannel());
        this.setConnectStatus(oneooneVO.getConnectStatus());
        this.setAutoConnect(oneooneVO.getAutoConnect());
        this.setMesTimeout(oneooneVO.getMesTimeout());
        mirror = this;
    }

    public void setTimeout(int timeout) {
        log.debug("parameter: {}", timeout);
        this.setMesTimeout(timeout);
        this.ioiSession.setDefaultTTL(timeout * 1000);
        mirror = this;
    }

    private void init() {
        new Thread(() -> {
            log.debug("mes thread start");
            connect();
            if (getConnectStatus().equals("Y")) {
                tuneChannel();
            }
        }).start();


        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (getAutoConnect().equals("Y")) {
                    String operationCode = CommonControlCode.OPCODE_EISTunerAlive;
                    log.info("[RFID M/W => EIS({})] [SEND REQUEST: '{}']        SESSION ID: {}", ioiSession.getConnectString(), operationCode, ioiSession.getSessionID());
                    //접속 여부 확인
                    try {
                        Message msg = ioiSession.createMessage();
                        msg.putProperty(CommonControlCode.XGEN_TAG_VERSION, CommonControlCode.XGEN_VERSION);
                        msg.putProperty(CommonControlCode.XGEN_TAG_MODULE, "EIS");
                        msg.putProperty(CommonControlCode.XGEN_TAG_INTERFACE, "EIS");
                        msg.putProperty(CommonControlCode.XGEN_TAG_OPERATION, operationCode);
                        msg.putProperty(CommonControlCode.XGEN_TAG_HOSTNAME, InetAddress.getLocalHost().getHostName());
                        msg.putProperty(CommonControlCode.XGEN_TAG_HOSTADDR, InetAddress.getLocalHost().getHostAddress());
                        msg.putChannel(getRfidTuneChannel());
                        msg.putDeliveryMode(DeliveryType.REQUEST);
                        msg.putTTL(getMesTimeout() * 1000);

                        //메시지 전송
                        Message rep = ioiSession.sendRequest(msg);
                        if (rep == null) {
                            log.debug("[OPERATION CODE: '{}'] NO REPLY FROM EIS({})       SESSION ID: {}", operationCode, ioiSession.getConnectString(), ioiSession.getSessionID());
                            setConnectStatus("N");
                            dbManageService.setOneooneConnectStatus(mirror);
                        } else {
                            setConnectStatus("Y");
                            log.info("[EIS({}) => RFID M/W] [RECEIVE REPLY: '{}']       SESSION ID: {}", ioiSession.getConnectString(), operationCode, ioiSession.getSessionID());
                        }
                    } catch (TrxException e) {
                        log.error(e.toString(), e);
                        setConnectStatus("N");
                        try {
                            dbManageService.setOneooneConnectStatus(mirror);
                        } catch (Exception e1) {
                            log.error(e1.toString(), e1);
                        }
                    } catch (Exception e) {
                        log.error(e.toString(), e);
                        setConnectStatus("N");
                        try {
                            dbManageService.setOneooneConnectStatus(mirror);
                        } catch (Exception e1) {
                            log.error(e1.toString(), e);
                        }
                    }
                    if (getConnectStatus().equals("N")) {
                        disconnect();
                        start();
                    }
                }
            }
        }, 30 * 1000, 30 * 1000);
    }

    private void connect() {
        try {
            ioiSession = Transceiver.createSession("RFIDTUNER",
                    Session.SESSION_INNER_STATION_MODE |
                            Session.SESSION_PUSH_DELIVERY_MODE);

            String stationXip = getStationXIp();
            int stationXport = getStationXPort();

            //세션 연결
            log.debug("try connect stationX Server..." + stationXip + " : " + stationXport);
            ioiSession.connect(stationXip + ":" + stationXport);
            log.debug("Session connected...");
            setConnectStatus("Y");
            dbManageService.setOneooneConnectStatus(this);
        } catch (Exception e) {
            log.error(e.toString());
//            log.error(e.toString(), e);
            setConnectStatus("N");
        }
    }

    //Tuner 생성
    private void tuneChannel() {
        try {
            //세션복원 기능 옵션
            ioiSession.setAutoRecovery(true);
            ioiSession.setDefaultTTL(getMesTimeout() * 1000);
            ioiSession.addMessageConsumer(new Consumer(oneooneManager));
            String stationXTuneChannel = getRfidTuneChannel();
            ioiSession.tuneUnicast(stationXTuneChannel);
            log.debug("tuneUnicast :" + stationXTuneChannel);
        } catch (TrxException e) {
            log.error(e.getMessage());
            log.error(e.toString(), e);
            if (e.getFaultCode().equals("TRX10")) {
                setConnectStatus("N");
                log.debug("101 연결 끊김");
            }
        }
    }

    public RFID_EIS_Reply_Out_Tag sendToEIS(EIS_RFID_Request_In_Tag requestTag, String eventType) {
        RFID_EIS_Reply_Out_Tag replyTag = new RFID_EIS_Reply_Out_Tag();
        try {
            String opCode = "";
            if (SEND_MES_START.equals(eventType)) {
                opCode = CommonControlCode.OPCODE_EISStartRequest;
            } else if ("REWRITE_SUCCESS".equals(eventType)) {
                opCode = CommonControlCode.OPCODE_EISReWriteSuccess;
            } else {
                opCode = CommonControlCode.OPCODE_EISEndRequest;
            }

            voidToNull(requestTag);
            log.info("[RFID M/W => EIS({})] [SEND REQUEST: '{}({})']        SESSION ID: {}", ioiSession.getConnectString(), opCode, eventType, ioiSession.getSessionID());
            log.info("[RFID M/W => EIS({})] REQUEST DATA: {}", requestTag.toString());
            log.info("MES STATION_X_ID: {}  STATION_X_IP: {}  MES STATION_X_PORT: {} ", mirror.getStationXId(), mirror.getStationXIp(), mirror.getStationXPort());
            log.info("[{}] READER IP: {}  RES ID: {}  CHANNEL: {}", eventType, requestTag.reader_ip, requestTag.res_id, requestTag.resv_flag_5);
            log.info("----- ANTENNA1 => TAG UID: {}  MAGAZINE ID: {}  LOT ID: {}  ", requestTag.uid_1, requestTag.magazine_id_1, requestTag.lot_id_1);
            log.info("----- ANTENNA2 => TAG UID: {}  MAGAZINE ID: {}  LOT ID: {}  ", requestTag.uid_2, requestTag.magazine_id_2, requestTag.lot_id_2);
            log.info("----- ANTENNA3 => TAG UID: {}  MAGAZINE ID: {}  LOT ID: {}  ", requestTag.uid_3, requestTag.magazine_id_3, requestTag.lot_id_3);
            log.info("----- ANTENNA4 => TAG UID: {}  MAGAZINE ID: {}  LOT ID: {}  \n", requestTag.uid_4, requestTag.magazine_id_4, requestTag.lot_id_4);

            String stationXMESChannel = getStationXChannel();
            //메시지 생성 및 송신 Channel, TTl , DeliveryMode, Data 설정
            Message msg = ioiSession.createMessage();
            msg.putProperty(CommonControlCode.XGEN_TAG_VERSION, CommonControlCode.XGEN_VERSION);
            msg.putProperty(CommonControlCode.XGEN_TAG_MODULE, "EIS");
            msg.putProperty(CommonControlCode.XGEN_TAG_INTERFACE, "EIS");
            msg.putProperty(CommonControlCode.XGEN_TAG_OPERATION, opCode);
            msg.putProperty(CommonControlCode.XGEN_TAG_HOSTNAME, InetAddress.getLocalHost().getHostName());
            msg.putProperty(CommonControlCode.XGEN_TAG_HOSTADDR, InetAddress.getLocalHost().getHostAddress());

            StreamTransformer former = new StreamTransformerImpl();
            serialize_EIS_RFID_Request_In_Tag(former, requestTag);
            msg.putData(former.getBytes());
            msg.putChannel(stationXMESChannel);
            msg.putDeliveryMode(DeliveryType.REQUEST);
            msg.putTTL(getMesTimeout() * 1000);

            //메시지 전송
            Message rep = ioiSession.sendRequest(msg);

            if (rep == null) {
                log.debug("[OPERATION CODE: '{}'] NO REPLY FROM EIS({})     SESSION ID: {}", opCode, ioiSession.getConnectString(), ioiSession.getSessionID());
                replyTag.h_status_value = MES_EVENT_FAIL;
                replyTag.h_msg = "reply data is null";
                return replyTag;
            } else {
                log.info("[EIS({}) => RFID M/W] [RECEIVE REPLY: '{}']       SESSION ID: {}", ioiSession.getConnectString(), opCode, ioiSession.getSessionID());
                former = new StreamTransformerImpl((byte[]) rep.getData());
                transform_RFID_EIS_Reply_Out_Tag(former, replyTag);
                log.debug("[EIS({}) => RFID M/W] [REPLY MESSAGE]  {}", ioiSession.getConnectString(), replyTag.toString());
                return replyTag;
            }

        } catch (TrxException e) {
            String event = "";
            if (SEND_MES_START.equals(eventType)) {
                event = "START FAIL";
            } else if ("REWRITE_SUCCESS".equals(eventType)) {
                event = "REWRITE_SUCCESS SEND FAIL";
            } else {
                event = "END FAIL";
            }

            log.info("[{}] READER IP: {}  RES ID: {}  CHANNEL: {}", event, requestTag.reader_ip, requestTag.res_id, requestTag.resv_flag_5);
            log.error("ERR CODE:" + e.getErrorCode() + " ERR MSG:" + e.getMessage());
            log.error("ERROR CODE: {}  ERROR MESSAGE: {} \n", e.getErrorCode(), e.getMessage());

            if (e.getErrorCode() == 11) {
                replyTag.h_status_value = MES_EVENT_NO_RESPONSE;
            } else {
                replyTag.h_status_value = MES_EVENT_FAIL;
            }
            replyTag.h_msg = e.getMessage();
            return replyTag;
        } catch (Exception e) {
            log.error(e.toString());
            e.printStackTrace();
            String message = String.format("[M/W ERROR] EIS로 %s 이벤트 전송 실패했습니다. 101 연결 상태 확인 바랍니다. :  %s", eventType, e.getMessage());
            replyTag.h_status_value = MES_EVENT_FAIL;
            replyTag.h_msg = message;
            return replyTag;
        }
    }

    /**
     * reader information send to EIS
     *
     * @param requestInReader
     * @param eventType
     * @return
     * @throws Exception
     */
    public RFID_EIS_Reply_Out_Tag sendToEIS(RFID_EIS_Change_Resource_Info_In requestInReader, String eventType) throws Exception {
        RFID_EIS_Reply_Out_Tag replyTag = new RFID_EIS_Reply_Out_Tag();

        try {
            String opCode = eventType;

            log.info("[RFID M/W => EIS({})] [SEND REQUEST: '{}']        SESSION ID: {}", ioiSession.getConnectString(), opCode, ioiSession.getSessionID());
            log.info("MES STATION_X_ID: {}  STATION_X_IP: {}  MES STATION_X_PORT: {} ", mirror.getStationXId(), mirror.getStationXIp(), mirror.getStationXPort());
            log.info("[{}] READER IP: {}  RES ID: {} CONNECTION: {}  CHANNEL CNT: {}  MES USE YN: {}\n", opCode, requestInReader.reader_ip, requestInReader.res_id, requestInReader.reader_status, requestInReader.channel, requestInReader.mes_use_flag);

            String stationXMESChannel = getStationXChannel();
            //메시지 생성 및 송신 Channel, TTl , DeliveryMode, Data 설정
            Message msg = ioiSession.createMessage();
            msg.putProperty(CommonControlCode.XGEN_TAG_VERSION, CommonControlCode.XGEN_VERSION);
            msg.putProperty(CommonControlCode.XGEN_TAG_MODULE, "EIS");
            msg.putProperty(CommonControlCode.XGEN_TAG_INTERFACE, "EIS");
            msg.putProperty(CommonControlCode.XGEN_TAG_OPERATION, opCode);
            msg.putProperty(CommonControlCode.XGEN_TAG_HOSTNAME, InetAddress.getLocalHost().getHostName());
            msg.putProperty(CommonControlCode.XGEN_TAG_HOSTADDR, InetAddress.getLocalHost().getHostAddress());

            StreamTransformer former = new StreamTransformerImpl();
            serialize_RFID_EIS_Change_Resource_Info_In(former, requestInReader);
            msg.putData(former.getBytes());
            msg.putChannel(stationXMESChannel);
            msg.putDeliveryMode(DeliveryType.REQUEST);
            msg.putTTL(5 * 1000);
//            msg.putTTL(20 * 1000);
//            msg.putTTL(getMesTimeout() * 1000);

            //메시지 전송
            Message rep = ioiSession.sendRequest(msg);

            if (rep == null) {
                log.debug("[OPERATION CODE: '{}'] NO REPLY FROM EIS()       SESSION ID: {}", opCode, ioiSession.getConnectString(), ioiSession.getSessionID());
                replyTag.h_status_value = MES_EVENT_FAIL;
                replyTag.h_msg = "data is null";
                return replyTag;
            } else {
                log.info("[EIS({}) => RFID M/W] [RECEIVE REPLY: '{}']       SEESION ID: {}", ioiSession.getConnectString(), opCode, ioiSession.getSessionID());
                former = new StreamTransformerImpl((byte[]) rep.getData());
                transform_RFID_EIS_Reply_Out_Tag(former, replyTag);
                log.debug("[EIS({}) => RFID M/W] [REPLY MESSAGE]  {}", ioiSession.getConnectString(), replyTag.toString());
                return replyTag;
            }
        } catch (TrxException e) {
            log.info("[{}] READER IP: {}  RES ID: {} ", String.format("%s FAIL", eventType), requestInReader.reader_ip, requestInReader.res_id);
            log.error("ERROR CODE: {}  ERROR MESSAGE: {} \n", e.getErrorCode(), e.getMessage());

            if (e.getErrorCode() == 11) {
                replyTag.h_status_value = MES_EVENT_NO_RESPONSE;
            } else {
                replyTag.h_status_value = MES_EVENT_FAIL;
            }
            replyTag.h_msg = e.getMessage();
            replyTag.h_msg_code = e.getFaultCode();
            return replyTag;
        }
    }

    private void voidToNull(EIS_RFID_Request_In_Tag requestTag) {

        if (requestTag.reader_ip != null && requestTag.reader_ip.equals("")) {
            requestTag.reader_ip = null;
        }

        if (requestTag.res_id != null && requestTag.res_id.equals("")) {
            requestTag.res_id = null;
        }

        if (requestTag.uid_1 != null && requestTag.uid_1.equals("")) {
            requestTag.uid_1 = null;
        }

        if (requestTag.uid_2 != null && requestTag.uid_2.equals("")) {
            requestTag.uid_2 = null;
        }
        if (requestTag.uid_3 != null && requestTag.uid_3.equals("")) {
            requestTag.uid_3 = null;
        }
        if (requestTag.uid_4 != null && requestTag.uid_4.equals("")) {
            requestTag.uid_4 = null;
        }

        if (requestTag.magazine_id_1 != null && requestTag.magazine_id_1.equals("")) {
            requestTag.magazine_id_1 = null;
        }

        if (requestTag.magazine_id_2 != null && requestTag.magazine_id_2.equals("")) {
            requestTag.magazine_id_2 = null;
        }

        if (requestTag.magazine_id_3 != null && requestTag.magazine_id_3.equals("")) {
            requestTag.magazine_id_3 = null;
        }

        if (requestTag.magazine_id_4 != null && requestTag.magazine_id_4.equals("")) {
            requestTag.magazine_id_4 = null;
        }

        if (requestTag.lot_id_1 != null && requestTag.lot_id_1.equals("")) {
            requestTag.lot_id_1 = null;
        }

        if (requestTag.lot_id_2 != null && requestTag.lot_id_2.equals("")) {
            requestTag.lot_id_2 = null;
        }
        if (requestTag.lot_id_3 != null && requestTag.lot_id_3.equals("")) {
            requestTag.lot_id_3 = null;
        }
        if (requestTag.lot_id_4 != null && requestTag.lot_id_4.equals("")) {
            requestTag.lot_id_4 = null;
        }
    }

    public void disconnect() {
        log.debug("stationX Server disconnect...");
        if (timer != null) {
            log.debug("timer cancel");
            timer.cancel();
            timer = null;
        }
        if (ioiSession != null) {
            try {
                log.debug("CONNECTION: {}  SESSION ID: {}  DISCONNECT & DESTROY START", ioiSession.getConnectString(), ioiSession.getSessionID());
                ioiSession.disconnect();
                ioiSession.destroy();
            } catch (TrxException e) {
                log.error("SESSION DISCONNECT & DESTROY FAILURE");
                log.error(e.toString(), e);
            } finally {
                ioiSession = null;
            }
        }
    }

    public void start() {
        timer = new Timer();
        init();
    }

}
