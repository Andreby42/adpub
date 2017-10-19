import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.model.ads.Station;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.client.ClientDto;
import com.bus.chelaile.model.client.JsonStr;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.push.AdsPushService;
import com.bus.chelaile.service.ServiceManager;
import com.bus.chelaile.service.StartService;
import com.bus.chelaile.service.UserHelper;
import com.bus.chelaile.util.JsonBinder;

/**
 * 测试说明：数据库中有一条advId为1的广告
 * rule有有很多条，测试哪一条rule，便将其advid改为1，其他都是0（无效）
 * @author 林子
 *
 */
public class RuleTest {
	private static ApplicationContext context;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("开始");
		context = new ClassPathXmlApplicationContext("classpath:servicebiz/locator-baseservice.xml");
		StartService st = context.getBean(StartService.class);
		AdsPushService pushService = context.getBean(AdsPushService.class);
//		st.init(); // 初始化
		ServiceManager ld = context.getBean(ServiceManager.class);
		AdvParam ad = new AdvParam();
		ad.setUdid("6b37295824e402c6d8d366e3c5f91e49fa5cac69");
//		ad.setUdid("ffff");
		ad.setAccountId("9769123");
		ad.setS("IOS");
		ad.setCityId("027");
		ad.setLng(116.41656316721179);
		ad.setLat(40.00264773227946);
		ad.setVc(10310);	//ios 投第三方[10220, ]，android投第三方[60, ]	
		ad.setV("5.26.0");
		ad.setIp("10.168.25.113");
		ad.setIdfa("C5E740CD-9972-4C5F-B50A-E61076589E84");
		ad.setUa("Mozilla/5.0 (iPhone; CPU iPhone OS 8_2 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko)\n"
				+ "Version/8.0 Mobile/12D436 Safari/600.1.4");
		
		
		String stn = "炎黄艺术馆,0,1;慧忠里,1,1;慧忠路东口,2,1;安慧桥北,3,0;秀园,4,0;慧忠路西口,5,0;大屯南,6,0;慧忠北路西口,7,0;";
		ad.setStationList(Station.parseStationList(stn));
		
		System.out.println("所有可投放的广告" + ld.getDisplayAdv(ad));

		
		int id=11123;
		String ruleId="1236";
		String ruleIds=null;
//		pushService.pushAds(id, ruleId, ruleIds);
		
//		//测试功能：‘reload当前时间之后生效的广告’
		// sleep 7min，保证获取广告的时间在初始化之后。而广告的生效时间在这2个时间之间。
//		Thread.sleep(7 * 60 * 1000 );
//		System.out.println("版本测试;");
//		System.out.println("版本测试结果：" + (getAdvId_0(ad, ld, "ios") == 1) + "," + (getAdvId_0(ad, ld, "android") == -1));
		// 通过
////
//		System.out.println("城市测试;");
//		System.out.println("城市测试结果：" + (getAdvId_1(ad, ld, "027") == 1) + "," + (getAdvId_1(ad, ld, "006") == -1) + ","
//				+ (getAdvId_1(ad, ld, "000000") == -1));
		// 通过
//
//		System.out.println("经纬度测试;");
//		System.out.println("经纬度测试结果：" + (getAdvId_2(ad, ld, 117.277623, 39.113485) == 1) + ","
//				+ (getAdvId_2(ad, ld, 117.277623, 33.113485) == -1));
//		// 通过

//		System.out.println("网络类型测试;");
//		System.out.println("网络类型测试结果：" + (getAdvId_3(ad, ld, "4G") == 1) + "," + (getAdvId_3(ad, ld, "3G") == -1));
//		// 通过
		
//		System.out.println("用户类型;");
//		System.out.println("用户类型测试结果：" + (getAdvId_4(ad, ld, "e30647e8cf40370a927c1d9101af467f89ac2cf0") == 1) + "," + 
//				(getAdvId_4(ad, ld, "e30647e8cf40370a927c1d9101af467f89ac2cxx") == -1));
		// 待线上测试
		
//		System.out.println("cacheTime测试;");
//		System.out.println("cacheTime测试结果：" + (getAdvId_5(ad, ld, "4G") == 1) + "," + (getAdvId_5(ad, ld, "3G") == -1));
		// 待测
		
//		System.out.println("右下角投放次数测试;");
//		System.out.println("右下角投放次数测试结果：" + (getAdvId_6(ad, ld, 5) == 1) + "," + (getAdvId_6(ad, ld, 2) == -1));
//		//通过
		
		System.out.println("投放总次数测试;");
		System.out.println("投放总次数测试结果：" + (getAdvId_8(ad, ld, 1) == 1));
		//测出问题， 将cacheRecord.buildAdPubCacheRecord(ad.getId()) 加上即解决
		
//		System.out.println("限定时间段+cacheTime投放次数测试;");
//		System.out.println("限定时间段+cacheTime投放次数测试结果：" + (getAdvId_9(ad, ld, 3) == 1));
//		Thread.sleep(20 * 1000);
//		System.out.println("限定时间段+cacheTime投放次数测试结果：" + (getAdvId_9(ad, ld, 3) == 1));
//		System.out.println("限定时间段+cacheTime投放次数测试结果：" + (getAdvId_9(ad, ld, 3) == -1));
//		//测出 时间校验 DateUtil 中的compareNowDate 格式错误，已经修正
		
		
//		System.out.println("UV限制测试;");
//		System.out.println("UV限制测试结果：" + (getAdvId_10(ad, ld, "aaa", 2) == 1));
//		Thread.sleep(2000);
//		System.out.println("UV限制测试结果：" + (getAdvId_10(ad, ld, "bbb", 2) == 1));
//		Thread.sleep(2000);
//		System.out.println("UV限制测试结果：" + (getAdvId_10(ad, ld, "ccc", 1) == 1));
//		Thread.sleep(2000);
//		System.out.println("UV限制测试结果：" + (getAdvId_10(ad, ld, "ddd", 1) == -1));//第四个UV，超出UV限制
//		Thread.sleep(2000);
//		System.out.println("UV限制测试结果：" + (getAdvId_10(ad, ld, "aaa", 2) == 1));
//		// 通过
		
		
//		System.out.println("自动黑名单测试;");
//		//测试方法：将redis中  NEWADSRECORD#aaa#05 的value手动修改，
//		//set  NEWADSRECORD#aaa#05  "{\"cacheRecordMap\":{1:{\"clickCount\":0,\"dayCountMap\":{\"2016-11-25\":2}}},\"cacheTime\":0,\"displayAdv\":true,\"firstClickMap\":{},\"todayHistoryMap\":{},\"todayOpenHistoryMap\":{},\"uninterestedMap\":{},\"uvMap\":{1:\"2016-11-24\"}}"
//		//让uvMap中的value值不等于今天即可达到次日测试效果
//		System.out.println("自动黑名单测试结果：" + (getAdvId_10(ad, ld, "aaa", 2) == -1));
//		//通过
		
//		System.out.println("预加载接口 测试;");
//		//测试方法：将redis中  NEWADSRECORD#aaa#05 的value手动修改，
//		System.out.println("预加载接口 测试结果：" + (getAdvId_11(ad, ld) == 3));
//		//通过

	}
	
	
	

	// rule限定：ios用户
	private static int getAdvId_0(AdvParam ad, ServiceManager ld, String s) throws Exception {
		ad.setS(s);
		BaseAdEntity adEntity = (BaseAdEntity) ld.getQueryValue(ad, "getLineDetails");
		if (adEntity != null) {
			System.out.println(adEntity.getId());
			return adEntity.getId();
		}
		System.out.println("返回广告结果是空！");
		return -1;
	}

	// rule限定：城市是北京
	private static int getAdvId_1(AdvParam ad, ServiceManager ld, String cityId) throws Exception {
		ad.setCityId(cityId);
//		BaseAdEntity adEntity = (BaseAdEntity) ld.getQueryValue(ad, "getLineDetails");
		BaseAdEntity adEntity = (BaseAdEntity) ld.getQueryValue(ad, "preLoadAds");
		if (adEntity != null)
			return adEntity.getId();
		System.out.println("返回广告结果是空！");
		return -1;
	}

	// rule限定：经纬度和距离是 117.277623,39.113485,300;
	private static int getAdvId_2(AdvParam ad, ServiceManager ld, double lng, double lat) throws Exception {
		ad.setLat(lat);
		ad.setLng(lng);
		BaseAdEntity adEntity = (BaseAdEntity) ld.getQueryValue(ad, "getLineDetails");
		if (adEntity != null)
			return adEntity.getId();
		System.out.println("返回广告结果是空！");
		return -1;
	}

	// rule限定：网络必须是4G
	private static int getAdvId_3(AdvParam ad, ServiceManager ld, String nw) throws Exception {
		ad.setNw(nw);
		BaseAdEntity adEntity = (BaseAdEntity) ld.getQueryValue(ad, "getLineDetails");
		if (adEntity != null)
			return adEntity.getId();
		System.out.println("返回广告结果是空！");
		return -1;
	}
	
	// rule限定：用户必须是新用户，
	//往redis中植入新用户：e30647e8cf40370a927c1d9101af467f89ac2cf0
	private static int getAdvId_4(AdvParam ad, ServiceManager ld, String udid) throws Exception {
		ad.setUdid(udid);
		CacheUtil.set("CREATEUSERTIME#e30647e8cf40370a927c1d9101af467f89ac2cf0", String.valueOf(System.currentTimeMillis()));
		System.out.println("新用户植入成功否：" + UserHelper.isNewUser("e30647e8cf40370a927c1d9101af467f89ac2cf0", "", ""));
		BaseAdEntity adEntity = (BaseAdEntity) ld.getQueryValue(ad, "getLineDetails");
		if (adEntity != null)
			return adEntity.getId();
		System.out.println("返回广告结果是空！");
		return -1;
	}
	
	// rule限定："cacheTime":60
	private static int getAdvId_5(AdvParam ad, ServiceManager ld, String cityId) throws Exception {
		BaseAdEntity adEntity = (BaseAdEntity) ld.getQueryValue(ad, "getLineDetails");
		if (adEntity != null)
			return adEntity.getId();
		System.out.println("返回广告结果是空！");
		return -1;
	}
	
	
	// rule限定："rightPushNum":5
	// adv内容：{"adMode":8}，右下角次数用完后返回 null 
	private static int getAdvId_6(AdvParam ad, ServiceManager ld, int times) throws Exception {
		for(int i = 0; i < times; i ++)	{
			BaseAdEntity adEntity = (BaseAdEntity) ld.getQueryValue(ad, "getLineDetails");
			if (adEntity != null)
				System.out.println(adEntity.getId());
			else {
				System.out.println("返回广告结果是空！");
				return -1;
			}
		}
		return 1;
	}	
	
	// rule限定：{"clickCount":5}
	private static int getAdvId_7(AdvParam ad, ServiceManager ld, int clickTimes) throws Exception {
		BaseAdEntity adEntity = (BaseAdEntity) ld.getQueryValue(ad, "getLineDetails");
		if (adEntity != null)
			return adEntity.getId();
		System.out.println("返回广告结果是空！");
		return -1;
	}
	
	// rule限定：{"autoBlackList":0,"totalCount":6}
	private static int getAdvId_8(AdvParam ad, ServiceManager ld, int times) throws Exception {
		for(int i = 0; i < times; i ++)	{
//			JSONObject obj = (JSONObject) ld.getQueryValue(ad, "preLoadAds");
//			JSONObject obj = (JSONObject) ld.getQueryValue(ad, "precacheResource");
//			JSONObject obj = (JSONObject) ld.getQueryValue(ad, "getDoubleAndSingleAds");
			JSONObject obj = (JSONObject) ld.getQueryValue(ad, "getLineDetails");
//			JSONObject obj = (JSONObject) ld.getQueryValue(ad, "getNewOpen");
			if (obj != null) {
				System.out.println(obj.toJSONString());
//				System.out.println((BaseAdEntity) obj.get("lineAds"));
//				System.out.println(((BaseAdEntity) obj.get("lineAds")).getId());
				System.out.println(getClienSucMap(obj));
			}
			else {
				System.out.println("返回广告结果是空！");
				System.out.println(getClientErrMap(Constants.STATUS_NO_DATA));
				return -1;
			}
			Thread.sleep(2000);
		}
		return 1;
	}
	
	// rule限定：{"cacheTime":10,"days":1,"perDayCount":4,"adTimeCounts":[{"count":30,"time":"10:00-20:00"}],"autoBlackList":0}
	// 如果需要限定 perDayCount或者totalCount，那么days不能为空
	// 如果需要限定cacheTime，那么adTimeCounts不能为空
	// 如果下面这个方法没有'Thread.sleep(2000)';，会出现发送了3次广告，但是cache中只记录了1次。是读写cache的延时性导致的
	private static int getAdvId_9(AdvParam ad, ServiceManager ld, int times) throws Exception {
		for(int i = 0; i < times; i ++)	{
			//测试详情页广告
//			BaseAdEntity adEntity = (BaseAdEntity) ld.getQueryValue(ad, "getLineDetails");
			//测试开屏广告
			BaseAdEntity adEntity = (BaseAdEntity) ld.getQueryValue(ad, "getNewOpen");
			if (adEntity != null)
 				System.out.println(adEntity.getId());
			else {
				System.out.println("返回广告结果是空！");
				return -1;
			}
			Thread.sleep(2000);
		}
		return 1;
	}
	
	//rule限定：{"uvLimit":3,"autoBlackList":1,"chatOrRide":0}
	private static int getAdvId_10(AdvParam ad, ServiceManager ld, String udid, int times) throws Exception {
		ad.setUdid(udid);
		for(int i = 0; i < times; i ++)	{
			//测试详情页广告
			BaseAdEntity adEntity = (BaseAdEntity) ld.getQueryValue(ad, "getLineDetails");
//			//测试开屏广告
//			BaseAdEntity adEntity = (BaseAdEntity) ld.getQueryValue(ad, "getNewOpen");
			if (adEntity != null)
 				System.out.println(adEntity.getId());
			else {
				System.out.println("返回广告结果是空！");
				return -1;
			}
			Thread.sleep(2000);
		}
		return 1;
	}
	
	
	// rule 生效时间，是两天后，测试这种情况下能否‘预加载成功’
	private static int getAdvId_11(AdvParam ad, ServiceManager ld) throws Exception {
		List<BaseAdEntity> adEntity = (List<BaseAdEntity>) ld.getQueryValue(ad, "preLoadAds");
		if (adEntity != null && adEntity.size() != 0) {
			for(BaseAdEntity adE : adEntity) {
				System.out.println(adE.getId() + "," + adE.toString());
			}
		}
		if (adEntity != null && adEntity.size() != 0) {
			return adEntity.get(0).getId();
		}
		System.out.println("返回广告结果是空！");
		return -1;
	}
	
	
	
	
    private static String getClienSucMap(Object obj) {
    	ClientDto clientDto = new ClientDto();
    	clientDto.setSuccessObject(obj, Constants.STATUS_REQUEST_SUCCESS);
        try {
            String json = JsonBinder.toJson(clientDto, JsonBinder.always);
            return "**YGKJ" + json + "YGKJ##";
        } catch (Exception e1) {
            return "";
        }
    }

    private static String getClientErrMap(String status) {
        JsonStr jsonStr = new JsonStr();
        jsonStr.setData(new JSONObject());
        jsonStr.setStatus(status);
        try {
            String json = JsonBinder.toJson(jsonStr, JsonBinder.always);
            return "**YGKJ" + json + "YGKJ##";
        } catch (Exception e1) {
            return "";
        }
    }
}
