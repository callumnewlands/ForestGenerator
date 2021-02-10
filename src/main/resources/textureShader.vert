#version 330 core
layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 norm;
layout (location = 2) in vec2 texCoord;
layout (location = 7) in vec3 tang;

out vec3 position;
out vec3 worldPos;
out vec3 normal;
out vec3 tangent;
out mat3 TBN;
out vec2 textureCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
    worldPos = vec3(model * vec4(pos, 1.0));
    mat3 normalMatrix = transpose(inverse(mat3(model)));
    vec3 T = normalize(normalMatrix * tang);
    vec3 N = normalize(normalMatrix * norm);
    T = normalize(T - dot(T, N) * N);// Not sure what this line does
    vec3 B = cross(N, T);
    TBN = mat3(T, B, N);

    normal = N;
    tangent = T;

    gl_Position = projection * view * vec4(worldPos, 1.0f);
    textureCoord = texCoord;
}