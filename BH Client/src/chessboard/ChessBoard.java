package chessboard;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

public class ChessBoard {

	private List<Piece> piecesTaken;
	
	private Piece[][] pieces = new Piece[8][8];
	
	public ChessBoard () {
		pieces[0][0] = new Piece(PieceColor.WHITE, Type.PAWN);
	}
	
	public void drawBoard (Graphics g, int cellSize) {
		for (int i=0; i<8; i++) {
			g.setColor(new Color(~g.getColor().getRGB()));
			for (int j=0; j<8; j++) {
				g.setColor(new Color(~g.getColor().getRGB()));
				g.fillRect(i*cellSize, j*cellSize, cellSize, cellSize);
				if (pieces[i][j] != null)
					pieces[i][j].drawPiece(g, i*cellSize, j*cellSize);
			}
		}
	}
	
	public void move(Point from, Point to) {
		if (pieces[to.x][to.y] != null) {
			piecesTaken.add(pieces[to.x][to.y]);
			pieces[to.x][to.y] = pieces[from.x][from.y];
		}
		pieces[from.x][from.y] = null;
	}
}
