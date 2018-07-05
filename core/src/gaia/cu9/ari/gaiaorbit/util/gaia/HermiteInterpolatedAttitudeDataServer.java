package gaia.cu9.ari.gaiaorbit.util.gaia;

import gaia.cu9.ari.gaiaorbit.util.Logger;
import gaia.cu9.ari.gaiaorbit.util.gaia.time.Duration;
import gaia.cu9.ari.gaiaorbit.util.gaia.time.GtiList;
import gaia.cu9.ari.gaiaorbit.util.gaia.utils.AttitudeUtils;
import gaia.cu9.ari.gaiaorbit.util.gaia.utils.Interpolator;
import gaia.cu9.ari.gaiaorbit.util.math.Quaterniond;

/**
 * This abstract class contains the fields needed to store numerical attitude in
 * the form of arrays (q and SRS rates), and a method to calculate the attitude
 * for any given time using cubic Hermite interpolation
 *
 * @author Lennart Lindegren
 *
 */
public abstract class HermiteInterpolatedAttitudeDataServer extends
        NumericalAttitudeDataServer<Attitude> {
    protected int nT;
    protected long[] tNs;
    protected double[] qX, qY, qZ, qW, rateX, rateY, rateZ;

    /**
     * Constructor for a given start time and mission length
     *
     * @param tStart
     *            start time of the valid attitude interval
     * @param tLength
     *            length of the valid attitude interval
     */
    public HermiteInterpolatedAttitudeDataServer(long tStart, Duration tLength) {
        long tEnd = tStart + tLength.asNanoSecs();
        gtis = new GtiList();
        try {
            super.gtis.add(tStart, tEnd);
        } catch (RuntimeException e) {
            Logger.error(e);
        }
    }

    /**
     * @see gaia.cu9.ari.gaiaorbit.util.gaia.NumericalAttitudeDataServer#initialize()
     *
     *      This method will compute the attitude and attitude rate at discrete
     *      points and store in the arrays tNs, qX, rateX, etc
     */
    @Override
    public abstract void initialize() throws RuntimeException;

    /**
     * @see gaia.cu9.ari.gaiaorbit.util.gaia.BaseAttitudeDataServer#getAttitude(long)
     *
     * @param t - the time elapsed since the epoch of J2010 in ns (TCB)
     * @return attitude for the given time
     */
    @Override
    public Attitude getAttitudeNative(final long t) throws RuntimeException {

        int left = AttitudeUtils.findLeftIndexVar(t, tNs, 0);
        if (left < 0 || left > nT - 2) {
            long time = t;
            String msg = "t < tBeg or >= tEnd, t = + " + time
                    + ", tBeg = " + getStartTime() + ", tEnd = "
                    + getStopTime();
            throw new RuntimeException(msg);
        }
        double qXDotL = 0.5 * (qY[left] * rateZ[left] - qZ[left] * rateY[left] + qW[left]
                * rateX[left]);
        double qYDotL = 0.5 * (-qX[left] * rateZ[left] + qZ[left] * rateX[left] + qW[left]
                * rateY[left]);
        double qZDotL = 0.5 * (qX[left] * rateY[left] - qY[left] * rateX[left] + qW[left]
                * rateZ[left]);
        double qWDotL = 0.5 * (-qX[left] * rateX[left] - qY[left] * rateY[left] - qZ[left]
                * rateZ[left]);
        double qXDotL1 = 0.5 * (qY[left + 1] * rateZ[left + 1] - qZ[left + 1]
                * rateY[left + 1] + qW[left + 1] * rateX[left + 1]);
        double qYDotL1 = 0.5 * (-qX[left + 1] * rateZ[left + 1] + qZ[left + 1]
                * rateX[left + 1] + qW[left + 1] * rateY[left + 1]);
        double qZDotL1 = 0.5 * (qX[left + 1] * rateY[left + 1] - qY[left + 1]
                * rateX[left + 1] + qW[left + 1] * rateZ[left + 1]);
        double qWDotL1 = 0.5 * (-qX[left + 1] * rateX[left + 1] - qY[left + 1]
                * rateY[left + 1] - qZ[left + 1] * rateZ[left + 1]);
        double timeUnit = 86400e9;
        double x0 = 0.0;
        double x1 = (tNs[left + 1] - tNs[left]) / timeUnit;
        double x = (t - tNs[left]) / timeUnit;

        // HERMITE
        //        double intX[] = Interpolator.hermite3(x, x0, qX[left], qXDotL, x1,
        //                qX[left + 1], qXDotL1);
        //        double intY[] = Interpolator.hermite3(x, x0, qY[left], qYDotL, x1,
        //                qY[left + 1], qYDotL1);
        //        double intZ[] = Interpolator.hermite3(x, x0, qZ[left], qZDotL, x1,
        //                qZ[left + 1], qZDotL1);
        //        double intW[] = Interpolator.hermite3(x, x0, qW[left], qWDotL, x1,
        //                qW[left + 1], qWDotL1);

        // LINEAR
        double intX[] = Interpolator.linear(x, x0, qX[left], x1,
                qX[left + 1]);
        double intY[] = Interpolator.linear(x, x0, qY[left], x1,
                qY[left + 1]);
        double intZ[] = Interpolator.linear(x, x0, qZ[left], x1,
                qZ[left + 1]);
        double intW[] = Interpolator.linear(x, x0, qW[left], x1,
                qW[left + 1]);

        Quaterniond qInt = new Quaterniond(intX[0], intY[0], intZ[0], intW[0]);
        double fact = 1.0 / Math.sqrt(qInt.len2());

        return new ConcreteAttitude(t, qInt.nor(),
                new Quaterniond(intX[1] * fact, intY[1] * fact, intZ[1] * fact,
                        intW[1] * fact), false);
    }
}
