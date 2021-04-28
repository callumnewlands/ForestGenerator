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

package modeldata;

import java.util.Map;
import java.util.function.Supplier;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.VertexAttribute;
import modeldata.meshdata.VertexBuffer;
import org.joml.Matrix4f;
import rendering.LevelOfDetail;

public class InstancedLODModel {

	private final int numberOfInstances;
	private final Map<LevelOfDetail, Model> models;

	public InstancedLODModel(Map<LevelOfDetail, Model> models, int numberOfInstances) {
		this.models = models;
		this.numberOfInstances = numberOfInstances;
		for (Model m : models.values()) {
			m.setIsInstanced(true);
		}
	}

	// Generates a model matrix for each instance of the model and assigns the same matrix to each level of detail
	// for the instance
	public void generateModelMatrices(Supplier<Matrix4f> generator) {
		float[] instancedModels = new float[numberOfInstances * 16];
		for (int i = 0; i < numberOfInstances; i++) {
			Matrix4f model = generator.get();
			for (int j = 0; j < 16; j++) {
				float[] mat = new float[16];
				model.get(mat);
				instancedModels[i * 16 + j] = mat[j];
			}
		}

		for (Model m : models.values()) {
			for (Mesh mesh : m.getMeshes()) {
				VertexBuffer instanceModel = new VertexBuffer(numberOfInstances, VertexAttribute.INSTANCE_MODEL);
				instanceModel.setVertexData(instancedModels.clone());
				mesh.getVertexArray().bindVertexBuffer(instanceModel);
				mesh.getVertexArray().setInstanced(true);
			}
		}

	}

	public void render(LevelOfDetail levelOfDetail, boolean renderForShadows) {
		if (numberOfInstances == 0) {
			return;
		}
		Model lodModel = models.get(levelOfDetail);
		if (lodModel == null) {
			switch (levelOfDetail) {
				case HIGH -> lodModel = models.get(LevelOfDetail.LOW);
				case LOW -> lodModel = models.get(LevelOfDetail.HIGH);
				default -> throw new RuntimeException("Unhandled LOD: " + levelOfDetail);
			}
		}
		for (Mesh mesh : lodModel.getMeshes()) {
			mesh.render(numberOfInstances, renderForShadows);
		}
	}
}
