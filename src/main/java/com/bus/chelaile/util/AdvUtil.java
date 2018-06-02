package com.bus.chelaile.util;

import com.alibaba.fastjson.JSONArray;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.common.ShortUrlUtil;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvUtil {
    public static final String ORGIN_URL_TAG = "{ORGIN_URL}";
    public static final String UTF8_ENCODING = "utf-8";
    public static final String WILDCARD_LINE_NO = "%LINENO%";
    public static final String WILDCARD_LINE_NAME = "%LINENAME%";

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    // 用户生成batchId的时间值
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
    private static final AtomicInteger batchSerialCount = new AtomicInteger(0);
    
    
    private static final String waildUrl = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "adv.udid.wildcard","%UDID%");
    private static final String waildAccountId = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "adv.udid.wildcardAccountId","%ACCOUNTID%");
    
    
    
  //  private static final String waildUrl = PropertiesReaderWrapper.read("adv.udid.wildcard", "%UDID%");
    
    private static final String redirectUrl = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "adv.redirect.url","http://redirect.chelaile.net.cn/?link={ORGIN_URL}");
    
    private static final String noParamUrl = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "api.redirect.url","http://ad.chelaile.net.cn/?link={ORGIN_URL}");
    
  //  private static final String redirectUrl = PropertiesReaderWrapper.read("adv.redirect.url", "http://redirect.chelaile.net.cn/?link={ORGIN_URL}");

    protected static final Logger logger = LoggerFactory.getLogger(AdvUtil.class);

    public static int getMaxGCD(int[] arr) {
        if (arr.length <2 ) {
            return arr[0];
        }

        int currGcd = gcd(arr[0], arr[1]);
        for (int i=2; i<arr.length; i++) {
            currGcd = gcd(currGcd, arr[i]);
        }
        return currGcd;
    }

    public static int gcd(int a, int b) {
        int temp;
        if (a > b) {
            temp = a;
            a = b;
            b = temp;
        }
        while(a > 0) {
            temp = b % a;
            b = a;
            a = temp;
        }

        return b;
    }

    public static String listToStr(List<? extends Object> list) {
        return listToStr(list, ",");
    }

    public static String listToStr(List<? extends Object> list, String separator) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuffer sb = new StringBuffer(list.get(0).toString());

        int size = list.size();
        for (int i = 1; i < size; i++) {
            sb.append(separator).append(list.get(i));
        }

        return sb.toString();
    }
    
    public static String mapToStr(Map<? extends Object, ? extends Object> map) {
    	return mapToStr(map, ",");
    }
    
    public static String mapToStr(Map<? extends Object, ? extends Object> map, String separator) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        StringBuffer sb = new StringBuffer("");

        int size = map.size();
//        for (int i = 0; i < size; i++) {
//            sb.append(separator).append(list.get(i));
//        }
        int i = 1;
        for(Entry<? extends Object, ? extends Object> entry : map.entrySet()) {
        	if(i < size)
        		sb.append(entry.getKey()).append(separator);
        	else
        		sb.append(entry.getKey());
        }

        return sb.toString();
    }

    /**
     * 如果max是0或者负数表示将list的所有元素输出。
     * @param list
     * @param maxDisplay
     * @return
     */
    public static String listToStr(List<String> list, int maxDisplay) {
        if (list == null) {
            return "null";
        }

        if (list.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        int displaySize = list.size();

        if (maxDisplay > 0 && displaySize > maxDisplay) {
            displaySize = maxDisplay;
            sb.append("size=").append(list.size()).append(", displaySize=").append(displaySize).append(", ");
        }
        sb.append(list.get(0));


        for (int i=1; i<displaySize; i++) {
           sb.append(", ").append(list.get(i));
        }
        sb.append("]");
        return sb.toString();
    }

    public static String toJson(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        StringBuffer sb = new StringBuffer("[");

        sb.append('\"').append(list.get(0)).append('\"');
        for (int idx = 1; idx < list.size(); idx++) {
            sb.append(", \"").append(list.get(idx)).append('\"');
        }
        sb.append(']');

        return sb.toString();
    }
    /**
     * 包含start， 但是不包含end
     * @param list
     * @param start
     * @param end
     * @return
     */
    public static List<Object> toJson(List<String> list, int start, int end, int bodyLength) {
        List<Object> resultList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        if (list == null || list.isEmpty() ) {
            resultList.add(jsonArray);
            resultList.add(0);
            return resultList;
        }
        start = (start < 0) ? 0 : start;
        end = (end >= list.size()) ? list.size() : end;
        if (start >= end || start >= list.size()) {
            resultList.add(jsonArray);
            resultList.add(0);
            return resultList;
        }
        String clientStr = null;
        int passMaxLengthSize = 0;
        for (int idx = start; idx < end; idx++) {
            String keyStr = list.get(idx);
            clientStr = StringUtils.substringBefore(keyStr, "#");
            if (!isNotPassMaxLength(clientStr, bodyLength)) {
                logger.info("passMaxLength, clinetStr : {}, bodyLength:{}", clientStr, bodyLength);
                passMaxLengthSize++;
                continue;
            }
            jsonArray.add(clientStr);
        }
        resultList.add(jsonArray);
        resultList.add(passMaxLengthSize);
        return resultList;
    }

    public static List<Object> toJson1(List<String> list, int start, int end, int bodyLength) {
        List<Object> resultList = new ArrayList<>();
        if (list == null || list.isEmpty() ) {
            resultList.add("[]");
            resultList.add(0);
            return resultList;
        }
        start = (start < 0) ? 0 : start;
        end = (end >= list.size()) ? list.size() : end;
        if (start >= end || start >= list.size()) {
            resultList.add("[]");
            resultList.add(0);
            return resultList;
        }
        StringBuffer sb = new StringBuffer("[");
        String clientStr = null;
        int passMaxLengthSize = 0;
        for (int idx = start; idx < end; idx++) {
            String keyStr = list.get(idx);
            clientStr = StringUtils.substringBefore(keyStr, "#");
            if (!isNotPassMaxLength(clientStr, bodyLength)) {
                logger.info("passMaxLength, clinetStr : {}, bodyLength:{}", clientStr, bodyLength);
                passMaxLengthSize++;
                continue;
            }
            if (idx != start) {
                sb.append(", ");
            }
            sb.append("\"").append(clientStr).append('\"');
        }

        sb.append(']');
        resultList.add(sb.toString());
        resultList.add(passMaxLengthSize);
        return resultList;
    }

    private static boolean isNotPassMaxLength(String clientStr, final Integer bodyLength) {
        try {
            if (bodyLength == Constants.PUSH_BODYLENGTH_DEFAULT) {
                return true;
            }
            if ((clientStr.getBytes("UTF-8").length + bodyLength) <= Constants.PUSH_IOS_BODYLENGTH_MAX) {
                return true;
            }
        } catch (Exception e) {
            logger.error("isNotPassMaxLength exception", e);
        }
        return false;
    }

    public static Date getTomorrow() {
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DATE, 1);// 加一个年
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date tomorrow = calendar.getTime();

        return tomorrow;
    }

    private static Date TODAY = null;

    // 由于每天凌晨服务器会重启， 因此可以将TODAY保存起来。
    public static Date getToday() {

        if (TODAY == null) {
            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            TODAY = calendar.getTime();
        }

        return TODAY;
    }

    public static Date getNDaysAfter(int nDays) {
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DATE, nDays);// 加一个年
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date ndaysAfter = calendar.getTime();

        return ndaysAfter;
    }

    /**
     * 给定的两个时间相差的毫秒数
     */
    public static long getTimeDiff(Date date1, Date date2) {
        long time1 = 0;
        long time2 = 0;
        if (date1 != null) {
            time1 = date1.getTime();
        }
        if (date2 != null) {
            time2 = date2.getTime();
        }
        return time1 - time2;
    }

    /**
     * 给定的两个时间相差的分钟数
     */
    public static long getTimeDiffMinute(Date date1, Date date2) {
        long diff = getTimeDiff(date1, date2);
        return diff / (1000 * 60);
    }

    /**
     * 给定的两个时间相差的秒数
     */
    public static long getTimeDiffSecond(Date date1, Date date2) {
        long diff = getTimeDiff(date1, date2);
        return diff / 1000;
    }

    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static boolean isNotEmpty(List<?> list) {
        return list != null && list.size() > 0;

    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return map != null && map.size() > 0;

    }

    public static boolean isTest() {
    	return Boolean.parseBoolean((PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "adv.isTest","false")));
        //return PropertiesReaderWrapper.readBool("adv.isTest", false);
    }

    public static String buildRedirectLink(String orgLink, Map<String, String> paramsMap, String udid, boolean isUseShortUrl, boolean isRepeatParam,int link_extra) {
        return buildRedirectLink(orgLink, paramsMap, udid, null, null, isUseShortUrl, isRepeatParam,link_extra);
    }

    public static String buildRedirectLink(String orgLink, Map<String, String> paramsMap, String udid, String lineNo, String lineName,
            boolean isUseShortUrl, boolean isRepeatParam,int link_extra) {
        logger.debug("buildRedirectLink orgLink: " + orgLink + " isused " + isUseShortUrl);
        if (StringUtils.isBlank(orgLink)) {
            return "";
        }
        
        // 2018-06-03 增加不中转的h5链接
        if(link_extra == 2) {
            return orgLink;
        }
        
        String waild = waildUrl;
        // 需要将orgLink之中的所有的参数copy一分放到外面去。
        String redirectUrl = getRedirectUrl();
        // 不需要参数
        if( link_extra == 0 ){
        	redirectUrl =	noParamUrl;
        }

        if (lineNo != null) {
            orgLink = orgLink.replaceAll(WILDCARD_LINE_NO, lineNo);
        }
        if (lineName != null) {
            orgLink = orgLink.replaceAll(WILDCARD_LINE_NAME, lineName);
        }

        String needEncodeOrgLink = orgLink.replaceAll(waild, udid);
        if (paramsMap.containsKey(Constants.PARAM_ACCOUNTID)) {
        	needEncodeOrgLink = needEncodeOrgLink.replaceAll(waildAccountId, paramsMap.get(Constants.PARAM_ACCOUNTID));
        } else if(needEncodeOrgLink.contains(waildAccountId)){
        	needEncodeOrgLink = needEncodeOrgLink.substring(0, needEncodeOrgLink.length() - 12); // 12 是accountId=%ACCOUNTID%的长度
        }
        
        needEncodeOrgLink = needEncodeOrgLink.contains("?")?needEncodeOrgLink:needEncodeOrgLink.replaceFirst("#!", "?#!");
        String encodedOrgUrl = encodeUrl(needEncodeOrgLink);
        
//      logger.info("redirectUrlfirst={},encodedOrgUrl={}",redirectUrl,encodedOrgUrl);
        
        redirectUrl = redirectUrl.replace(ORGIN_URL_TAG, encodedOrgUrl);
        
//        logger.info("redirectUrlsecond={},encodedOrgUrl={},isRepeatParam={}",redirectUrl,encodedOrgUrl,isRepeatParam);
        if (isRepeatParam) {
            if (paramsMap != null && paramsMap.size() > 0) {
                StringBuilder sb = new StringBuilder(redirectUrl);
                Set<Entry<String, String>> entrySet = paramsMap.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    String paramName = entry.getKey();
                    String paramValue = entry.getValue();
                    sb.append('&').append(paramName).append('=').append(paramValue);
                }
                redirectUrl = sb.toString();
            } else {
                // 追加一个假的参数
                redirectUrl += "&xyz=abc";
            }
        }
//        if( noNeedEncode != null ){
//        	redirectUrl += "&anchor="+noNeedEncode;
//        }else{
//        	redirectUrl += "&anchor=anchor";	//默认
//        }
        logger.debug("buildRedirectLink redircetUrl: {}", redirectUrl);
        if (isUseShortUrl) {
            String shortUrl = ShortUrlUtil.getShortUrl(redirectUrl);
            if (null != shortUrl) {
                logger.info("buildRedirectLink, " + udid + " shortUrl: " + shortUrl + " redirectUrl: " + redirectUrl);
                return shortUrl;
            } else {
                logger.info("shortUrl:{}, redirectUrl:{}, failed", shortUrl, redirectUrl);
            }
        }
        return redirectUrl;
    }

    /* 加参数代码
    public static String buildRedirectLink(String orgLink, Map<String, String> paramsMap, String udid, boolean isUseShortUrl, boolean isRepeatParam) {
        logger.debug("buildRedirectLink orgLink: " + orgLink + " isused " + isUseShortUrl);
        if (StringUtils.isEmpty(orgLink)) {
            return "";
        }
        StringBuilder paramStr = new StringBuilder();
        String waild = PropertiesReaderWrapper.read("adv.udid.wildcard", "%UDID%");
        // 需要将orgLink之中的所有的参数copy一分放到外面去。
        String redirectUrl = getRedirectUrl();
        String encodedOrgUrl = encodeUrl(orgLink.replaceAll(waild, udid));
        redirectUrl = redirectUrl.replace(ORGIN_URL_TAG, encodedOrgUrl);
        String orgParamStr = getParamsStr(orgLink);
        if (StringUtils.isNotEmpty(orgParamStr)) {
            String[] params = orgParamStr.split("&");
            for (int i = 0; i < params.length; i++) {
                String param = params[i];
                if (StringUtils.isNotBlank(param)) {
                    String[] strs = param.split("=");
                    if (strs.length == 2) {
                        if ("wse".equals(strs[0]) || "wtb".equals(strs[0]) || "wcb".equals(strs[0])) {
                            if (i != 0) {
                                paramStr.append("&");
                            }
                            paramStr.append(strs[0]).append("=").append(encodeUrl(strs[1]));
                        }
                    }
                }
            }
        }
        if (!isUseShortUrl && StringUtils.isNotBlank(paramStr.toString())) {
            redirectUrl += "&"+paramStr.toString();
        }
        if (isRepeatParam) {
            if (paramsMap != null && paramsMap.size() > 0) {
                StringBuilder sb = new StringBuilder(redirectUrl);
                Set<Entry<String, String>> entrySet = paramsMap.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    String paramName = entry.getKey();
                    String paramValue = entry.getValue();
                    sb.append('&').append(paramName).append('=').append(paramValue);
                }
                redirectUrl = sb.toString();
            } else {
                // 追加一个假的参数
                redirectUrl += "&xyz=abc";
            }
        }
        logger.debug("buildRedirectLink redircetUrl: {}", redirectUrl);
        if (isUseShortUrl) {
            String shortUrl = ShortUrlUtil.getShortUrl(redirectUrl);
            if (null != shortUrl) {
                logger.info("buildRedirectLink, " + udid + " shortUrl: " + shortUrl + " redirectUrl: " + redirectUrl);
                if (StringUtils.isNotBlank(paramStr.toString())) {
                    return shortUrl+"?"+paramStr.toString();
                }
                return shortUrl;
            } else {
                logger.info("shortUrl:{}, redirectUrl:{}, failed", shortUrl, redirectUrl);
            }
        }
        return redirectUrl;
    }
    */

    public static String getParamsStr(String orgLink) {
        if (StringUtils.isEmpty(orgLink)) {
            return null;
        }
        int idx = orgLink.indexOf("#!");
        if (idx >= 0) {
            orgLink = orgLink.substring(0, idx);
            if (orgLink == null) return null;
        }
        if (orgLink.contains("?")) {
            idx = orgLink.indexOf("?");
            return orgLink.substring(idx + 1);
        }
        return null;
    }

    public static String decodeUrl(String url) {
        try {
            if (url == null) {
                return null;
            }
            return URLDecoder.decode(url, UTF8_ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.error(String.format("解码URL出错: url=%s", url), e);
            return url;
        }
    }

    public static String encodeUrl(String url) {
        try {
            if (url == null) {
                return null;
            }
            return URLEncoder.encode(url, UTF8_ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.error(String.format("编码URL出错: url=%s", url), e);
            return url;
        }
    }

    public static String getRedirectUrl() {
        return redirectUrl;
    }

    public static <T, F> String printMap(Map<T, F> map, String name) {
        StringBuffer sb = new StringBuffer("Map[" + name + "]: {");
        if (map != null) {
            boolean isFirst = true;
            for (T key : map.keySet()) {
                if (isFirst) isFirst = false;
                else sb.append(", ");
                sb.append(key).append("=>").append(map.get(key));
            }
        } else {
            sb.append("null");
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * 从字符串之中解析出int数据，int数据之间用逗号分隔， 如: "12,345,2332"
     * @param str
     * @return
     */
    public static List<Integer> parseIntList(String str) {
        if (str != null) {
            //删除字符串之中的所有的空格
            str = str.replace(" ", "");
        }

        if (str == null || str.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> list = new ArrayList<Integer>();
        StringTokenizer st = new StringTokenizer(str, ",");
        while(st.hasMoreTokens()) {
            String token = st.nextToken();
            try {
                Integer value = Integer.valueOf(token);
                list.add(value);
            } catch (NumberFormatException nfe) {
                logger.error("WrongInteger: token={}, str={}, errMsg={}", token, str, nfe.getMessage());
            }
        }
        return list;
    }

    public static String getTimeStr() {
        return DATE_FORMAT.format(new Date());
    }

    public static String getPushBatchId(final String advId) {
        String batchId = dateFormat.format(new Date());

        int count = batchSerialCount.getAndIncrement();
        if (count >= 999) {
            int expect = count + 1;
            int newValue = 0;
            while (!batchSerialCount.compareAndSet(expect, newValue)) {
                count = batchSerialCount.get();
                newValue = count + 1;
                if (newValue >= 999) {
                    newValue = 0;
                }
            }
        }
        batchId += String.format("%03d", count);
        // 将 batchId 记录到数据库中
        return batchId;
    }

    /**
     * 判断字符串是否是乱码
     *
     * @param strName 字符串
     * @return 是否是乱码
     */
    public static boolean isMessyCode(String strName) {
        if (StringUtils.isBlank(strName)) {
            return true;
        }
        Pattern p = Pattern.compile("\\s*|t*|r*|n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = ch.length;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count = count + 1;
                }
            }
        }
        float result = count / chLength;
        //logger.info("isMessyCode cost {} ms", (System.currentTimeMillis()-st));
        if (result > 0.4) {
            return true;
        } else {
            return false;
        }
    }
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 判断某时间是否在某时段内
     * @param date          输入的时间
     * @param strDateBegin  时段开始时间
     * @param strDateEnd    时段结束时间
     * @param dateFormatStr 格式（HH:mm 或 HH:mm:ss）
     * @return
     */
    public static boolean isInTimeRange(Date date, String strDateBegin, String strDateEnd, String dateFormatStr) {

        SimpleDateFormat afterSdf = new SimpleDateFormat(dateFormatStr);
        Date dBegin = null;
        Date dEnd = null;
        try {
            dBegin = afterSdf.parse(strDateBegin);
            dEnd = afterSdf.parse(strDateEnd);
        } catch (ParseException e) {
            logger.error("Parse date error {}", e);
            return false;
        }
        Calendar calPlayer = Calendar.getInstance();
        calPlayer.setTime(date);

        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(dEnd);

        Calendar calendar = Calendar.getInstance();
        calBegin.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        calEnd.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        if (calEnd.after(calBegin)) {
            if (calPlayer.before(calEnd) && calPlayer.after(calBegin))
                return true;
            else
                return false;
        } else if (calBegin.after(calEnd)) {
            logger.warn("calBegin {} after calEnd {}", calBegin, calEnd);
            return false;
        } else {
            return false;
        }
    }
    
    public static void main(String args[]) {
    	String orgLink = " ";
    	System.out.println(StringUtils.isBlank(orgLink));
    }
}
