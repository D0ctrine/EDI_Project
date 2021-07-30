package com.edi.domain.application.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.edi.domain.application.QueryService;
import com.edi.domain.application.commands.config.query.CreateQueryCommand;
import com.edi.domain.application.commands.config.query.UpdateQueryCommand;
import com.edi.domain.model.commonfile.query.QueryRepository;
import com.edi.domain.model.commonfile.query.QuerySetting;

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
      QuerySetting qSetting = QuerySetting.create(qc.get(i).getUserId(), qc.get(i).getSettingId(), qc.get(i).getKey(), qc.get(i).getType(), qc.get(i).getQuery());
      queryRepository.save(qSetting);
      querySettingList.add(qSetting);
    }
    return querySettingList;
  }

  @Override
  public QuerySetting delete(UpdateQueryCommand qc) {
    QuerySetting qSetting = QuerySetting.update(qc.getId(), qc.getUserId(), qc.getSettingId(), qc.getKey(), qc.getType(), qc.getQuery());
      queryRepository.deleteQueryData(qSetting);
    return qSetting;
  }

  @Override
  public List<QuerySetting> getList(String settingId) {
    return queryRepository.getQueryData(settingId);
  }

  @Override
  public List<QuerySetting> update(List<UpdateQueryCommand> qc) {
    List<QuerySetting> querySettingList = new ArrayList<>();
    for(int i=0; i<qc.size(); i++){
      QuerySetting fSetting = QuerySetting.update(qc.get(i).getId(), qc.get(i).getUserId(), qc.get(i).getSettingId(), qc.get(i).getKey(), qc.get(i).getType(), qc.get(i).getQuery());
      querySettingList.add(fSetting);
    }
    querySettingList = queryRepository.updateQueryData(querySettingList);
    return querySettingList;
  }

  @Override
  public QuerySetting createMainQuery(CreateQueryCommand qc) {
    QuerySetting qSetting = QuerySetting.create(qc.getUserId(), qc.getSettingId(), qc.getKey(), qc.getType(), qc.getQuery());
    queryRepository.save(qSetting);
    return qSetting;
  }

}
