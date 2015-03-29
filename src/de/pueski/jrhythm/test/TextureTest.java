package de.pueski.jrhythm.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import de.pueski.jrhythm.util.PinkNoise;

public class TextureTest {
	
	private static final int WIDTH = 640;
	private static final int HEIGHT = 480;
	
	private static boolean[][] grid = new boolean[WIDTH][HEIGHT]; 

	private static JFrame frame;
	private static final Dimension size = new Dimension(WIDTH+50,HEIGHT+50); 
	
	public static void main(String[] args) throws Exception {

		frame = new JFrame("DTMTest");
		frame.setSize(size);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setLocationRelativeTo(null);

		final ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
		
		for (int i = 0; i < 25;i++) {
			images.add(PinkNoise.createNoiseTexture(WIDTH, HEIGHT,255));
		}
		
		final ImageIcon icon = new ImageIcon();
		JLabel label = new JLabel(icon);
		frame.add(label, BorderLayout.CENTER);

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while(true) {
					for (int i = 0; i < images.size(); i++) {
						icon.setImage(images.get(i));
						frame.repaint();
						try {
							Thread.sleep(10);
						} 
						catch (InterruptedException e) {
							e.printStackTrace();
						}
					}				
				}
				
			}
		}).start();
		
		
		
		frame.setVisible(true);
		
	}
	

	
}
