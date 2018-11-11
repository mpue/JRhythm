package de.pueski.jrhythm.scene;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.vecmath.Quat4f;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.BufferUtils;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.dynamics.vehicle.DefaultVehicleRaycaster;
import com.bulletphysics.dynamics.vehicle.RaycastVehicle;
import com.bulletphysics.dynamics.vehicle.VehicleRaycaster;
import com.bulletphysics.dynamics.vehicle.VehicleTuning;
import com.bulletphysics.dynamics.vehicle.WheelInfo;
import com.bulletphysics.linearmath.Clock;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;

import de.pueski.jrhythm.core.Light;
import de.pueski.jrhythm.math.Matrix4f;
import de.pueski.jrhythm.math.Vector3f;
import de.pueski.jrhythm.math.Vector4f;
import de.pueski.jrhythm.objects.Box;
import de.pueski.jrhythm.objects.Camera;
import de.pueski.jrhythm.objects.Camera.Mode;
import de.pueski.jrhythm.objects.DefaultNode;
import de.pueski.jrhythm.objects.Face;
import de.pueski.jrhythm.objects.Material;
import de.pueski.jrhythm.objects.Mesh;
import de.pueski.jrhythm.objects.Plane;
import de.pueski.jrhythm.objects.Vertex;
import de.pueski.jrhythm.texture.TextureManager;

@XmlRootElement
public class VehicleScene extends AbstractScene {

	private static final Log log = LogFactory.getLog(VehicleScene.class);

	final boolean renderToTexture = true;

	private DefaultNode sceneNode;
	private ArrayList<Light> lights;

	int colorTextureID;
	int framebufferID;
	int depthRenderBufferID;

	private Mesh map;
	
	private static final int rightIndex = 0;
	private static final int upIndex = 1;
	private static final int forwardIndex = 2;
	private static final javax.vecmath.Vector3f wheelDirectionCS0 = new javax.vecmath.Vector3f(0, -1, 0);
	private static final javax.vecmath.Vector3f wheelAxleCS = new javax.vecmath.Vector3f(-1, 0, 0);

	private static float gEngineForce = 0.f;
	private static float gBreakingForce = 0.f;

	private static float maxEngineForce = 2000.f;// this should be
													// engine/velocity dependent
	private static float maxBreakingForce = 100.f;

	private static float gVehicleSteering = 0.f;
	private static float steeringIncrement = 0.02f;
	private static float steeringClamp = 0.4f;
	private static float wheelRadius = 1.0f;
	private static float wheelWidth = 0.4f;
	private static float wheelFriction = 2000;// 1e30f;
	private static float suspensionStiffness = 5.f;
	private static float suspensionDamping = 2.0f;
	private static float suspensionCompression = 4.0f;
	private static float rollInfluence = 0.7f;// 1.0f;

	private static final float suspensionRestLength = 0.9f;

	private static final int CUBE_HALF_EXTENTS = 1;

	private RigidBody carChassis;
	private ObjectArrayList<CollisionShape> collisionShapes;
	private BroadphaseInterface overlappingPairCache;
	private CollisionDispatcher dispatcher;
	private ConstraintSolver constraintSolver;
	private DefaultCollisionConfiguration collisionConfiguration;


	private VehicleTuning tuning;
	private VehicleRaycaster vehicleRayCaster;
	private RaycastVehicle vehicle;

	protected final Clock clock = new Clock();

	protected DynamicsWorld dynamicsWorld = null;

	private static final Vector<Mesh> wheels = new Vector<Mesh>();

	private static final Quat4f rotationQ = new Quat4f();

	private final Transform m = new Transform();

	protected static boolean idle = false;

	public VehicleScene() {
		super();
	}

	public VehicleScene(String name) {
		super(name);
		initPhysics();
	}

	@Override
	public void render() {
	
		super.render();
		
		doPhysics();

		drawChildren(sceneNode);
		drawChildren(rootNode);
		
		for (Light light : lights) {
			calculateChildrenShadowSilhouette(rootNode, light);
			renderShadow(rootNode, light);
		}

	}

	private void doPhysics() {
		float dt = getDeltaTimeMicroseconds() * 0.000002f;

		// step the simulation
		if (dynamicsWorld != null) {
			dynamicsWorld.stepSimulation(dt,2);
		}

		if (wheels != null) {

			for (int i = 0; i < vehicle.getNumWheels(); i++) {
				// synchronize the wheels with the (interpolated) chassis
				// worldtransform
				vehicle.updateWheelTransform(i, true);
				// draw wheels (cylinders)
				Transform trans = vehicle.getWheelInfo(i).worldTransform;
				glPushMatrix();
				trans.getRotation(rotationQ);
				wheels.get(i).setLocation(new Vector3f(trans.origin.x, trans.origin.y, trans.origin.z));
				setRotation(wheels.get(i), rotationQ);
				glPopMatrix();
			}

		}

		{
			int wheelIndex = 2;
			vehicle.applyEngineForce(gEngineForce, wheelIndex);
			vehicle.setBrake(gBreakingForce, wheelIndex);
			wheelIndex = 3;
			vehicle.applyEngineForce(gEngineForce, wheelIndex);
			vehicle.setBrake(gBreakingForce, wheelIndex);

			wheelIndex = 0;
			vehicle.setSteeringValue(gVehicleSteering, wheelIndex);
			wheelIndex = 1;
			vehicle.setSteeringValue(gVehicleSteering, wheelIndex);
		}

		if (dynamicsWorld != null) {
			int numObjects = dynamicsWorld.getNumCollisionObjects();

			for (int i = 0; i < numObjects; i++) {
				CollisionObject colObj = dynamicsWorld.getCollisionObjectArray().getQuick(i);
				RigidBody body = RigidBody.upcast(colObj);

				if (body != null && body.getMotionState() != null) {
					DefaultMotionState myMotionState = (DefaultMotionState) body.getMotionState();
					m.set(myMotionState.graphicsWorldTrans);
				}
				else {
					colObj.getWorldTransform(m);
				}

				if (colObj != null) {

					if (null != colObj.getUserPointer() && colObj.getUserPointer() instanceof Mesh) {

						Mesh mesh = (Mesh) colObj.getUserPointer();
						glPushMatrix();
						m.getRotation(rotationQ);

						if (mesh.getName().equals("chassis")) {							
							mesh.setLocation(new de.pueski.jrhythm.math.Vector3f(m.origin.x, m.origin.y+1, m.origin.z));
							cameras.get(0).setView(new Vector3f(m.origin.x, m.origin.y, m.origin.z));
							cameras.get(0).setPosition(new Vector3f(cameras.get(0).getPosition().x, cameras.get(0).getPosition().y+212, cameras.get(0).getPosition().z));
							
							// cameras.get(0).setPosition(new Vector3f(forwardVector.x, forwardVector.y+4.5f, forwardVector.z-10));
						}
						else {
							mesh.setLocation(new de.pueski.jrhythm.math.Vector3f(m.origin.x, m.origin.y, m.origin.z));
						}
						setRotation(mesh, rotationQ);
						glPopMatrix();

					}

				}

			}
		}
		
		cameras.get(0).move(vehicle.getCurrentSpeedKmHour()/120);
		
		
	}

	public void setRotation(Mesh m, Quat4f q1) {

		float x, y, z;

		if (q1.w > 1)
			q1.normalize(); // if w>1 acos and sqrt will produce errors, this
							// cant happen if quaternion is normalised
		float angle = (float) (2 * Math.acos(q1.w));
		double s = Math.sqrt(1 - q1.w * q1.w); // assuming quaternion normalised
												// then w is less than 1, so
												// term always positive.
		if (s < 0.001) { // test to avoid divide by zero, s is always positive
							// due to sqrt
			// if s close to zero then direction of axis not important
			x = q1.x; // if it is important that axis is normalised then replace
						// with x=1; y=z=0;
			y = q1.y;
			z = q1.z;
		}
		else {
			x = (float) (q1.x / s); // normalise axis
			y = (float) (q1.y / s);
			z = (float) (q1.z / s);
		}

		m.getAxisAngleRotation().angle = (float) Math.toDegrees(angle);

		m.getAxisAngleRotation().x = x;
		m.getAxisAngleRotation().y = y;
		m.getAxisAngleRotation().z = z;

	}

	@Override
	public void init() {

		Camera camera1 = new Camera("cam1");
		camera1.update(0, 0);
		camera1.setMode(Mode.FIRST_PERSON);
		Camera camera2 = new Camera("cam2");
		camera2.update(0, 0);
		Camera camera3 = new Camera("cam3");
		camera3.update(0, 0);
		camera3.setHeight(25);

		getCameras().add(camera1);
		getCameras().add(camera2);
		getCameras().add(camera3);
		setCurrentCameraIndex(0);

		TextureManager tm = TextureManager.getInstance();

		tm.addTexture("textures/parkett.jpg");

		sceneNode = new DefaultNode("scene");

		lights = new ArrayList<Light>();

		Light light1 = new Light(GL_LIGHT1);
		light1.setAmbient(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
		light1.setDiffuse(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
		light1.setSpecular(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
		light1.setLocation(new Vector3f(-100.1f, 200.2f, -100.2f));

		totalObjects++;

		sceneNode.addChild(light1);
		lights.add(light1);
		
		wheels.add(addMesh(rootNode, "objects/tire.obj", new Vector3f(0, 0, 0)));
		wheels.add(addMesh(rootNode, "objects/tire.obj", new Vector3f(0, 0, 0)));
		wheels.add(addMesh(rootNode, "objects/tire.obj", new Vector3f(0, 0, 0)));
		wheels.add(addMesh(rootNode, "objects/tire.obj", new Vector3f(0, 0, 0)));

		for (Mesh wheel : wheels) {
			wheel.setShadow(true);
			//wheel.attachShader("shaders/shadowMap");
		}
		
		map = addMesh(rootNode, "objects/testmap.obj", new Vector3f(0,-4.5f,0));
		// map.attachShader("shaders/cloud");
		
		setBackgroundColor(new Vector4f(0.8f,0.8f,1.0f,1.0f));
		showGrid = false;
	}

	public void initPhysics() {

		collisionShapes = new ObjectArrayList<CollisionShape>();

		collisionConfiguration = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher(collisionConfiguration);
		Vector3f worldMin = new Vector3f(-1000, -1000, -1000);
		Vector3f worldMax = new Vector3f(1000, 1000, 1000);
		// overlappingPairCache = new AxisSweep3(worldMin, worldMax);
		// overlappingPairCache = new SimpleBroadphase();
		overlappingPairCache = new DbvtBroadphase();
		constraintSolver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, constraintSolver, collisionConfiguration);

		// m_dynamicsWorld->setGravity(btVector3(0,0,0));
		Transform tr = new Transform();
		tr.setIdentity();

		CollisionShape groundShape = map.createBvhTriangleMeshShape();
		
		tr.origin.set(0, -4.5f, 0);

		collisionShapes.add(groundShape);

		// create ground object
		localCreateRigidBody(0, tr, groundShape, null);
		
		
		CollisionShape chassisShape = new BoxShape(new javax.vecmath.Vector3f(2.0f, 1f, 2.5f));
		collisionShapes.add(chassisShape);

		CompoundShape compound = new CompoundShape();
		collisionShapes.add(compound);
		Transform localTrans = new Transform();
		localTrans.setIdentity();
		// localTrans effectively shifts the center of mass with respect to the
		// chassis
		localTrans.origin.set(0, 1, 0);
		// #endif

		compound.addChildShape(localTrans, chassisShape);

		tr.origin.set(0, 0, 0);

		Mesh box = addMesh(rootNode, "objects/testcube.obj", new de.pueski.jrhythm.math.Vector3f(0, 0, 0));
		box.setShadow(true);
		box.setName("chassis");
		carChassis = localCreateRigidBody(800, tr, compound, box); // chassisShape);
		
//		addCollisionMesh(10,0,20,0,"objects/testshape.obj");
//		addCollisionMesh(100, 10, 0, 3, "objects/simplechair.obj");
//		
//		addCollisionMesh(10,0,0,10,"objects/wippe.obj");
//		addCollisionMesh(1000, 0, 50, 13, "objects/cube.obj");
		
		for (int i=0; i < 20;i++) {
			addCollisionMesh(10, 0, 0, 10+4*i, "objects/domino.obj");	
		}
		
		
		
		clientResetScene();

		// create vehicle
		{
			tuning = new VehicleTuning();
			vehicleRayCaster = new DefaultVehicleRaycaster(dynamicsWorld);
			vehicle = new RaycastVehicle(tuning, carChassis, vehicleRayCaster);

			// never deactivate the vehicle
			carChassis.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

			dynamicsWorld.addVehicle(vehicle);

			float connectionHeight = 1.2f;

			boolean isFrontWheel = true;

			// choose coordinate system
			vehicle.setCoordinateSystem(rightIndex, upIndex, forwardIndex);

			float wheelXDistance = -4.0f;
			javax.vecmath.Vector3f connectionPointCS0 = new javax.vecmath.Vector3f(CUBE_HALF_EXTENTS - (wheelXDistance * wheelWidth), connectionHeight, 4f * CUBE_HALF_EXTENTS - wheelRadius);
			vehicle.addWheel(connectionPointCS0, wheelDirectionCS0, wheelAxleCS, suspensionRestLength, wheelRadius, tuning, isFrontWheel);

			connectionPointCS0.set(-CUBE_HALF_EXTENTS + (wheelXDistance * wheelWidth), connectionHeight, 4f * CUBE_HALF_EXTENTS - wheelRadius);
			vehicle.addWheel(connectionPointCS0, wheelDirectionCS0, wheelAxleCS, suspensionRestLength, wheelRadius, tuning, isFrontWheel);

			connectionPointCS0.set(-CUBE_HALF_EXTENTS + (wheelXDistance * wheelWidth), connectionHeight, -4f * CUBE_HALF_EXTENTS + wheelRadius);
			isFrontWheel = false;
			vehicle.addWheel(connectionPointCS0, wheelDirectionCS0, wheelAxleCS, suspensionRestLength, wheelRadius, tuning, isFrontWheel);

			connectionPointCS0.set(CUBE_HALF_EXTENTS - (wheelXDistance * wheelWidth), connectionHeight, -4f * CUBE_HALF_EXTENTS + wheelRadius);
			vehicle.addWheel(connectionPointCS0, wheelDirectionCS0, wheelAxleCS, suspensionRestLength, wheelRadius, tuning, isFrontWheel);

			for (int i = 0; i < vehicle.getNumWheels(); i++) {
				WheelInfo wheel = vehicle.getWheelInfo(i);
				wheel.suspensionStiffness = suspensionStiffness;
				wheel.wheelsDampingRelaxation = suspensionDamping;
				wheel.wheelsDampingCompression = suspensionCompression;
				wheel.frictionSlip = wheelFriction;
				wheel.rollInfluence = rollInfluence;
			}
		}

	}

	void addCollisionMesh(float mass,float x, float y, float z, String location) {
		Mesh m = addMesh(rootNode, location, new de.pueski.jrhythm.math.Vector3f(0, 0, 0));
		m.setName(location);
		m.setShadow(true);
		CollisionShape shape = m.createConvexHullShape();
		collisionShapes.add(shape);
		Transform localTrans = new Transform();
		localTrans.setIdentity();
		localTrans.origin.set(x, y, z);
		RigidBody body = localCreateRigidBody(mass, localTrans, shape, m);
	}

	

	void addBox(float x, float y, float z) {
		Mesh m = addMesh(rootNode, "objects/cube.obj", new de.pueski.jrhythm.math.Vector3f(0, 0, 0));
		m.setName("box");
		m.setShadow(true);
		CollisionShape boxShape = new BoxShape(new javax.vecmath.Vector3f(1.0f, 1.0f, 1.0f));
		collisionShapes.add(boxShape);
		Transform localTrans = new Transform();
		localTrans.setIdentity();
		localTrans.origin.set(x, y, z);
		localCreateRigidBody(10, localTrans, boxShape, m);
	}

	public void clientResetScene() {
		gVehicleSteering = 0f;
		Transform tr = new Transform();
		tr.setIdentity();
		carChassis.setCenterOfMassTransform(tr);
		carChassis.setLinearVelocity(new javax.vecmath.Vector3f(0, 0, 0));
		carChassis.setAngularVelocity(new javax.vecmath.Vector3f(0, 0, 0));
		dynamicsWorld.getBroadphase().getOverlappingPairCache().cleanProxyFromPairs(carChassis.getBroadphaseHandle(), dynamicsWorld.getDispatcher());
		if (vehicle != null) {
			vehicle.resetSuspension();
			for (int i = 0; i < vehicle.getNumWheels(); i++) {
				// synchronize the wheels with the (interpolated) chassis
				// worldtransform
				vehicle.updateWheelTransform(i, true);
			}
		}

		int numObjects = 0;
		if (dynamicsWorld != null) {
			dynamicsWorld.stepSimulation(1f / 60f, 0);
			numObjects = dynamicsWorld.getNumCollisionObjects();
		}

		for (int i = 0; i < numObjects; i++) {
			CollisionObject colObj = dynamicsWorld.getCollisionObjectArray().getQuick(i);
			RigidBody body = RigidBody.upcast(colObj);
			if (body != null) {
				if (body.getMotionState() != null) {
					DefaultMotionState myMotionState = (DefaultMotionState) body.getMotionState();
					myMotionState.graphicsWorldTrans.set(myMotionState.startWorldTrans);
					colObj.setWorldTransform(myMotionState.graphicsWorldTrans);
					colObj.setInterpolationWorldTransform(myMotionState.startWorldTrans);
					colObj.activate();
				}
				// removed cached contact points
				dynamicsWorld.getBroadphase().getOverlappingPairCache().cleanProxyFromPairs(colObj.getBroadphaseHandle(), dynamicsWorld.getDispatcher());

				body = RigidBody.upcast(colObj);
				if (body != null && !body.isStaticObject()) {
					RigidBody.upcast(colObj).setLinearVelocity(new javax.vecmath.Vector3f(0f, 0f, 0f));
					RigidBody.upcast(colObj).setAngularVelocity(new javax.vecmath.Vector3f(0f, 0f, 0f));
				}
			}

		}
	}

	public RigidBody localCreateRigidBody(float mass, Transform startTransform, CollisionShape shape, Mesh object) {
		// rigidbody is dynamic if and only if mass is non zero, otherwise
		// static
		boolean isDynamic = (mass != 0f);

		javax.vecmath.Vector3f localInertia = new javax.vecmath.Vector3f(0f, 0f, 0f);
		if (isDynamic) {
			shape.calculateLocalInertia(mass, localInertia);
		}

		// using motionstate is recommended, it provides interpolation
		// capabilities, and only synchronizes 'active' objects
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);

		RigidBodyConstructionInfo cInfo = new RigidBodyConstructionInfo(mass, myMotionState, shape, localInertia);

		RigidBody body = new RigidBody(cInfo);
		body.setUserPointer(object);
		dynamicsWorld.addRigidBody(body);

		return body;
	}

	public float getDeltaTimeMicroseconds() {
		float dt = clock.getTimeMicroseconds();
		clock.reset();
		return dt;
	}

	public void genLists() {
		rootListId = glGenLists(1);
		glNewList(rootListId, GL_COMPILE);
		drawChildren(rootNode);
		glEndList();
	}

	public void writeToXml(String location) throws Exception {
		Marshaller m = JAXBContext.newInstance(VehicleScene.class, Box.class, Plane.class, Light.class).createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(this, new FileOutputStream(new File(location)));
	}

	public void steerLeft() {
		gVehicleSteering += steeringIncrement / 2;
		if (gVehicleSteering > steeringClamp) {
			gVehicleSteering = steeringClamp;
		}
	}

	public void steerRight() {
		gVehicleSteering -= steeringIncrement / 2;
		if (gVehicleSteering < -steeringClamp) {
			gVehicleSteering = -steeringClamp;
		}
	}

	public void accelerate() {
		
		gEngineForce = maxEngineForce;
		gBreakingForce = 0.f;
	}

	public void resetSteer() {
		
		if (gVehicleSteering > 0) {
			gVehicleSteering -= steeringIncrement/2;
		}
		else if (gVehicleSteering < 0) {
			gVehicleSteering += steeringIncrement/2;
		}
		
		if (Math.abs(gVehicleSteering) < steeringIncrement/2)
			gVehicleSteering = 0;
	}
	
	public void brake() {
		gEngineForce = -maxEngineForce;
	}

	public void stop() {
		gEngineForce = 0;
		gBreakingForce = maxBreakingForce / 10;
	}
	
	public void reset() {
		clientResetScene();
	}
}
