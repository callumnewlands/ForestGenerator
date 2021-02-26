#version 330 core
in vec3 position;
in vec3 worldPos;
in vec3 normal;
in mat3 TBN;
in vec2 textureCoord;

uniform vec3 modelColour;
uniform vec3 lightPos;
uniform float ambientStrength;
uniform vec3 lightColour;
uniform sampler2D diffuseTexture;

out vec4 fragColour;

void main()
{

    vec4 vertexCol = texture(diffuseTexture, textureCoord);
    if (vertexCol.a < 0.01) {
        discard;
    }
    // ambient
    vec3 ambient = ambientStrength * lightColour;

    // diffuse
    vec3 norm = normalize(normal);
    vec3 lightDir = normalize(lightPos - worldPos);
    float diff = max(dot(norm, lightDir), 0.0f);
    vec3 diffuse = diff * lightColour;
    fragColour = vec4(ambient + diffuse, 1.0) * vertexCol;
//    fragColour = vec4(norm, 1.0);

}