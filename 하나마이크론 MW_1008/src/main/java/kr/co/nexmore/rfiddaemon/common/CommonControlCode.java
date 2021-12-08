package kr.co.nexmore.rfiddaemon.common;

/**
 * MES(EIS) 통신, 리더 STATUS, EVENT TYPE 공통 코드 Class
 */
public class CommonControlCode {
	public final static String VERSION="1.0";
	public final static int RESULT_CODE_SUCCESS = 1;
	public final static int RESULT_CODE_FAIL = 0;
	
	//Highway one-o-one 통신관련
	public static final int XGEN_SUCCESS = 0;
	public static final int XGEN_DEFAULT_TTL = 60000;
	public static final int XGEN_DEFAULT_GETMESSAGE_INTERVAL = 60000;
	public static final int XGEN_DEFAULT_RETRY_COUNT = 60;
	public static final int XGEN_DEFAULT_RETRY_TERM = 1000;
	
	public static final String XGEN_VERSION = "4.0";
	public static final String XGEN_TAG_VERSION = "VERSION";
	public static final String XGEN_TAG_MODULE = "MODULE";
	public static final String XGEN_TAG_INTERFACE = "INTERFACE";
	public static final String XGEN_TAG_OPERATION = "OPERATION";
	public static final String XGEN_TAG_HOSTNAME = "HOSTNAME";
	public static final String XGEN_TAG_HOSTADDR = "HOSTADDR";
	public static final String XGEN_TAG_PEERHOSTNAME = "PEERHOSTNAME";
	public static final String XGEN_TAG_PEERHOSTADDR = "PEERHOSTADDR";
	public static final String XGEN_TAG_RESULT_CODE = "RESULT_CODE";
	public static final String XGEN_TAG_RESULT_MSG = "RESULT_MSG";
	
	public static final int XGEN_ERROR_UNEXPECTED_VERSION = -0x15;
	public static final int XGEN_ERROR_UNEXPECTED_MODULE = -0x16;
	public static final int XGEN_ERROR_UNEXPECTED_OPERATION = -0x17;

	// EVENT HISTORY RET_VAL
	public static final int TAG_EVENT_SUCCESS = 0;
	public static final int TAG_EVENT_FAIL = 1;
	public static final int EVENT_RESPONSE_WAITING = 2;
	public static final int MES_EVENT_SUCCESS = 48;
	public static final int MES_EVENT_FAIL = 49;
	public static final int MES_EVENT_NO_RESPONSE = 57;
	
	// MES(Oneoone) OPCODE
	public static final String OPCODE_EISStartRequest = "RFID_EIS_Start_Magazine_Req";
	public static final String OPCODE_EISEndRequest = "RFID_EIS_End_Magazine_Req";
	public static final String OPCODE_EISManualEndRequest = "ManualEndRequest";
	public static final String OPCODE_writeMagazineId= "writeMagazineId";
	public static final String OPCODE_writeLotId= "writeLotId";
	public static final String OPCODE_EISTunerAlive = "EIS_RFID_Tuner_Alive";
	public static final String OPCODE_EISReWriteSuccess = "RFID_EIS_Rewrite_Success_Req";
//	public static final String OPCODE_EISWriteLotID = "EIS_RFID_Write_Lot_ID_Req";
	public static final String OPCODE_EISWriteLotIdOrEndLotId = "EIS_RFID_Write_Lot_ID_Req";
//	public static final String OPCODE_EISEndLotID = "EIS_RFID_End_Lot_ID_Req";
	public static final String OPCODE_EISChangeResourceInfoRequest = "RFID_EIS_Change_Res_Info";


	// eventType - reader connection
	public static final String READER_CONNECT = "CONNECT";
	public static final String READER_DISCONNECT = "DISCONNECT";


	// eventType - tagValidation
	public static final String VALIDATION_FAIL = "INVALID";
	public static final String TAG_UID_NOT_EXIST = "TAG_UID_NOT_EXIST";
	public static final String MZ_ID_NOT_EXIST = "MZ_ID_NOT_EXIST";
	public static final String INVALID_MZ_ID = "INVALID_MZ_ID";


	// eventType - attach, detach event
	public static final String TAG_EVENT_START = "START";
	public static final String TAG_EVENT_END = "END";
	public static final String TAG_EVENT_FORCE_END = "FORCE_END";

	// eventType - write
	public static final String WRITE_LOT_ID = "WRITE";

	// eventType - attach, detach send to eis event
	public static final String SEND_MES_START = "SEND_MES_START";
	public static final String SEND_MES_END = "SEND_MES_END";

	// eventType - equipment operation status (lot start or lot end)
	public static final String LOT_START = "LOT_START";
	public static final String LOT_END = "LOT_END";

	// eventType - equipment operation terminated.
	public static final String MAGAZINE_EJECT = "MZ_EJECT";
}
