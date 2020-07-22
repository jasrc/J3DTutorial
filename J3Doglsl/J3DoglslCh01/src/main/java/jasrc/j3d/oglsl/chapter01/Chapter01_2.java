package jasrc.j3d.oglsl.chapter01;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11C.GL_COLOR;
import static org.lwjgl.opengl.GL11C.GL_POINTS;
import static org.lwjgl.opengl.GL11C.glDrawArrays;
import static org.lwjgl.opengl.GL11C.glPointSize;
import static org.lwjgl.opengl.GL20C.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20C.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20C.glAttachShader;
import static org.lwjgl.opengl.GL20C.glCompileShader;
import static org.lwjgl.opengl.GL20C.glCreateProgram;
import static org.lwjgl.opengl.GL20C.glCreateShader;
import static org.lwjgl.opengl.GL20C.glDeleteProgram;
import static org.lwjgl.opengl.GL20C.glDeleteShader;
import static org.lwjgl.opengl.GL20C.glLinkProgram;
import static org.lwjgl.opengl.GL20C.glShaderSource;
import static org.lwjgl.opengl.GL20C.glUseProgram;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glClearBufferfv;
import static org.lwjgl.opengl.GL30C.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL45C.glCreateVertexArrays;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import jasrc.j3d.oglsl.lib.GLSLmain;

/**
 *
 *
 * @author Arnold, J.
 */
public class Chapter01_2 extends GLSLmain {

	// Source code for vertex shader
	/**
	 *
	 */
	public final static String vertex_shader_source = "#version 450\r\n" //
			+ "\r\n" //
			+ "void main()\r\n" //
			+ "{\r\n" //
			+ "    gl_Position = vec4(0.0, 0.0, 0.0, 1.0);\r\n" //
			+ "}\r\n";

	// Source code for fragment shader
	/**
	 *
	 */
	public final static String fragment_shader_source = "#version 450\r\n" //
			+ "\r\n" //
			+ "layout(location = 0) out vec4 color;\r\n" //
			+ "\r\n" //
			+ "void main()\r\n" //
			+ "{\r\n" //
			+ "    color = vec4(1.0);\r\n" //
			+ "}\r\n";

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		new Chapter01_2().run();
	}

	/**
	 *
	 */
	int rendering_program;
	/**
	 *
	 */
	IntBuffer vertex_array_object = BufferUtils.createIntBuffer(1);
	/**
	 *
	 */
	double currentTime;

	/**
	 *
	 */
	@Override
	protected void cleanupGL() {
		glDeleteProgram(rendering_program);
		glDeleteVertexArrays(vertex_array_object);
	}

	/**
	 * @return
	 */
	protected int compile_shaders() {
		int vertex_shader;
		int fragment_shader;
		int program;

		// Create and compile vertex shader
		vertex_shader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertex_shader, vertex_shader_source);
		glCompileShader(vertex_shader);

		// Create and compile fragment shader
		fragment_shader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragment_shader, fragment_shader_source);
		glCompileShader(fragment_shader);

		// Create program, attach shaders to it, and link it
		program = glCreateProgram();
		glAttachShader(program, vertex_shader);
		glAttachShader(program, fragment_shader);
		glLinkProgram(program);

		// Delete the shaders as the program has them now
		glDeleteShader(vertex_shader);
		glDeleteShader(fragment_shader);

		return program;
	}

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
	protected void drawWorld() {
		// Use the program object we created earlier for rendering
		glUseProgram(rendering_program);

		// Draw one point
		glDrawArrays(GL_POINTS, 0, 1);

		glPointSize((float) Math.abs(Math.sin(currentTime)) * 64.0f);
	}

	/**
	 *
	 */
	@Override
	public String getTitle() {
		return getClass().getCanonicalName() + ": Point";
	}

	/**
	 *
	 */
	@Override
	protected void prepare() {
		rendering_program = compile_shaders();
		glCreateVertexArrays(vertex_array_object);
		glBindVertexArray(vertex_array_object.get(0));
	}
}
