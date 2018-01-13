package com.bus.chelaile.mvc;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.koubei.CouponInfo;
import com.bus.chelaile.koubei.CouponService;
import com.bus.chelaile.koubei.KBUtil;
import com.bus.chelaile.koubei.KoubeiInfo;
import com.bus.chelaile.model.client.ClientDto;
import com.bus.chelaile.util.koubei.WowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhaoling on 2018/1/10.
 */
@Controller
@RequestMapping("/kb")
public class KbCouponController {

    @Resource
    CouponService couponService;


    public final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ResponseBody
    @RequestMapping("/listCoupons.action")
    public ClientDto listCoupons(String cityId, String udid, String secret, String accountId, String lng, String lat, String stnName) {
        ClientDto clientDto = new ClientDto();
        if (KBUtil.isBlankParam(new String[]{cityId,udid})) {
            clientDto.setErrorObject("paramError", "01");
            return clientDto;
        }
        if (!KBUtil.isBlankParam(new String[]{accountId, secret}) && !WowUtil.verifyAccount(accountId, secret, udid)) {
            clientDto.setErrorObject("verifyError", "03");
            return clientDto;
        }
        try {
            KoubeiInfo info = couponService.listCoupons(lng, lat, cityId, accountId, stnName);
            logger.info("listCoupons result {}", info);
            clientDto.setSuccessObject(info, "00");
            return clientDto;
        } catch (Throwable e) {
            logger.error("listCoupons param {}-{}-{}-{} Exception", cityId, accountId, lng, lat, e);
            clientDto.setErrorObject("exception", "02");
        }
        return clientDto;
    }

    @ResponseBody
    @RequestMapping("/myCoupons.action")
    public ClientDto myCoupons(String udid, String accountId, String secret) {
        ClientDto clientDto = new ClientDto();
        if (KBUtil.isBlankParam(new String[]{udid, accountId, secret})) {
            clientDto.setErrorObject("paramError", "01");
            return clientDto;
        }
        if (WowUtil.verifyAccount(accountId, secret, udid)) {
            clientDto.setErrorObject("verifyError", "03");
            return clientDto;
        }
        try {
            KoubeiInfo info = couponService.myCoupons(accountId);
            logger.info("myCoupons param {} result {}", accountId, info);
            clientDto.setSuccessObject(info, "00");
            return clientDto;
        } catch (Throwable e) {
            logger.error("myCoupons param {} Exception", accountId, e);
            clientDto.setErrorObject("exception", "02");
        }
        return clientDto;
    }

    @ResponseBody
    @RequestMapping(value = "/getCoupons.action", method = RequestMethod.POST)
    public ClientDto getCoupon(String udid, String accountId, String secret, CouponInfo couponInfo) {
        ClientDto clientDto = new ClientDto();
        if (KBUtil.isBlankParam(new String[]{udid, accountId, secret})) {
            clientDto.setErrorObject("paramError", "01");
            return clientDto;
        }
        if (WowUtil.verifyAccount(accountId, secret, udid)) {
            clientDto.setErrorObject("verifyError", "03");
            return clientDto;
        }
        try {
            boolean isSuccess = couponService.getCoupon(accountId, couponInfo);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("state", isSuccess ? 0 : 1);
            logger.info("getCoupons result {}", isSuccess);
            clientDto.setSuccessObject(jsonObject, "00");
            return clientDto;
        } catch (Throwable e) {
            logger.error("getCoupons param {}-{} Exception", accountId, couponInfo, e);
            clientDto.setErrorObject("exception", "02");
        }
        return clientDto;
    }
}
