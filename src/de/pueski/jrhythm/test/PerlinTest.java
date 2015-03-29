package de.pueski.jrhythm.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import de.pueski.jrhythm.util.ImageUtils;
import de.pueski.jrhythm.util.ImprovedNoise;
import de.pueski.jrhythm.util.PinkNoise;

public class PerlinTest {
	
	private static final int WIDTH = 520;
	private static final int HEIGHT = 520;

	private static JFrame frame;
	private static final Dimension size = new Dimension(WIDTH+50,HEIGHT+50); 

	private static final String outputDir = "C:\\tmp\\"; 
	
	public static void main(String[] args) throws Exception {

		frame = new JFrame("Noise");
		frame.setSize(size);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setLocationRelativeTo(null);


//		BufferedImage perlin2 = ImprovedNoise.createNoiseTexture(WIDTH, HEIGHT, 3.0d,1.5f);
//		
//		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
//		
//		for (int x = 0;x < WIDTH;x++) {
//			for (int y = 0;y < WIDTH;y++) {		
//				image.setRGB(x, y, perlin2.getRGB(x, y));
//			}			
//		}
		
//		for (int i = 0; i < 10; i++) {
//			image = ImageUtils.blur(image);
//		}
		
		// ImageIO.write(image, "PNG", new File(outputDir+"generated_"+System.currentTimeMillis()+".png"));
		
		Random r = new Random();

		BufferedImage voronoi = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("voronoi_inv.png"));
		
		BufferedImage image1 = ImprovedNoise.createNoiseTexture(WIDTH, HEIGHT, 4.0d, 1.0f,64);
		BufferedImage image2 = ImprovedNoise.createNoiseTexture(WIDTH, HEIGHT, 8.0d, 1.0f,64);
		BufferedImage image3 = ImprovedNoise.createNoiseTexture(WIDTH, HEIGHT, 16.0d, 1.0f,64);		
		BufferedImage image4 = PinkNoise.createNoiseTexture(WIDTH, HEIGHT,64);
		BufferedImage image5 = ImprovedNoise.createNoiseTexture(WIDTH, HEIGHT, 1.0d, 2.f,64);
		
		image4 = ImageUtils.scale(image4, 8);
		
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = (Graphics2D)image.getGraphics();
		g2d.setColor(Color.GRAY);
		g2d.fillRect(0, 0, WIDTH, HEIGHT);
		g2d.drawImage(image5, 0, 0, frame);
		g2d.drawImage(voronoi, 0, 0, frame);
		g2d.drawImage(image3, 0, 0, frame);
		g2d.drawImage(image2, 0, 0, frame);
		g2d.drawImage(image1, 0, 0, frame);
		
		RescaleOp rescaleOp = new RescaleOp(1.2f, 20, null);
		rescaleOp.filter(image, image); 
		
		for (int i = 0; i < 16;i++)
			image = ImageUtils.blur(image);
		
		image = ImageUtils.crop(image,8);
		image = ImageUtils.convertToGrayscale(image);
		
		final ImageIcon icon = new ImageIcon(image);
		JLabel label = new JLabel(icon);
		frame.add(label, BorderLayout.CENTER);				
		frame.setVisible(true);
		
	}
	

	
}
