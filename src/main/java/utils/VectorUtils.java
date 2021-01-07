package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector3f;
import rendering.VertexArray;
import rendering.VertexAttribute;

public final class VectorUtils {
	private VectorUtils() {
	}

	public static Vector3f add(final Vector3f vec1, final Vector3f vec2) {
		return (new Vector3f(vec1)).add(vec2);
	}

	public static Vector3f subtract(final Vector3f vec1, final Vector3f vec2) {
		return (new Vector3f(vec1)).sub(vec2);
	}

	public static Vector3f multiply(final float coefficient, final Vector3f vec) {
		return (new Vector3f(vec)).mul(coefficient);
	}

	public static Vector3f cross(final Vector3f vec1, final Vector3f vec2) {
		return (new Vector3f(vec1)).cross(vec2);
	}

	public static VertexArray getVAOWithPos(List<Vector3f> vertices) {
		float[] data = ArrayUtils.toPrimitive(vertices.stream().flatMap(v -> Stream.of(v.x, v.y, v.z)).toArray(Float[]::new));
		int[] indices = IntStream.range(0, (vertices.size() - 4) / 4).flatMap(
				// For each 4 vertices (V_n), construct a cuboid (with V_n as the base and V_{n+1} as the top)
				i -> List.of(
						0, 1, 2,
						2, 3, 0,

						1, 5, 6,
						6, 2, 1,

						7, 6, 5,
						5, 4, 7,

						4, 0, 3,
						3, 7, 4,

						4, 5, 1,
						1, 0, 4,

						3, 2, 6,
						6, 7, 3
				).stream().mapToInt(n -> 4 * i + n)
		).toArray();
		return new VertexArray(data, vertices.size(), indices, List.of(VertexAttribute.POSITION));
	}

	public static VertexArray getVAOWithPosNorm(List<Vector3f> vertices) {
		float[] data = ArrayUtils.toPrimitive(IntStream.range(0, (vertices.size() - 4) / 4).boxed().flatMap(
				// For each 4 vertices (V_n), construct a cuboid (with V_n as the base and V_{n+1} as the top)
				i -> List.of(
						// base
						List.of(0, 3, 2, 1),
						// right
						List.of(0, 1, 5, 4),
						// front
						List.of(1, 2, 6, 5),
						// left
						List.of(2, 3, 7, 6),
						// back
						List.of(3, 0, 4, 7),
						// top
						List.of(4, 5, 6, 7)
				).stream()
						.map(f -> f
								.stream()
								.map(n -> n + 4 * i)
								.collect(Collectors.toList()))// Convert to vertex index
						.flatMap(f -> {
							List<Float> faceData = new ArrayList<>(24);
							for (int n = 0; n < 4; n++) {
								int i0 = f.get(n);
								Vector3f v = vertices.get(i0);
								int i1 = f.get((n + 1) % 4);
								int i2 = f.get((n + 3) % 4);
								Vector3f a1 = VectorUtils.subtract(vertices.get(i1), v).normalize();
								Vector3f a2 = VectorUtils.subtract(vertices.get(i2), v).normalize();
								Vector3f norm = VectorUtils.cross(a2, a1).normalize();
//								Vector3f norm = new Vector3f(v.x, v.y, v.z);
								faceData.addAll(List.of(v.x, v.y, v.z, norm.x, norm.y, norm.z));
							}
							return faceData.stream();
						}) // Create vertex with normals for each face
		).toArray(Float[]::new));
		int[] indices = IntStream.range(0, (data.length / 6 - 4) / 4).flatMap(
				i -> List.of(
						0, 1, 2,
						2, 3, 0
				).stream().mapToInt(n -> 4 * i + n)
		).toArray();

//		data = ArrayUtils.toPrimitive(List.of(
//				0, 0, 0, 0, 0, -1,
//				1, 0, 0, 0, 0, -1,
//				1, 1, 0, 0, 0, -1,
//				0, 1, 0, 0, 0, -1,
//				0, 0, 1, 0, 0, 1,
//				1, 0, 1, 0, 0, 1,
//				1, 1, 1, 0, 0, 1,
//				0, 1, 1, 0, 0, 1,
//				0, 0, 0, -1, 0, 0,
//				1, 0, 0, 1, 0, 0,
//				1, 1, 0, 1, 0, 0,
//				0, 1, 0, -1, 0, 0,
//				0, 0, 1, -1, 0, 0,
//				1, 0, 1, 1, 0, 0,
//				1, 1, 1, 1, 0, 0,
//				0, 1, 1, -1, 0, 0,
//				0, 0, 0, 0, -1, 0,
//				1, 0, 0, 0, -1, 0,
//				1, 1, 0, 0, 1, 0,
//				0, 1, 0, 0, 1, 0,
//				0, 0, 1, 0, -1, 0,
//				1, 0, 1, 0, -1, 0,
//				1, 1, 1, 0, 1, 0,
//				0, 1, 1, 0, 1, 0).stream().map(Integer::floatValue).toArray(Float[]::new));
//
//		indices = ArrayUtils.toPrimitive(ArrayUtils.toArray(
//				4, 5, 6, 4, 6, 7,
//				8, 11, 15, 8, 12, 15,
//				16, 20, 21, 16, 17, 21,
//				9, 10, 14, 9, 13, 14,
//				19, 18, 22, 19, 22, 23,
//				0, 1, 2, 0, 2, 3));

		return new VertexArray(data, data.length / 6, indices, List.of(VertexAttribute.POSITION, VertexAttribute.NORMAL));
	}

}
