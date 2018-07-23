package com.bus.chelaile.mvc.utils;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.entity.TaskEntity;
import com.bus.chelaile.service.StaticAds;
import com.bus.chelaile.util.New;

/**
 * js文件处理
 * @author linzi
 *
 */
public class JSFileHandle {

    private static final Logger logger = LoggerFactory.getLogger(JSFileHandle.class);
    
    
    public static String replaceJs(String platform, String splashJS, ShowType showType, TaskEntity tgs, String tag) {

        Map<String, String> map = null;
        if (tgs != null && tgs.getTaskGroups() != null && tgs.getTaskGroups().getMap() != null) {
            map = tgs.getTaskGroups().getMap();
        } else {
            map = New.hashMap();
        }
        for (Entry<String, String> entry : map.entrySet()) {
            String displayType = entry.getValue();
            String aid = entry.getKey();

            String placementId = getPlaceMentId(platform, showType, aid, displayType);
            String placementReplaceKey = "${" + aid + "_placementId}";
            String displayTypeReplaceKey = "\"${" + aid + "_displayType}\"";
            String aidReplaceKey = "${" + aid + "_aid}";

            logger.info("**placementId={}, placementReplaceKey={}", placementId, placementReplaceKey);
            if(StringUtils.isNoneBlank(placementId)) {
                splashJS = splashJS.replace(placementReplaceKey, placementId);
            } else {
                logger.info("error occured when replace placementId, placementid is null , platform={}, showType={}, aid={}, displayType={}", 
                        platform, showType, aid, displayType);
            }
            splashJS = splashJS.replace(displayTypeReplaceKey, displayType);
            splashJS = splashJS.replace(aidReplaceKey, displayType);
        }

        return splashJS;
    }
    
    public static void replaceNewJs(String platform, ShowType showType, TaskEntity tgs, String tag,Map<String, String> map) {

    	
    	Map<String, String> retMap = null;
        if (tgs != null && tgs.getTaskGroups() != null && tgs.getTaskGroups().getMap() != null) {
        	retMap = tgs.getTaskGroups().getMap(); 	
        }else {
        	retMap = New.hashMap();
        }
        for (Entry<String, String> entry : retMap.entrySet()) {
            String displayType = entry.getValue();
            String aid = entry.getKey();

            String placementId = getPlaceMentId(platform, showType, aid, displayType);
            String placementReplaceKey = aid + "_placementId";
            String displayTypeReplaceKey = aid + "_displayType";
            String aidReplaceKey = aid + "_aid";

            
            map.put(placementReplaceKey, placementId);
            
            map.put(displayTypeReplaceKey, displayType);
            map.put(aidReplaceKey, displayType);
        }
        addDefaultPlaceMentId(map, showType);

    }
    
    


   
	private static void addDefaultPlaceMentId(Map<String, String> map, ShowType showType) {
		// 这里要替换placementid,displayType 也要替换成默认值
		String sdk_gdt_placementId = null;
		String sdk_toutiao_placementId = null;
		String sdk_voicead_placementId = null;
		String sdk_baidu_placementId = null;

		String sdk_gdt_displayType = null;
		String sdk_toutiao_displayType = null;
		String sdk_voicead_displayType = null;
		String sdk_baidu_displayType = null;

		


		// 广点通
		sdk_gdt_displayType = map.get("sdk_gdt_displayType");
		if (sdk_gdt_displayType == null) {

			map.put("sdk_gdt_displayType", "2");
			sdk_gdt_placementId = getPlaceMentId(showType, "2", 2);
			map.put("sdk_gdt_placementId", sdk_gdt_placementId);
			map.put("sdk_gdt_aid", "2");
		}

		// 头条
		sdk_toutiao_displayType = map.get("sdk_toutiao_displayType");
		if (sdk_toutiao_displayType == null) {
			
			map.put("sdk_toutiao_displayType", "2");
			sdk_toutiao_placementId = getPlaceMentId(showType, "7", 2);
			map.put("sdk_toutiao_placementId", sdk_toutiao_placementId);
			map.put("sdk_toutiao_aid", "2");
		}

		// 科大讯飞
		sdk_voicead_displayType = map.get("sdk_voicead_displayType");
		if (sdk_voicead_displayType == null) {
			
			map.put("sdk_voicead_displayType", "2");
			sdk_voicead_placementId = getPlaceMentId(showType, "10", 2);
			map.put("sdk_voicead_placementId", sdk_voicead_placementId);
			map.put("sdk_voicead_aid", "2");
		}

		String sdk_ifly_displayType = map.get("sdk_ifly_displayType");
		if (sdk_ifly_displayType == null) {
		
			map.put("sdk_ifly_displayType", "2");
			sdk_voicead_placementId = getPlaceMentId(showType, "10", 2);
			map.put("sdk_ifly_aid", "2");
			map.put("sdk_ifly_placementId", sdk_voicead_placementId);
		}

		// baidu
		sdk_baidu_displayType = map.get("sdk_baidu_displayType");
		if (sdk_baidu_displayType == null) {
			
			map.put("sdk_baidu_displayType", "2");
			sdk_baidu_placementId = getPlaceMentId(showType, "5", 2);
			map.put("sdk_baidu_placementId", sdk_baidu_placementId);

			map.put("sdk_baidu_aid", "2");
		}

		// 科大讯飞api
		String api_voicead_displayType = map.get("api_voicead_displayType");
		if (api_voicead_displayType == null) {
		
			map.put("api_voicead_displayType", "2");
			String api_voicead_placementId = getApiPlaceMentId(showType, 2, "api_voicead");
			map.put("api_voicead_placementId", api_voicead_placementId);

			map.put("api_voicead_aid", "2");
		}

		// 有道api
		String api_yd_displayType = map.get("api_yd_displayType");
		if (api_yd_displayType == null) {
			
			map.put("api_yd_displayType", "2");
			map.put("api_yd_aid", "2");
		}

		// 舜飞api
		String api_shunfei_displayType = map.get("api_shunfei_displayType");
		if (api_shunfei_displayType == null) {
			
			map.put("api_shunfei_displayType", "2");
			map.put("api_shunfei_aid", "2");
		}

		// 众盟api
		String api_zm_displayType = map.get("api_zm_displayType");
		if (api_zm_displayType == null) {
		
			map.put("api_zm_displayType", "2");
			map.put("api_zm_aid", "2");
		}


;
	}

	private static String getApiPlaceMentId(ShowType showType, int displayType, String apiName) {
		String placeMentId = "111";

//		// 双栏
//		if (showType.getValue() == ShowType.DOUBLE_COLUMN.getValue()) {
//
//			// 科大讯飞
//			if (apiName.equals("api_voicead")) {
//				if (displayType == 3) {
//					placeMentId = "ACB0BED305BBA908DEA75B0036E51ECE";
//				} else {
//					placeMentId = "C23BFCFFE1F3D8D5C06D7E1AEEA83812";
//				}
//			}
//
//		}
//
//		// 详情页底部
//		else if (showType.getValue() == ShowType.LINE_FEED_ADV.getValue()) {
//
//			// 科大讯飞
//			if (apiName.equals("api_voicead")) {
//				if (displayType == 3) {
//					placeMentId = "FD95828C09A32D712082DC08D36CC15D";
//				} else {
//					placeMentId = "5CBF4E804C06EBF6EEAF93DC5EA6BBCF";
//				}
//			} else if (apiName.equals("api_yd")) {
//				placeMentId = "";
//			}
//
//		}
//
//		// 换乘
//		else if (showType.getValue() == ShowType.TRANSFER_ADV.getValue()) {
//
//			// 科大讯飞
//			if (apiName.equals("api_voicead")) {
//				if (displayType == 3) {
//					placeMentId = "AE9A21B49A87F9B6C67EC7548FB5DE48";
//				} else {
//					placeMentId = "A7ABF4CFA257C79F064A8A162266D924";
//				}
//			}
//			// 网易
//			else if (apiName.equals("api_yd")) {
//				placeMentId = "";
//			}
//
//		}
//
//		// 车辆所有线路
//		else if (showType.getValue() == ShowType.CAR_ALL_LINE_ADV.getValue()) {
//
//			// 科大讯飞
//			if (apiName.equals("api_voicead")) {
//				if (displayType == 3) {
//					placeMentId = "67842F384D42AE16958A2ADDC8080BA0";
//				} else {
//					placeMentId = "3D7818F4980FB323DAEA8B544B567B7C";
//				}
//			}
//			// 网易
//			else if (apiName.equals("api_yd")) {
//				placeMentId = "";
//			}
//
//		}
//		// 更多车辆
//		else if (showType.getValue() == ShowType.ALL_CAR_ADV.getValue()) {
//
//			// 科大讯飞
//			if (apiName.equals("api_voicead")) {
//				if (displayType == 3) {
//					placeMentId = "B7880FAC93B6C405F219E54DABBAD2A3";
//				} else {
//					placeMentId = "BEAD9E183E07E296B1855E2AB3F2E6ED";
//				}
//			}
//
//			// 网易
//			else if (apiName.equals("api_yd")) {
//				placeMentId = "";
//			}
//
//		}

		return placeMentId;
	}

	private static String getPlaceMentId(ShowType showType, String provider_id, int displayType) {

		String placeMentId = "111";

//		// 开屏
//		if (showType.getValue() == ShowType.OPEN_SCREEN.getValue()) {
//			// 广点通
//			if (provider_id.equals("2")) {
//				placeMentId = "7030038393106222";
//			}
//			// innobe
//			else if (provider_id.equals("3")) {
//				placeMentId = "";
//			}
//			// 今日头条
//			else if (provider_id.equals("7")) {
//				placeMentId = "800673832";
//			}
//			// 科大讯飞
//			else if (provider_id.equals("10")) {
//				placeMentId = "D028C0ADDDBC38952DA01241B4939E64";
//			}
//			// 网易
//			else if (provider_id.equals("11")) {
//				placeMentId = "";
//			}
//
//		}
//		// 双栏
//		else if (showType.getValue() == ShowType.DOUBLE_COLUMN.getValue()) {
//			// 广点通
//			if (provider_id.equals("2")) {
//				if (displayType == 3) {
//					placeMentId = "6020731595504836";
//				} else {
//					placeMentId = "2030539481050032";
//				}
//			}
//			// innobe
//			else if (provider_id.equals("3")) {
//				placeMentId = "";
//			}
//			// 今日头条
//			else if (provider_id.equals("7")) {
//				if (displayType == 3) {
//					placeMentId = "900673292";
//				} else {
//					placeMentId = "900673519";
//				}
//			}
//			// 科大讯飞
//			else if (provider_id.equals("10")) {
//				if (displayType == 3) {
//					placeMentId = "ACB0BED305BBA908DEA75B0036E51ECE";
//				} else {
//					placeMentId = "C23BFCFFE1F3D8D5C06D7E1AEEA83812";
//				}
//			}
//			// 网易
//			else if (provider_id.equals("11")) {
//				placeMentId = "";
//			}
//			// 百度
//			else if (provider_id.equals("5")) {
//				if (displayType == 3) {
//					placeMentId = "5847843";
//				} else {
//					placeMentId = "5826173";
//				}
//			}
//
//		}
//		// 站点
//		else if (showType.getValue() == ShowType.STATION_ADV.getValue()) {
//			// 广点通
//			if (provider_id.equals("2")) {
//				placeMentId = "6000631364333392";
//			}
//			// innobe
//			else if (provider_id.equals("3")) {
//				placeMentId = "";
//			}
//			// 今日头条
//			else if (provider_id.equals("7")) {
//				placeMentId = "900673616";
//			}
//			// 科大讯飞
//			else if (provider_id.equals("10")) {
//
//			}
//			// 网易
//			else if (provider_id.equals("11")) {
//				placeMentId = "";
//			}
//
//		}
//		// 详情页底部
//		else if (showType.getValue() == ShowType.LINE_FEED_ADV.getValue()) {
//			// 广点通
//			if (provider_id.equals("2")) {
//				if (displayType == 3) {
//					placeMentId = "9080635585600817";
//				} else {
//					placeMentId = "3040333351258521";
//				}
//			}
//			// innobe
//			else if (provider_id.equals("3")) {
//				placeMentId = "";
//			}
//			// 今日头条
//			else if (provider_id.equals("7")) {
//				if (displayType == 3) {
//					placeMentId = "900673814";
//				} else {
//					placeMentId = "900673326";
//				}
//			}
//			// 科大讯飞
//			else if (provider_id.equals("10")) {
//				if (displayType == 3) {
//					placeMentId = "FD95828C09A32D712082DC08D36CC15D";
//				} else {
//					placeMentId = "5CBF4E804C06EBF6EEAF93DC5EA6BBCF";
//				}
//			}
//			// 网易
//			else if (provider_id.equals("11")) {
//				placeMentId = "";
//			}
//			// 百度
//			else if (provider_id.equals("5")) {
//				if (displayType == 3) {
//					placeMentId = "5847849";
//				} else {
//					placeMentId = "5826174";
//				}
//			}
//
//		}
//
//		// 右上角
//		else if (showType.getValue() == ShowType.LINE_RIGHT_ADV.getValue()) {
//			// 广点通
//			if (provider_id.equals("2")) {
//				placeMentId = "4060239431859044";
//			}
//			// innobe
//			else if (provider_id.equals("3")) {
//				placeMentId = "";
//			}
//			// 今日头条
//			else if (provider_id.equals("7")) {
//				placeMentId = "900673291";
//			}
//			// 科大讯飞
//			else if (provider_id.equals("10")) {
//				placeMentId = "2EC979D4F845F81DD899B62F497E3F67";
//			}
//			// 网易
//			else if (provider_id.equals("11")) {
//				placeMentId = "";
//			}
//
//		}
//
//		// 换乘
//		else if (showType.getValue() == ShowType.TRANSFER_ADV.getValue()) {
//			// 广点通
//			if (provider_id.equals("2")) {
//				if (displayType == 3) {
//					placeMentId = "7090134575505819";
//				} else {
//					placeMentId = "3010534505808848";
//				}
//			}
//			// innobe
//			else if (provider_id.equals("3")) {
//				placeMentId = "";
//			}
//			// 今日头条
//			else if (provider_id.equals("7")) {
//				if (displayType == 3) {
//					placeMentId = "900673297";
//				} else {
//					placeMentId = "900673966";
//				}
//			}
//			// 科大讯飞
//			else if (provider_id.equals("10")) {
//				if (displayType == 3) {
//					placeMentId = "AE9A21B49A87F9B6C67EC7548FB5DE48";
//				} else {
//					placeMentId = "A7ABF4CFA257C79F064A8A162266D924";
//				}
//			}
//			// 网易
//			else if (provider_id.equals("11")) {
//				placeMentId = "";
//			}
//
//			// 百度
//			else if (provider_id.equals("5")) {
//				if (displayType == 3) {
//					placeMentId = "5847855";
//				} else if (displayType == 4) {
//					placeMentId = "5847852";
//				} else {
//					placeMentId = "5847852";
//				}
//			}
//
//		}
//
//		// 车辆所有线路
//		else if (showType.getValue() == ShowType.CAR_ALL_LINE_ADV.getValue()) {
//			// 广点通
//			if (provider_id.equals("2")) {
//				if (displayType == 3) {
//					placeMentId = "2080634575306901";
//				} else {
//					placeMentId = "5030836525008930";
//				}
//			}
//			// innobe
//			else if (provider_id.equals("3")) {
//				placeMentId = "";
//			}
//			// 今日头条
//			else if (provider_id.equals("7")) {
//				if (displayType == 3) {
//					placeMentId = "900673492";
//				} else {
//					placeMentId = "900673424";
//				}
//			}
//			// 科大讯飞
//			else if (provider_id.equals("10")) {
//				if (displayType == 3) {
//					placeMentId = "67842F384D42AE16958A2ADDC8080BA0";
//				} else {
//					placeMentId = "3D7818F4980FB323DAEA8B544B567B7C";
//				}
//			}
//			// 网易
//			else if (provider_id.equals("11")) {
//				placeMentId = "";
//			}
//
//			// 百度
//			else if (provider_id.equals("5")) {
//				if (displayType == 3) {
//					placeMentId = "5847859";
//				} else if (displayType == 4) {
//					placeMentId = "5847856";
//				} else {
//					placeMentId = "5847856";
//				}
//			}
//
//		}
//		// 更多车辆
//		else if (showType.getValue() == ShowType.ALL_CAR_ADV.getValue()) {
//			// 广点通
//			if (provider_id.equals("2")) {
//				if (displayType == 3) {
//					placeMentId = "4070030575903993";
//				} else {
//					placeMentId = "7000631515604932";
//				}
//			}
//			// innobe
//			else if (provider_id.equals("3")) {
//				placeMentId = "";
//			}
//			// 今日头条
//			else if (provider_id.equals("7")) {
//				if (displayType == 3) {
//					placeMentId = "900673101";
//				} else {
//					placeMentId = "900673512";
//				}
//			}
//			// 科大讯飞
//			else if (provider_id.equals("10")) {
//				if (displayType == 3) {
//					placeMentId = "BEAD9E183E07E296B1855E2AB3F2E6ED";
//				} else {
//					placeMentId = "B7880FAC93B6C405F219E54DABBAD2A3";
//				}
//			}
//
//			// 网易
//			else if (provider_id.equals("11")) {
//				placeMentId = "";
//			}
//			// 百度
//			else if (provider_id.equals("5")) {
//				if (displayType == 3) {
//					placeMentId = "5847867";
//				} else if (displayType == 4) {
//					placeMentId = "5847862";
//				} else {
//					placeMentId = "5847862";
//				}
//			}
//
//		}

		return placeMentId;
	}
    
    

    // 新的获取placementId的方式
    private static String getPlaceMentId(String platform, ShowType showType, String aid, String displayType) {
        // 目前 除了3以外，其他的都一样
        if (!displayType.equals("3"))
            displayType = "2";
        StringBuilder key = new StringBuilder(showType.getType()).append("_").append(aid).append("_").append(displayType);
        if (platform.equals("android")) {
            return StaticAds.androidPlacementMap.get(key.toString());
        } else {
            return StaticAds.iosPlacementMap.get(key.toString());
        }
    }
    
    public static void main(String[] args) {
        String a = "return \"${sdk_toutiao_displayType}\"";
        System.out.println(a.replace("'${sdk_toutiao_displayType}'", "1111"));
        
        String aid = "sdk_toutiao";
        String displayTypeReplaceKey = "\"${" + aid + "_displayType}\"";
        System.out.println(displayTypeReplaceKey);
        System.out.println(a.replace("\"${sdk_toutiao_displayType}\"", "1111"));
    }
}
