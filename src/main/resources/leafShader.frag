#version 330 core
in vec3 position;
in vec3 worldPos;
in vec3 normal;
in mat3 TBN;
in vec2 textureCoord;

uniform float ambientStrength;
uniform vec3 modelColour;
uniform vec3 lightPos;
uniform vec3 viewPos;
uniform vec3 lightColour;

uniform sampler2D leaf_front;
uniform sampler2D leaf_transl_front;
uniform sampler2D leaf_TSNM_front;
uniform sampler2D leaf_TSHLM_front_t;

uniform sampler2D leaf_back;
uniform sampler2D leaf_transl_back;
uniform sampler2D leaf_TSNM_back;
uniform sampler2D leaf_TSHLM_back_t;

out vec4 fragColour;

// Code adapted from Habel et al. 2007 - Physically based realtime translucency for leaves

float D(float cosa, float m)
{
    return (1.0f / (pow(m, 2) * pow(cosa, 4))) * exp(-((1.0f / pow(cosa, 2) - 1.0f) / pow(m, 2)));
}

//This function computes the shadowing and masking term
float G(vec3 N, vec3 H, vec3 V, vec3 L)
{
    float m = min(2.0f * dot(N, H) * dot(N, V) / dot(V, H), 2.0f * dot(N, H) * dot(N, L) / dot(V, H));
    return min(1.0f, m);
}

//This function computes the fresnel term
float F(float n, float c)
{
    float g = sqrt(n * n + c * c - 1);
    return (0.5f) * (pow((g - c), 2) / pow((g + c), 2)) * (1 + pow((c * (g + c) - 1), 2) / pow((c * (g - c) + 1), 2));
}

void main() {
    vec4 front = texture(leaf_front, textureCoord);
    if (front.a < 0.01) {
        discard;
    }

    vec3 viewDir = normalize(viewPos - worldPos);
    vec3 lightDir = normalize(lightPos - worldPos);
    vec3 halfwayDir = normalize(lightDir + viewDir);
    vec3 normalDir = normalize(normal);

    // ambient
    vec3 ambient = ambientStrength * lightColour;

    vec4 col;
    if (dot(normalDir, viewDir) > 0) {

        vec3 mapNormal = (texture(leaf_TSNM_front, textureCoord).rgb * 2.0 - 1.0);
        vec3 norm = normalize(TBN * mapNormal);// TBN maps from tangent space to world space
        float diff = max(dot(norm, lightDir), 0.0f);
        vec3 mapHalflife = normalize(TBN * texture(leaf_TSHLM_front_t, textureCoord).rgb);

        #define SQRT6  0.40824829046386301636621401245098
        #define SQRT2  0.70710678118654752440084436210485
        #define SQRT3  0.57735026918962576450914878050196
        #define SQRT23 0.81649658092772603273242802490196

        float diff_t = dot(lightDir, TBN * vec3(-SQRT6, -SQRT2, -SQRT3)) * mapHalflife.x +
        dot(lightDir, TBN * vec3(-SQRT6, SQRT2, -SQRT3)) * mapHalflife.y +
        dot(lightDir, TBN * vec3(SQRT23, 0, -SQRT3)) * mapHalflife.z;

        #define REF_INDEX 1.6
        #define ROUGHNESS 0.6

        float fresnel = F(REF_INDEX, dot(viewDir, halfwayDir));
        float rough = D(dot(norm, halfwayDir), ROUGHNESS);
        float specular = (rough / dot(norm, lightDir)) * max(0, (G(norm, halfwayDir, viewDir, lightDir) / dot(norm, viewDir)) * fresnel/3.14);

        vec4 front_t = texture(leaf_transl_front, textureCoord);
        col.xyz = (front.xyz*(max(0, diff)+ambient)+ front_t.xyz*(max(0, diff_t))) * lightColour;// TODO + specular;
        col.w = front.w;
    } else {
        vec3 mapNormal = (texture(leaf_TSNM_back, textureCoord).rgb * 2.0 - 1.0);
        vec3 norm = normalize(TBN * mapNormal);// TBN maps from tangent space to world space
        norm.x = -norm.x;
        norm.y = -norm.y;

        float diff = max(dot(norm, -lightDir), 0.0f);
        vec3 mapHalflife = normalize(TBN * texture(leaf_TSHLM_back_t, textureCoord).rgb);

        float diff_t = dot(lightDir, TBN * vec3(-SQRT6, -SQRT2, -SQRT3)) * mapHalflife.x +
        dot(lightDir, TBN * vec3(-SQRT6, SQRT2, -SQRT3)) * mapHalflife.y +
        dot(lightDir, TBN * vec3(SQRT23, 0, -SQRT3)) * mapHalflife.z;

        vec4 back = texture(leaf_back, textureCoord);
        vec4 back_t = texture(leaf_transl_back, textureCoord);
        col.xyz = (back.xyz*(max(0, diff)+ambient) + back_t.xyz*(max(0, diff_t))) * lightColour;
        col.w = back.w;
    }
    fragColour = col;
}