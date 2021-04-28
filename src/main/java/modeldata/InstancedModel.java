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

import java.util.List;
import java.util.function.Supplier;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.VertexAttribute;
import modeldata.meshdata.VertexBuffer;
import org.joml.Matrix4f;

public class InstancedModel extends SingleModel {
	private final int numberOfInstances;

	public InstancedModel(Mesh mesh, int numberOfInstances) {
		this(List.of(mesh), numberOfInstances);
	}

	public InstancedModel(List<Mesh> meshes, int numberOfInstances) {
		super(meshes);
		this.numberOfInstances = numberOfInstances;
		this.setIsInstanced(true);
	}

	// Generates a model matrix for each instance of the model and assigns the same matrix to each mesh in the instance
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

		for (Mesh mesh : meshes) {
			VertexBuffer instanceModel = new VertexBuffer(numberOfInstances, VertexAttribute.INSTANCE_MODEL);
			instanceModel.setVertexData(instancedModels);
			mesh.getVertexArray().bindVertexBuffer(instanceModel);
			mesh.getVertexArray().setInstanced(true);
		}
	}

	@Override
	public void render() {
		for (Mesh mesh : meshes) {
			mesh.render(numberOfInstances);
		}
	}
}
