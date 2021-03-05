package sceneobjects;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import modeldata.SingleModel;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.Texture;
import modeldata.meshdata.Vertex;
import modeldata.meshdata.VertexAttribute;
import org.joml.Vector3f;
import rendering.ShaderProgram;

public class Cube {

	protected final SingleModel model;

	public Cube() {
		List<Vector3f> positions = List.of(
				new Vector3f(-1.0f, 1.0f, -1.0f),
				new Vector3f(-1.0f, -1.0f, -1.0f),
				new Vector3f(1.0f, -1.0f, -1.0f),
				new Vector3f(1.0f, -1.0f, -1.0f),
				new Vector3f(1.0f, 1.0f, -1.0f),
				new Vector3f(-1.0f, 1.0f, -1.0f),

				new Vector3f(-1.0f, -1.0f, 1.0f),
				new Vector3f(-1.0f, -1.0f, -1.0f),
				new Vector3f(-1.0f, 1.0f, -1.0f),
				new Vector3f(-1.0f, 1.0f, -1.0f),
				new Vector3f(-1.0f, 1.0f, 1.0f),
				new Vector3f(-1.0f, -1.0f, 1.0f),

				new Vector3f(1.0f, -1.0f, -1.0f),
				new Vector3f(1.0f, -1.0f, 1.0f),
				new Vector3f(1.0f, 1.0f, 1.0f),
				new Vector3f(1.0f, 1.0f, 1.0f),
				new Vector3f(1.0f, 1.0f, -1.0f),
				new Vector3f(1.0f, -1.0f, -1.0f),

				new Vector3f(-1.0f, -1.0f, 1.0f),
				new Vector3f(-1.0f, 1.0f, 1.0f),
				new Vector3f(1.0f, 1.0f, 1.0f),
				new Vector3f(1.0f, 1.0f, 1.0f),
				new Vector3f(1.0f, -1.0f, 1.0f),
				new Vector3f(-1.0f, -1.0f, 1.0f),

				new Vector3f(-1.0f, 1.0f, -1.0f),
				new Vector3f(1.0f, 1.0f, -1.0f),
				new Vector3f(1.0f, 1.0f, 1.0f),
				new Vector3f(1.0f, 1.0f, 1.0f),
				new Vector3f(-1.0f, 1.0f, 1.0f),
				new Vector3f(-1.0f, 1.0f, -1.0f),

				new Vector3f(-1.0f, -1.0f, -1.0f),
				new Vector3f(-1.0f, -1.0f, 1.0f),
				new Vector3f(1.0f, -1.0f, -1.0f),
				new Vector3f(1.0f, -1.0f, -1.0f),
				new Vector3f(-1.0f, -1.0f, 1.0f),
				new Vector3f(1.0f, -1.0f, 1.0f)
		);

		List<Vertex> vertices = positions.stream().map(Vertex::new).collect(Collectors.toList());
		int[] indices = IntStream.range(0, positions.size()).toArray();
		Mesh mesh = new Mesh(vertices, indices, List.of(VertexAttribute.POSITION));
		model = new SingleModel(List.of(mesh));
	}

	public void render() {
		model.render();
	}

	public void addTexture(String uniform, Texture texture) {
		model.addTextures(uniform, List.of(texture));
	}

	public void setShaderProgram(ShaderProgram shaderProgram) {
		model.setShaderProgram(shaderProgram);
	}
}
