package gaia.cu9.ari.gaiaorbit.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import gaia.cu9.ari.gaiaorbit.render.RenderingContext;
import gaia.cu9.ari.gaiaorbit.util.math.Vector3d;

/**
 * This class provides utils to use Sprites and Fonts as if they were Decals,
 * this is, flat textures in the 3D space.
 * 
 * @author Toni Sagrista
 *
 */
public class DecalUtils {

    static Vector3 tmp, tmp2;
    static Matrix4 idt, aux1, aux2;
    static {
        tmp = new Vector3();
        tmp2 = new Vector3();
        idt = new Matrix4();
        aux1 = new Matrix4();
        aux2 = new Matrix4();
    }

    /**
     * Draws the given text using the given font in the given 3D position using
     * the 3D coordinate space. If faceCamera is true, the text is rendered
     * always facing the camera. It assumes that {@link SpriteBatch#begin()} has
     * been called. This enables 3D techniques such as z-buffering to be applied
     * to the text textures.
     * 
     * @param font
     *            The font.
     * @param batch
     *            The sprite batch to use.
     * @param text
     *            The text to write.
     * @param position
     *            The 3D position.
     * @param camera
     *            The camera.
     */
    public static void drawFont3D(BitmapFont font, SpriteBatch batch, String text, Vector3 position, Camera camera, boolean faceCamera) {
        drawFont3D(font, batch, text, position, 1f, camera, faceCamera);
    }

    /**
     * Draws the given text using the given font in the given 3D position using
     * the 3D coordinate space. If faceCamera is true, the text is rendered
     * always facing the camera. It assumes that {@link SpriteBatch#begin()} has
     * been called. This enables 3D techniques such as z-buffering to be applied
     * to the text textures.
     * 
     * @param font
     *            The font.
     * @param batch
     *            The sprite batch to use.
     * @param text
     *            The text to write.
     * @param x
     *            The x coordinate.
     * @param y
     *            The y coordinate.
     * @param z
     *            The z coordinate.
     * @param scale
     *            The scale of the font.
     * @param rotationCenter
     *            Angles to rotate around center.
     * @param camera
     *            The camera.
     * @param faceCamera
     *            Whether to apply bill-boarding.
     */
    public static void drawFont3D(BitmapFont font, SpriteBatch batch, String text, float x, float y, float z, float scale, float rotationCenter, Camera camera, boolean faceCamera) {
        // Store batch matrices
        aux1.set(batch.getTransformMatrix());
        aux2.set(batch.getProjectionMatrix());

        Quaternion rotation = faceCamera ? getBillboardRotation(camera) : new Quaternion();

        batch.getTransformMatrix().set(camera.combined).translate(x, y, z).rotate(rotation).rotate(0, 1, 0, 180).rotate(0, 0, 1, rotationCenter).scale(scale, scale, scale);
        // Force matrices to be set to shader
        batch.setProjectionMatrix(idt);

        font.draw(batch, text, 0, 0);

        // Restore batch matrices
        batch.setTransformMatrix(aux1);
        batch.setProjectionMatrix(aux2);
    }

    /**
     * Draws the given text using the given font in the given 3D position using
     * the 3D coordinate space. If faceCamera is true, the text is rendered
     * always facing the camera. It assumes that {@link SpriteBatch#begin()} has
     * been called. This enables 3D techniques such as z-buffering to be applied
     * to the text textures.
     * 
     * @param font
     *            The font.
     * @param batch
     *            The sprite batch to use.
     * @param text
     *            The text to write.
     * @param position
     *            The 3D position.
     * @param camera
     *            The camera.
     * @param scale
     *            The scale of the font.
     */
    public static void drawFont3D(BitmapFont font, SpriteBatch batch, String text, Vector3 position, float scale, Camera camera, boolean faceCamera) {
        drawFont3D(font, batch, text, position.x, position.y, position.z, scale, 0, camera, faceCamera);
    }

    public static void drawFont2D(BitmapFont font, SpriteBatch batch, String text, Vector3 position) {
        font.draw(batch, text, position.x, position.y);
    }

    public static void drawFont2D(BitmapFont font, SpriteBatch batch, String text, float x, float y) {
        font.draw(batch, text, x, y);
    }

    public static void drawFont2D(BitmapFont font, SpriteBatch batch, RenderingContext rc, String text, float x, float y, float scale, float width) {
        drawFont2D(font, batch, rc, text, x, y, scale, -1);
    }

    public static void drawFont2D(BitmapFont font, SpriteBatch batch, RenderingContext rc, String text, float x, float y, float scale, int align) {
        // Save
        float scalex = font.getData().scaleX;
        float scaley = font.getData().scaleY;
        aux1.set(batch.getProjectionMatrix());

        batch.getProjectionMatrix().setToOrtho2D(0, 0, rc.w(), rc.h());
        font.getData().setScale(scale);

        if (align > 0) {
            font.draw(batch, text, x, y, rc.w(), align, false);
        } else {
            font.draw(batch, text, x, y);
        }

        // Restore
        batch.setProjectionMatrix(aux1);
        font.getData().setScale(scalex, scaley);

    }

    /**
     * Gets the billboard rotation using the parameters of the given camera
     * 
     * @param camera
     *            The camera
     * @return The quaternion with the rotation
     */
    public static Quaternion getBillboardRotation(Camera camera) {
        return getBillboardRotation(camera.direction, camera.up);
    }

    /**
     * Returns a Quaternion representing the billboard rotation to be applied to
     * a decal that is always to face the given direction and up vector
     * 
     * @param direction
     *            The direction vector
     * @param up
     *            The up vector
     * @return The quaternion with the rotation
     */
    public static Quaternion getBillboardRotation(Vector3 direction, Vector3 up) {
        Quaternion rotation = new Quaternion();
        setBillboardRotation(rotation, direction, up);
        return rotation;
    }

    /**
     * Sets the rotation of this decal based on the (normalized) direction and
     * up vector.
     * 
     * @param rotation
     *            out-parameter, quaternion where the result is set
     * @param direction
     *            the direction vector
     * @param up
     *            the up vector
     */
    public static void setBillboardRotation(Quaternion rotation, final Vector3 direction, final Vector3 up) {
        tmp.set(up).crs(direction).nor();
        tmp2.set(direction).crs(tmp).nor();
        rotation.setFromAxes(tmp.x, tmp2.x, direction.x, tmp.y, tmp2.y, direction.y, tmp.z, tmp2.z, direction.z);
    }

    /**
     * Sets the rotation of this decal based on the (normalized) direction and
     * up vector.
     * 
     * @param direction
     *            the direction vector
     * @param up
     *            the up vector
     */
    public static void setBillboardRotation(Quaternion rotation, final Vector3d direction, final Vector3d up) {
        tmp.set((float) up.x, (float) up.y, (float) up.z).crs((float) direction.x, (float) direction.y, (float) direction.z).nor();
        tmp2.set((float) direction.x, (float) direction.y, (float) direction.z).crs(tmp).nor();
        rotation.setFromAxes(tmp.x, tmp2.x, (float) direction.x, tmp.y, tmp2.y, (float) direction.y, tmp.z, tmp2.z, (float) direction.z);
    }

}
