package kr.co.nexmore.rfiddaemon.reader.tcp.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import kr.co.nexmore.rfiddaemon.reader.util.ByteMarshaller;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.request.RequestVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

import java.net.InetSocketAddress;

@Slf4j
@Scope("prototype")
public class RFIDClientMessageEncoder extends MessageToByteEncoder<RequestVO> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RequestVO msg, ByteBuf out) {
        String remoteAddr = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().toString().split("/")[1];
        log.debug("[{}] Send Message to Reader.", remoteAddr);
        log.debug(msg.toString());
        ByteMarshaller.marshal(out, msg.getHeaderVO());
        ByteMarshaller.marshal(out, msg.getBody());
        log.debug("[{}] encode end.", remoteAddr);
    }
}
