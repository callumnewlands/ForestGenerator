package meshdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11C.GL_REPEAT;
import static org.lwjgl.opengl.GL11C.GL_RGB;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11C.glTexImage2D;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;
import static org.lwjgl.system.MemoryStack.stackPush;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

public class Texture {

	private static final int RGBA_NO_OF_COMPONENTS = 4;
	private static final int RGB_NO_OF_COMPONENTS = 3;
	private static final int SINGLE_COMPONENT = 1;

	private int handle;
	private int width;
	private int height;
	private int numberOfComponents;
	private int unit;
	private Vector3f colour;

	public Texture(final String path, final Vector3f colour, final int textureUnit) {
		this.colour = colour;
		this.handle = glGenTextures();
		this.unit = textureUnit;
		this.bind();

		// set texture wrapping to GL_REPEAT in both directions (default wrapping method)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		// set texture filtering parameters
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		try {
			loadTextureFromFile(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Texture(final float[][] greyscaleValues, final int width, final int height, final int textureUnit) {
		this.colour = new Vector3f(0.3f);
		this.handle = glGenTextures();
		this.unit = textureUnit;
		this.bind();


		// set texture wrapping to GL_REPEAT in both directions (default wrapping method)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		// set texture filtering parameters
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		ByteBuffer image = BufferUtils.createByteBuffer(width * height * 3);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int col = (int) ((greyscaleValues[x][y] + 1) / 2 * 255);
				for (int component = 0; component < 3; component++) {
					image.put((byte) col);
				}
			}
		}
		image.flip();

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, image);

		glGenerateMipmap(GL_TEXTURE_2D);
	}

	private static ByteBuffer readFileToByteBuffer(final String path) throws IOException {
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

	private void loadTextureFromFile(final String path) throws IOException {
		ByteBuffer rawImageData;
		ByteBuffer image;

		rawImageData = readFileToByteBuffer(path);

		try (MemoryStack stack = stackPush()) {
			IntBuffer widthBuffer = stack.mallocInt(1);
			IntBuffer heightBuffer = stack.mallocInt(1);
			IntBuffer numberOfComponentsBuffer = stack.mallocInt(1);

			stbi_set_flip_vertically_on_load(true);

			image = stbi_load_from_memory(rawImageData, widthBuffer, heightBuffer,
					numberOfComponentsBuffer, 0);
			if (image == null) {
				throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
			}

			this.width = widthBuffer.get(0);
			this.height = heightBuffer.get(0);
			this.numberOfComponents = numberOfComponentsBuffer.get(0);
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

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, format, GL_UNSIGNED_BYTE, image);

		glGenerateMipmap(GL_TEXTURE_2D);


	}

	public void bind() {
		glActiveTexture(this.unit);
		glBindTexture(GL_TEXTURE_2D, this.handle);
	}

	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public int getUnit() {
		return this.unit - GL_TEXTURE0;
	}

	public Vector3f getColour() {
		return colour;
	}
}

