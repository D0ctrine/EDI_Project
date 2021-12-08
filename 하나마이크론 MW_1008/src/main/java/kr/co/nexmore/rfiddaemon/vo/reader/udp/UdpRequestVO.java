package kr.co.nexmore.rfiddaemon.vo.reader.udp;

import io.netty.buffer.ByteBuf;
import kr.co.nexmore.netty.lib.annotation.Segment;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

import static kr.co.nexmore.rfiddaemon.common.PacketConstant.UDP_SEARCH_REQUEST_COM;
import static kr.co.nexmore.rfiddaemon.common.PacketConstant.UDP_SET_NETWORK_REQUEST_COM;

@Getter
@Setter
@ToString
public class UdpRequestVO implements Serializable {
    private static final long serialVersionUID = 5582942532913675228L;

    @Segment(type = Segment.Type.CHAR, size = 8)
    private String fixCommand = "RFengine";

    @Segment(type = Segment.Type.BYTE)
    private byte command;

    @Segment(type = Segment.Type.BYTE)
    private byte totalCount = 0x00;

    @Segment(type = Segment.Type.BYTE)
    private byte currentCount = 0x00;

    private String readerMac;

    private byte activeMode = 0x01;     // server/client mode (0x01: server, 0x00: client)

    private String readerIp;

    private String changeIp;

    private String subnetMask = "255.255.255.0";

    private String gateway;

    @Segment(type = Segment.Type.HEX)
    private String downloadMac;     // firmware download ready mac

    private byte[] firmwarePackets;

    private ByteBuf responseBuf;

    public static UdpRequestVO setSearchCommand(String readerIp){
        UdpRequestVO udpRequestVO = new UdpRequestVO();
        udpRequestVO.setCommand(UDP_SEARCH_REQUEST_COM);
        udpRequestVO.setReaderIp(readerIp);

        return udpRequestVO;
    }

    public static UdpRequestVO setChangeNetworkCommand(String macAddress, String readerIp, String changeIp, String gateway, ByteBuf responseBuf){
        UdpRequestVO udpRequestVO = new UdpRequestVO();
        udpRequestVO.setCommand(UDP_SET_NETWORK_REQUEST_COM);
        udpRequestVO.setReaderMac(macAddress);
        udpRequestVO.setReaderIp(readerIp);
        udpRequestVO.setChangeIp(changeIp);
        udpRequestVO.setGateway(gateway);
        udpRequestVO.setResponseBuf(responseBuf);

        return udpRequestVO;
    }

    public static UdpRequestVO setDownloadReadyCommand(byte command, byte readerCount, String downloadMac, String readerIp){
        UdpRequestVO udpRequestVO = new UdpRequestVO();
        udpRequestVO.setCommand(command);
        udpRequestVO.setCurrentCount(readerCount);
        udpRequestVO.setDownloadMac(downloadMac);
        udpRequestVO.setReaderIp(readerIp);

        return udpRequestVO;
    }

    public static UdpRequestVO setFirmwarePacketCommand(byte command, byte totalCount, byte currentCount, String readerIp){
        UdpRequestVO udpRequestVO = new UdpRequestVO();
        udpRequestVO.setCommand(command);
        udpRequestVO.setTotalCount(totalCount);
        udpRequestVO.setCurrentCount(currentCount);
        udpRequestVO.setReaderIp(readerIp);

        return udpRequestVO;
    }

}
