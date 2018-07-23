package com.bus.chelaile.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.service.JSService;
import com.bus.chelaile.util.FileUtil;
import com.bus.chelaile.util.New;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public class ReplaceJs {

	protected static final Logger logger = LoggerFactory.getLogger(ReplaceJs.class);



	@RequiredArgsConstructor
	@ToString
	static class TextPlainImpl implements Text {
		final private String str;

		@Override
		public void write(Appendable buf, Map<String, String> ctx) throws IOException{
			buf.append(str);
		}
	}

	@RequiredArgsConstructor
	@ToString
	static class TextVarImpl implements Text {
		private final String name;

		@Override
		public void write(Appendable buf, Map<String, String> ctx) throws IOException {
			Object obj = ctx.get(name);

			buf.append(obj == null?"":obj.toString());
		}
	}

	public static List<Text> parse(String ori) {
		List<Text> list = new ArrayList<>();
		int pos1 = 0, pos2 = 0, pos3 = 0;
		while (true) {
			pos2 = ori.indexOf("${", pos1);
			if (pos2 < 0)
				break;

			pos3 = ori.indexOf("}", pos2 + 2);
			if (pos3 < 0)
				throw new IllegalArgumentException("${ } not match.");
			

			list.add(new TextPlainImpl(ori.substring(pos1, pos2)));
			list.add(new TextVarImpl(ori.substring(pos2 + 2, pos3)));

			pos1 = pos3 + 1;
		}

		list.add(new TextPlainImpl(ori.substring(pos1)));

		return list;
	}

	public static PositionJs getPositionJs(String context) {
		PositionJs js = new PositionJs();
		List<String> list = New.arrayList();
		List<String> replaceList = New.arrayList();
		js.setOriginList(list);
		js.setReplaceList(replaceList);
		for (;;) {
			int pos = context.indexOf("${");
			if (pos == -1) {
				return js;
			}
			String head = context.substring(0, pos);
			list.add(head);
			String tail = context.substring(pos, context.length());
			pos = tail.indexOf("}");

			String replaceStr = tail.substring(0, pos + 1);
			tail = tail.substring(pos + 1);
			replaceList.add(replaceStr);

			context = tail;

		}

	}
	
	
	public static void getNewReplaceStr(List<Text> list, Map<String, String> map,Appendable buffer) throws IOException {
		
		for (int i = 0; i < list.size(); i++) {
			Text origin = list.get(i);
			origin.write(buffer, map);
			
		}

	}

	public static String getReplaceStr(List<Text> list, Map<String, String> map) {
		
		StringBuffer buffer = new StringBuffer(30_000);
		for (int i = 0; i < list.size(); i++) {
			Text origin = list.get(i);
			if( origin instanceof TextPlainImpl ) {
				buffer.append(((TextPlainImpl) origin).str);
			}
			String value = "";
			String replace = "";
			if( origin instanceof TextVarImpl ) {  
				value = map.get(((TextVarImpl) origin).name);
				replace = ((TextVarImpl) origin).name;
				if( value != null ) {
					buffer.append(value);
				}else {
					buffer.append(replace);
				}
			}
			

			//logger.info("replace={},value={}", replace , value);
		}

//		for (Map.Entry<String, String> entry : map.entrySet()) {
//			String key = entry.getKey().toString();
//			String value = entry.getValue().toString();
//			logger.info("key=" + key + " value=" + value);
//		}

		return buffer.toString();
	}

	public static void main(String[] args) {
		String file = FileUtil.readFile("D:\\splash_origin.js");

		// PositionJs js = getPositionJs(file);
		List<Text> texts = parse(file);
		for (Text text : texts) {
			System.out.println(text);
		}
		
		Map<String,String> map = New.hashMap();
		 for (Entry<String, String> entry : map.entrySet()) {
			 
		 }
		
	}

}
