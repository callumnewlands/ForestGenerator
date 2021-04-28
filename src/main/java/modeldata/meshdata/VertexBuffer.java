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

import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import org.lwjgl.system.MemoryUtil;

/**
 * Represents a Vertex Buffer Object (VBO)
 * A VBO stores vertex data on the GPU
 */
public class VertexBuffer {
	private static final int SIZE_OF_FLOAT_BYTES = 4;

	private final List<VertexAttribute> attributes;
	private FloatBuffer dataBuffer;
	// Distance between the same attribute for consecutive vertices (= vertex size in bytes)
	private final int stride;
	private final int numberOfVertices;
	private final int handle;

	public VertexBuffer(final int numberOfVertices, final List<VertexAttribute> attributes) {
		this.attributes = attributes;
		this.numberOfVertices = numberOfVertices;
		final int floatComponentsPerVertex = getFloatComponentsPerVertex(attributes);
		this.stride = floatComponentsPerVertex * SIZE_OF_FLOAT_BYTES;
		this.dataBuffer = MemoryUtil.memAllocFloat(numberOfVertices * floatComponentsPerVertex);
		this.handle = glGenBuffers();
	}

	private static int getFloatComponentsPerVertex(final List<VertexAttribute> attributes) {
		return attributes.stream().mapToInt(VertexAttribute::getNumberOfFloatComponents).sum();
	}

	public void setVertexData(final float[] vertexData) {
		dataBuffer.put(vertexData).flip();
	}

	public void bind() {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferData(GL_ARRAY_BUFFER, dataBuffer, GL_STATIC_DRAW);

		int offset = 0;
		for (VertexAttribute attribute : attributes) {
			glVertexAttribPointer(
					attribute.getLocation(),
					attribute.getNumberOfFloatComponents(),
					GL_FLOAT,
					false,
					stride,
					offset);
			glEnableVertexAttribArray(attribute.getLocation());
			glVertexAttribDivisor(
					attribute.getLocation(),
					attribute.getDivisor());
			offset += attribute.getNumberOfFloatComponents() * SIZE_OF_FLOAT_BYTES;
		}

		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	public void freeBufferData() {
		if (dataBuffer != null) {
			MemoryUtil.memFree(dataBuffer);
		}
	}
}
