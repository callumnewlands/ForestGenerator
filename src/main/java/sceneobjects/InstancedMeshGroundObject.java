package sceneobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import generation.TerrainQuadtree;
import modeldata.InstancedMesh;
import modeldata.Mesh;
import modeldata.meshdata.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import rendering.ShaderProgram;

public abstract class InstancedMeshGroundObject {

	private final List<InstancedMesh> meshes = new ArrayList<>();

	public InstancedMeshGroundObject(int numberOfTypes, int numberOfInstances, Vector2f regionCentre, float regionWidth, TerrainQuadtree quadtree, boolean yRotationOnly) {
		for (int i = 0; i < numberOfTypes; i++) {
			InstancedMesh instancedMesh = new InstancedMesh(getMesh(), getNumber(numberOfInstances, numberOfTypes));
			instancedMesh.addTexture("diffuseTexture", getDiffuseTexture());
			Texture normal = getNormalTexture();
			if (normal != null) {
				instancedMesh.addTexture("normalTexture", normal);
			}
			instancedMesh.generateModelMatrices(() -> {
				Random r = new Random();
				float x = (r.nextFloat() - 0.5f) * regionWidth + regionCentre.x;
				float z = (r.nextFloat() - 0.5f) * regionWidth + regionCentre.y;
				Matrix4f model = new Matrix4f()
						.identity()
						.translate(x, quadtree.getHeight(x, z) + getHeightOffset(), z);
				if (!yRotationOnly) {
					model = model.rotate(
							r.nextFloat() * (float) Math.PI / 10,
							new Vector3f(r.nextFloat(), 0, r.nextFloat()).normalize()
					);
				}
				return model.rotate(r.nextFloat() * (float) Math.PI * 2, new Vector3f(0, 1, 0))
						.scale(getScale() * (r.nextFloat() + 0.5f));
			});
			meshes.add(instancedMesh);
		}
	}

	private int getNumber(int instances, int types) {
		float val = (float) instances / types;
		if (val < 1) {
			return (new Random()).nextInt(types) < instances ? 1 : 0;
		}
		return (int) val;
	}

	abstract float getScale();

	abstract float getHeightOffset();

	abstract Mesh getMesh();

	abstract Texture getDiffuseTexture();

	Texture getNormalTexture() {
		return null;
	}

	public void render(ShaderProgram shaderProgram) {
		for (InstancedMesh mesh : meshes) {
			mesh.render(shaderProgram);
		}
	}
}
