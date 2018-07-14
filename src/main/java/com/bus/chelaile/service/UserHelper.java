package com.bus.chelaile.service;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.AdvCache;
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
     * 7天内新增用户判断
     */
    public static boolean isNewUser(String udid, String userId, String accountId) {
        if (StringUtils.isEmpty(udid)) {
            return false;
        }

        String key = "CREATEUSERTIME#" + udid;
        try {
//            String.valueOf(null);
            String createTimeStr = CacheUtil.getFromCommonOcs(key);
            logger.info("getcreate time : key={}, createTimeStr={}", key, createTimeStr);
            // 获取用户的创建时间。
            if (createTimeStr == null) {
                /**
                 * 由于目前OCS之中用户的创建时间信息只保存了15天，因此当CREATETIME时，默认该用户是老用户。
                 */
                //                 logger.info("OCS中无用户创建时间: udid={}, accountId={}, userId={}", udid, accountId, userId);
                return false;
            }
            Long createTime = Long.parseLong(createTimeStr);
            int newUserPeriod = Constants.DEFAULT_NEW_USER_PERIOD;
            return createTime + newUserPeriod > System.currentTimeMillis();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("[NEWUSER_EXCEPTION] 判断是否新用户异常， errMsg={}, udid={}, accountId={}",
                    new Object[] {ex.getMessage(), udid, accountId});
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
            Long createTime = (Long)CacheUtil.get(key);
            if (createTime == null) {
                return false;
            }
            return createTime + Constants.ONE_DAY_NEW_USER_PERIOD >= System.currentTimeMillis();
        } catch(Exception ex) {
            logger.error("[TODAY_NEWUSER_EXCEPTION] 判断是否是当日新用户异常， errMsg={}, udid={}, accountId={}", ex, udid, accountId);
        }
        return false;
    }


    /*
     * 针对默认规则，是否返回广告
     * 老用户，投放
     * 新增当天，不投放任何广告。第二天到第七天之间，如果完成过首次查询，那么立即投放广告。
     */
	public static boolean isReturnAds(String udid) {
		if (StringUtils.isEmpty(udid)) {
            return false;
        }
        String key = "CREATEUSERTIME#" + udid;
        try {
             // 获取用户的创建时间。
            Long createTime = (Long)CacheUtil.get(key);
             if (createTime == null) {
                 return true;	// 老用户，返回广告
             }
             
             if(createTime + Constants.ONE_DAY_NEW_USER_PERIOD > System.currentTimeMillis()) {
            	 return false;	// 当天新增，不返回广告
             }
             
             if(createTime + Constants.DEFAULT_NEW_USER_PERIOD > System.currentTimeMillis()) {
            	 String lineDetailKey = AdvCache.getBusesDetailKey(udid);
            	 if(CacheUtil.getNew(lineDetailKey) != null) {	// 七天内新增，如果访问过详情页，那么返回广告
            		 return true;
            	 } else {
            		 return false;
            	 }
             }
        } catch(Exception ex) {
           logger.error("[NEWUSER_EXCEPTION] 判断是否给新用户返回广告异常， errMsg={}, udid={}", new Object[]{ex.getMessage(), udid});
        }
        return true;
	}

}
