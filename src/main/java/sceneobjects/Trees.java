package sceneobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static lsystems.modules.DefinedModules.LB;
import static lsystems.modules.DefinedModules.RB;
import static rendering.ShaderPrograms.billboardShaderProgram;
import static rendering.ShaderPrograms.instancedLeafShaderProgram;
import static rendering.ShaderPrograms.instancedNormalTextureShaderProgram;

import generation.TerrainQuadtree;
import generation.TurtleInterpreter;
import lsystems.LSystem;
import lsystems.Production;
import lsystems.ProductionBuilder;
import lsystems.modules.CharModule;
import lsystems.modules.Module;
import lsystems.modules.ParametricExpressionModule;
import lsystems.modules.ParametricParameterModule;
import lsystems.modules.ParametricValueModule;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.Vertex;
import modeldata.meshdata.VertexAttribute;
import org.apache.commons.lang3.NotImplementedException;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import params.ParameterLoader;
import params.Parameters;
import rendering.LevelOfDetail;
import rendering.Textures;
import utils.MeshUtils;

public class Trees extends InstancedGroundObject {

	private final int index;
	private static final Parameters parameters = ParameterLoader.getParameters();

	// TODO add leaf properties to yaml
	public static final float LEAF_SCALE = 0.7f;
	private final static Vector3f up = new Vector3f(0f, 1f, 0f);
	private final static Vector3f out = new Vector3f(0f, 0f, 1f);
	public final static Mesh leaf = new Mesh(
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

	public Trees(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree, boolean yRotationOnly, int index) {
		super(numberOfTypes, numberOfInstances, regionCentre, regionWidth, quadtree, yRotationOnly);
		this.index = index;
	}

	@Override
	float getScale() {
		return parameters.sceneObjects.trees.get(index).scale;
	}

	@Override
	float getHeightOffset() {
		return 0;
	}

	@Override
	Map<LevelOfDetail, List<Mesh>> getMeshes() {
		Parameters.SceneObjects.Tree params = parameters.sceneObjects.trees.get(index);
		int numEdges = params.numSides;
		TurtleInterpreter turtleInterpreter = new TurtleInterpreter(numEdges);
		turtleInterpreter.setSubModels(List.of(MeshUtils.transform(leaf, new Matrix4f().scale(LEAF_SCALE / params.scale))));
		turtleInterpreter.setIgnored(List.of('A'));
		Random r = ParameterLoader.getParameters().random.generator;
		int minI = params.minIterations;
		int maxI = params.maxIterations;
		List<Module> instructions = treeSystem().performDerivations(r.nextInt(maxI - minI) + minI);
		turtleInterpreter.interpretInstructions(instructions
				.stream()
				.map(m -> m.getName() == 'A' ? new ParametricValueModule('~', 0f) : m)
				.collect(Collectors.toList()));
		Mesh branches = turtleInterpreter.getMesh();
		branches.addTexture("diffuseTexture", Textures.bark);
		branches.addTexture("normalTexture", Textures.barkNormal);
		branches.setShaderProgram(instancedNormalTextureShaderProgram);

		Mesh canopy = turtleInterpreter.getCombinedSubModelMeshes().get(0);
		canopy.addTexture("leaf_front", Textures.leafFront);
		canopy.addTexture("leaf_transl_front", Textures.leafFrontT);
		canopy.addTexture("leaf_TSNM_front", Textures.leafFrontNorm);
		canopy.addTexture("leaf_TSHLM_front_t", Textures.leafFrontHL);
		canopy.addTexture("leaf_back", Textures.leafBack);
		canopy.addTexture("leaf_transl_back", Textures.leafBackT);
		canopy.addTexture("leaf_TSNM_back", Textures.leafBackNorm);
		canopy.addTexture("leaf_TSHLM_back_t", Textures.leafBackHL);
		canopy.setShaderProgram(instancedLeafShaderProgram);

		Mesh board = MeshUtils.transform(Trees.leaf, new Matrix4f()
				.scale(1f, 10f / params.scale, 1f / params.scale)
				.rotate((float) Math.PI / 2, out));
		board.addTexture("diffuseTexture", Textures.bark);
		board.addTexture("normalTexture", Textures.barkNormal);
		board.setShaderProgram(billboardShaderProgram);

		Mesh LODCanopy = new Mesh(canopy);
		LODCanopy.setShaderProgram(instancedLeafShaderProgram);
		List<Mesh> billboard = List.of(
				new Mesh(board),
				MeshUtils.transform(board, new Matrix4f().rotate((float) Math.PI / 2, up)),
				LODCanopy
		);

		return Map.of(
				LevelOfDetail.HIGH, List.of(new Mesh(branches), new Mesh(canopy)),
				LevelOfDetail.LOW, billboard);
	}

	private LSystem treeSystem() {
		Parameters.SceneObjects.Tree params = parameters.sceneObjects.trees.get(index);
		if (!(params instanceof Parameters.SceneObjects.BranchingTree)) {
			throw new NotImplementedException();
		}
		float a = params.lSystemParams.get("a");
		float lr = params.lSystemParams.get("lr");
		float vr = params.lSystemParams.get("vr");
		float e = params.lSystemParams.get("e");
		List<Parameters.SceneObjects.BranchingTree.Branching> branchings =
				((Parameters.SceneObjects.BranchingTree) params).branchings;

		CharModule A = new CharModule('A');
		ParametricParameterModule ExIn = new ParametricParameterModule('!', List.of("w"));
		Module ExOut = new ParametricExpressionModule('!', List.of("w"), vars -> List.of(vars.get("w") * vr));
		ParametricParameterModule FIn = new ParametricParameterModule('F', List.of("l"));
		Module FOut = new ParametricExpressionModule('F', List.of("l"), vars -> List.of(vars.get("l") * lr));

		List<Production> productions = new ArrayList<>();
		for (Parameters.SceneObjects.BranchingTree.Branching entry : branchings) {
			List<Module> successors = new ArrayList<>(List.of(
					new ParametricValueModule('!', vr),
					new ParametricValueModule('F', 50f))
			);

			for (float angle : entry.angles) {
				successors.addAll(List.of(
						LB,
						new ParametricValueModule('&', a),
						new ParametricValueModule('F', 50f),
						A,
						RB,
						new ParametricValueModule('/', angle))
				);
			}

			successors.addAll(List.of(
					new ParametricValueModule('&', a),
					new ParametricValueModule('F', 50f),
					A)
			);
			productions.add(new ProductionBuilder(List.of(A), successors).withProbability(entry.prob).build()
			);
		}
		productions.add(new ProductionBuilder(List.of(FIn), List.of(FOut)).build());
		productions.add(new ProductionBuilder(List.of(ExIn), List.of(ExOut)).build());

		return new LSystem(
				List.of(
						new ParametricValueModule('T', List.of(0f, -1f, 0f, e)),
						new ParametricValueModule('!', 1f),
						new ParametricValueModule('F', 200f),
//						new ParametricValueModule('/', (float) Math.PI / 4),
						A
				),
				List.of(),
				productions
		);
	}

}
