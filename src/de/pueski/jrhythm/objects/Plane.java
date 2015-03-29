package de.pueski.jrhythm.objects;

import java.nio.FloatBuffer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import de.pueski.jrhythm.core.GLDrawable;
import de.pueski.jrhythm.math.Vector3f;
import de.pueski.jrhythm.texture.TextureManager;

@SuppressWarnings("serial")
@XmlType(propOrder = { "color","size","textureScale" }, name="Plane")
@XmlAccessorType(XmlAccessType.FIELD)
public class Plane extends SceneNode {

	public Plane() {		
	}
	
	public Plane(Vector3f location, float size) {
		this.location = location;
		this.size = size;
	}

	private Vector3f color;
	private float size = 1.0f;
	private float textureScale = 1.0f;

	@XmlTransient
	private FloatBuffer matSpecular;
	@XmlTransient
	private FloatBuffer matShininess;
	@XmlTransient
	private FloatBuffer matDiffuse;
	@XmlTransient
	private FloatBuffer matAmbient;

	public void draw() {

		// use shader program if available
		if (useShader) {
			enableShader();
		}

		if (location != null)
			GL11.glTranslatef(location.getX(), location.getY(), location.getZ());

		GL11.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(yrot, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(zrot, 0.0f, 0.0f, 1.0f);

		setMaterials();

		if (!isTextured()) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		} else {
			if (textureLocation != null) {
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				TextureManager.getInstance().getTexture(textureLocation).bind();
			}
		}

		GL11.glBegin(GL11.GL_QUADS);

		if (color != null)
			GL11.glColor3f(color.getX(), color.getY(), color.getZ());
		else
			GL11.glColor3f(0.7f, 0.7f, 0.7f);

		// Top Face
		GL11.glNormal3f(0.0f, -1.0f, 0.0f);
		GL11.glTexCoord2f(0.0f, size / textureScale);
		GL11.glVertex3f(-size, 0.0f, -size);
		GL11.glTexCoord2f(0.0f, 0.0f);
		GL11.glVertex3f(-size, 0.0f, size);
		GL11.glTexCoord2f(size / textureScale, 0.0f);
		GL11.glVertex3f(size, 0.0f, size);
		GL11.glTexCoord2f(size / textureScale, size / textureScale);
		GL11.glVertex3f(size, 0.0f, -size);

		GL11.glEnd();

		GL11.glDisable(GL11.GL_TEXTURE_2D);

		// disable shader program if used
		if (useShader) {
			disableShader();
		}

	}

	public void setMaterials() {

		if (matSpecular != null)
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, matSpecular);

		if (matShininess != null)
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SHININESS, matShininess);

		if (matAmbient != null)
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, matAmbient);

		if (matDiffuse != null)
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, matDiffuse);

	}

	/**
	 * @return the color
	 */
	public Vector3f getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(Vector3f color) {
		this.color = color;
	}

	/**
	 * @return the size
	 */
	public float getSize() {
		return size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(float size) {
		this.size = size;
	}

	/**
	 * @return the textureScale
	 */
	public float getTextureScale() {
		return textureScale;
	}

	/**
	 * @param textureScale
	 *            the textureScale to set
	 */
	public void setTextureScale(float textureScale) {
		this.textureScale = textureScale;
	}

	/**
	 * @param matSpecular
	 *            the matSpecular to set
	 */
	public void setMatSpecular(float r, float g, float b, float a) {
		this.matSpecular = BufferUtils.createFloatBuffer(4).put(new float[] { r, g, b, a });
		matSpecular.rewind();
	}

	/**
	 * @param matShininess
	 *            the matShininess to set
	 */
	public void setMatShininess(float shininess) {
		this.matShininess = BufferUtils.createFloatBuffer(4).put(new float[] { shininess });
		matShininess.rewind();
	}

	/**
	 * @param matDiffuse
	 *            the matDiffuse to set
	 */
	public void setMatDiffuse(float r, float g, float b, float a) {
		this.matDiffuse = BufferUtils.createFloatBuffer(4).put(new float[] { r, g, b, a });
		matDiffuse.rewind();
	}

	/**
	 * @param matAmbient
	 *            the matAmbient to set
	 */
	public void setMatAmbient(float r, float g, float b, float a) {
		this.matAmbient = BufferUtils.createFloatBuffer(4).put(new float[] { r, g, b, a });
		matAmbient.rewind();
	}

}
