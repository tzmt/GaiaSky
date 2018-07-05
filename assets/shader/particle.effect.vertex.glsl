#version 120

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

<INCLUDE shader/lib_geometry.glsl>

attribute vec4 a_position;
attribute vec4 a_color;
// size
attribute float a_size;
// creation time
attribute float a_t;

uniform float u_alpha;

uniform mat4 u_projModelView;
uniform vec3 u_camPos;
uniform float u_sizeFactor;
uniform float u_t; // time in seconds

#ifdef relativisticEffects
    uniform vec3 u_velDir; // Velocity vector
    uniform float u_vc; // Fraction of the speed of light, v/c
    
<INCLUDE shader/lib_relativity.glsl>
#endif // relativisticEffects

#ifdef gravitationalWaves
    uniform vec4 u_hterms; // hpluscos, hplussin, htimescos, htimessin
    uniform vec3 u_gw; // Location of gravitational wave, cartesian
    uniform mat3 u_gwmat3; // Rotation matrix so that u_gw = u_gw_mat * (0 0 1)^T
    uniform float u_ts; // Time in seconds since start
    uniform float u_omgw; // Wave frequency
    <INCLUDE shader/lib_gravwaves.glsl>
#endif // gravitationalWaves
    
varying vec4 v_col;


<INCLUDE shader/lib_math.glsl>
// Time to live in seconds
#define ttl 0.1

void main() {
    vec3 pos = a_position.xyz - u_camPos;
    
    #ifdef relativisticEffects
        pos = computeRelativisticAberration(pos, length(pos), u_velDir, u_vc);
    #endif // relativisticEffects
    
    #ifdef gravitationalWaves
        pos = computeGravitationalWaves(pos, u_gw, u_gwmat3, u_ts, u_omgw, u_hterms);
    #endif // gravitationalWaves
    
    // Fade particles according to time to live (ttl)
    float live_time = u_t - a_t;
    float alpha = u_alpha * lint(live_time, 0.0, ttl, 1.0, 0.0); 
    
    v_col = vec4(a_color.rgb, a_color.a * alpha);

    gl_Position = u_projModelView * vec4(pos, 0.0);
    gl_PointSize = a_size * u_sizeFactor;
}
