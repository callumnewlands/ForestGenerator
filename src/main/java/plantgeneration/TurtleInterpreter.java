package plantgeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lsystems.modules.Module;
import lsystems.modules.ParametricValueModule;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import utils.VectorUtils;

public class TurtleInterpreter {

	private final float HALF = 0.5f;
	private final List<Vector3f> unitCross = List.of(
			new Vector3f(HALF, 0, -HALF),
			new Vector3f(HALF, 0, HALF),
			new Vector3f(-HALF, 0, HALF),
			new Vector3f(-HALF, 0, -HALF)
	);
	private final Stack<Turtle> states = new Stack<>();
	@Setter
	private float rotationAngle;
	@Setter
	private float stepSize;
	@Setter
	private List<Character> ignored = new ArrayList<>();
	@Setter
	private Vector4f tropism = null;
	private Turtle turtle = new Turtle();
	private List<Vector3f> vertices;

	public TurtleInterpreter() {
		this.stepSize = 1f;
		this.rotationAngle = (float) Math.PI / 2;
	}

	private void init() {
		turtle.position = new Vector3f(0, 0, 0);
		turtle.heading = new Vector3f(0, 1, 0);
		turtle.up = new Vector3f(0, 0, 1);
		this.vertices = new ArrayList<>(unitCross);
		turtle.prevCross = new ArrayList<>(unitCross);
		// Sets prevCross elements to be different objects to unitCross
		updateCrossSection((new Matrix4f()).identity());
	}

	private void updateCrossSection(Matrix4f model) {
		updateCrossSection(model, true);
	}

	private void updateCrossSection(Matrix4f model, boolean adjustForTropism) {
		turtle.prevCross = turtle.prevCross.stream()
				.map(Vector3f::new)
				.map(model::transformPosition)
				.collect(Collectors.toList());
		if (adjustForTropism) {
			adjustForTropisms();
		}
	}

	private void moveForwards(float distance) {
		Matrix4f model = (new Matrix4f()).translation(VectorUtils.multiply(distance, turtle.heading));
		turtle.position = model.transformPosition(turtle.position);
		updateCrossSection(model);

		this.vertices.addAll(turtle.prevCross);
	}

	private void turn(float angle, Vector3f axis) {
		Quaternionf rotation = new Quaternionf(new AxisAngle4f(angle, axis));
		Matrix4f model = (new Matrix4f()).identity().rotateAround(rotation,
				turtle.position.x,
				turtle.position.y,
				turtle.position.z);
		turtle.up = model.transformDirection(turtle.up);
		turtle.heading = model.transformDirection(turtle.heading);
		updateCrossSection(model);

		this.vertices.addAll(turtle.prevCross);
	}

	private void scale(float radius) {
		float oldRadius = turtle.radius;
		turtle.radius = radius;
		Matrix4f model = new Matrix4f().scaleAround(
				radius / oldRadius,
				turtle.position.x,
				turtle.position.y,
				turtle.position.z);
		updateCrossSection(model);

		if (oldRadius == 0.5f) {
			this.vertices.addAll(unitCross.stream()
					.map(Vector3f::new)
					.map(model::transformPosition)
					.collect(Collectors.toList()));
		}
	}


	private void adjustForTropisms() {
		if (tropism == null) {
			return;
		}
		Vector3f tropismVector = new Vector3f(tropism.x, tropism.y, tropism.z);
		float elasticity = tropism.w;
		float angle = elasticity * VectorUtils.cross(turtle.heading, tropismVector).length();
		Quaternionf rotation = new Quaternionf().identity().slerp(
				new Quaternionf().rotateTo(turtle.heading, tropismVector), angle / turtle.heading.angle(tropismVector));

		Matrix4f model = (new Matrix4f()).identity().rotateAround(rotation,
				turtle.position.x,
				turtle.position.y,
				turtle.position.z);
		turtle.up = model.transformDirection(turtle.up);
		turtle.heading = model.transformDirection(turtle.heading);
		updateCrossSection(model, false);
	}

	private void throwInvalidTypeException(Module module) throws RuntimeException {
		throw new RuntimeException(String.format("Unable to parse module type: %s for module %s",
				module.getClass(),
				module.toString()));
	}

	private float getFirstValueFromParametricModule(Module module) {
		return getAllValuesFromParametricModule(module).get(0);
	}

	private List<Float> getAllValuesFromParametricModule(Module module) {
		if (module instanceof ParametricValueModule) {
			return ((ParametricValueModule) module).getValues();
		} else {
			throwInvalidTypeException(module);
		}
		// Will never be reached as throwInvalidTypeException will throw first
		throw new RuntimeException();
	}

	private void parseF(Module module) {
		switch (module.getNumberOfParameters()) {
			case 0 -> moveForwards(this.stepSize);
			case 1 -> {
				float step = getFirstValueFromParametricModule(module);
				moveForwards(step);
			}
			default -> throw new RuntimeException("Too many parameters in: " + module.toString());
		}
	}

	private void parseT(Module module) {
		switch (module.getNumberOfParameters()) {
			case 1 -> {
				float val = getFirstValueFromParametricModule(module);
				// T(0) = stop applying tropism
				if (val == 0) {
					this.tropism = null;
				} else {
					throw new RuntimeException("Behaviour undefined: Non-zero single parameter in T module " + module.toString());
				}
			}
			case 4 -> {
				List<Float> vals = getAllValuesFromParametricModule(module);
				float[] floats = ArrayUtils.toPrimitive(vals.toArray(new Float[0]));
				this.tropism = new Vector4f(floats);
			}
			default -> throw new RuntimeException("Undefined number of parameters in: " + module.toString());
		}
	}

	private void parseRotation(Module module, Vector3f axis) {
		switch (module.getNumberOfParameters()) {
			case 0 -> turn(this.rotationAngle, axis);
			case 1 -> {
				float angle = getFirstValueFromParametricModule(module);
				turn(angle, axis);
			}
			default -> throw new RuntimeException("Too many parameters in: " + module.toString());
		}
	}

	private void parseEx(Module module) {
		if (module.getNumberOfParameters() == 1) {
			float diameter = getFirstValueFromParametricModule(module);
			scale(diameter / 2);
		} else {
			throw new RuntimeException("Too many parameters in: " + module.toString());
		}
	}
	// TODO Leaves (Geometry/model reference injection)
	// TODO cross section shape

	public List<Vector3f> interpretInstructions(List<Module> instructions) {
		init();
		for (Module module : instructions) {
			char name = module.getName();
			if (ignored.contains(name)) {
				continue;
			}
			switch (name) {
				case 'F' -> parseF(module);
				case 'T' -> parseT(module);
				case '+' -> parseRotation(module, turtle.up);
				case '-' -> turn(-this.rotationAngle, turtle.up);
				case '&' -> parseRotation(module, VectorUtils.cross(turtle.up, turtle.heading).normalize());
				case '/' -> parseRotation(module, turtle.heading);
				case '[' -> states.push(this.turtle.copy());
				case ']' -> turtle = states.pop();
				case '!' -> parseEx(module);
				default -> throw new RuntimeException("Unable to interpret module: " + module.toString() +
						". Is it missing from TurtleInterpreter.ignored?");
			}
		}
		return this.vertices;
	}

	public Vector3f getTurtleHeading() {
		return turtle.heading;
	}

	@NoArgsConstructor
	@AllArgsConstructor
	private static class Turtle {
		private Vector3f position;
		private Vector3f heading;
		private Vector3f up;
		private List<Vector3f> prevCross;
		private float radius = 0.5f;

		public Turtle copy() {
			return new Turtle(
					new Vector3f(this.position),
					new Vector3f(this.heading),
					new Vector3f(this.up),
					this.prevCross.stream()
							.map(Vector3f::new)
							.collect(Collectors.toList()),
					this.radius);
		}
	}
}
