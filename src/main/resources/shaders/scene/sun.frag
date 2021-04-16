#version 330 core

in vec3 normal;

out vec4 fragColour;
layout (location = 2) out vec4 gAlbedoSpec;
layout (location = 3) out vec3 gOcclusion;
layout (location = 4) out vec4 gTranslucency;

uniform vec3 lightColour;

void main() {
    fragColour = vec4(lightColour, 1.0f);
//    gAlbedoSpec.rgb = vec3(1, 0, 0);
    gAlbedoSpec.rgb = lightColour;
    gAlbedoSpec.a = 1.0f;
    gOcclusion = lightColour;
    gTranslucency = vec4(0);
}
