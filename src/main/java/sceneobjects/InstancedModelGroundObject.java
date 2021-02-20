package sceneobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import generation.TerrainQuadtree;
import modeldata.InstancedModel;
import modeldata.Mesh;
import modeldata.meshdata.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import rendering.LevelOfDetail;
import rendering.ShaderProgram;

public abstract class InstancedModelGroundObject {

	private final Map<LevelOfDetail, List<InstancedModel>> models = new HashMap<>();

	// TODO same model matrices for different LODs
	public InstancedModelGroundObject(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree, boolean yRotationOnly) {
		for (int i = 0; i < numberOfTypes; i++) {
			Map<LevelOfDetail, List<Mesh>> meshes = getMeshes();
			Map<LevelOfDetail, List<Texture>> diffuseTextures = getDiffuseTextures();
			Map<LevelOfDetail, List<Texture>> normalTextures = getNormalTextures();
			for (Map.Entry<LevelOfDetail, List<Mesh>> entry : meshes.entrySet()) {
				LevelOfDetail lod = entry.getKey();
				InstancedModel instancedModel = new InstancedModel(entry.getValue(), getNumber(numberOfInstances, numberOfTypes));
				instancedModel.addTextures("diffuseTexture", diffuseTextures.get(lod));
				if (normalTextures != null) {
					instancedModel.addTextures("normalTexture", normalTextures.get(lod));
				}
				instancedModel.generateModelMatrices(() -> {
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
				models.putIfAbsent(lod, new ArrayList<>(List.of(instancedModel)));
				models.computeIfPresent(lod, (k, v) -> {
					v.add(instancedModel);
					return v;
				});
			}

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
		List<InstancedModel> lodModels = models.get(levelOfDetail);
		if (lodModels == null || lodModels.size() == 0) {
			switch (levelOfDetail) {
				case HIGH -> lodModels = models.get(LevelOfDetail.LOW);
				case LOW -> lodModels = models.get(LevelOfDetail.HIGH);
			}
		}
		for (InstancedModel model : lodModels) {
			model.render(shaderProgram);
		}
	}
}
