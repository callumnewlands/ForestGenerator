#version 330 core
in vec3 position;
in vec3 worldPos;
in vec3 normal;

out vec4 fragColour;

void main()
{
    // light properties
    vec3 lightPos = vec3(5.0f, 50.0f, 2.0f);
    vec3 lightCol = vec3(1.0f);

    // ambient
    float ambientStrength = 0.5f;
    vec3 ambient = ambientStrength * lightCol;

    // diffuse
    vec3 norm = normalize(normal);
    vec3 lightDir = normalize(lightPos - worldPos);
    float diff = max(dot(norm, lightDir), 0.0f);
    vec3 diffuse = diff * lightCol;

    vec3 vertexCol = worldPos.y < 0.01 ? vec3(0.1f, 0.3f, 0.1f) : vec3(0.34f, 0.17f, 0.07f);
    vec3 result = (ambient + diffuse) * vertexCol;
    fragColour = vec4(result, 1.0);
}