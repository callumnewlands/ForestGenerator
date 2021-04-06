package sceneobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import static lsystems.modules.DefinedModules.LB;
import static lsystems.modules.DefinedModules.RB;
import static rendering.ShaderPrograms.billboardShaderProgram;
import static rendering.ShaderPrograms.instancedLeafShaderProgram;
import static rendering.ShaderPrograms.textureShader;

import generation.TerrainQuadtree;
import generation.TurtleInterpreter;
import lombok.Getter;
import lsystems.LSystem;
import lsystems.Production;
import lsystems.ProductionBuilder;
import lsystems.modules.AxiomaticModule;
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
import params.TreeTypes;
import rendering.LevelOfDetail;
import rendering.Textures;
import utils.MathsUtils;
import utils.MeshUtils;

public class Trees extends InstancedGroundObject {

	private static final Parameters parameters = ParameterLoader.getParameters();
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
	@Getter
	private final int index;

	public Trees(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree, boolean yRotationOnly, int index) {
		super(numberOfTypes, numberOfInstances, regionCentre, regionWidth, quadtree, yRotationOnly);
		this.index = index;
		generate();
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
		turtleInterpreter.setSubModels(List.of(MeshUtils.transform(leaf,
				new Matrix4f().scale(params.leafYScale / params.scale, 1, params.leafXScale / params.scale))));
		Random r = ParameterLoader.getParameters().random.generator;
		int minI = params.minIterations;
		int maxI = params.maxIterations;
		List<Module> instructions;
		if (params instanceof TreeTypes.BranchingTree) {
			turtleInterpreter.setIgnored(List.of('A'));
			instructions = branchingTreeSystem()
					.performDerivations(r.nextInt(maxI - minI) + minI)
					.stream()
					.map(m -> m.getName() == 'A' ? new ParametricValueModule('~', 0f) : m)
					.collect(Collectors.toList());
		} else if (params instanceof TreeTypes.MonopodialTree) {
			turtleInterpreter.setIgnored(List.of('A', 'B'));
			instructions = monopodialTreeSystem()
					.performDerivations(r.nextInt(maxI - minI) + minI);
		} else {
			throw new NotImplementedException();
		}
		turtleInterpreter.interpretInstructions(instructions);

		Textures.TreeTextures treeTextures = Textures.treeTextures.get(index);
		Mesh branches = turtleInterpreter.getMesh();
		branches.addTexture("diffuseTexture", treeTextures.bark);
		branches.addTexture("normalTexture", treeTextures.barkNormal);
		branches.setShaderProgram(textureShader);

		Mesh canopy = turtleInterpreter.getCombinedSubModelMeshes().get(0);
		canopy.addTexture("leafFront", treeTextures.leafFront);
		canopy.addTexture("leafFrontTranslucency", treeTextures.leafFrontT);
		canopy.addTexture("leafFrontNorm", treeTextures.leafFrontNorm);
		canopy.addTexture("leafFrontHalfLife", treeTextures.leafFrontHL);
		canopy.addTexture("leafBack", treeTextures.leafBack);
		canopy.addTexture("leafBackTranslucency", treeTextures.leafBackT);
		canopy.addTexture("leafBackNorm", treeTextures.leafBackNorm);
		canopy.addTexture("leafBackHalfLife", treeTextures.leafBackHL);
		canopy.setShaderProgram(instancedLeafShaderProgram);
		canopy.setColourFilter(params.leafColourFilter);

		// Uses leaf geometry to construct billboard
		Mesh board = MeshUtils.transform(Trees.leaf, new Matrix4f()
				.scale(1f, 10f / params.scale, 1f / params.scale)
				.rotate((float) Math.PI / 2, out));
		board.addTexture("diffuseTexture", treeTextures.bark);
		board.addTexture("normalTexture", treeTextures.barkNormal);
		board.setShaderProgram(billboardShaderProgram);

		Mesh LODCanopy = new Mesh(canopy);
		LODCanopy.setShaderProgram(instancedLeafShaderProgram);
		List<Mesh> billboard = List.of(
				new Mesh(board),
				MeshUtils.transform(board, new Matrix4f().rotate((float) Math.PI / 2, up)),
				LODCanopy
		);
		LODCanopy.setColourFilter(params.leafColourFilter);

		return Map.of(
				LevelOfDetail.HIGH, List.of(new Mesh(branches), new Mesh(canopy)),
				LevelOfDetail.LOW, billboard);
	}

	private float getFloatParam(Parameters.SceneObjects.Tree params, String name) {
		Random r = parameters.random.generator;
		float min = params.lSystemParamsLower.get(name).floatValue();
		float max = params.lSystemParamsUpper.get(name).floatValue();
		if (min == max) {
			return min;
		}
		return r.nextFloat() * (max - min) + min;
	}

	private int getIntParam(Parameters.SceneObjects.Tree params, String name) {
		Random r = parameters.random.generator;
		int min = params.lSystemParamsLower.get(name).intValue();
		int max = params.lSystemParamsUpper.get(name).intValue();
		if (min == max) {
			return min;
		}
		return r.nextInt(max - min) + min;
	}

	private LSystem branchingTreeSystem() {
		Parameters.SceneObjects.Tree params = parameters.sceneObjects.trees.get(index);
		List<TreeTypes.BranchingTree.Branching> branchings =
				((TreeTypes.BranchingTree) params).branchings;


		float e = getFloatParam(params, "e");
		float lB = getFloatParam(params, "lB");
		float wB = getFloatParam(params, "wB");

		CharModule A = new CharModule('A');

		List<Production> productions = new ArrayList<>();
		for (TreeTypes.BranchingTree.Branching entry : branchings) {
			List<Module> startModules = new ArrayList<>(List.of(
					new ParametricExpressionModule('!', List.of(), vars -> List.of(
							getFloatParam(params, "vr"))),
					new ParametricExpressionModule('/', List.of(), vars -> List.of(
							(float) Math.toRadians(getFloatParam(params, "a2")))),
					new ParametricExpressionModule('F', List.of(), vars -> List.of(
							getFloatParam(params, "l1"))))
			);

			List<Module> startModulesSide = List.of(
					new ParametricExpressionModule('!', List.of(),
							vars -> List.of(getFloatParam(params, "vr"))),
					new ParametricExpressionModule('/', List.of(), vars -> List.of(
							(float) Math.toRadians(getFloatParam(params, "a2")))),
					new ParametricExpressionModule('F', List.of(), vars -> List.of(
							getFloatParam(params, "l2") * getFloatParam(params, "l1"))),
					LB,
					new ParametricExpressionModule('&', List.of(), vars -> List.of(
							(float) Math.toRadians(getFloatParam(params, "aS")))),
					A,
					RB,
					new ParametricExpressionModule('F', List.of(), vars -> List.of(
							getFloatParam(params, "l3") * getFloatParam(params, "l1")))
			);

			List<Module> midModules = List.of(
					LB,
					new ParametricExpressionModule('&', List.of(), vars -> List.of(
							(float) Math.toRadians(getFloatParam(params, "a1")))),
					new ParametricExpressionModule('F', List.of(), vars -> List.of(
							getFloatParam(params, "l1"))),
					A,
					RB);

			List<Module> branchingModules = new ArrayList<>();
			for (float angle : entry.angles) {
				branchingModules.addAll(List.of(
						new ParametricExpressionModule('F', List.of(), vars -> List.of(
								getFloatParam(params, "bO"))),
						new ParametricValueModule('/', angle),
						LB,
						new ParametricExpressionModule('&', List.of(), vars -> List.of(
								(float) Math.toRadians(getFloatParam(params, "a1")))),
						new ParametricExpressionModule('F', List.of(), vars -> List.of(
								getFloatParam(params, "l1"))),
						A,
						RB
				));
			}

			// Without side branches
			List<Module> A1Out = new ArrayList<>();
			A1Out.addAll(startModules);
			A1Out.addAll(midModules);
			A1Out.addAll(branchingModules);

			// With side branches
			List<Module> A2Out = new ArrayList<>();
			A2Out.addAll(startModulesSide);
			A2Out.addAll(midModules);
			A2Out.addAll(branchingModules);

			float pS = getFloatParam(params, "pS");
			productions.add(new ProductionBuilder(List.of(A), A1Out).withProbability(entry.prob * (1 - pS)).build());
			productions.add(new ProductionBuilder(List.of(A), A2Out).withProbability(entry.prob * pS).build());
		}

		ParametricParameterModule FIn = new ParametricParameterModule('F', List.of("l"));
		Module FOut = new ParametricExpressionModule('F', List.of("l"), vars -> List.of(
				vars.get("l") * getFloatParam(params, "lr")));
		productions.add(new ProductionBuilder(List.of(FIn), List.of(FOut)).build());

		ParametricParameterModule ExIn = new ParametricParameterModule('!', List.of("w"));
		Module ExOut = new ParametricExpressionModule('!', List.of("w"), vars -> List.of(
				vars.get("w") * getFloatParam(params, "vr")));
		productions.add(new ProductionBuilder(List.of(ExIn), List.of(ExOut)).build());

		return new LSystem(
				List.of(
						new ParametricValueModule('T', List.of(0f, -1f, 0f, e)),
						new ParametricValueModule('!', wB),
						new ParametricValueModule('F', lB),
//						new ParametricValueModule('/', (float) Math.PI / 4),
						A
				),
				List.of(),
				productions
		);
	}

	private LSystem monopodialTreeSystem() {
		TreeTypes.MonopodialTree params = (TreeTypes.MonopodialTree) parameters.sceneObjects.trees.get(index);

		float e = getFloatParam(params, "e");
		float lB = getFloatParam(params, "lB");
		float l2 = getFloatParam(params, "l2");
		float lr2 = getFloatParam(params, "lr2");
		float wB = getFloatParam(params, "wB");
		int nB = getIntParam(params, "nB");
		int minI = params.minIterations;
		boolean heightVaryingAngles = params.heightVaryingAngles;
		boolean pineStyleBranches = params.pineStyleBranches;

		List<AxiomaticModule> axiom = List.of(
				new ParametricValueModule('T', List.of(0f, -1f, 0f, e)),
				new ParametricValueModule('!', wB),
				new ParametricValueModule('F', lB),
				new ParametricValueModule('A', List.of(wB, l2))
		);


		// Trunk
		ParametricParameterModule AIn = new ParametricParameterModule('A', List.of("w", "l"));
		List<Module> AOut = new ArrayList<>();
		for (int i = 0; i < nB; i++) {

			float lS = getFloatParam(params, "lS");
			float lSm = getFloatParam(params, "lSm");
			float wS = getFloatParam(params, "wS");
			float aB = getFloatParam(params, "aB");
			float aS = getFloatParam(params, "aS");
			float aS5 = getFloatParam(params, "aS5");
			float tH = getFloatParam(params, "tH");
			float l1 = getFloatParam(params, "l1");
			float vr = getFloatParam(params, "vr");
			float lr = getFloatParam(params, "lr");

			int finalI = i;
			Function<Map<String, Float>, Float> heightFraction = vars -> {
				float w0 = vars.get("w"); // Width of current segment
				if (w0 == wB) {
					return 1f;
				}
				float w1 = vars.get("w") + wB / minI; // Width of lower segment
				return MathsUtils.lerp(w0 / wB, w1 / wB, (float) finalI / nB);  // Fraction of height of current position (0 = top of tree)
			};
			Function<Map<String, Float>, Float> branchLen = vars -> {
				float pos = heightFraction.apply(vars);
				if (pos == 0 && tH == 0) {
					return 1f;
				}
				return lSm + (1 - lSm) * (pos <= tH ? pos / tH : (1 - pos) / (1 - tH));
			};
			AOut.addAll(List.of(
					new ParametricValueModule('/', (float) Math.toRadians(aS)), // Rotates around trunk
					LB,
					new ParametricExpressionModule('&', List.of("w"), vars -> List.of((float) Math.toRadians(
							heightVaryingAngles
									? (30 + 85 * heightFraction.apply(vars)) // TODO min-max angles
									: aB))),
					new ParametricExpressionModule('!', List.of("w"), vars -> List.of(vars.get("w") * vr * branchLen.apply(vars))),
					new ParametricExpressionModule('F', List.of("w"), vars -> List.of(vars.get("w") * lr)), // Move the base of the side branches away from the trunk centre
					new ParametricValueModule('/', (float) Math.toRadians(aS5)),
					new ParametricExpressionModule('B', List.of("w"), vars -> List.of(
							vars.get("w") * wS * branchLen.apply(vars),
							lS * branchLen.apply(vars))),
					RB,
					new ParametricExpressionModule('!', List.of("w"), vars -> List.of(vars.get("w") - finalI * (wB / minI) / (nB))), // Taper trunk
					new ParametricValueModule('F', l1)
			));
		}
		AOut.add(new ParametricExpressionModule('F', List.of("l"), vars -> List.of(vars.get("l"))));
		AOut.add(new ParametricExpressionModule('A', List.of("w", "l"), vars -> List.of(
				Math.max(vars.get("w") - wB / minI, 0),
				vars.get("l") * lr2)));

		// Side branches
		List<Production> branchProductions = pineStyleBranches
				? getPineSideBranches(params)
				: getAlternatingSideBranches(params);

		List<Production> productions = new ArrayList<>();
		productions.add(new ProductionBuilder(List.of(AIn), AOut).build());
		productions.addAll(branchProductions);

		return new LSystem(axiom, List.of(), productions);
	}

	private List<Production> getAlternatingSideBranches(TreeTypes.MonopodialTree params) {

		int nB2 = getIntParam(params, "nB2");

		ParametricParameterModule BIn = new ParametricParameterModule('B', List.of("w", "l"));
		List<Module> BOut = new ArrayList<>();
		for (int i = 0; i < nB2; i++) {

			float wS2 = getFloatParam(params, "wS2");
			float aS2 = getFloatParam(params, "aS2");
			float aS3 = getFloatParam(params, "aS3");
			float aS4 = getFloatParam(params, "aS4");
			float lS2 = getFloatParam(params, "lS2");
			float lS3 = getFloatParam(params, "lS3");
			float lS4 = getFloatParam(params, "lS4");
			float aU = getFloatParam(params, "aU");

			int finalI = i;
			Function<Map<String, Float>, Float> wI = vars -> vars.get("w") - vars.get("w") * finalI / nB2;
			BOut.addAll(List.of(
					new ParametricExpressionModule('!', List.of("w"), vars -> List.of(wI.apply(vars))),
					new ParametricExpressionModule('F', List.of("l"), vars -> List.of(vars.get("l") * lS2)),
					LB,
					new ParametricValueModule('/', (float) Math.toRadians(aS2) * finalI),
					new ParametricValueModule('&', (float) Math.toRadians(aS3)),
					new ParametricExpressionModule('!', List.of("w"), vars -> List.of(wS2 * wI.apply(vars))),
					new ParametricExpressionModule('F', List.of("l"), vars -> List.of(
							(float) Math.sqrt(nB2 - finalI) * vars.get("l") * lS3,
							(float) (int) ((Math.sqrt(nB2 - finalI) + 2) * 4 * vars.get("l")),
							0f,
							(float) Math.toRadians(140), // TODO number and angles of leaves
							(float) Math.toRadians(40))),
					new ParametricValueModule('&', (float) Math.toRadians(aS4)),
					new ParametricExpressionModule('F', List.of("l"), vars -> List.of(
							(float) Math.sqrt(nB2 - finalI) * vars.get("l") * lS4,
							(float) (int) ((Math.sqrt(nB2 - finalI) + 2) * 4 * vars.get("l")),
							0f,
							(float) Math.toRadians(140),
							(float) Math.toRadians(40))),
					new CharModule('%'),
					RB,
					new ParametricValueModule('&', (float) Math.toRadians(-aU)) // Causes slight curve downwards
			));
		}
//		BOut.add(new ParametricExpressionModule('F', List.of("l"), vars -> List.of(vars.get("l") / 2f))); // TODO for aspen
		return List.of(new ProductionBuilder(List.of(BIn), BOut).build());
	}

	private List<Production> getPineSideBranches(TreeTypes.MonopodialTree params) {

		ParametricParameterModule BIn = new ParametricParameterModule('B', List.of("w", "l"));
		List<Module> startModules = List.of(
				new ParametricExpressionModule('/', List.of(), vars -> List.of(
						(float) Math.toRadians(getFloatParam(params, "aS2")))),
				new ParametricExpressionModule('!', List.of("w"), vars -> List.of(vars.get("w"))),
				new ParametricExpressionModule('&', List.of(), vars -> List.of(
						(float) Math.toRadians(-getFloatParam(params, "aU")))),
				new ParametricExpressionModule('F', List.of("l"), vars -> List.of(
						vars.get("l"),
						300 * vars.get("l"), // TODO param
						0f,
						(float) Math.toRadians(140),
						(float) Math.toRadians(40)))
		);
		List<Module> sideModules = List.of(
				LB,
				new ParametricExpressionModule('+', List.of(), vars -> List.of(
						(float) Math.toRadians(getFloatParam(params, "aS3")))),
				new ParametricExpressionModule('B', List.of("w"), vars -> List.of(
						vars.get("w") * getFloatParam(params, "wS2"),
						vars.get("l") * 0.7f)),
				RB,
				LB,
				new ParametricExpressionModule('+', List.of(), vars -> List.of(
						(float) Math.toRadians(getFloatParam(params, "aS4")))),
				new ParametricExpressionModule('B', List.of("w"), vars -> List.of(
						vars.get("w") * getFloatParam(params, "wS2"),
						vars.get("l") * 0.7f)),
				RB
		);
		List<Module> endModules = List.of(
				new ParametricExpressionModule('B', List.of("w"), vars -> List.of(
						vars.get("w") * getFloatParam(params, "wS2"),
						vars.get("l") * 0.7f))
		);

		// Side branching
		List<Module> B1Out = new ArrayList<>();
		B1Out.addAll(startModules);
		B1Out.addAll(sideModules);
		B1Out.addAll(endModules);

		// Straight
		List<Module> B2Out = new ArrayList<>();
		B2Out.addAll(startModules);
		B2Out.addAll(endModules);

		return List.of(
				new ProductionBuilder(List.of(BIn), B1Out)
						.withCondition(vars -> vars.get("l") >= 0.6f).build(),
				new ProductionBuilder(List.of(BIn), B1Out)
						.withCondition(vars -> vars.get("l") >= 0.3f && vars.get("l") < 0.6f)
						.withProbability(0.6f).build(),
				new ProductionBuilder(List.of(BIn), B2Out)
						.withCondition(vars -> vars.get("l") >= 0.3f && vars.get("l") < 0.6f)
						.withProbability(0.4f).build(),
				new ProductionBuilder(List.of(BIn), B2Out)
						.withCondition(vars -> vars.get("l") < 0.3f).build()
		);
	}

}
