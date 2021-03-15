#version 330 core
in vec3 textureCoord;

uniform sampler2D hdr;

out vec4 fragColour;

// Project spherical (xyz) coordinate onto a 2D (uv) equirectangular projection
vec2 sphericalToEquirectangular(vec3 pos)
{
    float theta = atan(pos.z, pos.x);// 2pi*u = arctan(z/x)
    float phi = asin(pos.y);// pi*v = arcsin(y)

    const vec2 piFractions = vec2(0.1591, 0.3183);// ( 1/(2pi), 1/pi )
    vec2 uv = vec2(theta, phi) * piFractions;// Normalise to range [-0.5, 0.5]
    uv += 0.5;// Range [0, 1]
    return uv;
}

void main()
{
    vec2 faceTexCoords = sphericalToEquirectangular(normalize(textureCoord));
    fragColour = vec4(texture(hdr, faceTexCoords).rgb, 1.0);
}