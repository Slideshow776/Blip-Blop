// Author: shadertoy.com
// Title: the 'new' color variant

#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform vec2 u_mouse;
uniform float u_time;

void main() {
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = gl_FragCoord.xy/u_resolution.xy;

    // Time varying pixel color
    vec3 color = 0.5 + 0.5*cos(u_time*.1+uv.xyx+vec3(0,2,4));
    color *= vec3(.2, .2, .2);

    // Output to screen
    gl_FragColor = vec4(color,1.0);
}