package gaia.cu9.ari.gaiaorbit.scenegraph;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import gaia.cu9.ari.gaiaorbit.scenegraph.camera.ICamera;
import gaia.cu9.ari.gaiaorbit.scenegraph.camera.NaturalCamera;
import gaia.cu9.ari.gaiaorbit.scenegraph.component.RotationComponent;
import gaia.cu9.ari.gaiaorbit.util.ComponentTypes;
import gaia.cu9.ari.gaiaorbit.util.math.Matrix4d;
import gaia.cu9.ari.gaiaorbit.util.math.Quaterniond;
import gaia.cu9.ari.gaiaorbit.util.math.Vector3d;
import gaia.cu9.ari.gaiaorbit.util.time.ITimeFrameProvider;

/**
 * Contract that all focus objects must implement
 * 
 * @author tsagrista
 *
 */
public interface IFocus {

    /**
     * Returns the unique id of this focus
     * 
     * @return The id
     */
    public long getId();

    /**
     * Returns the id of the focus candidate of this object. Defaults to
     * {@link IFocus#getId()}
     * 
     * @return The id of the candidate
     */
    public long getCandidateId();

    /**
     * Returns the name of this focus
     * 
     * @return The name
     */
    public String getName();

    /**
     * Returns the name of the focus candidate of this object. Defaults to
     * {@link IFocus#getName()}
     * 
     * @return The name of the candidate
     */
    public String getCandidateName();

    /**
     * Returns the component types of this focus
     * 
     * @return The component types
     */
    public ComponentTypes getCt();

    /**
     * Returns whether this focus object is active or not. Useful for particle
     * groups
     * 
     * @return The active status
     */
    public boolean isActive();

    /**
     * Returns true if the focus is within the magnitude limit defined in
     * {@link gaia.cu9.ari.gaiaorbit.util.GlobalConf}
     * 
     * @return True if focus within magnitude limit
     */
    public boolean withinMagLimit();

    /**
     * Gets the first computed ancestor of this entity. Usually it is itself
     * 
     * @return First computed ancestor
     */
    public AbstractPositionEntity getComputedAncestor();

    /**
     * Gets the first ancestor of this node that is of type {@link Star}
     * 
     * @return The first ancestor of type {@link Star}
     */
    public SceneGraphNode getFirstStarAncestor();

    /**
     * Returns the absolute position of this entity in the native coordinates
     * (equatorial system)
     * 
     * @param aux
     *            Vector3d where to put the return value
     * @return The absolute position, same as aux
     */
    public Vector3d getAbsolutePosition(Vector3d aux);

    /**
     * Gets the position in equatorial spherical coordinates
     * 
     * @return The position in alpha, delta
     */
    public Vector2 getPosSph();

    /**
     * Gets the predicted position of this entity in the next time step in the
     * internal reference system using the given time provider and the given
     * camera
     * 
     * @param aux
     *            The out vector where the result will be stored
     * @param time
     *            The time frame provider
     * @param camera
     *            The camera
     * @param force
     *            Whether to force the computation if time is off
     * @return The aux vector for chaining
     */
    public Vector3d getPredictedPosition(Vector3d aux, ITimeFrameProvider time, ICamera camera, boolean force);

    /**
     * Returns the current distance to the camera in internal units
     * 
     * @return The current distance to the camera, in internal units
     */
    public double getDistToCamera();

    /**
     * Returns the current view angle of this entity, in radians
     * 
     * @return The view angle in radians
     */
    public double getViewAngle();

    /**
     * Returns the current apparent view angle (view angle corrected with the
     * field of view) of this entity, in radians
     * 
     * @return The apparent view angle in radians
     */
    public double getViewAngleApparent();

    /**
     * Returns the candidate apparent view angle (view angle corrected with the
     * field of view) of this entity, in radians
     * 
     * @return The apparent view angle in radians
     */
    public double getCandidateViewAngleApparent();

    /**
     * Returns the right ascension angle of this focus object
     * 
     * @return The right ascension angle in degrees
     */
    public double getAlpha();

    /**
     * Returns the declination angle of this focus object
     * 
     * @return The declination angle in degrees
     */
    public double getDelta();

    /**
     * Returns the size (diameter) of this entity in internal units
     * 
     * @return The size in internal units
     */
    public double getSize();

    /**
     * Returns the radius of this focus object in internal units
     * 
     * @return The radius of the focus, in internal units
     */
    public double getRadius();

    /**
     * Gets the apparent magnitude
     * 
     * @return The apparent magnitude
     */
    public float getAppmag();

    /**
     * Gets the absolute magnitude
     * 
     * @return The absolute magnitude
     */
    public float getAbsmag();

    /**
     * Returns the orientation matrix of this focus
     * 
     * @return The orientation matrix. Can be null
     */
    public Matrix4d getOrientation();

    /**
     * Returns the rotation component of this focus
     * 
     * @return The rotation component. Can be null
     */
    public RotationComponent getRotationComponent();

    /**
     * Returns the orientation quaternion of this focus
     * 
     * @return The orientation quaternion. Can be null
     */
    public Quaterniond getOrientationQuaternion();

    /**
     * Adds this focus to the hits list if it is hit by the [screenX, screenY]
     * position
     * 
     * @param screenX
     *            The x position of the hit
     * @param screenY
     *            The y position of the hit
     * @param w
     *            The viewport width
     * @param h
     *            The viewport height
     * @param pxdist
     *            The minimum pixel distance to consider as hit
     * @param camera
     *            The camera
     * @param hits
     *            The list where to add the element
     */
    public void addHit(int screenX, int screenY, int w, int h, int pxdist, NaturalCamera camera, Array<IFocus> hits);

    /**
     * Hook that runs when the candidate is actually made focus
     */
    public void makeFocus();

    /**
     * Prepares the candidate with the given name
     * 
     * @param name
     *            The name
     */
    public IFocus getFocus(String name);

    /**
     * Checks whether this foucs is within its valid time range, so that it can
     * be used as a focus
     * 
     * @return Whether the focus object is within its valid time range
     */
    public boolean isCoordinatesTimeOverflow();

    /**
     * Gets the depth of this focus object in the scene graph
     * 
     * @return The depth of the scene graph
     */
    public int getSceneGraphDepth();

}
