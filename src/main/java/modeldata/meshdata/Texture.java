package modeldata.meshdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11C.GL_RGB;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;
import static org.lwjgl.system.MemoryStack.stackPush;

import lombok.AllArgsConstructor;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

public abstract class Texture {

	static final int RGBA_NO_OF_COMPONENTS = 4;
	static final int RGB_NO_OF_COMPONENTS = 3;
	static final int SINGLE_COMPONENT = 1;

	protected final int handle;
	protected final int unit;
	private final Vector3f colour;

	public Texture(final Vector3f colour, final int textureUnit, final int textureWrap) {
		this.colour = colour;
		this.handle = glGenTextures();
		this.unit = GL_TEXTURE0 + textureUnit;
		this.bind();
		this.setHints(textureWrap);
	}

	protected static ByteBuffer readFileToByteBuffer(final String path) throws IOException {
		ByteBuffer buffer;
		File file = new File(Paths.get(path).toAbsolutePath().toString());
		if (file.exists() && file.isFile()) {
			FileInputStream fis = new FileInputStream(file);
			FileChannel fc = fis.getChannel();
			buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			fc.close();
			fis.close();
		} else {
			throw new IOException("Resource: " + path + " is not a file.");
		}
		return buffer;
	}

	protected abstract void setHints(int textureWrap);

	protected LoadedImage loadTextureFromFile(final String path) throws IOException {
		return loadTextureFromFile(path, true);
	}

	protected LoadedImage loadTextureFromFile(final String path, final boolean flip) throws IOException {
		ByteBuffer rawImageData;
		ByteBuffer image;

		rawImageData = readFileToByteBuffer(path);

		int width;
		int height;
		int numberOfComponents;
		try (MemoryStack stack = stackPush()) {
			IntBuffer widthBuffer = stack.mallocInt(1);
			IntBuffer heightBuffer = stack.mallocInt(1);
			IntBuffer numberOfComponentsBuffer = stack.mallocInt(1);

			stbi_set_flip_vertically_on_load(flip);

			image = stbi_load_from_memory(rawImageData, widthBuffer, heightBuffer,
					numberOfComponentsBuffer, 0);
			if (image == null) {
				throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
			}

			width = widthBuffer.get(0);
			height = heightBuffer.get(0);
			numberOfComponents = numberOfComponentsBuffer.get(0);
		}

		int format;
		if (numberOfComponents == RGBA_NO_OF_COMPONENTS) {
			format = GL_RGBA;
		} else if (numberOfComponents == RGB_NO_OF_COMPONENTS) {
			format = GL_RGB;
		} else if (numberOfComponents == SINGLE_COMPONENT) {
			format = GL_RED;
		} else {
			throw new IllegalArgumentException("Unsupported number of colour channels in Texture Class: "
					+ numberOfComponents);
		}

		return new LoadedImage(image, format, width, height);
	}

	abstract public void bind();

	abstract public void unbind();

	public int getUnitID() {
		return this.unit - GL_TEXTURE0;
	}

	public Vector3f getColour() {
		return colour;
	}

	@AllArgsConstructor
	static class LoadedImage {
		final ByteBuffer imageData;
		final int format;
		final int width;
		final int height;
	}
}
