#version 330 core
layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 norm;
layout (location = 2) in vec2 texCoord;
layout (location = 3) in mat4 instanceModel;
layout (location = 7) in vec3 tang;

out vec3 position;
out vec3 worldPos;
out vec3 normal;
out mat3 TBN;
out vec2 textureCoord;

uniform mat4 view;
uniform mat4 projection;

void main()
{
    worldPos = vec3(instanceModel * vec4(pos, 1.0));

    mat3 normalMatrix = transpose(inverse(mat3(instanceModel)));
    vec3 T = normalize(normalMatrix * tang);
    vec3 N = normalize(normalMatrix * norm);
    // re-orthogonalize T with respect to N
    T = normalize(T - dot(T, N) * N);
    vec3 B = cross(N, T);
    TBN = mat3(T, B, N);
    normal = N;

    gl_Position = projection * view * vec4(worldPos, 1.0f);
    textureCoord = texCoord;
}