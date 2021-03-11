package rendering;

import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11C.GL_REPEAT;
import static org.lwjgl.opengl.GL11C.GL_RGBA;
import static org.lwjgl.opengl.GL21C.GL_SRGB_ALPHA;

import modeldata.meshdata.CubemapTexture;
import modeldata.meshdata.Texture2D;
import org.joml.Vector3f;

public final class Textures {

	public static Texture2D leaf = new Texture2D(
			ShaderProgram.RESOURCES_PATH + "/textures/Leaf2_front_rotated.tga",
			new Vector3f(0.1f, 0.3f, 0.1f),
			0,
			GL_SRGB_ALPHA,
			GL_CLAMP);

	public static Texture2D leafNormal = new Texture2D(
			ShaderProgram.RESOURCES_PATH + "/textures/Leaf2_normals_front_rotated.tga",
			new Vector3f(0.1f, 0.3f, 0.1f),
			1,
			GL_RGBA,
			GL_CLAMP);

	public static Texture2D bark = new Texture2D(
			ShaderProgram.RESOURCES_PATH + "/textures/Bark_Pine_baseColor.jpg",
			new Vector3f(0.34f, 0.17f, 0.07f),
			2,
			GL_SRGB_ALPHA,
			GL_REPEAT);

	public static Texture2D ground = new Texture2D(
			ShaderProgram.RESOURCES_PATH + "/textures/floor2.png",
			new Vector3f(0.34f, 0.17f, 0.07f),
			3,
			GL_SRGB_ALPHA,
			GL_REPEAT);

	public static Texture2D barkNormal = new Texture2D(
			ShaderProgram.RESOURCES_PATH + "/textures/Bark_Pine_normal.jpg",
			new Vector3f(0.34f, 0.17f, 0.07f),
			4,
			GL_RGBA,
			GL_REPEAT);

	public static Texture2D grass = new Texture2D(
			ShaderProgram.RESOURCES_PATH + "/textures/grass.png",
			new Vector3f(0.1f, 0.3f, 0.1f),
			5,
			GL_SRGB_ALPHA,
			GL_CLAMP);

	public static Texture2D rock = new Texture2D(
			ShaderProgram.RESOURCES_PATH + "/textures/Mossy_rock_01_2K_Base_Color.png",
			new Vector3f(0.3f, 0.3f, 0.3f),
			6,
			GL_SRGB_ALPHA,
			GL_REPEAT);

	public static Texture2D rockNormal = new Texture2D(
			ShaderProgram.RESOURCES_PATH + "/textures/Mossy_rock_01_2K_Normal.png",
			new Vector3f(0.3f, 0.3f, 0.3f),
			7,
			GL_RGBA,
			GL_REPEAT);

	public static CubemapTexture skybox = new CubemapTexture(
			List.of("right.jpg",
					"left.jpg",
					"top.jpg",
					"bottom.jpg",
					"front.jpg",
					"back.jpg"
			).stream().map(s -> ShaderProgram.RESOURCES_PATH + "/textures/skybox/" + s).collect(Collectors.toList()),
			new Vector3f(.529f, .808f, .922f),
			8);

	public static CubemapTexture skybox2 = new CubemapTexture(
			List.of("posx.jpg",
					"negx.jpg",
					"posy.jpg",
					"negy.jpg",
					"posz.jpg",
					"negz.jpg"
			).stream().map(s -> ShaderProgram.RESOURCES_PATH + "/textures/skybox2/" + s).collect(Collectors.toList()),
			new Vector3f(.529f, .808f, .922f),
			8);

	public static CubemapTexture skybox3 = new CubemapTexture(
			List.of("posx.jpg",
					"negx.jpg",
					"posy.jpg",
					"negy.jpg",
					"posz.jpg",
					"negz.jpg"
			).stream().map(s -> ShaderProgram.RESOURCES_PATH + "/textures/skybox3/" + s).collect(Collectors.toList()),
			new Vector3f(.529f, .808f, .922f),
			8);


	private static final String leafName = "Leaf2";
	public static Texture2D leafFront = new Texture2D(
			ShaderProgram.RESOURCES_PATH + "/textures/" + leafName + "/" + leafName + "_front.tga",
			new Vector3f(0.1f, 0.3f, 0.1f),
			9,
			GL_SRGB_ALPHA,
			GL_CLAMP);
	public static Texture2D leafFrontT = new Texture2D(
			ShaderProgram.RESOURCES_PATH + "/textures/" + leafName + "/" + leafName + "_front_t.tga",
			new Vector3f(0.1f, 0.3f, 0.1f),
			10,
			GL_SRGB_ALPHA,
			GL_CLAMP);
	public static Texture2D leafBack = new Texture2D(
			ShaderProgram.RESOURCES_PATH + "/textures/" + leafName + "/" + leafName + "_back.tga",
			new Vector3f(0.1f, 0.3f, 0.1f),
			11,
			GL_SRGB_ALPHA,
			GL_CLAMP);
	public static Texture2D leafBackT = new Texture2D(
			ShaderProgram.RESOURCES_PATH + "/textures/" + leafName + "/" + leafName + "_back_T.tga",
			new Vector3f(0.1f, 0.3f, 0.1f),
			12,
			GL_SRGB_ALPHA,
			GL_CLAMP);
	public static Texture2D leafFrontHL = new Texture2D(
			ShaderProgram.RESOURCES_PATH + "/textures/" + leafName + "/" + leafName + "_halflife_front_t.tga",
			new Vector3f(0.1f, 0.3f, 0.1f),
			13,
			GL_RGBA,
			GL_CLAMP);
	public static Texture2D leafFrontNorm = new Texture2D(
			ShaderProgram.RESOURCES_PATH + "/textures/" + leafName + "/" + leafName + "_normals_front.tga",
			new Vector3f(0.1f, 0.3f, 0.1f),
			14,
			GL_RGBA,
			GL_CLAMP);
	public static Texture2D leafBackHL = new Texture2D(
			ShaderProgram.RESOURCES_PATH + "/textures/" + leafName + "/" + leafName + "_halflife_back_t.tga",
			new Vector3f(0.1f, 0.3f, 0.1f),
			15,
			GL_RGBA,
			GL_CLAMP);
	public static Texture2D leafBackNorm = new Texture2D(
			ShaderProgram.RESOURCES_PATH + "/textures/" + leafName + "/" + leafName + "_normals_back.tga",
			new Vector3f(0.1f, 0.3f, 0.1f),
			16,
			GL_RGBA,
			GL_CLAMP);

	private Textures() {

	}

}
