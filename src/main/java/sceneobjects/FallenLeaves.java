package sceneobjects;

import java.util.List;
import java.util.Map;

import static rendering.ShaderPrograms.instancedNormalTextureShaderProgram;

import generation.TerrainQuadtree;
import modeldata.meshdata.Mesh;
import org.joml.Vector2f;
import rendering.LevelOfDetail;
import rendering.Textures;

public class FallenLeaves extends InstancedGroundObject {

	public FallenLeaves(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree, boolean yRotationOnly) {
		super(numberOfTypes, numberOfInstances, regionCentre, regionWidth, quadtree, yRotationOnly);
		generate();
	}

	@Override
	float getScale() {
		return 0.7f; // TODO generate leaves from trees list
	}

	@Override
	float getHeightOffset() {
		return 0;
	}

	@Override
	Map<LevelOfDetail, List<Mesh>> getMeshes() {
		Mesh leafMesh = new Mesh(Trees.leaf);
		leafMesh.addTexture("diffuseTexture", Textures.leaf);
		leafMesh.addTexture("normalTexture", Textures.leafNormal);
		leafMesh.setShaderProgram(instancedNormalTextureShaderProgram);
		return Map.of(LevelOfDetail.LOW, List.of(leafMesh));
	}

}
