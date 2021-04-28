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
import java.util.stream.Collectors;
import generation.TerrainQuadtree;
import modeldata.LoadedModel;
import modeldata.Model;
import modeldata.meshdata.Mesh;
import org.joml.Vector2f;
import params.ParameterLoader;
import params.Parameters;
import rendering.LevelOfDetail;

public class ExternalModels extends InstancedGroundObject {

	private static final Parameters parameters = ParameterLoader.getParameters();
	private static final List<Model> models = parameters
			.sceneObjects
			.externalModels
			.stream()
			.map(params -> new LoadedModel(params.modelPath, params.texturesDir))
			.collect(Collectors.toList());
	private final int index;

	public ExternalModels(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree, int index) {
		super(numberOfTypes, numberOfInstances, regionCentre, regionWidth, quadtree, parameters.sceneObjects.externalModels.get(index));
		this.index = index;
		generate();
	}

	@Override
	Map<LevelOfDetail, List<Mesh>> getMeshes() {
		List<Mesh> meshes = models.get(index).getMeshes();
//		meshes.forEach(m -> {
//			m.setShaderProgram(instancedNormalTextureShaderProgram);
//			m.addTexture("diffuseTexture", Textures.rock);
//			m.addTexture("normalTexture", Textures.rockNormal);
//		});
		return Map.of(LevelOfDetail.HIGH, meshes);
	}

}
