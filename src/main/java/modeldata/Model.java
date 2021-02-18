package modeldata;

import java.util.List;
import lombok.AllArgsConstructor;
import rendering.ShaderProgram;

@AllArgsConstructor
public class Model {
	protected List<Mesh> meshes;

	public void render(ShaderProgram shaderProgram) {
		for (Mesh mesh : meshes) {
			mesh.render(shaderProgram);
		}
	}
}
