package kr.co.nexmore.rfiddaemon.vo.reader.tcp.request;

import kr.co.nexmore.netty.lib.annotation.Segment;
import kr.co.nexmore.rfiddaemon.common.PacketConstant;
import kr.co.nexmore.rfiddaemon.reader.util.ByteUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class CmdReqVO implements Serializable {

    private static final long serialVersionUID = 45468060825667803L;

    @Segment(type = Segment.Type.BYTE)
    private byte command;

    @Segment(type = Segment.Type.BYTE)
    private byte subCommand;

    @Segment(type = Segment.Type.HEX)
    private String param;

    @Segment(type = Segment.Type.BYTE)
    private byte LRC;

    @Segment(type = Segment.Type.BYTE)
    private byte ETX;

    private int paramSize;

    public CmdReqVO(byte command, byte subCommand, String param) {

        int paramLength = 0;

        if(param != null) {
            paramLength = ByteUtil.toBytes(param, 16).length;
        }

        this.command = command;
        this.subCommand = subCommand;
        this.param = param;
        this.paramSize = paramLength;
        this.ETX = PacketConstant.ETX;

        byte[] tmp = new byte[paramLength];
        tmp = ByteUtil.toBytes(param, 16);

        byte[] data = new byte[2 + paramLength];
        data[0] = command;
        data[1] = subCommand;

        for(int i = 0; i < paramLength; i++) {
            data[2+i] = tmp[i];
        }

        byte lrc = ByteUtil.calcXOR(data, data.length);
        this.LRC = lrc;
    }
}
