package de.pueski.jrhythm.objects;

public class Sector {

	private float size;
	private int index;
	private float xOffset;
	private float yOffset;
	private float zOffset;
	
	public Sector(float size, int index, float xOffset, float yOffset, float zOffset) {
		this.size = size;
		this.index = index;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
	}
	
	/**
	 * @return the size
	 */
	public float getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(float size) {
		this.size = size;
	}
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the xOffset
	 */
	public float getxOffset() {
		return xOffset;
	}

	/**
	 * @param xOffset the xOffset to set
	 */
	public void setxOffset(float xOffset) {
		this.xOffset = xOffset;
	}

	/**
	 * @return the yOffset
	 */
	public float getyOffset() {
		return yOffset;
	}

	/**
	 * @param yOffset the yOffset to set
	 */
	public void setyOffset(float yOffset) {
		this.yOffset = yOffset;
	}

	/**
	 * @return the zOffset
	 */
	public float getzOffset() {
		return zOffset;
	}

	/**
	 * @param zOffset the zOffset to set
	 */
	public void setzOffset(float zOffset) {
		this.zOffset = zOffset;
	}
	
	@Override
	public String toString() {
		return "[Sector] : "+xOffset+","+yOffset+","+zOffset+" size : "+size+" index :"+index;		
	}
	
	
}
