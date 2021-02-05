import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static lsystems.modules.DefinedModules.LB;
import static lsystems.modules.DefinedModules.RB;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
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
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11C.GL_LINE;
import static org.lwjgl.opengl.GL11C.GL_VERSION;
import static org.lwjgl.opengl.GL11C.glGetString;
import static org.lwjgl.opengl.GL11C.glPolygonMode;
import static org.lwjgl.opengl.GL13C.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE1;
import static org.lwjgl.system.MemoryUtil.NULL;

import generation.TerrainQuadtree;
import generation.TurtleInterpreter;
import lsystems.LSystem;
import lsystems.ProductionBuilder;
import lsystems.modules.CharModule;
import lsystems.modules.Module;
import lsystems.modules.ParametricExpressionModule;
import lsystems.modules.ParametricParameterModule;
import lsystems.modules.ParametricValueModule;
import meshdata.Mesh;
import meshdata.Texture;
import meshdata.Vertex;
import meshdata.VertexArray;
import meshdata.VertexAttribute;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import rendering.Camera;
import rendering.ShaderProgram;
import utils.MeshUtils;

public class App {

	public static final float TREE_SCALE = 0.01f; // 0.01f
	private static final int MAJOR_VERSION = 4;
	private static final int MINOR_VERSION = 6;
	private static final int WINDOW_WIDTH = 1200;
	private static final int WINDOW_HEIGHT = 800;
	private static final String VERTEX_SHADER_PATH = "/shader.vert";
	private static final String FRAGMENT_SHADER_PATH = "/shader.frag";
	private final int NUMBER_TREES = 4;
	private long window;
	private ShaderProgram shaderProgram;
	private ShaderProgram textureShaderProgram;
	private List<VertexArray> groundTiles = new ArrayList<>();
	private TerrainQuadtree quadtree;
	private List<VertexArray> trees = new ArrayList<>();
	private List<VertexArray> leaves = new ArrayList<>();
	private Texture leafTexture;
	private Texture barkTexture;
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
		glEnable(GL_MULTISAMPLE);


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
		textureShaderProgram = new ShaderProgram("/textureShader.vert", "/textureShader.frag");
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
		final float farPlane = 300.0f;
		Matrix4f projection = new Matrix4f()
				.perspective(perspectiveAngle, (float) WINDOW_WIDTH / WINDOW_HEIGHT, nearPlane, farPlane);
		shaderProgram.setUniform("projection", projection);
		textureShaderProgram.setUniform("projection", projection);
	}

	private void initScene() {
		glClearColor(.529f, .808f, .922f, 0f);

		float totalWidth = 300f;

		quadtree = new TerrainQuadtree(
				new Vector2f(0, 0),
				totalWidth,
				5,
				100
		);
		quadtree.setSeedPoint(new Vector2f(camera.getPosition().x, camera.getPosition().z));
		groundTiles = quadtree.getGroundTiles().stream().map(Mesh::getVAO).collect(Collectors.toList());

		Vector3f up = new Vector3f(0f, 0f, -1f);
		final int[] indices = {0, 1, 3, 1, 2, 3};
		List<VertexAttribute> attributes = List.of(
				VertexAttribute.POSITION, VertexAttribute.NORMAL, VertexAttribute.TEXTURE);
		Mesh leaf = new Mesh(List.of(
				new Vertex(new Vector3f(0f, 0f, -0.5f), up, new Vector2f(0, 0)),
				new Vertex(new Vector3f(1f, 0f, -0.5f), up, new Vector2f(0, 1)),
				new Vertex(new Vector3f(1f, 0f, 0.5f), up, new Vector2f(1, 1)),
				new Vertex(new Vector3f(0f, 0f, 0.5f), up, new Vector2f(1, 0))
		), indices, attributes);

		leafTexture = new Texture(
				ShaderProgram.RESOURCES_PATH + "/Leaf1_front.tga",
				new Vector3f(0.1f, 0.3f, 0.1f),
				GL_TEXTURE0);

		barkTexture = new Texture(
				ShaderProgram.RESOURCES_PATH + "/bark1.jpg",
				new Vector3f(0.34f, 0.17f, 0.07f),
				GL_TEXTURE1);

//		Create trees
		for (int i = 0; i < NUMBER_TREES; i++) {
			List<Module> instructions = treeSystem().performDerivations(new Random().nextInt(2) + 6);
			int numEdges = 10;
			TurtleInterpreter turtleInterpreter = new TurtleInterpreter(numEdges);
			turtleInterpreter.setSubModels(List.of(MeshUtils.transform(leaf, new Matrix4f().scale(1 / TREE_SCALE))));
			turtleInterpreter.setIgnored(List.of('A'));
			turtleInterpreter.interpretInstructions(instructions
					.stream()
					.map(m -> m.getName() == 'A' ? new ParametricValueModule('~', 0f) : m)
					.collect(Collectors.toList()));
			trees.add(turtleInterpreter.getMesh().getVAO());
			leaves.add(turtleInterpreter.getCombinedSubModelMeshes().get(0).getVAO());
		}
	}


	private LSystem treeSystem() {
		float d1 = 1.6535f; //94.74f;
		float d2 = 2.3148f; //132.63f;
		float a = 0.1053f * (float) Math.PI; //18.95f;
		float lr = 1.109f;
		float vr = 1.732f; //1.832f
		float e = 0.052f; //0.22f

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
						)).withProbability(0.7f).build(),
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
						)).withProbability(0.3f).build(),

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

		Vector3f lightPos = new Vector3f(5f, 100f, -20f);

		// draw trees
		for (int i = 0; i < NUMBER_TREES; i++) {

			Vector2f pos = treePositions.get(i);

			textureShaderProgram.use();
			textureShaderProgram.setUniform("view", camera.getViewMatrix());
			textureShaderProgram.setUniform("model", (new Matrix4f())
					.identity()
					.translate(new Vector3f(pos.x, 0, pos.y))
					.scale(TREE_SCALE));
			textureShaderProgram.setUniform("modelColour", new Vector3f(0.34f, 0.17f, 0.07f));
			textureShaderProgram.setUniform("lightPos", lightPos);
			textureShaderProgram.setUniform("diffuseTexture", 1);
			barkTexture.bind();

			trees.get(i).draw();

			barkTexture.unbind();

			textureShaderProgram.use();
			textureShaderProgram.setUniform("view", camera.getViewMatrix());
			textureShaderProgram.setUniform("model", (new Matrix4f())
					.identity()
					.translate(new Vector3f(pos.x, 0, pos.y))
					.scale(TREE_SCALE));
			textureShaderProgram.setUniform("modelColour", new Vector3f(0.1f, 0.3f, 0.1f));
			textureShaderProgram.setUniform("lightPos", lightPos);
			textureShaderProgram.setUniform("diffuseTexture", 0);
			leafTexture.bind();
			leaves.get(i).draw();
			leafTexture.unbind();
		}

		shaderProgram.use();
		shaderProgram.setUniform("view", camera.getViewMatrix());
		shaderProgram.setUniform("modelColour", new Vector3f(0.1f, 0.3f, 0.1f));
		shaderProgram.setUniform("lightPos", lightPos);
		shaderProgram.setUniform("model", (new Matrix4f())
				.identity());
		for (VertexArray groundTile : groundTiles) {
			groundTile.draw();
		}


	}

	// Not handled with a callback (instead polled each render cycle) to allow for holding down of keys
	private void pollKeys() {
		if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
			camera.move(Camera.MovementDirection.FORWARD, (float) deltaTime);
//			quadtree.setSeedPoint(new Vector2f(camera.getPosition().x, camera.getPosition().z));
//			groundTiles = quadtree.getGroundTiles().stream().map(Mesh::getVAO).collect(Collectors.toList());
		}
		if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
			camera.move(Camera.MovementDirection.LEFT, (float) deltaTime);
//			quadtree.setSeedPoint(new Vector2f(camera.getPosition().x, camera.getPosition().z));
//			groundTiles = quadtree.getGroundTiles().stream().map(Mesh::getVAO).collect(Collectors.toList());
		}
		if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
			camera.move(Camera.MovementDirection.BACKWARD, (float) deltaTime);
//			quadtree.setSeedPoint(new Vector2f(camera.getPosition().x, camera.getPosition().z));
//			groundTiles = quadtree.getGroundTiles().stream().map(Mesh::getVAO).collect(Collectors.toList());
		}
		if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
			camera.move(Camera.MovementDirection.RIGHT, (float) deltaTime);
//			quadtree.setSeedPoint(new Vector2f(camera.getPosition().x, camera.getPosition().z));
//			groundTiles = quadtree.getGroundTiles().stream().map(Mesh::getVAO).collect(Collectors.toList());
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
	}

	private void exit() {
		glfwSetWindowShouldClose(window, true);
	}


}