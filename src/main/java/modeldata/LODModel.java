package modeldata;

import java.util.Map;
import modeldata.meshdata.Mesh;
import org.joml.Matrix4f;
import rendering.LevelOfDetail;

public class LODModel {

	private final Map<LevelOfDetail, Model> models;

	public LODModel(Map<LevelOfDetail, Model> models) {
		this.models = models;
	}

	public void setModelMatrix(Matrix4f modelMat) {
		for (Model m : models.values()) {
			m.setModelMatrix(modelMat);
		}
	}

	public void render(LevelOfDetail levelOfDetail, boolean renderForShadows) {
		Model lodModel = models.get(levelOfDetail);
		if (lodModel == null) {
			switch (levelOfDetail) {
				case HIGH -> lodModel = models.get(LevelOfDetail.LOW);
				case LOW -> lodModel = models.get(LevelOfDetail.HIGH);
				default -> throw new RuntimeException("Unhandled LOD: " + levelOfDetail);
			}
		}
		for (Mesh mesh : lodModel.getMeshes()) {
			mesh.render(renderForShadows);
		}
	}

}
