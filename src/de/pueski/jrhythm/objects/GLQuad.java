package de.pueski.jrhythm.objects;

import java.util.ArrayList;

import de.pueski.jrhythm.math.Vector2f;
import de.pueski.jrhythm.math.Vector3f;


public class GLQuad {

	private Vector3f normal;
	private ArrayList<Vector2f> texCoords;
	private ArrayList<Vector3f> vertices;
	
	public GLQuad() {
		texCoords = new ArrayList<Vector2f>();
		vertices = new ArrayList<Vector3f>();
	}
	
	public Vector3f getNormal() {
		return normal;
	}
	public void setNormal(Vector3f normal) {
		this.normal = normal;
	}
	public ArrayList<Vector2f> getTexCoords() {
		return texCoords;
	}
	public void setTexCoords(ArrayList<Vector2f> texCoords) {
		this.texCoords = texCoords;
	}
	public ArrayList<Vector3f> getVertices() {
		return vertices;
	}
	public void setVertices(ArrayList<Vector3f> vertices) {
		this.vertices = vertices;
	}
	
	public void translate(Vector3f location) {
		for (Vector3f vertex : vertices) {
			vertex.x += location.x;
			vertex.y += location.y;
			vertex.z += location.z;
		}
	}
	
	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}
		
		if (obj instanceof GLQuad) {
			GLQuad quad = (GLQuad)obj;
			
			for (Vector3f v : quad.vertices) {
				if (!vertices.contains(v)) {
					return false;
				}
			}
			return true;
			
		}
		
		return false;
	}
}
