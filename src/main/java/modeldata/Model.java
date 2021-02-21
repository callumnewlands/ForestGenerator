package modeldata;

import java.util.List;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.Texture;
import rendering.ShaderProgram;

public interface Model {
	void render(ShaderProgram shaderProgram);

	void addTextures(String uniform, List<Texture> textures);

	List<Mesh> getMeshes();
}
