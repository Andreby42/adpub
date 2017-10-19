package com.bus.chelaile.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	/**
	 * 数字返回true
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if (str.equals("")) {
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * 字母返回true
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isLetter(String str) {
		if (str.equals("")) {
			return false;
		}
		Pattern pattern = Pattern.compile("[a-zA-Z]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	public static String listToString(List<String> list, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
			sb.append(separator);
		}
		return sb.toString().substring(0, sb.toString().length() - 1);
	}

	public static void main(String[] args) {
		System.out.println(isLetter("安"));
	}
}
