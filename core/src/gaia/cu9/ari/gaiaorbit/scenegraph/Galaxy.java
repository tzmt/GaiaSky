package gaia.cu9.ari.gaiaorbit.scenegraph;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

import gaia.cu9.ari.gaiaorbit.GaiaSky;
import gaia.cu9.ari.gaiaorbit.render.IRenderable;
import gaia.cu9.ari.gaiaorbit.render.SceneGraphRenderer;
import gaia.cu9.ari.gaiaorbit.scenegraph.camera.FovCamera;
import gaia.cu9.ari.gaiaorbit.scenegraph.camera.ICamera;
import gaia.cu9.ari.gaiaorbit.util.Constants;
import gaia.cu9.ari.gaiaorbit.util.GlobalConf;
import gaia.cu9.ari.gaiaorbit.util.math.Vector3d;
import gaia.cu9.ari.gaiaorbit.util.time.ITimeFrameProvider;

public class Galaxy extends Particle {

    /** Bmag [-4.6/21.4] - Apparent integral B band magnitude **/
    float bmag;

    /** a26 [arcmin] - Major angular diameter **/
    float a26;

    /** b/a - Apparent axial ratio **/
    float ba;

    /** HRV [km/s] - Heliocentric radial velocity **/
    int hrv;

    /**
     * i [deg] - [0/90] Inclination of galaxy from the face-on (i=0) position
     **/
    int i;

    /** TT [-3/11] - Morphology T-type code **/
    int tt;

    /**
     * Mcl [char] - Dwarf galaxy morphology (BCD, HIcld, Im, Ir, S0em, Sm, Sph,
     * Tr, dE, dEem, or dS0em)
     **/
    String mcl;

    public Galaxy() {
        super();
    }

    public Galaxy(Vector3d pos, float appmag, float absmag, float colorbv, String name, float ra, float dec, float bmag, float a26, float ba, int hrv, int i, int tt, String mcl, long starid) {
        super(pos, appmag, absmag, colorbv, name, ra, dec, starid);
        this.bmag = bmag;
        this.a26 = a26;
        this.ba = ba;
        this.hrv = hrv;
        this.i = i;
        this.tt = tt;
        this.mcl = mcl;
    }

    @Override
    public double THRESHOLD_NONE() {
        return (float) 0;
    }

    @Override
    public double THRESHOLD_POINT() {
        return (float) 4E-10;
    }

    @Override
    public double THRESHOLD_QUAD() {
        return (float) 1.7E-12;
    }

    @Override
    protected void setDerivedAttributes() {
        double flux = Math.pow(10, -absmag / 2.5f);
        setRGB(colorbv);

        // Calculate size - This contains arbitrary boundary values to make
        // things nice on the render side
        size = (float) (Math.max((Math.pow(flux, 0.5f) * Constants.PC_TO_U * 1e-3), .6e9f) * 4.5e0d) * 2.5f;
        computedSize = 0;
    }

    /**
     * Re-implementation of update method of {@link CelestialBody} and
     * {@link SceneGraphNode}.
     */
    @Override
    public void update(ITimeFrameProvider time, final Transform parentTransform, ICamera camera, float opacity) {
        if (appmag <= GlobalConf.runtime.LIMIT_MAG_RUNTIME) {
            TH_OVER_FACTOR = (float) (THRESHOLD_POINT() / GlobalConf.scene.LABEL_NUMBER_FACTOR);
            transform.position.set(parentTransform.position).add(pos);
            distToCamera = transform.position.len();

            this.opacity = opacity;

            if (!copy) {
                // addToRender(this, RenderGroup.POINT_GAL);

                viewAngle = (radius / distToCamera) / camera.getFovFactor();
                viewAngleApparent = viewAngle * GlobalConf.scene.STAR_BRIGHTNESS;

                addToRenderLists(camera);
            }

        }
    }

    protected boolean addToRender(IRenderable renderable, RenderGroup rg) {
        SceneGraphRenderer.render_lists.get(rg.ordinal()).add(renderable);
        return true;
    }

    @Override
    protected void addToRenderLists(ICamera camera) {
        if (opacity != 0) {
            if (camera.getCurrent() instanceof FovCamera) {
                // Render as point, do nothing
            } else {
                addToRender(this, RenderGroup.BILLBOARD_GAL);
            }
            if (renderText() && camera.isVisible(GaiaSky.instance.time, this)) {
                addToRender(this, RenderGroup.FONT_LABEL);
            }
        }

    }

    @Override
    public void render(ShaderProgram shader, float alpha, Mesh mesh, ICamera camera) {
        compalpha = alpha;

        float size = getFuzzyRenderSize(camera);

        Vector3 aux = aux3f1.get();
        shader.setUniformf("u_pos", transform.getTranslationf(aux));
        shader.setUniformf("u_size", size);

        shader.setUniformf("u_color", ccPale[0], ccPale[1], ccPale[2], alpha);
        shader.setUniformf("u_alpha", alpha * opacity);
        shader.setUniformf("u_distance", (float) distToCamera);
        shader.setUniformf("u_apparent_angle", (float) viewAngleApparent);
        shader.setUniformf("u_time", (float) GaiaSky.instance.getT() / 5f);

        shader.setUniformf("u_sliders", (tt + 3.4f) / 14f, 0.1f, 0f, i / 180f);
        // Vector3d sph = aux3d1.get();
        // Coordinates.cartesianToSpherical(camera.getDirection(), sph);
        // shader.setUniformf("u_ro", (float) sph.x);
        // shader.setUniformf("u_ta", (float) sph.y);

        shader.setUniformf("u_radius", (float) getRadius());

        // Sprite.render
        mesh.render(shader, GL20.GL_TRIANGLES, 0, 6);
    }

    @Override
    protected float labelFactor() {
        return 1.2e1f;
    }

    @Override
    protected float labelMax() {
        return 0.00004f;
    }

}
