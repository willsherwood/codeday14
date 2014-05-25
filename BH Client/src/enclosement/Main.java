package enclosement;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JApplet;
import javax.swing.JFrame;

import clientPackage.Client;

public class Main {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		final JApplet c = new Client();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				c.stop();
				c.destroy();
			}
		});
		frame.setSize(60*8*2+41, 720);
		frame.setTitle("BugHouse Chess ----- V1.0");
		c.init();
		c.start();
		frame.add(c);
		frame.setVisible(true);
	}

}
