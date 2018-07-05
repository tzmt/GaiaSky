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
/*
 * GTMSIM
 * Copyright (C) 2006 Gaia Data Processing and Analysis Consortium
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

import java.util.Date;

/**
 * A GTI (Good Time Interval), is a
 *
 * @author jhoar
 * @version $Id: Gti.java 300167 2013-06-24 09:42:58Z ejoliet $
 */
public class Gti implements Comparable<Gti> {
    /**
     * The time defining the beginning of the object validity.
     */
    private long start = -1;

    /**
     * The time defining the end of the object validity.
     */
    private long end = -1;

    /**
     * Constructor taking both start and end times.
     *
     * @param start
     *            the time defining the beginning of the object validity.
     * @param end
     *            the time defining the end of the object validity.
     * @throws RuntimeException
     *             Start is later than end
     */
    public Gti(final long start, final long end) throws RuntimeException {
        if (start > end) {
            throw new RuntimeException(
                    "Start time is later than end time");
        }

        this.start = start;
        this.end = end;
    }

    /**
     * This is the method that determines if two time intervals are identical
     * one of them will typically contain one time and the other one a start and
     * an end, but we have to also consider that both may have start/end times
     * only.
     *
     * @param t
     *            the GTI Object to be compared with
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(final Object t) {
        if (t == null) {
            return false;
        }

        // Generics do not allow us to check if we are passing in the right
        // type of time, so we could actually pass in (say) a EarthTime here
        // when comparing it to a GaiaTime.
        if (!(t instanceof Gti)) {
            return false;
        }

        final Gti t1 = (Gti) t;

        return (t1.getStart() == this.getStart() &&
                t1.getEnd() == this.getEnd());
    }

    /**
     * This is needed in order to have the search working in the HashMap In
     * order to choose the bucket we don't need more than one second accuracy.
     *
     * @return int
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (start >= 0)
            return (int) (new NanoSecs(this.start).asSecs());

        if (end >= 0)
            return (int) (new NanoSecs(this.end).asSecs());

        return 0;
    }

    /**
     * Return the end time.
     *
     * @return the time defining the end of the object validity
     */
    public long getEnd() {
        return this.end;
    }

    /**
     * Return the start time.
     *
     * @return the time defining the beginning of the object validity
     */
    public long getStart() {
        return this.start;
    }

    /**
     * Set the end time
     *
     * @param end
     *            the time defining the end of the object validity
     * @throws RuntimeException
     */
    public void setEnd(final long end) throws RuntimeException {
        if (end < this.start) {
            throw new RuntimeException(
                    "End time is earlier than start time");
        }

        this.end = end;
    }

    /**
     * Set the start time
     *
     * @param start
     *            the time defining the beginning of the object validity
     * @throws RuntimeException
     */
    public void setStart(final long start) throws RuntimeException {
        if (start > this.end) {
            throw new RuntimeException(
                    "Start time is later than end time");
        }

        this.start = start;
    }

    /**
     * Return the duration of the GTI
     *
     * @return Duration object
     */
    public Duration getDuration() {
        return new NanoSecs((end - start));
    }

    /**
     * Is an instant within the GTI; that is has a time
     * <ul>
     * <li>later than or equal to the start time <b>and</b></li>
     * <li>earlier than or equal to the end time</li>
     * </ul>
     * of the GTI
     *
     * @param time
     *            instant to check
     * @return true if inside
     */
    public boolean isInside(final long time) {
        if ((time > this.start || time == this.start) &&
                (time < this.end ||
                        time == this.end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Is this GTI X '<' than the GTI passed in Y? If X starts before Y it is
     * less than Y. If X and Y start at the same time, X is les than Y if it
     * ends before Y If X and Y are the same X is not less than Y
     *
     * @param i
     *            GTI to test
     * @return true if this GTI is earlier than the GTI passed in.
     */
    public boolean isLessThan(final Gti i) {
        if (this.start != i.getStart()) {
            return this.start < i.getStart();
        } else {
            return this.end < i.getEnd();
        }
    }

    // Can re-enable this annotation once we go to Java 6

    /**
     *
     *
     * @param o
     *
     * @return int
     */
    @Override
    public int compareTo(final Gti o) {
        if (this.start == o.getStart() &&
                this.end == o.getEnd()) {
            return 0;
        }

        if (this.isLessThan(o)) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * Return a useful string representation
     * @return String
     */
    @Override
    public String toString() {
        Date st = new Date();
        st.setTime(start / 1000000);
        Date ed = new Date();
        ed.setTime(end / 1000000);
        return st.toString() + " to " + ed.toString();
    }
}
