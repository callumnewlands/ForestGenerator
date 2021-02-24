package sceneobjects;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.lwjgl.opengl.GL11C.GL_LEQUAL;
import static org.lwjgl.opengl.GL11C.GL_LESS;
import static org.lwjgl.opengl.GL11C.glDepthFunc;
import static rendering.ShaderPrograms.skyboxShaderProgram;

import modeldata.SingleModel;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.Vertex;
import modeldata.meshdata.VertexAttribute;
import org.joml.Vector3f;
import rendering.Textures;

public class Skybox {

	private final SingleModel model;

	public Skybox() {
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
		model.addTextures("skyboxTexture", List.of(Textures.skybox3));
		model.setShaderProgram(skyboxShaderProgram);
	}

	public void render() {
		glDepthFunc(GL_LEQUAL);
		model.render();
		glDepthFunc(GL_LESS);
	}
}
