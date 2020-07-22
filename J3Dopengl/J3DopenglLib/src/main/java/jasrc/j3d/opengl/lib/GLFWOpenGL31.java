package jasrc.j3d.opengl.lib;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;

import jasrc.j3d.glfw.GLFWIfc;
import jasrc.j3d.glfw.GLFWWrapper;

/**
 * Class implementing extending the GLFW API Wrapper to use OpenGL up to Version
 * 3.1
 *
 * @author Arnold, J.
 */
public class GLFWOpenGL31 extends GLFWWrapper {

	/**
	 * @param glfwImpl
	 */
	protected GLFWOpenGL31(final GLFWIfc glfwImpl) {
		super(glfwImpl);
	}

	/**
	 * @param glfwImpl
	 * @param windowWidth
	 * @param windowHeight
	 */
	protected GLFWOpenGL31(final GLFWIfc glfwImpl, final int windowWidth, final int windowHeight) {
		super(glfwImpl, windowWidth, windowHeight);
	}

	/**
	 *
	 */
	@Override
	public void createWindow() {
		super.createWindow();

		// Make the OpenGL context current
		glfwMakeContextCurrent(getWindow());

		// Enable v-sync
		glfwSwapInterval(1);
	}

	/**
	 *
	 */
	@Override
	public void glfwWindowHints() {
		// latest OpenGL Version supported is 3.1
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);

	}
}
