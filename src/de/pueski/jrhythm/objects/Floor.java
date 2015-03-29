package de.pueski.jrhythm.objects;

import org.lwjgl.opengl.GL11;

import de.pueski.jrhythm.core.GLDrawable;

/**
 * The floor is acts as a reference. It is basically the bottom of a box.
 * 
 * @author Stephen Jones
 */
public class Floor implements GLDrawable {

	public void draw() {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor3f(0.7f, 0.7f, 0.7f);
		GL11.glNormal3f(0.0f,1.0f,0.0f);
		GL11.glVertex3f(-100.0f, 1.0f, 100.0f);
		GL11.glVertex3f(-100.0f, 1.0f, -100.0f);
		GL11.glVertex3f(100.0f, 1.0f, -100.0f);
		GL11.glVertex3f(100.0f, 1.0f, 100.0f);
		GL11.glEnd();
	}
}
