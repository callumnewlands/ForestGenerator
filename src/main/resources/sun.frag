#version 330 core
in vec3 worldPos;
in vec3 normal;

out vec4 fragColour;
layout (location = 3) out vec3 gOcclusion;

void main() {
    fragColour = vec4(1.0f, 0.98f, 0.96f, 1.0f);
    gOcclusion = vec3(1.5f);
}
