package com.edi.domain.application;

import java.util.List;

import com.edi.domain.application.commands.config.query.CreateQueryCommand;
import com.edi.domain.application.commands.config.query.UpdateQueryCommand;
import com.edi.domain.model.commonfile.query.QuerySetting;
import com.edi.domain.model.user.UserId;

public interface QueryService {
    /**
   * Get List from category table
   *
   * getList the List from Category
   * @return an instance of <code>Category</code> if found, null otherwise
   */
  List<QuerySetting> getList(String settingId);

  /**
   * Save a new user or an existing user
   *
   * @param user the user instance to be saved
   */
  List<QuerySetting> create(List<CreateQueryCommand> qc);

  /**
   * Create Copy
   * @return true or false
   */
  Boolean createCopy(List<QuerySetting> qc, UserId userid, String configId);

  QuerySetting createMainQuery(CreateQueryCommand qc);

  QuerySetting copyMainQuery(UserId userid, String settingId, String key, String type, String query, String dbType);

  /**
   * 카테고리 삭제
   * ID(PK)를 통해 카테고리를 삭제한다.
   */
  QuerySetting delete(UpdateQueryCommand qc);

  /**
   * 카테고리 갱신
   */
  List<QuerySetting> update(List<UpdateQueryCommand> qc);

  /**
   * 메인 쿼리 가져오기
   */
  QuerySetting getMainQuery(String settingId);
/**
 *
 * @param settingId
 * @return
 */
  List<QuerySetting> getUniqItemList(String settingId,String exConfigId);
}
