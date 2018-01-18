package com.bus.chelaile.model.rule;

import java.io.IOException;
import com.bus.chelaile.util.HttpUtils;

public class TestJoupForModel {
	public static void main(String[] args) throws IOException {
//		Document doc = Jsoup.parse(new File("E:\\wyangyiyun\\ori_text.html"), "utf-8");
//		doc.getElementById("title_wangyi").text("title_wangyi");
//		doc.getElementById("author_wangyi").text("author_wangyi");
//		doc.getElementById("time_wangyi").text(new Date().toString());
//		doc.getElementById("content_wangyi").text("content_wangyi");
//		System.out.println(doc.html());
		
		
		String url = "http://dev.chelaile.net.cn/adpub/adv!getUCArticles.action?udid=1fb6d0547-b3ba-435b-ba29-001a1bbe261b&stats_referer=discovrery&stats_act=get_more&ftime=&recoid=";
		System.out.println(HttpUtils.get(url, "utf-8"));
		
		System.exit(0);
	}
}
