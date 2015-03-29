package de.pueski.jrhythm.scene;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SceneManager {

	private static final Log log = LogFactory.getLog(SceneManager.class);

	private static SceneManager instance = null;

	private final ArrayList<AbstractScene> scenes;

	private AbstractScene currentScene;

	private int sceneIndex = 0;

	protected SceneManager() {
		log.info("initializing.");
		scenes = new ArrayList<AbstractScene>();
	}

	public static SceneManager getInstance() {
		if (instance == null)
			instance = new SceneManager();
		return instance;
	}

	public void addScene(AbstractScene scene) {
		scenes.add(scene);
		if (currentScene == null) {
			currentScene = scene;
		}
	}

	void removeScene(AbstractScene scene) {
		scenes.remove(scene);
	}

	public AbstractScene selectNext() {

		if (scenes.size() == 0) {
			return null;
		}

		if (sceneIndex < scenes.size() - 1) {
			sceneIndex++;
		}
		else {
			sceneIndex = 0;
		}
		
		currentScene = scenes.get(sceneIndex);
		
		log.info("Selected scene "+currentScene.getName());
		
		return currentScene;
	}

	public AbstractScene selectPrevious() {
		if (scenes.size() == 0) {
			return null;
		}
		if (sceneIndex > 0) {
			sceneIndex--;
		}
		currentScene = scenes.get(sceneIndex);
		return currentScene;
	}

	public AbstractScene getCurrentScene() {
		return currentScene;
	}

}
