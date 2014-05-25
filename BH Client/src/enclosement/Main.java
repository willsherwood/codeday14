package enclosement;

import java.awt.Frame;

import javax.swing.JApplet;
import javax.swing.JFrame;

import clientPackage.Client;

public class Main {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(60*8*2+41, 720);
		frame.setTitle("BugHouse Chess ----- V1.0");
		JApplet c = new Client();
		c.init();
		c.start();
		frame.add(c);
		frame.setVisible(true);
	}

}
