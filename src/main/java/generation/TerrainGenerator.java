package generation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.lwjgl.opengl.GL13C.GL_TEXTURE2;

import meshdata.Mesh;
import meshdata.Texture;
import meshdata.Vertex;
import meshdata.VertexAttribute;
import org.j3d.texture.procedural.PerlinNoiseGenerator;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class TerrainGenerator {

	// TODO remove
	public Texture heightmap;

	private static final int NO_OF_OCTAVES = 4;
	private static final float PERSISTANCE = 0.5f;
	private static final float LACUNARITY = 2.0f;
	private static final float NOISE_SCALE = 4.0f;

	/**
	 * Linear interpolation between x0 and x1 with factor p (in [0, 1])
	 */
	private float lerp(float x0, float x1, float p) {
		return x0 + p * (x1 - x0);
	}

	private float[][] getHeightmap(Vector2f centre, float width, int verticesPerSide) {

		float minX = centre.x - width / 2;
		float maxX = centre.x + width / 2;
		float minY = centre.y - width / 2;
		float maxY = centre.y + width / 2;

		PerlinNoiseGenerator noiseGenerator = new PerlinNoiseGenerator(0);
		float[][] heights = new float[verticesPerSide][verticesPerSide];
		for (int yi = 0; yi < verticesPerSide; yi++) {
			float y = lerp(minY, maxY, (float) yi / verticesPerSide);
			for (int xi = 0; xi < verticesPerSide; xi++) {
				float x = lerp(minX, maxX, (float) xi / verticesPerSide);

				float amplitude = 1.0f;
				float frequency = 1.0f;
				float totalValue = 0.0f;

				for (int i = 0; i < NO_OF_OCTAVES; i++) {
					float scaledX = x / NOISE_SCALE * frequency;
					float scaledY = y / NOISE_SCALE * frequency;
					totalValue += noiseGenerator.noise2(scaledX, scaledY) * amplitude;
					amplitude *= PERSISTANCE;
					frequency *= LACUNARITY;
				}

				heights[xi][yi] = totalValue;
			}
		}
		return heights;
	}

	public Mesh getGroundTile(Vector2f centre, float width, int verticesPerSide) {

		float minX = centre.x - width / 2;
		float maxX = centre.x + width / 2;
		float minY = centre.y - width / 2;
		float maxY = centre.y + width / 2;

		List<Vertex> vertices = new ArrayList<>();

		float[][] heights = getHeightmap(centre, width, verticesPerSide);

		for (int yi = 0; yi < verticesPerSide; yi++) {
			float y = lerp(minY, maxY, (float) yi / verticesPerSide);
			for (int xi = 0; xi < verticesPerSide; xi++) {
				float x = lerp(minX, maxX, (float) xi / verticesPerSide);

				float h = heights[xi][yi];
				Vector3f norm = new Vector3f(0, 1, 0);
				if (xi > 0 && xi < verticesPerSide - 1 && yi > 0 && yi < verticesPerSide - 1) {
					float u = heights[xi][yi + 1];
					float d = heights[xi][yi - 1];
					float l = heights[xi - 1][yi];
					float r = heights[xi + 1][yi];
					norm = new Vector3f(2 * (r - l), 2 * (d - u), -4).normalize();
				}

				vertices.add(new Vertex(
						new Vector3f(x, h, y),
						norm,
						new Vector2f((float) xi / verticesPerSide, (float) yi / verticesPerSide)
				));
			}
		}
		heightmap = new Texture(heights, verticesPerSide, verticesPerSide, GL_TEXTURE2);

		int numVertices = vertices.size();
		int[] indices = IntStream.range(0, (numVertices - verticesPerSide)).boxed().flatMapToInt(
				i -> {
					if ((i + 1) % verticesPerSide == 0) {
						// If at right hand edge of tile
						return IntStream.empty();
					}
					return IntStream.of(i, i + 1, i + verticesPerSide, i + 1, i + verticesPerSide, i + verticesPerSide + 1);
				}
		).toArray();

		return new Mesh(vertices, indices, List.of(VertexAttribute.POSITION, VertexAttribute.NORMAL, VertexAttribute.TEXTURE));
	}

}
