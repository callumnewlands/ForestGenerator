package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import models.meshdata.Texture;
import models.meshdata.Vertex;
import models.meshdata.VertexArray;
import models.meshdata.VertexAttribute;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import rendering.ShaderProgram;

@Getter
@Setter
public class Mesh {

	private List<Vertex> vertices;
	private int[] indices;
	private List<VertexAttribute> vertexAttributes;
	private Matrix4f model = new Matrix4f().identity();
	protected Map<String, Texture> textures = new HashMap<>();
	protected VertexArray vertexArray;

	public Mesh(List<Vertex> vertices, int[] indices, List<VertexAttribute> vertexAttributes) {
		this.vertices = vertices;
		this.indices = indices;
		this.vertexAttributes = vertexAttributes;
		this.vertexArray = createVAO();
	}

	public Mesh(Mesh mesh) {
		this.vertices = mesh.vertices;
		this.indices = mesh.indices;
		this.vertexAttributes = mesh.vertexAttributes;
		this.textures = mesh.textures;
		this.model = mesh.model;
		this.vertexArray = createVAO();
	}

	public Mesh(Mesh mesh, List<Vertex> transformedVertices) {
		this.vertices = transformedVertices;
		this.indices = mesh.indices.clone();
		this.vertexAttributes = new ArrayList<>(mesh.vertexAttributes);
		this.textures = new HashMap<>(mesh.textures);
		this.model = new Matrix4f(mesh.model);
		this.vertexArray = createVAO();
	}

	public void addTexture(String uniform, Texture texture) {
		textures.put(uniform, texture);
	}

	private VertexArray createVAO() {
		float[] data = ArrayUtils.toPrimitive(vertices.stream().flatMap(vd -> {
			List<Float> vertexData = new ArrayList<>();
			for (VertexAttribute attribute : vertexAttributes) {
				switch (attribute.getName()) {
					case "position" -> {
						Vector3f v = vd.getPosition();
						vertexData.addAll(List.of(v.x, v.y, v.z));
					}
					case "normal" -> {
						Vector3f n = vd.getNormal();
						vertexData.addAll(List.of(n.x, n.y, n.z));
					}
					case "tangent" -> {
						Vector3f t = vd.getTangent();
						vertexData.addAll(List.of(t.x, t.y, t.z));
					}
					case "texCoord" -> {
						Vector2f t = vd.getTexCoord();
						vertexData.addAll(List.of(t.x, t.y));
					}
					default -> throw new RuntimeException("Unknown attribute: " + attribute.getName());
				}
			}
			return vertexData.stream();
		}).toArray(Float[]::new));

		return new VertexArray(data, vertices.size(), indices, vertexAttributes);

	}

	public void render(ShaderProgram shaderProgram) {
		if (!(this instanceof InstancedMesh)) {
			shaderProgram.setUniform("model", model);
		}
		for (Map.Entry<String, Texture> texture : textures.entrySet()) {
			if (texture.getKey().equals("diffuseTexture")) {
				shaderProgram.setUniform("modelColour", texture.getValue().getColour());
			}
			shaderProgram.setUniform(texture.getKey(), texture.getValue().getUnitID());
			texture.getValue().bind();
		}
		draw();
		for (Texture texture : textures.values()) {
			texture.unbind();
		}
	}

	protected void draw() {
		vertexArray.draw();

	}

}
