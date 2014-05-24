package client;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public interface EmptyMouseListener extends MouseListener {

	@Override default public void mousePressed(MouseEvent e) {
		
	}

	@Override default public void mouseClicked(MouseEvent arg0) {
		
	}

	@Override default public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override default public void mouseExited(MouseEvent arg0) {
		
	}

	@Override default public void mouseReleased(MouseEvent arg0) {
		
	}
}
