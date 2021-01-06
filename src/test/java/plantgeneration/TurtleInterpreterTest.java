package plantgeneration;

import java.util.List;

import static lsystems.modules.DefinedModules.F;
import static lsystems.modules.DefinedModules.LB;
import static lsystems.modules.DefinedModules.MI;
import static lsystems.modules.DefinedModules.PL;
import static lsystems.modules.DefinedModules.RB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lsystems.modules.CharModule;
import lsystems.modules.Module;
import lsystems.modules.ParametricValueModule;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

class TurtleInterpreterTest {

	private static void assertVectorListsEqual(List<Vector3f> expected, List<Vector3f> actual) {
		assertEquals(expected.size(), actual.size());
		final float delta = 0.000001f;
		for (int i = 0; i < expected.size(); i++) {
			assertTrue(actual.get(i).equals(expected.get(i), delta),
					String.format("Actual(%d) = %s 'neq' Expected(%d) = %s", i, actual.get(i), i, expected.get(i)));
		}
	}

	@Test
	public void handlesForwardCorrectly() {
		TurtleInterpreter interpreter = new TurtleInterpreter();

		List<Vector3f> result = interpreter.interpretInstructions(List.of(F, F, F));

		final float r = 0.5f;
		List<Vector3f> expected = List.of(
				new Vector3f(r, 0, -r),
				new Vector3f(r, 0, r),
				new Vector3f(-r, 0, r),
				new Vector3f(-r, 0, -r),

				new Vector3f(r, 1, -r),
				new Vector3f(r, 1, r),
				new Vector3f(-r, 1, r),
				new Vector3f(-r, 1, -r),

				new Vector3f(r, 2, -r),
				new Vector3f(r, 2, r),
				new Vector3f(-r, 2, r),
				new Vector3f(-r, 2, -r),

				new Vector3f(r, 3, -r),
				new Vector3f(r, 3, r),
				new Vector3f(-r, 3, r),
				new Vector3f(-r, 3, -r)
		);

		assertVectorListsEqual(expected, result);
	}

	@Test
	public void handlesPositiveRotationAroundLocalUp() {
		TurtleInterpreter interpreter = new TurtleInterpreter();

		List<Vector3f> result = interpreter.interpretInstructions(List.of(PL, PL, PL, PL));

		// Positive (anticlockwise) 90deg rotations around -ve Z (looking towards origin)
		final float r = 0.5f;
		List<Vector3f> expected = List.of(
				new Vector3f(r, 0, -r),
				new Vector3f(r, 0, r),
				new Vector3f(-r, 0, r),
				new Vector3f(-r, 0, -r),

				new Vector3f(0, r, -r),
				new Vector3f(0, r, r),
				new Vector3f(0, -r, r),
				new Vector3f(0, -r, -r),

				new Vector3f(-r, 0, -r),
				new Vector3f(-r, 0, r),
				new Vector3f(r, 0, r),
				new Vector3f(r, 0, -r),

				new Vector3f(0, -r, -r),
				new Vector3f(0, -r, r),
				new Vector3f(0, r, r),
				new Vector3f(0, r, -r),

				new Vector3f(r, 0, -r),
				new Vector3f(r, 0, r),
				new Vector3f(-r, 0, r),
				new Vector3f(-r, 0, -r)
		);

		assertVectorListsEqual(expected, result);
	}

	@Test
	public void handlesNegativeRotationAroundLocalUp() {
		TurtleInterpreter interpreter = new TurtleInterpreter();

		Module MI = new ParametricValueModule('+', -(float) Math.PI / 2);

		List<Vector3f> result = interpreter.interpretInstructions(List.of(MI, MI, MI, MI));

		// Positive (anticlockwise) 90deg rotations around -ve Z (looking towards origin)
		final float r = 0.5f;
		List<Vector3f> expected = List.of(
				new Vector3f(r, 0, -r),
				new Vector3f(r, 0, r),
				new Vector3f(-r, 0, r),
				new Vector3f(-r, 0, -r),

				new Vector3f(0, -r, -r),
				new Vector3f(0, -r, r),
				new Vector3f(0, r, r),
				new Vector3f(0, r, -r),

				new Vector3f(-r, 0, -r),
				new Vector3f(-r, 0, r),
				new Vector3f(r, 0, r),
				new Vector3f(r, 0, -r),

				new Vector3f(0, r, -r),
				new Vector3f(0, r, r),
				new Vector3f(0, -r, r),
				new Vector3f(0, -r, -r),

				new Vector3f(r, 0, -r),
				new Vector3f(r, 0, r),
				new Vector3f(-r, 0, r),
				new Vector3f(-r, 0, -r)
		);

		assertVectorListsEqual(expected, result);
	}

	@Test
	public void handlesForwardsAndRotations() {

		TurtleInterpreter interpreter = new TurtleInterpreter();
		List<Vector3f> result = interpreter.interpretInstructions(List.of(F, PL, F, F, MI, F, F, MI));

		final float r = 0.5f;
		List<Vector3f> expected = List.of(
				new Vector3f(r, 0, -r),
				new Vector3f(r, 0, r),
				new Vector3f(-r, 0, r),
				new Vector3f(-r, 0, -r),

				new Vector3f(r, 1, -r),
				new Vector3f(r, 1, r),
				new Vector3f(-r, 1, r),
				new Vector3f(-r, 1, -r),

				new Vector3f(0, 1 + r, -r),
				new Vector3f(0, 1 + r, r),
				new Vector3f(0, 1 - r, r),
				new Vector3f(0, 1 - r, -r),

				new Vector3f(-1, 1 + r, -r),
				new Vector3f(-1, 1 + r, r),
				new Vector3f(-1, 1 - r, r),
				new Vector3f(-1, 1 - r, -r),

				new Vector3f(-2, 1 + r, -r),
				new Vector3f(-2, 1 + r, r),
				new Vector3f(-2, 1 - r, r),
				new Vector3f(-2, 1 - r, -r),

				new Vector3f(-2 + r, 1, -r),
				new Vector3f(-2 + r, 1, r),
				new Vector3f(-2 - r, 1, r),
				new Vector3f(-2 - r, 1, -r),

				new Vector3f(-2 + r, 2, -r),
				new Vector3f(-2 + r, 2, r),
				new Vector3f(-2 - r, 2, r),
				new Vector3f(-2 - r, 2, -r),

				new Vector3f(-2 + r, 3, -r),
				new Vector3f(-2 + r, 3, r),
				new Vector3f(-2 - r, 3, r),
				new Vector3f(-2 - r, 3, -r),

				new Vector3f(-2, 3 - r, -r),
				new Vector3f(-2, 3 - r, r),
				new Vector3f(-2, 3 + r, r),
				new Vector3f(-2, 3 + r, -r)

		);

		assertVectorListsEqual(expected, result);

	}

	@Test
	public void handlesIgnored() {
		TurtleInterpreter interpreter = new TurtleInterpreter();
		CharModule X = new CharModule('X');
		interpreter.setIgnored(List.of(X.getName()));
		List<Vector3f> result = interpreter.interpretInstructions(List.of(X, F, F, X, F, X, X));

		final float r = 0.5f;
		List<Vector3f> expected = List.of(
				new Vector3f(r, 0, -r),
				new Vector3f(r, 0, r),
				new Vector3f(-r, 0, r),
				new Vector3f(-r, 0, -r),

				new Vector3f(r, 1, -r),
				new Vector3f(r, 1, r),
				new Vector3f(-r, 1, r),
				new Vector3f(-r, 1, -r),

				new Vector3f(r, 2, -r),
				new Vector3f(r, 2, r),
				new Vector3f(-r, 2, r),
				new Vector3f(-r, 2, -r),

				new Vector3f(r, 3, -r),
				new Vector3f(r, 3, r),
				new Vector3f(-r, 3, r),
				new Vector3f(-r, 3, -r)
		);

		assertVectorListsEqual(expected, result);
	}

	@Test
	public void handlesBrackets() {
		TurtleInterpreter interpreter = new TurtleInterpreter();
		CharModule X = new CharModule('X');
		interpreter.setIgnored(List.of(X.getName()));
		List<Vector3f> result = interpreter.interpretInstructions(List.of(F, LB, PL, X, RB, LB, MI, X, RB, F, X));

		final float r = 0.5f;
		List<Vector3f> expected = List.of(
				new Vector3f(r, 0, -r),
				new Vector3f(r, 0, r),
				new Vector3f(-r, 0, r),
				new Vector3f(-r, 0, -r),

				new Vector3f(r, 1, -r),
				new Vector3f(r, 1, r),
				new Vector3f(-r, 1, r),
				new Vector3f(-r, 1, -r),

				new Vector3f(0, 1 + r, -r),
				new Vector3f(0, 1 + r, r),
				new Vector3f(0, 1 - r, r),
				new Vector3f(0, 1 - r, -r),

				new Vector3f(0, 1 - r, -r),
				new Vector3f(0, 1 - r, r),
				new Vector3f(0, 1 + r, r),
				new Vector3f(0, 1 + r, -r),

				new Vector3f(r, 2, -r),
				new Vector3f(r, 2, r),
				new Vector3f(-r, 2, r),
				new Vector3f(-r, 2, -r)
		);

		assertVectorListsEqual(expected, result);

	}

}