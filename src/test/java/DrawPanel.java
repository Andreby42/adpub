import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class DrawPanel extends JPanel {

	/**
	 * 
	 */

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		// 两点确定一条直线. 三个点(不在同一直线)两两连接就可以成为三角形
		g.setColor(Color.RED);// 设置第一条线的颜色
		g.drawLine(50, 50, 100, 100);// 画第一条线 点(50,50) 到点 (100,100)
		g.setColor(Color.BLUE);
		g.drawLine(50, 50, 50, 150);// 画第二条线 点(50,50) 到点 (50,150)
		g.setColor(Color.GREEN);
		g.drawLine(50, 150, 100, 100);// 画第三条线 点(50,150) 到点 (100,100)
	}
}
