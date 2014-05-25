package client;

import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JApplet;

import chessboard.ChessBoard;
import chessboard.Piece;

public class Client extends JApplet {

	public static JApplet jap;

	public static ChessBoard a;
	public static ChessBoard b;

	private BufferedImage aa;
	private BufferedImage bb;

	public static String team = "A";

	public void init() {
		jap = this;
		a = new ChessBoard(0);
		b = new ChessBoard(1);
		aa = new BufferedImage(60 * 8, 60 * 8, BufferedImage.TYPE_INT_RGB);
		bb = new BufferedImage(60 * 8, 60 * 8, BufferedImage.TYPE_INT_RGB);
		addMouseListener(new EmptyMouseListener() {
			@Override
			public void mousePressed(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				boolean board1 = false;
				if (x > 60 * 8)
					x -= 24;
				else {
					board1 = true;
					y = 60 * 8 - y;
				}
				x /= 60;
				y /= 60;
				(board1 ? a : b).select(board1 ? x : x - 8, y);
				repaint();
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
		g.clearRect(0, 60 * 80, 60 * 80 * 2 + 24, 60);
		int i = 0;
		for (Piece p : b.piecesTaken) {
			p.drawPiece(g, i++ % 8 * 60, 60 * 8 + ((i - 1) / 8) * 60);
		}
		i = 0;
		for (Piece p : a.piecesTaken) {
			p.drawPiece(g, i++ % 8 * 60 + 60 * 8 + 24,
					60 * 8 + ((i - 1) / 8) * 60);
		}
	}

	public static class A implements Runnable {
		public void run() {
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
				writer.close();
				reader.close();
				String[] tt = move.split("[ :]");
				(move.charAt(0) == '0' ? Client.a : Client.b).moveBoard(
						new Point(Integer.parseInt(tt[1]), Integer
								.parseInt(tt[2])),
						new Point(Integer.parseInt(tt[3]), Integer
								.parseInt(tt[4])));
				// make another listener
				new Thread(new A()).start();
			} catch (Throwable t) {
				writer.close();
			}
		}
	}
}