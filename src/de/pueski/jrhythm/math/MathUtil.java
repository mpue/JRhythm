package de.pueski.jrhythm.math;

public class MathUtil {
	
	public static float getDotProduct(Vector3f v1,Vector3f v2) {
		return v1.x*v2.x+v1.y*v2.y+v1.z*v2.z;
	}

	public static Vector3f sub(Vector3f a, Vector3f b) {
		return new Vector3f(a.x-b.x,a.y-b.y,a.z-b.z);		
	}
	
}
