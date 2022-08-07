#version 330 core
in vec3 worldPos;
in vec3 normal;
in mat3 TBN;
in vec2 textureCoord;

layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;
layout (location = 3) out vec3 gOcclusion;
layout (location = 4) out vec4 gTranslucency;
layout (location = 5) out vec3 gSegmentation;

uniform vec3 viewPos;
uniform bool hasNormalMap;
uniform sampler2D diffuseTexture;
uniform sampler2D normalTexture;

void main() {

    vec3 normalDir = normalize(normal);
    vec3 norm;
    if (hasNormalMap) {
        vec3 mapNormal = (texture(normalTexture, textureCoord).rgb * 2.0 - 1.0);
        norm = normalize(TBN * mapNormal);// TBN maps from tangent space to world space
    } else {
        norm = normalDir;
    }

    vec3 viewDir = normalize(viewPos - worldPos);
    if (dot(normalDir, viewDir) < 0) {
        // If viewing back face, invert normal
        norm = -norm;
    }

    vec4 vertexCol = texture(diffuseTexture, textureCoord);
    float alpha = pow(abs(dot(viewDir, normalDir)), 1.2);
    if (vertexCol.a < 0.01 || alpha < 0.01) {
        discard;
    }

    gPosition = worldPos;
    gNormal = norm;
    gAlbedoSpec.rgb = vertexCol.rgb;
    gAlbedoSpec.a = 0;
    gOcclusion = vec3(0);
    gTranslucency = vec4(0);
    gSegmentation = vec3(0.5, 0.5, 0.5); // Only grass should be enabled, we class this as TERRAIN (grey)
}
