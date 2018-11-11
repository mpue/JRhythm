package de.pueski.jrhythm.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;

public class VBOUtils {

	public static int createVBOID() {
		if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
			IntBuffer buffer = BufferUtils.createIntBuffer(1);
			ARBVertexBufferObject.glGenBuffersARB(buffer);
			return buffer.get(0);
		}
		return 0;
	}

	
	public static void bufferData(int id, FloatBuffer buffer) {
		if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
			ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, id);
			ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, buffer, ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
		}
	}

	public static void bufferElementData(int id, IntBuffer buffer) {
		if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
			ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, id);
			ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, buffer, ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
		}
	}
	
	public static void render(int mode,int vertexBufferID,int textureBufferID, int normalBufferID, int colourBufferID, int indexBufferID, int maxIndex, int indexBufferSize) {
		
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vertexBufferID);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0);

		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, textureBufferID);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);

		GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, normalBufferID);
		GL11.glNormalPointer(GL11.GL_FLOAT, 12,0);
		
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, colourBufferID);
		GL11.glColorPointer(3, GL11.GL_FLOAT, 12, 0);

		// GL11.glDrawArrays(GL11.GL_QUADS, 0, 24);
		
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, indexBufferID);
		GL12.glDrawRangeElements(mode, 0, maxIndex / 3, indexBufferSize, GL11.GL_UNSIGNED_INT, 0);
		
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		
	}
	
	public static void renderRange(int mode,int vertexBufferID,int textureBufferID, int normalBufferID, int colourBufferID, int indexBufferID, int start, int end, int count) {
		
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vertexBufferID);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0);

		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, textureBufferID);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, Float.BYTES * 2,0);

		GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, normalBufferID);
		GL11.glNormalPointer(GL11.GL_FLOAT, 12,0);
		
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, colourBufferID);
		GL11.glColorPointer(3, GL11.GL_FLOAT, 12, 0);

		// GL11.glDrawArrays(GL11.GL_QUADS, 0, 24);
		
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, indexBufferID);
		GL12.glDrawRangeElements(mode, start, end, count, GL11.GL_UNSIGNED_INT, 0);
		
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		
	}
}
