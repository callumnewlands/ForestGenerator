#version 330
in vec2 textureCoord;

uniform sampler2D diffuseTexture;
uniform sampler2D leafFront;
uniform bool isLeaf;

void main() {
    vec4 vertexCol = isLeaf ? texture(leafFront, textureCoord) : texture(diffuseTexture, textureCoord);
    if (vertexCol.a < 0.01) {
        discard;
    }

}
