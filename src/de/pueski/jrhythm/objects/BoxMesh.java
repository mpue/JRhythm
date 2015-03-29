package de.pueski.jrhythm.objects;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import de.pueski.jrhythm.core.GLDrawable;
import de.pueski.jrhythm.math.Vector2f;
import de.pueski.jrhythm.math.Vector3f;
import de.pueski.jrhythm.texture.TextureManager;

public class BoxMesh extends SceneNode {
	
	private static final long serialVersionUID = 6576132698443432505L;
	
	private ArrayList<GLQuad> quads;
	private Vector3f location;
	private Vector3f color;
	
	public BoxMesh(Vector3f location) {
		quads = new ArrayList<GLQuad>();
		this.location = location;
	}
	
	public void addBox(Box box) {
		for (GLQuad quad : box.getQuads()) {
			
			// if (!quads.contains(quad)) {
				quad.translate(box.getLocation());
				quads.add(quad);							
			// }
		}
	}

	public ArrayList<GLQuad> getQuads() {
		return quads;
	}

	public void setQuads(ArrayList<GLQuad> quads) {
		this.quads = quads;
	}

	public Vector3f getLocation() {
		return location;
	}

	public void setLocation(Vector3f location) {
		this.location = location;
	}
	
	public void draw() {

		// use shader program if available
		if (useShader) {
			enableShader();
		}
		
		if (location != null)
			GL11.glTranslatef(location.getX(), location.getY(), location.getZ());

		GL11.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(yrot, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(zrot, 0.0f, 0.0f, 1.0f);

		TextureManager.getInstance().getTexture("texgen/marble.png").bind();

		GL11.glBegin(GL11.GL_QUADS);

		if (color != null)
			GL11.glColor3f(color.getX(), color.getY(), color.getZ());
		else
			GL11.glColor3f(0.5f, 0.5f, 0.5f);
		
		for (GLQuad quad : quads) {
			
			GL11.glNormal3f(quad.getNormal().x,quad.getNormal().y,quad.getNormal().z);
			
			for (int i = 0; i < quad.getVertices().size();i++) {
				Vector2f texCoord = quad.getTexCoords().get(i);
				Vector3f vertex = quad.getVertices().get(i);
				GL11.glTexCoord2f(texCoord.x,texCoord.y);
				GL11.glVertex3f(vertex.x,vertex.y,vertex.z);
			}
			
		}

		GL11.glEnd();
		
		// disable shader program if used
		if (useShader) {
			disableShader();
		}
	}
	
}
