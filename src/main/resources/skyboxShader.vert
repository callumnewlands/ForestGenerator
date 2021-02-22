#version 330 core
layout (location = 0) in vec3 pos;

out vec3 textureCoord;

uniform mat4 view;
uniform mat4 projection;

void main()
{
    textureCoord = pos;
    vec4 clipPos = projection * view * vec4(pos, 1.0f);
    gl_Position = clipPos.xyww;
}