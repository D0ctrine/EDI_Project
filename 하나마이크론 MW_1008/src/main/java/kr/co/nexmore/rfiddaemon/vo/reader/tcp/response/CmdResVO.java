package kr.co.nexmore.rfiddaemon.vo.reader.tcp.response;

import kr.co.nexmore.netty.lib.annotation.Segment;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
public class CmdResVO implements Serializable {
    private static final long serialVersionUID = -929842548675537712L;

    @Segment(type=Segment.Type.BYTE)
    private byte comName;

    @Segment(type=Segment.Type.BYTE)
    private byte subComName;

    @Segment(type=Segment.Type.BYTE)
    private byte result;
}
