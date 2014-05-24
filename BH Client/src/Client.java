import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JApplet;
import javax.swing.JOptionPane;

import chessboard.ChessBoard;

public class Client extends JApplet {

	private ChessBoard a;
	private ChessBoard b;

	private BufferedImage aa;
	private BufferedImage bb;

	private String user;
	private String team;

	public void init() {
		a = new ChessBoard();
		b = new ChessBoard();
		aa = new BufferedImage(60 * 8, 60 * 8, BufferedImage.TYPE_INT_RGB);
		bb = new BufferedImage(60 * 8, 60 * 8, BufferedImage.TYPE_INT_RGB);

		new Thread() {
			@Override
			public void run() {
				Socket socket = null;
				PrintWriter writer = null;
				BufferedReader reader = null;
				try {
					socket = new Socket(JOptionPane.showInputDialog("Hostname?"), 8080);
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
				writer.write("Username\n" + user+"\n");
				writer.write("Move\n" + team+"\n");
				// write some move
			}
		}.start();
	}

	public void start() {

	}

	public void destroy() {

	}

	public void stop() {

	}

	public void paint(Graphics g) {
		a.drawBoard(aa.getGraphics(), 60, true);
		b.drawBoard(bb.getGraphics(), 60, false);
		g.drawImage(aa, 0, 0, null);
		g.drawImage(bb, 60 * 8 + 24, 0, null);
	}
}
