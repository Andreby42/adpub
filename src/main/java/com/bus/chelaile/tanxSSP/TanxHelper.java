package com.bus.chelaile.tanxSSP;

import java.io.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.flow.model.ChannelType;
import com.bus.chelaile.flow.model.FlowContent;
import com.bus.chelaile.flow.model.Thumbnail;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.entity.AdEntity;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.tanxSSP.TanxProtos.Response.Seat.Ad;
import com.bus.chelaile.tanxSSP.TanxProtos.Response.Seat.Ad.NativeAd.Attr;
import com.bus.chelaile.tanxSSP.TanxProtos.Request.*;
import com.bus.chelaile.tanxSSP.TanxProtos.*;
import com.bus.chelaile.util.HttpUtils;
import com.bus.chelaile.util.New;

public class TanxHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(TanxHelper.class);
	
//	public static final String TANX_URL = "http://140.205.241.3/api";	// test
//	public static final String TANX_URL = "http://ope.tanx.com/api";
	public static final String TANX_URL = "http://ope.tanx.com/api?trace=1";
	
	
	
	public static Response getResponse(AdvParam advParam) {
//		android mm_26632831_36214064_129188704
		// ios    mm_26632831_36214064_129188735
		String ip = advParam.getIp();
		String ua = advParam.getUa();
		ua = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_3 like Mac OS X) AppleWebKit/603.3.8 (KHTML, like Gecko) Mobile/14G60";
		String idfa = advParam.getIdfa();
		String os = advParam.getS().equals("ios") ? "iOS" : "Android";
		String osv = advParam.getSv();
		
		
		Impression imp = Impression.newBuilder().setId(0).setPid("mm_26632831_36214064_129188735").build();
		Device device = Device.newBuilder().setIp(ip).setUserAgent(ua)
				.setIdfa(idfa).setDeviceType(0).setOs(os).setOsv(osv).setNetwork(1).build();
//		App app = App.newBuilder().setPackageName("com.ygkj.chelaile.standard").setAppName("车来了").setCategory("101701").build();
		App app = App.newBuilder().setPackageName("com.xxx.news").setAppName("新闻").addCategory("101701").build();
		
		Request tanxRequest = Request.newBuilder().setVersion(2).setId("0a674362000057b6be176e7600d4c089")
				.addImp(imp).setDevice(device).setApp(app)
				.build();
		// 请求信息打印json
		System.out.println("打印tanx请求对象信息： " + tanxRequest.toString());
		logger.info("打印tanx请求对象信息： {}" + tanxRequest.toString());
		
		// 序列化对象方式传输
		Response responseData = null;
		InputStream in = null;
		try {
			in  = HttpUtils.postBytes(TANX_URL, tanxRequest.toByteArray(), "application/octet-stream");
			responseData = Response.parseFrom(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseData;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		
		// 构建 proto 对象
		//android mm_26632831_36214064_129188704
		// ios    mm_26632831_36214064_129188735
		Impression imp = Impression.newBuilder().setId(0).setPid("mm_26632831_36214064_129188704").build();
		Device device = Device.newBuilder().setIp("59.172.75.115").setUserAgent("Mozilla/5.0(Linux;U;Android	4.2.2;zh-CN;vivoY13	Build/JDQ39)	AppleWebKit/534.30	(KHTML,likeGecko)Version/4.0 UCBrowser/10.10.8.822U3/0.8.0Mobile Safari/534.30Mozilla/5.0(Linu")
				.setImei("862565031315219").setDeviceType(0).setBrand("Samsung").setModel("galaxy").setOs("Android").setOsv("4.4.4").setNetwork(1).setOperator(1).setWidth(140).setHeight(216).setPixelRatio(10000).setTimezoneOffset(480).build();
//		App app = App.newBuilder().setPackageName("com.ygkj.chelaile.standard").setAppName("车来了").setCategory("101701").build();
		App app = App.newBuilder().setPackageName("com.xxx.news").setAppName("新闻").addCategory("101701").build();
		
		Request tanxRequest = Request.newBuilder().setVersion(2).setId("0a674362000057b6be176e7610d4c081")
				.addImp(imp).setDevice(device)
				.setApp(app)
				.build();
		
		//请求信息打印json
//		System.out.println("打印json信息");
//		System.out.println(tanxRequest.toString());
//		// 写
//		FileOutputStream fos = new FileOutputStream("D:\\1.txt");
//		tanxRequest.writeTo(fos);
//		fos.close();
////		// 读
//		Request requestData = Request.parseFrom(new FileInputStream("D:\\1.txt"));
//		System.out.println("version = " + requestData.getVersion());
//		System.out.println("id = " + requestData.getId());
//		System.out.println("app = " + requestData.getApp());
//		System.out.println("appName = " + requestData.getApp().getAppName() + ", model = " + requestData.getDevice().getModel()
//				+ ", userAgent = " + requestData.getDevice().getUserAgent());
		
		
		
		// post bytes
//		 List<byte[]> bytesList = new ArrayList<byte[]>();
//		 for(Products2.Products22.Builder p22Builder : builderList){
//	            Products2.Products22 p22 = p22Builder.build();
//	            byte[] bytes = p22.toByteArray();
//	            bytesList.add(bytes);
//	        }
//		tanxRequest.
		
//		for(int i =0; i < 100; i ++ ){
		Response responseData = null;
		InputStream in = null;
		try {
			in  = HttpUtils.postBytes(TANX_URL, tanxRequest.toByteArray(), "application/octet-stream");
//			String byteStr = HttpUtils.sendPost(TANX_URL, tanxRequest.toByteArray(), "utf-8");
			responseData = Response.parseFrom(in);
			System.out.println(responseData.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}



//		ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(new File("D:/tanxRequest.txt")));
//		oo.writeObject(tanxRequest);
//		System.out.println("Person对象序列化成功！");
//		oo.close();
		
		// 序列化对象方式传输
//		Response responseData = HttpUtils.httpPostSerialObject(TANX_URL, tanxRequest);
//		
//		System.out.println("response : status=" + responseData.getStatus() + ", id=" + responseData.getId());
//		System.out.println(responseData.toString());
		

//		
//		// json格式写文件，对比上面写的内容
//		JsonRequest json = new JsonRequest();
//		json.setVersion(1);
//		json.setDetectedLanguage("Chinese");
//		String jsonStr = JSONObject.toJSONString(json);
//		FileWriter  fos2 = new FileWriter ("D:\\2.txt");
//		fos2.write(jsonStr);
//		fos2.close();
//		
//		

		
		
	
		
	}


	public static HashMap<Integer, FlowContent> getAdv(AdvParam advParam, int id, ChannelType channelType) {
		HashMap<Integer, FlowContent> ucMap = new HashMap<Integer, FlowContent>();
		try{
		Response response = getResponse(advParam);
		if(response == null) {
			logger.info("response = null, udid={}", advParam.getUdid());
			return null;
		}
		
		if(response.getStatus() != 0) {
			logger.info("response status is not 0, response={}", response.toString());
			return null;
		}
		
		logger.info("TanxResponse={}", response.toString());
		System.out.println("response=" + response.toString());
		
		AdEntity adEntity = new AdEntity(ShowType.FLOW_ADV.getValue());
		Ad ad = response.getSeat(0).getAd(0);
		adEntity.setId(ad.getId());
		adEntity.setLink(ad.getClickThroughUrl());	// 点击地址，在出发点击时，通过该地址到达落地页
		
		List<Attr> atrrs = ad.getNativeAd().getAttrList();
		HashMap<String, String> atrrsMap = New.hashMap();
		for(Attr a : atrrs) {
			atrrsMap.put(a.getName(), a.getValue());
		}
		
		
		
		FlowContent content = new FlowContent();
		content.setType(2);
		content.setImgsType(3); // 单图大图，且无遮罩模式
		content.setAdEntity(adEntity);
		
		
		content.setTitle(atrrsMap.get("title")); // 广告的title会显示在图片上面，所以去掉
		content.setId(adEntity.getId() + "");
		content.setUrl(adEntity.getLink());
//		adEntity

		ArrayList<Thumbnail> imgs = new ArrayList<Thumbnail>();
		imgs.add(new Thumbnail(atrrsMap.get("img_url")));
		content.setImgs(imgs);
//		content.setTag(adEntity.getButtonTitle()); // tag文字
//		content.setTagColor(adEntity.getButtonColor()); // tag颜色

		ucMap.put(2, content);	//测试，默认放第二个位置
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return ucMap;
	}
	
}
