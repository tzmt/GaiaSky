package gaia.cu9.ari.gaiaorbit.util.time;

import java.time.Instant;

import com.badlogic.gdx.utils.TimeUtils;

import gaia.cu9.ari.gaiaorbit.event.EventManager;
import gaia.cu9.ari.gaiaorbit.event.Events;

/**
 * Implements a real time clock. Time flows at the same pace as real life.
 * Similar to GlobalClock with a time warp of 1.
 * 
 * @author tsagrista
 *
 */
public class RealTimeClock implements ITimeFrameProvider {
    private static final double SEC_TO_HOUR = 1d / 3600d;

    long time;
    double dtHours;
    double lastUpdate = 0;

    public RealTimeClock() {
        time = Instant.now().toEpochMilli();
    }

    /**
     * The dt in hours
     */
    @Override
    public double getDt() {
        return SEC_TO_HOUR;
    }

    @Override
    public Instant getTime() {
        return Instant.ofEpochMilli(time);
    }

    @Override
    public void update(double dt) {
        dtHours = dt * SEC_TO_HOUR;
        time = TimeUtils.millis();

        // Post event each 1/2 second
        lastUpdate += dt;
        if (lastUpdate > .5) {
            EventManager.instance.post(Events.TIME_CHANGE_INFO, time);
            lastUpdate = 0;
        }
    }

    @Override
    public double getWarpFactor() {
        return SEC_TO_HOUR;
    }

    @Override
    public boolean isFixedRateMode() {
        return false;
    }

    @Override
    public float getFixedRate() {
        return -1;
    }

    @Override
    public boolean isTimeOn() {
        return true;
    }

}
