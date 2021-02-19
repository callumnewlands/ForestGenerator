package generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import modeldata.Mesh;
import modeldata.meshdata.Texture;
import modeldata.meshdata.Vertex;
import modeldata.meshdata.VertexAttribute;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import rendering.ShaderProgram;
import sceneobjects.FallenLeaves;
import sceneobjects.Grass;
import sceneobjects.Rocks;
import sceneobjects.Trees;
import sceneobjects.Twigs;

public class TerrainQuadtree {

	public static final float GROUND_WIDTH = 100f;
	public static final boolean RENDER_OBJECTS = true;
	// TODO swap this to be in terms of a world distance
	public static final float DISTANCE_COEFF = 1.7f;
	private static final int NUM_OF_INSTANCED_TREES = RENDER_OBJECTS ? (int) (GROUND_WIDTH * GROUND_WIDTH * 0.025) : 0;
	private static final int NUM_OF_TWIG_TYPES = 10;
	private static final int NUM_OF_INSTANCED_TWIGS = RENDER_OBJECTS ? (int) (GROUND_WIDTH * GROUND_WIDTH * 0.04) : 0;
	private static final int NUM_OF_ROCK_TYPES = 10;
	private static final int NUM_OF_INSTANCED_ROCKS = RENDER_OBJECTS ? (int) (GROUND_WIDTH * GROUND_WIDTH * 0.04) : 0;
	private static final int NUM_OF_INSTANCED_GRASS = RENDER_OBJECTS ? (int) (GROUND_WIDTH * GROUND_WIDTH * 2.20) : 0;
	private static final int NUM_OF_INSTANCED_LEAVES = RENDER_OBJECTS ? (int) (GROUND_WIDTH * GROUND_WIDTH * 1.50) : 0;

	private Quad quad;
	private int maxDepth;
	private int verticesPerTile;
	private int numberOfTilesAcross;
	private int numberOfMaxDepthTiles;
	private float textureWidth;
	private Vector2f seedPoint = new Vector2f(0, 0);
	private TerrainGenerator terrainGenerator = new TerrainGenerator();
	private Texture texture;

	private Vector3f up = new Vector3f(0f, 1f, 0f);
	private Vector3f out = new Vector3f(0f, 0f, 1f);
	private Mesh leaf = new Mesh(
			List.of(
					new Vertex(new Vector3f(0f, 0f, -0.5f), up, out, new Vector2f(0, 0)),
					new Vertex(new Vector3f(1f, 0f, -0.5f), up, out, new Vector2f(0, 1)),
					new Vertex(new Vector3f(1f, 0f, 0.5f), up, out, new Vector2f(1, 1)),
					new Vertex(new Vector3f(0f, 0f, 0.5f), up, out, new Vector2f(1, 0))
			),
			new int[] {0, 1, 3, 1, 2, 3},
			List.of(
					VertexAttribute.POSITION,
					VertexAttribute.NORMAL,
					VertexAttribute.TANGENT,
					VertexAttribute.TEXTURE)
	);


	public TerrainQuadtree(Vector2f centre, float width, int maxDepth, int verticesPerTile, Texture texture) {
		this.maxDepth = maxDepth;
		this.verticesPerTile = verticesPerTile;
		this.texture = texture;
		this.numberOfTilesAcross = (int) (Math.pow(2, maxDepth));
		this.numberOfMaxDepthTiles = (int) Math.pow(2, 2 * maxDepth);
		this.textureWidth = width / (float) numberOfTilesAcross;
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

	public void render(ShaderProgram textureProgram, ShaderProgram instanceProgram, ShaderProgram billboardProgram, Matrix4f MVP) {
		quad.render(textureProgram, instanceProgram, billboardProgram, MVP);
	}

	private class Quad {
		private Vector2f centre;
		private float width;
		private int depth;
		private Mesh mesh;
		private List<Quad> children = null;
		private SceneObjects sceneObjects = null;

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
				sceneObjects = new SceneObjects();
				children = null;
			}
		}

		// TODO filter quads not meshes and then get scene objects (for rendering) from (already fetched) ground tiles list to avoid filtering twice
		//		once this has been done, you'll have a list of quads to render each with an associated depth (LOD) so you can render at a given LOD
		private List<Mesh> getGroundTiles(Matrix4f MVP) {

			if (centre.distance(seedPoint) > DISTANCE_COEFF * width || this.children == null) {
				if (isOutsideView(MVP)) {
					return List.of();
				}
				return List.of(mesh);
			}
			return children.stream().flatMap(q -> q.getGroundTiles(MVP).stream()).collect(Collectors.toList());
		}

		private List<SceneObjects> getSceneObjects(Matrix4f MVP) {

			if (children == null) {
				if (isOutsideView(MVP)) {
					return List.of();
				}
				return List.of(sceneObjects);
			}
			return children.stream().flatMap(q -> q.getSceneObjects(MVP).stream()).collect(Collectors.toList());
		}


		public void render(ShaderProgram textureProgram, ShaderProgram instanceProgram, ShaderProgram billboardProgram, Matrix4f MVP) {
			for (Mesh groundTile : getGroundTiles(MVP)) {
				groundTile.render(textureProgram);
			}

			for (SceneObjects objects : getSceneObjects(MVP)) {
				objects.render(instanceProgram, billboardProgram);
			}

		}

		private int getNumber(int total) {
			float val = (float) total / numberOfMaxDepthTiles;
			if (val < 1) {
				return (new Random()).nextInt(numberOfMaxDepthTiles) < total ? 1 : 0;
			}
			return (int) val;
		}

		private class SceneObjects {
			private final Trees trees;
			private final Twigs twigs;
			private final Rocks rocks;
			private final FallenLeaves leaves;
			private final Grass grass;

			public SceneObjects() {
				trees = new Trees(1, getNumber(NUM_OF_INSTANCED_TREES), centre, width, TerrainQuadtree.this, true);
				leaves = new FallenLeaves(1, getNumber(NUM_OF_INSTANCED_LEAVES), centre, width, TerrainQuadtree.this, false);
				twigs = new Twigs(NUM_OF_TWIG_TYPES, getNumber(NUM_OF_INSTANCED_TWIGS), centre, width, TerrainQuadtree.this, false);
				rocks = new Rocks(NUM_OF_ROCK_TYPES, getNumber(NUM_OF_INSTANCED_ROCKS), centre, width, TerrainQuadtree.this, false);
				grass = new Grass(1, getNumber(NUM_OF_INSTANCED_GRASS), centre, width, TerrainQuadtree.this, false);
			}

			private void render(ShaderProgram instanceProgram, ShaderProgram billboardProgram) {

				trees.render(instanceProgram);
				twigs.render(instanceProgram);
				rocks.render(instanceProgram);
				grass.render(billboardProgram);

				// TODO replace with different shader uniform for texture colouring and add variation to leaves on model
				Vector3f lightCol = new Vector3f(0.74f, 0.37f, 0.27f);
				instanceProgram.setUniform("lightColour", lightCol);
				leaves.render(instanceProgram);
				lightCol = new Vector3f(0.9f);
				instanceProgram.setUniform("lightColour", lightCol);
			}

		}
	}
}
