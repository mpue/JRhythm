package de.pueski.jrhythm.objects;

import org.lwjgl.opengl.GL11;

import de.pueski.jrhythm.math.Vector2f;
import de.pueski.jrhythm.math.Vector3f;
import de.pueski.jrhythm.texture.TextureManager;

/**
 * A grid is a rectangular set of points needed to create less or more realistic
 * landscapes from height maps.
 * 
 * @author Matthias Pueski (16.05.2011)
 * 
 */
public class Grid extends SceneNode {
	
	private static final long serialVersionUID = -8445889856045698074L;
	
	private Vector3f[][] gridData;
	private int xsize;
	private int zsize;

	public Grid(int xsize, int zsize) {
		gridData = new Vector3f[xsize][zsize];
		this.zsize = zsize;
		this.xsize = xsize;
	}

	@Override
	public void draw() {
		TextureManager.getInstance().getTexture("texgen/grass.png").bind();
		GL11.glBegin(GL11.GL_QUADS);
		for (int x = 0; x < xsize - 1; x++) {
			for (int z = 0; z < zsize - 1; z++) {
				Vector3f v1 = Vector3f.sub(gridData[x][z], gridData[x + 1][z], null);
				Vector3f v2 = Vector3f.sub(gridData[x + 1][z], gridData[x + 1][z + 1], null);
				Vector3f cross = Vector3f.cross(v2, v1, null);
				Vector3f normal = cross.normalise(null);
				GL11.glColor3f(1.0f, 1.0f, 1.0f);
				GL11.glNormal3f(normal.getX(), normal.getY(), normal.getZ());
				GL11.glTexCoord2f(0.0f, 0.0f);
				GL11.glVertex3f(gridData[x][z].getX(), gridData[x][z].getY(), gridData[x][z].getZ());
				GL11.glTexCoord2f(1.0f, 0.0f);
				GL11.glVertex3f(gridData[x + 1][z].getX(), gridData[x + 1][z].getY(), gridData[x + 1][z].getZ());
				GL11.glTexCoord2f(1.0f, 1.0f);
				GL11.glVertex3f(gridData[x + 1][z + 1].getX(), gridData[x + 1][z + 1].getY(), gridData[x + 1][z + 1].getZ());
				GL11.glTexCoord2f(0.0f, 1.0f);
				GL11.glVertex3f(gridData[x][z + 1].getX(), gridData[x][z + 1].getY(), gridData[x][z + 1].getZ());
			}
		}
		GL11.glEnd();
	}

	/**
	 * @return the gridData
	 */
	public Vector3f[][] getGridData() {
		return gridData;
	}

	/**
	 * @param gridData the gridData to set
	 */
	public void setGridData(Vector3f[][] gridData) {
		this.gridData = gridData;
	}

	/**
	 * @return the xsize
	 */
	public int getXsize() {
		return xsize;
	}

	/**
	 * @param xsize the xsize to set
	 */
	public void setXsize(int xsize) {
		this.xsize = xsize;
	}

	/**
	 * @return the zsize
	 */
	public int getZsize() {
		return zsize;
	}

	/**
	 * @param zsize the zsize to set
	 */
	public void setZsize(int zsize) {
		this.zsize = zsize;
	}

	/**
	 * <p>
	 * Converts this grid object to a {@link Mesh} object. A mesh can be easily
	 * added to a scene graph and may be manipulated much easier than a grid.
	 * </p>
	 * <p>
	 * In order to convert this grid to a mesh we need to Create a face for
	 * every vertex of the grid and its three subsequent neighbours. We do this
	 * by adding all points of the grid as vertices. Then we get all neighbours
	 * and add them as vertex indices.
	 * </p>
	 * <p>
	 * Furthermore texture coordinates for each face and the vertex normals as
	 * well as the face normals need to be created for smooth shading,
	 * </p>
	 * 
	 * @return The converted grid as a mesh object.
	 */
	public Mesh toMesh(String textureLocation) {
		Mesh m = new Mesh();
		Material mat = new Material();
		mat.setName("default");
		m.getMaterials().put(m.getName(), mat);
		
		mat.setTexture(TextureManager.getInstance().getTexture(textureLocation));
		mat.setTextureLocation(textureLocation);
		mat.setTextured(true);
		for (int x = 0; x < xsize; x++) {
			for (int z = 0; z < zsize; z++) {
				m.getVertices().add(new Vertex(gridData[x][z].getX(), gridData[x][z].getY(), gridData[x][z].getZ()));
			}
		}
		int facecount = 0;
		for (int i = 0; i < m.getVertices().size() - xsize - 1; i++) {
			if (i > 0 && ((i + 1) % (xsize)) != 0) {
				// do not connect the outer bounds!!!
				Face f = new Face();
				f.getVertexIndices().add(i);
				f.getVertexIndices().add(i + 1);
				f.getVertexIndices().add(i + xsize + 1);
				f.getVertexIndices().add(i + xsize);
				mat.getFaces().add(f);
				facecount++;
			}
		}
		
		int radius = 1;
		
		// some sloppy kind of linear interpolation		
		for (int i = xsize*radius+1; i < m.getVertices().size() - xsize*radius - 1; i++) {
			if (i > 0 && ((i + 1) % (xsize*radius)) != 0) {
				Vertex v1 =  m.getVertices().get(i-xsize*radius-1);	
				Vertex v2 =  m.getVertices().get(i-xsize*radius);
				Vertex v3 =  m.getVertices().get(i-xsize*radius+1);
				Vertex v4 =  m.getVertices().get(i-1);
				Vertex v5 =  m.getVertices().get(i);
				Vertex v6 =  m.getVertices().get(i+1);
				Vertex v7 =  m.getVertices().get(i+xsize*radius-1);	
				Vertex v8 =  m.getVertices().get(i+xsize*radius);
				Vertex v9 =  m.getVertices().get(i+xsize*radius+1);
				float middleY = (v1.y + v2.y + v3.y + v4.y + v5.y +v6.y +v7.y + v8.y +v9.y) / 9;				
				v1.setY(middleY);
				v2.setY(middleY);
				v3.setY(middleY);
				v4.setY(middleY);
				v5.setY(middleY);
				v6.setY(middleY);
				v7.setY(middleY);
				v8.setY(middleY);
				v9.setY(middleY);				
			}			 
		}
		
		for (Face f : mat.getFaces()) {
			Vertex v1 = m.getVertices().get(f.getVertexIndices().get(0));
			Vertex v2 = m.getVertices().get(f.getVertexIndices().get(1));
			Vertex v3 = m.getVertices().get(f.getVertexIndices().get(2));
			Vertex v4 = m.getVertices().get(f.getVertexIndices().get(3));
			// calculate midpoint needed for collision detection
			float midPointX = (v1.x + v2.x + v3.x + v4.x) / 4;
			float midPointY = (v1.y + v2.y + v3.y + v4.y) / 4;
			float midPointZ = (v1.z + v2.z + v3.z + v4.z) / 4;
			f.setMidpoint(new Vector3f(midPointX, midPointY, midPointZ));
		}		
		
		// create uv coordinates		
		int uvIndex = 1;
		
		for (String key : m.getMaterials().keySet()) {
			Material material = m.getMaterials().get(key);
			
			for (Face f : material.getFaces()) {

				Vector3f v1 = Vector3f.sub(m.getVertices().get(f.getVertexIndices().get(0)),
						m.getVertices().get(f.getVertexIndices().get(1)), null);
				Vector3f v2 = Vector3f.sub(m.getVertices().get(f.getVertexIndices().get(1)),
						m.getVertices().get(f.getVertexIndices().get(2)), null);
				Vector3f cross = Vector3f.cross(v1, v2, null);
				Vector3f normal = cross.normalise(null);
				f.setNormal(normal);
				m.getUVCoordinates().add(new Vector2f(0.0f, 0.0f));
				m.getUVCoordinates().add(new Vector2f(1.0f, 0.0f));
				m.getUVCoordinates().add(new Vector2f(1.0f, 1.0f));
				m.getUVCoordinates().add(new Vector2f(0.0f, 1.0f));
				f.getUvVertexIndices().add(uvIndex + 0);
				f.getUvVertexIndices().add(uvIndex + 1);
				f.getUvVertexIndices().add(uvIndex + 2);
				f.getUvVertexIndices().add(uvIndex + 3);
				uvIndex += 4;
			}
		}
		
		m.setName("Jim"+System.currentTimeMillis());
		MeshFactory.getInstance().calculateVertexNormals(m);
		return m;
	}
}
