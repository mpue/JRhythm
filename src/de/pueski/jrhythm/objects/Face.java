package de.pueski.jrhythm.objects;

import java.util.Vector;

import de.pueski.jrhythm.math.Vector3f;

public class Face {

	private Vector <Integer> vertexIndex;
	private Vector<Integer> uvVertexIndices;
	private Vector3f normal;
	private Vector3f midpoint;
	private boolean visible = true;
	
	public final PlaneEquation plane = new PlaneEquation();
	
	public Face() {
		vertexIndex = new Vector<Integer>();
		this.uvVertexIndices = new Vector<Integer>();
		normal = new Vector3f();
	}
	
	/**
	 * @return the vertices
	 */
	public Vector<Integer> getVertexIndices() {
		return vertexIndex;
	}
	/**
	 * @param vertices the vertices to set
	 */
	public void setVertexIndices(Vector<Integer> vertices) {
		this.vertexIndex = vertices;
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
	 * @return the uvVertexIndices
	 */
	public Vector<Integer> getUvVertexIndices() {
		return uvVertexIndices;
	}

	/**
	 * @param uvVertexIndices the uvVertexIndices to set
	 */
	public void setUvVertexIndices(Vector<Integer> uvVertexIndices) {
		this.uvVertexIndices = uvVertexIndices;
	}

	
	/**
	 * @return the midpoint
	 */
	public Vector3f getMidpoint() {
		return midpoint;
	}

	
	/**
	 * @param midpoint the midpoint to set
	 */
	public void setMidpoint(Vector3f midpoint) {
		this.midpoint = midpoint;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void calculatePlane(Vertex v1,Vertex v2, Vertex v3) {

		plane.a = v1.y*(v2.z-v3.z) + v2.y*(v3.z-v1.z) + v3.y*(v1.z-v2.z);
		plane.b = v1.z*(v2.x-v3.x) + v2.z*(v3.x-v1.x) + v3.z*(v1.x-v2.x);
		plane.c = v1.x*(v2.y-v3.y) + v2.x*(v3.y-v1.y) + v3.x*(v1.y-v2.y);
		plane.d = -( v1.x*( v2.y*v3.z - v3.y*v2.z ) +
					    v2.x*(v3.y*v1.z - v1.y*v3.z) +
					    v3.x*(v1.y*v2.z - v2.y*v1.z) );
	}

}
