package de.pueski.jrhythm.sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MusicPlayer {

	private static final Log log = LogFactory.getLog(MusicPlayer.class);
	
	private static String inputDir = ".";	
	private ArrayList<File> songs;	
	private int songIndex = 0;	
	private InputStream songInput;	
	private Player player; 
	
	private boolean configured = false;
	
	public MusicPlayer() {		

		songs = new ArrayList<File>();
		
		try {
			Properties p = new Properties();
			p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));
			inputDir = p.getProperty("mp3dir");
		}
		catch (Exception e1) {
			e1.printStackTrace();
		}
		
		if (!new File(inputDir).exists()) {
			log.error("directory "+inputDir+" does not exist. Please configure your music path in application.properties.");
			return;
		}
		
		File root = new File(inputDir);
		
		File[] files = root.listFiles();

		
		for (int i=0; i < files.length;i++) {
			if (files[i].getName().endsWith(".mp3")) {
				songs.add(files[i]);
			}
		}

		// select a random song at the beginning
		Random r = new Random();
		songIndex = r.nextInt(songs.size());
		
		try {
			songInput = new FileInputStream(songs.get(songIndex));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		configured = true;
	}

	public void start() {
		if (configured)
			play();		
	}

	public void close() {
		if (configured)
			if (player != null)
				player.close();
	}
	
	public void playSong(int songIndex) {
		
		try {
			songInput = new FileInputStream(songs.get(songIndex));
		}
		catch (Exception e) {
			log.error(e.getMessage());
		}
		
		play();		
	}
	
	public void nextSong() {
		
		if (songIndex < songs.size() - 1) {
			songIndex++;
		}
		else {
			songIndex = 0;
		}
		
		if (player != null) {
			player.close();
		}
		
		try {
			songInput = new FileInputStream(songs.get(songIndex));
		}
		catch (Exception e) {
			log.error(e.getMessage());
		}
		
		play();
	}
	
	public void previousSong() {
		
		if (songIndex > 0) {
			songIndex--;
		}
		else {
			songIndex = songs.size() - 1;
		}
		
		if (player != null) {
			player.close();
		}
		
		try {
			songInput = new FileInputStream(songs.get(songIndex));
		}
		catch (Exception e) {
			log.error(e.getMessage());
		}
		
		play();
	}

	public void play() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					player = new Player(songInput);			
					player.play();
				}
				catch (JavaLayerException e) {
					e.printStackTrace();
				}
			}
		}).start();		
	}
	
	public String getCurrentSongName() {
		return songs.get(songIndex).getName();
	}
	
}