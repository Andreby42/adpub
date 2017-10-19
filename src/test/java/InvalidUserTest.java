import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bus.chelaile.model.ads.Station;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.AdvInvalidService;
import com.bus.chelaile.service.ServiceManager;
import com.bus.chelaile.service.StartService;


/**
 * 测试不投放广告,改接口实现的功能有1min的延时
 * 本地测试请启动redis服务
 * @author 林子
 *
 */
public class InvalidUserTest {
	
	private static ApplicationContext context;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		
		
	
		System.out.println("开始");
		
		context = new ClassPathXmlApplicationContext(
				"classpath:servicebiz/locator-baseservice.xml");
		
		StartService st = context.getBean(StartService.class);
		
		st.init();
		
		ServiceManager ld = context.getBean(ServiceManager.class);
		
		AdvParam ad = new AdvParam();
		ad.setUdid("2a620bc4561ae0cf0dfe7372e403a271e8784922");
		ad.setAccountId("22");
		ad.setS("ios");
		ad.setVc(61);
		ad.setV("5.20.1");
		ad.setCityId("027");
		ad.setLat(39.9581588343);
		ad.setLng(116.2651760976);
		ad.setScreenHeight(1200);
		ad.setType(1);
		String stn = "轮胎厂中路,0,1;地铁霍营站,1,0;霍营公交场站,2,0;枫丹丽舍小区西,3,0;龙跃苑东五区西门,4,0;";
		ad.setStationList(Station.parseStationList(stn));
		
		
		
		// 测试不投放广告
		// 请求成功后，1min生效
		AdvInvalidService ad2 = new AdvInvalidService();
		ad2.invalidUser("2a620bc4561ae0cf0dfe7372e403a271e8784922", "22", "2016-10-20", "2016-11-20");
		ad2.invalidUser("2a620bc4561ae0cf0dfe7372e403a271e8784923", "23", "2016-10-20", "2016-11-20");
		System.out.println("22是否不投放广告：" + ad2.isInvalid("22"));
		System.out.println("23是否不投放广告：" + ad2.isInvalid("23"));
		ld.getQueryValue(ad, "getLineDetails");
		
		System.out.println("清除不投放广告权限");
		ad2.clearAccountId("23");
		System.out.println("22是否不投放广告：" + ad2.isInvalid("22"));
		System.out.println("23是否不投放广告：" + ad2.isInvalid("23"));
		ld.getQueryValue(ad, "getLineDetails");
		
	}

}
