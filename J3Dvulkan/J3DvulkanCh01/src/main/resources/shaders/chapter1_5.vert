#version 450

const vec4 vertices[3] =
 vec4[](vec4(0.3, 0.3, 0.6, 1.0),
        vec4(0.3, -0.3, 0.6, 1.0),
        vec4(-0.3, 0.3, 0.6, 1.0));

void main()
{
    gl_Position = vertices[gl_VertexIndex];
}
