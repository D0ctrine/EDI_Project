package com.edi.domain.model.settingfile.headntail;

import java.util.List;

public interface hntRepository {
  /**
   * 설정된 Value값들을 가져온다.
   */
  List<headntail> getHnTData(String configId);

  /**
   * 해당하는 데이터를 가져온다.
   */
  headntail findHnTData(String hntId);

  /**
   * 저장된 데이터를 수정한다.
   */
  List<headntail> updateHnTData(List<headntail> hnt);

  /**
   * value 값 삭제
   */
  void deleteHnTData(headntail hnt);

  /**
   * 사용할 쿼리 데이터 신규 저장
   */
  void save(headntail hnt);
}
