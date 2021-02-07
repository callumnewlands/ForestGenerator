package meshdata;

import java.util.ArrayList;
import java.util.List;
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
	private Texture texture;
	private VertexArray vertexArray;

	public Mesh(List<Vertex> vertices, int[] indices, List<VertexAttribute> vertexAttributes) {
		this.vertices = vertices;
		this.indices = indices;
		this.vertexAttributes = vertexAttributes;
		this.vertexArray = createVAO();
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
		shaderProgram.setUniform("model", model);
		if (texture != null) {
			shaderProgram.setUniform("modelColour", texture.getColour());
			shaderProgram.setUniform("diffuseTexture", texture.getUnitID());
			texture.bind();
		}
		vertexArray.draw();
		if (texture != null) {
			texture.unbind();
		}
	}

}
