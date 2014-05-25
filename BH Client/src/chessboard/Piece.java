package chessboard;

import java.awt.Graphics;
import java.awt.Image;

public class Piece {

	public PieceColor color;
	public Type type;

	public Piece(PieceColor c, Type t) {
		color = c;
		type = t;
	}

	public void drawPiece(Graphics g, int x, int y) {
		Image i = type.getImage(color == PieceColor.WHITE);
		g.drawImage(i, x, y, null);
	}
	
	public String toString() {
		return type + " " + color;
	}
}