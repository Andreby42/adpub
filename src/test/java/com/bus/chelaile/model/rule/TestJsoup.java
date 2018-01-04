package com.bus.chelaile.model.rule;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class TestJsoup {
	public static void main(String[] args) {
		Document doc = Jsoup.parse("<img id=\"img\"/>");
		Element  e = doc.getElementById("img");
		
		e.attr("src", "https://pic1.chelaile.net.cn/adv/3093c653-da02-4a6a-9bcc-74a067ca42cb.png");
		e.attr("alt", "undefined");
		e.attr("style", "float:none;height:768;width:1024");
		System.out.println(e.outerHtml());
	}
	
	
	private String replacesByImgPattern(String source,List<String> imgLists) {
		String[] patternArrays = new String[imgLists.size()];
		String[] imgArrays=imgLists.toArray(new String[imgLists.size()]);
		for(int index=0;index<imgArrays.length;index++) {
			patternArrays[index]="${{"+index+"}}";
		}
		String des =StringUtils.replaceEach(source, patternArrays, imgArrays);
		//StringUtils.replaceEach(text, searchList, replacementList)
		
		return des;
	}
	
	private String installImg(String url,Integer height,Integer width) {
		Document doc = Jsoup.parse("<img id=\"img\"/>");
		Element  e = doc.getElementById("img");
		
		e.attr("src", url);
		e.attr("alt", "undefined");
		e.attr("style", "float:none;height:"+height+";width:"+width);
		return e.outerHtml();
	}
}
