#version 330
in vec2 textureCoord;

uniform sampler2D diffuseTexture;

void main() {
    vec4 vertexCol = texture(diffuseTexture, textureCoord);
    if (vertexCol.a < 0.01) {
        discard;
    }

}
