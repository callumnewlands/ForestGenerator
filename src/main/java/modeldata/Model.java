package modeldata;

import java.util.List;
import modeldata.meshdata.Texture;
import rendering.ShaderProgram;

// TODO refactor models and meshes

public interface Model {
	void render(ShaderProgram shaderProgram);

	void addTextures(String uniform, List<Texture> textures);

	List<Mesh> getMeshes();
}
