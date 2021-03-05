package sceneobjects;

import static org.lwjgl.opengl.GL11C.GL_LEQUAL;
import static org.lwjgl.opengl.GL11C.GL_LESS;
import static org.lwjgl.opengl.GL11C.glDepthFunc;
import static rendering.ShaderPrograms.skyboxShaderProgram;

import rendering.Textures;

public class Skybox extends Cube {

	public Skybox() {
		super();
		addTexture("skyboxTexture", Textures.skybox3);
		setShaderProgram(skyboxShaderProgram);
	}

	@Override
	public void render() {
		glDepthFunc(GL_LEQUAL);
		model.render();
		glDepthFunc(GL_LESS);
	}
}
