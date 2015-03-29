package de.pueski.jrhythm.scene;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.lwjgl.opengl.GL30;

import de.pueski.jrhythm.core.Game;
import de.pueski.jrhythm.core.Light;
import de.pueski.jrhythm.math.Vector3f;
import de.pueski.jrhythm.math.Vector4f;
import de.pueski.jrhythm.objects.Box;
import de.pueski.jrhythm.objects.Camera;
import de.pueski.jrhythm.objects.Camera.Mode;
import de.pueski.jrhythm.objects.DefaultNode;
import de.pueski.jrhythm.objects.Plane;
import de.pueski.jrhythm.objects.SceneNode;
import de.pueski.jrhythm.texture.TextureManager;
import de.pueski.jrhythm.util.FBOUtils;

@XmlRootElement
public class RenderToTextureScene extends AbstractScene {

	final boolean renderToTexture = true;
	
	private DefaultNode sceneNode;
	private ArrayList<Light> lights;

	int colorTextureID;
	int framebufferID;
	int depthRenderBufferID;
	
	public RenderToTextureScene() {
		super();
	}

	public RenderToTextureScene(String name) {
		super(name);
		attachShader("shaders/hblur");
	}
	
	@Override
	public void render() {
		
		glClearColor(0.8f, 0.8f, 1.0f, 1.0f);
		
		final int width = Game.getInstance().getDisplayMode().getWidth();
		final int height = Game.getInstance().getDisplayMode().getHeight();
		
		if (renderToTexture) {

			// FBO render pass
			
			glViewport (0, 0, width,height);									// set The Current Viewport to the fbo size
			glEnable(GL_TEXTURE_2D);										// enable texturing
			
			glBindTexture(GL_TEXTURE_2D, 0);								// unlink textures because if we dont it all is gonna fail
			glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID);		// switch to rendering on our FBO
			
			// glClearColor (0.0f,0.0f,0.0f, 1.0f);
			glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );			// Clear Screen And Depth Buffer on the fbo to red
			
		}
		
		drawChildren(sceneNode);
		drawChildren(rootNode);

		
		for (Light light : lights) {
			calculateChildrenShadowSilhouette(rootNode, light);
			renderShadow(rootNode, light);
		}
		
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);

		enableShader();
		FBOUtils.renderTexToViewport(colorTextureID, width, height);
		enableShader();
		
//		if (renderToTexture) {
//			
//			// Normal render pass
//			
//			glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);					// switch to rendering on the framebuffer
//			
//			// glClearColor (1.0f, 1.0f, 1.0f, 1.0f);
//			glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);			// Clear Screen And Depth Buffer on the framebuffer to black
//			
//			glBindTexture(GL_TEXTURE_2D, colorTextureID);					// bind our FBO texture
//
//			enableShader();
//			
//			int texLoc;
//			texLoc = GL20.glGetUniformLocation(shaderProgramID, "RTScene");
//			GL20.glUniform1i(texLoc, 0);
//			
//			glViewport (0, 0, width,height);									// set The Current Viewport
//			
//			glPushMatrix();
//			glLoadIdentity();
//			glMatrixMode(GL_PROJECTION);
//			glPushMatrix();
//			glLoadIdentity();
//			glOrtho(0, 1, 0, 1, 0, 1);
//			
//			// glDisable(GL_DEPTH_TEST);
//			glColor4f(0.0f, 1.0f, 1.0f,0.1f);
//			glBegin(GL_QUADS);
//			{
//				glTexCoord2f(0, 1);
//				glVertex2i(-1, -1);
//				glTexCoord2f(0, 0);
//				glVertex2i(-1, 1);
//				glTexCoord2f(1, 0);
//				glVertex2i(1, 1);
//				glTexCoord2f(1, 1);
//				glVertex2i(1, -1);
//			}
//			
//			
//			glEnd();
//			// glEnable(GL_DEPTH_TEST);
//			glPopMatrix();
//			glMatrixMode(GL_MODELVIEW);
//			glPopMatrix();
//			
//			glDisable(GL_TEXTURE_2D);
//			glFlush ();
//
//			disableShader();
//			
//		}
		
	}

	@Override
	public void init() {
		
		Camera camera1 = new Camera("cam1");
		camera1.update(0, 0);
		camera1.setMode(Mode.FIRST_PERSON);
		Camera camera2 = new Camera("cam2");
		camera2.update(0, 0);
		Camera camera3 = new Camera("cam3");
		camera3.update(0, 0);
		camera3.setHeight(25);
		
		getCameras().add(camera1);
		getCameras().add(camera2);
		getCameras().add(camera3);
		setCurrentCameraIndex(0);

		TextureManager tm = TextureManager.getInstance();

		tm.addTexture("textures/parkett.jpg");

		sceneNode = new DefaultNode("scene");

		lights = new ArrayList<Light>();

		Light light1 = new Light(GL_LIGHT1);
		light1.setAmbient(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
		light1.setDiffuse(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
		light1.setSpecular(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
		light1.setLocation(new Vector3f(-100.1f, 200.2f, -100.2f));
		
		totalObjects++;

		sceneNode.addChild(light1);
		lights.add(light1);

		Plane p1 = new Plane(new Vector3f(-0.0f, -1.0f, -0.0f), 40.0f);
		p1.attachTexture("textures/parkett.jpg");
		p1.setTextureScale(2);

		totalFaces++;
		totalVertices+=4;
		totalObjects++;
		
		sceneNode.addChild(p1);
//		
//		addMesh(rootNode,"objects/cylinder.obj" , new Vector3f(0, 0, 0));
//		addMesh(rootNode,"objects/hypercube.obj" , new Vector3f(-8.0f, 1, -8.0f));
//		Mesh chair = addMesh(rootNode,"objects/simplechair.obj" , new Vector3f(8.0f, 0, 11.0f));
//		chair.setYrot(180);
//
//		addMesh(rootNode,"objects/grid.obj" , new Vector3f(-8, 10, -8));		
//		addMesh(rootNode,"objects/grid.obj" , new Vector3f( 4, 10, -8));
//		addMesh(rootNode,"objects/grid.obj" , new Vector3f(-8, 10,  4));		
//		addMesh(rootNode,"objects/grid.obj" , new Vector3f( 4, 10,  4));
//		addMesh(rootNode,"objects/grid.obj" , new Vector3f(-8, 10,  16));		
//		addMesh(rootNode,"objects/grid.obj" , new Vector3f( 4, 10,  16));
//		addMesh(rootNode,"objects/simpleroom.obj" , new Vector3f(0, 0, 0));
		initFBO();
	}
	
	void initFBO() {
		
		final int width = Game.getInstance().getDisplayMode().getWidth();
		final int height = Game.getInstance().getDisplayMode().getHeight();
		
		framebufferID = glGenFramebuffersEXT();											// create a new framebuffer
		colorTextureID = glGenTextures();												// and a new texture used as a color buffer
		depthRenderBufferID = glGenRenderbuffersEXT();									// And finally a new depthbuffer

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID); 						// switch to the new framebuffer

		// initialize color texture
		glBindTexture(GL_TEXTURE_2D, colorTextureID);									// Bind the colorbuffer texture
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);				// make it linear filterd
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,GL_RGBA, GL_INT, (java.nio.ByteBuffer) null);	// Create the texture data
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT,GL_COLOR_ATTACHMENT0_EXT,GL_TEXTURE_2D, colorTextureID, 0); // attach it to the framebuffer


		// initialize depth renderbuffer
		glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, depthRenderBufferID);				// bind the depth renderbuffer
		glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL30.GL_DEPTH24_STENCIL8, width, height);	// get the data space for it
		glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT,GL_DEPTH_ATTACHMENT_EXT,GL_RENDERBUFFER_EXT, depthRenderBufferID); // bind it to the renderbuffer
		glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, GL_STENCIL_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT, depthRenderBufferID);
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		
		
	}
	
	
	public void genLists() {
//		rootListId = glGenLists(1);
//		glNewList(rootListId, GL_COMPILE);
//		drawChildren(rootNode);
//		glEndList();
	}

	public void writeToXml(String location) throws Exception {
		Marshaller m = JAXBContext.newInstance(RenderToTextureScene.class, Box.class, Plane.class, Light.class).createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(this, new FileOutputStream(new File(location)));
	}


}
