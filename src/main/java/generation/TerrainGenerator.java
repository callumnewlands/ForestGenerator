package generation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import meshdata.Mesh;
import meshdata.Vertex;
import meshdata.VertexAttribute;
import org.j3d.texture.procedural.PerlinNoiseGenerator;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class TerrainGenerator {

	private final PerlinNoiseGenerator noiseGenerator;

	private static final int NO_OF_OCTAVES = 8; //4
	private static final float PERSISTANCE = 0.5f;
	private static final float LACUNARITY = 2.0f;
	private static final float NOISE_SCALE = 10.0f;

	public TerrainGenerator() {
		noiseGenerator = new PerlinNoiseGenerator(0);
	}

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

		float[][] heights = new float[verticesPerSide][verticesPerSide];
		for (int yi = 0; yi < verticesPerSide; yi++) {
			float y = lerp(minY, maxY, (float) yi / (verticesPerSide - 1));
			for (int xi = 0; xi < verticesPerSide; xi++) {
				float x = lerp(minX, maxX, (float) xi / (verticesPerSide - 1));

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

	public Mesh getGroundTile(Vector2f centre, float width, int verticesPerSide, float textureWidth) {

		float minX = centre.x - width / 2;
		float maxX = centre.x + width / 2;
		float minY = centre.y - width / 2;
		float maxY = centre.y + width / 2;

		float textureTilesPerGroundTile = width / textureWidth;

		List<Vertex> vertices = new ArrayList<>();

		// Generate heightmap which is 1 vertex outside of the tile
		float[][] heights = getHeightmap(centre, width * (1 + (float) 2 / verticesPerSide), verticesPerSide + 2);

		for (int yi = 0; yi < verticesPerSide; yi++) {
			float y = lerp(minY, maxY, (float) yi / (verticesPerSide - 1));
			for (int xi = 0; xi < verticesPerSide; xi++) {
				float x = lerp(minX, maxX, (float) xi / (verticesPerSide - 1));

				int hx = xi + 1;
				int hy = yi + 1;
				float h = heights[hx][hy];
				float u = heights[hx][hy + 1];
				float d = heights[hx][hy - 1];
				float l = heights[hx - 1][hy];
				float r = heights[hx + 1][hy];
				Vector3f norm = new Vector3f(2 * (r - l), 2 * (d - u), -4).normalize();

				float texX = (xi * textureTilesPerGroundTile) / verticesPerSide;
				float texY = (yi * textureTilesPerGroundTile) / verticesPerSide;

				vertices.add(new Vertex(
						new Vector3f(x, h, y),
						norm,
						new Vector2f(texX, texY)
				));
			}
		}

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
