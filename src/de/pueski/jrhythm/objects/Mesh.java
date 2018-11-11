package de.pueski.jrhythm.objects;

import static org.lwjgl.opengl.GL11.GL_AMBIENT;
import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_CURRENT_BIT;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.GL_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LIGHTING_BIT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_BIT;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SHININESS;
import static org.lwjgl.opengl.GL11.GL_SPECULAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glMaterial;
import static org.lwjgl.opengl.GL11.glNormal3f;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.linearmath.convexhull.HullDesc;
import com.bulletphysics.linearmath.convexhull.HullFlags;
import com.bulletphysics.linearmath.convexhull.HullLibrary;
import com.bulletphysics.linearmath.convexhull.HullResult;
import com.bulletphysics.util.ObjectArrayList;

import de.pueski.jrhythm.core.Light;
import de.pueski.jrhythm.math.AxisAngleRotation;
import de.pueski.jrhythm.math.MathUtil;
import de.pueski.jrhythm.math.Vector2f;
import de.pueski.jrhythm.math.Vector3f;
import de.pueski.jrhythm.math.Vector4f;
import de.pueski.jrhythm.util.VBOUtils;
/**
 * <p>
 * This class represents an ordinary triangle mesh object, which currenly
 * can be also created from a wavefront object file.
 * </p>
 * <p>
 * Each mesh must have at least one material, due to the fact, that materials 
 * of a wavefront object file are groups of faces. Thus the file is loaded 
 * and rendered this way.
 * </p>
 * <p>
 * In order to create a {@link Mesh} from a wavefront file one must use the
 * {@link MeshFactory}.
 * </p>
 * <p>
 * This mesh class supports caluclating and drawing of stencil buffer shadow volumes,
 * as well as creation of {@link CollisionShape} objects for the bullet physics library.
 * </p>
 * 
 * @author Matthias Pueski
 *
 */
public class Mesh extends SceneNode {
	
	private static final Log log = LogFactory.getLog(Mesh.class);
	
	private static final long serialVersionUID = 3406084382045144472L;

	public static final boolean enableVBO = true; 
	
	public static final int DRAW_SOLID = 0;
	public static final int DRAW_SMOOTH = 1;
	public static final int DRAW_WIREFRAME = 2;
	
	private FloatBuffer matSpecular;
	private FloatBuffer matShininess;
	private FloatBuffer matDiffuse;
	private FloatBuffer matAmbient;
	
	private int vertexBufferID;
	private int textureBufferID;
	private int colourBufferID; 
	private int indexBufferID;
	private int normalBufferID;
	
	private FloatBuffer vertexBuffer;
	private IntBuffer indexBuffer;
	private FloatBuffer textureBuffer;
	private FloatBuffer normalBuffer;
	private FloatBuffer colorBuffer;
	
	private boolean shadow = false;
	
	static final FloatBuffer silhouetteColor = BufferUtils.createFloatBuffer(4).put(new float[] { 1.0f,0.0f,0.0f,1.0f });
	static final FloatBuffer shadowVolumeColor = BufferUtils.createFloatBuffer(4).put(new float[] { 1.0f,1.0f,1.0f,0.5f });
	
	static  {
		silhouetteColor.rewind();
		shadowVolumeColor.rewind();
	}
	
	final Vector<Face> faceCache = new Vector<Face>(); 
	
	int numFaces = 0;
	
	private HashMap<String, Material> materials;
	
	private Vector<Vertex> vertices;
	private Vector<Vector2f> uvCoordinates;
	
	private boolean drawSmooth = true;

	private String name;
	private String path;
	
	private final AxisAngleRotation axisAngleRotation = new AxisAngleRotation();
	
	public static int MODE_WAVEFRONT = 0;
	
	private Edge[] edges;
	private Vector<Edge> silhouette;
	
	int numEdges;

	public Mesh() {
		this.materials = new HashMap<String, Material>();
		this.vertices = new Vector<Vertex>();
		this.uvCoordinates = new Vector<Vector2f>();				
		this.textured = false;
		this.silhouette = new Vector<Edge>();
	}

	/**
	 * @return the vertices
	 */
	public Vector<Vertex> getVertices() {
		return vertices;
	}

	/**
	 * @param vertices the vertices to set
	 */
	public void setVertices(Vector<Vertex> vertices) {
		this.vertices = vertices;
	}

	/**
	 * @return the uVCoordinates
	 */
	public Vector<Vector2f> getUVCoordinates() {
		return uvCoordinates;
	}

	/**
	 * @param coordinates the uVCoordinates to set
	 */
	public void setUVCoordinates(Vector<Vector2f> coordinates) {
		uvCoordinates = coordinates;
	}	

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	void prepareVBO() {
		
		vertexBufferID = VBOUtils.createVBOID();
		normalBufferID = VBOUtils.createVBOID();
		textureBufferID = VBOUtils.createVBOID();
		colourBufferID = VBOUtils.createVBOID();
		indexBufferID  = VBOUtils.createVBOID(); 
		
		int numFaces = 0;
		int numVerts = 0;
		int numUvCo = 0;
		int numIdx  = 0;
		
		for (String key : materials.keySet()) {
			
			Material material = materials.get(key);
			
			for (Face face : material.getFaces()) {
				
				numFaces += material.getFaces().size();
				
				for (int j = 0; j < face.getVertexIndices().size(); j++) {
					numUvCo += 2;
					numVerts += face.getVertexIndices().size();
					
				}
				
				numIdx += face.getVertexIndices().size();
			}

			
		}
		
		vertexBuffer = BufferUtils.createFloatBuffer(numVerts*3);;		
		textureBuffer = BufferUtils.createFloatBuffer(numUvCo);
		normalBuffer = BufferUtils.createFloatBuffer(numVerts *3);
		colorBuffer = BufferUtils.createFloatBuffer(numVerts*3);
		indexBuffer = BufferUtils.createIntBuffer(numIdx);
	
		int i = 0;
		for (String key : materials.keySet()) {
			
			Material material = materials.get(key);
			
			
			for (Face face : material.getFaces()) {
				
				for (int j = 0; j < face.getVertexIndices().size(); j++) {
					

					if (material.isTextured() && material.getTexture() != null) {
						Integer index = face.getUvVertexIndices().get(j);

						textureBuffer.put((float)getUVCoordinates().get(index-1).x).put((float)getUVCoordinates().get(index-1).y);		
						
					}
					
					float x = getVertices().get(face.getVertexIndices().get(j)).getX();
					float y = getVertices().get(face.getVertexIndices().get(j)).getY();
					float z = getVertices().get(face.getVertexIndices().get(j)).getZ();

						
					float nx = getVertices().get(face.getVertexIndices().get(j)).getNormal().getX();
					float ny = getVertices().get(face.getVertexIndices().get(j)).getNormal().getY();
					float nz = getVertices().get(face.getVertexIndices().get(j)).getNormal().getZ();
						
					normalBuffer.put(nx).put(ny).put(nz);
					vertexBuffer.put(x).put(y).put(z);
					colorBuffer.put(1.0f).put(1.0f).put(1.0f);
				}
				indexBuffer.put(i++);
				indexBuffer.put(i++);
				indexBuffer.put(i++);
			}

			
		}

		textureBuffer.flip();
		normalBuffer.flip();
		vertexBuffer.flip();
		colorBuffer.flip();
		indexBuffer.flip();

	}	

	/**
	 * Draws the mesh onto the (previously) initialized OpenGL Screen
	 * 
	 */
	public void draw() {

		if (enableVBO) {
			
			drawVBO();
		}
		else {
			drawStandard();			
		}

	}

	private void drawVBO() {
		// use shader program if available
		if (useShader) {
			enableShader();
		}
		
		if (location != null)
			glTranslatef(location.getX(), location.getY(), location.getZ());

		glRotatef(axisAngleRotation.angle, axisAngleRotation.x, axisAngleRotation.y, axisAngleRotation.z);
		
		glRotatef(xrot, 1.0f, 0.0f, 0.0f);
		glRotatef(yrot, 0.0f, 1.0f, 0.0f);
		glRotatef(zrot, 0.0f, 0.0f, 1.0f);
		
		
		setMaterials();

		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		VBOUtils.bufferData(vertexBufferID, vertexBuffer);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		VBOUtils.bufferData(textureBufferID,textureBuffer);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);		

		GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
		VBOUtils.bufferData(normalBufferID, normalBuffer);
		GL11.glNormalPointer(GL11.GL_FLOAT, 12,0);
		
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		VBOUtils.bufferData(colourBufferID, colorBuffer);
		GL11.glColorPointer(3, GL11.GL_FLOAT, 12, 0);

		VBOUtils.bufferElementData(indexBufferID,indexBuffer); 
		// GL11.glDrawArrays(GL11.GL_QUADS, 0, 24);
		
		
		Material m = null;
		
		for (String key : materials.keySet()) {
			
			Material material = materials.get(key);
						
			// VBOUtils.renderRange(GL_TRIANGLES,vertexBufferID, textureBufferID, normalBufferID, colourBufferID, indexBufferID, start,material.getFaces().size() * 3, numFaces * 3);
			
			if (material.isTextured() && material.getTexture() != null) {
				glEnable(GL_TEXTURE_2D);
				material.getTexture().bind();
				m = material;
				break;
			}

			
		}
		
		//GL11.glDrawElements(GL11.GL_TRIANGLES, indexBuffer);
		GL12.glDrawRangeElements(GL_TRIANGLES, 0, numFaces * 3, numFaces * 3, GL11.GL_UNSIGNED_INT, 0);
		
		if (m.isTextured() && m.getTexture() != null) {
			glDisable(GL_TEXTURE_2D);
		}
		
		// VBOUtils.render(Gç numFaces*3,numFaces*3);
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		
		
		// disable shader program if used
		if (useShader) {
			disableShader();
		}
	}
	
	private void drawStandard() {
		
		// use shader program if available
		if (useShader) {
			enableShader();
		}
		
		int mode = DRAW_SMOOTH;
		
		setMaterials();
		
		glTranslatef(location.getX(), location.getY(), location.getZ());
		
		glRotatef(axisAngleRotation.angle, axisAngleRotation.x, axisAngleRotation.y, axisAngleRotation.z);
		
		glRotatef(xrot, 1.0f, 0.0f, 0.0f);
		glRotatef(yrot, 0.0f, 1.0f, 0.0f);
		glRotatef(zrot, 0.0f, 0.0f, 1.0f);
		
		for (String key : materials.keySet()) {
			
			Material material = materials.get(key);
						
			if (material.isTextured() && material.getTexture() != null) {
				glEnable(GL_TEXTURE_2D);
				material.getTexture().bind();				
			}

			glBegin(GL_TRIANGLES);
			
			for (Face face : material.getFaces()) {
				
				for (int j = 0; j < face.getVertexIndices().size(); j++) {
					

					if (material.isTextured() && material.getTexture() != null) {
						Integer index = face.getUvVertexIndices().get(j);
						glTexCoord2f((float)getUVCoordinates().get(index-1).x,(float)getUVCoordinates().get(index-1).y);
					}
					
					float x = getVertices().get(face.getVertexIndices().get(j)).getX();
					float y = getVertices().get(face.getVertexIndices().get(j)).getY();
					float z = getVertices().get(face.getVertexIndices().get(j)).getZ();
					
					if (mode == DRAW_SMOOTH) {
						
						float nx = getVertices().get(face.getVertexIndices().get(j)).getNormal().getX();
						float ny = getVertices().get(face.getVertexIndices().get(j)).getNormal().getY();
						float nz = getVertices().get(face.getVertexIndices().get(j)).getNormal().getZ();
						
						glNormal3f(nx, ny, nz);
					}
					glVertex3f(x, y, z);
				}
			}

			glEnd();
			
		}
				
		glDisable(GL_TEXTURE_2D);
		
		// disable shader program if used
		if (useShader) {
			disableShader();
		}

	}

	public void setMaterials() {
		
		if (matSpecular != null)		
			glMaterial(GL_FRONT, GL_SPECULAR, matSpecular);
		
		if (matShininess != null)
			glMaterial(GL_FRONT, GL_SHININESS, matShininess);
		
		if(matAmbient != null)
			glMaterial(GL_FRONT, GL_AMBIENT, matAmbient);
		
		if (matDiffuse != null)
			glMaterial(GL_FRONT, GL_DIFFUSE, matDiffuse);
		
	}

	/**
	 * @param matSpecular the matSpecular to set
	 */
	public void setMatSpecular(float r, float g, float b, float a) {
		this.matSpecular = BufferUtils.createFloatBuffer(4).put(new float[] { r, g, b, a });
		matSpecular.rewind();
	}

	/**
	 * @param matShininess the matShininess to set
	 */
	public void setMatShininess(float shininess) {
		this.matShininess = BufferUtils.createFloatBuffer(4).put(new float[] { shininess });
		matShininess.rewind();
	}

	/**
	 * @param matDiffuse the matDiffuse to set
	 */
	public void setMatDiffuse(float r, float g, float b, float a) {
		this.matDiffuse = BufferUtils.createFloatBuffer(4).put(new float[] { r, g, b, a });
		matDiffuse.rewind();
	}

	/**
	 * @param matAmbient the matAmbient to set
	 */
	public void setMatAmbient(float r, float g, float b, float a) {
		this.matAmbient = BufferUtils.createFloatBuffer(4).put(new float[] { r, g, b, a });		
		matAmbient.rewind();
	}

	/**
	 * @return the drawSmooth
	 */
	public boolean isDrawSmooth() {
		return drawSmooth;
	}

	/**
	 * @param drawSmooth the drawSmooth to set
	 */
	public void setDrawSmooth(boolean drawSmooth) {
		this.drawSmooth = drawSmooth;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	public HashMap<String, Material> getMaterials() {
		return materials;
	}

	public void setMaterials(HashMap<String, Material> materials) {
		this.materials = materials;
	}
	
	/**
	 * Builds a list of all edges of the mesh object. This is needed for 
	 * stencil shadow calculations.
	 * 
	 * @return the number of {@link Edge} objects.
	 */
	public int buildEdges() {
		
		int maxEdgeCount = numFaces * 3;
		edges = new Edge[maxEdgeCount];
		
		for (int i=0; i < maxEdgeCount;i++) {
			edges[i] = new Edge();
		}
		
		int[] firstEdge = new int[(vertices.size()+maxEdgeCount)];
		int[] nextEdge = new int[(vertices.size()+maxEdgeCount)];

	    for (int a = 0; a < vertices.size(); a++) firstEdge[a] = 0xFFFF;
	    for (int a = 0; a < vertices.size(); a++) nextEdge[a] = 0xFFFF;

	    /**
		 * First pass over all triangles. This finds all the edges satisfying
		 * the condition that the first vertex index is less than the second
		 * vertex index when the direction from the first vertex to the second
		 * vertex represents a counterclockwise winding around the triangle to
		 * which the edge belongs. For each edge found, the edge index is stored
		 * in a linked list of edges belonging to the lower-numbered vertex
		 * index i. This allows us to quickly find an edge in the second pass
		 * whose higher-numbered vertex index is i.
		 */
	    
	    int edgeCount = 0;
	    
		for (int a = 0; a < faceCache.size(); a++) {
			int i1 = faceCache.get(a).getVertexIndices().get(2);
			for (int b = 0; b < 3; b++) {
				int i2 = faceCache.get(a).getVertexIndices().get(b);
				if (i1 < i2) {
					Edge edge = edges[edgeCount];

					edge.vertexIndex[0] = i1;
					edge.vertexIndex[1] = i2;
					edge.faceIndex[0] = a;
					edge.faceIndex[1] =  a;

					int edgeIndex = firstEdge[i1];

					if (edgeIndex == 0xFFFF) {
						firstEdge[i1] = edgeCount;
					}
					else {
						for (;;) {
							int index = nextEdge[edgeIndex];
							if (index == 0xFFFF) {
								nextEdge[edgeIndex] = edgeCount;
								break;
							}

							edgeIndex = index;
						}
					}

					nextEdge[edgeCount] = 0xFFFF;
					edgeCount++;
				}

				i1 = i2;
			}

		}

		/**
		 * Second pass over all triangles. This finds all the edges satisfying
		 * the condition that the first vertex index is greater than the second
		 * vertex index when the direction from the first vertex to the second
		 * vertex represents a counterclockwise winding around the triangle to
		 * which the edge belongs. For each of these edges, the same edge should
		 * have already been found in the first pass for a different triangle.
		 * So we search the list of edges for the higher-numbered vertex index
		 * for the matching edge and fill in the second triangle index. The
		 * maximum number of comparisons in this search for any vertex is the
		 * number of edges having that vertex as an endpoint.
		 */
		
	    for (int a = 0; a < faceCache.size(); a++) {
			int i1 = faceCache.get(a).getVertexIndices().get(2);
			for (int b = 0; b < 3; b++) {
				int i2 = faceCache.get(a).getVertexIndices().get(b);
				if (i1 > i2) {
					for (int edgeIndex = firstEdge[i2]; edgeIndex != 0xFFFF; edgeIndex	= nextEdge[edgeIndex]) {
						Edge edge = edges[edgeIndex];
						
						if ((edge.vertexIndex[1] == i1) && (edge.faceIndex[0] == edge.faceIndex[1])) {
							edge.faceIndex[1] =  a;
							break;
						}
					}
				}

				i1 = i2;
			}

		}
	    
	    log.debug("Found "+edgeCount+" edges.");
	    
	    return (edgeCount);
	}
	
	/**
	 * Calculates the shadow silhouette of the mesh out of the {@link Edge}
	 * list.
	 * 
	 * @param light
	 */
	public void calculateSilhouette(Light light) {
		
		Vector4f fakeLightPos = light.getFakeTransform(this);
		
		silhouette = new Vector<Edge>();

	    // for each edge of the mesh
	    for (Edge edge : edges) {
	    	
	    	// generate a view vector pointing from one of the vertices
	    	// of the edge to the location of the light.
	    	
	    	Vector3f v = new Vector3f();
	    	Vertex vx = vertices.get(edge.vertexIndex[0]);
	    	
	    	v.x = fakeLightPos.x - vx.x;
	    	v.y = fakeLightPos.y - vx.y;
	    	v.z = fakeLightPos.z - vx.z;
	    	
	    	// take the dot product of the view vector with the face normals
	    	// of the two neighboring faces which form the edge.
	    	
	    	float fv1 = MathUtil.getDotProduct(v,faceCache.get(edge.faceIndex[0]).getNormal());
	    	float fv2 = MathUtil.getDotProduct(v,faceCache.get(edge.faceIndex[1]).getNormal());
	    	
	    	// compare the dot products for a sign difference,
	    	// if the signs are opposite, this is an silhouette edge
	    	
	    	if (fv1*fv2 < 0) {
	    		silhouette.add(edge);
	    	}
	    }

	}
	
	/**
	 * Builds a mesh local cache of all {@link Face} objects of all materials.
	 * This is just a convenience method. Since all {@link Material} objects
	 * are {@link Face} groups, we have a need for a complete list of all faces
	 * to simplify shadow calculations.
	 */
	void buildFaceCache() {
		
		for(String key : materials.keySet()) {	    	
			Material mat =  materials.get(key);	    	
			for (Face face : mat.getFaces()) {
				faceCache.add(face);				
			}	    	
		}
		
		log.debug("Cached "+faceCache.size()+" faces.");
	}
	
	/**
	 * Draws the {@link Edge} list of the mesh, which is relevant for shadowing,
	 * this method is for visual debugging purposes only.
	 */
	public void drawSilhouette() {

		glPushAttrib(GL_COLOR_BUFFER_BIT |
 					 GL_TEXTURE_BIT      |
					 GL_CURRENT_BIT      |
					 GL_LINE_BIT         |
					 GL_LIGHTING_BIT);

		glTranslatef(location.getX(), location.getY(), location.getZ());
		
		glRotatef(axisAngleRotation.angle, axisAngleRotation.x, axisAngleRotation.y, axisAngleRotation.z);
		
		glRotatef(xrot, 1.0f, 0.0f, 0.0f);
		glRotatef(yrot, 0.0f, 1.0f, 0.0f);
		glRotatef(zrot, 0.0f, 0.0f, 1.0f);

		for (int j = 0; j < silhouette.size();j++) {

	    	Vertex v1 = vertices.get(silhouette.get(j).vertexIndex[0]);
	    	Vertex v2 = vertices.get(silhouette.get(j).vertexIndex[1]);

	    	glLineWidth(2.0f);
	    	
	    	glColor3f(1.0f,0.0f,0.0f);
	    	
	    	glBegin(GL_LINE_LOOP);
				glVertex3f(v1.x, v1.y, v1.z);
				glVertex3f(v2.x, v2.y, v2.z);
	    	glEnd();
	    	
	    	glPointSize(5.0f);
	    	
	    	glBegin(GL_POINTS);
	    		glColor3f(0.0f,1.0f,0.0f);
				glVertex3f(v1.x, v1.y, v1.z);
				glColor3f(0.0f,1.0f,1.0f);
				glVertex3f(v2.x, v2.y, v2.z);
			glEnd();
	    	
	    }

	    glPopAttrib();
		
	}
	
	/**
	 * Draws the extruded shadow volume of the mesh according to the current light position.
	 * We need this to determine which objects are actually shadowed. The volume is later used
	 * in stencil shadow calculations.
	 * 
	 * @param lightPos The position of the light to draw the shadow volume for
	 */
	public void drawShadowVolume(Light light, boolean debug) {

		glEnable(GL_CULL_FACE);
		
		glPushAttrib(GL_COLOR_BUFFER_BIT |
					 GL_TEXTURE_BIT      |
					 GL_CURRENT_BIT      |
					 GL_LINE_BIT         |
					 GL_LIGHTING_BIT);

		Vector4f fakeLightPos = light.getFakeTransform(this);

		glTranslatef(location.getX(), location.getY(), location.getZ());
		
		glRotatef(axisAngleRotation.angle, axisAngleRotation.x, axisAngleRotation.y, axisAngleRotation.z);
		
		glRotatef(xrot, 1.0f, 0.0f, 0.0f);
		glRotatef(yrot, 0.0f, 1.0f, 0.0f);
		glRotatef(zrot, 0.0f, 0.0f, 1.0f);
		
		// float extrude = 10000000000f;
		float extrude = 10000000000000000.0f;

		/**
		 * Once the set of an object�s silhouette edges has been determined with
		 * respect to a light source, we must extrude each edge away from the
		 * light�s position to form the object�s shadow volume.
		 * 
		 * For a point light source, the extrusion of the silhouette edges
		 * consists of a set of quads, each of which has the two unmodified
		 * vertices belonging to an edge and two additional vertices
		 * corresponding to the extrusion of the same edge to infinity. For an
		 * infinite directional light source, all points project to the same
		 * point at infinity, so the extrusion of the silhouette edges can be
		 * represented by a set of triangles that all share a common vertex. We
		 * distinguish between points that should be treated normally and those
		 * that should be extruded to infinity by using 4D homogeneous
		 * coordinates. A w-coordinate of one is assigned to the unmodified
		 * vertices and a w-coordinate of zero is assigned to the extruded
		 * vertices. The extrusion methods that we present utilize the
		 * information stored in the w-coordinate to perform the appropriate
		 * vertex modifications.
		 */
		
		glDisable(GL_TEXTURE_2D);

		glFrontFace(GL_CW);
		
	    for (int j = 0; j < silhouette.size();j++) {

	    	Edge e = silhouette.get(j);
	    	
	    	// take the two vertices of an edge
	    	Vertex v1 = vertices.get(e.vertexIndex[0]);
	    	Vertex v2 = vertices.get(e.vertexIndex[1]);
	    	
	    	// the two other vertices of the shadow quad
	    	Vertex v3 = new Vertex();
	    	Vertex v4 = new Vertex();
	    	
	    	// extrude them along the vector between the light and one vertex of the edge
	    	v3.x = v2.x + ((v2.x - fakeLightPos.x)	* extrude);
	    	v3.y = v2.y + ((v2.y - fakeLightPos.y)	* extrude);
	    	v3.z = v2.z + ((v2.z - fakeLightPos.z)	* extrude);
	    	v4.x = v1.x + ((v1.x - fakeLightPos.x)	* extrude);
	    	v4.y = v1.y + ((v1.y - fakeLightPos.y)	* extrude);
	    	v4.z = v1.z + ((v1.z - fakeLightPos.z)	* extrude);
	    	
	    	glColor4f(0.3f,0.3f,0.3f,1.0f);
	    	
			/**
			 * We need to make sure that the vertices of each extrusion
			 * primitive are wound so that the face�s normal direction points
			 * out of the shadow volume. Suppose that a silhouette edge E has
			 * endpoints A and B. The edge-finding code presented in Listing 1
			 * associates the triangle for which the vertices A and B occur in
			 * counterclockwise order as the first triangle sharing the edge E.
			 * 
			 * Thus, if the first triangle faces toward the light source, then
			 * we want the vertices A and B to occur in the opposite order for
			 * the extruded primitive so that its vertices are wound
			 * counterclockwise. If the first triangle faces away from the light
			 * source, then we use the vertices A and B in the same order for
			 * the extruded primitive. 
			 */
	    	glBegin(GL_QUADS);
	    	
	    		if (faceCache.get(e.faceIndex[0]).isVisible()) {
	    			glVertex3f(v1.x, v1.y, v1.z);
	    			glVertex3f(v2.x, v2.y, v2.z);	
	    			glVertex3f(v3.x, v3.y, v3.z);	    		
	    			glVertex3f(v4.x, v4.y, v4.z);				
	    		}
	    		else {
	    			glVertex3f(v2.x, v2.y, v2.z);	
	    			glVertex3f(v1.x, v1.y, v1.z);
	    			glVertex3f(v4.x, v4.y, v4.z);				
	    			glVertex3f(v3.x, v3.y, v3.z);	    		
	    		}
	    	
	    	glEnd();

	    	if (debug) {
		    	
	    		glColor4f(1.0f,1.0f,1.0f,1.0f);
	    		glBegin(GL_LINES);
		    	
	    		if (faceCache.get(e.faceIndex[0]).isVisible()) {
	    			glVertex3f(v1.x, v1.y, v1.z);
	    			glVertex3f(v2.x, v2.y, v2.z);	
	    			glVertex3f(v3.x, v3.y, v3.z);	    		
	    			glVertex3f(v4.x, v4.y, v4.z);				
	    		}
	    		else {
	    			glVertex3f(v2.x, v2.y, v2.z);	
	    			glVertex3f(v1.x, v1.y, v1.z);
	    			glVertex3f(v4.x, v4.y, v4.z);				
	    			glVertex3f(v3.x, v3.y, v3.z);	    		
	    		}
	    	
	    		glEnd();
	    		glColor4f(0.3f,0.3f,0.3f,1.0f);
	    	}
	    }

	    
	    // draw the top cap -> all faces that are actually shadowed
	    glBegin(GL_TRIANGLES); {	    	
	    	for (Face face : faceCache ) {
	    		
	    		if (!face.isVisible()) {
	    			
	    			for (int j=0;j < 3;j++) {
	    				
	    				Integer index = face.getVertexIndices().get(j);	    				
	    				Vertex v = vertices.get(index);
	    				glVertex3f(v.x, v.y, v.z);
	    			}
	    		}
	    		
	    	}
	    }
	    glEnd();
	    
	    if (debug) {
		    // draw the top cap -> all faces that are actually shadowed
	    	glColor4f(1.0f,1.0f,1.0f,1.0f);
		    glBegin(GL_LINES); {	    	
		    	for (Face face : faceCache ) {
		    		
		    		if (!face.isVisible()) {
		    			
		    			for (int j=0;j < 3;j++) {
		    				
		    				Integer index = face.getVertexIndices().get(j);	    				
		    				Vertex v = vertices.get(index);
		    				glVertex3f(v.x, v.y, v.z);
		    			}
		    		}
		    		
		    	}
		    }
		    glEnd();
		    glColor4f(0.3f,0.3f,0.3f,1.0f);
	    }
	    
	    /**
	     *  draw the bottom cap, same procedure as above but at the extruded end
	     *  and with opposite face order
	     */
	    
	    glFrontFace(GL_CCW);
	    
	    glBegin(GL_TRIANGLES); {	    	
	    	for (Face face : faceCache ) {
	    		
	    		if (!face.isVisible()) {
	    			
	    			for (int j=0;j < 3;j++) {
	    				
	    				Integer index = face.getVertexIndices().get(j);	    				
	    				Vertex v = vertices.get(index);
	    				
	    				glVertex3f(v.x + ((v.x - fakeLightPos.x) * extrude),
	    						   v.y + ((v.y - fakeLightPos.y) * extrude),
	    						   v.z + ((v.z - fakeLightPos.z) * extrude));
	    			}
	    		}
	    		
	    	}
	    }
	    glEnd();
	    
	    if (debug) {
	    	glColor4f(1.0f,1.0f,1.0f,1.0f);
		    glBegin(GL_LINES); {	    	
		    	for (Face face : faceCache ) {
		    		
		    		if (!face.isVisible()) {
		    			
		    			for (int j=0;j < 3;j++) {
		    				
		    				Integer index = face.getVertexIndices().get(j);	    				
		    				Vertex v = vertices.get(index);
		    				
		    				glVertex3f(v.x + ((v.x - fakeLightPos.x) * extrude),
		    						   v.y + ((v.y - fakeLightPos.y) * extrude),
		    						   v.z + ((v.z - fakeLightPos.z) * extrude));
		    			}
		    		}
		    		
		    	}
		    }
		    glEnd();
		    glColor4f(0.3f,0.3f,0.3f,1.0f);
	    }
	    
	    glPopAttrib();
		glEnable(GL_TEXTURE_2D);

	}
	
	/**
	 * <p>
	 * Calculates the plane equation for each face in the form
	 * </p>
	 * <p>
	 * Ax + By + Cz - D = 0
	 * </p>
	 * 
	 */
	public void calculatePlanes() {
		
	    for (Face face : faceCache ) {
	    	
	    	Vector<Integer> indices = face.getVertexIndices(); 
	    	
			face.calculatePlane(vertices.get(indices.get(0)),
						   	    vertices.get(indices.get(1)),
						   	    vertices.get(indices.get(2)));

	    }
		
	}
	
	/**
	 * Checks the visibility of all faces from the lights
	 * point of view.
	 * 
	 * @param lightPos
	 */
	public void checkVisibility(Light light) {

		Vector4f fakeLightPos = light.getFakeTransform(this);
		
		for (int i = 0; i < faceCache.size(); i++) {
			
			Face f = faceCache.get(i);
			
			float side = f.plane.a * fakeLightPos.x +
						 f.plane.b * fakeLightPos.y +
						 f.plane.c * fakeLightPos.z +
						 f.plane.d;

			if (side > 0)
				f.setVisible(true);
			else
				f.setVisible(false);
		}
	}

	/**
	 * @return the silhouette
	 */
	public Vector<Edge> getSilhouette() {
		return silhouette;
	}

	/**
	 * @return the faceCache
	 */
	public Vector<Face> getFaceCache() {
		return faceCache;
	}

	/**
	 * @return the axisAngleRotation
	 */
	public AxisAngleRotation getAxisAngleRotation() {
		return axisAngleRotation;
	}

	/**
	 * @return the shadow
	 */
	public boolean isShadow() {
		return shadow;
	}

	/**
	 * @param shadow the shadow to set
	 */
	public void setShadow(boolean shadow) {
		this.shadow = shadow;
	}
	
	/**
	 * <p>
	 * Creates the {@link BvhTriangleMeshShape} for this mesh in 
	 * order to be used in the Bullet physics engine. This function builds
	 * the triangle mesh representation from the triangle data of this mesh.
	 * </p>
	 * <p>
	 * This function should be used if a non moving collision mesh is needed.
	 * </p>
	 * 
	 * @return the according {@link CollisionShape}
	 */
	public CollisionShape createBvhTriangleMeshShape() {

		int vertStride = 4 * 3 /* sizeof(btVector3) */;
		int indexStride = 3 * 4 /* 3*sizeof(int) */;

		final int totalTriangles = getFaceCache().size();
		final int totalVerts = getVertices().size();
		
		ByteBuffer vertices = ByteBuffer.allocateDirect(totalVerts * vertStride).order(ByteOrder.nativeOrder());
		ByteBuffer gIndices = ByteBuffer.allocateDirect(totalTriangles * 3 * 4).order(ByteOrder.nativeOrder());
		
		final Vector<Vertex> vertexData = getVertices();
		
		for (Vertex vertex : vertexData) {
			vertices.putFloat(vertex.x).putFloat(vertex.y).putFloat(vertex.z);			
		}	
		
		final HashMap<String, Material> materials = getMaterials();
		
		for (String key : materials.keySet()) {

			Material mat = materials.get(key);

			for (Face f : mat.getFaces()) {
				for (Integer index : f.getVertexIndices()) {
					gIndices.putInt(index);
				}
				
			}
		}
		
		TriangleIndexVertexArray indexVertexArrays = new TriangleIndexVertexArray(totalTriangles, gIndices, indexStride, totalVerts, vertices, vertStride);
		
		boolean useQuantizedAabbCompression = true; 
		return new BvhTriangleMeshShape(indexVertexArrays, useQuantizedAabbCompression);
	}	
	
	/**
	 * <p>
	 * Creates the {@link ConvexHullShape} for this mesh in 
	 * order to be used in the Bullet physics engine. This function builds
	 * the convex hull mesh representation from the triangle data of this mesh.
	 * </p>
	 * <p>
	 * This function should be used if a moving collision mesh is needed.
	 * </p>
	 * @return the according {@link CollisionShape}
	 */
	public CollisionShape createConvexHullShape() {
		
		ObjectArrayList<javax.vecmath.Vector3f> verts = new ObjectArrayList<javax.vecmath.Vector3f>();
		
		for (Vertex v : vertices) {
			verts.add(new javax.vecmath.Vector3f(v.x,v.y,v.z));
		}
		
		HullDesc desc = new HullDesc(HullFlags.TRIANGLES, vertices.size(), verts);
		HullResult result = new HullResult();
		
		HullLibrary hullLibrary = new HullLibrary();
		hullLibrary.createConvexHull(desc, result);
		
		ConvexHullShape shape = new ConvexHullShape(result.outputVertices);
		
		return shape;
	}
	
}
