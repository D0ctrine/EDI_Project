package com.edi.domain.model.commonfile.environment;

import java.util.List;

public interface EnvRepository {
  /**
   * 설정된 Env 값들을 가져온다.
   */
  List<EnvSetting> getEnvData(String configId);

  /**
   * 해당하는 데이터를 가져온다.
   */
  EnvSetting findEnvData(String envId);

  /**
   * 저장된 데이터를 수정한다.
   */
  List<EnvSetting> updateEnvData(List<EnvSetting> envSetting);

  /**
   * value 값 삭제
   */
  void deleteEnvData(EnvSetting envSetting);

  /**
   * 사용할 쿼리 데이터 신규 저장
   */
  void save(EnvSetting envSetting);
}
