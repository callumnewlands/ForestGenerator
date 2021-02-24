package sceneobjects;

import java.util.List;
import java.util.Map;

import static rendering.ShaderPrograms.instancedNormalTextureShaderProgram;

import generation.TerrainQuadtree;
import modeldata.LoadedModel;
import modeldata.Model;
import modeldata.meshdata.Mesh;
import org.joml.Vector2f;
import rendering.LevelOfDetail;
import rendering.ShaderProgram;
import rendering.Textures;

public class Rocks extends InstancedGroundObject {

	private static final Model rock = new LoadedModel(ShaderProgram.RESOURCES_PATH + "/Rock1.obj");

	public Rocks(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree, boolean yRotationOnly) {
		super(numberOfTypes, numberOfInstances, regionCentre, regionWidth, quadtree, yRotationOnly);
	}

	@Override
	float getScale() {
		return 0.3f;
	}

	@Override
	float getHeightOffset() {
		return -0.1f;
	}

	@Override
	Map<LevelOfDetail, List<Mesh>> getMeshes() {
		List<Mesh> meshes = rock.getMeshes();
		meshes.forEach(m -> {
			m.setShaderProgram(instancedNormalTextureShaderProgram);
			m.addTexture("diffuseTexture", Textures.rock);
			m.addTexture("normalTexture", Textures.rockNormal);
		});
		return Map.of(LevelOfDetail.HIGH, meshes);
	}

}
