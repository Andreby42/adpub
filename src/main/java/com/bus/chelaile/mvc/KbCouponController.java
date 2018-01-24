package com.bus.chelaile.mvc;

import com.bus.chelaile.koubei.*;
import com.bus.chelaile.model.client.ClientDto;
import com.bus.chelaile.util.koubei.WowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;


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
            clientDto.setErrorObject("参数错误", "01");
            return clientDto;
        }
        if (!KBUtil.isBlankParam(new String[]{accountId, secret}) && !WowUtil.verifyAccount(accountId, secret, udid)) {
            clientDto.setErrorObject("校验失败", "03");
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
    public ClientDto myCoupons(String udid, String accountId, String secret, int pn) {
        ClientDto clientDto = new ClientDto();
        if (KBUtil.isBlankParam(new String[]{udid, accountId, secret}) || pn <= 0) {
            clientDto.setErrorObject("参数错误", "01");
            return clientDto;
        }
        if (!WowUtil.verifyAccount(accountId, secret, udid)) {
            clientDto.setErrorObject("校验失败", "03");
            return clientDto;
        }
        try {
            KoubeiInfo info = couponService.myCoupons(accountId, pn);
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
            clientDto.setErrorObject("参数错误", "01");
            return clientDto;
        }
        if (!WowUtil.verifyAccount(accountId, secret, udid)) {
            clientDto.setErrorObject("校验失败", "03");
            return clientDto;
        }
        try {
            CouponUseInfo useInfo = couponService.getCoupon(accountId, couponInfo);
            logger.info("getCoupons result {}", useInfo);
            if (null == useInfo) {
                clientDto.setErrorObject("领取失败", "03");
                return clientDto;
            }
            clientDto.setSuccessObject(useInfo, "00");
            return clientDto;
        } catch (Throwable e) {
            logger.error("getCoupons param {}-{} Exception", accountId, couponInfo, e);
            clientDto.setErrorObject("exception", "02");
        }
        return clientDto;
    }
}
