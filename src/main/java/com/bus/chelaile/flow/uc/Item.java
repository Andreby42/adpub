package com.bus.chelaile.flow.uc;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.flow.model.Thumbnail;
import com.bus.chelaile.flow.model.FlowContent;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.AdvUtil;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.config.PropertiesUtils;

/**
 * 
 * @author linzi
 *
 */
public class Item {
	
	private String id;
	private String title;
	private String recoid;	 //记录推荐批次
	private int item_type; //文章类型, 目前只支持 0  和  1
	private String url; //文章链接
	private long grab_time; //文章抓取入库时间
	private String origin_src_name; //文章初始来源
	private ArrayList<Thumbnail> thumbnails; //
	private int content_type; //文章类型  0: 普通正文, 1: 图集, 2: webview, 3:专题
	
	private static final String LINKURL = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "myUClinkURL",
			"http://api.chelaile.net.cn:7000/adpub/adv!articlesPage.action?articleId=%s");
//	private static final String LINKURL = "http://test.chelaile.net.cn:7000/adpub/adv!articlesPage.action?articleId=%s";
	
	/**
	 * 给url加前缀及相应的参数
	 * 增加参数refer，标注进入页面的方式（直接进入||从相关推荐进入)
	 * @param advParam
	 * @return
	 */
	public String delUrl(AdvParam advParam) {
		// 给用户id，线路信息
		// 其他信息可以加入到第二个参数中
		HashMap<String, String> paramsMap = New.hashMap();
		if(StringUtils.isNoneBlank(advParam.getLineId())) {
			try {
				paramsMap.put("lineId", URLEncoder.encode(advParam.getLineId(), "utf-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Platform platform = Platform.from(advParam.getS()); 			//app的版本判断,ios 5.26.0之前(包含) 不走自定义页面
		if (platform.isIOS(platform.getDisplay()) && advParam.getVc() <= 10310) {
			paramsMap.put("udid", advParam.getUdid());
			String urlHttps = this.url;
			if(! this.url.contains("https")) {
				 urlHttps = this.url.replace("http", "https");	//  改成https
			}
			urlHttps += "&wse=1";
			urlHttps += "&linkRefer=direct";
			return AdvUtil.buildRedirectLink(urlHttps, paramsMap, advParam.getUdid(), null,
					null, false, true, 0);	//参数1，决定使用redirect； 0，决定使用ad
		}
		
		
		paramsMap.put("udid", advParam.getUdid());
		paramsMap.put("s", advParam.getS());
		paramsMap.put("cityId", advParam.getCityId());
		paramsMap.put("v", advParam.getV());
		paramsMap.put("wse", "1");
		paramsMap.put("linkRefer", "direct");
		
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
	
	/**
	 * 给img里面的每个url加上高度 和 宽度
	 * @param advParam
	 * @return
	 */
	public ArrayList<Thumbnail> delThumbnails(AdvParam advParam) {
		String urlSize = "";
		if(StringUtils.isNotBlank(advParam.getPicHeight()) && StringUtils.isNotBlank(advParam.getPicWidth())) {
			urlSize += "&width=" +advParam.getPicWidth() + "&height=" + advParam.getPicHeight();
			//eg.  &width=120&height=200
		}
		
		ArrayList<Thumbnail> imgs = new ArrayList<Thumbnail>();
		for(Thumbnail t : this.thumbnails) {
			Thumbnail tm = new Thumbnail();
			tm.setType(t.getType());
			tm.setHeight(t.getHeight());
			tm.setWidth(t.getWidth());
			tm.setUrl(t.getUrl() + urlSize);
			
			imgs.add(tm);
		}
		
		return imgs;
	}
	
	
	public void print() {
		System.out.println("id=" + getId());
		System.out.println("title=" + getTitle());
		System.out.println("origin_src_name=" + getOrigin_src_name());
		System.out.println("url=" + getUrl());
		System.out.println("Thumbnails=" + getThumbnails());
	}

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getRecoid() {
		return recoid;
	}


	public void setRecoid(String recoid) {
		this.recoid = recoid;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public long getGrab_time() {
		return grab_time;
	}


	public void setGrab_time(long grab_time) {
		this.grab_time = grab_time;
	}


	public ArrayList<Thumbnail> getThumbnails() {
		return thumbnails;
	}


	public void setThumbnails(ArrayList<Thumbnail> thumbnails) {
		this.thumbnails = thumbnails;
	}


	public String getOrigin_src_name() {
		return origin_src_name;
	}


	public void setOrigin_src_name(String origin_src_name) {
		this.origin_src_name = origin_src_name;
	}
	
	public static void main(String args[]) {
		String content = "{\"grab_time\":1485049522352,\"id\":\"10379942016756583909\",\"imgs\":[{\"height\":338,\"type\":\"jpg\",\"url\":\"http://image.uczzd.cn/16218259989589359785.jpg?id=0&width=203.45&height=140\",\"width\":500}],\"origin_src_name\":\"香蕉先生\",\"recoid\":\"17563143824888897029\",\"title\":\"一年只上5天班能挣65万 但这份工作常年缺人!\",\"url\":\"https://ad.chelaile.net.cn/?link=https%3A%2F%2Fm.uczzd.cn%2Fwebview%2Fnews%3Fapp%3Dchelaile-iflow%26aid%3D10379942016756583909%26cid%3D100%26zzd_from%3Dchelaile-iflow%26uc_param_str%3Ddndsfrvesvntnwpfgicp%26recoid%3D17563143824888897029%26rd_type%3Dreco%26sp_gz%3D0&userId=ozKKGuKZ2mDTReZB0DBIPBFJ31Hg&lineId=022-649-0\"}";
		JSON.parseObject(content, FlowContent.class);
		System.out.println("1");
	}

	public int getContent_type() {
		return content_type;
	}

	public void setContent_type(int content_type) {
		this.content_type = content_type;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", title=" + title + ", recoid=" + recoid + ", item_type=" + getItem_type() + ", url="
				+ url + ", grab_time=" + grab_time + ", origin_src_name=" + origin_src_name + ", thumbnails="
				+ thumbnails + ", item_type=" + item_type + "]";
	}

	public int getItem_type() {
		return item_type;
	}

	public void setItem_type(int item_type) {
		this.item_type = item_type;
	}
}
