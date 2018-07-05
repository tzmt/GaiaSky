package gaia.cu9.ari.gaiaorbit.scenegraph;

import gaia.cu9.ari.gaiaorbit.scenegraph.camera.ICamera;
import gaia.cu9.ari.gaiaorbit.util.Constants;
import gaia.cu9.ari.gaiaorbit.util.math.MathUtilsd;
import gaia.cu9.ari.gaiaorbit.util.time.ITimeFrameProvider;

/**
 * This acts as aggregator node. It does not represent a physical entity, but a
 * set of physical entities that must be rendered together if necessary.
 * 
 * @author Toni Sagrista
 *
 */
public abstract class Blob extends AbstractPositionEntity {

    /** Angle at which this entity is fully visible with an alpha of 1f **/
    protected float lowAngle;
    /**
     * Angle at which this entity is first rendered, but its alpha value is 0f
     **/
    protected float highAngle;

    protected Blob() {
        super();
    }

    public void update(ITimeFrameProvider time, final Transform parentTransform, ICamera camera, float opacity) {
        this.opacity = opacity * this.opacity;
        transform.set(parentTransform);

        // Update with translation/rotation/etc
        updateLocal(time, camera);

        if (children != null && viewAngle / camera.getFovFactor() > lowAngle) {
            for (int i = 0; i < children.size; i++) {
                float childOpacity = 1 - this.opacity;
                SceneGraphNode child = children.get(i);
                child.update(time, transform, camera, childOpacity);
            }
        }
    }

    @Override
    public void update(ITimeFrameProvider time, Transform parentTransform, ICamera camera) {

    }

    @Override
    public void updateLocal(ITimeFrameProvider time, ICamera camera) {
        super.updateLocal(time, camera);

        // Update alpha
        opacity = (float) MathUtilsd.lint(viewAngle / camera.getFovFactor(), lowAngle, highAngle, 1, 0);
    }

    /**
     * Sets the size of this entity in kilometers
     * 
     * @param size
     *            The diameter of the entity
     */
    public void setSize(Float size) {
        this.size = (float) (size * Constants.KM_TO_U);
    }

    @Override
    public void updateLocalValues(ITimeFrameProvider time, ICamera camera) {
    }

}
