package de.pueski.jrhythm.objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.lwjgl.util.glu.GLU;

import de.pueski.jrhythm.math.Vector3f;

/**
 * @author Matthias Pueski (15.05.2011)
 * 
 */
@XmlType(propOrder = { "name","position","view","upVector","strafe","mode" }, name = "Camera")
@XmlAccessorType(XmlAccessType.FIELD)
public class Camera {
		
	private String name;
	private Vector3f position;
	private Vector3f view;
	private Vector3f upVector;
	private Vector3f strafe;
	private Mode mode;
	@XmlTransient
	private float currentRotX = 0.0f;
	@XmlTransient
	private float height = 0.0f;
	
	public static enum Mode {
		FLY,
		FIRST_PERSON
	}

	public Camera() {		
	}

	public Camera(String name) {
		view = new Vector3f(0.0f, 0.0f, 0.0f);
		upVector = new Vector3f(0.0f, 1.0f, 0.0f);
		position = new Vector3f(0.0f, 5.0f, -35.0f);
		strafe = new Vector3f(0.0f, 0.0f, 0.0f);
		mode = Mode.FLY;
		this.name = name;
	}

	void position(float positionX, float positionY, float positionZ, float viewX, float viewY, float viewZ, float upVectorX,
			float upVectorY, float upVectorZ) {
		Vector3f position = new Vector3f(positionX, positionY, positionZ);
		Vector3f view = new Vector3f(viewX, viewY, viewZ);
		Vector3f upVector = new Vector3f(upVectorX, upVectorY, upVectorZ);
		this.position = position;
		this.view = view;
		this.upVector = upVector;
	}

	void setViewByMouse(float dx, float dy) {
		// This is the direction for looking up or down
		float angleY = 0.0f;
		// This will be the value we need to rotate around the Y axis (Left and
		// Right)
		float angleZ = 0.0f;
		currentRotX = 0.0f;
		angleY = (float) ((dx)) / 10.0f;
		angleZ = (float) ((dy)) / 10.0f;
		currentRotX -= angleZ;
		// To find the axis we need to rotate around for up and down
		// movements, we need to get a perpendicular vector from the
		// camera's view vector and up vector. This will be the axis.
		Vector3f vAxis = Vector3f.cross(Vector3f.sub(view, position, null), upVector, null);
		vAxis.normalise();
		// Rotate around our perpendicular axis and along the y-axis
		rotate(angleZ, vAxis.x, vAxis.y, vAxis.z);
		rotate(-angleY, 0, 1, 0);
	}

	void rotate(float angle, float x, float y, float z) {
		Vector3f vNewView = new Vector3f();
		// Get the view vector (The direction we are facing)
		Vector3f vView = Vector3f.sub(view, position, null);
		// Calculate the sine and cosine of the angle once
		float cosTheta = (float) Math.cos(Math.toRadians(angle));
		float sinTheta = (float) Math.sin(Math.toRadians(angle));
		// Find the new x position for the new rotated point
		vNewView.x = (cosTheta + (1 - cosTheta) * x * x) * vView.x;
		vNewView.x += ((1 - cosTheta) * x * y - z * sinTheta) * vView.y;
		vNewView.x += ((1 - cosTheta) * x * z + y * sinTheta) * vView.z;
		// Find the new y position for the new rotated point
		vNewView.y = ((1 - cosTheta) * x * y + z * sinTheta) * vView.x;
		vNewView.y += (cosTheta + (1 - cosTheta) * y * y) * vView.y;
		vNewView.y += ((1 - cosTheta) * y * z - x * sinTheta) * vView.z;		
		// Find the new z position for the new rotated point
		vNewView.z = ((1 - cosTheta) * x * z - y * sinTheta) * vView.x;
		vNewView.z += ((1 - cosTheta) * y * z + x * sinTheta) * vView.y;
		vNewView.z += (cosTheta + (1 - cosTheta) * z * z) * vView.z;
		// Now we just add the newly rotated vector to our position to set
		// our new rotated view of our camera.
		view = Vector3f.sub(position, vNewView, null);
	}

	public void move(float speed) {
		// Get the current view vector (the direction we are looking)		
		Vector3f vView = Vector3f.sub(view, position, null);
		vView.normalise();
		position.x += vView.x * speed;
		if (mode.equals(Mode.FIRST_PERSON)) {
			position.y = height + 4;
		}
		else
			position.y += vView.y * speed;
		position.z += vView.z * speed;
		view.x += vView.x * speed;
		if (mode.equals(Mode.FLY))
			view.y += vView.y * speed;
		view.z += vView.z * speed;
	}

	
	
	public void raise(float speed) {
		Vector3f vView = Vector3f.sub(view, position, null);
		vView.normalise();

		position.y += vView.y * speed;
		view.y     += vView.y * speed;
	}
	
	public void strafe(float speed) {
		// Add the strafe vector to our position
		position.x += strafe.x * speed;
		position.z += strafe.z * speed;
		// Add the strafe vector to our view
		view.x += strafe.x * speed;
		view.z += strafe.z * speed;
	}

	/**
	 * @brief makes the camera look into the current direction
	 */
	public void look() {
		// Give openGL our camera position, then camera view, then camera up
		// vector
		GLU.gluLookAt(position.x, position.y, position.z, 
					  view.x, view.y, view.z, 
					  upVector.x, upVector.y, upVector.z);
	}

	public void update(float dx, float dy) {
		Vector3f vAxis = Vector3f.cross(Vector3f.sub(view, position, null), upVector, null);
		vAxis.normalise();
		strafe = vAxis;
		setViewByMouse(dx, dy);
		look();
	}

	/**
	 * @return the position
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(Vector3f position) {
		this.position = position;
	}

	/**
	 * @return the view
	 */
	public Vector3f getView() {
		return view;
	}

	/**
	 * @param view the view to set
	 */
	public void setView(Vector3f view) {
		this.view = view;
	}

	/**
	 * @return the upVector
	 */
	public Vector3f getUpVector() {
		return upVector;
	}

	/**
	 * @param upVector the upVector to set
	 */
	public void setUpVector(Vector3f upVector) {
		this.upVector = upVector;
	}

	/**
	 * @return the strafe
	 */
	public Vector3f getStrafe() {
		return strafe;
	}

	/**
	 * @param strafe the strafe to set
	 */
	public void setStrafe(Vector3f strafe) {
		this.strafe = strafe;
	}
	
	@Override
	public String toString() {
		return "("+position.getX()+","+position.getY()+","+position.getZ()+")";
	}
	
	/**
	 * @return the height
	 */
	public float getHeight() {
		return height;
	}
	
	/**
	 * @param height the height to set
	 */
	public void setHeight(float height) {
		this.height = height;
	}

	/**
	 * @return the mode
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(Mode mode) {
		this.mode = mode;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
}
