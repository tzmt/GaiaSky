package gaia.cu9.ari.gaiaorbit.render;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.bitfire.postprocessing.filters.Glow;
import com.bitfire.utils.ShaderLoader;

import gaia.cu9.ari.gaiaorbit.GaiaSky;
import gaia.cu9.ari.gaiaorbit.assets.AtmosphereShaderProviderLoader.AtmosphereShaderProviderParameter;
import gaia.cu9.ari.gaiaorbit.assets.GroundShaderProviderLoader.GroundShaderProviderParameter;
import gaia.cu9.ari.gaiaorbit.assets.RelativisticShaderProviderLoader.RelativisticShaderProviderParameter;
import gaia.cu9.ari.gaiaorbit.event.EventManager;
import gaia.cu9.ari.gaiaorbit.event.Events;
import gaia.cu9.ari.gaiaorbit.event.IObserver;
import gaia.cu9.ari.gaiaorbit.render.IPostProcessor.PostProcessBean;
import gaia.cu9.ari.gaiaorbit.render.system.AbstractRenderSystem;
import gaia.cu9.ari.gaiaorbit.render.system.AbstractRenderSystem.RenderSystemRunnable;
import gaia.cu9.ari.gaiaorbit.render.system.BillboardSpriteRenderSystem;
import gaia.cu9.ari.gaiaorbit.render.system.BillboardStarRenderSystem;
import gaia.cu9.ari.gaiaorbit.render.system.FontRenderSystem;
import gaia.cu9.ari.gaiaorbit.render.system.IRenderSystem;
import gaia.cu9.ari.gaiaorbit.render.system.LineGPURenderSystem;
import gaia.cu9.ari.gaiaorbit.render.system.LineQuadRenderSystem;
import gaia.cu9.ari.gaiaorbit.render.system.LineRenderSystem;
import gaia.cu9.ari.gaiaorbit.render.system.MWModelRenderSystem;
import gaia.cu9.ari.gaiaorbit.render.system.MilkyWayRenderSystem;
import gaia.cu9.ari.gaiaorbit.render.system.ModelBatchRenderSystem;
import gaia.cu9.ari.gaiaorbit.render.system.ModelBatchRenderSystem.ModelRenderType;
import gaia.cu9.ari.gaiaorbit.render.system.OrbitalElementsParticlesRenderSystem;
import gaia.cu9.ari.gaiaorbit.render.system.ParticleEffectsRenderSystem;
import gaia.cu9.ari.gaiaorbit.render.system.ParticleGroupRenderSystem;
import gaia.cu9.ari.gaiaorbit.render.system.PixelRenderSystem;
import gaia.cu9.ari.gaiaorbit.render.system.ShapeRenderSystem;
import gaia.cu9.ari.gaiaorbit.render.system.StarGroupRenderSystem;
import gaia.cu9.ari.gaiaorbit.scenegraph.AbstractPositionEntity;
import gaia.cu9.ari.gaiaorbit.scenegraph.MilkyWay;
import gaia.cu9.ari.gaiaorbit.scenegraph.ModelBody;
import gaia.cu9.ari.gaiaorbit.scenegraph.Particle;
import gaia.cu9.ari.gaiaorbit.scenegraph.SceneGraphNode.RenderGroup;
import gaia.cu9.ari.gaiaorbit.scenegraph.Star;
import gaia.cu9.ari.gaiaorbit.scenegraph.camera.CameraManager.CameraMode;
import gaia.cu9.ari.gaiaorbit.scenegraph.camera.ICamera;
import gaia.cu9.ari.gaiaorbit.util.ComponentTypes;
import gaia.cu9.ari.gaiaorbit.util.Constants;
import gaia.cu9.ari.gaiaorbit.util.GlobalConf;
import gaia.cu9.ari.gaiaorbit.util.GlobalResources;
import gaia.cu9.ari.gaiaorbit.util.Logger;
import gaia.cu9.ari.gaiaorbit.util.gravwaves.RelativisticEffectsManager;
import gaia.cu9.ari.gaiaorbit.util.math.MathUtilsd;
import gaia.cu9.ari.gaiaorbit.util.math.Vector3d;
import gaia.cu9.ari.gaiaorbit.util.override.AtmosphereShaderProvider;
import gaia.cu9.ari.gaiaorbit.util.override.GroundShaderProvider;
import gaia.cu9.ari.gaiaorbit.util.override.RelativisticShaderProvider;
import gaia.cu9.ari.gaiaorbit.util.override.ShaderProgramProvider.ShaderProgramParameter;

/**
 * Renders a scenegraph.
 * 
 * @author Toni Sagrista
 *
 */
public class SceneGraphRenderer extends AbstractRenderer implements IProcessRenderer, IObserver {

    /** Contains the flags representing each type's visibility **/
    public static ComponentTypes visible;
    /** Contains the last update time of each of the flags **/
    public static long[] times;
    /** Alpha values for each type **/
    public static float[] alphas;

    public AbstractRenderSystem[] pixelRenderSystems;

    private BitmapFont font3d, font2d, fontTitles;

    private ShaderProgram fontShader;

    private ShaderProgram[] starGroupShaders, particleGroupShaders, particleEffectShaders, orbitElemShaders, lineShaders, lineQuadShaders, lineGpuShaders, mwPointShaders, mwOitShaders, mwNebulaShaders, pixelShaders, galShaders, spriteShaders, starShaders;
    private AssetDescriptor<ShaderProgram>[] starGroupDesc, particleGroupDesc, particleEffectDesc, orbitElemDesc, lineDesc, lineQuadDesc, lineGpuDesc, mwPointDesc, mwOitDesc, mwNebulaDesc, pixelDesc, galDesc, spriteDesc, starDesc;

    private int maxTexSize;

    /** Render lists for all render groups **/
    public static Array<Array<IRenderable>> render_lists;

    // Two model batches, for front (models), back and atmospheres
    private SpriteBatch spriteBatch, fontBatch;

    private Array<IRenderSystem> renderProcesses;

    RenderSystemRunnable blendNoDepthRunnable, blendDepthRunnable, additiveBlendDepthRunnable, restoreRegularBlend;

    /** The particular current scene graph renderer **/
    private ISGR sgr;
    /**
     * Renderers vector, with 0 = normal, 1 = stereoscopic, 2 = FOV, 3 = cubemap
     **/
    private ISGR[] sgrs;
    // Indexes
    final int SGR_DEFAULT_IDX = 0, SGR_STEREO_IDX = 1, SGR_FOV_IDX = 2, SGR_CUBEMAP_IDX = 3;

    // Camera at light position, with same direction. For shadow mapping
    private Camera cameraLight;
    private Array<ModelBody> candidates;
    public FrameBuffer[] shadowMapFb;
    public Matrix4[] shadowMapCombined;
    public Map<ModelBody, Texture> smTexMap;
    public Map<ModelBody, Matrix4> smCombinedMap;
    public ModelBatch modelBatchDepth;

    // Light glow pre-render
    public FrameBuffer glowFb;
    public Texture glowTex;
    public ModelBatch modelBatchOpaque;

    private Vector3 aux1;
    private Vector3d aux1d;

    Array<IRenderable> stars;

    AbstractRenderSystem billboardStarsProc;
    MWModelRenderSystem mwrs;

    public SceneGraphRenderer() {
        super();
    }

    private AssetDescriptor<ShaderProgram>[] loadShader(AssetManager manager, String vfile, String ffile, String[] names, String[] prependVertex) {
        @SuppressWarnings("unchecked")
        AssetDescriptor<ShaderProgram>[] result = new AssetDescriptor[prependVertex.length];

        int i = 0;
        for (String prep : prependVertex) {
            ShaderProgramParameter spp = new ShaderProgramParameter();
            spp.prependVertexCode = prep;
            spp.vertexFile = vfile;
            spp.fragmentFile = ffile;
            manager.load(names[i], ShaderProgram.class, spp);
            AssetDescriptor<ShaderProgram> desc = new AssetDescriptor<ShaderProgram>(names[i], ShaderProgram.class, spp);
            result[i] = desc;

            i++;
        }

        return result;
    }

    @Override
    public void initialize(AssetManager manager) {
        ShaderLoader.Pedantic = false;
        ShaderProgram.pedantic = false;

        /** LOAD SHADER PROGRAMS WITH ASSET MANAGER **/
        manager.load("shader/font.vertex.glsl", ShaderProgram.class);

        starDesc = loadShader(manager, "shader/star.vertex.glsl", "shader/star.fragment.glsl", new String[] { "star", "starRel", "starGrav", "starRelGrav" }, new String[] { "", "#define relativisticEffects\n", "#define gravitationalWaves\n", "#define relativisticEffects\n#define gravitationalWaves\n" });
        spriteDesc = loadShader(manager, "shader/sprite.vertex.glsl", "shader/sprite.fragment.glsl", new String[] { "sprite", "spriteRel", "spriteGrav", "spriteRelGrav" }, new String[] { "", "#define relativisticEffects\n", "#define gravitationalWaves\n", "#define relativisticEffects\n#define gravitationalWaves\n" });
        pixelDesc = loadShader(manager, "shader/point.vertex.glsl", "shader/point.fragment.glsl", new String[] { "pixel", "pixelRel", "pixelGrav", "pixelRelGrav" }, new String[] { "", "#define relativisticEffects\n", "#define gravitationalWaves\n", "#define relativisticEffects\n#define gravitationalWaves\n" });
        mwPointDesc = loadShader(manager, "shader/point.galaxy.vertex.glsl", "shader/point.galaxy.fragment.glsl", new String[] { "pointGal", "pointGalRel", "pointGalGrav", "pointGalRelGrav" }, new String[] { "", "#define relativisticEffects\n", "#define gravitationalWaves\n", "#define relativisticEffects\n#define gravitationalWaves\n" });
        mwOitDesc = loadShader(manager, "shader/galaxy.oit.vertex.glsl", "shader/galaxy.oit.fragment.glsl", new String[] { "galOit", "galOitRel", "galOitGrav", "galOitRelGrav" }, new String[] { "", "#define relativisticEffects\n", "#define gravitationalWaves\n", "#define relativisticEffects\n#define gravitationalWaves\n" });

        mwNebulaDesc = loadShader(manager, "shader/nebula.vertex.glsl", "shader/nebula.fragment.glsl", new String[] { "nebula", "nebulaRel", "nebulaGrav", "nebulaRelGrav" }, new String[] { "", "#define relativisticEffects\n", "#define gravitationalWaves\n", "#define relativisticEffects\n#define gravitationalWaves\n" });
        lineDesc = loadShader(manager, "shader/line.vertex.glsl", "shader/line.fragment.glsl", new String[] { "line", "lineRel", "lineGrav", "lineRelGrav" }, new String[] { "", "#define relativisticEffects\n", "#define gravitationalWaves\n", "#define relativisticEffects\n#define gravitationalWaves\n" });
        lineQuadDesc = loadShader(manager, "shader/line.quad.vertex.glsl", "shader/line.quad.fragment.glsl", new String[] { "lineQuad", "lineQuadRel", "lineQuadGrav", "lineQuadRelGrav" }, new String[] { "", "#define relativisticEffects\n", "#define gravitationalWaves\n", "#define relativisticEffects\n#define gravitationalWaves\n" });
        lineGpuDesc = loadShader(manager, "shader/line.gpu.vertex.glsl", "shader/line.gpu.fragment.glsl", new String[] { "lineGpu", "lineGpuRel", "lineGpuGrav", "lineGpuRelGrav" }, new String[] { "", "#define relativisticEffects\n", "#define gravitationalWaves\n", "#define relativisticEffects\n#define gravitationalWaves\n" });
        galDesc = loadShader(manager, "shader/gal.vertex.glsl", "shader/gal.fragment.glsl", new String[] { "gal", "galRel", "galGrav", "galRelGrav" }, new String[] { "", "#define relativisticEffects\n", "#define gravitationalWaves\n", "#define relativisticEffects\n#define gravitationalWaves\n" });
        particleEffectDesc = loadShader(manager, "shader/particle.effect.vertex.glsl", "shader/particle.effect.fragment.glsl", new String[] { "particleEffect", "particleEffectRel", "particleEffectGrav", "particleEffectRelGrav" }, new String[] { "", "#define relativisticEffects\n", "#define gravitationalWaves\n", "#define relativisticEffects\n#define gravitationalWaves\n" });
        particleGroupDesc = loadShader(manager, "shader/particle.group.vertex.glsl", "shader/particle.group.fragment.glsl", new String[] { "particleGroup", "particleGroupRel", "particleGroupGrav", "particleGroupRelGrav" }, new String[] { "", "#define relativisticEffects\n", "#define gravitationalWaves\n", "#define relativisticEffects\n#define gravitationalWaves\n" });
        starGroupDesc = loadShader(manager, "shader/star.group.vertex.glsl", "shader/star.group.fragment.glsl", new String[] { "starGroup", "starGroupRel", "starGroupGrav", "starGroupRelGrav" }, new String[] { "", "#define relativisticEffects\n", "#define gravitationalWaves\n", "#define relativisticEffects\n#define gravitationalWaves\n" });
        orbitElemDesc = loadShader(manager, "shader/orbitelem.vertex.glsl", "shader/particle.group.fragment.glsl", new String[] { "orbitElem", "orbitElemRel", "orbitElemGrav", "orbitElemRelGrav" }, new String[] { "", "#define relativisticEffects\n", "#define gravitationalWaves\n", "#define relativisticEffects\n#define gravitationalWaves\n" });

        manager.load("atmgrounddefault", GroundShaderProvider.class, new GroundShaderProviderParameter("shader/default.vertex.glsl", "shader/default.fragment.glsl"));
        manager.load("additive", RelativisticShaderProvider.class, new RelativisticShaderProviderParameter("shader/default.vertex.glsl", "shader/default.additive.fragment.glsl"));
        manager.load("grids", RelativisticShaderProvider.class, new RelativisticShaderProviderParameter("shader/default.vertex.glsl", "shader/default.grid.fragment.glsl"));
        manager.load("spsurface", RelativisticShaderProvider.class, new RelativisticShaderProviderParameter("shader/starsurface.vertex.glsl", "shader/starsurface.fragment.glsl"));
        manager.load("spbeam", RelativisticShaderProvider.class, new RelativisticShaderProviderParameter("shader/default.vertex.glsl", "shader/beam.fragment.glsl"));
        manager.load("spdepth", RelativisticShaderProvider.class, new RelativisticShaderProviderParameter("shader/normal.vertex.glsl", "shader/depth.fragment.glsl"));
        manager.load("spopaque", RelativisticShaderProvider.class, new RelativisticShaderProviderParameter("shader/normal.vertex.glsl", "shader/opaque.fragment.glsl"));
        manager.load("atm", AtmosphereShaderProvider.class, new AtmosphereShaderProviderParameter("shader/atm.vertex.glsl", "shader/atm.fragment.glsl"));
        manager.load("atmground", GroundShaderProvider.class, new GroundShaderProviderParameter("shader/normal.vertex.glsl", "shader/normal.fragment.glsl"));
        manager.load("cloud", GroundShaderProvider.class, new GroundShaderProviderParameter("shader/cloud.vertex.glsl", "shader/cloud.fragment.glsl"));

        BitmapFontParameter bfp = new BitmapFontParameter();
        bfp.magFilter = TextureFilter.Linear;
        bfp.minFilter = TextureFilter.Linear;
        manager.load("font/main-font.fnt", BitmapFont.class, bfp);
        manager.load("font/font2d.fnt", BitmapFont.class, bfp);
        manager.load("font/font-titles.fnt", BitmapFont.class, bfp);

        stars = new Array<IRenderable>();

        pixelRenderSystems = new AbstractRenderSystem[3];

        renderProcesses = new Array<IRenderSystem>();

        blendNoDepthRunnable = new RenderSystemRunnable() {
            @Override
            public void run(AbstractRenderSystem renderSystem, Array<IRenderable> renderables, ICamera camera) {
                Gdx.gl.glEnable(GL20.GL_BLEND);
                Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
                Gdx.gl.glDepthMask(false);
            }
        };
        blendDepthRunnable = new RenderSystemRunnable() {
            @Override
            public void run(AbstractRenderSystem renderSystem, Array<IRenderable> renderables, ICamera camera) {
                Gdx.gl.glEnable(GL20.GL_BLEND);
                Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
                Gdx.gl.glDepthMask(true);
            }
        };
        additiveBlendDepthRunnable = new RenderSystemRunnable() {
            @Override
            public void run(AbstractRenderSystem renderSystem, Array<IRenderable> renderables, ICamera camera) {
                Gdx.gl.glEnable(GL20.GL_BLEND);
                Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
                Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);
                Gdx.gl.glDepthMask(true);
            }
        };
        restoreRegularBlend = new RenderSystemRunnable() {
            @Override
            public void run(AbstractRenderSystem renderSystem, Array<IRenderable> renderables, ICamera camera) {
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            }
        };

        if (GlobalConf.scene.SHADOW_MAPPING) {
            // Shadow map camera
            cameraLight = new PerspectiveCamera(0.5f, GlobalConf.scene.SHADOW_MAPPING_RESOLUTION, GlobalConf.scene.SHADOW_MAPPING_RESOLUTION);

            // Aux vectors
            aux1 = new Vector3();
            aux1d = new Vector3d();

            // Build frame buffers and arrays
            buildShadowMapData();
        }

        if (GlobalConf.postprocess.POSTPROCESS_LIGHT_SCATTERING) {
            buildGlowData();
        }
    }

    private ShaderProgram[] fetchShaderProgram(AssetManager manager, AssetDescriptor<ShaderProgram>[] descriptors, String... names) {
        int n = descriptors.length;
        ShaderProgram[] shaders = new ShaderProgram[n];

        for (int i = 0; i < n; i++) {
            shaders[i] = manager.get(descriptors[i]);
            if (!shaders[i].isCompiled()) {
                Logger.error(new RuntimeException(), this.getClass().getName() + " - " + names[i] + " shader compilation failed:\n" + shaders[i].getLog());
            }
        }
        return shaders;
    }

    public void doneLoading(AssetManager manager) {
        IntBuffer intBuffer = BufferUtils.newIntBuffer(16);
        Gdx.gl20.glGetIntegerv(GL20.GL_MAX_TEXTURE_SIZE, intBuffer);
        maxTexSize = intBuffer.get();
        Logger.info(this.getClass().getSimpleName(), "Max texture size: " + maxTexSize + "^2 pixels");

        /**
         * STAR SHADER
         */
        starShaders = fetchShaderProgram(manager, starDesc, "Star", "Star (rel)", "Star (grav)");

        /**
         * GALAXY SHADER
         */
        galShaders = fetchShaderProgram(manager, galDesc, "Galaxy", "Galaxy (rel)", "Galaxy (grav)");

        /**
         * FONT SHADER
         */
        fontShader = manager.get("shader/font.vertex.glsl");
        if (!fontShader.isCompiled()) {
            Logger.error(new RuntimeException(), this.getClass().getName() + " - Font shader compilation failed:\n" + fontShader.getLog());
        }

        /**
         * SPRITE SHADER
         */
        spriteShaders = fetchShaderProgram(manager, spriteDesc, "Sprite", "Sprite (rel)", "Sprite (grav)");

        /**
         * LINE
         */
        lineShaders = fetchShaderProgram(manager, lineDesc, "Line", "Line (rel)", "Line (grav)");

        /**
         * LINE QUAD
         */
        lineQuadShaders = fetchShaderProgram(manager, lineQuadDesc, "Line quad", "Line quad (rel)", "Line quad (grav)");

        /**
         * LINE GPU
         */
        lineGpuShaders = fetchShaderProgram(manager, lineGpuDesc, "Line GPU", "Line GPU (rel)", "Line GPU (grav)");

        /**
         * MW POINTS
         */
        mwPointShaders = fetchShaderProgram(manager, mwPointDesc, "MW point", "MW point (rel)", "MW point (grav)");

        /**
         * MW Order-Independent Transparency
         */
        mwOitShaders = fetchShaderProgram(manager, mwOitDesc, "Gal OIT", "Gal OIT (rel)", "Gal OIT (grav)");

        /**
         * MW NEBULAE
         */
        mwNebulaShaders = fetchShaderProgram(manager, mwNebulaDesc, "MW nebula", "MW nebula (rel)", "MW nebula (grav)");

        /**
         * PARTICLE EFFECT - default and relativistic
         */
        particleEffectShaders = fetchShaderProgram(manager, particleEffectDesc, "Particle effects", "Particle effects (rel)", "Particle effects (grav)");

        /**
         * PARTICLE GROUP - default and relativistic
         */
        particleGroupShaders = fetchShaderProgram(manager, particleGroupDesc, "Particle group", "Particle group (rel)", "Particle group (grav)");

        /**
         * STAR GROUP - default and relativistic
         */
        starGroupShaders = fetchShaderProgram(manager, starGroupDesc, "Star group", "Star group (rel)", "Star group (grav)");

        /**
         * PIXEL
         */
        pixelShaders = fetchShaderProgram(manager, pixelDesc, "Pixel", "Pixel (rel), Pixel (grav)");

        /**
         * ORBITAL ELEMENTS PARTICLES - default and relativistic
         */
        orbitElemShaders = fetchShaderProgram(manager, orbitElemDesc, "Orbital elements particles", "Orbital elements particles (rel)", "Orbital elements particles (grav)");

        RenderGroup[] renderGroups = RenderGroup.values();
        render_lists = new Array<Array<IRenderable>>(renderGroups.length);
        for (RenderGroup rg : renderGroups) {
            render_lists.add(new Array<IRenderable>(40000));
        }

        ShaderProvider sp = manager.get("atmgrounddefault");
        ShaderProvider spadditive = manager.get("additive");
        ShaderProvider spgrids = manager.get("grids");
        ShaderProvider spnormal = manager.get("atmground");
        ShaderProvider spatm = manager.get("atm");
        ShaderProvider spcloud = manager.get("cloud");
        ShaderProvider spsurface = manager.get("spsurface");
        ShaderProvider spbeam = manager.get("spbeam");
        ShaderProvider spdepth = manager.get("spdepth");
        ShaderProvider spopaque = manager.get("spopaque");

        RenderableSorter noSorter = new RenderableSorter() {
            @Override
            public void sort(Camera camera, Array<Renderable> renderables) {
                // Does nothing
            }
        };

        ModelBatch modelBatchDefault = new ModelBatch(sp, noSorter);
        ModelBatch modelBatchMesh = new ModelBatch(spadditive, noSorter);
        modelBatchMesh.getRenderContext().setBlending(true, GL30.GL_ONE, GL30.GL_ONE);
        modelBatchMesh.getRenderContext().setDepthTest(GL30.GL_LEQUAL, 1e11f, 1e13f);
        ModelBatch modelBatchGrids = new ModelBatch(spgrids, noSorter);
        ModelBatch modelBatchNormal = new ModelBatch(spnormal, noSorter);
        ModelBatch modelBatchAtmosphere = new ModelBatch(spatm, noSorter);
        ModelBatch modelBatchCloud = new ModelBatch(spcloud, noSorter);
        ModelBatch modelBatchStar = new ModelBatch(spsurface, noSorter);
        ModelBatch modelBatchBeam = new ModelBatch(spbeam, noSorter);
        modelBatchDepth = new ModelBatch(spdepth, noSorter);
        modelBatchOpaque = new ModelBatch(spopaque, noSorter);

        // Fonts
        font3d = manager.get("font/main-font.fnt");
        font2d = manager.get("font/font2d.fnt");
        fontTitles = manager.get("font/font-titles.fnt");

        // Sprites
        spriteBatch = GlobalResources.spriteBatch;
        spriteBatch.enableBlending();

        // Font batch
        fontBatch = new SpriteBatch(2000, fontShader);
        fontBatch.enableBlending();

        ComponentType[] comps = ComponentType.values();

        // Set reference
        visible = new ComponentTypes();
        for (int i = 0; i < GlobalConf.scene.VISIBILITY.length; i++) {
            if (GlobalConf.scene.VISIBILITY[i]) {
                visible.set(ComponentType.values()[i].ordinal());
            }

        }

        times = new long[comps.length];
        alphas = new float[comps.length];
        for (int i = 0; i < comps.length; i++) {
            times[i] = -20000l;
            alphas[i] = 0f;
        }

        /**
         * DEPTH BUFFER BITS
         */

        intBuffer.clear();
        Gdx.gl.glGetIntegerv(GL20.GL_DEPTH_BITS, intBuffer);
        Logger.info(this.getClass().getSimpleName(), "Depth buffer size: " + intBuffer.get() + " bits");

        /**
         * INITIALIZE SGRs
         */
        sgrs = new ISGR[4];
        sgrs[SGR_DEFAULT_IDX] = new SGR();
        sgrs[SGR_STEREO_IDX] = new SGRStereoscopic();
        sgrs[SGR_FOV_IDX] = new SGRFov();
        sgrs[SGR_CUBEMAP_IDX] = new SGRCubemap();
        sgr = null;

        /**
         *
         * ======= INITIALIZE RENDER COMPONENTS =======
         *
         **/

        // POINTS
        AbstractRenderSystem pixelStarProc = new PixelRenderSystem(RenderGroup.POINT_STAR, alphas, pixelShaders, ComponentType.Stars);
        pixelStarProc.setPreRunnable(blendNoDepthRunnable);

        // MODEL FRONT-BACK - NO CULL FACE
        AbstractRenderSystem modelFrontBackProc = new ModelBatchRenderSystem(RenderGroup.MODEL_DEFAULT, alphas, modelBatchDefault, ModelRenderType.NORMAL);
        modelFrontBackProc.setPreRunnable(blendDepthRunnable);
        modelFrontBackProc.setPostRunnable(new RenderSystemRunnable() {
            @Override
            public void run(AbstractRenderSystem renderSystem, Array<IRenderable> renderables, ICamera camera) {
                // This always goes at the back, clear depth buffer
                Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
            }
        });

        // MODEL GRID
        AbstractRenderSystem modelGridsProc = new ModelBatchRenderSystem(RenderGroup.MODEL_GRIDS, alphas, modelBatchGrids, ModelRenderType.NORMAL);
        modelGridsProc.setPreRunnable(blendDepthRunnable);
        modelGridsProc.setPostRunnable(new RenderSystemRunnable() {
            @Override
            public void run(AbstractRenderSystem renderSystem, Array<IRenderable> renderables, ICamera camera) {
                // This always goes at the back, clear depth buffer
                Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
            }
        });

        // VOLUMETRIC CLOUDS
        //        AbstractRenderSystem cloudsProc = new VolumeCloudsRenderSystem(alphas);
        //        cloudsProc.setPreRunnable(blendNoDepthRunnable);

        // ANNOTATIONS
        AbstractRenderSystem annotationsProc = new FontRenderSystem(RenderGroup.FONT_ANNOTATION, alphas, spriteBatch, null);
        annotationsProc.setPreRunnable(blendNoDepthRunnable);
        annotationsProc.setPostRunnable(new RenderSystemRunnable() {
            @Override
            public void run(AbstractRenderSystem renderSystem, Array<IRenderable> renderables, ICamera camera) {
                // This always goes at the back, clear depth buffer
                Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
            }
        });

        // BILLBOARD STARS
        billboardStarsProc = new BillboardStarRenderSystem(RenderGroup.BILLBOARD_STAR, alphas, starShaders, "data/tex/star_glow_s.png", ComponentType.Stars.ordinal());
        billboardStarsProc.setPreRunnable(blendNoDepthRunnable);
        billboardStarsProc.setPostRunnable(new RenderSystemRunnable() {

            private float[] positions = new float[Glow.N * 2];
            private float[] viewAngles = new float[Glow.N];
            private float[] colors = new float[Glow.N * 3];
            private Vector3 auxv = new Vector3();
            private Vector3d auxd = new Vector3d();

            @Override
            public void run(AbstractRenderSystem renderSystem, Array<IRenderable> renderables, ICamera camera) {
                int size = renderables.size;
                if (PostProcessorFactory.instance.getPostProcessor().isLightScatterEnabled() && Particle.renderOn) {
                    // Compute light positions for light scattering or light
                    // glow
                    int lightIndex = 0;
                    float angleEdgeDeg = camera.getAngleEdge() * MathUtils.radDeg;
                    for (int i = size - 1; i >= 0; i--) {
                        IRenderable s = renderables.get(i);
                        if (s instanceof Particle) {
                            Particle p = (Particle) s;
                            if (!Constants.webgl && lightIndex < Glow.N && (GlobalConf.program.CUBEMAP360_MODE || GaiaSky.instance.cam.getDirection().angle(p.transform.position) < angleEdgeDeg)) {
                                Vector3d pos3d = p.transform.getTranslation(auxd);

                                // Aberration
                                GlobalResources.applyRelativisticAberration(pos3d, camera);
                                // GravWaves
                                RelativisticEffectsManager.getInstance().gravitationalWavePos(pos3d);
                                Vector3 pos3 = pos3d.put(auxv);

                                camera.getCamera().project(pos3);
                                // Here we **need** to use
                                // Gdx.graphics.getWidth/Height() because we use
                                // camera.project() which uses screen
                                // coordinates only
                                positions[lightIndex * 2] = auxv.x / Gdx.graphics.getWidth();
                                positions[lightIndex * 2 + 1] = auxv.y / Gdx.graphics.getHeight();
                                viewAngles[lightIndex] = (float) p.viewAngleApparent;
                                colors[lightIndex * 3] = p.cc[0];
                                colors[lightIndex * 3 + 1] = p.cc[1];
                                colors[lightIndex * 3 + 2] = p.cc[2];
                                lightIndex++;
                            }
                        }
                    }
                    EventManager.instance.post(Events.LIGHT_POS_2D_UPDATED, lightIndex, positions, viewAngles, colors, glowTex);
                } else {
                    EventManager.instance.post(Events.LIGHT_POS_2D_UPDATED, 0, positions, viewAngles, colors, glowTex);
                }
            }

        });

        // BILLBOARD GALAXIES
        AbstractRenderSystem billboardGalaxiesProc = new BillboardStarRenderSystem(RenderGroup.BILLBOARD_GAL, alphas, galShaders, "data/tex/static.jpg", ComponentType.Galaxies.ordinal());
        billboardGalaxiesProc.setPreRunnable(blendNoDepthRunnable);

        // BILLBOARD SPRITES
        AbstractRenderSystem billboardSpritesProc = new BillboardSpriteRenderSystem(RenderGroup.BILLBOARD_SPRITE, alphas, spriteShaders, ComponentType.Clusters.ordinal());
        billboardSpritesProc.setPreRunnable(blendNoDepthRunnable);

        // LINES CPU
        AbstractRenderSystem lineProc = getLineRenderSystem();

        // LINES GPU
        AbstractRenderSystem lineGpuProc = new LineGPURenderSystem(RenderGroup.LINE_GPU, alphas, lineGpuShaders);
        lineGpuProc.setPreRunnable(blendDepthRunnable);

        // MODEL MESH
        AbstractRenderSystem modelMeshProc = new ModelBatchRenderSystem(RenderGroup.MODEL_MESH, alphas, modelBatchMesh, ModelRenderType.NORMAL, false);
        modelMeshProc.setPreRunnable(blendDepthRunnable);

        // MODEL FRONT
        AbstractRenderSystem modelFrontProc = new ModelBatchRenderSystem(RenderGroup.MODEL_NORMAL, alphas, modelBatchNormal, ModelRenderType.NORMAL);
        modelFrontProc.setPreRunnable(blendDepthRunnable);

        // MODEL BEAM
        AbstractRenderSystem modelBeamProc = new ModelBatchRenderSystem(RenderGroup.MODEL_BEAM, alphas, modelBatchBeam, ModelRenderType.NORMAL, false);
        modelBeamProc.setPreRunnable(blendDepthRunnable);

        // GALAXY
        //mwrs = new MWModelRenderSystem(RenderGroup.GALAXY, alphas, MWModelRenderSystem.oit ? mwOitShaders : mwPointShaders);
        //AbstractRenderSystem galaxyProc = mwrs;
        AbstractRenderSystem galaxyProc = new MilkyWayRenderSystem(RenderGroup.GALAXY, alphas, modelBatchDefault, mwPointShaders, mwNebulaShaders);
        galaxyProc.setPreRunnable(blendNoDepthRunnable);

        // PARTICLE EFFECTS
        AbstractRenderSystem particleEffectsProc = new ParticleEffectsRenderSystem(null, alphas, particleEffectShaders);
        particleEffectsProc.setPreRunnable(blendNoDepthRunnable);

        // PARTICLE GROUP
        AbstractRenderSystem particleGroupProc = new ParticleGroupRenderSystem(RenderGroup.PARTICLE_GROUP, alphas, particleGroupShaders);
        particleGroupProc.setPreRunnable(blendNoDepthRunnable);

        // STAR GROUP
        AbstractRenderSystem starGroupProc = new StarGroupRenderSystem(RenderGroup.STAR_GROUP, alphas, starGroupShaders);
        starGroupProc.setPreRunnable(additiveBlendDepthRunnable);
        starGroupProc.setPostRunnable(restoreRegularBlend);

        // ORBITAL ELEMENTS PARTICLES
        AbstractRenderSystem orbitElemProc = new OrbitalElementsParticlesRenderSystem(RenderGroup.PARTICLE_ORBIT_ELEMENTS, alphas, orbitElemShaders);
        orbitElemProc.setPreRunnable(blendNoDepthRunnable);

        // MODEL STARS
        AbstractRenderSystem modelStarsProc = new ModelBatchRenderSystem(RenderGroup.MODEL_STAR, alphas, modelBatchStar, ModelRenderType.NORMAL);
        modelStarsProc.setPreRunnable(blendDepthRunnable);

        // LABELS
        AbstractRenderSystem labelsProc = new FontRenderSystem(RenderGroup.FONT_LABEL, alphas, fontBatch, fontShader, font3d, font2d, fontTitles);
        labelsProc.setPreRunnable(blendNoDepthRunnable);

        // BILLBOARD SSO
        AbstractRenderSystem billboardSSOProc = new BillboardStarRenderSystem(RenderGroup.BILLBOARD_SSO, alphas, starShaders, "img/sso.png", -1);
        billboardSSOProc.setPreRunnable(additiveBlendDepthRunnable);
        billboardSSOProc.setPostRunnable(restoreRegularBlend);

        // MODEL ATMOSPHERE
        AbstractRenderSystem modelAtmProc = new ModelBatchRenderSystem(RenderGroup.MODEL_ATM, alphas, modelBatchAtmosphere, ModelRenderType.ATMOSPHERE) {
            @Override
            public float getAlpha(IRenderable s) {
                return alphas[ComponentType.Atmospheres.ordinal()] * (float) Math.pow(alphas[s.getComponentType().getFirstOrdinal()], 2);
            }

            @Override
            protected boolean mustRender() {
                return alphas[ComponentType.Atmospheres.ordinal()] * alphas[ComponentType.Planets.ordinal()] > 0;
            }
        };
        modelAtmProc.setPreRunnable(blendDepthRunnable);
        modelAtmProc.setPostRunnable(new RenderSystemRunnable() {
            @Override
            public void run(AbstractRenderSystem renderSystem, Array<IRenderable> renderables, ICamera camera) {
                // Clear depth buffer before rendering things up close
                //Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
            }
        });

        // MODEL CLOUDS
        AbstractRenderSystem modelCloudProc = new ModelBatchRenderSystem(RenderGroup.MODEL_CLOUD, alphas, modelBatchCloud, ModelRenderType.CLOUD);

        // SHAPES
        AbstractRenderSystem shapeProc = new ShapeRenderSystem(RenderGroup.SHAPE, alphas);
        shapeProc.setPreRunnable(blendNoDepthRunnable);

        // MODEL CLOSE UP
        //        AbstractRenderSystem modelCloseUpProc = new ModelBatchRenderSystem(RenderGroup.MODEL_CLOSEUP,  alphas, modelBatchCloseUp, false);
        //        modelCloseUpProc.setPreRunnable(blendDepthRunnable);

        // Add components to set
        renderProcesses.add(modelFrontBackProc);
        renderProcesses.add(modelGridsProc);
        renderProcesses.add(pixelStarProc);
        renderProcesses.add(starGroupProc);
        renderProcesses.add(orbitElemProc);
        renderProcesses.add(annotationsProc);
        renderProcesses.add(particleGroupProc);

        // Billboards for galaxies and stars
        renderProcesses.add(billboardGalaxiesProc);
        renderProcesses.add(billboardStarsProc);
        renderProcesses.add(galaxyProc);

        // Billboard for sprites
        renderProcesses.add(billboardSpritesProc);

        renderProcesses.add(modelFrontProc);
        renderProcesses.add(modelBeamProc);
        renderProcesses.add(modelMeshProc);

        renderProcesses.add(labelsProc);
        renderProcesses.add(lineProc);
        renderProcesses.add(lineGpuProc);
        renderProcesses.add(billboardSSOProc);

        renderProcesses.add(modelStarsProc);
        renderProcesses.add(modelAtmProc);
        renderProcesses.add(modelCloudProc);
        renderProcesses.add(shapeProc);
        renderProcesses.add(particleEffectsProc);
        // renderProcesses.add(cloudsProc);
        // renderProcesses.add(modelCloseUpProc);

        // Use Direct3D [0..1] depth range instead of OpenGL default's [-1..1]
        //ARBClipControl.glClipControl(ARBClipControl.GL_LOWER_LEFT, ARBClipControl.GL_ZERO_TO_ONE);
        //Gdx.gl30.glDepthRangef(0, 1);

        EventManager.instance.subscribe(this, Events.TOGGLE_VISIBILITY_CMD, Events.PIXEL_RENDERER_UPDATE, Events.LINE_RENDERER_UPDATE, Events.STEREOSCOPIC_CMD, Events.CAMERA_MODE_CMD, Events.CUBEMAP360_CMD, Events.REBUILD_SHADOW_MAP_DATA_CMD, Events.LIGHT_SCATTERING_CMD);

    }

    private void initSGR(ICamera camera) {
        if (camera.getNCameras() > 1) {
            // FOV mode
            sgr = sgrs[SGR_FOV_IDX];
        } else if (GlobalConf.program.STEREOSCOPIC_MODE) {
            // Stereoscopic mode
            sgr = sgrs[SGR_STEREO_IDX];
        } else if (GlobalConf.program.CUBEMAP360_MODE) {
            // 360 mode: cube map -> equirectangular map
            sgr = sgrs[SGR_CUBEMAP_IDX];
        } else {
            // Default mode
            sgr = sgrs[SGR_DEFAULT_IDX];
        }
    }

    public void renderGlowPass(ICamera camera) {
        if (GlobalConf.postprocess.POSTPROCESS_LIGHT_SCATTERING && glowFb != null) {
            // Get all billboard stars
            Array<IRenderable> bbstars = render_lists.get(RenderGroup.BILLBOARD_STAR.ordinal());

            stars.clear();
            for (IRenderable st : bbstars) {
                if (st instanceof Star) {
                    stars.add(st);
                    break;
                }
            }

            // Get all models
            Array<IRenderable> models = render_lists.get(RenderGroup.MODEL_NORMAL.ordinal());

            glowFb.begin();
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            if (!GlobalConf.program.CUBEMAP360_MODE) {
                // Render billboard stars
                billboardStarsProc.renderStud(stars, camera, 0);

                // Render models
                modelBatchOpaque.begin(camera.getCamera());
                for (IRenderable model : models) {
                    if (model instanceof ModelBody) {
                        ModelBody mb = (ModelBody) model;
                        mb.renderOpaque(modelBatchOpaque, 1, 0);
                    }
                }
                modelBatchOpaque.end();
            }

            // Save to texture for later use
            glowTex = glowFb.getColorBufferTexture();

            glowFb.end();

        }

    }

    private void renderShadowMap(ICamera camera) {
        if (GlobalConf.scene.SHADOW_MAPPING) {
            /**
             * Shadow mapping here?
             * <ul>
             * <li>Extract model bodies (front)</li>
             * <li>Work out light direction</li>
             * <li>Set orthographic camera at set distance from bodies,
             * direction of light, clip planes</li>
             * <li>Render depth map to frame buffer (fb)</li>
             * <li>Send frame buffer texture in to ModelBatchRenderSystem along
             * with light position, direction, clip planes and light camera
             * combined matrix</li>
             * <li>Compare real distance from light to texture sample, render
             * shadow if different</li>
             * </ul>
             */
            Array<IRenderable> models = render_lists.get(RenderGroup.MODEL_NORMAL.ordinal());
            models.sort((a, b) -> {
                return Double.compare(((AbstractPositionEntity) a).getDistToCamera(), ((AbstractPositionEntity) b).getDistToCamera());
            });

            int shadowNRender = GlobalConf.program.STEREOSCOPIC_MODE ? 2 : GlobalConf.program.CUBEMAP360_MODE ? 6 : 1;

            if (candidates != null && shadowMapFb != null && smCombinedMap != null) {
                candidates.clear();
                int num = 0;
                for (int i = 0; i < models.size; i++) {
                    if (models.get(i) instanceof ModelBody) {
                        ModelBody mr = (ModelBody) models.get(i);
                        if (mr.isShadow()) {
                            candidates.insert(num, mr);
                            mr.shadow = 0;
                            num++;
                            if (num == GlobalConf.scene.SHADOW_MAPPING_N_SHADOWS)
                                break;
                        }
                    }
                }

                // Clear maps
                smTexMap.clear();
                smCombinedMap.clear();
                int i = 0;
                for (ModelBody candidate : candidates) {
                    // Yes!
                    candidate.shadow = shadowNRender;

                    Vector3 camDir = aux1.set(candidate.mc.dlight.direction);
                    // Direction is that of the light
                    cameraLight.direction.set(camDir);

                    double radius = candidate.getRadius();
                    // Distance from camera to object, radius * sv[0]
                    double distance = radius * candidate.shadowMapValues[0];
                    // Position, factor of radius
                    candidate.getAbsolutePosition(aux1d);
                    aux1d.sub(camera.getPos()).sub(camDir.nor().scl((float) distance));
                    aux1d.put(cameraLight.position);
                    // Up is perpendicular to dir
                    if (cameraLight.direction.y != 0 || cameraLight.direction.z != 0)
                        aux1.set(1, 0, 0);
                    else
                        aux1.set(0, 1, 0);
                    cameraLight.up.set(cameraLight.direction).crs(aux1);

                    // Near is sv[1]*radius before the object
                    cameraLight.near = (float) (distance - radius * candidate.shadowMapValues[1]);
                    // Far is sv[2]*radius after the object
                    cameraLight.far = (float) (distance + radius * candidate.shadowMapValues[2]);

                    // Update cam
                    cameraLight.update(false);

                    // Render model depth map to frame buffer
                    shadowMapFb[i].begin();
                    Gdx.gl.glClearColor(0, 0, 0, 0);
                    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
                    modelBatchDepth.begin(cameraLight);
                    candidate.render(modelBatchDepth, 1, 0);
                    modelBatchDepth.end();

                    // Save frame buffer and combined matrix
                    candidate.shadow = shadowNRender;
                    shadowMapCombined[i].set(cameraLight.combined);
                    smCombinedMap.put(candidate, shadowMapCombined[i]);
                    smTexMap.put(candidate, shadowMapFb[i].getColorBufferTexture());

                    shadowMapFb[i].end();
                    i++;
                }
            }
        }
    }

    private void renderMWPrePass(ICamera camera) {
        if (mwrs != null) {
            Array<IRenderable> arr = render_lists.get(RenderGroup.GALAXY.ordinal());
            if (arr != null && arr.size > 0)
                mwrs.renderPrePasses((MilkyWay) arr.get(0), camera);
        }
    }

    @Override
    public void render(ICamera camera, double t, int rw, int rh, FrameBuffer fb, PostProcessBean ppb) {
        if (sgr == null)
            initSGR(camera);

        // Shadow maps are the same for all
        renderShadowMap(camera);

        // In stereo and cubemap modes, the glow pass is rendered in the SGR itself
        if (!GlobalConf.program.STEREOSCOPIC_MODE && !GlobalConf.program.CUBEMAP360_MODE)
            renderGlowPass(camera);

        renderMWPrePass(camera);

        sgr.render(this, camera, t, rw, rh, fb, ppb);

        if (mwrs != null && MWModelRenderSystem.oit) {
            spriteBatch.begin();
            spriteBatch.draw(mwrs.oitFb.getTextureAttachments().get(0), 0, 0, 756, 504);
            spriteBatch.end();
        }

    }

    /**
     * Renders the scene
     * 
     * @param camera
     *            The camera to use
     * @param t
     *            The time in seconds since the start
     * @param rc
     *            The render context
     */
    public void renderScene(ICamera camera, double t, RenderingContext rc) {
        // Update time difference since last update
        for (ComponentType ct : ComponentType.values()) {
            alphas[ct.ordinal()] = calculateAlpha(ct, t);
        }

        int size = renderProcesses.size;
        for (int i = 0; i < size; i++) {
            IRenderSystem process = renderProcesses.get(i);
            // If we have no render group, this means all the info is already in
            // the render system. No lists needed
            if (process.getRenderGroup() != null) {
                Array<IRenderable> l = render_lists.get(process.getRenderGroup().ordinal());
                process.render(l, camera, t, rc);
            } else {
                process.render(null, camera, t, rc);
            }
        }

    }

    /**
     * Renders all the systems which are the same type of the given class
     * 
     * @param camera
     *            The camera to use
     * @param t
     *            The time in seconds since the start
     * @param rc
     *            The render contex
     * @param clazz
     *            The class
     */
    public void renderSystem(ICamera camera, double t, RenderingContext rc, Class<? extends IRenderSystem> clazz) {
        // Update time difference since last update
        for (ComponentType ct : ComponentType.values()) {
            alphas[ct.ordinal()] = calculateAlpha(ct, t);
        }

        int size = renderProcesses.size;
        for (int i = 0; i < size; i++) {
            IRenderSystem process = renderProcesses.get(i);
            if (clazz.isInstance(process)) {
                // If we have no render group, this means all the info is already in
                // the render system. No lists needed
                if (process.getRenderGroup() != null) {
                    Array<IRenderable> l = render_lists.get(process.getRenderGroup().ordinal());
                    process.render(l, camera, t, rc);
                } else {
                    process.render(null, camera, t, rc);
                }
            }
        }
    }

    /**
     * This must be called when all the rendering for the current frame has
     * finished.
     */
    public void clearLists() {
        for (RenderGroup rg : RenderGroup.values()) {
            render_lists.get(rg.ordinal()).clear();
        }
    }

    public String[] getRenderComponents() {
        ComponentType[] comps = ComponentType.values();
        String[] res = new String[comps.length];
        int i = 0;
        for (ComponentType comp : comps) {
            res[i++] = comp.getName();
        }
        return res;
    }

    /**
     * Checks if a given component type is on
     * 
     * @param comp
     *            The component
     * @return Whether the component is on
     */
    public boolean isOn(ComponentType comp) {
        return visible.get(comp.ordinal()) || alphas[comp.ordinal()] > 0;
    }

    /**
     * Checks if the component types are all on
     * 
     * @param comp
     *            The components
     * @return Whether the components are all on
     */
    public boolean isOn(ComponentTypes comp) {
        boolean allon = comp.allSetLike(visible);

        if (!allon) {
            allon = true;
            for (int i = comp.nextSetBit(0); i >= 0; i = comp.nextSetBit(i + 1)) {
                // operate on index i here
                allon = allon && alphas[i] > 0;
                if (i == Integer.MAX_VALUE) {
                    break; // or (i+1) would overflow
                }
            }
        }
        return allon;
    }

    public boolean isOn(int ordinal) {
        return visible.get(ordinal) || alphas[ordinal] > 0;
    }

    @Override
    public void notify(Events event, final Object... data) {
        switch (event) {
        case TOGGLE_VISIBILITY_CMD:
            ComponentType ct = ComponentType.getFromKey((String) data[0]);
            int idx = ct.ordinal();
            if (data.length == 3) {
                // We have the boolean
                boolean currvis = visible.get(ct.ordinal());
                boolean newvis = (boolean) data[2];
                if (currvis != newvis) {
                    // Only update if visibility different
                    if (newvis)
                        visible.set(ct.ordinal());
                    else
                        visible.clear(ct.ordinal());
                    times[idx] = (long) (GaiaSky.instance.getT() * 1000f);
                }
            } else {
                // Only toggle
                visible.flip(ct.ordinal());
                times[idx] = (long) (GaiaSky.instance.getT() * 1000f);
            }
            break;

        case PIXEL_RENDERER_UPDATE:
            Gdx.app.postRunnable(new Runnable() {

                @Override
                public void run() {
                    AbstractRenderSystem.POINT_UPDATE_FLAG = true;
                    // updatePixelRenderSystem();
                }

            });
            break;
        case LINE_RENDERER_UPDATE:
            Gdx.app.postRunnable(new Runnable() {

                @Override
                public void run() {
                    updateLineRenderSystem();
                }

            });
            break;
        case STEREOSCOPIC_CMD:
            boolean stereo = (Boolean) data[0];
            if (stereo)
                sgr = sgrs[SGR_STEREO_IDX];
            else {
                sgr = sgrs[SGR_DEFAULT_IDX];
            }
            break;
        case CUBEMAP360_CMD:
            boolean cubemap = (Boolean) data[0];
            if (cubemap)
                sgr = sgrs[SGR_CUBEMAP_IDX];
            else
                sgr = sgrs[SGR_DEFAULT_IDX];
            break;
        case CAMERA_MODE_CMD:
            CameraMode cm = (CameraMode) data[0];
            if (cm.isGaiaFov())
                sgr = sgrs[SGR_FOV_IDX];
            else {
                if (GlobalConf.program.STEREOSCOPIC_MODE)
                    sgr = sgrs[SGR_STEREO_IDX];
                else if (GlobalConf.program.CUBEMAP360_MODE)
                    sgr = sgrs[SGR_CUBEMAP_IDX];
                else
                    sgr = sgrs[SGR_DEFAULT_IDX];

            }
            break;
        case REBUILD_SHADOW_MAP_DATA_CMD:
            buildShadowMapData();
            break;
        case LIGHT_SCATTERING_CMD:
            boolean glow = (Boolean) data[0];
            if (glow) {
                buildGlowData();
            }
            break;
        default:
            break;
        }
    }

    /**
     * Computes the alpha for the given component type.
     * 
     * @param type
     *            The component type.
     * @param now
     *            The current time in seconds.
     * @return The alpha value.
     */
    private float calculateAlpha(ComponentType type, double t) {
        int ordinal = type.ordinal();
        long diff = (long) (t * 1000f) - times[ordinal];
        if (diff > GlobalConf.scene.OBJECT_FADE_MS) {
            if (visible.get(ordinal)) {
                alphas[ordinal] = 1;
            } else {
                alphas[ordinal] = 0;
            }
            return alphas[ordinal];
        } else {
            return visible.get(ordinal) ? MathUtilsd.lint(diff, 0, GlobalConf.scene.OBJECT_FADE_MS, 0, 1) : MathUtilsd.lint(diff, 0, GlobalConf.scene.OBJECT_FADE_MS, 1, 0);
        }
    }

    public void resize(final int w, final int h) {
        resize(w, h, false);
    }

    public void resize(final int w, final int h, boolean resizeRenderSys) {
        if (resizeRenderSys)
            resizeRenderSystems(w, h);

        for (ISGR sgr : sgrs) {
            sgr.resize(w, h);
        }
    }

    public void resizeRenderSystems(final int w, final int h) {
        for (IRenderSystem rendSys : renderProcesses) {
            rendSys.resize(w, h);
        }
    }

    public void dispose() {
        if (sgrs != null)
            for (ISGR sgr : sgrs) {
                if (sgr != null)
                    sgr.dispose();
            }
    }

    /**
     * Builds the shadow map data; frame buffers, arrays, etc.
     */
    private void buildShadowMapData() {
        if (shadowMapFb != null) {
            for (FrameBuffer fb : shadowMapFb)
                fb.dispose();
            shadowMapFb = null;
        }
        shadowMapCombined = null;

        // Shadow map frame buffer
        shadowMapFb = new FrameBuffer[GlobalConf.scene.SHADOW_MAPPING_N_SHADOWS];
        // Shadow map combined matrices
        shadowMapCombined = new Matrix4[GlobalConf.scene.SHADOW_MAPPING_N_SHADOWS];
        // Init
        for (int i = 0; i < GlobalConf.scene.SHADOW_MAPPING_N_SHADOWS; i++) {
            shadowMapFb[i] = new FrameBuffer(Format.RGBA8888, GlobalConf.scene.SHADOW_MAPPING_RESOLUTION, GlobalConf.scene.SHADOW_MAPPING_RESOLUTION, true);
            shadowMapCombined[i] = new Matrix4();
        }
        if (smTexMap == null)
            smTexMap = new HashMap<ModelBody, Texture>();
        smTexMap.clear();

        if (smCombinedMap == null)
            smCombinedMap = new HashMap<ModelBody, Matrix4>();
        smCombinedMap.clear();

        if (candidates == null)
            candidates = new Array<ModelBody>(GlobalConf.scene.SHADOW_MAPPING_N_SHADOWS);
        candidates.clear();
    }

    private void buildGlowData() {
        if (glowFb == null)
            glowFb = new FrameBuffer(Format.RGBA8888, 1080, 720, false);
    }

    public void updateLineRenderSystem() {
        LineRenderSystem current = null;
        for (IRenderSystem proc : renderProcesses) {
            if (proc instanceof LineRenderSystem) {
                current = (LineRenderSystem) proc;
            }
        }
        final int idx = renderProcesses.indexOf(current, true);
        if ((current instanceof LineQuadRenderSystem && GlobalConf.scene.isNormalLineRenderer()) || (!(current instanceof LineQuadRenderSystem) && !GlobalConf.scene.isNormalLineRenderer())) {
            renderProcesses.removeIndex(idx);
            AbstractRenderSystem lineSys = getLineRenderSystem();
            lineSys.setPreRunnable(blendDepthRunnable);
            renderProcesses.insert(idx, lineSys);
        }
    }

    private AbstractRenderSystem getLineRenderSystem() {
        AbstractRenderSystem sys = null;
        if (GlobalConf.scene.isNormalLineRenderer()) {
            // Normal
            sys = new LineRenderSystem(RenderGroup.LINE, alphas, lineShaders);
            sys.setPreRunnable(blendDepthRunnable);
        } else {
            // Quad
            sys = new LineQuadRenderSystem(RenderGroup.LINE, alphas, lineQuadShaders);
            sys.setPreRunnable(blendDepthRunnable);
        }
        return sys;
    }

}
