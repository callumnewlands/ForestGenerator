package modeldata.meshdata;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;
import org.joml.Vector3f;

@Getter
@Setter
public class Vertex {
	private Vector3f position;
	private Vector3f normal;
	private Vector3f tangent;
	private Vector2f texCoord;

	public Vertex(Vector3f position) {
		this.position = position;
	}


	public Vertex(Vector3f position, Vector3f normal) {
		this.position = position;
		this.normal = normal;
	}

	public Vertex(Vector3f position, Vector3f normal, Vector2f texCoord) {
		this.position = position;
		this.normal = normal;
		this.texCoord = texCoord;
	}

	public Vertex(Vector3f position, Vector3f normal, Vector3f tangent, Vector2f texCoord) {
		this.position = position;
		this.normal = normal;
		this.tangent = tangent;
		this.texCoord = texCoord;
	}
}
