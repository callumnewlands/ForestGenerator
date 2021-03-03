#version 330 core
in vec2 textureCoord;

uniform sampler2D occlusion;

uniform vec3 lightPos;
uniform mat4 view;
uniform mat4 projection;

const int NUM_SCATTERING_SAMPLES = 100;
const float density = 0.74f;// sample density
//const float weight = 1f; // intensity of each sample
const float decay = 1.0f;// light fall-off
const float exposure = 0.0034f;// light ray intensity

out vec3 fragColor;

void main()
{
    vec4 lightPosTransformed = (projection * view * vec4(lightPos, 1.0f));
    vec2 lightPosScreen = lightPosTransformed.xy / lightPosTransformed.w * 0.5 + 0.5;;
    // vector between samples in direction of light
    vec2 sampleDelta = textureCoord - lightPosScreen;
    sampleDelta *= 1f / NUM_SCATTERING_SAMPLES * density;
    vec3 colour = texture(occlusion, textureCoord).rgb;
    float illuminationDecay = 1.0f;

    // additive sampling
    vec2 scatteringCoordinate = textureCoord;
    for (int i = 0; i < NUM_SCATTERING_SAMPLES; i++) {
        scatteringCoordinate -= sampleDelta;
        vec3 sampleCol = texture(occlusion, scatteringCoordinate).rgb;
        sampleCol *= illuminationDecay;//* weight;
        colour += sampleCol;
        illuminationDecay *= decay;
    }
    fragColor = colour * exposure;
}