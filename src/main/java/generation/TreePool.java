package generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.joml.Matrix4f;
import params.ParameterLoader;
import params.Parameters;
import rendering.LevelOfDetail;
import sceneobjects.Tree;

public class TreePool {
	private static final Parameters parameters = ParameterLoader.getParameters();
	private static TreePool instance;

	private List<List<Tree>> trees = new ArrayList<>();

	private TreePool() {
		int numTreeTypes = parameters.sceneObjects.trees.size();
		for (int type = 0; type < numTreeTypes; type++) {
			Parameters.SceneObjects.Tree params = parameters.sceneObjects.trees.get(type);
			List<Tree> modelsForType = new ArrayList<>();
			int numModels = (int) (1 / params.instanceFraction);
			for (int i = 0; i < numModels; i++) {
				modelsForType.add(new Tree(type));
			}
			trees.add(modelsForType);
			System.out.println(numModels + " tree model(s) generated for tree: " + params.name);
		}

	}

	public static TreePool getTreePool() {
		if (instance == null) {
			instance = new TreePool();
		}
		return instance;
	}

	public int getTreeIndex(int type) {
		Random r = parameters.random.generator;
		return r.nextInt(trees.get(type).size());
	}

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
		return trees.get(type).get(index);
	}

	public void renderTreeWithModel(int type, int index, Matrix4f model, LevelOfDetail levelOfDetail, boolean renderForShadows) {
		Tree tree = trees.get(type).get(index);
		tree.setModelMatrix(model);
		tree.render(levelOfDetail, renderForShadows);
	}
}
