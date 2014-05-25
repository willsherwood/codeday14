package chessboard;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import clientPackage.Client;

public class ChessBoard {

	public transient List<Piece> piecesTaken = new ArrayList<>();

	public Piece[][] pieces = new Piece[8][8];

	public boolean isSelected = false;

	private int num;

	public ChessBoard(int num) {
		this();
		this.num = num;
	}

	
	public boolean whitesTurn = true;
	
	public ChessBoard() {
		for (int i = 0; i < 8; i++)
			pieces[i][1] = new Piece(PieceColor.BLACK, Type.PAWN);
		for (int i = 0; i < 8; i++)
			pieces[i][6] = new Piece(PieceColor.WHITE, Type.PAWN);
		// black setup
		pieces[0][0] = new Piece(PieceColor.BLACK, Type.ROOK);
		pieces[1][0] = new Piece(PieceColor.BLACK, Type.KNIGHT);
		pieces[2][0] = new Piece(PieceColor.BLACK, Type.BISHOP);
		pieces[3][0] = new Piece(PieceColor.BLACK, Type.QUEEN);
		pieces[4][0] = new Piece(PieceColor.BLACK, Type.KING);
		pieces[5][0] = new Piece(PieceColor.BLACK, Type.BISHOP);
		pieces[6][0] = new Piece(PieceColor.BLACK, Type.KNIGHT);
		pieces[7][0] = new Piece(PieceColor.BLACK, Type.ROOK);
		// white setup
		pieces[0][7] = new Piece(PieceColor.WHITE, Type.ROOK);
		pieces[1][7] = new Piece(PieceColor.WHITE, Type.KNIGHT);
		pieces[2][7] = new Piece(PieceColor.WHITE, Type.BISHOP);
		pieces[3][7] = new Piece(PieceColor.WHITE, Type.QUEEN);
		pieces[4][7] = new Piece(PieceColor.WHITE, Type.KING);
		pieces[5][7] = new Piece(PieceColor.WHITE, Type.BISHOP);
		pieces[6][7] = new Piece(PieceColor.WHITE, Type.KNIGHT);
		pieces[7][7] = new Piece(PieceColor.WHITE, Type.ROOK);

	}

	public void drawBoard(Graphics g, int cellSize, boolean inverted) {
		g.setColor(inverted ? Color.PINK : Color.RED);
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
				if (pieces[i][inverted ? 7 - j : 0 + j] != null)
					pieces[i][inverted ? 7 - j : 0 + j].drawPiece(g, i
							* cellSize, j * cellSize);
			}
		}
		g.setColor(Color.BLACK);
		for (int i=0; i<8; i++)
			for (int j=0; j<8 ;j++)
				g.drawRect(i*60, j*60, 60, 60);
			
		if (isSelected) {
			Color s = new Color(0, 255, 0, 122);
			g.setColor(s);
			g.fillRect(ps.x * cellSize, !inverted ? ps.y * cellSize
					: (7 - ps.y) * cellSize, cellSize, cellSize);
		}
	}

	public void sendToServer(String s) {
		Socket socket = null;
		BufferedReader reader = null;
		PrintWriter writer = null;
		try {
			socket = new Socket("192.168.1.252", 8080);
		} catch (HeadlessException | IOException e) {
			e.printStackTrace();
		}

		try {
			writer = new PrintWriter(socket.getOutputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.write("X-APPLICATION\n");
		writer.write(s);
		writer.flush();

		writer.close();
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void move(Point from, Point to) {
		if (from.equals(to)) {
			isSelected = false;
			return;
		}
		if (pieces[from.x][from.y] == null) {
			isSelected = false;
			return;
		}
		if (pieces[from.x][from.y] != null && pieces[to.x][to.y] != null)
			if (pieces[from.x][from.y].color == pieces[to.x][to.y].color) {
				isSelected = false;
				return;
			}
		if (pieces[from.x][from.y].color == PieceColor.WHITE) {
			if (!whitesTurn)
				return;
		} else {
			if (whitesTurn)
				return;
		}
		// sendToServer(from, to);
		// System.out.println("Move\n" + Client.team + "\n" + num + " " + from.x
		// + " " + from.y
		// + ":" + to.x + " " + to.y);
		sendToServer("Move\n" + Client.team + "\n" + num + " " + from.x + " "
				+ from.y + ":" + to.x + " " + to.y);
		// wait for board
		// redraw
	}

	public void moveBoard(Point from, Point to) {
		
		if (pieces[from.x][from.y].color == PieceColor.WHITE) {
			whitesTurn = false;
		} else {
			whitesTurn = true;
		}
		
		// castle
		if (pieces[from.x][from.y].type.equals(Type.KING)) {
			if (Math.abs(from.x - to.x) == 2 && from.y == to.y) {
				// castle
				if (from.x < to.x) {
					// rook is on the right
					// and moves left
					Piece rook = pieces[7][from.y];
					if (rook.type == Type.ROOK) {
						moveBoard(new Point(7, from.y), new Point(to.x - 1,
								to.y));
					}
				} else {
					// rook is on the left
					// and moves right
					Piece rook = pieces[0][from.y];
					if (rook.type == Type.ROOK) {
						moveBoard(new Point(0, from.y), new Point(to.x + 1,
								to.y));
					}
				}
			}
		}
		if (pieces[from.x][from.y].type.equals(Type.PAWN)) {
			// en passant
			if (from.x - to.x != 0) {
				// en passant is true
				if (pieces[to.x][to.y] == null) {
					if (pieces[to.x] != null)
						if (pieces[to.x][from.y] != null)
							if (pieces[to.x][from.y].type.equals(Type.PAWN)) {
								piecesTaken.add(pieces[to.x][from.y]);
								pieces[to.x][from.y] = null;
							}
				}
			}
		}
		if (pieces[to.x][to.y] != null) {
			piecesTaken.add(pieces[to.x][to.y]);
		}
		pieces[to.x][to.y] = pieces[from.x][from.y];
		pieces[from.x][from.y] = null;
		Client.jap.repaint();

	}

	private Point ps;

	public void clicked(int x, int y) {
		if (!isSelected) {
			isSelected = true;
			ps = new Point(x, y);
		} else {
			move(ps, new Point(x, y));
			isSelected = false;
		}
	}
}
