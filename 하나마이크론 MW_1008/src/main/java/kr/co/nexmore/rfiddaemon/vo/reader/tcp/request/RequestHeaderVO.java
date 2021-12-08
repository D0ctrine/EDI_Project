package kr.co.nexmore.rfiddaemon.vo.reader.tcp.request;

import kr.co.nexmore.netty.lib.annotation.Segment;
import kr.co.nexmore.rfiddaemon.common.PacketConstant;
import kr.co.nexmore.rfiddaemon.common.RequestCommand;
import lombok.*;

import java.io.Serializable;

import static kr.co.nexmore.rfiddaemon.common.PacketConstant.ADDRESS_BROADCAST;
import static kr.co.nexmore.rfiddaemon.common.PacketConstant.DIRECTION_HOST_TO_READER;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class RequestHeaderVO implements Serializable {
    private static final long serialVersionUID = -626761104422677574L;

    @Segment(type=Segment.Type.BYTE)
    private byte STX;

    @Segment(type=Segment.Type.BYTE)
    private byte address;

    @Segment(type=Segment.Type.BYTE)
    private byte direction;

    @Segment(type=Segment.Type.SHORT)
    private int dataSize;

    private String readerMac;
    private String resId;
    private String readerIp;

    public RequestHeaderVO(int dataSize) {
        this.STX = PacketConstant.STX;
        this.address = ADDRESS_BROADCAST;
        this.direction = DIRECTION_HOST_TO_READER;
        this.dataSize = dataSize;
    }

//    public RequestHeaderVO(byte STX, byte address, byte direction, int dataSize) {
    public RequestHeaderVO(byte address, byte direction, int dataSize) {
//        this.STX = STX;
        this.STX = PacketConstant.STX;
        this.address = address;
        this.direction = direction;
        this.dataSize = dataSize;
    }

}
