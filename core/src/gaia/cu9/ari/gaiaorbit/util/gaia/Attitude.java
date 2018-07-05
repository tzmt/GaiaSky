package gaia.cu9.ari.gaiaorbit.util.gaia;

import gaia.cu9.ari.gaiaorbit.util.math.Quaterniond;
import gaia.cu9.ari.gaiaorbit.util.math.Vector3d;

/**
 * This is the basic interface for all attitude representations and scanning
 * laws. An {@linkplain Attitude} represents the three-dimensional orientation
 * of the SRS of Gaia at a specific instant in time as well as its inertial
 * angular rotation at that moment.
 * 
 * Getters exists to provide the attitude and inertial rotation in various
 * forms, including a quaternion ({@link #getQuaternion()}, a set of heliotropic
 * or equatorial angles ({@link #getHeliotropicAnglesRates() and @link
 * #getEquatorialAnglesRates()} plus corresponding time derivatives. There are
 * also methods to obtain a number of attitude-related quantities, e.g., the
 * celestial pointings of two FOVs and the AL and AC rates for a particular
 * point in the FoV.
 * 
 * @author Lennart Lindegren, Uwe Lammers
 */
public interface Attitude {
    /**
     * Get the time that this attitude is valid for as a single long value. The
     * meaning of the time depends on the TimeContext of the AttitudeDataServer
     * that generated the attitude. Use #getGaiaTime() to get the time as an
     * absolute GaiaTime if needed.
     * 
     * @return time time that the attitude is valid for
     */
    public long getTime();

    /**
     * @return quaternion that represents the attitude
     */
    public Quaterniond getQuaternion();

    /**
     * 
     * @return time derivative [1/day] of the quaternion returned by
     *         {@link #getQuaternion()}
     */
    public Quaterniond getQuaternionDot();

    /**
     * Get the inertial spin vector in the SRS.
     * 
     * @return spin vector in [rad/day] relative to SRS
     */
    public Vector3d getSpinVectorInSrs();

    /**
     * Get the inertial spin vector in the ICRS (or CoMRS).
     * 
     * @return spin vector in [rad/day] relative to ICRS
     */
    public Vector3d getSpinVectorInIcrs();

    /**
     * Get the PFoV and FFoV directions as an array of unit vectors expressed in
     * the ICRS (or CoMRS).
     * 
     * @return array of two (PFoV, FFoV3) vectors
     */
    public Vector3d[] getFovDirections();

    /**
     * Get the x, y, z axes of the SRS as an array of three unit vectors
     * expressed in the ICRS (or CoMRS).
     * 
     * @return array of three (x, y, z) vectors
     */
    public Vector3d[] getSrsAxes(Vector3d[] xyz);

    /**
     * Compute the angular speed AL and AC of an inertial direction in the SRS
     * frame, using instrument angles (phi, zeta).
     * 
     * @param alInstrumentAngle
     *            (=AL angle phi) of the direction [rad]
     * @param acFieldAngle
     *            (=AC angle zeta) of the direction [rad]
     * @return two-element double array containing the angular speed AL and AC
     *         [rad/s]
     */
    public double[] getAlAcRates(double alInstrumentAngle, double acFieldAngle);

    /**
     * Compute the angular speed AL and AC of an inertial direction in the SRS
     * frame, using field angles (fov, eta, zeta).
     * 
     * @param fov
     *            FOV (Preceding or Following)
     * @param alFieldAngle
     *            (=AL angle eta) of the direction [rad]
     * @param acFieldAngle
     *            (=AC angle zeta) of the direction [rad]
     * @return two-element double array containing the angular speed AL and AC
     *         [rad/s]
     */
    public double[] getAlAcRates(FOV fov, double alFieldAngle, double acFieldAngle);
}
