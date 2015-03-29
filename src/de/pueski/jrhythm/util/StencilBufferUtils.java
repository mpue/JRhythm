package de.pueski.jrhythm.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

public class StencilBufferUtils {

	public static final int SIZE_FLOAT = 4;
	public static final int SIZE_BYTE = 1;
	
	public static ByteBuffer 	tmpByte = allocBytes(SIZE_BYTE);     // temp var used by getStencilValue()

	
	/**
	 * clear the stencil buffer
	 */
	public static void clearMask() {
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
	}


	/**
	 *  Begin creating a mask.  This function turns off the color and depth buffers
	 *  so all subsequent drawing will go only into the stencil buffer.
	 *  To use:
	 *          beginMask(1);
	 *          renderModel();  // draw some geometry
	 *          endMask();
	 */
	public static void beginMask(int maskvalue) {
		// turn off writing to the color buffer and depth buffer
		GL11.glColorMask(false, false, false, false);
		GL11.glDepthMask(false);

		// enable stencil buffer
		GL11.glEnable(GL11.GL_STENCIL_TEST);

		// set the stencil test to ALWAYS pass
		GL11.glStencilFunc(GL11.GL_ALWAYS, maskvalue, 0xFFFFFFFF);
		// REPLACE the stencil buffer value with maskvalue whereever we draw
		GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
	}

	/**
	 *  End the mask.  Freeze the stencil buffer and activate the color and depth buffers.
	 */
	public static void endMask() {
		// don't let future drawing modify the contents of the stencil buffer
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

		// turn the color and depth buffers back on
		GL11.glColorMask(true, true, true, true);
		GL11.glDepthMask(true);
	}

	/**
	 *  Restrict rendering to the masked area.
	 *  To use:
	 *          GLStencil.beginMask(1);
	 *          renderModel();
	 *          GLStencil.endMask();
	 */
	public static void activateMask(int maskvalue) {
		// enable stencil buffer
		GL11.glEnable(GL11.GL_STENCIL_TEST);

		// until stencil test is disabled, only write to areas where the
		// stencil buffer equals the mask value
		GL11.glStencilFunc(GL11.GL_EQUAL, maskvalue, 0xFFFFFFFF);
	}

	/**
	 *  turn off the stencil test so stencil has no further affect on rendering.
	 */
	public static void disableMask() {
		GL11.glDisable(GL11.GL_STENCIL_TEST);
	}

	/**
	 * Return the stencil buffer value at the given screen position.
	 */
	public static int getMaskValue(int x, int y)
	{
		tmpByte.clear();
		// read the stencil value at the given position, as an unsigned byte, store it in tmpByte
		GL11.glReadPixels(x, y, 1, 1, GL11.GL_STENCIL_INDEX, GL11.GL_UNSIGNED_BYTE, tmpByte);
		return (int) tmpByte.get(0);
	}

    public static FloatBuffer allocFloats(int howmany) {
    	return ByteBuffer.allocateDirect(howmany * SIZE_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }
    
    public static FloatBuffer allocFloats(float[] floatarray) {
    	FloatBuffer fb = ByteBuffer.allocateDirect(floatarray.length * SIZE_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
    	fb.put(floatarray).flip();
    	return fb;
    }
	
    public static ByteBuffer allocBytes(int howmany) {
    	return ByteBuffer.allocateDirect(howmany * SIZE_BYTE).order(ByteOrder.nativeOrder());
    }   
	
}
