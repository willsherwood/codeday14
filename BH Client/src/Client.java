import java.awt.Graphics;

import javax.swing.JApplet;

import chessboard.ChessBoard;

public class Client extends JApplet {

	private ChessBoard a;
	
	public void init() {
		a = new ChessBoard();
	}
	
	public void start() {
		
	}
	
	public void destroy() {
		
	}
	
	public void stop() {
		
	}
	
	public void paint(Graphics g) {
		a.drawBoard(g, 60, true);
	}
}
