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
	public static ShaderProgram scatteringShader;
	public static ShaderProgram hdrToCubemapShader;

	static {
		try {
			textureShaderProgram = new ShaderProgram("/shaders/textureShader.vert", "/shaders/textureGBuffer.frag");
			instancedNormalTextureShaderProgram = new ShaderProgram("/shaders/instTextureShader.vert", "/shaders/normTextureGBuffer.frag");
			billboardShaderProgram = new ShaderProgram("/shaders/instTextureShader.vert", "/shaders/billboardGBuffer.frag");
			skyboxShaderProgram = new ShaderProgram("/shaders/skyboxShader.vert", "/shaders/skyboxGBuffer.frag");
			instancedLeafShaderProgram = new ShaderProgram("/shaders/instTextureShader.vert", "/shaders/leafGBuffer.frag");
			lightingPassShader = new ShaderProgram("/shaders/gBufferLighting.vert", "/shaders/gBufferLighting.frag");
			ssaoShader = new ShaderProgram("/shaders/gBufferLighting.vert", "/shaders/ssao.frag");
			ssaoBlurShader = new ShaderProgram("/shaders/gBufferLighting.vert", "/shaders/ssaoBlur.frag");
			sunShader = new ShaderProgram("/shaders/sun.vert", "/shaders/sun.frag");
			scatteringShader = new ShaderProgram("/shaders/gBufferLighting.vert", "/shaders/scatter.frag");
			hdrToCubemapShader = new ShaderProgram("/shaders/hdrToCubemapShader.vert", "/shaders/hdrToCubemapShader.frag");
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
		function.accept(scatteringShader);
		function.accept(hdrToCubemapShader);
	}

}
