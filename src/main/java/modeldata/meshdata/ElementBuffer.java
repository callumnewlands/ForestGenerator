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

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBufferData;

import org.lwjgl.system.MemoryUtil;

/**
 * Represents and Element Buffer Object (EBO)
 * An EBO stores the indices to draw from a VBO
 */
public class ElementBuffer {

	private final int numberOfIndices;
	private final IntBuffer buffer;
	private final int handle;

	public ElementBuffer(final int numberOfIndices) {
		this.numberOfIndices = numberOfIndices;
		this.buffer = MemoryUtil.memAllocInt(numberOfIndices);
		this.handle = glGenBuffers();
	}

	public void setIndexData(final int[] indexData) {
		buffer.put(indexData).flip();
	}

	public void bind() {
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, handle);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
	}

}