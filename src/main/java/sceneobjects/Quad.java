package sceneobjects;

import java.util.List;
import modeldata.SingleModel;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.Vertex;
import modeldata.meshdata.VertexAttribute;
import org.joml.Vector2f;
import org.joml.Vector3f;
import rendering.ShaderProgram;

public class Quad {
	private final SingleModel model;

	public Quad(ShaderProgram shaderProgram) {
		List<Vertex> vertices = List.of(
				new Vertex(new Vector3f(-1.0f, 1.0f, 0.0f), new Vector2f(0.0f, 1.0f)),
				new Vertex(new Vector3f(-1.0f, -1.0f, 0.0f), new Vector2f(0.0f, 0.0f)),
				new Vertex(new Vector3f(1.0f, 1.0f, 0.0f), new Vector2f(1.0f, 1.0f)),
				new Vertex(new Vector3f(1.0f, -1.0f, 0.0f), new Vector2f(1.0f, 0.0f))
		);

		int[] indices = {0, 1, 3, 0, 2, 3};
		Mesh mesh = new Mesh(vertices, indices, List.of(VertexAttribute.POSITION, VertexAttribute.TEXTURE));
		model = new SingleModel(List.of(mesh));
		model.setShaderProgram(shaderProgram);
	}

	public void render() {
		model.render();
	}
}
