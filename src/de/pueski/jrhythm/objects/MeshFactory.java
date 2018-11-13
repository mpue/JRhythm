package de.pueski.jrhythm.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.pueski.jrhythm.math.Vector2f;
import de.pueski.jrhythm.math.Vector3f;
import de.pueski.jrhythm.math.Vector4f;
import de.pueski.jrhythm.texture.TextureManager;

/**
 * The {@link MeshFactory} is used to create {@link Mesh} obejcts from 
 * a Wavefront object file (.obj). It also performs basic mesh precalulations,
 * like vertex normals, edge lists and face-plane calculations.
 * 
 * @author Matthias Pueski (16.05.2011)
 *
 */
public class MeshFactory {
	
	private static final Log log = LogFactory.getLog(MeshFactory.class);
	
	private static MeshFactory instance = null;
	
	protected MeshFactory() {
		log.info("initializing");
	}

	public static MeshFactory getInstance() {
		if (instance == null)
			instance = new MeshFactory();
		return instance;
	}
	
	/**
	 * Pares the material file of a wavefront object. (*.mtl)
	 * 
	 * @param file the file to parse
	 * 
	 * @return a hashpam containing the material definitions
	 */
	private HashMap<String,Material> parseMaterials(File file, boolean loadTexture) {
		
		HashMap<String, Material> materials = new HashMap<String, Material>();

		try {

			Material mat = null;			
			String line = null;
			
			FileReader materialIn = new FileReader(file);
			BufferedReader materialReader = new BufferedReader(materialIn);
			
			while ((line = materialReader.readLine()) != null) {
				
				String matTokens[] = line.split(" ");
				
				if (matTokens[0].equals("newmtl")) {					
					String name = matTokens[1]; 
					mat = new Material();
					mat.setName(name);
					materials.put(mat.getName(), mat);
				}
				
				if (matTokens[0].equals("map_Kd") && loadTexture) {
					mat.setTextureLocation(file.getParentFile().getName()+"/"+matTokens[1]);
					log.debug("adding texture :"+mat.getTextureLocation());
					TextureManager.getInstance().addTexture(mat.getTextureLocation());					
					mat.setTexture(TextureManager.getInstance().getTexture(mat.getTextureLocation()));
					mat.setTextured(true);
				}
				else if (matTokens[0].equals("Ns")) {			
					mat.setShininess(Float.valueOf(matTokens[1]));																
				}
				else if (matTokens[0].equals("Ka")) {
					// TODO : concern alpha
					mat.setAmbient(new Vector4f(Float.valueOf(matTokens[1]), Float.valueOf(matTokens[2]), Float.valueOf(matTokens[3]),1.0f));
				}							 
				else if (matTokens[0].equals("Kd")) {
					mat.setDiffuse(new Vector4f(Float.valueOf(matTokens[1]), Float.valueOf(matTokens[2]), Float.valueOf(matTokens[3]),1.0f));
				}
				else if (matTokens[0].equals("Ks")) {
					mat.setSpecular(new Vector4f(Float.valueOf(matTokens[1]), Float.valueOf(matTokens[2]), Float.valueOf(matTokens[3]),1.0f));
				}
				
			}
					
		
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("problem reading material file "+file.getAbsolutePath());
		}
		
		return materials;
	}
	
	public ArrayList<SceneNode> loadFromWavefrontObj(File file, boolean createTextures) {
		ArrayList<SceneNode> meshes = new ArrayList<SceneNode>();
		Mesh mesh = null;
		
		String line;
		
		HashMap<String, Material> materials = null;
		Material currentMaterial = null;
		
		try {
			
			FileReader in = new FileReader(file);
			BufferedReader reader = new BufferedReader(in);
			
			while ((line = reader.readLine()) != null) {
				
				String tokens[] = line.split(" ");
				
				if (tokens[0].equals("mtllib")) {											
					log.info("Found material file "+tokens[1]);
					materials = parseMaterials(new File(file.getParent(),tokens[1]),createTextures);
					log.debug(materials);
				}
				if (tokens[0].equals("usemtl")) {
					currentMaterial = materials.get(tokens[1]);
					// log.debug("Assigning material "+currentMaterial.getName()+" to mesh "+mesh.getName());
					mesh.getMaterials().put(tokens[1], currentMaterial);
				}
				
				/*
				 * We found a new object if the mesh is not null, we already found an object before
				 * and need to store it inside the meshes list;
				 */
				if (tokens[0].equals("o")) {
					mesh = new Mesh();					
					mesh.setPath(file.getParentFile().getAbsolutePath());
					mesh.setName(tokens[1]);
					log.info("Found object : " + mesh.getName());
					meshes.add(mesh);
				}
				if (tokens[0].equals("v")) {
					float x = Float.parseFloat(tokens[1]);
					float y = Float.parseFloat(tokens[2]);
					float z = Float.parseFloat(tokens[3]);
					
					Vertex v = new Vertex(x, y, z);
					v.setNormal(new Vector3f(0.0f, 0.0f, 0.0f));
					mesh.getVertices().add(v);
					// log.info("Adding vertex (" + mesh.getVertices().size() + ") : " + x + "," + y + "," + z);
				}
				if (tokens[0].equals("vn")) {
					
				}
				if (tokens[0].equals("vt")) {
					float x = Float.parseFloat(tokens[1]);
					float y = Float.parseFloat(tokens[2]);
					mesh.getUVCoordinates().add(new Vector2f(x, y));
					// log.debug("Adding UV coordinate(" + mesh.getUVCoordinates().size() + " : " + x + "," + y);
				}
				if (tokens[0].equals("f")) {
					
					Face f = new Face();
					
					if (tokens[1].contains("/")) {
						for (int i = 1; i < tokens.length; i++) {
							String[] subTokens = tokens[i].split("/");
							try {
								f.getVertexIndices().add(new Integer(subTokens[0]) - 1);							
								f.getUvVertexIndices().add(new Integer(subTokens[1]));
							}
							catch (NumberFormatException nfe) {
								continue;
							}
						}
					}
					else {
						for (int i = 1; i < tokens.length; i++) {
							f.getVertexIndices().add(new Integer(tokens[i]));
							f.getVertexIndices().set(i - 1, f.getVertexIndices().get(i - 1) - 1);
						}
					}
					
					Vector<Vertex> vertices = mesh.getVertices();
					
					normalizeFace(f, vertices);
					
					if (currentMaterial != null) {
						currentMaterial.getFaces().add(f);
						mesh.numFaces++;
					}
					
				}
				if (tokens[0].equals("s")) {
					if (tokens[1].trim().equalsIgnoreCase("off"))
						mesh.setDrawSmooth(false);
				}
				
			}
			
			in.close();			
			
		}
		catch (Exception e) {
			e.printStackTrace();
	
		}
		
		for (SceneNode n : meshes) {
			Mesh m = (Mesh)n;
			calculateVertexNormals(m);
			m.buildFaceCache();
			m.numEdges = m.buildEdges();
			m.calculatePlanes();
			if (Mesh.enableVBO) {
				m.prepareVBO();
			}
		}
		
		return meshes;
		
		
		
	}

	private void normalizeFace(Face f, Vector<Vertex> vertices) {
		// calculate the corresponding vector from the vertices of the face
		
		// first vector
		
		float x = vertices.get(f.getVertexIndices().get(1)).getX()
				- vertices.get(f.getVertexIndices().get(0)).getX();
		
		float y = vertices.get(f.getVertexIndices().get(1)).getY()
				- vertices.get(f.getVertexIndices().get(0)).getY();
		
		float z = vertices.get(f.getVertexIndices().get(1)).getZ()
				- vertices.get(f.getVertexIndices().get(0)).getZ();
		
		Vector3f v1 = new Vector3f(x, y, z);
		
		// second vector
		
		Vertex vertexB = vertices.get(f.getVertexIndices().get(0));
		
		if (f.getVertexIndices().size() > 2 && vertices.size() > f.getVertexIndices().get(2)) {
			
			Vertex vertexA = vertices.get(f.getVertexIndices().get(2));
			
			x = vertexA.getX() - vertexB.getX();						
			y = vertexA.getY() - vertexB.getY();	
			z = vertexA.getZ() - vertexB.getZ();	
			
			Vector3f v2 = new Vector3f(x, y, z);
			
			// calculate the perpendicular vector, normalize it and add it to the face
			
			f.setNormal(Vector3f.cross(v1, v2, null));
		}
	}
	
	/**
	 * Reads a 3D-model from a wavefront obj file
	 * 
	 * @param file the file to read from
	 * @param mode the mode to use while reading
	 */
	public ArrayList<SceneNode> getFromWavefrontObj(String location) {

		
		URL url = Thread.currentThread().getContextClassLoader().getResource(location);
		
		if (url == null)
			throw new IllegalArgumentException("Cannot find resource "+location);
		
		File file = new File(url.getFile());

		return loadFromWavefrontObj(file,true);
		
	}	
	
	/**
	 * calculate the vertex normals of each vertex of the model in order to get
	 * a smooth shaded model
	 */
	public void calculateVertexNormals(Mesh mesh) {

		Vector<Vertex> vertices = mesh.getVertices();
		
		for (String key : mesh.getMaterials().keySet()) {
			
			Material material = mesh.getMaterials().get(key);
		
			log.debug("calculate vertex normals for material "+material.getName());
			
			Vector<Face> faces = material.getFaces();
			
			log.debug("calculating normals for "+faces.size()+" faces.");
	
			/**
			 * For real smooth shading, we need to calculate the normal of each
			 * vertex. In order to achieve this, we iterate through all faces and
			 * fetch the corresponding vertices.
			 * 
			 */
	
			for (int i = 0; i < faces.size(); i++) {
	
				/**
				 * For each vertex we add all the normals of each face the vertex
				 * belongs to and finally normalize the sum.
				 */
	
				for (int j = 0; j < faces.get(i).getVertexIndices().size(); j++) {
	
					// Fetch the components of the vertex normal
	
					float x = vertices.get(faces.get(i).getVertexIndices().get(j)).getNormal().getX();
					float y = vertices.get(faces.get(i).getVertexIndices().get(j)).getNormal().getY();
					float z = vertices.get(faces.get(i).getVertexIndices().get(j)).getNormal().getZ();
	
					// add the current vertex normal
	
					x += faces.get(i).getNormal().getX();
					y += faces.get(i).getNormal().getY();
					z += faces.get(i).getNormal().getZ();
	
					// set the new normal values
	
					vertices.get(faces.get(i).getVertexIndices().get(j)).getNormal().setX(x);
					vertices.get(faces.get(i).getVertexIndices().get(j)).getNormal().setY(y);
					vertices.get(faces.get(i).getVertexIndices().get(j)).getNormal().setZ(z);
	
				}
			}
	
		}
		
		// Normalize all vertex normals.
		
		for (int k = 0; k < vertices.size(); k++) {
			vertices.get(k).setNormal(vertices.get(k).getNormal().normalise(null));
		}
	}
	
}
