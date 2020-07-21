package jasrc.j3d.opengl.chapter01;

//Core OpenGL
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glClearColor;

import jasrc.j3d.opengl.lib.OpenGLmain;

/**
 *
 *
 * @author Arnold, J.
 */
public class Chapter01_2 extends OpenGLmain {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		new Chapter01_2().run();
	}

	/**
	 *
	 */
	@Override
	protected void drawInit() {
		// Set the clear color
		glClearColor(1.0f, // red
				0.0f, // green
				0.0f, // blue
				0.0f // alpha
		);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
	}

	/**
	 *
	 */
	@Override
	public String getTitle() {
		return getClass().getCanonicalName() + ": Simple color";
	}

}
