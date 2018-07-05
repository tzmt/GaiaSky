package gaia.cu9.ari.gaiaorbit.util.math;

import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

public class Intersectord {

    private static Vector3d auxd1 = new Vector3d(), auxd2 = new Vector3d(), auxd3 = new Vector3d();

    /**
     * Quick check whether the given {@link Ray} and {@link BoundingBoxd}
     * intersect.
     * 
     * @param ray
     *            The ray
     * @param center
     *            The center of the bounding box
     * @param dimensions
     *            The dimensions (width, height and depth) of the bounding box
     * @return Whether the ray and the bounding box intersect.
     */
    public static boolean intersectRayBoundsFast(Rayd ray, Vector3d center, Vector3d dimensions) {
        final double divX = 1f / ray.direction.x;
        final double divY = 1f / ray.direction.y;
        final double divZ = 1f / ray.direction.z;

        double minx = ((center.x - dimensions.x * .5f) - ray.origin.x) * divX;
        double maxx = ((center.x + dimensions.x * .5f) - ray.origin.x) * divX;
        if (minx > maxx) {
            final double t = minx;
            minx = maxx;
            maxx = t;
        }

        double miny = ((center.y - dimensions.y * .5f) - ray.origin.y) * divY;
        double maxy = ((center.y + dimensions.y * .5f) - ray.origin.y) * divY;
        if (miny > maxy) {
            final double t = miny;
            miny = maxy;
            maxy = t;
        }

        double minz = ((center.z - dimensions.z * .5f) - ray.origin.z) * divZ;
        double maxz = ((center.z + dimensions.z * .5f) - ray.origin.z) * divZ;
        if (minz > maxz) {
            final double t = minz;
            minz = maxz;
            maxz = t;
        }

        double min = Math.max(Math.max(minx, miny), minz);
        double max = Math.min(Math.min(maxx, maxy), maxz);

        return max >= 0 && max >= min;
    }

    public static boolean checkIntersectRaySpehre(Vector3d linePoint0, Vector3d linePoint1, Vector3d sphereCenter, double sphereRadius) {
        double cx = sphereCenter.x;
        double cy = sphereCenter.y;
        double cz = sphereCenter.z;

        double px = linePoint0.x;
        double py = linePoint0.y;
        double pz = linePoint0.z;

        double vx = linePoint1.x - px;
        double vy = linePoint1.y - py;
        double vz = linePoint1.z - pz;

        double A = vx * vx + vy * vy + vz * vz;
        double B = 2.0 * (px * vx + py * vy + pz * vz - vx * cx - vy * cy - vz * cz);
        double C = px * px - 2 * px * cx + cx * cx + py * py - 2 * py * cy + cy * cy + pz * pz - 2 * pz * cz + cz * cz - sphereRadius * sphereRadius;

        // discriminant
        double D = B * B - 4 * A * C;

        return D >= 0;
    }

    public synchronized static Array<Vector3d> intersectRaySphere(Vector3d linePoint0, Vector3d linePoint1, Vector3d sphereCenter, double sphereRadius) {
        // http://www.codeproject.com/Articles/19799/Simple-Ray-Tracing-in-C-Part-II-Triangles-Intersec

        double cx = sphereCenter.x;
        double cy = sphereCenter.y;
        double cz = sphereCenter.z;

        double px = linePoint0.x;
        double py = linePoint0.y;
        double pz = linePoint0.z;

        double vx = linePoint1.x - px;
        double vy = linePoint1.y - py;
        double vz = linePoint1.z - pz;

        double A = vx * vx + vy * vy + vz * vz;
        double B = 2.0 * (px * vx + py * vy + pz * vz - vx * cx - vy * cy - vz * cz);
        double C = px * px - 2 * px * cx + cx * cx + py * py - 2 * py * cy + cy * cy + pz * pz - 2 * pz * cz + cz * cz - sphereRadius * sphereRadius;

        // discriminant
        double D = B * B - 4 * A * C;

        Array<Vector3d> result = new Array<Vector3d>(2);

        if (D < 0) {
            return result;
        }

        double t1 = (-B - Math.sqrt(D)) / (2.0 * A);

        Vector3d solution1 = auxd1.set(linePoint0.x * (1 - t1) + t1 * linePoint1.x, linePoint0.y * (1 - t1) + t1 * linePoint1.y, linePoint0.z * (1 - t1) + t1 * linePoint1.z);
        if (D == 0) {
            result.add(solution1);
            return result;
        }

        double t2 = (-B + Math.sqrt(D)) / (2.0 * A);
        Vector3d solution2 = auxd2.set(linePoint0.x * (1 - t2) + t2 * linePoint1.x, linePoint0.y * (1 - t2) + t2 * linePoint1.y, linePoint0.z * (1 - t2) + t2 * linePoint1.z);

        // prefer a solution that's on the line segment itself

        if (Math.abs(t1 - 0.5) < Math.abs(t2 - 0.5)) {
            result.add(solution1);
            result.add(solution2);
            return result;
        }

        result.add(solution2);
        result.add(solution1);
        return result;
    }

    public static boolean checkIntersectSegmentSphere(Vector3d linePoint0, Vector3d linePoint1, Vector3d sphereCenter, double sphereRadius) {
        Array<Vector3d> solutions = intersectRaySphere(linePoint0, linePoint1, sphereCenter, sphereRadius);
        // Test each point
        int n = solutions.size;
        for (int i = 0; i < n; i++) {
            if (isBetween(linePoint0, linePoint1, solutions.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * TODO: Not working well due to aux vectors
     * @param linePoint0
     * @param linePoint1
     * @param sphereCenter
     * @param sphereRadius
     * @return
     */
    public static Array<Vector3d> intersectSegmentSphere(Vector3d linePoint0, Vector3d linePoint1, Vector3d sphereCenter, double sphereRadius) {
        Array<Vector3d> solutions = intersectRaySphere(linePoint0, linePoint1, sphereCenter, sphereRadius);
        int n = solutions.size;
        if (n <= 0) {
            return solutions;
        } else {
            Array<Vector3d> newSolutions = new Array<Vector3d>(n);
            // Test each point
            for (int i = 0; i < n; i++) {
                if (isBetween(linePoint0, linePoint1, solutions.get(i))) {
                    newSolutions.add(solutions.get(i));
                }
            }

            return newSolutions;
        }
    }

    /**
     * Returns true if c is between a and b. Assumes c is colinear with a and b.
     * 
     * @param a
     *            Point A
     * @param b
     *            Point B
     * @param c
     *            Point to check
     * @return
     */
    private static boolean isBetween(Vector3d a, Vector3d b, Vector3d c) {
        // -epsilon < (distance(a, c) + distance(c, b) - distance(a, b)) < epsilon
        double ab = a.dst(b);
        double ac = a.dst(c);
        double cb = c.dst(b);

        // ab * 1e-6 is our target precision
        double epsilon = ab * 1e-6 / 2;

        double value = ac + cb - ab;

        return -epsilon < value && value < epsilon;

    }

    /**
     * Returns the shortest distance between the line defined by x1 and x2 and
     * the point x0. See <a href=
     * "http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html">here</a>.
     * 
     * @param x1
     *            Segment first point
     * @param x2
     *            Segment second point
     * @param x0
     *            Point to test
     * @return The minimum distance between the line and the point
     */
    public synchronized static double distanceLinePoint(Vector3d x1, Vector3d x2, Vector3d x0) {
        Vector3d crs = auxd1;
        Vector3d aux1 = auxd2.set(x0).sub(x2);
        double nominador = crs.set(x0).sub(x1).crs(aux1).len();
        double denominador = aux1.set(x2).sub(x1).len();
        return nominador / denominador;
    }

    /**
     * Calculates the euclidean distance from a point to a line segment.
     * 
     * @param v
     *            the point
     * @param a
     *            start of line segment
     * @param b
     *            end of line segment
     * @return distance from v to line segment [a,b]
     */
    public synchronized static double distanceSegmentPoint(final Vector3d a, final Vector3d b, final Vector3d v) {
        final Vector3d ab = auxd1.set(b).sub(a);
        double ablen = ab.len();
        final Vector3d av = auxd2.set(v).sub(a);
        double avlen = av.len();

        if (av.dot(ab) <= 0.0) // Point is lagging behind start of the segment, so perpendicular distance is not viable.
            return avlen; // Use distance to start of segment instead.

        final Vector3d bv = auxd3.set(v).sub(b);
        double bvlen = bv.len();

        if (bv.dot(ab) >= 0.0) // Point is advanced past the end of the segment, so perpendicular distance is not viable.
            return bvlen; // Use distance to end of the segment instead.

        return (ab.crs(av)).len() / ablen; // Perpendicular distance of point to segment.
    }

}
