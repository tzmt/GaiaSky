package gaia.cu9.ari.gaiaorbit.util.gaia.utils;

public interface Area {

    /**
     * Determine the minimum angle between a great circle and the Area boundary
     *
     * @param spinAxisPlace
     *            great circle pole given as a Place
     * @return minimum angle [rad]
     */
    public double altitude(Place spinAxisPlace);

    /**
     * Determine whether a given Place is within the Area
     *
     * @param p
     *            the Place
     * @return true if p is within the Area
     */
    public boolean contains(Place p);

    /**
     * Determine the weighted mid-point of the Area
     *
     * @return the centre
     */
    public Place getMidPoint();

    /**
     * Determine the weight of the Area
     *
     * @return the weight
     */
    public double getWeight();
}
