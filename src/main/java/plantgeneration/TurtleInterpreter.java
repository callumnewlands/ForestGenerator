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
	private final float r = 0.5f;
	private final List<Vector3f> unitCross = List.of(
			new Vector3f(r, 0, -r),
			new Vector3f(r, 0, r),
			new Vector3f(-r, 0, r),
			new Vector3f(-r, 0, -r)
	);
	private Vector3f position;
	private Vector3f heading;
	private Vector3f up;
	private List<Vector3f> vertices;
	private List<Vector3f> prevCross;

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
		this.vertices = new ArrayList<>(unitCross);
		prevCross = new ArrayList<>(unitCross);
		// Sets prevCross elements to be different objects to unitCross
		updateCrossSection((new Matrix4f()).identity());
	}

	private void updateCrossSection(Matrix4f model) {
		prevCross = prevCross.stream()
				.map(Vector3f::new)
				.map(model::transformPosition)
				.collect(Collectors.toList());
	}

	private void moveForwards(float distance, boolean drawGeometry) {
		Matrix4f model = (new Matrix4f()).translation(MathUtils.multiply(distance, heading));
		position = model.transformPosition(position);
		updateCrossSection(model);
		// TODO this probably won't work for not drawing
		if (drawGeometry) {
			this.vertices.addAll(prevCross);
		}
	}

	private void turn(float angle, Vector3f axis) {
		Matrix4f model = (new Matrix4f()).identity().rotate(angle, axis);
		up = model.transformDirection(up);
		heading = model.transformDirection(heading);
		updateCrossSection(model);
		// TODO should it be drawn here too?
		this.vertices.addAll(prevCross);
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
}
