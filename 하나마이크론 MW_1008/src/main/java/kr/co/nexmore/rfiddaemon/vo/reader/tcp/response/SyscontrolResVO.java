package kr.co.nexmore.rfiddaemon.vo.reader.tcp.response;

import kr.co.nexmore.netty.lib.annotation.Segment;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class SyscontrolResVO implements Serializable {
    private static final long serialVersionUID = 1833992883157539429L;

    @Segment(type = Segment.Type.BYTE)
    private byte subCom;

    private String productRevision; // 제품 버전 정보

    @Segment(type = Segment.Type.BYTE)
    private byte comName;           // command 명

    @Segment(type = Segment.Type.BYTE)
    private byte subComName;        // subCommand 명

    @Segment(type = Segment.Type.BYTE)
    private byte registerValue;     // 해당 레지스터 값

    @Segment(type = Segment.Type.BYTE)
    private byte transferTime;      // mode register 가져올 시 사용
}
