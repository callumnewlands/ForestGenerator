package rendering;

import lombok.Getter;

public class VertexAttribute {

	private final String name;
	@Getter
	private final int numberOfFloatComponents;
	@Getter
	private final int location;

	public static final VertexAttribute POSITION = new VertexAttribute(0, "position", 3);

	public VertexAttribute(final int location, final String name, final int numberOfFloatComponents) {
		this.location = location;
		this.name = name;
		this.numberOfFloatComponents = numberOfFloatComponents;
	}
}