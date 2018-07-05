package gaia.cu9.ari.gaiaorbit.util.coord;

import gaia.cu9.ari.gaiaorbit.util.math.Quaterniond;
import gaia.cu9.ari.gaiaorbit.util.math.Vector3d;

/**
 * Analytical representation of the Nominal Sun for the Gaia NSL.
 * 
 * Uses a low precision formula for the solar longitude (apparent longitude)
 * valid for the period 1980-2020 with an accuracy of 0.005 deg.
 * 
 * The reference frame is the ecliptic equinox J2000.
 * 
 * The analytical formula for calculating the longitude is written in terms of
 * the number of days since J2000.0(TCB). Since the zero point for GaiaTime (the
 * Mission Reference Epoch, see GAIA-CA-SP-ARI-BAS-003-06, rev. 1, Sect. 3.5) is
 * different from J2000.0 (actually it is J2010.0), it is necessary to add the
 * number of days from J2000.0 to the Mission Reference Epoch before doing the
 * calculation. This number is given by missionReferenceEpochDaysFromJ2000.
 * 
 * Since the time may be specified as the number of elapsed ns since a time
 * origin that is a settable attribute of the attitude data server, the NslSun
 * has a field timeOriginDaysFromJ2000 that can be set with setTimeOrigin(). By
 * default the origin time is the Mission Reference Epoch.
 * 
 */
public class NslSun {

    // the zero point for mission reference
    static final double missionReferenceEpoch = 0l;

    static final double piHalf = Math.PI / 2.0;
    static final double NOMINALSUN_ORBITALECCENTRICITY_J2000 = 0.01671;
    static final double NOMINALSUN_MEANLONGITUDE_J2000 = 280.4665;// [deg] 
    static final double NOMINALSUN_MEANLONGITUDERATE_J2000 = 0.98560903; // [deg day^-1] 
    static final double NOMINALSUN_ORBITALMEANANOMALY_J2000 = 357.529; // [deg] 
    static final double NOMINALSUN_ORBITALMEANANOMALYRATE_J2000 = 0.98560020; // [deg day^-1] 

    /**
     * Constants used in the approximate longitude formula calculated from
     * obliquity and eccentricity taken from GPDB
     */

    static final double OBLIQUITY_DEG = Coordinates.OBLIQUITY_DEG_J2000;
    static final double e = NOMINALSUN_ORBITALECCENTRICITY_J2000;
    static final double d2e = Math.toDegrees(2. * e);
    static final double d5_2e2 = Math.toDegrees(2.5 * e * e);
    static final double sineObliquity = Math.sin(Coordinates.OBLIQUITY_RAD_J2000);
    static final double cosineObliquity = Math.cos(Coordinates.OBLIQUITY_RAD_J2000);
    static final double ABERRATION_CONSTANT_J2000 = 20.49122;
    // static final double ABERRATION_CONSTANT_J2000 =
    // GaiaParam.Nature.ABERRATION_CONSTANT_J2000;

    private final double timeOriginDaysFromJ2000 = missionReferenceEpoch - (AstroUtils.JD_J2000 - AstroUtils.JD_J2010);
    private final double timeOriginNsFromJ2000 = missionReferenceEpoch - (AstroUtils.JD_J2000 - AstroUtils.JD_J2010) * AstroUtils.DAY_TO_NS;


    /** Unit vectors **/
    static final Vector3d X_AXIS = Vector3d.getUnitX();
    static final Vector3d Y_AXIS = Vector3d.getUnitY();
    static final Vector3d Z_AXIS = Vector3d.getUnitZ();

    /**
     * Time dependent variables
     */
    private double sLon, sLonMod4Pi, sLonDot, sineLon, cosineLon;

    /**
     * Constructor
     */
    public NslSun() {
    }

    /**
     * Calculate all fields for a given julian date.
     * @param julianDate The julian date.
     */
    public void setTime(double julianDate) {
        long tNs = (long) ((julianDate - AstroUtils.JD_J2000) * AstroUtils.DAY_TO_NS);
        setTime(tNs);
    }

    /**
     * Calculate all fields for a given time
     * 
     * Author: F. Mignard
     * 
     * @param tNs
     *            time in [ns] since the time origin
     */
    public void setTime(long tNs) {
        final double daysFromJ2000 = timeOriginDaysFromJ2000 + (double) tNs * AstroUtils.NS_TO_DAY;

        // Mean apparent Sun longitude:
        final double xl = NOMINALSUN_MEANLONGITUDE_J2000
                - ABERRATION_CONSTANT_J2000 / 3600.0
                + NOMINALSUN_MEANLONGITUDERATE_J2000
                * daysFromJ2000;

        // Mean Sun anomaly:
        final double xm = NOMINALSUN_ORBITALMEANANOMALY_J2000
                + NOMINALSUN_ORBITALMEANANOMALYRATE_J2000
                * daysFromJ2000;

        final double sm = Math.sin(Math.toRadians(xm));
        final double cm = Math.cos(Math.toRadians(xm));

        // Longitude accurate to O(e^3)
        final double lon = xl + sm * (d2e + d5_2e2 * cm);

        this.sLonDot = Math
                .toRadians(NOMINALSUN_MEANLONGITUDERATE_J2000
                        + NOMINALSUN_ORBITALMEANANOMALYRATE_J2000
                        * Math.toRadians(d2e * cm + d5_2e2
                                * (cm * cm - sm * sm)));

        this.sLon = Math.toRadians(lon);
        this.sLonMod4Pi = Math.toRadians(lon % (2. * 360.0));
        this.sineLon = Math.sin(this.sLonMod4Pi);
        this.cosineLon = Math.cos(this.sLonMod4Pi);
    }

    /**
     * @return solar longitude in [rad]
     */
    public double getSolarLongitude() {
        return this.sLon;
    }

    /**
     * @return solar longitude in [rad], modulo 4*PI
     */
    public double getSolarLongitudeMod4Pi() {
        return this.sLonMod4Pi;
    }

    /**
     * @return time derivative of solar longitude in [rad/day]
     */
    public double getSolarLongitudeDot() {
        return sLonDot;
    }

    /**
     * @param out The output vector.
     * @return The output vector containing the solar direction as a unit 3-vector in BCRS.
     */
    public Vector3d getSolarDirection(Vector3d out) {
        return out.set(cosineLon, sineLon * cosineObliquity, sineLon
                * sineObliquity);
    }

    /**
     * Method to convert heliotropic angles to quaternion
     * 
     * @param t
     *            time [ns]
     * @param xi
     *            revolving angle (solar aspect angle) [rad]
     * @param nu
     *            revolving phase [rad]
     * @param Omega
     *            spin phase [rad]
     * @return attitude quaternion
     */
    public Quaterniond heliotropicToQuaternion(long t, double xi, double nu,
            double Omega) {
        setTime(t);
        double sLon = getSolarLongitude();


        /** SOME AXES NEED TO BE SWAPPED TO ALIGN WITH OUR REF SYS:
         * 	GLOBAL ->	GAIASANDBOX
         * 	Z -> Y
         * 	X -> Z
         * 	Y -> X
         */
        Quaterniond q = new Quaterniond(Z_AXIS, OBLIQUITY_DEG);
        q.mul(new Quaterniond(Y_AXIS, Math.toDegrees(sLon)));
        q.mul(new Quaterniond(Z_AXIS, Math.toDegrees(nu - piHalf)));
        q.mul(new Quaterniond(X_AXIS, Math.toDegrees(piHalf - xi)));
        q.mul(new Quaterniond(Y_AXIS, Math.toDegrees(Omega)));
        return q;
    }

    /**
     * Puts an angle in the base interval [ 0, nRev*2*PI )
     * 
     * @param x
     *            angle [rad]
     * @param nRev
     *            number of revolutions in base interval
     * @return angle in base interval [rad]
     */
    public double angleBase(double x, int nRev) {
        double x1 = x;
        double base = (double) nRev * 2.0 * Math.PI;
        while (x1 >= base) {
            x1 -= base;
        }
        while (x1 < 0.0) {
            x1 += base;
        }
        return x1;
    }
}
