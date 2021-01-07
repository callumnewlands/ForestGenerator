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

	public static VertexArray getVAOWithPosNorm(List<Vector3f> vertices, int numEdges) {

		List<List<Integer>> faces = new ArrayList<>();

		// TODO think about not repeating the top and bottom vertices so many times and/or only including them at the ends?
		//		How about adding a close symbol to the turtle which creates a zero width face and then never generating the ends of the prisms?

		// Base
		for (int i = 1; i < numEdges + 1; i++) {
			int j = (i % numEdges) + 1;
			faces.add(List.of(0, i, j));
		}
		// Top
		for (int i = 1; i < numEdges + 1; i++) {
			int j = (i % numEdges) + 1;
			faces.add(List.of(numEdges + 1, i + numEdges + 1, j + numEdges + 1));
		}

		// Sides
		for (int i = 1; i < numEdges + 1; i++) {
			int j = (i % numEdges) + 1;
			faces.add(List.of(i, j, j + numEdges + 1, i + numEdges + 1));
		}

		int numSegments = (vertices.size() - (numEdges + 1)) / (numEdges + 1);
		float[] data = ArrayUtils.toPrimitive(IntStream.range(0, numSegments).boxed().flatMap(
				// For each 'numEdges' vertices (V), construct a prism with (V) as the base
				i -> faces.stream()
						.map(f -> f
								.stream()
								.map(n -> n + (numEdges + 1) * i)
								.collect(Collectors.toList()))// Convert to vertex index
						.flatMap(f -> {
							List<Float> faceData = new ArrayList<>(6 * numEdges);
							int s = f.size();
							// TODO the normal is the same for each corner of the face
							for (int n = 0; n < s; n++) {
								int i0 = f.get(n);
								Vector3f v = vertices.get(i0);
								int i1 = f.get((n + 1) % s);
								int i2 = f.get((n + (s - 1)) % s);
								Vector3f a1 = VectorUtils.subtract(vertices.get(i1), v).normalize();
								Vector3f a2 = VectorUtils.subtract(vertices.get(i2), v).normalize();
								Vector3f norm = VectorUtils.cross(a2, a1).normalize();
								faceData.addAll(List.of(v.x, v.y, v.z, norm.x, norm.y, norm.z));
							}
							return faceData.stream();
						}) // Create vertex with normals for each face
		).toArray(Float[]::new));

		List<Integer> prismIndices = IntStream.range(0, 6 * numEdges).boxed().collect(Collectors.toList()); // base and top
		// sides
		for (int i = 0; i < numEdges; i++) {
			int finalI = i;
			prismIndices.addAll(List.of(0, 1, 2, 2, 3, 0).stream().map(n -> 6 * numEdges + n + 4 * finalI).collect(Collectors.toList()));
		}

		int[] indices = IntStream.range(0, numSegments).boxed().flatMapToInt(
				i -> prismIndices.stream().mapToInt(n -> n + (10 * numEdges) * i)
		).toArray();


		return new VertexArray(data, data.length / 6, indices, List.of(VertexAttribute.POSITION, VertexAttribute.NORMAL));
	}

}
