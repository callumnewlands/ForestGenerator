package rendering;

public class VertexAttribute {

	private final String name;
	private final int numberOfFloatComponents;
	private final int location;

	public static final VertexAttribute POSITION = new VertexAttribute(0, "position", 3);

	public VertexAttribute(final int location, final String name, final int numberOfFloatComponents) {
		this.location = location;
		this.name = name;
		this.numberOfFloatComponents = numberOfFloatComponents;
	}

	public int getNumberOfFloatComponents() {
		return numberOfFloatComponents;
	}

	public int getLocation() {
		return location;
	}
}