package sceneobjects;

import java.util.List;
import java.util.Map;
import java.util.Random;
import generation.TerrainQuadtree;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.Texture2D;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import rendering.LevelOfDetail;
import rendering.Textures;
import utils.MeshUtils;

public class Grass extends InstancedGroundObject {

	private final static Vector3f up = new Vector3f(0f, 1f, 0f);
	private final static Vector3f out = new Vector3f(0f, 0f, 1f);

	public Grass(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree, boolean yRotationOnly) {
		super(numberOfTypes, numberOfInstances, regionCentre, regionWidth, quadtree, yRotationOnly);
	}

	@Override
	float getScale() {
		return 0.7f;
	}

	@Override
	float getHeightOffset() {
		return -(new Random()).nextFloat() * 0.6f - 0.3f;
	}

	@Override
	Map<LevelOfDetail, List<Mesh>> getMeshes() {
		Mesh grass = MeshUtils.transform(Trees.leaf, new Matrix4f().rotate((float) Math.PI / 2, out));
		return Map.of(LevelOfDetail.LOW,
				List.of(new Mesh(grass), MeshUtils.transform(grass, new Matrix4f().rotate((float) Math.PI / 2, up))));
	}

	@Override
	Map<LevelOfDetail, List<Texture2D>> getDiffuseTextures() {
		return Map.of(LevelOfDetail.LOW, List.of(Textures.grass, Textures.grass));
	}
}
