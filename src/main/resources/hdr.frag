#version 330 core
in vec2 textureCoord;

uniform sampler2D hdrBuffer;
uniform bool hdrEnabled;

out vec4 fragColour;

void main() {
    const float gamma = 2.2;
    vec3 hdrColor = texture(hdrBuffer, textureCoord).rgb;
    if (hdrEnabled)
    {
        // reinhard
        vec3 result = hdrColor / (hdrColor + vec3(1.0));

        // exposure
        //        vec3 result = vec3(1.0) - exp(-hdrColor * exposure);

        // gamma correction
        result = pow(result, vec3(1.0 / gamma));
        fragColour = vec4(result, 1.0);
    }
    else
    {
        // gamma correction
        vec3 result = pow(hdrColor, vec3(1.0 / gamma));
        fragColour = vec4(result, 1.0);
    }
}
