package params;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joml.Vector3f;

@NoArgsConstructor
@Setter
public class Parameters {

	public Window window = new Window();
	public Camera camera = new Camera();
	public Terrain terrain = new Terrain();
	public Quadtree quadtree = new Quadtree();
	public SceneObjects sceneObjects = new SceneObjects();
	public Lighting lighting = new Lighting();

	@NoArgsConstructor
	@Setter
	public static class Window {
		public boolean fullscreen = true; // TODO
		public int width = 800;  // TODO
		public int height = 600;  // TODO
	}

	@NoArgsConstructor
	@Setter
	public static class Camera {
		@JsonDeserialize(using = ParameterLoader.Vec3Deserializer.class)
		public Vector3f startPosition = new Vector3f(0, 3.3f, 0);
		public boolean verticalMovement = true;  // TODO
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
		public boolean hdrEnabled = true;
		public float ambientStrength = 0.3f;
		public Sun sun = new Sun();
		public SSAO ssao = new SSAO();

		@NoArgsConstructor
		@Setter
		public static class Sun {
			public boolean display = true;
			public int numSides = 10;
			public float strength = 1f;
			@JsonDeserialize(using = ParameterLoader.Vec3Deserializer.class)
			public Vector3f position = new Vector3f(100.0f, 300.0f, -200.0f);
		}

		@NoArgsConstructor
		@Setter
		public static class SSAO {
			public boolean enabled = true;
			public int kernelSize = 32;
			public float radius = 0.5f;
			public float bias = 0.025f;
		}
	}
}
