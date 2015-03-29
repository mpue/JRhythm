package de.pueski.jrhythm.objects;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import de.pueski.jrhythm.math.Vector2f;
import de.pueski.jrhythm.math.Vector3f;
import de.pueski.jrhythm.texture.TextureManager;
import de.pueski.jrhythm.util.VBOUtils;

public class VBOBox extends SceneNode {

	private static final long serialVersionUID = -3787881149823359353L;

	private Vector3f location;
	private Vector3f color;

	public VBOBox(Vector3f location, String name) {
		super(name);
		this.location = location;
		createGeometry();
	}
	
	private int vertexBufferID;
	private int textureBufferID;
	private int colourBufferID; 
	private int indexBufferID;
	private int normalBufferID;
	
	private ArrayList<GLQuad> quads;

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

		prepareVBO();
	}

	void prepareVBO() {
		
		vertexBufferID = VBOUtils.createVBOID();
		normalBufferID = VBOUtils.createVBOID();
		textureBufferID = VBOUtils.createVBOID();
		colourBufferID = VBOUtils.createVBOID();
		indexBufferID  = VBOUtils.createVBOID(); 
		
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(24*3);
		
		for (GLQuad quad : quads) {
			for (int i = 0; i < quad.getVertices().size(); i++) {
				Vector3f vertex = quad.getVertices().get(i);
				vertexBuffer.put(vertex.x).put(vertex.y).put(vertex.z);
			}
			
		}
		
		vertexBuffer.flip();
		
		FloatBuffer textureBuffer = BufferUtils.createFloatBuffer(24*2);
		
		for (GLQuad quad : quads) {
			for(int i=0;i < quad.getTexCoords().size();i++) {
				Vector2f coord = quad.getTexCoords().get(i);
				textureBuffer.put(coord.x).put(coord.y);
			}
		}

		textureBuffer.rewind();
		
		FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(24*3);
		
		for (GLQuad quad : quads) {						
			for (int i = 0; i < 4;i++)
				normalBuffer.put(quad.getNormal().x).put(quad.getNormal().y).put(quad.getNormal().z);
		}
		normalBuffer.rewind();
		
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(24*3);
		
		for (int i = 0;i < 24;i++) {
			colorBuffer.put(0.5f).put(0.5f).put(0.5f);
		}
		
		colorBuffer.flip();
		
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(24);
		for (int i = 0;i < 24;i++) {
			indexBuffer.put(i);
		}
		
		indexBuffer.flip();
		
		VBOUtils.bufferData(vertexBufferID, vertexBuffer);
		VBOUtils.bufferData(normalBufferID, normalBuffer);
		VBOUtils.bufferData(textureBufferID, textureBuffer);
		VBOUtils.bufferData(colourBufferID, colorBuffer);
		VBOUtils.bufferElementData(indexBufferID, indexBuffer);
		
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

		VBOUtils.render(GL11.GL_QUADS,vertexBufferID, textureBufferID, normalBufferID, colourBufferID, indexBufferID, 24, 24);
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);	
		
		// disable shader program if used
		if (useShader) {
			disableShader();
		}
	}

	/**
	 * @return the location
	 */
	public Vector3f getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(Vector3f location) {
		this.location = location;
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
	 * @return the xrot
	 */
	public float getXrot() {
		return xrot;
	}

	/**
	 * @param xrot
	 *            the xrot to set
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
	 * @param yrot
	 *            the yrot to set
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
	 * @param zrot
	 *            the zrot to set
	 */
	public void setZrot(float zrot) {
		this.zrot = zrot;
	}

	public void setRotation(float x, float y, float z) {
		xrot = x;
		yrot = y;
		zrot = z;
	}

	@Override
	public String toString() {
		return location.toString();
	}

	public ArrayList<GLQuad> getQuads() {
		return quads;
	}
}
