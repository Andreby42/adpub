package com.bus.chelaile.flow.uc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.flow.model.Thumbnail;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.config.PropertiesUtils;

public class ArticleResponseData {
	
	private static String imageDiv = "<div class=\"img-container\"><img src=\"%s\" alt=\"\"> </div>";
	private static final String LINKURL = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "myUClinkURL",
			"http://api.chelaile.net.cn:7000/adpub/adv!articlesPage.action?articleId=%s");
//	private static final String LINKURL = "http://test.chelaile.net.cn:7000/adpub/adv!articlesPage.action?articleId=%s";

	private String title;
	private long grab_time ;
	private String content;
	private String origin_src_name;
	private ArrayList<Thumbnail> images;
	private ArrayList<Thumbnail> thumbnails;

	private double[] imgRtio;
	private String imgRtioStr;	//图片比例
	
	private String id; //文章id
	private int item_type; //文章类型，目前只取0 和 1

	/*
	 * 处理文章内容
	 */
	public String handleContent() {
		if(images.size() == 0) {
			return content;
		}
		int size = images.size();
		for(int i = 0; i < size; i ++) {
			String strRe = "<!--{img:" + i + "}-->";
			content =  StringUtils.replace(content, strRe, String.format(imageDiv, images.get(i).getUrl()));
		}
		
		return content;
	}
	
	/*
	 * 处理图片大小比例
	 */
	public String handleImgRtio() {
		if(images.size() ==0) {
			return null;
		}
		imgRtioStr = "[";
		int size = images.size();
		imgRtio = new double[size];
		for(int i = 0; i < size; i ++) {
			imgRtio[i] = (double)(images.get(i).getHeight()) / (double)(images.get(i).getWidth());
			if(i == size -1) {
				imgRtioStr += imgRtio[i] + "]";
				break;
			}
			imgRtioStr += imgRtio[i] + ",";
		}
		
		return imgRtioStr;
	}
	
	/*
	 * 处理 分享描述
	 */
	public String handlShareDesc() {
		int longest = 30;	//最长的描述长度
		if(this.content.indexOf("<p>") != -1) {
			String buf[] = this.content.split("p>");
			String firstP = buf[1].replace("</", "");	//第一个段落
			if(firstP.indexOf("<strong>") != -1) {
				firstP = firstP.replace("strong>", "").replace("<", "");
			}
			int firstL = firstP.length();
			if(firstP.length() >= longest) {
				return firstP.substring(0, longest -1 );
			}
			
			if(buf.length >= 4) {
				String secondP = buf[3].replace("</", "");	//第二个段落
				if(secondP.indexOf("<strong>") != -1) {
					secondP = secondP.replace("strong>", "").replace("<", "");
				}
				int secondL = secondP.length();
				if(firstL + secondL >= longest) {
					return firstP + "\n" + secondP.substring(0, longest -1  - firstL);
				} else {
					return firstP + "\n" + secondP;
				}
			} else {
				return firstP;
			}
		}
		return "this.title";	//如果获取内容失败，直接返回title
	}
	
	/*
	 * 处理 相关推荐文章  的链接
	 * 
	 * 增加参数refer，标注进入页面的方式（直接进入||从相关推荐进入)
	 */
	public String handleUrl(AdvParam advParam) {
		HashMap<String, String> paramsMap = New.hashMap();
		paramsMap.put("udid", advParam.getUdid());
		paramsMap.put("s", advParam.getS());
		paramsMap.put("cityId", advParam.getCityId());
		paramsMap.put("v", advParam.getV());
		paramsMap.put("wse", "1");
		paramsMap.put("linkRefer", "recommd");
		
		StringBuilder sb = new StringBuilder(String.format(LINKURL, this.id));
		
		if (paramsMap != null && paramsMap.size() > 0) {
            Set<Entry<String, String>> entrySet = paramsMap.entrySet();
            for (Entry<String, String> entry : entrySet) {
                String paramName = entry.getKey();
                String paramValue = entry.getValue();
                sb.append('&').append(paramName).append('=').append(paramValue);
            }
        }
		
		return sb.toString();
	}
	
	/*
	 * 处理  相关推荐缩略图
	 */
	public ArrayList<String> handleImgUrls() {
		if(thumbnails != null && thumbnails.size() > 0) {
			ArrayList<String> imgUrls = new ArrayList<String>();
			for(Thumbnail thumbnail : thumbnails) {
				imgUrls.add(thumbnail.getUrl());
			}
			return imgUrls;
		}
		return null;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public ArrayList<Thumbnail> getImages() {
		return images;
	}
	public void setImages(ArrayList<Thumbnail> images) {
		this.images = images;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public double[] getImgRtio() {
		return imgRtio;
	}

	public void setImgRtio(double[] imgRtio) {
		this.imgRtio = imgRtio;
	}

	public String getImgRtioStr() {
		return imgRtioStr;
	}

	public void setImgRtioStr(String imgRtioStr) {
		this.imgRtioStr = imgRtioStr;
	}

	public String getOrigin_src_name() {
		return origin_src_name;
	}

	public void setOrigin_src_name(String origin_src_name) {
		this.origin_src_name = origin_src_name;
	}

	public long getGrab_time() {
		return grab_time;
	}

	public void setGrab_time(long grab_time) {
		this.grab_time = grab_time;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<Thumbnail> getThumbnails() {
		return thumbnails;
	}

	public void setThumbnails(ArrayList<Thumbnail> thumbnails) {
		this.thumbnails = thumbnails;
	}

	public int getItem_type() {
		return item_type;
	}

	public void setItem_type(int item_type) {
		this.item_type = item_type;
	}
}
