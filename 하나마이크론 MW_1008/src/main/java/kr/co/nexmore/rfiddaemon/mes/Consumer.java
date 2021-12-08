package kr.co.nexmore.rfiddaemon.mes;

import com.miracom.oneoone.transceiverx.*;
import com.miracom.oneoone.transceiverx.parser.DeliveryType;
import com.miracom.oneoone.transceiverx.parser.StreamTransformerImpl;
import kr.co.nexmore.rfiddaemon.common.CommonControlCode;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.EIS_RFID_Request_In_Tag;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.RFID_EIS_Reply_Out_Tag;

import static kr.co.nexmore.rfiddaemon.vo.mes.EISType.serialize_RFID_EIS_Reply_Out_Tag;
import static kr.co.nexmore.rfiddaemon.vo.mes.EISType.transform_EIS_RFID_Request_In_Tag;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;


@Slf4j
public class Consumer implements MessageConsumer {

    private final OneooneManager oneooneManager;

    Consumer(OneooneManager oneooneManager) {
        this.oneooneManager = oneooneManager;
    }

    @Override
    public void onUnicast(Session arg0, Message arg1) {
        // TODO Auto-generated method stub

    }

    public void onMulticast(Session ss, Message msg) {
    }

    public void onGUnicast(Session ss, Message msg) {
    }

    public void onReply(Session ss, Message req, Message rep, Object hint) {
        // Not used
    }

    public void onGMulticast(Session ss, Message msg) {
        // Not used
    }

    public void onTimeout(Session ss, Message msg) {
        // Not used
    }

    private void sendReply(Session issuer, Message req, Message rep, int errCode, String errMessage) {
        try {
            rep.putProperty(CommonControlCode.XGEN_TAG_HOSTNAME, InetAddress.getLocalHost().getHostName());
            rep.putProperty(CommonControlCode.XGEN_TAG_HOSTADDR, InetAddress.getLocalHost().getHostAddress());
            rep.putProperty(CommonControlCode.XGEN_TAG_RESULT_CODE, String.valueOf(errCode));
            rep.putProperty(CommonControlCode.XGEN_TAG_RESULT_MSG, errMessage);
            issuer.sendReply(req, rep);
        } catch (TrxException e) {
            log.error(e.getMessage());
        } catch (UnknownHostException e) {
            log.error(e.getMessage());
        }
    }

    public void onRequest(Session ss, Message msg) {
        //수신 메시지 처리			
        try {
            String operation = (String) msg.getProperty(CommonControlCode.XGEN_TAG_OPERATION);
            String version = (String) msg.getProperty(CommonControlCode.XGEN_TAG_VERSION);

            log.info("[EIS({}) => RFID M/W] [RECEIVE REQUEST: '{}']        SESSION ID: {}", ss.getConnectString(), operation, ss.getSessionID());

            if (null == version || version.trim().equals("") || !version.equals(CommonControlCode.XGEN_VERSION)) {
                String message = "Unexpected Version!";
                log.info("[RFID M/W => EIS({})] [SEND REPLY: '{}']        SESSION ID: {}", ss.getConnectString(), operation, ss.getSessionID());
                log.info("[RFID M/W => EIS({})] [REPLY MESSAGE]  {}", ss.getConnectString(), message);
                sendReply(ss, msg, msg.createReply(), CommonControlCode.XGEN_ERROR_UNEXPECTED_VERSION, message);
                return;
            }

//            if (operation.equals(CommonControlCode.OPCODE_EISWriteLotID)) {
            if (operation.equals(CommonControlCode.OPCODE_EISWriteLotIdOrEndLotId)) {
                receiveRequestHandler(ss, msg);
            } else if (operation.equals(CommonControlCode.OPCODE_EISTunerAlive)) {
                String message = "Success";
                log.info("[RFID M/W => EIS({})] [SEND REPLY: '{}']        SESSION ID: {}", ss.getConnectString(), operation, ss.getSessionID());
                log.info("[RFID M/W => EIS({})] [REPLY MESSAGE]  {}", ss.getConnectString(), message);
                sendReply(ss, msg, msg.createReply(), CommonControlCode.XGEN_SUCCESS, message);
            } else {
                if (DeliveryType.REQUEST == msg.getDeliveryMode())
                    sendReply(ss, msg, msg.createReply(), CommonControlCode.XGEN_ERROR_UNEXPECTED_OPERATION, "Unexpected Operation!");
                log.error("Unexpected Operation!(Operation:" + operation + ")");
            }
            return;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void receiveRequestHandler(final Session issuer, final Message msg) throws Exception {
        //쓰레드로 처리
        new Thread(() -> {
            try {
                String operation = (String) msg.getProperty(CommonControlCode.XGEN_TAG_OPERATION);
                RFID_EIS_Reply_Out_Tag replyOutTag = null;

                StreamTransformer former = new StreamTransformerImpl((byte[]) msg.getData());
                EIS_RFID_Request_In_Tag requestInTag = new EIS_RFID_Request_In_Tag();
                transform_EIS_RFID_Request_In_Tag(former, requestInTag);

                log.info("[EIS({}) => RFID M/W] [REQUEST MESSAGE]  {}", issuer.getConnectString(), requestInTag.toString());

                // LotEnd Request
                if (requestInTag.h_proc_step == '2' && requestInTag.resv_flag_2 == 'Y' && requestInTag.resv_field_2 != null && !"".equals(requestInTag.resv_field_2)) {
                    replyOutTag = oneooneManager.endLotId(requestInTag);
                    // write Request
                } else if (requestInTag.h_proc_step == '1') {
                    replyOutTag = oneooneManager.writeLotId(requestInTag);
                }

                if (DeliveryType.isRequest(msg.getDeliveryMode())) /* Just RequestReply */ {
                    log.info("[RFID M/W => EIS({})] [SEND REPLY: '{}']        SESSION ID: {}", issuer.getConnectString(), operation, issuer.getSessionID());
                    log.info("[RFID M/W => EIS({})] [REPLY MESSAGE]  {}", issuer.getConnectString(), replyOutTag.toString());
                    Message rep = msg.createReply();
                    former = new StreamTransformerImpl();
                    serialize_RFID_EIS_Reply_Out_Tag(former, replyOutTag);
                    rep.putData(former.getBytes());
                    byte[] array = former.getBytes();
                    String arrayString = "";
                    for (int i = 0; i < array.length; i++) {
                        arrayString = arrayString + String.format("0x%02X ", array[i]);
                    }
                    log.debug("value: " + arrayString);
                    sendReply(issuer, msg, rep, 0, "");
                }
            } catch (Exception e) {
                log.error(e.toString(), e);
            }
        }).start();
    }
}
