package kr.co.nexmore.rfiddaemon.vo.common;

import kr.co.nexmore.rfiddaemon.vo.mes.EISType.EIS_RFID_Request_In_Tag;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.RFID_EIS_Reply_Out_Tag;
import kr.co.nexmore.rfiddaemon.vo.reader.ReaderVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.TagDataVO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EventHistoryVO {

    private int eventLogId;         // 이벤트로그 ID(SEQ)
    private String readerName;      // 리더명
    private String readerIp;        // 리더기 IP
    private String equipmentName;   // 설비명
    private String modelName;       // 모델명
    private int rdChannelCnt;       // 리더의 채널 수(2ch, 4ch)
    private String eventType;       // 이벤트 종류
    private int antennaNum;         // 이벤트 발생한 안테나 채널
    private String ant1TagUid;          // tag UID
    private String ant1MagazineId;      // magazineId(tagData)
    private String ant1LotId;           // lot id
    private String ant2TagUid;          // tag UID
    private String ant2MagazineId;      // magazineId(tagData)
    private String ant2LotId;           // lot id
    private String ant3TagUid;          // tag UID
    private String ant3MagazineId;      // magazineId(tagData)
    private String ant3LotId;           // lot id
    private String ant4TagUid;          // tag UID
    private String ant4MagazineId;      // magazineId(tagData)
    private String ant4LotId;           // lot id
    private int retVal;             // MES 리턴값
    private String retMsg;          // message
    private String regDate;         // 등록시간(timestamp)
    private String modeDate;        // 수정시간(timestamp)


    /**
     * MES 에 SEND_MES_START, SEND_MES_END 보내기 전
     * EventHistory Insert 시 사용하는 생성자
     *
     * @param reader ReaderVO
     * @param channel channel 이벤트 발생  채널
     * @param eventType eventType (SEND_MES_START, SEMD_MES, END)
     * @param retVal retVal 응답 값
     */
    public EventHistoryVO(ReaderVO reader, int channel, String eventType, int retVal) {
        this.readerName = reader.getReaderName();
        this.readerIp = reader.getReaderIp();
        this.equipmentName = reader.getEquipmentName();
        this.modelName = reader.getModelName();
        this.rdChannelCnt = reader.getChannelCnt();
        this.antennaNum = channel;
        this.eventType = eventType;
        this.retVal = retVal;
    }

    /**
     * START EVENT, END EVENT 시
     * 발생한 채널의 Data 를 EventHistory Insert 시 사용하는 생성자
     *
     * @param readerVO ReaderVO
     * @param tagDataVO TagDataVO
     * @param retVal retVal
     * @param retMsg retMsg
     */
    public EventHistoryVO(ReaderVO readerVO, TagDataVO tagDataVO, int retVal, String retMsg) {
        this.readerName = readerVO.getReaderName();
        this.readerIp = readerVO.getReaderIp();
        this.equipmentName = readerVO.getEquipmentName();
        this.modelName = readerVO.getModelName();
        this.rdChannelCnt = readerVO.getChannelCnt();
        this.eventType = tagDataVO.getEventStatus();
        this.antennaNum = tagDataVO.getAnt();

        if (this.antennaNum == 1) {
            this.ant1TagUid = tagDataVO.getTagUid();
            this.ant1MagazineId = tagDataVO.getData();
            this.ant1LotId = tagDataVO.getLotId();
        } else if (this.antennaNum == 2) {
            this.ant2TagUid = tagDataVO.getTagUid();
            this.ant2MagazineId = tagDataVO.getData();
            this.ant2LotId = tagDataVO.getLotId();
        } else if (this.antennaNum == 3) {
            this.ant3TagUid = tagDataVO.getTagUid();
            this.ant3MagazineId = tagDataVO.getData();
            this.ant3LotId = tagDataVO.getLotId();
        } else if (this.antennaNum == 4) {
            this.ant4TagUid = tagDataVO.getTagUid();
            this.ant4MagazineId = tagDataVO.getData();
            this.ant4LotId = tagDataVO.getLotId();
        }

        this.retVal = retVal;
        this.retMsg = retMsg;
    }

    /**
     * MES 로 부터 reply 에 대한 값을 세팅하는 메서드
     *
     * @param eventLogId  eventLogId
     * @param replyOutTag reply
     */
    public EventHistoryVO(int eventLogId, RFID_EIS_Reply_Out_Tag replyOutTag) {
        this.eventLogId = eventLogId;

//        this.ant1LotId = replyOutTag.resv_field_1;
//        this.ant2LotId = replyOutTag.resv_field_2;
        this.ant1LotId = replyOutTag.lot_id_1;
        this.ant2LotId = replyOutTag.lot_id_2;
        this.ant3LotId = replyOutTag.lot_id_3;
        this.ant4LotId = replyOutTag.lot_id_4;


        this.retVal = replyOutTag.h_status_value - 48;
        this.retMsg = replyOutTag.h_msg;
    }


    /**
     * MES -> MW
     * LOT_END, WRITE_LOT_ID REQUEST 시
     * EventHistory insert 하기 위한 생성자
     * @param readerVO
     * @param eventType
     * @param requestIn
     * @param retVal
     */
    public EventHistoryVO(ReaderVO readerVO, String eventType, EIS_RFID_Request_In_Tag requestIn, int retVal) {
        this.readerName = readerVO.getReaderName();
        this.readerIp = readerVO.getReaderIp();
        this.equipmentName = requestIn.res_id;
        this.modelName = readerVO.getModelName();
        this.rdChannelCnt = readerVO.getChannelCnt();
        this.eventType = eventType;

        this.ant1TagUid = requestIn.uid_1;
        this.ant1MagazineId = requestIn.magazine_id_1;

        this.ant2TagUid = requestIn.uid_2;
        this.ant2MagazineId = requestIn.magazine_id_2;
        this.ant2LotId = requestIn.resv_field_2;

        this.ant3TagUid = requestIn.uid_3;
        this.ant3MagazineId = requestIn.magazine_id_3;

        this.ant4TagUid = requestIn.uid_4;
        this.ant4MagazineId = requestIn.magazine_id_4;
        this.retVal = retVal;
    }

    /**
     * MES -> MW
     * LOT END REQUEST 시
     * EventHistory Update 하기 위한 생성자
     *
     *
     * @param eventLogId
     * @param readerName
     * @param requestInTag
     * @param retVal
     */
    public EventHistoryVO(int eventLogId, String readerName, EIS_RFID_Request_In_Tag requestInTag, int retVal) {
        this.eventLogId = eventLogId;
        this.readerName = readerName;

        this.ant2LotId = requestInTag.resv_field_2;
        this.retVal = retVal;
    }
}
