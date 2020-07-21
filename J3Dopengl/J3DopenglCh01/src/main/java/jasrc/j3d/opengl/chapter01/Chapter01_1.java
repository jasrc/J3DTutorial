package jasrc.j3d.opengl.chapter01;

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
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glClearColor;
//Core OpenGL
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.GLUtil;

/**
 *
 *
 * @author Arnold, J.
 */
public class Chapter01_1 {

	static {
		System.setProperty("org.lwjgl.util.Debug", "true");
		System.setProperty("org.lwjgl.util.DebugAllocator", "true");
		System.setProperty("org.lwjgl.util.DebugLoader", "true");
		System.setProperty("org.lwjgl.util.DebugStack", "true");
		System.setProperty("org.lwjgl.util.NoChecks", "false");
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		new Chapter01_1().run();
	}

	// The window handle
	/**
	 *
	 */
	private long window;
	// The window width
	/**
	 *
	 */
	private int windowWidth = 1024;
	// The window heigth
	/**
	 *
	 */
	private int windowHeight = 768;
	// Callback GLDebugMessages
	/**
	 *
	 */
	private GLDebugMessageCallback debugMessageCallback = null;

	/**
	 *
	 */
	private void cleanup() {
		GL.setCapabilities(null);

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		debugMessageCallback.free();
	}

	/**
	 *
	 */
	private void createWindow() {
		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(windowWidth, windowHeight, "Hello World!", NULL, NULL);
		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		glfwSetWindowSizeCallback(window, this::windowSizeChanged);

		// Setup a key callback. It will be called every time a key is pressed, repeated
		// or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (action == GLFW_RELEASE) {
				return;
			}

			switch (key) {
			case GLFW_KEY_ESCAPE:
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
				break;
			}
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

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);

		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);

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
	private void drawInit() {
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
	private void init() {
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
	 */
	private void loop() {
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!glfwWindowShouldClose(window)) {

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
			glViewport(0, 0, windowWidth, windowHeight);

			render(); // draw scene

			glfwSwapBuffers(window); // swap the color buffers
		}
	}

	/**
	 *
	 */
	private void render() {
		drawInit();
	}

	/**
	 *
	 */
	public void run() {
		try {
			System.out.println("Hello LWJGL " + Version.getVersion() + " World!");

			init();
			createWindow();

			loop();
		} finally {
			try {
				cleanup();
			} catch (final Throwable th) {
				th.printStackTrace();
			}
		}

	}

	/**
	 * @param window
	 * @param width
	 * @param height
	 */
	private void windowSizeChanged(final long window, final int width, final int height) {
		windowWidth = width;
		windowHeight = height;
	}

}
