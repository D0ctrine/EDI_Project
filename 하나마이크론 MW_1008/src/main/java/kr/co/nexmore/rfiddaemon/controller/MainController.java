package kr.co.nexmore.rfiddaemon.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/")
@Api(description = "System Control")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MainController {

    private final ApplicationContext context;
    MainController(ApplicationContext context) {
        this.context = context;
    }

    @ApiOperation(value = "SYSTEM ALIVE CHECK", notes = "SYSTEM ALIVE CHECK")
    @RequestMapping(value = "/system/aliveCheck", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map<String, Object>> aliveCheck() {

        log.info("SYSTEM ALIVE CHECK :: START");

        HttpStatus resStatus = HttpStatus.OK;
        Map<String, Object> resMap = new LinkedHashMap<String, Object>() {
            {
                put("result", resStatus.getReasonPhrase());
            }
            {
                put("body", null);
            }
        };

        log.info("SYSTEM OPERATION STATUS: {}", resMap.get("result"));
        log.info("SYSTEM ALIVE CHECK :: END");

        return new ResponseEntity<Map<String, Object>>(resMap, resStatus);
    }


    @ApiOperation("API 시스템 종료")
    @RequestMapping(value = "/system/shutdown", method = RequestMethod.GET)
    public ResponseEntity<Void> shutdownContext() {
        log.info("SYSTEM SHUTDOWN :: START");
        HttpStatus resStatus = HttpStatus.OK;
        try {
            ((ConfigurableApplicationContext) context).close();
        } catch (Exception e) {
            log.error(e.toString(), e);
            resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } finally {
            log.info("SYSTEM SHUTDOWN :: END");
        }
        return new ResponseEntity<Void>(resStatus);
    }
}
