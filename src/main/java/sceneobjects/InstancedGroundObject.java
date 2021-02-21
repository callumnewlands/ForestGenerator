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
import modeldata.meshdata.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import rendering.LevelOfDetail;
import rendering.ShaderProgram;

public abstract class InstancedGroundObject {

	private final List<InstancedLODModel> models = new ArrayList<>();

	public InstancedGroundObject(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree, boolean yRotationOnly) {
		for (int i = 0; i < numberOfTypes; i++) {
			Map<LevelOfDetail, List<Mesh>> lodMeshes = getMeshes();
			Map<LevelOfDetail, List<Texture>> diffuseTextures = getDiffuseTextures();
			Map<LevelOfDetail, List<Texture>> normalTextures = getNormalTextures();
			InstancedLODModelBuilder modelBuilder = new InstancedLODModelBuilder();
			// For each LOD, construct the lod representation for type i
			for (LevelOfDetail lod : lodMeshes.keySet()) {
				List<Mesh> meshes = lodMeshes.get(lod).stream().map(mesh -> new Mesh(mesh, true)).collect(Collectors.toList());
				SingleModel model = new SingleModel(meshes);
				model.addTextures("diffuseTexture", diffuseTextures.get(lod));
				if (normalTextures != null) {
					model.addTextures("normalTexture", normalTextures.get(lod));
				}
				model.setIsInstanced(true);
				modelBuilder.withLODModel(lod, model);
			}
			InstancedLODModel lodModel = modelBuilder.withNumberOfInstances(getNumber(numberOfInstances, numberOfTypes)).build();
			lodModel.generateModelMatrices(() -> {
				Random r = new Random();
				float x = (r.nextFloat() - 0.5f) * regionWidth + regionCentre.x;
				float z = (r.nextFloat() - 0.5f) * regionWidth + regionCentre.y;
				Matrix4f model = new Matrix4f()
						.identity()
						.translate(x, quadtree.getHeight(x, z) + getHeightOffset(), z);
				if (!yRotationOnly) {
					model = model.rotate(
							r.nextFloat() * (float) Math.PI / 10,
							new Vector3f(r.nextFloat(), 0, r.nextFloat()).normalize()
					);
				}
				return model.rotate(r.nextFloat() * (float) Math.PI * 2, new Vector3f(0, 1, 0))
						.scale(getScale() * (r.nextFloat() + 0.5f));
			});
			models.add(lodModel);
		}
	}

	private int getNumber(int instances, int types) {
		float val = (float) instances / types;
		if (val < 1) {
			return (new Random()).nextInt(types) < instances ? 1 : 0;
		}
		return (int) val;
	}

	abstract float getScale();

	abstract float getHeightOffset();

	abstract Map<LevelOfDetail, List<Mesh>> getMeshes();

	abstract Map<LevelOfDetail, List<Texture>> getDiffuseTextures();

	Map<LevelOfDetail, List<Texture>> getNormalTextures() {
		return null;
	}

	public void render(ShaderProgram shaderProgram, LevelOfDetail levelOfDetail) {
		for (InstancedLODModel model : models) {
			model.render(shaderProgram, levelOfDetail);
		}
	}
}
