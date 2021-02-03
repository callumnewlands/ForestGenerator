package generation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import meshdata.Mesh;
import meshdata.Vertex;
import meshdata.VertexAttribute;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class PerlinNoiseGenerator {

	// As defined by Ken Perlin
	private static final int[] permutationLookup = {151, 160, 137, 91, 90, 15,
			131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23,
			190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33,
			88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166,
			77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244,
			102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196,
			135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123,
			5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42,
			223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9,
			129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228,
			251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107,
			49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254,
			138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180
	};

	private static final List<Vector2f> gradients = List.of(
			new Vector2f(0, 1),
			new Vector2f(0, -1),
			new Vector2f(1, 0),
			new Vector2f(-1, 0)
	);

	private int[] p = new int[512];

	public PerlinNoiseGenerator() {
		for (int i = 0; i < 512; i++) {
			p[i] = permutationLookup[i % 256];
		}
	}

	private float fade(float t) {
		// 6t^5 - 15t^4 + 10t^3 = t^3(t(6t - 15) + 10)
		return t * t * t * (t * (6 * t - 15) + 10);
	}

	private float grad(int hash, float dx, float dy) {
		// TODO no idea if this is correct
		Vector2f gradient = gradients.get(hash % 4);
		Vector2f corner = new Vector2f(dx, dy);
		return gradient.dot(corner);
	}

	private float noiseValue(float x, float y) {

		int xi = (int) x & 255;
		int yi = (int) y & 255;

		float dx = x - (int) x;
		float dy = y - (int) y;

		float u = fade(dx);
		float v = fade(dy);

		int aa = p[p[xi] + yi];
		int ab = p[p[xi] + yi + 1];
		int ba = p[p[xi + 1] + yi];
		int bb = p[p[xi + 1] + yi + 1];

		float n0 = lerp(
				grad(aa, dx, dy),
				grad(ba, dx - 1, dy),
				u);
		float n1 = lerp(
				grad(ab, dx, dy - 1),
				grad(bb, dx - 1, dy - 1),
				u);
		return lerp(n0, n1, v);
	}

	/**
	 * Linear interpolation between x0 and x1 with factor p (in [0, 1])
	 */
	private float lerp(float x0, float x1, float p) {
		return x0 + p * (x1 - x0);
	}

	public Mesh getGroundTile(Vector2f centre, float width, int verticesPerSide) {

		float minX = centre.x - width / 2;
		float maxX = centre.x + width / 2;
		float minY = centre.y - width / 2;
		float maxY = centre.y + width / 2;

		List<Vertex> vertices = new ArrayList<>();

		for (int yi = 0; yi < verticesPerSide; yi++) {
			float y = lerp(minY, maxY, (float) yi / verticesPerSide);
			for (int xi = 0; xi < verticesPerSide; xi++) {
				float x = lerp(minX, maxX, (float) xi / verticesPerSide);
				float height = noiseValue(x, y);
				vertices.add(new Vertex(
						new Vector3f(x, height, y),
						new Vector3f(0, 1, 0) // TODO normals
				));
			}
		}

		int numVertices = verticesPerSide * verticesPerSide;
		int[] indices = IntStream.range(0, (numVertices - verticesPerSide)).boxed().flatMapToInt(
				i -> {
					if ((1 + 1) % verticesPerSide == 0) {
						// If at right hand edge of tile
						return IntStream.empty();
					}
					return IntStream.of(i, i + 1, i + verticesPerSide, i + verticesPerSide + 1);
				}
		).toArray();

		return new Mesh(vertices, indices, List.of(VertexAttribute.POSITION, VertexAttribute.NORMAL));
	}

}
