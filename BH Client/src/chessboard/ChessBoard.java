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

import client.Client;

public class ChessBoard {

	private List<Piece> piecesTaken = new ArrayList<>();

	private Piece[][] pieces = new Piece[8][8];

	private boolean isSelected = false;

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
	}

	public void sendToServer(String s) {
		Socket socket = null;
		BufferedReader reader;
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
	}

	public void move(Point from, Point to) {
		if (pieces[to.x][to.y] != null) {
			piecesTaken.add(pieces[to.x][to.y]);
			pieces[to.x][to.y] = pieces[from.x][from.y];
		}
		pieces[from.x][from.y] = null;
		// sendToServer(from, to);
		System.out.println(from.x + " " + from.y + ":" + to.x + " " + to.y);
		sendToServer(from.x + " " + from.y + ":" + to.x + " " + to.y);
		// wait for board
		// redraw
		Client.jap.repaint();

	}

	private Point ps;

	public void select(int x, int y) {
		if (!isSelected) {
			isSelected = true;
			ps = new Point(x, y);
		} else {
			move(ps, new Point(x, y));
		}
	}
}
