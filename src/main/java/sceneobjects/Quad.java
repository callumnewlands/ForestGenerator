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
import modeldata.SingleModel;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.Vertex;
import modeldata.meshdata.VertexAttribute;
import org.joml.Vector2f;
import org.joml.Vector3f;
import rendering.ShaderProgram;

public class Quad {
	private final SingleModel model;

	public Quad(ShaderProgram shaderProgram) {
		List<Vertex> vertices = List.of(
				new Vertex(new Vector3f(-1.0f, 1.0f, 0.0f), new Vector2f(0.0f, 1.0f)),
				new Vertex(new Vector3f(-1.0f, -1.0f, 0.0f), new Vector2f(0.0f, 0.0f)),
				new Vertex(new Vector3f(1.0f, 1.0f, 0.0f), new Vector2f(1.0f, 1.0f)),
				new Vertex(new Vector3f(1.0f, -1.0f, 0.0f), new Vector2f(1.0f, 0.0f))
		);

		int[] indices = {0, 1, 3, 0, 2, 3};
		Mesh mesh = new Mesh(vertices, indices, List.of(VertexAttribute.POSITION, VertexAttribute.TEXTURE));
		model = new SingleModel(List.of(mesh));
		model.setShaderProgram(shaderProgram);
	}

	public void render() {
		model.render();
	}
}
