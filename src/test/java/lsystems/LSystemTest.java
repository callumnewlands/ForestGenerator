package lsystems;

import java.util.List;

import static lsystems.modules.DefinedModules.F;
import static lsystems.modules.DefinedModules.LB;
import static lsystems.modules.DefinedModules.MI;
import static lsystems.modules.DefinedModules.PL;
import static lsystems.modules.DefinedModules.RB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import lsystems.modules.CharModule;
import lsystems.modules.Module;
import lsystems.modules.ParametricExpressionModule;
import lsystems.modules.ParametricParameterModule;
import lsystems.modules.ParametricValueModule;
import org.junit.jupiter.api.Test;

class LSystemTest {

	@Test
	public void LindenmayerAlgae() {
		CharModule A = new CharModule('A');
		CharModule B = new CharModule('B');
		LSystem ls = new LSystem(
				List.of(A),
				List.of(),
				List.of(new ProductionBuilder(
								List.of(A),
								List.of(A, B)
						).build(),
						new ProductionBuilder(
								List.of(B),
								List.of(A)
						).build()
				));
		assertEquals("A", ls.getStateSting());
		assertEquals("AB", ls.performDerivationStep());
		assertEquals("ABA", ls.performDerivationStep());
		assertEquals("ABAAB", ls.performDerivationStep());
		assertEquals("ABAABABA", ls.performDerivationStep());
		assertEquals("ABAABABAABAAB", ls.performDerivationStep());
		assertEquals("ABAABABAABAABABAABABA", ls.performDerivationStep());
		assertEquals("ABAABABAABAABABAABABAABAABABAABAAB", ls.performDerivationStep());

	}

	@Test
	public void SimpleBranching() {
		CharModule A = new CharModule('A');
		CharModule B = new CharModule('B');
		LSystem ls = new LSystem(
				List.of(A),
				List.of(LB, RB),
				List.of(new ProductionBuilder(
								List.of(B),
								List.of(B, B)
						).build(),
						new ProductionBuilder(
								List.of(A),
								List.of(B, LB, A, RB, A)
						).build()
				));
		assertEquals("A", ls.getStateSting());
		assertEquals("B[A]A", ls.performDerivationStep());
		assertEquals("BB[B[A]A]B[A]A", ls.performDerivationStep());
		assertEquals("BBBB[BB[B[A]A]B[A]A]BB[B[A]A]B[A]A", ls.performDerivationStep());
	}

	@Test
	public void MultipleBranching() {
		CharModule X = new CharModule('X');
		LSystem ls = new LSystem(
				List.of(X),
				List.of(LB, RB, PL, MI),
				List.of(new ProductionBuilder(
								List.of(X),
								List.of(F, LB, PL, X, RB, LB, MI, X, RB, F, X)
						).build(),
						new ProductionBuilder(
								List.of(F),
								List.of(F, F)
						).build()
				));
		assertEquals("X", ls.getStateSting());
		assertEquals("F[+X][-X]FX", ls.performDerivationStep());
		assertEquals("FF[+F[+X][-X]FX][-F[+X][-X]FX]FFF[+X][-X]FX", ls.performDerivationStep());
		assertEquals("FFFF[+FF[+F[+X][-X]FX][-F[+X][-X]FX]FFF[+X][-X]FX]" +
				"[-FF[+F[+X][-X]FX][-F[+X][-X]FX]FFF[+X][-X]FX]" +
				"FFFFFF[+F[+X][-X]FX][-F[+X][-X]FX]FFF[+X][-X]FX", ls.performDerivationStep());
	}

	@Test
	public void KochCurve() {
		LSystem ls = new LSystem(
				List.of(F),
				List.of(PL, MI),
				List.of(new ProductionBuilder(
								List.of(F),
								List.of(F, PL, F, MI, F, MI, F, PL, F)
						).build()
				));
		assertEquals("F", ls.getStateSting());
		assertEquals("F+F-F-F+F", ls.performDerivationStep());
		assertEquals("F+F-F-F+F+F+F-F-F+F-F+F-F-F+F-F+F-F-F+F+F+F-F-F+F", ls.performDerivationStep());
		assertEquals("F+F-F-F+F+F+F-F-F+F-F+F-F-F+F-F+F-F-F+F+F+F-F-F+F+" +
				"F+F-F-F+F+F+F-F-F+F-F+F-F-F+F-F+F-F-F+F+F+F-F-F+F-" +
				"F+F-F-F+F+F+F-F-F+F-F+F-F-F+F-F+F-F-F+F+F+F-F-F+F-" +
				"F+F-F-F+F+F+F-F-F+F-F+F-F-F+F-F+F-F-F+F+F+F-F-F+F+" +
				"F+F-F-F+F+F+F-F-F+F-F+F-F-F+F-F+F-F-F+F+F+F-F-F+F", ls.performDerivationStep());
	}

	@Test
	public void simpleParametricLSystem() {
		ParametricParameterModule Bin = new ParametricParameterModule('B', List.of("x"));
		Module Bout = new ParametricExpressionModule('B', List.of("x"), vars -> List.of(vars.get("x") + 1));
		LSystem ls = new LSystem(
				List.of(new ParametricValueModule('B', 0f)),
				List.of(),
				List.of(new ProductionBuilder(List.of(Bin), List.of(Bout)).build()
				));

		assertEquals("B(0.0)", ls.getStateSting());
		assertEquals("B(1.0)", ls.performDerivationStep());
		assertEquals("B(2.0)", ls.performDerivationStep());
		assertEquals("B(3.0)", ls.performDerivationStep());
		assertEquals("B(4.0)", ls.performDerivationStep());
		assertEquals("B(5.0)", ls.performDerivationStep());
		assertEquals("B(6.0)", ls.performDerivationStep());
		assertEquals("B(7.0)", ls.performDerivationStep());
		assertEquals("B(8.0)", ls.performDerivationStep());
		assertEquals("B(9.0)", ls.performDerivationStep());
		assertEquals("B(10.0)", ls.performDerivationStep());
	}

	@Test
	public void moreComplexParametricLSystem() {
		ParametricParameterModule Ain = new ParametricParameterModule('A', List.of("x", "y"));
		Module Aout1 = new ParametricExpressionModule('A', List.of("x", "y"),
				vars -> List.of(vars.get("x") * 2, vars.get("x") + vars.get("y")));
		Module Aout2 = new ParametricExpressionModule('B', List.of("x"),
				vars -> List.of(vars.get("x")));
		ParametricParameterModule Bin = new ParametricParameterModule('B', List.of("x"));
		Module Bout = new ParametricExpressionModule('B', List.of("x"), vars -> List.of(vars.get("x") - 1));
		LSystem ls = new LSystem(
				List.of(new ParametricValueModule('B', 2f),
						new ParametricValueModule('A', 4f, 2f)),
				List.of(),
				List.of(new ProductionBuilder(List.of(Bin), List.of(Bout)).build(),
						new ProductionBuilder(List.of(Ain), List.of(Aout1, Aout2)).build()
				));

		assertEquals("B(2.0)A(4.0,2.0)", ls.getStateSting());
		assertEquals("B(1.0)A(8.0,6.0)B(4.0)", ls.performDerivationStep());
		assertEquals("B(0.0)A(16.0,14.0)B(8.0)B(3.0)", ls.performDerivationStep());
		assertEquals("B(-1.0)A(32.0,30.0)B(16.0)B(7.0)B(2.0)", ls.performDerivationStep());
		assertEquals("B(-2.0)A(64.0,62.0)B(32.0)B(15.0)B(6.0)B(1.0)", ls.performDerivationStep());
		assertEquals("B(-3.0)A(128.0,126.0)B(64.0)B(31.0)B(14.0)B(5.0)B(0.0)", ls.performDerivationStep());
	}

	// L system from Figure 2.8 of 'Algorithmic Beauty of Plants' by Prusinkiewicz and Lindenmayer
	@Test
	public void canHandleABoP_2_8() {
		/*
			Param values used for fig 2.8:
			Fig d1 		d2 		a 		lr 		T (tropism force)	e 		n
			a 	94.74 	132.63 	18.95 	1.109 	0.00,-1.00,0.00 	0.22 	6
			b 	137.50 	137.50 	18.95 	1.109 	0.00,-1.00,0.00 	0.14 	8
			c 	112.50 	157.50 	22.50 	1.790 	-0.02,-1.00,0.00 	0.27 	8
			d 	180.00 	252.00 	36.00 	1.070 	-0.61,0.77,-0.19 	0.40 	6
		 */

		float d1 = 94.74f;
		float d2 = 132.63f;
		float a = 18.95f;
		float lr = 1.109f;
		float vr = 1.732f;

		CharModule A = new CharModule('A');
		ParametricParameterModule ExIn = new ParametricParameterModule('!', List.of("w"));
		Module ExOut = new ParametricExpressionModule('!', List.of("w"), vars -> List.of(vars.get("w") * vr));
		ParametricParameterModule FIn = new ParametricParameterModule('F', List.of("l"));
		Module FOut = new ParametricExpressionModule('F', List.of("l"), vars -> List.of(vars.get("l") * lr));

		LSystem ls = new LSystem(
				List.of(
						new ParametricValueModule('!', 1f),
						new ParametricValueModule('F', 200f),
						new ParametricValueModule('/', 45f),
						A
				),
				List.of(),
				List.of(
						new ProductionBuilder(List.of(A), List.of(
								new ParametricValueModule('!', vr),
								new ParametricValueModule('F', 50f),
								LB,
								new ParametricValueModule('&', a),
								new ParametricValueModule('F', 50f),
								A,
								RB,
								new ParametricValueModule('/', d1),
								LB,
								new ParametricValueModule('&', a),
								new ParametricValueModule('F', 50f),
								A,
								RB,
								new ParametricValueModule('/', d2),
								new ParametricValueModule('&', a),
								new ParametricValueModule('F', 50f),
								A
						)).build(),
						new ProductionBuilder(List.of(FIn), List.of(FOut)).build(),
						new ProductionBuilder(List.of(ExIn), List.of(ExOut)).build()
				));

		// Checks no errors thrown in 10 derivations steps
		for (int i = 0; i < 10; i++) {
			ls.performDerivationStep();
		}
	}
}