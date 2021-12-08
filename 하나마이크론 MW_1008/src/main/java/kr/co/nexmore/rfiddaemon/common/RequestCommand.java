package kr.co.nexmore.rfiddaemon.common;

/**
 *
 * RequestCommand
 * TCP 통신 command Enum Class
 *
 */
public enum RequestCommand {

    TAG_PACKET((byte)0xA0),   // READ 시 태그 PACKET
    FAULT_PACKET((byte)0xA1), // 실패 PACKET
    COMMAND_RESPONSE_PACKET((byte)0xA3),  // 명령에 대한 응답 PACKET
    KEEPALIVE_PACKET((byte)0xA4),

    SYS_CONF((byte)0x00),
    SYS_CONTROL((byte)0x01),
    ISO15693_CONF((byte)0x40),
    ISO15693_CONTROL((byte)0x41),
    TEST_MODE((byte)0xB0),
    FIRMWARE((byte)0xD0),

    // System Configuration Command & SubCommand
    SYS_CONF_OPERATNG_MODE(SYS_CONF.getCommand(), (byte)0x10, "OPERATING MODE SETTING"),    //  리더 동작모드 command & subCommand
    SYS_CONF_COMMUNICATION_MODE(SYS_CONF.getCommand(), (byte)0x20, "COMMUNICATION MODE SETTING"),      //  시리얼 통신 방식 선택 command & subCommand
    SYS_CONF_SERIAL_SETTING(SYS_CONF.getCommand(), (byte)0x21, "SERIAL DATARATE SETTING"),   //  시리얼 통신 방식 DataRate 선택 command & subCommand
    SYS_CONF_ANTTENA_SELECT(SYS_CONF.getCommand(), (byte)0x30, "SELECT ANTENNA"),   //  안테나 선택 command & subCommand
    SYS_CONF_BUZZER_ONOFF(SYS_CONF.getCommand(), (byte)0x70, "BUZZER ON/OFF SETTING"),     //  리더의 부저 ON/OFF command & subCommand
    SYS_CONF_KEEPALIVE_INTERVAL(SYS_CONF.getCommand(), (byte)0x11, "KEEPALIVE INTERVAL SETTING"),     //  리더의 keepAlive 주기 설정 command & subCommand

    // System Control Command & SubCommand
    SYS_CONTROL_READING(SYS_CONTROL.getCommand(), (byte)0x10, "READER START/STOP"),            //  리더의 읽기 동작 START/STOP command & subCommand
    SYS_CONTROL_READ_EXTERNAL_IO(SYS_CONTROL.getCommand(), (byte)0x20, "EXTERNAL I/O PORT READ"),   //  외부 I/O 포트의 상태를 읽는 command & subCommand
    SYS_CONTROL_WRITE_EXTERNAL_IO(SYS_CONTROL.getCommand(), (byte)0x30, "EXTERNAL I/O PORT WRITE"),  //  외부 I/O 포트에 특정한 값 출력 command & subCommand
    SYS_CONTROL_GET_PRODUCT_INFO(SYS_CONTROL.getCommand(), (byte)0x40, "GET READER PRODUCT INFORMATION"),   //  리더기의 모델명, 하드웨어 버전, 펌웨어 버전 확인 command & subCommand
    SYS_CONTROL_READER_RESET(SYS_CONTROL.getCommand(), (byte)0x50, "RESET READER"),       //  리더기 리셋 command & subCommand
    SYS_CONTROL_READ_REGISTER(SYS_CONTROL.getCommand(), (byte)0x60, "REGISTER READ"),      //  해당 레지스터값을 리더로부터 전송 받는 command & subCommand
    SYS_CONTROL_SAVE_REGISTER(SYS_CONTROL.getCommand(), (byte)0x70, "READER CURRENT SETTING SAVE"),      //  현재 리더의 있는 레지스터값을 리더의 메모리에 저장하는 command & subCommand
    SYS_CONTROL_LOAD_INITIALIZE(SYS_CONTROL.getCommand(), (byte)0x80, "INITIALIZE READER"),     //  초기 리더가 만들어진 시점의 레지스터값으로 setting 하는 command & subCommand


    // ISO 15693 Configuration Command & SubCommand
    ISO15693_CONF_SET_MEMORY_ADDR(ISO15693_CONF.getCommand(), (byte)0x10, "READ ADDRESS SETTING"),    //  Continuous 모드에서 Tag 메모리 주소 설정하는 command & subCommand
    ISO15693_CONF_SET_MEMORY_LENGTH(ISO15693_CONF.getCommand(), (byte)0x20, "READ LENGTH SETTING"),  //  Continuous 모드에서 Tag 메모리 길이 설정하는 command & subCommand
    ISO15693_CONF_SECURITY_MODE(ISO15693_CONF.getCommand(), (byte)0x30, "SECURITY MODE SETTING"),      //  보안모드 설정하는 command & subCommand
    ISO15693_CONF_SET_PASSWORD(ISO15693_CONF.getCommand(), (byte)0x40, "SECURITY MODE PASSWORD SETTING"),       //  NXP 태그 보안모드 패스워드 설정하는 command & subCommand


    // ISO 15693 Control Command & SubCommand
    ISO15693_CONTROL_READ_DATA(ISO15693_CONTROL.getCommand(), (byte)0x10, "READ TAG DATA"),    //  해당 메모리주소의 데이터 값을 읽는 command & subCommand
    ISO15693_CONTROL_READ_ID(ISO15693_CONTROL.getCommand(), (byte)0x11, "READ TAG UID"),      //  Tag ID를 읽는 command & subCommand
    ISO15693_CONTROL_WRITE_DATA(ISO15693_CONTROL.getCommand(), (byte)0x20, "WRITE TAG DATA"),   //  해당 메모리주소에 데이터 값을 쓰는 command & subCommand
    ISO15693_CONTROL_PROTECT_PAGE(ISO15693_CONTROL.getCommand(), (byte)0x20, "PROTECT MEMORY PAGE"), //  해당 메모리페이지를 주어진 패스워드로 protect 하는 command & subCommand


    // Test Mode Command & SubCommand
    TEST_MODE_RF_ON(TEST_MODE.getCommand(), (byte)0x10, "RF ON"),      //  RF 파워를 키는 command & subCommand
    TEST_MODE_RF_OFF(TEST_MODE.getCommand(), (byte)0x20, "RF OFF"),     //  RF 파워를 끄는 command & subCommand

    // Firmware command & subCommand
    FIRMWARE_DOWNLOAD(FIRMWARE.getCommand(), (byte)0x10, "FIRMWARE UPDATE");     // 펌웨어 업그레이드 시작 command & subCommand


    private final int command;
    private final int subCommand;
    private final String actionName;

    RequestCommand(int command) {
        this.command = command;
        subCommand = 0;
        this.actionName = null;
    }

/*
    RequestCommand(int command, int subCommand) {
        this.command = command;
        this.subCommand = subCommand;
        this.actionName = null;
    }
*/

    RequestCommand(int command, int subCommand, String actionName) {
        this.command = command;
        this.subCommand = subCommand;
        this.actionName = actionName;
    }

    public byte getCommand() {
        return (byte)command;
    }

    public static RequestCommand getCommand(byte com) {
        for(RequestCommand requestCommand : RequestCommand.values()) {
            if(requestCommand.getCommand() == com) {
                return requestCommand;
            }
        }
        return null;
    }

    public static String getActionName(byte com, byte subCom) {
        for(RequestCommand requestCommand : RequestCommand.values()) {
            if(requestCommand.getCommand() == com && requestCommand.getSubCommand() == subCom) {
                return requestCommand.getActionName();
            }
        }
        return null;
    }

    public byte getSubCommand() {
        return (byte)subCommand;
    }

    public String getActionName() {
        return actionName;
    }
}
