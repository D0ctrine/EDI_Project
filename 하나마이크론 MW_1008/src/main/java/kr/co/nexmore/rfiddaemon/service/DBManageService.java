package kr.co.nexmore.rfiddaemon.service;

import kr.co.nexmore.rfiddaemon.vo.common.EventHistoryVO;
import kr.co.nexmore.rfiddaemon.vo.common.TagWriteVO;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.RFID_EIS_Reply_Out_Tag;
import kr.co.nexmore.rfiddaemon.vo.mes.OneooneVO;
import kr.co.nexmore.rfiddaemon.vo.reader.ReaderVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.TagDataVO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface DBManageService {

//    int tagHistory(TagWriteVO vo) throws Exception;

//    int tagReadHistory(TagWriteVO vo) throws Exception;

//    int tagUidHistory(TagWriteVO vo) throws Exception;

    int createReader(ReaderVO readerVO);

    int deleteReader(String readerMac) throws Exception;

    /**
     * DB에 있는 리더기 정보 목록을 조회한다.
     * @return List<ReaderVO>
     * @throws Exception
     */
    List<ReaderVO> getReaderList() throws Exception;

//    List<ReaderVO> getPlasmaReaderList() throws Exception;

    /**
     * DB에 있는 하나의 리더기 정보를 조회한다.
     * @param readerMac
     * @return ReaderVO
     * @throws Exception
     */
    ReaderVO getReaderInfo(String readerMac) throws Exception;

    String equipmentExistedCheck(String equipmentCode) throws Exception;

    String getEquipmentName(String equipmentCode) throws Exception;

//    int setReaderSetting(ReaderVO readerVO) throws Exception;
    int updateReader(ReaderVO readerVO) throws Exception;

    int ipExistedCheck(String changeIp) throws Exception;

    /**
     * MES(Oneoone) 연결 상태를 업데이트한다. (Y, N)
     * @param oneooneVO
     * @return int (row 개수)
     * @throws Exception
     */
    int setOneooneConnectStatus(OneooneVO oneooneVO) throws Exception;

    /**
     * MES(Oneoone) 연결 정보 목록을 조회한다. (MES 서버가 모델별 여러개가 될 수 있음.)
     * @return List<OneooneVO>
     * @throws Exception
     */
    List<OneooneVO> getOneooneConnectionList() throws Exception;

    List<String> getMacAddress(ArrayList<String> validList) throws Exception;

    /**
     * 새로운 MES(Oneoone) 연결 정보를 DB에 삽입한다.
     * @param oneooneVO
     * @return int (row 개수)
     * @throws Exception
     */
    int createOneooneConnection(OneooneVO oneooneVO) throws Exception;

    int getStationXId(OneooneVO oneooneVO) throws Exception;

    int updateOneooneConnection(OneooneVO vo) throws Exception;

    int updateOneooneTimeout(OneooneVO oneooneVO) throws Exception;

    int deleteOneooneConnection(int station_x_id) throws Exception;

    LinkedHashMap getLastReaderData(ReaderVO readerVO, int eventChannel) throws Exception;

    LinkedHashMap getLastLotStatus(String readerMac) throws Exception;

    LinkedHashMap getLotStartData(String readerMac, String lotId) throws Exception;

    EventHistoryVO getEventHistory(int eventLogId) throws Exception;

    String getLastInsertTime(EventHistoryVO eventHistoryVO) throws Exception;

//    Map getLastInsertId(EventHistoryVO eventHistoryVO) throws Exception;

    int insertEventHistory(EventHistoryVO eventHistoryVO) throws Exception;

    int insertMesEventHistory(EventHistoryVO eventHistoryVO) throws Exception;

    int updateEventHistory(EventHistoryVO eventHistoryVO) throws Exception;

    int setReaderConnectStatus(String flag, String readerMac) throws Exception;

    int setLotStatus(RFID_EIS_Reply_Out_Tag replyOutTag, String readerMac) throws Exception;

    int setLotEndForWB(String readerMac) throws Exception;

    int setCh2ReaderStatus(TagDataVO tagDataVO, String mesMessage, String readerMac, String processName) throws Exception;

    int setCh4ReaderStatus(TagDataVO tagDataVO, String mesMessage, String readerMac, String processName) throws Exception;

//    int setReaderLotStatus(RFID_EIS_Reply_Out_Tag replyOutTag, String eventType, String readerMac) throws Exception;

    int setMesUseYn(String readerMac) throws Exception;

    int setFirmwareVersion(String firmwareVersion, String readerMac) throws Exception;

    int setReaderNetwork(String changeIp, String gateway, String readerMac) throws Exception;

}