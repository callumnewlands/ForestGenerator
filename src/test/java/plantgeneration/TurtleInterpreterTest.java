package plantgeneration;

import java.util.List;

import static lsystems.modules.DefinedModules.F;
import static lsystems.modules.DefinedModules.PL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import lsystems.modules.Module;
import lsystems.modules.ParametricValueModule;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

class TurtleInterpreterTest {

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

		assertEquals(expected, result);
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

		assertEquals(expected, result);
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

		assertEquals(expected, result);
	}

}