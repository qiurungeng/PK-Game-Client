
package ui;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author Winston Smith
 * 	英雄：吴亦凡
 */

public class HeroWuYiFan extends Hero {

	public HeroWuYiFan(int id,int x,int y) {
		super();
		name="吴亦凡";
		this.type=TYPE_WYF;
		this.id=id;
		// 获取吴亦凡初始图片
		initImgLeft= ImageUtil.getImg("/img/吴亦凡前进L1.png");
		initImgRight= ImageUtil.getImg("/img/吴亦凡前进R1.png");
		this.bg = initImgLeft;
		// 吴亦凡被打的图片
		beHurtImgLeft= ImageUtil.getImg("/img/吴亦凡被打L.png");
		beHurtImgRight= ImageUtil.getImg("/img/吴亦凡被打R.png");
		// 吴亦凡死了的图片
		dieImg= ImageUtil.getImg("/img/吴亦凡死了.png");
		// 刚出现时的坐标
		this.x = x;
		this.y = y;
		// 向前图片集
		this.forwardImgs=new ArrayList<>();
		for (int i = 1; i <= 6; i++) {
			this.forwardImgs.add(ImageUtil.getImg("/img/吴亦凡前进R" + i + ".png"));
		}
		//向后图片集
		this.backwardsImgs=new ArrayList<>();
		for (int i = 1; i <= 6; i++) {
			this.backwardsImgs.add(ImageUtil.getImg("/img/吴亦凡前进L" + i + ".png"));
		}
		
		init(bg, x, y, forwardImgs, backwardsImgs);
		
		attackForwardImgs = new ArrayList<BufferedImage>();
		for (int i = 0; i <= 7; i++) {
			attackForwardImgs.add(ImageUtil.getImg("/img/吴亦凡攻击R" + i + ".png"));

		}
		attackBackwardsImgs = new ArrayList<BufferedImage>();
		for (int i = 0; i <= 7; i++) {
			attackBackwardsImgs.add(ImageUtil.getImg("/img/吴亦凡攻击L" + i + ".png"));
		}
	}

	public HeroWuYiFan(){
		this(0,591,220);
	}

}
