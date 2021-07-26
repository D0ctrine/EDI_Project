package com.edi.domain.application.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.edi.domain.application.EmailService;
import com.edi.domain.application.commands.config.email.CreateEmailCommand;
import com.edi.domain.application.commands.config.email.UpdateEmailCommand;
import com.edi.domain.model.commonfile.email.EmailRepository;
import com.edi.domain.model.commonfile.email.EmailSetting;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class EmailServiceImpl implements EmailService {
  private EmailRepository emailRepository;

  public EmailServiceImpl(EmailRepository emailRepository) {
    this.emailRepository = emailRepository;
  }

  @Override
  public List<EmailSetting> create(List<CreateEmailCommand> ec) {
    List<EmailSetting> emailSettingList = new ArrayList<>();
    for(int i=0; i<ec.size(); i++){
      EmailSetting eSetting = EmailSetting.create(ec.get(i).getUserId(), ec.get(i).getCfgId(), ec.get(i).getHost(), ec.get(i).getPort(), ec.get(i).getAccessId(), ec.get(i).getAccessPwd());
      emailRepository.save(eSetting);
      emailSettingList.add(eSetting);
    }
    return emailSettingList;
  }

  @Override
  public EmailSetting delete(UpdateEmailCommand ec) {
      EmailSetting eSetting = EmailSetting.update(ec.getId(),ec.getUserId(), ec.getCfgId(), ec.getHost(), ec.getPort(), ec.getAccessId(), ec.getAccessPwd());
      emailRepository.deleteEmailData(eSetting);
    return eSetting;
  }

  @Override
  public List<EmailSetting> getList(String cfgId) {
    return emailRepository.getEmailData(cfgId);
  }

  @Override
  public List<EmailSetting> update(List<UpdateEmailCommand> ec) {
    List<EmailSetting> emailSettingList = new ArrayList<>();
    for(int i=0; i<ec.size(); i++){
    EmailSetting eSetting = EmailSetting.update(ec.get(i).getId(), ec.get(i).getUserId(), ec.get(i).getCfgId(), ec.get(i).getHost(), ec.get(i).getPort(), ec.get(i).getAccessId(), ec.get(i).getAccessPwd());
    emailSettingList.add(eSetting);
    }
    emailSettingList = emailRepository.updateEmailData(emailSettingList);
    return emailSettingList;
  }

}
