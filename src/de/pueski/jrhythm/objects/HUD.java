package de.pueski.jrhythm.objects;

import java.awt.Font;
import java.io.InputStream;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import de.pueski.jrhythm.core.GLBarGraphDisplay;
import de.pueski.jrhythm.core.GLColor3f;
import de.pueski.jrhythm.core.Game;
import de.pueski.jrhythm.scene.AbstractScene;
import de.pueski.jrhythm.scene.SceneManager;

/**
 * @author Matthias Pueski (15.05.2011)
 *
 */
public class HUD extends SceneNode {
	

	private static final long serialVersionUID = -1625439779451433882L;

	private int width;
	private int height;
	private SpriteSheet ss;
	
	UnicodeFont font = new UnicodeFont(new Font("Arial", Font.PLAIN, 12));
	
	private GLBarGraphDisplay progressBar;
	
	public HUD(int width, int height) {
		this.width = width;
		this.height = height;
		font.addAsciiGlyphs();
		font.getEffects().add(new ColorEffect(java.awt.Color.WHITE));		
		try {
			font.loadGlyphs();
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("logo/rhythm.png");		
			Image image = new Image(is,"logo",true);
			ss = new SpriteSheet(image,image.getWidth(), image.getHeight());
		}
		catch (SlickException e) {
			e.printStackTrace();
		}
		
		progressBar = new GLBarGraphDisplay(0,1.0f,0,-100,true,false);
		progressBar.setBarColor(new GLColor3f(0.0f,0.0f,1.0f));
		progressBar.setSpacing(5.0f);
		progressBar.setVisible(false);
	}

	@Override
	public void draw() {
		GL11.glTranslatef(width / 2, height / 2, 0.0f);			
		GL11.glPushMatrix();
		GL11.glFrontFace(GL11.GL_CCW);
		GL11.glScalef(1.0f, -1.0f, -1.0f);
		font.drawString(-width/2 + 10 ,-height/2 + 10,String.valueOf("FPS : "+Game.getInstance().getFps()));
		font.drawString(-width/2 + 10 ,-height/2 + 30,String.valueOf("V/F : "+SceneManager.getInstance().getCurrentScene().getTotalVertices()+"/"+SceneManager.getInstance().getCurrentScene().getTotalFaces()));
		font.drawString(-width/2 + 10 ,-height/2 + 50,String.valueOf("Objects : "+SceneManager.getInstance().getCurrentScene().getTotalObjects()));
		font.drawString(-width/2 + 10 ,-height/2 + 70,String.valueOf("CamPos : "+SceneManager.getInstance().getCurrentScene().getCurrentCamera()));
		font.drawString(-width/2 + 10 ,-height/2 + 90,String.valueOf("Velocity : "+Game.getInstance().getVelocity()));
//		
		AbstractScene scene = SceneManager.getInstance().getCurrentScene();
		
		int n = 1;
		
		for (SceneNode child : scene.getRootNode().getChildren()) {
			
			if (child instanceof Mesh) {
				Mesh mesh = (Mesh)child;				
				font.drawString(-width/2 + 10 ,-height/2 + 90 + n*20 ,mesh.getName()+" Lifetime : "+mesh.getCurrentLifeTime());
				n++;
			}
			
		}
//		
		font.drawString(-width/2 + 10,height/2-20,Game.getInstance().getMessage().toString());
		
		GL11.glFrontFace(GL11.GL_CW);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(width/2 - ss.getWidth() -10, -height/2+ss.getHeight()/2-50, 0);
		// ss.draw();		
		GL11.glPopMatrix();
		
//		GL11.glPushMatrix();
//		GL11.glRotatef(-90, 0, 0, 1);
//		progressBar.draw();
//		GL11.glPopMatrix();
		
	}

	/**
	 * @return the progressBar
	 */
	public GLBarGraphDisplay getProgressBar() {
		return progressBar;
	}
	
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	
	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
}
