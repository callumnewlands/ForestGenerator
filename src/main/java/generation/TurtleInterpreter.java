package generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lsystems.modules.Module;
import lsystems.modules.ParametricExpressionModule;
import lsystems.modules.ParametricValueModule;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.Vertex;
import modeldata.meshdata.VertexAttribute;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import params.ParameterLoader;
import utils.MathsUtils;
import utils.MeshUtils;
import utils.VectorUtils;

public class TurtleInterpreter {

	private final Stack<Turtle> states = new Stack<>();
	private final int numEdges;
	@Setter
	private float rotationAngle;
	@Setter
	private float stepSize;
	@Setter
	private List<Character> ignored = new ArrayList<>();
	private boolean firstScale = true;
	private Vector4f tropism = null;
	@Setter
	private List<Mesh> subModels = new ArrayList<>();
	private List<ModelReference> injectedModels = new ArrayList<>();

	private Turtle turtle = new Turtle();
	// List of lists so that discontinuities can be added to mesh
	private List<List<Vector3f>> vertices;

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
		this.vertices = new ArrayList<>();
		vertices.add(new ArrayList<>());
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
		turtle.prevRadius = turtle.radius;
		this.vertices.get(turtle.vertexListIndex).addAll(crossSection);
	}

	private void startNewVerticesSubList() {
		this.vertices.add(new ArrayList<>());
		this.turtle.vertexListIndex = this.vertices.size() - 1;
		addCrossSectionVertices(turtle.prevCross);
	}

	private void moveForwards(float distance) {
		if (distance == 0f) {
			return;
		}
		Matrix4f model = (new Matrix4f()).translation(VectorUtils.multiply(distance, VectorUtils.normalize(turtle.heading)));
		turtle.position = model.transformPosition(turtle.position);
		updateCrossSection(model);

		addCrossSectionVertices(turtle.prevCross);
	}

	private void moveForwardsWithLeaves(float distance, int numLeaves, int index, float radialAngle, float liftAngle) {
		liftAngle = -liftAngle;
		if (liftAngle > Math.PI) {
			liftAngle = -(float) (2 * Math.PI - liftAngle);
		} else if (liftAngle < -Math.PI) {
			liftAngle = (float) (2 * Math.PI + liftAngle);
		}
		if (radialAngle > Math.PI) {
			radialAngle = -(float) (2 * Math.PI - radialAngle);
		} else if (radialAngle < -Math.PI) {
			radialAngle = (float) (2 * Math.PI + radialAngle);
		}

		float step = distance / (numLeaves + 1);
		Turtle leafTurtle = turtle.copy();
		Vector3f pitchAxis = VectorUtils.cross(leafTurtle.up, leafTurtle.heading).normalize();
		Vector3f stemAxis = VectorUtils.normalize(leafTurtle.heading);
		Vector3f stepVector = VectorUtils.multiply(step, stemAxis);
		Vector3f centre = new Vector3f(leafTurtle.prevCross.get(0));

		// Pitch up by liftAngle
		Quaternionf rotation = new Quaternionf(new AxisAngle4f(liftAngle, pitchAxis));
		Matrix4f model = (new Matrix4f()).identity().rotateAround(rotation,
				leafTurtle.position.x,
				leafTurtle.position.y,
				leafTurtle.position.z);
		leafTurtle.up = model.transformDirection(leafTurtle.up).normalize();
		leafTurtle.heading = model.transformDirection(leafTurtle.heading).normalize();


		for (int i = 0; i < numLeaves; i++) {
			// Move along by step
			model = (new Matrix4f()).translation(stepVector);
			leafTurtle.position = model.transformPosition(leafTurtle.position);
			centre = model.transformPosition(centre);

			// Move along by offset
			Random r = ParameterLoader.getParameters().random.generator;
			float offsetStep = r.nextFloat() * step - step / 2;
			model = (new Matrix4f()).translation(VectorUtils.multiply(offsetStep, stemAxis));
			leafTurtle.position = model.transformPosition(leafTurtle.position);
			centre = model.transformPosition(centre);

			//  Move out by radius
			float currentRadius = MathsUtils.lerp(leafTurtle.prevRadius, leafTurtle.radius, (float) (i + 1) / (numLeaves + 1));
			model = (new Matrix4f()).translation(VectorUtils.multiply(currentRadius, leafTurtle.up));
			leafTurtle.position = model.transformPosition(leafTurtle.position);

			// Rotate around stem
			rotation = new Quaternionf(new AxisAngle4f(radialAngle, stemAxis));
			model = (new Matrix4f()).identity().rotateAround(rotation, centre.x, centre.y, centre.z);
			leafTurtle.up = model.transformDirection(leafTurtle.up).normalize();
			leafTurtle.heading = model.transformDirection(leafTurtle.heading).normalize();
			leafTurtle.position = model.transformPosition(leafTurtle.position);

			// Rotate by offset
			float offsetAngle = r.nextFloat() * radialAngle - radialAngle / 2;
			rotation = new Quaternionf(new AxisAngle4f(offsetAngle, stemAxis));
			model = (new Matrix4f()).identity().rotateAround(rotation, centre.x, centre.y, centre.z);
			leafTurtle.up = model.transformDirection(leafTurtle.up).normalize();
			leafTurtle.heading = model.transformDirection(leafTurtle.heading).normalize();
			leafTurtle.position = model.transformPosition(leafTurtle.position);

			// Inject leaf model
			injectedModels.add(new ModelReference(index, leafTurtle));

			// Rotate back by offset
			rotation = new Quaternionf(new AxisAngle4f(-offsetAngle, stemAxis));
			model = (new Matrix4f()).identity().rotateAround(rotation, centre.x, centre.y, centre.z);
			leafTurtle.up = model.transformDirection(leafTurtle.up).normalize();
			leafTurtle.heading = model.transformDirection(leafTurtle.heading).normalize();
			leafTurtle.position = model.transformPosition(leafTurtle.position);

			//  Move back in by radius
			model = (new Matrix4f()).translation(VectorUtils.multiply(-currentRadius, leafTurtle.up));
			leafTurtle.position = model.transformPosition(leafTurtle.position);

			// Move back by offset
			model = (new Matrix4f()).translation(VectorUtils.multiply(-offsetStep, stemAxis));
			leafTurtle.position = model.transformPosition(leafTurtle.position);
			centre = model.transformPosition(centre);
		}
		moveForwards(distance);
	}

	private void turn(float angle, Vector3f axis) {
		if (angle > Math.PI) {
			angle = -(float) (2 * Math.PI - angle);
		} else if (angle < -Math.PI) {
			angle = (float) (2 * Math.PI + angle);
		}
		Quaternionf rotation = new Quaternionf(new AxisAngle4f(angle, axis));
		Matrix4f model = (new Matrix4f()).identity().rotateAround(rotation,
				turtle.position.x,
				turtle.position.y,
				turtle.position.z);
		turtle.up = model.transformDirection(turtle.up).normalize();
		turtle.heading = model.transformDirection(turtle.heading).normalize();

		// Prevents twisting along prism axis
		if (!axis.equals(turtle.heading)) {
			updateCrossSection(model);
		}

	}

	private void turnToVertical() {
		Vector3f up = new Vector3f(0, 1, 0);
		Vector3f left = VectorUtils.cross(up, turtle.heading).normalize();

		turtle.up = VectorUtils.cross(turtle.heading, left).normalize();
//		addCrossSectionVertices(turtle.prevCross);
	}

	private void closeFace() {
		addCrossSectionVertices(Collections.nCopies(numEdges + 1, turtle.position)
				.stream()
				.map(v -> new Vector3f(v.x, v.y, v.z))
				.collect(Collectors.toList()));
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

		if (firstScale) {
			addCrossSectionVertices(getUnitCross().stream()
					.map(Vector3f::new)
					.map(model::transformPosition)
					.collect(Collectors.toList()));
			firstScale = false;
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
		turtle.up = model.transformDirection(turtle.up).normalize();
		turtle.heading = model.transformDirection(turtle.heading).normalize();
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
			case 1 -> {
				float distance = getFirstValueFromParametricModule(module);
				moveForwards(distance);
			}
			case 5 -> {
				List<Float> args = getAllValuesFromParametricModule(module);
				float distance = args.get(0);
				int numLeaves = args.get(1).intValue();
				if (numLeaves <= 0) {
					moveForwards(distance);
					return;
				}
				int index = args.get(2).intValue();
				float radialAngle = args.get(3);
				float liftAngle = args.get(4);
				if (index >= subModels.size()) {
					throw new RuntimeException("Referenced model ID: " + index + " is not in subModels list");
				}
				moveForwardsWithLeaves(distance, numLeaves, index, radialAngle, liftAngle);
			}
			default -> throw new RuntimeException("Undefined number of parameters in: " + module.toString());
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
		if (module.getNumberOfParameters() == 1) {
			float angle = getFirstValueFromParametricModule(module);
			turn(angle, axis);
		} else {
			throw new RuntimeException("Undefined number of parameters in: " + module.toString());
		}
	}

	private void parseEx(Module module) {
		if (module.getNumberOfParameters() == 1) {
			float diameter = getFirstValueFromParametricModule(module);
			scale(diameter / 2);
		} else {
			throw new RuntimeException("Incorrect number of parameters in: " + module.toString());
		}
	}

	private void parseTilde(Module module) {
		if (module.getNumberOfParameters() == 1) {
			int index = (int) getFirstValueFromParametricModule(module);
			if (index >= subModels.size()) {
				throw new RuntimeException("Referenced model ID: " + index + " is not in subModels list");
			}
			injectedModels.add(new ModelReference(index, turtle));
		} else {
			throw new RuntimeException("Incorrect number of parameters in: " + module.toString());
		}
	}

	public List<List<Vector3f>> interpretInstructions(List<Module> instructions) {
		init();
		for (Module module : instructions) {
			char name = module.getName();
			if (ignored.contains(name)) {
				continue;
			}
			if (module instanceof ParametricExpressionModule) {
				throw new RuntimeException("ParametricExpressionModule present and not ignored in instructions: " + module.toString());
			}
			switch (name) {
				case 'F' -> parseF(module);
				case 'T' -> parseT(module);
				case '+' -> parseRotation(module, turtle.up);
				case '-' -> turn(-this.rotationAngle, turtle.up);
				case '&' -> parseRotation(module, VectorUtils.cross(turtle.up, turtle.heading).normalize());
				case '/' -> parseRotation(module, turtle.heading);
				case '$' -> turnToVertical();
				case '%' -> closeFace();
				case '[' -> {
					states.push(this.turtle.copy());
					startNewVerticesSubList();
				}
				case ']' -> turtle = states.pop();
				case '!' -> parseEx(module);
				case '~' -> parseTilde(module);
				default -> throw new RuntimeException("Unable to interpret module: " + module.toString() +
						". Is it missing from TurtleInterpreter.ignored?");
			}
		}
		return this.vertices;
	}

	// Call after interpretInstructions
	public Mesh getMesh() {
		List<List<Integer>> faces = new ArrayList<>();

		// Sides
		for (int i = 1; i < numEdges + 1; i++) {
			int j = (i % numEdges) + 1;
			faces.add(List.of(i, j, j + numEdges + 1, i + numEdges + 1));
		}

		List<Vertex> vertexData = new ArrayList<>();
		HashMap<Vector3f, Vector3f> normalSum = new HashMap<>();
		HashMap<Vector3f, Vector3f> tangentSum = new HashMap<>();

		for (List<Vector3f> verts : vertices) {
			for (int i = 0; i < (verts.size() - (numEdges + 1)) / (numEdges + 1); i++) {
				for (List<Integer> face : faces) {
					int finalI = i;
					List<Integer> f = face.stream().map(n -> n + (numEdges + 1) * finalI).collect(Collectors.toList());

					int bottomRight = face.get(0);

					// TODO some way of mapping the texture across n (or 1/nth of) segments not stretching/squashing it to fit one?

					final float texXScale = 2;
					final float texYScale = 2;

					// Only works for rectangular faces
					List<Vector2f> textureCoords = List.of(
							new Vector2f((float) (bottomRight - 1) / numEdges * texXScale, 0),
							new Vector2f((float) bottomRight / numEdges * texXScale, 0),
							new Vector2f((float) bottomRight / numEdges * texXScale, texYScale),
							new Vector2f((float) (bottomRight - 1) / numEdges * texXScale, texYScale)
					);

					// Calculate normals
					int s = face.size();
					for (int n = 0; n < s; n++) {
						int i0 = f.get(n);
						Vector3f v = verts.get(i0);
						int i1 = f.get((n + 1) % s);
						int i2 = f.get((n + (s - 1)) % s);
						Vector3f a1 = VectorUtils.subtract(verts.get(i1), v).normalize();
						Vector3f a2 = VectorUtils.subtract(verts.get(i2), v).normalize();
						Vector3f norm = VectorUtils.cross(a2, a1).normalize().negate();
						normalSum.putIfAbsent(v, norm);
						normalSum.computeIfPresent(v, (key, val) -> VectorUtils.add(val, norm));
						// Only works for rectangular faces
						Vector3f tang = a1;
						tangentSum.putIfAbsent(v, tang);
						tangentSum.computeIfPresent(v, (key, val) -> VectorUtils.add(val, tang));
						vertexData.add(new Vertex(
								new Vector3f(v.x, v.y, v.z),
								new Vector3f(norm.x, norm.y, norm.z),
								new Vector3f(tang.x, tang.y, tang.z),
								textureCoords.get(n)));
					}
				}
			}
		}

		for (Vertex vertex : vertexData) {
			vertex.setNormal(normalSum.get(vertex.getPosition()).normalize());
			vertex.setTangent(tangentSum.get(vertex.getPosition()).normalize());
		}

		List<Integer> prismIndices = new ArrayList<>();
		// sides
		for (int i = 0; i < numEdges; i++) {
			int finalI = i;
			prismIndices.addAll(List.of(0, 1, 2, 2, 3, 0).stream().map(n -> n + 4 * finalI).collect(Collectors.toList()));
		}

		int numSegments = (vertices.stream().mapToInt(List::size).sum() - (numEdges + 1)) / (numEdges + 1);
		int[] indices = IntStream.range(0, numSegments).boxed().flatMapToInt(
				i -> prismIndices.stream().mapToInt(n -> n + (4 * numEdges) * i)
		).toArray();

		return new Mesh(vertexData, indices, List.of(VertexAttribute.POSITION, VertexAttribute.NORMAL, VertexAttribute.TANGENT, VertexAttribute.TEXTURE));
	}

	// Call after interpretInstructions
	public List<Mesh> getCombinedSubModelMeshes() {

		List<Mesh> meshes = new ArrayList<>();

		for (int i = 0; i < subModels.size(); i++) {
			int finalI = i;
			final Mesh model = subModels.get(finalI);
			final List<ModelReference> refs = injectedModels.stream()
					.filter(r -> r.index == finalI).collect(Collectors.toList());
			final List<Vertex> vertices = model.getVertices();
			final int[] indices = model.getIndices();
			int numVertices = vertices.size();

			List<Vertex> combinedVertices = new ArrayList<>();
			List<Integer> combinedIndices = new ArrayList<>();
			for (int j = 0; j < refs.size(); j++) {
				int finalJ = j;
				ModelReference ref = refs.get(j);
				Vector3f X = new Vector3f(1, 0, 0);
				List<Vertex> transformedVertices = MeshUtils.transform(
						vertices,
						new Matrix4f()
								.translate(ref.position)
								.rotate(new Quaternionf()
										.rotateTo(X, ref.heading)
										.rotateAxis((new Vector3f(0, 1, 0).angleSigned(ref.up, X)), X)
								));
				combinedVertices.addAll(transformedVertices);
				combinedIndices.addAll(Arrays.stream(indices)
						.map(n -> n + finalJ * numVertices)
						.boxed()
						.collect(Collectors.toList()));
			}
			int[] combinedIndicesArray = ArrayUtils.toPrimitive(combinedIndices.toArray(new Integer[0]));
			meshes.add(new Mesh(
					combinedVertices,
					combinedIndicesArray,
					model.getVertexAttributes()));
		}
		return meshes;
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
		private float prevRadius = 0.5f;
		private int vertexListIndex = 0;

		public Turtle copy() {
			return new Turtle(
					new Vector3f(this.position),
					new Vector3f(this.heading),
					new Vector3f(this.up),
					this.prevCross.stream()
							.map(Vector3f::new)
							.collect(Collectors.toList()),
					this.radius,
					this.prevRadius,
					this.vertexListIndex);
		}
	}

	private static class ModelReference {
		private final Vector3f position;
		private final Vector3f heading;
		private final Vector3f up;
		private final int index;

		public ModelReference(int index, Turtle turtle) {
			this.index = index;
			this.position = new Vector3f(turtle.position);
			this.heading = new Vector3f(turtle.heading);
			this.up = new Vector3f(turtle.up);
		}
	}
}
