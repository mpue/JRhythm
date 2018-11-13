package de.pueski.jrhythm.core;

import java.awt.EventQueue;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.pueski.jrhythm.math.Vector3f;
import de.pueski.jrhythm.math.Vector4f;
import de.pueski.jrhythm.scene.AbstractScene;
import de.pueski.jrhythm.scene.SceneManager;

public class Interpreter {

	private static final Log log = LogFactory.getLog(Interpreter.class);
	
	private static final Interpreter instance = new Interpreter();

	/**
	 * @return the instance
	 */
	public static Interpreter getInstance() {
		return instance;
	}
	
	protected Interpreter() {
		
	}
	
	public void handleCommand(String command) {
		
		try {
			String[] tokens = command.split(" ");
			
			if (tokens.length == 1) {			
				if (tokens[0].startsWith("render")) {
					try {
						Class<?> cls = Class.forName("SunflowGUI");
						Method meth = cls.getMethod("main", String[].class);
						String[] params = new String[0];
						meth.invoke(null, (Object) params);
					}
					catch (Exception e) {
						log.error(e.getMessage());
					} 							
				}
			}			
			else if (tokens.length == 2) {
				
				if (tokens[0].startsWith("add")) {					
					String name = tokens[1];				
					AbstractScene scene = SceneManager.getInstance().getCurrentScene();				
					SceneManager.getInstance().getCurrentScene().addMesh(scene.getRootNode(), "objects/"+name+".obj", new Vector3f(0,0,0));					
				}
				else if (tokens[0].startsWith("delete")) {
					String name = tokens[1];				
					AbstractScene scene = SceneManager.getInstance().getCurrentScene();				
					scene.removeMesh(scene.getRootNode(), name);
				}		
				else if (tokens[0].startsWith("load")) {
					String name = tokens[1];				
							
					URL url = Thread.currentThread().getContextClassLoader().getResource("scenes/"+name+".sce");
					
					if (url == null)
						throw new IllegalArgumentException("Cannot find resource "+name);
					
					File file = new File(url.getFile());
					
					@SuppressWarnings("unchecked")
					List<String> lines = FileUtils.readLines(file);
					
					for(String line : lines) {
						handleCommand(line);
					}
					
				}		
				
			}
			else if (tokens.length == 4) {
				if (tokens[0].startsWith("set")) {
					String name   = tokens[1];
					String method = tokens[2];
					String value  = tokens[3]; 
					
					// now try to determine which type the argument is
					
					Object arg = null;
					
					if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
						arg = Boolean.valueOf(value);
					}
					else {
						try {
							arg = Integer.parseInt(value);
						}
						catch (NumberFormatException nfe) {
							log.info("No integer.");
						}
						// could not parse int, try float
						if (arg == null) {
							try {
								arg = Float.parseFloat(value);
							}
							catch (NumberFormatException nfe) {
								log.info("No integer.");
							}								
						}
						// still no result, take as string
						if (arg == null) {
							arg = value;
						}
					}
					
					AbstractScene scene = SceneManager.getInstance().getCurrentScene();				
					scene.setMeshProperty(scene.getRootNode(), name, method, arg);
					
				}
				
			}			
			else if (tokens.length == 5) {
				if (tokens[0].startsWith("add")) {
					String name = tokens[1];
					
					float x = Float.valueOf(tokens[2]);
					float y = Float.valueOf(tokens[3]);
					float z = Float.valueOf(tokens[4]);
					
					AbstractScene scene = SceneManager.getInstance().getCurrentScene();				
					SceneManager.getInstance().getCurrentScene().addMesh(scene.getRootNode(), "objects/"+name+".obj", new Vector3f(x,y,z));
					
				}
				
				else if (tokens[0].startsWith("set")) {
					
					if (tokens[1].startsWith("bgColor")) {
						
						AbstractScene scene = SceneManager.getInstance().getCurrentScene();
						
						float x = Float.valueOf(tokens[2]);
						float y = Float.valueOf(tokens[3]);
						float z = Float.valueOf(tokens[4]);
						
						scene.setBackgroundColor(new Vector4f(x,y,z,1));
						
					}
					
				}
			}
			else if (tokens.length == 3) {
				if (tokens[0].startsWith("shader")) {
					String name = tokens[1];
					String shaderName = tokens[2];
					AbstractScene scene = SceneManager.getInstance().getCurrentScene();				
					scene.attachShader(scene.getRootNode(), name, shaderName);					
				}
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Syntax error.");
		}
		
		
		
	}
	
}
