package gaia.cu9.ari.gaiaorbit.util.gaia;

import gaia.cu9.ari.gaiaorbit.util.math.Quaterniond;
import gaia.cu9.ari.gaiaorbit.util.math.Vector3d;

/**
 * Implementation of the Ecliptic Pole Scanning Law (EPSL)
 *
 * @author Lennart Lindegren
 * @version $Id: Epsl.java 373288 2014-06-24 10:35:37Z pbalm $
 */
public class Epsl extends AnalyticalAttitudeDataServer {
    /**
     * Ecliptic pole scanning has two modes: PRECEDING (revolving phase angle =
     * 0) and FOLLOWING (revolving phase angle = 180 deg). PRECEDING mean that
     * the spin axis of Gaia precedes the Sun by the solar aspect angle (45 deg)
     * on the ecliptic.
     */
    public enum Mode {
        /** preceding scanning mode */
        PRECEDING,
        /** following scanning mode */
        FOLLOWING
    }

    ;

    /** The current mode **/
    private Mode currentMode;

    /** The unit vector towards the North Ecliptic Pole, expressed in ICRS **/
    static final Vector3d NECLP = new Vector3d(-Math.sin(OBLIQUITY_RAD), Math.cos(OBLIQUITY_RAD), 0.0);
    static final Vector3d[] xyz = new Vector3d[] { new Vector3d(), new Vector3d(), new Vector3d() };

    /** Auxiliary vector **/
    private Vector3d spinVector;

    /**
     * The spin phase becomes a continuous function of time when represented as
     * omega + TWO_PI * omegaRevs:
     */
    private double omega;
    private int omegaRevs;

    /**
     * Default constructor (uses Mode = PRECEDING):
     */
    public Epsl() {
        currentMode = Mode.PRECEDING;
        setDefault();
    }

    /**
     * Constructor that allows to initialize preceding or following EPSL:
     *
     * @param mode
     *            PRECEDING or FOLLOWING
     */
    public Epsl(Mode mode) {
        currentMode = mode;
        setDefault();
    }

    /**
     * @see gaia.cu9.ari.gaiaorbit.util.gaia.BaseAttitudeDataServer#getAttitude(long)
     *
     * @param t - the time elapsed since the epoch of J2010 in ns (TCB)
     * @return attitude for the given time
     */
    @Override
    public synchronized Attitude getAttitudeNative(long t) {
        // Set time to calculate solar longitude and rate of longitude
        nslSun.setTime(t);

        // Calculate the scan phase angle Omega (modulo 4*PI), which has to be 0 at the
        // reference time
        long tElapsed = t - getRefTime();
        long twicePeriod = 2L * getTargetScanPeriod();
        omegaRevs = 2 * (int) (tElapsed / twicePeriod);
        long tRemainder = tElapsed % twicePeriod;
        omega = getOmegaRef() + FOUR_PI * (double) tRemainder
                / (double) twicePeriod;

        /** SOME AXES NEED TO BE SWAPPED TO ALIGN WITH OUR REF SYS:
         * 	GLOBAL	GAIASANDBOX
         * 	Z -> Y
         * 	X -> Z
         * 	Y -> X
         */
        // Calculate and set the attitude quaternion
        Quaterniond q = new Quaterniond(Z_AXIS, OBLIQUITY_DEG);
        q.mul(new Quaterniond(Y_AXIS, Math.toDegrees(nslSun.getSolarLongitude())));
        q.mul(new Quaterniond(Z_AXIS, Math.toDegrees(super.getNuRef() - PI_HALF)));
        q.mul(new Quaterniond(X_AXIS, Math.toDegrees(PI_HALF - super.getXiRef())));
        q.mul(new Quaterniond(Y_AXIS, Math.toDegrees(omega)));

        ConcreteAttitude att = new ConcreteAttitude(t, q, null, true);

        // Calculate and set the time derivative (rate) of the attitude
        // quaternion [1/day]. First compute the inertial spin vector [rad/day]
        // in ICRS...
        spinVector.set(Epsl.NECLP).scl(nslSun.getSolarLongitudeDot());
        spinVector.scaleAdd(getTargetScanRate() * ARCSEC_PER_S_TO_DEG_PER_DAY,
                att.getSrsAxes(xyz)[2]);
        // ...then convert to quaternion rate using (A.17) in AGIS paper
        Quaterniond qDot = new Quaterniond(0.5 * spinVector.z,
                0.5 * spinVector.x, 0.5 * spinVector.y, 0.0);
        qDot.mul(q);
        att.setQuaternionDot(qDot);

        return att;
    }

    /**
     * @return current EPSL mode
     */
    public Mode getMode() {
        return currentMode;
    }

    /**
     * @see gaia.cu9.ari.gaiaorbit.util.gaia.AnalyticalAttitudeDataServer#setDefault()
     */
    @Override
    public void setDefault() {
        super.setDefault();
        setTargetPrecessionRate(0.0);

        switch (currentMode) {
        case PRECEDING:
            super.setNuRef(0.0);
            break;
        case FOLLOWING:
            super.setNuRef(Math.PI);
            break;
        }

        spinVector = new Vector3d();
        setInitialized(true);
    }

    /**
     * @return non-truncated spin phase
     */
    public double getOmegaFull() {
        return omega + omegaRevs * TWO_PI;
    }

    /**
     * @return spin phase normalised to range [0, 4 Pi]
     */
    public double getOmegaMod4Pi() {
        return omega + (omegaRevs % 2) * TWO_PI;
    }
}
