package rendering;

import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11C.GL_REPEAT;
import static org.lwjgl.opengl.GL11C.GL_RGBA;
import static org.lwjgl.opengl.GL12C.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL21C.GL_SRGB_ALPHA;

import modeldata.meshdata.Texture2D;
import org.joml.Vector3f;
import params.ParameterLoader;
import params.Parameters;

public final class Textures {

	public static Parameters parameters = ParameterLoader.getParameters();

	public static Texture2D leaf = new Texture2D(
			"textures/Leaf2_front_rotated.tga",
			new Vector3f(0.1f, 0.3f, 0.1f),
			0,
			GL_SRGB_ALPHA,
			GL_CLAMP_TO_EDGE);

	public static Texture2D leafNormal = new Texture2D(
			"textures/Leaf2_normals_front_rotated.tga",
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

	public static Texture2D twigBarkNormal = new Texture2D(
			parameters.sceneObjects.twigs.texture.normal,
			new Vector3f(0.34f, 0.17f, 0.07f),
			4,
			GL_RGBA,
			GL_REPEAT);

	public static Texture2D grass = new Texture2D(
			parameters.sceneObjects.grass.texture.diffuse,
			new Vector3f(0.1f, 0.3f, 0.1f),
			5,
			GL_SRGB_ALPHA,
			GL_CLAMP_TO_EDGE);

//	public static Texture2D rock = new Texture2D(
//			"textures/Mossy_rock_01_2K_Base_Color.png",
//			new Vector3f(0.3f, 0.3f, 0.3f),
//			6,
//			GL_SRGB_ALPHA,
//			GL_REPEAT);
//
//	public static Texture2D rockNormal = new Texture2D(
//			"textures/Mossy_rock_01_2K_Normal.png",
//			new Vector3f(0.3f, 0.3f, 0.3f),
//			7,
//			GL_RGBA,
//			GL_REPEAT);

	//	public static CubemapTexture skybox = new CubemapTexture(
//			List.of("right.jpg",
//					"left.jpg",
//					"top.jpg",
//					"bottom.jpg",
//					"front.jpg",
//					"back.jpg"
//			).stream().map(s ->  "textures/skybox/" + s).collect(Collectors.toList()),
//			new Vector3f(.529f, .808f, .922f),
//			8);
//
//	public static CubemapTexture skybox2 = new CubemapTexture(
//			List.of("posx.jpg",
//					"negx.jpg",
//					"posy.jpg",
//					"negy.jpg",
//					"posz.jpg",
//					"negz.jpg"
//			).stream().map(s ->  "textures/skybox2/" + s).collect(Collectors.toList()),
//			new Vector3f(.529f, .808f, .922f),
//			8);
//
//	public static CubemapTexture skybox3 = new CubemapTexture(
//			List.of("posx.jpg",
//					"negx.jpg",
//					"posy.jpg",
//					"negy.jpg",
//					"posz.jpg",
//					"negz.jpg"
//			).stream().map(s ->  "textures/skybox3/" + s).collect(Collectors.toList()),
//			new Vector3f(.529f, .808f, .922f),
//			8);

	public static List<TreeTextures> treeTextures = parameters.sceneObjects.trees
			.stream()
			.map(params -> new TreeTextures(params.barkTexture, params.leafTextures))
			.collect(Collectors.toList());

	private Textures() {

	}

	// TODO null textures
	// TODO check if file already loaded?
	public static class TreeTextures {
		public Texture2D bark;
		public Texture2D barkNormal;
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
			barkNormal = new Texture2D(
					barkPaths.normal,
					new Vector3f(0.34f, 0.17f, 0.07f),
					4,
					GL_RGBA,
					GL_REPEAT);
			leafFront = new Texture2D(
					leafPaths.frontAlbedo,
					new Vector3f(0.1f, 0.3f, 0.1f),
					9,
					GL_SRGB_ALPHA,
					GL_CLAMP_TO_EDGE);
			leafFrontT = new Texture2D(
					leafPaths.frontTranslucency,
					new Vector3f(0.1f, 0.3f, 0.1f),
					10,
					GL_SRGB_ALPHA,
					GL_CLAMP_TO_EDGE);
			leafBack = new Texture2D(
					leafPaths.backAlbedo,
					new Vector3f(0.1f, 0.3f, 0.1f),
					11,
					GL_SRGB_ALPHA,
					GL_CLAMP_TO_EDGE);
			leafBackT = new Texture2D(
					leafPaths.backTranslucency,
					new Vector3f(0.1f, 0.3f, 0.1f),
					12,
					GL_SRGB_ALPHA,
					GL_CLAMP_TO_EDGE);
			leafFrontHL = new Texture2D(
					leafPaths.frontHalfLife,
					new Vector3f(0.1f, 0.3f, 0.1f),
					13,
					GL_RGBA,
					GL_CLAMP_TO_EDGE);
			leafFrontNorm = new Texture2D(
					leafPaths.frontNormal,
					new Vector3f(0.1f, 0.3f, 0.1f),
					14,
					GL_RGBA,
					GL_CLAMP_TO_EDGE);
			leafBackHL = new Texture2D(
					leafPaths.backHalfLife,
					new Vector3f(0.1f, 0.3f, 0.1f),
					15,
					GL_RGBA,
					GL_CLAMP_TO_EDGE);
			leafBackNorm = new Texture2D(
					leafPaths.backNormal,
					new Vector3f(0.1f, 0.3f, 0.1f),
					16,
					GL_RGBA,
					GL_CLAMP_TO_EDGE);
		}
	}

}
