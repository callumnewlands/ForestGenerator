#version 330 core
in vec3 textureCoord;

uniform sampler2D hdr;

out vec4 fragColour;

// from learnopengl.com
const vec2 invAtan = vec2(0.1591, 0.3183);
vec2 sampleSphericalMap(vec3 v)
{
    vec2 uv = vec2(atan(v.z, v.x), asin(v.y));
    uv *= invAtan;
    uv += 0.5;
    return uv;
}

void main()
{
    vec2 faceTexCoords = sampleSphericalMap(normalize(textureCoord));
    fragColour = vec4(texture(hdr, faceTexCoords).rgb, 1.0);
}