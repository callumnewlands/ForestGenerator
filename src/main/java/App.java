import java.io.IOException;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwMaximizeWindow;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.GL_BLEND;
import static org.lwjgl.opengl.GL11C.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11C.GL_LINE;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_VERSION;
import static org.lwjgl.opengl.GL11C.glBlendFunc;
import static org.lwjgl.opengl.GL11C.glGetString;
import static org.lwjgl.opengl.GL11C.glPolygonMode;
import static org.lwjgl.opengl.GL13C.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;
import static org.lwjgl.system.MemoryUtil.NULL;

import generation.TerrainQuadtree;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import rendering.Camera;
import rendering.ShaderProgram;
import sceneobjects.Textures;

public class App {

	public static final float GROUND_WIDTH = TerrainQuadtree.GROUND_WIDTH;
	private static final int MAJOR_VERSION = 4;
	private static final int MINOR_VERSION = 6;
	private static final String VERTEX_SHADER_PATH = "/shader.vert";
	private static final String FRAGMENT_SHADER_PATH = "/shader.frag";

	private float sunStrength = 0.9f;
	private int WINDOW_WIDTH;
	private int WINDOW_HEIGHT;

	private long window;
	private Camera camera;
	private Boolean useNormalMapping = true;

	private ShaderProgram shaderProgram;
	private ShaderProgram textureShaderProgram;
	private ShaderProgram normalTextureShaderProgram;
	private ShaderProgram instancedTextureShaderProgram;
	private ShaderProgram instancedNormalTextureShaderProgram;
	private ShaderProgram billboardShaderProgram;

	private TerrainQuadtree quadtree;

	private double lastFrame = 0.0;
	private double deltaTime = 0.0;
	private double stepper = 0.0;

	private float lastX;
	private float lastY;
	private Matrix4f projection;

	public static void main(String[] args) throws IOException {
		new App().run();
	}

	public void run() throws IOException {
		System.out.println("Running LWJGL " + Version.getVersion());

		init();
		glfwShowWindow(window);
		loop();

		// Destroy window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		glfwTerminate();
		Objects.requireNonNull(glfwSetErrorCallback(null)).free();
	}

	private void init() throws IOException {
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		window = initWindow();
		GL.createCapabilities();
		System.out.println("Running OpenGL " + glGetString(GL_VERSION));

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_MULTISAMPLE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		initShaders();
		initScene();

	}

	private long initWindow() {
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, MAJOR_VERSION);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, MINOR_VERSION);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		glfwWindowHint(GLFW_SAMPLES, 4);

		GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		if (videoMode == null) {
			throw new RuntimeException("Failed to get primary monitor");
		}
		WINDOW_WIDTH = videoMode.width();
		WINDOW_HEIGHT = videoMode.height();
		long window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "Forest Simulator 2021", NULL, NULL);
		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		glfwMaximizeWindow(window);

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		return window;
	}

	private void initShaders() throws IOException {
		shaderProgram = new ShaderProgram(VERTEX_SHADER_PATH, FRAGMENT_SHADER_PATH);
		textureShaderProgram = new ShaderProgram("/textureShader.vert", "/textureShader.frag");
		instancedTextureShaderProgram = new ShaderProgram("/instTextureShader.vert", "/textureShader.frag");
		normalTextureShaderProgram = new ShaderProgram("/textureShader.vert", "/normTextureShader.frag");
		instancedNormalTextureShaderProgram = new ShaderProgram("/instTextureShader.vert", "/normTextureShader.frag");
		billboardShaderProgram = new ShaderProgram("/instTextureShader.vert", "/billboardTextureShader.frag");
		final Vector3f cameraPosition = new Vector3f(0, 3.30f, 0);
		final float cameraYaw = -90.0f;
		final float cameraPitch = 0.0f;
		camera = new Camera(cameraPosition, cameraYaw, cameraPitch);

		glfwSetKeyCallback(window, (windowHandle, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
				exit();
			}
		});

		glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
			float xoffset = (float) xpos - lastX;
			float yoffset = lastY - (float) ypos;
			lastX = (float) xpos;
			lastY = (float) ypos;
			camera.processMouseMovement(xoffset, yoffset);
		});
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		glfwSetCursorPos(window, lastX, lastY);

		final float perspectiveAngle = (float) Math.toRadians(45.0f);
		final float nearPlane = 0.1f;
		final float farPlane = 300.0f;
		projection = new Matrix4f()
				.perspective(perspectiveAngle, (float) WINDOW_WIDTH / WINDOW_HEIGHT, nearPlane, farPlane);
		shaderProgram.setUniform("projection", projection);
		textureShaderProgram.setUniform("projection", projection);
		instancedTextureShaderProgram.setUniform("projection", projection);
		normalTextureShaderProgram.setUniform("projection", projection);
		instancedNormalTextureShaderProgram.setUniform("projection", projection);
		billboardShaderProgram.setUniform("projection", projection);

		initLighting();
	}

	private void initScene() {
		glClearColor(.529f, .808f, .922f, 0f);

		int QUADTREE_DEPTH = 4;
		quadtree = new TerrainQuadtree(
				new Vector2f(0, 0),
				GROUND_WIDTH,
				QUADTREE_DEPTH,
				(int) (GROUND_WIDTH / 2 * Math.pow(2, Math.max((5 - QUADTREE_DEPTH), 0))),
				Textures.ground
		);
		quadtree.setSeedPoint(new Vector2f(camera.getPosition().x, camera.getPosition().z));

	}

	private void initLighting() {
		Vector3f lightPos = new Vector3f(5f, 100f, -20f);
		Vector3f lightCol = new Vector3f(sunStrength);

		normalTextureShaderProgram.setUniform("lightPos", lightPos);
		normalTextureShaderProgram.setUniform("lightColour", lightCol);
		textureShaderProgram.setUniform("lightPos", lightPos);
		textureShaderProgram.setUniform("lightColour", lightCol);
		instancedTextureShaderProgram.setUniform("lightPos", lightPos);
		instancedTextureShaderProgram.setUniform("lightColour", lightCol);
		instancedNormalTextureShaderProgram.setUniform("lightPos", lightPos);
		instancedNormalTextureShaderProgram.setUniform("lightColour", lightCol);
		billboardShaderProgram.setUniform("lightPos", lightPos);
		billboardShaderProgram.setUniform("lightColour", lightCol);
	}

	private void loop() {
		while (!glfwWindowShouldClose(window)) {
			updateDeltaTime();
			stepper += deltaTime / 10;
			stepper -= (int) stepper;
//			System.out.println(1 / deltaTime + " fps");
			renderScene();
			glfwSwapBuffers(window);
			pollKeys();
			glfwPollEvents();
		}
	}

	private void renderScene() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		ShaderProgram textureProgram = useNormalMapping ? normalTextureShaderProgram : textureShaderProgram;
		ShaderProgram instanceProgram = useNormalMapping ? instancedNormalTextureShaderProgram : instancedTextureShaderProgram;

		textureProgram.setUniform("view", camera.getViewMatrix());
		instanceProgram.setUniform("view", camera.getViewMatrix());
		textureShaderProgram.setUniform("view", camera.getViewMatrix());
		billboardShaderProgram.setUniform("view", camera.getViewMatrix());
		billboardShaderProgram.setUniform("viewPos", camera.getPosition());

		quadtree.render(textureShaderProgram, instanceProgram, billboardShaderProgram, camera.getViewMatrix().mulLocal(projection));

	}

	private void updateDeltaTime() {
		// On first frame
		if (lastFrame == 0.0) {
			lastFrame = glfwGetTime();
		}
		this.deltaTime = glfwGetTime() - lastFrame;
		lastFrame = glfwGetTime();
	}

	// Not handled with a callback (instead polled each render cycle) to allow for holding down of keys
	private void pollKeys() {
		if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
			camera.move(Camera.MovementDirection.FORWARD, (float) deltaTime);
			quadtree.setSeedPoint(camera.getPosition().x, camera.getPosition().z);
		}
		if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
			camera.move(Camera.MovementDirection.LEFT, (float) deltaTime);
			quadtree.setSeedPoint(camera.getPosition().x, camera.getPosition().z);
		}
		if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
			camera.move(Camera.MovementDirection.BACKWARD, (float) deltaTime);
			quadtree.setSeedPoint(camera.getPosition().x, camera.getPosition().z);
		}
		if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
			camera.move(Camera.MovementDirection.RIGHT, (float) deltaTime);
			quadtree.setSeedPoint(camera.getPosition().x, camera.getPosition().z);
		}
		if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
			camera.move(Camera.MovementDirection.UP, (float) deltaTime);
		}
		if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
			camera.move(Camera.MovementDirection.DOWN, (float) deltaTime);
		}
		if (glfwGetKey(window, GLFW_KEY_1) == GLFW_PRESS) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		}
		if (glfwGetKey(window, GLFW_KEY_1) == GLFW_RELEASE) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		}
		if (glfwGetKey(window, GLFW_KEY_2) == GLFW_PRESS) {
			useNormalMapping = false;
		}
		if (glfwGetKey(window, GLFW_KEY_2) == GLFW_RELEASE) {
			useNormalMapping = true;
		}
		if (glfwGetKey(window, GLFW_KEY_3) == GLFW_PRESS) {
			// TODO investigate gamma correction and washed out
			glEnable(GL_FRAMEBUFFER_SRGB);
		}
		if (glfwGetKey(window, GLFW_KEY_3) == GLFW_RELEASE) {
			glDisable(GL_FRAMEBUFFER_SRGB);
		}
	}

	private void exit() {
		glfwSetWindowShouldClose(window, true);
	}

}