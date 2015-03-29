package de.pueski.jrhythm.texture;


/**
 * @author Matthias Pueski (15.05.2011)
 *
 */
public class TextureGenerator {
	
	private static TextureGenerator instance = null;
	
	
	protected TextureGenerator() {		
	}
	
	public static TextureGenerator getInstance() {
		
		if (instance == null) {
			instance = new TextureGenerator();
		}
		
		return instance;
	}

	
	
	
}
