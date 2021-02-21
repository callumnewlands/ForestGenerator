package modeldata;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.VertexAttribute;
import modeldata.meshdata.VertexBuffer;
import org.joml.Matrix4f;
import rendering.ShaderProgram;

public class InstancedModel extends SingleModel {
	private final int numberOfInstances;

	public InstancedModel(Mesh mesh, int numberOfInstances) {
		this(List.of(mesh), numberOfInstances);
	}

	public InstancedModel(List<Mesh> meshes, int numberOfInstances) {
		super(meshes);
		this.numberOfInstances = numberOfInstances;
		this.meshes = this.meshes.stream().map(m -> new Mesh(m, true)).collect(Collectors.toList());
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
			mesh.getVertexArray().bindVertexBuffer(instanceModel);
			mesh.getVertexArray().setInstanced(true);
		}
	}

	@Override
	public void render(ShaderProgram shaderProgram) {
		for (Mesh mesh : meshes) {
			mesh.render(shaderProgram, numberOfInstances);
		}
	}
}
