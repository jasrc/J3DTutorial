package jasrc.j3d.opengl.chapter01;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
//Core OpenGL
import static org.lwjgl.opengl.GL11C.GL_COLOR;
import static org.lwjgl.opengl.GL30C.glClearBufferfv;

import jasrc.j3d.opengl.lib.OpenGLmain;

/**
 *
 *
 * @author Arnold, J.
 */
public class Chapter01_3 extends OpenGLmain {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		new Chapter01_3().run();
	}

	/**
	 *
	 */
	double currentTime;

	/**
	 *
	 */
	@Override
	protected void drawInit() {
		currentTime = glfwGetTime();

		// Set the clear color
		final var color = new float[] { ((float) Math.sin(currentTime) * 0.5f) + 0.5f, // red
				((float) Math.cos(currentTime) * 0.5f) + 0.5f, // green
				0.0f, // blue
				1.0f // alpha
		};
		glClearBufferfv(GL_COLOR, 0, color);
	}

	/**
	 *
	 */
	@Override
	public String getTitle() {
		return getClass().getCanonicalName() + ": Dynamic color";
	}
}
