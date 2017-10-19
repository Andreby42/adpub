package com.bus.chelaile.util;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class URLS {
	/**
	 * 下载文件
	 * @param file_url				请求url
	 * @param file_name				保存路径+文件名
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static int download_file(String file_url, String file_name) throws MalformedURLException,IOException
    {
        try
        {
            URL url = new URL(file_url);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            FileOutputStream fos = new FileOutputStream(file_name);

            FILES.saveFile(is, fos);

            fos.close();
            is.close();
      
            return 0;
        }
        catch (MalformedURLException e)
        {
            throw e;
        }
        catch (IOException e)
        {
        	throw e;
        }
 
    }
	
	
	
	public static String getRequestStr(String file_url,int timeout) throws MalformedURLException,IOException
    {
        try
        {
            URL url = new URL(file_url);
            URLConnection conn = url.openConnection();
            //	超时时间
            conn.setConnectTimeout(timeout);
            InputStream is = conn.getInputStream();
            
            ByteArrayOutputStream outstream=new ByteArrayOutputStream();  
            byte[] buffer=new byte[1024];  
            int len=-1;  
            while((len=is.read(buffer)) !=-1){  
                 outstream.write(buffer, 0, len);  
            }
            
            outstream.close();  
            is.close(); 
         
            return  new String(outstream.toByteArray(), "utf-8");
        }
        catch (MalformedURLException e)
        {
            throw e;
        }
        catch (IOException e)
        {
        	throw e;
        }
 
    }
}
