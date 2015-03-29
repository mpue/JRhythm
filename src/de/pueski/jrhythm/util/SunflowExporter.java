package de.pueski.jrhythm.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import org.apache.commons.io.FileUtils;

import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;

import de.pueski.jrhythm.objects.Face;
import de.pueski.jrhythm.objects.Material;
import de.pueski.jrhythm.objects.Mesh;
import de.pueski.jrhythm.objects.MeshFactory;
import de.pueski.jrhythm.objects.SceneNode;
import de.pueski.jrhythm.objects.Vertex;

public class SunflowExporter {

	private static JFrame frame;
	private static final Dimension dimension = new Dimension(1024,768);

	private static JMenuBar menubar;
	private static JMenu fileMenu;
	
	private static JTabbedPane tabpane;
	
	private static JPanel wavefrontPanel;
	private static JPanel sunflowPanel;
	
	private static JEditorPane wavefrontEditor;
	private static JEditorPane sunflowEditor;
	private static JEditorPane messagePane;
	
	public static void main(String[] args) {	
		
		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		}
		catch (Exception e) {
			System.out.println("Failed to set look and feel.");
		}
		
		frame = new JFrame("SunflowExporter");		
		frame.setSize(dimension);
		frame.setBounds(new Rectangle(dimension));
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		setupUI();
		setupMenu();
		
		frame.setVisible(true);
	}


	private static void setupMenu() {
		menubar = new JMenuBar();
		frame.setJMenuBar(menubar);
		
		fileMenu = new JMenu("File");		
		fileMenu.setMnemonic('F');
		
		fileMenu.add(new AbstractAction("Convert to sunflow") {			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fc = new JFileChooser(".");
				fc.setDialogTitle("Open Wavefront obj");
				
				int result = fc.showOpenDialog(frame);
				if (result == JFileChooser.APPROVE_OPTION) {
					if (fc.getSelectedFile() == null)
						return;
					File file = fc.getSelectedFile();					
					try {
						String data = FileUtils.readFileToString(file);
						wavefrontEditor.setText(data);
						wavefrontEditor.setCaretPosition(wavefrontEditor.getText().length());
					}
					catch (IOException e1) {
						JOptionPane.showMessageDialog(frame, "Error opening file");
					} 
					
					ArrayList<SceneNode> nodes = MeshFactory.getInstance().loadFromWavefrontObj(file,false);					
					sunflowEditor.setText(toSunflow(nodes));
					
				}
				else {
					return;
				}
				
				
				
			}

		});
		
		fileMenu.add(new AbstractAction("Exit") {			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);				
			}
		});
		
		
		menubar.add(fileMenu);
	}

	private static void log(String message) {
		messagePane.setText(messagePane.getText()+message+"\n");
	}
	
	private static String toSunflow(ArrayList<SceneNode> nodes) {

		StringBuffer data = new StringBuffer();
		
		for (SceneNode node : nodes) {
			
			if (node instanceof Mesh) {
				
				Mesh mesh = (Mesh) node;

				// header
				data.append("object {");
				data.append("\n");
				data.append("	type mesh");
				data.append("\n");
				data.append("	name "+mesh.getName());
				data.append("\n");
				data.append("	"+mesh.getVertices().size()+" "+mesh.getFaceCache().size());
				data.append("\n");
				
				log(mesh.getVertices().size()+" vertices.");
				log(mesh.getUVCoordinates().size()+" UV coordinates.");
				log(mesh.getFaceCache().size()+" faces.");
				
				for (Vertex v : mesh.getVertices() ) {
																	
					data.append("	v ");
					
					float x = v.getX();
					float y = v.getY();
					float z = v.getZ();
						
					data.append(x);
					data.append(" ");
					data.append(y);
					data.append(" ");
					data.append(z);
					data.append(" ");
					
					float nx = v.getNormal().getX();
					float ny = v.getNormal().getY();
					float nz = v.getNormal().getZ();

					data.append(nx);
					data.append(" ");
					data.append(ny);
					data.append(" ");
					data.append(nz);
					data.append(" ");
					
					data.append("0.0");
					data.append(" ");
					data.append("0.0");

					data.append("\n");
					
				}
				
				for (String key : mesh.getMaterials().keySet()) {
					
					Material material = mesh.getMaterials().get(key);
					
					for (Face face : material.getFaces()) {
						
						data.append("	t");
						data.append(" ");
						
						for (int j = 0; j < face.getVertexIndices().size(); j++) {
							data.append(face.getVertexIndices().get(j));
							data.append(" ");
						}
						
						data.append("\n");
					}
				}
				
				data.append("\n");
				data.append("}");
				data.append("\n");
				
			}
			
		}

		return data.toString();

	}

	private static void setupUI() {

		tabpane = new JTabbedPane();
		
		wavefrontPanel = new JPanel(new BorderLayout());
		wavefrontEditor = new JEditorPane();

		sunflowPanel = new JPanel(new BorderLayout());
		sunflowEditor = new JEditorPane();
		
		messagePane = new JEditorPane();
		
		wavefrontPanel.add(new JScrollPane(wavefrontEditor), BorderLayout.CENTER);
		sunflowPanel.add(new JScrollPane(sunflowEditor), BorderLayout.CENTER);
		
		tabpane.addTab("Wavefront Obj" , wavefrontPanel);
		tabpane.addTab("Sunflow", sunflowPanel);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(tabpane);
		
		JTabbedPane messageTabPane = new JTabbedPane();
		messageTabPane.addTab("Messages", new JScrollPane(messagePane));
		
		splitPane.setBottomComponent(messageTabPane);
		splitPane.setDividerLocation(600);
		
		frame.add(splitPane, BorderLayout.CENTER);
		
		
	}
	
}


