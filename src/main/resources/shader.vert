#version 330 core
layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 norm;

out vec3 position;
out vec3 worldPos;
out vec3 normal;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
    worldPos = vec3(model * vec4(pos, 1.0));
    normal = mat3(transpose(inverse(model))) * norm;
    gl_Position = projection * view * vec4(worldPos, 1.0f);
}