package kr.co.nexmore.rfiddaemon.reader.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import kr.co.nexmore.rfiddaemon.vo.reader.udp.UdpResponseVO;
import org.apache.commons.lang3.StringUtils;

public class UdpParseUtil {

    public static UdpResponseVO parseCommonResponse(ByteBuf buf) {

        byte com = buf.getByte(8);
        byte[] mac = new byte[6];
        buf.getBytes(11, mac, 0, 6);

        String readerMac = parseMacAddress(mac);

        UdpResponseVO udpResponseVO = new UdpResponseVO(com, buf);
        udpResponseVO.setReaderVO(readerMac, null, null, null);

        return udpResponseVO;
    }
    /**
     * Search 명령 응답 시 parsing 하여 VO setting (MAC address, Ip address, port, gateway)
     * @param buf
     * @return UdpResponseVO
     */
    public static UdpResponseVO parseSearchResponse(ByteBuf buf) {

        UdpResponseVO udpResponseVO = parseCommonResponse(buf);

        byte[] ip = new byte[4];
        buf.getBytes(18, ip, 0, 4);

        String readerIp = parseIpAddress(ip);

        byte[] gateway = new byte[4];
        buf.getBytes(26, gateway, 0, 4);

        String readerGateway = parseIpAddress(gateway);

        byte[] port = new byte[2];
        buf.getBytes(30, port, 0, 2);

        String portStr = ByteUtil.toHexString(port);
        int readerPort = Integer.parseInt(portStr, 16);

        udpResponseVO.setReaderVO(udpResponseVO.getReaderMac(), readerIp, readerPort, readerGateway);

        return udpResponseVO;
    }

    public static UdpResponseVO parseFirmwareResponse(ByteBuf buf) {

        UdpResponseVO udpResponseVO = parseCommonResponse(buf);
        udpResponseVO.setCount(buf.getByte(10));

        return udpResponseVO;
    }


    /**
     * 6 바이트로 된 MAC Address 값을 String 으로 변환하여 return
     * @param bytes
     * @return String   ex) 00:xx:aa:bb:dd:ff
     */
    private static String parseMacAddress(byte[] bytes) {
        if(bytes == null || bytes.length < 0) {
            return null;
        }
        String mac = ByteUtil.toHexString(bytes);
        String[] splitStr = mac.split("(?<=\\G.{" + 2 + "})");  // 2자리씩 잘라서 String 배열로 만듦.
        mac = StringUtils.join(splitStr, ":");  // 배열 사이에 ":" 를 넣어 하나의 스트링으로 합침.

        return mac;
    }

    /**
     * 4 바이트로 된 IP address 를 String 으로 변환하여 return
     * @param bytes
     * @return String   ex) 192.168.0.x
     */
    private static String parseIpAddress(byte[] bytes) {
        if(bytes == null || bytes.length < 0) {
            return null;
        }
        String ip = ByteUtil.toHexString(bytes);
        String[] splitStr = ip.split("(?<=\\G.{" + 2 + "})");  // 2자리씩 잘라서 String 배열로 만듦.
        // 각 배열의 숫자를 10진수로 변환
        for (int i = 0; i < splitStr.length; i++) {
            splitStr[i] = String.valueOf(Integer.parseInt(splitStr[i], 16));    // 16진수를 Integer 로 변환.
        }
        ip = StringUtils.join(splitStr, ".");  // 배열 사이에 "." 를 넣어 하나의 스트링으로 합침.

        return ip;
    }

    public static ByteBuf parseIpAddress(String ipAddr) {
        ByteBuf buf = Unpooled.buffer(4);
        String[] arry = ipAddr.split("\\.");
        for (int i = 0; i < arry.length; i++) {
            buf.writeByte((byte) Integer.parseInt(arry[i]));
        }
        return buf;
    }

}
