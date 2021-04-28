/*
 * Copyright (c) 2021 Callum Newlands
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     The additional term of 7.b applies: Requiring preservation of specified
 *     reasonable legal notices or author attributions in that material or in the
 *     Appropriate Legal Notices displayed by works containing it
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
import org.joml.Vector2f;
import org.joml.Vector3f;
import params.ParameterLoader;
import params.Parameters;

public class TerrainGenerator {

	private static final Parameters parameters = ParameterLoader.getParameters();
	private static final int NO_OF_OCTAVES = parameters.terrain.noise.octaves;
	private static final float PERSISTENCE = parameters.terrain.noise.persistence;
	private static final float LACUNARITY = parameters.terrain.noise.lacunarity;
	private static final float NOISE_SCALE_X = parameters.terrain.noise.xScale;
	private static final float NOISE_SCALE_Y = parameters.terrain.noise.yScale;
	private static final float VERTICAL_SCALE = parameters.terrain.verticalScale;
	private final FastNoiseLite noiseGenerator;

	public TerrainGenerator() {
		int seed = (int) parameters.random.seed;
		seed = seed == -1 ? (new Random()).nextInt() : seed;
		noiseGenerator = new FastNoiseLite(seed);
		noiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
		noiseGenerator.SetFractalType(FastNoiseLite.FractalType.FBm);
		noiseGenerator.SetFractalOctaves(NO_OF_OCTAVES);
		noiseGenerator.SetFractalLacunarity(LACUNARITY);
		noiseGenerator.SetFractalGain(PERSISTENCE);
	}

	private float round(float f) {
		int precision = 100;
		return Math.round(f * precision) / (float) precision;
	}

	// Could store this so it doesnt need calculated each time
	public float getHeight(float x, float y) {
		return noiseGenerator.GetNoise(x / NOISE_SCALE_X, y / NOISE_SCALE_Y) * VERTICAL_SCALE;
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
				// First order (linear) approximation to gradient vector (normal)
				Vector3f norm = new Vector3f(
						(r - l) / (2 * gridSize),
						-1,
						(u - d) / (2 * gridSize)).normalize().negate();
				Vector3f tang = new Vector3f(2 * gridSize, r - l, 0).normalize();

				float texX = (xi * textureTilesPerGroundTile) / verticesPerSide;
				float texY = (yi * textureTilesPerGroundTile) / verticesPerSide;

				Vector3f pos = new Vector3f(round(x), round(h), round(y));

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
