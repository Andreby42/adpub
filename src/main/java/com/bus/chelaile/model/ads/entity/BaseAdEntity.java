package com.bus.chelaile.model.ads.entity;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.third.IfengAx.model.response.Ad;
import com.bus.chelaile.util.AdvUtil;
import com.bus.chelaile.util.New;

/**
 * 包含每个广告的通用的最基本的属性;
 * 
 * @author liujh
 * 
 */
public abstract class BaseAdEntity {
	public static final String EMPTY_STR = "";
	protected static final Logger logger = LoggerFactory.getLogger(BaseAdEntity.class);

	protected int id; // 广告的唯一ID
	protected int showType; // 广告类型： 0 双栏，1 单栏，2 首页 ，3 推送, 4 全屏， 5 线路详情广告
	protected String link = ""; // 广告链接，默认为空串，表示没有链接
	protected int openType; // 打开方式：0 APP内部打开；1 浏览器打开；默认值为0

	protected String unfoldMonitorLink = "";
	protected int targetType;
	protected String clickMonitorLink = "";
	protected int monitorType;  // 监控类型 ， admaster或者秒针监控或者同步监控
	private int type = 1; // 1：我们自己广告，2：调用广点通广告，3：调用第三方广告   // 客户端点击的时候用到这个参数
	private int apiType = 1; // 1 原生，目前只有1 
	private String provider_id = "1"; // 我们自己的广告
	
	private String wxMiniProId; // 小程序appId
	private String wxMiniProPath;
	
	@JSONField(serialize=false)  
	private int priority;
	@JSONField(serialize=false)  
    private int adWeight;    // 轮播权重
	@JSONField(serialize=false)
	private int clickDown;   // 点击后排序到最后 ，用于第三方
	@JSONField(serialize=false)
	private TasksGroup tasksGroup; // 任务单 
	
	private int displayType = 2;// 1左⽂右图⼤ 2 左⽂右图小 3 三图 4 单图， 5 右侧单图（跳转信息流，没有‘广告’字样） ,2018-06-19 增加，满足三图片要求

	private List<String> picsList;
	
	private String placementId;
	
	
	public BaseAdEntity(int showType) {
		this.showType = showType;
	}

	public void dealUrl(AdvParam advParam, int monitorType) {
		// 秒针
		// if (monitorType == 1) {
		// link = SecondHand.replaceSecondUrl(link, param);
		// unfoldMonitorLink = SecondHand.replaceSecondUrl(
		// unfoldMonitorLink, param);
		// clickMonitorLink = SecondHand.replaceSecondUrl(
		// clickMonitorLink, param);
		// } else if (monitorType == 2) {
		// link = AdMaster.adMasterReplace(link, param);
		// unfoldMonitorLink = AdMaster.adMasterReplace(
		// unfoldMonitorLink, param);
		// clickMonitorLink = AdMaster.adMasterReplace(
		// clickMonitorLink, param);
		// }

		if (monitorType == 3) { // 同步监控类型
			link = creatMonitorUrl(link, advParam);
			logger.info("monitorType=3, link={}", link);
			// TODO 这个逻辑设定有问题，历史原因导致的。应该是某一次广告给的同步监控链接附加在 落地页link里面了
			this.monitorType = 3; //设置广告的监控类型为0，否则客户端监测监控链接为空，与这里的监控类型3不符合。结果无法发送展示埋点
			if(StringUtils.isBlank(clickMonitorLink) && StringUtils.isBlank(unfoldMonitorLink)) {
				this.monitorType = 0;
			}
		}

	}

	public String getPicUrl(String platform, String iosUrl, String androidUrl, String picUrl) {
		String url = "";
		if (platform.equalsIgnoreCase(Platform.ANDROID.getValue())) {
			url = androidUrl;
		} else {
			url = iosUrl;
		}
		if (url == null || url.equals("")) {
			url = picUrl;
		}
		return url;
	}

	public void fillBaseInfo(AdContent ad, AdvParam advParam, Map<String, String> paramMap) {
		if (ad == null || advParam == null) {
			return;
		}

		this.id = ad.getId();
		this.openType = ad.getOpenType();
		this.targetType = ad.getTargetType();

		fillLink(ad, advParam, paramMap);

		monitorType = ad.getMonitorType();
		unfoldMonitorLink = ad.getUnfoldMonitorLink();
		clickMonitorLink = ad.getClickMonitorLink();
		
		// monitorlink handle
		if(ad.getMonitorType() == 3) {
			dealUrl(advParam, ad.getMonitorType());
		}
		if(unfoldMonitorLink != null) {
			if(ad.getMonitorType() ==3) {
				unfoldMonitorLink = creatMonitorUrl(unfoldMonitorLink, advParam);
			} else {
				unfoldMonitorLink = AdvUtil.encodeUrl(unfoldMonitorLink);
			}
		} else {
			unfoldMonitorLink = EMPTY_STR;
		}
		
		if(clickMonitorLink != null) {
			if(ad.getMonitorType() ==3) {
				clickMonitorLink = creatMonitorUrl(clickMonitorLink, advParam);
			} else {
				clickMonitorLink = AdvUtil.encodeUrl(clickMonitorLink);
			}
		} else {
			clickMonitorLink = EMPTY_STR;
		}
		
//		if (unfoldMonitorLink == null) {
//			unfoldMonitorLink = EMPTY_STR;
//		}
//		if (clickMonitorLink == null) {
//			clickMonitorLink = EMPTY_STR;
//		}
//
//		if(ad.getMonitorType() == 3) {
////			dealUrl(advParam, ad.getMonitorType());
//			unfoldMonitorLink = AdvUtil.encodeUrl(creatMonitorUrl(unfoldMonitorLink, advParam));
//			clickMonitorLink = AdvUtil.encodeUrl(creatMonitorUrl(clickMonitorLink, advParam));
////			unfoldMonitorLink = creatMonitorUrl(unfoldMonitorLink, advParam);
////			clickMonitorLink = creatMonitorUrl(clickMonitorLink, advParam);
//		} else {
//			unfoldMonitorLink = AdvUtil.encodeUrl(unfoldMonitorLink);
//			clickMonitorLink = AdvUtil.encodeUrl(clickMonitorLink);
//		}
	}

	/**
	 * 处理link中的通配符
	 * 
	 * @param res
	 * @param advParam
	 */
	public void dealLink(AdvParam advParam) {
		if (link == null || link.equals("")) {
			return;
		}
		String key = "lineId";

		if (link.indexOf(key) == -1) {
			return;
		}

		// 如果有通配符,但是lineid为空,return null
		if (link.indexOf(key) != -1 && StringUtils.isEmpty(advParam.getLineId())) {
			throw new IllegalArgumentException("lineId为空");
		}

		if (link.indexOf("%25lineId%25") != -1) {
			link = link.replace("%25lineId%25", advParam.getLineId());
		}
		if (link.indexOf("%25lineName%25") != -1) {
			if (advParam.getLineName() == null) {
				throw new IllegalArgumentException("lineName为空");
			}
			link = link.replace("%25lineName%25", advParam.getLineName());
		}

		if (link.indexOf("%25lineNo%25") != -1) {
			if (advParam.getLineNo() == null) {
				throw new IllegalArgumentException("lineNo为空");
			}
			link = link.replace("%25lineNo%25", advParam.getLineNo());
		}
	}

	protected void fillLink(AdContent ad, AdvParam advParam, Map<String, String> param) {
//	    /**
//	     * 2018-09-20，修改。从此广告不再走中转链接了
//	     */
//	    link = AdvUtil.buildNewRedirectLink(ad.getLink(), advParam);
	    
		param.put("udid", advParam.getUdid());
		if (advParam.getS().equalsIgnoreCase(Platform.IOS.getDisplay()) && ad.getOpenType() == 1) { // IOS
			param.put(Constants.PARAM_DEVICE, advParam.getDeviceType());
			param.put(Constants.PARAM_LNG, advParam.getLng() + "");
			param.put(Constants.PARAM_LAT, advParam.getLat() + "");
			param.put(Constants.PARAM_NW, advParam.getNw());

			param.put(Constants.PARAM_IP, advParam.getIp());
			param.put(Constants.PARAM_IMEI, advParam.getImei());
			param.put(Constants.PARAM_IDFA, advParam.getIdfa());
			param.put(Constants.PARAM_UA, URLEncoder.encode(advParam.getUa()));
			param.put(Constants.PARAM_S, advParam.getS());
			param.put(Constants.PARAM_ANDROID, advParam.getAndroidID());
			param.put(Constants.PARAM_MAC, advParam.getMac());
		}
		//站点广告，加上站点的经纬度
		if(ShowType.STATION_ADV.getType().equals(ad.getShowType()) && StringUtils.isNoneBlank(advParam.getStnName())) {
			param.put(Constants.PARAM_STATION_LNG, advParam.getStnLng());
			param.put(Constants.PARAM_STATION_LAT, advParam.getStnLat());
			param.put(Constants.PARAM_STATION_NAME_H5, URLEncoder.encode(advParam.getStnName()));
		}
		link = AdvUtil.buildRedirectLink(ad.getLink(), genLinkParamMap(ad, param), advParam.getUdid(), false, true,
				ad.getLink_extra());
	}

	protected Map<String, String> genLinkParamMap(AdContent ad, Map<String, String> param) {
		// 默认的需要添加的参数
		param.put(Constants.PARAM_AD_ID, String.valueOf(ad.getId()));
		param.put(Constants.PARAM_AD_TYPE, gainShowTypeEnum().getType());
		// if (ad.getShowType().equals(ShowType.RIDE_DETAIL.getType())) {
		// param.put(Constants.PARAM_AD_TYPE, ShowType.RIDE_DETAIL.getType());
		// }

		return param;
	}

	private String creatMonitorUrl(String link, AdvParam advParam) {
		String url = link;
//		logger.info("替换前：udid={}, link={}", advParam.getUdid(), url);
		try {
			String platform = advParam.getS();
			String udid = advParam.getUdid();
			String imei = advParam.getImei();
			String idfa = advParam.getIdfa();
			String ua = advParam.getUa();
			String AndroidID = advParam.getAndroidID();
			String mac = advParam.getMac();
			String ip = advParam.getIp();

			String os = "0";
			if (platform.equalsIgnoreCase(Platform.IOS.getDisplay())) {
				os = "1";
			}
			url = url.replace("__OS__", os);
			if (ip == null) {
				logger.error("监控链接替换时 ip 为空, parameterMap={}, ip={}", JSONObject.toJSONString(advParam), ip);
				// return; //TODO
			} else {
				url = url.replace("__IP__", ip);
			}
			if (os.equals("0")) { // android
				if (imei != null)
//					url = url.replace("__IMEI__", DigestUtils.md5Hex(imei));
				url = url.replace("__IMEI__", imei);
				if (AndroidID != null)
					url = url.replace("__AndroidID__", DigestUtils.md5Hex(AndroidID));
			} else {
				if (idfa != null && !idfa.equals("00000000-0000-0000-0000-000000000000")) {
					url = url.replace("__IDFA__", idfa);
				}
//				url = url.replace("__OpenUDID__", udid);
			}
			url = url.replace("__UDID__", udid);

			if (mac != null) {
				mac = DigestUtils.md5Hex(mac.replace(":", "").toUpperCase());
				url = url.replace("__MAC__", mac);
			}
			if (ua != null) {
				url = url.replace("__UA__", AdvUtil.encodeUrl(ua));		 // UA需要做一下encode
			}
			 url = url.replace("__TS__", System.currentTimeMillis() + "");
//			logger.info("替换前：udid={}, link={}", advParam.getUdid(), url);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("广告监控出错! udid={}, url={}", advParam.getUdid(), link);
			return url;
		}
		return url;
	}

	public void buildIfendAxEntity(Ad ad) {
	    this.setLink(ad.getCreative().getStatics().getCurl());
        List<String> picList = New.arrayList();
        picList.add(ad.getCreative().getStatics().getAurl().get(0));
        this.setPicsList(picList);

        String clickMonitorLink = "";
        String unfoldMonitorLink = "";

        for (String s : ad.getCreative().getStatics().getAcurl()) {
            clickMonitorLink += s + ";";
        }
        if (clickMonitorLink.length() > 0) {
            this.monitorType = 3;
            clickMonitorLink = clickMonitorLink.substring(0, clickMonitorLink.length() - 1);
        }
        for (String s : ad.getCreative().getStatics().getMurl()) {
            unfoldMonitorLink += s + ";";
        }
        if (unfoldMonitorLink.length() > 0) {
            this.monitorType = 3;
            unfoldMonitorLink = unfoldMonitorLink.substring(0, unfoldMonitorLink.length() - 1);
        }
        this.setClickMonitorLink(clickMonitorLink);
        this.setUnfoldMonitorLink(unfoldMonitorLink);
    }
    
	protected abstract ShowType gainShowTypeEnum();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getShowType() {
		return showType;
	}

	public void setShowType(int showType) {
		this.showType = showType;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getOpenType() {
		return openType;
	}

	public void setOpenType(int openType) {
		this.openType = openType;
	}

	public int getTargetType() {
		return targetType;
	}

	public void setTargetType(int targetType) {
		this.targetType = targetType;
	}

	public String getUnfoldMonitorLink() {
		return unfoldMonitorLink;
	}

	public void setUnfoldMonitorLink(String unfoldMonitorLink) {
		this.unfoldMonitorLink = unfoldMonitorLink;
	}

	public String getClickMonitorLink() {
		return clickMonitorLink;
	}

	public void setClickMonitorLink(String clickMonitorLink) {
		this.clickMonitorLink = clickMonitorLink;
	}

	public int getMonitorType() {
		return monitorType;
	}

	public void setMonitorType(int monitorType) {
		this.monitorType = monitorType;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getProvider_id() {
		return provider_id;
	}

	public void setProvider_id(String provider_id) {
		this.provider_id = provider_id;
	}

	public static void main(String[] args) {
		String url = "www.baidu.com?ua=__UA__&imei=__IMEI__&ua1=__UA__";
		String ua = "111";
		url = url.replace("__UA__", ua);
		System.out.println(url);
		
		System.out.println(StringUtils.isBlank(null));
		System.out.println(StringUtils.isBlank(""));
		System.out.println(StringUtils.isBlank(" "));
		
		System.out.println(StringUtils.isEmpty(null));
		System.out.println(StringUtils.isEmpty(""));
		System.out.println(StringUtils.isEmpty(" "));
		
		
		String ua1 = "Mozilla/5.0 (Linux; Android 8.0.0; MI 6 Build/OPR1.170623.027; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/62.0.3202.84 Mobile Safari/537.36";
		System.out.println(AdvUtil.encodeUrl(ua1));
		System.out.println(AdvUtil.encodeUrl(AdvUtil.encodeUrl(ua1)));
		System.out.println(AdvUtil.encodeUrl(AdvUtil.encodeUrl(AdvUtil.encodeUrl(ua1))));
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

    /**
     * @return the adWeight
     */
    public int getAdWeight() {
        return adWeight;
    }

    /**
     * @param adWeight the adWeight to set
     */
    public void setAdWeight(int adWeight) {
        this.adWeight = adWeight;
    }

    /**
     * @return the apiType
     */
    public int getApiType() {
        return apiType;
    }

    /**
     * @param apiType the apiType to set
     */
    public void setApiType(int apiType) {
        this.apiType = apiType;
    }

    public int getClickDown() {
        return clickDown;
    }

    /**
     * @param clickDown the clickDown to set
     */
    public void setClickDown(int clickDown) {
        this.clickDown = clickDown;
    }

    /**
     * @return the tasksGroup
     */
    public TasksGroup getTasksGroup() {
        return tasksGroup;
    }

    /**
     * @param tasksGroup the tasksGroup to set
     */
    public void setTasksGroup(TasksGroup tasksGroup) {
        this.tasksGroup = tasksGroup;
    }

	public int getDisplayType() {
		return displayType;
	}

	public void setDisplayType(int displayType) {
		this.displayType = displayType;
	}

	public List<String> getPicsList() {
		return picsList;
	}

	public void setPicsList(List<String> picsList) {
		this.picsList = picsList;
	}

	public String getPlacementId() {
		return placementId;
	}

	public void setPlacementId(String placementId) {
		this.placementId = placementId;
	}

    public String getWxMiniProId() {
        return wxMiniProId;
    }

    public void setWxMiniProId(String wxMiniProId) {
        this.wxMiniProId = wxMiniProId;
    }

    public String getWxMiniProPath() {
        return wxMiniProPath;
    }

    public void setWxMiniProPath(String wxMiniProPath) {
        this.wxMiniProPath = wxMiniProPath;
    }
    
    
}
