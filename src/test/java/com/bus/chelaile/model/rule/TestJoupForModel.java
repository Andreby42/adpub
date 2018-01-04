package com.bus.chelaile.model.rule;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class TestJoupForModel {
	public static void main(String[] args) throws IOException {
		Document doc = Jsoup.parse(new File("E:\\wyangyiyun\\ori_text.html"), "utf-8");
		doc.getElementById("title_wangyi").text("title_wangyi");
		doc.getElementById("author_wangyi").text("author_wangyi");
		doc.getElementById("time_wangyi").text(new Date().toString());
		doc.getElementById("content_wangyi").text("content_wangyi");
		System.out.println(doc.html());
	}
}
