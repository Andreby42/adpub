package com.bus.chelaile.strategy;







/*
 * 参数adType决定了广告所属的大类。
 * 1 -> 自采买广告
 * 2 -> 广点通广告
 * 3 -> InMobi广告
 * 4 -> Adwo广告（暂时没上线）
 * 5 -> 百度
 * 6 -> 阿里妈妈
 * 7 -> LinkActive
 * 8 ->阅盟
 * 9 ->Ecoook
 * 10 ->科大讯飞
 * 参数apiType决定使用第三方广告时（即adType != 1）需要具体请求哪种广告,前三个一组,针对线路详情的处理
 * 1 -> 原生广告 (Native)
 * 2 -> 横幅广告 (Banner)
 * 3 -> 纯图片                              				
 * 4 -> 开屏
 * 5 -> 浮层
 */


public class AdCategory {
	private int adId;
	private int adType;
	private int apiType;

	public AdCategory(int adId, int adType, int apiType) {
		this.adId = adId;
		this.adType = adType;
		this.apiType = apiType;
	}

	public AdCategory() {

	}

	private String getAdTypeName()
	{
		switch (this.adType)
		{
			case 1:
				return "Own";
			case 2:
				return "GDT";
			case 3:
				return "InMobi";
			case 4:
				return "Adwo";
			case 5:
				return "Baidu";
			case 6:
				return "Alimama";
			case 7:
				return "LinkActive";
			case 8:
                return "YueMeng";
			case 9:
                return "Ecoook";
			case 10:
                return "KeDaXunFei";
		}
		return "Error_Ad_Type";
	}

	private String getApiTypeName()
	{
		switch (this.apiType)
		{
			case 1:
				return "Native";
			case 2:
				return "Banner";
			case 3:
				return "Picture";
			case 4:
				return "Start_Screen";
		}
		return "Error_API_Type";
	}

	@Override
	public String toString()
	{
		if (this.adType == 1) {
			return Integer.toString(this.adId);
		}
		return String.format("%s.%s", getAdTypeName(), getApiTypeName());
	}

	public boolean equals(Object o)
	{
		if (o == this) {
			return true;
		}
		if (!(o instanceof AdCategory)) {
			return false;
		}
		AdCategory other = (AdCategory)o;
		if (!other.canEqual(this)) {
			return false;
		}
		if (getAdId() != other.getAdId()) {
			return false;
		}
		if (getAdType() != other.getAdType()) {
			return false;
		}
		return getApiType() == other.getApiType();
	}

	private boolean canEqual(Object other)
	{
		return other instanceof AdCategory;
	}

	public int hashCode()
	{
		int PRIME = 59;
		int result = 1;
		result = result * PRIME + getAdId();
		result = result * PRIME + getAdType();
		result = result * PRIME + getApiType();
		return result;
	}
	public int getAdId() {
		return adId;
	}

	public void setAdId(int adId) {
		this.adId = adId;
	}

	public int getAdType() {
		return adType;
	}

	public void setAdType(int adType) {
		this.adType = adType;
	}

	public int getApiType() {
		return apiType;
	}

	public void setApiType(int apiType) {
		this.apiType = apiType;
	}
	
	
}
