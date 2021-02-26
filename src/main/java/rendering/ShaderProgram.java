package rendering;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.lwjgl.opengl.GL11C.GL_FALSE;
import static org.lwjgl.opengl.GL20C.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20C.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20C.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20C.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20C.glAttachShader;
import static org.lwjgl.opengl.GL20C.glCompileShader;
import static org.lwjgl.opengl.GL20C.glCreateProgram;
import static org.lwjgl.opengl.GL20C.glCreateShader;
import static org.lwjgl.opengl.GL20C.glDeleteShader;
import static org.lwjgl.opengl.GL20C.glGetProgrami;
import static org.lwjgl.opengl.GL20C.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20C.glGetShaderi;
import static org.lwjgl.opengl.GL20C.glGetUniformLocation;
import static org.lwjgl.opengl.GL20C.glLinkProgram;
import static org.lwjgl.opengl.GL20C.glShaderSource;
import static org.lwjgl.opengl.GL20C.glUniform1f;
import static org.lwjgl.opengl.GL20C.glUniform1i;
import static org.lwjgl.opengl.GL20C.glUniform2f;
import static org.lwjgl.opengl.GL20C.glUniform3f;
import static org.lwjgl.opengl.GL20C.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20C.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20C.glUseProgram;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.NativeType;

public class ShaderProgram {

	public static final String RESOURCES_PATH = "src/main/resources";
	private int handle;

	public ShaderProgram(final String vertexShaderPath, final String fragmentShaderPath) throws IOException {
		String vertexShaderSourceCode = new String(Files.readAllBytes(Paths.get(RESOURCES_PATH + vertexShaderPath)));
		String fragmentShaderSourceCode = new String(Files.readAllBytes(Paths.get(RESOURCES_PATH + fragmentShaderPath)));

		int vertexShader = compileShader(vertexShaderSourceCode, vertexShaderPath, GL_VERTEX_SHADER);
		int fragmentShader = compileShader(fragmentShaderSourceCode, fragmentShaderPath, GL_FRAGMENT_SHADER);
		this.attachShaders(vertexShader, fragmentShader);
	}

	private int compileShader(final String source, final String path, final @NativeType("GLenum") int type) {
		int shaderHandle = glCreateShader(type);
		glShaderSource(shaderHandle, source);
		glCompileShader(shaderHandle);

		int success = glGetShaderi(shaderHandle, GL_COMPILE_STATUS);
		if (success == GL_FALSE) {
			String infoLog = glGetShaderInfoLog(shaderHandle);
			throw new RuntimeException("ERROR : SHADER : "
					+ (type == GL_VERTEX_SHADER ? "VERTEX" : "FRAGMENT")
					+ " - COMPILATION FAILED in file:" + path + "\n" + infoLog);
		}
		return shaderHandle;
	}

	private void attachShaders(final int vertexShader, final int fragmentShader) {
		handle = glCreateProgram();
		glAttachShader(handle, vertexShader);
		glAttachShader(handle, fragmentShader);
		glLinkProgram(handle);

		int success = glGetProgrami(handle, GL_LINK_STATUS);
		if (success == GL_FALSE) {
			String infoLog = glGetShaderInfoLog(fragmentShader);
			System.out.println("ERROR : SHADER PROGRAM - LINKING FAILED\n" + infoLog);
		}

		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
	}

	public void use() {
		glUseProgram(this.handle);
	}

	public void setUniform(final String name, final Vector3f value) {
		this.use();
		int location = glGetUniformLocation(this.handle, name);
		glUniform3f(location, value.x, value.y, value.z);
	}

	public void setUniform(String name, Vector2f value) {
		this.use();
		int location = glGetUniformLocation(this.handle, name);
		glUniform2f(location, value.x, value.y);
	}

	public void setUniform(final String name, final Matrix4f value) {
		this.use();
		final int matrixOrder = 4;
		int location = glGetUniformLocation(this.handle, name);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(matrixOrder * matrixOrder);
		value.get(buffer);
		glUniformMatrix4fv(location, false, buffer);
	}

	public void setUniform(String name, Matrix3f value) {
		this.use();
		final int matrixOrder = 3;
		int location = glGetUniformLocation(this.handle, name);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(matrixOrder * matrixOrder);
		value.get(buffer);
		glUniformMatrix3fv(location, false, buffer);
	}

	public void setUniform(String name, int value) {
		this.use();
		int location = glGetUniformLocation(this.handle, name);
		glUniform1i(location, value);
	}

	public void setUniform(String name, boolean value) {
		this.use();
		int location = glGetUniformLocation(this.handle, name);
		glUniform1i(location, value ? 1 : 0);
	}

	public void setUniform(String name, float value) {
		this.use();
		int location = glGetUniformLocation(this.handle, name);
		glUniform1f(location, value);
	}

	public void setUniform(String name, List<Vector3f> values) {
		for (int i = 0; i < values.size(); i++) {
			setUniform(String.format("%s[%d]", name, i), values.get(i));
		}
	}
}
