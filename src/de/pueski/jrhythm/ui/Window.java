package de.pueski.jrhythm.ui;

public abstract class Window extends Widget {

	protected String name;
	protected int zindex;
	
	
	protected Window(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj == null) {
			return false;			
		}
		if (obj instanceof Window) {
			Window win = (Window)obj;
			
			return (win.width == width && 
					win.height == height &&
					win.zindex == zindex);
			
		}
		
		return false;

	}

	/**
	 * Determines if a specific coordinate is iinside the window bounds
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return
	 */
	boolean isInside(int x, int y) {
		return (x >= this.x && x <= this.x+this.width && y >= this.y	&& y <= this.y+this.height);
	}

	boolean isOnResizeEdge(int x, int y) {
		return (x >= this.x+this.width-10 && x <= this.x+this.width+10 && y >= this.y+this.height-10 && y <= this.y+this.height+10);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName()+ "("+name+" : "+x+","+y+")";		
	}
	
}
