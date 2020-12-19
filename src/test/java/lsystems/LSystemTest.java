package lsystems;

import java.util.List;

import static lsystems.DefinedModules.F;
import static lsystems.DefinedModules.LB;
import static lsystems.DefinedModules.MI;
import static lsystems.DefinedModules.PL;
import static lsystems.DefinedModules.RB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LSystemTest {

	@Test
	public void LindenmayerAlgae() {
		CharModule A = new CharModule('A');
		CharModule B = new CharModule('B');
		LSystem ls = new LSystem(
				List.of(A),
				List.of(),
				List.of(new Production(
						List.of(A),
						List.of(A, B)
				), new Production(
						List.of(B),
						List.of(A)
				)));
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
				List.of(new Production(
						List.of(B),
						List.of(B, B)
				), new Production(
						List.of(A),
						List.of(B, LB, A, RB, A)
				)));
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
				List.of(new Production(
						List.of(X),
						List.of(F, LB, PL, X, RB, LB, MI, X, RB, F, X)
				), new Production(
						List.of(F),
						List.of(F, F)
				)));
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
				List.of(new Production(
						List.of(F),
						List.of(F, PL, F, MI, F, MI, F, PL, F)
				)));
		assertEquals("F", ls.getStateSting());
		assertEquals("F+F-F-F+F", ls.performDerivationStep());
		assertEquals("F+F-F-F+F+F+F-F-F+F-F+F-F-F+F-F+F-F-F+F+F+F-F-F+F", ls.performDerivationStep());
		assertEquals("F+F-F-F+F+F+F-F-F+F-F+F-F-F+F-F+F-F-F+F+F+F-F-F+F+" +
				"F+F-F-F+F+F+F-F-F+F-F+F-F-F+F-F+F-F-F+F+F+F-F-F+F-" +
				"F+F-F-F+F+F+F-F-F+F-F+F-F-F+F-F+F-F-F+F+F+F-F-F+F-" +
				"F+F-F-F+F+F+F-F-F+F-F+F-F-F+F-F+F-F-F+F+F+F-F-F+F+" +
				"F+F-F-F+F+F+F-F-F+F-F+F-F-F+F-F+F-F-F+F+F+F-F-F+F", ls.performDerivationStep());
	}
}