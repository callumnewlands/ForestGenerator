package plantgeneration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

@Getter
@Setter
@AllArgsConstructor
public class Vertex {
	private Vector3f position;
	private Vector3f normal;
}
