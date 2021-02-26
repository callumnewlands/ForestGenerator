#version 330 core
in vec3 textureCoord;

layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;

uniform samplerCube skyboxTexture;

void main() {

    vec4 fragColour = texture(skyboxTexture, textureCoord);

    gPosition = textureCoord;
    gNormal = textureCoord;
    gAlbedoSpec.rgb = fragColour.rgb;
    gAlbedoSpec.a = 1.0f;
}
