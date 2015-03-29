package de.pueski.jrhythm.objects;


import org.lwjgl.util.Color;

import de.pueski.jrhythm.math.Vector3f;

public class Vertex extends Vector3f {
	
	private Vector3f normal;
	private Color  color;
	
	public Vertex() {
		
	}
	
	public Vertex(float x,float y,float z) {
		this.x = x;
		this.y = y;
		this.z = z;

		color = new Color(255,255,255,255);
		normal = new Vector3f();
		
		this.normal.setX(0.0f);
		this.normal.setY(0.0f);
		this.normal.setZ(0.0f);
		
	}
	
	public Vertex(Vertex v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;

		color = new Color();

		this.color = v.getColor();
		
		normal = new Vector3f();
		
		this.normal.setX(v.getNormal().getX());
		this.normal.setY(v.getNormal().getY());
		this.normal.setZ(v.getNormal().getZ());
		
	}
	

	/**
	 * @return the normal
	 */
	public Vector3f getNormal() {
		return normal;
	}

	/**
	 * @param normal the normal to set
	 */
	public void setNormal(Vector3f normal) {
		this.normal = normal;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

}
