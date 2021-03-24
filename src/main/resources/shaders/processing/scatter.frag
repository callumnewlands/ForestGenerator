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
uniform bool hdrEnabled;
uniform float toneExposure;

out vec3 fragColor;

void main()
{
    vec4 lightPosTransformed = (projection * view * vec4(lightPos, 1.0f));
    vec2 lightPosScreen = (lightPosTransformed.xy / lightPosTransformed.w) * 0.5 + 0.5;;
    // vector between samples in direction of light
    vec2 sampleDelta = textureCoord - lightPosScreen;
    sampleDelta *= (1.0f / numSamples) * sampleDensity;
    vec3 originalColour = texture(occlusion, textureCoord).rgb;
    vec3 colour = originalColour;
    float illuminationDecay = 1.0f;

    // additive sampling
    vec2 scatteringCoordinate = textureCoord;
    for (int i = 0; i < numSamples; i++) {
        scatteringCoordinate -= sampleDelta;
        vec3 sampleCol = texture(occlusion, scatteringCoordinate).rgb;
        // TODO param
        if (length(sampleCol) > 100f) {
            sampleCol = vec3(100f);
        }
        sampleCol *= illuminationDecay;
        colour += sampleCol;
        illuminationDecay *= decay;
    }
    fragColor = colour * exposure;

    if (hdrEnabled) {
        //        // Reinhard tone mapping
        //        fragColor = fragColor / (fragColor + vec3(1.0));
        //        //Exposure tone mapping
        //        fragColor = vec3(1.0) - exp(-fragColor * toneExposure);
        //        // Reinhard-Jodie tone mapping
        //        float luminance = dot(fragColor, vec3(0.2126f, 0.7152f, 0.0722f));
        //        vec3 tv = fragColor / (1.0f + fragColor);
        //        fragColor = mix(fragColor / (1.0f + luminance), tv, tv);
        // Extended Reinhard tone mapping
        float luminance = dot(fragColor, vec3(0.2126f, 0.7152f, 0.0722f));
        fragColor = fragColor / (vec3(1.0f) + luminance);
    }
}
