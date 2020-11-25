#version 460 core
layout (location = 0) in vec3 pos;

out vec3 position;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
    position = vec3(model * vec4(pos, 1.0));
    gl_Position = projection * view * vec4(position, 1.0f);
}