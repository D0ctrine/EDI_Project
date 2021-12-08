package com.edi.domain.application.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.edi.domain.application.QueryService;
import com.edi.domain.application.commands.config.query.CreateQueryCommand;
import com.edi.domain.application.commands.config.query.UpdateQueryCommand;
import com.edi.domain.model.commonfile.query.QueryRepository;
import com.edi.domain.model.commonfile.query.QuerySetting;
import com.edi.domain.model.user.UserId;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class QueryServiceImpl implements QueryService{
  private QueryRepository queryRepository;

  public QueryServiceImpl(QueryRepository queryRepository) {
    this.queryRepository = queryRepository;
  }

  @Override
  public List<QuerySetting> create(List<CreateQueryCommand> qc) {
    List<QuerySetting> querySettingList = new ArrayList<>();
    for(int i=0; i<qc.size(); i++){
      QuerySetting qSetting = QuerySetting.create(qc.get(i).getUserId(), qc.get(i).getSettingId(), qc.get(i).getKey(), qc.get(i).getType(), qc.get(i).getQuery(), qc.get(i).getDb_type());
      queryRepository.save(qSetting);
      querySettingList.add(qSetting);
    }
    return querySettingList;
  }

  @Override
  public Boolean createCopy(List<QuerySetting> qc, UserId userid, String configId) {
    try {
      for(int i=0; i<qc.size(); i++){
        QuerySetting qSetting = QuerySetting.create(userid, configId, qc.get(i).getKey(), qc.get(i).getType(), qc.get(i).getQuery(), qc.get(i).getDbtype());
        queryRepository.save(qSetting);
      }
      return true;
    } catch (Exception e) {
      new Error(e.getMessage());
    }
    return false;
  }

  @Override
  public QuerySetting delete(UpdateQueryCommand qc) {
    QuerySetting qSetting = QuerySetting.update(qc.getId(), qc.getUserId(), qc.getSettingId(), qc.getKey(), qc.getType(), qc.getQuery(), qc.getDb_type());
      queryRepository.deleteQueryData(qSetting);
    return qSetting;
  }

  @Override
  public List<QuerySetting> getList(String settingId) {
    return queryRepository.getQueryData(settingId);
  }

  @Override
  public QuerySetting getMainQuery(String settingId) {
    return queryRepository.getMainQueryData(settingId);
  }

  @Override
  public List<QuerySetting> update(List<UpdateQueryCommand> qc) {
    List<QuerySetting> querySettingList = new ArrayList<>();
    for(int i=0; i<qc.size(); i++){
      QuerySetting fSetting = QuerySetting.update(qc.get(i).getId(), qc.get(i).getUserId(), qc.get(i).getSettingId(), qc.get(i).getKey(), qc.get(i).getType(), qc.get(i).getQuery(), qc.get(i).getDb_type());
      querySettingList.add(fSetting);
    }
    querySettingList = queryRepository.updateQueryData(querySettingList);
    return querySettingList;
  }

  @Override
  public QuerySetting createMainQuery(CreateQueryCommand qc) {
    QuerySetting qSetting = QuerySetting.create(qc.getUserId(), qc.getSettingId(), qc.getKey(), qc.getType(), qc.getQuery(), qc.getDb_type());
    queryRepository.save(qSetting);
    return qSetting;
  }

  @Override
  public QuerySetting copyMainQuery(UserId userid, String settingId, String key, String type, String query, String dbType) {
    QuerySetting qSetting = QuerySetting.create(userid, settingId, key, type, query, dbType);
    queryRepository.save(qSetting);
    return qSetting;
  }

  @Override
  public List<QuerySetting> getUniqItemList(String settingId,String exConfigId) {
    return queryRepository.getUniqQueryData(settingId, exConfigId);
  }


}
