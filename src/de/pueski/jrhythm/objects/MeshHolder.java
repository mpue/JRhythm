package de.pueski.jrhythm.objects;

public class MeshHolder extends SceneNode {

	private final Mesh mesh;
	
	public MeshHolder(Mesh mesh) {
		this.mesh = mesh;
	}
	
	@Override
	public void draw() {
		mesh.setLocation(location);
		mesh.draw();
	}

	/**
	 * @return the mesh
	 */
	public Mesh getMesh() {
		return mesh;
	}

	
	
}

