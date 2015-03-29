package de.pueski.jrhythm.math;

import javax.vecmath.Quat4f;

import de.pueski.jrhythm.objects.Mesh;

/**
 * This is a helper object to hold rotational data obtained
 * from a {@link Quat4f} object. We need this to transform 
 * the rotational data of a {@link Mesh} object, if its
 * managed by the bullet physics engine.
 * 
 * @author Matthias Pueski
 *
 */
public class AxisAngleRotation {
	public float angle;
	public float x;
	public float y;
	public float z;
}
