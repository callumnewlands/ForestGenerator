package generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static utils.MathsUtils.lerp;

import modeldata.meshdata.Mesh;
import modeldata.meshdata.Texture2D;
import modeldata.meshdata.Vertex;
import modeldata.meshdata.VertexAttribute;
import org.j3d.texture.procedural.PerlinNoiseGenerator;
import org.joml.Vector2f;
import org.joml.Vector3f;
import params.ParameterLoader;
import params.Parameters;
import utils.VectorUtils;

public class TerrainGenerator {

	private final PerlinNoiseGenerator noiseGenerator;

	private static final Parameters parameters = ParameterLoader.getParameters();
	private static final int NO_OF_OCTAVES = parameters.terrain.noise.octaves;
	private static final float PERSISTENCE = parameters.terrain.noise.persistence;
	private static final float LACUNARITY = parameters.terrain.noise.lacunarity;
	// TODO horizontal scale
	private static final float NOISE_SCALE = parameters.terrain.noise.scale;

	public TerrainGenerator() {
		int seed = (int) parameters.random.seed;
		seed = seed == -1 ? (new Random()).nextInt() : seed;
		// TODO this uses Math.random() which does not take into account the seed value
		noiseGenerator = new PerlinNoiseGenerator(seed);
	}

	private float round(float f) {
		int precision = 100;
		return Math.round(f * precision) / (float) precision;
	}

	public float getHeight(float x, float y) {
		float amplitude = 1.0f;
		float frequency = 1.0f;
		float totalValue = 0.0f;

		for (int i = 0; i < NO_OF_OCTAVES; i++) {
			float scaledX = x / NOISE_SCALE * frequency;
			float scaledY = y / NOISE_SCALE * frequency;
			totalValue += noiseGenerator.noise2(scaledX, scaledY) * amplitude;
			amplitude *= PERSISTENCE;
			frequency *= LACUNARITY;
		}
		return totalValue;
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
				heights[xi][yi] = getHeight(x, y);
			}
		}
		return heights;
	}

	public Mesh getGroundTile(Vector2f centre, float width, int verticesPerSide, float textureWidth, Texture2D texture) {

		float minX = centre.x - width / 2;
		float maxX = centre.x + width / 2;
		float minY = centre.y - width / 2;
		float maxY = centre.y + width / 2;

		float textureTilesPerGroundTile = width / textureWidth;

		List<Vertex> vertices = new ArrayList<>();

		// Generate heightmap which is 1 vertex outside of the tile
		float[][] heights = getHeightmap(centre, width * (1 + (float) 2 / verticesPerSide), verticesPerSide + 2);

		float gridSize = width / verticesPerSide;
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
				Vector3f norm = new Vector3f((r - l) / (2 * gridSize), (d - u) / (2 * gridSize), -1).normalize();

				float texX = (xi * textureTilesPerGroundTile) / verticesPerSide;
				float texY = (yi * textureTilesPerGroundTile) / verticesPerSide;

				Vector3f pos = new Vector3f(round(x), round(h), round(y));
				Vector3f right = new Vector3f(lerp(minX, maxX, (float) hx / (verticesPerSide - 1)), h, y);
				Vector3f tang = VectorUtils.subtract(right, pos);

				vertices.add(new Vertex(
						pos,
						norm,
						tang,
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

		Mesh mesh = new Mesh(vertices, indices, List.of(VertexAttribute.POSITION, VertexAttribute.NORMAL, VertexAttribute.TANGENT, VertexAttribute.TEXTURE));
		mesh.addTexture("diffuseTexture", texture);
		return mesh;
	}

}
