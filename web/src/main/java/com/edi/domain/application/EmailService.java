package com.edi.domain.application;

import java.util.List;

import com.edi.domain.application.commands.config.email.CreateEmailCommand;
import com.edi.domain.application.commands.config.email.UpdateEmailCommand;
import com.edi.domain.model.commonfile.email.EmailSetting;

public interface EmailService {
    /**
   * Get List from category table
   *
   * getList the List from Category
   * @return an instance of <code>Category</code> if found, null otherwise
   */
  List<EmailSetting> getList(String cfgId);

  /**
   * Save a new user or an existing user
   *
   * @param user the user instance to be saved
   */
  List<EmailSetting> create(List<CreateEmailCommand> ec);

  /**
   * 카테고리 삭제
   * ID(PK)를 통해 카테고리를 삭제한다.
   */
  EmailSetting delete(UpdateEmailCommand ec);

  /**
   * 카테고리 갱신
   */
  List<EmailSetting> update(List<UpdateEmailCommand> ec);
}
