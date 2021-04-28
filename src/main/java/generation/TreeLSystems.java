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

package generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import static lsystems.modules.DefinedModules.LB;
import static lsystems.modules.DefinedModules.RB;

import lsystems.LSystem;
import lsystems.Production;
import lsystems.ProductionBuilder;
import lsystems.modules.AxiomaticModule;
import lsystems.modules.CharModule;
import lsystems.modules.Module;
import lsystems.modules.ParametricExpressionModule;
import lsystems.modules.ParametricParameterModule;
import lsystems.modules.ParametricValueModule;
import params.ParameterLoader;
import params.Parameters;
import params.TreeTypes;
import utils.MathsUtils;

public final class TreeLSystems {
	private TreeLSystems() {
	}

	private static final Parameters parameters = ParameterLoader.getParameters();

	private static float getParamBetween(float min, float max) {
		Random r = parameters.random.generator;
		if (min == max) {
			return min;
		}
		if (min > max) {
			throw new RuntimeException("max value: " + max + " < min value: " + min);
		}
		return r.nextFloat() * (max - min) + min;
	}

	private static float getFloatParam(Parameters.SceneObjects.Tree params, String name) {
		Random r = parameters.random.generator;
		float min = params.lSystemParamsLower.get(name).floatValue();
		float max = params.lSystemParamsUpper.get(name).floatValue();
		if (min == max) {
			return min;
		}
		if (min > max) {
			throw new RuntimeException("max value: " + max + " < min value: " + min + " for param: " + name + " in tree: " + params.name);
		}
		return r.nextFloat() * (max - min) + min;
	}

	private static int getIntParam(Parameters.SceneObjects.Tree params, String name) {
		Random r = parameters.random.generator;
		int min = params.lSystemParamsLower.get(name).intValue();
		int max = params.lSystemParamsUpper.get(name).intValue();
		if (min == max) {
			return min;
		}
		if (min > max) {
			throw new RuntimeException("max value: " + max + " < min value: " + min + " for param: " + name + " in tree: " + params.name);
		}
		return r.nextInt(max - min) + min;
	}

	public static LSystem branching(int typeIndex) {
		Parameters.SceneObjects.Tree params = parameters.sceneObjects.trees.get(typeIndex);
		List<TreeTypes.BranchingTree.Branching> branchings =
				((TreeTypes.BranchingTree) params).branchings;

		float e = getFloatParam(params, "e");
		float lB = getFloatParam(params, "lB");
		float wB = getFloatParam(params, "wB");
		int nT = getIntParam(params, "nT");
		boolean widenBase = params.widenBase;

		CharModule A = new CharModule('A');

		List<AxiomaticModule> axiom = new ArrayList<>(List.of(
				new ParametricValueModule('T', List.of(0f, -1f, 0f, e))
		));
		if (widenBase) {
			float tP = getFloatParam(params, "tP");
			float tF = getFloatParam(params, "tF");
			axiom.add(new ParametricValueModule('!', wB * (1 + tF)));
			for (int i = 1; i <= nT; i++) {
				axiom.add(new ParametricValueModule('!', wB * (1 + (float) Math.pow((float) (nT - i) / nT, tP) * tF)));
				axiom.add(new ParametricValueModule('F', lB / nT));
			}
		} else {
			axiom.add(new ParametricValueModule('!', wB));
			axiom.add(new ParametricValueModule('F', lB));
		}
		axiom.add(A);

		List<Production> productions = new ArrayList<>();
		for (TreeTypes.BranchingTree.Branching entry : branchings) {
			List<Module> startModules = new ArrayList<>(List.of(
					new ParametricExpressionModule('!', List.of(), vars -> List.of(
							getFloatParam(params, "rw"))),
					new ParametricExpressionModule('/', List.of(), vars -> List.of(
							(float) Math.toRadians(getFloatParam(params, "a2")))),
					new ParametricExpressionModule('F', List.of(), vars -> List.of(
							getFloatParam(params, "l1"))))
			);

			List<Module> startModulesSide = List.of(
					new ParametricExpressionModule('!', List.of(),
							vars -> List.of(getFloatParam(params, "rw"))),
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
							(float) Math.toRadians(getFloatParam(params, "a0")))),
					new ParametricExpressionModule('F', List.of(), vars -> List.of(
							getFloatParam(params, "l1"))),
					A,
					RB);

			List<Module> branchingModules = new ArrayList<>();
			if (entry.minAngles.size() != entry.maxAngles.size()) {
				throw new RuntimeException("Unmatched number of minimum and maximum branching angles in: " + entry.minAngles + " and " + entry.maxAngles);
			}
			for (int i = 0; i < entry.minAngles.size(); i++) {
				int finalI = i;
				branchingModules.addAll(List.of(
						new ParametricExpressionModule('F', List.of(), vars -> List.of(
								getFloatParam(params, "bO"))),
						new ParametricExpressionModule('/', List.of(), vars -> List.of(
								(float) Math.toRadians(getParamBetween(entry.minAngles.get(finalI), entry.maxAngles.get(finalI))))),
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
				vars.get("l") * getFloatParam(params, "rl")));
		productions.add(new ProductionBuilder(List.of(FIn), List.of(FOut)).build());

		ParametricParameterModule ExIn = new ParametricParameterModule('!', List.of("w"));
		Module ExOut = new ParametricExpressionModule('!', List.of("w"), vars -> List.of(
				vars.get("w") * getFloatParam(params, "rw")));
		productions.add(new ProductionBuilder(List.of(ExIn), List.of(ExOut)).build());

		return new LSystem(
				axiom,
				List.of(),
				productions
		);
	}

	public static LSystem monopodial(int typeIndex) {
		TreeTypes.MonopodialTree params = (TreeTypes.MonopodialTree) parameters.sceneObjects.trees.get(typeIndex);

		float e = getFloatParam(params, "e");
		float lB = getFloatParam(params, "lB");
		float l2 = getFloatParam(params, "l2");
		float lr2 = getFloatParam(params, "lr2");
		float wB = getFloatParam(params, "wB");
		int nB = getIntParam(params, "nB");
		int nT = getIntParam(params, "nT");
		int minI = params.minIterations;
		boolean heightVaryingAngles = params.heightVaryingAngles;
		boolean pineStyleBranches = params.pineStyleBranches;
		boolean widenBase = params.widenBase;

		List<AxiomaticModule> axiom = new ArrayList<>(List.of(
				new ParametricValueModule('T', List.of(0f, -1f, 0f, e))
		));
		if (widenBase) {
			float tP = getFloatParam(params, "tP");
			float tF = getFloatParam(params, "tF");
			axiom.add(new ParametricValueModule('!', wB * (1 + tF)));
			for (int i = 1; i <= nT; i++) {
				axiom.add(new ParametricValueModule('!', wB * (1 + (float) Math.pow((float) (nT - i) / nT, tP) * tF)));
				axiom.add(new ParametricValueModule('F', lB / nT));
			}
		} else {
			axiom.add(new ParametricValueModule('!', wB));
			axiom.add(new ParametricValueModule('F', lB));
		}
		axiom.add(new ParametricValueModule('A', List.of(wB, l2)));


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
			float aMin = getFloatParam(params, "aMin");
			float aMax = getFloatParam(params, "aMax");

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
									? (aMin + (aMax - aMin) * heightFraction.apply(vars))
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

	private static List<Production> getAlternatingSideBranches(TreeTypes.MonopodialTree params) {

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
							(float) (int) ((Math.sqrt(nB2 - finalI) + 2) * 4 * getFloatParam(params, "dL") * vars.get("l")),
							0f,
							(float) Math.toRadians(140),
							(float) Math.toRadians(40))),
					new ParametricValueModule('&', (float) Math.toRadians(aS4)),
					new ParametricExpressionModule('F', List.of("l"), vars -> List.of(
							(float) Math.sqrt(nB2 - finalI) * vars.get("l") * lS4,
							(float) (int) ((Math.sqrt(nB2 - finalI) + 2) * 4 * getFloatParam(params, "dL") * vars.get("l")),
							0f,
							(float) Math.toRadians(140),
							(float) Math.toRadians(40))),
					new CharModule('%'),
					RB,
					new ParametricValueModule('&', (float) Math.toRadians(-aU)) // Causes slight curve downwards
			));
		}
		BOut.add(new ParametricExpressionModule('F', List.of("l"), vars -> List.of(vars.get("l") / 2f)));
		return List.of(new ProductionBuilder(List.of(BIn), BOut).build());
	}

	private static List<Production> getPineSideBranches(TreeTypes.MonopodialTree params) {

		ParametricParameterModule BIn = new ParametricParameterModule('B', List.of("w", "l"));
		List<Module> startModules = List.of(
				new ParametricExpressionModule('/', List.of(), vars -> List.of(
						(float) Math.toRadians(getFloatParam(params, "aS2")))),
				new ParametricExpressionModule('!', List.of("w"), vars -> List.of(vars.get("w"))),
				new ParametricExpressionModule('&', List.of(), vars -> List.of(
						(float) Math.toRadians(-getFloatParam(params, "aU")))),
				new ParametricExpressionModule('F', List.of("l"), vars -> List.of(
						vars.get("l"),
						300 * getFloatParam(params, "dL") * vars.get("l"),
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
