package jasrc.j3d.glfw;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;

/**
 * Abstract class wraps the GLFW API
 *
 * @author Arnold, J.
 */
public abstract class GLFWWrapper {
	/**
	 * The window handle
	 */
	private long window;
	/**
	 * The window width
	 */
	private int windowWidth = 1024;
	/**
	 * The window heigth
	 */
	private int windowHeight = 768;
	/**
	 * The framebuffer width
	 */
	private int framebufferWidth = windowWidth;
	/**
	 * The framebuffer heigth
	 */
	private int framebufferHeight = windowHeight;
	/**
	 * switch on when window/framebuffer size has changed
	 */
	private boolean hasResized = false;
	/**
	 * Implementation of GLFWIfc (keyEvent, passLoop ...)
	 */
	private final GLFWIfc glfwImpl;

	/**
	 *
	 * @param glfwImpl - Implementation of GLFWIfc
	 */
	protected GLFWWrapper(final GLFWIfc glfwImpl) {
		super();
		this.glfwImpl = glfwImpl;
	}

	/**
	 *
	 * @param glfwImpl     - Implementation of GLFWIfc
	 * @param windowWidth  - initial window width
	 * @param windowHeight - initial window height
	 */
	protected GLFWWrapper(final GLFWIfc glfwImpl, final int windowWidth, final int windowHeight) {
		this(glfwImpl);
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
	}

	/**
	 * at the end destroys used objects
	 */
	public void cleanup() {
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	/**
	 * at the beginning creates window
	 */
	public void createWindow() {
		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
		glfwWindowHints();

		// Create the window
		window = glfwCreateWindow(windowWidth, windowHeight, glfwImpl.getTitle(), NULL, NULL);
		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		glfwSetWindowSizeCallback(window, this::windowSizeChanged);
		glfwSetFramebufferSizeCallback(window, this::framebufferSizeChanged);

		// Setup a key callback. It will be called every time a key is pressed, repeated
		// or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			glfwImpl.keyEvent(window, key, scancode, action, mods);
		});

		// Get the thread stack and push a new frame
		try (var stack = stackPush()) {
			final var pWidth = stack.mallocInt(1);
			final var pHeight = stack.mallocInt(1);

			final var windowSizeCallback = (GLFWWindowSizeCallbackI) this::windowSizeChanged;

			if (windowSizeCallback != null) {
				glfwGetWindowSize(window, pWidth, pHeight);
				windowSizeCallback.invoke(window, pWidth.get(0), pHeight.get(0));
			}
		} // the stack frame is popped automatically

		// Get the resolution of the primary monitor
		final var vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		// Center the window
		glfwSetWindowPos(window, (vidmode.width() - windowWidth) / 2, (vidmode.height() - windowHeight) / 2);
	}

	/**
	 * called if framebuffer size changes
	 *
	 * @param window
	 * @param width
	 * @param height
	 */
	protected void framebufferSizeChanged(final long window, final int width, final int height) {
		framebufferWidth = width;
		framebufferHeight = height;
		hasResized = true;
	}

	/**
	 *
	 * @return framebuffers height
	 */
	public int getFramebufferHeight() {
		return framebufferHeight;
	}

	/**
	 *
	 * @return framebuffers width
	 */
	public int getFramebufferWidth() {
		return framebufferWidth;
	}

	/**
	 *
	 * @return implementation of GLFWIfc
	 */
	protected GLFWIfc getGlfwImpl() {
		return glfwImpl;
	}

	/**
	 *
	 * @return time since glfw startup
	 */
	public double getTime() {
		return glfwGetTime();
	}

	/**
	 *
	 * @return window id
	 */
	protected final long getWindow() {
		return window;
	}

	/**
	 *
	 * @return windows height
	 */
	public final int getWindowHeight() {
		return windowHeight;
	}

	/**
	 *
	 * @return windows width
	 */
	public final int getWindowWidth() {
		return windowWidth;
	}

	/**
	 * implement to set glfwWindowHints (including API and Version)
	 */
	public abstract void glfwWindowHints();

	/**
	 * initializes glfw
	 */
	public void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint().set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

	}

	/**
	 *
	 * @return if sizes has changed
	 */
	public final boolean isHasResized() {
		return hasResized;
	}

	/**
	 *
	 * @return if window is renderable
	 */
	private boolean isWindowRenderable() {
		return (getWindowWidth() > 0) && (getWindowHeight() > 0);
	}

	/**
	 * implements a simple and limited key event processor overwrite to extend
	 * application-depending functionality
	 *
	 * @param window
	 * @param key
	 * @param scancode
	 * @param action
	 * @param mods
	 */
	public void keyEvent(final long window, final int key, final int scancode, final int action, final int mods) {
		if (action == GLFW_RELEASE) {
			return;
		}

		switch (key) {
		case GLFW_KEY_ESCAPE:
			glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
			break;
		}
	}

	/**
	 * main loop handling glfw related stuff
	 */
	public void mainLoop() {
		// Make the window visible
		glfwShowWindow(window);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!glfwWindowShouldClose(window)) {

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();

			updateFramebufferSize();
			if (!isWindowRenderable()) {
				// nothing to display
				continue;
			}

			glfwImpl.loop();

			glfwSwapBuffers(window); // swap the color buffers
		}
	}

	/**
	 *
	 * @param hasResized
	 */
	protected final void setHasResized(final boolean hasResized) {
		this.hasResized = hasResized;
	}

	/**
	 *
	 */
	public final void setHasResizedFalse() {
		setHasResized(false);
	}

	/**
	 *
	 * @param windowHeight
	 */
	protected final void setWindowHeight(final int windowHeight) {
		this.windowHeight = windowHeight;
	}

	/**
	 *
	 * @param windowWidth
	 */
	protected final void setWindowWidth(final int windowWidth) {
		this.windowWidth = windowWidth;
	}

	/**
	 *
	 */
	private void updateFramebufferSize() {
		try (var stack = stackPush()) {
			final var framebufferWidth = stack.mallocInt(1);
			final var framebufferHeight = stack.mallocInt(1);
			glfwGetFramebufferSize(getWindow(), framebufferWidth, framebufferHeight);
			if ((framebufferWidth.get(0) != getFramebufferWidth())
					|| (framebufferHeight.get(0) != getFramebufferHeight())) {
				framebufferSizeChanged(0, framebufferWidth.get(0), framebufferHeight.get(0));
				windowSizeChanged(0, framebufferWidth.get(0), framebufferHeight.get(0));
			}
		}
	}

	/**
	 * called if framebuffer size changes
	 *
	 * @param window
	 * @param width
	 * @param height
	 */
	public void windowSizeChanged(final long window, final int width, final int height) {
		windowWidth = width;
		windowHeight = height;
		hasResized = true;
	}

}
