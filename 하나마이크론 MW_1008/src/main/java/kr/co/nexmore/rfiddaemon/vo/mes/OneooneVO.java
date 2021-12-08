package kr.co.nexmore.rfiddaemon.vo.mes;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(description = "OneooneVO")
@Data
public class OneooneVO implements Serializable {

    private static final long serialVersionUID = -6200245178325563389L;

    @ApiModelProperty(name = "stationXId", value = "stationX ID", dataType = "int", example = "1")
    private int stationXId;

    @ApiModelProperty(name = "stationXIp", value = "stationX IP", dataType = "String", example = "127.0.0.1")
    private String stationXIp;

    @ApiModelProperty(name = "stationXPort", value = "stationX 포트", dataType = "int", example = "10101")
    private int stationXPort;

    @ApiModelProperty(name = "stationXChannel", value = "stationX Channel",dataType = "String", example = "/EIS/EISServer")
    private String stationXChannel;

    @ApiModelProperty(name = "rfidTuneChannel", value = "RFID Tuner Channel", dataType = "String", example = "/RFIDServer")
    private String rfidTuneChannel;

    @ApiModelProperty(name = "connectStatus", value = "connection 상태(Y/N)", dataType = "String", example = "N")
    private String connectStatus;

    @ApiModelProperty(name = "autoConnect", value = "자동 연결(Y/N)", dataType = "String", example = "Y")
    private String autoConnect;

    @ApiModelProperty(name = "mesTimeout", value = "connection timeout", dataType = "int", example = "60")
    private int mesTimeout;
}