package params;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joml.Vector3f;

@NoArgsConstructor
@Setter
public class Parameters {

	public String resourcesRoot = "./resources/";
	public Random random = new Random();
	public Input input = new Input();
	public Output output = new Output();
	public Camera camera = new Camera();
	public Terrain terrain = new Terrain();
	public Quadtree quadtree = new Quadtree();
	public SceneObjects sceneObjects = new SceneObjects();
	public Lighting lighting = new Lighting();

	@NoArgsConstructor
	@Setter
	public static class Random {
		public long seed = -1;
		@JsonIgnore
		public java.util.Random generator = null;
	}

	@NoArgsConstructor
	@Setter
	public static class Input {
		public boolean manual = true;
		public boolean stdin = false;
	}

	@NoArgsConstructor
	@Setter
	public static class Output {
		public boolean frameImages = false;
		public Window window = new Window();
		public boolean colour = true;
		public boolean depth = false;
		public boolean invertDepth = true;

		@NoArgsConstructor
		@Setter
		public static class Window {
			public boolean visible = true;
			public boolean fullscreen = true;
			public int width = 800;
			public int height = 600;
		}
	}

	@NoArgsConstructor
	@Setter
	public static class Camera {
		@JsonDeserialize(using = ParameterLoader.Vec3Deserializer.class)
		public Vector3f startPosition = new Vector3f(0, 3.3f, 0);
		public boolean verticalMovement = true;
	}

	@NoArgsConstructor
	@Setter
	public static class Texture {
		public String diffuse;
		public String normal = null;

		public Texture(String diffuse, String normal) {
			this.diffuse = diffuse;
			this.normal = normal;
		}

		public Texture(String diffuse) {
			this.diffuse = diffuse;
		}
	}

	@NoArgsConstructor
	@Setter
	public static class Terrain {
		public float width = 100f;
		public float verticalScale = 3.0f;
		public Noise noise = new Noise();
		public float vertexDensity = 0.7f;
		public Texture texture = new Texture("/textures/floor2.png");

		@NoArgsConstructor
		@Setter
		public static class Noise {
			public float xScale = 1.0f;
			public float yScale = 1.0f;
			public int octaves = 8;
			public float persistence = 0.5f;
			public float lacunarity = 2.0f;
		}
	}

	@NoArgsConstructor
	@Setter
	public static class Quadtree {
		public int levels = 2;
		public float thresholdCoefficient = 1.7f;
		public boolean frustumCulling = true;
	}

	@NoArgsConstructor
	@Setter
	public static class SceneObjects {
		public boolean display = true;
		@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
		public List<Tree> trees = List.of(
				new BranchingTree(),
				new MonopodialTree(),
				new PoplarTree()
		);
		public LSystemObject twigs = new LSystemObject(2, 5,
				new Texture("/textures/Bark_Pine_baseColor.jpg", "/textures/Bark_Pine_normal.jpg"));
		public SceneObject rocks = new SceneObject();
		public SceneObject grass = new SceneObject(new Texture("/textures/grass.png/"));
		public SceneObject fallenLeaves = new SceneObject();

		@NoArgsConstructor
		@Setter
		public static class SceneObject {
			public float density = 1.0f;
			public Texture texture;

			public SceneObject(Texture texture) {
				this.texture = texture;
			}
		}

		@NoArgsConstructor
		@AllArgsConstructor
		@Setter
		public static class LSystemObject extends SceneObject {
			public int typesPerQuad = 1; // TODO change to instance fraction
			public int numSides = 5;
			public Texture texture;
		}

		@NoArgsConstructor
		@AllArgsConstructor
		@Setter
		public static class Tree extends SceneObject {
			public String name = "Tree";
			public Map<String, Number> lSystemParams;
			public Texture barkTexture = new Texture(
					"/textures/Bark_Pine_baseColor.jpg",
					"/textures/Bark_Pine_normal.jpg");
			public LeafTextures leafTextures = new LeafTextures();
			public float instanceFraction = 0.2f;
			public int numSides = 5;
			public float scale = 1.0f;
			public float leafScale = 1.0f; // TODO this should probably scale with tree scale so that a 2* larger tree has 2* larger leaves than before
			public int minIterations = 7;
			public int maxIterations = 9;
			// TODO textures
		}

		@NoArgsConstructor
		@AllArgsConstructor
		@Setter
		public static class LeafTextures {
			public String frontAlbedo = "/textures/Leaf2/Leaf2_front.tga";
			public String frontNormal = "/textures/Leaf2/Leaf2_normals_front.tga";
			public String frontTranslucency = "/textures/Leaf2/Leaf2_front_t.tga";
			public String frontHalfLife = "/textures/Leaf2/Leaf2_halflife_front_t.tga";
			public String backAlbedo = "/textures/Leaf2/Leaf2_back.tga";
			public String backNormal = "/textures/Leaf2/Leaf2_normals_back.tga";
			public String backTranslucency = "/textures/Leaf2/Leaf2_back_t.tga";
			public String backHalfLife = "/textures/Leaf2/Leaf2_halflife_back_t.tga";
		}

		@Setter
		public static class BranchingTree extends Tree {
			public List<Branching> branchings = List.of(
					new Branching(List.of((float) Math.PI), 0.3f),
					new Branching(List.of(1.6535f, 2.3148f), 0.7f)
			);

			public BranchingTree() {
				super();
				lSystemParams = new HashMap<>(Map.of(
						"a", 0.3308f,
						"lr", 1.109f,
						"vr", 1.832f,
						"e", 0.052f));
				leafTextures = new LeafTextures(
						"/textures/Leaf1/Leaf1_front.tga",
						"/textures/Leaf1/Leaf1_normals_front.tga",
						"/textures/Leaf1/Leaf1_front_t.tga",
						"/textures/Leaf1/Leaf1_halflife_front_t.tga",
						"/textures/Leaf1/Leaf1_back.tga",
						"/textures/Leaf1/Leaf1_normals_back.tga",
						"/textures/Leaf1/Leaf1_back_t.tga",
						"/textures/Leaf1/Leaf1_halflife_back_t.tga"
				);
				name = "Tree 1";
				numSides = 6;
				scale = 0.01f;
				leafScale = 0.7f;
				density = 0.8f;
			}

			@NoArgsConstructor
			@AllArgsConstructor
			@Setter
			public static class Branching {
				public List<Float> angles;
				public float prob;
			}
		}

		public static class MonopodialTree extends Tree {
			public MonopodialTree() {
				super();
				lSystemParams = new HashMap<>(Map.ofEntries(
						Map.entry("lB", 4f),  // Base length
						Map.entry("lS", 0.6f),  // Side branch length
						Map.entry("lSm", 0.4f),  // Min ratio for side branch length (of ls) and width (of ws)
						Map.entry("wB", 0.5f), // Base width
						Map.entry("wS", 0.4f), // Side branch width
						Map.entry("wS2", 0.4f), // 3rd level side branch width
						Map.entry("vr", 0.04f), // Width of start of side branch
						Map.entry("aB", 90), // Branch angle to trunk
						Map.entry("aS", 80), // Branch angle around trunk
						Map.entry("aS2", 140), // 3rd level side branch angle
						Map.entry("aS3", 60), // 3rd level side branch angle upwards
						Map.entry("aS4", 45), // 3rd level side branch angle upwards for 2nd part
						Map.entry("tH", 0.7f), // Threshold for switching expansion/contraction (1 = bottom, 0 = top)
						Map.entry("aD", -1), // Angle to curve the side branches downward
						Map.entry("nB", 8), // Number of side branches (and trunk height segments) per iteration
						Map.entry("nB2", 17),// Branching factor of side branches
						Map.entry("l1", 0.1f), // Length of trunk sections
						Map.entry("lr", 0.5f), // Offset of side branches from centre
						Map.entry("e", 0))); // Elasticity
				name = "Aspen";
				numSides = 6;
				scale = 1f;
				leafScale = 0.28f;
				minIterations = 10;
				maxIterations = 12;
				density = 0.7f;
				barkTexture = new Texture(
						"/textures/Aspen_bark_001_COLOR.jpg",
						"/textures/Aspen_bark_001_NORM.jpg");
//				leafTextures = new LeafTextures(
//						"/textures/Leaf3/Leaf3_front.tga",
//						"/textures/Leaf3/Leaf3_normals_front.tga",
//						"/textures/Leaf3/Leaf3_front_t.tga",
//						"/textures/Leaf3/Leaf3_halflife_front_t.tga",
//						"/textures/Leaf3/Leaf3_back.tga",
//						"/textures/Leaf3/Leaf3_normals_back.tga",
//						"/textures/Leaf3/Leaf3_back_t.tga",
//						"/textures/Leaf3/Leaf3_halflife_back_t.tga"
//				);
			}
		}

		public static class PoplarTree extends MonopodialTree {
			public PoplarTree() {
				super();
				lSystemParams = new HashMap<>(Map.ofEntries(
						Map.entry("lB", 0f),  // Base length
						Map.entry("lS", 1.06f),  // Side branch length
						Map.entry("lSm", 1.0f),  // Min ratio for side branch length (of ls) and width (of ws)
						Map.entry("wB", 0.7f), // Base width
						Map.entry("wS", 0.3f), // Side branch width
						Map.entry("wS2", 0.2f), // 3rd level side branch width
						Map.entry("vr", 0.2f), // Width of start of side branch
						Map.entry("aB", 35), // Branch angle to trunk
						Map.entry("aS", 80), // Branch angle around trunk
						Map.entry("aS2", 140), // 3rd level side branch angle
						Map.entry("aS3", 30), // 3rd level side branch angle upwards
						Map.entry("aS4", -20), // 3rd level side branch angle upwards for 2nd part
						Map.entry("tH", 1.0f), // Threshold for switching expansion/contraction (1 = bottom, 0 = top)
						Map.entry("aD", -1.2), // Angle to curve the side branches downward
						Map.entry("nB", 2), // Number of side branches (and trunk height segments) per iteration
						Map.entry("nB2", 25),// Branching factor of side branches
						Map.entry("l1", 0.5f), // Length of trunk sections
						Map.entry("lr", 0f), // Offset of side branches from centre
						Map.entry("e", -0.01f)));  // Elasticity
				name = "Lombardy Poplar";
				scale = 0.7f;
				leafScale = 0.15f;
				minIterations = 13;
				maxIterations = 15;
				barkTexture = new Texture(
						"/textures/Bark_Pine_baseColor.jpg",
						"/textures/Bark_Pine_normal.jpg");
//				leafTextures = new LeafTextures(
//						"/textures/Leaf2/Leaf2_front.tga",
//						"/textures/Leaf2/Leaf2_normals_front.tga",
//						"/textures/Leaf2/Leaf2_front_t.tga",
//						"/textures/Leaf2/Leaf2_halflife_front_t.tga",
//						"/textures/Leaf2/Leaf2_back.tga",
//						"/textures/Leaf2/Leaf2_normals_back.tga",
//						"/textures/Leaf2/Leaf2_back_t.tga",
//						"/textures/Leaf2/Leaf2_halflife_back_t.tga"
//				);
			}
		}
	}

	@NoArgsConstructor
	@Setter
	public static class Lighting {
		public float ambientStrength = 0.2f;
		public Sun sun = new Sun();
		public SSAO ssao = new SSAO();
		public HDR hdr = new HDR();
		public GammaCorrection gammaCorrection = new GammaCorrection();
		public Translucency translucency = new Translucency();
		public Shadows shadows = new Shadows();
		public VolumetricScattering volumetricScattering = new VolumetricScattering();

		@NoArgsConstructor
		@Setter
		public static class Sun {
			public boolean display = false;
			public int numSides = 10;
			public float strength = 1f;
			public float scale = 20f;
			@JsonDeserialize(using = ParameterLoader.Vec3Deserializer.class)
			public Vector3f position = new Vector3f(50.0f, 200.0f, -50.0f);
		}

		@NoArgsConstructor
		@Setter
		public static class SSAO {
			public boolean enabled = true;
			public int kernelSize = 32;
			public float radius = 0.5f;
			public float bias = 0.025f;
		}

		@NoArgsConstructor
		@Setter
		public static class Translucency {
			public boolean enabled = true;
			public float factor = 0.5f;
		}

		@NoArgsConstructor
		@Setter
		public static class Shadows {
			public boolean enabled = true;
			public int resolution = 4096;
		}

		@NoArgsConstructor
		@Setter
		public static class VolumetricScattering {
			public boolean enabled = true;
			public int numSamples = 100;
			public float sampleDensity = 0.5f;
			public float decay = 0.99f;
			public float exposure = 0.0015f;
		}

		@NoArgsConstructor
		@Setter
		public static class HDR {
			public boolean enabled = true;
			public float exposure = 0.9f;
		}

		@NoArgsConstructor
		@Setter
		public static class GammaCorrection {
			public boolean enabled = true;
			public float gamma = 2.2f;
		}
	}
}
