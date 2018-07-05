package gaia.cu9.ari.gaiaorbit.util.gaia.utils;



/**
 * A circular area about a centre c (which is defined by a {@link Place}
 * object), and radius r.
 * 
 * @author llindegr
 * 
 */
public class CircleArea implements Area {

	private final static double piHalf = Math.PI / 2.0;

	private final static double squareDegreesOfSphere = 129600.0 / Math.PI;

	private Place centre;

	private double radius;

	/**
	 * Creates an instance of a CircleArea about a given centre and radius.
	 * 
	 * @param c
	 *            the centre
	 * @param r
	 *            the radius [rad]
	 */
	public CircleArea(Place c, double r) {
		centre = new Place(c);
		radius = r;
	}

	/**
	 */
	@Override
	public double altitude(Place pole) {
		double absLat = Math.abs(piHalf - pole.getAngleTo(centre));
		return Math.max(absLat - radius, 0.0);
	}

	/**
	 */
	@Override
	public boolean contains(Place p) {
		return (p.getAngleTo(centre) < radius);
	}

	/**
	 */
	@Override
	public Place getMidPoint() {
		return new Place(centre);
	}

	/**
	 */
	@Override
	public double getWeight() {
		double w;
		if (radius <= 0.0) {
			w = 0.0;
		} else if (radius < 1.0) {
			w = 0.5 * Math.pow(Math.sin(radius), 2) / (1.0 + Math.cos(radius));
		} else if (radius < Math.PI) {
			w = 0.5 * (1.0 - Math.cos(radius));
		} else {
			w = 1.0;
		}
		return w;
	}

	/**
	 * @return the centre
	 */
	public Place getCentre() {
		return this.centre;
	}

	/**
	 * @return the radius
	 */
	public double getRadius() {
		return this.radius;
	}

	/**
	 * @return the solid angle [deg^2]
	 */
	public double getSquareDegrees() {
		return this.getWeight() * squareDegreesOfSphere;
	}
}
