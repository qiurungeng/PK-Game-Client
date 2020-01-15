
package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * 
 * @author Winston Smith
 * 
 * 	类说明 游戏窗体
 * 
 */
// 继承窗体类
public class GameFrame extends JFrame {

	// 构造器
	public GameFrame() {
		// 设置窗体尺寸
		setSize(1067, 600);
		// 设置居中显示
		setLocationRelativeTo(null);
		// 设置关闭窗体时关闭游戏
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// 设置标题
		setTitle("蔡徐坤大战吴亦凡");

		// 设置Logo标题
		try {
			setIconImage(ImageIO.read(GameFrame.class.getResource("")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

			}
		});

	}

	public static void main(String[] args) {
		GameFrame gameFrame = new GameFrame();
		// 把窗体传过去，获取键盘监听
		GamePanel gPanel = new GamePanel(gameFrame);
		// 开始游戏执行窗口任务
		gPanel.myHero.action();
		// 添加一个画板
		gameFrame.add(gPanel);
		// 显示窗体
		gameFrame.setVisible(true);// 显示窗体 false隐藏标题

	}
}
