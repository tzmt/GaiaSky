#version 120

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform float u_viewAngle;
uniform float u_viewAnglePow;
uniform float u_thOverFactor;
uniform float u_thOverFactorScl;
uniform float u_componentAlpha;
uniform vec4 u_color;

varying vec4 v_color;
varying vec2 v_texCoords;
varying float v_opacity;

void main()
{
   float thOverFac = u_thOverFactor * u_thOverFactorScl;
   v_opacity = clamp((pow(u_viewAngle, u_viewAnglePow) - thOverFac) / thOverFac, 0.0, 0.95) * u_componentAlpha;
   v_color = u_color;
   v_texCoords = a_texCoord0;
   
   gl_Position =  u_projTrans * a_position;
}
