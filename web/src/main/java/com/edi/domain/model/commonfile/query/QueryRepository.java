package com.edi.domain.model.commonfile.query;

import java.util.List;

public interface QueryRepository {
  /**
   * 설정된 Value값들을 가져온다.
   */
  List<QuerySetting> getQueryData(String configId);
  /**
   * 설정된 Main Value값을 가져온다.
   */
  QuerySetting getMainQueryData(String configId);
  /**
   * 해당하는 데이터를 가져온다.
   */
  QuerySetting findQueryData(String qId);

  /**
   * 저장된 데이터를 수정한다.
   */
  List<QuerySetting> updateQueryData(List<QuerySetting> querySetting);

  /**
   * value 값 삭제
   */
  void deleteQueryData(QuerySetting qSetting);

  /**
   * 사용할 쿼리 데이터 신규 저장
   */
  void save(QuerySetting qSetting);

  /**
   *
   * @param configId
   * @return
   */
  List<QuerySetting> getUniqQueryData(String configId,String exConfigId);
}
