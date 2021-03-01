#version 330 core
in vec3 worldPos;
in vec3 normal;

out vec4 fragColour;

void main() {
    fragColour = vec4(1.0f, 0.98f, 0.96f, 1.0f);
}
