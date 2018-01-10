package com.bus.chelaile.koubei;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.DiscountInfo;
import com.alipay.api.request.KoubeiAdvertDeliveryDiscountBatchqueryRequest;
import com.alipay.api.response.KoubeiAdvertDeliveryDiscountBatchqueryResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoling on 2018/1/10.
 */
public class CouponService {

    public List<CouponInfo> listCoupons(String lng, String lat, String cityId, String accountId) throws AlipayApiException {
        List<CouponInfo> list = new ArrayList<>();
        KoubeiAdvertDeliveryDiscountBatchqueryRequest request = new KoubeiAdvertDeliveryDiscountBatchqueryRequest();
        String cityCode = CityCodeMap.getCityCodeByCityId(cityId);
        if (StringUtils.isBlank(cityCode)) {
            return list;
        }
        CouponQueryParam couponQueryParam = new CouponQueryParam(lng, lat, cityCode);
        if (StringUtils.isNotBlank(accountId)) {
            // ocs 中 获取 userId
        }
        request.setBizContent(JSONObject.toJSONString(couponQueryParam));
        KoubeiAdvertDeliveryDiscountBatchqueryResponse response = getAlipayClient().execute(request);
        System.out.println(response.getBody());
        if(!response.isSuccess()) {
            return list;
        }
        List<DiscountInfo> discounts = response.getDiscounts();
        if (discounts.isEmpty()) {
            return list;
        }
        for (int i = 0; i < discounts.size(); i++) {
            DiscountInfo discountInfo = discounts.get(i);
            list.add(new CouponInfo(discountInfo.getItemName(), discountInfo.getDistance(),
                    discountInfo.getApplyCondition(), discountInfo.getPrice()));
        }
        return list;
    }

    private AlipayClient getAlipayClient() {
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                "2014030400003751",
                "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKaSaZIkb9e2NjwJfBBaTcGERNnIjWviJ17IHHQgL2gBWt3j0ETrgZP9FCue3TfVOF0+FvkWu7OENV5mTXeAR+nTl4g9qw9HgdRTjvDjfS3zvvc3CKFe9osomncvXeQXvn7+RnwCTJp0OjcGCOJfuTA+pimITbvUS0rCiAw33XFPAgMBAAECgYBU78+ZT68gJa+eCZATnpiLlvCsxJEoc9dzg0LPDCJgPGCjSKlIm3YliiUg4Q8Yi0cEdMauGSN5NG8qRaw2xVjlVfebR9MkMrZLy0BwRh09umcPAdnoVfTCxtNpUbeG6CP4d//7a5SPQ3aPzRqbT6fL24BILM5qIx0OoNso0BuxAQJBANJg9xNdjRCjGmeqn0IUyWfgXBFtA2wBjwCspwZw5oM0MEroavVNh6IiH/ejlIE8aptek+XbNsTNknjsa/IYnA8CQQDKsYoy0WxKfheg8HjdEmGjWcrF75797wzUFy3PG2idqjdxh3/Pa80AmE1y2lqlFgxCEPeinIH0Nd2v3MoHMZbBAkEAilDWIRVQua+CnMXBD2E7SeBop8xUg55Ctt7MsZ9o7rpRRe6o476lfiORgO87o/xk2uHDu0v1Jk9CDd7i2bj0YQJBAIdzGwoYnsgs+PdIm0wIY4z4jSO2nEXPQIBeuPMEuuVZgVFxnfxraoQyQtc0iYx2blyb4BAfjEw4ztsdrTgfcEECQGQyJlJURXaWiYVtYf/LFKqTFuVyyxnrYLq4BhOsVcnDm1HUUYftj0g+PVIY00D3C3yK9+cOmYN/fyHCMGzI0vU=",
                "json",
                "UTF-8",
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB",
                "RSA");
        return alipayClient;
    }

}
