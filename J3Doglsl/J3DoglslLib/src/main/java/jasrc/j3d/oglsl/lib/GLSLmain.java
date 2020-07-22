package jasrc.j3d.oglsl.lib;

import static org.lwjgl.opengl.GL11.glViewport;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.GLUtil;

import jasrc.j3d.glfw.GLFWIfc;
import jasrc.j3d.glfw.GLFWWrapper;

/**
 *
 *
 * @author Arnold, J.
 */
public abstract class GLSLmain implements GLFWIfc {

	// The Container to handle GLFW
	/**
	 *
	 */
	private final GLFWWrapper glfwContainer;
	// Callback GLDebugMessages
	/**
	 *
	 */
	private GLDebugMessageCallback debugMessageCallback = null;

	/**
	 *
	 */
	protected GLSLmain() {
		super();
		glfwContainer = new GLFWOpenGL46C(this);
	}

	/**
	 * @param windowWidth
	 * @param windowHeight
	 */
	protected GLSLmain(final int windowWidth, final int windowHeight) {
		super();
		glfwContainer = new GLFWOpenGL46C(this, windowWidth, windowHeight);
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
