package jasrc.j3d.oglsl.lib;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;

import jasrc.j3d.glfw.GLFWIfc;
import jasrc.j3d.glfw.GLFWWrapper;

/**
 *
 *
 * @author Arnold, J.
 */
public class GLFWOpenGL46C extends GLFWWrapper {

	/**
	 * @param glfwImpl
	 */
	protected GLFWOpenGL46C(final GLFWIfc glfwImpl) {
		super(glfwImpl);
	}

	/**
	 * @param glfwImpl
	 * @param windowWidth
	 * @param windowHeight
	 */
	protected GLFWOpenGL46C(final GLFWIfc glfwImpl, final int windowWidth, final int windowHeight) {
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
		// latest OpenGL Version yet is 4.6
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
	}
}
