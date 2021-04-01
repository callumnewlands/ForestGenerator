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

uniform sampler2D leafFront;
uniform sampler2D leafFrontTranslucency;
uniform sampler2D leafFrontNorm;
uniform sampler2D leafFrontHalfLife;

uniform sampler2D leafBack;
uniform sampler2D leafBackTranslucency;
uniform sampler2D leafBackNorm;
uniform sampler2D leafBackHalfLife;

uniform vec3 colourFilter;
uniform float mixFactor;
uniform bool expMix;

uniform bool hasNormalMap;
uniform bool hasTranslucencyMap;
uniform bool hasHalfLifeBasisMap;

vec4 colourise(vec4 inp) {
    if (inp.a < 0.01 || mixFactor == 0.0) {
        return inp;
    }
    float luminance = dot(inp.rgb, vec3(0.2126f, 0.7152f, 0.0722f));
    vec4 greyscale = vec4(vec3(luminance), 1.0);

    vec4 colourised;
    if (expMix) {
        float colorAverage = (colourFilter.r + colourFilter.g + colourFilter.b) / 3;
        vec4 colorPow = 1.0 + (colorAverage - vec4(colourFilter, 1.0)) * 2.0;
        colourised = pow(greyscale, colorPow);
    } else {
        colourised = greyscale * vec4(colourFilter, 1.0);
    }
    return mix(inp, colourised, mixFactor);
}

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
        // Viewing front of leaf
        if (hasNormalMap) {
            vec3 mapNormal = (texture(leafFrontNorm, textureCoord).rgb * 2.0 - 1.0);
            norm = normalize(TBN * mapNormal);// TBN maps from tangent space to world space
        } else {
            norm = normalize(normal);
        }
        vertexCol = colourise(texture(leafFront, textureCoord));
        mapHalflife = hasHalfLifeBasisMap ? texture(leafFrontHalfLife, textureCoord).rgb : vec3(0.5);
        translColour = hasTranslucencyMap ? texture(leafFrontTranslucency, textureCoord) : vertexCol;
        translColour = colourise(translColour);
    } else {
        // Viewing back of leaf
        if (hasNormalMap) {
            vec3 mapNormal = (texture(leafBackNorm, textureCoord).rgb * 2.0 - 1.0);
            norm = normalize(TBN * mapNormal);// TBN maps from tangent space to world space
        } else {
            norm = normalize(normal);
        }
        // Invert normal so it is coming
        norm = -norm;
        vertexCol = colourise(texture(leafBack, textureCoord));
        mapHalflife = hasHalfLifeBasisMap ? texture(leafBackHalfLife, textureCoord).rgb : vec3(0.5);
        translColour = hasTranslucencyMap ? texture(leafBackTranslucency, textureCoord) : vertexCol;
        translColour = colourise(translColour);
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
    gAlbedoSpec.a = 0;
    gOcclusion = 0;
    gTranslucency.rgb = translColour.rgb;
    gTranslucency.a = max(0.0f, diffTransl);
}
