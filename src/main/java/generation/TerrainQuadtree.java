package generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static rendering.ShaderPrograms.instancedNormalTextureShaderProgram;
import static rendering.ShaderPrograms.instancedTextureShaderProgram;
import static rendering.ShaderPrograms.normalTextureShaderProgram;
import static rendering.ShaderPrograms.textureShaderProgram;

import modeldata.Mesh;
import modeldata.meshdata.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import rendering.LevelOfDetail;
import rendering.ShaderProgram;
import rendering.ShaderPrograms;
import sceneobjects.FallenLeaves;
import sceneobjects.Grass;
import sceneobjects.Rocks;
import sceneobjects.Trees;
import sceneobjects.Twigs;

public class TerrainQuadtree {

	public static final float GROUND_WIDTH = 100f;
	public static final boolean RENDER_OBJECTS = true;
	public static final float DISTANCE_COEFF = 1.7f;
	private static final int NUM_OF_INSTANCED_TREES = (int) (GROUND_WIDTH * GROUND_WIDTH * 0.025);
	private static final int NUM_OF_TWIG_TYPES = 10;
	private static final int NUM_OF_INSTANCED_TWIGS = (int) (GROUND_WIDTH * GROUND_WIDTH * 0.04);
	private static final int NUM_OF_INSTANCED_ROCKS = (int) (GROUND_WIDTH * GROUND_WIDTH * 0.01);
	private static final int NUM_OF_INSTANCED_GRASS = (int) (GROUND_WIDTH * GROUND_WIDTH * 2.30);
	private static final int NUM_OF_INSTANCED_LEAVES = (int) (GROUND_WIDTH * GROUND_WIDTH * 1.30);

	private final Quad quad;
	private final int maxDepth;
	private final int verticesPerTile;
	private final int numberOfTilesAcross;
	private final int numberOfMaxDepthTiles;
	private final float textureWidth;
	private final TerrainGenerator terrainGenerator = new TerrainGenerator();
	private final Texture texture;
	private Vector2f seedPoint = new Vector2f(0, 0);
	private int nodeCount = 0;

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
	}

	public float getHeight(float x, float z) {
		return terrainGenerator.getHeight(x, z);
	}

	public void render(Boolean useNormalMapping, Matrix4f MVP) {
		List<Quad> tiles = quad.getVisibleQuads(MVP);
		for (Quad tile : tiles) {
			tile.render(useNormalMapping);
		}
	}

	private class Quad {
		private final Vector2f centre;
		private final float width;
		private final int depth;
		private final Mesh mesh;
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
				if (depth == 2) {
					nodeCount += 1;
					System.out.printf("%.2f%% nodes generated %n", nodeCount / Math.pow(4, depth) * 100);
				}
			} else {
				sceneObjects = new SceneObjects();
				children = null;
			}
		}

		private List<Quad> getVisibleQuads(Matrix4f MVP) {

			if (Math.abs(centre.x - seedPoint.x) > DISTANCE_COEFF * width ||
					Math.abs(centre.y - seedPoint.y) > DISTANCE_COEFF * width ||
					this.children == null) {
				if (isOutsideView(MVP)) {
					return List.of();
				}
				return List.of(this);
			}
			return children.stream().flatMap(q -> q.getVisibleQuads(MVP).stream()).collect(Collectors.toList());
		}

		private List<SceneObjects> getSceneObjects() {

			if (children == null) {
				return List.of(sceneObjects);
			}
			return children.stream().flatMap(q -> q.getSceneObjects().stream()).collect(Collectors.toList());
		}


		public void render(Boolean useNormalMapping) {

			mesh.render(ShaderPrograms.textureShaderProgram);

			LevelOfDetail levelOfDetail;
			if (depth > (maxDepth + 1) / 2) {
				levelOfDetail = LevelOfDetail.HIGH;
			} else {
				levelOfDetail = LevelOfDetail.LOW;
			}

			if (RENDER_OBJECTS) {
				for (SceneObjects objects : getSceneObjects()) {
					objects.render(useNormalMapping, levelOfDetail);
				}
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
				rocks = new Rocks(1, getNumber(NUM_OF_INSTANCED_ROCKS), centre, width, TerrainQuadtree.this, false);
				grass = new Grass(1, getNumber(NUM_OF_INSTANCED_GRASS), centre, width, TerrainQuadtree.this, false);
			}

			private void render(Boolean useNormalMapping, LevelOfDetail levelOfDetail) {

				ShaderProgram textureProgram = useNormalMapping ? normalTextureShaderProgram : textureShaderProgram;
				ShaderProgram instanceProgram = useNormalMapping ? instancedNormalTextureShaderProgram : instancedTextureShaderProgram;

				trees.render(instanceProgram, levelOfDetail);

				twigs.render(instanceProgram);
				rocks.render(instancedTextureShaderProgram, levelOfDetail);
				// TODO fix grass alpha issue, render order needs to depend on view direction (depth from camera not a static position)
				//		may also be affected by order in relation to terrain tiles
				grass.render(ShaderPrograms.billboardShaderProgram, levelOfDetail);

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
