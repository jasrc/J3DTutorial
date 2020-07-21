package jasrc.j3d.opengl.chapter01;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
// deprecated OpenGL
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;
// Core OpenGL
import static org.lwjgl.opengl.GL11C.GL_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_BLEND;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_LINEAR;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_QUADS;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glBlendFunc;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.opengl.GL11C.glDisable;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL11C.glGenTextures;
import static org.lwjgl.opengl.GL11C.glTexImage2D;
import static org.lwjgl.opengl.GL11C.glTexParameteri;
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;
import static org.lwjgl.stb.STBTruetype.stbtt_PackBegin;
import static org.lwjgl.stb.STBTruetype.stbtt_PackEnd;
import static org.lwjgl.stb.STBTruetype.stbtt_PackFontRange;
import static org.lwjgl.stb.STBTruetype.stbtt_PackSetOversampling;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;

import jasrc.j3d.opengl.lib.OpenGLmain;
import jasrc.j3d.resources.ResourceUtil;

/**
 *
 *
 * @author Arnold, J.
 */
public class Chapter01_5 extends OpenGLmain {

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
	 * @param args
	 */
	public static void main(final String[] args) {
		new Chapter01_5().run();
	}

	/**
	 *
	 */
	double currentTime;

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
	 *
	 */
	@Override
	protected void drawInit() {
		glViewport(0, 0, getWindowWidth(), getWindowHeight()); // set viewport

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

		// projection
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, getWindowWidth(), getWindowHeight(), 0.0, -1.0, 1.0);
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

		glBegin(GL_QUADS);
		float x0, y0, x1, y1, s0, t0, s1, t1;
		for (var i = 0; i < text.length(); i++) {
			// fill quad with glyph of char at position i from text and Font with
			// font size
			stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, text.charAt(i), xBuffer, yBuffer, quad, false);

			s0 = quad.s0();
			t0 = quad.t0();
			x0 = quad.x0();
			y0 = quad.y0();
			s1 = quad.s1();
			x1 = quad.x1();
			t1 = quad.t1();
			y1 = quad.y1();
			glTexCoord2f(s0, t0);
			glVertex2f(x0, y0);
			glTexCoord2f(s1, t0);
			glVertex2f(x1, y0);
			glTexCoord2f(s1, t1);
			glVertex2f(x1, y1);
			glTexCoord2f(s0, t1);
			glVertex2f(x0, y1);
		}
		glEnd();
	}

	/**
	 *
	 */
	@Override
	protected void drawWorld() {
		glEnable(GL_BLEND); // enable blend, to blend text over background color
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		// Set the text color
		final var value = (((float) Math.sin(currentTime)) + ((float) Math.cos(currentTime))
				+ ((float) Math.sin(currentTime) * (float) Math.cos(currentTime))) < 1.5f ? 1.0f : 0.0f;
		glColor3f(value, // red
				value, // green
				value // blue
		);

		drawText((getWindowWidth() / 2) - (FONT_SIZE * 4.1f), (getWindowHeight() / 2) - FONT_SIZE, 0,
				"Hello OpenGL World!"); // write text
		drawText((getWindowWidth() / 2) - (FONT_SIZE * 4.1f), (getWindowHeight() / 2) + FONT_SIZE, 0,
				"Press Escape to exit ..."); // write text

		glDisable(GL_TEXTURE_2D);

		glDisable(GL_BLEND);
	}

	/**
	 *
	 */
	@Override
	public String getTitle() {
		return getClass().getCanonicalName() + ": white or black Text on dynamic background";
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
			glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
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
	}

}
