package kr.co.nexmore.rfiddaemon.reader.tcp.client.manager;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.Writer;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Component("clientLogManager")
public class ClientLogManager {

    /** log file writer */
    private Writer fw;

    /** 월의 마지막 날 */
    private int DayOfMonth;

    /** 로그 파일 위치 */
    @Value("${equipment.logPath:./logs}")
    private String defaultLogPath;

    private String equipmentLogPath = "equipment";

    /** logger */
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @PostConstruct
    public void createDir() {
        String equipmentPath = String.format("%s/%s", defaultLogPath, equipmentLogPath);
        File dir = new File(equipmentPath);
        if(!dir.exists()) {
            try{
                dir.mkdir(); //폴더 생성합니다.
            }
            catch(Exception e){
                e.getStackTrace();
            }
        }
    }

    /**
     * 해당일자 파일 오픈 Trace
     * @param message    메시지
     * @param logFileName   로그 파일명
     */
    public synchronized void trace(String logHeaderString, String message, String logFileName) {

        String loggingPath = String.format("%s/%s/%s", this.defaultLogPath, this.equipmentLogPath, logFileName);
        String loggingHeader = logHeaderString;

        String format = "HH:mm:ss.SSS";
        long now = System.currentTimeMillis();
        String currentTimeMillis = DateFormatUtils.format(now, format);

        openLog(loggingPath, logFileName);

        try {
            String logMessage = String.format("%s %s %s\n",currentTimeMillis, loggingHeader, message);
            fw.write(logMessage);
            fw.flush();

            if(logger.isDebugEnabled()){
//                logger.debug(logMessage); // for debug
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }finally {
            if (fw != null)
                closeLog();
        }
    }

    /**
     * Log File Open Write OpenLog
     *
     * @return int
     */
    protected int openLog(String logPath, String logFileName) {
        if (fw != null)
            closeLog();
        GregorianCalendar gc = new GregorianCalendar();
        DayOfMonth = gc.get(Calendar.DAY_OF_YEAR);
        String dateStr = gc.get(Calendar.YEAR) +"-"+ String.format("%02d", (gc.get(Calendar.MONTH) + 1)) + "-"
                + String.format("%02d", gc.get(Calendar.DAY_OF_MONTH));
        String logFileDir = "";

        boolean isNewFile = false;

        try {
            String logFile = String.format("%s/%s-%s.log",logPath, logFileName, dateStr);
            logFileDir =logPath;

            if(logger.isDebugEnabled()){
//                logger.debug(logFile); // for debug
            }

            File dir = new File(logFileDir);
            if(!dir.exists()) {
                try{
                    dir.mkdir(); //폴더 생성합니다.
                }
                catch(Exception e){
                    e.getStackTrace();
                }
            }

            File target = new File(logFileDir, logFileName);
            // file중복체크
            if (!target.exists()) {
                new File(logFileDir, logFileName);
                isNewFile = true;
            }
            fw = new FileWriterWithEncoding(logFile, "UTF-8", true);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 1;
    }

    /**
     * FileWriter Close CloseLog void
     */
    public void closeLog() {
        try {
            if (logger.isDebugEnabled()) {
//                logger.debug("fw.close()");
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        } finally {
            if (fw != null)
                try {
                    fw.close();
                } catch (Exception e) {
                    logger.error(e.toString(), e);
                }
        }
    }
}

