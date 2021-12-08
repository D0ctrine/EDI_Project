package kr.co.nexmore.rfiddaemon.reader.udp.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kr.co.nexmore.rfiddaemon.vo.reader.udp.UdpResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;


@Slf4j
@Component("rfidUdpMessageHandler")
@Scope("prototype")
public class RFIDUdpMessageHandler extends SimpleChannelInboundHandler {

    public static final Map<String, UdpResponseVO> RFID_READER_MAP = new HashMap<>();
    public static final ArrayBlockingQueue<UdpResponseVO> udpBlockingQueue = new ArrayBlockingQueue<UdpResponseVO>(1000);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelRegistered");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelActive");
        log.info("Channel is Active :: Success");
        log.info("ctx : {}", ctx);
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("channelRead");
        log.debug(msg.toString());

        UdpResponseVO responseVO = (UdpResponseVO) msg;
        byte command = responseVO.getCom();
        udpBlockingQueue.put(responseVO);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.debug("channelReadComplete");
        // 연결된 채널 데이터를 모두 읽었을 떄 이벤트 발생
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelInactive");
        super.channelInactive(ctx);

    }

    /**
     * channelRegistered의 이벤트와 반대로 채널이 이벤트 루프에서 제거 되었을 때 발생.
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelUnregistered");
        super.channelUnregistered(ctx);
    }
}
