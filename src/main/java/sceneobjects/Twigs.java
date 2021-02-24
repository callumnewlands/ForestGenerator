package sceneobjects;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static lsystems.modules.DefinedModules.LB;
import static lsystems.modules.DefinedModules.RB;
import static rendering.ShaderPrograms.instancedNormalTextureShaderProgram;

import generation.TerrainQuadtree;
import generation.TurtleInterpreter;
import lsystems.LSystem;
import lsystems.ProductionBuilder;
import lsystems.modules.CharModule;
import lsystems.modules.Module;
import lsystems.modules.ParametricExpressionModule;
import lsystems.modules.ParametricParameterModule;
import lsystems.modules.ParametricValueModule;
import modeldata.meshdata.Mesh;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import rendering.LevelOfDetail;
import rendering.Textures;
import utils.MeshUtils;

public class Twigs extends InstancedGroundObject {

	public Twigs(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree, boolean yRotationOnly) {
		super(numberOfTypes, numberOfInstances, regionCentre, regionWidth, quadtree, yRotationOnly);
	}

	@Override
	float getScale() {
		return 0.05f;
	}

	@Override
	float getHeightOffset() {
		return 0.1f;
	}

	@Override
	Map<LevelOfDetail, List<Mesh>> getMeshes() {
		int numEdges = 5;
		TurtleInterpreter twigTurtleInterpreter = new TurtleInterpreter(numEdges);
		twigTurtleInterpreter.setIgnored(List.of('A', 'B', 'C'));
		List<Module> instructions = twigSystem().performDerivations(new Random().nextInt(2) + 5);
		twigTurtleInterpreter.interpretInstructions(instructions);
		Mesh twig = MeshUtils.transform(twigTurtleInterpreter.getMesh(), new Matrix4f().rotate((float) Math.PI / 2, new Vector3f(1, 0, 0)));
		twig.addTexture("diffuseTexture", Textures.bark);
		twig.addTexture("normalTexture", Textures.barkNormal);
		twig.setShaderProgram(instancedNormalTextureShaderProgram);
		return Map.of(LevelOfDetail.HIGH, List.of(twig));
	}

	private LSystem twigSystem() {
		float r1 = 0.9f;
		float r2 = 0.6f;
		float a0 = (float) Math.PI / 8;
		float a2 = (float) Math.PI / 8;
		float d = 2.3998277f;
		float wr = 0.707f;

		CharModule D = new CharModule('$');
		Module AOut = new ParametricExpressionModule('A', List.of("l", "w"), vars -> List.of(vars.get("l") * r1, vars.get("w") * wr));
		Module B1Out = new ParametricExpressionModule('B', List.of("l", "w"), vars -> List.of(vars.get("l") * r1, vars.get("w") * wr));
		Module B2Out = new ParametricExpressionModule('B', List.of("l", "w"), vars -> List.of(vars.get("l") * r2, vars.get("w") * wr));
		Module C1Out = new ParametricExpressionModule('C', List.of("l", "w"), vars -> List.of(vars.get("l") * r1, vars.get("w") * wr));
		Module C2Out = new ParametricExpressionModule('C', List.of("l", "w"), vars -> List.of(vars.get("l") * r2, vars.get("w") * wr));
		Module ExOut = new ParametricExpressionModule('!', List.of("l", "w"), vars -> List.of(vars.get("w")));
		Module FOut = new ParametricExpressionModule('F', List.of("l", "w"), vars -> List.of(vars.get("l")));

		return new LSystem(
				List.of(
						new ParametricValueModule('!', 2f),
						new ParametricValueModule('A', List.of(10f, 1f))
				),
				List.of(),
				List.of(
						new ProductionBuilder(
								List.of(new ParametricParameterModule('A', List.of("l", "w"))),
								List.of(
										ExOut,
										FOut,
										LB,
										new ParametricValueModule('&', a0),
										B2Out,
										RB,
										new ParametricValueModule('/', d),
										AOut
								)).withProbability(0.4f).build(),
						new ProductionBuilder(
								List.of(new ParametricParameterModule('A', List.of("l", "w"))),
								List.of(
										ExOut,
										FOut,
										AOut
								)).withProbability(0.6f).build(),
						new ProductionBuilder(
								List.of(new ParametricParameterModule('B', List.of("l", "w"))),
								List.of(
										ExOut,
										FOut,
										LB,
										new ParametricValueModule('+', -a2),
										D,
										AOut,
										RB
								)).withProbability(0.3f).build(),
						new ProductionBuilder(
								List.of(new ParametricParameterModule('B', List.of("l", "w"))),
								List.of(
										ExOut,
										FOut
								)).withProbability(0.3f).build(),
						new ProductionBuilder(
								List.of(new ParametricParameterModule('B', List.of("l", "w"))),
								List.of(
										ExOut,
										FOut
								)).withProbability(0.4f).build()
				));
	}

}
