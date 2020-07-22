package jasrc.j3d.oglsl.chapter01;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11C.GL_COLOR;
import static org.lwjgl.opengl.GL30C.glClearBufferfv;

import jasrc.j3d.oglsl.lib.GLSLmain;

/**
 *
 *
 * @author Arnold, J.
 */
public class Chapter01_1 extends GLSLmain {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		new Chapter01_1().run();
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
