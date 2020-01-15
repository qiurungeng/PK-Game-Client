
package ui;

import lombok.Data;
import lombok.EqualsAndHashCode;
import network.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 
 * @author Winston Smith
 *	��˵�� ��Ϸ������
 * 
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class GamePanel extends JPanel {
	//��Ϸ����ͼƬ
	BufferedImage bg;
	//��������б�
	List<Hero> heroes=new ArrayList<Hero>();
	//�ҵ�Ӣ��
	Hero myHero;
	//udpͨ�Ŷ˿�
	int udp_normal;
	int udp_vital;
	// ����
	GameFrame fr;
    //�������ͨ�ŵ� �ͻ���
    Client client;
    //Ӯ��ID
	int winner=-1;
    // ���������������Ӣ�����ȶ���
    PriorityQueue<Hero> priorityQueue= new PriorityQueue<>((o1, o2) -> {
        int diff = o1.y - o2.y;
        if (diff == 0) return 0;
        else if (diff > 0) return 1;
        else return -1;
    });

	GamePanel(GameFrame gameFrame) {
		setLayout(null);
		//����
		this.fr = gameFrame;
		//������Ϸ����
		setGameConfigDialog(gameFrame,this);
		//ͨ��Client���ӵ�������
		init(udp_normal,udp_vital);
		//�رմ���ʱ�Ͽ���Ϸ����
		fr.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				client.disconnect();
			}
		});
		// ���ñ���ͼƬ
		bg = ImageUtil.getImg("/img/bg4.jpg");
		//������Ҽ���
		keyListener();
		myHero.panel=this;
		heroes.add(myHero);
	}

	// ��ͼ���� paint������Զ�����
	@Override
	public void paint(Graphics g) {

		super.paint(g);
		g.drawImage(bg, 0, 0, null);
		//Ѫ
		g.setColor(Color.red);
		g.fillRect(267+(int)(((double)(100- myHero.blood)/100)*700), 0, (int)(((double) myHero.blood/100)*700), 30);
		//��
		g.setColor(Color.blue);
		g.fillRect(267+(int)(((double)(100- myHero.magic)/100)*700), 32, (int)(((double) myHero.magic/100)*700), 15);

		//������ɫͷ��
		if (myHero.getType()==Hero.TYPE_CXK){
			g.drawImage(ImageUtil.getImg("/img/Cai_head.jpg"), 970, 2, 90, 45, null);
		}else {
			g.drawImage(ImageUtil.getImg("/img/Wu_head.jpg"), 970, 2, 90, 45, null);
		}

		g.setColor(Color.green);
		g.setFont(new Font("����", Font.BOLD, 50));
		g.drawString(""+myHero.getName()+myHero.getId(), 10, 48);

		// ������������Ӣ��
		paintHeroes(g);

		if (winner!=-1){
			g.setColor(Color.yellow);
			g.setFont(new Font("����", Font.BOLD, 50));
			g.drawString("��ϲ���"+winner+"ȡ����ʤ����", 200, 200);
		}
	}

	// ���ҵ�Ӣ�ۼ���
    private void keyListener() {
		myHero.panel=this;
		// ���������������뵽�������� ���̼�����������ڴ������� ���⣺���������ƶ�
		fr.addKeyListener(myHero.adapter);
	}

	// �����������λ���Ӣ��
    private void paintHeroes(Graphics g){
        priorityQueue.addAll(heroes);
        while (!priorityQueue.isEmpty()){
            Hero toPaint=priorityQueue.poll();
            //����
            g.setColor(Color.yellow);
            g.setFont(new Font("����", Font.BOLD, 15));
            g.drawString(toPaint.name+toPaint.id, toPaint.x, toPaint.y-10);
            //Ѫ
            g.setColor(Color.red);
            g.fillRect(toPaint.x, toPaint.y-5, (int)(((double)toPaint.blood/100)*toPaint.w), 5);
            //��
            g.setColor(Color.blue);
            g.fillRect(toPaint.x, toPaint.y, (int)(((double)toPaint.magic/100)*toPaint.w), 5);
            //Ӣ��ͼƬ
            g.drawImage(toPaint.bg, toPaint.x, toPaint.y, toPaint.w, toPaint.h, null);
        }
    }

    /**
     * ������ʼ��
     */
	public void init(int udp_normal,int udp_vital){
	    Client client =new Client(this,udp_normal,udp_vital);
	    client.connect("192.168.226.130",4399);
    }

    /**
     * �����ӵ�����������ң��ڱ��ؽ�������Ӣ���б����������߳�
     * @param hero
     */
	public void addHero(Hero hero){
        System.out.println("new Hero:"+hero.getId()+" is added");
        hero.setPanel(this);
        hero.action();
        heroes.add(hero);
	}

	/**
	 * �����������������Ϣ
	 * @param deadHeroId �������ID
	 */
	public void killHeroById(int deadHeroId) {
		new Thread(() -> {
			for (Hero hero : heroes) {
				if (hero.getId()==deadHeroId){
					hero.setDeadFlag(true);
					break;
				}
			}
		}).start();
	}

	/**
	 * ����������һ�ʤ��Ϣ
	 * @param winnerId ��ʤ���ID
	 */
	public void handleWinMsg(int winnerId) {
		this.winner=winnerId;
	}

	/**
	 * ��Ϸ���öԻ���
	 * ���ö˿ںţ�ѡ��Ӣ��
	 */
	private static void setGameConfigDialog(Frame owner, GamePanel gamePanel) {
		// ����һ��ģ̬�Ի���
		final JDialog dialog = new JDialog(owner, "��Ϸ����", true);
		// ���öԻ���Ŀ��
		dialog.setSize(500, 110);
		// ���öԻ����С���ɸı�
		dialog.setResizable(false);
		// ���öԻ��������ʾ��λ��
		dialog.setLocationRelativeTo(gamePanel);

		// ����һ����ǩ��ʾ��Ϣ����
		JLabel udp1Label = new JLabel("��ͨ��Ϣ�˿ڣ�");
		JTextField udp1TF=new JTextField("2220",4);
		JLabel udp2Label = new JLabel("��Ҫ��Ϣ�˿ڣ�");
		JTextField udp2TF=new JTextField("2221",4);
		JLabel heroLabel=new JLabel("Ӣ��ѡ��");
		JComboBox<String> heroCB=new JComboBox<>();
		heroCB.addItem("������");
		heroCB.addItem("���ෲ");

		// ����һ����ť����ȷ�ϲ��رնԻ���
		JButton okBtn = new JButton("ȷ��");
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String udp1_=udp1TF.getText();
				String udp2_=udp2TF.getText();
				String choice=(String) heroCB.getSelectedItem();
				System.out.println(udp1_+","+udp2_+","+choice);
				gamePanel.udp_normal=Integer.parseInt(udp1_);
				gamePanel.udp_vital=Integer.parseInt(udp2_);
				assert choice != null;
				if(choice.equals("������")){
					gamePanel.myHero=new HeroCaiXuKun();
				}else gamePanel.myHero=new HeroWuYiFan();
				// �رնԻ���
				dialog.dispose();
			}
		});
		// �����Ի�����������, ������ڿ��Ը����Լ�����Ҫ����κ�������������ǲ���
		JPanel dialogPanel = new JPanel();
		// �����������
		dialogPanel.add(udp1Label);
		dialogPanel.add(udp1TF);
		dialogPanel.add(udp2Label);
		dialogPanel.add(udp2TF);
		dialogPanel.add(heroLabel);
		dialogPanel.add(heroCB);
		dialogPanel.add(okBtn);
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				okBtn.doClick();
			}
		});
		// ���öԻ�����������
		dialog.setContentPane(dialogPanel);
		// ��ʾ�Ի���
		dialog.setVisible(true);
	}
}
