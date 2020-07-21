package jasrc.j3d.vulkan.lib;

import static org.lwjgl.glfw.GLFW.GLFW_CLIENT_API;
import static org.lwjgl.glfw.GLFW.GLFW_NO_API;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;

import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkInstance;

import jasrc.j3d.glfw.GLFWIfc;
import jasrc.j3d.glfw.GLFWWrapper;

/**
 *
 *
 * @author Arnold, J.
 */
public class GLFWVulkan extends GLFWWrapper {

	/**
	 *
	 */
	PointerBuffer requiredExtensions;

	/**
	 * @param glfwImpl
	 */
	protected GLFWVulkan(final GLFWIfc glfwImpl) {
		super(glfwImpl);
	}

	/**
	 * @param glfwImpl
	 * @param windowWidth
	 * @param windowHeight
	 */
	protected GLFWVulkan(final GLFWIfc glfwImpl, final int windowWidth, final int windowHeight) {
		super(glfwImpl, windowWidth, windowHeight);
	}

	/**
	 * @param instance
	 * @param allocator
	 * @param surface
	 * @return
	 */
	protected int createWindowSurface(final VkInstance instance, final VkAllocationCallbacks allocator,
			final LongBuffer surface) {
		return glfwCreateWindowSurface(instance, getWindow(), allocator, surface);
	}

	/**
	 * @return
	 */
	protected PointerBuffer getRequiredExtensions() {
		return requiredExtensions;
	}

	/**
	 *
	 */
	@Override
	public void glfwWindowHints() {
		glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
	}

	/**
	 *
	 */
	@Override
	public void init() {
		super.init();

		if (!glfwVulkanSupported()) {
			throw new AssertionError("GLFW failed to find the Vulkan loader");
		}
		requiredExtensions = glfwGetRequiredInstanceExtensions();
		if (requiredExtensions == null) {
			throw new AssertionError("Failed to find list of required Vulkan extensions");
		}
	}

	/**
	 *
	 */
	@Override
	public void mainLoop() {
		// Make the window visible
		glfwShowWindow(getWindow());

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!glfwWindowShouldClose(getWindow())) {

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();

			getGlfwImpl().loop();
		}
	}

}
