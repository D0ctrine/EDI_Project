package com.edi.domain.application.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.edi.domain.application.EnvironmentService;
import com.edi.domain.application.commands.config.environment.CreateEnvCommand;
import com.edi.domain.application.commands.config.environment.UpdateEnvCommand;
import com.edi.domain.model.commonfile.environment.EnvRepository;
import com.edi.domain.model.commonfile.environment.EnvSetting;
import com.edi.domain.model.user.UserId;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class EnvironmentServiceImpl implements EnvironmentService{
  private EnvRepository envRepository;

  public EnvironmentServiceImpl(EnvRepository envRepository) {
    this.envRepository = envRepository;
  }
  @Override
  public List<EnvSetting> create(List<CreateEnvCommand> ec) {
    List<EnvSetting> envSettingList = new ArrayList<>();
    for(int i=0; i<ec.size(); i++){
      EnvSetting eSetting = EnvSetting.create(ec.get(i).getUserId(), ec.get(i).getCfgId(), ec.get(i).getItem(), ec.get(i).getValue(), ec.get(i).getNote(), ec.get(i).getSet_type());
      envRepository.save(eSetting);
      envSettingList.add(eSetting);
    }
    return envSettingList;
  }

  @Override
  public Boolean createCopy(List<EnvSetting> ec,UserId userid,String cfgId) {
    try {
      for(int i=0; i<ec.size(); i++){
        EnvSetting eSetting = EnvSetting.create(userid, cfgId, ec.get(i).getItem(), ec.get(i).getValue(), ec.get(i).getNote(), ec.get(i).getSet_type());
        envRepository.save(eSetting);
      }
      return true;
    } catch (Exception e) {
      new Error(e.getMessage());
    }
    return false;
  }

  @Override
  public EnvSetting delete(UpdateEnvCommand ec) {
    EnvSetting eSetting = EnvSetting.update(ec.getId(), ec.getUserid(), ec.getCfgId(), ec.getItem(), ec.getValue(), ec.getNote(),ec.getSet_type());
    envRepository.deleteEnvData(eSetting);
    return eSetting;
  }

  @Override
  public List<EnvSetting> getList(String cfgId) {
    return envRepository.getEnvData(cfgId);
  }

  @Override
  public List<EnvSetting> update(List<UpdateEnvCommand> ec) {
    List<EnvSetting> envSettingList = new ArrayList<>();
    for(int i=0; i<ec.size(); i++){
      EnvSetting eSetting = EnvSetting.update(ec.get(i).getId(), ec.get(i).getUserid(), ec.get(i).getCfgId(), ec.get(i).getItem(), ec.get(i).getValue(), ec.get(i).getNote(),ec.get(i).getSet_type());
      envSettingList.add(eSetting);
    }
    envSettingList = envRepository.updateEnvData(envSettingList);
    return envSettingList;
  }

}
