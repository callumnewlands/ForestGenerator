#version 330 core
in vec3 textureCoord;

layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;
layout (location = 3) out vec3 gOcclusion;
layout (location = 4) out vec4 gTranslucency;

uniform samplerCube skyboxTexture;
uniform bool hdrEnabled;
uniform float toneExposure;

void main() {

    vec4 fragColour = texture(skyboxTexture, textureCoord);

    gPosition = textureCoord;
    gNormal = -textureCoord;
    gAlbedoSpec.rgb = fragColour.rgb;
    gAlbedoSpec.a = 1.0f;

    vec3 hdrColor = fragColour.rgb;
    vec3 screenColour = hdrEnabled ? vec3(1.0) - exp(-hdrColor * toneExposure) : hdrColor;
    gOcclusion = screenColour;
    gTranslucency = vec4(0);
}
