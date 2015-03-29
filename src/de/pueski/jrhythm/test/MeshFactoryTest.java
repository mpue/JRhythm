package de.pueski.jrhythm.test;

import java.util.ArrayList;

import de.pueski.jrhythm.objects.Face;
import de.pueski.jrhythm.objects.Material;
import de.pueski.jrhythm.objects.Mesh;
import de.pueski.jrhythm.objects.MeshFactory;
import de.pueski.jrhythm.objects.SceneNode;

public class MeshFactoryTest {

	public static void main(String[] args) {
		
		
		ArrayList<SceneNode> nodes = MeshFactory.getInstance().getFromWavefrontObj("objects/Chair.obj"); 
		
		for (SceneNode node : nodes) {
			
			System.out.println("Found mesh :"+ node.getName());
			
			Mesh mesh = (Mesh) node;
			
			System.out.println(mesh.getName()+" has "+mesh.getVertices().size()+" vertices.");
			
			for (String key : mesh.getMaterials().keySet()) {
				
				Material material = mesh.getMaterials().get(key);
				System.out.println("material "+material.getName()+" has "+material.getFaces().size()+" faces.");
				System.out.println("max vertex index "+getMaxvertexIndex(material));
			}
			
		}
		
	}
	
	private static int getMaxvertexIndex(Material material) {
		
		int maxIndex = 0;
		
		for (Face f : material.getFaces()) {
			
			for (Integer index : f.getVertexIndices()) {
				if (index > maxIndex) {
					maxIndex = index;
				}
			}
			
		}
		
		return maxIndex;
		
	}
	
}
