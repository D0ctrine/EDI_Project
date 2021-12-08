package kr.co.nexmore.rfiddaemon.vo.reader.udp;

import io.netty.buffer.ByteBuf;
import kr.co.nexmore.rfiddaemon.vo.reader.ReaderVO;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UdpResponseVO extends ReaderVO implements Serializable {
    private static final long serialVersionUID = -4850358808888236066L;

    private byte com;

    private byte count;

    private ByteBuf responseBuf;

    public UdpResponseVO(byte com, ByteBuf responseBuf) {
        this.com = com;
        this.responseBuf = responseBuf;
    }
}
