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
}