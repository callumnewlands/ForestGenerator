package utils;

import java.util.List;
import java.util.stream.Collectors;
import models.Mesh;
import models.meshdata.Vertex;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class MeshUtils {
	private MeshUtils() {
	}

	public static List<Vertex> transform(List<Vertex> vertices, Matrix4f model) {
		if (vertices.get(0).getTangent() != null) {
			return vertices.stream()
					.map(v -> new Vertex(
							model.transformPosition(new Vector3f(v.getPosition())),
							model.transformDirection(new Vector3f(v.getNormal())),
							model.transformDirection(new Vector3f(v.getTangent())),
							v.getTexCoord()))
					.collect(Collectors.toList());
		}
		return vertices.stream()
				.map(v -> new Vertex(
						model.transformPosition(new Vector3f(v.getPosition())),
						model.transformDirection(new Vector3f(v.getNormal())),
						v.getTexCoord()))
				.collect(Collectors.toList());
	}

	public static Mesh transform(Mesh mesh, Matrix4f model) {
		List<Vertex> transformedVertices = transform(mesh.getVertices(), model);
		return new Mesh(mesh, transformedVertices);
	}

}
