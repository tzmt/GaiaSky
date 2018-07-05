package gaia.cu9.ari.gaiaorbit.render.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import gaia.cu9.ari.gaiaorbit.GaiaSky;
import gaia.cu9.ari.gaiaorbit.event.EventManager;
import gaia.cu9.ari.gaiaorbit.event.Events;
import gaia.cu9.ari.gaiaorbit.event.IObserver;
import gaia.cu9.ari.gaiaorbit.render.IRenderable;
import gaia.cu9.ari.gaiaorbit.scenegraph.SceneGraphNode.RenderGroup;
import gaia.cu9.ari.gaiaorbit.scenegraph.StarGroup;
import gaia.cu9.ari.gaiaorbit.scenegraph.StarGroup.StarBean;
import gaia.cu9.ari.gaiaorbit.scenegraph.camera.CameraManager;
import gaia.cu9.ari.gaiaorbit.scenegraph.camera.FovCamera;
import gaia.cu9.ari.gaiaorbit.scenegraph.camera.ICamera;
import gaia.cu9.ari.gaiaorbit.util.Constants;
import gaia.cu9.ari.gaiaorbit.util.GlobalConf;
import gaia.cu9.ari.gaiaorbit.util.Logger;
import gaia.cu9.ari.gaiaorbit.util.comp.DistToCameraComparator;
import gaia.cu9.ari.gaiaorbit.util.coord.AstroUtils;

public class StarGroupRenderSystem extends ImmediateRenderSystem implements IObserver {
    private final double BRIGHTNESS_FACTOR;
    /** Hopefully we won't have more than 5000 star groups at once **/
    private final int N_MESHES = 50000;

    private Vector3 aux1, aux2;
    private int sizeOffset, pmOffset;
    private float[] pointAlpha, alphaSizeFovBr;

    public StarGroupRenderSystem(RenderGroup rg, float[] alphas, ShaderProgram[] shaders) {
        super(rg, alphas, shaders);
        BRIGHTNESS_FACTOR = Constants.webgl ? 15 : 10;
        this.comp = new DistToCameraComparator<IRenderable>();
        this.alphaSizeFovBr = new float[4];
        aux1 = new Vector3();
        aux2 = new Vector3();
        EventManager.instance.subscribe(this, Events.STAR_MIN_OPACITY_CMD, Events.DISPOSE_STAR_GROUP_GPU_MESH);
    }

    @Override
    protected void initShaderProgram() {
        pointAlpha = new float[] { GlobalConf.scene.POINT_ALPHA_MIN, GlobalConf.scene.POINT_ALPHA_MIN + GlobalConf.scene.POINT_ALPHA_MAX };
    }

    @Override
    protected void initVertices() {
        /** STARS **/
        meshes = new MeshData[N_MESHES];
    }

    /**
     * Adds a new mesh data to the meshes list and increases the mesh data index
     * 
     * @param nVertices
     *            The max number of vertices this mesh data can hold
     * @return The index of the new mesh data
     */
    private int addMeshData(int nVertices) {
        // look for index
        int mdi;
        for (mdi = 0; mdi < N_MESHES; mdi++) {
            if (meshes[mdi] == null) {
                break;
            }
        }

        if (mdi >= N_MESHES) {
            Logger.error(this.getClass().getSimpleName(), "No more free meshes!");
            return -1;
        }

        curr = new MeshData();
        meshes[mdi] = curr;

        maxVertices = nVertices;

        VertexAttribute[] attribs = buildVertexAttributes();
        curr.mesh = new Mesh(false, maxVertices, 0, attribs);

        curr.vertices = new float[maxVertices * (curr.mesh.getVertexAttributes().vertexSize / 4)];
        curr.vertexSize = curr.mesh.getVertexAttributes().vertexSize / 4;
        curr.colorOffset = curr.mesh.getVertexAttribute(Usage.ColorPacked) != null ? curr.mesh.getVertexAttribute(Usage.ColorPacked).offset / 4 : 0;
        pmOffset = curr.mesh.getVertexAttribute(Usage.Tangent) != null ? curr.mesh.getVertexAttribute(Usage.Tangent).offset / 4 : 0;
        sizeOffset = curr.mesh.getVertexAttribute(Usage.Generic) != null ? curr.mesh.getVertexAttribute(Usage.Generic).offset / 4 : 0;
        return mdi;
    }

    /**
     * Clears the mesh data at the index i
     * 
     * @param i
     *            The index
     */
    public void clearMeshData(int i) {
        assert i >= 0 && i < meshes.length : "Mesh data index out of bounds: " + i + " (n meshes = " + N_MESHES + ")";

        MeshData md = meshes[i];

        if (md != null && md.mesh != null) {
            md.mesh.dispose();
            md.vertices = null;
            md.indices = null;
            meshes[i] = null;
        }
    }

    @Override
    public void renderStud(Array<IRenderable> renderables, ICamera camera, double t) {
        // Enable gl_PointCoord
        Gdx.gl20.glEnable(34913);
        // Enable point sizes
        Gdx.gl20.glEnable(0x8642);


        //renderables.sort(comp);
        if (renderables.size > 0) {
            for (IRenderable renderable : renderables) {
                StarGroup starGroup = (StarGroup) renderable;
                synchronized (starGroup) {
                    if (!starGroup.disposed) {
                        curr = meshes[starGroup.offset];
                        /**
                         * ADD PARTICLES
                         */
                        if (!starGroup.inGpu) {
                            starGroup.offset = addMeshData(starGroup.size());

                            for (StarBean p : starGroup.data()) {
                                // COLOR
                                curr.vertices[curr.vertexIdx + curr.colorOffset] = (float) p.col();

                                // SIZE
                                curr.vertices[curr.vertexIdx + sizeOffset] = (float) (p.size() * Constants.STAR_SIZE_FACTOR);

                                // POSITION [u]
                                curr.vertices[curr.vertexIdx] = (float) p.x();
                                curr.vertices[curr.vertexIdx + 1] = (float) p.y();
                                curr.vertices[curr.vertexIdx + 2] = (float) p.z();

                                // PROPER MOTION [u/yr]
                                curr.vertices[curr.vertexIdx + pmOffset] = (float) p.pmx();
                                curr.vertices[curr.vertexIdx + pmOffset + 1] = (float) p.pmy();
                                curr.vertices[curr.vertexIdx + pmOffset + 2] = (float) p.pmz();

                                curr.vertexIdx += curr.vertexSize;
                            }
                            starGroup.count = starGroup.size() * curr.vertexSize;
                            curr.mesh.setVertices(curr.vertices, 0, starGroup.count);

                            starGroup.inGpu = true;

                        }

                        /**
                         * RENDER
                         */
                        if (curr != null) {
                            int fovmode = camera.getMode().getGaiaFovMode();

                            ShaderProgram shaderProgram = getShaderProgram();

                            shaderProgram.begin();
                            shaderProgram.setUniform2fv("u_pointAlpha", pointAlpha, 0, 2);
                            shaderProgram.setUniformMatrix("u_projModelView", camera.getCamera().combined);
                            shaderProgram.setUniformf("u_camPos", camera.getCurrent().getPos().put(aux1));
                            shaderProgram.setUniformf("u_camDir", camera.getCurrent().getCamera().direction);
                            shaderProgram.setUniformi("u_cubemap", GlobalConf.program.CUBEMAP360_MODE ? 1 : 0);

                            // Relativistic effects
                            addEffectsUniforms(shaderProgram, camera);

                            alphaSizeFovBr[0] = starGroup.opacity * alphas[starGroup.ct.getFirstOrdinal()];
                            alphaSizeFovBr[1] = fovmode == 0 ? (GlobalConf.scene.STAR_POINT_SIZE * rc.scaleFactor * (GlobalConf.program.isStereoFullWidth() ? 1 : 2)) : (GlobalConf.scene.STAR_POINT_SIZE * rc.scaleFactor * 10);
                            alphaSizeFovBr[2] = camera.getFovFactor();
                            alphaSizeFovBr[3] = (float) (GlobalConf.scene.STAR_BRIGHTNESS * BRIGHTNESS_FACTOR);
                            shaderProgram.setUniform4fv("u_alphaSizeFovBr", alphaSizeFovBr, 0, 4);

                            // Days since epoch
                            shaderProgram.setUniformi("u_t", (int) (AstroUtils.getMsSince(GaiaSky.instance.time.getTime(), starGroup.getEpoch()) * Constants.MS_TO_D));
                            shaderProgram.setUniformf("u_ar", GlobalConf.program.isStereoHalfWidth() ? 0.5f : 1f);
                            shaderProgram.setUniformf("u_thAnglePoint", (float) 1e-8);

                            // Update projection if fovmode is 3
                            if (fovmode == 3) {
                                // Cam is Fov1 & Fov2
                                FovCamera cam = ((CameraManager) camera).fovCamera;
                                // Update combined
                                PerspectiveCamera[] cams = camera.getFrontCameras();
                                shaderProgram.setUniformMatrix("u_projModelView", cams[cam.dirindex].combined);
                            }
                            try {
                                curr.mesh.render(shaderProgram, ShapeType.Point.getGlType());
                            } catch (IllegalArgumentException e) {
                                Logger.error("Render exception");
                            }
                            shaderProgram.end();
                        }
                    }
                }
            }
        }
    }

    protected VertexAttribute[] buildVertexAttributes() {
        Array<VertexAttribute> attribs = new Array<VertexAttribute>();
        attribs.add(new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));
        attribs.add(new VertexAttribute(Usage.Tangent, 3, "a_pm"));
        attribs.add(new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE));
        attribs.add(new VertexAttribute(Usage.Generic, 1, "a_size"));

        VertexAttribute[] array = new VertexAttribute[attribs.size];
        for (int i = 0; i < attribs.size; i++)
            array[i] = attribs.get(i);
        return array;
    }

    @Override
    public void notify(Events event, Object... data) {
        switch (event) {
        case STAR_MIN_OPACITY_CMD:
            pointAlpha[0] = (float) data[0];
            pointAlpha[1] = (float) data[0] + GlobalConf.scene.POINT_ALPHA_MAX;
            for (ShaderProgram p : programs) {
                if (p != null && p.isCompiled()) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            p.begin();
                            p.setUniform2fv("u_pointAlpha", pointAlpha, 0, 2);
                            p.end();
                        }
                    });
                }
            }
            break;
        case DISPOSE_STAR_GROUP_GPU_MESH:
            Integer meshIdx = (Integer) data[0];
            clearMeshData(meshIdx);
            break;
        default:
            break;
        }
    }

}
