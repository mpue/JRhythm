package de.pueski.jrhythm.core;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.Drawable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.SharedDrawable;
import org.lwjgl.util.glu.GLU;

import de.pueski.jrhythm.objects.Camera;
import de.pueski.jrhythm.objects.Console;
import de.pueski.jrhythm.objects.HUD;
import de.pueski.jrhythm.objects.SceneNode;
import de.pueski.jrhythm.objects.Sector;
import de.pueski.jrhythm.scene.AbstractScene;
import de.pueski.jrhythm.scene.AnotherScene;
import de.pueski.jrhythm.scene.PhysicsScene;
import de.pueski.jrhythm.scene.RenderDepthBufferScene;
import de.pueski.jrhythm.scene.RenderToTextureScene;
import de.pueski.jrhythm.scene.SceneManager;
import de.pueski.jrhythm.scene.VehicleScene;
import de.pueski.jrhythm.sound.MusicPlayer;
import de.pueski.jrhythm.util.UIUtils;

public class Game {

	private static final Log log = LogFactory.getLog(Game.class);

	protected static final Game instance = new Game();

	private long lastTime;

	private ArrayList<Sector> sectors;
	private Sector currentSector;

	private HUD hud;
	private int fps;

	private final StringBuffer message = new StringBuffer();

	private final SceneManager sceneManager = SceneManager.getInstance();

	private float moveVelocity = 0.0f;
	private float strafeVelocity = 0.0f;
	private float raiseVelocity = 0.0f;

	float acceleration = 4.0f;
	float maxVelocity = 240.0f;

	int index = 0; // for screenshots

	boolean mouseGrabbed = true;

	boolean consoleInput = false;

	private Drawable sharedDrawable;

	boolean mKeyPressed = false;
	boolean enterKeyPressed = false;
	boolean spaceKeyPressed = false;
	boolean keyPressed = false;
	boolean specialKeyPressed = false;
	boolean consoleKeyPressed = false;

	private boolean fog = false;
	private boolean showHud = true;

	private boolean fullscreen = true;

	protected final MusicPlayer musicPlayer = new MusicPlayer();

	private DisplayMode displayMode;

	public static Game getInstance() {
		return instance;
	}

	final Random random = new Random();

	long lastTimeKeyPressed = 0;

	private static final HashMap<Character, Boolean> keyMap = new HashMap<Character, Boolean>();
	private static final KeyboardMap KEYBOARD_MAP = new KeyboardMap();

	protected Game() {
	}

	private void setupFog() {
		GL11.glClearColor(0.5f, 0.5f, 0.5f, 1.0f); // We'll Clear To The Color
													// Of The Fog ( Modified )
		FloatBuffer fogColor = BufferUtils.createFloatBuffer(4).put(new float[] { .8f, .8f, .8f, 1.0f });
		fogColor.rewind();
		// Fog Mode
		GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
		// Set Fog Color
		GL11.glFog(GL11.GL_FOG_COLOR, fogColor);
		// How Dense Will The Fog Be
		GL11.glFogf(GL11.GL_FOG_DENSITY, 0.0005f);
		// Fog Hint Value
		GL11.glHint(GL11.GL_FOG_HINT, GL11.GL_DONT_CARE);
		// Fog Start Depth
		GL11.glFogf(GL11.GL_FOG_START, 1000.0f);
		// Fog End Depth
		GL11.glFogf(GL11.GL_FOG_END, 1200.0f);
		// Enable fog
		if (fog)
			GL11.glEnable(GL11.GL_FOG);

	}

	void init() {

		sectors = new ArrayList<Sector>();

		// initialize keyboard and mouse events
		try {
			Keyboard.create();
			Mouse.create();
			Mouse.setGrabbed(mouseGrabbed);
		}
		catch (LWJGLException e) {
			log.error("error setting up event hardware");
			System.exit(0);
		}
		lastTime = System.currentTimeMillis();

		try {
			sharedDrawable = getDrawable();
		}
		catch (LWJGLException e) {
			e.printStackTrace();
		}

		setupFog();
		hud = new HUD(displayMode.getWidth(), displayMode.getHeight());
		
		sceneManager.addScene(new VehicleScene("vehicleScene"));
		
		// sceneManager.addScene(new DefaultScene("DefaultScene"));
		
		// sceneManager.addScene(new RenderToTextureScene("RenderToTexture"));
		
//		
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					sharedDrawable.makeCurrent();
				}
				catch (LWJGLException e) {
					e.printStackTrace();
				}

				// sceneManager.addScene(new VehicleScene("VehicleScene"));
				
				sceneManager.addScene(new PhysicsScene());
				sceneManager.addScene(new AnotherScene("AnotherScene"));
				sceneManager.addScene(new RenderDepthBufferScene("DepthBuffer"));
				sceneManager.addScene(new RenderToTextureScene("RenderToTexture"));
				clearMessage();

			}
		}).start();

		// musicPlayer.start();

		//
		// try {
		// sceneManager.getCurrentScene().writeToXml("c:\\tmp\\scene1.xml");
		// }
		// catch (Exception e) {
		// e.printStackTrace();
		// }

		// try {
		// JAXBContext jaxbContext =
		// JAXBContext.newInstance(DefaultScene.class);
		// jaxbContext.generateSchema(new SchemaOutputResolver() {
		//
		// @Override
		// public Result createOutput(String namespaceUri, String
		// suggestedFileName) throws IOException {
		// File file = new File(suggestedFileName);
		// StreamResult result = new StreamResult(file);
		// System.out.println(file.getAbsolutePath());
		// result.setSystemId(file.toURI().toURL().toString());
		// return result;
		//
		// }
		// });
		// }
		// catch (Exception e) {
		// e.printStackTrace();
		// }

		// try {
		// Unmarshaller u =
		// JAXBContext.newInstance(DefaultScene.class).createUnmarshaller();
		// DefaultScene s = (DefaultScene) u.unmarshal(new FileInputStream(new
		// File("C:\\devel\\indigo_workspace\\JRhythm\\resources\\scene1.xml")));
		// postInitLight(s.getRootNode());
		// sceneManager.addScene(s);
		// }
		// catch (Exception e) {
		// e.printStackTrace();
		// }

	}

	protected void postInitLight(SceneNode node) {
		if (node.getChildren() != null && node.getChildren().size() > 0) {
			for (SceneNode child : node.getChildren()) {
				GL11.glPushMatrix();
				if (child instanceof Light) {
					Light l = (Light) child;
					l.postInit();
					GL11.glEnable(l.getIndex());
				}

				child.draw();
				GL11.glPopMatrix();
				postInitLight(child);
			}
		}
	}

	public void tick() {
		GL11.glClearStencil(0); // clear to zero
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GL11.glLoadIdentity();
		pollEvents();
		
		AbstractScene scene = sceneManager.getCurrentScene(); 		
		scene.updateChildren(scene.getRootNode());
		scene.render();

		if (consoleInput)
			UIUtils.drawOrtho(displayMode.getWidth(), displayMode.getHeight(), Console.getInstance());

		if (showHud)
			UIUtils.drawOrtho(displayMode.getWidth(), displayMode.getHeight(), hud);

	}

	private void pollEvents() {
		
		AbstractScene scene = sceneManager.getCurrentScene();
		
		// update timer
		long now = System.currentTimeMillis();
		float period = (now - lastTime) / (float) 1000;
		lastTime = now;
		// get mouse alterations
		float dx = Mouse.getDX();
		float dy = Mouse.getDY();
		// get keyboard events
		Keyboard.next();

		if (Keyboard.isKeyDown(Keyboard.KEY_F12)) {
			if (!mKeyPressed) {
				mouseGrabbed = !mouseGrabbed;
				Mouse.setGrabbed(mouseGrabbed);
				mKeyPressed = true;
			}
		}
		else {
			mKeyPressed = false;
		}

		if (consoleInput) {
			checkKeyboardInput();
			if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {

				if (!enterKeyPressed) {
					String command = Console.getInstance().getBuffer().toString();
					Interpreter.getInstance().handleCommand(command);
					Console.getInstance().clear();
					enterKeyPressed = true;
				}

			}
			else {
				enterKeyPressed = false;
			}
		}

		else {
			if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
				sceneManager.getCurrentScene().setCurrentCameraIndex(0);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
				sceneManager.getCurrentScene().setCurrentCameraIndex(1);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_3)) {
				sceneManager.getCurrentScene().setCurrentCameraIndex(2);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
				if (sceneManager.getCurrentScene().getCurrentCamera().getMode().equals(Camera.Mode.FLY))
					sceneManager.getCurrentScene().getCurrentCamera().setMode(Camera.Mode.FIRST_PERSON);
				else
					sceneManager.getCurrentScene().getCurrentCamera().setMode(Camera.Mode.FLY);

			}
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				strafeVelocity -= acceleration;
			}
			else {
				if (strafeVelocity < 0) {
					strafeVelocity += acceleration / 2;
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				strafeVelocity += acceleration;
			}
			else {
				if (strafeVelocity > 0) {
					strafeVelocity -= acceleration / 2;
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {

				if (moveVelocity < maxVelocity)
					moveVelocity += acceleration;
			}
			else {
				if (moveVelocity > 0) {
					moveVelocity -= acceleration / 2;
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				moveVelocity -= acceleration;
			}
			else {
				if (moveVelocity < 0) {
					moveVelocity += acceleration / 2;
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
				raiseVelocity += acceleration;
			}
			else {
				if (raiseVelocity > 0) {
					raiseVelocity -= acceleration / 2;
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
				raiseVelocity -= acceleration;
			}
			else {
				if (raiseVelocity < 0) {
					raiseVelocity += acceleration / 2;
				}
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_F3)) {
				fullscreen = !fullscreen;
				setDisplayMode(displayMode.getWidth(), displayMode.getHeight(), fullscreen);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
				if (fog) {
					fog = false;
					GL11.glDisable(GL11.GL_FOG);
				}
				else {
					fog = true;
					GL11.glEnable(GL11.GL_FOG);
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_H)) {
				showHud = !showHud;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_N)) {
				getMusicPlayer().nextSong();
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
				getMusicPlayer().previousSong();
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				if (!spaceKeyPressed) {
					sceneManager.selectNext();
					Display.setTitle("JRhythm v0.01 - " + sceneManager.getCurrentScene().getName());
					spaceKeyPressed = true;
				}
			}
			else {
				spaceKeyPressed = false;
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_BACKSLASH)) {
				if (!consoleKeyPressed) {
					consoleInput = !consoleInput;
					consoleKeyPressed = true;
				}
			}
			else {
				consoleKeyPressed = false;
			}

		}
		
		if (scene instanceof VehicleScene) {
			
			VehicleScene vehicleScene = (VehicleScene)scene;
			
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				vehicleScene.accelerate();
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				vehicleScene.brake();
			}
			else {
				vehicleScene.stop();				
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
				vehicleScene.steerLeft();
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
				vehicleScene.steerRight();
			}
			else {
				vehicleScene.resetSteer();
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {

				if (!enterKeyPressed) {
					vehicleScene.reset();
					enterKeyPressed = true;
				}

			}
			else {
				enterKeyPressed = false;
			}
		}

		if (mouseGrabbed) {
			sceneManager.getCurrentScene().getCurrentCamera().update(dx, dy);
		}
		else {
			sceneManager.getCurrentScene().getCurrentCamera().look();
		}

		sceneManager.getCurrentScene().getCurrentCamera().move(moveVelocity / 10 * period);
		sceneManager.getCurrentScene().getCurrentCamera().strafe(strafeVelocity / 10 * period);
		sceneManager.getCurrentScene().getCurrentCamera().raise(-raiseVelocity / 10 * period);

		for (Sector sector : sectors) {

			Camera cam = sceneManager.getCurrentScene().getCurrentCamera();

			if (cam.getPosition().getX() > sector.getxOffset() && cam.getPosition().getX() < sector.getxOffset() + sector.getSize() && cam.getPosition().getY() > sector.getyOffset()
					&& cam.getPosition().getY() < sector.getyOffset() + sector.getSize() && cam.getPosition().getZ() > sector.getzOffset()
					&& cam.getPosition().getZ() < sector.getzOffset() + sector.getSize()) {
				currentSector = sector;
				break;
			}

		}

	}

	private void checkKeyboardInput() {

		for (int keyNum = 0x01; keyNum < 0xDC; keyNum++) {

			Character key = null;
			try {
				key = KEYBOARD_MAP.get(keyNum);
			}
			catch (Exception e) {
				if (Keyboard.isKeyDown(keyNum)) {
					log.error("Not found " + keyNum);
				}
				return;
			}

			if (Keyboard.isKeyDown(keyNum)) {

				if (key != null && !keyMap.containsKey(key)) {

					if (Character.isLetterOrDigit(key) || key == ' ' || key == '.' || key == ',' || key == '+' || key == '-') {
						if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
							Console.getInstance().append(key);
						}
						else {
							Console.getInstance().append(Character.toLowerCase(key));
						}
					}
					else {
						if (key.equals(KEYBOARD_MAP.get(Keyboard.KEY_BACK))) {
							Console.getInstance().deleteLast();
						}
						if (key.equals(KEYBOARD_MAP.get(Keyboard.KEY_BACKSLASH))) {
							consoleInput = !consoleInput;
						}
					}

					keyMap.put(key, true);
				}

			}
			else {
				keyMap.remove(key);
			}

		}

	}

	/**
	 * @return the fps
	 */
	public int getFps() {
		return fps;
	}

	/**
	 * @param fps the fps to set
	 */
	public void setFps(int fps) {
		this.fps = fps;
	}

	/**
	 * @return the fog
	 */
	public boolean isFog() {
		return fog;
	}

	/**
	 * @param fog the fog to set
	 */
	public void setFog(boolean fog) {
		this.fog = fog;
	}

	/**
	 * @return the mouseGrabbed
	 */
	public boolean isMouseGrabbed() {
		return mouseGrabbed;
	}

	/**
	 * @param mouseGrabbed the mouseGrabbed to set
	 */
	public void setMouseGrabbed(boolean mouseGrabbed) {
		this.mouseGrabbed = mouseGrabbed;
	}

	/**
	 * @return the velocity
	 */
	public float getVelocity() {
		return moveVelocity;
	}

	/**
	 * @param velocity the velocity to set
	 */
	public void setVelocity(float velocity) {
		this.moveVelocity = velocity;
	}

	/**
	 * Set the display mode to be used
	 * 
	 * @param width The width of the display required
	 * @param height The height of the display required
	 * @param fullscreen True if we want fullscreen mode
	 */
	public void setDisplayMode(int width, int height, boolean fullscreen) {

		// return if requested DisplayMode is already set
		if ((Display.getDisplayMode().getWidth() == width) && (Display.getDisplayMode().getHeight() == height) && (Display.isFullscreen() == fullscreen)) {
			return;
		}

		try {
			DisplayMode targetDisplayMode = null;

			if (fullscreen) {
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;

				for (int i = 0; i < modes.length; i++) {
					DisplayMode current = modes[i];
					System.out.println(current);

					if ((current.getWidth() == width) && (current.getHeight() == height)) {
						if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
							if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}

						// if we've found a match for bpp and frequence against
						// the
						// original display mode then it's probably best to go
						// for this one
						// since it's most likely compatible with the monitor
						if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) && (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
							targetDisplayMode = current;
							break;
						}
					}
				}
			}
			else {
				targetDisplayMode = new DisplayMode(width, height);
			}

			if (targetDisplayMode == null) {
				log.error("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
				return;
			}

			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);

		}
		catch (LWJGLException e) {
			log.error("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
		}
	}

	public void setMessage(String message) {
		getMessage().delete(0, getMessage().length());
		getMessage().append(message);
	}

	public void clearMessage() {
		getMessage().delete(0, getMessage().length());
	}

	synchronized Drawable getDrawable() throws LWJGLException {
		return new SharedDrawable(Display.getDrawable());
	}

	public Sector getCurrentSector() {
		return currentSector;
	}

	/**
	 * @return the message
	 */
	public StringBuffer getMessage() {
		return message;
	}

	public MusicPlayer getMusicPlayer() {
		return musicPlayer;
	}

	/**
	 * @return the displayMode
	 */
	public DisplayMode getDisplayMode() {
		return displayMode;
	}

	/**
	 * @param displayMode the displayMode to set
	 */
	public void setDisplayMode(DisplayMode mode) {
		this.displayMode = mode;
		if (hud != null) {
			hud.setWidth(mode.getWidth());
			hud.setHeight(mode.getHeight());			
		}
		glViewport(0, 0, mode.getWidth(), mode.getHeight());
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(45.0f, ((float) mode.getWidth() / (float) mode.getHeight()), 0.1f, 10000.0f);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	/**
	 * @return the sharedDrawable
	 */
	public Drawable getSharedDrawable() {
		return sharedDrawable;
	}

}
