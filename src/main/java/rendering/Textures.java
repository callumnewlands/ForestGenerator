package rendering;

import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11C.GL_REPEAT;
import static org.lwjgl.opengl.GL11C.GL_RGBA;
import static org.lwjgl.opengl.GL12C.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL21C.GL_SRGB_ALPHA;

import modeldata.meshdata.Texture;
import modeldata.meshdata.Texture2D;
import org.joml.Vector3f;
import params.ParameterLoader;
import params.Parameters;

public final class Textures {

	public static Parameters parameters = ParameterLoader.getParameters();

	public static Texture2D leaf = new Texture2D(
			"textures/Leaf2/Leaf2_front.tga",
			new Vector3f(0.1f, 0.3f, 0.1f),
			0,
			GL_SRGB_ALPHA,
			GL_CLAMP_TO_EDGE);

	public static Texture2D leafNormal = new Texture2D(
			"textures/Leaf2/Leaf2_normals_front.tga",
			new Vector3f(0.1f, 0.3f, 0.1f),
			1,
			GL_RGBA,
			GL_CLAMP_TO_EDGE);

	public static Texture2D ground = new Texture2D(
			parameters.terrain.texture.diffuse,
			new Vector3f(0.34f, 0.17f, 0.07f),
			2,
			GL_SRGB_ALPHA,
			GL_REPEAT);

	public static Texture2D twigBark = new Texture2D(
			parameters.sceneObjects.twigs.texture.diffuse,
			new Vector3f(0.34f, 0.17f, 0.07f),
			3,
			GL_SRGB_ALPHA,
			GL_REPEAT);

	public static Texture2D twigBarkNormal = parameters.sceneObjects.twigs.texture.normal != null
			?
			new Texture2D(
					parameters.sceneObjects.twigs.texture.normal,
					new Vector3f(0.34f, 0.17f, 0.07f),
					4,
					GL_RGBA,
					GL_REPEAT)
			: null;

	public static Texture twigBarkGlossiness = parameters.sceneObjects.twigs.texture.glossiness != null
			?
			new Texture2D(
					parameters.sceneObjects.twigs.texture.glossiness,
					new Vector3f(0),
					17,
					GL_RGBA,
					GL_REPEAT)
			: null;


	public static List<Texture> billboardTextures = parameters.sceneObjects.crossedBillboards
			.stream()
			.map(params -> new Texture2D(
					params.texture.diffuse,
					new Vector3f(0.1f, 0.3f, 0.1f),
					5,
					GL_SRGB_ALPHA,
					GL_CLAMP_TO_EDGE)
			)
			.collect(Collectors.toList());

	public static List<TreeTextures> treeTextures = parameters.sceneObjects.trees
			.stream()
			.map(params -> new TreeTextures(params.barkTexture, params.leafTextures))
			.collect(Collectors.toList());

	private Textures() {

	}

	// Could check if textures are already loaded in a similar way to ExternalModel to reduce texture memory load
	public static class TreeTextures {
		public Texture2D bark;
		public Texture2D barkNormal;
		public Texture2D barkGlossiness;
		public Texture2D leafFront;
		public Texture2D leafFrontT;
		public Texture2D leafBack;
		public Texture2D leafBackT;
		public Texture2D leafFrontHL;
		public Texture2D leafFrontNorm;
		public Texture2D leafBackHL;
		public Texture2D leafBackNorm;

		public TreeTextures(Parameters.Texture barkPaths, Parameters.SceneObjects.LeafTextures leafPaths) {
			bark = new Texture2D(
					barkPaths.diffuse,
					new Vector3f(0.34f, 0.17f, 0.07f),
					3,
					GL_SRGB_ALPHA,
					GL_REPEAT);
			barkNormal = barkPaths.normal != null
					?
					new Texture2D(
							barkPaths.normal,
							new Vector3f(0.34f, 0.17f, 0.07f),
							4,
							GL_RGBA,
							GL_REPEAT)
					: null;
			barkGlossiness = barkPaths.glossiness != null
					?
					new Texture2D(
							barkPaths.glossiness,
							new Vector3f(0),
							17,
							GL_RGBA,
							GL_REPEAT
					)
					: null;
			leafFront = new Texture2D(
					leafPaths.frontAlbedo,
					new Vector3f(0.1f, 0.3f, 0.1f),
					9,
					GL_SRGB_ALPHA,
					GL_CLAMP_TO_EDGE);
			leafFrontT = leafPaths.frontTranslucency != null
					?
					new Texture2D(
							leafPaths.frontTranslucency,
							new Vector3f(0.1f, 0.3f, 0.1f),
							10,
							GL_SRGB_ALPHA,
							GL_CLAMP_TO_EDGE)
					: null;
			leafBack = new Texture2D(
					leafPaths.backAlbedo,
					new Vector3f(0.1f, 0.3f, 0.1f),
					11,
					GL_SRGB_ALPHA,
					GL_CLAMP_TO_EDGE);
			leafBackT = leafPaths.backTranslucency != null
					?
					new Texture2D(
							leafPaths.backTranslucency,
							new Vector3f(0.1f, 0.3f, 0.1f),
							12,
							GL_SRGB_ALPHA,
							GL_CLAMP_TO_EDGE)
					: null;
			leafFrontHL = leafPaths.frontHalfLife != null
					?
					new Texture2D(
							leafPaths.frontHalfLife,
							new Vector3f(0.1f, 0.3f, 0.1f),
							13,
							GL_RGBA,
							GL_CLAMP_TO_EDGE)
					: null;
			leafFrontNorm = leafPaths.frontNormal != null
					?
					new Texture2D(
							leafPaths.frontNormal,
							new Vector3f(0.1f, 0.3f, 0.1f),
							14,
							GL_RGBA,
							GL_CLAMP_TO_EDGE)
					: null;
			leafBackHL = leafPaths.backHalfLife != null
					?
					new Texture2D(
							leafPaths.backHalfLife,
							new Vector3f(0.1f, 0.3f, 0.1f),
							15,
							GL_RGBA,
							GL_CLAMP_TO_EDGE)
					: null;
			leafBackNorm = leafPaths.backNormal != null
					?
					new Texture2D(
							leafPaths.backNormal,
							new Vector3f(0.1f, 0.3f, 0.1f),
							16,
							GL_RGBA,
							GL_CLAMP_TO_EDGE)
					: null;
		}
	}

}
