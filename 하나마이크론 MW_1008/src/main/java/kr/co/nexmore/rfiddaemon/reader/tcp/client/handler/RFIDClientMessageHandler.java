package kr.co.nexmore.rfiddaemon.reader.tcp.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.handler.timeout.*;
import io.netty.util.concurrent.ScheduledFuture;
import kr.co.nexmore.rfiddaemon.common.RequestCommand;
import kr.co.nexmore.rfiddaemon.reader.process.DispatchProcessBiz;
import kr.co.nexmore.rfiddaemon.reader.process.WireBondProcessBiz;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.RFIDTcpClient;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.manager.ClientLogManager;
import kr.co.nexmore.rfiddaemon.reader.process.CommonProcessBiz;
import kr.co.nexmore.rfiddaemon.reader.process.PlasmaProcessBiz;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import kr.co.nexmore.rfiddaemon.mes.OneooneManager;
import kr.co.nexmore.rfiddaemon.reader.util.CommonUtil;
import kr.co.nexmore.rfiddaemon.service.DBManageService;
import kr.co.nexmore.rfiddaemon.reader.util.TcpSetCommandUtil;
import kr.co.nexmore.rfiddaemon.vo.common.EventHistoryVO;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.RFID_EIS_Reply_Out_Tag;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.EIS_RFID_Request_In_Tag;
import kr.co.nexmore.rfiddaemon.vo.reader.ReaderVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.ResponseVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.request.CmdReqVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.request.RequestVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.CmdResVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.SyscontrolResVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.TagDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static kr.co.nexmore.rfiddaemon.common.CommonControlCode.*;
import static kr.co.nexmore.rfiddaemon.common.RequestCommand.*;
import static kr.co.nexmore.rfiddaemon.reader.tcp.client.manager.ClientManager.rfidClientsMapByIp;

@ChannelHandler.Sharable
@Scope("prototype")
@Slf4j
public class RFIDClientMessageHandler extends ChannelInboundHandlerAdapter {

    /**
     * 채널핸들러 context
     */
    private ChannelHandlerContext ctx;

    /**
     * 현재 안테나 1번(ch1) 의 tagDataVO
     */
    protected TagDataVO ant1CurrentData = null;

    /**
     * 이전 안테나 1번(ch1) 의 tagDataVO
     */
    protected TagDataVO ant1PrevData = null;

    /**
     * 현재 안테나 2번(ch2) 의 tagDataVO
     */
    private TagDataVO ant2CurrentData = null;

    /**
     * 이전 안테나 2번(ch2) 의 tagDataVO
     */
    private TagDataVO ant2PrevData = null;

    /**
     * 현재 안테나 3번(ch3) 의 tagDataVO
     */
    private TagDataVO ant3CurrentData = null;

    /**
     * 이전 안테나 3번(ch3) 의 tagDataVO
     */
    private TagDataVO ant3PrevData = null;

    /**
     * 현재 안테나 4번(ch4) 의 tagDataVO
     */
    private TagDataVO ant4CurrentData = null;

    /**
     * 이전 안테나 4번(ch4) 의 tagDataVO
     */
    private TagDataVO ant4PrevData = null;


    /**
     * 설비 별 로그를 위한 logHeader
     * ex) [EX001] EX001-2C-01(xxx.xxx.xxx.xxx)
     */
    private String logHeaderMessage;


    public TagDataVO getCurrentData(int channel) {
        if (channel == 1) {
            return ant1CurrentData;
        } else if (channel == 2) {
            return ant2CurrentData;
        } else if (channel == 3) {
            return ant3CurrentData;
        } else {
            return ant4CurrentData;
        }
    }

    public void setCurrentData(TagDataVO tagDataVO, int channel) {
        if (channel == 1) {
            ant1CurrentData = tagDataVO;
        } else if (channel == 2) {
            ant2CurrentData = tagDataVO;
        } else if (channel == 3) {
            ant3CurrentData = tagDataVO;
        } else if (channel == 4) {
            ant4CurrentData = tagDataVO;
        }
    }

    public TagDataVO getPrevData(int channel) {
        if (channel == 1) {
            return ant1PrevData;
        } else if (channel == 2) {
            return ant2PrevData;
        } else if (channel == 3) {
            return ant3PrevData;
        } else {
            return ant4PrevData;
        }
    }

    public void setPrevData(TagDataVO tagDataVO, int channel) {
        if (channel == 1) {
            ant1PrevData = tagDataVO;
        } else if (channel == 2) {
            ant2PrevData = tagDataVO;
        } else if (channel == 3) {
            ant3PrevData = tagDataVO;
        } else if (channel == 4) {
            ant4PrevData = tagDataVO;
        }
    }


    /**
     * 안테나1 Detach 감지를 위한 scheduledFuture
     */
    private ScheduledFuture ch1ScheduledFuture;

    /**
     * 안테나2 Detach 감지를 위한 scheduledFuture
     */
    private ScheduledFuture ch2ScheduledFuture;

    /**
     * 안테나3 Detach 감지를 위한 scheduledFuture
     */
    private ScheduledFuture ch3ScheduledFuture;

    /**
     * 안테나4 Detach 감지를 위한 scheduledFuture
     */
    private ScheduledFuture ch4ScheduledFuture;

    /**
     * RFIDTcpClient
     */
    private final RFIDTcpClient client;

    /**
     * 리더 정보 readerVO
     */
    private ReaderVO reader;

    public void setReaderVO(ReaderVO readerVO) {
        String prevPRName = this.reader.getProcessName();

        this.reader = readerVO;

        if (!prevPRName.equals(this.reader.getProcessName())) {
            createProcess();
        }
    }

    /**
     * 리더의 응닶 값 처리를 위한 blockingQueue
     */
    private final ArrayBlockingQueue<Map> blockingQueue = new ArrayBlockingQueue<Map>(1000);

    /**
     * 현재 핸들러의 blockingQueue 를 return 한다.
     *
     * @return blockingQueue
     */
    public ArrayBlockingQueue<Map> getBlockingQueue() {
        return blockingQueue;
    }

    private final DBManageService dbManageService;
    private final OneooneManager oneooneManager;

    /**
     * 설비 별 로그를 위한 logger
     */
    private final ClientLogManager clientLogger;

    /**
     * 공정별 로직 처리 class 변수
     */
    private CommonProcessBiz process;

    public void changePollingThread(boolean flag) {
        this.process.changePolling(flag);
    }

    public CommonProcessBiz getProcess() {
        return this.process;
    }

    public boolean plasmaPaired() {
        if (this.process.getClass() == PlasmaProcessBiz.class) {
            return ((PlasmaProcessBiz) this.process).getPaired();
        } else {
            return true;
        }
    }

    public void pairRelease(String readerMac) {
        if (this.process.getClass() == PlasmaProcessBiz.class) {
            ((PlasmaProcessBiz) this.process).pairRelease(readerMac);
        }
    }

    /**
     * 생성자
     *
     * @param client
     * @param dbManageService
     * @param oneooneManager
     * @param clientLogManager
     */
    public RFIDClientMessageHandler(RFIDTcpClient client, DBManageService dbManageService, OneooneManager oneooneManager, ClientLogManager clientLogManager) {
        this.client = client;
        this.reader = client.getReaderVO();
        this.dbManageService = dbManageService;
        this.oneooneManager = oneooneManager;
        this.clientLogger = clientLogManager;
        this.logHeaderMessage = String.format("[%s] %s(%s)", reader.getEquipmentName(), reader.getReaderName(), reader.getReaderIp());
        this.init();
        log.debug("[{}] {}({}) PROCESS CLASS: {}({})", reader.getEquipmentName(), reader.getReaderName(), reader.getReaderIp(), this.process.toString(), this.process.hashCode());
    }

    public void init() {
        createProcess();
        dataInitialize();
    }

    public void lotStatusCheck() {
        process.lotStatusCheck();
    }

    public void forceLotAdd(String lotId, String magazineId, int channel) {
        if (this.process.getClass() == WireBondProcessBiz.class) {
            ((WireBondProcessBiz) this.process).forceLotAdd(lotId, magazineId, channel);
        }
    }

    /**
     * Daemon 최초 기동 시 Biz 로직을 다루는 process Class 생성
     */
    public void createProcess() {
        Class processClass = null;
        if (this.process != null) {
            processClass = this.process.getClass();
        }
        if ("PLASMA".equals(this.reader.getProcessName().toUpperCase())) {
            if (processClass != PlasmaProcessBiz.class) {
//                this.process = new PlasmaProcessBiz(dbManageService, oneooneManager, clientLogger, client);
                this.process = getPairProcess();
            }
        } else if ("WIRE BOND".equals(reader.getProcessName().toUpperCase())) {
            if (processClass != WireBondProcessBiz.class) {
                this.process = new WireBondProcessBiz(dbManageService, oneooneManager, clientLogger, client);
            }
        } else if ("DISPATCHING STATION".equals(reader.getProcessName().toUpperCase())) {
            if (processClass != DispatchProcessBiz.class) {
                this.process = new DispatchProcessBiz(dbManageService, oneooneManager, clientLogger, client);
            }
        } else {
            if (processClass != CommonProcessBiz.class) {
                this.process = new CommonProcessBiz(dbManageService, oneooneManager, clientLogger, client);
            }
        }
    }

    /**
     * 플라즈마 같은 경우 한 설비에 2개의 리더기가 존재 함.
     * Biz 로직을 다루는 process Class를 pairing(공유) 하도록 setting
     * 같은 설비명으로 생성된 process Class가 없으면 새로운 process Class를 생성
     *
     * @return PlasmaProcessBiz
     */
    public PlasmaProcessBiz getPairProcess() {
        PlasmaProcessBiz process = null;
        if (rfidClientsMapByIp != null && !rfidClientsMapByIp.isEmpty()) {
            for (String key : rfidClientsMapByIp.keySet()) {
                ReaderVO tmpReader = rfidClientsMapByIp.get(key).getReaderVO();
                String tmpProcessName = tmpReader.getProcessName();
                String tmpEquipmentName = tmpReader.getEquipmentName();
                String tmpReaderIp = tmpReader.getReaderIp();
                // 공정명, 설비명 같은 경우
                if (this.reader.getProcessName().equals(tmpProcessName) && this.reader.getEquipmentName().equals(tmpEquipmentName)) {
                    if (this.reader.getReaderIp() != tmpReaderIp) {   // 리더 ip 가 다르면
                        process = (PlasmaProcessBiz) rfidClientsMapByIp.get(key).getMessageHandler().getProcess();  // 해당 리더 client 의 processBiz Class 가져옴
                        if (process != null) {
                            process.setPairClient(this.client);
                        }
                    }
                }
            }
        }
        if (process == null) {
            process = new PlasmaProcessBiz(dbManageService, oneooneManager, clientLogger, client);
        }
        return process;
    }


    /**
     * Daemon 최초 기동 시 client 의 채널 별 마지막 Data를 DB에서 조회
     * 현재 값 or 이전 값으로 세팅
     */
    public void dataInitialize() {
        writeLog("DATA INITIALIZE :: START");
        try {
            LinkedHashMap ch1Data = dbManageService.getLastReaderData(this.reader, 1);
            LinkedHashMap ch2Data = dbManageService.getLastReaderData(this.reader, 2);

            settingCurrentData(ch1Data);
            settingCurrentData(ch2Data);

            if (ant1CurrentData != null) {
                log.debug(ant1CurrentData.toString());
                writeLog(String.format("[CH1 DATA] TAG UID: %s  MAGAZINE ID: %s", ant1CurrentData.getTagUid(), ant1CurrentData.getData()));
            }
            if (ant2CurrentData != null) {
                log.debug(ant2CurrentData.toString());
                writeLog(String.format("[CH2 DATA] TAG UID: %s  MAGAZINE ID: %s", ant2CurrentData.getTagUid(), ant2CurrentData.getData()));
            }

            if (this.reader.getChannelCnt() == 4) {
                LinkedHashMap ch3Data = dbManageService.getLastReaderData(this.reader, 3);
                LinkedHashMap ch4Data = dbManageService.getLastReaderData(this.reader, 4);

                settingCurrentData(ch3Data);
                settingCurrentData(ch4Data);

                if (ant3CurrentData != null) {
                    log.debug(ant3CurrentData.toString());
                    writeLog(String.format("[CH3 DATA] TAG UID: %s  MAGAZINE ID: %s", ant3CurrentData.getTagUid(), ant3CurrentData.getData()));
                }
                if (ant4CurrentData != null) {
                    log.debug(ant4CurrentData.toString());
                    writeLog(String.format("[CH4 DATA] TAG UID: %s  MAGAZINE ID: %s", ant4CurrentData.getTagUid(), ant4CurrentData.getData()));
                }
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
            writeLog(e.toString());
        }
        writeLog("DATA INITIALIZE :: END");
    }

    /**
     * Daemon 최초 기동 시 client 의 채널 별 마지막 Data를 현재 값으로 세팅
     *
     * @param initData DB 상 마지막 데이터
     * @return TagDataVO
     */
    public void settingCurrentData(LinkedHashMap initData) {
        if (initData == null) {
            return;
        }
        log.debug(initData.toString());
        int eventLogId = Integer.parseInt(initData.get("eventLogId").toString());
        int eventChannel = Integer.parseInt(initData.get("eventChannel").toString());
        String eventType = initData.get("eventType").toString();

        String tagUid = null;
        String magazineId = null;

        if (initData.get("tagUid") != null) {
            tagUid = initData.get("tagUid").toString();
        }
        if (initData.get("magazineId") != null) {
            magazineId = initData.get("magazineId").toString();
        }

        int retVal = Integer.parseInt(initData.get("retVal").toString());
        long regDate = 0;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            Date date = sdf.parse(initData.get("regDate").toString());
            regDate = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TagDataVO tagDataVO = new TagDataVO();
        tagDataVO.setAnt(eventChannel);
        tagDataVO.setTagUid(tagUid);
        tagDataVO.setData(magazineId);
        tagDataVO.setAttachTime(regDate);

        if (eventType != null) {
            if ((eventType.equals(SEND_MES_START) && retVal != 2)) {    // START 상태로 세팅 후 떨어짐 감지 대기   retVal != 2 => 응답대기 상태가 아닐 경우
                tagDataVO.setStartProcess(true);
                tagDataVO.setEventStatus(SEND_MES_START);
                setCurrentData(tagDataVO, eventChannel);

            } else if (eventType.equals(SEND_MES_END)) {    // "SEND_MES_END" 완료 시 이전 데이터로 setting
                if (retVal == 2) {
                    tagDataVO.setStartProcess(true);
                    tagDataVO.setEventStatus(SEND_MES_START);
                    setCurrentData(tagDataVO, eventChannel);
                } else {
                    tagDataVO.setStartProcess(true);
                    tagDataVO.setEndProcess(true);
                    tagDataVO.setEventStatus(SEND_MES_END);
                    setPrevData(tagDataVO, eventChannel);
                }
            }
        }
    }

    /**
     * 설비 별 로그를 저장한다.
     *
     * @param logMessage 로그 남길 메세지
     */
    public void writeLog(String logMessage) {
        clientLogger.trace(logHeaderMessage, logMessage, reader.getEquipmentName());
    }

    /**
     * MES(EIS)에서 write 명령 시 실행되는 메서드
     * lotId를 세팅한다.
     *
     * @param lotId
     * @param antNum
     */
    public void setLotId(String lotId, int antNum) {
        if (antNum == 1) {
            ant1CurrentData.setLotId(lotId);
        } else if (antNum == 2) {
            ant2CurrentData.setLotId(lotId);
        } else if (antNum == 3) {
            ant3CurrentData.setLotId(lotId);
        } else if (antNum == 4) {
            ant4CurrentData.setLotId(lotId);
        }
    }

    public void setLotStatusEnd(String lotId) {
        process.setLotStatusEnd(lotId);
    }

    public RFID_EIS_Reply_Out_Tag setLotStatusEnd(EIS_RFID_Request_In_Tag requestInTag) {
        return ((WireBondProcessBiz) process).setLotStatusEnd(requestInTag);
    }


    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRegistered(ChannelHandlerContext) (io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }


    /**
     * 서버에 클라이언트가 커넥션이 이뤄지면 실행되는 메서드
     *
     * @param ctx 채널핸들러
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelActive(ChannelHandlerContext) (io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("[ChannelActive] {}", this.process);
        log.info("[ChannelActive] {}", ctx.channel().remoteAddress().toString());
        this.ctx = ctx;

        writeLog("연결이 활성화 되었습니다.");

        dbManageService.setReaderConnectStatus("Y", reader.getReaderMac()); // reader status connect 상태로 변경
        process.getReaderConfig(this.client, reader.getReaderMac());    // 현재 reader config 값 DB 조회하여 setting

        // connect log insert
        TagDataVO tagDataVO = new TagDataVO();
        tagDataVO.setEventStatus(READER_CONNECT);
        process.insertEventHistory(this.client, tagDataVO, null);


        new Thread(() -> {
            process.intervalSynchronize(this.client);  // interval 체크하여 변경
            process.buzzerSynchronize(this.client);
            process.firmwareCheck(this.client);
        }).start();

        detachScheduleStart(0); // 모든 detach 감지 schedule start
    }


    public void setReaderStatus(TagDataVO tagDataVO) {
        process.setReaderStatus(this.client, tagDataVO, null);
    }

    /**
     * 채널을 읽을 때 동작할 코드를 정의
     *
     * @param ctx 채널핸들러
     * @param msg 수신받아 변환된 object
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRead(ChannelHandlerContext, Object) (io.netty.channel.ChannelHandlerContext,
     * java.lang.Object)
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (this.process == null) {
            log.debug("[ChannelRead] this process is null");
            return;
        }
        ResponseVO responseVO = (ResponseVO) msg;
        if (responseVO != null) {
            byte command = responseVO.getHeaderVO().getCommand();
            // TagData packet 일 경우
            if (command == TAG_PACKET.getCommand()) {
                TagDataVO receiveTagData = (TagDataVO) responseVO.getBody(); // 수신한 tagDataVO
                this.process.tagEventHandler(this.client, receiveTagData);
                // keepalive packet 일 경우
            } else if (command == KEEPALIVE_PACKET.getCommand()) {
                // system_control response packet 일 경우 (production information, reader register value)
            } else if (command == SYS_CONTROL.getCommand()) {
                SyscontrolResVO syscontrolResVO = (SyscontrolResVO) responseVO.getBody();
                // production information
                if (syscontrolResVO.getSubCom() == SYS_CONTROL_GET_PRODUCT_INFO.getSubCommand()) {
                    LinkedHashMap result = new LinkedHashMap();
                    result.put("action", RequestCommand.getActionName(command, syscontrolResVO.getSubCom()));
                    result.put("result", syscontrolResVO);
                    result.put("success", true);
                    this.blockingQueue.put(result);
                    // reader register value
                } else if (syscontrolResVO.getSubCom() == SYS_CONTROL_READ_REGISTER.getSubCommand()) {
                    LinkedHashMap result = new LinkedHashMap();
                    String action = RequestCommand.getActionName(syscontrolResVO.getComName(), syscontrolResVO.getSubComName());
                    if (action.equals(SYS_CONF_OPERATNG_MODE.getActionName())) {
                        String currentMode = syscontrolResVO.getRegisterValue() == 0 ? "CONTINUE" : "TRIGGER";
                        writeLog(String.format("%s REGISTER VALUE => MODE: %s INTERVAL: %s초", action, currentMode, String.valueOf(syscontrolResVO.getTransferTime() / 10.0)));
                    } else {
                        writeLog(String.format("%s REGISTER VALUE: %d", action, syscontrolResVO.getRegisterValue()));
                    }
                    result.put("action", action);
                    result.put("result", syscontrolResVO);
                    this.blockingQueue.put(result);
                }
                // command response packet 일 경우(command 시 해당 command 가 성공했는지 0 = 성공, 1 = 실패)
            } else if (command == COMMAND_RESPONSE_PACKET.getCommand()) {
                CmdResVO cmdResVO = (CmdResVO) responseVO.getBody();
                LinkedHashMap result = new LinkedHashMap();
                result.put("action", RequestCommand.getActionName(cmdResVO.getComName(), cmdResVO.getSubComName()));
                result.put("success", (cmdResVO.getResult() == 0x00) ? true : false);
                writeLog(String.format("%s RESULT => %s", result.get("action"), result.get("success")));
                this.blockingQueue.put(result);

            } else {
                writeLog("Data Packet 이 올바르지 않습니다.");
                writeLog(String.format("Receive Data: %s", responseVO.toString()));
                return;
            }
        }
    }


    /**
     * 연결된 채널 데이터를 모두 읽었을 때 동작할 코드를 정의
     *
     * @param ctx 채널핸들러
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelReadComplete(ChannelHandlerContext) (io.netty.channel.ChannelHandlerContext,
     * java.lang.Object)
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // 연결된 채널 데이터를 모두 읽었을 떄 이벤트 발생
        ctx.flush();
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        writeLog("userEventTriggered START");
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                writeLog("READER_IDLE");
                writeLog(String.format("연결을 종료합니다."));
                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                writeLog("WRITE_IDLE");
            }
        }
    }

    /**
     * channelActive와 반대로 채널이 비활성화 되었을 때 발생.
     * 해당 이벤트가 발생한 이후에는 채널에 해당 입출력 작업을 수행 못한다.
     *
     * @param ctx 채널핸들러
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelInactive(ChannelHandlerContext) (io.netty.channel.ChannelHanderContext, java.lang.Object)
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        writeLog("연결이 끊어졌습니다.");
        detachScheduleDestroy();
        dbManageService.setReaderConnectStatus("N", reader.getReaderMac());
        TagDataVO tagDataVO = new TagDataVO();
        tagDataVO.setEventStatus(READER_DISCONNECT);
        process.insertEventHistory(this.client, tagDataVO, null);
        ctx.close();
    }

    /**
     * channelRegistered의 이벤트와 반대로 채널이 이벤트 루프에서 제거 되었을 때 발생.
     *
     * @param ctx 채널핸들러
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelUnregistered(ChannelHandlerContext) (io.netty.channel.ChannelHandlerContext, java.lang.Oject)
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        if (ctx.channel() != null) {
            ctx.channel().close();
        }
    }

    /**
     * exception 발생 시 실행되는 메서드
     *
     * @param ctx   채널핸들러
     * @param cause Throwable
     * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(ChannelHandlerContext, Throwable) (io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        writeLog("#### exceptionCaught!!!!!");
        log.error("##### exceptionCaught #####");
        log.error(cause.toString());
        cause.printStackTrace();
        writeLog(cause.toString());
        writeLog(cause.getMessage());
        if (cause.getCause() instanceof ReadTimeoutException) {
            writeLog("일정 시간 지연으로 리더 연결을 종료합니다.");
            if (ctx != null) {
                ctx.close();
            }
        } else {
            writeLog(cause.getMessage());
            ctx.close();
        }
    }

    /**
     * 연결된 Channel(리더기)에 COMMAND 를 전송한다.
     *
     * @param requestVO
     */
    public void sendCommand(RequestVO requestVO) {
        CmdReqVO cmdReqVO = (CmdReqVO) requestVO.getBody();
        String commandName = RequestCommand.getActionName(cmdReqVO.getCommand(), cmdReqVO.getSubCommand());
        writeLog(String.format("리더에 [%s] COMMAND를 전송합니다.", commandName));
        this.ctx.writeAndFlush(requestVO).addListener(future -> {
            writeLog(String.format("[%s] COMMAND 전송 결과: %s", commandName, future.isSuccess() ? "SUCCESS" : "FAILURE[" + future.cause().getMessage() + "]"));
        });
    }

    /**
     * Reader 에 aliveCheck command 전송
     */
    public void aliveCheck() {
        writeLog("READER ALIVE STATUS CHECK");
        sendCommand(TcpSetCommandUtil.getRegister(SYS_CONF_KEEPALIVE_INTERVAL, reader.getReaderMac()));
    }

    public ScheduledFuture detachScheduler(TagDataVO tagDataVO) {
        int startTime = 0;
        int readerInterval = (int) (reader.getReaderInterval() * 1000);

        if (reader.getChannelCnt() == 2) {
            startTime = readerInterval + 1000;  // 2채널일 경우 + 1초
        } else {
            startTime = readerInterval + 2000;  // 4채널일 경우 + 2초
        }

        if (tagDataVO != null) {
            // D/A 떨어짐 감지 2채널 2배
            if ("DIE ATTACH".equalsIgnoreCase(reader.getProcessName()) && tagDataVO.getAnt() == 2) {
                startTime = startTime * 2;
            }
        }

        ScheduledFuture scheduledFuture = ctx.executor().schedule(() -> {
            this.tagDetachCheck(tagDataVO);
        }, startTime, TimeUnit.MILLISECONDS);
        return scheduledFuture;
    }

    //    public synchronized void detachScheduleStart(int channel) {
    public void detachScheduleStart(int channel) {
        if (channel == 1) {
            if (ch1ScheduledFuture != null) {
                ch1ScheduledFuture.cancel(true);
                ch1ScheduledFuture = null;
            }
            ch1ScheduledFuture = detachScheduler(ant1CurrentData);
        } else if (channel == 2) {
            if (ch2ScheduledFuture != null) {
                ch2ScheduledFuture.cancel(true);
                ch2ScheduledFuture = null;
            }
            ch2ScheduledFuture = detachScheduler(ant2CurrentData);
        } else if (channel == 3) {
            if (ch3ScheduledFuture != null) {
                ch3ScheduledFuture.cancel(true);
                ch3ScheduledFuture = null;
            }
            ch3ScheduledFuture = detachScheduler(ant3CurrentData);
        } else if (channel == 4) {
            if (ch4ScheduledFuture != null) {
                ch4ScheduledFuture.cancel(true);
                ch4ScheduledFuture = null;
            }
            ch4ScheduledFuture = detachScheduler(ant4CurrentData);
        } else {
            detachScheduleStart(1);
            detachScheduleStart(2);
            if (reader.getChannelCnt() == 4) {
                detachScheduleStart(3);
                detachScheduleStart(4);
            }
        }
    }

    public void detachScheduleDestroy() {

        if (ch1ScheduledFuture != null) {
            ch1ScheduledFuture.cancel(false);
            ch1ScheduledFuture = null;
        }
        if (ch2ScheduledFuture != null) {
            ch2ScheduledFuture.cancel(false);
            ch2ScheduledFuture = null;
        }
        if (ch3ScheduledFuture != null) {
            ch3ScheduledFuture.cancel(false);
            ch3ScheduledFuture = null;
        }
        if (ch4ScheduledFuture != null) {
            ch4ScheduledFuture.cancel(false);
            ch4ScheduledFuture = null;
        }
    }


    /**
     * detach 감지 메서드
     * END EVENT 처리
     *
     * @param tagDataVO
     */
//    public synchronized void tagDetachCheck(TagDataVO tagDataVO) {
    public void tagDetachCheck(TagDataVO tagDataVO) {
        long currentTime = System.currentTimeMillis();  // 현재시간 millisecond

        if (tagDataVO != null && tagDataVO.getTagUid() != null && !"".equals(tagDataVO.getTagUid())) {

            long elapsedTime = currentTime - tagDataVO.getAttachTime();
            double intervalTime = reader.getReaderInterval();

            if (elapsedTime / 1000.000 > intervalTime / 1.000) {
                writeLog(String.format("CHANNEL-%d ELAPSED TIME CHECK: %s초", tagDataVO.getAnt(), String.valueOf(elapsedTime / 1000.000)));
                aliveCheck();
                ctx.executor().schedule(() -> {
                    if (ctx.channel().isActive()) {
                        process.detachHandler(client, tagDataVO);
                    }
                }, 500, TimeUnit.MILLISECONDS);
            }
        }
    }
}
