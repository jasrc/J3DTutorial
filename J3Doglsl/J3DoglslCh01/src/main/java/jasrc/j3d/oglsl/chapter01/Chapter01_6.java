package jasrc.j3d.oglsl.chapter01;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11C.GL_BLEND;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_LINEAR;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_RED;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_TRUE;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glBlendFunc;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.opengl.GL11C.glDisable;
import static org.lwjgl.opengl.GL11C.glDrawArrays;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL11C.glGenTextures;
import static org.lwjgl.opengl.GL11C.glTexImage2D;
import static org.lwjgl.opengl.GL11C.glTexParameteri;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL20C.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20C.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20C.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20C.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20C.glAttachShader;
import static org.lwjgl.opengl.GL20C.glBindAttribLocation;
import static org.lwjgl.opengl.GL20C.glCompileShader;
import static org.lwjgl.opengl.GL20C.glCreateProgram;
import static org.lwjgl.opengl.GL20C.glCreateShader;
import static org.lwjgl.opengl.GL20C.glDeleteProgram;
import static org.lwjgl.opengl.GL20C.glDeleteShader;
import static org.lwjgl.opengl.GL20C.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20C.glGetShaderi;
import static org.lwjgl.opengl.GL20C.glLinkProgram;
import static org.lwjgl.opengl.GL20C.glShaderSource;
import static org.lwjgl.opengl.GL20C.glUseProgram;
import static org.lwjgl.opengl.GL20C.glVertexAttrib3fv;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.glBindBufferBase;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;
import static org.lwjgl.opengl.GL31C.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL44C.GL_DYNAMIC_STORAGE_BIT;
import static org.lwjgl.opengl.GL45C.glCreateBuffers;
import static org.lwjgl.opengl.GL45C.glNamedBufferStorage;
import static org.lwjgl.opengl.GL45C.glNamedBufferSubData;
import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;
import static org.lwjgl.stb.STBTruetype.stbtt_PackBegin;
import static org.lwjgl.stb.STBTruetype.stbtt_PackEnd;
import static org.lwjgl.stb.STBTruetype.stbtt_PackFontRange;
import static org.lwjgl.stb.STBTruetype.stbtt_PackSetOversampling;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;

import jasrc.j3d.oglsl.lib.GLSLmain;
import jasrc.j3d.resources.ResourceUtil;

/**
 *
 *
 * @author Arnold, J.
 */
public class Chapter01_6 extends GLSLmain {

	// Source code for vertex shader
	/**
	 *
	 */
	public final static String vertex_shader_source = "#version 450\r\n" //
			+ "\r\n" //
			+ "layout(binding = 0, std140) uniform UNI_FORM\r\n" //
			+ "{\r\n" //
			+ "    mat4 orthoMatrix;\r\n" //
			+ "} vs_uni_form;\r\n" //
			+ "\r\n" //
			+ "layout(location = 0) in vec2 positions;\r\n" //
			+ "layout(location = 0) out vec2 outVertTextureCoordinates;\r\n" //
			+ "layout(location = 1) in vec2 textureCoordinates;\r\n" //
			+ "layout(location = 1) out vec3 outColor;\r\n" //
			+ "layout(location = 2) in vec3 color;\r\n" //
			+ "\r\n" //
			+ "void main()\r\n" //
			+ "{\r\n" //
			+ "    gl_Position = vs_uni_form.orthoMatrix * vec4(positions, 0.0, 1.0);\r\n" //
			+ "    outVertTextureCoordinates = textureCoordinates;\r\n" //
			+ "    outColor = color;\r\n" //
			+ "}\r\n";

	// Source code for fragment shader
	/**
	 *
	 */
	public final static String fragment_shader_source = "#version 450\r\n" //
			+ "\r\n" //
			+ "layout(binding = 0) uniform sampler2D fontAtlas;\r\n" //
			+ "\r\n" //
			+ "layout(location = 0) out vec4 finalColor;\r\n" //
			+ "layout(location = 1) in vec3 outColor;\r\n" //
			+ "layout(location = 0) in vec2 outVertTextureCoordinates;\r\n" //
			+ "\r\n" //
			+ "void main()\r\n" //
			+ "{\r\n" //
			// set the color
			+ "    finalColor = vec4(outColor.x, outColor.y, outColor.z, texture(fontAtlas, outVertTextureCoordinates).x);\r\n" //
			+ "}\r\n";

	/**
	 *
	 */
	private static final int BITMAP_W = 512;
	/**
	 *
	 */
	private static final int BITMAP_H = 512;

	/**
	 *
	 */
	private static final String FONT_FILE = "otf/LibertinusSans-Bold.otf"; // "ttf/FiraSans-Regular.ttf";
	/**
	 *
	 */
	private static final float FONT_SIZE = 36.0f;

	/**
	 * @param output
	 * @param a0
	 * @param b0
	 * @param a1
	 * @param b1
	 */
	private static void addDataToArrayList(final ArrayList<Float> output, final float a0, final float b0,
			final float a1, final float b1) {
		output.add(a1);
		output.add(b0);

		output.add(a1);
		output.add(b1);

		output.add(a0);
		output.add(b1);

		output.add(a0);
		output.add(b1);

		output.add(a0);
		output.add(b0);

		output.add(a1);
		output.add(b0);
	}

	/**
	 * @param data
	 * @param flip
	 * @return
	 */
	private static FloatBuffer createFloatBuffer(final List<Float> data, final boolean flip) {
		final var resultBuffer = BufferUtils.createFloatBuffer(data.size());
		data.forEach(resultBuffer::put);
		if (flip) {
			resultBuffer.flip();
		}
		return resultBuffer;
	}

	/**
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 * @param near
	 * @param far
	 * @return
	 */
	private static Matrix4f createOrthographicProjectionMatrix(final float left, final float top, final float right,
			final float bottom, final float near, final float far) {
		final var matrix = new Matrix4f();

		matrix.m00(2.0f / (right - left));
		matrix.m01(0.0f);
		matrix.m02(0.0f);
		matrix.m03(0.0f);

		matrix.m10(0.0f);
		matrix.m11(2.0f / (top - bottom));
		matrix.m12(0.0f);
		matrix.m13(0.0f);

		matrix.m20(0.0f);
		matrix.m21(0.0f);
		matrix.m22(-2.0f / (far - near));
		matrix.m23(0.0f);

		matrix.m30(-(right + left) / (right - left));
		matrix.m31(-(top + bottom) / (top - bottom));
		matrix.m32(-(far + near) / (far - near));
		matrix.m33(1.0f);

		return matrix;
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		new Chapter01_6().run();
	}

	// The font texture handle
	/**
	 *
	 */
	private int fontTextureID;
	// The font chardata
	/**
	 *
	 */
	private final STBTTPackedchar.Buffer chardata = STBTTPackedchar.malloc(128);
	// A glyph as quad
	/**
	 *
	 */
	private final STBTTAlignedQuad quad = STBTTAlignedQuad.malloc();

	// The x Buffer
	/**
	 *
	 */
	private final FloatBuffer xBuffer = memAllocFloat(1);
	// The y Buffer
	/**
	 *
	 */
	private final FloatBuffer yBuffer = memAllocFloat(1);

	// The shader program handle
	/**
	 *
	 */
	private int programID;

	// The vertex attribute object handle
	/**
	 *
	 */
	private int vaoID;
	// The vertex handle
	/**
	 *
	 */
	private int vertexID;
	// The texture coordinates handle
	/**
	 *
	 */
	private int texCoordID;

	// The vertices of text in Font
	/**
	 *
	 */
	private final ArrayList<Float> fontDrawVertices = new ArrayList<>();
	// The texture coordinates of text in Font
	/**
	 *
	 */
	private final ArrayList<Float> fontDrawTexCoords = new ArrayList<>();

	// The uniform buffer object handle
	/**
	 *
	 */
	private Integer uboID = null;

	/**
	 *
	 */
	private final int uboBinding = 0;
	/**
	 *
	 */
	private final int UBO_BUFFER_SIZE = Float.BYTES * 16;
	/**
	 *
	 */
	private final ByteBuffer uboBuffer = BufferUtils.createByteBuffer(UBO_BUFFER_SIZE);
	// OrthographicProjectionMatrix
	/**
	 *
	 */
	private final FloatBuffer matrixBuffer = uboBuffer.asFloatBuffer();

	/**
	 *
	 */
	private double currentTime;

	/**
	 *
	 */
	@Override
	protected void cleanupGL() {
		glDeleteProgram(programID);
	}

	/**
	 *
	 */
	@Override
	protected void cleanupNonGL() {
		// free memory memAllocFloat calls
		memFree(yBuffer);
		memFree(xBuffer);

		// free memory of STBT objects
		quad.free();
		chardata.free();
	}

	/**
	 * @param shaderSource
	 * @param type
	 * @param programID
	 * @return
	 */
	private int compileShader(final String shaderSource, final int type, final int programID) {
		// Create, compile and attach shader
		final var shaderID = glCreateShader(type);
		glShaderSource(shaderID, shaderSource);
		glCompileShader(shaderID);
		if (glGetShaderi(shaderID, GL_COMPILE_STATUS) != GL_TRUE) {
			throw new RuntimeException("Could not compile shader!\nSource: " + shaderSource + "\nReason: "
					+ glGetShaderInfoLog(shaderID, 500));
		}
		glAttachShader(programID, shaderID);
		return shaderID;
	}

	/**
	 * Create the UBO for all programs.
	 */
	/**
	 *
	 */
	private void createOrthographicProjectionMatrixUbo() {
		uboID = glCreateBuffers();
		glNamedBufferStorage(uboID, UBO_BUFFER_SIZE, GL_DYNAMIC_STORAGE_BIT);
	}

	/**
	 *
	 */
	@Override
	protected void drawInit() {
		// Set the clear color
		{
			currentTime = glfwGetTime();
			glClearColor(((float) Math.sin(currentTime) * 0.5f) + 0.5f, // red
					((float) Math.cos(currentTime) * 0.5f) + 0.5f, // green
					((float) Math.sin(currentTime) * (float) Math.cos(currentTime) * 0.5f) + 0.5f, // blue
					1.0f // alpha
			);
		}

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
	}

	/**
	 * @param x
	 * @param y
	 * @param font
	 * @param text
	 */
	private void drawText(final float x, final float y, final int font, final String text) {
		xBuffer.put(0, x); // set x coordinate
		yBuffer.put(0, y); // set y coordinate

		chardata.position(0);

		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, fontTextureID);

		for (var i = 0; i < text.length(); i++) {
			// fill quad with glyph of char at position i from text and Font with
			// font size
			stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, text.charAt(i), xBuffer, yBuffer, quad, false);

			addDataToArrayList(fontDrawVertices, quad.x0(), quad.y0(), quad.x1(), quad.y1());
			addDataToArrayList(fontDrawTexCoords, quad.s0(), quad.t0(), quad.s1(), quad.t1());
		}

		glBindVertexArray(vaoID);

		glBindBuffer(GL_ARRAY_BUFFER, vertexID);
		glBufferData(GL_ARRAY_BUFFER, createFloatBuffer(fontDrawVertices, true), GL_STATIC_DRAW);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

		glBindBuffer(GL_ARRAY_BUFFER, texCoordID);
		glBufferData(GL_ARRAY_BUFFER, createFloatBuffer(fontDrawTexCoords, true), GL_STATIC_DRAW);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

		// Set the text color
		final var color = new float[] { 1.0f - (float) Math.sin(currentTime), // red
				1.0f - (float) Math.cos(currentTime), // green
				1.0f - (float) Math.sin(currentTime), // blue
		};
		// Update the value of input attribute 0
		glVertexAttrib3fv(2, color);

		updateOrthographicProjectionMatrixUbo();

		glUseProgram(programID);

		/* Bind UBO for rendering */
		glBindBufferBase(GL_UNIFORM_BUFFER, uboBinding, uboID);

		glBindTexture(GL_TEXTURE_2D, fontTextureID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glDrawArrays(GL_TRIANGLES, 0, fontDrawVertices.size() / 2);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindTexture(GL_TEXTURE_2D, 0);

		/* Unbind UBO */
		glBindBufferBase(GL_UNIFORM_BUFFER, uboBinding, 0);

		glUseProgram(0);

		glBindVertexArray(0);

		fontDrawVertices.clear();
		fontDrawTexCoords.clear();
	}

	/**
	 *
	 */
	@Override
	protected void drawWorld() {
		glEnable(GL_BLEND); // enable blend, to blend text over background color
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		drawText((getWindowWidth() / 2) - (FONT_SIZE * 4), (getWindowHeight() / 2) - FONT_SIZE, 0,
				"Hello Shader World!"); // write text
		drawText((getWindowWidth() / 2) - (FONT_SIZE * 4), (getWindowHeight() / 2) + FONT_SIZE, 0,
				"Press Escape to exit ..."); // write text

		glDisable(GL_TEXTURE_2D);

		glDisable(GL_BLEND);
	}

	/**
	 *
	 */
	@Override
	public String getTitle() {
		return getClass().getCanonicalName() + ": dynamic Text on dynamic background";
	}

	/**
	 * @param vertex_shader_source
	 * @param fragment_shader_source
	 * @return
	 */
	protected int linkProgram(final String vertex_shader_source, final String fragment_shader_source) {
		// Create program
		programID = glCreateProgram();

		// Create, compile and attach shader
		final var vertexID = compileShader(vertex_shader_source, GL_VERTEX_SHADER, programID);
		final var fragmentID = compileShader(fragment_shader_source, GL_FRAGMENT_SHADER, programID);

		// vertex shader input parameters
		glBindAttribLocation(programID, 0, "positions");
		glBindAttribLocation(programID, 1, "textureCoordinates");
		glBindAttribLocation(programID, 2, "color");

		glLinkProgram(programID);

		if (glGetShaderi(programID, GL_LINK_STATUS) == GL_TRUE) {
			throw new RuntimeException("Could not link program!");
		}

		// Delete the shaders as the program has them now
		glDeleteShader(vertexID);
		glDeleteShader(fragmentID);

		return programID;
	}

	/**
	 *
	 */
	private void loadFont() {
		fontTextureID = glGenTextures();

		// STBTruetype context
		try (final var pc = STBTTPackContext.malloc()) {
			final var ttf = ResourceUtil.loadGlobalResource(FONT_FILE); // load OpenTypeFont (*.otf) /
																		// TrueTypeFont (*.ttf)

			final var bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H); // texture bitmap

			stbtt_PackBegin(pc, bitmap, BITMAP_W, BITMAP_H, 0, 1); // initialize STBTruetype context

			final var first_unicode_char_in_range = 32; // 32 to remove the non-text characters, 0 to allow all
			chardata.position(first_unicode_char_in_range);
			stbtt_PackSetOversampling(pc, 1, 1);
			stbtt_PackFontRange(pc, ttf, 0, FONT_SIZE, first_unicode_char_in_range, chardata); // set Font with
																								// font size
			chardata.clear(); // clear buffer, contains Font with font size

			stbtt_PackEnd(pc); // cleanup

			// set Texture
			glBindTexture(GL_TEXTURE_2D, fontTextureID);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, BITMAP_W, BITMAP_H, 0, GL_RED, GL_UNSIGNED_BYTE, bitmap);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 *
	 */
	@Override
	protected void prepare() {
		// load TrueType Font
		loadFont();

		programID = linkProgram(vertex_shader_source, fragment_shader_source);

		setOrthographicProjectionMatrix();
		createOrthographicProjectionMatrixUbo();

		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		vertexID = glGenBuffers();
		texCoordID = glGenBuffers();
		glBindVertexArray(0);
	}

	/**
	 *
	 */
	@Override
	protected void resized() {
		setOrthographicProjectionMatrix();
		/* Update OrthographicProjectionMatrix UBO */
		updateOrthographicProjectionMatrixUbo();
	}

	/**
	 *
	 */
	private void setOrthographicProjectionMatrix() {
		matrixBuffer.clear();
		createOrthographicProjectionMatrix(0, 0, getWindowWidth(), getWindowHeight(), -1, 1).get(matrixBuffer);
		if (matrixBuffer.position() > 0) {
			matrixBuffer.flip();
		}
	}

	/**
	 *
	 */
	private void updateOrthographicProjectionMatrixUbo() {
		/* Update the UBO */
		matrixBuffer.rewind();
		glNamedBufferSubData(uboID, 0, uboBuffer);
	}
}
