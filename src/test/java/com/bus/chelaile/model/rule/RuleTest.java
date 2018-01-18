/**
 * Created by tingx on 2016/11/17.
 */
package com.bus.chelaile.model.rule;

import com.bus.chelaile.model.ads.Station;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.AdvInvalidService;
import com.bus.chelaile.service.ServiceManager;
import com.bus.chelaile.service.StartService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;


public class RuleTest {
    ApplicationContext context = new ClassPathXmlApplicationContext(
            "classpath:servicebiz/locator-baseservice.xml");
    StartService st = context.getBean(StartService.class);
    ServiceManager ld = context.getBean(ServiceManager.class);
    AdvParam ad = new AdvParam();
    public RuleTest() {
        st.init();
        ad.setUdid("2a620bc4561ae0cf0dfe7372e403a271e8784923");
        ad.setS("android");
        ad.setVc(61);
        ad.setV("5.20.1");
        ad.setCityId("006");
        ad.setLat(39.9581588343);
        ad.setLng(116.2651760976);
        ad.setScreenHeight(1200);
        ad.setType(0);
        String stn = "白酒厂大道,0,1;民权门,1,0;狮子林桥,2,0;双马公交站,3,0;第二工人文化宫,4,0;";
        ad.setStationList(Station.parseStationList(stn));
    }

    // Test cases for Station Lists (站点名称列表)
    @Test
    public void testNothingMatched() {

        try {
            BaseAdEntity adEntity = (BaseAdEntity)ld.getQueryValue(ad, "getLineDetails");
            assertEquals(adEntity.getId(), 2415);
            AdvInvalidService ad2 = new AdvInvalidService();
            ad2.invalidUser("12", "22", "2016-10-20", "2016-10-20");
            ad2.clearAccountId("22");
        } catch (Exception e) {
            System.err.println("Exception caught");
            e.printStackTrace();
        }
    }

    @Test
    public void testStationListMatched() {
        ad.setStnName("八里台");
        try {
            BaseAdEntity adEntity = (BaseAdEntity)ld.getQueryValue(ad, "getLineDetails");
            assertEquals(adEntity.getId(), 2416);
        } catch (Exception e) {
            System.err.println("Exception caught");
            e.printStackTrace();
        }
    }

    @Test
    public void testStationListNotMatched() {
        ad.setStnName("七里台");
        try {
            BaseAdEntity adEntity = (BaseAdEntity)ld.getQueryValue(ad, "getLineDetails");
            assertEquals(adEntity.getId(), 2415);
        } catch (Exception e) {
            System.err.println("Exception caught");
            e.printStackTrace();
        }
    }

    @Test
    public void testVersionListMatched() {
        ad.setV("5.20.0");
        try {
            BaseAdEntity adEntity = (BaseAdEntity)ld.getQueryValue(ad, "getLineDetails");
            assertEquals(adEntity.getId(), 2417);
        } catch (Exception e) {
            System.err.println("Exception caught");
            e.printStackTrace();
        }
    }

    @Test
    public void testUdidListMatched() {
        ad.setUdid("2a73f980-ad3e-11e6-b3a7-94533073614e");
        try {
            BaseAdEntity adEntity = (BaseAdEntity)ld.getQueryValue(ad, "getLineDetails");
            assertEquals(adEntity.getId(), 2418);
        } catch (Exception e) {
            System.err.println("Exception caught");
            e.printStackTrace();
        }
    }

    @Test
    public void testDailyMaxCountPerUser() {
        int[] adidList = new int [4];
        int[] desiredAdidList = {2420, 2420, 2420, 2415};
        try {
            for (int i = 0; i < 4; i++) {
                BaseAdEntity adEntity = (BaseAdEntity) ld.getQueryValue(ad, "getLineDetails");
                adidList[i] = adEntity.getId();
            }
            assertArrayEquals(desiredAdidList, adidList);
        } catch (Exception e) {
            System.err.println("Exception caught");
            e.printStackTrace();
        }
    }
}
