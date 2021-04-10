package generation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import params.ParameterLoader;
import params.Parameters;
import sceneobjects.Tree;

public class EcosystemSimulation {
	private static final Parameters parameters = ParameterLoader.getParameters();
	private static final float GROUND_WIDTH = parameters.terrain.width;
	private static final float DEFAULT_TREE_DENSITY = 0.02f;

	private final TerrainQuadtree quadtree;

	private List<Plant> plants = new ArrayList<>();

	public EcosystemSimulation(TerrainQuadtree quadtree) {
		System.out.println("Simulating ecosystem");
		Random r = parameters.random.generator;
		this.quadtree = quadtree;
		int numTypes = parameters.sceneObjects.trees.size();

		int indexCount = 0;
		List<List<Integer>> indicesByType = new ArrayList<>();
		for (int type = 0; type < numTypes; type++) {
			Parameters.SceneObjects.Tree params = parameters.sceneObjects.trees.get(type);
			int numTrees = (int) (GROUND_WIDTH * GROUND_WIDTH * DEFAULT_TREE_DENSITY * params.density / numTypes);
			indicesByType.add(IntStream.range(indexCount, indexCount + numTrees).boxed().collect(Collectors.toList()));
			for (int i = 0; i < numTrees; i++) {
				plants.add(new Plant(type));
			}
			indexCount += numTrees;
		}

		indicesByType = indicesByType.stream()
				.map(is -> is.stream()
						.sorted(Comparator.comparingDouble(i -> plants.get((int) i).getCanopyXZRadius()).reversed())
						.collect(Collectors.toList()))
				.collect(Collectors.toList());

		int numPlants = indicesByType.stream().map(List::size).reduce(Integer::sum).orElse(0);

		// Sorted by size high -> low then types are interleaved
		List<Integer> sortedIndices = new ArrayList<>();
		while (sortedIndices.size() < numPlants) {
			for (List<Integer> typeIIndices : indicesByType) {
				if (typeIIndices.size() > 0) {
					sortedIndices.add(typeIIndices.remove(0));
				}
			}
		}

		// Greedy approach
		int MAX_COUNT = 5000;
		for (Integer i : sortedIndices) {
			Plant plant = plants.get(i);
			int count = 0;
			do {
				float x = (r.nextFloat() - 0.5f) * GROUND_WIDTH;
				float z = (r.nextFloat() - 0.5f) * GROUND_WIDTH;
				plants.get(i).position = new Vector2f(x, z);
				count += 1;
			} while (collidingCanopies(i) && count < MAX_COUNT);
			if (count == MAX_COUNT) {
				count = 0;
				do {
					float x = (r.nextFloat() - 0.5f) * GROUND_WIDTH;
					float z = (r.nextFloat() - 0.5f) * GROUND_WIDTH;
					plants.get(i).position = new Vector2f(x, z);
					count += 1;
				} while (collidingTrunks(i) && count < MAX_COUNT);
//				if (count != MAX_COUNT) {
//					System.out.println("Tree " + parameters.sceneObjects.trees.get(plant.type) + " placed with intersecting canopy");
//				}
				if (count == MAX_COUNT) {
					plants.set(i, null);
					System.out.println("Unable to place tree " + parameters.sceneObjects.trees.get(plant.type) + " : Maximum number of attempts reached");
				}
			}
		}

		plants = plants.stream().filter(Objects::nonNull).collect(Collectors.toList());
	}

	public List<Tree.Reference> simulate(int numIterations) {
		System.out.println("Generating tree models");
		List<Tree.Reference> trees = plants.stream().map(Plant::toReference).collect(Collectors.toList());
		TreePool.getTreePool().printGenerationStatistics();
		return trees;
	}

	/**
	 * True if the canopy (cylinder) for plant[index] is intersecting with another canopy or trunk
	 */
	private boolean collidingCanopies(int index) {
		Plant p1 = plants.get(index);
		for (int i = 0; i < plants.size(); i++) {
			if (i == index) {
				continue;
			}
			Plant p2 = plants.get(i);
			if (p2 == null || p2.position == null) {
				continue;
			}
			// Canopies colliding
			if (cylindersColliding(p1.position, p1.getCanopyCentreY(), p1.getCanopyXZRadius(), p1.getCanopyYRadius(),
					p2.position, p2.getCanopyCentreY(), p2.getCanopyXZRadius(), p2.getCanopyYRadius())) {
				return true;
			}
			// p1 canopy and p2 trunk
			if (circlesColliding(p1.position, p1.getCanopyXZRadius(), p2.position, p2.getTrunkRadius())) {
				return true;
			}
			// p1 trunk and p2 canopy
			if (circlesColliding(p1.position, p1.getTrunkRadius(), p2.position, p2.getCanopyXZRadius())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * True if the trunk (circle) for plant[index] is intersecting with another canopy or trunk
	 */
	private boolean collidingTrunks(int index) {
		Plant p1 = plants.get(index);
		for (int i = 0; i < plants.size(); i++) {
			if (i == index) {
				continue;
			}
			Plant p2 = plants.get(i);
			if (p2 == null || p2.position == null) {
				continue;
			}
			// Both trunks (with slack of 50% leaf radius)
			if (circlesColliding(p1.position, (p1.getTrunkRadius() + p1.getCanopyXZRadius()) / 2,
					p2.position, (p2.getTrunkRadius() + p2.getCanopyXZRadius()) / 2)) {
				return true;
			}
			// p1 canopy and p2 trunk
			if (circlesColliding(p1.position, p1.getCanopyXZRadius(), p2.position, p2.getTrunkRadius())) {
				return true;
			}
			// p1 trunk and p2 canopy
			if (circlesColliding(p1.position, p1.getTrunkRadius(), p2.position, p2.getCanopyXZRadius())) {
				return true;
			}
		}
		return false;
	}


	private boolean circlesColliding(Vector2f centre1, float radius1, Vector2f centre2, float radius2) {
		float distanceSquared = centre1.distanceSquared(centre2);
		float radiusSum = radius1 + radius2;
		return (distanceSquared < (radiusSum * radiusSum));
	}

	private boolean cylindersColliding(Vector2f centre1, float centre1Y, float radius1, float height1,
									   Vector2f centre2, float centre2Y, float radius2, float height2) {
		float distanceSquared = centre1.distanceSquared(centre2);
		float radiusSum = radius1 + radius2;
		if (distanceSquared < (radiusSum * radiusSum)) {
			float heightDist = Math.abs(centre2Y - centre1Y);
			float ySum = height1 + height2;
			return heightDist < ySum;
		}
		return false;
	}

	private class Plant {
		private final int type;
		private final int age;
		private final int maxAge;
		private final Tree.Mask minMask;
		private final Tree.Mask maxMask;
		private Vector2f position;
		private final float modelScale;
		private final float minScaleFactor;
		private final float maxScaleFactor;

		private Plant(int type) {
			this.type = type;
			this.maxAge = 100; // TODO param
			Random r = parameters.random.generator;
			this.age = r.nextInt(maxAge);

			TreePool treePool = TreePool.getTreePool();
			this.minMask = treePool.getMinimumMask(type);
			this.maxMask = treePool.getMaximumMask(type);
			Parameters.SceneObjects.Tree params = parameters.sceneObjects.trees.get(type);
			this.modelScale = params.scale;
			this.minScaleFactor = params.minScaleFactor;
			this.maxScaleFactor = params.maxScaleFactor;
		}

		private float getCurrentFromMinMax(Function<Tree.Mask, Float> property) {
			float min = property.apply(minMask) * minScaleFactor;
			float max = property.apply(maxMask) * maxScaleFactor;
			return ((float) age / maxAge * (max - min) + min) * modelScale;
		}

		private float getCanopyXZRadius() {
			return getCurrentFromMinMax(Tree.Mask::getCanopyXZRadius);
		}

		private float getCanopyYRadius() {
			return getCurrentFromMinMax(Tree.Mask::getCanopyYRadius);
		}

		private float getCanopyCentreY() {
			return getCurrentFromMinMax(m -> m.getCanopyCentre().y);
		}

		private float getTrunkRadius() {
			return getCurrentFromMinMax(Tree.Mask::getTrunkRadius);
		}


		Tree.Reference toReference() {
			Random r = parameters.random.generator;
			Parameters.SceneObjects.Tree params = parameters.sceneObjects.trees.get(type);
			float x = position.x;
			float z = position.y;
			float y = quadtree.getHeight(x, z) + params.yOffset;
			Matrix4f model = new Matrix4f()
					.identity()
					.translate(x, y, z);
			if (params.pitchVariability > 0) {
				model = model.rotate(
						r.nextFloat() * (float) Math.PI * params.pitchVariability,
						new Vector3f(r.nextFloat(), 0, r.nextFloat()).normalize()
				);
			}

			int maxI = params.maxIterations;
			int minI = params.minIterations;
			int iterationStep = (int) ((float) age / maxAge * ((maxI + 1) - minI));
			int iterations = iterationStep + minI;
			float scaleFactor = ((float) age / maxAge *
					((maxI + 1) - minI) - iterationStep) *
					(maxScaleFactor - minScaleFactor)
					+ minScaleFactor;

			model = model.rotate(r.nextFloat() * (float) Math.PI * 2, new Vector3f(0, 1, 0))
					.scale(scaleFactor * modelScale);

			TreePool treePool = TreePool.getTreePool();
			int poolIndex = treePool.getTreeIndexWithIterations(type, iterations);

			return new Tree.Reference(type, poolIndex, new Vector3f(x, y, z), model);
		}
	}

}
