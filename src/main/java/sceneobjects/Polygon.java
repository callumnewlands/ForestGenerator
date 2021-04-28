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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import modeldata.SingleModel;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.Vertex;
import modeldata.meshdata.VertexAttribute;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import rendering.ShaderProgram;

public class Polygon {
	private final SingleModel model;

	public Polygon(int numEdges, ShaderProgram shaderProgram) {
		List<Vector3f> positions = new ArrayList<>();
		positions.add(new Vector3f(0, 0, 0)); // Centre
		for (int i = 0; i < numEdges; i++) {
			double theta = 2 * Math.PI * i / numEdges;
			positions.add(new Vector3f((float) Math.sin(theta) / 2, 0, (float) Math.cos(theta) / 2));
		}

		List<Vertex> vertices = positions.stream().map(pos -> new Vertex(pos, new Vector3f(0, 1, 0))).collect(Collectors.toList());
		int[] indices = IntStream.range(1, numEdges + 1).boxed().flatMapToInt(
				i -> IntStream.of(0, i, (i % numEdges) + 1)
		).toArray();
		Mesh mesh = new Mesh(vertices, indices, List.of(VertexAttribute.POSITION, VertexAttribute.NORMAL));
		model = new SingleModel(List.of(mesh));
		model.setShaderProgram(shaderProgram);
	}

	public void render() {
		model.render();
	}

	public void setModelMatrix(Matrix4f value) {
		model.setModelMatrix(value);
	}
}
