package com.edi.domain.model.commonfile.ftp;

import java.util.List;

public interface FtpRepository {
  /**
   * 설정된 FTP 값들을 가져온다.
   */
  List<FtpSetting> getFtpData(String configId);

  /**
   * 해당하는 데이터를 가져온다.
   */
  FtpSetting findFtpData(String ftpId);

  /**
   * 저장된 데이터를 수정한다.
   */
  List<FtpSetting> updateFtpData(List<FtpSetting> ftpSetting);

  /**
   * value 값 삭제
   */
  void deleteQueryData(FtpSetting fSetting);

  /**
   * 사용할 쿼리 데이터 신규 저장
   */
  void save(FtpSetting fSetting);
}
