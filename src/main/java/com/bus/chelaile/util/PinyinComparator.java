package com.bus.chelaile.util;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;

public class PinyinComparator implements Comparator<Object>{
	  public int compare(Object o1, Object o2) {  
	        char c1 = ((String) o1).charAt(0);  
	        char c2 = ((String) o2).charAt(0);  
	        return concatPinyinStringArray(  
	                PinyinHelper.toHanyuPinyinStringArray(c1)).compareTo(  
	                concatPinyinStringArray(PinyinHelper  
	                        .toHanyuPinyinStringArray(c2)));  
	    }  
	    private String concatPinyinStringArray(String[] pinyinArray) {
	        StringBuffer pinyinSbf = new StringBuffer();  
	        if ((pinyinArray != null) && (pinyinArray.length > 0)) {  
	            for (int i = 0; i < pinyinArray.length; i++) {  
	                pinyinSbf.append(pinyinArray[i]);  
	            }  
	        }  
	        return pinyinSbf.toString();  
	    }
	    
	    public static void main(String[] args) {
	    	
	    	String zw = "找";
	    	
	    	System.out.println(zw.getBytes().length);
	    	
	    	zw = "b";
	    	
	    	System.out.println(zw.getBytes().length);
	    	
	    	String[] arr = { "am", "b","A2","s8","S2","B"};  
	    	 Comparator cmp = Collator.getInstance(java.util.Locale.CHINA);
	        List<String> list = Arrays.asList(arr);  
	        
	        java.util.Arrays.sort(arr,String.CASE_INSENSITIVE_ORDER);
	        
	        
	      //  Arrays.sort(arr, new PinyinComparator());  
	        System.out.println(list);  
		}
	
}
