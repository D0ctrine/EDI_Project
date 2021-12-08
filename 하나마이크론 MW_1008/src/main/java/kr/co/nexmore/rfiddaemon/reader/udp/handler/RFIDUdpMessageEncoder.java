package kr.co.nexmore.rfiddaemon.reader.udp.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import kr.co.nexmore.rfiddaemon.reader.util.ByteMarshaller;
import kr.co.nexmore.rfiddaemon.reader.util.UdpParseUtil;
import kr.co.nexmore.rfiddaemon.vo.reader.udp.UdpRequestVO;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

import static kr.co.nexmore.rfiddaemon.common.PacketConstant.*;


@Slf4j
public class RFIDUdpMessageEncoder extends MessageToMessageEncoder<UdpRequestVO> {

    private final InetSocketAddress remoteAddress;

    public RFIDUdpMessageEncoder(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, UdpRequestVO requestVO, List<Object> out) throws Exception {
        log.debug("UDP encode");
        log.debug("[TARGET: {}]", requestVO.getReaderIp());

        if (requestVO == null) {
            return;
        }

        ByteBuf buf = null;

        switch (requestVO.getCommand()) {
            case UDP_SEARCH_REQUEST_COM:
                buf = Unpooled.buffer(11);
//                ByteMarshaller.marshal(buf, requestVO, "readerMac", "activeMode", "readerIp", "subnetMask", "gateway", "downloadMacList");
                ByteMarshaller.marshal(buf, requestVO, "downloadMac");
                break;
            case UDP_SET_NETWORK_REQUEST_COM:
                buf = Unpooled.buffer(171);
                ByteMarshaller.marshal(buf, requestVO, "totalCount", "currentCount", "downloadMac");
                buf.writeBytes(requestVO.getResponseBuf(), 9, buf.writableBytes());
                buf.setBytes(18, UdpParseUtil.parseIpAddress(requestVO.getChangeIp()), 4);
                buf.setBytes(26, UdpParseUtil.parseIpAddress(requestVO.getGateway()), 4);
                break;
            case UDP_CORE_READY_REQUEST_COM:
            case UDP_FIRMWARE_READY_REQUEST_COM:
                buf = Unpooled.buffer(250);
                ByteMarshaller.marshal(buf, requestVO);
                while(buf.writerIndex() != buf.capacity()) {
                    buf.writeByte(0x00);
                }
                log.debug("{}", ByteBufUtil.hexDump(buf));
                break;
            case UDP_CORE_DOWNLOAD_REQUEST_COM:
            case UDP_FIRMWARE_DOWNLOAD_REQUEST_COM:
                buf = Unpooled.buffer(235);
                ByteMarshaller.marshal(buf, requestVO, "downloadMac");
                buf.writeBytes(requestVO.getFirmwarePackets());
                log.debug("{}", ByteBufUtil.hexDump(buf));
                break;
            default:
                break;
        }
//        out.add(new DatagramPacket(buf, remoteAddress));
        out.add(new DatagramPacket(buf, new InetSocketAddress(requestVO.getReaderIp(), 7100)));
    }
}
