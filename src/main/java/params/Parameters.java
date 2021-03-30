package params;

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
	@AllArgsConstructor
	@Setter
	public static class ColourFilter {
		@JsonDeserialize(using = ParameterLoader.Vec3Deserializer.class)
		public Vector3f colour = new Vector3f(1, 0, 1);
		public float mixFactor = 0f;
		public boolean expMix = false;

		public ColourFilter(ColourFilter old) {
			this.colour = new Vector3f(old.colour);
			this.mixFactor = old.mixFactor;
			this.expMix = old.expMix;
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
				new TreeTypes.BranchingTree(),
				new TreeTypes.MonopodialTree(),
				new TreeTypes.AspenTree(),
				new TreeTypes.PoplarTree()
		);
		public Twigs twigs = new Twigs();
		public SceneObject rocks = new SceneObject();
		public Grass grass = new Grass();
		public SceneObject fallenLeaves = new SceneObject();

		@NoArgsConstructor
		@Setter
		public static class SceneObject {
			public float density = 1.0f;
		}

		@NoArgsConstructor
		@Setter
		public static class Twigs extends SceneObject {
			public int typesPerQuad = 2; // TODO change to instance fraction
			public int numSides = 5;
			public Texture texture = new Texture("/textures/Bark_Pine_baseColor.jpg", "/textures/Bark_Pine_normal.jpg");
		}

		@NoArgsConstructor
		@Setter
		public static class Grass extends SceneObject {
			public Texture texture = new Texture("/textures/grass.png/");
		}

		@NoArgsConstructor
		@AllArgsConstructor
		@Setter
		public static class Tree extends SceneObject {
			public String name = "Tree";
			// TODO see if you can update params map instead of overwriting on load from yaml
			public Map<String, Number> lSystemParams;
			public Texture barkTexture = new Texture(
					"/textures/Bark_Pine_baseColor.jpg",
					"/textures/Bark_Pine_normal.jpg");
			public LeafTextures leafTextures = new LeafTextures();
			public ColourFilter leafColourFilter = new ColourFilter();
			public float instanceFraction = 0.2f;
			public int numSides = 5;
			public float scale = 1.0f;
			public float leafXScale = 1.0f;
			public float leafYScale = 1.0f;
			public int minIterations = 7;
			public int maxIterations = 9;
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
