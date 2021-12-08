package kr.co.nexmore.rfiddaemon.controller;

import io.swagger.annotations.*;
import kr.co.nexmore.rfiddaemon.mes.Oneoone;
import kr.co.nexmore.rfiddaemon.mes.OneooneManager;
import kr.co.nexmore.rfiddaemon.service.DBManageService;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.RFIDTcpClient;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.manager.ClientManager;
import kr.co.nexmore.rfiddaemon.reader.util.CommonUtil;
import kr.co.nexmore.rfiddaemon.vo.reader.ReaderVO;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.RFID_EIS_Reply_Out_Tag;
import kr.co.nexmore.rfiddaemon.vo.mes.EISType.RFID_EIS_Change_Resource_Info_In;
import kr.co.nexmore.rfiddaemon.vo.mes.OneooneVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static kr.co.nexmore.rfiddaemon.common.CommonControlCode.MES_EVENT_SUCCESS;
import static kr.co.nexmore.rfiddaemon.common.CommonControlCode.OPCODE_EISChangeResourceInfoRequest;

@Api(value = "mes", description = "MES(Oneoone) 연결 및 관리 Controller")
@Slf4j
@RestController
@RequestMapping(value = "mes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MesController {

    private final OneooneManager oneooneManager;
    private final ClientManager clientManager;
    private final DBManageService dbManageService;

    public MesController(OneooneManager oneooneManager, ClientManager clientManager, DBManageService dbManageService) {
        this.oneooneManager = oneooneManager;
        this.clientManager = clientManager;
        this.dbManageService = dbManageService;
    }
    

    @RequestMapping(value = "/sendReaderStatus", method = RequestMethod.POST, produces = {"application/json"})
    public ResponseEntity<Map<String, Object>> sendReaderStatus(@RequestBody Map<String, Object> params) {
        log.info("[WEB REQUEST] SEND READER STATUS TO MES SERVER - 리더 상태 전송 :: START");
        Map response = null;
        HttpStatus resStatus = HttpStatus.OK;
        Map<String, Object> resMap = new LinkedHashMap<String, Object>() {
            {
                put("result", null);
            }

            {
                put("body", null);
            }
        };

        String message = null;
        boolean checked = false;
        boolean isValid = true;

        String readerMac = params.get("readerMac").toString();

        log.info("[SEND READER STATUS TO MES SERVER] PARAMETER VALIDATION CHECK  => readerMac: {}", readerMac);

        boolean readerMacValid = CommonUtil.validationMac(readerMac);

        if (!readerMacValid) {
            message = "MAC 주소가 올바르지 않습니다.";
            resStatus = HttpStatus.BAD_REQUEST;
            isValid = false;
        }

        if (isValid) {
            try {
                ReaderVO readerVO = dbManageService.getReaderInfo(readerMac);
                RFID_EIS_Change_Resource_Info_In requestInReader = null;
                if (readerVO == null) {
                    message = "해당 MAC 주소의 리더는 존재하지 않습니다.";
                } else {
                    requestInReader = new RFID_EIS_Change_Resource_Info_In(readerVO);
                    RFIDTcpClient client = clientManager.getClient(readerMac);
                    if (client != null && client.isActived()) {
                        requestInReader.reader_status = 'Y';
                    }
                    log.debug("RequestInReader: {}", requestInReader.toString());
                    RFID_EIS_Reply_Out_Tag replyOutTag = oneooneManager.sendToEIS(readerVO.getStationXId(), requestInReader, OPCODE_EISChangeResourceInfoRequest);

                    if (replyOutTag != null) {
                        if (replyOutTag.h_status_value == MES_EVENT_SUCCESS) {
                            message = String.format("MES 연동 완료(%s)", readerVO.getEquipmentName());
                            int result = dbManageService.setMesUseYn(readerMac);
                            if( result > 0) {
                                checked = true;
                            } else {
                                message = String.format("MES_USE_FLAG DB UPDATE FAILURE");
                            }
                        } else {
                            message = String.format("MES 연동 실패(%s) : %s", readerVO.getEquipmentName(), replyOutTag.h_msg);
//                            message = String.format("MES 연동 실패(%s) : %s", readerVO.getEquipmentName(), replyOutTag.h_msg_code);
                        }
                        response = new LinkedHashMap();
                        response.put("checked", checked);
                        response.put("request", requestInReader);
                        response.put("reply", replyOutTag);
                    }
                }
            } catch (Exception e) {
                log.error(e.toString(), e);
                message = String.format("Middleware System Error : %s", e.getCause());
            }
        }

        resMap.put("result", message);
        resMap.put("body", response);

        log.debug("[SEND READER STATUS TO MES SERVER] Result Message: {}", message);
        log.debug("[SEND READER STATUS TO MES SERVER] Result body: {}", response);

        log.info("[WEB REQUEST] SEND READER STATUS TO MES SERVER - 리더 상태 전송 :: END");
        return new ResponseEntity<Map<String, Object>>(resMap, resStatus);
    }


    @ApiOperation(value = "MES(Oneoone) 연결 정보 생성")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oneooneVO", value = "oneooneVO", dataType = "OneooneVO", paramType = "body", required = true)
    })
    @RequestMapping(value = "/createOneoone", method = RequestMethod.POST, produces = {"application/json"})
    public ResponseEntity<Map<String, Object>> createOneoone(@RequestBody OneooneVO oneooneVO) {
        log.info("[WEB REQUEST] CREATE ONEOONE MES SERVER CONNECTION - MES 연결 정보 생성 :: START");
        log.debug(oneooneVO.toString());
        Map response = null;
        HttpStatus resStatus = HttpStatus.OK;
        Map<String, Object> resMap = new LinkedHashMap<String, Object>() {
            {
                put("result", null);
            }

            {
                put("body", null);
            }
        };
        String message = "fail";
        boolean isValid = true;

        log.info("[CREATE ONEOONE MES SERVER CONNECTION] PARAMETER VALIDATION CHECK  => stationXIp: {}  stationXPort: {}  stationXChannel: {}  rfidTuneChannel: {}  autoConnect: {}  mesTimeOut: {}",
                oneooneVO.getStationXIp(), oneooneVO.getStationXPort(), oneooneVO.getStationXChannel(), oneooneVO.getRfidTuneChannel(), oneooneVO.getAutoConnect(), oneooneVO.getMesTimeout());

        boolean ipValid = CommonUtil.validationIp(oneooneVO.getStationXIp());

        if (!ipValid) {
            message = "IP형식이 올바르지 않습니다.";
            isValid = false;
            resStatus = HttpStatus.BAD_REQUEST;
        }

        if (isValid) {
            try {
                response = oneooneManager.createOneoone(oneooneVO);
                if (response != null && !response.isEmpty() && response.get("success") != null) {
                    if ((boolean) response.get("success")) {
                        message = resStatus.getReasonPhrase();
                    } else {
                        message = "DB 저장 실패";
                    }
                }
            } catch (Exception e) {
                log.error(e.toString(), e);
                resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        resMap.put("result", message);
        resMap.put("body", response);

        log.info("[WEB REQUEST] CREATE ONEOONE MES SERVER CONNECTION - MES 연결 정보 생성 :: END");
        return new ResponseEntity<Map<String, Object>>(resMap, resStatus);
    }


    @ApiOperation(value = "MES(Oneoone) 연결 정보 변경", notes = "MES(Oneoone) connection 정보, 옵션(autoConnect) 변경")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oneooneVO", value = "oneooneVO", dataType = "OneooneVO", paramType = "body", required = true)
    })
    @RequestMapping(value = "/updateOneoone", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<Map<String, Object>> updateOneoone(@RequestBody OneooneVO oneooneVO) {
        log.debug("updateOneoone start");
        log.info("[WEB REQUEST] UPDATE ONEOONE MES SERVER CONNECTION - MES 연결 정보 수정 :: START");
        Map response = null;
        HttpStatus resStatus = HttpStatus.OK;
        Map<String, Object> resMap = new LinkedHashMap<String, Object>() {
            {
                put("result", null);
            }

            {
                put("body", null);
            }
        };
        String message = "fail";
        boolean isValid = true;

        log.info("[UPDATE ONEOONE MES SERVER CONNECTION] PARAMETER VALIDATION CHECK => stationXId: {}  stationXIp: {}  stationXPort: {}  stationXChannel: {}  rfidTuneChannel: {}  autoConnect: {}  mesTimeOut: {}",
                oneooneVO.getStationXId(), oneooneVO.getStationXIp(), oneooneVO.getStationXPort(), oneooneVO.getStationXChannel(), oneooneVO.getRfidTuneChannel(), oneooneVO.getAutoConnect(), oneooneVO.getMesTimeout());


        if (oneooneVO.getStationXId() <= 0) {
            message = "STATION_X_ID 가 올바르지 않습니다.";
            isValid = false;
            resStatus = HttpStatus.BAD_REQUEST;
        } else {
            boolean ipValid = CommonUtil.validationIp(oneooneVO.getStationXIp());
            if (!ipValid) {
                message = "IP형식이 올바르지 않습니다.";
                isValid = false;
                resStatus = HttpStatus.BAD_REQUEST;
            }
        }

        if (isValid) {
            try {
                response = oneooneManager.updateOneoone(oneooneVO);
                if (response != null && !response.isEmpty() && response.get("success") != null) {
                    if ((boolean) response.get("success")) {
                        message = resStatus.getReasonPhrase();
                    } else {
                        message = response.get("message").toString();
                    }
                }
            } catch (Exception e) {
                log.error(e.toString(), e);
                resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        resMap.put("result", message);
        resMap.put("body", response);

        log.info("[WEB REQUEST] UPDATE ONEOONE MES SERVER CONNECTION - MES 연결 정보 수정 :: END");
        return new ResponseEntity<Map<String, Object>>(resMap, resStatus);
    }


    @ApiOperation(value = "MES(Oneoone) Timeout 변경", notes = "MES(Oneoone) Timeout 변경")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oneooneVO", value = "oneooneVO", dataType = "OneooneVO", paramType = "body", required = true)
    })
    @RequestMapping(value = "/updateTimeout", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<Map<String, Object>> updateTimeout(@RequestBody OneooneVO oneooneVO) {
        log.info("[WEB REQUEST] UPDATE ONEOONE MES SERVER TIMEOUT - MES TIMEOUT 변경 :: START");

        Map response = null;
        HttpStatus resStatus = HttpStatus.OK;
        Map<String, Object> resMap = new LinkedHashMap<String, Object>() {
            {
                put("result", null);
            }

            {
                put("body", null);
            }
        };
        String message = "fail";
        boolean isValid = true;

        log.info("[UPDATE ONEOONE MES TIMEOUT] PARAMETER VALIDATION CHECK => stationXId: {}  mesTimeout: {}", oneooneVO.getStationXId(), oneooneVO.getMesTimeout());

        if (oneooneVO.getStationXId() <= 0) {
            message = "STATION_X_ID 가 올바르지 않습니다.";
            isValid = false;
            resStatus = HttpStatus.BAD_REQUEST;
        } else {
            Oneoone oneoone = oneooneManager.getOneooneMap().get(oneooneVO.getStationXId());
            if(oneoone == null) {
                message = "존재하지 않는 MES(101) 연결 정보 입니다.";
                isValid = false;
                resStatus = HttpStatus.BAD_REQUEST;
            }
        }

        if(isValid) {
            try {
                response = oneooneManager.updateTimeOut(oneooneVO);
                if((boolean) response.get("success")){
                    message = resStatus.getReasonPhrase();
                } else {
                    message = response.get("message").toString();
                }
            } catch (Exception e) {
                log.error(e.toString(), e);
                resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        resMap.put("result", message);
        resMap.put("body", response);

        log.info("[WEB REQUEST] UPDATE ONEOONE MES TIMEOUT - MES TIMEOUT 변경 :: END");
        return new ResponseEntity<Map<String, Object>>(resMap, resStatus);
    }


    @ApiOperation(value = "MES(Oneoone) 연결 삭제", notes = "MES(Oneoone) 연결 정보 삭제", produces = "application/json", consumes = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stationXId", value = "stationXId", dataType = "int", paramType = "path", required = true)
    })
    @RequestMapping(value = "/deleteOneoone/{stationXId}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<Map<String, Object>> deleteOneoone(@PathVariable(value = "stationXId") int stationXId) {
        log.info("[WEB REQUEST] DELETE ONEOONE MES SERVER CONNECTION - MES 연결 정보 삭제 :: START");
        Map response = null;
        HttpStatus resStatus = HttpStatus.OK;
        Map<String, Object> resMap = new LinkedHashMap<String, Object>() {
            {
                put("result", null);
            }

            {
                put("body", null);
            }
        };
        String message = "fail";
        boolean isValid = true;

        log.info("[DELETE ONEOONE MES SERVER CONNECTION] PARAMETER VALIDATION CHECK  => stationXId: {}", stationXId);

        if (stationXId <= 0) {
            isValid = false;
            message = "STATION_X_ID가 올바르지 않습니다.";
            resStatus = HttpStatus.BAD_REQUEST;
        }

        if (isValid) {
            try {
                response = oneooneManager.deleteOneoone(stationXId);
                if (response != null && !response.isEmpty() && response.get("success") != null) {
                    if ((boolean) response.get("success")) {
                        message = resStatus.getReasonPhrase();
                    } else {
                        message = response.get("message").toString();
                    }
                }
            } catch (Exception e) {
                log.error(e.toString(), e);
                resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        resMap.put("result", message);
        resMap.put("body", response);

        log.info("[WEB REQUEST] DELETE ONEOONE MES SERVER CONNECTION - MES 연결 정보 삭제 :: END");
        return new ResponseEntity<Map<String, Object>>(resMap, resStatus);
    }
}
