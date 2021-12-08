package kr.co.nexmore.rfiddaemon.reader.util;

import kr.co.nexmore.rfiddaemon.vo.reader.tcp.response.TagDataVO;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.regex.Pattern;

import static kr.co.nexmore.rfiddaemon.common.CommonControlCode.*;

public class CommonUtil {

    /**
     * ip 유효성 검사
     *
     * @param ip
     * @return boolean (true, false)
     */
    public static boolean validationIp(String ip) {
        boolean isValid = false;
        if (ip != null && !"".equals(ip)) {
            String validIp = "^([1-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}$";
            if (Pattern.matches(validIp, ip)) {
                isValid = true;
            }
        }
        return isValid;
    }


    /**
     * MAC 주소 유효성 검사
     *
     * @param macAddress
     * @return
     */
    public static boolean validationMac(String macAddress) {
        boolean isValid = false;

        if (macAddress != null && !"".equals(macAddress)) {
            if ("BROADCAST".equals(macAddress)) {
                isValid = true;
            } else {
                String validMac = "^([0-9a-fA-F][0-9a-fA-F]:){5}([0-9a-fA-F][0-9a-fA-F])$";
                if (Pattern.matches(validMac, macAddress)) {
                    isValid = true;
                }
            }
        }
        return isValid;
    }


    /**
     * gateway 생성 (ip 기준 D class 1로 생성)
     * ex) 40.40.40.160 -> 40.40.40.1
     *
     * @param ip
     * @return gateway
     */
    public static String setGateway(String ip) {
        String gateway = null;

        int dClass = ip.lastIndexOf(".");
        gateway = String.format("%s.1", ip.substring(0, dClass));

        return gateway;
    }

    /**
     * tagData 유효성검사 메서드 (영문2자리 + 숫자4자리 (ex)KAxxxx)
     *
     * @param tagDataVO
     * @return HashMap
     */
    public static HashMap<String, String> validationTagData(TagDataVO tagDataVO) {
        HashMap<String, String> result = new HashMap();
        if(TAG_EVENT_FORCE_END.equals(tagDataVO.getEventStatus())) {
            result.put("eventType", TAG_EVENT_END);
            result.put("message", null);
            return result;
        }

        if (tagDataVO == null || tagDataVO.getTagUid() == null || "".equals(tagDataVO.getTagUid())) {
            result.put("eventType", VALIDATION_FAIL);
            result.put("message", TAG_UID_NOT_EXIST);
            return result;
        } else {
            if (tagDataVO.getData() == null || "".equals(tagDataVO.getData())) {
                result.put("eventType", VALIDATION_FAIL);
                result.put("message", MZ_ID_NOT_EXIST);
                return result;
            } else {
                String pattern = "^[a-zA-Z]{2}(\\d{4})";
                if (!tagDataVO.getData().matches(pattern)) {
                    result.put("eventType", VALIDATION_FAIL);
                    result.put("message", String.format("[%s] MAGAZINE ID: %s", INVALID_MZ_ID, tagDataVO.getData()));
                    return result;
                } else {
                    result.put("eventType", tagDataVO.getEventStatus());
                    result.put("message", null);
                    return result;
                }
            }
        }
    }

    /**
     * long type 현재시간 -> String type 현재시간으로 convert
     * pattern: yyyyMMddHHmmssSSS (ex)20201028191200123
     *
     * @param currentTimeMillis
     * @return String
     */
    public static String getCurrentTimeMillis(long currentTimeMillis) {
        if (currentTimeMillis < 0) {
            currentTimeMillis = System.currentTimeMillis();
        }
        SimpleDateFormat sdf = new SimpleDateFormat ( "yyyyMMddHHmmssSSS");
        return sdf.format(currentTimeMillis);
    }
}
