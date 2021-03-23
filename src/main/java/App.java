import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_7;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT;
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
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_INVALID_ENUM;
import static org.lwjgl.opengl.GL11.GL_INVALID_OPERATION;
import static org.lwjgl.opengl.GL11.GL_INVALID_VALUE;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_OUT_OF_MEMORY;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_STACK_OVERFLOW;
import static org.lwjgl.opengl.GL11.GL_STACK_UNDERFLOW;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_BORDER_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL11.glTexParameterfv;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11C.GL_LINE;
import static org.lwjgl.opengl.GL11C.GL_NONE;
import static org.lwjgl.opengl.GL11C.GL_RED;
import static org.lwjgl.opengl.GL11C.GL_REPEAT;
import static org.lwjgl.opengl.GL11C.GL_RGB;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11C.GL_VERSION;
import static org.lwjgl.opengl.GL11C.glDrawBuffer;
import static org.lwjgl.opengl.GL11C.glGetString;
import static org.lwjgl.opengl.GL11C.glPolygonMode;
import static org.lwjgl.opengl.GL11C.glReadBuffer;
import static org.lwjgl.opengl.GL11C.glTexImage2D;
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.opengl.GL12C.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE8;
import static org.lwjgl.opengl.GL13C.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL13C.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE7;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL20C.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT3;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT4;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30C.GL_INVALID_FRAMEBUFFER_OPERATION;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_OUTPUT;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_OUTPUT_SYNCHRONOUS;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_SEVERITY_HIGH;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_SEVERITY_LOW;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_SEVERITY_MEDIUM;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_SEVERITY_NOTIFICATION;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_SOURCE_API;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_SOURCE_APPLICATION;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_SOURCE_OTHER;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_SOURCE_SHADER_COMPILER;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_SOURCE_THIRD_PARTY;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_SOURCE_WINDOW_SYSTEM;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_TYPE_ERROR;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_TYPE_MARKER;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_TYPE_OTHER;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_TYPE_PERFORMANCE;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_TYPE_POP_GROUP;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_TYPE_PORTABILITY;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_TYPE_PUSH_GROUP;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR;
import static org.lwjgl.opengl.GL43C.glDebugMessageCallback;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memByteBuffer;
import static rendering.ShaderPrograms.billboardShaderProgram;
import static rendering.ShaderPrograms.instancedLeafShaderProgram;
import static rendering.ShaderPrograms.lightingPassShader;
import static rendering.ShaderPrograms.scatteringShader;
import static rendering.ShaderPrograms.skyboxShaderProgram;
import static rendering.ShaderPrograms.ssaoBlurShader;
import static rendering.ShaderPrograms.ssaoShader;
import static rendering.ShaderPrograms.sunShader;
import static utils.MathsUtils.lerp;

import generation.TerrainQuadtree;
import javax.imageio.ImageIO;
import modeldata.meshdata.HDRTexture;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import params.ParameterLoader;
import params.Parameters;
import rendering.Camera;
import rendering.ShaderPrograms;
import rendering.Textures;
import sceneobjects.Polygon;
import sceneobjects.Quad;
import sceneobjects.Skybox;

public class App {

	private static final int MAJOR_VERSION = 4;
	private static final int MINOR_VERSION = 3;
	private final Parameters parameters = ParameterLoader.getParameters();
	private int windowWidth;
	private int windowHeight;
	private long window;
	private int gBuffer, ssaoBuffer, ssaoBlurBuffer, scatterBuffer, shadowBuffer;
	private int gNormal, gAlbedoSpecular, gPosition, gOcclusion, gTranslucency, gDepth;
	private int ssaoColor, ssaoColorBlur;
	private int scatterColor;
	private int shadowMap;
	private int ssaoNoiseTexture;
	private int polygonMode = GL_FILL;

	private Camera camera;
	private Matrix4f lightVP;
	private List<Vector3f> ssaoKernel;
	private TerrainQuadtree quadtree;
	private Skybox skybox;
	private Polygon sun;
	private Vector3f sunPosition;

	private double lastFrame = 0.0;
	private double deltaTime = 0.0;
	private double stepper = 0.0;

	private float lastX;
	private float lastY;
	private Matrix4f projection;

	private int frame = 0;
	private BufferedReader inputStream;

	public App() {
		if (parameters.input.stdin) {
			inputStream = new BufferedReader(new InputStreamReader(System.in));
		}
	}

	public static void main(String[] args) throws IOException {
		if (args.length > 1) {
			throw new RuntimeException("Usage: java -jar ForestGenerator.jar [params.yaml]");
		}
		if (args.length == 1) {
			ParameterLoader.loadParameters(args[0]);
		}
		new App().run();
	}

	public void run() {
		System.out.println("Running LWJGL " + Version.getVersion());

		init();
		if (parameters.output.window.visible) {
			glfwShowWindow(window);
		}
		loop();

		// Destroy window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		glfwTerminate();
		Objects.requireNonNull(glfwSetErrorCallback(null)).free();
	}

	private void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		window = initWindow();
		GL.createCapabilities();
		System.out.println("Running OpenGL " + glGetString(GL_VERSION));

		glEnable(GL_DEBUG_OUTPUT);
		glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_MULTISAMPLE);
		// TODO reincorporate this using combined forward and deferred shading
//		glEnable(GL_BLEND);
//		glEnable(GL_SAMPLE_ALPHA_TO_COVERAGE);
//		glEnable(GL_SAMPLE_ALPHA_TO_ONE);
//		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


		initShaders();
		initScene();
		checkError("scene initialisation");
		if (parameters.lighting.shadows.enabled) {
			renderShadowMap();
			checkError("shadow map rendering");
		}

		checkError("initialisation");
		glViewport(0, 0, windowWidth, windowHeight);
	}

	private long initWindow() {
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, MAJOR_VERSION);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, MINOR_VERSION);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE);
		glfwWindowHint(GLFW_SAMPLES, 4);

		GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		if (videoMode == null) {
			throw new RuntimeException("Failed to get primary monitor");
		}
		windowWidth = parameters.output.window.fullscreen ? videoMode.width() : parameters.output.window.width;
		windowHeight = parameters.output.window.fullscreen ? videoMode.height() : parameters.output.window.height;
		long window = glfwCreateWindow(windowWidth, windowHeight, "Forest Simulator 2021", NULL, NULL);
		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		return window;
	}

	private void initShaders() {
		final Vector3f cameraPosition = new Vector3f(parameters.camera.startPosition);
		final float cameraYaw = -90.0f;
		final float cameraPitch = 0.0f;
		camera = new Camera(cameraPosition, cameraYaw, cameraPitch);

		if (parameters.input.manual) {
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
		}

		glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
			if (id == 131169 || id == 131185 || id == 131218 || id == 131204) {
				return;
			}

			String msg = MemoryUtil.memUTF8(memByteBuffer(message, length));

			System.err.println("---------------");
			System.err.println("Debug message (" + id + "): " + msg);

			switch (source) {
				case GL_DEBUG_SOURCE_API -> System.err.print("Source: API");
				case GL_DEBUG_SOURCE_WINDOW_SYSTEM -> System.err.print("Source: Window System");
				case GL_DEBUG_SOURCE_SHADER_COMPILER -> System.err.print("Source: Shader Compiler");
				case GL_DEBUG_SOURCE_THIRD_PARTY -> System.err.print("Source: Third Party");
				case GL_DEBUG_SOURCE_APPLICATION -> System.err.print("Source: Application");
				case GL_DEBUG_SOURCE_OTHER -> System.err.print("Source: Other");
			}
			System.err.println();

			switch (type) {
				case GL_DEBUG_TYPE_ERROR -> System.err.print("Type: Error");
				case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR -> System.err.print("Type: Deprecated Behaviour");
				case GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR -> System.err.print("Type: Undefined Behaviour");
				case GL_DEBUG_TYPE_PORTABILITY -> System.err.print("Type: Portability");
				case GL_DEBUG_TYPE_PERFORMANCE -> System.err.print("Type: Performance");
				case GL_DEBUG_TYPE_MARKER -> System.err.print("Type: Marker");
				case GL_DEBUG_TYPE_PUSH_GROUP -> System.err.print("Type: Push Group");
				case GL_DEBUG_TYPE_POP_GROUP -> System.err.print("Type: Pop Group");
				case GL_DEBUG_TYPE_OTHER -> System.err.print("Type: Other");
			}
			System.err.println();

			switch (severity) {
				case GL_DEBUG_SEVERITY_HIGH -> System.err.print("Severity: high");
				case GL_DEBUG_SEVERITY_MEDIUM -> System.err.print("Severity: medium");
				case GL_DEBUG_SEVERITY_LOW -> System.err.print("Severity: low");
				case GL_DEBUG_SEVERITY_NOTIFICATION -> System.err.print("Severity: notification");
			}
			System.err.println();
			System.err.println();
		}, 0);

		glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
			windowWidth = width;
			windowHeight = height;
		});

		final float perspectiveAngle = (float) Math.toRadians(45.0f);
		final float nearPlane = 0.1f;
		final float farPlane = 300.0f;
		projection = new Matrix4f()
				.perspective(perspectiveAngle, (float) windowWidth / windowHeight, nearPlane, farPlane);
		ShaderPrograms.forAll(sp -> sp.setUniform("projection", projection));

		gBuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, gBuffer);

		gPosition = glGenTextures();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, gPosition);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, windowWidth, windowHeight, 0, GL_RGBA, GL_FLOAT, NULL);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, gPosition, 0);

		gNormal = glGenTextures();
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, gNormal);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, windowWidth, windowHeight, 0, GL_RGBA, GL_FLOAT, NULL);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, gNormal, 0);

		gAlbedoSpecular = glGenTextures();
		glActiveTexture(GL_TEXTURE2);
		glBindTexture(GL_TEXTURE_2D, gAlbedoSpecular);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, windowWidth, windowHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, gAlbedoSpecular, 0);

		gOcclusion = glGenTextures();
		glActiveTexture(GL_TEXTURE5);
		glBindTexture(GL_TEXTURE_2D, gOcclusion);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, windowWidth, windowHeight, 0, GL_RGBA, GL_FLOAT, NULL);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT3, GL_TEXTURE_2D, gOcclusion, 0);

		gTranslucency = glGenTextures();
		glActiveTexture(GL_TEXTURE8);
		glBindTexture(GL_TEXTURE_2D, gTranslucency);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, windowWidth, windowHeight, 0, GL_RGBA, GL_FLOAT, NULL);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT4, GL_TEXTURE_2D, gTranslucency, 0);

		glDrawBuffers(new int[] {GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2, GL_COLOR_ATTACHMENT3, GL_COLOR_ATTACHMENT4});

		gDepth = glGenTextures();
		glActiveTexture(GL_TEXTURE6);
		glBindTexture(GL_TEXTURE_2D, gDepth);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, windowWidth, windowHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, gDepth, 0);

		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("Deferred rendering framebuffer not complete");
		}
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		checkError("gBuffer init.");

		if (parameters.lighting.shadows.enabled) {
			shadowBuffer = glGenFramebuffers();
			glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer);
			shadowMap = glGenTextures();
			glActiveTexture(GL_TEXTURE7);
			glBindTexture(GL_TEXTURE_2D, shadowMap);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, parameters.lighting.shadows.resolution, parameters.lighting.shadows.resolution, 0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
			glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, new float[] {1f, 1f, 1f, 1f});
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowMap, 0);
			glDrawBuffer(GL_NONE);
			glReadBuffer(GL_NONE);
			if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
				System.err.println("Shadow map framebuffer not complete");
			}
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			checkError("shadow buffer init.");
		}

		if (parameters.lighting.ssao.enabled) {
			ssaoBuffer = glGenFramebuffers();
			glBindFramebuffer(GL_FRAMEBUFFER, ssaoBuffer);
			ssaoColor = glGenTextures();
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, ssaoColor);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, windowWidth, windowHeight, 0, GL_RED, GL_FLOAT, NULL);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, ssaoColor, 0);
			if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
				System.err.println("SSAO framebuffer not complete");
			}
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			checkError("SSAO buffer init.");

			ssaoBlurBuffer = glGenFramebuffers();
			glBindFramebuffer(GL_FRAMEBUFFER, ssaoBlurBuffer);
			ssaoColorBlur = glGenTextures();
			glActiveTexture(GL_TEXTURE3);
			glBindTexture(GL_TEXTURE_2D, ssaoColorBlur);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, windowWidth, windowHeight, 0, GL_RED, GL_FLOAT, NULL);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, ssaoColorBlur, 0);
			if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
				System.err.println("SSAO blur framebuffer not complete");
			}
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			checkError("SSAO blur buffer init.");
		}

		if (parameters.lighting.volumetricScattering.enabled) {
			scatterBuffer = glGenFramebuffers();
			glBindFramebuffer(GL_FRAMEBUFFER, scatterBuffer);
			scatterColor = glGenTextures();
			glActiveTexture(GL_TEXTURE4);
			glBindTexture(GL_TEXTURE_2D, scatterColor);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, windowWidth, windowHeight, 0, GL_RGBA, GL_FLOAT, NULL);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, scatterColor, 0);
			if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
				System.err.println("Scatter framebuffer not complete");
			}
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			checkError("scattering buffer init.");
		}

		initLighting();
	}

	private void initScene() {
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		skybox = new Skybox();
		// immenstadter_horn_8k.hdr
		// noon_grass_8k.hdr
		// gamrig_8k.hdr
		// qwantani_8k.hdr
		HDRTexture skyboxTexture = new HDRTexture("textures/gamrig_8k.hdr", 2048, new Vector3f(.529f, .808f, .922f), 8);
		sunPosition = skyboxTexture.getBrightestArea();
		skybox.addTexture("skyboxTexture", skyboxTexture);
		checkError("skybox loading");
		System.out.println("Skybox HDR loaded");

		sun = new Polygon(parameters.lighting.sun.numSides, sunShader);

		int quadtreeDepth = parameters.quadtree.levels;
		quadtree = new TerrainQuadtree(
				new Vector2f(0, 0),
				parameters.terrain.width,
				quadtreeDepth,
				(int) (parameters.terrain.width / 2 *
						Math.pow(2, Math.max((5 - quadtreeDepth), 0)) *
						parameters.terrain.vertexDensity),
				Textures.ground
		);
		quadtree.setSeedPoint(new Vector2f(camera.getPosition().x, camera.getPosition().z));
	}

	private void initLighting() {
		Vector3f lightCol = new Vector3f(23.47f, 21.31f, 20.79f);

		ShaderPrograms.forAll(sp -> sp.setUniform("lightPos", parameters.lighting.sun.position));
		ShaderPrograms.forAll(sp -> sp.setUniform("lightColour", lightCol));
		ShaderPrograms.forAll(sp -> sp.setUniform("ambientStrength", parameters.lighting.ambientStrength));

		lightingPassShader.setUniform("hdrEnabled", parameters.lighting.hdr.enabled);
		lightingPassShader.setUniform("gammaEnabled", parameters.lighting.gammaCorrection.enabled);
		lightingPassShader.setUniform("gamma", parameters.lighting.gammaCorrection.gamma);
		lightingPassShader.setUniform("aoEnabled", parameters.lighting.ssao.enabled);
		lightingPassShader.setUniform("renderDepth", parameters.output.depth);
		lightingPassShader.setUniform("invertDepth", parameters.output.invertDepth);
		lightingPassShader.setUniform("shadowsEnabled", parameters.lighting.shadows.enabled);
		lightingPassShader.setUniform("translucencyEnabled", parameters.lighting.translucency.enabled);
		lightingPassShader.setUniform("translucencyFactor", parameters.lighting.translucency.factor);
		lightingPassShader.setUniform("toneExposure", parameters.lighting.hdr.exposure);

		scatteringShader.setUniform("numSamples", parameters.lighting.volumetricScattering.numSamples);
		scatteringShader.setUniform("sampleDensity", parameters.lighting.volumetricScattering.sampleDensity);
		scatteringShader.setUniform("decay", parameters.lighting.volumetricScattering.decay);
		scatteringShader.setUniform("exposure", parameters.lighting.volumetricScattering.exposure);

		skyboxShaderProgram.setUniform("hdrEnabled", parameters.lighting.hdr.enabled);
		skyboxShaderProgram.setUniform("toneExposure", parameters.lighting.hdr.exposure);

		if (parameters.lighting.ssao.enabled) {
			ssaoKernel = new ArrayList<>();
			Random r = ParameterLoader.getParameters().random.generator;
			for (int i = 0; i < parameters.lighting.ssao.kernelSize; i++) {
				float scale = i / (float) parameters.lighting.ssao.kernelSize;
				scale = lerp(0.1f, 1.0f, scale * scale);
				Vector3f sample = new Vector3f(r.nextFloat() * 2.0f - 1.0f, r.nextFloat() * 2.0f - 1.0f, r.nextFloat())
						.normalize()
						.mul(r.nextFloat() * scale);
				ssaoKernel.add(sample);
			}

			ssaoNoiseTexture = glGenTextures();
			float[] image = new float[16 * 3];
			for (int i = 0; i < 16; i++) {
				image[i * 3] = r.nextFloat() * 2.0f - 1.0f;
				image[i * 3 + 1] = r.nextFloat() * 2.0f - 1.0f;
				image[i * 3 + 2] = 0.0f;
			}
			glActiveTexture(GL_TEXTURE3);
			glBindTexture(GL_TEXTURE_2D, ssaoNoiseTexture);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, 4, 4, 0, GL_RGB, GL_FLOAT, image);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		}
		checkError("lighting init");
	}

	private void renderShadowMap() {
		glCullFace(GL_FRONT);
		glViewport(0, 0, parameters.lighting.shadows.resolution, parameters.lighting.shadows.resolution);
		glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer);
		glClear(GL_DEPTH_BUFFER_BIT);
		//	TODO rework shader system and use much simpler shader to render scene for shadow map depth sampling
		float shadowMapScale = parameters.terrain.width;
		Vector3f sunPos = parameters.lighting.sun.position;
		float farPlane = (float) Math.sqrt(Math.pow(sunPos.y, 2) + Math.pow(parameters.terrain.width, 2)) + 1;
		Matrix4f lightProjection = new Matrix4f().ortho(-shadowMapScale, shadowMapScale, -shadowMapScale, shadowMapScale, 0.1f, farPlane);
		Matrix4f lightView = new Matrix4f().lookAt(
				sunPos,
				new Vector3f(0),
				new Vector3f(0, 1, 0));

		// View matrix for following player when generating dynamic maps
		//		Matrix4f lightView = new Matrix4f().lookAt(
		//				new Vector3f(sunPos.x + pos.x, sunPos.y, sunPos.z + pos.z),
		//				new Vector3f(pos.x, 0, pos.z),
		//				new Vector3f(0, 1, 0));

		ShaderPrograms.forAll(sp -> sp.setUniform("projection", lightProjection));
		ShaderPrograms.forAll(sp -> sp.setUniform("view", lightView));
		lightVP = lightView.mulLocal(lightProjection);

		// render all shadow casting objects
		quadtree.render(lightVP);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glCullFace(GL_BACK);

		System.out.println("Shadow map generated");
	}

	// FIXME occasionally (and seemingly randomly) the program runs through 2 render cycles just outputting a black
	//  	screen and then on the third, crashes (on glfwSwapBuffers) with the exit code -1073740791 (0xc0000409)
	//		An event in the Windows log which seems to coincide is:
	//			A TDR has been detected. The application must close. Error code: 7 (pid=32376 tid=29344 java.exe 64bit)
	//			Visit http://nvidia.custhelp.com/app/answers/detail/a_id/3633 for more information.
	//		From the NVIDIA driver and:
	//			java.exe
	//   		14.0.2.0
	//   		5f0657a8
	//   		nvoglv64.dll
	//   		27.21.14.6172
	//   		6035730c
	//  		c0000409
	//   		00000000011833e9
	//   		7e78
	//   		01d7175d0da63ae6
	//   		C:\Program Files\Java\jdk-14.0.2\bin\java.exe
	//   		C:\WINDOWS\System32\DriverStore\FileRepository\nvdmi.inf_amd64_d4f5fb7711ba6c16\nvoglv64.dll
	//   		79151a35-b41e-4339-b932-f7f014b87aff
	//    	From the Application
	//
	private void loop() {
		while (!glfwWindowShouldClose(window)) {
			updateDeltaTime();
			stepper += deltaTime / 10;
			stepper -= (int) stepper;
//			System.out.println(1 / deltaTime + " fps");

			glPolygonMode(GL_FRONT_AND_BACK, polygonMode);

			// Geometry Pass
			glViewport(0, 0, windowWidth, windowHeight);
			glBindFramebuffer(GL_FRAMEBUFFER, gBuffer);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			ShaderPrograms.forAll(sp -> sp.setUniform("projection", projection));
			ShaderPrograms.forAll(sp -> sp.setUniform("view", camera.getViewMatrix()));

			ShaderPrograms.forAll(sp -> sp.setUniform("lightPos", sunPosition));
			ShaderPrograms.forAll(sp -> sp.setUniform("lightColour", new Vector3f(parameters.lighting.sun.strength)));
			ShaderPrograms.forAll(sp -> sp.setUniform("ambientStrength", parameters.lighting.ambientStrength));

			billboardShaderProgram.setUniform("viewPos", camera.getPosition());
			instancedLeafShaderProgram.setUniform("viewPos", camera.getPosition());
			renderScene();
			glBindFramebuffer(GL_FRAMEBUFFER, 0);

			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

			if (parameters.lighting.ssao.enabled) {
				// SSAO pass
				glBindFramebuffer(GL_FRAMEBUFFER, ssaoBuffer);
				glClear(GL_COLOR_BUFFER_BIT);
				ssaoShader.setUniform("samples", ssaoKernel);
				ssaoShader.setUniform("noiseScale", new Vector2f(windowWidth / 4f, windowHeight / 4f));
				ssaoShader.setUniform("kernelSize", parameters.lighting.ssao.kernelSize);
				ssaoShader.setUniform("radius", parameters.lighting.ssao.radius);
				ssaoShader.setUniform("bias", parameters.lighting.ssao.bias);

				ssaoShader.setUniform("gPosition", 0);
				glActiveTexture(GL_TEXTURE0);
				glBindTexture(GL_TEXTURE_2D, gPosition);

				ssaoShader.setUniform("gNormal", 1);
				glActiveTexture(GL_TEXTURE1);
				glBindTexture(GL_TEXTURE_2D, gNormal);

				ssaoShader.setUniform("texNoise", 3);
				glActiveTexture(GL_TEXTURE3);
				glBindTexture(GL_TEXTURE_2D, ssaoNoiseTexture);

				(new Quad(ssaoShader)).render();
				glBindFramebuffer(GL_FRAMEBUFFER, 0);

				// SSAO blur pass
				glBindFramebuffer(GL_FRAMEBUFFER, ssaoBlurBuffer);
				glClear(GL_COLOR_BUFFER_BIT);
				ssaoBlurShader.setUniform("ssaoInput", 0);
				glActiveTexture(GL_TEXTURE0);
				glBindTexture(GL_TEXTURE_2D, ssaoColor);
				(new Quad(ssaoBlurShader)).render();
				glBindFramebuffer(GL_FRAMEBUFFER, 0);
			}

			if (parameters.lighting.volumetricScattering.enabled) {
				// Light scattering pass
				glBindFramebuffer(GL_FRAMEBUFFER, scatterBuffer);
				glClear(GL_COLOR_BUFFER_BIT);
				scatteringShader.setUniform("viewPos", camera.getPosition());
				scatteringShader.setUniform("viewDir", camera.getDirection());
				scatteringShader.setUniform("occlusion", 5);
				glActiveTexture(GL_TEXTURE5);
				glBindTexture(GL_TEXTURE_2D, gOcclusion);
				(new Quad(scatteringShader)).render();
				glBindFramebuffer(GL_FRAMEBUFFER, 0);
			}

			// Lighting pass
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			if (parameters.lighting.shadows.enabled) {
				lightingPassShader.setUniform("lightVP", lightVP);
			}
			lightingPassShader.setUniform("viewPos", camera.getPosition());

			lightingPassShader.setUniform("gPosition", 0);
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, gPosition);

			lightingPassShader.setUniform("gNormal", 1);
			glActiveTexture(GL_TEXTURE1);
			glBindTexture(GL_TEXTURE_2D, gNormal);

			lightingPassShader.setUniform("gAlbedoSpec", 2);
			glActiveTexture(GL_TEXTURE2);
			glBindTexture(GL_TEXTURE_2D, gAlbedoSpecular);

			lightingPassShader.setUniform("ssao", 3);
			glActiveTexture(GL_TEXTURE3);
			glBindTexture(GL_TEXTURE_2D, ssaoColorBlur);

			lightingPassShader.setUniform("scatter", 4);
			glActiveTexture(GL_TEXTURE4);
			glBindTexture(GL_TEXTURE_2D, scatterColor);

			lightingPassShader.setUniform("occlusion", 5);
			glActiveTexture(GL_TEXTURE5);
			glBindTexture(GL_TEXTURE_2D, gOcclusion);

			lightingPassShader.setUniform("gDepth", 6);
			glActiveTexture(GL_TEXTURE6);
			glBindTexture(GL_TEXTURE_2D, gDepth);

			lightingPassShader.setUniform("shadowMap", 7);
			glActiveTexture(GL_TEXTURE7);
			glBindTexture(GL_TEXTURE_2D, shadowMap);

			lightingPassShader.setUniform("gTranslucency", 8);
			glActiveTexture(GL_TEXTURE8);
			glBindTexture(GL_TEXTURE_2D, gTranslucency);

			if (parameters.output.frameImages && parameters.output.colour) {
				lightingPassShader.setUniform("renderDepth", false);
			}
			(new Quad(lightingPassShader)).render();

			glfwSwapBuffers(window);
			if (parameters.output.frameImages) {
				outputFrame(parameters.output.colour ? "colour" : "depth");

				// If parameters.output.colour is set, then colour frame was just saved, so now save depth frame
				if (parameters.output.colour && parameters.output.depth) {
					glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
					lightingPassShader.setUniform("renderDepth", true);
					(new Quad(lightingPassShader)).render();
					glfwSwapBuffers(window);
					outputFrame("depth");
				}

				lightingPassShader.setUniform("renderDepth", parameters.output.depth);
				frame += 1;
			}

			if (parameters.input.manual) {
				pollKeys();
				glfwPollEvents();
			}
			if (parameters.input.stdin) {
				blockAndProcessInputStream();
			}

			checkError("render loop");
		}
	}

	private void checkError(String stage) {
		int err;
		while ((err = glGetError()) != GL_NO_ERROR) {
			String error;
			switch (err) {
				case GL_INVALID_ENUM -> error = "INVALID_ENUM";
				case GL_INVALID_VALUE -> error = "INVALID_VALUE";
				case GL_INVALID_OPERATION -> error = "INVALID_OPERATION";
				case GL_STACK_OVERFLOW -> error = "STACK_OVERFLOW";
				case GL_STACK_UNDERFLOW -> error = "STACK_UNDERFLOW";
				case GL_OUT_OF_MEMORY -> error = "OUT_OF_MEMORY";
				case GL_INVALID_FRAMEBUFFER_OPERATION -> error = "INVALID_FRAMEBUFFER_OPERATION";
				default -> error = "UNKNOWN ERROR";
			}
			System.err.println("Error in " + stage + ": " + error);
		}
	}

	private void renderScene() {
		quadtree.render(camera.getViewMatrix().mulLocal(projection));

		glDepthFunc(GL_LEQUAL);

		sunShader.setUniform("view", new Matrix4f(new Matrix3f(camera.getViewMatrix())));
		if (parameters.lighting.sun.display) {
			sun.setModelMatrix(new Matrix4f()
					.translate(sunPosition)
					.rotateTowards(new Vector3f(camera.getDirection()).negate(), camera.getUp())
					.rotate((float) Math.PI / 2, new Vector3f(-1, 0, 0))
					.scale(parameters.lighting.sun.scale));
			sun.render();
		}
		skyboxShaderProgram.setUniform("view", new Matrix4f(new Matrix3f(camera.getViewMatrix())));
		skybox.render();

		glDepthFunc(GL_LESS);
	}

	private void updateDeltaTime() {
		if (parameters.input.stdin) {
			final int FPS = 30;
			deltaTime = 1f / FPS;
			return;
		}
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
			polygonMode = GL_LINE;
		}
		if (glfwGetKey(window, GLFW_KEY_1) == GLFW_RELEASE) {
			polygonMode = GL_FILL;
		}
		if (glfwGetKey(window, GLFW_KEY_3) == GLFW_PRESS) {
			lightingPassShader.setUniform("hdrEnabled", !parameters.lighting.hdr.enabled);
			skyboxShaderProgram.setUniform("hdrEnabled", !parameters.lighting.hdr.enabled);
		}
		if (glfwGetKey(window, GLFW_KEY_3) == GLFW_RELEASE) {
			lightingPassShader.setUniform("hdrEnabled", parameters.lighting.hdr.enabled);
			skyboxShaderProgram.setUniform("hdrEnabled", parameters.lighting.hdr.enabled);
		}
		if (glfwGetKey(window, GLFW_KEY_4) == GLFW_PRESS) {
			lightingPassShader.setUniform("aoEnabled", !parameters.lighting.ssao.enabled);
		}
		if (glfwGetKey(window, GLFW_KEY_4) == GLFW_RELEASE) {
			lightingPassShader.setUniform("aoEnabled", parameters.lighting.ssao.enabled);
		}
		if (glfwGetKey(window, GLFW_KEY_5) == GLFW_PRESS) {
			lightingPassShader.setUniform("renderDepth", !parameters.output.depth);
		}
		if (glfwGetKey(window, GLFW_KEY_5) == GLFW_RELEASE) {
			lightingPassShader.setUniform("renderDepth", parameters.output.depth);
		}
		if (glfwGetKey(window, GLFW_KEY_6) == GLFW_PRESS) {
			lightingPassShader.setUniform("shadowsEnabled", !parameters.lighting.shadows.enabled);
		}
		if (glfwGetKey(window, GLFW_KEY_6) == GLFW_RELEASE) {
			lightingPassShader.setUniform("shadowsEnabled", parameters.lighting.shadows.enabled);
		}
		if (glfwGetKey(window, GLFW_KEY_7) == GLFW_PRESS) {
			lightingPassShader.setUniform("translucencyEnabled", !parameters.lighting.translucency.enabled);
		}
		if (glfwGetKey(window, GLFW_KEY_7) == GLFW_RELEASE) {
			lightingPassShader.setUniform("translucencyEnabled", parameters.lighting.translucency.enabled);
		}
	}

	private void blockAndProcessInputStream() {
		// TODO param
		final int MOUSE_ANGLE = 20;
		try {
			String s = inputStream.readLine().toUpperCase();
			for (char c : s.toCharArray()) {
				switch (c) {
					case 'W' -> camera.move(Camera.MovementDirection.FORWARD, (float) deltaTime);
					case 'A' -> camera.move(Camera.MovementDirection.LEFT, (float) deltaTime);
					case 'S' -> camera.move(Camera.MovementDirection.BACKWARD, (float) deltaTime);
					case 'D' -> camera.move(Camera.MovementDirection.RIGHT, (float) deltaTime);
					case ' ' -> camera.move(Camera.MovementDirection.UP, (float) deltaTime);
					case '-' -> camera.move(Camera.MovementDirection.DOWN, (float) deltaTime);
					case 'I' -> camera.processMouseMovement(0, MOUSE_ANGLE);
					case 'K' -> camera.processMouseMovement(0, -MOUSE_ANGLE);
					case 'J' -> camera.processMouseMovement(-MOUSE_ANGLE, 0);
					case 'L' -> camera.processMouseMovement(MOUSE_ANGLE, 0);
					case 'X' -> exit();
				}
				quadtree.setSeedPoint(camera.getPosition().x, camera.getPosition().z);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void outputFrame(String type) {
		float[] array = new float[windowWidth * windowHeight * 3];
		glReadBuffer(GL_FRONT);
		glReadPixels(0, 0, windowWidth, windowHeight, GL_RGB, GL_FLOAT, array);

		BufferedImage image = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < array.length / 3; i++) {
			int r = (int) (array[i * 3] * 255);
			int g = (int) (array[i * 3 + 1] * 255);
			int b = (int) (array[i * 3 + 2] * 255);
			image.setRGB(i % windowWidth, windowHeight - 1 - i / windowWidth, (r << 16) + (g << 8) + b);
		}
		try {
			File dir = new File("./frames");
			dir.mkdir();
			File file = new File(String.format("./frames/frame-%s-%d.jpg", type, frame));
			file.createNewFile();
			ImageIO.write(image, "jpg", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void exit() {
		glfwSetWindowShouldClose(window, true);
	}

}