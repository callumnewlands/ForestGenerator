package plantgeneration;

import java.util.ArrayList;
import java.util.Collections;
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
import utils.VectorUtils;

class TurtleInterpreterTest {


	private static void assertVectorsEqual(Vector3f expected, Vector3f actual, float delta) {
		assertTrue(actual.equals(expected, delta),
				String.format("Actual: %s 'neq' Expected: %s", actual, expected));

	}

	private static void assertVectorsEqual(Vector3f expected, Vector3f actual) {
		final float delta = 0.000001f;
		assertVectorsEqual(expected, actual, delta);
	}

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
	public void canHandleVeryLargeString() {
		TurtleInterpreter interpreter = new TurtleInterpreter();

		List<Module> instructions = List.of(
				F, LB, PL, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB,
				MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI,
				F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F,
				F, LB, PL, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB,
				MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI,
				F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F,
				F, LB, PL, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB,
				MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI,
				F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F,
				F, LB, PL, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB,
				MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI,
				F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F,
				F, LB, PL, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB,
				MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI,
				F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F,
				F, LB, PL, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB,
				MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI,
				F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F,
				F, LB, PL, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB,
				MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI,
				F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F,
				F, LB, PL, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB,
				MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI,
				F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F,
				F, LB, PL, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB,
				MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI,
				F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F,
				F, LB, PL, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB,
				MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI,
				F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F,
				F, LB, PL, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB,
				MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI,
				F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F,
				F, LB, PL, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB,
				MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI,
				F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F,
				F, LB, PL, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB,
				MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI,
				F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F,
				F, LB, PL, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB,
				MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI,
				F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F,
				F, LB, PL, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB,
				MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI,
				F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F,
				F, LB, PL, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB,
				MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI,
				F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F,
				F, LB, PL, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB,
				MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI,
				F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F,
				F, LB, PL, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB,
				MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, LB, MI, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL,
				F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, LB, MI,
				F, LB, PL, F, RB, LB, MI, F, RB, F, F, RB, F, F, LB, PL, F, RB, LB, MI, F, RB, F, F);

		List<Vector3f> result = interpreter.interpretInstructions(instructions);

		long expectedSize = 4 * (instructions.stream().filter(m -> !m.equals(LB) && !m.equals(RB)).count() + 1);

		assertEquals(expectedSize, result.size());
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

	@Test
	public void handlesTropism() {
		TurtleInterpreter interpreter = new TurtleInterpreter();
		float e = 0.1f;
		ParametricValueModule T = new ParametricValueModule('T', List.of(0f, -1f, 0f, e));

		List<Module> instructions = new ArrayList<>(List.of(T, F, PL));

		Vector3f prevHeading = new Vector3f(-1, 0, 0);
		double prevAngle = 0;
		// Doesn't work past i = 3 but I think that's because of compounded rounding errors
		for (int i = 0; i < 3; i++) {
			instructions.addAll(Collections.nCopies(i, F));
			System.out.println("Inst: " + instructions);
			interpreter.interpretInstructions(instructions);

			float a = e * VectorUtils.cross(prevHeading, new Vector3f(0, -1, 0)).length();
			double angle = prevAngle + a;
			prevAngle = angle;
			prevHeading = interpreter.getTurtleHeading();

			assertVectorsEqual(
					new Vector3f(-(float) Math.cos(angle), -(float) Math.sin(angle), 0),
					prevHeading,
					0.1f);
		}

	}

}