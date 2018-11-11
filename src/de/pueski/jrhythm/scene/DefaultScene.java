package de.pueski.jrhythm.scene;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import de.pueski.jrhythm.core.Light;
import de.pueski.jrhythm.math.Vector3f;
import de.pueski.jrhythm.math.Vector4f;
import de.pueski.jrhythm.objects.Box;
import de.pueski.jrhythm.objects.Camera;
import de.pueski.jrhythm.objects.Camera.Mode;
import de.pueski.jrhythm.objects.DefaultNode;
import de.pueski.jrhythm.objects.Mesh;
import de.pueski.jrhythm.objects.Plane;
import de.pueski.jrhythm.texture.TextureManager;

@XmlRootElement
public class DefaultScene extends AbstractScene {

	private DefaultNode sceneNode;
	private ArrayList<Light> lights;
	
	float camrot = 0.0f;
	float radius = 35.0f;
	
	float lightRot = 0.0f;
	float lightRadius = 300.0f;

	long currentTime;
	long delta;
	
	private Mesh cube;
	
	int num = 0;
	
	public DefaultScene() {
		super();
		currentTime = System.currentTimeMillis();
	}

	public DefaultScene(String name) {
		super(name);
		currentTime = System.currentTimeMillis();
	}

	@Override
	public void render() {
		
		super.render();
		
		drawChildren(sceneNode);
		drawChildren(rootNode);

		for (Light light : lights) {
			if (light.isDropShadow()) {
				calculateChildrenShadowSilhouette(rootNode, light);
				renderShadow(rootNode, light);				
			}
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

		showGrid = true;
		
		TextureManager tm = TextureManager.getInstance();
//
		tm.addTexture("textures/parkett.jpg");
//
		sceneNode = new DefaultNode("scene");
//
		lights = new ArrayList<Light>();
//
		Light light1 = new Light(GL_LIGHT1);
		light1.setAmbient(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
		light1.setDiffuse(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
		light1.setSpecular(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
		light1.setLocation(new Vector3f(-100.1f, 200.2f, -100.2f));
		light1.setDropShadow(true);
//		
		totalObjects++;

		sceneNode.addChild(light1);
		lights.add(light1);
//
//		Plane p1 = new Plane(new Vector3f(-0.0f, 0.0f, -0.0f), 40.0f);
//		p1.attachTexture("textures/parkett.jpg");
//		p1.setTextureScale(2);
//		totalFaces++;
//		totalVertices+=4;
//		totalObjects++;
//		sceneNode.addChild(p1);		
//		
//		cube = addMesh(rootNode, "objects/cube.obj", new Vector3f(0,1,0));
//		cube.setShadow(true);
		
	}

	public void genLists() {
		rootListId = glGenLists(1);
		glNewList(rootListId, GL_COMPILE);
		drawChildren(rootNode);
		glEndList();
	}

	public void writeToXml(String location) throws Exception {
		Marshaller m = JAXBContext.newInstance(DefaultScene.class, Box.class, Plane.class, Light.class).createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(this, new FileOutputStream(new File(location)));
	}



	
	
}
