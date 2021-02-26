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
	public static ShaderProgram skyboxShaderProgram;
	public static ShaderProgram instancedLeafShaderProgram;
	public static ShaderProgram leafShaderProgram;
	public static ShaderProgram hdrShaderProgram;

	// TODO separate ShaderPrograms for differen meshes (e.g. billboard trunks and leaves) - maybe do sim. to textures

	static {
		try {
			shaderProgram = new ShaderProgram("/shader.vert", "/shader.frag");
			textureShaderProgram = new ShaderProgram("/textureShader.vert", "/textureShader.frag");
			instancedTextureShaderProgram = new ShaderProgram("/instTextureShader.vert", "/textureShader.frag");
			normalTextureShaderProgram = new ShaderProgram("/textureShader.vert", "/normTextureShader.frag");
			instancedNormalTextureShaderProgram = new ShaderProgram("/instTextureShader.vert", "/normTextureShader.frag");
			billboardShaderProgram = new ShaderProgram("/instTextureShader.vert", "/billboardTextureShader.frag");
			skyboxShaderProgram = new ShaderProgram("/skyboxShader.vert", "/skyboxShader.frag");
			instancedLeafShaderProgram = new ShaderProgram("/instTextureShader.vert", "/leafShader.frag");
			leafShaderProgram = new ShaderProgram("/textureShader.vert", "/leafShader.frag");
			hdrShaderProgram = new ShaderProgram("/hdr.vert", "/hdr.frag");
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
		function.accept(skyboxShaderProgram);
		function.accept(instancedLeafShaderProgram);
		function.accept(leafShaderProgram);
		function.accept(hdrShaderProgram);
	}

	private ShaderPrograms() {

	}

}
