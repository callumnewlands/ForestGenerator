#version 330 core
in vec2 textureCoord;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;
uniform sampler2D ssao;
uniform sampler2D occlusion;
uniform sampler2D gTranslucency;
uniform sampler2D scatter;
uniform sampler2D gDepth;
uniform sampler2D shadowMap;

uniform bool hdrEnabled;
uniform bool gammaEnabled;
uniform bool aoEnabled;
uniform bool shadowsEnabled;
uniform bool translucencyEnabled;
uniform bool renderDepth;
uniform bool invertDepth;

uniform float gamma;
uniform float translucencyFactor;
uniform float toneExposure;
uniform float ambientStrength;
uniform vec3 lightPos;
uniform vec3 viewPos;
uniform vec3 lightColour;
uniform mat4 view;
uniform mat4 projection;
uniform mat4 lightVP;
uniform float farPlane;
uniform float maxDepthOutput;
uniform float shadowBias;
uniform int specularPower;

out vec4 fragColour;

float shadowCalculation(vec4 lightSpacePos, vec3 normal, vec3 lightDir) {

    // Position of fragment in the light's screen space
    vec3 lightScreenSpacePos = lightSpacePos.xyz / lightSpacePos.w  * 0.5 + 0.5;// Light clip-space to screen space range [0,1]
    float lightDistance = lightScreenSpacePos.z;

    float bias = shadowBias;

    // Average the 3x3 kernel around the position
    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
    for (int x = -1; x <= 1; ++x) {
        for (int y = -1; y <= 1; ++y) {
            float mapDistance = texture(shadowMap, lightScreenSpacePos.xy + vec2(x, y) * texelSize).r;
            shadow += lightDistance - bias > mapDistance  ? 1.0 : 0.0;
        }
    }
    shadow /= 9.0;

    if (lightDistance > 1.0) {
        shadow = 0.0;// Default to unshadowed if past far plane of view frustum
    }
    return shadow;
}

void main() {

    vec3 worldPos = texture(gPosition, textureCoord).rgb;
    vec3 normal = texture(gNormal, textureCoord).rgb;
    vec3 diffuse = texture(gAlbedoSpec, textureCoord).rgb;
    vec3 translColour = texture(gTranslucency, textureCoord).rgb;
    float pixelTranslFactor = translucencyEnabled ? texture(gTranslucency, textureCoord).a : 0.0f;
    float spec = texture(gAlbedoSpec, textureCoord).a;
    float ambientOcclusion = texture(ssao, textureCoord).r;
    vec3 occ = texture(occlusion, textureCoord).rgb;
    vec3 scattering = texture(scatter, textureCoord).rgb;
    float depth = texture(gDepth, textureCoord).r;

    if (renderDepth) {
        float z = depth * 2.0 - 1.0;// nonlinear in [-1, 1]
        float far = farPlane;
        const float near = 0.1;
        z = (2.0 * near * far) / (far + near - z * (far - near)) + 0.000001;// linear in [far, near]
        float maxDepth = maxDepthOutput;
        fragColour = vec4(vec3(min(z / maxDepth, 1.0)), 1.0);
        if (invertDepth) fragColour = 1.0 - fragColour;
        return;
    }

    vec3 norm = normalize(normal);
    vec3 lightDir = normalize(lightPos - worldPos);
    vec3 viewDir = normalize(viewPos - worldPos);
    vec3 halfwayDir = normalize(lightDir + viewDir);
    vec4 lightSpacePos = lightVP * vec4(worldPos, 1.0);
    float shadow = shadowsEnabled ? shadowCalculation(lightSpacePos, norm, lightDir) : 0;

    vec3 screenColour;
    if (length(occ) < 0.0001) {
        vec3 ambient;
        if (aoEnabled) {
            ambient =  ambientStrength * diffuse * ambientOcclusion;
        } else {
            ambient = ambientStrength * diffuse;
        }

        float diffFactor = max(dot(norm, lightDir), 0.0f);
        float specFactor = pow(max(dot(norm, halfwayDir), 0.0), specularPower);

        vec3 hdrColor =  ambient +
        (1.0 - shadow) * diffFactor * lightColour * diffuse +
        translucencyFactor * translColour * pixelTranslFactor * lightColour +
        (1.0 - shadow) * specFactor * lightColour * spec +
        scattering;

        screenColour = hdrColor;
        if (hdrEnabled) {
            //Exposure tone mapping
            screenColour = vec3(1.0) - exp(-hdrColor * toneExposure);
        }
    } else {
        screenColour = diffuse;
    }


    // gamma correction
    vec3 gammaCorrected = gammaEnabled ? pow(screenColour, vec3(1.0 / gamma)) : screenColour;
    fragColour = vec4(gammaCorrected, 1.0);

    //     fragColour = vec4(texture(shadowMap, textureCoord).xyz, 1.0);
}