package de.pueski.jrhythm.util;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL14.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FBOUtils {

	private static final Log log = LogFactory.getLog(FBOUtils.class);
	
	/**
	 * Renders a given {@link RenderCallback} to a previously configured Framebuffer 
	 * 
	 * @param frameBufferId The id of the framebuffer to use
	 * @param callback the callback to use to render the scebe
	 * @param width the width of the render viewport 
	 * @param height the height of the render viewport
	 */
	public static void renderDepthBufferToTexture(int frameBufferId,RenderCallback callback, int width, int height) {
		
		glViewport (0, 0, width,height);									// set The Current Viewport to the fbo size
		glEnable(GL_TEXTURE_2D);										// enable texturing
		
		glBindTexture(GL_TEXTURE_2D, 0);								// unlink textures because if we dont it all is gonna fail
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, frameBufferId);		// switch to rendering on our FBO
		
		glEnable(GL_DEPTH_TEST);	
		
		glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );	
		
		glDrawBuffer(GL_NONE);
		glReadBuffer(GL_NONE);

		callback.render();
		
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		
		glDrawBuffer(GL_FRONT);
		glReadBuffer(GL_FRONT);
	}
	
	
	/**
	 * Configures a framebuffer object for depth buffer rendering and returns the according buffer id 
	 * 
	 * @param textureId the texture to render the depth buffer to
	 * @param width the width of the texture
	 * @param height the height of the texture
	 * @return the framebuffer id
	 */
	public static int getDepthBufferFBO(int textureId, int width, int height) {

		glBindTexture(GL_TEXTURE_2D, textureId);
		
//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_DEPTH_TEXTURE_MODE, GL_INTENSITY);
//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);
//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
		
		//glTexParameterfv( GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor );
		
		// No need to force GL_DEPTH_COMPONENT24, drivers usually give you the max precision if available 
		glTexImage2D( GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, width, height, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_INT, (java.nio.ByteBuffer) null);
		// glBindTexture(GL_TEXTURE_2D, 0);
		
		// create a framebuffer object
		int framebufferID = glGenFramebuffersEXT();		
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID);

		// attach the texture to FBO depth attachment point
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT,GL_TEXTURE_2D, textureId, 0);
		
		// Instruct openGL that we won't bind a color texture with the currently binded FBO
		glDrawBuffer(GL_NONE);
		glReadBuffer(GL_NONE);
		
		// check FBO status
		int FBOstatus = glCheckFramebufferStatusEXT(GL_FRAMEBUFFER_EXT);
		if(FBOstatus != GL_FRAMEBUFFER_COMPLETE_EXT)
			log.error("GL_FRAMEBUFFER_COMPLETE_EXT failed, CANNOT use FBO\n");
		else
			log.info("Frambuffer supported.");
				
		return framebufferID;
	}	
	
	/**
	 * Renders a given texture 2D to the viewport with a given size
	 * 
	 * @param textureId the texture id of the texture to use
	 * @param width the width of the viewport
	 * @param height the height of the viewport
	 */
	public static void renderTexToViewport(int textureId, int width, int height)  {
		
		glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);			

		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, textureId);							
		glViewport (0, 0, width, height);				
		
		glPushMatrix();
		glLoadIdentity();
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, 1, 0, 1, 0, 1);
		glColor4f(0.5f, 0.5f, 0.5f, 1);
		
		glBegin(GL_QUADS);
		{
			glTexCoord2f(0, 1);
			glVertex2i(0, 1);
			glTexCoord2f(0, 0);
			glVertex2i(0, 0);
			glTexCoord2f(1, 0);
			glVertex2i(1, 0);
			glTexCoord2f(1, 1);
			glVertex2i(1, 1);
		}		
		glEnd();

		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();
		
		glDisable(GL_TEXTURE_2D);
		glFlush ();		
	}
}
