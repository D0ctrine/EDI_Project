package com.edi.domain.application;

import java.util.List;

import com.edi.domain.application.commands.config.ftp.CreateFtpCommand;
import com.edi.domain.application.commands.config.ftp.UpdateFtpCommand;
import com.edi.domain.model.commonfile.ftp.FtpSetting;

public interface FtpService {
    /**
   * Get List from category table
   *
   * getList the List from Category
   * @return an instance of <code>Category</code> if found, null otherwise
   */
  List<FtpSetting> getList(String cfgId);

  /**
   * Save a new user or an existing user
   *
   * @param user the user instance to be saved
   */
  List<FtpSetting> create(List<CreateFtpCommand> fc);

  /**
   * 카테고리 삭제
   * ID(PK)를 통해 카테고리를 삭제한다.
   */
  FtpSetting delete(UpdateFtpCommand fc);

  /**
   * 카테고리 갱신
   */
  List<FtpSetting> update(List<UpdateFtpCommand> fc);
}
