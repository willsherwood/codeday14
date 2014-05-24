package chessboard;

import java.awt.Graphics;

public class Piece {

	private PieceColor color;
	private Type type;
	
	public Piece(PieceColor c, Type t) {
		color = c;
		type = t;
	}
	
	public void drawPiece(Graphics g, int x, int y) {
		g.drawImage(type.getImage(), x, y, null);
	}
}
