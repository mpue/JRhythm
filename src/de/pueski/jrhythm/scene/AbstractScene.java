package de.pueski.jrhythm.scene;

import static org.lwjgl.opengl.GL11.*;

import java.io.DataInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.bulletphysics.linearmath.Clock;

import de.pueski.jrhythm.core.Game;
import de.pueski.jrhythm.core.Light;
import de.pueski.jrhythm.math.Vector3f;
import de.pueski.jrhythm.math.Vector4f;
import de.pueski.jrhythm.objects.Camera;
import de.pueski.jrhythm.objects.DefaultNode;
import de.pueski.jrhythm.objects.Mesh;
import de.pueski.jrhythm.objects.MeshFactory;
import de.pueski.jrhythm.objects.MeshHolder;
import de.pueski.jrhythm.objects.SceneNode;
import de.pueski.jrhythm.util.UIUtils;


@XmlRootElement
@XmlType(propOrder = { "name","rootNode","currentCameraIndex","cameras" })
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractScene {

	@XmlTransient
	private static final Log log = LogFactory.getLog(AbstractScene.class);
	
	protected String name;	
	@XmlElements({
		@XmlElement(name="rootNode", type=DefaultNode.class),
	})
	protected SceneNode rootNode;	
	protected int currentCameraIndex = 0;
	protected ArrayList<Camera> cameras;	
	@XmlTransient
	protected int rootListId;

	@XmlTransient
	protected int shaderProgramID;
	
	@XmlTransient
	protected boolean useShader = false;
	
	@XmlTransient
	protected int totalVertices = 0;
	@XmlTransient
	protected int totalFaces = 0;
	@XmlTransient
	protected int totalObjects = 0;

	protected Vector4f backgroundColor;
	
	protected boolean showGrid = true;
	
	protected boolean physicsEnabled = false;
	
	protected final Clock clock = new Clock();
	
	public AbstractScene() {		
	}
	
	protected AbstractScene(String name) {
		if (name == null || name.length() < 1) {
			throw new IllegalArgumentException("Scene must have a name.");
		}
		
		this.name = name;
		rootNode = new DefaultNode("root");
		cameras = new ArrayList<Camera>();
		
		Game.getInstance().setMessage("Initializing scene : "+name);
		init();
		genLists();
		
	}
	
	public void updateChildren(SceneNode node) {				
		if (node.getChildren() != null && node.getChildren().size() > 0) {
			for (Iterator<SceneNode> it = node.getChildren().iterator();it.hasNext();) {
				
				SceneNode child = it.next();
				
				child.setCurrentLifeTime(System.currentTimeMillis() - child.getBirthTime());
				
				GL11.glPushMatrix(); {					
					if (!physicsEnabled) {
						child.setLocation(new Vector3f(child.getLocation().x + child.getLinearVelocity().getX(), 
													   child.getLocation().y + child.getLinearVelocity().getY(), 
													   child.getLocation().z + child.getLinearVelocity().getZ()));
						
					}					
				}
				GL11.glPopMatrix();
				
				// if lifetime is enabled we check if the child is still alive
				if (child.getMaxLifeTime() > 0) {
					if (child.getCurrentLifeTime() >= child.getMaxLifeTime()) {
						it.remove();						
					}
				}
				else {
					updateChildren(child);					
				}
				
			}
		}		
	}
	
	public void render() {
		
		if (backgroundColor != null)
			glClearColor(backgroundColor.x,backgroundColor.y,backgroundColor.z,backgroundColor.w);
		else
			glClearColor(0, 0, 0, 1);
		
		if (showGrid)
			UIUtils.drawZGrid(0,2.5f, 100, 100);
	}
	
	/**
	 * <p>
	 * Renders the shadow based on the Z-Fail method.
	 * </p>
	 * <p>
	 * This also known as Carmack's Reverse. Based on the fact that Creative
	 * Labs has a patent on it (lol), we need to obfuscate this in some way later.
	 * </p>   
	 */
	void renderShadow(SceneNode node, Light light) {
		glColorMask(false,false,false,false);
		glDepthMask(false);
		glEnable(GL_CULL_FACE);
		glEnable(GL_STENCIL_TEST);
		glEnable(GL_POLYGON_OFFSET_FILL);
		// glPolygonOffset(0.0f, 100.0f);

		glCullFace(GL_FRONT);
		glStencilFunc(GL_ALWAYS, 0x0, 0xff);
		glStencilOp(GL_KEEP, GL_INCR, GL_KEEP);
		drawChildrenShadowVolume(node, light);

		glCullFace(GL_BACK);
		glStencilFunc(GL_ALWAYS, 0x0, 0xff);
		glStencilOp(GL_KEEP, GL_DECR, GL_KEEP);
		drawChildrenShadowVolume(node, light);

		glDisable(GL_POLYGON_OFFSET_FILL);
		glDisable(GL_CULL_FACE);
		glColorMask(true,true,true,true);
		glDepthMask(true);

		glStencilFunc(GL_NOTEQUAL, 0x0, 0xff);
		glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
		drawShadow();
		glDisable(GL_STENCIL_TEST);		
	}
	
	
	/**
	 * Draws the stencil shadowed areas onto the screen.
	 */
	void drawShadow() {
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
	}	
	
	/**
	 * Recursively draws all children of a selected node 
	 * 
	 * @param node
	 */
	protected void drawChildren(SceneNode node) {				
		if (node.getChildren() != null && node.getChildren().size() > 0) {
			for (SceneNode child : node.getChildren()) {
				GL11.glPushMatrix();
					child.draw();
				GL11.glPopMatrix();			
				drawChildren(child);
			}
		}		
	}

	protected void drawChildrenShadowSilhouette(SceneNode node, Light light) {				
		if (node.getChildren() != null && node.getChildren().size() > 0) {
			for (SceneNode child : node.getChildren()) {
				GL11.glPushMatrix();
					if (child instanceof Mesh) {
						Mesh mesh = (Mesh)child;		
						mesh.drawSilhouette();
					}
				GL11.glPopMatrix();			
				drawChildrenShadowSilhouette(child, light);
			}
		}		
	}	

	protected void calculateChildrenShadowSilhouette(SceneNode node, Light light) {				
		if (node.getChildren() != null && node.getChildren().size() > 0) {
			for (SceneNode child : node.getChildren()) {
				GL11.glPushMatrix();
					if (child instanceof Mesh) {
						Mesh mesh = (Mesh)child;
						if (mesh.isShadow())
							mesh.calculateSilhouette(light);		
					}
					else if (child instanceof MeshHolder) {
						MeshHolder holder = (MeshHolder)child;
						if (holder.getMesh().isShadow()) {
							holder.getMesh().setLocation(holder.getLocation());
							holder.getMesh().calculateSilhouette(light);
						}
						
						
					}
				GL11.glPopMatrix();			
				calculateChildrenShadowSilhouette(child, light);
			}
		}		
	}	

	protected void drawChildrenShadowVolume(SceneNode node, Light light) {				
		if (node.getChildren() != null && node.getChildren().size() > 0) {
			for (SceneNode child : node.getChildren()) {
				GL11.glPushMatrix();
					if (child instanceof Mesh) {
						Mesh mesh = (Mesh)child;
						if (mesh.isShadow()) {
							mesh.calculatePlanes();
							mesh.checkVisibility(light);
							mesh.drawShadowVolume(light,false);							
						}
					}
					else if(child instanceof MeshHolder) {
						MeshHolder holder = (MeshHolder)child;
						Mesh mesh = holder.getMesh();
						mesh.setLocation(holder.getLocation());
						mesh.calculatePlanes();
						mesh.checkVisibility(light);
						mesh.drawShadowVolume(light,false);							
					}
				GL11.glPopMatrix();			
				drawChildrenShadowVolume(child, light);
			}
		}		
	}
	
	/**
	 * <p>
	 * Convenience method for adding meshes to a node:
	 * </p>
	 * <p>
	 * Loads and adds a {@link Mesh} from a given path to a specific parent node.
	 * </p>
	 * 
	 * @param parent   The parent node to add the mesh to
	 * @param path     The path to load the mesh from
	 * @param location The location of t he mesh in the scene
	 * 
	 * @return The added {@link Mesh} object
	 */
	public Mesh addMesh(SceneNode parent, String path , Vector3f location) {
		return addMesh(parent,null,path, location);
	}
	
	/**
	 * <p>
	 * Convenience method for adding meshes to a node:
	 * </p>
	 * <p>
	 * Loads and adds a {@link Mesh} from a given path to a specific parent node.
	 * </p>
	 * 
	 * @param parent   The parent node to add the mesh to
	 * @param name     The name of the mesh to be added, if it is null, the name is determined automatically
	 * @param path     The path to load the mesh from
	 * @param location The location of t he mesh in the scene
	 * 
	 * @return The added {@link Mesh} object
	 */
	public Mesh addMesh(SceneNode parent,String name, String path , Vector3f location) {
		Mesh mesh = (Mesh) MeshFactory.getInstance().getFromWavefrontObj(path).get(0);
		mesh.setLocation(location);
		mesh.setBirthTime(System.currentTimeMillis());

		if (name == null) {
			
			int copies = 0;
			
			for (SceneNode child : parent.getChildren()) {
				
				if (child.getName().equals(mesh.getName()) || child.getName().startsWith(mesh.getName()+".")) {
					copies++;
				}
				
			}
			
			if (copies > 0) {
				mesh.setName(mesh.getName()+"."+String.valueOf(copies));			
			}
			
		}
		else {
			mesh.setName(name);
		}
		
		parent.addChild(mesh);		
		totalFaces += mesh.getFaceCache().size();
		totalVertices += mesh.getVertices().size();
		totalObjects++;
		log.info("added mesh "+mesh.getName());
		return mesh;
	}
	
	
	
	/**
	 * Convenience method for removing {@link Mesh} from a {@link SceneNode}
	 * 
	 * @param parent the parent node to remove the mesh from
	 * @param name the name of the mesh to be removed
	 */
	public void removeMesh(SceneNode parent,String name) {
		
		for (Iterator<SceneNode> it = parent.getChildren().iterator();it.hasNext();) {
			
			SceneNode node = it.next();
			
			if (node instanceof Mesh) {
				Mesh mesh = (Mesh)node;
				if(mesh.getName().equals(name)) {
					it.remove();
					return;
				}
				
			}
			
		}
		
	}
	
	public void setMeshProperty(SceneNode parent,String name,String propertyName,Object value) {
		
		for (Iterator<SceneNode> it = parent.getChildren().iterator();it.hasNext();) {
			
			SceneNode node = it.next();
			
			if (node instanceof Mesh) {
				Mesh mesh = (Mesh)node;
				if(mesh.getName().equals(name)) {

			    	for (int i=0;i < Mesh.class.getMethods().length;i++) {
			    		Method method = Mesh.class.getMethods()[i];
			    		// only search in setters
			    		if (method.getName().startsWith("set")) {
			    			// setter has only one param and name is the desired property
			    			if (method.getParameterTypes().length == 1 &&
			    				method.getName().substring(3).equalsIgnoreCase(propertyName)	) {
			    				// argument type is the desired type
			    				// if (method.getParameterTypes()[0] == value.getClass()) {			    					
			    					try {										 
										method.invoke(mesh, value);
									}
									catch (Exception e) {
										log.error(e.getMessage());
									}			    					
			    				// }			    				
			    			}			    			
			    		}					
			    	}					
				}
				
				return;
			}
			
		}
		
	}
 
	
	public void attachShader(SceneNode parent, String name, String shaderName) {
		for (Iterator<SceneNode> it = parent.getChildren().iterator();it.hasNext();) {
			
			SceneNode node = it.next();
			
			if (node instanceof Mesh) {
				Mesh mesh = (Mesh)node;
				if(mesh.getName().equals(name)) {
					mesh.attachShader("shaders/"+shaderName);
					return;
				}
				
			}
			
		}
		
	}
	
	protected abstract void init();
	
	protected abstract void genLists();
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the rootNode
	 */
	public SceneNode getRootNode() {
		return rootNode;
	}

	/**
	 * @return the cameras
	 */
	public ArrayList<Camera> getCameras() {
		return cameras;
	}
	
	public Camera getCurrentCamera() {
		return cameras.get(currentCameraIndex);
	}
	
	/**
	 * @return the currentCameraIndex
	 */
	public int getCurrentCameraIndex() {
		return currentCameraIndex;
	}
	
	/**
	 * @param currentCameraIndex the currentCameraIndex to set
	 */
	public void setCurrentCameraIndex(int currentCameraIndex) {
		this.currentCameraIndex = currentCameraIndex;
	}
	
	/**
	 * @return the totalVertices
	 */
	public int getTotalVertices() {
		return totalVertices;
	}

	/**
	 * @param totalVertices the totalVertices to set
	 */
	public void setTotalVertices(int totalVertices) {
		this.totalVertices = totalVertices;
	}

	/**
	 * @return the totalFaces
	 */
	public int getTotalFaces() {
		return totalFaces;
	}

	/**
	 * @param totalFaces the totalFaces to set
	 */
	public void setTotalFaces(int totalFaces) {
		this.totalFaces = totalFaces;
	}

	/**
	 * @return the totalObjects
	 */
	public int getTotalObjects() {
		return totalObjects;
	}

	/**
	 * @param totalObjects the totalObjects to set
	 */
	public void setTotalObjects(int totalObjects) {
		this.totalObjects = totalObjects;
	}	
	
	/**
	 * @return the backgroundColor
	 */
	public Vector4f getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public void setBackgroundColor(Vector4f backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	@Override
	public boolean equals(Object obj) {
		return name.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	public void attachShader(String name) {
		try {
			final ByteBuffer vertexShader = loadShader(name+".vert");
			final ByteBuffer fragmentShader = loadShader(name+".frag");
			prepareShader(vertexShader, fragmentShader);
		} 
		catch (Exception e) {
			log.error("Failed loading GLSL shader program "+name+".");
		}
		useShader = true;
	}
	
	public void enableShader() {
		ARBShaderObjects.glUseProgramObjectARB(shaderProgramID);		
	}
	
	public void disableShader() {
		ARBShaderObjects.glUseProgramObjectARB(0);
	}
	
	/**
	 * Loads a shader program for this node
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected ByteBuffer loadShader(String name) throws Exception {
		ClassLoader loader = this.getClass().getClassLoader();
		InputStream is = loader.getResourceAsStream(name);
		byte[] shadercode = null;
	    DataInputStream dis = new DataInputStream(is);
	    dis.readFully(shadercode = new byte[is.available()]);
	    dis.close();
	    is.close();
		ByteBuffer shader = BufferUtils.createByteBuffer(shadercode.length);
		shader.put(shadercode);
		shader.flip();
		return shader;
	}
	
	/**
	 * Prepares (e.g. compiles) the given vertex and fragment shader, 
	 * geometry shaders are not supported at the moment.
	 * 
	 * @param vertexShader
	 * @param fragmentShader
	 */
	protected void prepareShader(ByteBuffer vertexShader, ByteBuffer fragmentShader) {
		int vertexShaderID = ARBShaderObjects.glCreateShaderObjectARB(
		ARBVertexShader.GL_VERTEX_SHADER_ARB);
		int fragmentShaderID = ARBShaderObjects.glCreateShaderObjectARB(
		ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
		ARBShaderObjects.glShaderSourceARB(vertexShaderID, vertexShader);
		ARBShaderObjects.glCompileShaderARB(vertexShaderID);	
		ARBShaderObjects.glShaderSourceARB(fragmentShaderID, fragmentShader);
		ARBShaderObjects.glCompileShaderARB(fragmentShaderID);		
		System.out.println(GL20.glGetShaderInfoLog(fragmentShaderID, 1024));
		shaderProgramID = ARBShaderObjects.glCreateProgramObjectARB();
		ARBShaderObjects.glAttachObjectARB(shaderProgramID, vertexShaderID);
		ARBShaderObjects.glAttachObjectARB(shaderProgramID, fragmentShaderID);
		ARBShaderObjects.glLinkProgramARB(shaderProgramID);
	}
	
	public abstract void writeToXml(String location) throws Exception;

	public boolean isShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}

	/**
	 * @return the physicsEnabled
	 */
	public boolean isPhysicsEnabled() {
		return physicsEnabled;
	}

	/**
	 * @param physicsEnabled the physicsEnabled to set
	 */
	public void setPhysicsEnabled(boolean physicsEnabled) {
		this.physicsEnabled = physicsEnabled;
	}

	public float getDeltaTimeMicroseconds() {
		// #ifdef USE_BT_CLOCK
		float dt = clock.getTimeMicroseconds();
		clock.reset();
		return dt;
		// #else
		// return btScalar(16666.);
		// #endif
	}
	
	
}
