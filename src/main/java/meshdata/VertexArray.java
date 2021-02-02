package meshdata;

import java.util.List;

import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * Represents a Vertex Array Object (VAO)
 * A VAO stores references to VBOs and EBOs
 */
public class VertexArray {

	private final int handle;
	private final int numberOfVertices;
	private final int numberOfIndices;

	public VertexArray(final float[] vertices, final int numberOfVertices, final int[] indices, final List<VertexAttribute> attributes) {

		this.numberOfVertices = numberOfVertices;
		VertexBuffer vertexBuffer = new VertexBuffer(numberOfVertices, attributes);
		vertexBuffer.setVertexData(vertices);

		handle = glGenVertexArrays();
		glBindVertexArray(handle);

		vertexBuffer.bind();
		vertexBuffer.freeBufferData();

		this.numberOfIndices = indices.length;
		ElementBuffer elementBuffer = new ElementBuffer(indices.length);
		elementBuffer.setIndexData(indices);
		elementBuffer.bind();

		glBindVertexArray(0);
	}

	public void draw() {
		glBindVertexArray(handle);
		glDrawElements(GL_TRIANGLES, numberOfIndices, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);
	}
}