package kr.co.nexmore.rfiddaemon.common;

import kr.co.nexmore.rfiddaemon.vo.reader.tcp.request.RequestHeaderVO;


/**
 * 리더 통신 - Request 에 사용하는 RequestHeader Constant Class
 */
public final class HeaderConstant {

    public static final RequestHeaderVO GET_READER_INFO_HEADER  = new RequestHeaderVO(2);

    public static final RequestHeaderVO SET_READER_MODE_HEADER  = new RequestHeaderVO(4);

    public static final RequestHeaderVO SET_BUZZER_ONOFF_HEADER  = new RequestHeaderVO(3);

    public static final RequestHeaderVO SET_KEEPALIVE_INTERVAL_HEADER  = new RequestHeaderVO(3);

    public static final RequestHeaderVO GET_READER_EXTERNAL_IO_HEADER  = new RequestHeaderVO(2);

    public static final RequestHeaderVO READ_REGISTER_HEADER  = new RequestHeaderVO(4);

    public static final RequestHeaderVO SAVE_REGISTER_HEADER  = new RequestHeaderVO(2);

    public static final RequestHeaderVO LOAD_INITIALIZE_HEADER  = new RequestHeaderVO(2);

    public static final RequestHeaderVO START_STOP_READING_HEADER  = new RequestHeaderVO(3);

    public static final RequestHeaderVO RESET_READER_HEADER = new RequestHeaderVO(2);

    public static final RequestHeaderVO SET_MEMORY_ADDR_HEADER = new RequestHeaderVO(3);

    public static final RequestHeaderVO SET_MEMORY_LENGTH_HEADER = new RequestHeaderVO(3);

    public static final RequestHeaderVO READ_DATA_HEADER  = new RequestHeaderVO(5);

    public static final RequestHeaderVO READ_UID_HEADER  = new RequestHeaderVO(3);

    public static final RequestHeaderVO WRITE_DATA_HEADER  = new RequestHeaderVO(5);

}
