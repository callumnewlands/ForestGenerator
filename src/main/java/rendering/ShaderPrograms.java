package rendering;

import java.io.IOException;
import java.util.function.Consumer;

public final class ShaderPrograms {
	public static ShaderProgram textureShaderProgram;
	public static ShaderProgram instancedNormalTextureShaderProgram;
	public static ShaderProgram billboardShaderProgram;
	public static ShaderProgram skyboxShaderProgram;
	public static ShaderProgram instancedLeafShaderProgram;
	public static ShaderProgram lightingPassShader;
	public static ShaderProgram ssaoShader;
	public static ShaderProgram ssaoBlurShader;
	public static ShaderProgram sunShader;

	static {
		try {
			textureShaderProgram = new ShaderProgram("/textureShader.vert", "/textureGBuffer.frag");
			instancedNormalTextureShaderProgram = new ShaderProgram("/instTextureShader.vert", "/normTextureGBuffer.frag");
			billboardShaderProgram = new ShaderProgram("/instTextureShader.vert", "/billboardGBuffer.frag");
			skyboxShaderProgram = new ShaderProgram("/skyboxShader.vert", "/skyboxShader.frag");
			instancedLeafShaderProgram = new ShaderProgram("/instTextureShader.vert", "/leafGBuffer.frag");
			lightingPassShader = new ShaderProgram("/gBufferLighting.vert", "/gBufferLighting.frag");
			ssaoShader = new ShaderProgram("/gBufferLighting.vert", "/ssao.frag");
			ssaoBlurShader = new ShaderProgram("/gBufferLighting.vert", "/ssaoBlur.frag");
			sunShader = new ShaderProgram("/sun.vert", "/sun.frag");
		} catch (IOException e) {
			throw new RuntimeException("Unable to load 1 or more shader programs:" + e.getMessage());
		}
	}

	private ShaderPrograms() {

	}

	public static void forAll(Consumer<ShaderProgram> function) {
		function.accept(textureShaderProgram);
		function.accept(instancedNormalTextureShaderProgram);
		function.accept(billboardShaderProgram);
		function.accept(skyboxShaderProgram);
		function.accept(instancedLeafShaderProgram);
		function.accept(lightingPassShader);
		function.accept(ssaoShader);
		function.accept(ssaoBlurShader);
		function.accept(sunShader);
	}

}
