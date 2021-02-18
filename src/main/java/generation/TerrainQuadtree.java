package generation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import modeldata.Mesh;
import modeldata.meshdata.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import rendering.ShaderProgram;

public class TerrainQuadtree {

	// TODO swap this to be in terms of a world distance
	public static final float DISTANCE_COEFF = 1.7f;
	private Quad quad;
	private int maxDepth;
	private int verticesPerTile;
	private float textureWidth;
	private Vector2f seedPoint = new Vector2f(0, 0);
	private TerrainGenerator terrainGenerator = new TerrainGenerator();
	private Texture texture;

	public TerrainQuadtree(Vector2f centre, float width, int maxDepth, int verticesPerTile, Texture texture) {
		this.maxDepth = maxDepth;
		this.verticesPerTile = verticesPerTile;
		this.texture = texture;
		this.textureWidth = width / (float) (Math.pow(2, maxDepth));
		this.quad = new Quad(centre, width, 0, texture);
		this.quad.updateChildren();
	}

	public void setSeedPoint(float x, float z) {
		setSeedPoint(new Vector2f(x, z));
	}

	public void setSeedPoint(Vector2f seedPoint) {
		this.seedPoint = seedPoint;
//		this.quad.updateChildren();
	}

	public List<Mesh> getGroundTiles(Matrix4f MVP) {
		return quad.getGroundTiles(MVP);
	}

	public float getHeight(float x, float z) {
		return terrainGenerator.getHeight(x, z);
	}

	public void render(ShaderProgram textureShaderProgram, Matrix4f MVP) {
		for (Mesh groundTile : getGroundTiles(MVP)) {
			groundTile.render(textureShaderProgram);
		}
	}

	private class Quad {
		private Vector2f centre;
		private float width;
		private int depth;
		private Mesh mesh;

		private List<Quad> children = null;

		Quad(Vector2f centre, float width, int depth, Texture texture) {
			this.centre = centre;
			this.width = width;
			this.depth = depth;
			this.mesh = terrainGenerator.getGroundTile(centre, width, verticesPerTile, textureWidth, texture);
		}

		private boolean containsSeedPoint() {
			float minX = centre.x - width / 2;
			float maxX = centre.x + width / 2;
			float minY = centre.y - width / 2;
			float maxY = centre.y + width / 2;
			float sx = seedPoint.x;
			float sy = seedPoint.y;

			return sx <= maxX && sx >= minX && sy <= maxY && sy >= minY;
		}

		private boolean isOutsideView(Matrix4f MVP) {
			return !MVP.testSphere(centre.x, 0, centre.y, width);
		}

		private void generateChildren() {
			children = new ArrayList<>();

			float cx = centre.x;
			float cy = centre.y;
			float w4 = width / 4;
			float w2 = width / 2;

			children.add(new Quad(new Vector2f(cx - w4, cy - w4), w2, depth + 1, texture));
			children.add(new Quad(new Vector2f(cx - w4, cy + w4), w2, depth + 1, texture));
			children.add(new Quad(new Vector2f(cx + w4, cy - w4), w2, depth + 1, texture));
			children.add(new Quad(new Vector2f(cx + w4, cy + w4), w2, depth + 1, texture));
		}

		void updateChildren() {
			if (depth < maxDepth) {
				generateChildren();
				for (Quad child : children) {
					child.updateChildren();
				}
			} else {
				children = null;
			}
		}

		private List<Mesh> getGroundTiles(Matrix4f MVP) {

			if (centre.distance(seedPoint) > DISTANCE_COEFF * width || this.children == null) {
				if (isOutsideView(MVP)) {
					return List.of();
				}
				return List.of(mesh);
			}
			return children.stream().flatMap(q -> q.getGroundTiles(MVP).stream()).collect(Collectors.toList());
		}

	}
}
