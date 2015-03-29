package de.pueski.jrhythm.core;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.spi.LoggingEvent;

public class GLAppender extends ConsoleAppender {

	public GLAppender() {
		super();
	}
	
	@Override
	protected void subAppend(LoggingEvent event) {
		super.subAppend(event);
		if (Game.getInstance() != null)
			Game.getInstance().setMessage(event.getLevel()+" "+event.getLocationInformation().getFileName()+":"+event.getRenderedMessage());
	}

}
