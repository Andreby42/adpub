package com.bus.chelaile.model.rule;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import com.bus.chelaile.flow.wangyiyun.WangYIParamForSignature;
import com.google.gson.Gson;

public class TestWangyi {
	
	private static  String secretkey= "3eb746dfa4a54361964e7b49d0e3e2dc";
	
	private static String appkey="379a2e02a7e24d389a490637891d0514";
	
	public static void main(String[] args) {
		
//		UUID uuid = UUID.
//		System.out.println(uuid);
//		
		//installChannel();
	//	installNewList();
		//installNewDetail();
//		Map<String,String> testMap = new HashMap<>();
//		testMap.put("publishTime", "2017-12-18 17:13:59");
//		Gson  g = new Gson();
//		TestDate t =  g.fromJson(g.toJson(testMap),TestDate.class);
//		System.out.println(t.publishTime);
	}
	
	class TestDate{
		Date publishTime;
	}
	
	private static Set<WangYIParamForSignature> initBaseParam(){
		
		WangYIParamForSignature timestamp = new WangYIParamForSignature("timestamp", System.currentTimeMillis()+"");
		System.out.println("timestamp:"+timestamp.getValue());
		WangYIParamForSignature platform = new WangYIParamForSignature("platform", 3+"");
		WangYIParamForSignature version = new WangYIParamForSignature("version", "v1.4.0");
		WangYIParamForSignature apk = new WangYIParamForSignature("appkey", appkey);
		Set<WangYIParamForSignature> set = new TreeSet<>();
		set.add(timestamp);
		set.add(version);
		set.add(platform);
		set.add(apk);
		return set;
	}
	
	
	
	private static Set<WangYIParamForSignature> installNewDetail() {
		Gson g=new Gson();
		Set<WangYIParamForSignature> set =initBaseParam();
		WangYIParamForSignature infoid = new WangYIParamForSignature("infoid", "D603QKKA00018AOR");
		WangYIParamForSignature producer = new WangYIParamForSignature("producer", "recommendation");
		set.add(infoid);
		set.add(producer);
		String singnature = makeSignature(secretkey, set);
		System.out.println(singnature);
		return set;
	}
	
	
	private static Set<WangYIParamForSignature> installNewList() {
		Gson g=new Gson();
		Set<WangYIParamForSignature> set =initBaseParam();
		WangYIParamForSignature scene = new WangYIParamForSignature("scene", "f");
		WangYIParamForSignature channelid = new WangYIParamForSignature("channelid", "1853");
		WangYIParamForSignature num = new WangYIParamForSignature("num", 20+"");
		WangYIParamForSignature userid = new WangYIParamForSignature("userid", "575042c1eac22112dcab5620179035829b878a91");
		set.add(channelid);
		set.add(scene);
		set.add(num);
		set.add(userid);
		String singnature = makeSignature(secretkey, set);
		System.out.println(singnature);
		return set;
	}
	
	
	
	private static void installChannel() {
		
		
		Gson g=new Gson();
		Set<WangYIParamForSignature> set =initBaseParam();
		
		System.out.println(g.toJson(set));
		String singnature = makeSignature(secretkey, set);
		
	    System.out.println("installChannel:"+singnature);
	}
	
	private static String makeSignature(String secretkey,Set<WangYIParamForSignature> set) {
		StringBuilder sb = new StringBuilder(secretkey);
		for(WangYIParamForSignature w: set) {
			sb.append(w.getKeyName()).append(w.getValue());
		}
		String params =sb.toString();
		System.out.println(params);
		byte[] bytes=params.getBytes();
		MessageDigest md5=null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] md5bytes = md5.digest(bytes);
		String result = DatatypeConverter.printHexBinary(md5bytes).toLowerCase();
		return result;
	}
}
