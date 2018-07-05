package gaia.cu9.ari.gaiaorbit.util.units;

import gaia.cu9.ari.gaiaorbit.util.coord.Coordinates;
import gaia.cu9.ari.gaiaorbit.util.math.Vector3d;
import gaia.cu9.ari.gaiaorbit.util.units.Quantity.Angle;
import gaia.cu9.ari.gaiaorbit.util.units.Quantity.Angle.AngleUnit;
import gaia.cu9.ari.gaiaorbit.util.units.Quantity.Length;
import gaia.cu9.ari.gaiaorbit.util.units.Quantity.Length.LengthUnit;

/**
 * Helper class that transforms various positional information into the internal
 * position of the application.
 * 
 * @author Toni Sagrista
 *
 */
public class Position {

    public enum PositionType {
        EQ_SPH_DIST,
        EQ_SPH_PLX,

        ECL_SPH_DIST,
        ECL_SPH_PLX,

        GAL_SPH_DIST,
        GAL_SPH_PLX,

        EQ_XYZ,
        ECL_XYZ,
        GAL_XYZ
    }

    public final Vector3d gsposition;

    /**
     * Works out the cartesian equatorial position in the Gaia Sandbox reference
     * system. The units of the result are parsecs.
     * 
     * @param a
     * @param unitA
     * @param b
     * @param unitB
     * @param c
     * @param unitC
     * @param type
     */
    public Position(double a, String unitA, double b, String unitB, double c, String unitC, PositionType type) {
        if (Double.isNaN(c)) {
            c = 0.04;
            unitC = "mas";
        }
        gsposition = new Vector3d();

        switch (type) {
        case EQ_SPH_DIST:

            Angle lon = new Angle(a, unitA);
            Angle lat = new Angle(b, unitB);
            Length dist = new Length(c, unitC);

            Coordinates.sphericalToCartesian(lon.get(AngleUnit.RAD), lat.get(AngleUnit.RAD), dist.get(LengthUnit.PC), gsposition);

            break;
        case EQ_SPH_PLX:

            lon = new Angle(a, unitA);
            lat = new Angle(b, unitB);
            dist = new Angle(c, unitC).getParallaxDistance();

            Coordinates.sphericalToCartesian(lon.get(AngleUnit.RAD), lat.get(AngleUnit.RAD), dist.get(LengthUnit.PC), gsposition);

            break;
        case GAL_SPH_DIST:
            lon = new Angle(a, unitA);
            lat = new Angle(b, unitB);
            dist = new Length(c, unitC);

            Coordinates.sphericalToCartesian(lon.get(AngleUnit.RAD), lat.get(AngleUnit.RAD), dist.get(LengthUnit.PC), gsposition);
            gsposition.mul(Coordinates.galToEq());
            break;
        case GAL_SPH_PLX:
            lon = new Angle(a, unitA);
            lat = new Angle(b, unitB);
            dist = new Angle(c, unitC).getParallaxDistance();

            Coordinates.sphericalToCartesian(lon.get(AngleUnit.RAD), lat.get(AngleUnit.RAD), dist.get(LengthUnit.PC), gsposition);
            gsposition.mul(Coordinates.galToEq());
            break;
        case ECL_SPH_DIST:
            lon = new Angle(a, unitA);
            lat = new Angle(b, unitB);
            dist = new Length(c, unitC);

            Coordinates.sphericalToCartesian(lon.get(AngleUnit.RAD), lat.get(AngleUnit.RAD), dist.get(LengthUnit.PC), gsposition);
            gsposition.mul(Coordinates.eclToEq());
            break;
        case ECL_SPH_PLX:
            lon = new Angle(a, unitA);
            lat = new Angle(b, unitB);
            dist = new Angle(c, unitC).getParallaxDistance();

            Coordinates.sphericalToCartesian(lon.get(AngleUnit.RAD), lat.get(AngleUnit.RAD), dist.get(LengthUnit.PC), gsposition);
            gsposition.mul(Coordinates.eclToEq());
            break;
        case EQ_XYZ:
            Length x = new Length(a, unitA);
            Length y = new Length(b, unitB);
            Length z = new Length(c, unitC);

            gsposition.set(x.get(LengthUnit.PC), y.get(LengthUnit.PC), z.get(LengthUnit.PC));

            break;
        case GAL_XYZ:
            x = new Length(a, unitA);
            y = new Length(b, unitB);
            z = new Length(c, unitC);

            gsposition.set(x.get(LengthUnit.PC), y.get(LengthUnit.PC), z.get(LengthUnit.PC));
            gsposition.mul(Coordinates.galToEq());
            break;
        case ECL_XYZ:
            x = new Length(a, unitA);
            y = new Length(b, unitB);
            z = new Length(c, unitC);

            gsposition.set(x.get(LengthUnit.PC), y.get(LengthUnit.PC), z.get(LengthUnit.PC));
            gsposition.mul(Coordinates.eclToEq());
            break;
        default:
            break;
        }

    }

    private void swapCoordinates() {
        // Switch axes
        double aux = gsposition.x;
        gsposition.x = gsposition.y;
        gsposition.y = gsposition.z;
        gsposition.x = aux;
    }
}
