package clientPackage;

import java.awt.Color;
import java.awt.Font;
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
import java.io.Reader;
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
	public static int num = -1;

	private boolean ratchetChessBoolean = false;
	private boolean ratchetChessBoolean2 = false;
	private Piece ratchetChessPiece = null;
	private int ratchetChessInt = 0;

	public static String team = "A";
	
	public static Thread listenThread = null;
	public static Reader currentReader = null;
	public static Socket listenerSocket = null;

	public void init() {
		team = JOptionPane.showInputDialog("Please enter room name");
		if (team == null)
			team = "A";
		jap = this;
		a = new ChessBoard(0);
		b = new ChessBoard(1);

		System.out.println("Login");
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
		writer.write("Login\n" + team + "\n");
		writer.flush();

		try {
			Client.num = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JOptionPane.showMessageDialog(null, "You are "
				+ (num % 2 == 0 ? "WHITE" : "BLACK") + " on the LEFT board");
		try {
			reader.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		writer.close();
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
				// if (num==0||num==3) {
				// x = 60*8*2+24-x;
				// }
				int y = e.getY() - 32;
				if (num == 2 || num == 3) {
					if (x < 8 * 60)
						x += 8 * 60 + 24;
					else
						x -= 8 * 60 + 24;
				}
				if ((num == 0 || num == 3) && 60 * 8 - y >= 0) {
					y = 60 * 8 - y;
					System.out.println("invert " + y);
				}
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
		(listenThread = new Thread(new A())).start();
	}

	public void start() {

	}

	public void destroy() {
		
	}

	public void stop() {
		try {
			listenerSocket.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (currentReader != null)
			try {
				currentReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		listenThread.interrupt();
	}

	public synchronized void paint(Graphics g) {
		g.setFont(new Font("Arial", -1, 24));
		g.drawString("Player "+(num+1) + (num%2==0?" WHITE":" BLACK"), 16, 30);
		
		a.drawBoard(aa.getGraphics(), 60, !(num == 0 || num == 3));
		b.drawBoard(bb.getGraphics(), 60, !(num == 1 || num == 2));
		g.drawImage(num < 2 ? aa : bb, 0, 32, null);
		g.drawImage(num < 2 ? bb : aa, 60 * 8 + 24, 32, null);
		g.clearRect(0, 32 + 60 * 8, 60 * 8 * 2 + 24, 60 * 4);
		int i = 0;
		for (Piece p : num == 2 || num == 3? a.piecesTaken : b.piecesTaken) {
			p.drawPiece(g, i++ % 8 * 60, 32 + 60 * 8 + ((i - 1) / 8) * 60);
		}
		i = 0;
		for (Piece p : num == 2 || num == 3? b.piecesTaken : a.piecesTaken) {
			p.drawPiece(g, i++ % 8 * 60 + 60 * 8 + 24, 60 * 8 + ((i - 1) / 8)
					* 60 + 32);
		}
		if (yolo1 != 0 && yolo2 != 0) {
			if (num == 2 || num == 3) {
				if (yolo1 < 8 * 60)
					yolo1 += 8 * 60 + 24;
				else
					yolo1 -= 8 * 60 + 24;
			}
			g.setColor(new Color(0, 0, 255, 100));
			g.fillRect(yolo1 - 30, yolo2, 60, 2 + 60);
		}
	}

	public static class A implements Runnable {
		public synchronized void run() {
			System.out.println("Listener thread");
			Socket socket = null;
			PrintWriter writer = null;
			BufferedReader reader = (BufferedReader) (currentReader = null);
			try {
				socket = listenerSocket = new Socket("192.168.1.252", 8080);
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
				currentReader = reader = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			writer.write("X-APPLICATION\n");
			writer.write("Listen\n" + team + "\n");
			System.out.println("actually writing this line");
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
				if (!Thread.interrupted())
					(listenThread = new Thread(new A())).start();
			} catch (Throwable t) {
				t.printStackTrace();
				writer.println("Closing");
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				writer.close();
				currentReader = null;
			} finally {
				listenerSocket = null;
			}
		}
	}
}