package generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static rendering.ShaderPrograms.textureShader;

import modeldata.meshdata.Mesh;
import modeldata.meshdata.Texture2D;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import params.ParameterLoader;
import params.Parameters;
import rendering.LevelOfDetail;
import sceneobjects.CrossedBillboard;
import sceneobjects.ExternalModels;
import sceneobjects.FallenLeaves;
import sceneobjects.Trees;
import sceneobjects.Twigs;

public class TerrainQuadtree {

	private static final Parameters parameters = ParameterLoader.getParameters();
	private static final float GROUND_WIDTH = parameters.terrain.width;
	private static final float DEFAULT_ROCK_DENSITY = 0.02f;
	private static final float DEFAULT_GRASS_DENSITY = 2.30f;
	private static final float DEFAULT_TREE_DENSITY = 0.02f;
	private static final int NUM_OF_INSTANCED_TWIGS = (int) (GROUND_WIDTH * GROUND_WIDTH * 0.04 * parameters.sceneObjects.twigs.density);
	private static final int NUM_OF_INSTANCED_LEAVES = (int) (GROUND_WIDTH * GROUND_WIDTH * 1.30 * parameters.sceneObjects.fallenLeaves.density);

	private final Quad quad;
	private final int maxDepth;
	private final int verticesPerTile;
	private final int numberOfTilesAcross;
	private final int numberOfMaxDepthTiles;
	private final float textureWidth;
	private final TerrainGenerator terrainGenerator = new TerrainGenerator();
	private final Texture2D texture;
	private Vector2f seedPoint = new Vector2f(0, 0);
	private int nodeCount = 0;

	public TerrainQuadtree(Vector2f centre, float width, int maxDepth, int verticesPerTile, Texture2D texture) {
		this.maxDepth = maxDepth;
		this.verticesPerTile = verticesPerTile;
		this.texture = texture;
		this.numberOfTilesAcross = (int) (Math.pow(2, maxDepth));
		this.numberOfMaxDepthTiles = (int) Math.pow(2, 2 * maxDepth);
		// TODO param
		int textureScale = 2;
		this.textureWidth = width / (numberOfTilesAcross * (float) (Math.pow(2, textureScale)));
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

	public void render(Matrix4f MVP) {
		List<Quad> tiles = quad.getVisibleQuads(MVP);
		for (Quad tile : tiles) {
			tile.render();
		}
	}

	private class Quad {
		private final Vector2f centre;
		private final float width;
		private final int depth;
		private final Mesh mesh;
		private List<Quad> children = null;
		private SceneObjects sceneObjects = null;

		Quad(Vector2f centre, float width, int depth, Texture2D texture) {
			this.centre = centre;
			this.width = width;
			this.depth = depth;
			this.mesh = terrainGenerator.getGroundTile(centre, width, verticesPerTile, textureWidth, texture);
			this.mesh.setShaderProgram(textureShader);
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
			if (!parameters.quadtree.frustumCulling) {
				return false;
			}
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
				if (depth == maxDepth - 1) {
					nodeCount += 1;
					System.out.printf("%.2f%% generated %n", nodeCount / Math.pow(4, depth) * 100);
				}
			} else {
//				mesh = terrainGenerator.getGroundTile(centre, width, verticesPerTile, textureWidth, texture);
//				mesh.setShaderProgram(textureShaderProgram);
				sceneObjects = new SceneObjects();
				children = null;
			}
		}

		private List<Quad> getVisibleQuads(Matrix4f MVP) {
			// Stop recursion to children at leaf node or if distance from camera > thresholdCoefficient * width
			if (Math.abs(centre.x - seedPoint.x) > parameters.quadtree.thresholdCoefficient * width ||
					Math.abs(centre.y - seedPoint.y) > parameters.quadtree.thresholdCoefficient * width ||
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
				return sceneObjects != null ? List.of(sceneObjects) : List.of();
			}
			return children.stream().flatMap(q -> q.getSceneObjects().stream()).collect(Collectors.toList());
		}


		private List<Mesh> getMeshes() {

			if (children == null) {
				return mesh != null ? List.of(mesh) : List.of();
			}
			return children.stream().flatMap(q -> q.getMeshes().stream()).collect(Collectors.toList());
		}

		public void render() {

			for (Mesh mesh : getMeshes()) {
				mesh.render();
			}

			LevelOfDetail levelOfDetail;
			if (maxDepth == 0 || maxDepth == 1 || depth > (maxDepth + 1) / 2) {
				levelOfDetail = LevelOfDetail.HIGH;
			} else {
				levelOfDetail = LevelOfDetail.LOW;
			}

			if (parameters.sceneObjects.display) {
				for (SceneObjects objects : getSceneObjects()) {
					objects.render(levelOfDetail);
				}
			}

		}

		private int getNumber(int total) {
			Random r = ParameterLoader.getParameters().random.generator;
			float val = (float) total / numberOfMaxDepthTiles;
			// If fewer than 1 should be present in this quad generate 1 with probability (total/numberOfMaxDepthTiles)
			if (val < 1) {
				return r.nextInt(numberOfMaxDepthTiles) < total ? 1 : 0;
			}
			return (int) val;
		}

		private class SceneObjects {
			private final List<Trees> trees;
			private final Twigs twigs;
			private final List<ExternalModels> externalModels;
			private final FallenLeaves leaves;
			private final List<CrossedBillboard> billboards;

			public SceneObjects() {
				trees = new ArrayList<>();
				int numTreeTypes = parameters.sceneObjects.trees.size();
				for (int i = 0; i < numTreeTypes; i++) {
					int typesPerQuad = (int) (1 / parameters.sceneObjects.trees.get(i).instanceFraction);
					int numInstances = (int) (GROUND_WIDTH * GROUND_WIDTH * DEFAULT_TREE_DENSITY * parameters.sceneObjects.trees.get(i).density / numTreeTypes);
					trees.add(new Trees(typesPerQuad, getNumber(numInstances), centre, width, TerrainQuadtree.this, true, i));
				}

				leaves = new FallenLeaves(1, getNumber(NUM_OF_INSTANCED_LEAVES), centre, width, TerrainQuadtree.this, false);
				twigs = new Twigs(parameters.sceneObjects.twigs.typesPerQuad, getNumber(NUM_OF_INSTANCED_TWIGS), centre, width, TerrainQuadtree.this, false);

				externalModels = new ArrayList<>();
				int numExternalModels = parameters.sceneObjects.externalModels.size();
				for (int i = 0; i < numExternalModels; i++) {
					int numInstances = (int) (GROUND_WIDTH * GROUND_WIDTH * DEFAULT_ROCK_DENSITY * parameters.sceneObjects.externalModels.get(i).density / numExternalModels);
					externalModels.add(new ExternalModels(1, getNumber(numInstances), centre, width, TerrainQuadtree.this, false, i));
				}

				billboards = new ArrayList<>();
				int numBillboardTypes = parameters.sceneObjects.crossedBillboards.size();
				for (int i = 0; i < numBillboardTypes; i++) {
					int numInstances = (int) (GROUND_WIDTH * GROUND_WIDTH * DEFAULT_GRASS_DENSITY * parameters.sceneObjects.crossedBillboards.get(i).density / numBillboardTypes);
					billboards.add(new CrossedBillboard(1, getNumber(numInstances), centre, width, TerrainQuadtree.this, false, i));
				}
			}

			private void render(LevelOfDetail levelOfDetail) {
				for (Trees tree : trees) {
					tree.render(levelOfDetail);
				}
				twigs.render(levelOfDetail);
				for (ExternalModels model : externalModels) {
					model.render(levelOfDetail);
				}
				for (CrossedBillboard billboard : billboards) {
					billboard.render(levelOfDetail);
				}
				leaves.render(levelOfDetail);
			}

		}
	}
}
