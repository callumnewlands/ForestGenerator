package modeldata.meshdata;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11C.GL_REPEAT;
import static org.lwjgl.opengl.GL11C.GL_RGB;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11C.glTexImage2D;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class Texture2D extends Texture {


	public Texture2D(final String path, final Vector3f colour, final int textureUnit) {
		this(path, colour, textureUnit, GL_REPEAT);
	}

	public Texture2D(final String path, final Vector3f colour, final int textureUnit, final int textureWrap) {
		super(colour, textureUnit, textureWrap);
		try {
			LoadedImage image = loadTextureFromFile(path);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.width, image.height, 0, image.format,
					GL_UNSIGNED_BYTE, image.imageData);
			glGenerateMipmap(GL_TEXTURE_2D);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Texture2D(final float[][] greyscaleValues, final int width, final int height, final int textureUnit) {
		super(new Vector3f(0.3f), textureUnit, GL_REPEAT);
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

	@Override
	protected void setHints(int textureWrap) {
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, textureWrap);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, textureWrap);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	}

	@Override
	public void bind() {
		glActiveTexture(this.unit);
		glBindTexture(GL_TEXTURE_2D, this.handle);
	}

	@Override
	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

}
