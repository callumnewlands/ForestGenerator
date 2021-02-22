#version 330 core
in vec3 textureCoord;

uniform samplerCube skyboxTexture;

out vec4 fragColour;

void main()
{
    fragColour = texture(skyboxTexture, textureCoord);
}