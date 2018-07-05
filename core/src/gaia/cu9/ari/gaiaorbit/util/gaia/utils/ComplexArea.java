package gaia.cu9.ari.gaiaorbit.util.gaia.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import gaia.cu9.ari.gaiaorbit.util.math.Vector3d;

/**
 * Specifies a combination of {@link Area}s.
 *
 * @author hsiddiqu
 * @version $Id$
 */
public class ComplexArea implements Area {

    private Collection<Area> listOfAreas;

    private final static double piHalf = Math.PI / 2.0;

    private String name;

    private Place p;

    public ComplexArea() {
        listOfAreas = new ArrayList<Area>();
    }

    public void add(Area a) {
        listOfAreas.add(a);
    }

    /**
     */
    @Override
    public double altitude(Place spinAxisPlace) {
        double alt = Math.PI;
        for (Area a : listOfAreas) {
            alt = Math.min(alt, a.altitude(spinAxisPlace));
        }
        return alt;
    }

    /**
     */
    @Override
    public boolean contains(Place p) {
        boolean isIn = false;
        for (Area a : listOfAreas) {
            isIn = (isIn || a.contains(p));
        }
        return isIn;
    }

    /**
     */
    @Override
    public Place getMidPoint() {
        Vector3d sum = new Vector3d();
        for (Area a : listOfAreas) {
            sum.scaleAdd(a.getWeight(), a.getMidPoint().getDirection());
        }
        if (sum.len() == 0.0) {
            //sum.fromSphericalCoordinates(0.0, 0.0);
            sum.set(0, 0, 1);
        }
        return new Place(sum);
    }

    /**
     */
    @Override
    public double getWeight() {
        double sum = 0.0;
        for (Area a : listOfAreas) {
            sum += a.getWeight();
        }
        return sum;
    }

    /**
     * Get a random Place within the ComplexArea
     *
     * @param rnd
     *            Random number generator
     * @return random Place
     */
    public Place getRandomPlace(Random rnd) {

        // First find an enclosing circular area:
        Place centre = new Place(this.getMidPoint());
        double radius = piHalf - this.altitude(centre);
        p = new Place(rnd);
        p.moveToRandom(rnd, centre, radius);
        while (this.contains(p) == false) {
            p.moveToRandom(rnd, centre, radius);
        }
        return p;
    }

    /**
     * @return the solid angle [deg^2]
     */
    public double getSquareDegrees() {

        // Use Monte Carlo integration to estimate the solid angle. First find
        // an enclosing circular area:
        Place centre = new Place(this.getMidPoint());
        double radius = piHalf - this.altitude(centre);
        CircleArea circle = new CircleArea(centre, radius);
        int nMC = 1000000;
        int in = 0;
        Random rnd = new Random(0L);
        Place p = new Place();
        for (int i = 0; i < nMC; i++) {
            p.moveToRandom(rnd, centre, radius);
            if (this.contains(p))
                in++;
        }
        return circle.getSquareDegrees() * ((double) in / (double) nMC);
    }

    /**
     * @return the listOfAreas
     */
    public Collection<Area> getListOfAreas() {
        return this.listOfAreas;
    }

    /**
     * Set the name of the area
     *
     * @param str
     *            The string
     */
    public void setName(String str) {
        this.name = str;
    }

    /**
     * Get the name of the area
     *
     * @return The name
     */
    public String getName() {
        return this.name;
    }
}
