package de.pueski.jrhythm.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JFrame;

import de.pueski.jrhythm.delaunay.DelaunayPanel;
import de.pueski.jrhythm.delaunay.Pnt;

public class VoronoiTest {
	
	private static final int WIDTH = 520;
	private static final int HEIGHT = 520;

	private static JFrame frame;
	private static final Dimension size = new Dimension(WIDTH+50,HEIGHT+50); 

	private static final String outputDir = "C:\\tmp\\"; 
	
	static int dragStartX;
	static int dragStartY;
	
	public static void main(String[] args) throws Exception {

		frame = new JFrame("BrainDesigner 1.0");
		frame.setSize(size);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setLocationRelativeTo(null);

		final DelaunayPanel panel = new DelaunayPanel();
		
		panel.setColorful(true);
		panel.setVoronoi(true);
		

		panel.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				panel.addSite(new Pnt(e.getX(),e.getY()));
				panel.repaint();
			}
			
		});

		frame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					panel.clear();
					panel.repaint();
				}
				else if (e.getKeyCode() == KeyEvent.VK_R) {
					
					panel.clear();
					
					Random r = new Random();
					
//					boolean odd = true;
//
//					for (int x = 0; x < WIDTH;x+=150) {
//						for (int y = 0; y < HEIGHT; y+=150) {
//							Pnt p;
//							
//							p = new Pnt(x+r.nextInt(100),y+r.nextInt(100));
//							
//							panel.addSite(p);
//							odd = !odd;
//						}
//					}

					for (int i=0; i < 50;i++)
						panel.addSite(new Pnt(r.nextInt(WIDTH),r.nextInt(HEIGHT)));
						
					panel.repaint();
					
					
				}
			}
		});
		
		frame.add(panel, BorderLayout.CENTER);				
		frame.setVisible(true);
		
	}
	

	
}
