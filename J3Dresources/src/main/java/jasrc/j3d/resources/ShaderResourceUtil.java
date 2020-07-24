package jasrc.j3d.resources;

import static jasrc.j3d.resources.ResourceUtil.loadGlobalResource;
//import static jasrc.j3d.resources.IOUtils.ioResourceToByteBuffer;
import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_anyhit_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_callable_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_closesthit_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compilation_status_success;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compile_into_spv;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compile_options_initialize;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compile_options_set_include_callbacks;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compile_options_set_optimization_level;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compiler_initialize;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compiler_release;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compute_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_fragment_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_geometry_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_intersection_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_miss_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_optimization_level_performance;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_raygen_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_result_get_bytes;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_result_get_compilation_status;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_result_get_error_message;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_result_get_length;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_result_release;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_tess_control_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_tess_evaluation_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_vertex_shader;
import static org.lwjgl.vulkan.NVRayTracing.VK_SHADER_STAGE_ANY_HIT_BIT_NV;
import static org.lwjgl.vulkan.NVRayTracing.VK_SHADER_STAGE_CALLABLE_BIT_NV;
import static org.lwjgl.vulkan.NVRayTracing.VK_SHADER_STAGE_CLOSEST_HIT_BIT_NV;
import static org.lwjgl.vulkan.NVRayTracing.VK_SHADER_STAGE_INTERSECTION_BIT_NV;
import static org.lwjgl.vulkan.NVRayTracing.VK_SHADER_STAGE_MISS_BIT_NV;
import static org.lwjgl.vulkan.NVRayTracing.VK_SHADER_STAGE_RAYGEN_BIT_NV;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_COMPUTE_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_GEOMETRY_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_TESSELLATION_CONTROL_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_TESSELLATION_EVALUATION_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_VERTEX_BIT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.shaderc.ShadercIncludeResolve;
import org.lwjgl.util.shaderc.ShadercIncludeResult;
import org.lwjgl.util.shaderc.ShadercIncludeResultRelease;

/**
 *
 *
 * @author Arnold, J.
 */
public class ShaderResourceUtil {
	// .conf to provide a config file that replaces the default configuration(see -c
	// option below for generating a template)
	public static final String CONFIGURATION = ".conf";
	// .vert for a vertex shader
	public static final String VERTEX = ".vert";
	// .tesc for a tessellation control shader
	public static final String TESS_CONTROL = ".tesc";
	// .tese for a tessellation evaluation shader
	public static final String TESS_EVALUATION = ".tese";
	// .geom for a geometry shader
	public static final String GEOMETRY = ".geom";
	// .frag for a fragment shader
	public static final String FRAGMENT = ".frag";
	// .comp for a compute shader
	public static final String COMPUTE = ".comp";
	// .mesh for a mesh shader
	public static final String MESH = ".mesh";
	// .task for a task shader
	public static final String TASK = ".task";
	// .rgen for a ray generation shader
	public static final String RAY_GENERATION = ".rgen";
	// .rint for a ray intersection shader
	public static final String RAY_INTERSECTION = ".rint";
	// .rahit for a ray any hit shader
	public static final String RAY_ANY_HIT = ".rahit";
	// .rchit for a ray closest hit shader
	public static final String RAY_CLOSEST_HIT = ".rchit";
	// .rmiss for a ray miss shader
	public static final String RAY_MISS = ".rmiss";
	// .rcall for a ray callable shader
	public static final String RAY_CALLABLE = ".rcall";
	// .glsl for .vert.glsl, .tesc.glsl, ..., .comp.glsl compound suffixes
	public static final String GLSL = ".glsl"; // only in combination with none glsl/hlsl as postfix
	// .hlsl for .vert.hlsl, .tesc.hlsl, ..., .comp.hlsl compound suffixes
	public static final String HLSL = ".hlsl"; // only in combination with none glsl/hlsl as postfix

	public static void compileShader(final File dir, final String shadersource, final String chapter,
			final String extension) {
	}

	public static void compileShaders(final File dir, final String targetEnvironment) {
		if ((!dir.exists()) || (!dir.isDirectory())) {
			return; // nothing to compile
		}
		try {
			String file;
			final var builder = new ProcessBuilder();
			builder.directory(dir);
			PrintWriter writer;
			try (var stream = Files.newDirectoryStream(Paths.get(dir.toURI()))) {
				for (final Path path : stream) {
					if (!Files.isDirectory(path)) {
						file = path.toFile().getAbsolutePath();
						if ((!file.endsWith(".spv")) && (!file.endsWith(".out"))) {
							builder.command("glslc", "--target-env=" + targetEnvironment, "-o", file + ".spv", file);
							writer = new PrintWriter(new FileWriter(file + ".out"));
							final var process = builder.start();
							new BufferedReader(new InputStreamReader(process.getInputStream())).lines()
									.forEach(writer::println);
							if (process.waitFor() != 0) {
								System.out.println(
										"glslc --target-env=" + targetEnvironment + " -o " + file + ".spv " + file);
							}
						}
					}
				}
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void decompileShaders(final File dir, final String targetEnvironment) {
		if ((!dir.exists()) || (!dir.isDirectory())) {
			return; // nothing to compile
		}
		try {
			String file;
			String stage;
			final var builder = new ProcessBuilder();
			builder.directory(dir);
			PrintWriter writer;
			try (var stream = Files.newDirectoryStream(Paths.get(dir.toURI()))) {
				for (final Path path : stream) {
					if (!Files.isDirectory(path)) {
						file = path.toFile().getAbsolutePath();
						if (file.endsWith(".spv")) {
							stage = file.substring(0, file.lastIndexOf("."));
							stage = stage.substring(stage.indexOf("."));
							builder.command("spirv-cross", file, "--output", file + "." + targetEnvironment,
									getSource(targetEnvironment), getStage(stage));
							writer = new PrintWriter(new FileWriter(file + ".out"));
							final var process = builder.start();
							new BufferedReader(new InputStreamReader(process.getInputStream())).lines()
									.forEach(writer::println);
							if (process.waitFor() != 0) {
								System.out.println("spirv-cross " + file + " --output " + file + "." + targetEnvironment
										+ " " + getSource(targetEnvironment) + " " + getStage(stage));
							}
						}
					}
				}
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getExtension(String filename) {
		final var extension = Optional.ofNullable(filename).filter(f -> f.contains("."))
				.map(f -> f.substring(filename.lastIndexOf(".")));
		return (extension.isEmpty()) ? "" : extension.get();
	}

	public static String getSource(final String targetEnvironment) {
		switch (targetEnvironment.toLowerCase()) {
		case "opengl":
			return "";
		case "vulkan":
			return "-V";
		case "metal":
			return "--msl";
		case "hlsl":
			return "--hlsl";
		case "reflect":
			return "--reflect";
		case "cpp":
			return "--cpp";
		default:
			throw new IllegalArgumentException("getSource: " + targetEnvironment);
		}
	}

	public static String getStage(final String stage) {
		switch (stage) {
		case VERTEX:
			return "--stage vert";
		case TESS_CONTROL:
			return "--stage tesc";
		case TESS_EVALUATION:
			return "--stage tese";
		case GEOMETRY:
			return "--stage geom";
		case FRAGMENT:
			return "--stage frag";
		case COMPUTE:
			return "--stage comp";
		default:
			return "";
		// throw new IllegalArgumentException("Stage: " + stage);
		}
	}

	public static ByteBuffer shader2Spirv(final ByteBuffer src, final String shaderStage, final String file)
			throws IOException {
		final var compiler = shaderc_compiler_initialize();
		final var options = shaderc_compile_options_initialize();
		shaderc_compile_options_set_optimization_level(options, shaderc_optimization_level_performance);
		long res;
		try (var stack = MemoryStack.stackPush()) {
			res = shaderc_compile_into_spv(compiler, src,
					vulkanStageToShadercKind(shaderStageToVulkanStage(shaderStage)), stack.UTF8(file),
					stack.UTF8("main"), options);
			if (res == 0L) {
				throw new AssertionError("Internal error during compilation!");
			}
		}
		if (shaderc_result_get_compilation_status(res) != shaderc_compilation_status_success) {
			throw new AssertionError("Shader compilation failed: " + shaderc_result_get_error_message(res));
		}
		final var size = (int) shaderc_result_get_length(res);
		final var resultBytes = createByteBuffer(size);
		resultBytes.put(shaderc_result_get_bytes(res));
		resultBytes.flip();
		shaderc_result_release(res);
		shaderc_compiler_release(compiler);
		return resultBytes;
	}

	public static ByteBuffer shaderfileToSpirv(final String classPath, final int vulkanStage) throws IOException {
		final var src = loadGlobalResource(classPath); // ioResourceToByteBuffer(classPath, 1024);
		final var compiler = shaderc_compiler_initialize();
		final var options = shaderc_compile_options_initialize();
		ShadercIncludeResolve resolver;
		ShadercIncludeResultRelease releaser;
		shaderc_compile_options_set_optimization_level(options, shaderc_optimization_level_performance);
		shaderc_compile_options_set_include_callbacks(options, resolver = new ShadercIncludeResolve() {
			@Override
			public long invoke(final long user_data, final long requested_source, final int type,
					final long requesting_source, final long include_depth) {
				final var res = ShadercIncludeResult.calloc();
				try {
					final var src = classPath.substring(0, classPath.lastIndexOf('/')) + "/"
							+ memUTF8(requested_source);
					res.content(loadGlobalResource(src)); // ioResourceToByteBuffer(src, 1024));
					res.source_name(memUTF8(src));
					return res.address();
				} catch (final IOException e) {
					throw new AssertionError("Failed to resolve include: " + src);
				}
			}
		}, releaser = new ShadercIncludeResultRelease() {
			@Override
			public void invoke(final long user_data, final long include_result) {
				final var result = ShadercIncludeResult.create(include_result);
				memFree(result.source_name());
				result.free();
			}
		}, 0L);
		long res;
		try (var stack = MemoryStack.stackPush()) {
			res = shaderc_compile_into_spv(compiler, src, vulkanStageToShadercKind(vulkanStage), stack.UTF8(classPath),
					stack.UTF8("main"), options);
			if (res == 0L) {
				throw new AssertionError("Internal error during compilation!");
			}
		}
		if (shaderc_result_get_compilation_status(res) != shaderc_compilation_status_success) {
			throw new AssertionError("Shader compilation failed: " + shaderc_result_get_error_message(res));
		}
		final var size = (int) shaderc_result_get_length(res);
		final var resultBytes = createByteBuffer(size);
		resultBytes.put(shaderc_result_get_bytes(res));
		resultBytes.flip();
		shaderc_result_release(res);
		shaderc_compiler_release(compiler);
		releaser.free();
		resolver.free();
		return resultBytes;
	}

	private static int shaderStageToVulkanStage(final String stage) {
		switch (stage) {
		case VERTEX:
			return VK_SHADER_STAGE_VERTEX_BIT;
		case TESS_CONTROL:
			return VK_SHADER_STAGE_TESSELLATION_CONTROL_BIT;
		case TESS_EVALUATION:
			return VK_SHADER_STAGE_TESSELLATION_EVALUATION_BIT;
		case GEOMETRY:
			return VK_SHADER_STAGE_GEOMETRY_BIT;
		case FRAGMENT:
			return VK_SHADER_STAGE_FRAGMENT_BIT;
		case COMPUTE:
			return VK_SHADER_STAGE_COMPUTE_BIT;
		case RAY_GENERATION:
			return VK_SHADER_STAGE_RAYGEN_BIT_NV;
		case RAY_INTERSECTION:
			return VK_SHADER_STAGE_INTERSECTION_BIT_NV;
		case RAY_CLOSEST_HIT:
			return VK_SHADER_STAGE_CLOSEST_HIT_BIT_NV;
		case RAY_MISS:
			return VK_SHADER_STAGE_MISS_BIT_NV;
		case RAY_ANY_HIT:
			return VK_SHADER_STAGE_ANY_HIT_BIT_NV;
		case RAY_CALLABLE:
			return VK_SHADER_STAGE_CALLABLE_BIT_NV;
		default:
			throw new IllegalArgumentException("Stage: " + stage);
		}
	}

	public static ByteBuffer shaderToSpirv(final String classPath, final String shaderStage) throws IOException {
		return shaderfileToSpirv(classPath, shaderStageToVulkanStage(shaderStage));
	}

	private static int vulkanStageToShadercKind(final int stage) {
		switch (stage) {
		case VK_SHADER_STAGE_VERTEX_BIT:
			return shaderc_vertex_shader;
		case VK_SHADER_STAGE_TESSELLATION_CONTROL_BIT:
			return shaderc_tess_control_shader;
		case VK_SHADER_STAGE_TESSELLATION_EVALUATION_BIT:
			return shaderc_tess_evaluation_shader;
		case VK_SHADER_STAGE_GEOMETRY_BIT:
			return shaderc_geometry_shader;
		case VK_SHADER_STAGE_FRAGMENT_BIT:
			return shaderc_fragment_shader;
		case VK_SHADER_STAGE_COMPUTE_BIT:
			return shaderc_compute_shader;
		case VK_SHADER_STAGE_RAYGEN_BIT_NV:
			return shaderc_raygen_shader;
		case VK_SHADER_STAGE_INTERSECTION_BIT_NV:
			return shaderc_intersection_shader;
		case VK_SHADER_STAGE_CLOSEST_HIT_BIT_NV:
			return shaderc_closesthit_shader;
		case VK_SHADER_STAGE_MISS_BIT_NV:
			return shaderc_miss_shader;
		case VK_SHADER_STAGE_ANY_HIT_BIT_NV:
			return shaderc_anyhit_shader;
		case VK_SHADER_STAGE_CALLABLE_BIT_NV:
			return shaderc_callable_shader;
		default:
			throw new IllegalArgumentException("Stage: " + stage);
		}
	}

	public static void writeShader(final File dir, final String shadersource, final String chapter,
			final String extension) {
		if (!dir.exists()) {
			dir.mkdir();
		}
		final var path = Paths.get(dir.getAbsolutePath() + "/" + chapter + extension);
		final var strToBytes = shadersource.getBytes();

		try {
			Files.write(path, strToBytes);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
