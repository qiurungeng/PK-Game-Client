
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
 *	类说明 游戏画板类
 * 
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class GamePanel extends JPanel {
	//游戏背景图片
	BufferedImage bg;
	//在线玩家列表
	List<Hero> heroes=new ArrayList<Hero>();
	//我的英雄
	Hero myHero;
	//udp通信端口
	int udp_normal;
	int udp_vital;
	// 窗体
	GameFrame fr;
    //与服务器通信的 客户端
    Client client;
    //赢家ID
	int winner=-1;
    // 根据纵坐标排序的英雄优先队列
    PriorityQueue<Hero> priorityQueue= new PriorityQueue<>((o1, o2) -> {
        int diff = o1.y - o2.y;
        if (diff == 0) return 0;
        else if (diff > 0) return 1;
        else return -1;
    });

	GamePanel(GameFrame gameFrame) {
		setLayout(null);
		//窗体
		this.fr = gameFrame;
		//设置游戏配置
		setGameConfigDialog(gameFrame,this);
		//通过Client连接到服务器
		init(udp_normal,udp_vital);
		//关闭窗口时断开游戏连接
		fr.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				client.disconnect();
			}
		});
		// 设置背景图片
		bg = ImageUtil.getImg("/img/bg4.jpg");
		//监听玩家键盘
		keyListener();
		myHero.panel=this;
		heroes.add(myHero);
	}

	// 画图方法 paint虚拟机自动调用
	@Override
	public void paint(Graphics g) {

		super.paint(g);
		g.drawImage(bg, 0, 0, null);
		//血
		g.setColor(Color.red);
		g.fillRect(267+(int)(((double)(100- myHero.blood)/100)*700), 0, (int)(((double) myHero.blood/100)*700), 30);
		//蓝
		g.setColor(Color.blue);
		g.fillRect(267+(int)(((double)(100- myHero.magic)/100)*700), 32, (int)(((double) myHero.magic/100)*700), 15);

		//顶部角色头像
		if (myHero.getType()==Hero.TYPE_CXK){
			g.drawImage(ImageUtil.getImg("/img/Cai_head.jpg"), 970, 2, 90, 45, null);
		}else {
			g.drawImage(ImageUtil.getImg("/img/Wu_head.jpg"), 970, 2, 90, 45, null);
		}

		g.setColor(Color.green);
		g.setFont(new Font("宋体", Font.BOLD, 50));
		g.drawString(""+myHero.getName()+myHero.getId(), 10, 48);

		// 画出所有联网英雄
		paintHeroes(g);

		if (winner!=-1){
			g.setColor(Color.yellow);
			g.setFont(new Font("宋体", Font.BOLD, 50));
			g.drawString("恭喜玩家"+winner+"取得了胜利！", 200, 200);
		}
	}

	// 监我的英雄键盘
    private void keyListener() {
		myHero.panel=this;
		// 将键盘适配器加入到监听器中 键盘监听器必须加在窗体上面 问题：面板里控制移动
		fr.addKeyListener(myHero.adapter);
	}

	// 按纵坐标依次画出英雄
    private void paintHeroes(Graphics g){
        priorityQueue.addAll(heroes);
        while (!priorityQueue.isEmpty()){
            Hero toPaint=priorityQueue.poll();
            //名字
            g.setColor(Color.yellow);
            g.setFont(new Font("宋体", Font.BOLD, 15));
            g.drawString(toPaint.name+toPaint.id, toPaint.x, toPaint.y-10);
            //血
            g.setColor(Color.red);
            g.fillRect(toPaint.x, toPaint.y-5, (int)(((double)toPaint.blood/100)*toPaint.w), 5);
            //蓝
            g.setColor(Color.blue);
            g.fillRect(toPaint.x, toPaint.y, (int)(((double)toPaint.magic/100)*toPaint.w), 5);
            //英雄图片
            g.drawImage(toPaint.bg, toPaint.x, toPaint.y, toPaint.w, toPaint.h, null);
        }
    }

    /**
     * 联网初始化
     */
	public void init(int udp_normal,int udp_vital){
	    Client client =new Client(this,udp_normal,udp_vital);
	    client.connect("192.168.226.130",4399);
    }

    /**
     * 新连接到服务器的玩家，在本地将它加入英雄列表，开启动作线程
     * @param hero
     */
	public void addHero(Hero hero){
        System.out.println("new Hero:"+hero.getId()+" is added");
        hero.setPanel(this);
        hero.action();
        heroes.add(hero);
	}

	/**
	 * 处理联机玩家死亡消息
	 * @param deadHeroId 死亡玩家ID
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
	 * 处理联机玩家获胜消息
	 * @param winnerId 获胜玩家ID
	 */
	public void handleWinMsg(int winnerId) {
		this.winner=winnerId;
	}

	/**
	 * 游戏设置对话框
	 * 设置端口号，选择英雄
	 */
	private static void setGameConfigDialog(Frame owner, GamePanel gamePanel) {
		// 创建一个模态对话框
		final JDialog dialog = new JDialog(owner, "游戏设置", true);
		// 设置对话框的宽高
		dialog.setSize(500, 110);
		// 设置对话框大小不可改变
		dialog.setResizable(false);
		// 设置对话框相对显示的位置
		dialog.setLocationRelativeTo(gamePanel);

		// 创建一个标签显示消息内容
		JLabel udp1Label = new JLabel("普通消息端口：");
		JTextField udp1TF=new JTextField("2220",4);
		JLabel udp2Label = new JLabel("重要消息端口：");
		JTextField udp2TF=new JTextField("2221",4);
		JLabel heroLabel=new JLabel("英雄选择：");
		JComboBox<String> heroCB=new JComboBox<>();
		heroCB.addItem("蔡徐坤");
		heroCB.addItem("吴亦凡");

		// 创建一个按钮用于确认并关闭对话框
		JButton okBtn = new JButton("确定");
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
				if(choice.equals("蔡徐坤")){
					gamePanel.myHero=new HeroCaiXuKun();
				}else gamePanel.myHero=new HeroWuYiFan();
				// 关闭对话框
				dialog.dispose();
			}
		});
		// 创建对话框的内容面板, 在面板内可以根据自己的需要添加任何组件并做任意是布局
		JPanel dialogPanel = new JPanel();
		// 添加组件到面板
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
		// 设置对话框的内容面板
		dialog.setContentPane(dialogPanel);
		// 显示对话框
		dialog.setVisible(true);
	}
}
