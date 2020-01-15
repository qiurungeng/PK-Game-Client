
package ui;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author Winston Smith
 * 	Ӣ�ۣ����ෲ
 */

public class HeroWuYiFan extends Hero {

	public HeroWuYiFan(int id,int x,int y) {
		super();
		name="���ෲ";
		this.type=TYPE_WYF;
		this.id=id;
		// ��ȡ���ෲ��ʼͼƬ
		initImgLeft= ImageUtil.getImg("/img/���ෲǰ��L1.png");
		initImgRight= ImageUtil.getImg("/img/���ෲǰ��R1.png");
		this.bg = initImgLeft;
		// ���ෲ�����ͼƬ
		beHurtImgLeft= ImageUtil.getImg("/img/���ෲ����L.png");
		beHurtImgRight= ImageUtil.getImg("/img/���ෲ����R.png");
		// ���ෲ���˵�ͼƬ
		dieImg= ImageUtil.getImg("/img/���ෲ����.png");
		// �ճ���ʱ������
		this.x = x;
		this.y = y;
		// ��ǰͼƬ��
		this.forwardImgs=new ArrayList<>();
		for (int i = 1; i <= 6; i++) {
			this.forwardImgs.add(ImageUtil.getImg("/img/���ෲǰ��R" + i + ".png"));
		}
		//���ͼƬ��
		this.backwardsImgs=new ArrayList<>();
		for (int i = 1; i <= 6; i++) {
			this.backwardsImgs.add(ImageUtil.getImg("/img/���ෲǰ��L" + i + ".png"));
		}
		
		init(bg, x, y, forwardImgs, backwardsImgs);
		
		attackForwardImgs = new ArrayList<BufferedImage>();
		for (int i = 0; i <= 7; i++) {
			attackForwardImgs.add(ImageUtil.getImg("/img/���ෲ����R" + i + ".png"));

		}
		attackBackwardsImgs = new ArrayList<BufferedImage>();
		for (int i = 0; i <= 7; i++) {
			attackBackwardsImgs.add(ImageUtil.getImg("/img/���ෲ����L" + i + ".png"));
		}
	}

	public HeroWuYiFan(){
		this(0,591,220);
	}

}
