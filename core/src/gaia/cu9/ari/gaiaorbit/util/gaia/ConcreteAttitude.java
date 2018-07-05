package gaia.cu9.ari.gaiaorbit.util.gaia;

import gaia.cu9.ari.gaiaorbit.util.coord.Coordinates;
import gaia.cu9.ari.gaiaorbit.util.coord.NslSun;
import gaia.cu9.ari.gaiaorbit.util.math.Quaterniond;
import gaia.cu9.ari.gaiaorbit.util.math.Vector3d;

/**
 * This class implements the Attitude interface and contains just the minimum
 * fields necessary to define a unique attitude at a given time, plus a large
 * number of methods to compute quantities that depend only on this attitude and
 * time.
 * 
 * The various scanning laws and specific attitude representations (e.g., using
 * splines, or tables for interpolation) can be implemented as providing
 * attitude objects of this type.
 * 
 * In some cases (in particular for simple analytical scanning laws) there are
 * more direct ways to retrieve some of the quantities for which there are
 * methods in this class (e.g., {@link #getHeliotropicAnglesRates()}); the
 * present method(s) may then be overridden for efficiency.
 * 
 * @author Lennart Lindegren, Uwe Lammers
 */
public class ConcreteAttitude implements Attitude {

    private static final double BASICANGLE_DEGREE = 106.5;
    private static final Vector3d[] xyz = new Vector3d[] { new Vector3d(), new Vector3d(), new Vector3d() };
    private static final Vector3d[] fovDirections = new Vector3d[] { new Vector3d(), new Vector3d() };
    private static final Vector3d aux = new Vector3d();
    /**
     * time to which the attitude refers, in elapsed ns since the reference epoch
     */
    private long t;

    /**
     * attitude quaternion at time t
     */
    private Quaterniond q;

    /**
     * time derivative of the attitude at time t
     */
    private Quaterniond qDot = null;

    // half the conventional basic angle Gamma [rad]
    private double halfGamma = Math.toRadians(.5 * BASICANGLE_DEGREE);

    /**
     * Construct object from time, and a quaternion. This leaves the time
     * derivative undefined. It can be set later with
     * {@link #setQuaternionDot(Quaterniond)}
     * 
     * @param t
     *            time of the attitude
     * @param q
     *            quaternion
     */
    public ConcreteAttitude(long t, Quaterniond q, boolean withZeroSigmaCorr) {
        this(t, q, null, withZeroSigmaCorr);
    }

    /**
     * Construct object from time, quaternion and its derivative.
     * 
     * @param t
     *            time of the attitude
     * @param q
     *            quaternion
     * @param qDot
     *            time derivativ of quaternion [1/day]
     */
    public ConcreteAttitude(long t, Quaterniond q, Quaterniond qDot,
            boolean withZeroSigmaCorr) {
        this.t = t;
        this.q = q;
        this.qDot = qDot;
    }

    /**
     */
    @Override
    public long getTime() {
        return t;
    }

    /**
     * Set the time of the attitude. This usually does not make sense as the
     * time is set during construction of the object
     *
     * @param time
     *            time of the attitude in [ns] since reference epoch
     */
    public void setTime(long time) {
        t = time;
    }

    /**
     */
    @Override
    public Quaterniond getQuaternion() {
        return q;
    }

    /**
     * The the quaternion of the attitude. * Set the time of the attitude. This
     * usually does not make sense as the time is set during construction of the
     *
     * @param q
     */
    public void setQuaternion(Quaterniond q) {
        this.q = q;
    }

    /**
     * Get the time derivative of the attitude.
     * 
     * @return time derivative of the attitude quaternion [1/day]
     */
    @Override
    public Quaterniond getQuaternionDot() {
        return qDot;
    }

    /**
     * @param qDot
     *            quaternion derivative to set - all components in [1/day]
     */
    public void setQuaternionDot(Quaterniond qDot) {
        this.qDot = qDot;
    }

    /**
     */
    public HeliotropicAnglesRates getHeliotropicAnglesRates() {
        HeliotropicAnglesRates anglesAndRates = new HeliotropicAnglesRates();

        // k is a unit vector (in ICRS) towards the north ecliptic pole:
        double obliquity = Coordinates.OBLIQUITY_RAD_J2000;
        double cosObliquity = Math.cos(obliquity);
        double sinObliquity = Math.sin(obliquity);
        Vector3d k = new Vector3d(0.0, -sinObliquity, cosObliquity);

        // s is a unit vector (in ICRS) towards the nominal sun:
        NslSun sun = new NslSun();
        double cosLSun = Math.cos(sun.getSolarLongitude());
        double sinLSun = Math.sin(sun.getSolarLongitude());
        Vector3d s = new Vector3d(cosLSun, sinLSun * cosObliquity, sinLSun
                * sinObliquity);

        // xyz[0], xyz[1], xyz[2] are unit vectors (in ICRS) along the SRS axes:
        getSrsAxes(xyz);

        // m = s x z is a non-unit vector (of length sinXi) normal to the plane
        // containing s and z:
        Vector3d m = aux;
        m.set(s);
        m.crs(xyz[2]);

        // compute solar aspect angle xi in range [0, pi]:
        double sinXi = m.len();
        double cosXi = s.dot(xyz[2]);
        anglesAndRates.setFirstAngle(Math.atan2(sinXi, cosXi));

        // NOTE: all subsequent computations fail if sinXi = 0

        // compute revolving phase angle nu in range [-pi, pi]:
        double sinXiCosNu = k.dot(m);
        double sinXiSinNu = k.dot(xyz[2]);
        anglesAndRates.setSecondAngle(Math.atan2(sinXiSinNu, sinXiCosNu));

        // compute spin phase Omega:
        double sinXiCosOmega = -m.dot(xyz[1]);
        double sinXiSinOmega = -m.dot(xyz[0]);
        anglesAndRates.setThirdAngle(Math.atan2(sinXiSinOmega, sinXiCosOmega));

        // inertial spin rate in ICRS:
        Vector3d spin = getSpinVectorInIcrs();

        // subtract motion of the nominal sun to get heliotropic spin rate:
        Vector3d spinHel = new Vector3d(spin);
        spinHel.scaleAdd(-sun.getSolarLongitudeDot(), k);

        // scalar products with s, z, and m are used to determine the angular
        // rates:
        double sSpinHel = s.dot(spinHel);
        double zSpinHel = xyz[2].dot(spinHel);
        double mSpinHel = m.dot(spinHel);
        // d(xi)/dt:
        anglesAndRates.setFirstRate(mSpinHel / sinXi);
        // d(nu)/dt:
        anglesAndRates.setSecondRate((sSpinHel - zSpinHel * cosXi)
                / (sinXi * sinXi));
        // d(Omega)/dt:
        anglesAndRates.setThirdRate((zSpinHel - sSpinHel * cosXi)
                / (sinXi * sinXi));

        return anglesAndRates;
    }

    /**
     */
    @Override
    public Vector3d getSpinVectorInSrs() {
        // Using (A.18) in AGIS paper (A&A 538, A78, 2012):
        Quaterniond tmp = q.cpy();
        tmp.inverse().mul(qDot);
        return new Vector3d(2. * tmp.x, 2. * tmp.y, 2. * tmp.z);
    }

    /**
     */
    @Override
    public Vector3d getSpinVectorInIcrs() {
        // Using (A.17) in AGIS paper (A&A 538, A78, 2012):
        Quaterniond tmp = qDot.cpy();
        tmp.mulInverse(q);
        return new Vector3d(2. * tmp.x, 2. * tmp.y, 2. * tmp.z);
    }

    /**
     */
    @Override
    public Vector3d[] getFovDirections() {
        // half the nominal basic angle:
        double halfBasicAngle = 0.5 * Math.toRadians(BASICANGLE_DEGREE);

        // xyz[0], xyz[1], xyz[2] are unit vectors (in ICRS) along the SRS axes:
        getSrsAxes(xyz);
        Vector3d xScaled = xyz[0].scl(Math.cos(halfBasicAngle));
        Vector3d yScaled = xyz[1].scl(Math.sin(halfBasicAngle));

        // PFoV = x * cos(halfBasicAngle) + y * sin(halfBasicAngle):
        fovDirections[0].set(xScaled).add(yScaled); // .set(xScaled).add(yScaled);

        // FFoV = x * cos(halfBasicAngle) - y * sin(halfBasicAngle):
        fovDirections[1].set(xScaled).sub(yScaled);

        return fovDirections;
    }

    /**
     * @see gaia.cu9.ari.gaiaorbit.util.gaia.Attitude#getSrsAxes(Vector3d[])
     */
    @Override
    public Vector3d[] getSrsAxes(Vector3d[] xyz) {
        // computed from q using vector rotation on three unit vectors
        xyz[0].set(1, 0, 0).rotateVectorByQuaternion(q);
        xyz[1].set(0, 1, 0).rotateVectorByQuaternion(q);
        xyz[2].set(0, 0, 1).rotateVectorByQuaternion(q);

        return xyz;
    }

    /**
     */
    @Override
    public double[] getAlAcRates(double alInstrumentAngle, double acFieldAngle) {
        // Formulas (11) and (12) from GAIA-LL-056 : valid for any scanning law
        double cphi = Math.cos(alInstrumentAngle);
        double sphi = Math.sin(alInstrumentAngle);
        double tzeta = Math.tan(acFieldAngle);

        // The inertial rate in SRS in [rad/s]:
        Vector3d spinRate = getSpinVectorInSrs().scl(86400.);
        // Along scan speed in rad/s
        double phip = -spinRate.z
                + (spinRate.x * cphi + spinRate.y * sphi) * tzeta;
        // Across scan speed in rad/s
        double zetap = -spinRate.x * sphi + spinRate.y * cphi;

        return new double[] { phip, zetap };
    }

    /**
     */
    @Override
    public double[] getAlAcRates(FOV fov, double alFieldAngle,
            double acFieldAngle) {

        return getAlAcRates(alFieldAngle + fov.getNumericalFieldIndex()
                * halfGamma, acFieldAngle);
    }
}
