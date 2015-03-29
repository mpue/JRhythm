package de.pueski.jrhythm.core;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.lwjgl.BufferUtils;

import de.pueski.jrhythm.math.AxisAngleRotation;
import de.pueski.jrhythm.math.Matrix4f;
import de.pueski.jrhythm.math.Vector3f;
import de.pueski.jrhythm.math.Vector4f;
import de.pueski.jrhythm.objects.Mesh;
import de.pueski.jrhythm.objects.SceneNode;

/**
 * The {@link Light} class is the {@link SceneNode} implementation for an OpenGL
 * light object.
 * 
 * @author Matthias Pueski
 * 
 */
@XmlType(propOrder = { "index", "ambient", "diffuse", "specular" }, name = "Light")
@XmlAccessorType(XmlAccessType.FIELD)
public class Light extends SceneNode {

	private static final long serialVersionUID = -6307674633780547669L;

	private int index;

	private Vector4f ambient;
	private Vector4f diffuse;
	private Vector4f specular;

	private boolean dropShadow = false;
	
	@XmlTransient
	private final FloatBuffer ambientBuffer;
	@XmlTransient
	private final FloatBuffer noAmbientBuffer;
	@XmlTransient
	private final FloatBuffer diffuseBuffer;
	@XmlTransient
	private final FloatBuffer specularBuffer;
	@XmlTransient
	private final FloatBuffer locationBuffer;
	@XmlTransient
	private final FloatBuffer neg = BufferUtils.createFloatBuffer(16);	
	@XmlTransient
	private final Matrix4f modelView = new Matrix4f();

	public Light() {
		ambientBuffer = BufferUtils.createFloatBuffer(4);
		noAmbientBuffer = BufferUtils.createFloatBuffer(4);
		diffuseBuffer = BufferUtils.createFloatBuffer(4);
		specularBuffer = BufferUtils.createFloatBuffer(4);
		locationBuffer = BufferUtils.createFloatBuffer(4);
	}

	public Light(int index) {
		this();
		this.index = index;
		glEnable(index);
		noAmbientBuffer.put(0.0f);
		noAmbientBuffer.put(0.0f);
		noAmbientBuffer.put(0.0f);
		noAmbientBuffer.put(1.0f);
		noAmbientBuffer.rewind();
	}

	@Override
	public void draw() {
		glTranslatef(location.x, location.y, location.z);
		glLight(index, GL_AMBIENT, ambientBuffer);
		glLight(index, GL_DIFFUSE, diffuseBuffer);
		glLight(index, GL_SPECULAR, specularBuffer);
		glLight(index, GL_POSITION, locationBuffer);
	}

	public void drawNoAmbient() {
		glLight(index, GL_AMBIENT, noAmbientBuffer);
		glLight(index, GL_DIFFUSE, diffuseBuffer);
		glLight(index, GL_SPECULAR, specularBuffer);
		glLight(index, GL_POSITION, locationBuffer);
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the ambient
	 */
	public Vector4f getAmbient() {
		return ambient;
	}

	public void postInit() {
		ambientBuffer.clear();
		ambientBuffer.put(ambient.x);
		ambientBuffer.put(ambient.y);
		ambientBuffer.put(ambient.z);
		ambientBuffer.put(ambient.w);
		ambientBuffer.rewind();
		diffuseBuffer.clear();
		diffuseBuffer.put(diffuse.x);
		diffuseBuffer.put(diffuse.y);
		diffuseBuffer.put(diffuse.z);
		diffuseBuffer.put(diffuse.w);
		diffuseBuffer.rewind();
		specularBuffer.clear();
		specularBuffer.put(specular.x);
		specularBuffer.put(specular.y);
		specularBuffer.put(specular.z);
		specularBuffer.put(specular.w);
		specularBuffer.rewind();
		locationBuffer.clear();
		locationBuffer.put(location.x);
		locationBuffer.put(location.y);
		locationBuffer.put(location.z);
		locationBuffer.put(1.0f);
		locationBuffer.rewind();
	}

	/**
	 * @param ambient
	 *            the ambient to set
	 */
	public void setAmbient(Vector4f ambient) {
		this.ambient = ambient;
		ambientBuffer.clear();
		ambientBuffer.put(ambient.x);
		ambientBuffer.put(ambient.y);
		ambientBuffer.put(ambient.z);
		ambientBuffer.put(ambient.w);
		ambientBuffer.rewind();

	}

	/**
	 * @return the diffuse
	 */
	public Vector4f getDiffuse() {
		return diffuse;
	}

	/**
	 * @param diffuse
	 *            the diffuse to set
	 */
	public void setDiffuse(Vector4f diffuse) {
		this.diffuse = diffuse;
		diffuseBuffer.clear();
		diffuseBuffer.put(diffuse.x);
		diffuseBuffer.put(diffuse.y);
		diffuseBuffer.put(diffuse.z);
		diffuseBuffer.put(diffuse.w);
		diffuseBuffer.rewind();

	}

	/**
	 * @return the specular
	 */
	public Vector4f getSpecular() {
		return specular;
	}

	/**
	 * @param specular
	 *            the specular to set
	 */
	public void setSpecular(Vector4f specular) {
		this.specular = specular;
		specularBuffer.clear();
		specularBuffer.put(specular.x);
		specularBuffer.put(specular.y);
		specularBuffer.put(specular.z);
		specularBuffer.put(specular.w);
		specularBuffer.rewind();
	}

	@Override
	public void setLocation(Vector3f location) {
		super.setLocation(location);
		locationBuffer.clear();
		locationBuffer.put(location.x);
		locationBuffer.put(location.y);
		locationBuffer.put(location.z);
		locationBuffer.put(1.0f);
		locationBuffer.rewind();
	}
	
	/**
	 * <p>
	 * This function calculates the light position for a translated {@link Mesh}
	 * as if the mesh had been moved.
	 * </p> 
	 * <p>
	 * The calculations of the shadow silhouette as well the calculations of the
	 * shadow volume and the visibility of each triangle of some mesh is based
	 * on the current location and rotation of the mesh. Thus the shadow is only
	 * right, if the mesh is not being rotated by glRotate and translated by
	 * glTranslate.
	 * </p>
	 * <p>
	 * Because of that we have to fake the light position and translate the according light
	 * source instead.
	 * </p>
	 * 
	 * @param mesh The mesh to base the light calculation on
	 * @return the faked light position
	 */
	
	public Vector4f getFakeTransform(Mesh mesh) {
		
		Vector4f lightPos = null;
		Vector4f fakeLightPos = null;
		
		glPushMatrix(); {
			
			glLoadIdentity();		
			
			AxisAngleRotation aar = mesh.getAxisAngleRotation();
			
			glRotatef(-aar.angle, aar.x, aar.y, aar.z);
			
			glRotatef(-mesh.getXrot(), 1.0f, 0.0f, 0.0f);
			glRotatef(-mesh.getYrot(), 0.0f, 1.0f, 0.0f);
			glRotatef(-mesh.getZrot(), 0.0f, 0.0f, 1.0f);

			glTranslatef(-mesh.getLocation().getX(), -mesh.getLocation().getY(), -mesh.getLocation().getZ());
			
			neg.clear();		
			glGetFloat(GL_MODELVIEW_MATRIX,neg);
			
			lightPos = new Vector4f(location.x,location.y,location.z,1.0f);
			
			modelView.load(neg);
			
			fakeLightPos = Matrix4f.transform(modelView,lightPos,null);

		}
		

		glPopMatrix();
		
		return fakeLightPos;
	}

	/**
	 * @return the dropShadow
	 */
	public boolean isDropShadow() {
		return dropShadow;
	}

	/**
	 * @param dropShadow the dropShadow to set
	 */
	public void setDropShadow(boolean dropShadow) {
		this.dropShadow = dropShadow;
	}

}
