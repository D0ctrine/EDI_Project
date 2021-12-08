package kr.co.nexmore.rfiddaemon.vo.reader.tcp.response;

import kr.co.nexmore.netty.lib.annotation.Segment;

import java.io.Serializable;

public class FaultPacketVO implements Serializable {
    private static final long serialVersionUID = -5773652320832299257L;

    @Segment(type = Segment.Type.BYTE)
    private byte faultCode;
}
