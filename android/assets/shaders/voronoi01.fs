// Author: Txoka from ShaderToy.com
// Title: Background example

#ifdef GL_ES
precision highp float;
#endif

#define PI 3.14159265359

uniform vec2 u_resolution;
uniform float u_time;
uniform vec3 u_color;

float rand(vec2 co) {
    return fract(sin(mod(dot(co.xy ,vec2(12.9898,78.233)),3.14))*43758.5453);
}

float tnoise(vec2 co){
    vec2 w=co;
    co.y+=co.x/2.;
    const vec2 s=vec2(1.,0.);
	vec2 p=floor(co);
    if(fract(co.x)<fract(co.y))p+=0.5;
  	return rand(p);
}

void main(){
    vec2 uv = (gl_FragCoord.xy*2.-u_resolution.xy)/190.;
    float n=tnoise(uv);
    vec3 fragColor = vec3(sin(u_time*n*2.+n*PI*2.)*0.5+0.5)*0.3+0.5;
    fragColor+=sin((uv.x-uv.y)*30.)/2.;
    fragColor+=rand(uv)/2.;
    fragColor *= u_color;

    gl_FragColor = vec4(fragColor, 1.);
}
