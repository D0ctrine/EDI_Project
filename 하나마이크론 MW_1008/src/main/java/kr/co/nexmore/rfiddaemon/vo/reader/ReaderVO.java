package kr.co.nexmore.rfiddaemon.vo.reader;

import io.netty.buffer.ByteBuf;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@ApiModel(description = "ReaderVO")
@Data
@NoArgsConstructor
public class ReaderVO {

    @ApiModelProperty(name = "readerMac", value = "MAC 주소", dataType = "String", example = "00:50:C2:E5:00:00")
    private String readerMac;             // 리더기 mac 주소

    @ApiModelProperty(name = "readerName", value = "리더명", dataType = "String", example = "BW349-2C")
    private String readerName;          // 리더기명

    @ApiModelProperty(name = "readerIp", value = "리더기 IP", dataType = "String", example = "40.40.40.160")
    private String readerIp;            // 리더기 IP

    @ApiModelProperty(name = "readerPort", value = "리더기 PORT", dataType = "int", example = "5100")
    private Integer readerPort;         // 리더기 PORT

    @ApiModelProperty(name = "process", value = "공정명", dataType = "String", example = "WIRE BOND")
    private String processName;

    @ApiModelProperty(name = "modelName", value = "모델명", dataType = "String", example = "IConn")
    private String modelName;           // 모델명

    @ApiModelProperty(name = "equipmentCode", value = "설비 코드", dataType = "String", example = "EQ0001")
    private String equipmentCode;       // 설비코드

    @ApiModelProperty(name = "equipmentName", value = "설비명", dataType = "String", example = "BW349")
    private String equipmentName;       // 설비명

    @ApiModelProperty(name = "readerSubnetMask", value = "리더기 Subnet Mask", dataType = "String", example = "255.255.255.0")
    private String readerSubnetMask;    // 리더기 Subnet Mask

    @ApiModelProperty(name = "readerGateway", value = "리더기 gateway", dataType = "String", example = "40.40.40.1")
    private String readerGateway;       // 리더기 gateway

    @ApiModelProperty(name = "readerMode", value = "리더기 모드(continue, trigger)", dataType = "int", example = "0")
    private int readerMode;

    @ApiModelProperty(name = "channelCnt", value = "리더기의 채널 수", dataType = "int", example = "2")
    private int channelCnt;             // 리더기의 채널 수(2ch: 2, 4ch: 4)

    @ApiModelProperty(name = "readerInterval", value = "리더기가 데이터를 보내는 전송 주기", dataType = "int", example = "3")
    private double readerInterval = 3.0;         // 리더기가 tagData 를 보내는 주기(unit: s)

    @ApiModelProperty(name = "buzzerYn", value = "부저(비프음) ON/OFF 여부", dataType = "String", example = "Y")
    private String buzzerYn = "Y";            // buzzer on/off (Y,N)

    @ApiModelProperty(name = "readAddress", value = "리더기가 읽을 메모리주소 위치", dataType = "int", example = "0")
    private int readAddress;            // 리더기가 읽을 메모리 주소 위치 (default: 0)

    @ApiModelProperty(name = "readLength", value = "리더기가 읽을 데이터 길이", dataType = "int", example = "60")
    private int readLength;             // 리더기가 읽을 메모리 길이(default: 60   / 10Bytes(magazineId) + 50Bytes(lotId) = 60Bytes)

    @ApiModelProperty(name = "mesUseYn", value = "MES 연동 사용 여부", dataType = "String", example = "Y")
    private String mesUseYn;            // MES 연동 사용 여부

    @ApiModelProperty(name = "stationXId", value = "MES stationX ID", dataType = "int", example = "1")
    private int stationXId;             // MES id

    @ApiModelProperty(name = "firmwareVersion", value = "firmwareVersion", dataType = "String", example = "V.20.10.19")
    private String firmwareVersion;

    @ApiModelProperty(name = "regId", value = "리더 등록 및 수정 시 사용자 ID", dataType = "String", example = "rfidmw")
    private String regId;                  // 리더 등록 및 수정 시 사용자 ID

    @ApiModelProperty(name = "readerSettingBuf", value = "UDP SEARCH 명령 시 응답 Buf", dataType = "ByteBuf")
    private ByteBuf readerSettingBuf;

    public ReaderVO(String readerMac, String readerIp, Integer readerPort, String readerGateway, ByteBuf readerSettingBuf) {
        this.readerMac = readerMac;
        this.readerIp = readerIp;
        this.readerPort = readerPort;
        this.readerGateway = readerGateway;
        this.readerSettingBuf = readerSettingBuf;
    }

    public void setCreateReaderVO(String readerMac, String readerName, String equipmentCode, String equipmentName, String readerIp, String readerGateway, int channelCnt) {
        this.readerMac = readerMac;
        this.readerName = readerName;
        this.equipmentCode = equipmentCode;
        this.equipmentName = equipmentName;
        this.readerIp = readerIp;
        this.readerPort = 5100;
        this.readerSubnetMask = "255.255.255.0";
        this.readerGateway = readerGateway;
        this.channelCnt = channelCnt;
    }

    public void setReaderVO(String readerMac, String readerIp, Integer readerPort, String readerGateway) {
        this.readerMac = readerMac;
        this.readerIp = readerIp;
        this.readerPort = readerPort;
        this.readerGateway = readerGateway;
    }

}
