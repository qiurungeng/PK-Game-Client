
package ui;

import network.HeroAction;
import network.HeroMoveMsg;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author Winston Smith
 * 	”¢–€£∫≤Ã–Ï¿§
 */
public class HeroCaiXuKun extends Hero {

	public HeroCaiXuKun(int id, int x, int y) {
		super();
		name="≤Ã–Ï¿§";
		this.type=TYPE_CXK;
		this.id=id;
		// ªÒ»°≤Ã–Ï¿§≥ı ºÕº∆¨
		initImgLeft= ImageUtil.getImg("/img/≤Ã–Ï¿§––◊ﬂ1.png");
		initImgRight= ImageUtil.getImg("/img/≤Ã–Ï¿§––◊ﬂ1.png");
		bg = initImgRight;
		// ≤Ã–Ï¿§±ª¥ÚµƒÕº∆¨
		beHurtImgLeft= ImageUtil.getImg("/img/≤Ã–Ï¿§±ª¥ÚL.png");
		beHurtImgRight= ImageUtil.getImg("/img/≤Ã–Ï¿§±ª¥ÚR.png");
		// ≤Ã–Ï¿§À¿¡ÀµƒÕº∆¨
		dieImg= ImageUtil.getImg("/img/≤Ã–Ï¿§À¿¡À.png");
		// ∏’≥ˆœ÷ ±µƒ◊¯±Í
		this.x = x;
		this.y = y;
		forwardImgs = new ArrayList<BufferedImage>();
		for (int i = 1; i <= 8; i++) {
			forwardImgs.add(ImageUtil.getImg("/img/≤Ã–Ï¿§––◊ﬂ" + i + ".png"));

		}
		backwardsImgs = new ArrayList<BufferedImage>();
		for (int i = 1; i <= 8; i++) {
			backwardsImgs.add(ImageUtil.getImg("/img/≤Ã–Ï¿§––◊ﬂ" + i + ".png"));

		}
		
		init(bg, x, y, forwardImgs, backwardsImgs);
		
		attackForwardImgs = new ArrayList<BufferedImage>();
		for (int i = 1; i <= 8; i++) {
			attackForwardImgs.add(ImageUtil.getImg("/img/≤Ã–Ï¿§π•ª˜" + i + ".png"));

		}
		attackBackwardsImgs = new ArrayList<BufferedImage>();
		for (int i = 1; i <= 8; i++) {
			attackBackwardsImgs.add(ImageUtil.getImg("/img/≤Ã–Ï¿§π•ª˜" + i + "L.png"));
		}

	}
	
    public HeroCaiXuKun(){
	    this(0,591,220);
    }

}
