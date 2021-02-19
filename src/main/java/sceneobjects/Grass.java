package sceneobjects;

import java.util.List;
import java.util.Random;
import generation.TerrainQuadtree;
import modeldata.Mesh;
import modeldata.meshdata.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import utils.MeshUtils;

public class Grass extends InstancedModelGroundObject {

	private final static Vector3f up = new Vector3f(0f, 1f, 0f);
	private final static Vector3f out = new Vector3f(0f, 0f, 1f);

	public Grass(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree, boolean yRotationOnly) {
		super(numberOfTypes, numberOfInstances, regionCentre, regionWidth, quadtree, yRotationOnly);
	}

	@Override
	float getScale() {
		return 0.9f;
	}

	@Override
	float getHeightOffset() {
		return -(new Random()).nextFloat() * 0.6f - 0.3f;
	}

	@Override
	List<Mesh> getMeshes() {
		Mesh grass = MeshUtils.transform(Trees.leaf, new Matrix4f().rotate((float) Math.PI / 2, out));
		Mesh grassBoard = new Mesh(grass);
		grassBoard.addTexture("diffuseTexture", Textures.grass);
		return List.of(grassBoard, MeshUtils.transform(grassBoard, new Matrix4f().rotate((float) Math.PI / 2, up)));
	}

	@Override
	List<Texture> getDiffuseTextures() {
		return List.of(Textures.grass, Textures.grass);
	}
}
