#version 330 core
layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 norm;
layout (location = 2) in vec2 texCoord;

out vec3 position;
out vec3 worldPos;
out vec3 normal;
out vec2 textureCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
    worldPos = vec3(model * vec4(pos, 1.0));
    // TODO which normal is correct? Depends on whether the normals are in object-space or tangent-space I think
    //    normal = mat3(transpose(inverse(view * model))) * norm;
    normal = mat3(transpose(inverse(model))) * norm;
    //    normal = norm;
    gl_Position = projection * view * vec4(worldPos, 1.0f);
    textureCoord = texCoord;
}