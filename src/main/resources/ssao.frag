#version 330 core
in vec2 textureCoord;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D texNoise;

uniform vec3 samples[32];
uniform vec2 noiseScale;
uniform mat4 projection;
uniform mat4 view;

uniform int kernelSize;
uniform float radius;
uniform float bias;

out float fragColor;

void main()
{
    // TODO switch into storing view-space coords to avoid this expensive maths
    mat3 viewNormMat = transpose(inverse(mat3(view)));

    vec3 fragPos = vec3(view * vec4(texture(gPosition, textureCoord).xyz, 1.0f));// to view-space
    vec3 normal = normalize(vec3(viewNormMat * texture(gNormal, textureCoord).rgb));// to view-space
    vec3 randomVec = normalize(texture(texNoise, textureCoord * noiseScale).xyz);

    vec3 tangent = normalize(randomVec - normal * dot(randomVec, normal));
    vec3 bitangent = cross(normal, tangent);
    mat3 TBN = mat3(tangent, bitangent, normal);

    float occlusion = 0.0;
    for (int i = 0; i < kernelSize; ++i)
    {
        vec3 samplePos = TBN * samples[i];// to view-space
        samplePos = fragPos + samplePos * radius;

        vec4 offset = vec4(samplePos, 1.0);
        offset = projection * offset;// to clip-space
        offset.xyz /= offset.w;// perspective divide
        offset.xyz = offset.xyz * 0.5 + 0.5;// range 0.0 - 1.0

        float sampleDepth = vec3(view * vec4(texture(gPosition, offset.xy).xyz, 1.0f)).z;// to view-space

        float rangeCheck = smoothstep(0.0, 1.0, radius / abs(fragPos.z - sampleDepth));
        occlusion += ((sampleDepth >= samplePos.z + bias) ? 1.0 : 0.0) * rangeCheck;
    }
    occlusion = 1.0 - (occlusion / kernelSize);

    fragColor = occlusion;
}