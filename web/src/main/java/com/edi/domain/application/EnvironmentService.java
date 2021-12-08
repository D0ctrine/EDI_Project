package com.edi.domain.application;

import java.util.List;

import com.edi.domain.application.commands.config.environment.CreateEnvCommand;
import com.edi.domain.application.commands.config.environment.UpdateEnvCommand;
import com.edi.domain.model.commonfile.environment.EnvSetting;
import com.edi.domain.model.user.UserId;

public interface EnvironmentService {
    /**
   * Get List from category table
   *
   * getList the List from Category
   * @return an instance of <code>Category</code> if found, null otherwise
   */
  List<EnvSetting> getList(String cfgId);

  /**
   * Save a new user or an existing user
   *
   * @param user the user instance to be saved
   */
  List<EnvSetting> create(List<CreateEnvCommand> ec);

  /**
   * Copy EnvSetting
   */
  Boolean createCopy(List<EnvSetting> ec, UserId userid, String cfgId);

  /**
   * 카테고리 삭제
   * ID(PK)를 통해 카테고리를 삭제한다.
   */
  EnvSetting delete(UpdateEnvCommand ec);

  /**
   * 카테고리 갱신
   */
  List<EnvSetting> update(List<UpdateEnvCommand> ec);
}
