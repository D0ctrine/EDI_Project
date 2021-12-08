package kr.co.nexmore.rfiddaemon.common;

/**
 * PacketConstant Class
 *
 * 리더 통신에 사용되는 Packet 정의
 * UDP 통신에서 사용하는 Command Packet 정의(Request, Response)
 *
 */
public final class PacketConstant {

    // Common(공통 packet)
    public static final byte STX = (byte)0x7E;    // 시작 태그
    public static final byte ETX = (byte)0x7F;    // 끝 태그

    // Address
    public static final byte ADDRESS_BROADCAST = (byte)0x00;  // address : BROADCAST
    public static final byte ADDRESS_HOST = (byte)0x01;   // address : HOST

    // Direction
    public static final byte DIRECTION_HOST_TO_READER = (byte)0x00;   // Direction : Host -> Reader
    public static final byte DIRECTION_READER_TO_HOST = (byte)0x01;   // Direction : Reader -> Host

    // UDP Command
    public static final byte UDP_SEARCH_REQUEST_COM = (byte)0x00;
    public static final byte UDP_SET_NETWORK_REQUEST_COM = (byte)0x05;
    public static final byte UDP_FIRMWARE_READY_REQUEST_COM = (byte)0x0D;
    public static final byte UDP_FIRMWARE_DOWNLOAD_REQUEST_COM = (byte)0x0F;

    public static final byte UDP_CORE_READY_REQUEST_COM = (byte)0x1D;
    public static final byte UDP_CORE_DOWNLOAD_REQUEST_COM = (byte)0x1F;

//    public static final byte[] UDP_COMMON = {'R', 'F', 'e', 'g', 'i', 'n', 'e'};
    public static final byte UDP_SEARCH_RESPONSE_COM = (byte)0x80;
    public static final byte UDP_SET_NETWORK_RESPONSE_COM = (byte)0x85;
    public static final byte UDP_FIRMWARE_READY_RESPONSE_COM = (byte)0x8D;
    public static final byte UDP_FIRMWARE_DOWNLOAD_RESPONSE_COM = (byte)0x8F;

    public static final byte UDP_CORE_READY_RESPONSE_COM = (byte)0x9D;
    public static final byte UDP_CORE_DOWNLOAD_RESPONSE_COM = (byte)0x9F;
    
}
