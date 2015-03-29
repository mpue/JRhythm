package de.pueski.jrhythm.objects;

import org.lwjgl.opengl.GL11;

import de.pueski.jrhythm.math.Vector3f;

public class WireBox extends SceneNode {

	private static final long serialVersionUID = -3787881149823359353L;

	public WireBox(Vector3f location, String name, float scale) {
		super(name);
		this.location = location;
		this.scale = scale;
	}

	private Vector3f location;
	private Vector3f color;

	private float xrot = 0.0f;
	private float yrot = 0.0f;
	private float zrot = 0.0f;
	
	private float scale;
	
	public void draw() {
				
		if (location != null)
			GL11.glTranslatef(location.getX(),location.getY(), location.getZ());
		
		GL11.glLineWidth(1.0f);
		
		GL11.glBegin(GL11.GL_LINES);
		
		if (color != null)
			GL11.glColor3f(color.getX(), color.getY(), color.getZ());
		else
			GL11.glColor3f(0.5f,0.5f,0.5f);
		

		GL11.glVertex3f(-scale, -scale, -scale); // left back bottom
		GL11.glVertex3f(-scale,  scale, -scale); // left back top 
		GL11.glVertex3f( scale, -scale, -scale); // right back bottom
		GL11.glVertex3f( scale,  scale, -scale); // right back top 

		GL11.glVertex3f(-scale, -scale,  scale); // left  front bottom
		GL11.glVertex3f(-scale,  scale,  scale); // left  front top 
		GL11.glVertex3f( scale, -scale,  scale); // right front bottom
		GL11.glVertex3f( scale,  scale,  scale); // right front top 

		GL11.glVertex3f(-scale, -scale, -scale); // left back bottom
		GL11.glVertex3f(-scale, -scale,  scale); // left front bottom
		GL11.glVertex3f( scale, -scale, -scale); // right back bottom
		GL11.glVertex3f( scale, -scale,  scale); // right front bottom
		
		GL11.glVertex3f(-scale,  scale, -scale); // left back top
		GL11.glVertex3f(-scale,  scale,  scale); // left front top
		GL11.glVertex3f( scale,  scale, -scale); // right back top
		GL11.glVertex3f( scale,  scale,  scale); // right front top
		
		GL11.glVertex3f(-scale,  scale, -scale); // left back top
		GL11.glVertex3f( scale,  scale, -scale); // right back top
		GL11.glVertex3f(-scale, -scale, -scale); // left back bottom
		GL11.glVertex3f( scale, -scale, -scale); // right back bottom
		
		GL11.glVertex3f(-scale,  scale,  scale); // left  front top
		GL11.glVertex3f( scale,  scale,  scale); // right front top
		GL11.glVertex3f(-scale,  scale, -scale); // left back top
		GL11.glVertex3f( scale,  scale, -scale); // right back top
		
		GL11.glEnd();
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
	 * @return the color
	 */
	public Vector3f getColor() {
		return color;
	}


	/**
	 * @param color the color to set
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


	public void setRotation(float x, float y, float z) {
		xrot = x;
		yrot = y;
		zrot = z;
	}
	
	@Override
	public String toString() {
		return location.toString();
	}
}
