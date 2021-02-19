package sceneobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import generation.TerrainQuadtree;
import modeldata.InstancedModel;
import modeldata.Mesh;
import modeldata.meshdata.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import rendering.ShaderProgram;

public abstract class InstancedModelGroundObject {

	private final List<InstancedModel> models = new ArrayList<>();

	public InstancedModelGroundObject(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree, boolean yRotationOnly) {
		for (int i = 0; i < numberOfTypes; i++) {
			List<Mesh> meshes = getMeshes();
			InstancedModel instancedModel = new InstancedModel(meshes, getNumber(numberOfInstances, numberOfTypes));
			List<Texture> diffuseTextures = getDiffuseTextures();
			List<Texture> normalTextures = getNormalTextures();
			instancedModel.addTextures("diffuseTexture", diffuseTextures);
			if (normalTextures != null) {
				instancedModel.addTextures("normalTexture", normalTextures);
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
			models.add(instancedModel);
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

	abstract List<Mesh> getMeshes();

	abstract List<Texture> getDiffuseTextures();

	List<Texture> getNormalTextures() {
		return null;
	}

	public void render(ShaderProgram shaderProgram) {
		for (InstancedModel model : models) {
			model.render(shaderProgram);
		}
	}
}
