package gaia.cu9.ari.gaiaorbit.util.coord;

import java.time.Instant;

import gaia.cu9.ari.gaiaorbit.scenegraph.AbstractPositionEntity;
import gaia.cu9.ari.gaiaorbit.scenegraph.ModelBody;
import gaia.cu9.ari.gaiaorbit.scenegraph.Orbit;
import gaia.cu9.ari.gaiaorbit.scenegraph.component.RotationComponent;
import gaia.cu9.ari.gaiaorbit.util.Constants;
import gaia.cu9.ari.gaiaorbit.util.math.Matrix4d;
import gaia.cu9.ari.gaiaorbit.util.math.Vector3d;

/**
 * A position that never changes
 * 
 * @author Toni Sagrista
 *
 */
public class StaticParentRotationCoordinates implements IBodyCoordinates {

    ModelBody parent;
    Vector3d position;
    Matrix4d trf;

    public StaticParentRotationCoordinates() {
        super();
        trf = new Matrix4d();
    }

    @Override
    public void doneLoading(Object... params) {
        AbstractPositionEntity me = (AbstractPositionEntity) params[1];
        if (me.parent != null && me.parent instanceof ModelBody) {
            parent = (ModelBody) me.parent;
        }
    }

    @Override
    public Vector3d getEclipticSphericalCoordinates(Instant date, Vector3d out) {
        return getEquatorialCartesianCoordinates(date, out);
    }

    @Override
    public Vector3d getEclipticCartesianCoordinates(Instant date, Vector3d out) {
        return getEquatorialCartesianCoordinates(date, out);
    }

    @Override
    public Vector3d getEquatorialCartesianCoordinates(Instant date, Vector3d out) {
        out.set(position);
        RotationComponent rc = parent.rc;
        if (rc != null) {
            out.rotate((float) rc.ascendingNode, 0, 1, 0).rotate((float) (rc.inclination + rc.axialTilt), 0, 0, 1).rotate((float) rc.angle, 0, 1, 0);
        }

        return out;
    }

    public void setPosition(double[] position) {
        this.position = new Vector3d(position[0] * Constants.KM_TO_U, position[1] * Constants.KM_TO_U, position[2] * Constants.KM_TO_U);
    }

    @Override
    public Orbit getOrbitObject() {
        return null;
    }

}
