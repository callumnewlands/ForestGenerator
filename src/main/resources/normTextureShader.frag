#version 330 core
in vec3 position;
in vec3 worldPos;
in vec3 normal;
in vec2 textureCoord;

uniform vec3 modelColour;
uniform vec3 lightPos;
uniform sampler2D diffuseTexture;
uniform sampler2D normalTexture;

out vec4 fragColour;

void main()
{
    // light properties
    vec3 lightCol = vec3(1.2f);

    // ambient
    float ambientStrength = 0.5f;
    vec3 ambient = ambientStrength * lightCol;

    // diffuse
    vec3 mapNormal = normalize(texture(normalTexture, textureCoord).rgb * 2.0 - 1.0);
    vec3 norm = normalize(mapNormal);
    vec3 lightDir = normalize(lightPos - worldPos);
    float diff = max(dot(norm, lightDir), 0.0f);
    vec3 diffuse = diff * lightCol;

    vec4 vertexCol = texture(diffuseTexture, textureCoord);
    if (vertexCol.a < 0.01) {
        discard;
    }
    fragColour = vec4(ambient + diffuse, 1.0) * vertexCol;
    //    fragColour = vec4(norm, 1.0);
    //        fragColour = vec4(textureCoord.x, textureCoord.y, textureCoord.x + textureCoord.y, 1.0f);

}