
package ui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Winston Smith
 *	图片工具类
 */

public class ImageUtil {

	public static BufferedImage getImg(String path) {
		// 加载图片 封装读取图片流程
		try {
			// 反射获取图片
			BufferedImage img = ImageIO.read(ImageUtil.class.getResource(path));
			return img;
		} catch (IOException e) {
			// 会输出找不到的原因
			e.printStackTrace();
		}
		return null;
	}
}
