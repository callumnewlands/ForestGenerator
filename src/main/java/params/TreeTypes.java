package params;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joml.Vector3f;

public class TreeTypes {

	@Setter
	public static class BranchingTree extends Parameters.SceneObjects.Tree {
		public List<Branching> branchings = List.of( // TODO randomise and convert to degrees
				new Branching(List.of((float) Math.PI), 0.3f),
				new Branching(List.of(1.6535f, 2.3148f), 0.7f)
		);

		public BranchingTree() {
			super();
			lSystemParamsLower = new HashMap<>(Map.of(
					"a", 15f,
					"lr", 1f,
					"vr", 1.732f,
					"e", 0.050f));
			lSystemParamsUpper = new HashMap<>(Map.of(
					"a", 30f,
					"lr", 1.5f,
					"vr", 1.932f,
					"e", 0.055f));
			barkTexture = new Parameters.Texture(
					"/textures/Bark_02_2K_Base_Color.png",
					"/textures/Bark_02_2K_Normal.png");
			leafTextures = new Parameters.SceneObjects.LeafTextures(
					"/textures/Leaf1/Leaf1_front.tga",
					"/textures/Leaf1/Leaf1_normals_front.tga",
					"/textures/Leaf1/Leaf1_front_t.tga",
					"/textures/Leaf1/Leaf1_halflife_front_t.tga",
					"/textures/Leaf1/Leaf1_back.tga",
					"/textures/Leaf1/Leaf1_normals_back.tga",
					"/textures/Leaf1/Leaf1_back_t.tga",
					"/textures/Leaf1/Leaf1_halflife_back_t.tga"
			);
			name = "Tree 1";
			numSides = 6;
			scale = 0.01f;
			leafXScale = 0.7f;
			leafYScale = 0.7f;
			minIterations = 7;
			maxIterations = 9;
			density = 0.8f;

		}

		@NoArgsConstructor
		@AllArgsConstructor
		@Setter
		public static class Branching {
			public List<Float> angles;
			public float prob;
		}
	}

	@Setter
	public static class MonopodialTree extends Parameters.SceneObjects.Tree {

		public boolean heightVaryingAngles = true;
		public boolean pineStyleBranches = false;

		public MonopodialTree() {
			super();
			lSystemParamsLower = new HashMap<>(Map.ofEntries(
					Map.entry("lB", 2f),  // Base length
					Map.entry("lS", 1.1f),  // Side branch length
					Map.entry("lSm", 0.5f),  // Min ratio for side branch length (of ls) and width (of ws)
					Map.entry("wB", 0.5f), // Base width
					Map.entry("wS", 0.3f), // Side branch width
					Map.entry("wS2", 0.4f), // 3rd level side branch width
					Map.entry("vr", 0.3f), // Width of start of side branch
					Map.entry("aB", 80), // Branch angle to trunk
					Map.entry("aS", 110), // Branch angle around trunk
					Map.entry("aS2", 100), // 3rd level side branch angle
					Map.entry("aS3", 80), // 3rd level side branch angle downwards
					Map.entry("aS4", -45), // 3rd level side branch angle downwards for 2nd part
					Map.entry("aS5", 0), // Initial angle of rotation around branch for 3rd level side branches
					Map.entry("lS2", 0.333f), // Distance ratio for gap between 3rd level branches (ignored for pine style branches)
					Map.entry("lS3", 0.25f), // Distance ratio for first part of 3rd level branches (ignored for pine style branches)
					Map.entry("lS4", 0.25f), // Distance ratio for second part of 3rd level branches (ignored for pine style branches)
					Map.entry("tH", 0.9f), // Threshold for switching expansion/contraction (1 = bottom, 0 = top)
					Map.entry("aU", 0), // Angle to curve the side branches downward
					Map.entry("nB", 8), // Number of side branches (and trunk height segments) per iteration
					Map.entry("nB2", 12),// Branching factor of side branches (ignored for pine style branches)
					Map.entry("l1", 0.2f), // Length of trunk sections between branches
					Map.entry("l2", 0f),    // Length of trunk sections after branches
					Map.entry("lr", 0.5f), // Offset of side branches from centre
					Map.entry("lr2", 1.0f), // Ratio of decrease of l2
					Map.entry("e", 0.001))); // Elasticity
			lSystemParamsUpper = new HashMap<>(lSystemParamsLower);
			barkTexture = new Parameters.Texture(
					"/textures/Bark_02_2K_Base_Color.png",
					"/textures/Bark_02_2K_Normal.png");
			leafTextures = new Parameters.SceneObjects.LeafTextures(
					"/textures/Leaf3/Leaf3_front.tga",
					"/textures/Leaf3/Leaf3_normals_front.tga",
					"/textures/Leaf3/Leaf3_front_t.tga",
					"/textures/Leaf3/Leaf3_halflife_front_t.tga",
					"/textures/Leaf3/Leaf3_back.tga",
					"/textures/Leaf3/Leaf3_normals_back.tga",
					"/textures/Leaf3/Leaf3_back_t.tga",
					"/textures/Leaf3/Leaf3_halflife_back_t.tga");
			name = "Monopodial Tree";
			numSides = 6;
			scale = 0.75f;
			leafXScale = 0.17f;
			leafYScale = 0.17f;
			minIterations = 7;
			maxIterations = 9;
			density = 0.7f;
		}
	}

	public static class AspenTree extends MonopodialTree {
		public AspenTree() {
			super();
			lSystemParamsLower = new HashMap<>(Map.ofEntries(
					Map.entry("lB", 4f),    // Base length
					Map.entry("lS", 0.5f),    // Side branch length
					Map.entry("lSm", 0.2f),  // Min ratio for side branch length (of ls) and width (of ws)
					Map.entry("wB", 0.7f),    // Base width
					Map.entry("wS", 0.35f),    // Side branch width
					Map.entry("wS2", 0.35f),    // 3rd level side branch width
					Map.entry("vr", 0.04f),    // Width of start of side branch
					Map.entry("aB", 50),        // Branch angle to trunk
					Map.entry("aS", 65),        // Branch angle around trunk
					Map.entry("aS2", 120),    // 3rd level side branch angle
					Map.entry("aS3", 40),    // 3rd level side branch angle downwards
					Map.entry("aS4", 35),    // 3rd level side branch angle downwards for 2nd part
					Map.entry("aS5", 0),        // Initial angle of rotation around branch for 3rd level side branches
					Map.entry("lS2", 0.33f),    // Distance ratio for gap between 3rd level branches (ignored for pine style branches)
					Map.entry("lS3", 0.33f),    // Distance ratio for first part of 3rd level branches (ignored for pine style branches)
					Map.entry("lS4", 0.3f),    // Distance ratio for second part of 3rd level branches (ignored for pine style branches)
					Map.entry("tH", 0.7f),    // Threshold for switching expansion/contraction (1 = bottom, 0 = top)
					Map.entry("aU", 0.5),    // Angle to curve the side branches upwards
					Map.entry("nB", 7),        // Number of side branches (and trunk height segments) per iteration
					Map.entry("nB2", 15),        // Branching factor of side branches (ignored for pine style branches)
					Map.entry("l1", 0.1f),    // Length of trunk sections between branches
					Map.entry("l2", 0f),        // Length of trunk sections after branches
					Map.entry("lr2", 1.0f),    // Ratio of decrease of l2
					Map.entry("lr", 0.5f),    // Offset of side branches from centre
					Map.entry("e", 0)));        // Elasticity
			lSystemParamsUpper = new HashMap<>(Map.ofEntries(
					Map.entry("lB", 9f),    // Base length
					Map.entry("lS", 0.8f),    // Side branch length
					Map.entry("lSm", 0.4f),  // Min ratio for side branch length (of ls) and width (of ws)
					Map.entry("wB", 0.9f),    // Base width
					Map.entry("wS", 0.55f),    // Side branch width
					Map.entry("wS2", 0.45f),    // 3rd level side branch width
					Map.entry("vr", 0.04f),    // Width of start of side branch
					Map.entry("aB", 85),        // Branch angle to trunk
					Map.entry("aS", 95),        // Branch angle around trunk
					Map.entry("aS2", 160),    // 3rd level side branch angle
					Map.entry("aS3", 80),    // 3rd level side branch angle downwards
					Map.entry("aS4", 55),    // 3rd level side branch angle downwards for 2nd part
					Map.entry("aS5", 0),        // Initial angle of rotation around branch for 3rd level side branches
					Map.entry("lS2", 0.5f),    // Distance ratio for gap between 3rd level branches (ignored for pine style branches)
					Map.entry("lS3", 0.5f),    // Distance ratio for first part of 3rd level branches (ignored for pine style branches)
					Map.entry("lS4", 0.4f),    // Distance ratio for second part of 3rd level branches (ignored for pine style branches)
					Map.entry("tH", 0.85f),    // Threshold for switching expansion/contraction (1 = bottom, 0 = top)
					Map.entry("aU", 2.5),    // Angle to curve the side branches upwards
					Map.entry("nB", 9),        // Number of side branches (and trunk height segments) per iteration
					Map.entry("nB2", 19),        // Branching factor of side branches (ignored for pine style branches)
					Map.entry("l1", 0.2f),    // Length of trunk sections between branches
					Map.entry("l2", 0f),        // Length of trunk sections after branches
					Map.entry("lr2", 1.0f),    // Ratio of decrease of l2
					Map.entry("lr", 0.5f),    // Offset of side branches from centre
					Map.entry("e", 0.001)));        // Elasticity
			leafColourFilter = new Parameters.ColourFilter(new Vector3f(1f, 0.79f, 0.1f), 0.9f, true);
			barkTexture = new Parameters.Texture(
					"/textures/Aspen_bark_001_COLOR.jpg",
					"/textures/Aspen_bark_001_NORM.jpg");
			leafTextures = new Parameters.SceneObjects.LeafTextures(
					"/textures/Leaf2/Leaf2_front.tga",
					"/textures/Leaf2/Leaf2_normals_front.tga",
					"/textures/Leaf2/Leaf2_front_t.tga",
					"/textures/Leaf2/Leaf2_halflife_front_t.tga",
					"/textures/Leaf2/Leaf2_back.tga",
					"/textures/Leaf2/Leaf2_normals_back.tga",
					"/textures/Leaf2/Leaf2_back_t.tga",
					"/textures/Leaf2/Leaf2_halflife_back_t.tga"
			);
			name = "Aspen";
			numSides = 6;
			scale = 1f;
			leafXScale = 0.28f;
			leafYScale = 0.28f;
			minIterations = 9;
			maxIterations = 11;
			density = 0.7f;
		}
	}


	public static class PoplarTree extends MonopodialTree {
		public PoplarTree() {
			super();
			lSystemParamsLower = new HashMap<>(Map.ofEntries(
					Map.entry("lB", 0f),    // Base length
					Map.entry("lS", 1.1f),    // Side branch length
					Map.entry("lSm", 1.0f),    // Min ratio for side branch length (of ls) and width (of ws)
					Map.entry("wB", 0.6f),    // Base width
					Map.entry("wS", 0.2f),    // Side branch width
					Map.entry("wS2", 0.1f),    // 3rd level side branch width
					Map.entry("vr", 0.2f),    // Width of start of side branch
					Map.entry("aB", 20),        // Branch angle to trunk
					Map.entry("aS", 70),        // Branch angle around trunk
					Map.entry("aS2", 120),    // 3rd level side branch angle
					Map.entry("aS3", 20),    // 3rd level side branch angle downwards
					Map.entry("aS4", -30),    // 3rd level side branch angle downwards for 2nd part
					Map.entry("aS5", 0),        // Initial angle of rotation around branch for 3rd level side branches
					Map.entry("lS2", 0.25f),    // Distance ratio for gap between 3rd level branches (ignored for pine style branches)
					Map.entry("lS3", 0.2f),    // Distance ratio for first part of 3rd level branches (ignored for pine style branches)
					Map.entry("lS4", 0.2f),    // Distance ratio for second part of 3rd level branches (ignored for pine style branches)
					Map.entry("tH", 1.0f),    // Threshold for switching expansion/contraction (1 = bottom, 0 = top)
					Map.entry("aU", 0.6),    // Angle to curve the side branches downward
					Map.entry("nB", 2),        // Number of side branches (and trunk height segments) per iteration
					Map.entry("nB2", 20),        // Branching factor of side branches (ignored for pine style branches)
					Map.entry("l1", 0.45f),    // Length of trunk sections between branches
					Map.entry("l2", 0f),        // Length of trunk sections after branches
					Map.entry("lr2", 1.0f),    // Ratio of decrease of l2
					Map.entry("lr", 0f),        // Offset of side branches from centre
					Map.entry("e", -0.01f)));    // Elasticity
			lSystemParamsUpper = new HashMap<>(Map.ofEntries(
					Map.entry("lB", 0.5f),    // Base length
					Map.entry("lS", 1.2f),    // Side branch length
					Map.entry("lSm", 1.2f),    // Min ratio for side branch length (of ls) and width (of ws)
					Map.entry("wB", 0.9f),    // Base width
					Map.entry("wS", 0.4f),    // Side branch width
					Map.entry("wS2", 0.3f),    // 3rd level side branch width
					Map.entry("vr", 0.2f),    // Width of start of side branch
					Map.entry("aB", 35),        // Branch angle to trunk
					Map.entry("aS", 90),        // Branch angle around trunk
					Map.entry("aS2", 160),    // 3rd level side branch angle
					Map.entry("aS3", 40),    // 3rd level side branch angle downwards
					Map.entry("aS4", -10),    // 3rd level side branch angle downwards for 2nd part
					Map.entry("aS5", 0),        // Initial angle of rotation around branch for 3rd level side branches
					Map.entry("lS2", 0.4f),    // Distance ratio for gap between 3rd level branches (ignored for pine style branches)
					Map.entry("lS3", 0.35f),    // Distance ratio for first part of 3rd level branches (ignored for pine style branches)
					Map.entry("lS4", 0.35f),    // Distance ratio for second part of 3rd level branches (ignored for pine style branches)
					Map.entry("tH", 1.0f),    // Threshold for switching expansion/contraction (1 = bottom, 0 = top)
					Map.entry("aU", 1.8),    // Angle to curve the side branches downward
					Map.entry("nB", 2),        // Number of side branches (and trunk height segments) per iteration
					Map.entry("nB2", 30),        // Branching factor of side branches (ignored for pine style branches)
					Map.entry("l1", 0.55f),    // Length of trunk sections between branches
					Map.entry("l2", 0f),        // Length of trunk sections after branches
					Map.entry("lr2", 1.0f),    // Ratio of decrease of l2
					Map.entry("lr", 0f),        // Offset of side branches from centre
					Map.entry("e", -0.01f)));    // Elasticity
			leafColourFilter = new Parameters.ColourFilter(new Vector3f(0.055f, 0.21f, 0.055f), 0.7f, false);
			barkTexture = new Parameters.Texture(
					"/textures/Bark_06_BaseColor.jpg",
					"/textures/Bark_06_Normal.jpg");
			leafTextures = new Parameters.SceneObjects.LeafTextures(
					"/textures/Leaf2/Leaf2_front.tga",
					"/textures/Leaf2/Leaf2_normals_front.tga",
					"/textures/Leaf2/Leaf2_front_t.tga",
					"/textures/Leaf2/Leaf2_halflife_front_t.tga",
					"/textures/Leaf2/Leaf2_back.tga",
					"/textures/Leaf2/Leaf2_normals_back.tga",
					"/textures/Leaf2/Leaf2_back_t.tga",
					"/textures/Leaf2/Leaf2_halflife_back_t.tga"
			);
			name = "Lombardy Poplar";
			scale = 0.7f;
			leafXScale = 0.15f;
			leafYScale = 0.15f;
			minIterations = 13;
			maxIterations = 15;
			heightVaryingAngles = false;
		}
	}

	public static class PineTree extends MonopodialTree {
		public PineTree() {
			super();
			lSystemParamsLower = new HashMap<>(Map.ofEntries(
					Map.entry("lB", 0.6f),  // Base length
					Map.entry("lS", 0.4f),     // Side branch length
					Map.entry("lSm", 0.02f),   // Min ratio for side branch length (of ls) and width (of ws)
					Map.entry("wB", 0.15f),    // Base width
					Map.entry("wS", 0.2f),    // Side branch width
					Map.entry("wS2", 0.7f),    // 3rd level side branch width
					Map.entry("vr", 0.25f),    // Width of start of side branch
					Map.entry("aB", 70),         // Branch angle to trunk
					Map.entry("aS", 50),         // Branch angle around trunk
					Map.entry("aS2", -5),         // 3rd level side branch angle around branch
					Map.entry("aS3", -55),    // 3rd level side branch angle downwards
					Map.entry("aS4", 35),    // 3rd level side branch angle downwards for 2nd part
					Map.entry("aS5", 0),    // Initial angle of rotation around branch for 3rd level side branches
					Map.entry("tH", 1.0f),    // Threshold for switching expansion/contraction (1 = bottom, 0 = top)
					Map.entry("aU", 1.5),     // Angle to curve the side branches upwards
					Map.entry("nB", 6),         // Number of side branches (and trunk height segments) per iteration
					Map.entry("l1", 0f),     // Length of trunk sections between branches
					Map.entry("l2", 0.4f),       // Length of trunk sections after branches
					Map.entry("lr2", 0.92f),       // Ratio of decrease of l2
					Map.entry("lr", 0.5f),     // Offset of side branches from centre
					Map.entry("e", 0)));         // Elasticity
			lSystemParamsUpper = new HashMap<>(Map.ofEntries(
					Map.entry("lB", 1.8f),  // Base length
					Map.entry("lS", 0.8f),     // Side branch length
					Map.entry("lSm", 0.03f),   // Min ratio for side branch length (of ls) and width (of ws)
					Map.entry("wB", 0.35f),    // Base width
					Map.entry("wS", 0.4f),    // Side branch width
					Map.entry("wS2", 0.9f),    // 3rd level side branch width
					Map.entry("vr", 0.25f),    // Width of start of side branch
					Map.entry("aB", 100),         // Branch angle to trunk
					Map.entry("aS", 70),         // Branch angle around trunk
					Map.entry("aS2", 5),         // 3rd level side branch angle around branch
					Map.entry("aS3", -35),    // 3rd level side branch angle downwards
					Map.entry("aS4", 55),    // 3rd level side branch angle downwards for 2nd part
					Map.entry("aS5", 0),    // Initial angle of rotation around branch for 3rd level side branches
					Map.entry("tH", 1.0f),    // Threshold for switching expansion/contraction (1 = bottom, 0 = top)
					Map.entry("aU", 3.5),     // Angle to curve the side branches upwards
					Map.entry("nB", 6),         // Number of side branches (and trunk height segments) per iteration
					Map.entry("l1", 0.01f),     // Length of trunk sections between branches
					Map.entry("l2", 0.6f),       // Length of trunk sections after branches
					Map.entry("lr2", 0.98f),       // Ratio of decrease of l2
					Map.entry("lr", 0.5f),     // Offset of side branches from centre
					Map.entry("e", 0)));         // Elasticity
			barkTexture = new Parameters.Texture(
					"/textures/Bark_Pine_baseColor.jpg",
					"/textures/Bark_Pine_normal.jpg");
			leafTextures = new Parameters.SceneObjects.LeafTextures(
					"/textures/Leaf3/Leaf3_front.tga",
					"/textures/Leaf3/Leaf3_normals_front.tga",
					"/textures/Leaf3/Leaf3_front_t.tga",
					"/textures/Leaf3/Leaf3_halflife_front_t.tga",
					"/textures/Leaf3/Leaf3_back.tga",
					"/textures/Leaf3/Leaf3_normals_back.tga",
					"/textures/Leaf3/Leaf3_back_t.tga",
					"/textures/Leaf3/Leaf3_halflife_back_t.tga"
			);
			name = "Pine";
			leafColourFilter = new Parameters.ColourFilter(new Vector3f(0.1f, 0.41f, 0.1f), 0.7f, false);
			numSides = 6;
			scale = 1.5f;
			leafXScale = 0.03f;
			leafYScale = 0.4f;
			minIterations = 8;
			maxIterations = 16;
			density = 0.7f;
			heightVaryingAngles = true;
			pineStyleBranches = true;
		}
	}


}
