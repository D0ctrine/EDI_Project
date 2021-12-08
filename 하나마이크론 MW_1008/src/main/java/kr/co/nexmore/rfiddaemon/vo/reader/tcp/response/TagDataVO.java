package kr.co.nexmore.rfiddaemon.vo.reader.tcp.response;

import kr.co.nexmore.netty.lib.annotation.Segment;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class TagDataVO implements Serializable {

    private static final long serialVersionUID = -1971223248140470720L;

    @Segment(type = Segment.Type.BYTE)
    private int subCom;

    @Segment(type=Segment.Type.BYTE)
    private int ant;

    @Segment(type=Segment.Type.BYTE)
    private int address;

    @Segment(type = Segment.Type.BYTE)
    private int length;

    @Segment(type=Segment.Type.HEX, size = 8)   // TAG_UID SIZE: 8bytes
    private String tagUid;

    @Segment(type=Segment.Type.CHAR, size = 10) // 하나마이크론 MAGAZINE_ID SIZE: 10bytes
    private String data;

    @Segment(type=Segment.Type.CHAR, size = 50) // 하나마이크론 LOT_ID SIZE: 50bytes  태그 안에 lotId 가 있으면 READ (현재는 WRITE 사용 안하지만 사용하게 될 수도 있음.)
    private String lotId;

    private String mesLotId1;  // MES 에서 내려주는 lotId1
    private String mesLotId2;  // MES 에서 내려주는 lotId2
    private String mesLotId3;  // MES 에서 내려주는 lotId3
    private String mesLotId4;  // MES 에서 내려주는 lotId4

    private long attachTime;    // systemTime -> detach 감지를 위한 서비스가 설치 된 서버(장비)의 systemTime

    private boolean startProcess = false;
    private boolean endProcess = false;

//    private boolean isMesReplyed = false;

    private String eventStatus;  // 현재 상태 (START, END, SEND_MES_START, SEND_MES_END, LOT_START, LOT_END)
    private String eventTime;    // 이벤트(attach, detach) 발생시간 (DB서버 systemTime)

}
