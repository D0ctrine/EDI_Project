package kr.co.nexmore.rfiddaemon.reader.util;

import kr.co.nexmore.rfiddaemon.common.RequestCommand;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.request.CmdReqVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.request.RequestHeaderVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.request.RequestVO;
import lombok.extern.slf4j.Slf4j;

import static kr.co.nexmore.rfiddaemon.common.HeaderConstant.*;

@Slf4j
public class TcpSetCommandUtil {


    /**
     * 리더 product 정보 요청 command setting
     *
     * @param readerMac
     * @return RequestVO
     */
    public static RequestVO getReaderInformation(String readerMac) {
        return setCommand(RequestCommand.SYS_CONTROL_GET_PRODUCT_INFO, GET_READER_INFO_HEADER, null, readerMac);
    }

    public static RequestVO getRegister(RequestCommand requestCommand, String readerMac) {
        String param = ByteUtil.toHexString(requestCommand.getCommand()) + ByteUtil.toHexString(requestCommand.getSubCommand());
        return setCommand(RequestCommand.SYS_CONTROL_READ_REGISTER, READ_REGISTER_HEADER, param, readerMac);
    }

    public static RequestVO getExternalIO(String readerMac) {
        return setCommand(RequestCommand.SYS_CONTROL_READ_EXTERNAL_IO, GET_READER_EXTERNAL_IO_HEADER, null, readerMac);
    }



    /**
     *
     * 리더 모드 및 설정 변경 command setting
     *
     * @param mode 모드 0: continue, 1: trigger
     * @param time 리더기에서 읽은 데이터를 보내는 주기(value: 1~25, unit: second)
     * @param readerMac
     * @return RequestVO
     */
    public static RequestVO setMode(int mode, double time, String readerMac) {
        String opMode = ByteUtil.toHexString((byte)mode);
        String transferTime = ByteUtil.toHexString((byte)(time * 10));
        String param = opMode + transferTime;
        return setCommand(RequestCommand.SYS_CONF_OPERATNG_MODE, SET_READER_MODE_HEADER, param, readerMac);
    }

    /**
     *
     * 리더 부저 ON/OFF command setting
     *
     * @param flag flag 0: OFF, 1: ON
     * @param readerMac
     * @return RequestVO
     */
    public static RequestVO setBuzzer(int flag, String readerMac) {
        String onOff = ByteUtil.toHexString((byte) flag);
        return setCommand(RequestCommand.SYS_CONF_BUZZER_ONOFF, SET_BUZZER_ONOFF_HEADER, onOff, readerMac);
    }

    /**
     *
     * 리더 keepAlive 주기 변경 command setting
     *
     * @param time keepAlive 주기(value: 1~ 255, unit: second)
     * @param readerMac
     * @return RequestVO
     */
    public static RequestVO setKeepalive(int time, String readerMac) {
        String interval = ByteUtil.toHexString((byte) time);
        return setCommand(RequestCommand.SYS_CONF_KEEPALIVE_INTERVAL, SET_KEEPALIVE_INTERVAL_HEADER, interval, readerMac);
    }

    /**
     *
     * 리더가 Data를 읽는 length 설정 command setting
     *
     * @param length
     * @param readerMac
     * @return RequestVO
     */
    public static RequestVO setLength(int length, String readerMac) {
        String memoryLength = ByteUtil.toHexString((byte)length);
        return setCommand(RequestCommand.ISO15693_CONF_SET_MEMORY_LENGTH, SET_MEMORY_LENGTH_HEADER, memoryLength, readerMac);
    }

    /**
     *
     * 리더가 읽을 Data 의 Address(위치)
     *
     * @param addr default: 0
     * @param readerMac
     * @return RequestVO
     */
    public static RequestVO setReadAddress(int addr, String readerMac) {
        String address = ByteUtil.toHexString((byte)addr);
        return setCommand(RequestCommand.ISO15693_CONF_SET_MEMORY_ADDR, SET_MEMORY_ADDR_HEADER, address, readerMac);
    }

    /**
     *
     * 리더의 현재 설정 값을 저장하는 command setting
     *
     * @param readerMac
     * @return RequestVO
     */
    public static RequestVO setSaveRegistry(String readerMac) {
        return setCommand(RequestCommand.SYS_CONTROL_SAVE_REGISTER, SAVE_REGISTER_HEADER, null, readerMac);
    }

    /**
     * 리더의 안테나에 attach 된 태그에 write 하는 command setting
     *
     * @param ant 안테나 번호
     * @param addr write 할 address
     * @param length write 할 data의 length
     * @param data write 할 data
     * @param readerMac
     * @return RequestVO
     */
    public static RequestVO setWriteCommand(int ant, int addr, int length, String data, String readerMac) {

        String antenna = ByteUtil.toHexString((byte) ant);
        String address = ByteUtil.toHexString((byte) addr);
        String dataLength = ByteUtil.toHexString((byte) length);
        String value = ByteUtil.stringToHex(data);

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(antenna).append(address).append(dataLength).append(value);


        RequestHeaderVO header = WRITE_DATA_HEADER;
        header.setDataSize(5 + length);

        return setCommand(RequestCommand.ISO15693_CONTROL_WRITE_DATA, header, stringBuffer.toString(), readerMac);
    }

    /**
     * lotId write command 생성
     *
     * @param ant   안테나 번호(channel)
     * @param data  write 할 data
     * @param readerMac 리더 mac 주소
     * @return RequestVO
     */
    public static RequestVO setWriteLotId(int ant, String data, String readerMac) {

        byte[] tmpBytes = new byte[60]; // 60 bytes 길이 bytes 초기화

        if(data != null && !"".equals(data)) {
            System.arraycopy(data.getBytes(), 0, tmpBytes, 0, data.getBytes().length);
            System.arraycopy(new byte[tmpBytes.length - data.getBytes().length], 0, tmpBytes, data.getBytes().length, tmpBytes.length - data.getBytes().length);
        }

        StringBuffer lotBuffer = new StringBuffer();
        String lotId = lotBuffer.append(new String(tmpBytes)).toString();

        String antenna = ByteUtil.toHexString((byte) ant);
        String address = ByteUtil.toHexString((byte) 10);   // tagData address : 0~10(10 bytes), lotId address : 10~70(60 bytes)
        String dataLength = ByteUtil.toHexString((byte) lotId.length());
        String value = ByteUtil.stringToHex(lotId);

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(antenna).append(address).append(dataLength).append(value);

        RequestHeaderVO header = WRITE_DATA_HEADER;
        header.setDataSize(5 + lotId.length());

        return setCommand(RequestCommand.ISO15693_CONTROL_WRITE_DATA, header, stringBuffer.toString(), readerMac);
    }

    /**
     *
     * 리더 READ TagData command setting
     * Trigger 모드에서 사용 하는 command
     *
     * @param ant 안테나 번호
     * @param addr READ 할 address
     * @param length READ 할 data length
     * @param readerMac
     * @return RequestVO
     */
    public static RequestVO setReadTagData(int ant, int addr, int length, String readerMac) {

        String antenna = ByteUtil.toHexString((byte) ant);
        String address = ByteUtil.toHexString((byte) addr);
        String memoryLength = ByteUtil.toHexString((byte) length);

        String param = antenna + address + memoryLength;

        return setCommand(RequestCommand.ISO15693_CONTROL_READ_DATA, READ_DATA_HEADER, param, readerMac);
    }

    /**
     *
     * 리더 READ TagUid command setting
     * Trigger 모드에서 사용하는 command
     *
     * @param ant 안테나 번호
     * @param readerMac
     * @return RequestVO
     */
    public static RequestVO setReadTagUid(int ant, String readerMac) {

        String antenna = ByteUtil.toHexString((byte) ant);
        return setCommand(RequestCommand.ISO15693_CONTROL_READ_ID, READ_UID_HEADER, antenna, readerMac);
    }


    /**
     *
     * 리더기에 전송할 command 를 setting 하는 메서드
     *
     * @param command command
     * @param headerVO header
     * @param param param
     * @param readerMac
     * @return RequestVO
     */
    private static RequestVO setCommand(RequestCommand command, RequestHeaderVO headerVO, String param, String readerMac) {
        RequestVO requestVO = null;
        RequestHeaderVO header = headerVO;
        if(header != null) {
            headerVO.setReaderMac(readerMac);
        }
        requestVO = new RequestVO(headerVO, new CmdReqVO(command.getCommand(), command.getSubCommand(), param));
        return requestVO;
    }
}
