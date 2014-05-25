package chessboard;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public enum Type {

	PAWN, ROOK, KNIGHT, BISHOP, KING, QUEEN;

	private Image wi;
	private Image wb;
	private boolean w;
	private boolean b;

	public Image getImage(boolean white) {
		if (white) {
			try {
				wi = ImageIO.read(getClass().getResourceAsStream(
						(white ? "W" : "B") + name() + ".png"));
			} catch (IOException e) {
			}
			return wi;
		} else {
try {
		wb = ImageIO.read(getClass().getResourceAsStream(
		(white ? "W" : "B") + name() + ".png"));
		} catch (IOException e) {
		}
		return wb;
		}
		}
		}