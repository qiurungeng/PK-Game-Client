
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
 * 	��˵�� ��Ϸ����
 * 
 */
// �̳д�����
public class GameFrame extends JFrame {

	// ������
	public GameFrame() {
		// ���ô���ߴ�
		setSize(1067, 600);
		// ���þ�����ʾ
		setLocationRelativeTo(null);
		// ���ùرմ���ʱ�ر���Ϸ
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// ���ñ���
		setTitle("��������ս���ෲ");

		// ����Logo����
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
		// �Ѵ��崫��ȥ����ȡ���̼���
		GamePanel gPanel = new GamePanel(gameFrame);
		// ��ʼ��Ϸִ�д�������
		gPanel.myHero.action();
		// ���һ������
		gameFrame.add(gPanel);
		// ��ʾ����
		gameFrame.setVisible(true);// ��ʾ���� false���ر���

	}
}
