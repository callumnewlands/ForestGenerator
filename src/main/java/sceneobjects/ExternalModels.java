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
