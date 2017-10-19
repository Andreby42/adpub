package com.bus.chelaile.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class FILES {
	
	/**
	 * 判断文件是否存在
	 * @param path			路径
	 * @param name			文件绝对路径
	 * @return				true:存在,false:不存在
	 */
	public static boolean fileIsExist(String path,String name) {
		File file = new File(path);
		for (String temp : file.list()) {
			temp = path+temp;
			if( temp.equals(name) ){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 保存文件
	 * @param is
	 * @param fos
	 * @throws IOException
	 */
	public static void saveFile(InputStream is,FileOutputStream fos) throws IOException{
        int byte_read;
        byte[] buffer = new byte[1024];

        while (-1 != (byte_read = is.read(buffer)))
        {
            fos.write(buffer, 0, byte_read);
            fos.flush();
        }
	}

	public static void main(String args[]) {
		fileIsExist("D:\\temp\\bus","75_007.csv");// 指定遍历的目录
	}
}
