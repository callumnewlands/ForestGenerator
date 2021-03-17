#version 330 core
in vec2 textureCoord;

uniform sampler2D occlusion;

uniform vec3 lightPos;
uniform vec3 viewPos;
uniform vec3 viewDir;
uniform mat4 view;
uniform mat4 projection;

uniform int numSamples;
uniform float sampleDensity;// sample density
uniform float decay;// light fall-off
uniform float exposure;// light ray intensity

out vec3 fragColor;

void main()
{
    vec4 lightPosTransformed = (projection * view * vec4(lightPos, 1.0f));
    vec2 lightPosScreen = (lightPosTransformed.xy / lightPosTransformed.w) * 0.5 + 0.5;;
    // vector between samples in direction of light
    vec2 sampleDelta = textureCoord - lightPosScreen;
    sampleDelta *= (1f / numSamples) * sampleDensity;
    vec3 originalColour = texture(occlusion, textureCoord).rgb;
    vec3 colour = originalColour;
    float illuminationDecay = 1.0f;

    // additive sampling
    vec2 scatteringCoordinate = textureCoord;
    for (int i = 0; i < numSamples; i++) {
        scatteringCoordinate -= sampleDelta;
        vec3 sampleCol = texture(occlusion, scatteringCoordinate).rgb;
        sampleCol *= illuminationDecay;//* weight;
        colour += sampleCol;
        illuminationDecay *= decay;
    }
    fragColor = colour * exposure;

    // TODO not sure this is the correct way to do this
    vec3 lightDir = normalize(lightPos - viewPos);
    if (dot(lightDir, viewDir) < 0.1) {
        fragColor = originalColour;
    }
}