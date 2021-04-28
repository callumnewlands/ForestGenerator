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

package modeldata.meshdata;

import java.util.List;

import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

import lombok.Setter;

/**
 * Represents a Vertex Array Object (VAO)
 * A VAO stores references to VBOs and EBOs
 */
public class VertexArray {

	private final int handle;
	private final int numberOfVertices;
	private final int numberOfIndices;
	@Setter
	private boolean isInstanced;

	public VertexArray(final float[] vertices, final int numberOfVertices, final int[] indices, final List<VertexAttribute> attributes) {

		handle = glGenVertexArrays();

		this.numberOfVertices = numberOfVertices;
		VertexBuffer vertexBuffer = new VertexBuffer(numberOfVertices, attributes);
		vertexBuffer.setVertexData(vertices);
		bindVertexBuffer(vertexBuffer);

		glBindVertexArray(handle);
		this.numberOfIndices = indices.length;
		ElementBuffer elementBuffer = new ElementBuffer(indices.length);
		elementBuffer.setIndexData(indices);
		elementBuffer.bind();

		glBindVertexArray(0);

		isInstanced = attributes.contains(VertexAttribute.INSTANCE_MODEL.get(0));
	}

	public void bindVertexBuffer(VertexBuffer vertexBuffer) {
		glBindVertexArray(handle);

		vertexBuffer.bind();
		vertexBuffer.freeBufferData();
		glBindVertexArray(0);
	}

	public void draw() {
		glBindVertexArray(handle);
		if (isInstanced) {
			draw(1);
		} else {
			glDrawElements(GL_TRIANGLES, numberOfIndices, GL_UNSIGNED_INT, 0);
		}
		glBindVertexArray(0);
	}

	public void draw(int numberOfInstances) {
		glBindVertexArray(handle);
		if (isInstanced) {
			glDrawElementsInstanced(GL_TRIANGLES, numberOfIndices, GL_UNSIGNED_INT, 0, numberOfInstances);
		} else {
			throw new RuntimeException("draw() called with int parameter for non-instanced VAO");
		}
		glBindVertexArray(0);
	}
}