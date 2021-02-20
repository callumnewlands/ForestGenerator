package sceneobjects;

import static org.lwjgl.opengl.GL11.GL_CLAMP;

import modeldata.meshdata.Texture;
import org.joml.Vector3f;
import rendering.ShaderProgram;

// TODO do shader programs similar to textures

public final class Textures {

	public static Texture leaf = new Texture(
			ShaderProgram.RESOURCES_PATH + "/textures/Leaf2_front_rotated.tga",
			new Vector3f(0.1f, 0.3f, 0.1f),
			0,
			GL_CLAMP);

	public static Texture leafNormal = new Texture(
			ShaderProgram.RESOURCES_PATH + "/textures/Leaf2_normals_front_rotated.tga",
			new Vector3f(0.1f, 0.3f, 0.1f),
			1,
			GL_CLAMP);

	public static Texture bark = new Texture(
			ShaderProgram.RESOURCES_PATH + "/textures/Bark_Pine_baseColor.jpg",
			new Vector3f(0.34f, 0.17f, 0.07f),
			2);

	public static Texture ground = new Texture(
			ShaderProgram.RESOURCES_PATH + "/textures/floor2.png",
			new Vector3f(0.34f, 0.17f, 0.07f),
			3);

	public static Texture barkNormal = new Texture(
			ShaderProgram.RESOURCES_PATH + "/textures/Bark_Pine_normal.jpg",
			new Vector3f(0.34f, 0.17f, 0.07f),
			4);

	public static Texture grass = new Texture(
			ShaderProgram.RESOURCES_PATH + "/textures/grass.png",
			new Vector3f(0.1f, 0.3f, 0.1f),
			5,
			GL_CLAMP);

	public static Texture rock = new Texture(
			ShaderProgram.RESOURCES_PATH + "/textures/Mossy_rock_01_2K_Base_Color.png",
			new Vector3f(0.3f, 0.3f, 0.3f),
			6);

	public static Texture rockNormal = new Texture(
			ShaderProgram.RESOURCES_PATH + "/textures/Mossy_rock_01_2K_Normal.png",
			new Vector3f(0.3f, 0.3f, 0.3f),
			7);


	private Textures() {

	}

}
