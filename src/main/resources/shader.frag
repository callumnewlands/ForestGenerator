#version 330 core
in vec3 position;
in vec3 worldPos;
in vec3 normal;

uniform vec3 modelColour;
uniform vec3 lightPos;
uniform vec3 lightColour;

out vec4 fragColour;

void main()
{

    // ambient
    float ambientStrength = 0.5f;
    vec3 ambient = ambientStrength * lightColour;

    // diffuse
    vec3 norm = normalize(normal);
    vec3 lightDir = normalize(lightPos - worldPos);
    float diff = max(dot(norm, lightDir), 0.0f);
    vec3 diffuse = diff * lightColour;

    vec3 vertexCol = modelColour;
    vec3 result = (ambient + diffuse) * vertexCol;
    fragColour = vec4(result, 1.0);
    //    fragColour = vec4(diffuse, 1.0);
}