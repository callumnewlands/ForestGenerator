package sceneobjects;

import java.util.List;
import java.util.Map;
import generation.TerrainQuadtree;
import modeldata.LoadedModel;
import modeldata.Model;
import modeldata.meshdata.Mesh;
import org.joml.Vector2f;
import rendering.LevelOfDetail;

public class Rocks extends InstancedGroundObject {

	private static final Model rock = new LoadedModel("models/Rock1/Rock1.obj", "models/Rock1");

	public Rocks(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree, boolean yRotationOnly) {
		super(numberOfTypes, numberOfInstances, regionCentre, regionWidth, quadtree, yRotationOnly);
		generate();
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
//		meshes.forEach(m -> {
//			m.setShaderProgram(instancedNormalTextureShaderProgram);
//			m.addTexture("diffuseTexture", Textures.rock);
//			m.addTexture("normalTexture", Textures.rockNormal);
//		});
		return Map.of(LevelOfDetail.HIGH, meshes);
	}

}
