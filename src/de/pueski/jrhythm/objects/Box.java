package de.pueski.jrhythm.objects;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.lwjgl.opengl.GL11;

import de.pueski.jrhythm.math.Vector2f;
import de.pueski.jrhythm.math.Vector3f;
import de.pueski.jrhythm.texture.TextureManager;

@XmlType(propOrder = { "color" }, name = "Box")
@XmlAccessorType(XmlAccessType.FIELD)
public class Box extends SceneNode {

	@XmlTransient
	private static final long serialVersionUID = -3787881149823359353L;

	private Vector3f color;

	@XmlTransient
	private ArrayList<GLQuad> quads;

	public Box() {
		createGeometry();
	}

	public Box(Vector3f location, String name) {
		super(name);
		this.location = location;
		createGeometry();
	}

	private void createGeometry() {

		quads = new ArrayList<GLQuad>();

		GLQuad front = new GLQuad();

		front.setNormal(new Vector3f(0.0f, 0.0f, 1.0f));

		front.getTexCoords().add(new Vector2f(0.0f, 0.0f));
		front.getTexCoords().add(new Vector2f(0.0f, 1.0f));
		front.getTexCoords().add(new Vector2f(1.0f, 1.0f));
		front.getTexCoords().add(new Vector2f(1.0f, 0.0f));
		front.getVertices().add(new Vector3f(-1.0f, 1.0f, 1.0f));
		front.getVertices().add(new Vector3f(1.0f, 1.0f, 1.0f));
		front.getVertices().add(new Vector3f(1.0f, -1.0f, 1.0f));
		front.getVertices().add(new Vector3f(-1.0f, -1.0f, 1.0f));

		GLQuad back = new GLQuad();

		back.setNormal(new Vector3f(0.0f, 0.0f, -1.0f));

		back.getTexCoords().add(new Vector2f(0.0f, 0.0f));
		back.getTexCoords().add(new Vector2f(0.0f, 1.0f));
		back.getTexCoords().add(new Vector2f(1.0f, 1.0f));
		back.getTexCoords().add(new Vector2f(1.0f, 0.0f));
		back.getVertices().add(new Vector3f(1.0f, -1.0f, -1.0f));
		back.getVertices().add(new Vector3f(1.0f, 1.0f, -1.0f));
		back.getVertices().add(new Vector3f(-1.0f, 1.0f, -1.0f));
		back.getVertices().add(new Vector3f(-1.0f, -1.0f, -1.0f));

		GLQuad top = new GLQuad();

		top.setNormal(new Vector3f(0.0f, 1.0f, 0.0f));

		top.getTexCoords().add(new Vector2f(1.0f, 1.0f));
		top.getTexCoords().add(new Vector2f(1.0f, 0.0f));
		top.getTexCoords().add(new Vector2f(0.0f, 0.0f));
		top.getTexCoords().add(new Vector2f(0.0f, 1.0f));
		top.getVertices().add(new Vector3f(1.0f, 1.0f, -1.0f));
		top.getVertices().add(new Vector3f(1.0f, 1.0f, 1.0f));
		top.getVertices().add(new Vector3f(-1.0f, 1.0f, 1.0f));
		top.getVertices().add(new Vector3f(-1.0f, 1.0f, -1.0f));

		GLQuad bottom = new GLQuad();

		bottom.setNormal(new Vector3f(0.0f, -1.0f, 0.0f));

		bottom.getTexCoords().add(new Vector2f(1.0f, 0.0f));
		bottom.getTexCoords().add(new Vector2f(0.0f, 0.0f));
		bottom.getTexCoords().add(new Vector2f(0.0f, 1.0f));
		bottom.getTexCoords().add(new Vector2f(1.0f, 1.0f));
		bottom.getVertices().add(new Vector3f(-1.0f, -1.0f, 1.0f));
		bottom.getVertices().add(new Vector3f(1.0f, -1.0f, 1.0f));
		bottom.getVertices().add(new Vector3f(1.0f, -1.0f, -1.0f));
		bottom.getVertices().add(new Vector3f(-1.0f, -1.0f, -1.0f));

		GLQuad right = new GLQuad();

		right.setNormal(new Vector3f(1.0f, 0.0f, 0.0f));

		right.getTexCoords().add(new Vector2f(0.0f, 0.0f));
		right.getTexCoords().add(new Vector2f(0.0f, 1.0f));
		right.getTexCoords().add(new Vector2f(1.0f, 1.0f));
		right.getTexCoords().add(new Vector2f(1.0f, 0.0f));
		right.getVertices().add(new Vector3f(1.0f, -1.0f, 1.0f));
		right.getVertices().add(new Vector3f(1.0f, 1.0f, 1.0f));
		right.getVertices().add(new Vector3f(1.0f, 1.0f, -1.0f));
		right.getVertices().add(new Vector3f(1.0f, -1.0f, -1.0f));

		GLQuad left = new GLQuad();

		left.setNormal(new Vector3f(-1.0f, 0.0f, 0.0f));

		left.getTexCoords().add(new Vector2f(0.0f, 1.0f));
		left.getTexCoords().add(new Vector2f(1.0f, 1.0f));
		left.getTexCoords().add(new Vector2f(1.0f, 0.0f));
		left.getTexCoords().add(new Vector2f(0.0f, 0.0f));
		left.getVertices().add(new Vector3f(-1.0f, 1.0f, -1.0f));
		left.getVertices().add(new Vector3f(-1.0f, 1.0f, 1.0f));
		left.getVertices().add(new Vector3f(-1.0f, -1.0f, 1.0f));
		left.getVertices().add(new Vector3f(-1.0f, -1.0f, -1.0f));

		quads.add(front);
		quads.add(back);
		quads.add(top);
		quads.add(bottom);
		quads.add(right);
		quads.add(left);

	}

	public void drawShadow() {

		if (location != null)
			GL11.glTranslatef(location.getX(), location.getY(), location.getZ());

		GL11.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(yrot, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(zrot, 0.0f, 0.0f, 1.0f);

		GL11.glBegin(GL11.GL_QUADS);

		for (GLQuad quad : quads) {

			GL11.glNormal3f(quad.getNormal().x, quad.getNormal().y, quad.getNormal().z);

			for (int i = 0; i < quad.getVertices().size(); i++) {
				Vector3f vertex = quad.getVertices().get(i);
				GL11.glVertex3f(vertex.x, vertex.y, vertex.z);
			}

		}

		GL11.glEnd();

	}	
	
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
		
		if (!isTextured()) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
		else {
			if (textureLocation != null) {
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				TextureManager.getInstance().getTexture(textureLocation).bind();
			}
		}

		GL11.glBegin(GL11.GL_QUADS);

		for (GLQuad quad : quads) {

			GL11.glNormal3f(quad.getNormal().x, quad.getNormal().y, quad.getNormal().z);

			for (int i = 0; i < quad.getVertices().size(); i++) {
				if (isTextured()) {
					Vector2f texCoord = quad.getTexCoords().get(i);
					GL11.glTexCoord2f(texCoord.x, texCoord.y);
				}

				if (color != null)
					GL11.glColor3f(color.getX(), color.getY(), color.getZ());
				else
					GL11.glColor3f(0.5f, 0.5f, 0.5f);

				Vector3f vertex = quad.getVertices().get(i);
				GL11.glVertex3f(vertex.x, vertex.y, vertex.z);
			}

		}

		GL11.glEnd();

		GL11.glDisable(GL11.GL_TEXTURE_2D);

		// disable shader program if used
		if (useShader) {
			disableShader();
		}
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

	@Override
	public String toString() {
		return location.toString();
	}

	public ArrayList<GLQuad> getQuads() {
		return quads;
	}
}
