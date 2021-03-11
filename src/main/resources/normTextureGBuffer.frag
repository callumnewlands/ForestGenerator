#version 330 core
in vec3 position;
in vec3 worldPos;
in mat3 TBN;
in vec2 textureCoord;

layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;
layout (location = 3) out vec3 gOcclusion;
layout (location = 4) out vec4 gTranslucency;

uniform sampler2D diffuseTexture;
uniform sampler2D normalTexture;

void main() {

    vec4 vertexCol = texture(diffuseTexture, textureCoord);
    if (vertexCol.a < 0.01) {
        discard;
    }

    vec3 mapNormal = (texture(normalTexture, textureCoord).rgb * 2.0 - 1.0);
    vec3 norm = normalize(TBN * mapNormal);// TBN maps from tangent space to world space

    gPosition = worldPos;
    gNormal = norm;
    gAlbedoSpec.rgb = vertexCol.rgb;
    gAlbedoSpec.a = 0;
    gOcclusion = vec3(0);
    gTranslucency = vec4(0);
}
