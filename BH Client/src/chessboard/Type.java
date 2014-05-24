package chessboard;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public enum Type {

	PAWN(), ROOK, KNIGHT, BISHOP, KING, QUEEN;

	private Image i;

	public Image getImage() {
		System.out.println(name()+".png");
		if (i==null) {
			try {
				i = ImageIO.read(getClass().getResourceAsStream(name()+".png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return i;
	}
}