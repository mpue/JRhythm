package de.pueski.jrhythm.util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import de.pueski.jrhythm.core.GLDrawable;
import de.pueski.jrhythm.core.Game;

/**
 * @author Matthias Pueski (15.05.2011)
 * 
 */
public class UIUtils {
	
	public static void drawOrtho(int width, int height, GLDrawable g) {
		// save actual projection mode
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		// go to front in ortho mode
		glLoadIdentity();
		gluOrtho2D(0, width, 0, height);
		// if needed save current transformations
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();
		// no depth test
		glDisable(GL_DEPTH_TEST);
		// no loghting needed
		glDisable(GL_LIGHTING);
		// draw the drawable
		g.draw();
		// back to 3D stuff
		glEnable(GL_LIGHTING);
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();		
	}
	
	public static void drawString(float x, float y,String s) {
		int width = Game.getInstance().getDisplayMode().getWidth();
		int height = Game.getInstance().getDisplayMode().getHeight();
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		gluOrtho2D(0, width, 0, height);
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_LIGHTING);
		glColor4f(0.2f, 1.0f, 0f, 0.7f);
		glPushMatrix();
		glTranslatef(x, y, -0.1f);		
		GLWriter.getInstance().glPrint(s);
		glPopMatrix();
		glEnable(GL_LIGHTING);
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();
	}
	
	public static void drawZGrid(float y,  float stepsize,  float max_x, float max_z) {

		glPushMatrix();

		glTranslatef(0, 0, 0);
		
		glPushAttrib(GL_COLOR_BUFFER_BIT |
					 GL_TEXTURE_BIT      |
					 GL_CURRENT_BIT      |
					 GL_LINE_BIT         |
					 GL_LIGHTING_BIT);

		glDisable(GL_TEXTURE_2D);
		
		boolean lightingOn  = glIsEnabled(GL_LIGHTING);
	 	if (lightingOn) glDisable(GL_LIGHTING);

	 	glColor3f(0.5f,0.5f,0.5f);

	 	glLineWidth(0.5f);
		
		glBegin(GL_LINES);	
			for (float x=-(max_x/2); x < max_x/2+1;x+= stepsize) {			
				glVertex3f(x,y,-max_z/2);
				glVertex3f(x,y,max_z/2);									
				glVertex3f(-max_x/2,y,x);
				glVertex3f(max_x/2,y,x);
			}

		glEnd();	
		
		glEnable(GL_TEXTURE_2D);
		
		glPopAttrib();		
		glPopMatrix();

	}
	
}
