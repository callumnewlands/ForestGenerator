/*
 * Copyright (c) 2021 Callum Newlands
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     The additional term of 7.b applies: Requiring preservation of specified
 *     reasonable legal notices or author attributions in that material or in the
 *     Appropriate Legal Notices displayed by works containing it
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package rendering;

import java.io.IOException;
import java.util.function.Consumer;

public final class ShaderPrograms {
	public static ShaderProgram textureShader;
	public static ShaderProgram billboardShaderProgram;
	public static ShaderProgram skyboxShaderProgram;
	public static ShaderProgram leafShaderProgram;
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
			leafShaderProgram = new ShaderProgram("/shaders/scene/texture.vert", "/shaders/scene/leaf.frag");
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
		function.accept(leafShaderProgram);
		function.accept(lightingPassShader);
		function.accept(ssaoShader);
		function.accept(ssaoBlurShader);
		function.accept(sunShader);
		function.accept(scatteringShader);
		function.accept(hdrToCubemapShader);
		function.accept(shadowsShader);
	}

}
