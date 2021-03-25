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
import static rendering.ShaderPrograms.instancedNormalTextureShaderProgram;

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
		turtleInterpreter.setSubModels(List.of(MeshUtils.transform(leaf, new Matrix4f().scale(params.leafScale / params.scale))));
		Random r = ParameterLoader.getParameters().random.generator;
		int minI = params.minIterations;
		int maxI = params.maxIterations;
		List<Module> instructions;
		if (params instanceof Parameters.SceneObjects.BranchingTree) {
			turtleInterpreter.setIgnored(List.of('A'));
			instructions = branchingTreeSystem()
					.performDerivations(r.nextInt(maxI - minI) + minI)
					.stream()
					.map(m -> m.getName() == 'A' ? new ParametricValueModule('~', 0f) : m)
					.collect(Collectors.toList());
		} else if (params instanceof Parameters.SceneObjects.MonopodialTree) {
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
		branches.setShaderProgram(instancedNormalTextureShaderProgram);

		Mesh canopy = turtleInterpreter.getCombinedSubModelMeshes().get(0);
		canopy.addTexture("leaf_front", treeTextures.leafFront);
		canopy.addTexture("leaf_transl_front", treeTextures.leafFrontT);
		canopy.addTexture("leaf_TSNM_front", treeTextures.leafFrontNorm);
		canopy.addTexture("leaf_TSHLM_front_t", treeTextures.leafFrontHL);
		canopy.addTexture("leaf_back", treeTextures.leafBack);
		canopy.addTexture("leaf_transl_back", treeTextures.leafBackT);
		canopy.addTexture("leaf_TSNM_back", treeTextures.leafBackNorm);
		canopy.addTexture("leaf_TSHLM_back_t", treeTextures.leafBackHL);
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

	private LSystem branchingTreeSystem() {
		Parameters.SceneObjects.Tree params = parameters.sceneObjects.trees.get(index);
		float a = params.lSystemParams.get("a").floatValue();
		float lr = params.lSystemParams.get("lr").floatValue();
		float vr = params.lSystemParams.get("vr").floatValue();
		float e = params.lSystemParams.get("e").floatValue();
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


	private LSystem monopodialTreeSystem() {
		Parameters.SceneObjects.Tree params = parameters.sceneObjects.trees.get(index);
		float e = params.lSystemParams.get("e").floatValue();
		float lB = params.lSystemParams.get("lB").floatValue();
		float lS = params.lSystemParams.get("lS").floatValue();
		float lSm = params.lSystemParams.get("lSm").floatValue();
		float wB = params.lSystemParams.get("wB").floatValue();
		float wS = params.lSystemParams.get("wS").floatValue();
		float wS2 = params.lSystemParams.get("wS2").floatValue();
		float aB = params.lSystemParams.get("aB").floatValue();
		float aS = params.lSystemParams.get("aS").floatValue();
		float aS2 = params.lSystemParams.get("aS2").floatValue();
		float aS3 = params.lSystemParams.get("aS3").floatValue();
		float aS4 = params.lSystemParams.get("aS4").floatValue();
		float tH = params.lSystemParams.get("tH").floatValue();
		float aD = params.lSystemParams.get("aD").floatValue();
		int nB = params.lSystemParams.get("nB").intValue();
		int nB2 = params.lSystemParams.get("nB2").intValue();
		float l1 = params.lSystemParams.get("l1").floatValue();
		float vr = params.lSystemParams.get("vr").floatValue();
		float lr = params.lSystemParams.get("lr").floatValue();
		int maxi = params.maxIterations;

		List<AxiomaticModule> axiom = List.of(
				new ParametricValueModule('T', List.of(0f, -1f, 0f, e)),
				new ParametricValueModule('!', wB),
				new ParametricValueModule('F', lB),
				new ParametricValueModule('A', List.of(wB, l1))
		);

		boolean variableAngles = false;

		// Trunk
		ParametricParameterModule AIn = new ParametricParameterModule('A', List.of("w", "l"));
		List<Module> AOut = new ArrayList<>();
		for (int i = 0; i < nB; i++) {
			int finalI = i;
			Function<Map<String, Float>, Float> heightFraction = vars -> {
				float w0 = vars.get("w"); // Width of current segment
				if (w0 == wB) {
					return 1f;
				}
				float w1 = vars.get("w") + wB / (maxi - 1); // Width of lower segment
				return MathsUtils.lerp(w0 / wB, w1 / wB, (float) finalI / nB);  // Fraction of height of current position (0 = top of tree)
			};
			Function<Map<String, Float>, Float> branchLen = vars -> {
				float pos = heightFraction.apply(vars);
				if (pos == 0 && tH == 0) {
					return 1f;
				}
				return lSm + (1 - lSm) * (pos <= tH ? pos / tH : (1 - pos) / (1 - tH));
			};
//			float angle = (float) Math.toRadians(aB); // Branch angle to trunk
			AOut.addAll(List.of(
					new ParametricValueModule('/', (float) Math.toRadians(aS)), // Rotates around trunk
					LB,
					new ParametricExpressionModule('&', List.of("w"), vars -> List.of((float) Math.toRadians(
							variableAngles
									? (30 + 85 * heightFraction.apply(vars))
									: aB))), // TODO min-max angles
					new ParametricExpressionModule('!', List.of("w"), vars -> List.of(vars.get("w") * vr * branchLen.apply(vars))),
					new ParametricExpressionModule('F', List.of("w"), vars -> List.of(vars.get("w") * lr)), // Move the base of the side branches away from the trunk centre
					new ParametricExpressionModule('B', List.of("w"), vars -> List.of(
							vars.get("w") * wS * branchLen.apply(vars),
							lS * branchLen.apply(vars))),
					RB,
					new ParametricExpressionModule('!', List.of("w"), vars -> List.of(vars.get("w") - finalI * (wB / (maxi - 1)) / (nB))), // Taper trunk
					new ParametricExpressionModule('F', List.of("l"), vars -> List.of(vars.get("l")))
			));
		}
		AOut.add(new ParametricExpressionModule('A', List.of("w", "l"), vars -> List.of(
				Math.max(vars.get("w") - wB / (maxi - 1), 0),
				vars.get("l"))));

		// Side branches
		ParametricParameterModule BIn = new ParametricParameterModule('B', List.of("w", "l"));
		List<Module> BOut = new ArrayList<>();
		for (int i = 0; i < nB2; i++) {
			int finalI = i;
			Function<Map<String, Float>, Float> wI = vars -> vars.get("w") - vars.get("w") * finalI / nB2;
			BOut.addAll(List.of(
					new ParametricExpressionModule('!', List.of("w"), vars -> List.of(wI.apply(vars))),
					new ParametricExpressionModule('F', List.of("l"), vars -> List.of(vars.get("l") / 3)), // distance between 3rd level branches
					LB,
					new ParametricValueModule('/', (float) Math.toRadians(aS2) * finalI),
					new ParametricValueModule('&', (float) Math.toRadians(aS3)),
					new ParametricExpressionModule('!', List.of("w"), vars -> List.of(wS2 * wI.apply(vars))),
					new ParametricExpressionModule('F', List.of("l"), vars -> List.of(
							(float) Math.sqrt(nB2 - finalI) * vars.get("l") / 4, // TODO /3 for aspen
							(float) (int) ((Math.sqrt(nB2 - finalI) + 2) * 4 * vars.get("l")),
							0f,
							(float) Math.toRadians(140),
							(float) Math.toRadians(40))),
					new ParametricValueModule('&', (float) Math.toRadians(aS4)),
					new ParametricExpressionModule('F', List.of("l"), vars -> List.of(
							(float) Math.sqrt(nB2 - finalI) * vars.get("l") / 4,
							(float) (int) ((Math.sqrt(nB2 - finalI) + 2) * 4 * vars.get("l")),
							0f,
							(float) Math.toRadians(140),
							(float) Math.toRadians(40))),
					new CharModule('%'),
					RB,
					// TODO +() for aspen
					new ParametricValueModule('&', (float) Math.toRadians(aD)) // Causes slight curve inwards
			));
		}
//		BOut.add(new ParametricExpressionModule('F', List.of("l"), vars -> List.of(vars.get("l") / 2f))); // TODO for aspen


		return new LSystem(
				axiom,
				List.of(),
				List.of(
						new ProductionBuilder(List.of(AIn), AOut).build(),
						new ProductionBuilder(List.of(BIn), BOut).build()
				));

	}

}
