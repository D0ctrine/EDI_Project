package com.edi.domain.application.impl;

import javax.transaction.Transactional;

import com.edi.domain.application.FileDefService;
import com.edi.domain.application.commands.setting.fileDefine.CreateFileDefCommand;
import com.edi.domain.application.commands.setting.fileDefine.UpdateFileDefCommand;
import com.edi.domain.model.settingfile.Setting.fileDefRepository;
import com.edi.domain.model.settingfile.Setting.file_define;
import com.edi.domain.model.user.UserId;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class FileDefServiceImpl implements FileDefService{

  private fileDefRepository fDefRepository;

  public FileDefServiceImpl(fileDefRepository fDefRepository) {
    this.fDefRepository = fDefRepository;
  }

  @Override
  public file_define create(CreateFileDefCommand ec) {
      file_define fDef = file_define.create(ec.getUserId(), ec.getCfgId(), ec.getFileName(), ec.getDescription(), ec.getFileSaveFolder(), ec.getFileCharset(), ec.getDataType(), ec.getFileType(), ec.getFtpEnvid(), ec.getSchMin(), ec.getSchHour(), ec.getSchDay(), ec.getSchWeek(), ec.getSchMonth(), ec.getNoDataSend(), ec.getSendFlag());
      fDefRepository.save(fDef);
    return fDef;
  }

  @Override
  public file_define createCopy(UserId userid, String configID, String fileName, String description, String saveFolder,
      String fileCharset, String dataType, String fileType, String ftpEnvId, String scheduleMin, String scheduleHour, String scheduleDay,
      String scheduleWeek, String scheduleMonth, String noDataSend, String sendFlag) {
        file_define fDef = file_define.create(userid, configID, fileName, description, saveFolder, fileCharset, dataType, fileType, ftpEnvId, scheduleMin, scheduleHour, scheduleDay, scheduleWeek, scheduleMonth, noDataSend, sendFlag);
        fDefRepository.save(fDef);
      return fDef;
  }

  @Override
  public file_define delete(UpdateFileDefCommand ec) {
    file_define fdef = file_define.update(ec.getId(), ec.getUserid(), ec.getCfgId(), ec.getFileName(), ec.getDescription(), ec.getFileSaveFolder(), ec.getFileCharset(), ec.getDataType(), ec.getFileType(), ec.getFtpEnvid(), ec.getSchMin(), ec.getSchHour(), ec.getSchDay(), ec.getSchWeek(), ec.getSchMonth(), ec.getNoDataSend());
    fdef.setDelete_date(fdef.getUpdate_date());
    fdef.setDelete_user(fdef.getUpdate_user());
    fDefRepository.deleteFileDefData(fdef);
    return fdef;
  }

  @Override
  public file_define getList(String cfgId) {
    file_define fdefList = fDefRepository.getFileDefData(cfgId);
    return fdefList;
  }

  @Override
  public file_define update(UpdateFileDefCommand ec) {
    file_define fdefList = file_define.update(ec.getId(), ec.getUserid(), ec.getCfgId(), ec.getFileName(), ec.getDescription(), ec.getFileSaveFolder(), ec.getFileCharset(), ec.getDataType(), ec.getFileType(), ec.getFtpEnvid(), ec.getSchMin(), ec.getSchHour(), ec.getSchDay(), ec.getSchWeek(), ec.getSchMonth(), ec.getNoDataSend());
    fDefRepository.updateFileDefData(fdefList);
    return fdefList;
  }

  @Override
  public int updateCronSendFlag(String id, String flag) {
    return fDefRepository.updateFileDefSendFlag(id, flag);
  }

}
