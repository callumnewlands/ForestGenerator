package modeldata;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.Texture;
import org.joml.Matrix4f;
import rendering.ShaderProgram;

@AllArgsConstructor
public class SingleModel implements Model {
	@Getter
	protected List<Mesh> meshes;

	public void render() {
		for (Mesh mesh : meshes) {
			mesh.render();
		}
	}

	public void addTextures(String uniform, List<? extends Texture> textures) {
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

	@Override
	public void setModelMatrix(Matrix4f model) {
		meshes.forEach(m -> m.setModel(model));
	}

	@Override
	public void setShaderProgram(ShaderProgram shaderProgram) {
		meshes.forEach(m -> m.setShaderProgram(shaderProgram));
	}

	@Override
	public void setIsInstanced(boolean value) {
		for (Mesh mesh : meshes) {
			mesh.setInstanced(value);
		}
	}
}