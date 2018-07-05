package gaia.cu9.ari.gaiaorbit.render.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import gaia.cu9.ari.gaiaorbit.GaiaSky;
import gaia.cu9.ari.gaiaorbit.event.Events;
import gaia.cu9.ari.gaiaorbit.event.IObserver;
import gaia.cu9.ari.gaiaorbit.render.IRenderable;
import gaia.cu9.ari.gaiaorbit.scenegraph.Orbit;
import gaia.cu9.ari.gaiaorbit.scenegraph.SceneGraphNode.RenderGroup;
import gaia.cu9.ari.gaiaorbit.scenegraph.camera.ICamera;
import gaia.cu9.ari.gaiaorbit.scenegraph.component.OrbitComponent;
import gaia.cu9.ari.gaiaorbit.util.GlobalConf;
import gaia.cu9.ari.gaiaorbit.util.Logger;
import gaia.cu9.ari.gaiaorbit.util.coord.AstroUtils;
import gaia.cu9.ari.gaiaorbit.util.coord.Coordinates;
import gaia.cu9.ari.gaiaorbit.util.math.MathUtilsd;

public class OrbitalElementsParticlesRenderSystem extends ImmediateRenderSystem implements IObserver {
    private final int N_MESHES = 1;
    private Vector3 auxf1;
    private Matrix4 maux;
    private int elems01Offset, elems02Offset, count;

    public OrbitalElementsParticlesRenderSystem(RenderGroup rg, float[] alphas, ShaderProgram[] shaders) {
        super(rg, alphas, shaders);
        auxf1 = new Vector3();
        maux = new Matrix4();
    }

    @Override
    protected void initShaderProgram() {
    }

    @Override
    protected void initVertices() {
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
        elems01Offset = curr.mesh.getVertexAttribute(Usage.Tangent) != null ? curr.mesh.getVertexAttribute(Usage.Tangent).offset / 4 : 0;
        elems02Offset = curr.mesh.getVertexAttribute(Usage.Generic) != null ? curr.mesh.getVertexAttribute(Usage.Generic).offset / 4 : 0;
        return mdi;
    }

    @Override
    public void renderStud(Array<IRenderable> renderables, ICamera camera, double t) {
        if (renderables.size > 0 && renderables.first().getOpacity() > 0) {
            Orbit first = (Orbit) renderables.first();
            if (!first.elemsInGpu) {
                addMeshData(renderables.size);

                for (IRenderable renderable : renderables) {
                    Orbit orbitElems = (Orbit) renderable;
                    curr = meshes[0];

                    if (!orbitElems.elemsInGpu) {
                        OrbitComponent oc = orbitElems.oc;
                        // ORBIT ELEMS 01
                        curr.vertices[curr.vertexIdx + elems01Offset + 0] = (float) Math.sqrt(AstroUtils.MU_SOL / Math.pow(oc.semimajoraxis * 1000d, 3d));
                        curr.vertices[curr.vertexIdx + elems01Offset + 1] = (float) oc.epoch;
                        curr.vertices[curr.vertexIdx + elems01Offset + 2] = (float) (oc.semimajoraxis * 1000d); // In metres
                        curr.vertices[curr.vertexIdx + elems01Offset + 3] = (float) oc.e;

                        // ORBIT ELEMS 02
                        curr.vertices[curr.vertexIdx + elems02Offset + 0] = (float) (oc.i * MathUtilsd.degRad);
                        curr.vertices[curr.vertexIdx + elems02Offset + 1] = (float) (oc.ascendingnode * MathUtilsd.degRad);
                        curr.vertices[curr.vertexIdx + elems02Offset + 2] = (float) (oc.argofpericenter * MathUtilsd.degRad);
                        curr.vertices[curr.vertexIdx + elems02Offset + 3] = (float) (oc.meananomaly * MathUtilsd.degRad);

                        curr.vertexIdx += curr.vertexSize;

                        orbitElems.elemsInGpu = true;

                    }
                    count = renderables.size * curr.vertexSize;
                    curr.mesh.setVertices(curr.vertices, 0, count);
                }
            }

            if (curr != null) {
                // Enable gl_PointCoord
                Gdx.gl20.glEnable(34913);
                // Enable point sizes
                Gdx.gl20.glEnable(0x8642);

                // Additive blending
                Gdx.gl20.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);

                ShaderProgram shaderProgram = getShaderProgram();

                boolean stereohw = GlobalConf.program.isStereoHalfWidth();

                shaderProgram.begin();
                shaderProgram.setUniformMatrix("u_projModelView", camera.getCamera().combined);
                shaderProgram.setUniformMatrix("u_eclToEq", maux.setToRotation(0, 1, 0, -90).mul(Coordinates.equatorialToEclipticF()));
                shaderProgram.setUniformf("u_camPos", camera.getCurrent().getPos().put(auxf1));
                shaderProgram.setUniformf("u_alpha", alphas[first.ct.getFirstOrdinal()] * first.getOpacity());
                shaderProgram.setUniformf("u_ar", stereohw ? 0.5f : 1f);
                shaderProgram.setUniformf("u_size", rc.scaleFactor);
                shaderProgram.setUniformf("u_scaleFactor", 2 * (stereohw ? 2 : 1));
                shaderProgram.setUniformf("u_ar", stereohw ? 0.5f : 1f);
                shaderProgram.setUniformf("u_profileDecay", 0.1f);
                double currt = AstroUtils.getJulianDate(GaiaSky.instance.time.getTime());
                shaderProgram.setUniformf("u_t", (float) currt);
                // dt in seconds
                shaderProgram.setUniformf("u_dt_s", (float) (86400d * (currt - ((Orbit) renderables.first()).oc.epoch)));

                // Relativistic effects
                addEffectsUniforms(shaderProgram, camera);

                curr.mesh.render(shaderProgram, ShapeType.Point.getGlType());
                shaderProgram.end();

                // Restore
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            }

        }

    }

    protected VertexAttribute[] buildVertexAttributes() {
        Array<VertexAttribute> attribs = new Array<VertexAttribute>();
        attribs.add(new VertexAttribute(Usage.Tangent, 4, "a_orbitelems01"));
        attribs.add(new VertexAttribute(Usage.Generic, 4, "a_orbitelems02"));

        VertexAttribute[] array = new VertexAttribute[attribs.size];
        for (int i = 0; i < attribs.size; i++)
            array[i] = attribs.get(i);
        return array;
    }

    @Override
    public void notify(Events event, Object... data) {
    }

}
