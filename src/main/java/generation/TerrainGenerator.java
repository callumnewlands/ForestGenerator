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

		PerlinNoiseGenerator noiseGenerator = new PerlinNoiseGenerator(0);
		float[][] heights = new float[verticesPerSide][verticesPerSide];
		for (int yi = 0; yi < verticesPerSide; yi++) {
			float y = lerp(minY, maxY, (float) yi / verticesPerSide);
			for (int xi = 0; xi < verticesPerSide; xi++) {
				float x = lerp(minX, maxX, (float) xi / verticesPerSide);
				float height = noiseGenerator.noise2(x, y);
				heights[xi][yi] = height;
			}
		}

		for (int yi = 0; yi < verticesPerSide; yi++) {
			float y = lerp(minY, maxY, (float) yi / verticesPerSide);
			for (int xi = 0; xi < verticesPerSide; xi++) {
				float x = lerp(minX, maxX, (float) xi / verticesPerSide);

				vertices.add(new Vertex(
						new Vector3f(x, heights[xi][yi], y),
						new Vector3f(0, 1, 0), // TODO normals,
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
