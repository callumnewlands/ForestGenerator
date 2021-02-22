package modeldata;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.lwjgl.assimp.Assimp.aiImportFile;
import static org.lwjgl.assimp.Assimp.aiProcess_CalcTangentSpace;
import static org.lwjgl.assimp.Assimp.aiProcess_FixInfacingNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_FlipUVs;
import static org.lwjgl.assimp.Assimp.aiProcess_GenSmoothNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;

import modeldata.meshdata.Mesh;
import modeldata.meshdata.Texture;
import modeldata.meshdata.Vertex;
import modeldata.meshdata.VertexAttribute;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import rendering.ShaderProgram;

public class LoadedModel implements Model {

	private final SingleModel model;

	public LoadedModel(final String resourcePath) {
		this(resourcePath,
//				texturesDir,
				aiProcess_JoinIdenticalVertices
						| aiProcess_CalcTangentSpace
						| aiProcess_GenSmoothNormals
						| aiProcess_Triangulate
						| aiProcess_FixInfacingNormals
						| aiProcess_FlipUVs);
	}

	public LoadedModel(final String resourcePath, final int flags) {
//		this.texturesDirectory = texturesDir;

		AIScene scene = aiImportFile(resourcePath, flags);
		if (scene == null || scene.mRootNode() == null) {
			throw new RuntimeException("Error loading model");
		}

//		loadMaterials(scene);
		List<Mesh> meshes = loadMeshes(scene);
		model = new SingleModel(meshes);
	}

	private static Vector4f aiColor4DToVector4f(final AIColor4D colour) {
		return new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
	}

	private static float[] listsToFloatArray(
			final List<Float> positions,
			final List<Float> normals,
			final List<Float> textureCoordinates) {
		int size = positions.size() + normals.size() + textureCoordinates.size();
		float[] array = new float[size];

		int vertexCount = 0;
		int arrayCount = 0;
		int posSize = VertexAttribute.POSITION.getNumberOfFloatComponents();
		int normSize = VertexAttribute.NORMAL.getNumberOfFloatComponents();
		int texSize = VertexAttribute.TEXTURE.getNumberOfFloatComponents();
		int numberOfVertices = positions.size() / posSize;

		while (vertexCount < numberOfVertices) {
			array[arrayCount] = positions.get(vertexCount * posSize);
			arrayCount++;
			array[arrayCount] = positions.get(vertexCount * posSize + 1);
			arrayCount++;
			array[arrayCount] = positions.get(vertexCount * posSize + 2);
			arrayCount++;
			if (normals.size() > 0) {
				array[arrayCount] = normals.get(vertexCount * normSize);
				arrayCount++;
				array[arrayCount] = normals.get(vertexCount * normSize + 1);
				arrayCount++;
				array[arrayCount] = normals.get(vertexCount * normSize + 2);
				arrayCount++;
			}
			if (textureCoordinates.size() > 0) {
				array[arrayCount] = textureCoordinates.get(vertexCount * texSize);
				arrayCount++;
				array[arrayCount] = textureCoordinates.get(vertexCount * texSize + 1);
				arrayCount++;
			}
			vertexCount++;
		}

		return array;
	}

	private static List<Vertex> listsToVertexArray(
			final List<Float> positions,
			final List<Float> normals,
			final List<Float> tangents,
			final List<Float> textureCoordinates) {

		int posSize = VertexAttribute.POSITION.getNumberOfFloatComponents();
		List<Vector3f> posVectors = IntStream.range(0, positions.size() / posSize)
				.mapToObj(n -> new Vector3f(positions.get(n * 3), positions.get(n * 3 + 1), positions.get(n * 3 + 2)))
				.collect(Collectors.toList());

		int normSize = VertexAttribute.NORMAL.getNumberOfFloatComponents();
		List<Vector3f> normVectors = IntStream.range(0, positions.size() / normSize)
				.mapToObj(n -> new Vector3f(normals.get(n * 3), normals.get(n * 3 + 1), normals.get(n * 3 + 2)))
				.collect(Collectors.toList());

		int tangSize = VertexAttribute.NORMAL.getNumberOfFloatComponents();
		List<Vector3f> tangVectors = IntStream.range(0, tangents.size() / tangSize)
				.mapToObj(n -> new Vector3f(tangents.get(n * 3), tangents.get(n * 3 + 1), tangents.get(n * 3 + 2)))
				.collect(Collectors.toList());

		List<Vector2f> texVectors = IntStream.range(0, positions.size() / 3)
				.mapToObj(n -> new Vector2f(normals.get(n * 2), normals.get(n * 2 + 1)))
				.collect(Collectors.toList());

		if (posVectors.size() != normVectors.size() ||
				tangVectors.size() != 0 && posVectors.size() != tangVectors.size() ||
				texVectors.size() != 0 && posVectors.size() != texVectors.size()) {
			throw new RuntimeException("Unequal object data");
		}

		List<Vertex> vertices = new ArrayList<>();
		for (int i = 0; i < posVectors.size(); i++) {
			if (texVectors.size() != 0 && tangVectors.size() != 0) {
				vertices.add(new Vertex(posVectors.get(i), normVectors.get(i), tangVectors.get(i), texVectors.get(i)));
			} else if (texVectors.size() != 0) {
				vertices.add(new Vertex(posVectors.get(i), normVectors.get(i), texVectors.get(i)));
			} else {
				vertices.add(new Vertex(posVectors.get(i), normVectors.get(i)));
			}
		}

		return vertices;
	}

	private static List<VertexAttribute> getAttributes(
			final List<Float> positions,
			final List<Float> normals,
			final List<Float> tangents,
			final List<Float> textureCoordinates) {
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

	// TODO materials
//	private void loadMaterials(final AIScene scene) {
//		int noOfMaterials = scene.mNumMaterials();
//		PointerBuffer aiMaterials = scene.mMaterials();
//		for (int i = 0; i < noOfMaterials; i++) {
//			AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
//			materials.add(processMaterial(aiMaterial));
//		}
//	}
//
//	private Material processMaterial(final AIMaterial material) {
////		material.mNumAllocated();
//		// Note: only loads (1st) *diffuse* texture of mesh atm.
//		// TODO load specular and multiple textures if needed
//
//		AIColor4D colour = AIColor4D.create();
//		Vector4f ambient = new Vector4f(1.0f);
//		Vector4f diffuse = new Vector4f(1.0f);
//		Vector4f specular = new Vector4f(1.0f);
//
//		if (aiGetMaterialColor(material, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, colour) == 0) {
//			ambient = aiColor4DToVector4f(colour);
//		}
//		if (aiGetMaterialColor(material, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, colour) == 0) {
//			diffuse = aiColor4DToVector4f(colour);
//		}
//		if (aiGetMaterialColor(material, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, colour) == 0) {
//			specular = aiColor4DToVector4f(colour);
//		}
//
//		AIString texturePathPointer = AIString.calloc();
//		aiGetMaterialTexture(material, aiTextureType_DIFFUSE, 0, texturePathPointer,
//				(IntBuffer) null, null, null, null, null, null);
//		String texturePath = texturePathPointer.dataString();
//		Texture diffuseTexture = null;
//		if (texturePath != null && texturePath.length() > 0) {
//			if (loadedTextures.containsKey(texturePath)) {
//				diffuseTexture = loadedTextures.get(texturePath);
//			} else {
//				diffuseTexture = new Texture(texturesDirectory + "/" + texturePath, diffuse,
//						GL_TEXTURE0);
//				loadedTextures.put(texturePath, diffuseTexture);
//			}
//		}
//		// TODO?
////		IntBuffer max = IntBuffer.allocate(1); //.put(1).flip()
////		FloatBuffer shininessBuffer = FloatBuffer.allocate(1);
////		if (aiGetMaterialFloatArray(material, AI_MATKEY_SHININESS,
////		aiTextureType_NONE, 0, shininessBuffer, max) == 0) {
////			shininess = shininessBuffer.get();
////		}
//		if (diffuseTexture != null) {
//			return new Material(diffuseTexture, specular, shaderProgram);
//		} else {
//			return new Material(ambient, diffuse, specular, shaderProgram);
//		}
//	}

	private List<Mesh> loadMeshes(final AIScene scene) {
		List<Mesh> meshes = new ArrayList<>();
		final int noOfMeshes = scene.mNumMeshes();
		PointerBuffer aiMeshes = scene.mMeshes();
		for (int i = 0; i < noOfMeshes; i++) {
			AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
			meshes.add(processMesh(aiMesh, scene));
		}
		return meshes;
	}

	private Mesh processMesh(final AIMesh mesh, final AIScene scene) {
		List<Float> positions = processPositions(mesh);
		List<Float> normals = processNormals(mesh);
		List<Float> tangents = processTangents(mesh);
		List<Float> textureCoordinates = processTexCoords(mesh);
		List<Vertex> vertices = listsToVertexArray(positions, normals, tangents, textureCoordinates);

		List<Integer> indices = processIndices(mesh);
		int[] indexData = new int[indices.size()];
		for (int i = 0; i < indices.size(); i++) {
			indexData[i] = indices.get(i);
		}

		List<VertexAttribute> attributes = getAttributes(positions, normals, tangents, textureCoordinates);

//		final int materialIndex = mesh.mMaterialIndex();
//		if (materialIndex >= 0) {
//			Material material = materials.get(materialIndex);
//			return new Mesh(vertexData, attributes, indexData, material, shaderProgram);
//		} else {
		return new Mesh(vertices, indexData, attributes);
//		}

	}

	private List<Float> processPositions(final AIMesh mesh) {
		List<Float> positions = new ArrayList<>();
		AIVector3D.Buffer aiVertices = mesh.mVertices();
		while (aiVertices.remaining() > 0) {
			AIVector3D aiVertex = aiVertices.get();
			positions.add(aiVertex.x());
			positions.add(aiVertex.y());
			positions.add(aiVertex.z());
		}
		return positions;
	}

	private List<Float> processNormals(final AIMesh mesh) {
		List<Float> normals = new ArrayList<>();
		AIVector3D.Buffer aiNormals = mesh.mNormals();
		if (aiNormals == null) {
			return normals;
		}
		while (aiNormals.remaining() > 0) {
			AIVector3D aiNormal = aiNormals.get();
			normals.add(aiNormal.x());
			normals.add(aiNormal.y());
			normals.add(aiNormal.z());
		}
		return normals;
	}

	private List<Float> processTangents(final AIMesh mesh) {
		List<Float> tangents = new ArrayList<>();
		AIVector3D.Buffer aiTangents = mesh.mTangents();
		if (aiTangents == null) {
			return tangents;
		}
		while (aiTangents.remaining() > 0) {
			AIVector3D aiTangent = aiTangents.get();
			tangents.add(aiTangent.x());
			tangents.add(aiTangent.y());
			tangents.add(aiTangent.z());
		}
		return tangents;
	}


	private List<Float> processTexCoords(final AIMesh mesh) {
		List<Float> textureCoordinates = new ArrayList<>();
		if (mesh.mTextureCoords(0) == null) {
			return textureCoordinates;
		}
		AIVector3D.Buffer aiTexCoords = mesh.mTextureCoords(0);
		while (aiTexCoords.remaining() > 0) {
			AIVector3D aiTexCoord = aiTexCoords.get();
			textureCoordinates.add(aiTexCoord.x());
			textureCoordinates.add(aiTexCoord.y());
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

	public void render(ShaderProgram shaderProgram) {
		model.render(shaderProgram);
	}

	public void addTextures(String uniform, List<? extends Texture> textures) {
		model.addTextures(uniform, textures);
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

