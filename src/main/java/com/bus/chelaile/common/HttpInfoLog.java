package com.bus.chelaile.common;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;







/**
 * 需要单独记录的info
 * @author zzz
 *
 */
public class HttpInfoLog {

	private static final Logger  log = LoggerFactory.getLogger(HttpInfoLog.class);
	

	
	public static void info(String context){
		log.info(context);
		
	}
	
	public static void info(String arg0, Object... arg1){
		log.info(arg0, arg1);
		
	}
	
	public static void error(String error,Exception e){
		log.error(error,e);
	}
	
	public static void error(String error){
		log.error(error);
	}
}
