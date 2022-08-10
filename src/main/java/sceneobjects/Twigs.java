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

package sceneobjects;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static lsystems.modules.DefinedModules.LB;
import static lsystems.modules.DefinedModules.RB;
import static rendering.ShaderPrograms.textureShader;

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
import params.ParameterLoader;
import rendering.LevelOfDetail;
import rendering.Textures;
import segmentation.Colour;
import utils.MeshUtils;

public class Twigs extends InstancedGroundObject {

	public Twigs(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree) {
		super(numberOfTypes, numberOfInstances, regionCentre, regionWidth, quadtree, ParameterLoader.getParameters().sceneObjects.twigs);
		generate();
	}

	@Override
	Map<LevelOfDetail, List<Mesh>> getMeshes() {
		int numEdges = ParameterLoader.getParameters().sceneObjects.twigs.numSides;
		TurtleInterpreter twigTurtleInterpreter = new TurtleInterpreter(numEdges);
		twigTurtleInterpreter.setIgnored(List.of('A', 'B', 'C'));
		Random r = ParameterLoader.getParameters().random.generator;
		List<Module> instructions = twigSystem().performDerivations(r.nextInt(2) + 5);
		twigTurtleInterpreter.interpretInstructions(instructions);
		Mesh twig = MeshUtils.transform(twigTurtleInterpreter.getMesh(), new Matrix4f().rotate((float) Math.PI / 2, new Vector3f(1, 0, 0)));
		twig.addTexture("diffuseTexture", Textures.twigBark);
		twig.addTexture("normalTexture", Textures.twigBarkNormal);
		twig.addTexture("specularTexture", Textures.twigBarkGlossiness);
		twig.setShaderProgram(textureShader);
		twig.setSegColour(Colour.twig);
		return Map.of(LevelOfDetail.HIGH, List.of(twig));
	}

	private LSystem twigSystem() {
		float r1 = 0.9f;
		float r2 = 0.6f;
		float a0 = 22.5f;
		float d = 137.5f;
		float wr = 0.707f;

		CharModule D = new CharModule('$');
		Module AOut = new ParametricExpressionModule('A', List.of("l", "w"), vars -> List.of(vars.get("l") * r1, vars.get("w") * wr));
		Module BOut = new ParametricExpressionModule('B', List.of("l", "w"), vars -> List.of(vars.get("l") * r2, vars.get("w") * wr));
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
										new ParametricValueModule('&', (float) Math.toRadians(a0)),
										BOut,
										RB,
										new ParametricValueModule('/', (float) Math.toRadians(d)),
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
										new ParametricValueModule('+', -(float) Math.toRadians(a0)),
										D,
										AOut,
										RB
								)).withProbability(0.3f).build(),
						new ProductionBuilder(
								List.of(new ParametricParameterModule('B', List.of("l", "w"))),
								List.of(
										ExOut,
										FOut
								)).withProbability(0.7f).build()
				));
	}

}
