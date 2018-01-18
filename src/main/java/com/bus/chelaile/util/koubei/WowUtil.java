package com.bus.chelaile.util.koubei;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.HttpUtils;
import com.bus.chelaile.util.config.PropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.alibaba.fastjson.JSON.parseObject;

/**
 * Created by zhaoling on 2018/1/13.
 */
public class WowUtil {

    public static final String RESPONSE_PREIFX = "**YGKJ";
    public static final String RESPONSE_SURIFX = "YGKJ##";
    public static final String wowVerifyAccountUrl = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
            "wow.verify.account", "http://10.168.67.142:6080/wow/user!verifyAccount.action?accountId=%1$s&secret=%2$s&udid=%3$s");
    private static final Logger logger = LoggerFactory.getLogger(WowUtil.class);

    /**
     *
     * @Description: 验证用户合法性
     *
     * @Date: 下午12:12 2018/1/13
     */
    public static boolean verifyAccount(String accountId, String secret, String udid) {

        String requestUrl = String.format(wowVerifyAccountUrl, accountId,
                secret, udid);
        String responseBody = null;
        try {
            responseBody = HttpUtils.get(requestUrl, null, "UTF-8");
        } catch (IOException e) {
            logger.error("获取用户信息失败, accountId={} exception", accountId, e);
            return true;
        }
        if (StringUtils.isNotEmpty(responseBody)) {
            responseBody = StringUtils.substringBetween(responseBody,
                    RESPONSE_PREIFX, RESPONSE_SURIFX);
            JSONObject userObject = parseObject(responseBody);
            JSONObject jsonr = userObject.getJSONObject("jsonr");
            String status = jsonr.getString("status");
            if ("00".equals(status)) {
                return true;
            }
        } else {
            logger.info("获取用户信息失败, accountId={}", accountId);
        }
        return false;
    }

}
