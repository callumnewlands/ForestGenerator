package sceneobjects;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static rendering.ShaderPrograms.instancedNormalTextureShaderProgram;

import generation.TerrainQuadtree;
import modeldata.LoadedModel;
import modeldata.Model;
import modeldata.meshdata.Mesh;
import org.joml.Vector2f;
import rendering.LevelOfDetail;
import rendering.Textures;

public class Rocks extends InstancedGroundObject {

	private static final Model rock;

	static {
		try {
			rock = new LoadedModel("resources/models/Rock1.obj");
		} catch (IOException e) {
			throw new RuntimeException("Unable to load rocks model: " + e.getLocalizedMessage());
		}
	}

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
