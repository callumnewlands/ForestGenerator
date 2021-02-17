package models;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import models.meshdata.VertexAttribute;
import models.meshdata.VertexBuffer;
import org.joml.Matrix4f;

public class InstancedModel extends Model {
	private int numberOfInstances;

	public InstancedModel(List<Mesh> meshes, int numberOfInstances) {
		super(meshes);
		this.numberOfInstances = numberOfInstances;
		this.meshes = this.meshes.stream().map(m -> new InstancedMesh(m, numberOfInstances)).collect(Collectors.toList());
	}

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

		for (Mesh mesh : meshes) {
			VertexBuffer instanceModel = new VertexBuffer(numberOfInstances, VertexAttribute.INSTANCE_MODEL);
			instanceModel.setVertexData(instancedModels);
			mesh.vertexArray.bindVertexBuffer(instanceModel);
			mesh.vertexArray.setInstanced(true);
		}
	}
}
