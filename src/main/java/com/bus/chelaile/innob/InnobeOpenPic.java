package com.bus.chelaile.innob;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InnobeOpenPic {
	
	//private static String picUrlPath = PropertiesReaderWrapper.read("innobeOpenPicNewPath");
	protected static final Logger logger = LoggerFactory.getLogger(InnobeOpenPic.class);
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
	 //      File file1 = new File(picUrlPath, "template.jpg");
	 //       File file2 = new File(picUrlPath, "2.jpg");
	        
	    mergeImage("d:/temp/template.jpg", "d:/temp/2.jpg", "京东白条是最棒的白条，随便用随便花不用换，呵呵呵呵呵", "中华人民共和国中华人!,民共和国你好啊世界大", "d:/temp/", "6.jpg");  
	}

	public static void mergeImage(String templateName, String originalName,String title,String desc,String path,String picName) throws IOException {
		File file = new File (path+picName);
		
		if( file.exists() ){
			return ;
		}
		
		File file1 = new File(templateName);
	   File file2 = new File(originalName);
		
		
		BufferedImage image1 = ImageIO.read(file1);
		BufferedImage image2 = ImageIO.read(file2);

		BufferedImage combined = new BufferedImage(image1.getWidth(),
				image1.getHeight(), BufferedImage.TYPE_INT_RGB);

		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.drawImage(image1, 0, 0, null);
		g.drawImage(image2, 55, 253, 640, 360, null);
		g.setColor(Color.WHITE);

		setFont(combined.createGraphics(), title, desc);
		
		// Save as new image
		ImageIO.write(combined, "JPG", new File(path, picName));
		
		logger.info(path+picName);
		
	}
	
	
	
	  public static void setFont(Graphics2D  g2,String title,String desc) throws IOException {
	      //  BufferedImage image1 = ImageIO.read(file1);
	       // BufferedImage image2 = ImageIO.read(file2);

	        //Font font = new Font("宋体", Font.BOLD, 48);
	       // BufferedImage combined = new BufferedImage(image1.getWidth(), image1.getHeight(), BufferedImage.TYPE_INT_RGB);
	        // paint both images, preserving the alpha channels
	      //  Graphics2D  g2 =  combined.createGraphics();
	      //  g2.drawImage(image1, 0, 0, null);
	     //   g.drawImage(image2, 55, 253, null);
	        
	        
	        Font font = new Font(".萍方-简", Font.PLAIN, 30);
	        g2.setFont(font);
	        g2.setPaint(Color.white);
	       
	        
	       // String str="今日特惠大牌特惠";
	        // http://blog.csdn.net/piaoxuwuyu/article/details/50374109
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);  
	       
	        
	        String first = "";
	        String second = "";
	        if( title.length() > 12 ){
	        	String ele = title.substring(10, 11);
	        	first = title.substring(0,10);
	        	//	如果不是中文
	        	if(!ele.matches("[\\u4E00-\\u9FA5]+")){
	        		//	如果不是数字
	        		if( !isNumeric(ele) ){
	        			first += ele;
	        		}
	        	}
	        	
	        	second = title.substring(first.length(),title.length());
	        	
	        }else{
	        	first = title;
	        }
	        
	        int size = g2.getFontMetrics().stringWidth(first);
	        
	        
	        
	        double x = 750 / 2 - size/2;
	        double y =660+48-5;

	       
	        g2.drawString(first, (int) x, (int) y);
	        
	        if( second == null || second.equals("") ){
	        	return;
	        }
	        
	        if( second.length() > 10 ){
	        	second = second.substring(0,9);
	        	second += "...";
	        }
	        
	        size = g2.getFontMetrics().stringWidth(second);
	        
	        y += 48 + 17;
	        
	        x = 750 / 2 - size / 2  ;
	        
	       
	        
	       // System.out.println(size);
	        
	        g2.drawString(second, (int) x, (int) y);
	        
	        // Save as new image
	        
	      //  ImageIO.write(combined, "JPG", new File(path, "3.jpg"));
	      //  System.out.println("111111");
	    }
	  
	  
	  public static boolean isNumeric(String str) {
			if(  str.equals("") ){
				return false;
			}
			Pattern pattern = Pattern.compile("[0-9]*");
			Matcher isNum = pattern.matcher(str);
			if (!isNum.matches()) {
				return false;
			}
			return true;
		}
}
