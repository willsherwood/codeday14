package chessboard;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

public class ChessBoard {

	private List<Piece> piecesTaken;

	private Piece[][] pieces = new Piece[8][8];

	public ChessBoard() {
		for (int i = 0; i < 8; i++)
			pieces[i][1] = new Piece(PieceColor.BLACK, Type.PAWN);
		for (int i = 0; i < 8; i++)
			pieces[i][6] = new Piece(PieceColor.WHITE, Type.PAWN);
	}

	public void drawBoard(Graphics g, int cellSize) {
		g.setColor(Color.RED);
		for (int i = 0; i < 8; i++) {
			if (g.getColor() == Color.RED)
				g.setColor(Color.PINK);
			else
				g.setColor(Color.RED);
			for (int j = 0; j < 8; j++) {
				if (g.getColor() == Color.RED)
					g.setColor(Color.PINK);
				else
					g.setColor(Color.RED);
				g.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
				if (pieces[i][j] != null)
					pieces[i][j].drawPiece(g, i * cellSize, j * cellSize);
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
