#define PI 3.1415927
#define PI2 3.1415927 * 2.0

// Returns >=0 if visible, <0 if not visible 
float in_view(vec3 pos, vec3 dir, float dist, float angle_edge) {
    return angle_edge - acos(dot(pos, dir) / dist);
}

/*
 * Gets the quaternion to rotate around the given axis by the given angle in radians
 * 
 */
vec4 get_quat_rotation(vec3 axis, float radians) {
    float d = 1.0 / length(axis);
    float l_ang;
    if(radians < 0.0) {
        l_ang = PI2 - (mod(-radians, PI2));
    }else {
        l_ang = mod(radians, PI2);
    }
    float l_sin = sin(l_ang / 2.0);
    float l_cos = cos(l_ang / 2.0);
    return  normalize(vec4(d * axis.x * l_sin, d * axis.y * l_sin, d * axis.z * l_sin, l_cos));
}

vec4 quat_mult(vec4 q1, vec4 q2) { 
  vec4 qr;
  qr.x = (q1.w * q2.x) + (q1.x * q2.w) + (q1.y * q2.z) - (q1.z * q2.y);
  qr.y = (q1.w * q2.y) - (q1.x * q2.z) + (q1.y * q2.w) + (q1.z * q2.x);
  qr.z = (q1.w * q2.z) + (q1.x * q2.y) - (q1.y * q2.x) + (q1.z * q2.w);
  qr.w = (q1.w * q2.w) - (q1.x * q2.x) - (q1.y * q2.y) - (q1.z * q2.z);
  return qr;
}

vec4 quat_conj(vec4 q) { 
  return vec4(-q.x, -q.y, -q.z, q.w); 
}


vec3 rotate_vertex_position(vec3 position, vec3 axis, float angle)
{ 
  vec4 q = get_quat_rotation(axis, angle);
  vec3 v = position.xyz;
  return v + 2.0 * cross(q.xyz, cross(q.xyz, v) + q.w * v);
}