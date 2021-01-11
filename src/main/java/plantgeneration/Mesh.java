package plantgeneration;

import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector3f;
import rendering.VertexArray;
import rendering.VertexAttribute;

@AllArgsConstructor
@Getter
@Setter
public class Mesh {

	private List<Vertex> vertices;
	private int[] indices;
	private List<VertexAttribute> vertexAttributes;

	public VertexArray getVAO() {
		float[] data = ArrayUtils.toPrimitive(vertices.stream().flatMap(vd -> {
			Vector3f v = vd.getPosition();
			Vector3f n = vd.getNormal();
			return Stream.of(v.x, v.y, v.z, n.x, n.y, n.z);
		}).toArray(Float[]::new));

		return new VertexArray(data, vertices.size(), indices, vertexAttributes);

	}

}
