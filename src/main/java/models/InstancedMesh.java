package models;

import java.util.List;
import java.util.function.Supplier;
import models.meshdata.Vertex;
import models.meshdata.VertexAttribute;
import models.meshdata.VertexBuffer;
import org.joml.Matrix4f;

public class InstancedMesh extends Mesh {

	private int numberOfInstances;

	public InstancedMesh(Mesh mesh, int numberOfInstances) {
		super(mesh);
		this.numberOfInstances = numberOfInstances;
	}

	public InstancedMesh(List<Vertex> vertices, int[] indices, List<VertexAttribute> vertexAttributes, int numberOfInstances) {
		super(vertices, indices, vertexAttributes);
		this.numberOfInstances = numberOfInstances;
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
		VertexBuffer instanceModel = new VertexBuffer(numberOfInstances, VertexAttribute.INSTANCE_MODEL);
		instanceModel.setVertexData(instancedModels);
		this.vertexArray.bindVertexBuffer(instanceModel);
		this.vertexArray.setInstanced(true);
	}

	@Override
	protected void draw() {
		vertexArray.draw(numberOfInstances);
	}
}
