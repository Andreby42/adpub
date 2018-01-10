package com.bus.chelaile.flow;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;



import com.bus.chelaile.flow.model.FlowChannel;
import com.bus.chelaile.flow.model.FlowContent;
import com.bus.chelaile.flow.wangyiyun.WangYIParamForSignature;
import com.bus.chelaile.flow.wangyiyun.WangYiYunDetailModel;
import com.bus.chelaile.flow.wangyiyun.WangYiYunDetailModel.Img;
import com.bus.chelaile.flow.wangyiyun.WangYiYunResultBaseDto;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.HttpUtils;
import com.bus.chelaile.util.config.PropertiesUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AbstractWangYiYunHelp implements InterfaceFlowHelp {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected final static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	protected final static Gson gsonFormat = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

	protected final static Gson gsonSerNulls = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();
//	@Value("${wangyiyun.secretkey}")
//	protected  String secretkey = "3eb746dfa4a54361964e7b49d0e3e2dc";
	protected static final String secretkey = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"wangyiyun.secretkey", "3eb746dfa4a54361964e7b49d0e3e2dc");
	
	
//	@Value("${wangyiyun.appkey}")
//	protected  String appkey = "379a2e02a7e24d389a490637891d0514";
	protected static final String appkey = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"wangyiyun.appkey", "379a2e02a7e24d389a490637891d0514");
	
//	@Value("${wangyiyun.wangYiYunChannelListUrl}")
//	protected  String wangYiYunChannelListUrl = "https://youliao.163yun.com/api-server/api/v1/channel/list";
	protected static final String wangYiYunChannelListUrl = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"wangyiyun.wangYiYunChannelListUrl", "https://youliao.163yun.com/api-server/api/v1/channel/list");
	
//	@Value("${wangyiyun.wangYiYunNewListUrl}")
//	protected  String wangYiYunNewListUrl = "https://youliao.163yun.com/api-server/api/v1/info/list";
	protected static final String wangYiYunNewListUrl = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"wangyiyun.wangYiYunNewListUrl", "https://youliao.163yun.com/api-server/api/v1/info/list");
	
//	@Value("${wangyiyun.wangYuYunNewDetailUrl}")
//	protected  String wangYuYunNewDetailUrl = "https://youliao.163yun.com/api-server/api/v1/info/detail";
	protected static final String wangYuYunNewDetailUrl = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"wangyiyun.wangYuYunNewDetailUrl", "https://youliao.163yun.com/api-server/api/v1/info/detail");
	
//	@Value("${wangyiyun.modelFileName}")
//	protected String modelFileName="E:\\wyangyiyun\\ori_text.html";
	protected static final String modelFileName = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"wangyiyun.modelFileName", "D:\\用户目录\\Downloads\\ori_text.html");
	
//	@Value("${wangyiyun.cdnPath}")
//	protected String cdnPath = "D:\\Program Files (x86)\\nginx-1.12.2\\html\\";
	protected static final String cdnPath = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"wangyiyun.cdnPath", "D:\\temp\\html\\");
	
//	@Value("${wangyiyun.newUrl}")
//	protected  String newUrl="http://127.0.0.1/";
	protected static final String newUrl = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"wangyiyun.newUrl", "http://127.0.0.1/");
	
    protected static final String wangyiArticleHost = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), 
    		"wangyiyun.article.host", "https://youliao.163yun.com/h5/#/info?");
	
	protected <T> WangYiYunResultBaseDto<T> getWangYiYunResponse(String url, Set<WangYIParamForSignature> paramSet,Type type) {
		List<NameValuePair> pairs = new ArrayList<>();
		String signature = makeSignature(paramSet);
		for (WangYIParamForSignature wp : paramSet) {
			pairs.add(new BasicNameValuePair(wp.getKeyName(), wp.getValue()));
		}
		pairs.add(new BasicNameValuePair("signature", signature));
		String response = null;
		try {
			response = HttpUtils.get(url, pairs, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		WangYiYunResultBaseDto<T> result = gson.fromJson(response, type);
		return result;
	}

	protected String makeSignature(Set<WangYIParamForSignature> set) {
		StringBuilder sb = new StringBuilder(secretkey);
		for (WangYIParamForSignature w : set) {
			sb.append(w.getKeyName()).append(w.getValue());
		}
		String params = sb.toString();
		byte[] bytes = params.getBytes();
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] md5bytes = md5.digest(bytes);
		String result = DatatypeConverter.printHexBinary(md5bytes).toLowerCase();
		return result;
	}

	protected String replacesByImgPattern(String source, List<String> imgLists) {
		String[] patternArrays = new String[imgLists.size()];
		String[] imgArrays = imgLists.toArray(new String[imgLists.size()]);
		for (int index = 0; index < imgArrays.length; index++) {
			patternArrays[index] = "${{" + index + "}}$";
		}
		String des = StringUtils.replaceEach(source, patternArrays, imgArrays);

		return des;
	}

	protected String installImg(String url, Integer height, Integer width) {
		Document doc = Jsoup.parse("<img id=\"img\"/>");
		Element e = doc.getElementById("img");

		e.attr("src", url);
		e.attr("alt", "undefined");
		e.attr("style", "float:none;height:" + height + ";width:" + width);
		return e.outerHtml();
	}

	protected Set<WangYIParamForSignature> initBaseParam(String platformParam, Long timestampParam) {

		WangYIParamForSignature timestamp = new WangYIParamForSignature("timestamp", System.currentTimeMillis() + "");
//		System.out.println("timestamp:" + timestamp.getValue());
		WangYIParamForSignature platform = new WangYIParamForSignature("platform", 3 + "");
		WangYIParamForSignature version = new WangYIParamForSignature("version", "v1.4.0");
		WangYIParamForSignature apk = new WangYIParamForSignature("appkey", appkey);
		Set<WangYIParamForSignature> set = new TreeSet<>();
		set.add(timestamp);
		set.add(version);
		set.add(platform);
		set.add(apk);
		return set;
	}

	protected Set<WangYIParamForSignature> installNewDetailParam(String platformParam, Long timestampParam,
			String infoidParam, String producerParam) {
		Gson g = new Gson();
		Set<WangYIParamForSignature> set = initBaseParam(platformParam, timestampParam);
		WangYIParamForSignature infoid = new WangYIParamForSignature("infoid", infoidParam);
		WangYIParamForSignature producer = new WangYIParamForSignature("producer", "recommendation");
		set.add(infoid);
		set.add(producer);
		return set;
	}

	protected Set<WangYIParamForSignature> installNewList(String platformParam, Long timestampParam,
			String channelidParam, String useridParam) {
		Gson g = new Gson();
		Set<WangYIParamForSignature> set = initBaseParam(platformParam, timestampParam);
		WangYIParamForSignature scene = new WangYIParamForSignature("scene", "f");
		WangYIParamForSignature channelid = new WangYIParamForSignature("channelid", channelidParam);
		WangYIParamForSignature num = new WangYIParamForSignature("num", 20 + "");
		WangYIParamForSignature userid = new WangYIParamForSignature("userid",
				useridParam); //575042c1eac22112dcab5620179035829b878a91
		set.add(channelid);
		set.add(scene);
		set.add(num);
		set.add(userid);
		return set;
	}
	
	protected String installNewDetailHtml(String modelHtml,WangYiYunDetailModel model) throws IOException {
		Document doc = Jsoup.parse(new File(modelHtml), "utf-8");
		doc.getElementById("title_wangyi").text(model.getTitle());
		doc.getElementById("author_wangyi").text(model.getSource());
//		DateFormatUtils.format(date, pattern)
//		doc.getElementById("time_wangyi").text(model.getPublishTime());
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		try {
			doc.getElementById("time_wangyi").text(DateFormatUtils.format(sdf.parse(model.getPublishTime()), "MM-dd HH:mm"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		doc.getElementById("content_wangyi").text(model.getContent());
		return doc.html();
	}
	
	protected void formatImagInContent(WangYiYunDetailModel wyDetailModel) {
		List<Img> detailImgList = wyDetailModel.getImgs();
		if (detailImgList != null && !detailImgList.isEmpty()) {

			List<String> imgHtmlLists = new LinkedList<>();
			for (Img img : detailImgList) {
				imgHtmlLists.add(installImg(img.getUrl(), img.getHeight(), img.getWidth()));
			}
			wyDetailModel.setContent(replacesByImgPattern(wyDetailModel.getContent(), imgHtmlLists));
		}
	}

	@Override
	public List<FlowContent> getInfoByApi(AdvParam advParam, long ftime, String recoid, int channelId, boolean isShowAd)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> parseChannel(FlowChannel ucChannel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FlowContent> parseResponse(AdvParam advParam, long ftime, String recoid, String token,
			String channelId, boolean isShowAd) {
		// TODO Auto-generated method stub
		return null;
	}
}
