package CompressionProject;

import java.awt.EventQueue;

public class Main {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    CompressionGUI window = new CompressionGUI();
                    window.frame.setVisible(true);//for the user to see the window on screen
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

	}

}
