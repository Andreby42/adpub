package com.bus.chelaile.mvc;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.Text;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.entity.TaskEntity;
import com.bus.chelaile.mvc.utils.JSFileHandle;
import com.bus.chelaile.push.pushModel.SinglePushParam;
import com.bus.chelaile.service.StaticAds;
import com.bus.chelaile.util.New;

public class AbstractController {
    
    protected static final Logger logger = LoggerFactory.getLogger(AbstractController.class);
    
    protected void produceJS(AdvParam p, String originJs, TaskEntity tgs, String tag, HttpServletRequest request,
            ShowType showType, Writer writer) {
        if (StringUtils.isBlank(originJs)) {
            return;
            // return "originjs file is null ";
        }

        if (tgs == null || tgs.getTaskGroups() == null || tgs.getTaskGroups().getTasks().size() == 0) {
            logger.info("udid={}, tgs is null", p.getUdid());
            return;
        }
        
        
        Map<String, String> map = New.hashMap();

        if (tgs != null) {
            map.put("TASKS", tgs.getTaskGroups().getTasks().toString());
            map.put("TIMEOUTS", tgs.getTaskGroups().getTimeouts().toString());
            if (StringUtils.isNoneBlank(tgs.getTraceid())) {
                map.put("TRACEID", tgs.getTraceid());
            }
            map.put("closePic", tgs.getTaskGroups().getClosePic());
            map.put("hostSpotSize", tgs.getTaskGroups().getHostSpotSize());
            map.put("fakeRate", tgs.getTaskGroups().getFakeRate().toString());
            map.put("JSID", String.valueOf(tgs.getJsid()));
            for (List<String> tasks : tgs.getTaskGroups().getTasks()) {
                for (String task : tasks) {
                    if (task.contains("api_chelaile")) {
                        map.put("QUERY_STRING", request.getQueryString());
                        map.put("API_CHELAILE_DATA", tgs.getAdDataString());
                    }
                }
            }

            Text list = StaticAds.NEW_JS_FILE_STR.get(tag);


            JSFileHandle.replaceNewJs(p.getS(), showType, tgs, tag, map);

            try {
                list.write(writer, map);
//                ReplaceJs.getNewReplaceStr(list, map, writer);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public AdvParam getActionParam(HttpServletRequest request) {
        AdvParam param = new AdvParam();
        
        param.setTraceid(request.getParameter("traceid"));
        
        param.setUserId(request.getParameter("userId"));
        param.setAccountId(request.getParameter("accountId"));
        param.setS(request.getParameter("s"));
        param.setSv(request.getParameter("sv"));
        param.setUdid(request.getParameter("udid"));
        param.setV(request.getParameter("v"));
        param.setVc(getInt(request, "vc"));
        param.setNw(request.getParameter("nw"));
        param.setFirst_src(request.getParameter("first_src"));

        // 位置相关
        param.setLng(getDouble(request, "lng"));
        param.setLat(getDouble(request, "lat"));
        if(param.getLng() == -1.0 || param.getLat() == -1.0) {
            param.setLng(getDouble(request, "geo_lng"));
            param.setLat(getDouble(request, "geo_lat"));
        }
        param.setStnName(request.getParameter("stnName"));
        if(StringUtils.isBlank(param.getStnName()))
            param.setStnName(request.getParameter("stationName"));

        param.setDeviceType(request.getParameter("deviceType"));
        param.setIdfa(request.getParameter("idfa"));
        param.setImei(request.getParameter("imei"));
        param.setMac(request.getParameter("mac"));
        param.setAndroidID(request.getParameter("AndroidID"));
        param.setUa(request.getParameter("userAgent"));
        param.setO1(request.getParameter("o1"));

        param.setLineId(request.getParameter("lineId"));
        param.setLineNo(request.getParameter("lineNo"));
        param.setLineName(request.getParameter("lineName"));

        param.setScreenHeight(getInt(request, "screenHeight"));
        param.setCityId(request.getParameter("cityId"));
        param.setH5Src(request.getParameter("h5_src"));
        param.setFrom(request.getParameter("from"));
        // 如果h5_src参数为空，取src参数
        if (StringUtils.isEmpty(param.getH5Src()))
            param.setH5Src(request.getParameter("src"));
        param.setH5User(request.getParameter("h5_user"));

        // 针对h5用户，手动把h5Id赋值给udid吗，用于后续的 udid规则控制
        // userId复给udid
        if ((StringUtils.isBlank(param.getUdid()) || param.getUdid().equals("null"))
                && StringUtils.isNotBlank(param.getUserId())) {
            param.setUdid(param.getUserId());
        }
        if ((StringUtils.isBlank(param.getUdid()) || param.getUdid().equals("null")) 
                && StringUtils.isNoneBlank(param.getH5User())
                && !param.getH5User().equals("null")) {
            param.setUdid(param.getH5User());
        }
        param.setShareId(request.getParameter("shareId"));

        param.setStatsAct(request.getParameter("stats_act"));

        if (request.getParameter("ip") != null && !request.getParameter("ip").equals("")) {
            param.setIp(request.getParameter("ip"));
        } else {
            param.setIp(request.getParameter("remote_addr"));
        }
        param.setScreenWidth( getInt(request, "screenWidth"));   // 屏幕高度
        param.setScreenDensity(getDouble(request, "screenDensity")); // 几倍屏
        param.setDpi(request.getParameter("dpi"));
        param.setGpsAccuracy(request.getParameter("geo_lac"));
        if( request.getParameter("gpsAccuracy") != null && !request.getParameter("gpsAccuracy").equals("") ) {
        	param.setGpsAccuracy(request.getParameter("gpsAccuracy"));
        }
        if( request.getParameter("wifissid") != null ) {
        	if( param.getMac() != null ) {
        		param.setWifissid(param.getMac() + "," + request.getParameter("wifissid"));
        	}else {
        		param.setWifissid( request.getParameter("wifissid"));
        	}
        	
        }
        
        param.setAdContainerShown(getInt(request, "adContainerShown"));

        return param;
    }
    
    public void setWechatParams(HttpServletRequest request, AdvParam param) {
        param.setWxs(request.getParameter("wxs")); // 小程序来源
        param.setSrc(request.getParameter("src"));
        if (StringUtils.isEmpty(param.getH5Src()))
            param.setH5Src(request.getParameter("src"));
        param.setUdid(request.getParameter("h5Id"));     // 接口没定义好，客户端上传的是h5Id
        param.setSite(getInt(request, "site"));  // 区分位置。 目前小程序相关，只有引流位不care这个位置参数
    }

    public SinglePushParam getPushParam(HttpServletRequest request) {
        SinglePushParam singlePushParam = new SinglePushParam();

        String body = request.getParameter("body");
        String link = request.getParameter("link");
        String pushKey = request.getParameter("pushKey");
        String pushType = request.getParameter("pushType");
        String title = request.getParameter("title");

        singlePushParam.setBody(body == null ? "" : body);
        singlePushParam.setLink(link == null ? "" : link);
        singlePushParam.setPushKey(pushKey == null ? "" : pushKey);
        singlePushParam.setPushType(pushType);
        singlePushParam.setTitle(title == null ? "" : title);

        return singlePushParam;
    }

    protected static int getInt(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value == null || value.length() == 0) {
            return -1;
        } else {
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
                return -1;
            }
        }
    }

    protected static long getLong(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value == null || value.length() == 0) {
            return 0L;
        } else {
            try {
                return Long.parseLong(value);
            } catch (Exception e) {
                return 0L;
            }
        }
    }

    protected static Double getDouble(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value == null || value.length() == 0) {
            return -1.0;
        } else {
            try {
                return Double.parseDouble(value);
            } catch (Exception e) {
                return -1.0;
            }
        }
    }

    /*
     * 返回布尔类型的参数值
     */
    protected static boolean getBoolean(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (StringUtils.isBlank(paramName)) {
            return false;
        } else {
            try {
                return Boolean.parseBoolean(value);
            } catch (Exception e) {
                return false;
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
    }
}
