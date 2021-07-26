package com.edi.domain.application.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.edi.domain.application.FileDefService;
import com.edi.domain.application.commands.setting.fileDefine.CreateFileDefCommand;
import com.edi.domain.application.commands.setting.fileDefine.UpdateFileDefCommand;
import com.edi.domain.model.settingfile.Setting.fileDefRepository;
import com.edi.domain.model.settingfile.Setting.file_define;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class FileDefServiceImpl implements FileDefService{
  private fileDefRepository fDefRepository;

  public FileDefServiceImpl(fileDefRepository fDefRepository) {
    this.fDefRepository = fDefRepository;
  }

  @Override
  public List<file_define> create(List<CreateFileDefCommand> ec) {
    List<file_define> fdefList = new ArrayList<file_define>();
    for(int i=0;i<ec.size();i++){
      file_define fDef = file_define.create(ec.get(i).getUserId(), ec.get(i).getCfgId(), ec.get(i).getDescription(), ec.get(i).getScheduleMonth(), ec.get(i).getScheduleWeek(), ec.get(i).getScheduleDay(), ec.get(i).getScheduleHour(), ec.get(i).getScheduleMin(), ec.get(i).getFileExtractType());
      fDefRepository.save(fDef);
      fdefList.add(fDef);
    }
    return fdefList;
  }

  @Override
  public file_define delete(UpdateFileDefCommand ec) {
    file_define fdef = file_define.update(ec.getId(), ec.getUserid(), ec.getCfgId(), ec.getDescription(), ec.getScheduleMonth(), ec.getScheduleWeek(), ec.getScheduleDay(), ec.getScheduleHour(), ec.getScheduleMin(), ec.getFileExtractType());
    fdef.setDelete_date(fdef.getUpdate_date());
    fdef.setDelete_user(fdef.getUpdate_user());
    fDefRepository.deleteFileDefData(fdef);
    return fdef;
  }

  @Override
  public List<file_define> getList(String cfgId) {
    List<file_define> fdefList = fDefRepository.getFileDefData(cfgId);
    return fdefList;
  }

  @Override
  public List<file_define> update(List<UpdateFileDefCommand> ec) {
    List<file_define> fdefList = new ArrayList<file_define>();
    for(int i=0;i<ec.size();i++){
      fdefList.add(file_define.update(ec.get(i).getId(), ec.get(i).getUserid(), ec.get(i).getCfgId(), ec.get(i).getDescription(), ec.get(i).getScheduleMonth(), ec.get(i).getScheduleWeek(), ec.get(i).getScheduleDay(), ec.get(i).getScheduleHour(), ec.get(i).getScheduleMin(), ec.get(i).getFileExtractType()));
    }
    fDefRepository.updateFileDefData(fdefList);
    return fdefList;
  }

}
