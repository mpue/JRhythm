package de.pueski.jrhythm.objects;

public class Edge {

	public final int[] vertexIndex   = new int[2];
	final int[] faceIndex     = new int[2];
	final int[] triangleIndex = new int[2];
	public boolean clockwise = false;
	
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("vi[0] : "+vertexIndex[0]+" vi[1] : "+vertexIndex[1]+" fi[0] : " + faceIndex[0]+ " fi[1] : "+faceIndex[1]);
		return s.toString();
	}
	
}
