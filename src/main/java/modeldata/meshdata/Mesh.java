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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static rendering.ShaderPrograms.shadowsShader;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import params.Parameters;
import rendering.ShaderProgram;

@Getter
@Setter
public class Mesh {

	protected Map<String, Texture> textures = new HashMap<>();
	protected VertexArray vertexArray;
	private List<Vertex> vertices;
	private int[] indices;
	private List<VertexAttribute> vertexAttributes;
	private Matrix4f model = new Matrix4f().identity();
	private Parameters.ColourFilter colourFilter;
	private boolean isInstanced;
	private boolean isLeaf;
	@Accessors(fluent = true)
	private boolean hasNormalMap;
	@Accessors(fluent = true)
	private boolean hasTranslucencyMap;
	@Accessors(fluent = true)
	private boolean hasSpecularMap;
	@Accessors(fluent = true)
	private boolean hasHalfLifeBasisMap;
	private ShaderProgram shaderProgram;

	public Mesh(List<Vertex> vertices, int[] indices, List<VertexAttribute> vertexAttributes) {
		this(vertices, indices, vertexAttributes, false, false, false, false, false, false);
	}

	public Mesh(List<Vertex> vertices,
				int[] indices,
				List<VertexAttribute> vertexAttributes,
				boolean isInstanced,
				boolean hasNormalMap,
				boolean hasTranslucencyMap,
				boolean hasSpecularMap,
				boolean hasHalfLifeBasisMap,
				boolean isLeaf) {
		this.vertices = vertices;
		this.indices = indices;
		this.vertexAttributes = vertexAttributes;
		this.isInstanced = isInstanced;
		this.hasNormalMap = hasNormalMap;
		this.hasTranslucencyMap = hasTranslucencyMap;
		this.hasSpecularMap = hasSpecularMap;
		this.hasHalfLifeBasisMap = hasHalfLifeBasisMap;
		this.isLeaf = isLeaf;
		this.colourFilter = null;
		this.vertexArray = createVAO();
	}

	public Mesh(Mesh mesh) {
		this(mesh, mesh.vertices);
	}

	public Mesh(Mesh mesh, boolean isInstanced) {
		this(mesh, mesh.vertices, isInstanced);
	}

	public Mesh(Mesh mesh, List<Vertex> transformedVertices) {
		this(mesh, transformedVertices, mesh.isInstanced);
	}

	public Mesh(Mesh mesh, List<Vertex> transformedVertices, boolean isInstanced) {
		this.vertices = transformedVertices;
		this.indices = mesh.indices.clone();
		this.vertexAttributes = new ArrayList<>(mesh.vertexAttributes);
		this.textures = new HashMap<>(mesh.textures);
		this.model = new Matrix4f(mesh.model);
		this.shaderProgram = mesh.shaderProgram;
		this.isInstanced = isInstanced;
		this.hasNormalMap = mesh.hasNormalMap;
		this.hasTranslucencyMap = mesh.hasTranslucencyMap;
		this.hasSpecularMap = mesh.hasSpecularMap;
		this.hasHalfLifeBasisMap = mesh.hasHalfLifeBasisMap;
		this.isLeaf = mesh.isLeaf;
		this.vertexArray = createVAO();
		this.colourFilter = mesh.colourFilter != null ? new Parameters.ColourFilter(mesh.colourFilter) : null;
	}

	public void addTexture(String uniform, Texture texture) {
		if (texture == null) {
			return;
		}
		switch (uniform) {
			case "normalTexture", "leafFrontNorm", "leafBackNorm" -> this.hasNormalMap = true;
			case "specularTexture" -> this.hasSpecularMap = true;
			case "leafFrontTranslucency", "leafBackTranslucency" -> this.hasTranslucencyMap = true;
			case "leafFrontHalfLife", "leafBackHalfLife" -> this.hasHalfLifeBasisMap = true;
		}
		textures.put(uniform, texture);
	}

	private VertexArray createVAO() {
		float[] data = ArrayUtils.toPrimitive(vertices.stream().flatMap(vd -> {
			List<Float> vertexData = new ArrayList<>();
			for (VertexAttribute attribute : vertexAttributes) {
				switch (attribute.getName()) {
					case "position" -> {
						Vector3f v = vd.getPosition();
						vertexData.addAll(List.of(v.x, v.y, v.z));
					}
					case "normal" -> {
						Vector3f n = vd.getNormal();
						vertexData.addAll(List.of(n.x, n.y, n.z));
					}
					case "tangent" -> {
						Vector3f t = vd.getTangent();
						vertexData.addAll(List.of(t.x, t.y, t.z));
					}
					case "texCoord" -> {
						Vector2f t = vd.getTexCoord();
						vertexData.addAll(List.of(t.x, t.y));
					}
					default -> throw new RuntimeException("Unknown attribute: " + attribute.getName());
				}
			}
			return vertexData.stream();
		}).toArray(Float[]::new));

		return new VertexArray(data, vertices.size(), indices, vertexAttributes);

	}

	private void bindForRender() {
		shaderProgram.use();
		if (!isInstanced) {
			shaderProgram.setUniform("model", model);
		}
		shaderProgram.setUniform("isInstanced", isInstanced);
		shaderProgram.setUniform("hasNormalMap", hasNormalMap);
		shaderProgram.setUniform("hasTranslucencyMap", hasTranslucencyMap);
		shaderProgram.setUniform("hasHalfLifeBasisMap", hasHalfLifeBasisMap);
		shaderProgram.setUniform("hasSpecularMap", hasSpecularMap);
		for (Map.Entry<String, Texture> texture : textures.entrySet()) {
			if (texture.getKey().equals("diffuseTexture")) {
				shaderProgram.setUniform("modelColour", texture.getValue().getColour());
			}
			shaderProgram.setUniform(texture.getKey(), texture.getValue().getUnitID());
			texture.getValue().bind();
		}
		if (colourFilter != null) {
			shaderProgram.setUniform("colourFilter", colourFilter.colour);
			shaderProgram.setUniform("mixFactor", colourFilter.mixFactor);
			shaderProgram.setUniform("expMix", colourFilter.expMix);
		} else {
			shaderProgram.setUniform("mixFactor", 0.0f);
		}
	}

	private void unbindFromRender() {
		for (Texture texture : textures.values()) {
			texture.unbind();
		}
	}

	public void render() {
		render(false);
	}

	public void render(boolean renderForShadows) {
		ShaderProgram prevShader = null;
		if (renderForShadows) {
			prevShader = this.shaderProgram;
			this.shaderProgram = shadowsShader;
			shadowsShader.setUniform("isLeaf", isLeaf);
		}
		bindForRender();
		draw();
		unbindFromRender();
		if (renderForShadows) {
			this.shaderProgram = prevShader;
		}
	}

	public void render(int numberOfInstances) {
		render(numberOfInstances);
	}

	public void render(int numberOfInstances, boolean renderForShadows) {
		ShaderProgram prevShader = null;
		if (renderForShadows) {
			prevShader = this.shaderProgram;
			this.shaderProgram = shadowsShader;
			shadowsShader.setUniform("isLeaf", isLeaf);
		}
		bindForRender();
		draw(numberOfInstances);
		unbindFromRender();
		if (renderForShadows) {
			this.shaderProgram = prevShader;
		}
	}

	protected void draw() {
		vertexArray.draw();
	}

	protected void draw(int numberOfInstances) {
		if (isInstanced) {
			vertexArray.draw(numberOfInstances);
		} else {
			throw new RuntimeException("Attempting to draw single mesh as instanced mesh");
		}
	}

}
