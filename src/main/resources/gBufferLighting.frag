#version 330 core
in vec2 textureCoord;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;
uniform sampler2D ssao;
uniform sampler2D occlusion;
uniform sampler2D scatter;
uniform sampler2D gDepth;

uniform bool hdrEnabled;
uniform bool aoEnabled;
uniform bool renderDepth;

uniform float ambientStrength;
uniform vec3 lightPos;
uniform vec3 lightColour;
uniform mat4 view;
uniform mat4 projection;

out vec4 fragColour;


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
    if (length(occ) < 0.01) {
        if (aoEnabled) {
            vec3 ambient =  vec3(ambientStrength * diffuse * ambientOcclusion);
            vec3 diff = max(dot(norm, lightDir), 0.0f) * diffuse * lightColour;
            hdrColor = ambient + diff + scattering;
        } else {
            vec3 ambient = ambientStrength * lightColour;
            vec3 diff = max(dot(norm, lightDir), 0.0f) * lightColour;
            hdrColor = (ambient + diff) * diffuse + scattering;
        }

    } else {
        hdrColor = diffuse;
    }

    ////     Reinhard tone mapping
    //    vec3 screenColour = hdrEnabled ? hdrColor / (hdrColor + vec3(1.0)) : hdrColor;

    //     Exposure tone mapping
    // TODO uniform
    const float exposure = 1f;
    vec3 screenColour = hdrEnabled ? vec3(1.0) - exp(-hdrColor * exposure) : hdrColor;

    // gamma correction
    const float gamma = 2.2;
    vec3 gammaCorrected = pow(screenColour, vec3(1.0 / gamma));
    fragColour = vec4(gammaCorrected, 1.0);

}