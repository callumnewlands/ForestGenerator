package plantgeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lsystems.modules.Module;
import lsystems.modules.ParametricValueModule;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import utils.MathUtils;

public class TurtleInterpreter {

	private final float rotationAngle;
	private final float stepSize;
	private Vector3f position;
	private Vector3f heading;
	private Vector3f up;
	private List<Vector3f> vertices;

	public TurtleInterpreter() {
		this(1f, (float) Math.PI / 2);
	}

	public TurtleInterpreter(float stepSize, float rotationAngle) {
		this.rotationAngle = rotationAngle;
		this.stepSize = stepSize;
	}

	private void init() {
		this.position = new Vector3f(0, 0, 0);
		this.heading = new Vector3f(0, 1, 0);
		this.up = new Vector3f(0, 0, 1);
		this.vertices = new ArrayList<>(getCrossSection());
	}

	private List<Vector3f> getCrossSection() {

		final float r = 0.5f;
		List<Vector3f> unitCross = List.of(
				new Vector3f(-r, r, 0),
				new Vector3f(r, r, 0),
				new Vector3f(-r, -r, 0),
				new Vector3f(r, -r, 0)
		);
		Matrix4f model = (new Matrix4f())
				.identity()
				.translate(position);

		Vector3f globalUp = new Vector3f(0, 1, 0);
		Vector3f globalZ = new Vector3f(0, 0, 1);
		if (heading.equals(globalUp)) {
			model = model.lookAlong(heading, globalZ);
		} else {
			model = model.lookAlong(heading, globalUp);
		}
		return unitCross.stream().map(model::transformPosition).collect(Collectors.toList());
	}

	private void moveForwards(float distance, boolean drawGeometry) {
		position = MathUtils.add(position, MathUtils.multiply(distance, heading));
		// TODO this probably won't work for not drawing
		if (drawGeometry) {
			this.vertices.addAll(getCrossSection());
		}
	}

	private void turn(float angle, Vector3f axis) {
		// Negative angle as positive rotation is anticlockwise around axis when looking along the negative direction
		Matrix4f model = (new Matrix4f()).identity().rotate(-angle, axis);
		up = model.transformDirection(up);
		heading = model.transformDirection(heading);
		// TODO should it be drawn here too?
		this.vertices.addAll(getCrossSection());
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

	public List<Vector3f> interpretString(List<Module> instructions) {
		init();
		for (Module module : instructions) {
			switch (module.getName()) {
				case 'F' -> parseF(module);
				case '+' -> parseRotation(module, up);
				case '&' -> parseRotation(module, MathUtils.cross(up, heading).normalize());
				case '/' -> parseRotation(module, heading);
				default -> throw new RuntimeException("Unable to interpret module: " + module.toString());
			}
		}
		return this.vertices;
	}

	enum Axis {
		Heading, Up, Left
	}
}
