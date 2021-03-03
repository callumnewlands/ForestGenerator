#version 330 core
in vec3 position;
in vec3 worldPos;
in vec3 normal;
in mat3 TBN;
in vec2 textureCoord;

layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;
layout (location = 3) out float gOcclusion;

uniform sampler2D leaf_front;
uniform sampler2D leaf_transl_front;
uniform sampler2D leaf_TSNM_front;
uniform sampler2D leaf_TSHLM_front_t;

uniform sampler2D leaf_back;
uniform sampler2D leaf_transl_back;
uniform sampler2D leaf_TSNM_back;
uniform sampler2D leaf_TSHLM_back_t;

void main() {

    vec4 vertexCol = texture(leaf_front, textureCoord);
    if (vertexCol.a < 0.01) {
        discard;
    }

    vec3 mapNormal = (texture(leaf_TSNM_front, textureCoord).rgb * 2.0 - 1.0);
    vec3 norm = normalize(TBN * mapNormal);// TBN maps from tangent space to world space

    gPosition = worldPos;
    gNormal = norm;
    gAlbedoSpec.rgb = vertexCol.rgb;
    gAlbedoSpec.a = 0;
    gOcclusion = 0;
}
