package de.pueski.jrhythm.core;

public class GLColor4f extends GLColor3f {

	private float alpha = 1.0f;

	public GLColor4f(float red, float green, float blue) {
		super(red, green, blue);
	}

	public GLColor4f(float red, float green, float blue, float alpha) {
		super(red, green, blue);
		this.alpha = alpha;
	}

	/**
	 * @return the alpha
	 */
	public float getAlpha() {
		return alpha;
	}

	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

}
