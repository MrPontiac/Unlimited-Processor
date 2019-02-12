import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.lang.Math;
import javax.imageio.*;
import java.awt.image.BufferedImage;

class Main {
	public static ReInfinite UpArrow(ReInfinite num1, ReInfinite num2) {
		ReInfinite result = new ReInfinite(num1);
		ReInfinite orig = new ReInfinite(num1);
		for (ReInfinite i = new ReInfinite(1); !i.isGreaterThan(num2); i.add(new ReInfinite(1))) {
			result = ReInfinite.power(orig, result);
		}
		return result;
	}

	public static void main(String[] args) {
		String imagePath = "A.png";
		BufferedImage myPicture = null;
		try {
			myPicture = ImageIO.read(new File(imagePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (myPicture != null) {
				Graphics2D g = (Graphics2D) myPicture.getGraphics();
				g.setStroke(new BasicStroke(3));
				g.setColor(new Color(2, 75, 37));
				g.fillRect(0, 0, myPicture.getWidth(), myPicture.getHeight());
				g.setColor(new Color(181, 177, 174));
				for (int i = 0; i < 9; i++) {
					g.drawLine(70 + i * 30, 20, 70 + i * 30, 26);
				}
				for (int i = 0; i < 9; i++) {
					g.drawLine(70 + i * 30, 273, 70 + i * 30, 281);
				}
				g.setColor(new Color(72, 73, 75));
				g.fillRoundRect(65, 25, 250, 250, 30, 30);
				g.setColor(new Color(181, 177, 174));
				g.drawRoundRect(65, 25, 250, 250, 30, 30);
				g.setColor(new Color(120, 121, 123));
				Font myFont = new Font("Dialog", 1, 12);
				g.setFont(myFont);
				double sLimit = 1e7D;
				double limit = sLimit;
				ReInfinite base = UpArrow(new ReInfinite(Double.MAX_VALUE, Double.MAX_VALUE), new ReInfinite(sLimit));
				ReInfinite largeNum = UpArrow(
						UpArrow(UpArrow(base, new ReInfinite(sLimit)), new ReInfinite(limit)),
						new ReInfinite(limit));
				String str = UpArrow(largeNum, new ReInfinite(limit)).toString();
				System.out.println(str);
				g.drawString(str, (myPicture.getWidth() / 2) - (float) (4D * (double) (str.length())),
						(myPicture.getHeight() / 2) + 10);
				str = "Unlimited Processer \u00a9";
				g.drawString(str, (myPicture.getWidth() / 2) - (float) (4.5D * (double) (str.length())),
						(myPicture.getHeight() / 2) + 30);
				g.dispose();
				ImageIO.write(myPicture, "png", new File("B.png"));
			} else {
				System.out.println("File is null");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}