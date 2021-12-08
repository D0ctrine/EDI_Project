package kr.co.nexmore.rfiddaemon.controller;


import io.swagger.annotations.*;
import kr.co.nexmore.rfiddaemon.reader.process.DispatchProcessBiz;
import kr.co.nexmore.rfiddaemon.service.DBManageService;
import kr.co.nexmore.rfiddaemon.service.ReaderControlService;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.RFIDTcpClient;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.manager.ClientManager;
import kr.co.nexmore.rfiddaemon.reader.util.CommonUtil;
import kr.co.nexmore.rfiddaemon.reader.util.TcpSetCommandUtil;
import kr.co.nexmore.rfiddaemon.vo.reader.ReaderVO;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.TagDataVO;
import kr.co.nexmore.rfiddaemon.vo.reader.udp.UdpResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.*;

import static kr.co.nexmore.rfiddaemon.common.CommonControlCode.TAG_EVENT_END;
import static kr.co.nexmore.rfiddaemon.common.RequestCommand.SYS_CONF_BUZZER_ONOFF;
import static kr.co.nexmore.rfiddaemon.common.RequestCommand.SYS_CONF_OPERATNG_MODE;
import static kr.co.nexmore.rfiddaemon.reader.tcp.client.manager.ClientManager.rfidClientsMapByIp;


@Slf4j
@RestController
@RequestMapping(value = "reader")
@Api(value = "reader", description = "리더기 설정 관리를 위한 Controller")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReaderController {

    private final ClientManager clientManager;
    private final ReaderControlService readerControlService;
    private final DBManageService dbManageService;

    public ReaderController(ClientManager clientManager, ReaderControlService readerControlService, DBManageService dbManageService) {
        this.clientManager = clientManager;
        this.readerControlService = readerControlService;
        this.dbManageService = dbManageService;
    }


    @RequestMapping(value = "/control/forceEndData", method = RequestMethod.POST, produces = {"application/json"})
    public ResponseEntity<Map<String, Object>> forceEndData(@RequestBody Map<String, Object> params) {
        log.info("[WEB REQUEST] FORCE END DATA - 강제 END EVENT 처리 :: START");
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

        String readerMac = params.get("readerMac").toString();
        int channel = Integer.parseInt(params.get("channel").toString());
        RFIDTcpClient client = null;

        log.info("[FORCE END DATA] PARAMETER VALIDATION CHECK  => readerMac: {}, channel: {}", readerMac, channel);

        boolean macValid = CommonUtil.validationMac(readerMac);

        if (!macValid) {
            isValid = false;
            message = "MAC ADDRESS가 올바르지 않습니다.";
            resStatus = HttpStatus.BAD_REQUEST;
        } else {
            if (channel < 0 || channel > 4) {
                isValid = false;
                message = "채널 값이 올바르지 않습니다.";
            } else {
                client = clientManager.getClient(readerMac);
                if (client == null || !client.isActived()) {
                    isValid = false;
                    message = "리더기 연결되지 않았습니다.";
                    resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
            }
        }

        if (isValid) {
            try {
                ReaderVO readerVO = client.getReaderVO();
                if ("DISPATCHING STATION".equals(readerVO.getProcessName().toUpperCase())) {
                    ((DispatchProcessBiz) (client.getMessageHandler().getProcess())).forceEndEvent(channel);
                    message = resStatus.getReasonPhrase();
                } else {
                    message = String.format("[%s] %s(%s) 리더기는 DISPATCHING STATION(TABLE) 이 아닙니다.", readerVO.getEquipmentName(), readerVO.getReaderName(), readerVO.getReaderIp());
                    log.error("{}", message);
                    resStatus = HttpStatus.BAD_REQUEST;
                }
            } catch (Exception e) {
                message = String.format("강제 END 처리 중 ERROR 가 발생했습니다. %s", e.getCause());
                log.error(message);
                log.error(e.toString(), e);
                resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        resMap.put("result", message);
        resMap.put("body", null);

        log.info("[WEB REQUEST] FORCE END DATA - 강제 END EVENT 처리 :: END");

        return new ResponseEntity<Map<String, Object>>(resMap, resStatus);
    }


    /**
     * 리더 등록
     *
     * @return
     */
    @ApiOperation(value = "새로운 리더기 등록", notes = "새로운 리더기 등록")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "readerVO", value = "readerVO", dataType = "ReaderVO", paramType = "body", required = true)
    })
    @RequestMapping(value = "/control/createReader", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map<String, Object>> createReader(@RequestBody ReaderVO readerVO) {
        log.info("[WEB REQUEST] CREATE READER - 리더 등록 :: START");
//        Map response = null;
        RFIDTcpClient client = null;
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

        String readerMac = null;
        String equipmentName = null;

        log.info("[CREATE READER] PARAMETER VALIDATION CHECK  => readerIp: {}, equipmentCode: {}, channelCnt: {}", readerVO.getReaderIp(), readerVO.getEquipmentCode(), readerVO.getChannelCnt());

        if (!(readerVO.getChannelCnt() == 2 || readerVO.getChannelCnt() == 4)) {
            isValid = false;
            message = "채널수가 올바르지 않습니다.(2 or 4)";
            resStatus = HttpStatus.BAD_REQUEST;
        } else if (readerVO.getEquipmentCode() != null && !"".equals(readerVO.getEquipmentCode())) {
            try {
                equipmentName = dbManageService.equipmentExistedCheck(readerVO.getEquipmentCode());
            } catch (Exception e) {
                log.error(e.toString(), e);
                isValid = false;
                resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
        if (equipmentName == null || "".equals(equipmentName)) {
            isValid = false;
            message = "설비코드가 올바르지 않습니다.";
            resStatus = HttpStatus.BAD_REQUEST;
        } else if (readerVO.getReaderIp() != null) {
            boolean ipValid = CommonUtil.validationIp(readerVO.getReaderIp());
            if (!ipValid) {
                isValid = false;
                message = "IP형식이 올바르지 않습니다.";
                resStatus = HttpStatus.BAD_REQUEST;
            } else {
                try {
                    UdpResponseVO searchResponse = readerControlService.searchReader(readerVO.getReaderIp());
                    if (searchResponse == null) {
                        isValid = false;
                        message = String.format("[IP: %s]에 해당하는 리더기가 존재하지 않습니다.", readerVO.getReaderIp());
                        resStatus = HttpStatus.BAD_REQUEST;
                    } else {
                        log.debug("{}", searchResponse.toString());
                        readerMac = searchResponse.getReaderMac();
                    }
                } catch (InterruptedException e) {
                    log.error(e.toString(), e);
                    isValid = false;
                    resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
            }
        }
        if (isValid) {
            try {
                String readerName = dbManageService.getEquipmentName(readerVO.getEquipmentCode());
                if (readerName == null || "".equals(readerName)) {
                    readerName = String.format("%s-%dC-01", equipmentName, readerVO.getChannelCnt());
                } else {
                    int numbering = Integer.parseInt(readerName.substring(readerName.length() - 2)) + 1;
                    readerName = String.format("%s%02d", readerName.substring(0, readerName.length() - 2), numbering);
                }

                readerVO.setCreateReaderVO(readerMac, readerName, readerVO.getEquipmentCode(), equipmentName, readerVO.getReaderIp(),
                        CommonUtil.setGateway(readerVO.getReaderIp()), readerVO.getChannelCnt());
                log.debug(readerVO.toString());
                int result = dbManageService.createReader(readerVO);
                if (result > 0) {
                    readerVO = dbManageService.getReaderInfo(readerMac);
                    client = clientManager.addClient(readerVO);
                    message = resStatus.getReasonPhrase();
                }
            } catch (Exception e) {
                log.error(e.toString(), e);
                resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        resMap.put("result", message);
        if (client != null) {
            resMap.put("body", client.getReaderVO());
        } else {
            resMap.put("body", client);
        }

        log.info("result: {}", message);
        log.info("[WEB REQUEST] CREATE READER - 리더 등록 :: END");

        return new ResponseEntity<Map<String, Object>>(resMap, resStatus);
    }

    @ApiOperation(value = "리더 연결 및 정보 삭제", notes = "리더 연결 및 정보 삭제", produces = "application/json", consumes = "application/json")
    @RequestMapping(value = "/control/deleteReader", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<Map<String, Object>> deleteReader(@RequestBody Map<String, String> param) {
        log.info("[WEB REQUEST] DELETE READER - 리더 삭제 :: START");
        ReaderVO readerVO = null;
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

        String readerMac = param.get("readerMac");

        log.info("[DELETE READER] PARAMETER VALIDATION CHECK  => readerMac: {}", readerMac);
        boolean macValid = CommonUtil.validationMac(readerMac);

        if (!macValid) {
            isValid = false;
            message = "MAC ADDRESS가 올바르지 않습니다.";
            resStatus = HttpStatus.BAD_REQUEST;
        }

        if (isValid) {
            try {
                int result = dbManageService.deleteReader(readerMac);

                if (result > 0) {
                    message = resStatus.getReasonPhrase();
                    log.debug("데이터 삭제 성공.");

                    LinkedHashMap<String, RFIDTcpClient> rfidClientsMap = clientManager.getClientsMap();
                    if (rfidClientsMap != null && !rfidClientsMap.isEmpty() && rfidClientsMap.get(readerMac) != null) {
                        RFIDTcpClient client = rfidClientsMap.get(readerMac);
                        if (client != null) {
                            readerVO = client.getReaderVO();
                            client.shutdown();
                            rfidClientsMap.remove(readerMac);
                            rfidClientsMapByIp.remove(readerVO.getReaderIp());
                            if ("PLASMA".equals(readerVO.getProcessName().toUpperCase())) {
                                boolean flag = client.getMessageHandler().plasmaPaired();
                                if (flag) {
                                    client.getMessageHandler().pairRelease(readerMac);
                                } else {
                                    client.getMessageHandler().changePollingThread(false);
                                }
                            } else {
                                client.getMessageHandler().changePollingThread(false);
                            }
                            client = null;
                        }
                    }
                } else {
                    message = "일치하는 리더 정보가 없습니다.";
                }
            } catch (Exception e) {
                log.error(e.toString(), e);
                resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        resMap.put("result", message);
        resMap.put("body", readerVO);

        log.info("result: {}", message);
        log.info("[WEB REQUEST] DELETE READER - 리더 삭제 :: END");

        return new ResponseEntity<Map<String, Object>>(resMap, resStatus);
    }

    @ApiOperation(value = "리더 옵션 변경", notes = "Buzzer, Interval")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "readerVO", value = "readerVO", dataType = "ReaderVO", paramType = "body", required = true)
    })
    @RequestMapping(value = "/sysConfig/updateReader", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map<String, Object>> updateReader(@RequestBody ReaderVO readerVO) {
        log.info("[WEB REQUEST] UPDATE READER SETTING - 리더 설정 변경 :: START");
        LinkedHashMap setModeResponse;
        LinkedHashMap setBuzzerResponse;
        List<LinkedHashMap> resultList = new ArrayList<>();
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

        log.info("[UPDATE READER SETTING] PARAMETER VALIDATION CHECK  => readerMac: {}, buzzerYn: {}, readerInterval: {}", readerVO.getReaderMac(), readerVO.getBuzzerYn(), readerVO.getReaderInterval());

        boolean macValid = CommonUtil.validationMac(readerVO.getReaderMac());
        if (!macValid) {
            message = "MAC ADDRESS가 올바르지 않습니다.";
            isValid = false;
            resStatus = HttpStatus.BAD_REQUEST;
        } else {
            if (!"BROADCAST".equals(readerVO.getReaderMac())) {

                if (clientManager.getClient(readerVO.getReaderMac()) == null) {
                    message = "리더와 연결되어 있지 않습니다.";
                    isValid = false;
                    resStatus = HttpStatus.BAD_REQUEST;
                }
                if (readerVO.getEquipmentCode() == null || "".equals(readerVO.getEquipmentCode())) {
                    message = "설비코드가 올바르지 않습니다.";
                    isValid = false;
                    resStatus = HttpStatus.BAD_REQUEST;
                }
                if (readerVO.getEquipmentName() == null || "".equals(readerVO.getEquipmentName())) {
                    message = "설비명이 올바르지 않습니다.";
                    isValid = false;
                    resStatus = HttpStatus.BAD_REQUEST;
                }
            }
        }

        if (readerVO.getBuzzerYn() == null || "".equals(readerVO.getBuzzerYn())) {
            message = "BUZZER 설정 값이 올바르지 않습니다.(Y/N)";
            isValid = false;
            resStatus = HttpStatus.BAD_REQUEST;
        } else if (readerVO.getReaderInterval() < 0 || readerVO.getReaderInterval() > 25.5) {
            message = "리더 INTERVAL 값이 올바르지 않습니다.(1 ~ 25.5)";
            isValid = false;
            resStatus = HttpStatus.BAD_REQUEST;
        }

        if (isValid) {
            int flag = "Y".equals(readerVO.getBuzzerYn()) ? 1 : 0;

            if (!"BROADCAST".equals(readerVO.getReaderMac())) {
                try {
                    LinkedHashMap readerResponse = new LinkedHashMap();
                    readerResponse.put("readerMac", readerVO.getReaderMac());

                    RFIDTcpClient client = clientManager.getClient(readerVO.getReaderMac());
                    client.getMessageHandler().detachScheduleDestroy();

                    setBuzzerResponse = readerControlService.sendTcpCommand(TcpSetCommandUtil.setBuzzer(flag, readerVO.getReaderMac()), client, false);
                    setModeResponse = readerControlService.sendTcpCommand(TcpSetCommandUtil.setMode(readerVO.getReaderMode(), readerVO.getReaderInterval(), readerVO.getReaderMac()), client);

                    if (setBuzzerResponse != null && !setBuzzerResponse.isEmpty() && setBuzzerResponse.get("success") != null) {
                        readerResponse.put("buzzer", setBuzzerResponse.get("success"));
                    }
                    if (setModeResponse != null && !setModeResponse.isEmpty() && setModeResponse.get("success") != null) {
                        readerResponse.put("interval", setModeResponse.get("success"));
                        if (setModeResponse.get("isSave") != null) {
                            readerResponse.put("isSave", setModeResponse.get("isSave"));
                        }
                    }
                    if ((boolean) readerResponse.get("isSave")) {
                        try {

                            ReaderVO readerInfo = dbManageService.getReaderInfo(readerVO.getReaderMac());   // 현재 리더기 DB data(설정값)
                            
                            if (!readerInfo.getEquipmentCode().equals(readerVO.getEquipmentCode())) {

                                String equipmentName = dbManageService.equipmentExistedCheck(readerVO.getEquipmentCode());
                                String readerName = dbManageService.getEquipmentName(readerVO.getEquipmentCode());

                                if (readerName == null || "".equals(readerName)) {
                                    readerName = String.format("%s-%dC-01", equipmentName, readerInfo.getChannelCnt());
                                } else {
                                    int numbering = Integer.parseInt(readerName.substring(readerName.length() - 2)) + 1;
                                    readerName = String.format("%s%02d", readerName.substring(0, readerName.length() - 2), numbering);
                                }
                                readerVO.setReaderName(readerName);
                            }

                            int result = dbManageService.updateReader(readerVO);
                            if (result > 0) {
                                readerResponse.put("dbSave", true);

                                message = resStatus.getReasonPhrase();

                                ReaderVO updateReader = dbManageService.getReaderInfo(readerVO.getReaderMac());

                                readerControlService.sendTcpCommand(TcpSetCommandUtil.getRegister(SYS_CONF_BUZZER_ONOFF, updateReader.getReaderMac()), client, false);
                                readerControlService.sendTcpCommand(TcpSetCommandUtil.getRegister(SYS_CONF_OPERATNG_MODE, updateReader.getReaderMac()), client, false);
                                client.setReaderVO(updateReader);

                            } else {
                                readerResponse.put("dbSave", false);
                                message = "DB 저장 실패";
                            }
                        } catch (Exception e) {
                            log.error(e.toString(), e);
                            message = "DB 저장 실패";
                            resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                        }
                    }

                    resultList.add(readerResponse);

                } catch (InterruptedException e) {
                    log.error(e.toString(), e);
                    resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "설정 실패";
                }
            } else {
                try {
                    for (String readerMac : clientManager.getClientsMap().keySet()) {
                        LinkedHashMap readerResponse = new LinkedHashMap();
                        readerResponse.put("readerMac", readerMac);

                        RFIDTcpClient client = clientManager.getClient(readerMac);
                        client.getMessageHandler().detachScheduleDestroy();

                        setBuzzerResponse = readerControlService.sendTcpCommand(TcpSetCommandUtil.setBuzzer(flag, readerMac), client, false);
                        setModeResponse = readerControlService.sendTcpCommand(TcpSetCommandUtil.setMode(readerVO.getReaderMode(), readerVO.getReaderInterval(), readerMac), client);

                        if (setBuzzerResponse != null && !setBuzzerResponse.isEmpty() && setBuzzerResponse.get("success") != null) {
                            readerResponse.put("buzzer", setBuzzerResponse.get("success"));
                        }
                        if (setModeResponse != null && !setModeResponse.isEmpty() && setModeResponse.get("success") != null) {
                            readerResponse.put("interval", setModeResponse.get("success"));
                            if (setModeResponse.get("isSave") != null) {
                                readerResponse.put("isSave", setModeResponse.get("isSave"));
                            }
                        }

                        if ((boolean) readerResponse.get("isSave")) {
                            try {
                                readerVO.setEquipmentCode(client.getReaderVO().getEquipmentCode());
                                readerVO.setReaderMac(readerMac);
                                int result = dbManageService.updateReader(readerVO);
                                if (result > 0) {
                                    readerResponse.put("dbSave", true);
                                    message = resStatus.getReasonPhrase();

                                    ReaderVO updateReader = dbManageService.getReaderInfo(readerVO.getReaderMac());

                                    readerControlService.sendTcpCommand(TcpSetCommandUtil.getRegister(SYS_CONF_BUZZER_ONOFF, readerVO.getReaderMac()), client, false);
                                    readerControlService.sendTcpCommand(TcpSetCommandUtil.getRegister(SYS_CONF_OPERATNG_MODE, readerVO.getReaderMac()), client, false);
                                    client.setReaderVO(updateReader);

                                } else {
                                    readerResponse.put("dbSave", false);
                                    message = "DB 저장 실패";
                                }
                            } catch (Exception e) {
                                log.error(e.toString(), e);
                                message = "DB 저장 실패";
                                resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                            }
                        }
                        resultList.add(readerResponse);
                    }
                } catch (Exception e) {
                    log.error(e.toString(), e);
                    resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "설정 실패";
                }
            }
        }

        resMap.put("result", message);
        resMap.put("body", resultList);
        log.info("result: {}", message);
        log.info("[WEB REQUEST] UPDATE READER SETTING - 리더 설정 변경 :: END");

        return new ResponseEntity<Map<String, Object>>(resMap, resStatus);
    }


    /**
     * 리더 Network 정보 변경 IP/PORT
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "리더기의 IP 변경", notes = "리더기의 IP를 변경")
    @RequestMapping(value = "/sysConfig/changeIp", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map<String, Object>> changeIp(@RequestBody Map<String, String> params) {
        log.info("[WEB REQUEST] CHANGE IP - 리더 IP 변경 :: START");
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

        String readerMac = params.get("readerMac");
        String readerIp = params.get("currentIp");
        String changeIp = params.get("changeIp");

        log.info("[CHANGE IP] PARAMETER VALIDATION CHECK  => readerMac{}, readerIp: {}, filePath: {}", readerMac, readerIp, changeIp);

        boolean readerMacValid = CommonUtil.validationMac(readerMac);
        boolean readerIpValid = CommonUtil.validationIp(readerIp);
        boolean changeIpValid = CommonUtil.validationIp(changeIp);

        if (!readerMacValid) {
            message = "MAC 주소가 올바르지 않습니다.";
            resStatus = HttpStatus.BAD_REQUEST;
            isValid = false;
        } else if (!(readerIpValid && changeIpValid)) {
            message = "IP 형식이 올바르지 않습니다.";
            resStatus = HttpStatus.BAD_REQUEST;
            isValid = false;
        } else {
            try {
                int result = dbManageService.ipExistedCheck(changeIp);
                if (result > 0) {
                    message = "중복된 IP 입니다.";
                    resStatus = HttpStatus.BAD_REQUEST;
                    isValid = false;
                }
            } catch (Exception e) {
                log.error(e.toString(), e);
                message = "IP 중복 체크 실패";
                resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                isValid = false;
            }
        }

        if (isValid) {
            try {
                UdpResponseVO searchResponse = readerControlService.searchReader(readerIp);
                if (searchResponse == null) {
                    message = String.format("[readerIp: %s] SEARCH COMMAND 응답이 없습니다. 리더기 IP를 확인해주세요.", readerIp);
                } else {
                    String gateway = CommonUtil.setGateway(changeIp);

                    UdpResponseVO setNetworkResponse = readerControlService.setReaderIp(readerMac, readerIp, changeIp, gateway, searchResponse);
                    if (setNetworkResponse == null) {
                        message = String.format("[readerIp: %s -> %s] IP 변경 실패. 리더기로부터 응답을 받지 못했습니다.", readerIp, changeIp);
                    } else {
                        if (setNetworkResponse.getReaderMac() != null) {
                            String macAddr = setNetworkResponse.getReaderMac();
                            message = resStatus.getReasonPhrase();
                            response = new HashMap();
                            response.put("readerMac", macAddr);
                            RFIDTcpClient client = clientManager.getClient(macAddr);
                            if (client != null) {
                                client.shutdown();  // 리더 연결 종료. disconnect
                            }

                            int result = dbManageService.setReaderNetwork(changeIp, gateway, macAddr);
                            if (result > 0) {
                                ReaderVO readerVO = dbManageService.getReaderInfo(macAddr);
                                if (client != null) {
                                    client.setReaderVO(readerVO);
                                } else {
                                    client = clientManager.addClient(readerVO);
                                }
                                client.start();
//                                client.connect(readerVO);
                            } else {
                                message = "DB 저장 실패";
                                log.error("리더기 IP 변경 ERROR :: DB 저장 실패");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                log.error(e.toString(), e);
            }
        }

        resMap.put("result", message);
        resMap.put("body", response);

        log.info("result: {}", message);
        log.info("[WEB REQUEST] CHANGE IP - 리더 IP 변경 :: END");
        return new ResponseEntity<Map<String, Object>>(resMap, resStatus);
    }


    @ApiOperation(value = "리더기 펌웨어 업데이트", notes = "리더기 펌웨어 업데이트")
    @RequestMapping(value = "/sysConfig/firmwareUpdate", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map<String, Object>> firmwareUpdate(@RequestBody Map<String, Object> params) {
        log.info("[WEB REQUEST] FIRMWARE UPDATE - 펌웨어 업데이트 :: START");
        LinkedHashMap<String, String> response = null;
        File file = null;
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

        String readerMac = params.get("readerMac").toString();
        String readerIp = params.get("readerIp").toString();
        String filePath = params.get("filePath").toString();

        log.info("[FIRMWARE UPDATE] PARAMETER VALIDATION CHECK  => readerMac{}, readerIp: {}, filePath: {}", readerMac, readerIp, filePath);

        boolean readerMacValid = CommonUtil.validationMac(readerMac);
        boolean readerIpValid = CommonUtil.validationIp(readerIp);

        if (!readerMacValid) {
            message = "MAC 주소가 올바르지 않습니다.";
            resStatus = HttpStatus.BAD_REQUEST;
            isValid = false;
        } else if (!readerIpValid) {
            message = "IP 형식이 올바르지 않습니다.";
            resStatus = HttpStatus.BAD_REQUEST;
            isValid = false;
        } else {
            try {
                int result = dbManageService.ipExistedCheck(readerIp);
                if (result <= 0) {
                    message = "존재하지 않는 IP 입니다.";
                    resStatus = HttpStatus.BAD_REQUEST;
                    isValid = false;
                } else {
                    UdpResponseVO searchResponse = readerControlService.searchReader(readerIp);
                    if (searchResponse == null || searchResponse.getReaderMac() == null) {
                        isValid = false;
                        message = String.format("[IP: %s] 리더기 SEARCH 실패하였습니다.");
                        resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    }
                }
            } catch (Exception e) {
                log.error(e.toString(), e);
                message = "IP 중복 체크 실패";
                resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                isValid = false;
            }
        }

        if (filePath == null || "".equals(filePath)) {
            message = "올바르지 않은 요청입니다.(filePath is null)";
            isValid = false;
            resStatus = HttpStatus.BAD_REQUEST;
        } else {
            file = new File(filePath);
            if (!file.exists()) {
                message = "파일이 존재하지 않습니다.";
                isValid = false;
                resStatus = HttpStatus.BAD_REQUEST;
            }
        }

        if (isValid) {
            try {
                response = readerControlService.firmwareUpdate(file, readerMac, readerIp);
                if (response == null) {
                    message = "DOWNLOAD FAIL";
                } else if (response != null && !response.isEmpty() && response.get("ready") != null) {
                    if (response.get("ready").equals("FAIL")) {
                        message = "FIRMWARE DOWNLOAD READY FAIL";
                    } else {
                        message = resStatus.getReasonPhrase();
                    }
                }
            } catch (Exception e) {
                log.error(e.toString(), e);
                resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        resMap.put("result", message);
        resMap.put("body", response);

        log.info("result: {}", message);
        log.info("[WEB REQUEST] FIRMWARE UPDATE - 펌웨어 업데이트 :: END");
        return new ResponseEntity<Map<String, Object>>(resMap, resStatus);
    }
}

