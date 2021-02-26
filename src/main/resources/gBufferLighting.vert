#version 330 core
layout (location = 0) in vec3 pos;
layout (location = 2) in vec2 texCoord;

out vec2 textureCoord;

void main() {
    textureCoord = texCoord;
    gl_Position = vec4(pos, 1.0);
}
