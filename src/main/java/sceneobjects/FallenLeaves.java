package sceneobjects;

import java.util.List;
import java.util.Map;

import static sceneobjects.Trees.LEAF_SCALE;

import generation.TerrainQuadtree;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.Texture2D;
import org.joml.Vector2f;
import rendering.LevelOfDetail;
import rendering.Textures;

public class FallenLeaves extends InstancedGroundObject {

	public FallenLeaves(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree, boolean yRotationOnly) {
		super(numberOfTypes, numberOfInstances, regionCentre, regionWidth, quadtree, yRotationOnly);
	}

	@Override
	float getScale() {
		return LEAF_SCALE;
	}

	@Override
	float getHeightOffset() {
		return 0;
	}

	@Override
	Map<LevelOfDetail, List<Mesh>> getMeshes() {
		return Map.of(LevelOfDetail.LOW, List.of(Trees.leaf));
	}

	@Override
	Map<LevelOfDetail, List<Texture2D>> getDiffuseTextures() {
		return Map.of(LevelOfDetail.LOW, List.of(Textures.leaf));
	}

	@Override
	Map<LevelOfDetail, List<Texture2D>> getNormalTextures() {
		return Map.of(LevelOfDetail.LOW, List.of(Textures.leafNormal));
	}

}
