#version 330 core
in vec3 position;
in vec3 worldPos;
in vec3 normal;
in mat3 TBN;
in vec2 textureCoord;

layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;
layout (location = 3) out vec3 gOcclusion;

uniform sampler2D diffuseTexture;

void main() {

    vec4 vertexCol = texture(diffuseTexture, textureCoord);
    if (vertexCol.a < 0.01) {
        discard;
    }

    vec3 norm = normalize(normal);

    gPosition = worldPos;
    gNormal = norm;
    gAlbedoSpec.rgb = vertexCol.rgb;
    gAlbedoSpec.a = 0;
    gOcclusion = vec3(0);
}
