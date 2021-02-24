package modeldata.meshdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
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
	private boolean isInstanced;
	private ShaderProgram shaderProgram;

	public Mesh(List<Vertex> vertices, int[] indices, List<VertexAttribute> vertexAttributes) {
		this(vertices, indices, vertexAttributes, false);
	}

	public Mesh(List<Vertex> vertices, int[] indices, List<VertexAttribute> vertexAttributes, boolean isInstanced) {
		this.vertices = vertices;
		this.indices = indices;
		this.vertexAttributes = vertexAttributes;
		this.isInstanced = isInstanced;
		this.vertexArray = createVAO();
	}

	public Mesh(Mesh mesh) {
		this(mesh, mesh.vertices);
	}

	public Mesh(Mesh mesh, boolean isInstanced) {
		this(mesh, mesh.vertices, isInstanced);
	}

	public Mesh(Mesh mesh, List<Vertex> transformedVertices) {
		this(mesh, transformedVertices, mesh.isInstanced);
	}

	public Mesh(Mesh mesh, List<Vertex> transformedVertices, boolean isInstanced) {
		this.vertices = transformedVertices;
		this.indices = mesh.indices.clone();
		this.vertexAttributes = new ArrayList<>(mesh.vertexAttributes);
		this.textures = new HashMap<>(mesh.textures);
		this.model = new Matrix4f(mesh.model);
		this.shaderProgram = mesh.shaderProgram;
		this.isInstanced = isInstanced;
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

	private void bindForRender() {
		shaderProgram.use();
		if (!isInstanced) {
			shaderProgram.setUniform("model", model);
		}
		for (Map.Entry<String, Texture> texture : textures.entrySet()) {
			if (texture.getKey().equals("diffuseTexture")) {
				shaderProgram.setUniform("modelColour", texture.getValue().getColour());
			}
			shaderProgram.setUniform(texture.getKey(), texture.getValue().getUnitID());
			texture.getValue().bind();
		}
	}

	private void unbindFromRender() {
		for (Texture texture : textures.values()) {
			texture.unbind();
		}
	}

	public void render() {
		bindForRender();
		draw();
		unbindFromRender();
	}

	public void render(int numberOfInstances) {
		bindForRender();
		draw(numberOfInstances);
		unbindFromRender();
	}

	protected void draw() {
//		if (isInstanced) {
//			vertexArray.draw(1);
//		} else {
		vertexArray.draw();
//		}
	}

	protected void draw(int numberOfInstances) {
		if (isInstanced) {
			vertexArray.draw(numberOfInstances);
		} else {
			throw new RuntimeException("Attempting to draw single mesh as instanced mesh");
		}
	}

}
