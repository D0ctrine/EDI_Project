package com.edi.domain.application;

import java.util.List;

import com.edi.domain.application.commands.setting.fileDefine.CreateFileDefCommand;
import com.edi.domain.application.commands.setting.fileDefine.UpdateFileDefCommand;
import com.edi.domain.model.settingfile.Setting.file_define;

public interface FileDefService {
    /**
   * 파일 설명, 주기, 추출 방식 리스트 가져오기
   */
  file_define getList(String cfgId);
  /**
   * 파일 세팅 신규 생성
   */
  file_define create(CreateFileDefCommand ec);
  /**
   * 파일 세팅 삭제
   * ID(PK)를 통해 카테고리를 삭제한다.
   */
  file_define delete(UpdateFileDefCommand ec);

  /**
   * 파일 세팅 갱신
   */
  file_define update(UpdateFileDefCommand ec);
}
