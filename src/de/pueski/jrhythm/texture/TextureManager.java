package de.pueski.jrhythm.texture;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Matthias Pueski (15.05.2011)
 *
 */
public class TextureManager {
	
	private static final Log log = LogFactory.getLog(TextureManager.class);

	private static final HashMap<String, Texture> textures = new HashMap<String, Texture>();
	private static final TextureLoader textureLoader = new TextureLoader();
	
	private static TextureManager instance = null;
	
	protected TextureManager() {
		log.info("initializing.");
		addTexture("textures/notexture.png");
	}
		
	public static TextureManager getInstance() {
		if (instance == null)
			instance = new TextureManager();
		return instance;
	}
	
	public void addTexture(String resourceName) {
		
		Texture tex = null;
		try {
			tex = textureLoader.getTexture(resourceName);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		if (tex != null) {
			if (!textures.containsKey(resourceName))
				textures.put(resourceName, tex);
		}
	}
	
	public Texture getTexture(String texName) {
		return textures.get(texName);
	}
	
}
