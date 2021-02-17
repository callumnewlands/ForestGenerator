#version 330 core
in vec3 position;
in vec3 worldPos;
in mat3 TBN;
in vec2 textureCoord;

uniform vec3 modelColour;
uniform vec3 lightPos;
uniform vec3 viewPos;
uniform vec3 lightColour;
uniform sampler2D diffuseTexture;
uniform sampler2D normalTexture;

out vec4 fragColour;

void main() {

    // ambient
    float ambientStrength = 0.5f;
    vec3 ambient = ambientStrength * lightColour;

    // diffuse
    vec3 mapNormal = (texture(normalTexture, textureCoord).rgb * 2.0 - 1.0);
    vec3 norm = normalize(TBN * mapNormal);// TBN maps from tangent space to world space
    vec3 lightDir = normalize(lightPos - worldPos);
    float diff = max(dot(norm, lightDir), 0.0f);
    vec3 diffuse = diff * lightColour;

    vec4 vertexCol = texture(diffuseTexture, textureCoord);
    vec3 viewDir = normalize(viewPos - worldPos);
    float alpha = pow(abs(dot(viewDir, norm)), 2);
    if (vertexCol.a < 0.01 || alpha < 0.01) {
        discard;
    }

    fragColour = vec4(ambient + diffuse, alpha) * vertexCol;
    //        fragColour = vec4(norm, 1.0);

}