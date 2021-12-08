package kr.co.nexmore.rfiddaemon.reader.tcp.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import kr.co.nexmore.rfiddaemon.mes.OneooneManager;
import kr.co.nexmore.rfiddaemon.service.DBManageService;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.manager.ClientLogManager;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.handler.*;
import kr.co.nexmore.rfiddaemon.vo.reader.ReaderVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.request.RequestVO;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * RFID Client
 * 리더기와 연결하는 Client (1:1 connection)
 */
@Slf4j
public class RFIDTcpClient {

    private final DBManageService dbManageService;

    private final OneooneManager oneooneManager;

    private final ClientLogManager clientLogger;


    private ExecutorService executorService = null;
    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private ChannelFuture future;

    private ReaderVO readerVO;
    private RFIDClientMessageHandler messageHandler;
    private String logHeaderMessage;
    private RFIDTcpClient client;

    /**
     * 활성화 여부
     */
    private boolean actived = false;

    /**
     * 활성화 여부
     *
     * @return boolean
     **/
    public boolean isActived() {
        return actived;
    }

    public ReaderVO getReaderVO() {
        return this.readerVO;
    }

    public void writeLog(String logMessage) {
        clientLogger.trace(logHeaderMessage, logMessage, readerVO.getEquipmentName());
        log.debug(logMessage);
    }

    public RFIDClientMessageHandler getMessageHandler() {
        return messageHandler;
    }


    public RFIDTcpClient(ReaderVO readerVO, DBManageService dbManageService, OneooneManager oneooneManager, ClientLogManager clientLogManager) {
        this.client = this;
        this.readerVO = readerVO;
        this.dbManageService = dbManageService;
        this.oneooneManager = oneooneManager;
        this.clientLogger = clientLogManager;
        this.logHeaderMessage = String.format("[%s] %s(%s)", readerVO.getEquipmentName(), readerVO.getReaderName(), readerVO.getReaderIp());
        this.messageHandler = new RFIDClientMessageHandler(client, dbManageService, oneooneManager, clientLogger);
    }

    public void setReaderVO(ReaderVO readerVO) {
        this.readerVO = readerVO;
        RFIDClientMessageDecoder decoder = future.channel().pipeline().get(RFIDClientMessageDecoder.class);
        messageHandler.setReaderVO(this.readerVO);
        if (decoder != null) {
            decoder.setReaderVO(this.readerVO);
        }
    }

    public synchronized void start() {
        writeLog(String.format("CLIENT START TO CONNECTION => %s:%d", readerVO.getReaderIp(), readerVO.getReaderPort()));
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Thread.currentThread().setName(String.format("%s-%s(%s)", Thread.currentThread().getName(), getReaderVO().getReaderName(), getReaderVO().getReaderIp()));
            try {
                bootstrap = new Bootstrap();
                group = new NioEventLoopGroup(1);
                bootstrap.group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3 * 1000)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline p = ch.pipeline();
                                p.addLast("decoder", new RFIDClientMessageDecoder(client, readerVO));
                                p.addLast("encoder", new RFIDClientMessageEncoder());
                                p.addLast("aliveCheckHandler", new RFIDClientAliveCheckHandler(400));
//                                p.addLast("handler", messageHandler = new RFIDClientMessageHandler(client, readerControlService, dbManageService, oneooneManager, channelGroup, clientLogger));
                                p.addLast(group, "handler", messageHandler);
                                p.addFirst("idleStateHandler", new IdleStateHandler(32, 0, 0));
                            }
                        });
                future = bootstrap.connect(readerVO.getReaderIp(), readerVO.getReaderPort()).awaitUninterruptibly().addListener(future -> {
                    writeLog(String.format("RFIDClient Connection: %s:%d  => %s", readerVO.getReaderIp(), readerVO.getReaderPort(), future.isSuccess() ? "SUCCESS" : "FAILURE"));
                    actived = future.isSuccess();
                });
                writeLog(messageHandler.toString());

                while (true) {
                    // channel active 감지
                    if (executorService.isShutdown()) {
                        break;
                    }
                    if (future.channel().isActive()) {
                        actived = true;
                        TimeUnit.MILLISECONDS.sleep(10000);
                    } else {
                        actived = false;
                        TimeUnit.MILLISECONDS.sleep(10000);
                        ReaderVO vo = dbManageService.getReaderInfo(readerVO.getReaderMac());
                        if (vo == null) {
                            writeLog("일치하는 리더기가 없습니다.");
                            shutdown();
                            break;
                        } else {
                            readerVO = vo;
                            future.channel().close().addListener(future -> {
                                writeLog(String.format("CURRENT CONNECTION CHANNEL CLOSE => %s", future.isSuccess() ? "SUCCESS" : "FAILURE[" + future.cause().getMessage() + "]"));
                            });
                            future = bootstrap.connect(readerVO.getReaderIp(), readerVO.getReaderPort()).awaitUninterruptibly().addListener(future -> {
                                writeLog(String.format("TRY RECONNECTION TERM: 10 SECONDS => %s", future.isSuccess() ? "SUCCESS" : "FAILURE[" + future.cause().getMessage() + "]"));
                            });
                        }
                        continue;
                    }
                }
            } catch (Exception e) {
                writeLog("executorService Error");
                log.error(e.toString(), e);
                writeLog(e.toString());
            }
        });
    }

    public synchronized void sendRequest(RequestVO requestVO) {
        messageHandler.sendCommand(requestVO);
    }


    /**
     * RFIDClient Shutdown
     */
    @PreDestroy
    public synchronized void shutdown() {
        if (this.future != null) {
            writeLog("future close");
            future.channel().close();
        }
        if (this.group != null) {
            writeLog("group shutdown");
            group.shutdownGracefully();
        }
        writeLog("executorService shutdown");
        executorService.shutdown();
    }
}
