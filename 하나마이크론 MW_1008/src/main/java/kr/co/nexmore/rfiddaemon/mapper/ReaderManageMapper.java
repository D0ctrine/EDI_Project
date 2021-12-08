package kr.co.nexmore.rfiddaemon.mapper;

import kr.co.nexmore.rfiddaemon.vo.common.EventHistoryVO;
import kr.co.nexmore.rfiddaemon.vo.common.TagWriteVO;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.RFID_EIS_Reply_Out_Tag;
import kr.co.nexmore.rfiddaemon.vo.reader.ReaderVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.TagDataVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component("readerManageMapper")
public interface ReaderManageMapper {

    int tagHistory(TagWriteVO vo) throws Exception;

    int tagReadHistory(TagWriteVO vo) throws Exception;

    int tagUidHistory(TagWriteVO vo) throws Exception;

    int createReader(ReaderVO readerVO) throws Exception;

    int createReaderStatus(ReaderVO readerVO)throws Exception;

    int deleteReader(String readerVO) throws Exception;

    List<ReaderVO> getReaderList() throws Exception;

//    List<ReaderVO> getPlasmaReaderList() throws Exception;

    ReaderVO getReaderInfo(String readerMac) throws Exception;

    String equipmentExistedCheck(String equipmentCode) throws Exception;

    String getEquipmentName(String equipmentCode) throws Exception;

    int updateReader(ReaderVO readerVO) throws Exception;
//    int setReaderSetting(ReaderVO readerVO) throws Exception;

    List getMacAddress(ArrayList<String> validList) throws Exception;

    int ipExistedCheck(String changeIp) throws Exception;

    LinkedHashMap getLastReaderData(@Param(value = "vo") ReaderVO readerVO, @Param(value = "eventChannel") int eventChannel) throws Exception;

    LinkedHashMap getLastLotStatus(String readerMac) throws Exception;

    LinkedHashMap getLotStartData(@Param(value = "readerMac") String readerMac, @Param(value = "lotId")String lotId) throws Exception;

    EventHistoryVO getEventHistory(int eventLogId) throws Exception;

    String getLastInsertTime(EventHistoryVO eventHistoryVO) throws Exception;

//    Map getLastInsertId(EventHistoryVO eventHistoryVO) throws Exception;

    int insertEventHistory(EventHistoryVO eventHistoryVO) throws Exception;

    int insertMesEventHistory(EventHistoryVO eventHistoryVO) throws Exception;

    int updateEventHistory(EventHistoryVO eventHistoryVO) throws Exception;

    int setReaderConnectStatus(@Param(value = "flag") String flag, @Param(value = "readerMac") String readerMac) throws Exception;

    int setLotStatus(@Param(value = "reply") RFID_EIS_Reply_Out_Tag replyOutTag, @Param(value = "readerMac") String readerMac) throws Exception;

    int setLotEndForWB(String readerMac) throws Exception;

    int setCh2ReaderStatus(@Param(value = "vo")TagDataVO tagDataVO, @Param(value = "mesMessage") String mesMessage, @Param(value = "readerMac") String readerMac, @Param(value = "processName") String processName) throws Exception;

    int setCh4ReaderStatus(@Param(value = "vo")TagDataVO tagDataVO, @Param(value = "mesMessage") String mesMessage, @Param(value = "readerMac") String readerMac, @Param(value = "processName") String processName) throws Exception;

    int setReaderLotStatus(@Param(value = "reply") RFID_EIS_Reply_Out_Tag replyOutTag, @Param("eventType") String eventType, @Param(value = "readerMac") String readerMac) throws Exception;

    int setMesUseYn(String readerMac) throws Exception;

    int setReaderNetwork(@Param(value = "changeIp") String changeIp, @Param(value = "gateway") String gateway, @Param(value = "readerMac") String readerMac) throws Exception;

    int setFirmwareVersion(@Param(value = "version") String firmwareVersion, @Param(value = "readerMac") String readerMac) throws Exception;
}
