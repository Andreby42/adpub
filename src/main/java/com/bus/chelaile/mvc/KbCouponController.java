package com.bus.chelaile.mvc;

import com.bus.chelaile.koubei.CouponInfo;
import com.bus.chelaile.koubei.CouponService;
import com.bus.chelaile.model.client.ClientDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

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
    public ClientDto listCoupons(String cityId, String accountId, String lng, String lat) {
        ClientDto clientDto = new ClientDto();
        if (isNotBlankParam(new String[]{cityId, lng, lat})) {
            clientDto.setErrorObject("paramError", "01");
            return clientDto;
        }
        try {
            List<CouponInfo> list = couponService.listCoupons(lng, lat, cityId, accountId);
            clientDto.setSuccessObject(list, "00");
            return clientDto;
        } catch (Throwable e) {
            logger.error("listCoupons param {}-{}-{}-{} Exception", cityId, accountId, lng, lat, e);
            clientDto.setErrorObject("exception", "02");
        }
        return clientDto;
    }

    private boolean isNotBlankParam(String ... params) {
        for (int i = 0; i < params.length; i++) {
            if (StringUtils.isBlank(params[i])) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println("hhhh" + new KbCouponController().isNotBlankParam(new String[]{"nhao", "hhh"}));
    }
}
