package modeldata;

import java.util.List;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.Texture;
import org.joml.Matrix4f;
import rendering.ShaderProgram;

public interface Model {
	void render();

	void addTextures(String uniform, List<? extends Texture> textures);

	void setModelMatrix(Matrix4f model);

	void setShaderProgram(ShaderProgram shaderProgram);

	List<Mesh> getMeshes();

	void setIsInstanced(boolean value);
}
