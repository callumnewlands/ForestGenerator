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

package sceneobjects;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import modeldata.SingleModel;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.Texture;
import modeldata.meshdata.Vertex;
import modeldata.meshdata.VertexAttribute;
import org.joml.Vector3f;
import rendering.ShaderProgram;

public class Cube {

	protected final SingleModel model;

	public Cube() {
		List<Vector3f> positions = List.of(
				new Vector3f(-1.0f, 1.0f, -1.0f),
				new Vector3f(-1.0f, -1.0f, -1.0f),
				new Vector3f(1.0f, -1.0f, -1.0f),
				new Vector3f(1.0f, -1.0f, -1.0f),
				new Vector3f(1.0f, 1.0f, -1.0f),
				new Vector3f(-1.0f, 1.0f, -1.0f),

				new Vector3f(-1.0f, -1.0f, 1.0f),
				new Vector3f(-1.0f, -1.0f, -1.0f),
				new Vector3f(-1.0f, 1.0f, -1.0f),
				new Vector3f(-1.0f, 1.0f, -1.0f),
				new Vector3f(-1.0f, 1.0f, 1.0f),
				new Vector3f(-1.0f, -1.0f, 1.0f),

				new Vector3f(1.0f, -1.0f, -1.0f),
				new Vector3f(1.0f, -1.0f, 1.0f),
				new Vector3f(1.0f, 1.0f, 1.0f),
				new Vector3f(1.0f, 1.0f, 1.0f),
				new Vector3f(1.0f, 1.0f, -1.0f),
				new Vector3f(1.0f, -1.0f, -1.0f),

				new Vector3f(-1.0f, -1.0f, 1.0f),
				new Vector3f(-1.0f, 1.0f, 1.0f),
				new Vector3f(1.0f, 1.0f, 1.0f),
				new Vector3f(1.0f, 1.0f, 1.0f),
				new Vector3f(1.0f, -1.0f, 1.0f),
				new Vector3f(-1.0f, -1.0f, 1.0f),

				new Vector3f(-1.0f, 1.0f, -1.0f),
				new Vector3f(1.0f, 1.0f, -1.0f),
				new Vector3f(1.0f, 1.0f, 1.0f),
				new Vector3f(1.0f, 1.0f, 1.0f),
				new Vector3f(-1.0f, 1.0f, 1.0f),
				new Vector3f(-1.0f, 1.0f, -1.0f),

				new Vector3f(-1.0f, -1.0f, -1.0f),
				new Vector3f(-1.0f, -1.0f, 1.0f),
				new Vector3f(1.0f, -1.0f, -1.0f),
				new Vector3f(1.0f, -1.0f, -1.0f),
				new Vector3f(-1.0f, -1.0f, 1.0f),
				new Vector3f(1.0f, -1.0f, 1.0f)
		);

		List<Vertex> vertices = positions.stream().map(Vertex::new).collect(Collectors.toList());
		int[] indices = IntStream.range(0, positions.size()).toArray();
		Mesh mesh = new Mesh(vertices, indices, List.of(VertexAttribute.POSITION));
		model = new SingleModel(List.of(mesh));
	}

	public void render() {
		model.render();
	}

	public void addTexture(String uniform, Texture texture) {
		model.addTextures(uniform, List.of(texture));
	}

	public void setShaderProgram(ShaderProgram shaderProgram) {
		model.setShaderProgram(shaderProgram);
	}
}
