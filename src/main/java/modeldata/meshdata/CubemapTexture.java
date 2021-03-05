package modeldata.meshdata;

import java.io.IOException;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11C.GL_LINEAR;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11C.glTexImage2D;
import static org.lwjgl.opengl.GL12C.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12C.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL21C.GL_SRGB_ALPHA;

import org.joml.Vector3f;

public class CubemapTexture extends Texture {

	public CubemapTexture(Vector3f colour, int textureUnit) {
		super(colour, textureUnit, GL_LINEAR);
	}

	public CubemapTexture(List<String> paths, Vector3f colour, int textureUnit) {
		this(colour, textureUnit);
		for (int i = 0; i < paths.size(); i++) {
			String path = paths.get(i);
			try {
				LoadedByteImage image = loadByteTextureFromFile(path, false);
				glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_SRGB_ALPHA, image.width, image.height,
						0, image.format, GL_UNSIGNED_BYTE, image.imageData);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void setHints(int textureWrap) {
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, textureWrap);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, textureWrap);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
	}

	@Override
	public void bind() {
		glActiveTexture(this.unit);
		glBindTexture(GL_TEXTURE_CUBE_MAP, this.handle);
	}

	@Override
	public void unbind() {
		glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
	}
}
