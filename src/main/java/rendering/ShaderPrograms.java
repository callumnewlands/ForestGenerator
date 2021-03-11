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
			textureShaderProgram = new ShaderProgram("/shaders/scene/texture.vert", "/shaders/scene/texture.frag");
			instancedNormalTextureShaderProgram = new ShaderProgram("/shaders/scene/instanceTexture.vert", "/shaders/scene/normalMapped.frag");
			billboardShaderProgram = new ShaderProgram("/shaders/scene/instanceTexture.vert", "/shaders/scene/billboard.frag");
			skyboxShaderProgram = new ShaderProgram("/shaders/scene/skybox.vert", "/shaders/scene/skybox.frag");
			instancedLeafShaderProgram = new ShaderProgram("/shaders/scene/instanceTexture.vert", "/shaders/scene/leaf.frag");
			lightingPassShader = new ShaderProgram("/shaders/processing/gBufferLighting.vert", "/shaders/processing/gBufferLighting.frag");
			ssaoShader = new ShaderProgram("/shaders/processing/gBufferLighting.vert", "/shaders/processing/ssao.frag");
			ssaoBlurShader = new ShaderProgram("/shaders/processing/gBufferLighting.vert", "/shaders/processing/ssaoBlur.frag");
			sunShader = new ShaderProgram("/shaders/scene/sun.vert", "/shaders/scene/sun.frag");
			scatteringShader = new ShaderProgram("/shaders/processing/gBufferLighting.vert", "/shaders/processing/scatter.frag");
			hdrToCubemapShader = new ShaderProgram("/shaders/processing/hdrToCubemapShader.vert", "/shaders/processing/hdrToCubemapShader.frag");
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
