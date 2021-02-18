package modeldata.meshdata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;
import org.joml.Vector3f;

@Getter
@Setter
@AllArgsConstructor
public class Vertex {
	private Vector3f position;
	private Vector3f normal;
	private Vector3f tangent;
	private Vector2f texCoord;

	public Vertex(Vector3f position, Vector3f normal) {
		this.position = position;
		this.normal = normal;
	}

	public Vertex(Vector3f position, Vector3f normal, Vector2f texCoord) {
		this.position = position;
		this.normal = normal;
		this.texCoord = texCoord;
	}
}
