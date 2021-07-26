package com.edi.web.apis;

import java.util.ArrayList;
import java.util.List;

import com.edi.domain.application.EnvironmentService;
import com.edi.domain.application.FileDefService;
import com.edi.domain.application.HeadnTailService;
import com.edi.domain.application.QueryService;
import com.edi.domain.application.commands.config.environment.CreateEnvCommand;
import com.edi.domain.application.commands.config.environment.UpdateEnvCommand;
import com.edi.domain.application.commands.config.query.CreateQueryCommand;
import com.edi.domain.application.commands.config.query.UpdateQueryCommand;
import com.edi.domain.application.commands.setting.fileDefine.CreateFileDefCommand;
import com.edi.domain.application.commands.setting.fileDefine.UpdateFileDefCommand;
import com.edi.domain.application.commands.setting.headntail.CreateHnTCommand;
import com.edi.domain.application.commands.setting.headntail.UpdateHnTCommand;
import com.edi.domain.common.security.CurrentUser;
import com.edi.domain.model.commonfile.environment.EnvSetting;
import com.edi.domain.model.commonfile.query.QuerySetting;
import com.edi.domain.model.settingfile.Setting.file_define;
import com.edi.domain.model.settingfile.headntail.headntail;
import com.edi.domain.model.user.SimpleUser;
import com.edi.web.payload.config.CreateEnvPayload;
import com.edi.web.payload.config.CreateQueryPayload;
import com.edi.web.payload.config.UpdateEnvPayload;
import com.edi.web.payload.config.UpdateQueryPayload;
import com.edi.web.payload.settings.CreateAllSettingPayload;
import com.edi.web.payload.settings.CreateFileDefPayload;
import com.edi.web.payload.settings.CreateHnTPayload;
import com.edi.web.payload.settings.UpdateAllSettingPayload;
import com.edi.web.payload.settings.UpdateFileDefPayload;
import com.edi.web.payload.settings.UpdateHnTPayload;
import com.edi.web.results.ApiResult;
import com.edi.web.results.Result;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SettingApiController {
  private EnvironmentService envService;
  private QueryService queryService;
  private FileDefService fileDefService;
  private HeadnTailService hntService;

  public SettingApiController(EnvironmentService envService,QueryService queryService,FileDefService fileDefService,HeadnTailService hntService) {
    this.envService = envService;
    this.queryService = queryService;
    this.fileDefService = fileDefService;
    this.hntService = hntService;
  }

  @GetMapping("/api/setting")
  public ResponseEntity<ApiResult> getSetting(@RequestParam String categoryId,
                                             @CurrentUser SimpleUser currentUser) {

    List<EnvSetting> envList = new ArrayList<>();
    List<QuerySetting> itemList = new ArrayList<>();
    List<file_define> fileDefList = new ArrayList<>();
    List<headntail> HnTList = new ArrayList<>();

    if(categoryId!=null){
      envList = envService.getList(categoryId);
      itemList = queryService.getList(categoryId);
      fileDefList = fileDefService.getList(categoryId);
      HnTList = hntService.getList(categoryId);
    }

    ApiResult apiResult = ApiResult.blank();
    apiResult.add("envList", envList);
    apiResult.add("itemList", itemList);
    apiResult.add("filedefList", fileDefList);
    apiResult.add("hntList", HnTList);
    return Result.ok(apiResult);
  }

  @PostMapping("/api/setting/create")
  public ResponseEntity<ApiResult> CreateSetting(@RequestBody CreateAllSettingPayload payload,
                                             @CurrentUser SimpleUser currentUser) {
    List<CreateEnvPayload> envSettingList = payload.getEnv();
    List<CreateQueryPayload> querySettingList = payload.getItemGrp();
    List<CreateFileDefPayload> fileDefSettingList = payload.getFileDef();
    List<CreateHnTPayload> HnTSettingList = payload.getHeadNtail();
    String configId = payload.getCg_id();

    List<CreateEnvCommand> envCommandList = new ArrayList<>();
    List<CreateQueryCommand> queryCommandList = new ArrayList<>();
    List<CreateFileDefCommand> fileDefCommandList = new ArrayList<>();
    List<CreateHnTCommand> HnTCommandList = new ArrayList<>();


    for(CreateEnvPayload envSetting : envSettingList){
      envSetting.setCfgId(configId);
      CreateEnvCommand envCommand = envSetting.toCommand(currentUser.getUserId());
      envCommandList.add(envCommand);
    }
    List<EnvSetting> envList = envService.create(envCommandList);

    for(CreateQueryPayload querySetting : querySettingList){
      querySetting.setSettingId(configId);
      CreateQueryCommand queryCommand = querySetting.toCommand(currentUser.getUserId());
      queryCommandList.add(queryCommand);
    }
    List<QuerySetting> queryList = queryService.create(queryCommandList);

    for(CreateFileDefPayload fileDefSetting : fileDefSettingList){
      fileDefSetting.setCfgId(configId);
      CreateFileDefCommand fileDefCommand = fileDefSetting.toCommand(currentUser.getUserId());
      fileDefCommandList.add(fileDefCommand);
    }
    List<file_define> fileDefList = fileDefService.create(fileDefCommandList);

    for(CreateHnTPayload HnTSetting : HnTSettingList){
      HnTSetting.setCfgId(configId);
      CreateHnTCommand HnTCommand = HnTSetting.toCommand(currentUser.getUserId());
      HnTCommandList.add(HnTCommand);
    }
    List<headntail> hntList = hntService.create(HnTCommandList);

    ApiResult apiResult = ApiResult.blank();
    apiResult.add("envList", envList);
    apiResult.add("queryList", queryList);
    apiResult.add("fileDefList", fileDefList);
    apiResult.add("hntList", hntList);

    return Result.ok(apiResult);
  }

  @PostMapping("/api/setting/update")
  public ResponseEntity<ApiResult> UpdateSetting(@RequestBody UpdateAllSettingPayload payload,
                                             @CurrentUser SimpleUser currentUser) {
      List<UpdateEnvPayload> envSettingList = payload.getEnv();
      List<UpdateQueryPayload> querySettingList = payload.getItemGrp();
      List<UpdateFileDefPayload> fileDefSettingList = payload.getFileDef();
      List<UpdateHnTPayload> hntSettingList = payload.getHeadNtail();
      String configId = payload.getCg_id();

      List<UpdateEnvCommand> envCommandList = new ArrayList<>();
      List<UpdateQueryCommand> queryCommandList = new ArrayList<>();
      List<UpdateHnTCommand> hntCommandList = new ArrayList<>();
      List<UpdateFileDefCommand> fileDefCommandList = new ArrayList<>();

      for( int i=0; i<envSettingList.size(); i++){
        envSettingList.get(i).setCfgId(configId);
        UpdateEnvCommand envCommand = envSettingList.get(i).toCommand(currentUser.getUserId());
        envCommandList.add(envCommand);
      }
      if(envSettingList.isEmpty()==false)envService.update(envCommandList);

      for( int i=0; i<querySettingList.size(); i++){
        querySettingList.get(i).setSettingId(configId);
        UpdateQueryCommand queryCommand = querySettingList.get(i).toCommand(currentUser.getUserId());
        queryCommandList.add(queryCommand);
      }
      if(querySettingList.isEmpty()==false)queryService.update(queryCommandList);

      for(int i=0;i<fileDefSettingList.size();i++){
        fileDefSettingList.get(i).setCfgId(configId);
        UpdateFileDefCommand fileDefCommand = fileDefSettingList.get(i).toCommand(currentUser.getUserId());
        fileDefCommandList.add(fileDefCommand);
      }
      if(fileDefSettingList.isEmpty()==false)fileDefService.update(fileDefCommandList);

      for(int i=0;i<hntSettingList.size();i++){
        hntSettingList.get(i).setCfgId(configId);
        UpdateHnTCommand hnTCommand = hntSettingList.get(i).toCommand(currentUser.getUserId());
        hntCommandList.add(hnTCommand);
      }
      if(hntSettingList.isEmpty()==false)hntService.update(hntCommandList);

      ApiResult apiResult = ApiResult.blank();
      return Result.ok(apiResult);
  }

  @PostMapping("/api/setting/filedefine/delete") //굳이 필요없을듯.....
  public ResponseEntity<ApiResult> DeletFileDefine(@RequestBody UpdateFileDefPayload  payload, @CurrentUser SimpleUser currentUser) {
      UpdateFileDefCommand fileDefCommand = payload.toCommand(currentUser.getUserId());
      fileDefService.delete(fileDefCommand);
      ApiResult apiResult = ApiResult.blank();
      return Result.ok(apiResult);
  }

  @PostMapping("/api/setting/headntail/delete")
  public ResponseEntity<ApiResult> DeleteHeadnTail(@RequestBody UpdateHnTPayload  payload, @CurrentUser SimpleUser currentUser) {
      UpdateHnTCommand hntCommand = payload.toCommand(currentUser.getUserId());
      hntService.delete(hntCommand);
      ApiResult apiResult = ApiResult.blank();
      return Result.ok(apiResult);
  }

  @PostMapping("/api/setting/env/delete")
  public ResponseEntity<ApiResult> DeleteEnv(@RequestBody UpdateEnvPayload payload, @CurrentUser SimpleUser currentUser) {
      UpdateEnvCommand envCommand = payload.toCommand(currentUser.getUserId());
      envService.delete(envCommand);
      ApiResult apiResult = ApiResult.blank();
      return Result.ok(apiResult);
  }

  @PostMapping("/api/setting/query/delete")
  public ResponseEntity<ApiResult> DeleteQuery(@RequestBody UpdateQueryPayload payload, @CurrentUser SimpleUser currentUser) {
      UpdateQueryCommand queryCommand = payload.toCommand(currentUser.getUserId());
      queryService.delete(queryCommand);
      ApiResult apiResult = ApiResult.blank();
      return Result.ok(apiResult);
  }
}
