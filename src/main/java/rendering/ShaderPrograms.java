package rendering;

import java.io.IOException;
import java.util.function.Consumer;

public final class ShaderPrograms {
	public static ShaderProgram textureShader;
	public static ShaderProgram billboardShaderProgram;
	public static ShaderProgram skyboxShaderProgram;
	public static ShaderProgram instancedLeafShaderProgram;
	public static ShaderProgram lightingPassShader;
	public static ShaderProgram ssaoShader;
	public static ShaderProgram ssaoBlurShader;
	public static ShaderProgram sunShader;
	public static ShaderProgram scatteringShader;
	public static ShaderProgram hdrToCubemapShader;
	public static ShaderProgram shadowsShader;

	static {
		try {
			textureShader = new ShaderProgram("/shaders/scene/texture.vert", "/shaders/scene/texture.frag");
			billboardShaderProgram = new ShaderProgram("/shaders/scene/texture.vert", "/shaders/scene/billboard.frag");
			instancedLeafShaderProgram = new ShaderProgram("/shaders/scene/texture.vert", "/shaders/scene/leaf.frag");
			skyboxShaderProgram = new ShaderProgram("/shaders/scene/skybox.vert", "/shaders/scene/skybox.frag");
			sunShader = new ShaderProgram("/shaders/scene/sun.vert", "/shaders/scene/sun.frag");

			shadowsShader = new ShaderProgram("/shaders/processing/shadows.vert", "/shaders/processing/shadows.frag");
			hdrToCubemapShader = new ShaderProgram("/shaders/processing/hdrToCubemapShader.vert", "/shaders/processing/hdrToCubemapShader.frag");
			scatteringShader = new ShaderProgram("/shaders/processing/gBufferLighting.vert", "/shaders/processing/scatter.frag");
			ssaoShader = new ShaderProgram("/shaders/processing/gBufferLighting.vert", "/shaders/processing/ssao.frag");
			ssaoBlurShader = new ShaderProgram("/shaders/processing/gBufferLighting.vert", "/shaders/processing/ssaoBlur.frag");
			lightingPassShader = new ShaderProgram("/shaders/processing/gBufferLighting.vert", "/shaders/processing/gBufferLighting.frag");
		} catch (IOException e) {
			throw new RuntimeException("Unable to load 1 or more shader programs:" + e.getMessage());
		}
	}

	private ShaderPrograms() {

	}

	public static void forAll(Consumer<ShaderProgram> function) {
		function.accept(textureShader);
		function.accept(billboardShaderProgram);
		function.accept(skyboxShaderProgram);
		function.accept(instancedLeafShaderProgram);
		function.accept(lightingPassShader);
		function.accept(ssaoShader);
		function.accept(ssaoBlurShader);
		function.accept(sunShader);
		function.accept(scatteringShader);
		function.accept(hdrToCubemapShader);
		function.accept(shadowsShader);
	}

}
