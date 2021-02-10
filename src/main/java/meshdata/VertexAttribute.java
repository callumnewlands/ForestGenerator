package meshdata;

import java.util.List;
import lombok.Getter;

public class VertexAttribute {

	@Getter
	private final String name;
	@Getter
	private final int numberOfFloatComponents;
	@Getter
	private final int location;
	@Getter
	private final int divisor;

	public static final VertexAttribute POSITION = new VertexAttribute(0, "position", 3);
	public static final VertexAttribute NORMAL = new VertexAttribute(1, "normal", 3);
	public static final VertexAttribute TEXTURE = new VertexAttribute(2, "texCoord", 2);
	public static final List<VertexAttribute> INSTANCE_MODEL = List.of(
			new VertexAttribute(3, "instanceModel", 4, 1),
			new VertexAttribute(4, "instanceModel", 4, 1),
			new VertexAttribute(5, "instanceModel", 4, 1),
			new VertexAttribute(6, "instanceModel", 4, 1)
	);

	public VertexAttribute(final int location, final String name, final int numberOfFloatComponents) {
		this(location, name, numberOfFloatComponents, 0);
	}

	public VertexAttribute(final int location, final String name, final int numberOfFloatComponents, final int divisor) {
		this.location = location;
		this.name = name;
		this.numberOfFloatComponents = numberOfFloatComponents;
		this.divisor = divisor;
	}
}