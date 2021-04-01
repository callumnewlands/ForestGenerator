package sceneobjects;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static rendering.ShaderPrograms.billboardShaderProgram;

import generation.TerrainQuadtree;
import lombok.Getter;
import modeldata.meshdata.Mesh;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import params.ParameterLoader;
import params.Parameters;
import rendering.LevelOfDetail;
import rendering.Textures;
import utils.MeshUtils;

public class CrossedBillboard extends InstancedGroundObject {

	private final static Vector3f up = new Vector3f(0f, 1f, 0f);
	private final static Vector3f out = new Vector3f(0f, 0f, 1f);
	@Getter
	private final int index;

	public CrossedBillboard(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree, boolean yRotationOnly, int index) {
		super(numberOfTypes, numberOfInstances, regionCentre, regionWidth, quadtree, yRotationOnly);
		this.index = index;
		generate();
	}

	@Override
	float getScale() {
		return 0.7f;
	}

	@Override
	float getHeightOffset() {
		Random r = ParameterLoader.getParameters().random.generator;
		return 0.0f;
//		return -r.nextFloat() * 0.6f - 0.3f;
	}

	private Matrix4f getRandomRotation(float yAngle) {
		Random r = ParameterLoader.getParameters().random.generator;
		return new Matrix4f()
				.rotate(yAngle, up)
				.rotate((float) (r.nextFloat() * Math.PI / 4 + Math.PI / 6),
						new Vector3f(r.nextFloat(), 0, r.nextFloat()).normalize());
	}

	@Override
	Map<LevelOfDetail, List<Mesh>> getMeshes() {
		Parameters.SceneObjects.CrossedBillboard params = ParameterLoader.getParameters().sceneObjects.crossedBillboards.get(index);
		Mesh board = MeshUtils.transform(Trees.leaf, new Matrix4f()
				.rotate((float) Math.PI / 2, out)
				.scale(params.yScale, 1, params.xScale));
		board.addTexture("diffuseTexture", Textures.billboardTextures.get(index));
		board.setShaderProgram(billboardShaderProgram);
		final int numOfBoards = 4;
		return Map.of(
				LevelOfDetail.HIGH, IntStream.range(0, numOfBoards)
						.mapToObj(i -> MeshUtils.transform(board, getRandomRotation((float) (i * Math.PI / numOfBoards))))
						.collect(Collectors.toList()),
				LevelOfDetail.LOW, List.of(
						new Mesh(board),
						MeshUtils.transform(board, new Matrix4f().rotate((float) Math.PI / 2, up)))
		);
	}

}
