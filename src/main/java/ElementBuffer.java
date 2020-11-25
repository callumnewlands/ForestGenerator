import java.nio.IntBuffer;


import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBufferData;

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