#version 330 core
in vec3 textureCoord;

layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;
layout (location = 3) out vec3 gOcclusion;
layout (location = 4) out vec4 gTranslucency;
layout (location = 5) out vec3 gSegmentation;

uniform samplerCube skyboxTexture;

void main() {

    vec4 fragColour = texture(skyboxTexture, textureCoord);

    gPosition = textureCoord;
    gNormal = -textureCoord;
    gAlbedoSpec.rgb = fragColour.rgb;
    gAlbedoSpec.a = 1.0f;

    vec3 hdrColor = fragColour.rgb;
    gOcclusion = hdrColor;
    gTranslucency = vec4(0);
    gSegmentation = vec3(0.0, 0.0, 0.0); // Sky is black (BACKGROUND)
}
