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

package modeldata;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.Texture;
import org.joml.Matrix4f;
import rendering.ShaderProgram;

@AllArgsConstructor
public class SingleModel implements Model {
	@Getter
	protected List<Mesh> meshes;

	public void render() {
		for (Mesh mesh : meshes) {
			mesh.render();
		}
	}

	public void addTextures(String uniform, List<? extends Texture> textures) {
		if (textures.size() != meshes.size()) {
			throw new RuntimeException("Unequal number of meshes and textures");
		}
		for (int i = 0; i < meshes.size(); i++) {
			Texture texture = textures.get(i);
			if (texture == null) {
				continue;
			}
			meshes.get(i).addTexture(uniform, texture);
		}
	}

	@Override
	public void setModelMatrix(Matrix4f model) {
		meshes.forEach(m -> m.setModel(model));
	}

	@Override
	public void setShaderProgram(ShaderProgram shaderProgram) {
		meshes.forEach(m -> m.setShaderProgram(shaderProgram));
	}

	@Override
	public void setIsInstanced(boolean value) {
		for (Mesh mesh : meshes) {
			mesh.setInstanced(value);
		}
	}
}
