package com.bus.chelaile.innob.response.ad;




import java.io.IOException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Decoder;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.util.New;

/**
 * Created by Administrator on 2016/8/9.
 */

public class NativeResponse {
	
	protected static final Logger logger = LoggerFactory
			.getLogger(NativeResponse.class);

    private static class Ad {
        private String pubContent;
        private String landingPage;
//        private String beaconUrl;
        
        private Map<String, Map<String, ArrayList<String>>> eventTracking;

		public String getPubContent() {
			return pubContent;
		}

		public void setPubContent(String pubContent) {
			this.pubContent = pubContent;
		}

		public String getLandingPage() {
			return landingPage;
		}

		public void setLandingPage(String landingPage) {
			this.landingPage = landingPage;
		}

//		public String getBeaconUrl() {
//			return beaconUrl;
//		}
//
//		public void setBeaconUrl(String beaconUrl) {
//			this.beaconUrl = beaconUrl;
//		}

		public Map<String, Map<String, ArrayList<String>>> getEventTracking() {
			return eventTracking;
		}

		public void setEventTracking(
				Map<String, Map<String, ArrayList<String>>> eventTracking) {
			this.eventTracking = eventTracking;
		}
        
        
    }
    private ArrayList<Ad> ads;
    private String requestId;
    private List<DecodedAd> decodeAdList = New.arrayList();


    public static class DecodedAd {
        private String title;
//        private String description;
//        private Icon icon;
        private Icon screenshots;
        private String landingURL;
        private String cta;

        
       
        public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

//		public String getDescription() {
//			return description;
//		}
//
//		public void setDescription(String description) {
//			this.description = description;
//		}

//		public Icon getIcon() {
//			return icon;
//		}
//
//		public void setIcon(Icon icon) {
//			this.icon = icon;
//		}

		public Icon getScreenshots() {
			return screenshots;
		}

		public void setScreenshots(Icon screenshots) {
			this.screenshots = screenshots;
		}

		public String getLandingURL() {
			return landingURL;
		}

		public void setLandingURL(String landingURL) {
			this.landingURL = landingURL;
		}

		public String getCta() {
			return cta;
		}

		public void setCta(String cta) {
			this.cta = cta;
		}

		public static class Icon {
            private int width;
            private int height;
            private String url;
            private double aspectRatio;
			public int getWidth() {
				return width;
			}
			public void setWidth(int width) {
				this.width = width;
			}
			public int getHeight() {
				return height;
			}
			public void setHeight(int height) {
				this.height = height;
			}
			public String getUrl() {
				return url;
			}
			public void setUrl(String url) {
				this.url = url;
			}
			public double getAspectRatio() {
				return aspectRatio;
			}
			public void setAspectRatio(double aspectRatio) {
				this.aspectRatio = aspectRatio;
			}
            
            
        }

        public void print() {
            System.out.println("title: " + title);
//            System.out.println("description: " + description);
//            System.out.println("icon width: " + getIcon().getWidth());
//            System.out.println("icon height: " + getIcon().getHeight());
//            System.out.println("icon url: " + getIcon().getUrl());
//            System.out.println("icon aspect ratio: " + getIcon().getAspectRatio());
            System.out.println("Screenshots is empty: " + getScreenshots() == null);
            System.out.println("landing url: " + landingURL);
            System.out.println("cta: " + cta);
        }
    }

//    public void print() {
//        System.out.println("pubContent: " + getPubContent());
//        System.out.println("landingPage: " + getLandingPage());
//        System.out.println("beaconUrl: " + getBeaconUrl());
//        System.out.println("Tracking url 1: ");
//        ArrayList<String> urls = getEventTracking1Urls();
//        for (String s : urls)
//            System.out.println(s);
//        System.out.println("Tracking url 8: ");
//        urls = getEventTracking8Urls();
//        for (String s : urls)
//            System.out.println(s);
//        System.out.println("Tracking url 18: ");
//        urls = getEventTracking18Urls();
//        for (String s : urls)
//            System.out.println(s);
//        System.out.println("Tracking url 120: ");
//        urls = getEventTracking120Urls();
//        for (String s : urls)
//            System.out.println(s);
//        System.out.println("Request Id: " + getRequestId());
//    }

    public String getPubContent(int pos) {
        return ads.get(pos).getPubContent();
    }

    public String getLandingPage(int pos) {
        return ads.get(pos).getLandingPage();
    }

//    public String getBeaconUrl(int pos) {
//        return ads.get(pos).getBeaconUrl();
//    }

    public ArrayList<String> getEventTracking1Urls(int pos) {
        ArrayList<String> urls1 = ads.get(pos).getEventTracking().get("1").get("urls");
        replaceTSMacro(urls1);
        return urls1;
    }

    public ArrayList<String> getEventTracking8Urls(int pos) {
        ArrayList<String> urls8 = ads.get(pos).getEventTracking().get("8").get("urls");
        replaceTSMacro(urls8);
        return urls8;
    }

    public ArrayList<String> getEventTracking18Urls(int pos) {
        ArrayList<String> urls18 = ads.get(pos).getEventTracking().get("18").get("urls");
        replaceTSMacro(urls18);
        return urls18;
    }

    public ArrayList<String> getEventTracking120Urls(int pos) {
        ArrayList<String> urls120 = ads.get(pos).getEventTracking().get("120").get("urls");
        replaceTSMacro(urls120);
        return urls120;
    }

    private void replaceTSMacro(ArrayList<String> urls) {
        for (int i = 0; i < urls.size(); i++) {
            String originalUrl = urls.get(i);
            if (originalUrl.startsWith("http://et.w.inmobi.com") || originalUrl.startsWith("http://c.w.inmobi.com") ||
                originalUrl.startsWith("https://et.w.inmobi.com") || originalUrl.startsWith("https://c.w.inmobi.com")) {
                BigDecimal bd = new BigDecimal(System.currentTimeMillis());
                String millisecondsStr = bd.toPlainString();
                int index = originalUrl.lastIndexOf("$TS");
                if (index >= 0) {
                    String filledStr = new StringBuilder(originalUrl).
                            replace(index, index + 3, millisecondsStr).
                            toString();
                    urls.set(i, filledStr);
                }
            }
        }
    }

    public DecodedAd getDecodedAd(int pos) throws IOException {
    	return decodeAdList.get(pos);
    }
    
    public void setDecodeAd(){
//    	if( ads == null || ads.size() == 0 ){
//    		logger.error("inmobe返回ads为空");
//    		return;
////    		throw new IllegalArgumentException("返回的ads为空"); // 错误太多，不予记录详情了
//    	}
        for( Ad ad : ads ){
        	DecodedAd obj = convertJson( ad.getPubContent() );
        	if( obj != null ){
        		if( 
//        				obj.getDescription() == null || obj.getDescription().equals("") || 
        				obj.getTitle() == null || obj.getTitle().equals("") 
        			){
        			logger.info("innobe标题或者描述为空:"+ad.getPubContent());
        			continue;
        		}
        		decodeAdList.add(obj);
        	}
        	ad.pubContent = null;
        }
        
    }
    
    private DecodedAd convertJson(String json){
        BASE64Decoder decoder = new BASE64Decoder();
        DecodedAd decodedAd = null;
        try {
            String originalAdJsonString = new String(decoder.decodeBuffer(json));

            decodedAd = JSON.parseObject(originalAdJsonString, DecodedAd.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return decodedAd;
    }

	public ArrayList<Ad> getAds() {
		return ads;
	}

	public void setAds(ArrayList<Ad> ads) {
		this.ads = ads;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public List<DecodedAd> getDecodeAdList() {
		return decodeAdList;
	}

	public void setDecodeAdList(List<DecodedAd> decodeAdList) {
		this.decodeAdList = decodeAdList;
	}
    
    
}
