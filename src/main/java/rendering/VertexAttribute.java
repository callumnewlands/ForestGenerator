package rendering;

import lombok.Getter;

public class VertexAttribute {

	@Getter
	private final String name;
	@Getter
	private final int numberOfFloatComponents;
	@Getter
	private final int location;

	public static final VertexAttribute POSITION = new VertexAttribute(0, "position", 3);
	public static final VertexAttribute NORMAL = new VertexAttribute(1, "normal", 3);
	public static final VertexAttribute TEXTURE = new VertexAttribute(2, "texCoord", 2);

	public VertexAttribute(final int location, final String name, final int numberOfFloatComponents) {
		this.location = location;
		this.name = name;
		this.numberOfFloatComponents = numberOfFloatComponents;
	}
}