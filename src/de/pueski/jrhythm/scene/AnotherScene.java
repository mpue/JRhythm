package de.pueski.jrhythm.scene;

import static org.lwjgl.opengl.GL11.GL_LIGHT1;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import de.pueski.jrhythm.core.Game;
import de.pueski.jrhythm.core.Light;
import de.pueski.jrhythm.math.Vector3f;
import de.pueski.jrhythm.math.Vector4f;
import de.pueski.jrhythm.objects.Box;
import de.pueski.jrhythm.objects.Camera;
import de.pueski.jrhythm.objects.Camera.Mode;
import de.pueski.jrhythm.objects.DefaultNode;
import de.pueski.jrhythm.objects.Mesh;
import de.pueski.jrhythm.objects.Plane;
import de.pueski.jrhythm.objects.SceneNode;
import de.pueski.jrhythm.texture.TextureManager;

@XmlRootElement
public class AnotherScene extends AbstractScene {

	final boolean renderToTexture = true;
	
	private DefaultNode sceneNode;
	private ArrayList<Light> lights;

	int colorTextureID;
	int framebufferID;
	int depthRenderBufferID;
	
	public AnotherScene() {
		super();
	}

	public AnotherScene(String name) {
		super(name);
	}

	@Override
	public void render() {
		
		drawChildren(sceneNode);
		drawChildren(rootNode);
		
		for (Light light : lights) {
			calculateChildrenShadowSilhouette(rootNode, light);
			// drawChildrenShadowSilhouette(rootNode, light);
			// drawChildrenShadowVolume(rootNode, light);
			renderShadow(rootNode, light);
		}
		
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
		light1.setLocation(new Vector3f(-500.1f, 500.2f, -500.2f));
		light1.setDropShadow(true);
		
		totalObjects++;

		sceneNode.addChild(light1);
		lights.add(light1);

		Plane p1 = new Plane(new Vector3f(-0.0f, 0.0f, -0.0f), 40.0f);
		p1.attachTexture("textures/parkett.jpg");
		p1.setTextureScale(2);
		totalFaces++;
		totalVertices+=4;
		totalObjects++;
		sceneNode.addChild(p1);

//		for (int x = 0; x < 28; x += 4) {
//
//			for (int y = 0; y < 28; y += 4) {
//				Mesh m = addMesh(rootNode,"objects/simplechair.obj" , new Vector3f(x, 0, y));
//				m.attachShader("shader/simpleTexture");
//			}
//			
//		}
		
		Mesh mesh;
		
		mesh = addMesh(rootNode,"objects/cube.obj" , new Vector3f(0, 1, 0));
		mesh.setShadow(true);
		mesh.attachShader("shaders/simpleTexture");
		mesh = addMesh(rootNode,"objects/hypercube.obj" , new Vector3f(-8.0f, 1, -8.0f));
		mesh.setShadow(true);
		mesh.attachShader("shaders/simpleTexture");
		mesh = addMesh(rootNode,"objects/simplechair.obj" , new Vector3f(8.0f, 0, 11.0f));
		mesh.setShadow(true);
		mesh.setYrot(180);
		mesh.attachShader("shaders/simpleTexture");
		mesh = addMesh(rootNode,"objects/simplechair.obj" , new Vector3f(8.0f, 0, 1.0f));
		mesh.setShadow(true);
		mesh.attachShader("shaders/simpleTexture");
		mesh.setShadow(true);
		mesh = addMesh(rootNode,"objects/simplechair.obj" , new Vector3f(8.0f, 0, 5.0f));
		mesh.setShadow(true);
		mesh.attachShader("shaders/simpleTexture");
		mesh = addMesh(rootNode,"objects/simplechair.obj" , new Vector3f(4.0f, 0, 8.0f));
		mesh.setShadow(true);
		mesh.attachShader("shaders/simpleTexture");
		
		mesh = addMesh(rootNode,"objects/simplechair.obj" , new Vector3f(8.0f, 10, 1.0f));
		mesh.setShadow(true);
		mesh.attachShader("shaders/simpleTexture");
		mesh = addMesh(rootNode,"objects/simplechair.obj" , new Vector3f(8.0f, 10, 5.0f));
		mesh.setShadow(true);
		mesh.attachShader("shaders/simpleTexture");
		mesh = addMesh(rootNode,"objects/simplechair.obj" , new Vector3f(4.0f, 10, 8.0f));
		mesh.setShadow(true);
		mesh.attachShader("shaders/simpleTexture");

		
//		addMesh(rootNode,"objects/grid.obj" , new Vector3f(-8, 10, -8));		
//		addMesh(rootNode,"objects/grid.obj" , new Vector3f( 4, 10, -8));
//		addMesh(rootNode,"objects/grid.obj" , new Vector3f(-8, 10,  4));		
//		addMesh(rootNode,"objects/grid.obj" , new Vector3f( 4, 10,  4));
//		addMesh(rootNode,"objects/grid.obj" , new Vector3f(-8, 10,  16));		
//		addMesh(rootNode,"objects/grid.obj" , new Vector3f( 4, 10,  16));
		
		for (int i = 0; i < 10; i++) {
			mesh = addMesh(rootNode,"objects/simpleroom.obj" , new Vector3f(0, i*10, 0));			
		}
		
		

		mesh = addMesh(rootNode,"objects/bottom.obj" , new Vector3f(0, 10, 0));
		mesh.attachShader("shaders/simpleTexture");
	}
	
	public void genLists() {
//		rootListId = glGenLists(1);
//		glNewList(rootListId, GL_COMPILE);
//		drawChildren(rootNode);
//		glEndList();
	}

	public void writeToXml(String location) throws Exception {
		Marshaller m = JAXBContext.newInstance(AnotherScene.class, Box.class, Plane.class, Light.class).createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(this, new FileOutputStream(new File(location)));
	}

}
