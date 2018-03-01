package com.bus.chelaile.push.pushModel;



import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.model.Platform;
public class PushMessageBody {

	public static JSONObject getMessageBody(int type, String content, String pushKey, Platform platform,String title){
		 if (platform == Platform.IOS || platform == Platform.IOSJG) {
			 return getIosMessage(type, content, pushKey);
		 }else	if( platform == Platform.ANDROID || platform == Platform.GT || platform == Platform.YM  || platform == Platform.JG){
			 return getAndroidMessage(type, content, title, pushKey);
		 }
		 throw new IllegalArgumentException("没有找到要推送的类型:"+type);
	}
	
	private static JSONObject getIosMessage(int type, String content,
			String pushKey) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("loc-key", content);
		jsonObject.put("a", pushKey);
		jsonObject.put("type", type);
		jsonObject.put("st", 2);	//子类型   1:话题首页，2:未读消息页面，3:话题发布页面,现在给的是默认2
		return jsonObject;
	}
	
	private static JSONObject getAndroidMessage(int type, String content,String title,
			String pushKey) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", type);
		jsonObject.put("title", title);
		jsonObject.put("message", content);
		jsonObject.put("push_key", pushKey);
		jsonObject.put("subtype", 2);//子类型  1:话题首页，2:未读消息页面，3:话题发布页面,现在给的是默认2
		return jsonObject;
	}

}
