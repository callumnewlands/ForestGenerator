package plantgeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
import rendering.VertexArray;
import rendering.VertexAttribute;
import utils.VectorUtils;

public class TurtleInterpreter {

	private final float HALF = 0.5f;
	private final Stack<Turtle> states = new Stack<>();
	private final int numEdges;
	@Setter
	private float rotationAngle;
	@Setter
	private float stepSize;
	@Setter
	private List<Character> ignored = new ArrayList<>();
	@Setter
	private Vector4f tropism = null;
	private Turtle turtle = new Turtle();
	// List of lists so that discontinuities can be added to mesh
	private List<List<Vector3f>> vertices;

	// TODO Leaves (Geometry/model reference injection)

	public TurtleInterpreter(int numEdges) {
		this.numEdges = numEdges;
		this.stepSize = 1f;
		this.rotationAngle = (float) Math.PI / 2;
	}

	public TurtleInterpreter() {
		this(4);
	}

	private void init() {
		turtle.position = new Vector3f(0, 0, 0);
		turtle.heading = new Vector3f(0, 1, 0);
		turtle.up = new Vector3f(0, 0, 1);
		this.vertices = new ArrayList<>(List.of(getUnitCross()));
		turtle.prevCross = new ArrayList<>(getUnitCross());
		// Sets prevCross elements to be different objects to unitCross()
		updateCrossSection((new Matrix4f()).identity());
	}

	private List<Vector3f> getUnitCross() {
		List<Vector3f> vertices = new ArrayList<>();
		vertices.add(new Vector3f(0, 0, 0)); // Centre
		for (int i = 0; i < numEdges; i++) {
			double theta = 2 * Math.PI * i / numEdges;
			vertices.add(new Vector3f((float) Math.sin(theta) / 2, 0, (float) Math.cos(theta) / 2));
		}
		return vertices;
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

	private void addCrossSectionVertices(List<Vector3f> crossSection) {
		this.vertices.get(this.vertices.size() - 1).addAll(crossSection);
	}

	private void startNewVerticesSubList() {
		this.vertices.add(new ArrayList<>());
		addCrossSectionVertices(turtle.prevCross);
	}

	private void moveForwards(float distance) {
		Matrix4f model = (new Matrix4f()).translation(VectorUtils.multiply(distance, turtle.heading));
		turtle.position = model.transformPosition(turtle.position);
		updateCrossSection(model);

		addCrossSectionVertices(turtle.prevCross);
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

		addCrossSectionVertices(turtle.prevCross);
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

		// Add cross section only on first scale
		if (oldRadius == 0.5f) {
			addCrossSectionVertices(getUnitCross().stream()
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

	public List<List<Vector3f>> interpretInstructions(List<Module> instructions) {
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
				case '[' -> {
					states.push(this.turtle.copy());
				}
				case ']' -> {
					turtle = states.pop();
					startNewVerticesSubList();
				}
				case '!' -> parseEx(module);
				default -> throw new RuntimeException("Unable to interpret module: " + module.toString() +
						". Is it missing from TurtleInterpreter.ignored?");
			}
		}
		return this.vertices;
	}

	// Call after interpretInstructions
	public VertexArray getVAO() {
		List<List<Integer>> faces = new ArrayList<>();

		// Sides
		for (int i = 1; i < numEdges + 1; i++) {
			int j = (i % numEdges) + 1;
			faces.add(List.of(i, j, j + numEdges + 1, i + numEdges + 1));
		}

		int numSegments = (vertices.stream().mapToInt(List::size).sum() - (numEdges + 1)) / (numEdges + 1);
		float[] data = ArrayUtils.toPrimitive(
				IntStream.range(0, vertices.size()).boxed().flatMap(
						vertIndex -> IntStream.range(0, (vertices.get(vertIndex).size() - (numEdges + 1)) / (numEdges + 1)).boxed().flatMap(
								// For each segment, construct a prism
								i -> faces.stream()
										.map(f -> f
												.stream()
												.map(n -> n + (numEdges + 1) * i)
												.collect(Collectors.toList()))
										// For each face, calculate the normals and extract the vertex data
										.flatMap(f -> {
											List<Float> faceData = new ArrayList<>(6 * numEdges);
											int s = f.size();
											// TODO the normal is the same for each corner of the face
											// TODO smooth shading (add normals around vertex and normalise)
											for (int n = 0; n < s; n++) {
												int i0 = f.get(n);
												Vector3f v = vertices.get(vertIndex).get(i0);
												int i1 = f.get((n + 1) % s);
												int i2 = f.get((n + (s - 1)) % s);
												Vector3f a1 = VectorUtils.subtract(vertices.get(vertIndex).get(i1), v).normalize();
												Vector3f a2 = VectorUtils.subtract(vertices.get(vertIndex).get(i2), v).normalize();
												Vector3f norm = VectorUtils.cross(a2, a1).normalize();
												faceData.addAll(List.of(v.x, v.y, v.z, norm.x, norm.y, norm.z));
											}
											return faceData.stream();
										}))
				).toArray(Float[]::new));

		List<Integer> prismIndices = new ArrayList<>();
		// sides
		for (int i = 0; i < numEdges; i++) {
			int finalI = i;
			prismIndices.addAll(List.of(0, 1, 2, 2, 3, 0).stream().map(n -> n + 4 * finalI).collect(Collectors.toList()));
		}

		int[] indices = IntStream.range(0, numSegments).boxed().flatMapToInt(
				i -> prismIndices.stream().mapToInt(n -> n + (4 * numEdges) * i)
		).toArray();

		return new VertexArray(data, data.length / 6, indices, List.of(VertexAttribute.POSITION, VertexAttribute.NORMAL));
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
