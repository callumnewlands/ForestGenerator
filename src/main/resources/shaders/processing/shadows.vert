#version 330 core
layout (location = 0) in vec3 pos;
layout (location = 3) in mat4 instanceModel;
layout (location = 2) in vec2 texCoord;

uniform mat4 model;
uniform mat4 lightVP;
uniform bool isInstanced;

out vec2 textureCoord;

void main() {
    mat4 correctModel = isInstanced ? instanceModel : model;
    vec4 worldPos = correctModel * vec4(pos, 1.0);
    textureCoord = texCoord;
    gl_Position = lightVP * worldPos;
}
