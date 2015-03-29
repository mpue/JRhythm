package de.pueski.jrhythm.objects;

import java.awt.Font;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;



/**
 * @author Matthias Pueski (15.05.2011)
 *
 */
public class Console extends SceneNode {
	
	private static final long serialVersionUID = -1625439779451433882L;

	private int width;
	private int height;
	
	int cursorPos = 0;
	
	
	private final StringBuffer buffer = new StringBuffer(); 
	
	private static Console INSTANCE = null;
	
	private final UnicodeFont f = new UnicodeFont(new Font("Arial", Font.PLAIN, 16));
	
	public static Console getInstance() {
		
		if (INSTANCE == null) {
			INSTANCE = new Console(300, 200);			
		}
		
		return INSTANCE;
		
	}

	@SuppressWarnings("unchecked")
	protected Console(int width, int height) {
		this.width = width;
		this.height = height;
		
		f.addAsciiGlyphs();		
		f.getEffects().add(new ColorEffect(java.awt.Color.WHITE));		
		try {
			f.loadGlyphs();
		}
		catch (SlickException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void draw() {
//		bufferText.setText(">"+buffer.toString());
//		bufferText.draw();		
//		cursorText.setText(">"+buffer.toString());		
//		cursorText.draw();
		GL11.glColor4f(1.0f,1.0f, 1.0f, 1.0f);
		// GL11.glScalef(0f, -1f, 0f);
		GL11.glTranslatef(width / 2, height / 2, 0.0f);
		GL11.glPushMatrix();
		GL11.glFrontFace(GL11.GL_CCW);
		GL11.glScalef(1.0f, -1.0f, -1.0f);
		f.drawString(-width/2 + 10,60, ">"+buffer.toString()+"_");
		GL11.glFrontFace(GL11.GL_CW);
		GL11.glPopMatrix();
		
	}

	/**
	 * @return the buffer
	 */
	public StringBuffer getBuffer() {
		return buffer;
	}

	public void clear() {
		buffer.delete(0, buffer.length());
	}
	
	public void append(char key) {
		buffer.append(key);
		cursorPos++;		
	}
	
	public void deleteLast() {
		if (buffer.length() == 0)
			return;
		
		buffer.delete(buffer.length()-1,buffer.length());
		cursorPos--;
	}
	
	
}
