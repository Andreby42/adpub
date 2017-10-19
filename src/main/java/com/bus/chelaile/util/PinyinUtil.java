package com.bus.chelaile.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 汉字转拼音
 * 
 * @author zzz
 * 
 */
public class PinyinUtil {

	private static final Logger log = LoggerFactory.getLogger(PinyinUtil.class);
	
	// key	中文大写数字,value	小写数字
	public static Map<String,String> capitalizationMap = New.hashMap();

	static{
		capitalizationMap.put("零", "0");
		capitalizationMap.put("一", "1");
		capitalizationMap.put("二", "2");
		capitalizationMap.put("三", "3");
		capitalizationMap.put("四", "4");
		capitalizationMap.put("五", "5");
		capitalizationMap.put("六", "6");
		capitalizationMap.put("七", "7");
		capitalizationMap.put("八", "8");
		capitalizationMap.put("九", "9");
		capitalizationMap.put("十", "10");
	}
	
	/**
	 * 转化成小写
	 * @param filterKey
	 * @return
	 */
	public static String converToxiaoxie(String filterKey){
		StringBuffer buffer = new StringBuffer();
		for( int i = 0;i < filterKey.length();i++ ){
			String ak = String.valueOf(filterKey.charAt(i));
			String value = PinyinUtil.capitalizationMap.get(ak);
			if( value != null ){
				buffer.append(value);
			}else{
				buffer.append(ak);
			}
		}
		return buffer.toString();
	}
	
	/**
	 * 过滤掉符号
	 * @param name
	 * @return
	 */
	public static String filterSign(String name){
		String filterName = name.replaceAll("[\\pP\\p{Punct}]", "");
		return  filterName.replaceAll(" ", "");
	}

	public static String getPinYin(String src) {
		char[] t1 = null;
		t1 = src.toCharArray();
		String[] t2 = new String[t1.length];
		// 设置汉字拼音输出的格式
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		try {
			for (int i = 0; i < t0; i++) {
				// 判断是否为汉字字符
				if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);// 将汉字的几种全拼都存到t2数组中
					t4 += t2[0];// 取出该汉字全拼的第一种读音并连接到字符串t4后
				} else {
					// 如果不是汉字字符，直接取出字符并连接到字符串t4后
					t4 += Character.toString(t1[i]);
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			log.error("getPinYin error.", e);
		}
		return t4;
	}

	public static String getPinYinHeadChar(String str) {
		String convert = "";
		for (int j = 0; j < str.length(); j++) {
			char word = str.charAt(j);
			// 提取汉字的首字母
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null) {
				convert += pinyinArray[0].charAt(0);
			} else {
				convert += word;
			}
		}
		return convert;
	}

	public static String getCnASCII(String cnStr) {
		StringBuffer strBuf = new StringBuffer();
		// 将字符串转换成字节序列
		byte[] bGBK = cnStr.getBytes();
		for (int i = 0; i < bGBK.length; i++) {
			strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
		}
		return strBuf.toString();
	}
	/**
	 * false 非数字,true 纯数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

//		System.out.println("12332支".matches(".*\\p{Alpha}.*"));
		String s = String.valueOf("支123".charAt(1));
		System.out.println(s.getBytes().length);
		//System.out.println(isNumeric("3路"));
		 String cnStr = "中华人民共和国12（A-C，‘’。-,./f）";
		// System.out.println(getPinYin(cnStr));
		 System.out.println(getPinYinHeadChar(cnStr));
		// System.out.println(getCnASCII(cnStr));
		//
		//
		 String str = "!!！？？!!!!%*）$#%￥^%$%^&*()!@#$%^&*^&*!@#$%^&*()d！@#￥%……&*（）！KTV去符号标号！！当然,，。!!..**半 角'‘’（） ()，";
		// str = " 1504(M)";
		 System.out.println(str);
		 String str1 =filterSign(str);
		 System.out.println("str1:" + str1);
		//
		//
		// String str2 = str.replaceAll("[//pP]", "");
		// System.out.println("str2:" + str2);
		//
		//
		// String str3 = str.replaceAll("[//p{P}]", "");
		// System.out.println("str3:" + str3);
	}

}
