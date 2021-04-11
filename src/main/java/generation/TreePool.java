package generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.joml.Matrix4f;
import params.ParameterLoader;
import params.Parameters;
import rendering.LevelOfDetail;
import sceneobjects.Tree;

public class TreePool {
	private static final Parameters parameters = ParameterLoader.getParameters();
	private static TreePool instance;

	private final List<List<Tree>> treesByType = new ArrayList<>();
	private final List<Tree.Mask> minMasks = new ArrayList<>();
	private final List<Tree.Mask> maxMasks = new ArrayList<>();

	private TreePool() {
		System.out.println("Calculating tree masks");
		int numTreeTypes = parameters.sceneObjects.trees.size();
		for (int type = 0; type < numTreeTypes; type++) {
			Parameters.SceneObjects.Tree params = parameters.sceneObjects.trees.get(type);
			treesByType.add(new ArrayList<>());
			minMasks.add((new Tree(type, params.minIterations)).getMask());
			maxMasks.add((new Tree(type, params.maxIterations - 1)).getMask());
		}
	}

	public static TreePool getTreePool() {
		if (instance == null) {
			instance = new TreePool();
		}
		return instance;
	}

//	public int getTreeIndex(int type) {
//		Random r = parameters.random.generator;
//		return r.nextInt(treesByType.get(type).size());
//	}

//	public Tree.Reference getTree(int type, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree) {
//		Random r = parameters.random.generator;
//		Parameters.SceneObjects.Tree params = parameters.sceneObjects.trees.get(type);
////		float x = (r.nextFloat() - 0.5f) * regionWidth + regionCentre.x;
////		float z = (r.nextFloat() - 0.5f) * regionWidth + regionCentre.y;
////		float y = quadtree.getHeight(x, z) + params.yOffset;
////		Matrix4f model = new Matrix4f()
////				.identity()
////				.translate(x, y, z);
//		Matrix4f model = new Matrix4f().identity();
//		if (params.pitchVariability > 0) {
//			model = model.rotate(
//							r.nextFloat() * (float) Math.PI * params.pitchVariability,
//							new Vector3f(r.nextFloat(), 0, r.nextFloat()).normalize()
//					);
//		}
//		model = model.rotate(r.nextFloat() * (float) Math.PI * 2, new Vector3f(0, 1, 0))
//				.scale(params.scale * (
//						r.nextFloat() * (params.maxScaleFactor - params.minScaleFactor) + params.minScaleFactor)
//				);
//		int index = r.nextInt(trees.get(type).size());
//		return new Tree.Reference(type, index, new Vector3f(), model);
//	}

	public Tree getTree(int type, int index) {
		return treesByType.get(type).get(index);
	}

	public Tree.Mask getMinimumMask(int type) {
		return minMasks.get(type);
	}

	public Tree.Mask getMaximumMask(int type) {
		return maxMasks.get(type);
	}

	public void renderTreeWithModel(int type, int index, Matrix4f model, LevelOfDetail levelOfDetail, boolean renderForShadows) {
		Tree tree = getTree(type, index);
		tree.setModelMatrix(model);
		tree.render(levelOfDetail, renderForShadows);
	}

	private List<Integer> getIndicesByTypeAndIterations(int type, int iterations) {
		int maxI = treesByType.get(type).size();

		return IntStream.range(0, maxI)
				.filter(i -> treesByType.get(type).get(i).getNumIterations() == iterations)
				.boxed()
				.collect(Collectors.toList());
	}

	public int getTreeIndexWithIterations(int type, int iterations) {
		Parameters.SceneObjects.Tree params = parameters.sceneObjects.trees.get(type);
		List<Integer> treesOfIterationSize = getIndicesByTypeAndIterations(type, iterations);
		if (treesOfIterationSize.size() < params.numPerIterationSize) {
			treesByType.get(type).add(new Tree(type, iterations));
			return treesByType.get(type).size() - 1;
		}
		Random r = parameters.random.generator;
		return treesOfIterationSize.get(r.nextInt(treesOfIterationSize.size()));
	}

	public void printGenerationStatistics() {
		int numTreeTypes = parameters.sceneObjects.trees.size();
		for (int type = 0; type < numTreeTypes; type++) {
			int numModels = treesByType.get(type).size();
			String name = parameters.sceneObjects.trees.get(type).name;
			System.out.println(numModels + " tree model(s) generated for tree: " + name);
		}
	}
}
