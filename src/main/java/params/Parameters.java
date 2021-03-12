package params;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joml.Vector3f;

@NoArgsConstructor
@Setter
public class Parameters {

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
	public static class Terrain {
		public float width = 200f;
		public Noise noise = new Noise();
		public float vertexDensity = 1f;

		@NoArgsConstructor
		@Setter
		public static class Noise {
			public float scale = 9.0f;
			public int octaves = 8;
			public float persistence = 0.5f;
			public float lacunarity = 0.025f;
		}
	}

	@NoArgsConstructor
	@Setter
	public static class Quadtree {
		public int levels = 3;
		public float thresholdCoefficient = 1.7f;
		public boolean frustumCulling = true;
	}

	@NoArgsConstructor
	@Setter
	public static class SceneObjects {
		public boolean display = true;
		public LSystemObject trees = new LSystemObject(1, 6);
		public LSystemObject twigs = new LSystemObject(2, 5);
		public SceneObject rocks = new SceneObject();
		public SceneObject grass = new SceneObject();
		public SceneObject fallenLeaves = new SceneObject();

		@NoArgsConstructor
		@Setter
		public static class SceneObject {
			public float density = 1.0f;
		}

		@NoArgsConstructor
		@AllArgsConstructor
		@Setter
		public static class LSystemObject extends SceneObject {
			public int typesPerQuad = 1;
			public int numSides = 5;
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
			public float sampleDensity = 0.9f;
			public float decay = 1f;
			public float exposure = 0.004f;
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
