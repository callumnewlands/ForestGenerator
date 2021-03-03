import java.io.IOException;
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
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_4;
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
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11C.GL_LINE;
import static org.lwjgl.opengl.GL11C.GL_RED;
import static org.lwjgl.opengl.GL11C.GL_REPEAT;
import static org.lwjgl.opengl.GL11C.GL_RGB;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11C.GL_VERSION;
import static org.lwjgl.opengl.GL11C.glGetString;
import static org.lwjgl.opengl.GL11C.glPolygonMode;
import static org.lwjgl.opengl.GL11C.glTexImage2D;
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.opengl.GL12C.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13C.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL20C.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT3;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;
import static org.lwjgl.system.MemoryUtil.NULL;
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
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import params.ParameterLoader;
import params.Parameters;
import rendering.Camera;
import rendering.ShaderProgram;
import rendering.ShaderPrograms;
import rendering.Textures;
import sceneobjects.Polygon;
import sceneobjects.Quad;
import sceneobjects.Skybox;

public class App {

	private static final int MAJOR_VERSION = 4;
	private static final int MINOR_VERSION = 6;

	private int windowWidth;
	private int windowHeight;

	private long window;
	private int gBuffer, ssaoBuffer, ssaoBlurBuffer, scatterBuffer;
	private int gNormal, gAlbedoSpecular, gPosition, gOcclusion;
	private int ssaoColor, ssaoColorBlur;
	private int scatterColor;
	private int ssaoNoiseTexture;
	private Camera camera;
	private Boolean useNormalMapping = true;

	private List<Vector3f> ssaoKernel;

	private final Parameters parameters = ParameterLoader.getParameters();
	private TerrainQuadtree quadtree;
	private Skybox skybox;
	private Polygon sun;

	private double lastFrame = 0.0;
	private double deltaTime = 0.0;
	private double stepper = 0.0;

	private float lastX;
	private float lastY;
	private Matrix4f projection;

	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			ParameterLoader.loadParameters(ShaderProgram.RESOURCES_PATH + "/defaults.yaml");
		} else if (args.length == 1) {
			ParameterLoader.loadParameters(ShaderProgram.RESOURCES_PATH + "/" + args[0]);
		} else {
			throw new RuntimeException("Usage: java -jar ForestGenerator.jar [params.yaml]");
		}
		new App().run();
	}

	public void run() {
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

	private void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		window = initWindow();
		GL.createCapabilities();
		System.out.println("Running OpenGL " + glGetString(GL_VERSION));

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_MULTISAMPLE);
		// TODO reincorporate this using combined forward and deferred shading
//		glEnable(GL_BLEND);
//		glEnable(GL_SAMPLE_ALPHA_TO_COVERAGE);
//		glEnable(GL_SAMPLE_ALPHA_TO_ONE);
//		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glViewport(0, 0, windowWidth, windowHeight);

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
		windowWidth = videoMode.width() - 100;
		windowHeight = videoMode.height() - 100;
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

		glfwSetFramebufferSizeCallback(window, (window, height, width) -> {
			System.out.printf("Framebuffer size callback: width = %d, height = %d%n", width, height);
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

		glDrawBuffers(new int[] {GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2, GL_COLOR_ATTACHMENT3});

		int gRenderBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, gRenderBuffer);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, windowWidth, windowHeight);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, gRenderBuffer);
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("Deferred rendering framebuffer not complete");
		}
		glBindFramebuffer(GL_FRAMEBUFFER, 0);

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

		initLighting();
	}

	private void initScene() {
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		skybox = new Skybox();

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
		Vector3f lightCol = new Vector3f(parameters.lighting.sun.strength);

		ShaderPrograms.forAll(sp -> sp.setUniform("lightPos", parameters.lighting.sun.position));
		ShaderPrograms.forAll(sp -> sp.setUniform("lightColour", lightCol));
		ShaderPrograms.forAll(sp -> sp.setUniform("ambientStrength", parameters.lighting.ambientStrength));

		lightingPassShader.setUniform("hdrEnabled", parameters.lighting.hdrEnabled);
		lightingPassShader.setUniform("aoEnabled", parameters.lighting.ssao.enabled);

		ssaoKernel = new ArrayList<>();
		Random r = new Random();

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

	private void loop() {
		while (!glfwWindowShouldClose(window)) {
			updateDeltaTime();
			stepper += deltaTime / 10;
			stepper -= (int) stepper;
//			System.out.println(1 / deltaTime + " fps");

			// Geometry Pass
			glBindFramebuffer(GL_FRAMEBUFFER, gBuffer);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			renderScene();
			glBindFramebuffer(GL_FRAMEBUFFER, 0);

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

			// Light scattering pass
			glBindFramebuffer(GL_FRAMEBUFFER, scatterBuffer);
			glClear(GL_COLOR_BUFFER_BIT);
			scatteringShader.setUniform("occlusion", 5);
			glActiveTexture(GL_TEXTURE5);
			glBindTexture(GL_TEXTURE_2D, gOcclusion);
			(new Quad(scatteringShader)).render();
			glBindFramebuffer(GL_FRAMEBUFFER, 0);

			// Lighting pass
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			lightingPassShader.use();

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
			glBindTexture(GL_TEXTURE_2D, ssaoColorBlur); //ssaoColorBlur

			lightingPassShader.setUniform("scatter", 4);
			glActiveTexture(GL_TEXTURE4);
			glBindTexture(GL_TEXTURE_2D, scatterColor); //scatterColor

			lightingPassShader.setUniform("occlusion", 5);
			glActiveTexture(GL_TEXTURE5);
			glBindTexture(GL_TEXTURE_2D, gOcclusion);

			(new Quad(lightingPassShader)).render();

			glfwSwapBuffers(window);
			pollKeys();
			glfwPollEvents();
		}
	}

	private void renderScene() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		ShaderPrograms.forAll(sp -> sp.setUniform("view", camera.getViewMatrix()));
		billboardShaderProgram.setUniform("viewPos", camera.getPosition());
		instancedLeafShaderProgram.setUniform("viewPos", camera.getPosition());

		quadtree.render(useNormalMapping, camera.getViewMatrix().mulLocal(projection));

		if (parameters.lighting.sun.display) {
			sun.setModelMatrix(new Matrix4f()
					.translate(parameters.lighting.sun.position)
					.rotateTowards(new Vector3f(camera.getDirection()).negate(), camera.getUp())
					.rotate((float) Math.PI / 2, new Vector3f(-1, 0, 0))
					.scale(20f)); // TODO param
			sun.render();
		}

		skyboxShaderProgram.setUniform("view", new Matrix4f(new Matrix3f(camera.getViewMatrix())));
		// TODO draw skybox without SSAO or lighting
		skybox.render();
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
			lightingPassShader.setUniform("hdrEnabled", !parameters.lighting.hdrEnabled);
		}
		if (glfwGetKey(window, GLFW_KEY_3) == GLFW_RELEASE) {
			lightingPassShader.setUniform("hdrEnabled", parameters.lighting.hdrEnabled);
		}
		if (glfwGetKey(window, GLFW_KEY_4) == GLFW_PRESS) {
			lightingPassShader.setUniform("aoEnabled", !parameters.lighting.ssao.enabled);
		}
		if (glfwGetKey(window, GLFW_KEY_4) == GLFW_RELEASE) {
			lightingPassShader.setUniform("aoEnabled", parameters.lighting.ssao.enabled);
		}
	}

	private void exit() {
		glfwSetWindowShouldClose(window, true);
	}

}