package modeldata.meshdata;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glTexImage2D;
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.GL_RGB16F;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;
import static org.lwjgl.system.MemoryUtil.NULL;
import static rendering.ShaderPrograms.hdrToCubemapShader;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import sceneobjects.Cube;

public class HDRTexture extends CubemapTexture {

	public HDRTexture(String path, int resolution, Vector3f colour, int textureUnit) {
		super(colour, textureUnit);

		int frameBuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
		int renderBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, resolution, resolution);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderBuffer);
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("HDR framebuffer not complete");
		}
//		glBindFramebuffer(GL_FRAMEBUFFER, 0);

		Texture hdr2DImage = new Texture2D(path, colour, 0, GL_RGB16F, GL_CLAMP_TO_EDGE, true);

		this.bind();
		for (int i = 0; i < 6; i++) {
			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, resolution, resolution,
					0, GL_RGB, GL_FLOAT, NULL);
		}
		setHints(GL_CLAMP_TO_EDGE);

		Vector3f origin = new Vector3f(0);
		List<Matrix4f> captureViews = List.of(
				new Matrix4f().lookAt(origin, new Vector3f(1, 0, 0), new Vector3f(0, -1, 0)),
				new Matrix4f().lookAt(origin, new Vector3f(-1, 0, 0), new Vector3f(0, -1, 0)),
				new Matrix4f().lookAt(origin, new Vector3f(0, 1, 0), new Vector3f(0, 0, 1)),
				new Matrix4f().lookAt(origin, new Vector3f(0, -1, 0), new Vector3f(0, 0, -1)),
				new Matrix4f().lookAt(origin, new Vector3f(0, 0, 1), new Vector3f(0, -1, 0)),
				new Matrix4f().lookAt(origin, new Vector3f(0, 0, -1), new Vector3f(0, -1, 0))
		);

		Cube cube = new Cube();
		cube.addTexture("hdr", hdr2DImage);
		cube.setShaderProgram(hdrToCubemapShader);
		hdrToCubemapShader.setUniform("projection",
				new Matrix4f().perspective((float) Math.toRadians(90f), 1, 0.1f, 1.1f)
		);
		hdr2DImage.bind();

//		glActiveTexture(GL_TEXTURE8);
		glViewport(0, 0, resolution, resolution);
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
		for (int i = 0; i < 6; i++) {
			hdrToCubemapShader.setUniform("view", captureViews.get(i));
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, this.handle, 0);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			cube.render();
		}
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

}
