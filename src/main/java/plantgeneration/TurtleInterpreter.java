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
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import utils.MathUtils;

public class TurtleInterpreter {

	private final float r = 0.5f;
	private final List<Vector3f> unitCross = List.of(
			new Vector3f(r, 0, -r),
			new Vector3f(r, 0, r),
			new Vector3f(-r, 0, r),
			new Vector3f(-r, 0, -r)
	);
	@Setter
	private float rotationAngle;
	@Setter
	private float stepSize;
	@Setter
	private List<Character> ignored = new ArrayList<>();
	private Turtle turtle = new Turtle();
	private List<Vector3f> vertices;
	private final Stack<Turtle> states = new Stack<>();

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
		turtle.prevCross = turtle.prevCross.stream()
				.map(Vector3f::new)
				.map(model::transformPosition)
				.collect(Collectors.toList());
	}

	private void moveForwards(float distance, boolean drawGeometry) {
		Matrix4f model = (new Matrix4f()).translation(MathUtils.multiply(distance, turtle.heading));
		turtle.position = model.transformPosition(turtle.position);
		updateCrossSection(model);
		// TODO this probably won't work for not drawing - probably need to specify EBO as well
		if (drawGeometry) {
			this.vertices.addAll(turtle.prevCross);
		}
	}

	private void turn(float angle, Vector3f axis) {
//		Matrix4f model = (new Matrix4f()).identity().rotate(angle, axis);
		Matrix4f model = (new Matrix4f()).identity()
				.rotateAround(
						new Quaternionf(new AxisAngle4f(angle, axis)),
						turtle.position.x,
						turtle.position.y,
						turtle.position.z);
		turtle.up = model.transformDirection(turtle.up);
		turtle.heading = model.transformDirection(turtle.heading);
		updateCrossSection(model);
		// TODO should it be drawn here too?
		this.vertices.addAll(turtle.prevCross);
	}

	private void throwInvalidTypeException(Module module) {
		throw new RuntimeException(String.format("Unable to parse module type: %s for module %s",
				module.getClass(),
				module.toString()));
	}

	private void parseF(Module module) {
		switch (module.getNumberOfParameters()) {
			case 0:
				moveForwards(this.stepSize, true);
				break;
			case 1:
				if (module instanceof ParametricValueModule) {
					float step = ((ParametricValueModule) module).getValues().get(0);
					moveForwards(step, true);
				} else {
					throwInvalidTypeException(module);
				}
				break;
			default:
				throw new RuntimeException("Too many parameters in: " + module.toString());
		}
	}

	private void parseRotation(Module module, Vector3f axis) {
		switch (module.getNumberOfParameters()) {
			case 0:
				turn(this.rotationAngle, axis);
				break;
			case 1:
				if (module instanceof ParametricValueModule) {
					float angle = ((ParametricValueModule) module).getValues().get(0);
					turn(angle, axis);
				} else {
					throwInvalidTypeException(module);
				}
				break;
			default:
				throw new RuntimeException("Too many parameters in: " + module.toString());
		}
	}

	// Leaves
	// TODO cross section shape and size
	// TODO Tropism and other global flags?

	public List<Vector3f> interpretInstructions(List<Module> instructions) {
		init();
		for (Module module : instructions) {
			char name = module.getName();
			if (ignored.contains(name)) {
				continue;
			}
			switch (name) {
				case 'F' -> parseF(module);
				case '+' -> parseRotation(module, turtle.up);
				case '-' -> turn(-this.rotationAngle, turtle.up);
				case '&' -> parseRotation(module, MathUtils.cross(turtle.up, turtle.heading).normalize());
				case '/' -> parseRotation(module, turtle.heading);
				case '[' -> states.push(this.turtle.copy());
				case ']' -> turtle = states.pop();
				default -> throw new RuntimeException("Unable to interpret module: " + module.toString());
			}
		}
		return this.vertices;
	}

	@NoArgsConstructor
	@AllArgsConstructor
	private static class Turtle {
		private Vector3f position;
		private Vector3f heading;
		private Vector3f up;
		private List<Vector3f> prevCross;

		public Turtle copy() {
			return new Turtle(
					new Vector3f(this.position),
					new Vector3f(this.heading),
					new Vector3f(this.up),
					this.prevCross.stream()
							.map(Vector3f::new)
							.collect(Collectors.toList()));
		}
	}
}
