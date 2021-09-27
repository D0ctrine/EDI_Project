package com.edi.web.apis;

import java.util.ArrayList;
import java.util.List;

import com.edi.domain.application.EmailService;
import com.edi.domain.application.EnvironmentService;
import com.edi.domain.application.FtpService;
import com.edi.domain.application.QueryService;
import com.edi.domain.application.commands.config.email.CreateEmailCommand;
import com.edi.domain.application.commands.config.email.UpdateEmailCommand;
import com.edi.domain.application.commands.config.environment.CreateEnvCommand;
import com.edi.domain.application.commands.config.environment.UpdateEnvCommand;
import com.edi.domain.application.commands.config.ftp.CreateFtpCommand;
import com.edi.domain.application.commands.config.ftp.UpdateFtpCommand;
import com.edi.domain.application.commands.config.query.CreateQueryCommand;
import com.edi.domain.application.commands.config.query.UpdateQueryCommand;
import com.edi.domain.common.security.CurrentUser;
import com.edi.domain.model.commonfile.email.EmailSetting;
import com.edi.domain.model.commonfile.environment.EnvSetting;
import com.edi.domain.model.commonfile.ftp.FtpSetting;
import com.edi.domain.model.commonfile.query.QuerySetting;
import com.edi.domain.model.user.SimpleUser;
import com.edi.web.payload.config.CreateAllPayload;
import com.edi.web.payload.config.CreateEmailPayload;
import com.edi.web.payload.config.CreateEnvPayload;
import com.edi.web.payload.config.CreateFtpPayload;
import com.edi.web.payload.config.CreateQueryPayload;
import com.edi.web.payload.config.UpdateAllPayload;
import com.edi.web.payload.config.UpdateEmailPayload;
import com.edi.web.payload.config.UpdateEnvPayload;
import com.edi.web.payload.config.UpdateFtpPayload;
import com.edi.web.payload.config.UpdateQueryPayload;
import com.edi.web.results.ApiResult;
import com.edi.web.results.Result;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConfigApiController {
  private EmailService emailService;
  private EnvironmentService envService;
  private FtpService ftpService;
  private QueryService queryService;

  public ConfigApiController(EmailService emailService,EnvironmentService envService,FtpService ftpService,QueryService queryService) {
    this.emailService = emailService;
    this.envService = envService;
    this.ftpService = ftpService;
    this.queryService = queryService;
  }

  @GetMapping("/api/config")
  public ResponseEntity<ApiResult> getConfig(@RequestParam String categoryId,
                                             @CurrentUser SimpleUser currentUser) {

    List<EnvSetting> envList = new ArrayList<>();
    List<EmailSetting> emailList = new ArrayList<>();
    List<FtpSetting> ftpList = new ArrayList<>();
    List<QuerySetting> itemList = new ArrayList<>();
    if(categoryId!=null){
      envList = envService.getList(categoryId);
      emailList = emailService.getList(categoryId);
      ftpList = ftpService.getList(categoryId);
      itemList = queryService.getList(categoryId);
    }

    ApiResult apiResult = ApiResult.blank();
    apiResult.add("envList", envList);
    apiResult.add("emailList", emailList);
    apiResult.add("ftpList", ftpList);
    apiResult.add("itemList", itemList);
    return Result.ok(apiResult);
  }

  @PostMapping("/api/config/create")
  public ResponseEntity<ApiResult> CreateConfig(@RequestBody CreateAllPayload payload,
                                             @CurrentUser SimpleUser currentUser) {
    List<CreateEnvPayload> envSettingList = payload.getEnv();
    List<CreateEmailPayload> emailSettingList = payload.getEmail();
    List<CreateFtpPayload> ftpSettingList = payload.getFtp();
    List<CreateQueryPayload> querySettingList = payload.getItemGrp();
    String configId = payload.getCg_id();

    List<CreateEnvCommand> envCommandList = new ArrayList<>();
    List<CreateEmailCommand> emailCommandList = new ArrayList<>();
    List<CreateFtpCommand> ftpCommandList = new ArrayList<>();
    List<CreateQueryCommand> queryCommandList = new ArrayList<>();

    for( int i=0; i<envSettingList.size(); i++){
      envSettingList.get(i).setCfgId(configId);
      CreateEnvCommand envCommand = envSettingList.get(i).toCommand(currentUser.getUserId());
      envCommandList.add(envCommand);
    }
    List<EnvSetting> envList = envService.create(envCommandList);

    for( int i=0; i<emailSettingList.size(); i++){
      emailSettingList.get(i).setCfgId(configId);
      CreateEmailCommand emailCommand = emailSettingList.get(i).toCommand(currentUser.getUserId());
      emailCommandList.add(emailCommand);
    }
    List<EmailSetting> emailList = emailService.create(emailCommandList);

    for( int i=0; i<ftpSettingList.size(); i++){
      ftpSettingList.get(i).setCfgId(configId);
      CreateFtpCommand ftpCommand = ftpSettingList.get(i).toCommand(currentUser.getUserId());
      ftpCommandList.add(ftpCommand);
    }
    List<FtpSetting> ftpList = ftpService.create(ftpCommandList);

    for( int i=0; i<querySettingList.size(); i++){
      querySettingList.get(i).setSettingId(configId);
      CreateQueryCommand queryCommand = querySettingList.get(i).toCommand(currentUser.getUserId());
      queryCommandList.add(queryCommand);
    }
    List<QuerySetting> queryList = queryService.create(queryCommandList);

    ApiResult apiResult = ApiResult.blank();
    apiResult.add("envList", envList);
    apiResult.add("emailList", emailList);
    apiResult.add("ftpList", ftpList);
    apiResult.add("queryList", queryList);

    return Result.ok(apiResult);
  }

  @PostMapping("/api/config/update")
  public ResponseEntity<ApiResult> UpdateConfig(@RequestBody UpdateAllPayload payload,
                                             @CurrentUser SimpleUser currentUser) {
      List<UpdateEnvPayload> envSettingList = payload.getUpdtEnv();
      List<UpdateEmailPayload> emailSettingList = payload.getUpdtEmail();
      List<UpdateFtpPayload> ftpSettingList = payload.getUpdtFtp();
      List<UpdateQueryPayload> querySettingList = payload.getUpdtItemGrp();
      String configId = payload.getCg_id();

      List<UpdateEnvCommand> envCommandList = new ArrayList<>();
      List<UpdateEmailCommand> emailCommandList = new ArrayList<>();
      List<UpdateFtpCommand> ftpCommandList = new ArrayList<>();
      List<UpdateQueryCommand> queryCommandList = new ArrayList<>();

      for( int i=0; i<envSettingList.size(); i++){
        envSettingList.get(i).setCfgId(configId);
        UpdateEnvCommand envCommand = envSettingList.get(i).toCommand(currentUser.getUserId());
        envCommandList.add(envCommand);
      }
      if(envSettingList.isEmpty()==false)envService.update(envCommandList);

      for( int i=0; i<emailSettingList.size(); i++){
        emailSettingList.get(i).setCfgId(configId);
        UpdateEmailCommand emailCommand = emailSettingList.get(i).toCommand(currentUser.getUserId());
        emailCommandList.add(emailCommand);
      }
      if(emailSettingList.isEmpty()==false)emailService.update(emailCommandList);

      for( int i=0; i<ftpSettingList.size(); i++){
        ftpSettingList.get(i).setCfgId(configId);
        UpdateFtpCommand ftpCommand = ftpSettingList.get(i).toCommand(currentUser.getUserId());
        ftpCommandList.add(ftpCommand);
      }
      if(ftpSettingList.isEmpty()==false)ftpService.update(ftpCommandList);

      for( int i=0; i<querySettingList.size(); i++){
        querySettingList.get(i).setSettingId(configId);
        UpdateQueryCommand queryCommand = querySettingList.get(i).toCommand(currentUser.getUserId());
        queryCommandList.add(queryCommand);
      }
      if(querySettingList.isEmpty()==false)queryService.update(queryCommandList);
      ApiResult apiResult = ApiResult.blank();
      return Result.ok(apiResult);
  }

  @PostMapping("/api/config/email/delete")
  public ResponseEntity<ApiResult> DeleteEmail(@RequestBody UpdateEmailPayload payload, @CurrentUser SimpleUser currentUser) {
      UpdateEmailCommand emailCommand = payload.toCommand(currentUser.getUserId());
      emailService.delete(emailCommand);
      ApiResult apiResult = ApiResult.blank();
      return Result.ok(apiResult);
  }
  @PostMapping("/api/config/env/delete")
  public ResponseEntity<ApiResult> DeleteEnv(@RequestBody UpdateEnvPayload payload, @CurrentUser SimpleUser currentUser) {
      UpdateEnvCommand envCommand = payload.toCommand(currentUser.getUserId());
      envService.delete(envCommand);
      ApiResult apiResult = ApiResult.blank();
      return Result.ok(apiResult);
  }
  @PostMapping("/api/config/ftp/delete")
  public ResponseEntity<ApiResult> DeleteFtp(@RequestBody UpdateFtpPayload payload, @CurrentUser SimpleUser currentUser) {
      UpdateFtpCommand ftpCommand = payload.toCommand(currentUser.getUserId());
      ftpService.delete(ftpCommand);
      ApiResult apiResult = ApiResult.blank();
      return Result.ok(apiResult);
  }
  @PostMapping("/api/config/query/delete")
  public ResponseEntity<ApiResult> DeleteQuery(@RequestBody UpdateQueryPayload payload, @CurrentUser SimpleUser currentUser) {
      UpdateQueryCommand queryCommand = payload.toCommand(currentUser.getUserId());
      queryService.delete(queryCommand);
      ApiResult apiResult = ApiResult.blank();
      return Result.ok(apiResult);
  }

  @GetMapping("/api/config/ftp")
  public ResponseEntity<ApiResult> getConfigFtp(@RequestParam String categoryId,
                                             @CurrentUser SimpleUser currentUser) {
    List<FtpSetting> ftpList = new ArrayList<>();
    if(categoryId!=null) ftpList = ftpService.getList(categoryId);
    ApiResult apiResult = ApiResult.blank();
    apiResult.add("ftpList", ftpList);
    return Result.ok(apiResult);
  }

  @GetMapping("/api/config/item")
  public ResponseEntity<ApiResult> getConfigItem(@RequestParam String categoryId,@RequestParam String exConfigId,
                                             @CurrentUser SimpleUser currentUser) {
    List<QuerySetting> itemList = new ArrayList<>();
    if(categoryId!=null) itemList = queryService.getUniqItemList(categoryId, exConfigId);
    ApiResult apiResult = ApiResult.blank();
    apiResult.add("itemList", itemList);
    return Result.ok(apiResult);
  }
}
