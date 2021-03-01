#version 330 core
in vec2 textureCoord;

uniform bool hdrEnabled;
uniform bool aoEnabled;
uniform float ambientStrength;
uniform vec3 lightPos;
uniform vec3 lightColour;
uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;
uniform sampler2D ssao;
uniform mat4 projection;

out vec4 fragColour;

void main() {

    vec3 worldPos = texture(gPosition, textureCoord).rgb;
    vec3 normal = texture(gNormal, textureCoord).rgb;
    vec3 diffuse = texture(gAlbedoSpec, textureCoord).rgb;
    float specular = texture(gAlbedoSpec, textureCoord).a;
    float ambientOcclusion = texture(ssao, textureCoord).r;

    vec3 hdrColor;
    vec3 norm = normalize(normal);
    vec3 lightDir = normalize(lightPos - worldPos);
    if (aoEnabled) {
        vec3 ambient =  vec3(ambientStrength * diffuse * ambientOcclusion);
        vec3 diff = max(dot(norm, lightDir), 0.0f) * diffuse * lightColour;
        hdrColor = ambient + diff;
    } else {
        vec3 ambient = ambientStrength * lightColour;
        vec3 diff = max(dot(norm, lightDir), 0.0f) * lightColour;
        hdrColor = (ambient + diff) * diffuse;
    }

    const float gamma = 2.2;

    if (hdrEnabled)
    {
        // reinhard
        vec3 result = hdrColor / (hdrColor + vec3(1.0));

        // gamma correction
        result = pow(result, vec3(1.0 / gamma));
        fragColour = vec4(result, 1.0);
    }
    else
    {
        // gamma correction
        vec3 result = pow(hdrColor, vec3(1.0 / gamma));
        fragColour = vec4(result, 1.0);
    }
}