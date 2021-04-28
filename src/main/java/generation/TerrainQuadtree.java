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
import java.util.Optional;
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
import sceneobjects.Tree;
import sceneobjects.Twigs;

public class TerrainQuadtree {

	private static final Parameters parameters = ParameterLoader.getParameters();
	private static final float GROUND_WIDTH = parameters.terrain.width;
	private static final float DEFAULT_ROCK_DENSITY = 0.02f;
	private static final float DEFAULT_GRASS_DENSITY = 2.30f;
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
		int textureScale = parameters.terrain.textureScale;
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

	public void render(Matrix4f MVP, boolean renderForShadows) {
		List<Quad> tiles = quad.getVisibleQuads(MVP);
		for (Quad tile : tiles) {
			tile.render(renderForShadows);
		}
	}

	public void render(Matrix4f MVP) {
		render(MVP, false);
	}

	public void placeTree(Tree.Reference tree) {
		Vector2f position = new Vector2f(tree.getPosition().x, tree.getPosition().z);
		Optional<LeafQuad> containingLeaf = quad.getLeafQuads().stream()
				.filter(q -> q.containsPoint(position))
				.findFirst();
		if (containingLeaf.isEmpty()) {
			System.out.println("Attempting to place tree outside of quadtree bounds: " + tree);
			return;
		}
		LeafQuad leaf = containingLeaf.get();
		leaf.addTree(tree);
	}

	private class Quad {
		protected final Vector2f centre;
		protected final float width;
		protected final int depth;
		protected final Texture2D texture;
		protected List<Quad> children = null;

		Quad(Vector2f centre, float width, int depth, Texture2D texture) {
			this.centre = centre;
			this.width = width;
			this.depth = depth;
			this.texture = texture;
		}

		protected boolean containsPoint(Vector2f point) {
			float minX = centre.x - width / 2;
			float maxX = centre.x + width / 2;
			float minY = centre.y - width / 2;
			float maxY = centre.y + width / 2;
			float sx = point.x;
			float sy = point.y;

			return sx <= maxX && sx >= minX && sy <= maxY && sy >= minY;
		}

		protected boolean isOutsideView(Matrix4f MVP) {
			if (!parameters.quadtree.frustumCulling) {
				return false;
			}
			return !(MVP.testSphere(centre.x, 0, centre.y, width * 2) || MVP.testSphere(centre.x, width * 2, centre.y, width * 2));
		}

		private void generateChildren() {
			children = new ArrayList<>();

			float cx = centre.x;
			float cy = centre.y;
			float w4 = width / 4;
			float w2 = width / 2;

			if (maxDepth == 0 || depth == maxDepth - 1) {
				children.add(new LeafQuad(new Vector2f(cx - w4, cy - w4), w2, depth + 1, texture));
				children.add(new LeafQuad(new Vector2f(cx - w4, cy + w4), w2, depth + 1, texture));
				children.add(new LeafQuad(new Vector2f(cx + w4, cy - w4), w2, depth + 1, texture));
				children.add(new LeafQuad(new Vector2f(cx + w4, cy + w4), w2, depth + 1, texture));
			} else {
				children.add(new Quad(new Vector2f(cx - w4, cy - w4), w2, depth + 1, texture));
				children.add(new Quad(new Vector2f(cx - w4, cy + w4), w2, depth + 1, texture));
				children.add(new Quad(new Vector2f(cx + w4, cy - w4), w2, depth + 1, texture));
				children.add(new Quad(new Vector2f(cx + w4, cy + w4), w2, depth + 1, texture));
			}
		}

		protected void updateChildren() {
			generateChildren();
			for (Quad child : children) {
				child.updateChildren();
			}
			if (depth == maxDepth - 1) {
				nodeCount += 1;
				System.out.printf("Terrain and ground cover: %.2f%% generated %n", nodeCount / Math.pow(4, depth) * 100);
			}
		}

		protected List<Quad> getVisibleQuads(Matrix4f MVP) {
			// Stop recursion to children if distance from camera > thresholdCoefficient * width
			if (Math.abs(centre.x - seedPoint.x) > parameters.quadtree.thresholdCoefficient * width &&
					Math.abs(centre.y - seedPoint.y) > parameters.quadtree.thresholdCoefficient * width) {
				return isOutsideView(MVP) ? List.of() : List.of(this);
			}
			return children.stream().flatMap(q -> q.getVisibleQuads(MVP).stream()).collect(Collectors.toList());
		}

		protected List<LeafQuad.SceneObjects> getSceneObjects() {
			return children.stream().flatMap(q -> q.getSceneObjects().stream()).collect(Collectors.toList());
		}

		protected List<LeafQuad> getLeafQuads() {
			return children.stream().flatMap(q -> q.getLeafQuads().stream()).collect(Collectors.toList());
		}

		protected List<Mesh> getMeshes() {
			return children.stream().flatMap(q -> q.getMeshes().stream()).collect(Collectors.toList());
		}

		public void render(boolean renderForShadows) {
			for (Mesh mesh : getMeshes()) {
				mesh.render(renderForShadows);
			}

			LevelOfDetail levelOfDetail;
			if (depth >= (maxDepth - 1)) {
				levelOfDetail = LevelOfDetail.HIGH;
			} else {
				levelOfDetail = LevelOfDetail.LOW;
			}

			if (parameters.sceneObjects.display) {
				for (LeafQuad.SceneObjects objects : getSceneObjects()) {
					objects.render(levelOfDetail, renderForShadows);
				}
			}
		}
	}

	private class LeafQuad extends Quad {
		private Mesh mesh = null;
		private SceneObjects sceneObjects = null;

		LeafQuad(Vector2f centre, float width, int depth, Texture2D texture) {
			super(centre, width, depth, texture);
			if (depth == maxDepth) {
				this.mesh = terrainGenerator.getGroundTile(this.centre, this.width, verticesPerTile, textureWidth, this.texture);
				this.mesh.setShaderProgram(textureShader);
			}
		}

		@Override
		protected void updateChildren() {
//				mesh = terrainGenerator.getGroundTile(centre, width, verticesPerTile, textureWidth, texture);
//				mesh.setShaderProgram(textureShaderProgram);
			sceneObjects = new LeafQuad.SceneObjects();
			children = null;
		}

		@Override
		protected List<Quad> getVisibleQuads(Matrix4f MVP) {
			return isOutsideView(MVP) ? List.of() : List.of(this);
		}

		@Override
		public List<SceneObjects> getSceneObjects() {
			return sceneObjects != null ? List.of(sceneObjects) : List.of();
		}

		@Override
		protected List<Mesh> getMeshes() {
			return mesh != null ? List.of(mesh) : List.of();
		}

		@Override
		protected List<LeafQuad> getLeafQuads() {
			return List.of(this);
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

		public void addTree(Tree.Reference tree) {
			sceneObjects.addTree(tree);
		}

		private class SceneObjects {
			private final List<Tree.Reference> trees;
			private final Twigs twigs;
			private final List<ExternalModels> externalModels;
			private final FallenLeaves leaves;
			private final List<CrossedBillboard> billboards;

			public SceneObjects() {
				trees = new ArrayList<>();

				leaves = new FallenLeaves(1, getNumber(NUM_OF_INSTANCED_LEAVES), centre, width, TerrainQuadtree.this);
				twigs = new Twigs(parameters.sceneObjects.twigs.typesPerQuad, getNumber(NUM_OF_INSTANCED_TWIGS), centre, width, TerrainQuadtree.this);

				externalModels = new ArrayList<>();
				int numExternalModels = parameters.sceneObjects.externalModels.size();
				for (int i = 0; i < numExternalModels; i++) {
					int numInstances = (int) (GROUND_WIDTH * GROUND_WIDTH * DEFAULT_ROCK_DENSITY * parameters.sceneObjects.externalModels.get(i).density / numExternalModels);
					externalModels.add(new ExternalModels(1, getNumber(numInstances), centre, width, TerrainQuadtree.this, i));
				}

				billboards = new ArrayList<>();
				int numBillboardTypes = parameters.sceneObjects.crossedBillboards.size();
				for (int i = 0; i < numBillboardTypes; i++) {
					int numInstances = (int) (GROUND_WIDTH * GROUND_WIDTH * DEFAULT_GRASS_DENSITY * parameters.sceneObjects.crossedBillboards.get(i).density / numBillboardTypes);
					billboards.add(new CrossedBillboard(1, getNumber(numInstances), centre, width, TerrainQuadtree.this, i));
				}
			}

			private void render(LevelOfDetail levelOfDetail, boolean renderForShadows) {
				for (Tree.Reference tree : trees) {
					tree.render(levelOfDetail, renderForShadows);
				}
				twigs.render(levelOfDetail, renderForShadows);
				for (ExternalModels model : externalModels) {
					model.render(levelOfDetail, renderForShadows);
				}
				for (CrossedBillboard billboard : billboards) {
					billboard.render(levelOfDetail, renderForShadows);
				}
				leaves.render(levelOfDetail, renderForShadows);
			}

			public void addTree(Tree.Reference tree) {
				trees.add(tree);
			}
		}
	}
}
