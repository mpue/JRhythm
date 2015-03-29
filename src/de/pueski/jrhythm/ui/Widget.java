package de.pueski.jrhythm.ui;

import java.util.Vector;

import de.pueski.jrhythm.core.GLDrawable;

/**
 * <p>
 * This is the abstract root class of any UI element. UI elements
 * are for example windows, dialogs and frames but also elements like
 * labels, input buxes and so on.
 * </p>
 * <p>
 * The ui system is organized hierarchical, such that any alement 
 * can have child elements.  The {@link UIManager} takes care of this
 * draws the UI hierarchy automatically and layouts all children.
 * </p>
 * 
 * @author Matthias Pueski
 *
 */
public abstract class Widget implements GLDrawable {
	
	/**
	 * x-location 
	 */	
	protected int x;
	/**
	 *  y-location
	 */
	protected int y;
	/**
	 * The width of the widget in pixels
	 */
	protected int width;
	/**
	 * The height of the widget in pixels
	 */
	protected int height;
	/**
	 * Minimum width of the widget
	 */
	protected int minWidth;
	/**
	 * Maximum width of the widget;
	 */	
	protected int maxWidth;
	/**
	 * Minimum height of the widget
	 */
	protected int minHeight;
	/**
	 * Maximum width of the widget;
	 */	
	protected int maxHeight;	
	/**
	 * visibility
	 */
	protected boolean visible;
	/**
	 * Childs of the widget
	 */
	protected Vector<Widget> children;
	/**
	 * Has it focus? False by default
	 */
	protected boolean focus = false;
	
	protected Widget(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.children = new Vector<Widget>();
	}
	
	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}
	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}
	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	/**
	 * @return the minWidth
	 */
	public int getMinWidth() {
		return minWidth;
	}
	/**
	 * @param minWidth the minWidth to set
	 */
	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}
	/**
	 * @return the maxWidth
	 */
	public int getMaxWidth() {
		return maxWidth;
	}
	/**
	 * @param maxWidth the maxWidth to set
	 */
	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}
	/**
	 * @return the minHeight
	 */
	public int getMinHeight() {
		return minHeight;
	}
	/**
	 * @param minHeight the minHeight to set
	 */
	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}
	/**
	 * @return the maxHeight
	 */
	public int getMaxHeight() {
		return maxHeight;
	}
	/**
	 * @param maxHeight the maxHeight to set
	 */
	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}
	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}
	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	/**
	 * @return the focus
	 */
	public boolean hasFocus() {
		return focus;
	}

	/**
	 * @param focus the focus to set
	 */
	public void setFocus(boolean focus) {
		this.focus = focus;
	}

	/**
	 * @return the children
	 */
	public Vector<Widget> getChildren() {
		return children;
	}
	/**
	 * @param children the children to set
	 */
	public void setChildren(Vector<Widget> children) {
		this.children = children;
	}
	
	

}
