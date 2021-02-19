package sceneobjects;

import java.util.List;
import java.util.Random;
import generation.TerrainQuadtree;
import generation.TurtleInterpreter;
import lsystems.LSystem;
import lsystems.ProductionBuilder;
import lsystems.modules.CharModule;
import lsystems.modules.Module;
import lsystems.modules.ParametricExpressionModule;
import lsystems.modules.ParametricParameterModule;
import lsystems.modules.ParametricValueModule;
import modeldata.Mesh;
import modeldata.meshdata.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import utils.MeshUtils;

public class Rocks extends InstancedMeshGroundObject {

	public Rocks(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree, boolean yRotationOnly) {
		super(numberOfTypes, numberOfInstances, regionCentre, regionWidth, quadtree, yRotationOnly);
	}

	@Override
	float getScale() {
		return 0.6f;
	}

	@Override
	float getHeightOffset() {
		return 0.2f;
	}

	@Override
	Mesh getMesh() {
		int numEdges = 5;
		TurtleInterpreter rockTurtleInterpreter = new TurtleInterpreter(numEdges);
		rockTurtleInterpreter.setIgnored(List.of('A', 'B', 'C'));
		List<Module> instructions = prismSystem().performDerivations(new Random().nextInt(1));
		rockTurtleInterpreter.interpretInstructions(instructions);
		return MeshUtils.transform(rockTurtleInterpreter.getMesh(), new Matrix4f().rotate((float) Math.PI / 2, new Vector3f(1, 0, 0)));
	}

	private LSystem prismSystem() {

		return new LSystem(
				List.of(new CharModule('*'),
						new ParametricValueModule('!', 1f),
						new ParametricValueModule('F', 1f),
						new CharModule('*')),
				List.of(),
				List.of(new ProductionBuilder(
						List.of(new ParametricParameterModule('F', List.of("w"))),
						List.of(new ParametricExpressionModule('F', List.of("w"), vars -> List.of(vars.get("w"))),
								new ParametricExpressionModule('F', List.of("w"), vars -> List.of(vars.get("w")))))
						.build()
				));
	}

	@Override
	Texture getDiffuseTexture() {
		return Textures.rock;
	}

	@Override
	Texture getNormalTexture() {
		return Textures.rockNormal;
	}

}
