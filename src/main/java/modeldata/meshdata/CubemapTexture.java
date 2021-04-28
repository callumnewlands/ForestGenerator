/*
 * Copyright (c) 2021 Callum Newlands
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     The additional term of 7.b applies: Requiring preservation of specified
 *     reasonable legal notices or author attributions in that material or in the
 *     Appropriate Legal Notices displayed by works containing it
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
		super(colour, textureUnit, GL_CLAMP_TO_EDGE);
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
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	protected void setHints(int textureWrap) {
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, textureWrap);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, textureWrap);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, textureWrap);
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
