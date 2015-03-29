package de.pueski.jrhythm.text;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Matthias Pueski (19.05.2011)
 *
 */
public class TextFactory  {
	
	private static final Log log = LogFactory.getLog(TextFactory.class);	

	private static TextFactory instance = null;
	
	protected TextFactory() {
		log.info("initializing.");
	}
	
	public static TextFactory getInstance() {
		if (instance == null)
			instance = new TextFactory();
		return instance;
	}
	
}
