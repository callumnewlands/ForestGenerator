#version 330 core
in vec2 textureCoord;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;
uniform sampler2D ssao;
uniform sampler2D occlusion;
uniform sampler2D scatter;
uniform sampler2D gDepth;
uniform sampler2D shadowMap;

uniform bool hdrEnabled;
uniform bool aoEnabled;
uniform bool shadowsEnabled;
uniform bool renderDepth;

uniform float ambientStrength;
uniform vec3 lightPos;
uniform vec3 lightColour;
uniform mat4 view;
uniform mat4 projection;
uniform mat4 lightVP;

out vec4 fragColour;

// from learnopengl.com: https://learnopengl.com/Advanced-Lighting/Shadows/Shadow-Mapping
float shadowCalculation(vec4 fragPosLightSpace, vec3 normal, vec3 lightDir)
{
    // perform perspective divide
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    // transform to [0,1] range
    projCoords = projCoords * 0.5 + 0.5;
    // get closest depth value from light's perspective (using [0,1] range fragPosLight as coords)
    float closestDepth = texture(shadowMap, projCoords.xy).r;
    // get depth of current fragment from light's perspective
    float currentDepth = projCoords.z;
    // check whether current frag pos is in shadow

    float bias = max(0.05 * (1.0 - dot(normal, lightDir)), 0.005);

    // PCF
    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
    for (int x = -1; x <= 1; ++x)
    {
        for (int y = -1; y <= 1; ++y)
        {
            float pcfDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
            shadow += currentDepth - bias > pcfDepth  ? 1.0 : 0.0;
        }
    }
    shadow /= 9.0;

    // Non-PCF
    //    float shadow = currentDepth - bias > closestDepth  ? 1.0 : 0.0;

    if (projCoords.z > 1.0) {
        shadow = 1.0;// Default to shadowed if past far plane of view frustum
    }
    return shadow;
}

void main() {

    vec3 worldPos = texture(gPosition, textureCoord).rgb;
    vec3 normal = texture(gNormal, textureCoord).rgb;
    vec3 diffuse = texture(gAlbedoSpec, textureCoord).rgb;
    float specular = texture(gAlbedoSpec, textureCoord).a;
    float ambientOcclusion = texture(ssao, textureCoord).r;
    vec3 occ = texture(occlusion, textureCoord).rgb;
    vec3 scattering = texture(scatter, textureCoord).rgb;
    float depth = texture(gDepth, textureCoord).r;

    if (renderDepth) {
        float z = depth * 2.0 - 1.0;// nonlinear in [-1, 1]
        const float far = 300;
        const float near = 0.1;
        z = (2.0 * near * far) / (far + near - z * (far - near));// linear in [far, near]
        const float max = 20;
        fragColour = vec4(vec3(z / max), 1.0);
        return;
    }


    vec3 hdrColor;
    vec3 norm = normalize(normal);
    vec3 lightDir = normalize(lightPos - worldPos);
    vec4 lightSpacePos = lightVP * vec4(worldPos, 1.0);
    float shadow = shadowsEnabled ? shadowCalculation(lightSpacePos, norm, lightDir) : 0;
    if (length(occ) < 0.0001) {
        if (aoEnabled) {
            vec3 ambient =  vec3(ambientStrength * diffuse * ambientOcclusion);
            vec3 diff = max(dot(norm, lightDir), 0.0f) * diffuse * lightColour;
            hdrColor = ambient + (1.0 - shadow) * diff + scattering;
        } else {
            vec3 ambient = ambientStrength * lightColour;
            vec3 diff = max(dot(norm, lightDir), 0.0f) * lightColour;
            hdrColor = (ambient + (1.0 - shadow) * diff) * diffuse + scattering;
        }

    } else {
        hdrColor = diffuse;
    }

    ////     Reinhard tone mapping
    //    vec3 screenColour = hdrEnabled ? hdrColor / (hdrColor + vec3(1.0)) : hdrColor;

    //     Exposure tone mapping
    // TODO uniform
    const float exposure = 0.9f;
    vec3 screenColour = hdrEnabled ? vec3(1.0) - exp(-hdrColor * exposure) : hdrColor;

    // gamma correction
    const float gamma = 2.2;
    vec3 gammaCorrected = pow(screenColour, vec3(1.0 / gamma));
    fragColour = vec4(gammaCorrected, 1.0);


    //    fragColour = vec4(vec3(texture(shadowMap, textureCoord).r), 1.0);
}