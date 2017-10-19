package com.bus.chelaile.util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;

public class StorageLog {
	private static OutputStreamWriter osw = null ;
	private static long currentTime = System.currentTimeMillis() ;
	private static String prefix ="tokenAndUdid";
	private static String basePath = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "logPath","/data/logs/");
			
	
	public static void setPrefix(String p){
		prefix = p ;
	}
	
	public static void setBasePath(String path){
		basePath = path;
	}
	
	
	@SuppressWarnings("deprecation")
	public synchronized static void setPath() throws Exception{
			Date today = new Date() ;
			String logpath = basePath+prefix+"."+(today.getYear()+1900)+"-"+(today.getMonth()+1)+"-"+today.getDate()+".txt" ;
			try {
				if(osw!=null) osw.close();
				osw = new OutputStreamWriter(new FileOutputStream(new File(logpath), true),"UTF-8");
			} catch (Exception e) {
				throw e;
			} 
	}
	

	
	public static synchronized void log(List<String> list) throws Exception{
		try {
			if(osw==null) setPath();
			if(System.currentTimeMillis()-currentTime >600*1000){
				currentTime = System.currentTimeMillis();
				setPath() ;
			}
			for( String str : list ){
				osw.write(str);
				osw.write("\n");
			}
			osw.flush() ;
			
		}catch(Exception e){
			osw.close();
			throw e;
		}
	}
	
	public static String getNowTime(){
		try{
			String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date());
			return nowDate;
		}catch( Exception e ){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		
		
		for(int i=0;i<100;i++){
			//StorageLog.log("test") ;
			//Thread.sleep(100);
		}
	}

}
