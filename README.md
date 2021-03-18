# ForestGenerator

## Prerequisites

* Java 14
* OpenGL 4.3

## Building the application (from source)

1. Run ```mvn clean install``` in the root directory
2. Navigate into the ```target/``` directory
3. In the root directory run the command:
   ```java -jar ForestGenerator-1.0-SNAPSHOT-jar-with-dependencies.jar [config.yaml]```
   * ```params.yaml``` (optional) is the path to the yaml config file (see below)
   * Note: The ```resourcesRoot``` configuration parameter will need to be set to ```./classes/```

## Running the Application (from release)

1. Download and extract the latest release file (.zip)
2. In the root directory run the command:
   ```java -jar ForestGenerator-1.0-SNAPSHOT.jar [config.yaml]```
   * ```params.yaml``` (optional) is the path to the yaml config file (see below)

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
| 3     | (use config file) | Toggle HDR tone mapping                |
| 4     | (use config file) | Screen-space ambient-occlustion (SSAO) |
| 5     | (use config file) | Toggle depth output                    |
| 6     | (use config file) | Toggle shadows                         |
| 7     | (use config file) | Toggle leaf translucency               |
| Mouse | I                 | Look up                                |
| Mouse | K                 | Look down                              |
| Mouse | J                 | Look left                              |
| Mouse | L                 | Look right                             |

## YAML Configuration File

Many aspects of the application can be configured through the use of YAML configuration files. See ```default.yaml```
for a file containing all parameters and their default values.

To override these values, create a new .yaml file containing the parameters you with to override and their new values.

Parameters written with integers in ```default.yaml``` are integers, parameters written with decimals (e.g. 1.0, 2.992)
are floating -point numbers

### default.yaml:

```yaml
# relative path to the directory containing texture and model resources
resourcesRoot: ./resources/

random:
   # The value of the random seed for the application
   seed: -1 # A value of -1 will result in a seed derived from the system clock value

input:
   # Enable keyboard and mouse input (via the application window)
   manual: true # It is advised to set one of manual XOR stdin to true
   # Enable input via stdin (each line will cause a new frame to render)
   stdin: false # Note: Enabling stdin will case render loop to block after each frame for input and will disable delta time

output:
   window:
      visible: true
      fullscreen: true
      width: 800
      height: 600
   # Output the frames of the application to the /frames directory
   frameImages: false
   # Output a colour image
   colour: true
   # Output a depth image
   depth: false # If outputting to window the depth output will be displayed instead of colour if this is true, if rendering to images, both can be output
   # Invert the depth colours (default: near = black, far = white)
   invertDepth: true


camera:
   startPosition: [ 0.0, 3.3, 0.0 ]
   # Enable/disable vertical motion
   verticalMovement: true

terrain:
   # The size of the scene to generate
   width: 200.0
   # The vertical scaling on the terrain geometry (larger value = taller terrain)
   verticalScale: 3.0
   noise:
      xScale: 1.0
      yScale: 1.0
      # The number of octaves of perlin noise (detail) to use (higher = more detail)
      octaves: 8
      # The amplitude multiplier for successive octaves (smaller = smoother terrain)
      persistence: 0.5
      # The frequency multiplier for successive octaves (>1 => each octave will contribute finer details)
      lacunarity: 2.0
   # Density of vertices in the terrain mesh (higher = more vertices)
   vertexDensity: 1.0

quadtree:
   # Levels of detail (LODs) in the quad-tree
   levels: 2
   # Distance multiplier for switching (LOD) (smaller = LOD threshold is closer)
   thresholdCoefficient: 1.7
   # Enable view-frustum culling of geometry
   frustumCulling: true

sceneObjects:
   display: true
   # List of tree types and their generation parameters
   trees:
      - !Parameters$SceneObjects$BranchingTree
         name: tree 1
         lSystemParams:
            a: 0.3308
            lr: 1.109
            vr: 1.832
            e: 0.052
         branchings:
            - angles: [ 3.14159 ]
              prob: 0.3
            - angles: [ 1.6535, 2.3148 ]
              prob: 0.7
         # The fraction of all models of this tree which are instanced from a given generated mesh (random configuration) (Larger = more repeated instances)
         instanceFraction: 0.2
         # Size scalar for the models
         scale: 0.01
         # Number of sides to use in the cross section of the branches
         numSides: 6
         # Minimum number of iterations of the L-system
         minIterations: 7 # inclusive
         # Maximum number of iterations of the L-system
         maxIterations: 9 # exclusive
         # Density of models in the scene
         density: 1.0
   twigs:
      typesPerQuad: 2
      numSides: 5
      # Density of models in the scene (to disable set to 0)
      density: 1.0
   rocks:
      # Density of models in the scene (to disable set to 0)
      density: 1.0
   grass:
      # Density of models in the scene (to disable set to 0)
      density: 1.0
   fallenLeaves:
      # Density of models in the scene (to disable set to 0)
      density: 1.0

lighting:
   # Strength of the ambient lighting in the scene
   ambientStrength: 0.2
   hdr:
      enabled: true
      # The exposure value to use in the tone mapping algorithm (larger = more light)
      exposure: 0.9
   gammaCorrection:
      enabled: true
      # The gamma value to use for gamma correction
      gamma: 2.2
   sun:
      # Display a polygon representation of the sun
      display: false
      # Number of sides to use in the polygon
      numSides: 10
      # Strength of sun to use in lighting calculations
      strength: 1.0
      # Position of the sun polygon and for use in lighting calculations (Currently overwritten by the HDR image)
      position: [ 50.0, 200.0, -50.0 ]
      # Scale of the sun polygon
      scale: 20.0
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
      factor: 0.5
   shadows:
      enabled: true
      # Resolution of the shadow map (larger scenes will need a larger resolution to avoid pixelation artefacts at the cost of GPU memory
      resolution: 4096
   volumetricScattering:
      enabled: true
      # Number of samples along each light ray
      numSamples: 100
      # Density of samples along each light ray
      sampleDensity: 0.9
      # Impact decay of each sample
      decay: 1.0 # range [0.0, 1.0]
      # Scalar for brightness of each ray
      exposure: 0.004
```
