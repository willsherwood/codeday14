import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JApplet;

import chessboard.ChessBoard;

public class Client extends JApplet {

	private ChessBoard a;
	private ChessBoard b;
	
	private BufferedImage aa;
	private BufferedImage bb;
	
	public void init() {
		a = new ChessBoard();
		b = new ChessBoard();
		aa = new BufferedImage(60*8, 60*8, BufferedImage.TYPE_INT_RGB);
		bb = new BufferedImage(60*8, 60*8, BufferedImage.TYPE_INT_RGB);
		
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
		g.drawImage(bb, 60*8+24, 0, null);
	}
}
