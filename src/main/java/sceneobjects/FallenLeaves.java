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

public class FallenLeaves extends InstancedGroundObject {
	// TODO generate leaves from trees list
	public FallenLeaves(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree) {
		super(numberOfTypes, numberOfInstances, regionCentre, regionWidth, quadtree, ParameterLoader.getParameters().sceneObjects.fallenLeaves);
		generate();
	}

	@Override
	Map<LevelOfDetail, List<Mesh>> getMeshes() {
		Mesh leafMesh = new Mesh(Trees.leaf);
		leafMesh.addTexture("diffuseTexture", Textures.leaf);
		leafMesh.addTexture("normalTexture", Textures.leafNormal);
		leafMesh.setShaderProgram(textureShader);
		return Map.of(LevelOfDetail.LOW, List.of(leafMesh));
	}

}
