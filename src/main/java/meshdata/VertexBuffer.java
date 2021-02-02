package meshdata;

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
