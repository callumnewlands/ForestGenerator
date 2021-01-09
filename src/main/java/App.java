import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static lsystems.modules.DefinedModules.LB;
import static lsystems.modules.DefinedModules.RB;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
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
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
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
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.GL_VERSION;
import static org.lwjgl.opengl.GL11C.glGetString;
import static org.lwjgl.system.MemoryUtil.NULL;

import lsystems.LSystem;
import lsystems.ProductionBuilder;
import lsystems.modules.CharModule;
import lsystems.modules.Module;
import lsystems.modules.ParametricExpressionModule;
import lsystems.modules.ParametricParameterModule;
import lsystems.modules.ParametricValueModule;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import plantgeneration.TurtleInterpreter;
import rendering.Camera;
import rendering.ShaderProgram;
import rendering.VertexArray;
import rendering.VertexAttribute;

public class App {

	private static final int MAJOR_VERSION = 4;
	private static final int MINOR_VERSION = 6;
	private static final int WINDOW_WIDTH = 1200;
	private static final int WINDOW_HEIGHT = 800;
	private static final String VERTEX_SHADER_PATH = "/shader.vert";
	private static final String FRAGMENT_SHADER_PATH = "/shader.frag";
	private long window;

	private ShaderProgram shaderProgram;
	private VertexArray rectangleVertexArray;
	private final int NUMBER_TREES = 4;
	private List<VertexArray> trees = new ArrayList<>();
	private List<Vector2f> treePositions = List.of(new Vector2f(-3, 18), new Vector2f(5, 3), new Vector2f(-2, -10), new Vector2f(20, -4));
	private Camera camera;

	private double lastFrame = 0.0;
	private double deltaTime = 0.0;
	private double stepper = 0.0;

	private float lastX;
	private float lastY;

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

		long window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "Hello World!", NULL, NULL);
		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// Centre window
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		if (vidmode == null) {
			throw new RuntimeException("Failed to get primary monitor");
		}
		glfwSetWindowPos(
				window,
				(vidmode.width() - WINDOW_WIDTH) / 2,
				(vidmode.height() - WINDOW_HEIGHT) / 2
		);

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		return window;
	}

	private void initShaders() throws IOException {
		shaderProgram = new ShaderProgram(VERTEX_SHADER_PATH, FRAGMENT_SHADER_PATH);

		final Vector3f cameraPosition = new Vector3f(0.62f, 4.30f, 26.8f);
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
		final float farPlane = 100.0f;
		Matrix4f projection = new Matrix4f()
				.perspective(perspectiveAngle, (float) WINDOW_WIDTH / WINDOW_HEIGHT, nearPlane, farPlane);
		shaderProgram.setUniform("projection", projection);
	}

	private void initScene() {
		glClearColor(.529f, .808f, .922f, 0f);

		float[] vertices = {
				0.5f, 0.5f, 0.0f, 0f, 0f, -1f,
				0.5f, -0.5f, 0.0f, 0f, 0f, -1f,
				-0.5f, -0.5f, 0.0f, 0f, 0f, -1f,
				-0.5f, 0.5f, 0.0f, 0f, 0f, -1f
		};
		List<VertexAttribute> attributes = List.of(VertexAttribute.POSITION, VertexAttribute.NORMAL);
		final int[] indices = {0, 1, 3, 1, 2, 3};
		rectangleVertexArray = new VertexArray(vertices, 4, indices, attributes);

//		Create a tree
		for (int i = 0; i < NUMBER_TREES; i++) {
			List<Module> instructions = treeSystem().performDerivations(7);
			int numEdges = 10;
			TurtleInterpreter turtleInterpreter = new TurtleInterpreter(numEdges);
			turtleInterpreter.setIgnored(List.of('A'));
			turtleInterpreter.interpretInstructions(instructions);
			trees.add(turtleInterpreter.getVAO());
		}
	}

	private LSystem treeSystem() {
		float d1 = 1.6535f;//94.74f;
		float d2 = 2.3148f; //132.63f;
		float a = 0.1053f * (float) Math.PI;//18.95f;
		float lr = 1.109f;
		float vr = 1.732f;
		float e = 0.052f;//0.22f

		CharModule A = new CharModule('A');
		ParametricParameterModule ExIn = new ParametricParameterModule('!', List.of("w"));
		Module ExOut = new ParametricExpressionModule('!', List.of("w"), vars -> List.of(vars.get("w") * vr));
		ParametricParameterModule FIn = new ParametricParameterModule('F', List.of("l"));
		Module FOut = new ParametricExpressionModule('F', List.of("l"), vars -> List.of(vars.get("l") * lr));

		return new LSystem(
				List.of(
						new ParametricValueModule('T', List.of(0f, -1f, 0f, e)),
						new ParametricValueModule('!', 1f),
						new ParametricValueModule('F', 200f),
//						new ParametricValueModule('/', (float) Math.PI / 4),
						A
				),
				List.of(),
				List.of(
						new ProductionBuilder(List.of(A), List.of(
								new ParametricValueModule('!', vr),
								new ParametricValueModule('F', 50f),
								LB,
								new ParametricValueModule('&', a),
								new ParametricValueModule('F', 50f),
								A,
								RB,
								new ParametricValueModule('/', d1),
								LB,
								new ParametricValueModule('&', a),
								new ParametricValueModule('F', 50f),
								A,
								RB,
								new ParametricValueModule('/', d2),
								new ParametricValueModule('&', a),
								new ParametricValueModule('F', 50f),
								A
						)).withProbability(0.7f).build(), //0.7
						new ProductionBuilder(List.of(A), List.of(
								new ParametricValueModule('!', vr),
								new ParametricValueModule('F', 50f),
								LB,
								new ParametricValueModule('&', a),
								new ParametricValueModule('F', 50f),
								A,
								RB,
								new ParametricValueModule('/', (float) Math.PI),
								new ParametricValueModule('&', a),
								new ParametricValueModule('F', 50f),
								A
						)).withProbability(0.3f).build(), // 0.3

						new ProductionBuilder(List.of(FIn), List.of(FOut)).build(),
						new ProductionBuilder(List.of(ExIn), List.of(ExOut)).build()
				));
	}

	private LSystem cubeSystem() {

		return new LSystem(
				List.of(new ParametricValueModule('F', 1f)),
				List.of(),
				List.of(new ProductionBuilder(
						List.of(new ParametricParameterModule('F', List.of("w"))),
						List.of(new ParametricExpressionModule('F', List.of("w"), vars -> List.of(vars.get("w"))),
								new ParametricExpressionModule('F', List.of("w"), vars -> List.of(vars.get("w")))))
						.build()
				));
	}

	private void loop() {
		while (!glfwWindowShouldClose(window)) {
			updateDeltaTime();
			stepper += deltaTime / 10;
			stepper -= (int) stepper;
			renderScene();
			glfwSwapBuffers(window);
			pollKeys();
			glfwPollEvents();
		}
	}

	private void updateDeltaTime() {
		// On first frame
		if (lastFrame == 0.0) {
			lastFrame = glfwGetTime();
		}
		this.deltaTime = glfwGetTime() - lastFrame;
		lastFrame = glfwGetTime();
	}

	private void renderScene() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		shaderProgram.use();
		shaderProgram.setUniform("view", camera.getViewMatrix());

		// draw trees
		for (int i = 0; i < NUMBER_TREES; i++) {
			Vector2f pos = treePositions.get(i);
			shaderProgram.setUniform("model", (new Matrix4f())
					.identity()
					.translate(new Vector3f(pos.x, 0, pos.y))
					.scale(0.01f));
//					.rotate((float) (Math.PI * 2 * stepper), new Vector3f(0f, 1f, 0f)));
			trees.get(i).draw();
		}

		// draw ground
		shaderProgram.setUniform("model", (new Matrix4f())
				.identity()
				.scale(100f)
				.rotate((float) (Math.PI / 2), new Vector3f(1f, 0f, 0f)));
		rectangleVertexArray.draw();
	}

	// Not handled with a callback (instead polled each render cycle) to allow for holding down of keys
	private void pollKeys() {
		if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
			camera.move(Camera.MovementDirection.FORWARD, (float) deltaTime);
		}
		if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
			camera.move(Camera.MovementDirection.LEFT, (float) deltaTime);
		}
		if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
			camera.move(Camera.MovementDirection.BACKWARD, (float) deltaTime);
		}
		if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
			camera.move(Camera.MovementDirection.RIGHT, (float) deltaTime);
		}
		if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
			camera.move(Camera.MovementDirection.UP, (float) deltaTime);
		}
		if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
			camera.move(Camera.MovementDirection.DOWN, (float) deltaTime);
		}
	}

	private void exit() {
		glfwSetWindowShouldClose(window, true);
	}


}