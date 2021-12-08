package kr.co.nexmore.rfiddaemon.reader.tcp.client.handler;

import java.security.InvalidParameterException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.EventExecutor;
import kr.co.nexmore.rfiddaemon.common.RequestCommand;
import kr.co.nexmore.rfiddaemon.reader.util.ByteUtil;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.ResponseVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.request.CmdReqVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.request.RequestVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.SyscontrolResVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

import static kr.co.nexmore.rfiddaemon.common.RequestCommand.SYS_CONF_KEEPALIVE_INTERVAL;
import static kr.co.nexmore.rfiddaemon.common.RequestCommand.SYS_CONTROL;
import static kr.co.nexmore.rfiddaemon.common.RequestCommand.SYS_CONTROL_READ_REGISTER;

@Slf4j
@Scope("prototype")
public class RFIDClientAliveCheckHandler extends ChannelDuplexHandler {

    private static final long MIN_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(1);

    // writing한 후 reading이 없는 제한시간
    private final long readTimeout;

    volatile ScheduledFuture<?> readerIdleTimeoutAfterWriting;
    volatile long lastReadTime;

    // 가장 마지막에 writing한 시간
    volatile long lastWriteTime;

    private volatile boolean readed;

    public RFIDClientAliveCheckHandler(long readerIdleTimeAfterWriting) {
        if (readerIdleTimeAfterWriting <= 0) {
            throw new InvalidParameterException("time must > 0");
        }
        readTimeout = Math.max(TimeUnit.MILLISECONDS.toNanos(readerIdleTimeAfterWriting), MIN_TIMEOUT_NANOS);
    }


    public long getReaderIdleTimeInMillis() {
        return TimeUnit.NANOSECONDS.toMillis(readTimeout);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        destroy();
        super.handlerRemoved(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        destroy();
        super.channelInactive(ctx);
    }

    @Override
    public void write(final ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        RequestVO requestVO = (RequestVO) msg;
        CmdReqVO cmdReqVO = (CmdReqVO) requestVO.getBody();
        if (RequestCommand.getActionName(cmdReqVO.getCommand(), cmdReqVO.getSubCommand()).equals(SYS_CONTROL_READ_REGISTER.getActionName())) {
            log.debug("read register command write");
            byte[] bytes = ByteUtil.toBytesFromHexString(cmdReqVO.getParam());
            if (bytes[0] == SYS_CONF_KEEPALIVE_INTERVAL.getCommand() && bytes[1] == SYS_CONF_KEEPALIVE_INTERVAL.getSubCommand()) {
                log.debug("alive Check Start");
                if (readTimeout > 0) {
                    ChannelPromise unvoid = promise.unvoid();
                    ctx.write(msg, unvoid).addListener(future -> {
                        log.debug("initialize aliveChecker");
                        lastWriteTime = System.nanoTime();
                        readed = false;
                        initialize(ctx);
                    });
                } else {
                    ctx.write(requestVO, promise);
                }
            } else {
                ctx.write(requestVO, promise);
            }
        } else {
            ctx.write(requestVO, promise);
        }
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ResponseVO responseVO = (ResponseVO) msg;
        if (responseVO != null) {
            destroy();
            lastReadTime = System.nanoTime();
            readed = true;
            byte command = responseVO.getHeaderVO().getCommand();
            if (command == SYS_CONTROL.getCommand()) {
                SyscontrolResVO syscontrolResVO = (SyscontrolResVO) responseVO.getBody();
                if(syscontrolResVO.getSubCom() == SYS_CONTROL_READ_REGISTER.getSubCommand()) {
                    String action = RequestCommand.getActionName(syscontrolResVO.getComName(), syscontrolResVO.getSubComName());
                    if(action.equals(SYS_CONF_KEEPALIVE_INTERVAL.getActionName())) {
                        log.debug("receive alive check");
//                        destroy();
//                        lastReadTime = System.nanoTime();
//                        readed = true;
                    } else {
                        ctx.fireChannelRead(msg);
                    }
                } else {
                    ctx.fireChannelRead(msg);
                }
            } else {
                ctx.fireChannelRead(msg);
            }
        }
    }

    private void initialize(ChannelHandlerContext ctx) {

        log.debug("before readerIdleTimeoutAfterWriting=" + readerIdleTimeoutAfterWriting);

        if (readerIdleTimeoutAfterWriting != null && !readerIdleTimeoutAfterWriting.isDone()) {
            readerIdleTimeoutAfterWriting.cancel(false);
        }
        EventExecutor loop = ctx.executor();
        readerIdleTimeoutAfterWriting = loop.schedule(
                new ReaderIdleTimeoutTask(ctx),
                readTimeout, TimeUnit.NANOSECONDS);

        log.debug("after readerIdleTimeoutAfterWriting=" + readerIdleTimeoutAfterWriting);
    }


    private void destroy() {
        if (readerIdleTimeoutAfterWriting != null) {
            readerIdleTimeoutAfterWriting.cancel(false);
            readerIdleTimeoutAfterWriting = null;
        }
    }


    /**
     * Is called when an {@link IdleStateEvent} should be fired. This implementation calls
     * {@link ChannelHandlerContext#fireUserEventTriggered(Object)}.
     */
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        log.debug("fireUserEventTriggered");
        ctx.fireUserEventTriggered(evt);
    }


    private final class ReaderIdleTimeoutTask implements Runnable {

        private final ChannelHandlerContext ctx;

        ReaderIdleTimeoutTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        public void run() {
            if (!ctx.channel().isOpen()) {
                return;
            }

            long nextDelay = readTimeout;   // 400 millis
            if (!readed) {
                log.debug("readed: {}", readed);
                log.debug("lastReadTime: {}", lastReadTime);
                nextDelay -= System.nanoTime() - lastReadTime;
            }
            log.debug("nexDelay: {}", nextDelay);
            if (nextDelay <= 0) {
                // Reader is idle - set a new timeout and notify the callback.
//                readerIdleTimeoutAfterWriting = ctx.executor().schedule(this, readTimeout, TimeUnit.NANOSECONDS);
                destroy();
                try {
                    channelIdle(ctx, IdleStateEvent.READER_IDLE_STATE_EVENT);
                } catch (Throwable t) {
                    ctx.fireExceptionCaught(t);
                }
            } else {
                // Read occurred before the timeout - set a new timeout with shorter delay.
//                readerIdleTimeoutAfterWriting = ctx.executor().schedule(this, nextDelay, TimeUnit.NANOSECONDS);
            }
        }
    }
}
