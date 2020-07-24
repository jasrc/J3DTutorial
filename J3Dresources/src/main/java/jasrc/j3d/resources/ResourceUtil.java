package jasrc.j3d.resources;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.BufferUtils;

/**
 *
 *
 * @author Arnold, J.
 */
public class ResourceUtil {

	public static ByteBuffer loadGlobalResource(final String resource) throws IOException {
		return loadResource(ResourceUtil.class, resource);
	}

	public static ByteBuffer loadResource(final Class<?> clazz, final String resource) throws IOException {
		ByteBuffer buffer;
		Path path = null;
		final var classLoader = clazz.getClassLoader();
		try {
			// is resource a File?
			path = Paths.get(resource);
		} catch (final Throwable th) {
			// resource is not a File
		}
		if ((path == null) || (!Files.isReadable(path))) {
			try {
				// is resource a File (class loading)?
				path = Paths.get(new File(classLoader.getResource(resource).getFile()).getAbsolutePath());
			} catch (final Throwable th) {
				// resource is not a File (class loading)
			}
		}
		if ((path != null) && (Files.isReadable(path))) {
			// load resource as File
			try (var inChannel = Files.newByteChannel(path)) {
				buffer = BufferUtils.createByteBuffer((int) inChannel.size() + 1);
				while (inChannel.read(buffer) != -1) {

				}
			}
		} else {
			// load resource as Stream (class loading)
			try (var source = classLoader.getResourceAsStream(resource); var inChannel = Channels.newChannel(source)) {
				buffer = createByteBuffer(512 * 1024); // 512 kB initial size
				while (inChannel.read(buffer) != -1) {
					if (buffer.remaining() == 0) {
						buffer.flip();
						// resize capacity +100% to read rest of file
						buffer = createByteBuffer((buffer.capacity() * 2)).put(buffer);
					}
				}
			}
		}
		buffer.flip();
		return buffer;
	}

	public static ByteBuffer loadResourceMemAlloc(final Class<?> clazz, final String resource) throws IOException {
		ByteBuffer buffer;
		Path path = null;
		final var classLoader = clazz.getClassLoader();
		try {
			// is resource a File?
			path = Paths.get(resource);
		} catch (final Throwable th) {
			// resource is not a File
		}
		if ((path == null) || (!Files.isReadable(path))) {
			try {
				// is resource a File (class loading)?
				path = Paths.get(new File(classLoader.getResource(resource).getFile()).getAbsolutePath());
			} catch (final Throwable th) {
				// resource is not a File (class loading)
			}
		}
		if ((path != null) && (Files.isReadable(path))) {
			// load resource as File
			try (var inChannel = Files.newByteChannel(path)) {
				buffer = memAlloc((int) inChannel.size() + 1);
				while (inChannel.read(buffer) != -1) {

				}
			}
		} else {
			// load resource as Stream (class loading)
			try (var source = classLoader.getResourceAsStream(resource); var inChannel = Channels.newChannel(source)) {
				buffer = memAlloc(512 * 1024); // 512 kB initial size
				while (inChannel.read(buffer) != -1) {
					if (buffer.remaining() == 0) {
						buffer.flip();
						// resize capacity +100% to read rest of file
						final var old = buffer;
						buffer = memAlloc((buffer.capacity() * 2)).put(buffer);
						memFree(old);
					}
				}
			}
		}
		buffer.flip();
		return buffer;
	}

}
