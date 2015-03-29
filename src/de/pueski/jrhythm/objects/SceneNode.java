package de.pueski.jrhythm.objects;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;

import de.pueski.jrhythm.core.GLDrawable;
import de.pueski.jrhythm.core.Light;
import de.pueski.jrhythm.math.Vector3f;
import de.pueski.jrhythm.scene.AbstractScene;
import de.pueski.jrhythm.test.GLShadowOnPlane;


/**
 * <p>
 * This class represents an abstract element of a typical {@link AbstractScene} hierarchy.
 * </p>
 * <p>
 * Typically a scene contains a root node, from which the scene is being rendered recursively.
 * This allows us to build complex structures and scale and translate them very easy. We can 
 * use this for BSP trees and cisibility checks as well
 * </p>
 * <p>
 * Each object in a scene such as {@link Light}, {@link Mesh} and {@link Box} objects
 * must inherit this class in order to be available inside a scene. 
 * </p>
 * <p>
 * This class is also used to serialize a scene to XML.
 * </p>
 * 
 * @see DefaultNode
 * @see AbstractScene
 * @see Mesh
 * @see Box
 * 
 * @author Matthias Pueski
 */

@XmlType(propOrder = {"name","children","location","xrot","yrot","zrot","shaderProgramName","textureLocation","textured"}, name="SceneNode")
@XmlSeeAlso({Box.class,Plane.class,Light.class})
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class SceneNode implements Serializable, Comparable<SceneNode>, GLDrawable {
	
	@XmlTransient
	private static final long serialVersionUID = -5753960609985066324L;
	@XmlTransient
	private static final Log log = LogFactory.getLog(SceneNode.class);
	@XmlTransient
	protected static final String PATH_DELIMITER = "/";
	@XmlTransient
	protected Long id;
	protected String name;
	@XmlTransient
	protected String path = "";
	@XmlTransient
	protected boolean restricted;
	@XmlTransient
	protected SceneNode parent;
	@XmlElements({
		@XmlElement(name="children"),
	})
	// The children of this node
	protected SortedSet<SceneNode> children = new TreeSet<SceneNode>();;
	// the location in 3D space
	protected Vector3f location = new Vector3f(0.0f,0.0f,0.0f);
	@XmlTransient
	// Shadow caster for planar surfaces
	protected GLShadowOnPlane shadow; 
	
	public float xrot = 0.0f;
	public float yrot = 0.0f;
	public float zrot = 0.0f;
	
	@XmlTransient
	private int shaderProgramID;
	private String shaderProgramName;
	
	@XmlTransient
	protected boolean useShader = false;
	protected boolean textured = false;
	
	protected String textureLocation = null;
	
	@XmlTransient
	protected final LinearVelocity linearVelocity;
	@XmlTransient
	protected long birthTime = 0;
	@XmlTransient
	protected long currentLifeTime = 0;
	@XmlTransient
	protected long maxLifeTime = 0;
	
	protected SceneNode() {
		linearVelocity = new LinearVelocity();
	}	
	
	/**
	 * Creates a new {@link SceneNode} with a given name
	 * 
	 * @param name
	 */
	public SceneNode(String name) {
		this();
		this.name = name;
	}
	
	/**
	 * Determines the full path of this node until the root node is reached
	 * 
	 * @return the path
	 */
	public String getPath() {
		path = "";
		evalPath(this);
		path = path.substring(0, path.length() - 1);
		return path;
	}

	/**
	 * Searches the path for a node ending at the root node
	 * 
	 * @param n the node to evaluate the path for
	 */
	protected void evalPath(SceneNode n) {
		if (n != null) {
			path = n.getName() + PATH_DELIMITER + path;
			if (n.getParent() != null)
				evalPath(n.getParent());
		}
	}

	/**
	 * gets the id of the node
	 * 
	 * @return the id of the node
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id of the node
	 * 
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the name of the node
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the node
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets all the children of this node
	 * 
	 * @return the associated chilren
	 */
	public Set<SceneNode> getChildren() {
		return children;
	}

	/**
	 * Sets the children of this node
	 * 
	 * @param children
	 */
	public void setChildren(SortedSet<SceneNode> children) {
		this.children = children;
	}

	/**
	 * Gets the parent of this node
	 * 
	 * @return
	 */
	public SceneNode getParent() {
		return parent;
	}

	/**
	 * Sets the parent of this node
	 * 
	 * @param parent the parent to set
	 */
	public void setParent(SceneNode parent) {
		this.parent = parent;
	}

	/**
	 * Checks if this node is a root node or not
	 * 
	 * @return true if this node is root false if not
	 */
	public boolean isRootNode() {
		return (this.getParent() != null) ? false : true;
	}

	public int compareTo(SceneNode o) {
//		if (o != null)
//			return o.getPath().compareTo(getPath());
//		else return -1;
		return 328;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof SceneNode) {
			SceneNode node = (SceneNode) obj;
			if (node.getParent() == null) {
				if (this.name.equals(node.getName()) && this.getParent() == null)
					return true;
			}
			else {
				if (this.getParent() == null)
					return false;
				else if (this.name.equals(node.getName()) && this.parent.equals(node.getParent()))
					return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (name == null)
			return 42;
		return this.name.hashCode();
	}

	/**
	 * Checks if a given node is a hidden node, a node is hidden if it contains
	 * any underslash in its name.
	 * 
	 * @return true if the node is hidden, false if not
	 */
	public boolean isHidden() {
		return (getName().indexOf("_") >= 0);
	}

	public boolean hasChildren() {
		if (children == null)
			return false;
		return (getChildren().size() > 0);
	}

	/**
	 * @return the location
	 */
	public Vector3f getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Vector3f location) {
		this.location = location;
	}

	/**
	 * @return the xrot
	 */
	public float getXrot() {
		return xrot;
	}

	/**
	 * @param xrot the xrot to set
	 */
	public void setXrot(float xrot) {
		this.xrot = xrot;
	}

	/**
	 * @return the yrot
	 */
	public float getYrot() {
		return yrot;
	}

	/**
	 * @param yrot the yrot to set
	 */
	public void setYrot(float yrot) {
		this.yrot = yrot;
	}

	/**
	 * @return the zrot
	 */
	public float getZrot() {
		return zrot;
	}

	/**
	 * @param zrot the zrot to set
	 */
	public void setZrot(float zrot) {
		this.zrot = zrot;
	}
	
	/**
	 * @return the linearVelocity
	 */
	public LinearVelocity getLinearVelocity() {
		return linearVelocity;
	}

	/**
	 * Adds a child to this node
	 * 
	 * @param child
	 */
	public void addChild(SceneNode child) {
		children.add(child);
	}
	
	/**
	 * Adds a child to this node
	 * 
	 * @param child
	 */
	public void addAllChildren(ArrayList<SceneNode> childs) {		
		for (SceneNode child : childs) {
			children.add(child);	
		}		
	}
	
	/**
	 * @return the birthTime
	 */
	public long getBirthTime() {
		return birthTime;
	}

	/**
	 * @param birthTime the birthTime to set
	 */
	public void setBirthTime(long birthTime) {
		this.birthTime = birthTime;
	}

	/**
	 * @return the currentLifeTime
	 */
	public long getCurrentLifeTime() {
		return currentLifeTime;
	}

	/**
	 * @param currentLifeTime the currentLifeTime to set
	 */
	public void setCurrentLifeTime(long currentLifeTime) {
		this.currentLifeTime = currentLifeTime;
	}

	/**
	 * @return the maxLifeTime
	 */
	public long getMaxLifeTime() {
		return maxLifeTime;
	}

	/**
	 * @param maxLifeTime the maxLifeTime to set
	 */
	public void setMaxLifeTime(long maxLifeTime) {
		this.maxLifeTime = maxLifeTime;
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
		shaderProgramID = ARBShaderObjects.glCreateProgramObjectARB();
		ARBShaderObjects.glAttachObjectARB(shaderProgramID, vertexShaderID);
		ARBShaderObjects.glAttachObjectARB(shaderProgramID, fragmentShaderID);
		ARBShaderObjects.glLinkProgramARB(shaderProgramID);
	}
	
	public void attachTexture(String name) {		
		textureLocation = name;	
		textured = true;
	}

	
	/**
	 * @return the textured
	 */
	public boolean isTextured() {
		return textured;
	}

	/**
	 * @param textured the textured to set
	 */
	public void setTextured(boolean textured) {
		this.textured = textured;
	}

	/**
	 * @return the shaderProgramName
	 */
	public String getShaderProgramName() {
		return shaderProgramName;
	}

	/**
	 * @param shaderProgramName the shaderProgramName to set
	 */
	public void setShaderProgramName(String shaderProgramName) {
		this.shaderProgramName = shaderProgramName;
	}

}
