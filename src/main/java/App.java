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
import generation.TurtleInterpreter;
import lsystems.LSystem;
import lsystems.ProductionBuilder;
import lsystems.modules.CharModule;
import lsystems.modules.Module;
import lsystems.modules.ParametricExpressionModule;
import lsystems.modules.ParametricParameterModule;
import lsystems.modules.ParametricValueModule;
import models.InstancedMesh;
import models.InstancedModel;
import models.Mesh;
import models.Model;
import models.meshdata.Texture;
import models.meshdata.Vertex;
import models.meshdata.VertexAttribute;
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

	public static final float TREE_SCALE = 0.01f;
	public static final float LEAF_SCALE = 1;
	public static final float TWIG_SCALE = 0.06f;
	public static final float GRASS_SCALE = 1f;
	private static final int MAJOR_VERSION = 4;
	private static final int MINOR_VERSION = 6;
	private static final int WINDOW_WIDTH = 1920;
	private static final int WINDOW_HEIGHT = 1080;
	private static final float GROUND_WIDTH = 100f;
	private static final String VERTEX_SHADER_PATH = "/shader.vert";
	private static final String FRAGMENT_SHADER_PATH = "/shader.frag";
	private static final int NUM_OF_INSTANCED_TREES = 200;
	private static final int NUM_OF_TWIG_TYPES = 10;
	private static final int NUM_OF_INSTANCED_TWIGS = 400;
	private static final int NUM_OF_INSTANCED_GRASS = 10000;
	private static final int NUM_OF_INSTANCED_LEAVES = 20000;
	private static final int NUMBER_TREES = 4;
	private static final List<Vector2f> treePositions = List.of(new Vector2f(-3, 18), new Vector2f(5, 3), new Vector2f(-2, -10), new Vector2f(20, -4));

	//	private final List<Mesh> leaves = new ArrayList<>();
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
	private List<Model> trees = new ArrayList<>();
	private List<Mesh> groundTiles = new ArrayList<>();
	private InstancedModel instancedTree;
	private InstancedMesh instancedLeaf;
	private InstancedModel grassBillboard;
	private List<InstancedMesh> instancedTwigs = new ArrayList<>();

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
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


		initShaders();
		initScene();
		initLighting();
	}

	private long initWindow() {
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, MAJOR_VERSION);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, MINOR_VERSION);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		glfwWindowHint(GLFW_SAMPLES, 4);

		long window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "Forest Simulator 2021", NULL, NULL);
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
		Matrix4f projection = new Matrix4f()
				.perspective(perspectiveAngle, (float) WINDOW_WIDTH / WINDOW_HEIGHT, nearPlane, farPlane);
		shaderProgram.setUniform("projection", projection);
		textureShaderProgram.setUniform("projection", projection);
		instancedTextureShaderProgram.setUniform("projection", projection);
		normalTextureShaderProgram.setUniform("projection", projection);
		instancedNormalTextureShaderProgram.setUniform("projection", projection);
		billboardShaderProgram.setUniform("projection", projection);
	}

	private void initScene() {
		glClearColor(.529f, .808f, .922f, 0f);

		quadtree = new TerrainQuadtree(
				new Vector2f(0, 0),
				GROUND_WIDTH,
				7,
				100
		);
		quadtree.setSeedPoint(new Vector2f(camera.getPosition().x, camera.getPosition().z));
		groundTiles = quadtree.getGroundTiles();

		Texture floorTexture = new Texture(
				ShaderProgram.RESOURCES_PATH + "/textures/floor2.png",
				new Vector3f(0.34f, 0.17f, 0.07f),
				2);
//		Texture normalFloorTexture = new Texture(
//				ShaderProgram.RESOURCES_PATH + "/textures/Ground_Forest_003_normal.jpg",
//				new Vector3f(0.34f, 0.17f, 0.07f),
//				5);

		for (Mesh tile : groundTiles) {
			tile.addTexture("diffuseTexture", floorTexture);
//			tile.addTexture("normalTexture", normalFloorTexture);
		}

		Vector3f up = new Vector3f(0f, 1f, 0f);
		Vector3f out = new Vector3f(0f, 0f, 1f);
		final int[] indices = {0, 1, 3, 1, 2, 3};
		Mesh leaf = new Mesh(List.of(
				new Vertex(new Vector3f(0f, 0f, -0.5f), up, out, new Vector2f(0, 0)),
				new Vertex(new Vector3f(1f, 0f, -0.5f), up, out, new Vector2f(0, 1)),
				new Vertex(new Vector3f(1f, 0f, 0.5f), up, out, new Vector2f(1, 1)),
				new Vertex(new Vector3f(0f, 0f, 0.5f), up, out, new Vector2f(1, 0))
		), indices, List.of(
				VertexAttribute.POSITION,
				VertexAttribute.NORMAL,
				VertexAttribute.TANGENT,
				VertexAttribute.TEXTURE)
		);

		Texture leafTexture = new Texture(
				ShaderProgram.RESOURCES_PATH + "/textures/Leaf1_front.tga",
				new Vector3f(0.1f, 0.3f, 0.1f),
				0);

		Texture normalLeafTexture = new Texture(
				ShaderProgram.RESOURCES_PATH + "/textures/Leaf1_normals_front.tga",
				new Vector3f(0.1f, 0.3f, 0.1f),
				4);

		Texture barkTexture = new Texture(
				ShaderProgram.RESOURCES_PATH + "/textures/Bark_Pine_baseColor.jpg",
				new Vector3f(0.34f, 0.17f, 0.07f),
				1);

		Texture normalBarkTexture = new Texture(
				ShaderProgram.RESOURCES_PATH + "/textures/Bark_Pine_normal.jpg",
				new Vector3f(0.34f, 0.17f, 0.07f),
				3);

		Texture grassTexture = new Texture(
				ShaderProgram.RESOURCES_PATH + "/textures/grass.png",
				new Vector3f(0.1f, 0.3f, 0.1f),
				5);


//		Create trees
		for (int i = 0; i < NUMBER_TREES; i++) {
			int numEdges = 10;
			TurtleInterpreter turtleInterpreter = new TurtleInterpreter(numEdges);
			turtleInterpreter.setSubModels(List.of(MeshUtils.transform(leaf, new Matrix4f().scale(LEAF_SCALE / TREE_SCALE))));
			turtleInterpreter.setIgnored(List.of('A'));
			List<Module> instructions = treeSystem().performDerivations(new Random().nextInt(2) + 7);
			turtleInterpreter.interpretInstructions(instructions
					.stream()
					.map(m -> m.getName() == 'A' ? new ParametricValueModule('~', 0f) : m)
					.collect(Collectors.toList()));
			Mesh branches = turtleInterpreter.getMesh();
			Mesh leaves = turtleInterpreter.getCombinedSubModelMeshes().get(0);

			Vector2f pos = treePositions.get(i);

			branches.setModel((new Matrix4f())
					.identity()
					.translate(new Vector3f(pos.x, 0, pos.y))
					.scale(TREE_SCALE));
			branches.addTexture("diffuseTexture", barkTexture);
			branches.addTexture("normalTexture", normalBarkTexture);

			leaves.setModel((new Matrix4f())
					.identity()
					.translate(new Vector3f(pos.x, 0, pos.y))
					.scale(TREE_SCALE));
			leaves.addTexture("diffuseTexture", leafTexture);
			leaves.addTexture("normalTexture", normalLeafTexture);

			trees.add(new Model(List.of(branches, leaves)));
		}

		// Instanced tree
		int numEdges = 10;
		TurtleInterpreter turtleInterpreter = new TurtleInterpreter(numEdges);
		turtleInterpreter.setSubModels(List.of(MeshUtils.transform(leaf, new Matrix4f().scale(LEAF_SCALE / TREE_SCALE))));
		turtleInterpreter.setIgnored(List.of('A'));
		List<Module> instructions = treeSystem().performDerivations(new Random().nextInt(2) + 7);
		turtleInterpreter.interpretInstructions(instructions
				.stream()
				.map(m -> m.getName() == 'A' ? new ParametricValueModule('~', 0f) : m)
				.collect(Collectors.toList()));
		InstancedMesh instanceBranches = new InstancedMesh(turtleInterpreter.getMesh(), NUM_OF_INSTANCED_TREES);
		instanceBranches.addTexture("diffuseTexture", barkTexture);
		instanceBranches.addTexture("normalTexture", normalBarkTexture);
		InstancedMesh instancedCanopies = new InstancedMesh(turtleInterpreter.getCombinedSubModelMeshes().get(0), NUM_OF_INSTANCED_TREES);
		instancedCanopies.addTexture("diffuseTexture", leafTexture);
		instancedCanopies.addTexture("normalTexture", normalLeafTexture);
		instancedTree = new InstancedModel(List.of(instanceBranches, instancedCanopies), NUM_OF_INSTANCED_TREES);
		instancedTree.generateModelMatrices(() -> {
			Random r = new Random();
			float x = (r.nextFloat() - 0.5f) * GROUND_WIDTH;
			float z = (r.nextFloat() - 0.5f) * GROUND_WIDTH;
			return new Matrix4f().identity()
					.translate(x, quadtree.getHeight(x, z), z)
					.scale(r.nextFloat() + 0.5f)
					.scale(TREE_SCALE);
		});

		// Instanced leaves
		instancedLeaf = new InstancedMesh(leaf, NUM_OF_INSTANCED_LEAVES);
		instancedLeaf.addTexture("diffuseTexture", leafTexture);
		instancedLeaf.addTexture("normalTexture", normalLeafTexture);
		instancedLeaf.generateModelMatrices(() -> {
			Random r = new Random();
			float x = (r.nextFloat() - 0.5f) * GROUND_WIDTH;
			float z = (r.nextFloat() - 0.5f) * GROUND_WIDTH;
			return new Matrix4f().identity()
					.translate(x, quadtree.getHeight(x, z), z)
					.rotate(r.nextFloat() * (float) Math.PI * 2, new Vector3f(0, 1, 0))
					.rotate(r.nextFloat() * (float) Math.PI / 10, new Vector3f(r.nextFloat(), 0, r.nextFloat()).normalize())
					.scale(LEAF_SCALE);
		});

		for (int i = 0; i < NUM_OF_TWIG_TYPES; i++) {
			numEdges = 5;
			TurtleInterpreter twigTurtleInterpreter = new TurtleInterpreter(numEdges);
			twigTurtleInterpreter.setIgnored(List.of('A', 'B', 'C'));
			instructions = twigSystem().performDerivations(new Random().nextInt(2) + 5);
			twigTurtleInterpreter.interpretInstructions(instructions);
			Mesh twig = MeshUtils.transform(twigTurtleInterpreter.getMesh(), new Matrix4f().rotate((float) Math.PI / 2, new Vector3f(1, 0, 0)));

			InstancedMesh instancedTwig = new InstancedMesh(twig, NUM_OF_INSTANCED_TWIGS / NUM_OF_TWIG_TYPES);
			instancedTwig.addTexture("diffuseTexture", barkTexture);
			instancedTwig.addTexture("normalTexture", normalBarkTexture);
			instancedTwig.generateModelMatrices(() -> {
				Random r = new Random();
				float x = (r.nextFloat() - 0.5f) * GROUND_WIDTH;
				float z = (r.nextFloat() - 0.5f) * GROUND_WIDTH;
				return new Matrix4f().identity()
						.translate(x, quadtree.getHeight(x, z) + 0.2f, z)
						.rotate(r.nextFloat() * (float) Math.PI * 2, new Vector3f(0, 1, 0))
						.rotate(r.nextFloat() * (float) Math.PI / 10, new Vector3f(r.nextFloat(), 0, r.nextFloat()).normalize())
						.scale(TWIG_SCALE * (r.nextFloat() + 0.5f));
			});
			instancedTwigs.add(instancedTwig);
		}

		// Instanced grass
		Mesh grass = MeshUtils.transform(leaf, new Matrix4f().rotate((float) Math.PI / 2, out));
		Mesh grassBoard = new Mesh(grass);
		grassBoard.addTexture("diffuseTexture", grassTexture);
		grassBillboard = new InstancedModel(
				List.of(grassBoard, MeshUtils.transform(grassBoard, new Matrix4f().rotate((float) Math.PI / 2, up))),
				NUM_OF_INSTANCED_GRASS);
		grassBillboard.generateModelMatrices(() -> {
			Random r = new Random();
			float x = (r.nextFloat() - 0.5f) * GROUND_WIDTH;
			float z = (r.nextFloat() - 0.5f) * GROUND_WIDTH;
			float y = quadtree.getHeight(x, z) - r.nextFloat() * 0.6f - 0.3f;
			return new Matrix4f().identity()
					.translate(x, y, z)
					.rotate(r.nextFloat() * (float) Math.PI * 2, new Vector3f(0, 1, 0))
					.rotate(r.nextFloat() * (float) Math.PI / 10, new Vector3f(r.nextFloat(), 0, r.nextFloat()).normalize())
					.scale(GRASS_SCALE);
		});

	}

	private void initLighting() {
		Vector3f lightPos = new Vector3f(5f, 100f, -20f);
		Vector3f lightCol = new Vector3f(1.0f);

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

	private LSystem treeSystem() {
		float d1 = 1.6535f; //94.74f;
		float d2 = 2.3148f; //132.63f;
		float a = 0.1053f * (float) Math.PI; //18.95f;
		float lr = 1.109f;
		float vr = 1.832f; //1.732f
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

	private LSystem twigSystem() {
		float r1 = 0.9f;
		float r2 = 0.6f;
		float a0 = (float) Math.PI / 8;
		float a2 = (float) Math.PI / 8;
		float d = 2.3998277f;
		float wr = 0.707f;

		CharModule D = new CharModule('$');
		Module AOut = new ParametricExpressionModule('A', List.of("l", "w"), vars -> List.of(vars.get("l") * r1, vars.get("w") * wr));
		Module B1Out = new ParametricExpressionModule('B', List.of("l", "w"), vars -> List.of(vars.get("l") * r1, vars.get("w") * wr));
		Module B2Out = new ParametricExpressionModule('B', List.of("l", "w"), vars -> List.of(vars.get("l") * r2, vars.get("w") * wr));
		Module C1Out = new ParametricExpressionModule('C', List.of("l", "w"), vars -> List.of(vars.get("l") * r1, vars.get("w") * wr));
		Module C2Out = new ParametricExpressionModule('C', List.of("l", "w"), vars -> List.of(vars.get("l") * r2, vars.get("w") * wr));
		Module ExOut = new ParametricExpressionModule('!', List.of("l", "w"), vars -> List.of(vars.get("w")));
		Module FOut = new ParametricExpressionModule('F', List.of("l", "w"), vars -> List.of(vars.get("l")));

		return new LSystem(
				List.of(
						new ParametricValueModule('!', 2f),
						new ParametricValueModule('A', List.of(10f, 1f))
				),
				List.of(),
				List.of(
						new ProductionBuilder(
								List.of(new ParametricParameterModule('A', List.of("l", "w"))),
								List.of(
										ExOut,
										FOut,
										LB,
										new ParametricValueModule('&', a0),
										B2Out,
										RB,
										new ParametricValueModule('/', d),
										AOut
								)).withProbability(0.4f).build(),
						new ProductionBuilder(
								List.of(new ParametricParameterModule('A', List.of("l", "w"))),
								List.of(
										ExOut,
										FOut,
										AOut
								)).withProbability(0.6f).build(),
						new ProductionBuilder(
								List.of(new ParametricParameterModule('B', List.of("l", "w"))),
								List.of(
										ExOut,
										FOut,
										LB,
										new ParametricValueModule('+', -a2),
										D,
										AOut,
										RB
								)).withProbability(0.3f).build(),
						new ProductionBuilder(
								List.of(new ParametricParameterModule('B', List.of("l", "w"))),
								List.of(
										ExOut,
										FOut
								)).withProbability(0.3f).build(),
						new ProductionBuilder(
								List.of(new ParametricParameterModule('B', List.of("l", "w"))),
								List.of(
										ExOut,
										FOut
								)).withProbability(0.4f).build()
				));
	}

	private LSystem pyramidSystem() {

		return new LSystem(
				List.of(new ParametricValueModule('!', 1f),
						new ParametricValueModule('F', 1f),
						new ParametricValueModule('!', 0.1f),
						new ParametricValueModule('F', 1f)),
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

	private void renderScene() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		ShaderProgram textureProgram = useNormalMapping ? normalTextureShaderProgram : textureShaderProgram;
		ShaderProgram instanceProgram = useNormalMapping ? instancedNormalTextureShaderProgram : instancedTextureShaderProgram;

		textureProgram.use();
		textureProgram.setUniform("view", camera.getViewMatrix());

		// draw trees
		for (int i = 0; i < NUMBER_TREES; i++) {
			trees.get(i).render(textureProgram);
		}

		textureShaderProgram.use();
		textureShaderProgram.setUniform("view", camera.getViewMatrix());

		for (Mesh groundTile : groundTiles) {
			groundTile.render(textureShaderProgram);
		}

		instanceProgram.use();
		instanceProgram.setUniform("view", camera.getViewMatrix());
		instancedTree.render(instanceProgram);

		for (InstancedMesh twig : instancedTwigs) {
			twig.render(instanceProgram);
		}

		billboardShaderProgram.use();
		billboardShaderProgram.setUniform("view", camera.getViewMatrix());
		billboardShaderProgram.setUniform("viewPos", camera.getPosition());
		grassBillboard.render(billboardShaderProgram);

		// TODO replace with different shader uniform for texture colouring and add variation to leaves on model
		Vector3f lightCol = new Vector3f(0.74f, 0.37f, 0.27f);
		instanceProgram.setUniform("lightColour", lightCol);

		instancedLeaf.render(instanceProgram);

		lightCol = new Vector3f(1.0f);
		instanceProgram.setUniform("lightColour", lightCol);

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