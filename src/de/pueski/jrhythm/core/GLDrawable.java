package de.pueski.jrhythm.core;

import de.pueski.jrhythm.objects.SceneNode;
import de.pueski.jrhythm.scene.SceneManager;

/**
 * <p>
 * This interface is the drawing interface for all drawable objects of the engine.
 * </p>
 * <p>
 * Typically all {@link SceneNode} objects implement this interface indirectly.
 * </p>
 * 
 * @author pueskma
 *
 */

public interface GLDrawable {

	/**
	 * Draw the given object in {@link SceneManager}
	 */
	void draw();
	
}
