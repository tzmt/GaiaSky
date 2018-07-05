/*
 * Copyright (C) 2006-2011 Gaia Data Processing and Analysis Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package gaia.cu9.ari.gaiaorbit.util.gaia.time;

import java.io.Serializable;

/**
 * A {@code Duration} represents an amount of time on a proper time scale.
 *
 * There are two implementations provided of the conversions methods one as
 * object interface, where an object of the current class has to be
 * instantiated. The oder implementation is provided as static class methods.
 *
 * Performance tests of both implementations have come up with a performance
 * improvement of 20% of the static methods compared with the object methods.
 *
 * @author Uwe Lammers
 * @version $Id: Duration.java 405499 2014-12-18 20:21:02Z hsiddiqu $
 */
public interface Duration extends Serializable {
    /**
     * A few obvious constants  
     */
    public final long NS_PER_SEC = 1000000000L;
    public final double SECS_PER_MIN = 60.;
    public final double MINS_PER_HOUR = 60.;
    public final double HOURS_PER_DAY = 24.;
    public final double DAYS_PER_JULIAN_YEAR = 365.25D;
    public final double HOURS_PER_REV = 6;
    public final double REVS_PER_DAY = 4;

    /* _PER_JULIAN_YEAR */
    public final double REVS_PER_JULIAN_YEAR = DAYS_PER_JULIAN_YEAR * REVS_PER_DAY;
    public final double HOURS_PER_JULIAN_YEAR = DAYS_PER_JULIAN_YEAR * HOURS_PER_DAY;
    public final double MINS_PER_JULIAN_YEAR = HOURS_PER_JULIAN_YEAR * MINS_PER_HOUR;
    public final double SECS_PER_JULIAN_YEAR = MINS_PER_JULIAN_YEAR * SECS_PER_MIN;
    public final double NS_PER_JULIAN_YEAR = SECS_PER_JULIAN_YEAR * (double) NS_PER_SEC;
    public final long NS_PER_JULIAN_YEAR_L = (long) NS_PER_JULIAN_YEAR;

    /* _PER_DAY */
    public final double MINS_PER_DAY = HOURS_PER_DAY * MINS_PER_HOUR;
    public final double SECS_PER_DAY = MINS_PER_DAY * SECS_PER_MIN;
    public final double NS_PER_DAY = SECS_PER_DAY * (double) NS_PER_SEC;
    public final long NS_PER_DAY_L = (long) NS_PER_DAY;

    /* _PER_REV */
    public final double MINS_PER_REV = HOURS_PER_REV * MINS_PER_HOUR;
    public final double SECS_PER_REV = MINS_PER_REV * SECS_PER_MIN;
    public final double NS_PER_REV = SECS_PER_REV * (double) NS_PER_SEC;
    public final long NS_PER_REV_L = (long) NS_PER_REV;

    /* _PER_HOUR */
    public final double SECS_PER_HOUR = MINS_PER_HOUR * SECS_PER_MIN;
    public final double NS_PER_HOUR = SECS_PER_HOUR * (double) NS_PER_SEC;
    public final long NS_PER_HOUR_L = (long) NS_PER_HOUR;

    /* _PER_MIN */
    public final double NS_PER_MIN = SECS_PER_MIN * (double) NS_PER_SEC;
    public final long NS_PER_MIN_L = (long) NS_PER_MIN;

    /**
     * Set this duration to a new given one
     *
     * @param d
     *            duration to set this one to
     * @return updated object
     */
    public Duration set(final Duration d);

    /**
     * @return duration expressed in ns
     */
    public long asNanoSecs();

    /**
     * @return duration expressed in s
     */
    public double asSecs();

    /**
     * @return duration expressed in min
     */
    public double asMins();

    /**
     * @return duration expressed in h
     */
    public double asHours();

    /**
     * @return number of ns expressed days
     */
    public double asDays();

    /**
     * @return duration expressed in Julian years
     */
    public double asJulianYears();

    /**
     * @return duration expressed in revolutions
     */
    public double asRevs();

    /**
     * @return negated amount of time
     */
    public Duration negate();

    /**
     * Add a duration to this one
     *
     * @param d
     *            amount of time to add
     * @return updated object
     */
    public Duration add(final Duration d);

    /**
     * Subtract a duration from this one
     *
     * @param d
     *            amount of time to subtract
     * @return updated object
     */
    public Duration sub(final Duration d);

    /**
     * Check that this duration is longer than a given one 
     * @param d duration to compare to
     * @return {@code true} if this duration is longer than {@code d}
     */
    public boolean isLongerThan(Duration d);

    /**
     * Multiply this duration by a given factor
     * @param s scale factor
     *
     * @return updated object
     */
    public Duration mult(final double s);

    /**
     * @return Current time scale of the duration
     */
    public TimeScale getScale();

    /**
     * Set the time scale for this duration
     * @param scale time scale to set the duration to
     */
    public void setScale(TimeScale scale);
}
