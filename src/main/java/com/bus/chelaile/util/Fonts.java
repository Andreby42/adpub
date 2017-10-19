package com.bus.chelaile.util;

import java.io.UnsupportedEncodingException;

public class Fonts {
	public static String big5ToChinese( String s )
	{
	    try{
	        if ( s == null || s.equals( "" ) )
	            return("");
	        String newstring = null;
	        newstring = new String( s.getBytes( "big5" ), "gb2312" );
	        return(newstring);
	    }
	    catch ( UnsupportedEncodingException e )
	    {
	    	e.printStackTrace();
	        return(s);
	    }
	}


	public static String ChineseTobig5( String s )
	{
	    try{
	        if ( s == null || s.equals( "" ) )
	            return("");
	        String newstring = null;
	        newstring = new String( s.getBytes( "gb2312" ), "big5" );
	        return(newstring);
	    }
	    catch ( UnsupportedEncodingException e )
	    {
	    	e.printStackTrace();
	        return(s);
	    }
	}
	
	public static void main(String[] args) {
		System.out.println(ChineseTobig5("重慶"));
	}
}
