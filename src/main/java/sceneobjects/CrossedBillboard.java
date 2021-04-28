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
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static rendering.ShaderPrograms.billboardShaderProgram;

import generation.TerrainQuadtree;
import lombok.Getter;
import modeldata.meshdata.Mesh;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import params.ParameterLoader;
import params.Parameters;
import rendering.LevelOfDetail;
import rendering.Textures;
import utils.MeshUtils;

public class CrossedBillboard extends InstancedGroundObject {

	private final static Vector3f up = new Vector3f(0f, 1f, 0f);
	private final static Vector3f out = new Vector3f(0f, 0f, 1f);
	@Getter
	private final int index;

	public CrossedBillboard(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree, int index) {
		super(numberOfTypes, numberOfInstances, regionCentre, regionWidth, quadtree, ParameterLoader.getParameters().sceneObjects.crossedBillboards.get(index));
		this.index = index;
		generate();
	}

	private Matrix4f getRandomRotation(float yAngle) {
		Random r = ParameterLoader.getParameters().random.generator;
		return new Matrix4f()
				.rotate(yAngle, up)
				.rotate((float) (r.nextFloat() * Math.PI / 4 + Math.PI / 6),
						new Vector3f(r.nextFloat(), 0, r.nextFloat()).normalize());
	}

	@Override
	Map<LevelOfDetail, List<Mesh>> getMeshes() {
		Parameters.SceneObjects.CrossedBillboard params = ParameterLoader.getParameters().sceneObjects.crossedBillboards.get(index);
		Mesh board = MeshUtils.transform(Tree.leaf, new Matrix4f()
				.rotate((float) Math.PI / 2, out)
				.scale(params.yScale, 1, params.xScale));
		board.addTexture("diffuseTexture", Textures.billboardTextures.get(index));
		board.setShaderProgram(billboardShaderProgram);
		return Map.of(
				LevelOfDetail.HIGH, IntStream.range(0, params.numBoards)
						.mapToObj(i -> MeshUtils.transform(board, getRandomRotation((float) (i * Math.PI / params.numBoards))))
						.collect(Collectors.toList()),
				LevelOfDetail.LOW, List.of(
						new Mesh(board),
						MeshUtils.transform(board, new Matrix4f().rotate((float) Math.PI / 2, up)))
		);
	}

}
