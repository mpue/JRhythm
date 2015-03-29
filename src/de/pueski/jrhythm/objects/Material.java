package de.pueski.jrhythm.objects;

import java.util.Vector;

import de.pueski.jrhythm.math.Vector4f;
import de.pueski.jrhythm.texture.Texture;

public class Material {

	private String name;
	private String textureLocation;
	float shininess;
	private Vector4f ambient;
	private Vector4f diffuse;
	private Vector4f specular;
	private Vector<Face> faces;
	private Texture texture;
	private boolean textured;

	public Material() {
		this.faces = new Vector<Face>();
	}

	public void attachTexture(String name) {
		textureLocation = name;
		textured = true;
	}

	/**
	 * @return the textured
	 */
	public boolean isTextured() {
		return textured;
	}

	/**
	 * @param textured
	 *            the textured to set
	 */
	public void setTextured(boolean textured) {
		this.textured = textured;
	}

	/**
	 * @return the texture
	 */
	public Texture getTexture() {
		return texture;
	}

	/**
	 * @param texture
	 *            the texture to set
	 */
	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	/**
	 * @return the faces
	 */
	public Vector<Face> getFaces() {
		return faces;
	}

	/**
	 * @param faces
	 *            the faces to set
	 */
	public void setFaces(Vector<Face> faces) {
		this.faces = faces;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTextureLocation() {
		return textureLocation;
	}

	public void setTextureLocation(String textureLocation) {
		this.textureLocation = textureLocation;
	}

	public float getShininess() {
		return shininess;
	}

	public void setShininess(float shininess) {
		this.shininess = shininess;
	}

	public Vector4f getAmbient() {
		return ambient;
	}

	public void setAmbient(Vector4f ambient) {
		this.ambient = ambient;
	}

	public Vector4f getDiffuse() {
		return diffuse;
	}

	public void setDiffuse(Vector4f diffuse) {
		this.diffuse = diffuse;
	}

	public Vector4f getSpecular() {
		return specular;
	}

	public void setSpecular(Vector4f specular) {
		this.specular = specular;
	}

}
