package jasrc.j3d.opengl.lib;

//Core OpenGL
import static org.lwjgl.opengl.GL11C.glViewport;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.GLUtil;

import jasrc.j3d.glfw.GLFWIfc;

/**
 * Abstract class implementing the GLFW API Wrapper Interface Subclasses can use
 * OpenGL up to Version 3.1
 *
 * @author Arnold, J.
 */
public abstract class OpenGLmain implements GLFWIfc {

	// The Container to handle GLFW
	/**
	 *
	 */
	private final GLFWOpenGL31 glfwContainer;
	// Callback GLDebugMessages
	/**
	 *
	 */
	private GLDebugMessageCallback debugMessageCallback = null;

	/**
	 *
	 */
	protected OpenGLmain() {
		super();
		glfwContainer = new GLFWOpenGL31(this);
	}

	/**
	 * @param windowWidth
	 * @param windowHeight
	 */
	protected OpenGLmain(final int windowWidth, final int windowHeight) {
		super();
		glfwContainer = new GLFWOpenGL31(this, windowWidth, windowHeight);
	}

	/**
	 *
	 */
	private void cleanup() {
		cleanupGL();

		GL.setCapabilities(null);

		// Free the window callbacks and destroy the window
		// Terminate GLFW and free the error callback
		glfwContainer.cleanup();

		debugMessageCallback.free();

		cleanupNonGL();
	}

	/**
	 *
	 */
	protected void cleanupGL() {
		// empty
	}

	/**
	 *
	 */
	protected void cleanupNonGL() {
		// empty
	}

	/**
	 *
	 */
	protected void drawInit() {
		// empty
	}

	/**
	 *
	 */
	protected void drawWorld() {
		// empty
	}

	/**
	 * @return
	 */
	protected double getTime() {
		return glfwContainer.getTime();
	}

	/**
	 * @return
	 */
	protected int getWindowHeight() {
		return glfwContainer.getWindowHeight();
	}

	/**
	 * @return
	 */
	protected int getWindowWidth() {
		return glfwContainer.getWindowWidth();
	}

	/**
	 *
	 */
	private void initGL() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		debugMessageCallback = (GLDebugMessageCallback) GLUtil.setupDebugMessageCallback();
	}

//	protected void framebufferSizeChanged(final long window, final int width, final int height) {
//		framebufferWidth = width;
//		framebufferHeight = height;
//		hasResized = true;
//	}
//
//	protected int getFramebufferHeight() {
//		return framebufferHeight;
//	}
//
//	protected int getFramebufferWidth() {
//		return framebufferWidth;
//	}

	/**
	 *
	 */
	@Override
	public void keyEvent(final long window, final int key, final int scancode, final int action, final int mods) {
		glfwContainer.keyEvent(window, key, scancode, action, mods);
	}

	/**
	 *
	 */
	@Override
	public final void loop() {
		glViewport(0, 0, glfwContainer.getWindowWidth(), glfwContainer.getWindowHeight());

		render(); // draw scene
	}

	/**
	 *
	 */
	protected void prepare() {
	}

	/**
	 *
	 */
	private void render() {
		if (glfwContainer.isHasResized()) {
			glfwContainer.setHasResizedFalse();
			resized();
		}

		drawInit();

		drawWorld();
	}

	/**
	 *
	 */
	protected void resized() {
	}

	/**
	 *
	 */
	public final void run() {
		try {
			Version.getVersion();

			glfwContainer.init();
			glfwContainer.createWindow();
			initGL();

			prepare();

			glfwContainer.mainLoop();
		} finally {
			try {
				cleanup();
			} catch (final Throwable th) {
				th.printStackTrace();
			}
		}

	}

	/**
	 *
	 */
	@Override
	public void windowSizeChanged(final long window, final int width, final int height) {
		glfwContainer.windowSizeChanged(window, width, height);
	}

}
