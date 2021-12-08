package kr.co.nexmore.rfiddaemon.vo.reader.tcp.response;

import kr.co.nexmore.netty.lib.annotation.Segment;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
public class KeepAlivePacketVO implements Serializable {
    private static final long serialVersionUID = 981760218726841149L;

    @Segment(type = Segment.Type.BYTE)
    private int interval;
}
