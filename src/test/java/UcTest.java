import java.util.HashMap;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.flow.FlowService;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.flow.model.ChannelType;
import com.bus.chelaile.flow.model.FlowContent;
import com.bus.chelaile.util.New;


public class UcTest {
	private static ApplicationContext context;

	public static void main(String[] args) throws Exception {
		context = new ClassPathXmlApplicationContext("classpath:servicebiz/locator-baseservice.xml");

		FlowService ld = context.getBean(FlowService.class);
		
		AdvParam ad = new AdvParam();
		ad.setS("ios");
		ad.setUserId("ou7Q_aaa");
		ad.setLineId("027-1000000000000-1");
		
		ad.setUdid("d41d8cd98f00b204e9800998ecf8427e292f5b44");
//		ad.setUdid("ffff");
		ad.setAccountId("22");
		ad.setSv("10.0.2");
		ad.setCityId("027");
		ad.setLng(117.277623);
		ad.setLat(39.113485);
		ad.setNw("4G");
		ad.setVc(10250);	//ios 投第三方[10220, ]，android投第三方[60, ]	
		ad.setV("5.24.0");
		ad.setScreenHeight(1200);
		ad.setType(0);
		ad.setIp("114.112.124.83");
		ad.setDeviceType("iPhone6s");
		ad.setIdfa("C5E740CD-9972-4C5F-B50A-E61076589E84");
		ad.setUa("Mozilla/5.0 (iPhone; CPU iPhone OS 8_2 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko)\n"
				+ "Version/8.0 Mobile/12D436 Safari/600.1.4");
		
		
		long ftime = 1489996120294l;
		String recoid = "12486031420915323127";
		int id = 1;
		
//		List<FlowContent> ucContentList = (List<FlowContent>) ld.getArticlesInfoList(ad, ftime, recoid, id, ChannelType.TOUTIAO, 0);
		
		HashMap<String, List<FlowContent>> res = New.hashMap();
//		res.put("ad", ucContentList);
		
		System.out.println(JSONObject.toJSONString(res));
	}
}
