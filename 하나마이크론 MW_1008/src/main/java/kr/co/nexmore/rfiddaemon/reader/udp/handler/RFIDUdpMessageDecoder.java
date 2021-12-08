package kr.co.nexmore.rfiddaemon.reader.udp.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;
import kr.co.nexmore.rfiddaemon.reader.util.UdpParseUtil;
import kr.co.nexmore.rfiddaemon.vo.reader.udp.UdpResponseVO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static kr.co.nexmore.rfiddaemon.common.PacketConstant.*;

@Slf4j
public class RFIDUdpMessageDecoder extends MessageToMessageDecoder<DatagramPacket> {

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket datagramPacket, List<Object> out) throws Exception {
        log.debug("udp decode start");

        ByteBuf in = datagramPacket.content();
        int length = in.readableBytes();

        if (length < 10) {
            log.debug("length is invalid.");
            return;
        }

        ByteBuf copy = in.copy();   // search 명령어 응답 buf 는 ip&port 변경 시 필요하므로 저장해놔야 함.

        ByteBuf fixBuf = Unpooled.buffer(8);
        in.readBytes(fixBuf, 8);

        if (!"RFengine".equals(fixBuf.toString(CharsetUtil.UTF_8))) {
            log.debug("packet is invalid");
            return;
        }

        log.debug("{}", fixBuf.toString(CharsetUtil.UTF_8));

        in.markReaderIndex();

        UdpResponseVO responseVO = null;

        switch (in.readByte()) {
            case UDP_SEARCH_RESPONSE_COM:
                log.debug("SEARCH RESPONSE.");
                log.debug("{}", ByteBufUtil.hexDump(in));
                responseVO = UdpParseUtil.parseSearchResponse(copy);
                log.debug("responseVO : {}", responseVO);
                break;
            case UDP_SET_NETWORK_RESPONSE_COM:
                log.debug("NETWORK CHANGE RESPONSE.");
                log.debug("{}", ByteBufUtil.hexDump(in));
                responseVO = UdpParseUtil.parseCommonResponse(copy);
                copy.release();
                break;
            case UDP_CORE_READY_RESPONSE_COM:
            case UDP_FIRMWARE_READY_RESPONSE_COM:
                log.debug("FIRMWARE DOWNLOAD READY RESPONSE.");
                log.debug("{}", ByteBufUtil.hexDump(in));
                responseVO = UdpParseUtil.parseCommonResponse(copy);
                copy.release();
                break;
            case UDP_FIRMWARE_DOWNLOAD_RESPONSE_COM:
            case UDP_CORE_DOWNLOAD_RESPONSE_COM:
                log.debug("FIRMWARE DOWNLOAD RESPONSE.");
                log.debug("{}", ByteBufUtil.hexDump(in));
                responseVO = UdpParseUtil.parseFirmwareResponse(copy);
                copy.release();
                break;
            default:
                break;
        }

        out.add(responseVO);
    }
}
