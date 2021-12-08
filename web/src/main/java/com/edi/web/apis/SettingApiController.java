package com.edi.web.apis;

import java.util.ArrayList;
import java.util.List;

import com.edi.domain.application.CategoryService;
import com.edi.domain.application.EnvironmentService;
import com.edi.domain.application.FileDefService;
import com.edi.domain.application.HeadnTailService;
import com.edi.domain.application.QueryService;
import com.edi.domain.application.SQLService;
import com.edi.domain.application.commands.config.environment.CreateEnvCommand;
import com.edi.domain.application.commands.config.environment.UpdateEnvCommand;
import com.edi.domain.application.commands.config.query.CreateQueryCommand;
import com.edi.domain.application.commands.config.query.UpdateQueryCommand;
import com.edi.domain.application.commands.setting.fileDefine.CreateFileDefCommand;
import com.edi.domain.application.commands.setting.fileDefine.UpdateFileDefCommand;
import com.edi.domain.application.commands.setting.headntail.CreateHnTCommand;
import com.edi.domain.application.commands.setting.headntail.UpdateHnTCommand;
import com.edi.domain.common.security.CurrentUser;
import com.edi.domain.model.category.Category;
import com.edi.domain.model.commonfile.environment.EnvSetting;
import com.edi.domain.model.commonfile.query.QuerySetting;
import com.edi.domain.model.settingfile.Setting.file_define;
import com.edi.domain.model.settingfile.headntail.headntail;
import com.edi.domain.model.user.SimpleUser;
import com.edi.web.payload.SelectSQLPayload;
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
  private SQLService sqlService;
  private CategoryService catService;

  public SettingApiController(CategoryService categoryService,EnvironmentService envService,QueryService queryService,FileDefService fileDefService,HeadnTailService hntService,SQLService sqlService) {
    this.catService = categoryService;
    this.envService = envService;
    this.queryService = queryService;
    this.fileDefService = fileDefService;
    this.hntService = hntService;
    this.sqlService = sqlService;
  }

  @GetMapping("/api/copySetting")
  public ResponseEntity<ApiResult> copySetting(@RequestParam String categoryId,
                                             @CurrentUser SimpleUser currentUser) {
    ApiResult apiResult = ApiResult.blank();

    // Copy Setting
    file_define fileDefList = new file_define();
    List<EnvSetting> envList = new ArrayList<>();
    List<QuerySetting> itemList = new ArrayList<>();
    List<headntail> HnTList = new ArrayList<>();

    if(categoryId!=null) {
      // Copy Category
      Category cg = catService.copyOne(categoryId, currentUser.getUserId());
      envList = envService.getList(categoryId);
      itemList = queryService.getList(categoryId);
      fileDefList = fileDefService.getList(categoryId);
      HnTList = hntService.getList(categoryId);
      QuerySetting query = queryService.getMainQuery(categoryId); // Copy SQL
      categoryId = cg.getId().toString();
      // File Definition
      fileDefService.createCopy(currentUser.getUserId(), categoryId, fileDefList.getFileName(), fileDefList.getFile_desc(), fileDefList.getFileSaveFolder(), fileDefList.getFileCharset(), fileDefList.getDataType(), fileDefList.getFileType(), fileDefList.getFtpEnvId(), fileDefList.getScheduleMin(), fileDefList.getScheduleHour(), fileDefList.getScheduleDay(), fileDefList.getScheduleWeek(), fileDefList.getScheduleMonth(), fileDefList.getNoDataSend(), fileDefList.getSend_flag());

      // Env Setting
      if(envList.size()!=0) envService.createCopy(envList, currentUser.getUserId(), categoryId);

      // Item Setting
      if(itemList.size()!=0) queryService.createCopy(itemList, currentUser.getUserId(), categoryId);

      // Head and Tail Settings
      if(HnTList.size()!=0) hntService.createCopy(HnTList, currentUser.getUserId(), categoryId);


      if (query!=null) queryService.copyMainQuery(currentUser.getUserId(), categoryId, query.getKey(), query.getType(), query.getQuery(), query.getDbtype());

      apiResult.add("result", "Success");
    } else {
      apiResult.add("result", "Fail");
    }

    return Result.ok(apiResult);
  }

  @GetMapping("/api/setting")
  public ResponseEntity<ApiResult> getSetting(@RequestParam String categoryId,
                                             @CurrentUser SimpleUser currentUser) {

    List<EnvSetting> envList = new ArrayList<>();
    List<QuerySetting> itemList = new ArrayList<>();
    file_define fileDefList = new file_define();
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
    if(fileDefList!=null)apiResult.add("filedefList", fileDefList);
    apiResult.add("hntList", HnTList);
    return Result.ok(apiResult);
  }

  @PostMapping("/api/setting/create")
  public ResponseEntity<ApiResult> CreateSetting(@RequestBody CreateAllSettingPayload payload,
                                             @CurrentUser SimpleUser currentUser) {
    List<CreateEnvPayload> envSettingList = payload.getEnv();
    List<CreateQueryPayload> querySettingList = payload.getItemGrp();
    CreateFileDefPayload fileDefSettingList = payload.getFileDef();
    List<CreateHnTPayload> HnTSettingList = payload.getHeadNtail();
    String configId = payload.getCg_id();

    List<CreateEnvCommand> envCommandList = new ArrayList<>();
    List<CreateQueryCommand> queryCommandList = new ArrayList<>();
    List<CreateHnTCommand> HnTCommandList = new ArrayList<>();

    ApiResult apiResult = ApiResult.blank();//Result

    if(fileDefSettingList!=null){
    fileDefSettingList.setCfgId(configId);
    CreateFileDefCommand fileDefCommandList = fileDefSettingList.toCommand(currentUser.getUserId());
    file_define fileDefList = fileDefService.create(fileDefCommandList);
    apiResult.add("fileDefList", fileDefList);
    }

    for(CreateEnvPayload envSetting : envSettingList){
      envSetting.setCfgId(configId);
      CreateEnvCommand envCommand = envSetting.toCommand(currentUser.getUserId());
      envCommandList.add(envCommand);
    }
    List<EnvSetting> envList = envService.create(envCommandList);
    apiResult.add("envList", envList);

    for(CreateQueryPayload querySetting : querySettingList){
      querySetting.setSettingId(configId);
      CreateQueryCommand queryCommand = querySetting.toCommand(currentUser.getUserId());
      queryCommandList.add(queryCommand);
    }
    List<QuerySetting> queryList = queryService.create(queryCommandList);
    apiResult.add("queryList", queryList);

    for(CreateHnTPayload HnTSetting : HnTSettingList){
      HnTSetting.setCfgId(configId);
      CreateHnTCommand HnTCommand = HnTSetting.toCommand(currentUser.getUserId());
      HnTCommandList.add(HnTCommand);
    }
    List<headntail> hntList = hntService.create(HnTCommandList);
    apiResult.add("hntList", hntList);

    return Result.ok(apiResult);
  }

  @PostMapping("/api/setting/update")
  public ResponseEntity<ApiResult> UpdateSetting(@RequestBody UpdateAllSettingPayload payload,
                                             @CurrentUser SimpleUser currentUser) {
      List<UpdateEnvPayload> envSettingList = payload.getEnv();
      List<UpdateQueryPayload> querySettingList = payload.getItemGrp();
      UpdateFileDefPayload fileDefSettingList = payload.getFileDef();
      List<UpdateHnTPayload> hntSettingList = payload.getHeadNtail();
      String configId = payload.getCg_id();

      List<UpdateEnvCommand> envCommandList = new ArrayList<>();
      List<UpdateQueryCommand> queryCommandList = new ArrayList<>();
      List<UpdateHnTCommand> hntCommandList = new ArrayList<>();

      fileDefSettingList.setCfgId(configId);
      UpdateFileDefCommand fileDefCommandList = fileDefSettingList.toCommand(currentUser.getUserId());
      if(fileDefSettingList!=null)fileDefService.update(fileDefCommandList);

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

      for(int i=0;i<hntSettingList.size();i++){
        hntSettingList.get(i).setCfgId(configId);
        UpdateHnTCommand hnTCommand = hntSettingList.get(i).toCommand(currentUser.getUserId());
        hntCommandList.add(hnTCommand);
      }
      if(hntSettingList.isEmpty()==false)hntService.update(hntCommandList);

      ApiResult apiResult = ApiResult.blank();
      return Result.ok(apiResult);
  }

  @PostMapping("/api/setting/filedefine/delete") //지금 필요없지만 후에 필요할수도
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

  //쿼리 결과 가져오기
  @PostMapping("/api/setting/sql")
  public ResponseEntity<ApiResult> selectSQL(@RequestBody SelectSQLPayload payload, @CurrentUser SimpleUser currentUser){
    ApiResult apiResult = ApiResult.blank();
    if(payload.getDatabase().equals("REPORT")){
      apiResult.add("returnList", sqlService.getReportQuery(payload));
    }else if(payload.getDatabase().equals("MES")){
      apiResult.add("returnList", sqlService.getMESQuery(payload));
    }else if(payload.getDatabase().equals("COMS")){
      apiResult.add("returnList", sqlService.getComsQuery(payload));
    }
      return Result.ok(apiResult);
  }

  @PostMapping("/api/setting/bigQuery")
  public ResponseEntity<ApiResult> saveMainQuery(@RequestBody CreateQueryPayload payload, @CurrentUser SimpleUser currentUser){
    CreateQueryCommand queryCommand = payload.toCommand(currentUser.getUserId());
    ApiResult apiResult = ApiResult.blank();
    apiResult.add("SQL", queryService.createMainQuery(queryCommand));
      return Result.ok(apiResult);
  }

  // 파일 본문 쿼리 가져오기
  @GetMapping("/api/setting/getMainQuery")
  public ResponseEntity<ApiResult> getMainQuery(@RequestParam String categoryId, @CurrentUser SimpleUser currentUser){
      ApiResult apiResult = ApiResult.blank();
      QuerySetting query = queryService.getMainQuery(categoryId);
      if(query==null)apiResult.add("MainQuery", "");
      else apiResult.add("MainQuery", query);

      return Result.ok(apiResult);
  }

  // 스케줄러 정지 | 실행 기능
  @GetMapping("/api/fileDefine/statusChange")
  public ResponseEntity<ApiResult> fileDefineStatusChange(@RequestParam String fileDefineId,@RequestParam String flag,
                                             @CurrentUser SimpleUser currentUser) {
    int result=-1;
    if(fileDefineId!=null){
      result = fileDefService.updateCronSendFlag(fileDefineId, flag);
    }
    ApiResult apiResult = ApiResult.blank();
    apiResult.add("statusChageResult",result);
    return Result.ok(apiResult);
  }
}
