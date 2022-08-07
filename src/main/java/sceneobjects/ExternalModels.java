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
import modeldata.meshdata.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;
import params.ParameterLoader;
import params.Parameters;
import rendering.LevelOfDetail;
import segmentation.Colour;
import utils.MathsUtils;

public class ExternalModels extends InstancedGroundObject {

	private static final Parameters parameters = ParameterLoader.getParameters();
	private static final List<LoadedModel> models = parameters
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

		for (Mesh mesh : meshes) {
			mesh.setSegColour(Colour.extModel);
		}

		return Map.of(LevelOfDetail.HIGH, meshes);
	}

	public void checkCollisions(Vector3f userPosition) {
		if (parameters.sceneObjects.externalModels.get(index).collidable) {
			for (int i = 0; i < positions.size(); i++) {
				float userRadius = parameters.output.collisions.userRadius;
				float objectHeight = models.get(index).getMaskHeight() * scales.get(i);
				float yPos = positions.get(i).y;
				if (userPosition.y + userRadius < yPos || userPosition.y - userRadius > yPos + objectHeight) {
					return;
				}
				float objectRadius = models.get(index).getMaskRadius() * scales.get(i);
				Vector2f objectPosition = new Vector2f(positions.get(i).x, positions.get(i).z);
				if (MathsUtils.circlesColliding(objectPosition, objectRadius,
						new Vector2f(userPosition.x, userPosition.z), userRadius)) {
					System.out.println("COLLISION: MODEL: type=" + index);
				}
			}
		}
	}
}
