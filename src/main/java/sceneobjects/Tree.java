package sceneobjects;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static rendering.ShaderPrograms.billboardShaderProgram;
import static rendering.ShaderPrograms.leafShaderProgram;
import static rendering.ShaderPrograms.textureShader;

import generation.TreeLSystems;
import generation.TreePool;
import generation.TurtleInterpreter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lsystems.modules.Module;
import lsystems.modules.ParametricValueModule;
import modeldata.LODModel;
import modeldata.LODModelBuilder;
import modeldata.SingleModel;
import modeldata.meshdata.Mesh;
import modeldata.meshdata.Vertex;
import modeldata.meshdata.VertexAttribute;
import org.apache.commons.lang3.NotImplementedException;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import params.ParameterLoader;
import params.Parameters;
import params.TreeTypes;
import rendering.LevelOfDetail;
import rendering.Textures;
import utils.MeshUtils;
import utils.VectorUtils;

public class Tree {

	private static final Parameters parameters = ParameterLoader.getParameters();
	private final static Vector3f up = new Vector3f(0f, 1f, 0f);
	private final static Vector3f out = new Vector3f(0f, 0f, 1f);
	public final static Mesh leaf = new Mesh(
			List.of(
					new Vertex(new Vector3f(0f, 0f, -0.5f), up, out, new Vector2f(0, 0)),
					new Vertex(new Vector3f(1f, 0f, -0.5f), up, out, new Vector2f(0, 1)),
					new Vertex(new Vector3f(1f, 0f, 0.5f), up, out, new Vector2f(1, 1)),
					new Vertex(new Vector3f(0f, 0f, 0.5f), up, out, new Vector2f(1, 0))
			),
			new int[] {0, 1, 3, 1, 2, 3},
			List.of(
					VertexAttribute.POSITION,
					VertexAttribute.NORMAL,
					VertexAttribute.TANGENT,
					VertexAttribute.TEXTURE)
	);
	private final int typeIndex;
	private LODModel model;
	@Getter
	private float canopyXZRadius = 0;
	@Getter
	private float canopyYRadius = 0;
	@Getter
	private float trunkRadius = 0;
	@Getter
	private Vector3f canopyCentre;

	public Tree(int typeIndex) {
		this.typeIndex = typeIndex;
		Map<LevelOfDetail, List<Mesh>> lodMeshes = getMeshes();

		LODModelBuilder modelBuilder = new LODModelBuilder();
		// For each LOD, construct the mesh representation
		for (LevelOfDetail lod : lodMeshes.keySet()) {
			List<Mesh> meshes = lodMeshes.get(lod);
			modelBuilder.withLODModel(lod, new SingleModel(meshes));
		}
		model = modelBuilder.build();
	}

	public void setModelMatrix(Matrix4f modelMat) {
		this.model.setModelMatrix(modelMat);
	}

	public void render(LevelOfDetail levelOfDetail, boolean renderForShadows) {
		model.render(levelOfDetail, renderForShadows);
	}

	Map<LevelOfDetail, List<Mesh>> getMeshes() {

		int lowLODEdges = 2; //  TODO param
		int lowLODLeafMerges = 1; //  TODO param

		Parameters.SceneObjects.Tree params = parameters.sceneObjects.trees.get(typeIndex);
		int numEdges = params.numSides;
		TurtleInterpreter turtleInterpreter = new TurtleInterpreter(numEdges);
		turtleInterpreter.setSubModels(List.of(MeshUtils.transform(leaf,
				new Matrix4f().scale(params.leafYScale / params.scale, 1, params.leafXScale / params.scale))));
		TurtleInterpreter lowLODInterpreter = new TurtleInterpreter(lowLODEdges);
		lowLODInterpreter.setSubModels(List.of(MeshUtils.transform(leaf,
				new Matrix4f().scale(
						params.leafYScale * (1 + 0.1f * lowLODLeafMerges) / params.scale,
						1,
						params.leafXScale * (2 * lowLODLeafMerges) / params.scale))));
		Random r = ParameterLoader.getParameters().random.generator;
		int minI = params.minIterations;
		int maxI = params.maxIterations;
		List<Module> instructions;
		if (params instanceof TreeTypes.BranchingTree) {
			turtleInterpreter.setIgnored(List.of('A'));
			lowLODInterpreter.setIgnored(List.of('A'));
			instructions = TreeLSystems.branching(typeIndex)
					.performDerivations(r.nextInt(maxI - minI) + minI)
					.stream()
					.map(m -> m.getName() == 'A' ? new ParametricValueModule('~', 0f) : m)
					.collect(Collectors.toList());
		} else if (params instanceof TreeTypes.MonopodialTree) {
			turtleInterpreter.setIgnored(List.of('A', 'B'));
			lowLODInterpreter.setIgnored(List.of('A', 'B'));
			instructions = TreeLSystems.monopodial(typeIndex)
					.performDerivations(r.nextInt(maxI - minI) + minI);
		} else {
			throw new NotImplementedException();
		}
		turtleInterpreter.interpretInstructions(instructions);
		lowLODInterpreter.interpretInstructions(instructions);

		Textures.TreeTextures treeTextures = Textures.treeTextures.get(typeIndex);
		Mesh branches = turtleInterpreter.getMesh();
		branches.addTexture("diffuseTexture", treeTextures.bark);
		branches.addTexture("normalTexture", treeTextures.barkNormal);
		branches.setShaderProgram(textureShader);

		findMaxRadii(branches);

		Mesh canopy = turtleInterpreter.getCombinedSubModelMeshes().get(0);
		canopy.addTexture("leafFront", treeTextures.leafFront);
		canopy.addTexture("leafFrontTranslucency", treeTextures.leafFrontT);
		canopy.addTexture("leafFrontNorm", treeTextures.leafFrontNorm);
		canopy.addTexture("leafFrontHalfLife", treeTextures.leafFrontHL);
		canopy.addTexture("leafBack", treeTextures.leafBack);
		canopy.addTexture("leafBackTranslucency", treeTextures.leafBackT);
		canopy.addTexture("leafBackNorm", treeTextures.leafBackNorm);
		canopy.addTexture("leafBackHalfLife", treeTextures.leafBackHL);
		canopy.setShaderProgram(leafShaderProgram);
		canopy.setColourFilter(params.leafColourFilter);

		findCanopyMask(canopy);

		// Uses leaf geometry to construct billboard
//		Mesh board = MeshUtils.transform(Tree.leaf, new Matrix4f()
//				.scale(1f, 10f / params.scale, 1f / params.scale)
//				.rotate((float) Math.PI / 2, out));
		Mesh board = lowLODInterpreter.getMesh();
		board.addTexture("diffuseTexture", treeTextures.bark);
		board.addTexture("normalTexture", treeTextures.barkNormal);
		board.setShaderProgram(billboardShaderProgram);

		for (int i = 0; i < lowLODLeafMerges; i++) {
			lowLODInterpreter.reduceSubModelCount();
		}
		Mesh LODCanopy = lowLODInterpreter.getCombinedSubModelMeshes().get(0);
		LODCanopy.addTexture("leafFront", treeTextures.leafFront);
		LODCanopy.addTexture("leafFrontTranslucency", treeTextures.leafFrontT);
		LODCanopy.addTexture("leafFrontNorm", treeTextures.leafFrontNorm);
		LODCanopy.addTexture("leafFrontHalfLife", treeTextures.leafFrontHL);
		LODCanopy.addTexture("leafBack", treeTextures.leafBack);
		LODCanopy.addTexture("leafBackTranslucency", treeTextures.leafBackT);
		LODCanopy.addTexture("leafBackNorm", treeTextures.leafBackNorm);
		LODCanopy.addTexture("leafBackHalfLife", treeTextures.leafBackHL);
		LODCanopy.setShaderProgram(leafShaderProgram);
		LODCanopy.setColourFilter(params.leafColourFilter);
//		List<Mesh> billboard = List.of(
//				new Mesh(board),
//				MeshUtils.transform(board, new Matrix4f().rotate((float) Math.PI / 2, up)),
//				LODCanopy
//		);

		return Map.of(
				LevelOfDetail.HIGH, List.of(new Mesh(branches), new Mesh(canopy)),
				LevelOfDetail.LOW, List.of(new Mesh(board), new Mesh(LODCanopy)));
//		return Map.of(
//				LevelOfDetail.HIGH, List.of(new Mesh(branches)),
//				LevelOfDetail.LOW, List.of(new Mesh(board)));
	}

	private void findMaxRadii(Mesh branches) {
		List<Vector3f> positions = branches.getVertices().stream().map(Vertex::getPosition).collect(Collectors.toList());
		Parameters.SceneObjects.Tree params = parameters.sceneObjects.trees.get(typeIndex);
		for (int i = 0; i < 4 * params.numSides; i++) {
			Vector3f position = positions.get(i);
			float len = (new Vector2f(position.x, position.z)).length();
			if (len > trunkRadius) {
				trunkRadius = len;
			}
		}
		for (Vector3f position : positions) {
			float len = (new Vector2f(position.x, position.z)).length();
			if (len > canopyXZRadius) {
				canopyXZRadius = len;
			}
		}
	}

	private void findCanopyMask(Mesh canopy) {
		List<Vertex> vertices = canopy.getVertices();
		List<Vector3f> positions = vertices.stream().map(Vertex::getPosition).collect(Collectors.toList());
		canopyCentre = positions.stream()
				.reduce(VectorUtils::add)
				.map(p -> p.div(vertices.size()))
				.orElse(new Vector3f());
		Vector3f topPoint = positions.stream().max(Comparator.comparingDouble(p -> p.y)).orElse(new Vector3f());
		Vector3f bottomPoint = positions.stream().min(Comparator.comparingDouble(p -> p.y)).orElse(new Vector3f());
		float canopyHeight = Math.abs(topPoint.y - bottomPoint.y);
		canopyYRadius = canopyHeight / 2;
		canopyCentre.y = (topPoint.y + bottomPoint.y) / 2;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class Reference {
		private int typeIndex;
		private int treePoolIndex;
		private Vector3f position;
		private Matrix4f model;

		public void render(LevelOfDetail levelOfDetail, boolean renderForShadows) {
			TreePool treePool = TreePool.getTreePool();
			treePool.renderTreeWithModel(typeIndex, treePoolIndex, model, levelOfDetail, renderForShadows);
		}

	}
}
