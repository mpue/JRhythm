package de.pueski.jrhythm.scene;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;
import java.util.Random;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.bulletphysics.BulletStats;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Clock;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;

import de.pueski.jrhythm.objects.Camera;
import de.pueski.jrhythm.objects.Mesh;
import de.pueski.jrhythm.objects.SceneNode;

public class PhysicsScene extends Filter2dScene {

	private ObjectArrayList<CollisionShape> collisionShapes;
	private BroadphaseInterface broadphase;
	private CollisionDispatcher dispatcher;
	private ConstraintSolver solver;
	private DefaultCollisionConfiguration collisionConfiguration;
	private DynamicsWorld dynamicsWorld = null;

	private final Transform m = new Transform();
	private final Transform cam = new Transform();
	private static FloatBuffer floatBuf = BufferUtils.createFloatBuffer(16);

	private CollisionShape colShape;
	
	private static final Quat4f rotationQ = new Quat4f();
	
	protected final Clock clock = new Clock();

	public PhysicsScene() {
		super("Physics");
		initPhysics();
	}
	
	final BoxShape collider = new BoxShape(new Vector3f(1,1,1));

	@Override
	public void init() {
		super.init();
		cameras.get(0).setPosition(new de.pueski.jrhythm.math.Vector3f(0, 2, -20));
	}

	@Override
	public void render() {
		super.render();
		doPhysics();
	}

	private void doPhysics() {
		// simple dynamics world doesn't handle fixed-time-stepping
		float ms = getDeltaTimeMicroseconds();

		// step the simulation
		if (dynamicsWorld != null) {
			dynamicsWorld.stepSimulation(ms / 100000f);
			// optional but useful: debug drawing
			dynamicsWorld.debugDrawWorld();
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
						mesh.setLocation(new de.pueski.jrhythm.math.Vector3f(m.origin.x, m.origin.y, m.origin.z));
						setRotation(mesh,rotationQ);												
						glPopMatrix();
						
					}

				}

			}
		}
	}
	
	public void setRotation(Mesh m,Quat4f q1) {

		float x,y,z;
		
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
		
		m.getAxisAngleRotation().angle = (float)Math.toDegrees(angle); 

		m.getAxisAngleRotation().x = x;
		m.getAxisAngleRotation().y = y;
		m.getAxisAngleRotation().z = z;
		
	}

	public void initPhysics() {

		collisionShapes = new ObjectArrayList<CollisionShape>();

		// collision configuration contains default setup for memory, collision
		// setup
		collisionConfiguration = new DefaultCollisionConfiguration();

		// use the default collision dispatcher. For parallel processing you can
		// use a diffent dispatcher (see Extras/BulletMultiThreaded)
		dispatcher = new CollisionDispatcher(collisionConfiguration);

		broadphase = new DbvtBroadphase();

		// the default constraint solver. For parallel processing you can use a
		// different solver (see Extras/BulletMultiThreaded)
		SequentialImpulseConstraintSolver sol = new SequentialImpulseConstraintSolver();
		solver = sol;

		// TODO: needed for SimpleDynamicsWorld
		// sol.setSolverMode(sol.getSolverMode() &
		// ~SolverMode.SOLVER_CACHE_FRIENDLY.getMask());

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);

		dynamicsWorld.setGravity(new Vector3f(0f, -10f, 0f));

		// create a few basic rigid bodies
		CollisionShape groundShape = new BoxShape(new Vector3f(50f, 1f, 50f));
		// CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0, 1,
		// 0), 50);

		collisionShapes.add(groundShape);

		Transform groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(0, -2, 0);

		// We can also use DemoApplication::localCreateRigidBody, but for
		// clarity it is provided here:
		{
			float mass = 0f;

			// rigidbody is dynamic if and only if mass is non zero, otherwise
			// static
			boolean isDynamic = (mass != 0f);

			Vector3f localInertia = new Vector3f(0, 0, 0);
			if (isDynamic) {
				groundShape.calculateLocalInertia(mass, localInertia);
			}

			// using motionstate is recommended, it provides interpolation
			// capabilities, and only synchronizes 'active' objects
			DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
			RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, groundShape, localInertia);
			RigidBody body = new RigidBody(rbInfo);

			// add the body to the dynamics world
			dynamicsWorld.addRigidBody(body);
		}

		{
			// create a few dynamic rigidbodies
			// Re-using the same collision is better for memory usage and
			// performance

			colShape = new BoxShape(new Vector3f(1, 1, 1));
			collisionShapes.add(colShape);

			// Create Dynamic Objects
			Transform startTransform = new Transform();
			startTransform.setIdentity();

			float mass = 10f;

			// rigidbody is dynamic if and only if mass is non zero, otherwise
			// static
			boolean isDynamic = (mass != 0f);

			Vector3f localInertia = new Vector3f(0, 0, 0);
			if (isDynamic) {
				colShape.calculateLocalInertia(mass, localInertia);
			}

			Random r = new Random();

			for (int x = 0; x < 15; x++) {
				for (int z = 0; z < 15; z++) {

					float x_ = x * (r.nextFloat() + 1);
					float y  = r.nextFloat() * 10 + 10;
					float z_ = z * (r.nextFloat() + 1);
					
					startTransform.origin.set(x_, y, z_);				
					Mesh box = addMesh(rootNode, "objects/cube.obj", new de.pueski.jrhythm.math.Vector3f(0,0,0));
					box.setMatAmbient(0.8f,0.8f,0.8f,1.0f);
					box.setMatDiffuse(0.8f,0.8f,0.8f,1.0f);
					box.setMatSpecular(1.0f,1.0f,1.0f,1.0f);
					box.setMatShininess(10.0f);
					box.attachShader("shaders/simpleTexture");
					addBody(20, colShape,localInertia, startTransform, box);

				}

			}

			addBody(0, collider,localInertia, startTransform, getCameras().get(0));
		}
		
		
		
		clientResetScene();
	}

	public void addBody(float mass, CollisionShape shape,Vector3f localInertia, Transform startTransform, Object object) {
		// using motionstate is recommended, it provides interpolation
		// capabilities, and only synchronizes 'active' objects
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, shape, localInertia);
		RigidBody body = new RigidBody(rbInfo);
		body.setUserPointer(object);
		body.setActivationState(RigidBody.ISLAND_SLEEPING);
		dynamicsWorld.addRigidBody(body);
		body.setActivationState(RigidBody.ISLAND_SLEEPING);
		// rootNode.addChild(object);
		body.activate();
	}

	public void clientResetScene() {
		// #ifdef SHOW_NUM_DEEP_PENETRATIONS
		BulletStats.gNumDeepPenetrationChecks = 0;
		BulletStats.gNumGjkChecks = 0;
		// #endif //SHOW_NUM_DEEP_PENETRATIONS

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
					RigidBody.upcast(colObj).setLinearVelocity(new Vector3f(0f, 0f, 0f));
					RigidBody.upcast(colObj).setAngularVelocity(new Vector3f(0f, 0f, 0f));
				}
			}

			/*
			 * //quickly search some issue at a certain simulation frame,
			 * pressing space to reset int fixed=18; for (int i=0;i<fixed;i++) {
			 * getDynamicsWorld()->stepSimulation(1./60.f,1); }
			 */
		}
	}

	public void glMultMatrix(float[] m) {
		floatBuf.clear();
		floatBuf.put(m).flip();
		GL11.glMultMatrix(floatBuf);
	}

	public float getDeltaTimeMicroseconds() {
		// #ifdef USE_BT_CLOCK
		float dt = clock.getTimeMicroseconds();
		clock.reset();
		return dt;
		// #else
		// return btScalar(16666.);
		// #endif
	}

}
