package generation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.math3.util.Pair;
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
	private static final int MAX_COUNT = 5000;

	private final TerrainQuadtree quadtree;

	private List<Plant> plants = new ArrayList<>();
	private List<Float> coveredAreaByType = new ArrayList<>();

	public EcosystemSimulation(TerrainQuadtree quadtree) {
		this.quadtree = quadtree;
		init();
		calculateCoveredArea();
	}

	/**
	 * Initialises the simulation by scattering (non-colliding) trees according to density parameters
	 */
	private void init() {
		System.out.println("Simulating ecosystem");
		Random r = parameters.random.generator;
		int numTypes = parameters.sceneObjects.trees.size();

		int indexCount = 0;
		List<List<Integer>> indicesByType = new ArrayList<>();
		for (int type = 0; type < numTypes; type++) {
			Parameters.SceneObjects.Tree params = parameters.sceneObjects.trees.get(type);
			int numTrees = (int) (GROUND_WIDTH * GROUND_WIDTH * DEFAULT_TREE_DENSITY * params.density / numTypes);
			indicesByType.add(IntStream.range(indexCount, indexCount + numTrees).boxed().collect(Collectors.toList()));
			for (int i = 0; i < numTrees; i++) {
				plants.add(new Plant(type, 0));
			}
			indexCount += numTrees;
			coveredAreaByType.add(0f);
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
		for (Integer i : sortedIndices) {
			Plant plant = plants.get(i);
			int count = 0;
			do {
				float x = (r.nextFloat() - 0.5f) * GROUND_WIDTH;
				float z = (r.nextFloat() - 0.5f) * GROUND_WIDTH;
				plant.position = new Vector2f(x, z);
				count += 1;
			} while (collidingCanopies(i) && count < MAX_COUNT);
			if (count == MAX_COUNT) {
				count = 0;
				do {
					float x = (r.nextFloat() - 0.5f) * GROUND_WIDTH;
					float z = (r.nextFloat() - 0.5f) * GROUND_WIDTH;
					plant.position = new Vector2f(x, z);
					count += 1;
				} while (collidingTrunks(i) && count < MAX_COUNT);
				if (count == MAX_COUNT) {
					plants.set(i, null);
					System.out.println("Unable to place tree " + parameters.sceneObjects.trees.get(plant.type) +
							" : Maximum number of attempts reached. Consider reducing density or scale parameters.");
				}
			}
		}

		plants = plants.stream().filter(Objects::nonNull).collect(Collectors.toList());
	}

	public List<Tree.Reference> simulate(int numIterations) {

		int stepsPerYear = parameters.ecosystemSimulation.yearLength;
		for (int i = 0; i < numIterations; i++) {
			if (i % stepsPerYear == 0) {
				List<Plant> newSeeds = new ArrayList<>();
				for (Plant plant : plants) {
					newSeeds.addAll(plant.seed());
				}
				plants.addAll(newSeeds);
			}
			calculateCoveredArea();
			removeColliding();
			// preventing deaths seems to prevent holes in the distribution from large plants dying
			plants = plants.stream().filter(Predicate.not(Plant::isDead)).collect(Collectors.toList());
			plants.forEach(Plant::grow);
			if (i % stepsPerYear == 0) {
				printAreas();
			}
		}

		System.out.println("Generating tree models for " + plants.size() + " plants");
		List<Tree.Reference> trees = plants.stream().map(Plant::toReference).collect(Collectors.toList());
		TreePool.getTreePool().printGenerationStatistics();
		return trees;
	}

	private void printAreas() {
		StringBuilder stringBuilder = new StringBuilder("Areas: ");
		float totalArea = coveredAreaByType.stream().reduce(Float::sum).orElse(0f);
		for (int type = 0; type < coveredAreaByType.size(); type++) {
			Parameters.SceneObjects.Tree params = parameters.sceneObjects.trees.get(type);
			stringBuilder.append(params.name)
					.append(": ")
					.append(String.format("%.4f", coveredAreaByType.get(type) / totalArea))
					.append(", ");
		}
		System.out.println(stringBuilder.toString());
	}

	private void removeColliding() {
		Random r = parameters.random.generator;
		int numPlants = plants.size();
		List<Pair<Integer, Integer>> collidingPlants = IntStream.range(0, numPlants)
				.boxed()
				.flatMap(i1 -> IntStream
						.range(0, i1)
						.mapToObj(i2 -> Pair.create(i1, i2)))
				.filter(pair -> {
					int i1 = pair.getFirst();
					int i2 = pair.getSecond();
					Plant p1 = plants.get(i1);
					Plant p2 = plants.get(i2);
					// Only removes those where a canopy is intersecting with a trunk (not just 2 canopies)
					return collidingTrunks(p1, p2);
				})
				.collect(Collectors.toList());

		List<Integer> removed = new ArrayList<>();
		for (Pair<Integer, Integer> pair : collidingPlants) {
			if (removed.contains(pair.getFirst()) || removed.contains(pair.getSecond())) {
				continue;
			}
			Plant p1 = plants.get(pair.getFirst());
			Plant p2 = plants.get(pair.getSecond());
			float viability1 = p1.getViability();
			float viability2 = p2.getViability();
			removed.add(viability1 > viability2 ? pair.getSecond() : pair.getFirst());
		}
		removed = removed.stream().sorted((i1, i2) -> Integer.compare(i2, i1)).collect(Collectors.toList());
		for (Integer index : removed) {
			plants.remove((int) index);
		}
	}

	private void calculateCoveredArea() {
		for (int type = 0; type < coveredAreaByType.size(); type++) {
			coveredAreaByType.set(type, 0f);
		}
		for (Plant plant : plants) {
			coveredAreaByType.set(plant.type, coveredAreaByType.get(plant.type) + plant.getCoveredArea());
		}
	}

	/**
	 * True if the canopy (cylinder) for plant[index] is intersecting with another canopy (or trunk)
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
			if (collidingCanopies(p1, p2)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * True if the canopy (cylinder)  for p1 is intersecting with the canopy (or trunk) of p2
	 */
	private boolean collidingCanopies(Plant p1, Plant p2) {
		// Canopies colliding
		if (cylindersColliding(p1.position, p1.getCanopyCentreY(), p1.getCanopyXZRadius(), p1.getCanopyYRadius(),
				p2.position, p2.getCanopyCentreY(), p2.getCanopyXZRadius(), p2.getCanopyYRadius())) {
			return true;
		}
		// p1 canopy and p2 trunk
		if (cylindersColliding(p1.position, p1.getCanopyCentreY(), p1.getCanopyXZRadius(), p1.getCanopyYRadius(),
				p2.position, p2.getTrunkCentreY(), p2.getTrunkRadius(), p2.getTrunkCentreY())) {
			return true;
		}
		// p1 trunk and p2 canopy
		return cylindersColliding(p1.position, p1.getTrunkCentreY(), p1.getTrunkRadius(), p1.getTrunkCentreY(),
				p2.position, p2.getCanopyCentreY(), p2.getCanopyXZRadius(), p2.getCanopyYRadius());
	}

	/**
	 * True if the trunk (circle) for plant[index] is intersecting with another canopy or trunk, or vice versa
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
			if (collidingTrunks(p1, p2)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * True if the trunk (circle) for p1 is intersecting with the canopy or trunk of p2, or vice versa
	 */
	private boolean collidingTrunks(Plant p1, Plant p2) {
		// Both trunks (with slack of 50% leaf radius)
		if (circlesColliding(p1.position, (p1.getTrunkRadius() + p1.getCanopyXZRadius()) / 2,
				p2.position, (p2.getTrunkRadius() + p2.getCanopyXZRadius()) / 2)) {
			return true;
		}
		// p1 canopy and p2 trunk
		if (cylindersColliding(p1.position, p1.getCanopyCentreY(), p1.getCanopyXZRadius(), p1.getCanopyYRadius(),
				p2.position, p2.getTrunkCentreY(), p2.getTrunkRadius(), p2.getTrunkCentreY())) {
			return true;
		}
		// p1 trunk and p2 canopy
		return cylindersColliding(p1.position, p1.getTrunkCentreY(), p1.getTrunkRadius(), p1.getTrunkCentreY(),
				p2.position, p2.getCanopyCentreY(), p2.getCanopyXZRadius(), p2.getCanopyYRadius());
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
		private final int maxAge;
		private final int type;
		private final Tree.Mask minMask;
		private final Tree.Mask maxMask;
		private final float modelScale;
		private final float minScaleFactor;
		private final float maxScaleFactor;
		private final Parameters.SceneObjects.Tree treeParams;
		private Vector2f position;
		private int age;

		Plant(int type, int age) {
			this.type = type;
			this.age = age;

			TreePool treePool = TreePool.getTreePool();
			this.minMask = treePool.getMinimumMask(type);
			this.maxMask = treePool.getMaximumMask(type);
			this.treeParams = parameters.sceneObjects.trees.get(type);
			this.maxAge = treeParams.maxAge;
			this.modelScale = treeParams.scale;
			this.minScaleFactor = treeParams.minScaleFactor;
			this.maxScaleFactor = treeParams.maxScaleFactor;
		}

		private float getCurrentFromMinMax(Function<Tree.Mask, Float> property) {
			float min = property.apply(minMask) * minScaleFactor;
			float max = property.apply(maxMask) * maxScaleFactor;
			return (Math.min((float) age / maxAge, 1) * (max - min) + min) * modelScale;
		}

		float getCanopyXZRadius() {
			return getCurrentFromMinMax(Tree.Mask::getCanopyXZRadius);
		}

		float getCanopyYRadius() {
			return getCurrentFromMinMax(Tree.Mask::getCanopyYRadius);
		}

		float getCanopyCentreY() {
			return getCurrentFromMinMax(m -> m.getCanopyCentre().y);
		}

		float getTrunkRadius() {
			return getCurrentFromMinMax(Tree.Mask::getTrunkRadius);
		}

		public float getTrunkCentreY() {
			return (getCanopyCentreY() + getCanopyYRadius()) / 2;
		}

		private float getSeedRadius() {
			return getCanopyXZRadius() * treeParams.seedRadiusMultiplier;
		}

		float getViability() {
			float threshold = parameters.ecosystemSimulation.ageThreshold;
			float x = Math.min((float) age / maxAge, 1);
			float plantViability = x < threshold
					? x / threshold
					: (1 - x) / (1 - threshold);
			float totalSum = coveredAreaByType.stream().reduce(Float::sum).orElse(0f);
			float speciesWeightedViability = (1 - coveredAreaByType.get(type) / totalSum) * plantViability;
			float avgRadius = (float) plants.stream().mapToDouble(Plant::getCanopyXZRadius).average().orElse(0);
			float maxRadius = (float) plants.stream().mapToDouble(Plant::getCanopyXZRadius).max().orElse(0);
			float minRadius = (float) plants.stream().mapToDouble(Plant::getCanopyXZRadius).min().orElse(0);
			float scaledAvgRadius = (avgRadius - minRadius) / (maxRadius - minRadius);
			float scaledRadius = (this.getCanopyXZRadius() - minRadius) / (maxRadius - minRadius);
			float p = parameters.ecosystemSimulation.smallRadiusViability;
			float radiusViability = scaledRadius < scaledAvgRadius
					? (-p) / scaledAvgRadius * scaledRadius + p
					: (scaledRadius - scaledAvgRadius) / (1 - scaledAvgRadius);
			float rW = parameters.ecosystemSimulation.radiusWeight;
			return radiusViability * rW + speciesWeightedViability * (1 - rW);
		}

		float getCoveredArea() {
			float r = getCanopyXZRadius();
			return (float) (Math.PI * r * r);
		}

		Tree.Reference toReference() {
			Random r = parameters.random.generator;
			float x = position.x;
			float z = position.y;
			float y = quadtree.getHeight(x, z) + treeParams.yOffset;
			Matrix4f model = new Matrix4f()
					.identity()
					.translate(x, y, z);
			if (treeParams.pitchVariability > 0) {
				model = model.rotate(
						r.nextFloat() * (float) Math.PI * treeParams.pitchVariability,
						new Vector3f(r.nextFloat(), 0, r.nextFloat()).normalize()
				);
			}

			int maxI = treeParams.maxIterations;
			int minI = treeParams.minIterations;
			int iterationStep = (int) (Math.min((float) age / maxAge, 1) * ((maxI + 1) - minI));
			int iterations = iterationStep + minI;
			float scaleFactor = (Math.min((float) age / maxAge, 1) *
					((maxI + 1) - minI) - iterationStep) *
					(maxScaleFactor - minScaleFactor)
					+ minScaleFactor;

			model = model.rotate(r.nextFloat() * (float) Math.PI * 2, new Vector3f(0, 1, 0))
					.scale(scaleFactor * modelScale);

			TreePool treePool = TreePool.getTreePool();
			int poolIndex = treePool.getTreeIndexWithIterations(type, iterations);

			return new Tree.Reference(type, poolIndex, new Vector3f(x, y, z), model);
		}

		public void grow() {
			this.age += 1;
		}

		public boolean isDead() {
			return age >= maxAge;
		}

		public List<Plant> seed() {
			Random r = parameters.random.generator;
			float trunkRadius2 = getTrunkRadius() * 2;
			float seedRadius = getSeedRadius();
			float seedArea = (float) (Math.PI * seedRadius * seedRadius - Math.PI * trunkRadius2 * trunkRadius2);
			int numSeeds = (int) (seedArea * DEFAULT_TREE_DENSITY * treeParams.density);
			List<Plant> seeds = new ArrayList<>();
			for (int i = 0; i < numSeeds; i++) {
				Plant seed = new Plant(type, 0);
				float angle = (float) (r.nextFloat() * Math.PI * 2);
				float distance = r.nextFloat() * (seedRadius - trunkRadius2) + trunkRadius2;
				float xOffset = distance * (float) Math.cos(angle);
				float zOffset = distance * (float) Math.sin(angle);
				seed.position = new Vector2f(position.x + xOffset, position.y + zOffset);
				if (Math.abs(seed.position.x) <= GROUND_WIDTH / 2 && Math.abs(seed.position.y) <= GROUND_WIDTH / 2) {
					seeds.add(seed);
				}
			}
			return seeds;
		}
	}

}
