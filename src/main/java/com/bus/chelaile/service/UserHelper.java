package com.bus.chelaile.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.model.UserType;

public class UserHelper {
    // 用于查询用户相关的属性， 调用WOW
    protected static final Logger logger = LoggerFactory.getLogger(UserHelper.class);

    public static int getUserType(String userId, String accountType) {
        int userType = 0;
        if (accountType == null || accountType.isEmpty()) {
            userType |= UserType.ANONYMOUS.getType();
        }

        return userType;
    }

    /*
     * 新增用户判断
     */
    public static boolean isNewUser(String s, String h5Src, String udid) {
        if (StringUtils.isEmpty(udid) || StringUtils.isEmpty(s)) {
            return false;
        }
        try {
            String key = "";
            long newUserPeriod = 0L;
            if (s.equals("android") || s.equals("ios")) {
                key = "CREATEUSERTIME#" + udid;
                newUserPeriod = Constants.DEFAULT_NEW_USER_PERIOD;
                String createTimeStr = CacheUtil.getFromCommonOcs(key);
                if (StringUtils.isNotEmpty(createTimeStr)) {
                    logger.info("getcreate time : key={}, createTimeStr={}", key, createTimeStr);
                    Long createTime = Long.parseLong(createTimeStr);

                    return createTime + newUserPeriod > System.currentTimeMillis();
                }
            } else if (s.equals("h5") && h5Src.equals("weixinapp_cx")) {
                key = "wechat#unionId#2#" + udid;
                
                newUserPeriod = Constants.TOW_DAY_NEW_USER_PERIOD;
                String wechatappNewTime = StaticAds.SETTINGSMAP.get(Constants.SETTING_WECHATAP_NEWTIME_KEY);
                if(StringUtils.isNoneBlank(wechatappNewTime)) {
                    newUserPeriod = Long.parseLong(wechatappNewTime);
                }
                
                //            key
                //            wechat#unionId#2#openid
                //            value
                //            unionId#createTime
//                String createTimeStr = CacheUtil.getFromCommonOcs(key);
                String createTimeStr = CacheUtil.getFromWechatOcs(key);
                if (StringUtils.isNotEmpty(createTimeStr)) {
                    String buf[] = createTimeStr.split("#");
                    if (buf.length >= 2) {
                        Long createTime = Long.parseLong(buf[1]);
                        logger.info("getcreate time : key={}, createTimeStr={}", key, createTimeStr);

                        return createTime + newUserPeriod > System.currentTimeMillis();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("判断用户是否是新用户出错， s={}, udid={}, h5Src={}", s, udid, h5Src);
        }

        return false;

    }

    public static boolean isAnonymousUser(String userId, String accountId) {
        return StringUtils.isBlank(accountId);
    }

    public static boolean isRegisteredUser(String userId, String accountId) {
        if (StringUtils.isNotBlank(accountId)) {
            return true;
        }

        return false;
    }

    /**
     * 判断用户是否是当日新增加用户
     * @param udid udid
     * @param accountId 账户id
     * @return 是否是当日新增加用户 true:是,false:否
     */
    public static boolean isTodayNewUser(String udid, String accountId) {
        if (StringUtils.isEmpty(udid)) {
            return false;
        }

        String key = "CREATEUSERTIME#" + udid;
        try {
            // 获取用户的创建时间
            Long createTime = (Long) CacheUtil.get(key);
            if (createTime == null) {
                return false;
            }
            return createTime + Constants.ONE_DAY_NEW_USER_PERIOD >= System.currentTimeMillis();
        } catch (Exception ex) {
            logger.error("[TODAY_NEWUSER_EXCEPTION] 判断是否是当日新用户异常， errMsg={}, udid={}, accountId={}", ex, udid, accountId);
        }
        return false;
    }

    /*
     * 针对默认规则，是否返回广告
     * 老用户，投放
     * 新增当天，不投放任何广告。第二天到第七天之间，如果完成过首次查询，那么立即投放广告。
     */
    public static boolean isReturnAds(String s, String h5Src, String udid) {

        return !isNewUser(s, h5Src, udid);
    }

}
