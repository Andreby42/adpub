package com.bus.chelaile.push.pushModel;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.rule.Rule;
import com.bus.chelaile.util.AdvUtil;
import com.bus.chelaile.util.config.PropertiesUtils;



public class QueryUserList {
    protected static final Logger logger = LoggerFactory.getLogger(QueryUserList.class);
    public static String DEFAULT_MIN_VERSION_IOS = "";
    public static String DEFAULT_MIN_VERSION_ANDROID = "2.7.0";

    public static MultiThreadedHttpConnectionManager connMgr = null;

    private static MultiThreadedHttpConnectionManager getConnMgr() {
        if (connMgr == null) {
            final int connNum = 5;
            final int expire = 10000;
            connMgr = new MultiThreadedHttpConnectionManager();
            HttpConnectionManagerParams connMgrParams = connMgr.getParams();
            connMgrParams.setMaxTotalConnections(connNum);  
            connMgrParams.setDefaultMaxConnectionsPerHost(connNum);
            connMgrParams.setReceiveBufferSize(2048); // buffer size 2k.
            connMgrParams.setConnectionTimeout(expire);
            connMgrParams.setSoTimeout(expire);
        }
        return connMgr;
    }
    
    
    public static String query(Rule rule) {
        final String requestId = genRequestID();
        List<String> userList = rule.getUserIds();
        
        if (userList == null || userList.isEmpty()) {
            queryFromData(requestId, rule);
        } else {
            queryFromWow(requestId, userList);
        }
        
        return requestId;
    }
    
    private static void queryFromWow(String requestId, List<String> userIdList) {
            /*
             * 从用户管理模块获取用户的UDID, 
             * 目前暂时没有实现，等有需求再支持吧。
             */
        
        return ;
    }
    
    public static String queryFromData(String requestId, Rule rule) {
        String url = null;  
        url = getListUDIDUrl() + "?" + buildParam(rule, requestId);
//        url = "http://121.40.250.70/userList/firstOut?cityList=042&verisonList=2.7.0&platform=Android&userType=0&requestId=" + requestId;
        
        logger.info("[Query_User_URL] url={}", url);

        HttpClient httpClient = new HttpClient(getConnMgr());
        httpClient.getParams().setContentCharset("utf-8");
        httpClient.getHostConfiguration().setProxyHost(null);
        
        long start = System.currentTimeMillis();
        GetMethod get = new GetMethod(url);
        
        try {
            int httpCode = httpClient.executeMethod(get);
            if (httpCode == 200) {
                InputStream input = get.getResponseBodyAsStream();
                
                byte[] buf = new byte[512];
                int len = 0;
                StringBuffer sb = new StringBuffer();
                while((len = input.read(buf)) > 0) {
                    System.out.println(AdvUtil.getTimeStr() + " - len=" + len);
                    sb.append(new String(buf, 0, len));
                }
                logger.info("[Query_USER_SUCCESS] response={}", sb.toString());
            }
        } catch (IOException ex) {
            logger.error(String.format("[QUREY_USER_FAIL] 查询用户UDID失败: url=%s, errMsg=%s", url,
                            ex.getMessage()), ex);
        } finally {
            if (get != null) {
                get.releaseConnection();
            }
        }

        long end = System.currentTimeMillis();
        System.out.println(AdvUtil.getTimeStr() + " - TIME: " + (end - start) + "ms");
        return requestId;
    }

    public String requestUserList(final Rule rule) {
        final String requestId = genRequestID();
        final String apiUrl = getListUDIDUrl();

        String url = apiUrl + "?" + buildParam(rule, requestId);

        logger.info("[REQUEST_UDID_URL] url={}, rule={}", url, rule);

        return requestId;
    }

    private static String buildParam(Rule rule, String requestId) {
        StringBuilder sb = new StringBuilder();

        sb.append("requestId=").append(requestId).append("&cityList=").append(AdvUtil.mapToStr(rule.getCities()))
                .append("&firstSrcList=").append(AdvUtil.listToStr(rule.getChannels()))
                .append("&versionList=").append(AdvUtil.mapToStr(rule.getVersions()))
                .append("&platform=").append(AdvUtil.listToStr(rule.getPlatforms()))
                .append("&userType=").append(rule.getUserType());

        return sb.toString();
    }

    public static String getListUDIDUrl() {
        return PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "adv.list.udid.url");
    }

    public static String genRequestID() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(new Date()) + "TF" + format(getCount());
    }

    private static int count = 0;

    public static synchronized int getCount() {
        return ++count;
    }

    public static String format(int cnt) {
        if (cnt < 10) {
            return "00" + cnt;
        }
        if (cnt < 100) {
            return "0" + cnt;
        }

        return "" + cnt;
    }

    public static void main(String[] args) {
        query(null);
    }
}
