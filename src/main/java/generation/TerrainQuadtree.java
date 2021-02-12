package generation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import meshdata.Mesh;
import org.joml.Vector2f;

public class TerrainQuadtree {

	private Quad quad;
	private int maxDepth;
	private int verticesPerTile;
	private float textureWidth;
	private Vector2f seedPoint = new Vector2f(0, 0);
	private TerrainGenerator terrainGenerator = new TerrainGenerator();

	public TerrainQuadtree(Vector2f centre, float width, int maxDepth, int verticesPerTile) {
		this.maxDepth = maxDepth;
		this.verticesPerTile = verticesPerTile;
		this.textureWidth = width / (float) (Math.pow(2, maxDepth));
		this.quad = new Quad(centre, width, 0);
		this.quad.updateChildren();
	}

	public void setSeedPoint(Vector2f seedPoint) {
		this.seedPoint = seedPoint;
		this.quad.updateChildren();
	}

	public List<Mesh> getGroundTiles() {
		return quad.getGroundTiles();
	}

	public float getHeight(float x, float z) {
		return terrainGenerator.getHeight(x, z);
	}

	private class Quad {
		private Vector2f centre;
		private float width;
		private int depth;
		private Mesh mesh;


		private List<Quad> children = null;

		Quad(Vector2f centre, float width, int depth) {
			this.centre = centre;
			this.width = width;
			this.depth = depth;
			this.mesh = terrainGenerator.getGroundTile(centre, width, verticesPerTile, textureWidth);
		}

		boolean containsSeedPoint() {
			float minX = centre.x - width / 2;
			float maxX = centre.x + width / 2;
			float minY = centre.y - width / 2;
			float maxY = centre.y + width / 2;
			float sx = seedPoint.x;
			float sy = seedPoint.y;

			return sx <= maxX && sx >= minX && sy <= maxY && sy >= minY;
		}

		private void generateChildren() {
			children = new ArrayList<>();

			float cx = centre.x;
			float cy = centre.y;
			float w4 = width / 4;
			float w2 = width / 2;

			children.add(new Quad(new Vector2f(cx - w4, cy - w4), w2, depth + 1));
			children.add(new Quad(new Vector2f(cx - w4, cy + w4), w2, depth + 1));
			children.add(new Quad(new Vector2f(cx + w4, cy - w4), w2, depth + 1));
			children.add(new Quad(new Vector2f(cx + w4, cy + w4), w2, depth + 1));
		}

		void updateChildren() {
			if (this.containsSeedPoint() && depth < maxDepth) {
				generateChildren();
				for (Quad child : children) {
					child.updateChildren();
				}
			} else {
				children = null;
			}
		}

		private List<Mesh> getGroundTiles() {
			if (this.children == null) {
				return List.of(mesh);
			}
			return children.stream().flatMap(q -> q.getGroundTiles().stream()).collect(Collectors.toList());
		}
	}
}
