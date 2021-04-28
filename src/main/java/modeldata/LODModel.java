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
