package sceneobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import generation.TerrainQuadtree;
import modeldata.InstancedLODModel;
import modeldata.InstancedLODModelBuilder;
import modeldata.SingleModel;
import modeldata.meshdata.Mesh;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import params.ParameterLoader;
import params.Parameters;
import rendering.LevelOfDetail;

public abstract class InstancedGroundObject {

	private final List<InstancedLODModel> models = new ArrayList<>();
	private final int numberOfTypes;
	private final int numberOfInstances;
	private final Vector2f regionCentre;
	private final float regionWidth;
	private final TerrainQuadtree quadtree;
	private final Parameters.SceneObjects.SceneObject params;
	private boolean generated = false;

	public InstancedGroundObject(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree, Parameters.SceneObjects.SceneObject params) {
		this.numberOfTypes = numberOfTypes;
		this.numberOfInstances = numberOfInstances;
		this.regionCentre = regionCentre;
		this.regionWidth = regionWidth;
		this.quadtree = quadtree;
		this.params = params;
	}

	public void generate() {
		generated = true;
		if (numberOfInstances == 0) {
			return;
		}
		Random r = ParameterLoader.getParameters().random.generator;
		for (int i = 0; i < numberOfTypes; i++) {
			int numberOfThisType = getNumber(numberOfInstances, numberOfTypes);
			if (numberOfThisType == 0) {
				continue;
			}
			Map<LevelOfDetail, List<Mesh>> lodMeshes = getMeshes();
			InstancedLODModelBuilder modelBuilder = new InstancedLODModelBuilder();
			// For each LOD, construct the lod representation for type i
			for (LevelOfDetail lod : lodMeshes.keySet()) {
				List<Mesh> meshes = lodMeshes.get(lod).stream().map(mesh -> new Mesh(mesh, true)).collect(Collectors.toList());
				SingleModel model = new SingleModel(meshes);
				model.setIsInstanced(true);
				modelBuilder.withLODModel(lod, model);
			}
			InstancedLODModel lodModel = modelBuilder.withNumberOfInstances(numberOfThisType).build();
			lodModel.generateModelMatrices(() -> {
				float x = (r.nextFloat() - 0.5f) * regionWidth + regionCentre.x;
				float z = (r.nextFloat() - 0.5f) * regionWidth + regionCentre.y;
				Matrix4f model = new Matrix4f()
						.identity()
						.translate(x, quadtree.getHeight(x, z) + params.yOffset, z);
				if (params.pitchVariability > 0) {
					model = model.rotate(
							r.nextFloat() * (float) Math.PI * params.pitchVariability,
							new Vector3f(r.nextFloat(), 0, r.nextFloat()).normalize()
					);
				}
				return model.rotate(r.nextFloat() * (float) Math.PI * 2, new Vector3f(0, 1, 0))
						.scale(params.scale * (
								r.nextFloat() * (params.maxScaleFactor - params.minScaleFactor) + params.minScaleFactor)
						);
			});
			models.add(lodModel);
		}
	}

	private int getNumber(int instances, int types) {
		Random r = ParameterLoader.getParameters().random.generator;
		float val = (float) instances / types;
		// If fewer than 1 should be present in this quad, generate 1 with probability (instances/types)
		if (val < 1) {
			return r.nextInt(types) < instances ? 1 : 0;
		}
		return (int) val;
	}

	abstract Map<LevelOfDetail, List<Mesh>> getMeshes();

	public void render(LevelOfDetail levelOfDetail, boolean renderForShadows) {
		if (!generated) {
			throw new RuntimeException("Model is attempting to be rendered without being generated. Is there a subclass constructor which does not call generate()?");
		}
		for (InstancedLODModel model : models) {
			model.render(levelOfDetail, renderForShadows);
		}
	}
}
