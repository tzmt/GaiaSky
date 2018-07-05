package gaia.cu9.ari.gaiaorbit.render;

import com.badlogic.gdx.graphics.g3d.ModelBatch;

/**
 * To be implemented by all entities wanting to render a clouds layer.
 * 
 * @author Toni Sagrista
 *
 */
public interface ICloudRenderable extends IRenderable {

    /**
     * Renders the clouds.
     * 
     * @param modelBatch
     *            The model batch to use.
     * @param alpha
     *            The opacity.
     * @param t
     *            The time in seconds since the start.
     */
    public void renderClouds(ModelBatch modelBatch, float alpha, double t);
}
