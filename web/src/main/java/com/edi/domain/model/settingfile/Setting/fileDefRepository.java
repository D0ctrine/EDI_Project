package com.edi.domain.model.settingfile.Setting;

import java.util.List;

public interface fileDefRepository {
  /**
   * 설정된 Value값들을 가져온다.
   */
  List<file_define> getFileDefData(String configId);

  /**
   * 해당하는 데이터를 가져온다.
   */
  file_define findFileDefData(String fdefId);

  /**
   * 저장된 데이터를 수정한다.
   */
  List<file_define> updateFileDefData(List<file_define> fdef);

  /**
   * value 값 삭제
   */
  void deleteFileDefData(file_define fdef);

  /**
   * 사용할 쿼리 데이터 신규 저장
   */
  void save(file_define fdef);
}
