package de.pueski.jrhythm.util;

import de.pueski.jrhythm.math.Vector3f;
import de.pueski.jrhythm.objects.Box;
import de.pueski.jrhythm.objects.BoxMesh;
import de.pueski.jrhythm.test.PerlinGen;

/**
 * @author Matthias Pueski (16.05.2011)
 *
 */
public class ShapeUtil {
		
//	public static Grid createSineGrid(float height) {
//		Random randy = new Random();
//		float y = 2.0f;
//		float y2 = 0.0f;
//		float angle = 0.0f;
//		float angle2 = 0.0f;
//		float offset = 0.0f;
//		
//
//		Grid grid = new Grid(250,250);
//		grid.setName("grid");
//		
//		for (float x = 0.0f; x < 500; x+=2.0f) {
//			
//			y2 =  (float)Math.sin(Math.toRadians(angle2))*height + offset;
//			
//			for (float z = 0.0f; z < 500; z+=2.0f) {
//				
//				y =  (float)Math.sin(Math.toRadians(angle)) * height + offset + y2;
//					
//				grid.getGridData()[(int)x/2][(int)z/2] = new Vector3f(x,y,z);				
//				angle += 360.0f/50.0f;				
//				height -= randy.nextFloat()/1000; 
//				
//			}
// 			angle2 += 360.0f/50f;
//			
//		}
//		
//		return grid;
//	}
//		
//	public static Grid createRandomGrid(int length,float height,int width) {
//
//		Dirty tex = new Dirty(new ComplexMarble(new Random().nextFloat()*2));		
//		
//		TextureImage ti = new TextureImage(length,width);
//		ti.setTexture(tex);		
//		ti.renderAndWait();
//		
//		BufferedImage image = ti.getImage();
//		
//		image = ImageUtils.convertToGrayscale(image);
//
//		Grid grid = new Grid(length,width);
//		grid.setName("grid");
//		
//		float y = 0.0f;
//		
//		for (float x = 0.0f; x < length*2; x+=2.0f) {		
//			for (float z = 0.0f; z < width*2; z+=2.0f) {				
//				y =  (float)Math.abs(image.getRGB((int)x/2, (int)z/2)/(float)300000);					
//				grid.getGridData()[(int)x/2][(int)z/2] = new Vector3f(x,y,z);								
//			}
//		}
//		
//		return grid;
//	}
//	
//	public static Grid createGridFromImage(float height, BufferedImage image) {
//		
//		image = ImageUtils.convertToGrayscale(image);
//
//		Grid grid = new Grid(image.getWidth(),image.getHeight());
//		grid.setName("grid");
//		
//		float y = 0.0f;
//		
//		for (int x = 0; x < image.getWidth(); x++) {		
//			for (int z = 0; z < image.getHeight(); z++) {								
//				y = (float)image.getRGB(x,z)/300000.0f*height;
//				grid.getGridData()[x][z] = new Vector3f(x,y,z);								
//			}
//		}
//		
//		return grid;
//	}
//	
//	/**
//	 * Places random cubes on the surface depending on height and angle
//	 * 
//	 * @return
//	 */
//	public static int placeRandomMeshOnSurface(SceneNode rootNode,Mesh ground, String meshFile) {
//		
//		Random r = new Random();		
//		int count = 0;		
//		
//		for (String key : ground.getMaterials().keySet()) {
//			Material material = ground.getMaterials().get(key);
//			
//			for (Face font : material.getFaces()) {
//
//				Vertex v1 = ground.getVertices().get(font.getVertexIndices().get(0));
//				Vertex v2 = ground.getVertices().get(font.getVertexIndices().get(1));
//				Vertex v3 = ground.getVertices().get(font.getVertexIndices().get(2));
//	
//				// create an averaged vector from the first three vertices of the face
//				
//				float x = (v1.getX() + v2.getX() + v3.getX()) / 3;
//				float y = (v1.getY() + v2.getY() + v3.getY()) / 3;
//				float z = (v1.getZ() + v2.getZ() + v3.getZ()) / 3;
//	
//				// construct two vectors which form the edge of the face
//				
//				Vector3f fv1 = Vector3f.sub(ground.getVertices().get(font.getVertexIndices().get(0)),ground.getVertices().get(font.getVertexIndices().get(1)), null);
//				Vector3f fv2 = Vector3f.sub(ground.getVertices().get(font.getVertexIndices().get(1)),ground.getVertices().get(font.getVertexIndices().get(2)), null);				
//				
//				Vector3f normal = new Vector3f(0.0f, 1.0f, 0.0f);				
//				
//				// now determine the angles of both x and z direction
//				
//				double cos1 = (fv1.x*normal.x+fv1.y*normal.y+fv1.z*normal.z) / (Math.sqrt(Math.pow(fv1.x,2) + Math.pow(fv1.y,2) + Math.pow(fv1.z,2)) * 
//																			   Math.sqrt(Math.pow(normal.x,2) + Math.pow(normal.y,2) + Math.pow(normal.z,2)));
//	
//				double cos2 = (fv2.x*normal.x+fv2.y*normal.y+fv2.z*normal.z) / (Math.sqrt(Math.pow(fv2.x,2) + Math.pow(fv2.y,2) + Math.pow(fv2.z,2)) * 
//						   Math.sqrt(Math.pow(normal.x,2) + Math.pow(normal.y,2) + Math.pow(normal.z,2)));
//	
//				
//				double angle1 = Math.toDegrees(Math.acos(cos1));
//				double angle2 = Math.toDegrees(Math.acos(cos2));
//									
//				if (r.nextFloat() > 0.95) {
//					Vector3f pos = new Vector3f(x,y,z);
//					// place boxes depending on slope and height
//					 if (Math.abs(90-angle1) < 10 && Math.abs(90-angle2) < 10 && pos.getY() < 50) {
//						pos.setY(pos.getY()+1.0f);					
//						
//						Mesh mesh = (Mesh) MeshFactory.getInstance().getFromWavefrontObj(meshFile).get(0);
//						mesh.setPosition(new Vector3f(x,y,z));
//	
//						// some random rotation for the box
//						mesh.setYrot(r.nextFloat()*100);
//	 					// if desired let boxes follow slope of the face (buggy!)
//	//					b.setZrot((float)angle2);
//	//					b.setXrot((float)angle1);
//						rootNode.addChild(mesh);
//						count++;
//					 }
//				}
//			}
//				
//		}
//		return count;
//	}
//	
//	/**
//	 * Places random cubes on the surface depending on height and angle
//	 * 
//	 * @return
//	 */
//	public static int placeRandomCubesOnSurface(SceneNode rootNode,Mesh ground) {
//		Random r = new Random();		
//		int count = 0;		
//
//		for (String key : ground.getMaterials().keySet()) {
//			Material material = ground.getMaterials().get(key);
//			
//			for (Face font : material.getFaces()) {
//
//				Vertex v1 = ground.getVertices().get(font.getVertexIndices().get(0));
//				Vertex v2 = ground.getVertices().get(font.getVertexIndices().get(1));
//				Vertex v3 = ground.getVertices().get(font.getVertexIndices().get(2));
//	
//				// create an averaged vector from the first three vertices of the face
//				
//				float x = (v1.getX() + v2.getX() + v3.getX()) / 3;
//				float y = (v1.getY() + v2.getY() + v3.getY()) / 3;
//				float z = (v1.getZ() + v2.getZ() + v3.getZ()) / 3;
//	
//				// construct two vectors which form the edge of the face
//				
//				Vector3f fv1 = Vector3f.sub(ground.getVertices().get(font.getVertexIndices().get(0)),ground.getVertices().get(font.getVertexIndices().get(1)), null);
//				Vector3f fv2 = Vector3f.sub(ground.getVertices().get(font.getVertexIndices().get(1)),ground.getVertices().get(font.getVertexIndices().get(2)), null);				
//				
//				Vector3f normal = new Vector3f(0.0f, 1.0f, 0.0f);				
//				
//				// now determine the angles of both x and z direction
//				
//				double cos1 = (fv1.x*normal.x+fv1.y*normal.y+fv1.z*normal.z) / (Math.sqrt(Math.pow(fv1.x,2) + Math.pow(fv1.y,2) + Math.pow(fv1.z,2)) * 
//																			   Math.sqrt(Math.pow(normal.x,2) + Math.pow(normal.y,2) + Math.pow(normal.z,2)));
//	
//				double cos2 = (fv2.x*normal.x+fv2.y*normal.y+fv2.z*normal.z) / (Math.sqrt(Math.pow(fv2.x,2) + Math.pow(fv2.y,2) + Math.pow(fv2.z,2)) * 
//						   Math.sqrt(Math.pow(normal.x,2) + Math.pow(normal.y,2) + Math.pow(normal.z,2)));
//	
//				
//				double angle1 = Math.toDegrees(Math.acos(cos1));
//				double angle2 = Math.toDegrees(Math.acos(cos2));
//									
//				if (r.nextFloat() > 0.95) {
//					Vector3f pos = new Vector3f(x,y,z);
//					// place boxes depending on slope and height
//					 if (Math.abs(90-angle1) < 10 && Math.abs(90-angle2) < 10 && pos.getY() < 50) {
//						pos.setY(pos.getY()+1.0f);
//						Box b = new Box(pos, "box_"+count);
//						// some random rotation for the box
//	 					b.setYrot(r.nextFloat()*100);
//	 					// if desired let boxes follow slope of the face (buggy!)
//	//					b.setZrot((float)angle2);
//	//					b.setXrot((float)angle1);
//						rootNode.addChild(b);
//						count++;
//					 }
//				}
//			}
//			
//		}
//		return count;
//	}
	
	public static BoxMesh createPerlinGeometry(int size, double scale, Vector3f location) {
		int i=0;
		
		boolean[][][] grid = new boolean[size][size][size];
		
		double[][][] perlin = PerlinGen.makePerlin(size,scale);
		
		for (int x=0; x < size;x++) {
			for (int y=0; y < size;y++) {
				for (int z=0; z < size;z++) {
					if (perlin[x][y][z] > 0.1) {
						grid[x][y][z] = true;
					}
				}				
			}			
		}
		
		BoxMesh boxMesh = new BoxMesh(location);
		
		for (int x = 0; x < size * 2; x += 2) {
			for (int y = 0; y < size * 2; y += 2) {
				for (int z = 0; z < size * 2; z += 2) {
					if (grid[x / 2][y / 2][z / 2]) {
						Box box = new Box(new Vector3f(x, y, z), "box_" + i++);
						box.attachTexture("texgen/marble.png");
						boxMesh.addBox(box);						
					}

				}

			}
		}
		return boxMesh;
	}	
	
}
