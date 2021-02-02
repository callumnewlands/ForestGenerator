package meshdata;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;

@AllArgsConstructor
@Getter
@Setter
public class Mesh {

	private List<Vertex> vertices;
	private int[] indices;
	private List<VertexAttribute> vertexAttributes;

	public VertexArray getVAO() {
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

}
