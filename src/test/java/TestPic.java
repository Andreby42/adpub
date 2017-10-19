

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class TestPic {

//    public static String path = "d:/temp";
//
//    public static void main(String[] args) throws IOException {
//    	
//    	System.out.println(URLEncoder.encode("车来了"));
//    	
//        File file1 = new File(path, "1.jpg");
//        File file2 = new File(path, "2.jpg");
//     //   mergeImage(file1, file2);
//
//         test(file1,file2);
//    }
//
//    public static void mergeImage(File file1, File file2) throws IOException {
//        BufferedImage image1 = ImageIO.read(file1);
//        BufferedImage image2 = ImageIO.read(file2);
//
//        //Font font = new Font("宋体", Font.BOLD, 48);
//        BufferedImage combined = new BufferedImage(image1.getWidth(), image1.getHeight(), BufferedImage.TYPE_INT_RGB);
//        // paint both images, preserving the alpha channels
//        Graphics2D  g2 =  combined.createGraphics();
//        g2.drawImage(image1, 0, 0, null);
//     //   g.drawImage(image2, 55, 253, null);
//        g2.setColor(Color.WHITE);
//        
//        Font font = new Font(".萍方-简", Font.PLAIN, 48);
//        g2.setFont(font);
//        g2.setPaint(Color.white);
//       
//        
//        String str="今日特惠大牌特惠";
//        // http://blog.csdn.net/piaoxuwuyu/article/details/50374109
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);  
//       // Rectangle2D bounds = font.getStringBounds(str, context);
//        double x = 750 / 2 - 48 * 4;
//        double y =660+48-5;
//
//       
////        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
////                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//
//       
//
//       // e.Graphics.DrawString("文本内容", font, fontBrush, new Rectangle(0, 35, 700, 40), sf);
//        
//      
//        
//        g2.drawString(str, (int) x, (int) y);
//        
//        str = "满268减20";
//        
//        y += 48 + 17;
//        
//        x = 750 / 2 - 241 / 2  ;
//        
//        int size = g2.getFontMetrics().stringWidth("满268减20");
//        
//        System.out.println(size);
//        
//        g2.drawString(str, (int) x, (int) y);
//
//        // Save as new image
//        
//        ImageIO.write(combined, "JPG", new File(path, "3.jpg"));
//        System.out.println("111111");
//    }
//    //
//    //
//     public static void test(File file1,File file2) throws IOException{
//    
//    	 BufferedImage image1 = ImageIO.read(file1);  
//         BufferedImage image2 = ImageIO.read(file2);  
//   
//         BufferedImage combined = new BufferedImage(image1.getWidth(), image1.getHeight(), BufferedImage.TYPE_INT_RGB);  
//   
//         // paint both images, preserving the alpha channels  
//         Graphics g = combined.getGraphics();  
//         g.drawImage(image1, 0, 0, null);  
//         g.drawImage(image2, 55, 253,640,360,null);  
//           
//         // Save as new image  
//         ImageIO.write(combined, "JPG", new File(path, "7.jpg"));  
//     }
//
//    
//    //
//    // public static void main(String[] args) {
//    // TestPic jc = new TestPic();
//    // jc.changeImage("d:/temp/", "2.jpg", "4.jpg", 1);
//    // }

	
		public static void main(String[] args) {
			//创建容器
			JFrame frame= new JFrame("G特卖");
			//创建布局管理器
			frame.setBounds(300, 100, 500, 600);//设置窗体位置和大小
			frame.setVisible(true);//设置窗体可见
			JLabel jl=new JLabel("让您吃的开心，吃的放心！");
			frame.add(jl);
			JLabel jl1=new JLabel("您的微笑是对我们最大的好评！");
			frame.add(jl1);
			jl.setFont(new Font("宋体",Font.BOLD,20));
			jl.setBounds(0,0, 300, 30);
			jl1.setFont(new Font("宋体",Font.BOLD,20));
			jl1.setBounds(200,-10, 300, 100);
			frame.setLayout(null);
			
			JPanel jp=new DrawPanel();
			frame.add(jp);
			Graphics g=jp.getGraphics();
		}
		
}
