# ForestGenerator

![GitHub release (latest by date)](https://img.shields.io/github/v/release/callumnewlands/ForestGenerator)

An application for generating and rendering realistic forest scenes using L-systems and OpenGL created for my [MEng Computer Science Individual Project](https://www.southampton.ac.uk/courses/modules/comp3200)

![Screenshot](https://user-images.githubusercontent.com/26446570/116328276-94fb7900-a7c0-11eb-8712-d7c22463e47b.jpg)

## Prerequisites

* Java 14 (https://jdk.java.net/14/)
* OpenGL 4.3

## License

This code was created by Callum Newlands, 2021 and is licenced here under
the [GNU General Public License (GPL) v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html)
with the additional term of: 7.b Requiring preservation of specified reasonable legal notices or author attributions in
that material or in the Appropriate Legal Notices displayed by works containing it

See ```LICENSE.txt``` for the full license, but in summary:

1. Anyone can copy, modify and distribute this software.
2. Source code must be made available when the software is distributed.
3. You have to include the license and copyright notice with each and every distribution.
4. You can use this software privately.
5. You can use this software for commercial purposes.
6. If you modify this software, you have to indicate changes made to the code.
7. Any modifications of this code base MUST be distributed with the same license, GPLv3.
8. This software is provided without warranty.
9. The software author or license can not be held liable for any damages inflicted by the software.
10. Any existing author attribution statements must be preserved with any copies, modifications or distributions

## Recommended HDRi Environment Maps

The following maps are too large to upload to GitHub, but are specified in some configuration files, so it is
recommended to download them and save them inside the ```/resources/textures/``` directory, or override the parameters:

* autumn_park_8k.hdr (https://hdrihaven.com/hdri/?h=autumn_park)
* gamrig_8k.hdr (https://hdrihaven.com/hdri/?h=gamrig)
* noon_grass_8k.hdr (https://hdrihaven.com/hdri/?h=noon_grass)

## Building the application (from source)

1. Run ```mvn clean install``` in the root directory
2. Navigate into the ```target/``` directory
3. In the root directory run the qcommand:
   ```java -jar ForestGenerator-1.0-windows.jar [config.yaml]```
   * ```config.yaml``` (optional) is the path to the yaml config file (see below)
   * Note: The ```resourcesRoot``` configuration parameter will need to be set to ```./classes/```
   * In the event of a ```java.lang.OutOfMemoryError: Java heap space``` error, the allocated heap space for the JVM may
     need to be increased with e.g.: ```java -jar -Xmx4G ...```
   * For unix or mac systems use file ```ForestGenerator-1.0-unix.jar``` or ```ForestGenerator-1.0-mac.jar```
     respectively

## Running the Application (from release)

1. Download and extract the latest release file (.zip)
2. In the root directory run the command:
   ```java -jar ForestGenerator-1.0-windows.jar [config.yaml]```
   * ```config.yaml``` (optional) is the path to the yaml config file (see below)
   * In the event of a ```java.lang.OutOfMemoryError: Java heap space``` error, the allocated heap space for the JVM may
     need to be increased with e.g.: ```java -jar -Xmx4G ...```
   * For unix or mac systems use file ```ForestGenerator-1.0-unix.jar``` or ```ForestGenerator-1.0-mac.jar```
     respectively

## Controls:

| Key   | stdin Character   | Description                            |
|-------|-------------------|----------------------------------------|
| W     | W                 | Move forward                           |
| S     | S                 | Move backwards                         |
| A     | A                 | Move left                              |
| D     | D                 | Move right                             |
| Space | Space (' ')       | Move upwards                           |
| Shift | -                 | Move downwards                         |
| Esc   | X                 | Exit the application                   |
| 1     | (use config file) | Toggle wireframe                       |
| 2     | (use config file) | Toggle gamma correction                |
| 3     | (use config file) | Toggle HDR tone mapping                |
| 4     | (use config file) | Screen-space ambient-occlustion (SSAO) |
| 5     | (use config file) | Toggle depth output                    |
| 6     | (use config file) | Toggle shadows                         |
| 7     | (use config file) | Toggle leaf translucency               |
| 9     |                   | Print current position and direction   |
| Mouse | I                 | Look up                                |
| Mouse | K                 | Look down                              |
| Mouse | J                 | Look left                              |
| Mouse | L                 | Look right                             |

## External Resource Sources

For materials which consist of multiple textures from the same source (e.g. diffuse + normal + glossiness) the common
file-name prefix is given along with the source.

| Resource Name            | Description                 | Licence      | Source                                                                                            |
|--------------------------|-----------------------------|--------------|--------------------------------------------------------------------------------------------|
| Autumn_leaf_08_1K        | Autumn leaf textures        | CC0          | https://www.cgbookcase.com/textures/autumn-leaf-08                                         |
| Aspen_bark_001           | Aspen bark textures         | CC0          | https://3dtextures.me/2017/12/09/aspen-bark-001/                                           |
| Bark_02_2K               | Bark textures               | CC0          | https://www.cgbookcase.com/textures/bark-02                                                |
| Bark_Pine                | Pine bark texture           | CC0          | https://3dtextures.me/2020/01/21/bark-pine-002/                                            |
| fern1_rotated.png        | Fern billboard texture      | Personal Use | https://www.clipartmax.com/middle/m2H7i8d3A0N4H7K9_fern-texture-png/                       |
| fern2_rotated.png        | Fern billboard texture      | Personal Use | https://www.clipartmax.com/middle/m2H7i8d3A0N4H7K9_fern-texture-png/                       |
| floor2.png               | Ground texture              | Unlimited    | https://www.deviantart.com/fabooguy/art/Dirt-Ground-Texture-Tileable-2048x2048-441212191   |
| grass2.png               | Grass billboard texture     | Personal Use | https://www.clipartmax.com/middle/m2i8Z5i8H7K9b1G6_free-icons-png-grass-alpha-texture-png/ |
| Leaf1                    | Sycamore-like leaf textures |              | https://www.cg.tuwien.ac.at/research/publications/2007/Habel_2007_RTT/                     |
| Leaf2                    | Hazel leaf textures         |              | https://www.cg.tuwien.ac.at/research/publications/2007/Habel_2007_RTT/                     |
| Leaf3                    | Laurel leaf textures        |              | https://www.cg.tuwien.ac.at/research/publications/2007/Habel_2007_RTT/                     |
| LeafSet016_2K            | Oak leaf textures           | CC0          | https://cc0textures.com/view?id=LeafSet016                                                 |
| Mossy_rock_01_2K         | Mossy rock textures         | CC0          | https://www.cgbookcase.com/textures/mossy-rock-01                                          |
| Oak_Bark_4k              | Oak bark textures           | Unlimited    | https://www.artstation.com/artwork/xkYZR                                                   |
| Rock1.obj                | Rock model and materials    | Personal Use | https://free3d.com/3d-model/low-poly-rock-4631.html                                        |

## YAML Configuration File

Many aspects of the application can be configured through the use of YAML configuration files. See ```default.yaml```
for a file containing all parameters and their default values.

To override these values, create a new .yaml file containing the parameters you with to override and their new values.

Parameters written with integers in ```default.yaml``` are integers, parameters written with decimals (e.g. 1.0, 2.992)
are floating -point numbers

### Example files supplied:

| File                  | Description                                                                                                                  |
|-----------------------|------------------------------------------------------------------------------------------------------------------------------|
| async.yaml            | A small scene which receives only stdin input and outputs only frame images                                                  |
| default.yaml          | A full list of all of the configuration parameters and their default values (these are the values used if no file is loaded) |
| dense.yaml            | A dense forest                                                                                                               |
| ground.yaml           | Just generates and renders the ground terrain                                                                                |
| highGraphics.yaml     | A scene with high-quality render settings                                                                                    |
| large.yaml            | A large scene                                                                                                                |
| lowGraphics.yaml      | A small scene where lighting effects are disabled and render quality is reduced                                              |
| medium.yaml           | A medium size scene                                                                                                          |
| singleTree.yaml       | A single tree                                                                                                                | 
| singleTypeForest.yaml | A forest consisting of a single tree type                                                                                    |
| small.yaml            | A small scene                                                                                                                |
| sparse.yaml           | A sparse forest                                                                                                               |

### default.yaml:

```yaml
# Note: Full parameter specification is contained in this file, but to override any parameter in a custom config file,
#       only that parameter needs specified
#
#       The exception to this are the lSystemParamsLower and lSystemParamsUpper properties in the trees, in order to
#       change one of these lSystemParams (e.g. "lB"), the entire lSystemParamsLower list must be included in the
#       config file.
#
#       To include a pre-made tree type in a config file, all that is needed is to include the tree class name and at
#       least 1 parameter value. E.g.
#           ...
#           trees:
#             - !TreeTypes$BranchingTree
#               name: tree 1
#             - !TreeTypes$AspenTree
#               name: tree 2
#           ...

# relative path to the directory containing texture and model resources
resourcesRoot: ./resources/

random:
   # The value of the random seed for the application
   seed: -1 # A value of -1 will result in a seed derived from the system clock value

input:
   # It is advised to set one of manual XOR stdin to true
   # Enable keyboard and mouse input (via the application window)
   manual: true
   # Input via stdin (each line will cause a new frame to render)
   stdin:
      enabled: false # Note: Enabling stdin will case render loop to block after each frame for input and will disable delta time
      # The amount the viewing angle changes with a look command (I, J, K, or L)
      lookOffset: 10.0
      # The frames per second of the resulting output (controls movement speed -> larger = slower movement speed/shorter movement)
      fps: 30

output:
   window:
      visible: true
      fullscreen: false
      width: 800
      height: 600
   # Output the frames of the application to the /frames directory
   frameImages:
      enabled: false
      fileExtension: jpg
      filePrefix: ""
   # Output a colour image
   colour: true
   # Output a depth image
   depth: false # If outputting to window the depth output will be displayed instead of colour if this is true, if rendering to images, both can be output
   # Invert the depth colours (default: near = black, far = white)
   invertDepth: true
   # Distance to the far plane of the scene
   renderDistance: 300.0
   # Maximum depth for depth output
   maxDepthOutput: 20.0

camera:
   startPosition: [ 0.0, 3.3, 0.0 ]
   startDirection: [ 0.0, 0.0, 1.0 ]
   # Enable/disable vertical motion
   verticalMovement: true

terrain:
   # The size of the scene to generate
   width: 100.0
   # The vertical scaling on the terrain geometry (larger value = taller terrain)
   verticalScale: 3.0
   # Number of repetitions of the texture per terrain tile side = 2 ^ textureScale
   textureScale: 2
   noise:
      xScale: 1.0
      yScale: 1.0
      # The number of octaves of simplex noise (detail) to use (higher = more detail)
      octaves: 8
      # The amplitude multiplier for successive octaves (smaller = smoother terrain)
      persistence: 0.5
      # The frequency multiplier for successive octaves (>1 => each octave will contribute finer details)
      lacunarity: 2.0
   # Density of vertices in the terrain mesh (higher = more vertices)
   vertexDensity: 0.7
   texture:
      diffuse: "/textures/floor2.png/"
      normal: null
      glossiness: null

ecosystemSimulation:
   # Number of iterations to run for
   numIterations: 400
   # Number of iterations per year
   yearLength: 20
   # Age of highest viability (range [0,1])
   ageThreshold: 0.7
   # Weighting for radius viability
   radiusWeight: 0.3
   # Viability for minimum radius plants
   smallRadiusViability: 0.4
   # Viability for average radius plants
   averageRadiusViability: 0.4

quadtree:
   # Levels of detail (LODs) in the quad-tree
   levels: 3
   # Distance multiplier for switching (LOD) (smaller = LOD threshold is closer)
   thresholdCoefficient: 1.5
   # Enable view-frustum culling of geometry
   frustumCulling: true

sceneObjects:
   display: true
   # List of tree types and their generation parameters
   trees:
      - !TreeTypes$BranchingTree # Base class for Oak
         name: tree 1
         lSystemParamsLower:
            a0: 15.0 # Angle away after branching for first branch
            a1: 15.0 # Angle away after branching
            a2: -15.0 # Angle around vertical axis between splittings
            l1: 45.0 # Length of branches
            pS: 0.0 # Probability of side branch
            l2: 0.0 # Length before side branches as a fraction of l1
            l3: 0.0 # Length after side branches as a fraction of l1
            bO: 0.0 # offset at branching point
            aS: 25.0 # Angle of side branches from main branch
            lB: 150.0 # Length of trunk base
            wB: 1.0 # Width of trunk base
            nT: 7  # Number of steps in taper
            tP: 6.0 # Falloff power for taper
            tF: 0.5 # Taper widest width as a factor of wB
            rl: 1.0 # Ratio of increase of branch length (from ends to trunk)
            rw: 1.8 # Ratio of increase of branch width (from ends to trunk)
            e: 0.050  # Elasticity
         lSystemParamsUpper:
            a0: 30.0 # Angle away after branching for first branch
            a1: 30.0 # Angle away after branching
            a2: 15.0 # Angle around vertical axis between splittings
            l1: 55.0 # Length of branches
            pS: 0.0 # Probability of side branch
            l2: 0.0 # Length before side branches as a fraction of l1
            l3: 0.0 # Length after side branches as a fraction of l1
            bO: 20.0 # offset at branching point
            aS: 50.0 # Angle of side branches from main branch
            lB: 250.0 # Length of trunk base
            wB: 1.1 # Width of trunk base
            nT: 7  # Number of steps in taper
            tP: 7.0 # Falloff power for taper
            tF: 0.7 # Taper widest width as a factor of wB
            rl: 1.3 # Ratio of increase of branch length (from ends to trunk)
            rw: 1.82 # Ratio of increase of branch width (from ends to trunk)
            e: 0.057  # Elasticity
         # Angles and probabilities for branchings to occur, (number of angles + 1) = number of branches to split into
         branchings:
            - minAngles: [ 120.0 ]
              maxAngles: [ 240.0 ]
              prob: 0.3
            - minAngles: [ 60.0, 120.0 ]
              maxAngles: [ 110.0, 150.0 ]
              prob: 0.7
         # The (maximum) number of models to generate for each iteration count (age) of tree needed in the scene
         numPerIterationSize: 3
         # Size scalar for the models
         scale: 0.01
         # minimum factor of s for each instance's scale
         minScaleFactor: 0.7
         # maximum factor of s for each instance's scale
         maxScaleFactor: 1.1
         # vertical offset of world position (height above/below the terrain)
         yOffset: 0.0
         # variability in the x-z angle of each instance
         pitchVariability: 0.0
         # Size scalar for the leaf models
         leafXScale: 0.7
         leafYScale: 0.7
         barkTexture:
            diffuse: "/textures/Bark_02_2K_Base_Color.png"
            normal: "/textures/Bark_02_2K_Normal.png"
            glossiness: null
         leafTextures:
            frontAlbedo: "/textures/Leaf1/Leaf1_front.tga"
            frontNormal: "/textures/Leaf1/Leaf1_normals_front.tga"
            frontTranslucency: "/textures/Leaf1/Leaf1_front_t.tga"
            frontHalfLife: "/textures/Leaf1/Leaf1_halflife_front_t.tga"
            backAlbedo: "/textures/Leaf1/Leaf1_back.tga"
            backNormal: "/textures/Leaf1/Leaf1_normals_back.tga"
            backTranslucency: "/textures/Leaf1/Leaf1_back_t.tga"
            backHalfLife: "/textures/Leaf1/Leaf1_halflife_back_t.tga"
         leafColourFilter: null
         # Number of sides to use in the cross section of the branches
         numSides: 6
         # Minimum number of iterations of the L-system
         minIterations: 5 # inclusive
         # Maximum number of iterations of the L-system
         maxIterations: 9 # exclusive
         # Density of models in the scene
         density: 0.5
         # Number of edges to use in the cross section of the low LOD representation
         lowLODEdges: 2
         # Number of times to merge successive pairs of leaves for the low LOD representation
         lowLODLeafMerges: 1
         # Whether to widen the base of the tree
         widenBase: true
         # Max age for the tree in the ecosystem simulation
         maxAge: 200
         # Radius multiplier around the trunk when distributing seeds in the ecosystem simulation
         seedRadiusMultiplier: 2.0


      - !TreeTypes$OakTree
         name: Oak
         lSystemParamsLower:
            a0: 0.0 # Angle away after branching for first branch
            a1: 10.0 # Angle away after branching
            a2: 25.0 # Angle around vertical axis between splittings
            l1: 30.0 # Length of branches
            pS: 0.48 # Probability of side branch
            l2: 0.40 # Length before side branches as a fraction of l1
            l3: 0.6 # Length after side branches as a fraction of l1
            bO: 5.0 # offset at branching point
            aS: 20.0 # Angle of side branches from main branch
            lB: 200.0 # Length of trunk base
            wB: 1.0 # Width of trunk base
            nT: 6  # Number of steps in taper
            tP: 6.0 # Falloff power for taper
            tF: 0.6 # Taper widest width as a factor of wB
            rl: 1.0 # Ratio of increase of branch length (from ends to trunk)
            rw: 1.4 # Ratio of increase of branch width (from ends to trunk)
            e: 0.01  # Elasticity
         lSystemParamsUpper:
            a0: 10.0 # Angle away after branching for first branch
            a1: 35.0 # Angle away after branching
            a2: 35.0 # Angle around vertical axis between splittings
            l1: 30.0 # Length of branches
            pS: 0.48 # Probability of side branch
            l2: 0.5 # Length before side branches as a fraction of l1
            l3: 0.6 # Length after side branches as a fraction of l1
            bO: 35.0 # offset at branching point
            aS: 40.0 # Angle of side branches from main branch
            lB: 300.0 # Length of trunk base
            wB: 1.1 # Width of trunk base
            nT: 6  # Number of steps in taper
            tP: 7.0 # Falloff power for taper
            tF: 0.75 # Taper widest width as a factor of wB
            rl: 1.1 # Ratio of increase of branch length (from ends to trunk)
            rw: 1.42 # Ratio of increase of branch width (from ends to trunk)
            e: 0.01  # Elasticity
         branchings:
            - minAngles: [ ]
              maxAngles: [ ]
              prob: 0.5
            - minAngles: [ 120.0 ]
              maxAngles: [ 240.0 ]
              prob: 0.5
         numPerIterationSize: 3
         scale: 0.015
         minScaleFactor: 0.7
         maxScaleFactor: 1.1
         yOffset: 0.0
         pitchVariability: 0.0
         leafXScale: 0.4
         leafYScale: 0.4
         barkTexture:
            diffuse: "/textures/Oak_Bark_4k_Albedo.png"
            normal: "/textures/Oak_Bark_4k_Normal.png"
            glossiness: "/textures/Oak_Bark_4k_Glossiness.png"
         leafTextures:
            frontAlbedo: "/textures/Leaf5/LeafSet016_2K_Color_Cropped.png"
            frontNormal: "/textures/Leaf5/LeafSet016_2K_Normal_Cropped.png"
            frontTranslucency: null
            frontHalfLife: null
            backAlbedo: "/textures/Leaf5/LeafSet016_2K_Color_Cropped.png"
            backNormal: "/textures/Leaf5/LeafSet016_2K_Normal_Cropped.png"
            backTranslucency: null
            backHalfLife: null
         leafColourFilter:
            colour: [ 0.7, 0.6, 0.0 ]
            mixFactor: 0.4
            expMix: false
         numSides: 6
         minIterations: 8 # inclusive
         maxIterations: 13 # exclusive
         density: 0.5
         lowLODEdges: 2
         lowLODLeafMerges: 1
         widenBase: true
         maxAge: 200
         seedRadiusMultiplier: 2.0

      - !TreeTypes$MonopodialTree # Base class for Aspen, Poplar and Pine
         name: Monopodial Tree
         lSystemParamsLower:
            lB: 2.0   # Base length
            lS: 1.1   # Side branch length
            lSm: 0.5  # Min ratio for side branch length (of ls) and width (of ws)
            wB: 0.5   # Base width
            nT: 7     # Number of steps in taper
            tP: 6.0   # Falloff power for taper
            tF: 0.25  # Taper widest width as a factor of wB
            wS: 0.3   # Side branch width
            wS2: 0.4  # 3rd level side branch width
            vr: 0.3   # Width of start of side branch
            aB: 80.0  # Branch angle to trunk
            aS: 110.0  # Branch angle around trunk
            aMin: 5.0  # Minimum varying angle
            aMax: 85.0 # Maximum varying angle
            aS2: 100.0 # 3rd level side branch angle
            aS3: 80.0  # 3rd level side branch angle downwards
            aS4: -45.0 # 3rd level side branch angle upwards for 2nd part
            aS5: 0.0   # Initial angle of rotation around branch for 3rd level side branches
            lS2: 0.333 # Distance ratio for gap between 3rd level branches (ignored for pine style branches)
            lS3: 0.25  # Distance ratio for first part of 3rd level branches (ignored for pine style branches)
            lS4: 0.25  # Distance ratio for second part of 3rd level branches (ignored for pine style branches)
            tH: 0.9    # Threshold for switching expansion/contraction (1 = bottom, 0 = top)
            aU: 0.0    # Angle to curve the side branches upwards
            nB: 8      # Number of side branches (and trunk height segments) per iteration
            nB2: 12    # Branching factor of side branches (ignored for pine style branches)
            l1: 0.2    # Length of trunk sections between branches
            l2: 0.0    # Length of trunk sections after branches
            lr: 0.5    # Offset of side branches from centre
            lr2: 1.0   # Ratio of decrease of l2
            dL: 1.0 # Density of leaves
            e: 0.0001     # Elasticity
         lSystemParamsUpper:
            lB: 2.0   # Base length
            lS: 1.1   # Side branch length
            lSm: 0.5  # Min ratio for side branch length (of ls) and width (of ws)
            wB: 0.5   # Base width
            nT: 7     # Number of steps in taper
            tP: 6.0   # Falloff power for taper
            tF: 0.25  # Taper widest width as a factor of wB
            wS: 0.3   # Side branch width
            wS2: 0.4  # 3rd level side branch width
            vr: 0.3   # Width of start of side branch
            aB: 80.0  # Branch angle to trunk
            aS: 110.0  # Branch angle around trunk
            aMin: 5.0  # Minimum varying angle
            aMax: 85.0 # Maximum varying angle
            aS2: 100.0 # 3rd level side branch angle
            aS3: 80.0  # 3rd level side branch angle downwards
            aS4: -45.0 # 3rd level side branch angle upwards for 2nd part
            aS5: 0.0   # Initial angle of rotation around branch for 3rd level side branches
            lS2: 0.333 # Distance ratio for gap between 3rd level branches (ignored for pine style branches)
            lS3: 0.25  # Distance ratio for first part of 3rd level branches (ignored for pine style branches)
            lS4: 0.25  # Distance ratio for second part of 3rd level branches (ignored for pine style branches)
            tH: 0.9    # Threshold for switching expansion/contraction (1 = bottom, 0 = top)
            aU: 0.0    # Angle to curve the side branches upwards
            nB: 8      # Number of side branches (and trunk height segments) per iteration
            nB2: 12    # Branching factor of side branches (ignored for pine style branches)
            l1: 0.2    # Length of trunk sections between branches
            l2: 0.0    # Length of trunk sections after branches
            lr: 0.5    # Offset of side branches from centre
            lr2: 1.0   # Ratio of decrease of l2
            dL: 1.0 # Density of leaves
            e: 0.0001     # Elasticity
         # The (maximum) number of models to generate for each iteration count (age) of tree needed in the scene
         numPerIterationSize: 3
         # Size scalar for the models
         scale: 0.75
         # minimum factor of s for each instance's scale
         minScaleFactor: 0.7
         # maximum factor of s for each instance's scale
         maxScaleFactor: 1.1
         # vertical offset of world position (height above/below the terrain)
         yOffset: 0.0
         # variability in the x-z angle of each instance
         pitchVariability: 0.0
         # Size scalar for the leaf models
         leafXScale: 0.17
         leafYScale: 0.17
         barkTexture:
            diffuse: "/textures/Bark_02_2K_Base_Color.png"
            normal: "/textures/Bark_02_2K_Normal.png"
            glossiness: null
         leafTextures:
            frontAlbedo: "/textures/Leaf3/Leaf3_front.tga"
            frontNormal: "/textures/Leaf3/Leaf3_normals_front.tga"
            frontTranslucency: "/textures/Leaf3/Leaf3_front_t.tga"
            frontHalfLife: "/textures/Leaf3/Leaf3_halflife_front_t.tga"
            backAlbedo: "/textures/Leaf3/Leaf3_back.tga"
            backNormal: "/textures/Leaf3/Leaf3_normals_back.tga"
            backTranslucency: "/textures/Leaf3/Leaf3_back_t.tga"
            backHalfLife: "/textures/Leaf3/Leaf3_halflife_back_t.tga"
         leafColourFilter: null
         # Number of sides to use in the cross section of the branches
         numSides: 6
         # Minimum number of iterations of the L-system
         minIterations: 5 # inclusive
         # Maximum number of iterations of the L-system
         maxIterations: 9 # exclusive
         # Density of models in the scene
         density: 0.5
         # Vary branch angle with height (higher branches point upwards, flattening around tH then pointing down)
         heightVaryingAngles: true
         # Use pine tree style branching (opposite and recursive)
         pineStyleBranches: false
         lowLODEdges: 2
         lowLODLeafMerges: 1
         widenBase: true
         maxAge: 200
         seedRadiusMultiplier: 2.0

      - !TreeTypes$AspenTree
         name: Aspen
         lSystemParamsLower:
            lB: 4.0   # Base length
            lS: 0.5   # Side branch length
            lSm: 0.2  # Min ratio for side branch length (of ls) and width (of ws)
            wB: 0.5   # Base width
            nT: 10     # Number of steps in taper
            tP: 7.0   # Falloff power for taper
            tF: 0.5  # Taper widest width as a factor of wB
            wS: 0.35  # Side branch width
            wS2: 0.35  # 3rd level side branch width
            vr: 0.04  # Width of start of side branch
            aB: 50.0  # Branch angle to trunk
            aS: 65.0  # Branch angle around trunk
            aMin: 30.0  # Minimum varying angle
            aMax: 115.0 # Maximum varying angle
            aS2: 120.0 # 3rd level side branch angle
            aS3: 40.0 # 3rd level side branch angle upwards
            aS4: 35.0 # 3rd level side branch angle upwards for 2nd part
            aS5: 0.0   # Initial angle of rotation around branch for 3rd level side branches
            lS2: 0.33 # Distance ratio for gap between 3rd level branches (ignored for pine style branches)
            lS3: 0.33  # Distance ratio for first part of 3rd level branches (ignored for pine style branches)
            lS4: 0.3  # Distance ratio for second part of 3rd level branches (ignored for pine style branches)
            tH: 0.7   # Threshold for switching expansion/contraction (1 = bottom, 0 = top)
            aU: 0.5  # Angle to curve the side branches upwards
            nB: 7    # Number of side branches (and trunk height segments) per iteration
            nB2: 15   # Branching factor of side branches (ignored for pine style branches)
            l1: 0.1   # Length of trunk sections between branches
            l2: 0.0   # Length of trunk sections after branches
            lr: 0.5   # Offset of side branches from centre
            lr2: 1.0  # Ratio of decrease of l2
            dL: 0.8 # Density of leaves
            e: 0.0    # Elasticity
         lSystemParamsUpper:
            lB: 9.0   # Base length
            lS: 0.8   # Side branch length
            lSm: 0.4  # Min ratio for side branch length (of ls) and width (of ws)
            wB: 0.7   # Base width
            nT: 10     # Number of steps in taper
            tP: 8.0   # Falloff power for taper
            tF: 0.6  # Taper widest width as a factor of wB
            wS: 0.55  # Side branch width
            wS2: 0.45  # 3rd level side branch width
            vr: 0.04  # Width of start of side branch
            aB: 85.0  # Branch angle to trunk
            aS: 95.0  # Branch angle around trunk
            aMin: 30.0  # Minimum varying angle
            aMax: 115.0 # Maximum varying angle
            aS2: 160.0 # 3rd level side branch angle
            aS3: 80.0 # 3rd level side branch angle upwards
            aS4: 55.0 # 3rd level side branch angle upwards for 2nd part
            aS5: 0.0   # Initial angle of rotation around branch for 3rd level side branches
            lS2: 0.5 # Distance ratio for gap between 3rd level branches (ignored for pine style branches)
            lS3: 0.5  # Distance ratio for first part of 3rd level branches (ignored for pine style branches)
            lS4: 0.4  # Distance ratio for second part of 3rd level branches (ignored for pine style branches)
            tH: 0.85   # Threshold for switching expansion/contraction (1 = bottom, 0 = top)
            aU: 2.5  # Angle to curve the side branches upwards
            nB: 9    # Number of side branches (and trunk height segments) per iteration
            nB2: 19   # Branching factor of side branches (ignored for pine style branches)
            l1: 0.2   # Length of trunk sections between branches
            l2: 0.0   # Length of trunk sections after branches
            lr: 0.5   # Offset of side branches from centre
            lr2: 1.0  # Ratio of decrease of l2
            dL: 0.9 # Density of leaves
            e: 0.001    # Elasticity
         numPerIterationSize: 3
         scale: 0.9
         minScaleFactor: 0.7
         maxScaleFactor: 1.1
         yOffset: 0.0
         pitchVariability: 0.0
         leafXScale: 0.28
         leafYScale: 0.28
         barkTexture:
            diffuse: "/textures/Aspen_bark_001_COLOR.jpg"
            normal: "/textures/Aspen_bark_001_NORM.jpg"
            glossiness: "/textures/Aspen_bark_001_SPEC.jpg"
         leafTextures:
            frontAlbedo: "/textures/Leaf4/Autumn_leaf_08_1K_front_Base_Color.png"
            frontNormal: "/textures/Leaf4/Autumn_leaf_08_1K_front_Normal.png"
            frontTranslucency: null
            frontHalfLife: null
            backAlbedo: "/textures/Leaf4/Autumn_leaf_08_1K_back_Base_Color.png"
            backNormal: "/textures/Leaf4/Autumn_leaf_08_1K_back_Normal.png"
            backTranslucency: null
            backHalfLife: null
         leafColourFilter:
            colour: [ 1.0, 0.79, 0.1 ]
            mixFactor: 0.9
            expMix: true
         numSides: 6
         minIterations: 6 # inclusive
         maxIterations: 11 # exclusive
         density: 0.5
         heightVaryingAngles: true
         pineStyleBranches: false
         lowLODEdges: 2
         lowLODLeafMerges: 1
         widenBase: true
         maxAge: 200
         seedRadiusMultiplier: 2.0

      - !TreeTypes$PoplarTree
         name: Poplar
         lSystemParamsLower:
            lB: 0.0   # Base length
            lS: 1.1  # Side branch length
            lSm: 1.0  # Min ratio for side branch length (of ls) and width (of ws)
            wB: 0.6   # Base width
            nT: 3     # Number of steps in taper
            tP: 3.0   # Falloff power for taper
            tF: 0.25  # Taper widest width as a factor of wB
            wS: 0.2   # Side branch width
            wS2: 0.1  # 3rd level side branch width
            vr: 0.2   # Width of start of side branch
            aB: 20.0  # Branch angle to trunk
            aS: 70.0  # Branch angle around trunk
            aMin: 0.0  # Minimum varying angle
            aMax: 0.0 # Maximum varying angle
            aS2: 120.0 # 3rd level side branch angle
            aS3: 20.0 # 3rd level side branch angle upwards
            aS4: -30.0 # 3rd level side branch angle upwards for 2nd part
            aS5: 0.0   # Initial angle of rotation around branch for 3rd level side branches
            lS2: 0.25 # Distance ratio for gap between 3rd level branches (ignored for pine style branches)
            lS3: 0.2  # Distance ratio for first part of 3rd level branches (ignored for pine style branches)
            lS4: 0.2  # Distance ratio for second part of 3rd level branches (ignored for pine style branches)
            tH: 1.0   # Threshold for switching expansion/contraction (1 = bottom, 0 = top)
            aU: 0.6  # Angle to curve the side branches upwards
            nB: 2     # Number of side branches (and trunk height segments) per iteration
            nB2: 20   # Branching factor of side branches (ignored for pine style branches)
            l1: 0.45   # Length of trunk sections between branches
            l2: 0.0   # Length of trunk sections after branches
            lr: 0.0   # Offset of side branches from centre
            lr2: 1.0   # Ratio of decrease of l2
            dL: 0.9 # Density of leaves
            e: -0.01  # Elasticity
         lSystemParamsUpper:
            lB: 0.5   # Base length
            lS: 1.2  # Side branch length
            lSm: 1.2  # Min ratio for side branch length (of ls) and width (of ws)
            wB: 0.9   # Base width
            nT: 3     # Number of steps in taper
            tP: 3.5   # Falloff power for taper
            tF: 0.3  # Taper widest width as a factor of wB
            wS: 0.4   # Side branch width
            wS2: 0.3  # 3rd level side branch width
            vr: 0.2   # Width of start of side branch
            aB: 35.0  # Branch angle to trunk
            aS: 90.0  # Branch angle around trunk
            aMin: 0.0  # Minimum varying angle
            aMax: 0.0 # Maximum varying angle
            aS2: 160.0 # 3rd level side branch angle
            aS3: 40.0 # 3rd level side branch angle upwards
            aS4: -10.0 # 3rd level side branch angle upwards for 2nd part
            aS5: 0.0   # Initial angle of rotation around branch for 3rd level side branches
            lS2: 0.4 # Distance ratio for gap between 3rd level branches (ignored for pine style branches)
            lS3: 0.35  # Distance ratio for first part of 3rd level branches (ignored for pine style branches)
            lS4: 0.35  # Distance ratio for second part of 3rd level branches (ignored for pine style branches)
            tH: 1.0   # Threshold for switching expansion/contraction (1 = bottom, 0 = top)
            aU: 1.8  # Angle to curve the side branches upwards
            nB: 2     # Number of side branches (and trunk height segments) per iteration
            nB2: 30   # Branching factor of side branches (ignored for pine style branches)
            l1: 0.55   # Length of trunk sections between branches
            l2: 0.0   # Length of trunk sections after branches
            lr: 0.0   # Offset of side branches from centre
            lr2: 1.0   # Ratio of decrease of l2
            dL: 1.1 # Density of leaves
            e: -0.01  # Elasticity
         numPerIterationSize: 3
         scale: 0.7
         minScaleFactor: 0.7
         maxScaleFactor: 1.1
         yOffset: 0.0
         pitchVariability: 0.0
         leafXScale: 0.15
         leafYScale: 0.15
         barkTexture:
            diffuse: "/textures/Bark_06_BaseColor.jpg"
            normal: "/textures/Bark_06_Normal.jpg"
            glossiness: "/textures/Bark_06_Glossiness.jpg"
         leafTextures:
            frontAlbedo: "/textures/Leaf2/Leaf2_front.tga"
            frontNormal: "/textures/Leaf2/Leaf2_normals_front.tga"
            frontTranslucency: "/textures/Leaf2/Leaf2_front_t.tga"
            frontHalfLife: "/textures/Leaf2/Leaf2_halflife_front_t.tga"
            backAlbedo: "/textures/Leaf2/Leaf2_back.tga"
            backNormal: "/textures/Leaf2/Leaf2_normals_back.tga"
            backTranslucency: "/textures/Leaf2/Leaf2_back_t.tga"
            backHalfLife: "/textures/Leaf2/Leaf2_halflife_back_t.tga"
         leafColourFilter:
            colour: [ 0.055, 0.21, 0.055 ]
            mixFactor: 0.7
            expMix: false
         numSides: 6
         minIterations: 6 # inclusive
         maxIterations: 18 # exclusive
         density: 0.5
         heightVaryingAngles: false
         pineStyleBranches: false
         lowLODEdges: 2
         lowLODLeafMerges: 1
         widenBase: true
         maxAge: 200
         seedRadiusMultiplier: 2.75

      - !TreeTypes$PineTree
         name: Pine Tree
         lSystemParamsLower:
            lB: 0.6   # Base length
            lS: 0.4   # Side branch length
            lSm: 0.02  # Min ratio for side branch length (of ls) and width (of ws)
            wB: 0.15   # Base width
            nT: 5     # Number of steps in taper
            tP: 5.0   # Falloff power for taper
            tF: 0.4  # Taper widest width as a factor of wB
            wS: 0.2   # Side branch width
            wS2: 0.7  # 3rd level side branch width
            vr: 0.25   # Width of start of side branch
            aB: 70.0  # Branch angle to trunk
            aS: 50.0  # Branch angle around trunk
            aMin: 0.0  # Minimum varying angle
            aMax: 115.0 # Maximum varying angle
            aS2: -5.0 # 3rd level side branch angle
            aS3: -55.0  # 3rd level side branch angle downwards
            aS4: 35.0 # 3rd level side branch angle upwards for 2nd part
            aS5: 0.0   # Initial angle of rotation around branch for 3rd level side branches
            tH: 1.0    # Threshold for switching expansion/contraction (1 = bottom, 0 = top)
            aU: 1.5    # Angle to curve the side branches upwards
            nB: 6      # Number of side branches (and trunk height segments) per iteration
            l1: 0.0    # Length of trunk sections between branches
            l2: 0.4    # Length of trunk sections after branches
            lr: 0.5    # Offset of side branches from centre
            lr2: 0.92   # Ratio of decrease of l2
            dL: 1.1 # Density of leaves
            e: 0.0     # Elasticity
         lSystemParamsUpper:
            lB: 2.8   # Base length
            lS: 0.8   # Side branch length
            lSm: 0.03  # Min ratio for side branch length (of ls) and width (of ws)
            wB: 0.5   # Base width
            nT: 5     # Number of steps in taper
            tP: 6.0   # Falloff power for taper
            tF: 0.45  # Taper widest width as a factor of wB
            wS: 0.4   # Side branch width
            wS2: 0.9  # 3rd level side branch width
            vr: 0.25   # Width of start of side branch
            aB: 100.0  # Branch angle to trunk
            aS: 70.0  # Branch angle around trunk
            aMin: 0.0  # Minimum varying angle
            aMax: 115.0 # Maximum varying angle
            aS2: 5.0 # 3rd level side branch angle
            aS3: -35.0  # 3rd level side branch angle downwards
            aS4: 55.0 # 3rd level side branch angle upwards for 2nd part
            aS5: 0.0   # Initial angle of rotation around branch for 3rd level side branches
            tH: 1.0    # Threshold for switching expansion/contraction (1 = bottom, 0 = top)
            aU: 3.5    # Angle to curve the side branches upwards
            nB: 6      # Number of side branches (and trunk height segments) per iteration
            l1: 0.01    # Length of trunk sections between branches
            l2: 0.6    # Length of trunk sections after branches
            lr: 0.5    # Offset of side branches from centre
            lr2: 0.98   # Ratio of decrease of l2
            dL: 1.4 # Density of leaves
            e: 0.0     # Elasticity
         numPerIterationSize: 3
         scale: 1.9
         minScaleFactor: 0.7
         maxScaleFactor: 1.1
         yOffset: 0.0
         pitchVariability: 0.0
         leafXScale: 0.035
         leafYScale: 0.4
         barkTexture:
            diffuse: "/textures/Bark_02_2K_Base_Color.png"
            normal: "/textures/Bark_02_2K_Normal.png"
            glossiness: "/textures/Bark_Pine_glossiness.jpg"
         leafTextures:
            frontAlbedo: "/textures/Leaf3/Leaf3_front.tga"
            frontNormal: "/textures/Leaf3/Leaf3_normals_front.tga"
            frontTranslucency: "/textures/Leaf3/Leaf3_front_t.tga"
            frontHalfLife: "/textures/Leaf3/Leaf3_halflife_front_t.tga"
            backAlbedo: "/textures/Leaf3/Leaf3_back.tga"
            backNormal: "/textures/Leaf3/Leaf3_normals_back.tga"
            backTranslucency: "/textures/Leaf3/Leaf3_back_t.tga"
            backHalfLife: "/textures/Leaf3/Leaf3_halflife_back_t.tga"
         numSides: 6
         minIterations: 6 # inclusive
         maxIterations: 16 # exclusive
         density: 0.5
         heightVaryingAngles: true
         pineStyleBranches: true
         lowLODEdges: 2
         lowLODLeafMerges: 1
         widenBase: true
         maxAge: 200
         seedRadiusMultiplier: 2.0

   twigs:
      # Number of different twig models per terrain quad
      typesPerQuad: 2
      texture:
         diffuse: "/textures/Bark_Pine_baseColor.jpg"
         normal: "/textures/Bark_Pine_normal.jpg"
         glossiness: null
      numSides: 5
      # Size scalar for the models
      scale: 0.05
      # minimum factor of s for each instance's scale
      minScaleFactor: 0.75
      # maximum factor of s for each instance's scale
      maxScaleFactor: 1.2
      # vertical offset of world position (height above/below the terrain)
      yOffset: 0.1
      # variability in the x-z angle of each instance
      pitchVariability: 0.1
      density: 1.0
   externalModels:
      # Path to model (.obj) file
      - modelPath: "/models/Rock1/Rock1.obj"
         # Path to directory containing model textures defined in material file (.mtl)
        texturesDir: "/models/Rock1"
         # Texture to overwrite the model texture with (default = <null, null>)
        scale: 0.3
        minScaleFactor: 0.75
        maxScaleFactor: 1.2
        yOffset: -0.1
        pitchVariability: 0.1
        density: 1.0
   crossedBillboards:
      # texture to use on the billboards
      - texture:
           diffuse: "/textures/grass2.png"
           normal: null
         # the number of intersecting boards to generate
        numBoards: 4
         # width scalar of each board
        xScale: 1.0
         # height scalar of each board
        yScale: 0.35
         # overall scalar for the model
        scale: 0.7
        minScaleFactor: 0.75
        maxScaleFactor: 1.2
        yOffset: 0.0
        pitchVariability: 0.1
        density: 20.0
      - texture:
           diffuse: "/textures/fern1_rotated.png"
           normal: null
        numBoards: 4
        xScale: 1.2
        yScale: 1.2
         # overall scalar for the model
        scale: 0.7
        minScaleFactor: 0.75
        maxScaleFactor: 1.2
        yOffset: 0.0
        pitchVariability: 0.1
        density: 0.2
      - texture:
           diffuse: "/textures/fern2_rotated.png"
           normal: null
        numBoards: 4
        xScale: 1.2
        yScale: 1.2
         # overall scalar for the model
        scale: 0.7
        minScaleFactor: 0.75
        maxScaleFactor: 1.2
        yOffset: 0.0
        pitchVariability: 0.1
        density: 0.2
   fallenLeaves:
      density: 1.0
      scale: 0.7
      minScaleFactor: 0.75
      maxScaleFactor: 1.2
      yOffset: 0.0
      pitchVariability: 0.1

lighting:
   # Strength of the ambient lighting in the scene
   ambientStrength: 0.4
   # Power to use in the specular calcualtion
   specularPower: 16
   hdr:
      enabled: true
      # The exposure value to use in the tone mapping algorithm (larger = more light)
      exposure: 0.6
   gammaCorrection:
      enabled: true
      # The gamma value to use for gamma correction
      gamma: 2.2
   sun:
      # Display a polygon representation of the sun
      display: false
      # Automatically determine the sun's position from the sky HDR
      autoPosition: true
      # Number of sides to use in the polygon
      numSides: 10
      # Strength (and colour) of sun to use in lighting calculations
      strength: [ 3.8, 3.3, 3.2 ]
      # Position of the sun polygon and for use in lighting calculations (Currently overwritten by the HDR image)
      position: [ 50.0, 200.0, -50.0 ]
      # Scale of the sun polygon
      scale: 20.0
   sky:
      # Path to HDR file
      hdrFile: "textures/gamrig_8k.hdr"
      # Resolution of generated cubemap face textures
      resolution: 2048
   ssao:
      enabled: true
      # Number of samples in the kernel
      kernelSize: 32
      # Radius of samples
      radius: 0.5
      # Bias for depth test
      bias: 0.025
   translucency:
      enabled: true
      # Scalar for leaf translucency (larger = brighter impact from translucency)
      factor: 0.1
   shadows:
      enabled: true
      # Resolution of the shadow map (larger scenes will need a larger resolution to avoid pixelation artefacts/missing details at the cost of GPU memory)
      resolution: 4096
      # Bias to avoid shadow acne, reduce if shadows are detached from objects, increase if quantization errors (stripes) appear
      bias: 0.005
   volumetricScattering:
      enabled: true
      # Number of samples along each light ray (larger = higher resolution)
      numSamples: 100
      # Density of samples along each light ray (higher = samples taken from a smaller area = shorter, brighter rays)
      sampleDensity: 1.3
      # Impact decay of each sample (lower = greater fall-off)
      decay: 0.99 # range [0.0, 1.0)
      # Scalar for brightness of each ray (higher = brighter)
      exposure: 0.0015
      # Brightness value to clamp samples at
      maxBrightness: 100.0
```
