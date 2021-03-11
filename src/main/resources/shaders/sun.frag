#version 330 core
in vec3 worldPos;
in vec3 normal;

out vec4 fragColour;
layout (location = 2) out vec4 gAlbedoSpec;
layout (location = 3) out vec3 gOcclusion;
layout (location = 4) out vec4 gTranslucency;

void main() {
    fragColour = vec4(1.0f, 0.98f, 0.96f, 1.0f);
    gAlbedoSpec.rgb = vec3(1f);
    gAlbedoSpec.a = 1f;
    gOcclusion = vec3(1f);
    gTranslucency = vec4(0);
}
