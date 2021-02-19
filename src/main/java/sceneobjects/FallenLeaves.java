package sceneobjects;

import static sceneobjects.Trees.LEAF_SCALE;

import generation.TerrainQuadtree;
import modeldata.Mesh;
import modeldata.meshdata.Texture;
import org.joml.Vector2f;

public class FallenLeaves extends InstancedMeshGroundObject {

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
	Mesh getMesh() {
		return Trees.leaf;
	}

	@Override
	Texture getDiffuseTexture() {
		return Textures.leaf;
	}

	@Override
	Texture getNormalTexture() {
		return Textures.leafNormal;
	}
}
