package kr.co.nexmore.rfiddaemon.reader.tcp.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import kr.co.nexmore.rfiddaemon.common.RequestCommand;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.RFIDTcpClient;
import kr.co.nexmore.rfiddaemon.reader.util.ByteUnmarshaller;
import kr.co.nexmore.rfiddaemon.reader.util.ByteUtil;
import kr.co.nexmore.rfiddaemon.reader.util.CommonUtil;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.ResponseHeaderVO;
import kr.co.nexmore.rfiddaemon.vo.reader.ReaderVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.ResponseVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

import java.net.InetSocketAddress;
import java.util.List;

import static kr.co.nexmore.rfiddaemon.common.PacketConstant.*;
import static kr.co.nexmore.rfiddaemon.common.RequestCommand.*;

@Slf4j
@Scope("prototype")
public class RFIDClientMessageDecoder extends ByteToMessageDecoder {

    private ReaderVO readerVO;
    private RFIDTcpClient client;

    public RFIDClientMessageDecoder(RFIDTcpClient client, ReaderVO readerVO) {
        this.readerVO = readerVO;
        this.client = client;
    }

    public void setReaderVO(ReaderVO readerVO) {
        this.readerVO = readerVO;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        String remoteAddr = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().toString().split("/")[1];
//        String remoteAddr = ctx.channel().remoteAddress().toString() + "L/" + ctx.channel().localAddress().toString();
        String logHeader = String.format("[%s] %s(%s)", this.readerVO.getEquipmentName(), this.readerVO.getReaderName(), remoteAddr);

        log.debug("{} Reader message Received. Decode Start", logHeader);

        int length = in.readableBytes();

        log.debug("{}  Reader message Received. Packet length: {}", logHeader, length);
        log.debug("{}  Message to Hex : {}", logHeader, ByteBufUtil.hexDump(in));
        // 1. STX, ETX, LRC 검증 시작
        // STX 검증(첫 패킷)
        if (in.getByte(in.readerIndex()) != STX) {
            log.error("{}  STX [{}] is invalid.", logHeader, in.getByte(in.readerIndex()));
            if (in.indexOf(in.readerIndex(), in.readableBytes(), STX) != -1) {
                log.error("{}  첫번째 STX 위치를 찾아 해당 위치로 readIndex 위치를 설정합니다. STX index: {}", logHeader, in.indexOf(in.readerIndex(), in.readableBytes(), STX));
                in.readerIndex(in.indexOf(in.readerIndex(), in.readableBytes(), STX));
                in.discardReadBytes();
            } else {
                log.error("{}  시작 태그 [STX({})] 가 존재하지 않습니다.", logHeader, STX);
                return;
            }
        }

        if(length < 6) {
            log.debug("{}  데이터의 길이가 작습니다.", logHeader);
            return;
        }

        ByteBuf lengthBuf = Unpooled.buffer(2);
        in.getBytes(in.readerIndex() + 3, lengthBuf, 2);   // packet index : 3, 4  -> data length packet
        int dataLength = Integer.parseInt(ByteUtil.toHexString(lengthBuf), 16);
        log.debug("{}  dataLength: {}", logHeader, dataLength);

        if (length < dataLength + 7) {
            log.debug("{} 전체 ByteBuf의 길이가 data의 길이보다 작습니다.", logHeader);
            return;
        }

        int etxLocation = (in.readerIndex() + 5) + (dataLength + 2) - 1;
        if(in.getByte(etxLocation) != ETX) {
            log.error("{}  ETX is not exist.", logHeader);
            return;
        }

        // LRC 검증
        ByteBuf dataBuf = Unpooled.buffer(dataLength);
        in.getBytes(in.readerIndex() + 5, dataBuf, dataLength);    // data 부분 추출
        byte calcLRC = ByteUtil.calcXOR(dataBuf, dataLength);   // LRC 계산
//        byte inputLRC = in.getByte(in.readerIndex() + dataLength);
        byte inputLRC = in.getByte(etxLocation - 1);

        if (calcLRC != inputLRC) {
            log.error("{}  LRC is invalid. Receive LRC : {}, calc LRC : {}", logHeader, inputLRC, calcLRC);
            in.readerIndex(in.indexOf(in.readerIndex(), in.readableBytes(), STX));
            in.discardReadBytes();
            return;
        }

        log.debug("{}  STX & LRC & ETX Validation Check :: Success", logHeader);

        ResponseHeaderVO headerVO = ByteUnmarshaller.unmarshal(ResponseHeaderVO.class, in);
        ResponseVO responseVO = new ResponseVO(headerVO, null);
        TagDataVO tagDataVO;

        switch (RequestCommand.getCommand(headerVO.getCommand())) {
            case TAG_PACKET:    // continue mode 시 tag data com packet
                log.debug("{}  Read Tag Data Packet.", logHeader);
                tagDataVO = ByteUnmarshaller.unmarshal(TagDataVO.class, in, "subCom");
//                tagDataVO.setData(ByteUnmarshaller.readString(in, 10).trim());    // data의 길이는 가변길이이기때문에 따로 세팅
//                tagDataVO.setLotId(ByteUnmarshaller.readString(in, tagDataVO.getLength()-10).trim());
                tagDataVO.setAnt(tagDataVO.getAnt() + 1); // 리더 기준 ant1=0, ant2=1 이기때문에 + 1 하여 알기 쉽도록 하기 위함.
                tagDataVO.setAttachTime(System.currentTimeMillis());    // 현재시간 세팅
                log.debug("{}  CHANNEL: {}  TAG UID: {}  MAGAZINE ID: {} ATTACH TIME: {}", logHeader, tagDataVO.getAnt(), tagDataVO.getTagUid(), tagDataVO.getData(), CommonUtil.getCurrentTimeMillis(tagDataVO.getAttachTime()));
                client.writeLog(String.format("CHANNEL: %d  TAG UID: %s  MAGAZINE ID: %s ATTACH TIME: %s", tagDataVO.getAnt(), tagDataVO.getTagUid(), tagDataVO.getData(), CommonUtil.getCurrentTimeMillis(tagDataVO.getAttachTime())));
                responseVO.setBody(tagDataVO);
                break;
            case KEEPALIVE_PACKET:
                log.debug("{}  Read KeepAlive Packet.", logHeader);
                KeepAlivePacketVO keepAlivePacketVO = ByteUnmarshaller.unmarshal(KeepAlivePacketVO.class, in);
                responseVO.setBody(keepAlivePacketVO);
                break;
            case FAULT_PACKET:
                log.debug("{}  Read Fault Packet.", logHeader);
                FaultPacketVO faultPacketVO = ByteUnmarshaller.unmarshal(FaultPacketVO.class, in);
                responseVO.setBody(faultPacketVO);
                break;
            case COMMAND_RESPONSE_PACKET:
                log.debug("{}  Read Command Response Packet.", logHeader);
                CmdResVO cmdResVO = ByteUnmarshaller.unmarshal(CmdResVO.class, in);
                responseVO.setBody(cmdResVO);
                break;
            case SYS_CONTROL:
                log.debug("{}  System Control Command Response Packet.", logHeader);
                SyscontrolResVO syscontrolResVO = ByteUnmarshaller.unmarshal(SyscontrolResVO.class, in, "comName", "subComName", "registerValue", "transferTime");
                if (syscontrolResVO.getSubCom() == SYS_CONTROL_GET_PRODUCT_INFO.getSubCommand()) {
                    syscontrolResVO.setProductRevision(ByteUnmarshaller.readString(in, headerVO.getDataSize() - 2));    // com, subcom 은 이미 읽었기때문에 -2 적용.
                    responseVO.setBody(syscontrolResVO);
                } else if (syscontrolResVO.getSubCom() == SYS_CONTROL_READ_REGISTER.getSubCommand()) {
                    in.readerIndex(in.readerIndex()-1);
                    log.debug("{}  Message to Hex : {}", logHeader, ByteBufUtil.hexDump(in));
                    syscontrolResVO = ByteUnmarshaller.unmarshal(SyscontrolResVO.class, in, "registerValue", "transferTime");
                    in.readerIndex(in.readerIndex()-3);
                    if(syscontrolResVO.getComName() == SYS_CONF_OPERATNG_MODE.getCommand() && syscontrolResVO.getSubComName() == SYS_CONF_OPERATNG_MODE.getSubCommand()) {
                        syscontrolResVO = ByteUnmarshaller.unmarshal(SyscontrolResVO.class, in);
                    } else {
                        syscontrolResVO = ByteUnmarshaller.unmarshal(SyscontrolResVO.class, in,  "transferTime");
                    }
                    log.debug(syscontrolResVO.toString());
                    responseVO.setBody(syscontrolResVO);
                }
                break;
            case ISO15693_CONTROL:    // read data, read uid 명령 시 응답 packet 의 command
                log.debug("{}  ISO15693 Control Read Command Response Packet.", logHeader);
                // TagData 읽기 명령 일 경우
                if (in.getByte(in.readerIndex()) == ISO15693_CONTROL_READ_DATA.getSubCommand()) {
//                    log.info("수신받은 데이터는 [TRIGGER MODE-TAG DATA] 입니다.");
                    tagDataVO = ByteUnmarshaller.unmarshal(TagDataVO.class, in, "tagUid");
//                    tagDataVO.setData(ByteUnmarshaller.readString(in, tagDataVO.getLength()));    // data는 가변길이
//                    tagDataVO.setData(ByteUnmarshaller.readString(in, 10).trim());    // data의 길이는 가변길이이기때문에 따로 세팅
//                    tagDataVO.setLotId(ByteUnmarshaller.readString(in, tagDataVO.getLength() - 10).trim());
                    tagDataVO.setAnt(tagDataVO.getAnt() + 1);
                    responseVO.setBody(tagDataVO);
                }
                // TagUid 읽기 명령 일 경우
//                log.info("수신받은 데이터는 [TRIGGER MODE-TAG UID] 입니다.");
                if (in.getByte(in.readerIndex()) == ISO15693_CONTROL_READ_ID.getSubCommand()) {
                    tagDataVO = ByteUnmarshaller.unmarshal(TagDataVO.class, in, "address", "length", "data", "lotId");
                    tagDataVO.setAnt(tagDataVO.getAnt() + 1);
                    responseVO.setBody(tagDataVO);
                }
                break;
            default:
                break;
        }

        out.add(responseVO);
        in.readShort(); // 데이터부 길이까지 VO 세팅 후 LRC, ETX 읽음 처리.
        in.slice(in.readerIndex(), in.readableBytes());
    }
}
