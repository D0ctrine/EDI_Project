package kr.co.nexmore.rfiddaemon.service;

import kr.co.nexmore.rfiddaemon.mapper.OneooneManageMapper;
import kr.co.nexmore.rfiddaemon.mapper.ReaderManageMapper;
import kr.co.nexmore.rfiddaemon.vo.common.EventHistoryVO;
import kr.co.nexmore.rfiddaemon.vo.common.TagWriteVO;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.RFID_EIS_Reply_Out_Tag;
import kr.co.nexmore.rfiddaemon.vo.mes.OneooneVO;
import kr.co.nexmore.rfiddaemon.vo.reader.ReaderVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.TagDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("dbManageService")
public class DBManageServiceImpl implements DBManageService {

    private ReaderManageMapper readerManageMapper;
    private OneooneManageMapper oneooneManageMapper;

    public DBManageServiceImpl(ReaderManageMapper readerManageMapper, OneooneManageMapper oneooneManageMapper) {
        this.readerManageMapper = readerManageMapper;
        this.oneooneManageMapper = oneooneManageMapper;
    }

    public int tagHistory(TagWriteVO vo) throws Exception {
        return readerManageMapper.tagHistory(vo);
    }

    public int tagReadHistory(TagWriteVO vo) throws Exception {
        return readerManageMapper.tagReadHistory(vo);
    }

    public int tagUidHistory(TagWriteVO vo) throws Exception {
        return readerManageMapper.tagUidHistory(vo);
    }

    @Transactional
    public int createReader(ReaderVO readerVO) {
        int result = 0;
        try {
            int createResult = readerManageMapper.createReader(readerVO);

            if (createResult > 0) {
                result = readerManageMapper.createReaderStatus(readerVO);
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }

        return result;
    }

    public int deleteReader(String readerMac) throws Exception {
        return readerManageMapper.deleteReader(readerMac);
    }

    public List<ReaderVO> getReaderList() throws Exception {
        return readerManageMapper.getReaderList();
    }


//    public List<ReaderVO> getPlasmaReaderList() throws Exception {
//        return readerManageMapper.getPlasmaReaderList();
//    }


    public ReaderVO getReaderInfo(String readerMac) throws Exception {
        return readerManageMapper.getReaderInfo(readerMac);
    }

    public String equipmentExistedCheck(String equipmentCode) throws Exception {
        return readerManageMapper.equipmentExistedCheck(equipmentCode);
    }

    public String getEquipmentName(String equipmentCode) throws Exception {
        return readerManageMapper.getEquipmentName(equipmentCode);
    }

    public int updateReader(ReaderVO readerVO) throws Exception {
        return readerManageMapper.updateReader(readerVO);
    }
/*
    public int setReaderSetting(ReaderVO readerVO) throws Exception {
        return readerManageMapper.setReaderSetting(readerVO);
    }
*/

    public int ipExistedCheck(String changeIp) throws Exception {
        return readerManageMapper.ipExistedCheck(changeIp);
    }

    public LinkedHashMap getLastReaderData(ReaderVO readerVO, int eventChannel) throws Exception {
        return readerManageMapper.getLastReaderData(readerVO, eventChannel);
    }

    public LinkedHashMap getLastLotStatus(String readerMac) throws Exception {
        return readerManageMapper.getLastLotStatus(readerMac);
    }

    public LinkedHashMap getLotStartData(String readerMac, String lotId) throws Exception {
        return readerManageMapper.getLotStartData(readerMac, lotId);
    }

    public EventHistoryVO getEventHistory(int eventLogId) throws Exception {
        return readerManageMapper.getEventHistory(eventLogId);
    }
    public String getLastInsertTime(EventHistoryVO eventHistoryVO) throws Exception {
        return readerManageMapper.getLastInsertTime(eventHistoryVO);
    }

//    public Map getLastInsertId(EventHistoryVO eventHistoryVO) throws Exception {
//        return readerManageMapper.getLastInsertId(eventHistoryVO);
//    }

    public List getMacAddress(ArrayList<String> validList) throws Exception {
        return readerManageMapper.getMacAddress(validList);
    }

    public int insertEventHistory(EventHistoryVO eventHistoryVO) throws Exception {
        return readerManageMapper.insertEventHistory(eventHistoryVO);
    }

    public int insertMesEventHistory(EventHistoryVO eventHistoryVO) throws Exception {
        return readerManageMapper.insertMesEventHistory(eventHistoryVO);
    }

    public int updateEventHistory(EventHistoryVO eventHistoryVO) throws Exception {
        return readerManageMapper.updateEventHistory(eventHistoryVO);
    }

    public int setReaderConnectStatus(String flag, String readerMac) throws Exception {
        return readerManageMapper.setReaderConnectStatus(flag, readerMac);
    }

    public int setLotStatus(RFID_EIS_Reply_Out_Tag replyOutTag, String readerMac) throws Exception {
        return readerManageMapper.setLotStatus(replyOutTag, readerMac);
    }

    public int setLotEndForWB(String readerMac) throws Exception {
        return readerManageMapper.setLotEndForWB(readerMac);
    }

    public int setCh2ReaderStatus(TagDataVO tagDataVO, String mesMessage, String readerMac, String processName) throws Exception {
        return readerManageMapper.setCh2ReaderStatus(tagDataVO, mesMessage, readerMac, processName);
    }

    //    public int setReaderStatus(TagDataVO tagDataVO, String mesMessage, String eventType, String readerMac) throws Exception {
    public int setCh4ReaderStatus(TagDataVO tagDataVO, String mesMessage, String readerMac, String processName) throws Exception {
        return readerManageMapper.setCh4ReaderStatus(tagDataVO, mesMessage, readerMac, processName);
//        return readerManageMapper.setReaderStatus(tagDataVO, mesMessage, eventType, readerMac);
    }

    /*public int setReaderLotStatus(RFID_EIS_Reply_Out_Tag replyOutTag, String eventType, String readerMac) throws Exception {
        return readerManageMapper.setReaderLotStatus(replyOutTag, eventType, readerMac);
    }*/
    public int setMesUseYn(String readerMac) throws Exception {
        return readerManageMapper.setMesUseYn(readerMac);
    }

    public int setReaderNetwork(String changeIp, String gateway, String readerMac) throws Exception {
        return readerManageMapper.setReaderNetwork(changeIp, gateway, readerMac);
    }

    public int setFirmwareVersion(String firmwareVersion, String readerMac) throws Exception {
        return readerManageMapper.setFirmwareVersion(firmwareVersion, readerMac);
    }

    /**
     * MES(Oneoone) 연결 상태 업데이트
     *
     * @param vo
     * @return
     * @throws Exception
     */
    public int setOneooneConnectStatus(OneooneVO vo) throws Exception {
        return oneooneManageMapper.setOneooneConnectStatus(vo);
    }

    public List<OneooneVO> getOneooneConnectionList() throws Exception {
        return oneooneManageMapper.getOneooneConnectionList();
    }

    public int createOneooneConnection(OneooneVO vo) throws Exception {
        return oneooneManageMapper.createOneooneConnection(vo);
    }

    public int getStationXId(OneooneVO oneooneVO) throws Exception {
        return oneooneManageMapper.getStationXId(oneooneVO);
    }

    public int updateOneooneConnection(OneooneVO vo) throws Exception {
        return oneooneManageMapper.updateOneooneConnection(vo);
    }

    public int deleteOneooneConnection(int station_x_id) throws Exception {
        return oneooneManageMapper.deleteOneooneConnection(station_x_id);
    }

    public int updateOneooneTimeout(OneooneVO oneooneVO) throws Exception {
        return oneooneManageMapper.updateOneooneTimeout(oneooneVO);
    }
}
