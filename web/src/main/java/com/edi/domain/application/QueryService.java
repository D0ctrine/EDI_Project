package com.edi.domain.application;

import java.util.List;

import com.edi.domain.application.commands.config.query.CreateQueryCommand;
import com.edi.domain.application.commands.config.query.UpdateQueryCommand;
import com.edi.domain.model.commonfile.query.QuerySetting;

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

  QuerySetting createMainQuery(CreateQueryCommand qc);

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
