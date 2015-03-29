package de.pueski.jrhythm.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.texgen.gui.TextureImage;
import org.texgen.textures.complex.ComplexMarble;
import org.texgen.textures.complex.Dirty;

/**
 * @author Matthias Pueski (15.05.2010)
 *
 */

public class TextureTest2 {
	
	private static final int WIDTH = 128;
	private static final int HEIGHT = 128;
	
	public static void main(String[] args)  throws Exception  {
		
		
		// ComplexMarble tex = new ComplexMarble(new Random().nextFloat()*2);		
		// DirtyBrick tex = new DirtyBrick();		
		// Camouflage tex = new Camouflage();		
		Dirty tex = new Dirty(new ComplexMarble(new Random().nextFloat()*2));		
		// CloudTexture tex = new CloudTexture();
		// TigerColors tex = new TigerColors();
		// SunsetTexture tex = new SunsetTexture();		
		// Squares tex = new Squares();		
		// Checker tex = new Checker();
		// BrickTexture tex = new BrickTexture();
		// Fur tex = new Fur(new Camouflage());
		// GridPattern tex = new GridPattern(new ComplexMarble());		
		// Mandelbrot tex = new Mandelbrot();
		
		TextureImage ti = new TextureImage(WIDTH,HEIGHT);
		ti.setTexture(tex);		
		ti.renderAndWait();
		
		BufferedImage image = ti.getImage();
		
		// image = ImageUtils.convertToGrayscale(image);
		
		for (int x = 0; x < image.getWidth();x++) {
			for (int y = 0; y < image.getHeight();y++) {
				System.out.println((float)Math.abs(image.getRGB(x, y)/(float)1000000));
			}
		}
		
		
		// ImageIO.write(ti.getImage(), "PNG", new File("/home/mpue/devel/sp_workspace/JRhythm/resources/texgen/dirt.png"));
		
		JFrame frame = new JFrame();
		Dimension size = new Dimension(WIDTH+50,HEIGHT+50);
		frame.setSize(size);
		frame.setBounds(new Rectangle(size));
		frame.setLayout(new BorderLayout());		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(frame.getRootPane());
		frame.add(new JLabel(new ImageIcon(image)), BorderLayout.CENTER);
		frame.setVisible(true);
		
		
	}

}
