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

    // Ӣ�����ͣ������������ෲ
    public int type;
    // Ӣ��ID
	protected int id;
	// Ӣ�۵�ͼƬ
	protected BufferedImage bg;
	// x����
	protected int x;
	// y����
	protected int y;
	// ��
	protected int h;
	// ��
	protected int w;
	// ��ɫ����ķ���,����Ϊfalse,����Ϊtrue
	protected boolean faceToRight;
	
	// ǰ�������˵�ͼƬ����
	protected List<BufferedImage> forwardImgs,backwardsImgs;
	// ��ǰ��󹥻���ͼƬ����
	protected List<BufferedImage>  attackForwardImgs, attackBackwardsImgs;
	// ��ʼ��ͼƬ(�泯��)
	protected BufferedImage initImgLeft;
	// ��ʼ��ͼƬ(�泯�ҷ�)
	protected BufferedImage initImgRight;
	// ����ͼƬ
	protected BufferedImage beHurtImgLeft;
	protected BufferedImage beHurtImgRight;
	// ����ͼƬ
	protected BufferedImage dieImg;
	// ��ǰ���������ͼƬ��������
	private int index;
	// ��ǰ����󹥻���ͼƬ��������
	protected int attack_index = 0;
	
	// ���ڻ��洰��
	GamePanel panel;
	// ����������״̬:�ϡ��¡����ҡ�����������
	boolean up,down,right,left,attack,behurt=false;
	// �߳�ʱ��Ƭ��С
	int time_slice=100;
	int step_length=10;
	
	//Ѫ
	int blood;
	//��
	int magic;
	//�������
	boolean deadFlag=false;

	String name;
	//����Ķ����߳�
	private ExecutorService exec=Executors.newFixedThreadPool(1);
	//���ڽ��й�����������������ʧЧ
	private boolean doing_attack;

	// ��Ӣ�۵ļ���������
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
	
	// ��ʼͼƬ(�����κζ���ʱ��ͼƬ)
	public void initImg() {
		if (faceToRight) {
			bg=initImgRight;
		}else {
			bg=initImgLeft;
		}
	}
	
	// �ƶ�
	public void move() {
		if (faceToRight) {
			moveForward();
		}else {
			moveBackwards();
		}
	}
	
	// ��ǰ�ƶ�
	private void moveForward() {
		if (index == forwardImgs.size()) {
			index = 0;
		}
		bg = forwardImgs.get(index);
		index++;
	}
	
	// ����ƶ�
	private void moveBackwards() {
		if (index == backwardsImgs.size()) {
			index = 0;
		}
		bg = backwardsImgs.get(index);
		index++;
	}
	
	// ��������
	private void attackOpponent() {
		//��Ӣ��Ϊ�ͻ������������Ӣ�ۣ�����������͹���֪ͨ
		if (id==panel.getMyHero().getId()){
			System.out.println("Player:"+id+" want to attack");
			HeroAttackMsg attackMsg=new HeroAttackMsg(this);
			panel.getClient().sendVitalMsg(attackMsg);
		}
	}
	
	// �������ֱ
	public void beHurt() {
		System.out.println("[Hero]"+id+":beHurt() is called");
		exec.execute(new BeHurt());
	}
	
	// ����
	private void magicRecovery() {
		if (magic<100) {
			this.magic+=1;
		}
	}

	
	/**
	 * Ӣ�� ������ �̣߳�ÿ��ʱ��Ƭ�ж�״̬ �ػ�һ��
	 */
	public void action() {
		new Thread() {
			@Override
			public void run() {
				while (true) {
					
					// �߳�����
					try {
						Thread.sleep(100);
						panel.repaint();// ˢ��
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					//������ʱǿ��Continueѭ����ֻ���ֱ���ͼƬ�����ߣ�������Ӧ�κζ���
					if (behurt) {
						bg=faceToRight?beHurtImgRight:beHurtImgLeft;
						continue;
					}
					
					//�����й�������ʱ������������Ӧ��ʧЧ
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
					
					
					// ����ʱ������ѭ���������߳�
					if (deadFlag) {
						bg=dieImg;
						break;
					}
					
					//ÿ��ʱ��Ƭ�ص���
					magicRecovery();
					
				}

			}
		}.start();
	}
	
	
	//���������߳���
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
	
	//�������߳���
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
