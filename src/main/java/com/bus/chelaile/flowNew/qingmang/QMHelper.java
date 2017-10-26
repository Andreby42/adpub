package com.bus.chelaile.flowNew.qingmang;

import java.io.IOException;
import java.util.*;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.flowNew.model.ArticleContent;
import com.bus.chelaile.flowNew.qingmang.model.Article;
import com.bus.chelaile.util.HttpUtils;
import com.bus.chelaile.util.New;

/**
 * 处理轻芒接口
 * @author quekunkun
 *
 */
public class QMHelper {

	protected static final Logger logger = LoggerFactory.getLogger(QMHelper.class);
	private static String ARTICLE_GET  = "http://api.qingmang.me/v2/article.list?token=2625bc965189419c94f2f6b3d8491876&category_id=%s";
	
	
	
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		
		QMHelper qm = new QMHelper();
		
		String channelId = "c284209068";
		String url = String.format(ARTICLE_GET, channelId);
		
		String response = HttpUtils.get(url, "utf-8");
//		System.out.println(response);
		JSONObject resJ = JSONObject.parseObject(response);
		JSONArray articlesJ = resJ.getJSONArray("articles");
		
		System.out.println(response);
		
		List<Article> articles = New.arrayList();
		articles =JSONObject.parseArray(articlesJ.toJSONString(), Article.class);
		
			
		for(Article ar : articles) {
			
			System.out.println(ar.toString());
			List<ArticleContent> myArticles = New.arrayList();
			qm.createMyArticles(myArticles, ar);
			
		}
		
	}





	private void createMyArticles(List<ArticleContent> myArticles, Article ar) {
		
		ArticleContent myAr = new ArticleContent();
		
		myAr.setArticleId(ar.getArticleId());
		
		myAr.setType(1);
		if(ar.getCovers() != null && ar.getCovers().size() > 1) {
			myAr.setPic(ar.getCovers().get(0).getUrl());
		} else if(ar.getImages() != null && ar.getImages().size() > 1) {
				myAr.setPic(ar.getImages().get(0).getUrl());
		} else {
			myAr.setType(0);
			logger.info("没有任何图片可以用来做封面图！ Article={}", ar.toString());
		}
		
		myAr.setDesc(ar.getSnippet());
		myAr.setSource(ar.getAuthor());
		if(ar.getMusics() != null && ar.getMusics().size() > 1) {
			myAr.setMusic(ar.getMusics().get(0).getUrl());
		}
		
		if(ar.getVideos() != null &&ar.getVideos().size() > 1) {
			myAr.setVideo(ar.getVideos().get(0));
			
		}
	}
}
