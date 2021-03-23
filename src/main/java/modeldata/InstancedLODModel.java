package modeldata;

import java.util.Map;
import java.util.function.Supplier;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.VertexAttribute;
import modeldata.meshdata.VertexBuffer;
import org.joml.Matrix4f;
import rendering.LevelOfDetail;

public class InstancedLODModel {

	private final int numberOfInstances;
	private final Map<LevelOfDetail, Model> models;

	public InstancedLODModel(Map<LevelOfDetail, Model> models, int numberOfInstances) {
		this.models = models;
		this.numberOfInstances = numberOfInstances;
		for (Model m : models.values()) {
			m.setIsInstanced(true);
		}
	}

	// Generates a model matrix for each instance of the model and assigns the same matrix to each level of detail
	// for the instance
	public void generateModelMatrices(Supplier<Matrix4f> generator) {
		float[] instancedModels = new float[numberOfInstances * 16];
		for (int i = 0; i < numberOfInstances; i++) {
			Matrix4f model = generator.get();
			for (int j = 0; j < 16; j++) {
				float[] mat = new float[16];
				model.get(mat);
				instancedModels[i * 16 + j] = mat[j];
			}
		}

		for (Model m : models.values()) {
			for (Mesh mesh : m.getMeshes()) {
				VertexBuffer instanceModel = new VertexBuffer(numberOfInstances, VertexAttribute.INSTANCE_MODEL);
				instanceModel.setVertexData(instancedModels.clone());
				mesh.getVertexArray().bindVertexBuffer(instanceModel);
				mesh.getVertexArray().setInstanced(true);
			}
		}

	}

	public void render(LevelOfDetail levelOfDetail) {
		if (numberOfInstances == 0) {
			return;
		}
		Model lodModel = models.get(levelOfDetail);
		if (lodModel == null) {
			switch (levelOfDetail) {
				case HIGH -> lodModel = models.get(LevelOfDetail.LOW);
				case LOW -> lodModel = models.get(LevelOfDetail.HIGH);
				default -> throw new RuntimeException("Unhandled LOD: " + levelOfDetail);
			}
		}
		for (Mesh mesh : lodModel.getMeshes()) {
			mesh.render(numberOfInstances);
		}
	}
}
