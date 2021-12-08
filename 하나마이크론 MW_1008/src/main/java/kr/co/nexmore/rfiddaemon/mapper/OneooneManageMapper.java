package kr.co.nexmore.rfiddaemon.mapper;

import kr.co.nexmore.rfiddaemon.vo.mes.OneooneVO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("oneooneManageMapper")
public interface OneooneManageMapper {

    int setOneooneConnectStatus(OneooneVO oneooneVO) throws Exception;

    List<OneooneVO> getOneooneConnectionList() throws Exception;

    int createOneooneConnection(OneooneVO oneooneVO) throws Exception;

    int getStationXId(OneooneVO oneooneVO) throws Exception;

    int updateOneooneConnection(OneooneVO oneooneVO) throws Exception;

    int updateOneooneTimeout(OneooneVO oneooneVO) throws Exception;

    int deleteOneooneConnection(int stationXId) throws Exception;

}
