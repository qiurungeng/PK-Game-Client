
package ui;

import network.HeroAction;
import network.HeroMoveMsg;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author Winston Smith
 * 	Ӣ�ۣ�������
 */
public class HeroCaiXuKun extends Hero {

	public HeroCaiXuKun(int id, int x, int y) {
		super();
		name="������";
		this.type=TYPE_CXK;
		this.id=id;
		// ��ȡ��������ʼͼƬ
		initImgLeft= ImageUtil.getImg("/img/����������1.png");
		initImgRight= ImageUtil.getImg("/img/����������1.png");
		bg = initImgRight;
		// �����������ͼƬ
		beHurtImgLeft= ImageUtil.getImg("/img/����������L.png");
		beHurtImgRight= ImageUtil.getImg("/img/����������R.png");
		// ���������˵�ͼƬ
		dieImg= ImageUtil.getImg("/img/����������.png");
		// �ճ���ʱ������
		this.x = x;
		this.y = y;
		forwardImgs = new ArrayList<BufferedImage>();
		for (int i = 1; i <= 8; i++) {
			forwardImgs.add(ImageUtil.getImg("/img/����������" + i + ".png"));

		}
		backwardsImgs = new ArrayList<BufferedImage>();
		for (int i = 1; i <= 8; i++) {
			backwardsImgs.add(ImageUtil.getImg("/img/����������" + i + ".png"));

		}
		
		init(bg, x, y, forwardImgs, backwardsImgs);
		
		attackForwardImgs = new ArrayList<BufferedImage>();
		for (int i = 1; i <= 8; i++) {
			attackForwardImgs.add(ImageUtil.getImg("/img/����������" + i + ".png"));

		}
		attackBackwardsImgs = new ArrayList<BufferedImage>();
		for (int i = 1; i <= 8; i++) {
			attackBackwardsImgs.add(ImageUtil.getImg("/img/����������" + i + "L.png"));
		}

	}
	
    public HeroCaiXuKun(){
	    this(0,591,220);
    }

}
