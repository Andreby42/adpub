package com.bus.chelaile.flowNew.qingmang;

import java.io.IOException;
import java.util.*;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.flowNew.FlowStaticContents;
import com.bus.chelaile.flowNew.model.ArticleContent;
import com.bus.chelaile.flowNew.qingmang.model.Article;
import com.bus.chelaile.util.HttpUtils;
import com.bus.chelaile.util.New;


/***
 * 处理轻芒接口
 * @author quekunkun
 *
 */
public class QMHelper {

	protected static final Logger logger = LoggerFactory.getLogger(QMHelper.class);
	private static String ARTICLE_GET = "http://api.qingmang.me/v2/article.list?token=2625bc965189419c94f2f6b3d8491876&category_id=%s";


	/***
	 * 从轻芒接口获取文章列表
	 * @param channelId
	 * @return List<ArticleContent>
	 */
	public static int getArticlesFromAPI(String channelId, Map<String, ArticleContent> articleContents, int articleNo, String date) {

		String url = String.format(ARTICLE_GET, channelId);
		String response = null;
		try {
			response = HttpUtils.get(url, "utf-8");
		}  catch (Exception e) {
			e.printStackTrace();
			logger.error("请求轻芒接口出错！response={}", response);
			return articleNo;
		}
		JSONObject resJ = JSONObject.parseObject(response);
		JSONArray articlesJ = resJ.getJSONArray("articles");
//		System.out.println(response);
		List<Article> articles = New.arrayList();
		articles = JSONObject.parseArray(articlesJ.toJSONString(), Article.class);

		int i = 0;
		for (Article ar : articles) {
			System.out.println(ar.toString());
			String articleKey = AdvCache.getQMArticleKey(date + "#" + articleNo);
			if(createMyArticles(articleContents, ar, articleKey)) {
				articleNo ++;
				i ++;
			}
		}
		FlowStaticContents.setArticleNo(date, articleNo);
		logger.info("拉取频道 ：{} ,有效文章数量为：{}", channelId, i);
		return articleNo;
	}


	/***
	 * 根据接口返回的Article，构建ArticleContent
	 * @param articleContents
	 * @param ar
	 * @param articleNo
	 * @param date
	 * @return 是否构建成功
	 */
	private static boolean createMyArticles(Map<String, ArticleContent> articleContents, Article ar, String articleKey) {

		ArticleContent myAr = new ArticleContent();
		myAr.setArticleId(ar.getArticleId());

		myAr.setType(1);
		if (ar.getCovers() != null && ar.getCovers().size() > 1) {
			myAr.setPic(ar.getCovers().get(0).getUrl());
		} else if (ar.getImages() != null && ar.getImages().size() > 1) {
			myAr.setPic(ar.getImages().get(0).getUrl());
		} else {
			myAr.setType(0);	// 无图模式
			logger.info("无图 ！ArticleId={}", myAr.getArticleId());
		}

		myAr.setDesc(ar.getSnippet());
		myAr.setSource(ar.getAuthor());
		if (ar.getMusics() != null && ar.getMusics().size() > 1) {
			myAr.setMusic(ar.getMusics().get(0).getUrl());
		}

		if (ar.getVideos() != null && ar.getVideos().size() > 1) {
			myAr.setVideo(ar.getVideos().get(0));
		}
		
		// TODO 切图
		if(myAr.getType() == 1) {
			
		}
		
		// TODO 根据 myAr 的articleId获取内容，然后构建html文件，写到指定的机器。赋值对应的link
		
		articleContents.put(articleKey, myAr);
		return true;
	}
	
	
	/**
	 * for test
	 * @param args
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException {

		String channelId = "c284209068";
		String url = String.format(ARTICLE_GET, channelId);

		String response = HttpUtils.get(url, "utf-8");
		// System.out.println(response);
		JSONObject resJ = JSONObject.parseObject(response);
		JSONArray articlesJ = resJ.getJSONArray("articles");

		System.out.println(response);

		List<Article> articles = New.arrayList();
		articles = JSONObject.parseArray(articlesJ.toJSONString(), Article.class);

		for (Article ar : articles) {
			System.out.println(ar.toString());
//			List<ArticleContent> myArticles = New.arrayList();
//			createMyArticles(myArticles, ar);
		}

	}
}
