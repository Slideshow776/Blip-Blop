#ifdef GL_ES
precision mediump float;
#endif

uniform float u_time;
uniform vec2 u_resolution;
#define PI 3.1415926535897932384626433832795

float rand(vec2 co)
{
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main()
{
    vec2 st = gl_FragCoord.xy/u_resolution.xy;

	float size = 30.0;
	float prob = 0.95;

	vec2 pos = floor(1.0 / size * st.xy);

	float color = 0.0;
	float starValue = rand(pos);

	if (starValue > prob)
	{
		vec2 center = size * pos + vec2(size, size) * 0.5;

		float t = 0.9 + 0.2 * sin(u_time + (starValue - prob) / (1.0 - prob) * 45.0);

		color = 1.0 - distance(st.xy, center) / (0.5 * size);
		color = color * t / (abs(st.y - center.y)) * t / (abs(st.x - center.x));
	}
	else if (rand(st.xy / u_resolution.xy) > 0.996)
	{
		float r = rand(st.xy);
		color = r * (0.25 * sin(u_time * (r * 5.0) + 720.0 * r) + 0.75);
	}

	gl_FragColor = vec4(vec3(color), 1.0);
}
