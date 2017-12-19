package com.bus.chelaile.flowNew;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.flow.ActivityService;
import com.bus.chelaile.flow.model.ActivityContent;
import com.bus.chelaile.flowNew.model.ArticleContent;
import com.bus.chelaile.flowNew.model.FlowNewContent;
import com.bus.chelaile.flowNew.qingmang.QMHelper;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.DateUtil;
import com.bus.chelaile.util.New;

public class FlowStaticContents {

	protected static final Logger logger = LoggerFactory.getLogger(FlowStaticContents.class);

	// 定时拉取的文章列表,key=自定义文章序列(有递增项)，value=文章详情
	// key="QM_ARTICLE_KEY#" + ${date} + "#" + articleNo
	// articleNo从0开始递增,每天从0开始
	public static final Map<String, ArticleContent> ARTICLE_CONTENTS = New.hashMap();
	public static final List<ActivityContent> activityContens = New.arrayList();
	
	// 详情页下方滚动栏内容
	public static final Map<Integer, List<FlowNewContent>> LINE_DETAIL_FLOWS = New.hashMap();
	public static final int ARTICLE_NUMBER_LIMIT = 100;
	private static final int ARTICLE_RETURN_NUMBER = 10;

	public static final Set<String> CHANNELS = New.hashSet();
//	private static final List<String> READ_PICS = New.arrayList();
	static {
		CHANNELS.add("c284209068");
		CHANNELS.add("c284209082");
		CHANNELS.add("c284209029");
		
//		READ_PICS.add("http://chelaile-user-avatar.oss-cn-hangzhou.aliyuncs.com/8a2aa5ac42724bffb51755dff98883d2");
//		READ_PICS.add("http://chelaile-user-avatar.oss-cn-hangzhou.aliyuncs.com/5297d615a03d42178aa697f17b18d1f1");
//		READ_PICS.add("http://q.qlogo.cn/qqapp/100490037/78EDB12F2EBBD5C82510C068BE62DC18/100");
//		READ_PICS.add("http://wx.qlogo.cn/mmopen/I1puq4ib1uadjZkQbYiaG9MYyibRtAsicTicSE5jNVKG34fg4qaqHrTCmG5wyMhppiaRU2zy34kyabXp0NjDHCibOiab9moChxnu6ttN/0");

	}
	// 样本html文件，图文模式
	private static String textHtml = "/data/advConfig/ori_text.html";
	public static StringBuilder ORI_TEXT_HTML;
	public static List<String> FAKE_PHOTOS = New.arrayList();
		
	/**
	 * 初始化拉取文章
	 */
	public static void initArticleContents() {
		ARTICLE_CONTENTS.clear();
		LINE_DETAIL_FLOWS.clear();
		String date = DateUtil.getTodayStr("yyyy-MM-dd");
		
		// 初始化文章
		try {
			ORI_TEXT_HTML = new StringBuilder();
			readHtml(); 
			logger.info("QM初始化文章模板结束~");
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 文章缓存
		setArticleFirstNo(date);
		while (ARTICLE_CONTENTS.size() < ARTICLE_NUMBER_LIMIT) {
			for (String channelId : CHANNELS) {
				int articleNo = getArticleNo(date + "#" + channelId); // 获取本次拉取之前的 ‘文章no’起始值
				logger.info("QM轻芒文章,本次拉取文章，起始id数为：{}", articleNo);
				articleNo = QMHelper.getArticlesFromAPI(channelId, articleNo, date);	// 获取文章，填入缓存
				if(articleNo == -1) {
					logger.info("轻芒接口错误！！,本次拉取直接结束！！！ ");
					return;
				}
				logger.info("QM轻芒文章,拉取频道 ：{} 之后，最新文章id数为：{}", channelId, articleNo);
			}
		}
		// 导入ocs中
		for(String articleKey : ARTICLE_CONTENTS.keySet()) {
			CacheUtil.setNew(articleKey, Constants.ONE_DAY_TIME, JSONObject.toJSONString(ARTICLE_CONTENTS.get(articleKey)));
		}
		
		
		
		logger.info("QM更新完毕，该批次所有频道目前缓存文章信息如下：");
		for (String channelId : CHANNELS) {
			String dateChannelId = date + "#" + channelId;
			logger.info("QM： channelId={}, firstNo={}, LastNo={}", channelId, getArticleFirstNo(dateChannelId), getArticleNo(dateChannelId));
		}
	}

	private static void readHtml() throws IOException {
		// 读取源文件
		BufferedReader reader = null;
		InputStreamReader input = null;
		try {
			input = new InputStreamReader(new FileInputStream(textHtml));
			reader = new BufferedReader(input);
			String line;
			while ((line = reader.readLine()) != null) {
				ORI_TEXT_HTML.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(input != null) {
				input.close();
			}
			if(reader != null) {
				reader.close();
			}
		}

	}

	// 获取最新文章i序列编号
	public static int getArticleNo(String dateChannelId) {
		int articleNo = 0;
		String articleNoStr = (String) CacheUtil.getNew(AdvCache.getQMArticleNo(dateChannelId));
		if (articleNoStr != null) {
			articleNo = Integer.parseInt(articleNoStr);
		}
		return articleNo;
	}

	// 更新最新文章序列编号
	public static void setArticleNo(String dateChannel, int no) {
		String key = AdvCache.getQMArticleNo(dateChannel);
		CacheUtil.setNew(key, Constants.ONE_DAY_TIME, String.valueOf(no));
	}
	
	
	// 获取缓存中第一篇文章i序列编号
	public static int getArticleFirstNo(String dateChannelId) {
		int articleNo = 0;
		String articleNoStr = (String) CacheUtil.getFromRedis(AdvCache.getQMArticleFirstNo(dateChannelId));
		if (articleNoStr != null) {
			articleNo = Integer.parseInt(articleNoStr);
		} 
		return articleNo;
	}
	
	
	// 更新缓存中第一篇文章的id
	// 将所有频道之前最后一篇文章id+1，即可
	private static void setArticleFirstNo(String date) {
		for (String channelId : CHANNELS) {
			int articleNo = getArticleNo(date + "#" + channelId); // 获取上次拉取的最后一篇文章id
			
			String key = AdvCache.getQMArticleFirstNo(date + "#" + channelId);
			CacheUtil.setToRedis(key, Constants.ONE_DAY_TIME, String.valueOf(articleNo + 1));
		}
	}


	public static void addFlowsToMap(int type, FlowNewContent flow) {
		if (LINE_DETAIL_FLOWS.containsKey(type)) {
			LINE_DETAIL_FLOWS.get(type).add(flow);
		} else {
			List<FlowNewContent> flowList = New.arrayList();
			flowList.add(flow);
			LINE_DETAIL_FLOWS.put(type, flowList);
		}
	}

	/*
	 * 从缓存获取文章列表
	 */
	public static List<ArticleContent> getArticleList(String channelId, int articlePersonNo, AdvParam param) {
		List<ArticleContent> articles = New.arrayList();
		String date = DateUtil.getTodayStr("yyyy-MM-dd");
		int articleNoLast = articlePersonNo;
		if (!channelId.equals(-1)) {
			for (int i = articlePersonNo + 1; i <= articlePersonNo + ARTICLE_RETURN_NUMBER; i++) {
				String key = AdvCache.getQMArticleKey(date + "#" + channelId + "#" + i);
				if (!CHANNELS.contains(channelId)) {
					logger.error("不合法的频道id, channelId={}, 频道id取值范围是 CHANNELS={}", channelId, CHANNELS.toString());
				}
				String contentStr = (String) CacheUtil.getNew(key);
//				logger.info("key={}, constantStr={}", key, contentStr);
				if(StringUtils.isNoneBlank(contentStr)) {
					ArticleContent myAr = new ArticleContent();
					
					ArticleContent arFromOcs = JSON.parseObject(contentStr, ArticleContent.class);
					myAr.creatMyArticle(channelId, arFromOcs, param);

					articles.add(myAr);
					articleNoLast ++;
				} else {
					logger.error("没有对应articleNo的文章！ ,key={}", key);
				}
				
//				if (ARTICLE_CONTENTS.containsKey(key)) {
//					ArticleContent myAr = new ArticleContent();
//					myAr.creatMyArticle(channelId, ARTICLE_CONTENTS.get(key), param);
//
//					articles.add(myAr);
//					articleNoLast ++;
//				} 
			}
		} else {
			// TODO channelId=-1,获取所有
		}
		
		// 记录返回给用户最后一篇文章id	  // TODO 待规范化
		CacheUtil.setNew("QM_last_person_articleId_" + date + "_" + param.getAccountId() + "_" + channelId, Constants.ONE_DAY_TIME, String.valueOf(articleNoLast));
		return articles;
	}
	

	/*
	 * 随机从FAKEPHOTOS中获取用户图像
	 */
	public static List<String> getRandomFakePics() {
		int size = FAKE_PHOTOS.size();
		List<String> photos = New.arrayList();
		if(size == 0) {
			return photos;
		}
		int i = 0;
		while (i < 4) {
			int num = (int) (Math.random() * size);
			if(photos.contains(FAKE_PHOTOS.get(num))) {
				continue;
			} else if(FAKE_PHOTOS.get(num).contains("http://wx.qlogo.cn") && FAKE_PHOTOS.get(num).contains("/0")) {
				continue;
			} else {
				photos.add(FAKE_PHOTOS.get(num));
				i++;
			}
		}
		return photos;
	}
	
	
	/*
	 * 获取阅读文章的用户数
	 * 基数：20-50
	 * 每增加一个用户，+ 5
	 */
	public static int getReadArticleNum(String channelId, String articleId) {
		String key = "QM_ARTICLE_CLICK_" + channelId + "_" + articleId;
		String valueStr = (String)CacheUtil.getNew(key);
		int value = 0;
		if(valueStr == null) {
			value = (int) (150 + Math.random() * 250);
			CacheUtil.setNew(key, Constants.LONGEST_CACHE_TIME, String.valueOf(value));	 // 文章的初始化阅读值
		} else {
			value = Integer.parseInt(valueStr);
		}
		return value;
	}
	
	public static Map<Integer, List<FlowNewContent>> getLineDetailFlows() {
		return LINE_DETAIL_FLOWS;
	}
	
	
	/**
	 * 是否返回详情页下方入口
	 * @param param
	 * @return
	 */
	public static  boolean isReturnLineDetailFlows(AdvParam param) {
		if(isFirstWord(param.getUdid(), FlowServiceManager.HEADLIST)) {
			return true;					// udid开头的一些用户打开详情页入口||支持小说的，全部打开详情页入口
		}
		if (ActivityService.FLOWUDIDS.contains(param.getUdid())) {
			return true;
		}
		if (Constants.ISTEST) {
			return true; 			// FOR TEST
		}
		return false;
	}
	
	private static boolean isFirstWord(String udid, List<String> headlist) {
		if(headlist != null && udid != null) {
			for(String s : headlist) {
				if(udid.startsWith(s)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static void main(String[] args) throws IOException {
		
//		QMHelper.getArticlesFromAPI("c284209082", 80, "20178-10-30");
//		
//		readHtml();
//		System.out.println(ORI_TEXT_HTML);
		
		System.out.println((int) (20 + Math.random() * 30));
		
		String str = "http://wx.qlogo.cn/mmopen/eYIsNuTT4CCFCeArU1l59CoFAzTNoS7Be1uUhKiagxyNAnIrC20QLbo16HicIwic9fibulgEYV2m4eN3jmzz5jf9krpb7qVBIndV/0";
		System.out.println(str.contains("http://wx.qlogo.cn") && str.contains("/0"));
		
		Set<String> a = New.hashSet();
		a.add("1");
		a.add("http://223423");
		a.add("http://wwws.baidu.com");
		a.add("dfasfdadsdf");
		a.add("http://www.sdfbaidu.com");
		a.add("dfasfdasddsdfsf");
		a.add("http://wwwas.baidu.com");
		
		List<String> b = new ArrayList<>(a);
		int size = b.size();
		List<String> photos = New.arrayList();
		int i = 0; 
		while(i < 4) {
			int num = (int) (Math.random() * size);
			System.out.println(num);
			if(!photos.contains(b.get(num))) {
				photos.add(b.get(num));
				i ++;
			}
		}
		System.out.println(photos.toString());
	}
}
