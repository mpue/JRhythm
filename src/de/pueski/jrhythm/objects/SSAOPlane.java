package de.pueski.jrhythm.objects;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.EXTFramebufferObject;

public class SSAOPlane extends SceneNode {

	public SSAOPlane() {
		attachShader("shaders/ssao");
		EXTFramebufferObject.glGenFramebuffersEXT();
		
	}

	@Override
	public void draw() {

		// use shader program if available
		if (useShader) {
			enableShader();
		}

		glPushMatrix();
		glLoadIdentity();
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, 1, 1, 0, 0, 1);
		glDisable(GL_DEPTH_TEST);
		glColor4f(0.0f, 0.0f, 0.0f, 0.6f);
		glBegin(GL_QUADS);
		{
			glVertex2i(0, 0);
			glVertex2i(0, 1);
			glVertex2i(1, 1);
			glVertex2i(1, 0);
		}
		glEnd();
		glEnable(GL_DEPTH_TEST);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();

		// use shader program if available
		if (useShader) {
			disableShader();
		}

	}

}
