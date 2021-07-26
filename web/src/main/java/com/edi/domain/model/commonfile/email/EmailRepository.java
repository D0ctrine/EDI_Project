package com.edi.domain.model.commonfile.email;

import java.util.List;

public interface EmailRepository {
  /**
   * 설정된 이메일 환경변수값들을 가져온다.
   */
  List<EmailSetting> getEmailData(String configId);

  /**
   * 이메일 하나의 환경변수 데이터를 가져온다.
   */
  EmailSetting findEmailData(String emailId);

  /**
   * 이메일 환경변수들을 수정한다.
   */
  List<EmailSetting> updateEmailData(List<EmailSetting> emailSetting);

  /**
   * 이메일 환경변수값 삭제
   */
  void deleteEmailData(EmailSetting emailSetting);

  /**
   * 이메일 환경변수 신규 저장
   */
  void save(EmailSetting emailSetting);
}
