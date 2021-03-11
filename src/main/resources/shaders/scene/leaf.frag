#version 330 core
in vec3 worldPos;
in vec3 normal;
in mat3 TBN;
in vec2 textureCoord;

layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;
layout (location = 3) out float gOcclusion;
layout (location = 4) out vec4 gTranslucency;

uniform vec3 lightPos;
uniform vec3 viewPos;

// TS = tangent space, NM = normal map
uniform sampler2D leaf_front;
uniform sampler2D leaf_transl_front;
uniform sampler2D leaf_TSNM_front;
uniform sampler2D leaf_TSHLM_front_t;

uniform sampler2D leaf_back;
uniform sampler2D leaf_transl_back;
uniform sampler2D leaf_TSNM_back;
uniform sampler2D leaf_TSHLM_back_t;

void main() {
    mat3 TBN_inv = transpose(TBN);
    vec3 viewDir = normalize(viewPos - worldPos);
    vec3 lightDir_TS = TBN_inv * normalize(lightPos - worldPos);
    vec3 normalDir = normalize(normal);

    vec3 mapHalflife;
    vec3 norm;
    vec4 vertexCol;
    vec4 translColour;
    if (dot(normalDir, viewDir) > 0) {
        vec3 mapNormal = (texture(leaf_TSNM_front, textureCoord).rgb * 2.0 - 1.0);
        norm = normalize(TBN * mapNormal);// TBN maps from tangent space to world space
        vertexCol = texture(leaf_front, textureCoord);
        mapHalflife = texture(leaf_TSHLM_front_t, textureCoord).rgb;
        translColour = texture(leaf_transl_front, textureCoord);
    } else {
        vec3 mapNormal = (texture(leaf_TSNM_back, textureCoord).rgb * 2.0 - 1.0);
        norm = normalize(TBN * mapNormal);// TBN maps from tangent space to world space
        vertexCol = texture(leaf_back, textureCoord);
        mapHalflife = texture(leaf_TSHLM_back_t, textureCoord).rgb;
        translColour = texture(leaf_transl_back, textureCoord);
    }

    if (vertexCol.a < 0.01) {
        discard;
    }

    const float SQRT6 = 0.40824829046386301636621401245098;// sqrt(1/6)
    const float SQRT2 = 0.70710678118654752440084436210485;// sqrt(1/2)
    const float SQRT3 = 0.57735026918962576450914878050196;// sqrt(1/3)
    const float SQRT23 = 0.81649658092772603273242802490196;// sqrt(2/3)

    float diffTransl =  dot(lightDir_TS, vec3(-SQRT6, -SQRT2, -SQRT3)) * mapHalflife.x +
    dot(lightDir_TS, vec3(-SQRT6, SQRT2, -SQRT3)) * mapHalflife.y +
    dot(lightDir_TS, vec3(SQRT23, 0, -SQRT3)) * mapHalflife.z;

    gPosition = worldPos;
    gNormal = norm;
    gAlbedoSpec.rgb = vertexCol.rgb;
    gAlbedoSpec.a = 0;// TODO specular
    gOcclusion = 0;
    gTranslucency.rgb = translColour.rgb;
    gTranslucency.a = max(0.0f, diffTransl);
}
