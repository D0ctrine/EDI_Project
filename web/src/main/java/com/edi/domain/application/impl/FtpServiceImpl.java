package com.edi.domain.application.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.edi.domain.application.FtpService;
import com.edi.domain.application.commands.config.ftp.CreateFtpCommand;
import com.edi.domain.application.commands.config.ftp.UpdateFtpCommand;
import com.edi.domain.model.commonfile.ftp.FtpRepository;
import com.edi.domain.model.commonfile.ftp.FtpSetting;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class FtpServiceImpl implements FtpService{
  private FtpRepository ftpRepository;

  public FtpServiceImpl(FtpRepository ftpRepository) {
    this.ftpRepository = ftpRepository;
  }
  @Override
  public List<FtpSetting> create(List<CreateFtpCommand> fc) {
    List<FtpSetting> ftpSettingList = new ArrayList<>();
    for(int i=0; i<fc.size(); i++){
      FtpSetting fSetting = FtpSetting.create(fc.get(i).getUserId(), fc.get(i).getCfgId(), fc.get(i).getFtpAddr(),fc.get(i).getUserid(), fc.get(i).getPwd(), fc.get(i).getBaseDir(), fc.get(i).getType());
      ftpRepository.save(fSetting);
      ftpSettingList.add(fSetting);
    }
    return ftpSettingList;
  }

  @Override
  public FtpSetting delete(UpdateFtpCommand fc) {
    FtpSetting fSetting = FtpSetting.update(fc.getId(), fc.getUserId(), fc.getCfgId(), fc.getFtpAddr(), fc.getUserid(), fc.getPwd(), fc.getBaseDir(), fc.getType());
      ftpRepository.deleteQueryData(fSetting);
    return fSetting;
  }

  @Override
  public List<FtpSetting> getList(String cfgId) {
    return ftpRepository.getFtpData(cfgId);
  }

  @Override
  public List<FtpSetting> update(List<UpdateFtpCommand> fc) {
    List<FtpSetting> ftpSettingList = new ArrayList<>();
    for(int i=0; i<fc.size(); i++){
      FtpSetting fSetting = FtpSetting.update(fc.get(i).getId(), fc.get(i).getUserId(), fc.get(i).getCfgId(), fc.get(i).getFtpAddr(), fc.get(i).getUserid(), fc.get(i).getPwd(), fc.get(i).getBaseDir(), fc.get(i).getType());
      ftpSettingList.add(fSetting);
    }
    ftpSettingList = ftpRepository.updateFtpData(ftpSettingList);
    return ftpSettingList;
  }

}
