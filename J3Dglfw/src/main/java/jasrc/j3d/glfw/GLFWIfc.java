package jasrc.j3d.glfw;

/**
 * Interface to implement application-dependent methods used by the GLFW API
 * Wrapper
 *
 * @author Arnold, J.
 */
public interface GLFWIfc {
	/**
	 * @return String to set as window title
	 */
	String getTitle();

	/**
	 * Implementation should handle key evetns
	 *
	 * @param window
	 * @param key
	 * @param scancode
	 * @param action
	 * @param mods
	 */
	void keyEvent(long window, int key, int scancode, int action, int mods);

	/**
	 * Implementation should do the loop
	 */
	void loop();

	/**
	 * Implementation should act on windowSizeChanged
	 *
	 * @param window
	 * @param width
	 * @param height
	 */
	void windowSizeChanged(final long window, final int width, final int height);
}
