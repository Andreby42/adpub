package com.bus.chelaile.koubei;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.DiscountInfo;
import com.alipay.api.request.KoubeiAdvertDeliveryDiscountBatchqueryRequest;
import com.alipay.api.request.KoubeiAdvertDeliveryItemApplyRequest;
import com.alipay.api.request.KoubeiMarketingCampaignVoucherDetailQueryRequest;
import com.alipay.api.response.KoubeiAdvertDeliveryDiscountBatchqueryResponse;
import com.alipay.api.response.KoubeiAdvertDeliveryItemApplyResponse;
import com.alipay.api.response.KoubeiMarketingCampaignVoucherDetailQueryResponse;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.dao.CouponOrderMapper;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.GPSConvert;
import com.bus.chelaile.util.config.PropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhaoling on 2018/1/10.
 */
public class CouponService {

    private final String OCSALIUSERIDKEYPRE = "KouBeiUserInfo_accountId:";
    private final String OCSALITONKENKEYPRE = "KouBeiUserInfo_userId:";
    private final String kbAliServerUrl = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "kb.ali.url", "https://openapi.alipay.com/gateway.do");
    private final String kbAliAppId = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "kb.ali.appid", "2014030400003751");
    private final String kbAliGY = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "kb.ali.gy", "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB");
    private final String kbAliSY = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "kb.ali.sy", "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKaSaZIkb9e2NjwJfBBaTcGERNnIjWviJ17IHHQgL2gBWt3j0ETrgZP9FCue3TfVOF0+FvkWu7OENV5mTXeAR+nTl4g9qw9HgdRTjvDjfS3zvvc3CKFe9osomncvXeQXvn7+RnwCTJp0OjcGCOJfuTA+pimITbvUS0rCiAw33XFPAgMBAAECgYBU78+ZT68gJa+eCZATnpiLlvCsxJEoc9dzg0LPDCJgPGCjSKlIm3YliiUg4Q8Yi0cEdMauGSN5NG8qRaw2xVjlVfebR9MkMrZLy0BwRh09umcPAdnoVfTCxtNpUbeG6CP4d//7a5SPQ3aPzRqbT6fL24BILM5qIx0OoNso0BuxAQJBANJg9xNdjRCjGmeqn0IUyWfgXBFtA2wBjwCspwZw5oM0MEroavVNh6IiH/ejlIE8aptek+XbNsTNknjsa/IYnA8CQQDKsYoy0WxKfheg8HjdEmGjWcrF75797wzUFy3PG2idqjdxh3/Pa80AmE1y2lqlFgxCEPeinIH0Nd2v3MoHMZbBAkEAilDWIRVQua+CnMXBD2E7SeBop8xUg55Ctt7MsZ9o7rpRRe6o476lfiORgO87o/xk2uHDu0v1Jk9CDd7i2bj0YQJBAIdzGwoYnsgs+PdIm0wIY4z4jSO2nEXPQIBeuPMEuuVZgVFxnfxraoQyQtc0iYx2blyb4BAfjEw4ztsdrTgfcEECQGQyJlJURXaWiYVtYf/LFKqTFuVyyxnrYLq4BhOsVcnDm1HUUYftj0g+PVIY00D3C3yK9+cOmYN/fyHCMGzI0vU=");
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final ExecutorService execService = new ThreadPoolExecutor(2, 4, 300L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    private final int pageSize = 10;
    private final int preCount = 10000;

    @Resource
    private CouponOrderMapper couponOrderMapper;

    /**
     *
     * @Description: 口碑券list（周边）
     *
     * @Date: 下午4:20 2018/1/13
     */
    public KoubeiInfo listCoupons(String lng, String lat, String cityId, String accountId, String stnName) throws AlipayApiException {
        long startTime = System.currentTimeMillis();
        KoubeiInfo koubeiInfo = new KoubeiInfo();
        List<CouponInfo> list = new ArrayList<>();
        String aliUserId = null;
        if (StringUtils.isNotBlank(accountId)) {
            // ocs 中 获取 userId
            TokenInfo tokenInfo = getTokenInfo(accountId);
            if (null != tokenInfo) {
                aliUserId = tokenInfo.getAliUserId();
                logger.info("listCoupons accountId{} , aliUserId {}", accountId, aliUserId);
                String token = tokenInfo.getToken();
                if (StringUtils.isNotBlank(token)) { // 已授权
                    logger.info("listCoupons accountId{} , token {}", accountId, token);
                    koubeiInfo.setStatus(1);
                }
            }
        }
        List<DiscountInfo> discounts = getDiscounts(cityId, lng, lat, aliUserId, accountId, stnName, true);
        if (null == discounts) {
            return koubeiInfo;
        }
        // 处理list 目的：使list的第一个值与广告标题尽量一致
        CouponInfo ocsCoupon = null;
        if (StringUtils.isNotBlank(stnName)) {
            String ocsValue = (String) CacheUtil.get(KBUtil.getKbCouponOcsKey(cityId, stnName));
            if (StringUtils.isNotBlank(ocsValue)) {
                ocsCoupon = JSONObject.parseObject(ocsValue, CouponInfo.class);
            }
        }
        logger.info("listCoupons oscCoupon {}", ocsCoupon);
        boolean isFound = false;
        for (int i = 0; i < discounts.size(); i++) {
            DiscountInfo discountInfo = discounts.get(i);
            String type = discountInfo.getType();
            CouponType couponType = CouponType.getType(type);
            if (null == couponType) {
                continue;
            }
            CouponInfo couponInfo = new CouponInfo(discountInfo.getItemId(), discountInfo.getDistance()
                    , discountInfo.getShopName(), discountInfo.getImageUrl());
            if (null != ocsCoupon && couponInfo.getItemId().equals(ocsCoupon.getItemId())
                    && couponInfo.getShopName().equals(ocsCoupon.getShopName())) {
                isFound = true;
                continue;
            }
            parseCouponInfo(discountInfo, couponType, couponInfo);
            if (StringUtils.isBlank(couponInfo.getItemName()) || couponInfo.getItemName().contains("null") || couponInfo.getItemName().contains("NULL")) {
                logger.info("listCoupons accountId{} , itemName {} null", accountId, couponInfo.getItemId());
                continue;
            }
            list.add(couponInfo);
        }
        if (isFound && !list.isEmpty()) {
            list.set(0, ocsCoupon);
        }
        koubeiInfo.setCoupons(list);
        logger.info("listCoupons params {}-{}-{}-{}-{} costs {} ms", accountId, cityId, stnName, lng, lat, (System.currentTimeMillis()-startTime));
        return koubeiInfo;
    }

    public void parseCouponInfo(DiscountInfo discountInfo, CouponType couponType, CouponInfo couponInfo) {
        String itemName = "";
        switch (couponType) {
            case discount:
                float discount = Float.valueOf(discountInfo.getDiscount());
                String discountStr = String.valueOf(discount*10);
                if (StringUtils.endsWith(discountStr, ".0")) {
                    discountStr = StringUtils.substringBeforeLast(discountStr, ".0");
                }
                itemName = discountStr+"折";
                break;
            case cash:
                if (StringUtils.isNotBlank(discountInfo.getPerPrice())) {
                    itemName = "满"+discountInfo.getThresholdPrice()+"元减"+discountInfo.getPerPrice()+
                            "元";
                    if (StringUtils.isNotBlank(discountInfo.getTopPrice())) {
                        couponInfo.setCondition("封顶"+discountInfo.getTopPrice()+"元");
                    }
                } else {
                    itemName = discountInfo.getPrice()+"元代金券";
                    couponInfo.setCondition(discountInfo.getApplyCondition());
                }
                break;
            case exchange:
                itemName = "兑换券";
                break;
            case limit_reduce_cash:
                itemName = "凭券特价"+discountInfo.getPrice()+"元";
                break;
        }
        couponInfo.setItemName(itemName);
    }


    /**
     *
     * @Description: 调用ali 批量获取口碑券推荐接口
     *
     * @Date: 下午4:20 2018/1/13
     */
    public List<DiscountInfo> getDiscounts(String cityId, String lng, String lat, String aliUserId, String accountId, String stnName, boolean isNeedLog) throws AlipayApiException {
        long startTime = System.currentTimeMillis();
        String cityCode = CityCodeMap.getCityCodeByCityId(cityId);
        if (StringUtils.isBlank(cityCode)) {
            return null;
        }
        CouponQueryParam couponQueryParam = new CouponQueryParam(cityCode);
        if (!KBUtil.isBlankParam(new String[]{lng, lat})) {
            double[] gcjGps = GPSConvert.bd2gcj(Double.valueOf(lng), Double.valueOf(lat));
            couponQueryParam = new CouponQueryParam(String.valueOf(gcjGps[0]), String.valueOf(gcjGps[1]), cityCode);
        }
        if (StringUtils.isNotBlank(aliUserId)) {
            couponQueryParam.setUser_id(aliUserId);
        }
        KoubeiAdvertDeliveryDiscountBatchqueryRequest request = new KoubeiAdvertDeliveryDiscountBatchqueryRequest();
        request.setBizContent(JSONObject.toJSONString(couponQueryParam));
        KoubeiAdvertDeliveryDiscountBatchqueryResponse response = getAlipayClient().execute(request);
        if (isNeedLog) {
            logger.info("getDiscounts param {}-{}-{}-{}-{}-{} body {} costs {} ms", cityId, accountId, aliUserId, stnName, lng, lat, response.getBody(), (System.currentTimeMillis()-startTime));
        }
        if(!response.isSuccess()) {
            return null;
        }
        List<DiscountInfo> discounts = response.getDiscounts();
        if (null == discounts || discounts.isEmpty()) {
            return null;
        }
        return discounts;
    }

    /**
     *
     * @Description: 用户已领取的口碑券list
     *
     * @Date: 下午4:21 2018/1/13
     */
    public KoubeiInfo myCoupons(String accountId, int pn) throws AlipayApiException {
        long startTime = System.currentTimeMillis();
        KoubeiInfo koubeiInfo = new KoubeiInfo();
        List<CouponInfo> list = new ArrayList<>();
        TokenInfo tokenInfo = getTokenInfo(accountId);
        if (null == tokenInfo || StringUtils.isBlank(tokenInfo.getAliUserId())) {
            return koubeiInfo;
        }
        if (StringUtils.isNotBlank(tokenInfo.getToken())) {
            koubeiInfo.setStatus(1); // 已授权
        }
        Integer couponCount = couponOrderMapper.couponCount(accountId, tokenInfo.getAliUserId());
        if (null == couponCount) {
            return koubeiInfo;
        } else if (couponCount > pageSize*pn) {
            koubeiInfo.setMore(1);
        }
        List<CouponOrder> orders = couponOrderMapper.listCouponOrders(accountId, tokenInfo.getAliUserId(), (pn-1)*pageSize, pageSize);
        logger.info("myCoupons params{}-{} costs {} ms", accountId, orders, (System.currentTimeMillis()-startTime));
        if (null == orders) {
            koubeiInfo.setCoupons(list);
            return koubeiInfo;
        }
        for (int i = 0; i < orders.size(); i++) {
             CouponOrder order = orders.get(i);
             if (order == null || order.getStatus() == CouponState.DELETED.getIndex()) {
                 logger.info("myCoupons params{} null", accountId);
                 continue;
             }
             CouponInfo couponInfo = new CouponInfo(order.getCouponId(), order.getItemName(),"-1", null, order.getShopName(), order.getImageUrl());
             if (StringUtils.isNotBlank(order.getCondition()) && !"null".equalsIgnoreCase(order.getCondition())) {
                 couponInfo.setCondition(order.getCondition());
             }
             couponInfo.setStatus(order.getStatus());
             couponInfo.setPartnerId(order.getPartnerId());
             couponInfo.setBenefitId(order.getBenefitId());
             list.add(couponInfo);
        }
        koubeiInfo.setCoupons(list);
        return koubeiInfo;
    }

    /**
     *
     * @Description: 定时更新口碑券状态
     *
     * @Date: 下午2:26 2018/1/15
     */
    public void updateCouponStatus() {

        logger.info("updateCouponStatus starting");
        // 状态是1的coupon数量
        Integer allCount = couponOrderMapper.allCouponCount();
        int cycleSize = allCount/preCount;
        if (allCount%preCount != 0) {
            cycleSize++;
        }
        for (int j = 0; j < cycleSize; j++) {
            List<CouponOrder> orders = couponOrderMapper.listAllCoupons(cycleSize*preCount, preCount);
            //logger.info("myCoupons params{}-{} costs {} ms", accountId, orders, (System.currentTimeMillis()-startTime));
            if (null == orders) {
                logger.info("updateCouponStatus has no order to updateStatus");
                return;
            }
            List<CouponOrder> updateOrders = new ArrayList<>();
            for (int i = 0; i < orders.size(); i++) {
                CouponOrder order = orders.get(i);
                if (null == order || order.getStatus() != CouponState.VALID.getIndex()) {
                    continue;
                }
                String accountId = order.getAccountId();
                String aliUserId = getAliUserId(accountId);
                if (StringUtils.isBlank(aliUserId)) {
                    continue;
                }
                try {
                    KoubeiMarketingCampaignVoucherDetailQueryRequest request = new KoubeiMarketingCampaignVoucherDetailQueryRequest();
                    CouponDetailParam couponDetailParam = new CouponDetailParam(aliUserId, order.getBenefitId());
                    request.setBizContent(JSONObject.toJSONString(couponDetailParam));
                    KoubeiMarketingCampaignVoucherDetailQueryResponse response = getAlipayClient().execute(request);
                    logger.info("myCoupons params{}-{}-{} responseBody {}", accountId, aliUserId, order.getBenefitId(), response.getBody());
                    if(response.isSuccess()){
                        String sta = response.getStatus();
                        int state = CouponState.getIndex(sta);
                        if (CouponState.VALID.getIndex() != state) {
                            CouponOrder couponOrder = new CouponOrder(order.getId(), state);
                            updateOrders.add(couponOrder);
                        }
                        if (-1 == state) {
                            logger.info("myCoupons params{}-{} -1 == state {}", accountId, aliUserId, sta);
                            continue;
                        }
                    }
                } catch (AlipayApiException e) {
                    logger.error("myCoupons params{}-{} exception ", accountId, aliUserId, e);
                }
            }
            for (int i = 0; i < updateOrders.size(); i++) {
                couponOrderMapper.updateStatus(updateOrders.get(i));
            }
        }
        logger.info("updateCouponStatus completed!");

    }


    /**
     *
     * @Description: 异步更新数据库中口碑券的状态
     *
     * @Date: 下午4:22 2018/1/13
     */
    private void updateOrderStatus(List<CouponOrder> updateOrders) {
        for (int i = 0; i < updateOrders.size(); i++) {
            final CouponOrder updateOrder = updateOrders.get(i);
            execService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        couponOrderMapper.updateStatus(updateOrder);
                    } catch (Exception e) {
                        logger.error("updateStatus order{} failed ", updateOrder, e);
                    } finally {
                        logger.info("updateStatus order{}", updateOrder);
                    }
                }
            });
        }
    }

    /**
     *
     * @Description: 调用ali 接口领取口碑券
     *
     * @Date: 下午4:22 2018/1/13
     */
    public CouponUseInfo getCoupon(String accountId, CouponInfo couponInfo) throws AlipayApiException {
        long startTime = System.currentTimeMillis();
        if (null == couponInfo) {
            logger.info("getCoupon params{}-{} coupon is null", accountId, couponInfo);
            return null;
        }
        // ocs 中 获取 userId
        TokenInfo tokenInfo = getTokenInfo(accountId);
        if (null == tokenInfo || StringUtils.isBlank(tokenInfo.getToken())) {
            logger.info("getCoupon params{}-{} token is null", accountId, couponInfo);
            return null;
        }
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String couponId = couponInfo.getItemId();
        CouponGetParam couponGetParam = new CouponGetParam(couponInfo.getItemId(), uuid);
        KoubeiAdvertDeliveryItemApplyRequest request = new KoubeiAdvertDeliveryItemApplyRequest();
        request.setBizContent(JSONObject.toJSONString(couponGetParam));
        KoubeiAdvertDeliveryItemApplyResponse response = getAlipayClient().execute(request, tokenInfo.getToken());
        logger.info("getCoupon params{}-{}-{} responseBody {} costs {} ms ", accountId, couponId, uuid, response.getBody(), (System.currentTimeMillis()-startTime));
        if (!response.isSuccess()) {
            logger.info("getCoupon params{}-{} response is null", accountId, couponId);
            return null;
        }
        String benefitId = response.getBenefitId();
        String benefitDetail = response.getBenefitDetail();
        JSONObject jsonObject = JSON.parseObject(benefitDetail);
        String partnerId = jsonObject.getString("partnerId");
        if (StringUtils.isBlank(benefitId) || StringUtils.isBlank(partnerId)) {
            return null;
        }
        CouponOrder order = new CouponOrder(accountId, tokenInfo.getAliUserId(), couponId, benefitId, partnerId);
        fromInfo2Order(couponInfo, order);
        int count = couponOrderMapper.insertCouponOrder(order);
        if (1 != count) {
            logger.info("getCoupon params{}-{}, insert Failed {}", accountId, couponId, order);
        }
        logger.info("getCoupon params{}-{} partnerId {}, costs {} ms", accountId, couponId, benefitId+"#"+partnerId, (System.currentTimeMillis()-startTime));
        return new CouponUseInfo(partnerId, benefitId);
    }

    private void fromInfo2Order(CouponInfo info, CouponOrder order) {
        order.setCondition(info.getCondition());
        order.setImageUrl(info.getImageUrl());
        order.setItemName(info.getItemName());
        order.setShopName(info.getShopName());
    }

    /**
     *
     * @Description: 获取用户ali账号
     *
     * @Date: 下午4:23 2018/1/13
     */
    private String getAliUserId(String accountId) {
        long startTime = System.currentTimeMillis();
        String value = (String) CacheUtil.getFromCommonOcs(OCSALIUSERIDKEYPRE+accountId);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        WowKBUserInfo userInfo = JSONObject.parseObject(value, WowKBUserInfo.class);
        if (null == userInfo) {
            return null;
        }
        logger.info("getAliUserId accountId {} costs {} ms", accountId, (System.currentTimeMillis()-startTime));
        return userInfo.getAuthUserId();
    }

    /**
     *
     * @Description: 获取用户ali账号及tonken
     *
     * @Date: 下午4:24 2018/1/13
     */
    private TokenInfo getTokenInfo(String accountId) {
        long startTime = System.currentTimeMillis();
        String aliUserId = getAliUserId(accountId);
        if (StringUtils.isBlank(aliUserId)) {
            return null;
        }
        TokenInfo tokenInfo = new TokenInfo(aliUserId);
        String value = (String) CacheUtil.getFromCommonOcs(OCSALITONKENKEYPRE+aliUserId);
        tokenInfo.setToken(value);
        logger.info("getTokenInfo accountId{} costs {} ms", accountId, (System.currentTimeMillis()-startTime));
        return tokenInfo;
    }

    private AlipayClient getAlipayClient() {
        AlipayClient alipayClient = new DefaultAlipayClient(kbAliServerUrl,
                kbAliAppId,
                kbAliSY,
                "json",
                "UTF-8",
                kbAliGY,
                "RSA");
        return alipayClient;
    }

}
