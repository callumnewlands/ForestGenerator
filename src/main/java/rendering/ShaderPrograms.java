package rendering;

import java.io.IOException;
import java.util.function.Consumer;

public final class ShaderPrograms {
	public static ShaderProgram shaderProgram;
	public static ShaderProgram textureShaderProgram;
	public static ShaderProgram normalTextureShaderProgram;
	public static ShaderProgram instancedTextureShaderProgram;
	public static ShaderProgram instancedNormalTextureShaderProgram;
	public static ShaderProgram billboardShaderProgram;

	static {
		try {
			shaderProgram = new ShaderProgram("/shader.vert", "/shader.frag");
			textureShaderProgram = new ShaderProgram("/textureShader.vert", "/textureShader.frag");
			instancedTextureShaderProgram = new ShaderProgram("/instTextureShader.vert", "/textureShader.frag");
			normalTextureShaderProgram = new ShaderProgram("/textureShader.vert", "/normTextureShader.frag");
			instancedNormalTextureShaderProgram = new ShaderProgram("/instTextureShader.vert", "/normTextureShader.frag");
			billboardShaderProgram = new ShaderProgram("/instTextureShader.vert", "/billboardTextureShader.frag");
		} catch (IOException e) {
			throw new RuntimeException("Unable to load 1 or more shader programs");
		}
	}

	public static void forAll(Consumer<ShaderProgram> function) {
		function.accept(shaderProgram);
		function.accept(textureShaderProgram);
		function.accept(instancedTextureShaderProgram);
		function.accept(normalTextureShaderProgram);
		function.accept(instancedNormalTextureShaderProgram);
		function.accept(billboardShaderProgram);
	}

	private ShaderPrograms() {

	}

}
