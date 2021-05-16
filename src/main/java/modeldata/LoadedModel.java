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

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.assimp.Assimp.aiGetMaterialTexture;
import static org.lwjgl.assimp.Assimp.aiImportFile;
import static org.lwjgl.assimp.Assimp.aiProcess_CalcTangentSpace;
import static org.lwjgl.assimp.Assimp.aiProcess_FixInfacingNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_FlipUVs;
import static org.lwjgl.assimp.Assimp.aiProcess_GenSmoothNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE;
import static org.lwjgl.assimp.Assimp.aiTextureType_HEIGHT;
import static org.lwjgl.assimp.Assimp.aiTextureType_NORMALS;
import static org.lwjgl.opengl.GL11C.GL_REPEAT;
import static org.lwjgl.opengl.GL11C.GL_RGBA;
import static org.lwjgl.opengl.GL21C.GL_SRGB_ALPHA;
import static rendering.ShaderPrograms.textureShader;

import lombok.Getter;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.Texture;
import modeldata.meshdata.Texture2D;
import modeldata.meshdata.Vertex;
import modeldata.meshdata.VertexAttribute;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import params.ParameterLoader;
import params.Parameters;
import rendering.ShaderProgram;

public class LoadedModel implements Model {

	static final Parameters parameters = ParameterLoader.getParameters();
	private final SingleModel model;
	private final Map<String, Texture> loadedTextures = new HashMap<>();
	private final String texturesDir;
	@Getter
	private float maskRadius;
	@Getter
	private float maskHeight;

	public LoadedModel(final String resourcePath, final String texturesDir) {
		this(resourcePath,
				texturesDir,
				aiProcess_JoinIdenticalVertices
						| aiProcess_CalcTangentSpace
						| aiProcess_GenSmoothNormals
						| aiProcess_Triangulate
						| aiProcess_FixInfacingNormals
						| aiProcess_FlipUVs
		);
	}

	public LoadedModel(final String resourcePath, final String texturesDir, final int flags) {
		this.texturesDir = texturesDir;

		String absPath = new File(parameters.resourcesRoot + resourcePath).getAbsolutePath();
		AIScene scene = aiImportFile(absPath, flags);
		if (scene == null || scene.mRootNode() == null) {
			throw new RuntimeException("Error loading model: " + absPath);
		}

		List<Map<String, Texture>> textures = loadTextures(scene);
		List<Mesh> meshes = loadMeshes(scene, textures);
		model = new SingleModel(meshes);
	}

	private static List<Vertex> listsToVertexArray(
			final List<Vector3f> positions,
			final List<Vector3f> normals,
			final List<Vector3f> tangents,
			final List<Vector2f> textureCoordinates) {

		if (positions.size() != normals.size() ||
				tangents.size() != 0 && positions.size() != tangents.size() ||
				textureCoordinates.size() != 0 && positions.size() != textureCoordinates.size()) {
			throw new RuntimeException("Unequal object data");
		}

		List<Vertex> vertices = new ArrayList<>();
		for (int i = 0; i < positions.size(); i++) {
			if (tangents.size() != 0 && textureCoordinates.size() != 0) {
				vertices.add(new Vertex(positions.get(i), normals.get(i), tangents.get(i), textureCoordinates.get(i)));
			} else if (textureCoordinates.size() != 0) {
				vertices.add(new Vertex(positions.get(i), normals.get(i), textureCoordinates.get(i)));
			} else {
				vertices.add(new Vertex(positions.get(i), normals.get(i)));
			}
		}

		return vertices;
	}

	private static List<VertexAttribute> getAttributes(
			final List<Vector3f> positions,
			final List<Vector3f> normals,
			final List<Vector3f> tangents,
			final List<Vector2f> textureCoordinates) {
		List<VertexAttribute> attributes = new ArrayList<>();
		if (positions.size() > 0) {
			attributes.add(VertexAttribute.POSITION);
		}
		if (normals.size() > 0) {
			attributes.add(VertexAttribute.NORMAL);
		}
		if (tangents.size() > 0) {
			attributes.add(VertexAttribute.TANGENT);
		}
		if (textureCoordinates.size() > 0) {
			attributes.add(VertexAttribute.TEXTURE);
		}
		return attributes;
	}


	private List<Map<String, Texture>> loadTextures(final AIScene scene) {
		List<Map<String, Texture>> textures = new ArrayList<>();
		int noOfMaterials = scene.mNumMaterials();
		PointerBuffer aiMaterials = scene.mMaterials();
		for (int i = 0; i < noOfMaterials; i++) {
			AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
			textures.add(processMaterial(aiMaterial));
		}
		return textures;
	}

	// Currently only loads 1st texture of each mesh
	private Map<String, Texture> processMaterial(final AIMaterial material) {

		AIString texturePathPointer = AIString.calloc();
		aiGetMaterialTexture(material, aiTextureType_DIFFUSE, 0, texturePathPointer,
				(IntBuffer) null, null, null, null, null, null);
		String texturePath = texturePathPointer.dataString();
		Texture diffuseTexture = null;
		if (texturePath != null && texturePath.length() > 0) {
			if (loadedTextures.containsKey(texturePath)) {
				diffuseTexture = loadedTextures.get(texturePath);
			} else {
				diffuseTexture = new Texture2D(texturesDir + "/" + texturePath, new Vector3f(), 6,
						GL_SRGB_ALPHA, GL_REPEAT);
				loadedTextures.put(texturePath, diffuseTexture);
			}
		}

		aiGetMaterialTexture(material, aiTextureType_NORMALS, 0, texturePathPointer,
				(IntBuffer) null, null, null, null, null, null);
		texturePath = texturePathPointer.dataString();
		Texture normalTexture = null;
		if (texturePath == null || texturePath.length() == 0) {
			// Try aiTextureType_HEIGHT
			aiGetMaterialTexture(material, aiTextureType_HEIGHT, 0, texturePathPointer,
					(IntBuffer) null, null, null, null, null, null);
			texturePath = texturePathPointer.dataString();
		}
		if (texturePath != null && texturePath.length() > 0) {
			if (loadedTextures.containsKey(texturePath)) {
				normalTexture = loadedTextures.get(texturePath);
			} else {
				normalTexture = new Texture2D(texturesDir + "/" + texturePath, new Vector3f(), 7,
						GL_RGBA, GL_REPEAT);
				loadedTextures.put(texturePath, diffuseTexture);
			}
		}

		Map<String, Texture> materialTextures = new HashMap<>();
		if (diffuseTexture != null) {
			materialTextures.put("diffuseTexture", diffuseTexture);
		}
		if (normalTexture != null) {
			materialTextures.put("normalTexture", normalTexture);
		}

		return materialTextures;
	}


	private List<Mesh> loadMeshes(final AIScene scene, List<Map<String, Texture>> textures) {
		List<Mesh> meshes = new ArrayList<>();
		final int noOfMeshes = scene.mNumMeshes();
		PointerBuffer aiMeshes = scene.mMeshes();
		for (int i = 0; i < noOfMeshes; i++) {
			AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
			meshes.add(processMesh(aiMesh, textures));
		}
		return meshes;
	}

	private Mesh processMesh(final AIMesh mesh, List<Map<String, Texture>> textures) {
		List<Vector3f> positions = processPositions(mesh);
		List<Vector3f> normals = processNormals(mesh);
		List<Vector3f> tangents = processTangents(mesh);
		List<Vector2f> textureCoordinates = processTexCoords(mesh);
		List<Vertex> vertices = listsToVertexArray(positions, normals, tangents, textureCoordinates);

		setMask(positions);

		List<Integer> indices = processIndices(mesh);
		int[] indexData = new int[indices.size()];
		for (int i = 0; i < indices.size(); i++) {
			indexData[i] = indices.get(i);
		}

		List<VertexAttribute> attributes = getAttributes(positions, normals, tangents, textureCoordinates);

		Mesh meshData = new Mesh(vertices, indexData, attributes);
		int materialIndex = mesh.mMaterialIndex();
		if (materialIndex >= 0) {
			Map<String, Texture> meshTextures = textures.get(materialIndex);
			for (Map.Entry<String, Texture> texture : meshTextures.entrySet()) {
				meshData.addTexture(texture.getKey(), texture.getValue());
			}
		}
		meshData.setShaderProgram(textureShader);
		return meshData;

	}

	private void setMask(List<Vector3f> positions) {
		float maxRadius = 0;
		for (Vector3f position : positions) {
			float len = (new Vector2f(position.x, position.z)).length();
			if (len > maxRadius) {
				maxRadius = len;
			}
		}
		maskRadius = maxRadius;
		Vector3f topPoint = positions.stream().max(Comparator.comparingDouble(p -> p.y)).orElse(new Vector3f());
		Vector3f bottomPoint = positions.stream().min(Comparator.comparingDouble(p -> p.y)).orElse(new Vector3f());
		maskHeight = Math.abs(topPoint.y - bottomPoint.y);
	}

	private List<Vector3f> processPositions(final AIMesh mesh) {
		List<Vector3f> positions = new ArrayList<>();
		AIVector3D.Buffer aiVertices = mesh.mVertices();
		while (aiVertices.remaining() > 0) {
			positions.add(aiVectorToJoml(aiVertices.get()));
		}
		return positions;
	}

	private List<Vector3f> processNormals(final AIMesh mesh) {
		List<Vector3f> normals = new ArrayList<>();
		AIVector3D.Buffer aiNormals = mesh.mNormals();
		if (aiNormals == null) {
			return normals;
		}
		while (aiNormals.remaining() > 0) {
			normals.add(aiVectorToJoml(aiNormals.get()).negate());
		}
		return normals;
	}

	private List<Vector3f> processTangents(final AIMesh mesh) {
		List<Vector3f> tangents = new ArrayList<>();
		AIVector3D.Buffer aiTangents = mesh.mTangents();
		if (aiTangents == null) {
			return tangents;
		}
		while (aiTangents.remaining() > 0) {
			tangents.add(aiVectorToJoml(aiTangents.get()));
		}
		return tangents;
	}


	private List<Vector2f> processTexCoords(final AIMesh mesh) {
		List<Vector2f> textureCoordinates = new ArrayList<>();
		if (mesh.mTextureCoords(0) == null) {
			return textureCoordinates;
		}
		AIVector3D.Buffer aiTexCoords = mesh.mTextureCoords(0);
		while (aiTexCoords.remaining() > 0) {
			AIVector3D texCoord = aiTexCoords.get();
			textureCoordinates.add(new Vector2f(texCoord.x(), texCoord.y()));
		}
		return textureCoordinates;
	}

	private List<Integer> processIndices(final AIMesh mesh) {
		List<Integer> indices = new ArrayList<>();
		int noOfFaces = mesh.mNumFaces();
		AIFace.Buffer aiFaces = mesh.mFaces();
		for (int i = 0; i < noOfFaces; i++) {
			AIFace aiFace = aiFaces.get(i);
			IntBuffer buffer = aiFace.mIndices();
			while (buffer.remaining() > 0) {
				indices.add(buffer.get());
			}
		}
		return indices;
	}

	private Vector3f aiVectorToJoml(AIVector3D vector3D) {
		return new Vector3f(vector3D.x(), vector3D.y(), vector3D.z());
	}

	public void render() {
		model.render();
	}

	public void addTextures(String uniform, List<? extends Texture> textures) {
		model.addTextures(uniform, textures);
	}

	@Override
	public void setModelMatrix(Matrix4f model) {
		this.model.setModelMatrix(model);
	}

	@Override
	public void setShaderProgram(ShaderProgram shaderProgram) {
		model.setShaderProgram(shaderProgram);
	}

	@Override
	public List<Mesh> getMeshes() {
		return model.getMeshes();
	}

	@Override
	public void setIsInstanced(boolean value) {
		for (Mesh mesh : model.getMeshes()) {
			mesh.setInstanced(value);
		}
	}
}

