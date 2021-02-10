#version 330 core
layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 norm;
layout (location = 2) in vec2 texCoord;
layout (location = 3) in mat4 instanceModel;

out vec3 position;
out vec3 worldPos;
out vec3 normal;
out vec2 textureCoord;

//uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
    worldPos = vec3(instanceModel * vec4(pos, 1.0));
    // TODO which normal is correct?
    //    normal = mat3(transpose(inverse(model))) * norm;
    normal = norm;
    gl_Position = projection * view * vec4(worldPos, 1.0f);
    textureCoord = texCoord;
}