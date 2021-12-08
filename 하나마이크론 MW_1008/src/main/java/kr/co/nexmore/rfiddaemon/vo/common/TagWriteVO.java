package kr.co.nexmore.rfiddaemon.vo.common;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TagWriteVO {

    private String readerMac;
    private int channel;
    private String writeData;
    private String readData;
    private String tagUid;
    private String readerIp;

    public TagWriteVO(String readerMac, int channel, String writeData, String readerIp) {
        this.readerMac = readerMac;
        this.channel = channel;
        this.writeData = writeData;
        this.readerIp = readerIp;
    }
}
