package clientPackage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import chessboard.ChessBoard;
import chessboard.Piece;
import chessboard.PieceColor;
import chessboard.Type;

public class Client extends JApplet {
	private int yolo1;
	private int yolo2;
	public static JApplet jap;

	public static ChessBoard a;
	public static ChessBoard b;

	private BufferedImage aa;
	private BufferedImage bb;

	private boolean ratchetChessBoolean = false;
	private boolean ratchetChessBoolean2 = false;
	private Piece ratchetChessPiece = null;
	private int ratchetChessInt = 0;

	public static String team = "A";

	public void init() {
		team = getParameter("team");
		if (team == null)
			team = "A";
		jap = this;
		a = new ChessBoard(0);
		b = new ChessBoard(1);
		aa = new BufferedImage(60 * 8, 60 * 8, BufferedImage.TYPE_INT_RGB);
		bb = new BufferedImage(60 * 8, 60 * 8, BufferedImage.TYPE_INT_RGB);
		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					ratchetChessBoolean = false;
					ratchetChessPiece = null;
					ratchetChessInt = 0;
					a.isSelected = false;
					b.isSelected = false;
					yolo1 = 0;
					yolo2 = 0;
					jap.repaint();
					return;
				}
				int x = e.getX();
				int y = e.getY();
				if (y < 60 * 8) {
					boolean board1 = false;
					if (x > 60 * 8)
						x -= 24;
					else {
						board1 = true;
						y = 60 * 8 - y;
					}
					x /= 60;
					y /= 60;
					if (ratchetChessBoolean) {
						// make sure board1 && !ratchetChessBoolean2
						if (board1
								^ ratchetChessBoolean2
								&& (board1 ? a : b).pieces[board1 ? x : x - 8][y] == null) {
							ratchetChessBoolean = false;
							b.sendToServer("Move\n" + team + "\nAdd "
									+ (ratchetChessBoolean2 ? "1" : "0") + " "
									+ ratchetChessPiece.color + " "
									+ ratchetChessPiece.type + " "
									+ (board1 ? x : x - 8) + " " + y + " "
									+ ratchetChessInt);
							ratchetChessPiece = null;
							yolo1 = 0;
							yolo2 = 0;
							ratchetChessInt = 0;
						} else {
							alert("Invalid addition");
						}
					} else
						(board1 ? a : b).clicked(board1 ? x : x - 8, y);
					repaint();
				} else {
					boolean board1 = false;
					yolo1 = x > 60 * 8 + 24 ? x - 24 : x;
					yolo2 = y - 60 * 8;
					yolo1 = yolo1 / 60 * 60 + 30;
					if (yolo1 > 60 * 8 + 12)
						yolo1 += 24;
					yolo2 = yolo2 / 60 * 60 + 60 * 8 + 30;
					y -= 60 * 8;
					if (x > 60 * 8)
						x -= 24 + 60 * 8;
					else
						board1 = true;

					x /= 60;
					y /= 60;
					int index = x + y * 8;

					if (board1) {
						if (index < b.piecesTaken.size()) {
							/*
							 * Piece p = b.piecesTaken.get(index);
							 * b.sendToServer("Move\n" + team + "\nAdd 0 " +
							 * p.color + " " + p.type + " " + 4 + " " + 4 + " "
							 * + index);
							 */
							ratchetChessBoolean = true;
							ratchetChessBoolean2 = false;
							ratchetChessPiece = b.piecesTaken.get(index);
							ratchetChessInt = index;
						}
						System.out.println(index);
						jap.repaint();
					} else {
						if (index < a.piecesTaken.size()) {
							/*
							 * Piece p = a.piecesTaken.get(index);
							 * a.sendToServer("Move\n" + team + "\nAdd 1 " +
							 * p.color + " " + p.type + " " + 4 + " " + 4 + " "
							 * + index); System.out.println(index);
							 */
							ratchetChessBoolean = true;
							ratchetChessBoolean2 = true;
							ratchetChessPiece = a.piecesTaken.get(index);
							ratchetChessInt = index;
							jap.repaint();
						}
					}
				}
			}

			private void alert(String string) {
				JOptionPane.showMessageDialog(null, string);
			}
		});
		new Thread(new A()).start();
	}

	public void start() {

	}

	public void destroy() {

	}

	public void stop() {

	}

	public synchronized void paint(Graphics g) {
		a.drawBoard(aa.getGraphics(), 60, true);
		b.drawBoard(bb.getGraphics(), 60, false);
		g.drawImage(aa, 0, 0, null);
		g.drawImage(bb, 60 * 8 + 24, 0, null);
		g.clearRect(0, 60 * 8, 60 * 8 * 2 + 24, 60 * 4);
		int i = 0;
		for (Piece p : b.piecesTaken) {
			p.drawPiece(g, i++ % 8 * 60, 60 * 8 + ((i - 1) / 8) * 60);
		}
		i = 0;
		for (Piece p : a.piecesTaken) {
			p.drawPiece(g, i++ % 8 * 60 + 60 * 8 + 24,
					60 * 8 + ((i - 1) / 8) * 60);
		}
		if (yolo1 != 0 && yolo2 != 0) {
			g.setColor(new Color(0, 0, 255, 100));
			g.fillRect(yolo1 - 30, yolo2 - 30, 60, 60);
		}
	}

	public static class A implements Runnable {
		public synchronized void run() {
			System.out.println("Listener thread");
			Socket socket = null;
			PrintWriter writer = null;
			BufferedReader reader = null;
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
			writer.write("Listen\n" + team + "\n");
			// write some move
			writer.flush();
			try {
				String move = reader.readLine();
				// do something
				System.out.println("Received: " + move);
				if (move.startsWith("Add")) {
					String[] t = move.split(" ");
					Piece p = new Piece(PieceColor.valueOf(t[2]),
							Type.valueOf(t[3]));
					(t[1].equals("0") ? b : a).piecesTaken.remove(Integer
							.parseInt(t[6]));
					(t[1].equals("0") ? a : b).pieces[Integer.parseInt(t[4])][Integer
							.parseInt(t[5])] = p;
					jap.repaint();
				} else {
					String[] tt = move.split("[ :]");
					(move.charAt(0) == '0' ? Client.a : Client.b).moveBoard(
							new Point(Integer.parseInt(tt[1]), Integer
									.parseInt(tt[2])),
							new Point(Integer.parseInt(tt[3]), Integer
									.parseInt(tt[4])));
					// make another listener
				}
				writer.close();
				reader.close();
				new Thread(new A()).start();
			} catch (Throwable t) {
				writer.close();
			}
		}
	}
}