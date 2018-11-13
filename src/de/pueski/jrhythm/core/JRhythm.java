package de.pueski.jrhythm.core;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_MATERIAL;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_MODULATE;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV_MODE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glTexEnvi;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

import de.pueski.jrhythm.objects.MeshFactory;
import de.pueski.jrhythm.objects.SceneNode;
import de.pueski.jrhythm.scene.SceneManager;

public class JRhythm {
	
	private static final boolean SHOW_SWING_UI = false;
	
	private static final int MAX_FRAMERATE = 120;
	
	private static final Log log = LogFactory.getLog(JRhythm.class);

	private Game game;
	private boolean done = false;// game runs until done is set to true

	/** time at last frame */
	long lastFrame;	
	/** frames per second */
	int fps;
	/** last fps time */
	long lastFPS;
	
	private static Preferences prefs;
	
	private static final AtomicReference<Dimension> newCanvasSize = new AtomicReference<Dimension>();
	private static final Canvas canvas = new Canvas();
	private static final JFrame frame = new JFrame("JRhythm");
	
	public JRhythm() {
		prefs = Preferences.userRoot().node(this.getClass().getName());
	}
	
	public void run(DisplayMode mode, boolean fullscreen, boolean vsync) {
		init(mode,fullscreen,vsync);// set up display and openGL context
		game = Game.getInstance();
		game.setDisplayMode(mode);
		game.init();
		
		getDelta(); // call once before loop to initialise lastFrame
		lastFPS = getTime(); // call before loop to initialise fps timer
		
		// game loop		
		while (!done) {

			if (SHOW_SWING_UI) {				
				Dimension newDim = newCanvasSize.getAndSet(null);
				if (newDim != null) {
					updateScreenDimensions(newDim.width, newDim.height);
				}
			}

			if (Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
				done = true;
			game.tick();// called once per game loop
			int delta = getDelta();			
			update(delta);
			Display.update();// do the render
			Display.sync(MAX_FRAMERATE);
		}
		Keyboard.destroy();// cleanup
		Mouse.destroy();
		Display.destroy();	
		Game.getInstance().getMusicPlayer().close();
	}

	private void init(DisplayMode mode, boolean fullscreen, boolean vsync) {
		
		if (SHOW_SWING_UI) {
			initSwingUI();			
		}
		
		log.info("initializing application");

		try {
			Display.setDisplayMode(mode);
			Display.setVSyncEnabled(vsync);
			Display.setFullscreen(fullscreen);
			Display.setTitle("JRhythm v0.01");
			Display.create(new PixelFormat(8, 16, 1, 8));
			if (SHOW_SWING_UI) {
				Display.setParent(canvas);				
			}
		}
		catch (Exception e) {
			log.error("Error setting up display");
			System.exit(0);
		}

		
		glViewport(0, 0, mode.getWidth(), mode.getHeight());
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(45.0f, ((float) mode.getWidth() / (float) mode.getHeight()), 0.1f, 1000.0f);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glShadeModel(GL_SMOOTH);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClearDepth(1.0f);
		glEnable(GL_DEPTH_TEST);		
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glEnable(GL_TEXTURE_2D);
	    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    glEnable( GL_BLEND );
	    glEnable(GL_COLOR_MATERIAL);
	    glEnable(GL_LIGHTING);

		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);		
	}

	private void initSwingUI() {
	
		frame.setSize(new Dimension(1024,768));
		frame.setLocationRelativeTo(null);
		frame.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);		
		frame.setLayout(new BorderLayout());
		
		JMenuBar menuBar = new JMenuBar();		
		JMenu fileMenu = new JMenu("File");
		JMenuItem addMeshItem = new JMenuItem("Add mesh");
		
		addMeshItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				File file;				
				JFileChooser fc = new JFileChooser(new File(System.getProperty("user.home")));
				fc.setDialogTitle("Open mesh");
				int result = fc.showOpenDialog(frame);
				if (result == JFileChooser.APPROVE_OPTION) {
					if (fc.getSelectedFile() == null)
						return;
					file = fc.getSelectedFile();
					
					ArrayList<SceneNode> children = MeshFactory.getInstance().loadFromWavefrontObj(file,true);					
					SceneManager.getInstance().getCurrentScene().getRootNode().addAllChildren(children);
					
				}
				else {
					return;
				}
				
				
				
			}
		});
		
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				done = true;
			}
		});
		
		fileMenu.add(addMeshItem);
		fileMenu.add(exitItem);
		
		menuBar.add(fileMenu);
		
		frame.setJMenuBar(menuBar);
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,new JTextArea(),new JScrollPane(canvas));
		frame.add(sp,BorderLayout.CENTER);

		
		canvas.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				newCanvasSize.set(canvas.getSize());
			}

		});
	}

	public void update(int delta) {		
		updateFPS(); // update FPS Counter
	}
	
	/** 
	 * Calculate how many milliseconds have passed 
	 * since last frame.
	 * 
	 * @return milliseconds passed since last frame 
	 */
	public int getDelta() {
	    long time = getTime();
	    int delta = (int) (time - lastFrame);
	    lastFrame = time;
	 
	    return delta;
	}
	
	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	public long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	/**
	 * Calculate the FPS and set it in the title bar
	 */
	public void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			game.setFps(fps);
			// Display.setTitle("FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
		}
		fps++;		
	}	
	
	public void quit() {
		done = true;
	}

	public static void main(String[] args) {
		
		final JRhythm game = new JRhythm();

		try {			
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e) {
			log.info("failed to set look and feel.");
		}
		
		if (SHOW_SWING_UI) {			
			DisplayMode mode = new DisplayMode(1024, 786);			
			game.run(mode, false, true);	
		}
		else {
			runLauncher(game);			
		}
		
	}

	private static void runLauncher(final JRhythm game) {
		final JFrame frame = new JFrame("JRhythm launcher");
		frame.setSize(new Dimension(370,300));
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setUndecorated(true);
	
		BufferedImage icon;
		try {
			icon = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("logo/rhythm_ico_16.png"));
			frame.setIconImage(icon);
		}
		catch (IOException e1) {
			System.out.println("Could not load icon.");
		}
		
		final ConfigPanel configPanel = new ConfigPanel();
		frame.add(configPanel, BorderLayout.CENTER);
		
		configPanel.getFullscreenCheckBox().setSelected(prefs.getBoolean("fullscreen", false));
		configPanel.getVSyncCheckBox().setSelected(prefs.getBoolean("vsync", false));
		configPanel.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.BLACK), 
				                                 BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		configPanel.getLaunchButton().addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				
				prefs.putBoolean("fullscreen", configPanel.getFullscreenCheckBox().isSelected());
				prefs.putBoolean("vsync", configPanel.getVSyncCheckBox().isSelected());
				prefs.putInt("mode", configPanel.getDisplayModeCombo().getSelectedIndex());
				
				game.run((DisplayMode) configPanel.getDisplayModeCombo().getSelectedItem(),
						               configPanel.getFullscreenCheckBox().isSelected(),
						               configPanel.getVSyncCheckBox().isSelected());				
			}
		});
		
	
		
		configPanel.getExitButton().addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);				
			}			
		});
		
		try {
			DisplayMode[] modes = Display.getAvailableDisplayModes();

			ArrayList<DisplayMode> displayModes = new ArrayList<DisplayMode>();
			
			for (DisplayMode mode : modes) {
				displayModes.add(mode);	
			}
			
			Collections.sort(displayModes,new Comparator<DisplayMode>() {

				@Override
				public int compare(DisplayMode o1, DisplayMode o2) {
					
					if  (o1.getWidth() > o2.getWidth()) {
						return 1;						
					}
					else if (o1.getWidth() < o2.getWidth()) {
						return -1;
					}
					else return 0;
					
				}
			});
			
			for (DisplayMode mode : displayModes) {
				configPanel.getDisplayModeCombo().addItem(mode);	
			}

			
			configPanel.getDisplayModeCombo().setSelectedIndex(prefs.getInt("mode", 0));
		}
		catch (LWJGLException e) {
			throw new RuntimeException(e);
		}
		
		frame.setVisible(true);
		
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				configPanel.getLaunchButton().requestFocus();
				configPanel.getLaunchButton().requestFocusInWindow();				
			}
		});
		
	}
	
	static void updateScreenDimensions(int width,int height) {
		GL11.glViewport(0, 0, width, height);
		try {
			DisplayMode mode = new DisplayMode(width, height);
			Display.setDisplayMode(mode);
			Game.getInstance().setDisplayMode(mode);
			log.info("new display mode :"+mode);
		}
		catch (LWJGLException e) {
			e.printStackTrace();
		}		
	}
}
