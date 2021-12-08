package kr.co.nexmore.rfiddaemon.vo.reader.tcp.response;

//import com.nexmore.sp.lib.netty.annotation.Segment;
import kr.co.nexmore.netty.lib.annotation.Segment;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResponseHeaderVO implements Serializable {

    private static final long serialVersionUID = 5466678125148130407L;

    @Segment(type=Segment.Type.BYTE)
    private byte STX;

    @Segment(type=Segment.Type.BYTE)
    private byte address;

    @Segment(type=Segment.Type.BYTE)
    private byte direction;

    @Segment(type=Segment.Type.SHORT)
    private short dataSize;

    @Segment(type=Segment.Type.BYTE)
    private byte command;
}
