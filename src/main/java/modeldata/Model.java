package modeldata;

import java.util.List;
import lombok.AllArgsConstructor;
import modeldata.meshdata.Texture;
import rendering.ShaderProgram;

@AllArgsConstructor
public class Model {
	protected List<Mesh> meshes;

	public void render(ShaderProgram shaderProgram) {
		for (Mesh mesh : meshes) {
			mesh.render(shaderProgram);
		}
	}

	public void addTextures(String uniform, List<Texture> textures) {
		if (textures.size() != meshes.size()) {
			throw new RuntimeException("Unequal number of meshes and textures");
		}
		for (int i = 0; i < meshes.size(); i++) {
			Texture texture = textures.get(i);
			if (texture == null) {
				continue;
			}
			meshes.get(i).addTexture(uniform, texture);
		}
	}
}
