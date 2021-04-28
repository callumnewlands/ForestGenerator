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

package sceneobjects;

import static org.lwjgl.opengl.GL11C.GL_LEQUAL;
import static org.lwjgl.opengl.GL11C.GL_LESS;
import static org.lwjgl.opengl.GL11C.glDepthFunc;
import static rendering.ShaderPrograms.skyboxShaderProgram;

public class Skybox extends Cube {

	public Skybox() {
		super();
//		addTexture("skyboxTexture", Textures.skybox3);
		setShaderProgram(skyboxShaderProgram);
	}

	@Override
	public void render() {
		glDepthFunc(GL_LEQUAL);
		model.render();
		glDepthFunc(GL_LESS);
	}
}
