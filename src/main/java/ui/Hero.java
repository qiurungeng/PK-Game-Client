package ui;


import lombok.Data;
import network.HeroAction;
import network.HeroAttackMsg;
import network.HeroMoveMsg;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Data
public class Hero {
    public static final int TYPE_CXK=0;
    public static final int TYPE_WYF=1;

    // 英雄类型：蔡徐坤或吴亦凡
    public int type;
    // 英雄ID
	protected int id;
	// 英雄的图片
	protected BufferedImage bg;
	// x坐标
	protected int x;
	// y坐标
	protected int y;
	// 高
	protected int h;
	// 宽
	protected int w;
	// 角色朝向的方向,向左为false,向右为true
	protected boolean faceToRight;
	
	// 前进及后退的图片数组
	protected List<BufferedImage> forwardImgs,backwardsImgs;
	// 向前向后攻击的图片数组
	protected List<BufferedImage>  attackForwardImgs, attackBackwardsImgs;
	// 初始化图片(面朝左方)
	protected BufferedImage initImgLeft;
	// 初始化图片(面朝右方)
	protected BufferedImage initImgRight;
	// 被打图片
	protected BufferedImage beHurtImgLeft;
	protected BufferedImage beHurtImgRight;
	// 死亡图片
	protected BufferedImage dieImg;
	// 向前及向后动作的图片数组索引
	private int index;
	// 向前及向后攻击的图片数组索引
	protected int attack_index = 0;
	
	// 所在画面窗口
	GamePanel panel;
	// 被监听按键状态:上、下、左、右、攻击、被打
	boolean up,down,right,left,attack,behurt=false;
	// 线程时间片大小
	int time_slice=100;
	int step_length=10;
	
	//血
	int blood;
	//蓝
	int magic;
	//死亡与否
	boolean deadFlag=false;

	String name;
	//额外的动作线程
	private ExecutorService exec=Executors.newFixedThreadPool(1);
	//正在进行攻击，将令其他按键失效
	private boolean doing_attack;

	// 该英雄的键盘适配器
	KeyAdapter adapter = new KeyAdapter() {

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            switch (key) {
                case KeyEvent.VK_LEFT:
                    left=false;
                    break;
                case KeyEvent.VK_RIGHT:
                    right=false;
                    break;
                case KeyEvent.VK_UP:
                    up=false;
                    break;
                case KeyEvent.VK_DOWN:
                    down=false;
                    break;
                case KeyEvent.VK_C:
                    attack=false;
                    break;
                default:
                    break;
            }

            HeroMoveMsg msg=new HeroMoveMsg(Hero.this.id,panel,
                    new HeroAction(Hero.this.x, Hero.this.y,up,down,left,right,attack));
            panel.getClient().send(msg);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
//				moveForward();
            switch (key) {
                case KeyEvent.VK_LEFT:
                    faceToRight=false;
                    left=true;
                    break;
                case KeyEvent.VK_RIGHT:
                    faceToRight=true;
                    right=true;
                    break;
                case KeyEvent.VK_UP:
                    up=true;
                    break;
                case KeyEvent.VK_DOWN:
                    down=true;
                    break;
                case KeyEvent.VK_C:
                    attack=true;
                    break;
                default:
                    break;
            }

            HeroMoveMsg msg=new HeroMoveMsg(Hero.this.id,panel,
                    new HeroAction(Hero.this.x, Hero.this.y,up,down,left,right,attack));
            panel.getClient().send(msg);
        }

    };


    public Hero() {

	}
	
	public void init(BufferedImage bg,int x,int y,List<BufferedImage> forwardImgs,List<BufferedImage> backwardsImgs) {
		this.bg=bg;
		this.x=x;
		this.y=y;
		h=bg.getHeight();
		w=bg.getWidth();
		this.forwardImgs=forwardImgs;
		this.backwardsImgs=backwardsImgs;
		index=0;
		blood=100;
		magic=100;
		
	}
	
	// 初始图片(即无任何动作时的图片)
	public void initImg() {
		if (faceToRight) {
			bg=initImgRight;
		}else {
			bg=initImgLeft;
		}
	}
	
	// 移动
	public void move() {
		if (faceToRight) {
			moveForward();
		}else {
			moveBackwards();
		}
	}
	
	// 向前移动
	private void moveForward() {
		if (index == forwardImgs.size()) {
			index = 0;
		}
		bg = forwardImgs.get(index);
		index++;
	}
	
	// 向后移动
	private void moveBackwards() {
		if (index == backwardsImgs.size()) {
			index = 0;
		}
		bg = backwardsImgs.get(index);
		index++;
	}
	
	// 攻击对手
	private void attackOpponent() {
		//若英雄为客户端玩家所控制英雄，向服务器发送攻击通知
		if (id==panel.getMyHero().getId()){
			System.out.println("Player:"+id+" want to attack");
			HeroAttackMsg attackMsg=new HeroAttackMsg(this);
			panel.getClient().sendVitalMsg(attackMsg);
		}
	}
	
	// 被击打后僵直
	public void beHurt() {
		System.out.println("[Hero]"+id+":beHurt() is called");
		exec.execute(new BeHurt());
	}
	
	// 回蓝
	private void magicRecovery() {
		if (magic<100) {
			this.magic+=1;
		}
	}

	
	/**
	 * 英雄 主动作 线程，每个时间片判断状态 重绘一次
	 */
	public void action() {
		new Thread() {
			@Override
			public void run() {
				while (true) {
					
					// 线程休眠
					try {
						Thread.sleep(100);
						panel.repaint();// 刷新
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					//被击打时强制Continue循环，只保持被打图片并休眠，不能响应任何动作
					if (behurt) {
						bg=faceToRight?beHurtImgRight:beHurtImgLeft;
						continue;
					}
					
					//当进行攻击动作时，其他按键都应该失效
					if (!doing_attack) {
						if (up) {
							y -= 10;
							if (y <= 0) {
								y = 0;
							}
						}
						if (down) {
							y += 10;
							if (y >= 600 - h) {
								y = 600 - h;
							}
						}
						if (left) {
						    faceToRight=false;
							x -= 10;
							if (x <= 0) {
								x = 0;
							}
						}
						if (right) {
						    faceToRight=true;
							x += 10;
							if (x >= 1067 - w) {
								x = 1067 - w;
							}
						}
						
						if (up||down||left||right) {
							move();
						}else if(!attack||!behurt){
							initImg();
						}
						
						if (attack) {
							if (magic>10) {
								exec.execute(new Attack());
							}
						}
					}
					
					
					// 死亡时，跳出循环，结束线程
					if (deadFlag) {
						bg=dieImg;
						break;
					}
					
					//每个时间片回点蓝
					magicRecovery();
					
				}

			}
		}.start();
	}
	
	
	//攻击动作线程类
	class Attack implements Runnable{
		List<BufferedImage> attackImgs=faceToRight?attackForwardImgs:attackBackwardsImgs;
		String name;
		@Override
		public void run() {
			doing_attack=true;
			
			magic-=10;
			for (BufferedImage bufferedImage : attackImgs) {
				bg=bufferedImage;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			attackOpponent();
			
			doing_attack=false;
		}
	}
	
	//被打动作线程类
	class BeHurt implements Runnable{
		@Override
		public void run() {
			behurt=true;
			System.out.println(name+":"+behurt);
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			behurt=false;
		}
	}



}
