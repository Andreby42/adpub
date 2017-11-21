package com.bus.chelaile.flowNew.qingmang;

import java.io.*;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.flowNew.FlowStaticContents;
import com.bus.chelaile.flowNew.model.ArticleContent;
import com.bus.chelaile.flowNew.qingmang.model.Article;
import com.bus.chelaile.flowNew.qingmang.model.ArticleParagraph;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;
import com.bus.chelaile.util.DateUtil;
import com.bus.chelaile.util.HttpUtils;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.UrlUtil;


/***
 * 处理轻芒接口
 * @author quekunkun
 *
 */
public class QMHelper {

	protected static final Logger logger = LoggerFactory.getLogger(QMHelper.class);
	private static String ARTICLE_DUMP = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"flowNew.articleList.url", "http://api.qingmang.me/v2/article.dump?token=2625bc965189419c94f2f6b3d8491876&category_id=%s");
	private static String ARTICLE_GET = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"flowNew.articleGet.url", "http://api.qingmang.me/v2/article.get?token=2625bc965189419c94f2f6b3d8491876&format=raml&id=%s");
	
	private static String imageDiv = "<div class=\"img-container\"><img src=\"%s\" alt=\"\"> </div>";
	
	private static final String htmlPath = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"flowNew.htmlPath", "/opt/tomcat-new-6080/webapps/adpub/QM/");
	private static String articleLinkHost = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"flowNew.articleHost", "https://api.chelaile.net.cn/adpub/QM/");
	
	// test
//	private static String htmlPath = "/opt/tomcat-6090/webapps/adpub/QM/";
//	private static String articleLinkHost = "https://dev.chelaile.net.cn/adpub/QM/"; 	//　测试地址
	
	//stage
//	private static String htmlPath = "/root/apache-tomcat-new-8.0.32/webapps/adpub/QM/";
//	private static String articleLinkHost = "https://stage.chelaile.net.cn/adpub/QM/"; 	//　stage地址
	
	//pro
//	private static String htmlPath = "/opt/tomcat-new-6080/webapps/adpub/QM/";
//	private static String articleLinkHost = "https://api.chelaile.net.cn/adpub/QM/"; 	//　api地址
	
	
//	private static final String QMfolder = "adv/QMpic/";
//	private static String QMPicNewPath = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), ("QMPicNewPath"),
//			"/data/logs/pic/QM/");

	/***
	 * 从轻芒接口获取文章列表
	 * @param channelId
	 * @return List<ArticleContent>
	 */
	public static int getArticlesFromAPI(String channelId, int articleNo, String date) {
		String url = String.format(ARTICLE_DUMP, channelId);
		String response = null;
		try {
			response = HttpUtils.get(url, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("请求轻芒接口出错！response={}", response);
			return -1;
		}
		JSONObject resJ = JSONObject.parseObject(response);
		JSONArray articlesJ = resJ.getJSONArray("articles");
		List<Article> articles = New.arrayList();
		articles = JSONObject.parseArray(articlesJ.toJSONString(), Article.class);
		// 如果dump内容用完了，那么reset一次即可，增加参数reset=true
		if (articles == null || articles.size() < 1) {
			// TODO　
			logger.error("QM，dump接口数据拉尽了~ ");
			return -1;
		}
		for (Article ar : articles) {
			String articleKey = AdvCache.getQMArticleKey(date + "#" + channelId + "#" + articleNo);
			if (createMyArticles(ar, articleKey, articleNo, channelId)) {
				articleNo++;
			}
		}
		FlowStaticContents.setArticleNo(date + "#" + channelId, articleNo);
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
	private static boolean createMyArticles(Article ar, String articleKey, int articleNo, String channelId) {
		// 做一些校验性的监测，比如title、来源之类的
		if (StringUtils.isBlank(ar.getTitle())) {
			logger.error("返回文章内容详情不符合要求，article={}", ar.toString());
			return false;
		}
		
		ArticleContent myAr = new ArticleContent();
		myAr.setArticleId(ar.getArticleId() + "_" + articleNo);
		
		myAr.setType(1);
		if (ar.getCovers() != null && ar.getCovers().size() > 0) {
			myAr.setPic(ar.getCovers().get(0).getUrl());
		} 
//		else if (ar.getImages() != null && ar.getImages().size() > 0) {
//			myAr.setPic(ar.getImages().get(0).getUrl());
//		} 
		else {
			myAr.setType(0);	// 无图模式
		}
		
		if (ar.getVideos() != null && ar.getVideos().size() > 0) {
			myAr.setVideo(ar.getVideos().get(0));
		}
//		if(myAr.getType() == 0 && (myAr.getVideo() != null)) {
//			logger.error("QM 轻芒文章,遇到有视频，但是没有封面的文章，直接略去，articleKey={}, Article={}", articleKey, ar);
//			return false;
//		}
		
		myAr.setTitle(ar.getTitle());
		myAr.setDesc(ar.getSnippet());
		myAr.setSource(ar.getProviderName());		// author
		myAr.setPublishTimestamp(ar.getPublishTimestamp());
		if (ar.getMusics() != null && ar.getMusics().size() > 0) {
			myAr.setMusic(ar.getMusics().get(0).getUrl());
		}
		
		// 根据 myAr 的articleId获取内容，然后构建html文件，写到指定的机器。赋值对应的link
		String link = writeArticleHtml(myAr, ar.getArticleId(), channelId);
		if(link == null ){
//			logger.error("生成静态html文件错误，articleId={}", ar.getArticleId());
			return false;
		}
//		logger.info("QM,记录有效源：channel={}, source={}, title={}", 
//				channelId, myAr.getSource(), myAr.getTitle());
		myAr.setLink(articleLinkHost + link);
		
		// 压缩封面图			//不压缩看看效果
//		if (myAr.getType() == 1) {
//			// 原始图片
//			String picDownName;
//			try {
//				picDownName = UrlUtil.saveUrlPic(myAr.getPic(), "QM_" + myAr.getArticleId());
//				logger.info("QMpic saved , picName={},title={}", picDownName, "QM_" + myAr.getArticleId());
//				
////				String picSmall = QMPicNewPath + "QM_" + myAr.getArticleId() + ".jpeg";
////				ImgUtils.scale(picDownName, picSmall, 180 * 3, 240 * 3, false);// 等比例缩放
////				
////				File file = new File(picSmall);
////				logger.info("生成图片地址：file={}", file);
////				String url = OSSUtil.putPhoto("QM_" + myAr.getArticleId() + ".jpg", file, "image/jpeg", QMfolder);
////				if(url != null)
////					myAr.setPic(url);	// 上传压缩后的图片，并获链接
//			} catch (Exception e) {
//				e.printStackTrace();
//				logger.error("QM 压缩图片失败");
////				return false;
//			}
//		}
		
//		logger.info("轻芒文章详情： key={}, myAr={}", articleKey, JSON.toJSONString(myAr)); 
		FlowStaticContents.ARTICLE_CONTENTS.put(articleKey, myAr);
		logger.info("对应articleNo的文章，no={},articleKey={}", articleNo, articleKey);
		return true;
	}
	
	/*
	 * 生成静态html文件！，并复制给myAr以文件链接
	 */
	private static String writeArticleHtml(ArticleContent myAr, String articleId, String channelId) {
		try {
			String url = String.format(ARTICLE_GET, articleId);
			String response = null;
			try {
				response = HttpUtils.get(url, "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("请求轻芒接口出错！response={}", response);
				return null;
			}
			JSONObject resJ = JSONObject.parseObject(response);
			JSONObject articleJ = resJ.getJSONObject("article");
			String title = articleJ.getString("title");
			String author = articleJ.getString("providerName");
			String content = articleJ.getString("content");
//			JSONArray imagesJ = articleJ.getJSONArray("images");

			if (StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
				logger.error("返回文章内容详情不符合要求，response={}", response);
				return null;
			}
			
			List<ArticleParagraph> paragraphList = JSONObject.parseArray(content, ArticleParagraph.class);
//			System.out.println(paragraphList.toString());
//			logger.info("轻芒文章，获取到的正文部分内容是:{}", paragraphList.toString());
			List<Double> imgRtio = New.arrayList();
			StringBuilder htmlContent = new StringBuilder();
			for(ArticleParagraph arPara : paragraphList) {
				if(arPara.getType() == 0) {
					// 文本
					htmlContent.append("<p>" + arPara.getText().getText() + "</p>");
				} else if(arPara.getType() == 1) {
					// 单图
					htmlContent.append(String.format(imageDiv, arPara.getImage().getSource()));
					imgRtio.add((double)(arPara.getImage().getHeight()) / (double)(arPara.getImage().getWidth()));
				} else if(arPara.getType() == 2) {
					// 音频
					logger.info("QM 遇到视频，暂时略过, channelId={}, source={}", channelId, author);
//					continue;  	
					return null;
				}
			}
			
			
			String htmlOut = FlowStaticContents.ORI_TEXT_HTML.toString();
			htmlOut = htmlOut.replaceAll("#title#", title);
			htmlOut = htmlOut.replace("#author#", author);
			htmlOut = htmlOut.replace("#date#", DateUtil.flowFormatTime(String.valueOf(myAr.getPublishTimestamp())));
			htmlOut = htmlOut.replace("#content#", htmlContent.toString());
			htmlOut = htmlOut.replace("#imgRtio#", imgRtio.toString());
			if(StringUtils.isNoneBlank(myAr.getDesc())) {
				htmlOut = htmlOut.replace("#shareText#", myAr.getDesc());		// 分享
			} else {
				htmlOut = htmlOut.replace("#shareText#", "");
			}
			
			// 写文件
			BufferedWriter writer = null;
			OutputStreamWriter output = null;
			output = new OutputStreamWriter(new FileOutputStream(htmlPath + "QM_" + myAr.getArticleId()));	 	 //文章内容， 文章存放和同步
			writer = new BufferedWriter(output);
			writer.write(htmlOut);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("e={}", e, e.getMessage());
			return null;
		}

		return "QM_" + myAr.getArticleId();
	}


	/**
	 * for test
	 * @param args
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException {

//		String channelId = "c284209068";
//		String url = String.format(ARTICLE_DUMP, channelId);
//
//		String response = HttpUtils.get(url, "utf-8");
//		// System.out.println(response);
//		JSONObject resJ = JSONObject.parseObject(response);
//		JSONArray articlesJ = resJ.getJSONArray("articles");
//
//		System.out.println(response);
//
//		List<Article> articles = New.arrayList();
//		articles = JSONObject.parseArray(articlesJ.toJSONString(), Article.class);
//
//		for (Article ar : articles) {
//			System.out.println(ar.toString());
////			List<ArticleContent> myArticles = New.arrayList();
////			createMyArticles(myArticles, ar);
//		}
		
		String articleId = "-1580713567948843281";
		try {
			String url = String.format(ARTICLE_GET, articleId);
			String response = null;
			try {
				response = HttpUtils.get(url, "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("请求轻芒接口出错！response={}", response);
			}
			JSONObject resJ = JSONObject.parseObject(response);
			JSONObject articleJ = resJ.getJSONObject("article");
			String title = articleJ.getString("title");
			String author = articleJ.getString("author");
			String content = articleJ.getString("content");
//			JSONArray contentJ = JSONObject.parseArray(content);
			
			List<ArticleParagraph> paragraphList = JSONObject.parseArray(content, ArticleParagraph.class);
			System.out.println(paragraphList.toString());
			
//			if()
//			double[] imgRtio = null;
			List<Double> imgRtio = New.arrayList();
			StringBuilder htmlContent = new StringBuilder();
			for(ArticleParagraph arPara : paragraphList) {
				if(arPara.getType() == 0) {
					// 文本
					htmlContent.append("<p>" + arPara.getText().getText() + "</p>");
				} else if(arPara.getType() == 1) {
					// 单图
					htmlContent.append(String.format(imageDiv, arPara.getImage().getSource()));
					imgRtio.add((double)(arPara.getImage().getHeight()) / (double)(arPara.getImage().getWidth()));
				} else if(arPara.getType() == 2) {
					// 音频
					logger.info("遇到视频，暂时略过");
				} else {
					logger.info("其他类型，暂时略过：type={}", arPara.getType());
				}
			}
//			System.out.println("组装后的htmlContent=" + htmlContent.toString());
			
			// 读取源文件
			BufferedReader reader = null;
			InputStreamReader input = null;
			String htmlOri = null;
			input = new InputStreamReader(new FileInputStream("D://ori.html"));
			reader = new BufferedReader(input);
			String line;
			while((line = reader.readLine()) != null) {
				htmlOri += line;
			}
			input.close();
			reader.close();
			
			String htmlOut = htmlOri;
			htmlOut = htmlOut.replaceAll("#title#", title);
			htmlOut = htmlOut.replace("#author#", author);
			htmlOut = htmlOut.replace("#content#", htmlContent.toString());
			htmlOut = htmlOut.replace("#imgRtio#", imgRtio.toString());
			
			// 写文件
			BufferedWriter writer = null;
			OutputStreamWriter output = null;
			output = new OutputStreamWriter(new FileOutputStream("D://out.html"));
			writer = new BufferedWriter(output);
			writer.write(htmlOut);
			writer.flush();
			writer.close();
			
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
