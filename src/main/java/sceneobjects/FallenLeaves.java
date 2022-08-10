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

import static rendering.ShaderPrograms.textureShader;

import generation.TerrainQuadtree;
import modeldata.meshdata.Mesh;
import org.joml.Vector2f;
import params.ParameterLoader;
import rendering.LevelOfDetail;
import rendering.Textures;
import segmentation.Colour;

public class FallenLeaves extends InstancedGroundObject {
	// Could generate leaves from trees list
	public FallenLeaves(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree) {
		super(numberOfTypes, numberOfInstances, regionCentre, regionWidth, quadtree, ParameterLoader.getParameters().sceneObjects.fallenLeaves);
		generate();
	}

	@Override
	Map<LevelOfDetail, List<Mesh>> getMeshes() {
		Mesh leafMesh = new Mesh(Tree.leaf);
		leafMesh.addTexture("diffuseTexture", Textures.leaf);
		leafMesh.addTexture("normalTexture", Textures.leafNormal);
		leafMesh.setSegColour(Colour.fallenLeaves);
		leafMesh.setShaderProgram(textureShader);
		return Map.of(LevelOfDetail.LOW, List.of(leafMesh));
	}

}
