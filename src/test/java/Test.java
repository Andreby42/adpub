import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdCommonContent;
import com.bus.chelaile.model.ads.AdStationlInnerContent;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.StartService;
import com.bus.chelaile.service.impl.OtherManager;
import com.bus.chelaile.util.JsonBinder;
import com.bus.chelaile.util.New;


public class Test {
	public static void main(String[] args) throws Exception {
		
		String js = "{\"displayType\":1,\"adWeight\":2,\"autoInterval\":1500,\"backup\":0,\"barColor\":\"\",\"brandIcon\":\"\",\"brandName\":\"\",\"buttonColor\":\"\",\"buttonIcon\":\"\",\"buttonTitle\":\"\",\"buttonType\":0,\"clickDown\":0,\"feedId\":\"\",\"head\":\"\",\"lat\":0,\"lng\":0,\"mixInterval\":1500,\"pic\":\"\",\"position\":0,\"promoteTitle\":\"\",\"provider_id\":5,\"showDistance\":0,\"subhead\":\"\",\"tag\":\"\",\"tagId\":\"\",\"timeout\":0}";
		
		
		AdCommonContent adc = new AdCommonContent();
		adc.setAndParseJson(js);
		
		AdStationlInnerContent ad1 = new AdStationlInnerContent();
		ad1.setAndParseJson(js);
		
		List<String> list = New.arrayList();
		
		list.add("https://image3.chelaile.net.cn/a2f29092cfe34beda37c23592d9c44b2#600,33");
		list.add("https://image3.chelaile.net.cn/a2f29092cfe34beda37c23592d9c44b2#400,33");
		list.add("https://image3.chelaile.net.cn/a2f29092cfe34beda37c23592d9c44b2#200,33");
		
		System.out.println(JsonBinder.toJson(list, JsonBinder.nonNull));

		ApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:servicebiz/locator-baseservice.xml");
		
		  // CacheUtil.initClient();
		   
		   OtherManager om =  context.getBean(OtherManager.class);
		   
		  
		
//		
//		StartService st = context.getBean(StartService.class);
//		ServiceManager ld = context.getBean(ServiceManager.class);
//		QuchenshiService quchenshiService = context.getBean(QuchenshiService.class);
//		
		AdvParam ad = new AdvParam();
		ad.setUdid("2d41d8cd98f00b204e9800998ecf8427e292f5b44");
//		ad.setUdid("ffff");
		ad.setAccountId("22");
		ad.setS("ios");
		ad.setSv("10.0.2");
		ad.setCityId("027");
		ad.setLng(117.277623);
		ad.setLat(39.113485);
		ad.setNw("4G");
		ad.setVc(10250);	//ios 投第三方[10220, ]，android投第三方[60, ]	
		ad.setV("5.23.0");
		ad.setScreenHeight(1200);
		ad.setType(0);
		ad.setIp("114.112.124.83");
		ad.setDeviceType("iPhone6s");
		ad.setIdfa("C5E740CD-9972-4C5F-B50A-E61076589E84");
		ad.setUa("Mozilla/5.0 (iPhone; CPU iPhone OS 8_2 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko)\n"
				+ "Version/8.0 Mobile/12D436 Safari/600.1.4");
		
		StartService ss =	context.getBean(StartService.class);
		ss.init();
		
		om.doServiceList(ad, ShowType.TRANSFER_ADV, null);
		
		
		 //om.doService(ad, ShowType, isNeedApid, queryParam, isRecord);
//		
//		
//		String stn = "轮胎厂中路,0,1;地铁霍营站,1,0;霍营公交场站,2,0;枫丹丽舍小区西,3,0;龙跃苑东五区西门,4,0;";
//		ad.setStationList(Station.parseStationList(stn));
//		
//		//调用开屏广告
//		ld.getQueryValue(ad, "getNewOpen");
//		
//		Thread.sleep(500);
//		System.out.println("是否成功写入‘有效udid’缓存:" + AdvCache.isRealUsers(ad.getUdid())); //是否成功写入‘有效udid’缓存
//		
//		System.out.println("是否获取过二维码" + quchenshiService.getQrCodeFromOCS(ad));
//		
//		quchenshiService.getQrCode(ad);
//		System.out.println("是否获取过二维码" + quchenshiService.getQrCodeFromOCS(ad));
//		
//		Thread.sleep(500);
//		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
//		System.out.println("当日获取二维码的数目:" + CacheUtil.getFromRedis(todayStr + "_gained"));
		
//		System.out.println(CypherHelper.genMD5("2DtLS2TMxSEWCqm"));
		
		Set<String> pics = New.hashSet();
		pics.add("11111");
		pics.add("2222");
		pics.add("33333");
		System.out.println(JSONObject.toJSONString(pics));
	}
}
