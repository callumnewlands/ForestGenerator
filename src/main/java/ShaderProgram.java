import org.lwjgl.system.NativeType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
import static org.lwjgl.opengl.GL20C.glLinkProgram;
import static org.lwjgl.opengl.GL20C.glShaderSource;
import static org.lwjgl.opengl.GL20C.glUseProgram;

public class ShaderProgram {

	private static final String RESOURCES_PATH = "src/main/resources";
	private int handle;

	public ShaderProgram(final String vertexShaderPath, final String fragmentShaderPath) throws IOException {
		String vertexShaderSourceCode = new String(Files.readAllBytes(Paths.get(RESOURCES_PATH + vertexShaderPath)));
		String fragmentShaderSourceCode = new String(Files.readAllBytes(Paths.get(RESOURCES_PATH + fragmentShaderPath)));

		int vertexShader = compileShader(vertexShaderSourceCode, GL_VERTEX_SHADER);
		int fragmentShader = compileShader(fragmentShaderSourceCode, GL_FRAGMENT_SHADER);
		this.attachShaders(vertexShader, fragmentShader);
	}
	private int compileShader(final String source, final @NativeType("GLenum") int type) {
		int shaderHandle = glCreateShader(type);
		glShaderSource(shaderHandle, source);
		glCompileShader(shaderHandle);

		int success = glGetShaderi(shaderHandle, GL_COMPILE_STATUS);
		if (success == GL_FALSE) {
			String infoLog = glGetShaderInfoLog(shaderHandle);
			System.out.println("ERROR : SHADER : "
					+ (type == GL_VERTEX_SHADER ? "VERTEX" : "FRAGMENT")
					+ " - COMPILATION_FAILED\n" + infoLog);
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
			System.out.println("ERROR : SHADER PROGRAM - LINKING_FAILED\n" + infoLog);
		}

		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
	}

	public void use() {
		glUseProgram(this.handle);
	}

}
