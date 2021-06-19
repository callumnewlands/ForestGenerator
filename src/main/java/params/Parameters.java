/*
 * Copyright (c) 2021 Callum Newlands
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     The additional term of 7.b applies: Requiring preservation of specified
 *     reasonable legal notices or author attributions in that material or in the
 *     Appropriate Legal Notices displayed by works containing it
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package params;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joml.Vector3f;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Parameters {

	public String resourcesRoot = "./resources/";
	public Random random = new Random();
	public Input input = new Input();
	public Output output = new Output();
	public Camera camera = new Camera();
	public Terrain terrain = new Terrain();
	public EcosystemSimulation ecosystemSimulation = new EcosystemSimulation();
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
		public StdIn stdin = new StdIn();

		@NoArgsConstructor
		@Setter
		public static class StdIn {
			public boolean enabled = false;
			public float lookOffset = 10f;
			public int fps = 30;
		}
	}

	@NoArgsConstructor
	@Setter
	public static class Output {
		public FrameImages frameImages = new FrameImages();
		public Window window = new Window();
		public boolean colour = true;
		public boolean depth = false;
		public boolean invertDepth = true;
		public float renderDistance = 300.0f;
		public float maxDepthOutput = 20.0f;
		public Collisions collisions = new Collisions();

		@NoArgsConstructor
		@Setter
		public static class Window {
			public boolean visible = true;
			public boolean fullscreen = false;
			public int width = 800;
			public int height = 600;
		}

		@NoArgsConstructor
		@Setter
		public static class FrameImages {
			public boolean enabled = false;
			public String fileExtension = "jpg";
			public String filePrefix = "";
		}

		@NoArgsConstructor
		@Setter
		public static class Collisions {
			public boolean enabled = false;
			public float userRadius = 0.5f;
		}
	}

	@NoArgsConstructor
	@Setter
	public static class Camera {
		@JsonDeserialize(using = ParameterLoader.Vec3Deserializer.class)
		@JsonSerialize(using = ParameterLoader.Vec3Serializer.class)
		public Vector3f startPosition = new Vector3f(0, 3.3f, 0);
		@JsonDeserialize(using = ParameterLoader.Vec3Deserializer.class)
		@JsonSerialize(using = ParameterLoader.Vec3Serializer.class)
		public Vector3f startDirection = new Vector3f(0, 0, 1);
		public boolean verticalMovement = true;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Setter
	public static class Texture {
		public String diffuse;
		public String normal = null;
		public String glossiness = null;

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
		@JsonSerialize(using = ParameterLoader.Vec3Serializer.class)
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
		public int textureScale = 2;
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
	public static class EcosystemSimulation {
		public int numIterations = 400;
		public int yearLength = 20;
		public float ageThreshold = 0.7f;
		public float radiusWeight = 0.3f;
		public float smallRadiusViability = 0.4f;
		public float averageRadiusViability = 0.4f;
	}

	@NoArgsConstructor
	@Setter
	public static class Quadtree {
		public int levels = 3;
		public float thresholdCoefficient = 1.5f;
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
		public List<ExternalModel> externalModels = List.of(
				new ExternalModel("/models/Rock1/Rock1.obj", "/models/Rock1", true, 0.3f)
		);
		public List<CrossedBillboard> crossedBillboards = List.of(
				new CrossedBillboard(new Texture("/textures/grass2.png"), 4, 1.0f, 0.35f, 20.0f),
				new CrossedBillboard(new Texture("/textures/fern1_rotated.png"), 4, 1.2f, 1.2f, 0.2f),
				new CrossedBillboard(new Texture("/textures/fern2_rotated.png"), 4, 1.2f, 1.2f, 0.2f)
		);
		public SceneObject fallenLeaves = new SceneObject(1f, 0.75f, 1.25f, 0.7f, 0.0f, 0.1f);

		@NoArgsConstructor
		@AllArgsConstructor
		@Setter
		public static class SceneObject {
			public float density = 1.0f;
			public float minScaleFactor = 0.75f;
			public float maxScaleFactor = 1.25f;
			public float scale = 1.0f;
			public float yOffset = 0.0f;
			public float pitchVariability = 0.1f;
		}

		@Setter
		public static class Twigs extends SceneObject {
			public int typesPerQuad = 2;
			public int numSides = 5;
			public Texture texture = new Texture(
					"/textures/Bark_Pine_baseColor.jpg",
					"/textures/Bark_Pine_normal.jpg",
					"/textures/Bark_Pine_glossiness.jpg"
			);

			public Twigs() {
				super();
				this.yOffset = 0.1f;
				this.scale = 0.05f;
			}
		}

		@AllArgsConstructor
		@Setter
		public static class CrossedBillboard extends SceneObject {
			public Texture texture = new Texture("/textures/grass2.png");
			public int numBoards = 4;
			public float xScale = 1.0f;
			public float yScale = 1.0f;

			public CrossedBillboard() {
				super();
				this.scale = 0.7f;
			}

			public CrossedBillboard(Texture texture, int numBoards, float xScale, float yScale, float density) {
				this(texture, numBoards, xScale, yScale);
				this.density = density;
				this.scale = 0.7f;
			}
		}

		@AllArgsConstructor
		@Setter
		public static class ExternalModel extends SceneObject {
			public String modelPath = "/models/Rock1/Rock1.obj";
			public String texturesDir = "/models/Rock1";
			public boolean collidable = true;

			public ExternalModel() {
				super();
				this.yOffset = -0.1f;
			}

			public ExternalModel(String modelPath, String texturesDir, boolean collidable, float scale) {
				this(modelPath, texturesDir, collidable);
				this.scale = scale;
				this.yOffset = -0.1f;
			}
		}

		@Setter
		public static class Tree extends SceneObject {
			public String name = "Tree";
			public Map<String, Number> lSystemParamsLower;
			public Map<String, Number> lSystemParamsUpper;
			public Texture barkTexture = new Texture(
					"/textures/Bark_Pine_baseColor.jpg",
					"/textures/Bark_Pine_normal.jpg",
					"/textures/Bark_Pine_glossiness.jpg");
			public LeafTextures leafTextures = new LeafTextures();
			public ColourFilter leafColourFilter = null;
			public float numPerIterationSize = 3;
			public int numSides = 5;
			public float leafXScale = 1.0f;
			public float leafYScale = 1.0f;
			public int minIterations = 7;
			public int maxIterations = 9;
			public boolean widenBase = true;
			public int maxAge = 200;
			public float seedRadiusMultiplier = 2.0f;
			public int lowLODEdges = 2;
			public int lowLODLeafMerges = 1;

			public Tree() {
				super();
				this.pitchVariability = 0;
				this.minScaleFactor = 0.7f;
				this.maxScaleFactor = 1.1f;
				this.density = 0.5f;
			}
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
		public float ambientStrength = 0.4f;
		public int specularPower = 16;
		public Sun sun = new Sun();
		public Sky sky = new Sky();
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
			public boolean autoPosition = true;
			public int numSides = 10;
			@JsonDeserialize(using = ParameterLoader.Vec3Deserializer.class)
			@JsonSerialize(using = ParameterLoader.Vec3Serializer.class)
			public Vector3f strength = new Vector3f(3.8f, 3.3f, 3.2f);
			public float scale = 20f;
			@JsonDeserialize(using = ParameterLoader.Vec3Deserializer.class)
			@JsonSerialize(using = ParameterLoader.Vec3Serializer.class)
			public Vector3f position = new Vector3f(50.0f, 200.0f, -50.0f);
		}

		@NoArgsConstructor
		@Setter
		public static class Sky {
			public String hdrFile = "textures/gamrig_8k.hdr";
			public int resolution = 2048;
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
			public float factor = 0.1f;
		}

		@NoArgsConstructor
		@Setter
		public static class Shadows {
			public boolean enabled = true;
			public int resolution = 4096;
			public float bias = 0.005f;
		}

		@NoArgsConstructor
		@Setter
		public static class VolumetricScattering {
			public boolean enabled = true;
			public int numSamples = 100;
			public float sampleDensity = 1.3f;
			public float decay = 0.99f;
			public float exposure = 0.0015f;
			public float maxBrightness = 100f;
		}

		@NoArgsConstructor
		@Setter
		public static class HDR {
			public boolean enabled = true;
			public float exposure = 0.6f;
		}

		@NoArgsConstructor
		@Setter
		public static class GammaCorrection {
			public boolean enabled = true;
			public float gamma = 2.2f;
		}
	}
}
