package gaia.cu9.ari.gaiaorbit.interfce;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import gaia.cu9.ari.gaiaorbit.GaiaSky;
import gaia.cu9.ari.gaiaorbit.event.EventManager;
import gaia.cu9.ari.gaiaorbit.event.Events;
import gaia.cu9.ari.gaiaorbit.interfce.KeyBindings.ProgramAction;
import gaia.cu9.ari.gaiaorbit.interfce.beans.ComboBoxBean;
import gaia.cu9.ari.gaiaorbit.interfce.beans.FileComboBoxBean;
import gaia.cu9.ari.gaiaorbit.interfce.beans.LangComboBoxBean;
import gaia.cu9.ari.gaiaorbit.scenegraph.camera.CameraManager;
import gaia.cu9.ari.gaiaorbit.util.ConfInit;
import gaia.cu9.ari.gaiaorbit.util.Constants;
import gaia.cu9.ari.gaiaorbit.util.GlobalConf;
import gaia.cu9.ari.gaiaorbit.util.GlobalConf.PostprocessConf.Antialias;
import gaia.cu9.ari.gaiaorbit.util.GlobalConf.ScreenshotMode;
import gaia.cu9.ari.gaiaorbit.util.GlobalResources;
import gaia.cu9.ari.gaiaorbit.util.I18n;
import gaia.cu9.ari.gaiaorbit.util.Logger;
import gaia.cu9.ari.gaiaorbit.util.SysUtilsFactory;
import gaia.cu9.ari.gaiaorbit.util.format.INumberFormat;
import gaia.cu9.ari.gaiaorbit.util.format.NumberFormatFactory;
import gaia.cu9.ari.gaiaorbit.util.math.MathUtilsd;
import gaia.cu9.ari.gaiaorbit.util.scene2d.FileChooser;
import gaia.cu9.ari.gaiaorbit.util.scene2d.FileChooser.ResultListener;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnCheckBox;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnImageButton;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnLabel;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnScrollPane;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnSelectBox;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnSlider;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnTextArea;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnTextButton;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnTextField;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnTextIconButton;
import gaia.cu9.ari.gaiaorbit.util.validator.IValidator;
import gaia.cu9.ari.gaiaorbit.util.validator.IntValidator;
import gaia.cu9.ari.gaiaorbit.util.validator.RegexpValidator;

/**
 * The default preferences window.
 * 
 * @author tsagrista
 *
 */
public class PreferencesWindow extends GenericDialog {

    private Array<Actor> contents;
    private Array<OwnLabel> labels;

    private IValidator widthValidator, heightValidator, screenshotsSizeValidator, frameoutputSizeValidator, limitfpsValidator;

    private INumberFormat nf3, nf1;

    private CheckBox fullscreen, windowed, vsync, limitfpsCb, multithreadCb, lodFadeCb, cbAutoCamrec, real, nsl, report, inverty, highAccuracyPositions, shadowsCb, pointerCoords, datasetChooser;
    private OwnSelectBox<DisplayMode> fullscreenResolutions;
    private OwnSelectBox<ComboBoxBean> gquality, aa, orbitRenderer, lineRenderer, numThreads, screenshotMode, frameoutputMode, nshadows;
    private OwnSelectBox<LangComboBoxBean> lang;
    private OwnSelectBox<String> theme;
    private OwnSelectBox<FileComboBoxBean> controllerMappings;
    private OwnTextField widthField, heightField, sswidthField, ssheightField, frameoutputPrefix, frameoutputFps, fowidthField, foheightField, camrecFps, cmResolution, smResolution, limitFps;
    private OwnSlider lodTransitions;
    private OwnTextButton screenshotsLocation, frameoutputLocation;
    private OwnTextButton[] catalogs;
    private Map<Button, String> candidates;

    // Backup values
    private float brightnessBak, contrastBak, hueBak, saturationBak, gammaBak, motionblurBak, bloomBak;
    private boolean lensflareBak, lightglowBak;

    public PreferencesWindow(Stage stage, Skin skin) {
        super(txt("gui.settings") + " - v" + GlobalConf.version.version + " - " + txt("gui.build", GlobalConf.version.build), skin, stage);

        this.contents = new Array<Actor>();
        this.labels = new Array<OwnLabel>();

        this.nf1 = NumberFormatFactory.getFormatter("0.0");
        this.nf3 = NumberFormatFactory.getFormatter("0.000");

        setAcceptText(txt("gui.saveprefs"));
        setCancelText(txt("gui.cancel"));

        // Build UI
        buildSuper();
    }

    @Override
    protected void build() {
        float contentw = 700 * GlobalConf.SCALE_FACTOR;
        float contenth = 700 * GlobalConf.SCALE_FACTOR;
        final float tawidth = 400 * GlobalConf.SCALE_FACTOR;
        float tabwidth = 180 * GlobalConf.SCALE_FACTOR;
        float textwidth = 65 * GlobalConf.SCALE_FACTOR;
        float scrollh = 330 * GlobalConf.SCALE_FACTOR;
        float controlsscrollw = 410 * GlobalConf.SCALE_FACTOR;
        float controllsscrollh = 250 * GlobalConf.SCALE_FACTOR;
        float sliderWidth = textwidth * 3;

        // Create the tab buttons
        VerticalGroup group = new VerticalGroup();
        group.align(Align.left | Align.top);

        final Button tabGraphics = new OwnTextIconButton(txt("gui.graphicssettings"), new Image(skin.getDrawable("icon-p-graphics")), skin, "toggle-big");
        tabGraphics.pad(pad);
        tabGraphics.setWidth(tabwidth);
        final Button tabUI = new OwnTextIconButton(txt("gui.ui.interfacesettings"), new Image(skin.getDrawable("icon-p-interface")), skin, "toggle-big");
        tabUI.pad(pad);
        tabUI.setWidth(tabwidth);
        final Button tabPerformance = new OwnTextIconButton(txt("gui.performance"), new Image(skin.getDrawable("icon-p-performance")), skin, "toggle-big");
        tabPerformance.pad(pad);
        tabPerformance.setWidth(tabwidth);
        final Button tabControls = new OwnTextIconButton(txt("gui.controls"), new Image(skin.getDrawable("icon-p-controls")), skin, "toggle-big");
        tabControls.pad(pad);
        tabControls.setWidth(tabwidth);
        final Button tabScreenshots = new OwnTextIconButton(txt("gui.screenshots"), new Image(skin.getDrawable("icon-p-screenshots")), skin, "toggle-big");
        tabScreenshots.pad(pad);
        tabScreenshots.setWidth(tabwidth);
        final Button tabFrames = new OwnTextIconButton(txt("gui.frameoutput.title"), new Image(skin.getDrawable("icon-p-frameoutput")), skin, "toggle-big");
        tabFrames.pad(pad);
        tabFrames.setWidth(tabwidth);
        final Button tabCamera = new OwnTextIconButton(txt("gui.camerarec.title"), new Image(skin.getDrawable("icon-p-camera")), skin, "toggle-big");
        tabCamera.pad(pad);
        tabCamera.setWidth(tabwidth);
        final Button tab360 = new OwnTextIconButton(txt("gui.360.title"), new Image(skin.getDrawable("icon-p-360")), skin, "toggle-big");
        tab360.pad(pad);
        tab360.setWidth(tabwidth);
        final Button tabData = new OwnTextIconButton(txt("gui.data"), new Image(skin.getDrawable("icon-p-data")), skin, "toggle-big");
        tabData.pad(pad);
        tabData.setWidth(tabwidth);
        final Button tabGaia = new OwnTextIconButton(txt("gui.gaia"), new Image(skin.getDrawable("icon-p-gaia")), skin, "toggle-big");
        tabGaia.pad(pad);
        tabGaia.setWidth(tabwidth);
        final Button tabSystem = new OwnTextIconButton(txt("gui.system"), new Image(skin.getDrawable("icon-p-system")), skin, "toggle-big");
        tabSystem.pad(pad);
        tabSystem.setWidth(tabwidth);

        group.addActor(tabGraphics);
        group.addActor(tabUI);
        group.addActor(tabPerformance);
        group.addActor(tabControls);
        group.addActor(tabScreenshots);
        group.addActor(tabFrames);
        group.addActor(tabCamera);
        group.addActor(tab360);
        group.addActor(tabData);
        group.addActor(tabGaia);
        group.addActor(tabSystem);
        content.add(group).align(Align.left | Align.top).padLeft(pad);

        // Create the tab content. Just using images here for simplicity.
        Stack tabContent = new Stack();
        tabContent.setSize(contentw, contenth);

        /**
         * ==== GRAPHICS ====
         **/
        final Table contentGraphicsTable = new Table(skin);
        final OwnScrollPane contentGraphics = new OwnScrollPane(contentGraphicsTable, skin, "minimalist-nobg");
        contentGraphics.setHeight(scrollh);
        contentGraphics.setScrollingDisabled(true, false);
        contentGraphics.setFadeScrollBars(false);
        contents.add(contentGraphics);
        contentGraphicsTable.align(Align.top | Align.left);

        // RESOLUTION/MODE
        Label titleResolution = new OwnLabel(txt("gui.resolutionmode"), skin, "help-title");
        Table mode = new Table();

        // Full screen mode resolutions
        Array<DisplayMode> modes = new Array<DisplayMode>(Gdx.graphics.getDisplayModes());
        modes.sort(new Comparator<DisplayMode>() {

            @Override
            public int compare(DisplayMode o1, DisplayMode o2) {
                return Integer.compare(o2.height * o2.width, o1.height * o1.width);
            }
        });
        fullscreenResolutions = new OwnSelectBox<DisplayMode>(skin);
        fullscreenResolutions.setWidth(textwidth * 3.3f);
        fullscreenResolutions.setItems(modes);

        DisplayMode selectedMode = null;
        for (DisplayMode dm : modes) {
            if (dm.width == GlobalConf.screen.FULLSCREEN_WIDTH && dm.height == GlobalConf.screen.FULLSCREEN_HEIGHT) {
                selectedMode = dm;
                break;
            }
        }
        if (selectedMode != null)
            fullscreenResolutions.setSelected(selectedMode);

        // Get current resolution
        Table windowedResolutions = new Table(skin);
        DisplayMode nativeMode = Gdx.graphics.getDisplayMode();
        widthValidator = new IntValidator(100, nativeMode.width);
        widthField = new OwnTextField(Integer.toString(MathUtils.clamp(GlobalConf.screen.SCREEN_WIDTH, 100, nativeMode.width)), skin, widthValidator);
        widthField.setWidth(textwidth);
        heightValidator = new IntValidator(100, nativeMode.height);
        heightField = new OwnTextField(Integer.toString(MathUtils.clamp(GlobalConf.screen.SCREEN_HEIGHT, 100, nativeMode.height)), skin, heightValidator);
        heightField.setWidth(textwidth);
        final OwnLabel widthLabel = new OwnLabel(txt("gui.width") + ":", skin);
        final OwnLabel heightLabel = new OwnLabel(txt("gui.height") + ":", skin);

        windowedResolutions.add(widthLabel).left().padRight(pad);
        windowedResolutions.add(widthField).left().padRight(pad);
        windowedResolutions.add(heightLabel).left().padRight(pad);
        windowedResolutions.add(heightField).left().row();

        // Radio buttons
        fullscreen = new OwnCheckBox(txt("gui.fullscreen"), skin, "radio", pad);
        fullscreen.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeEvent) {
                    selectFullscreen(fullscreen.isChecked(), widthField, heightField, fullscreenResolutions, widthLabel, heightLabel);
                    return true;
                }
                return false;
            }
        });
        fullscreen.setChecked(GlobalConf.screen.FULLSCREEN);

        windowed = new OwnCheckBox(txt("gui.windowed"), skin, "radio", pad);
        windowed.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeEvent) {
                    selectFullscreen(!windowed.isChecked(), widthField, heightField, fullscreenResolutions, widthLabel, heightLabel);
                    return true;
                }
                return false;
            }
        });
        windowed.setChecked(!GlobalConf.screen.FULLSCREEN);
        selectFullscreen(GlobalConf.screen.FULLSCREEN, widthField, heightField, fullscreenResolutions, widthLabel, heightLabel);

        new ButtonGroup<CheckBox>(fullscreen, windowed);

        // VSYNC
        vsync = new OwnCheckBox(txt("gui.vsync"), skin, "default", pad);
        vsync.setChecked(GlobalConf.screen.VSYNC);

        // LIMIT FPS
        limitfpsValidator = new IntValidator(1, 1000);
        limitFps = new OwnTextField(Integer.toString(MathUtils.clamp(GlobalConf.screen.LIMIT_FPS, 1, 1000)), skin, limitfpsValidator);
        limitFps.setDisabled(GlobalConf.screen.LIMIT_FPS == 0);

        limitfpsCb = new OwnCheckBox(txt("gui.limitfps"), skin, "default", pad);
        limitfpsCb.setChecked(GlobalConf.screen.LIMIT_FPS > 0);
        limitfpsCb.addListener((event) -> {
            if (event instanceof ChangeEvent) {
                enableComponents(limitfpsCb.isChecked(), limitFps);
                return true;
            }
            return false;
        });

        mode.add(fullscreen).left().padRight(pad * 2);
        mode.add(fullscreenResolutions).left().row();
        mode.add(windowed).left().padRight(pad * 2).padTop(pad * 2);
        mode.add(windowedResolutions).left().padTop(pad * 2).row();
        mode.add(vsync).left().padTop(pad * 2).colspan(2).row();
        mode.add(limitfpsCb).left().padRight(pad * 2);
        mode.add(limitFps).left();

        // Add to content
        contentGraphicsTable.add(titleResolution).left().padBottom(pad * 2).row();
        contentGraphicsTable.add(mode).left().padBottom(pad * 4).row();

        // GRAPHICS SETTINGS
        Label titleGraphics = new OwnLabel(txt("gui.graphicssettings"), skin, "help-title");
        Table graphics = new Table();

        OwnLabel gqualityLabel = new OwnLabel(txt("gui.gquality"), skin);
        gqualityLabel.addListener(new TextTooltip(txt("gui.gquality.info"), skin));

        ComboBoxBean[] gqs = new ComboBoxBean[] { new ComboBoxBean(txt("gui.gquality.high"), 0), new ComboBoxBean(txt("gui.gquality.normal"), 1), new ComboBoxBean(txt("gui.gquality.low"), 2) };
        gquality = new OwnSelectBox<ComboBoxBean>(skin);
        gquality.setItems(gqs);
        gquality.setWidth(textwidth * 3f);
        gquality.setSelected(gqs[GlobalConf.scene.GRAPHICS_QUALITY]);

        OwnImageButton gqualityTooltip = new OwnImageButton(skin, "tooltip");
        gqualityTooltip.addListener(new TextTooltip(txt("gui.gquality.info"), skin));

        // AA
        OwnLabel aaLabel = new OwnLabel(txt("gui.aa"), skin);
        aaLabel.addListener(new TextTooltip(txt("gui.aa.info"), skin));

        ComboBoxBean[] aas = new ComboBoxBean[] { new ComboBoxBean(txt("gui.aa.no"), 0), new ComboBoxBean(txt("gui.aa.fxaa"), -1), new ComboBoxBean(txt("gui.aa.nfaa"), -2) };
        aa = new OwnSelectBox<ComboBoxBean>(skin);
        aa.setItems(aas);
        aa.setWidth(textwidth * 3f);
        aa.setSelected(aas[idxAa(2, GlobalConf.postprocess.POSTPROCESS_ANTIALIAS)]);

        OwnImageButton aaTooltip = new OwnImageButton(skin, "tooltip");
        aaTooltip.addListener(new TextTooltip(txt("gui.aa.info"), skin));

        // ORBITS
        OwnLabel orbitsLabel = new OwnLabel(txt("gui.orbitrenderer"), skin);
        ComboBoxBean[] orbitItems = new ComboBoxBean[] { new ComboBoxBean(txt("gui.orbitrenderer.line"), 0), new ComboBoxBean(txt("gui.orbitrenderer.gpu"), 1) };
        orbitRenderer = new OwnSelectBox<ComboBoxBean>(skin);
        orbitRenderer.setItems(orbitItems);
        orbitRenderer.setWidth(textwidth * 3f);
        orbitRenderer.setSelected(orbitItems[GlobalConf.scene.ORBIT_RENDERER]);

        // LINE RENDERER
        OwnLabel lrLabel = new OwnLabel(txt("gui.linerenderer"), skin);
        ComboBoxBean[] lineRenderers = new ComboBoxBean[] { new ComboBoxBean(txt("gui.linerenderer.normal"), 0), new ComboBoxBean(txt("gui.linerenderer.quad"), 1) };
        lineRenderer = new OwnSelectBox<ComboBoxBean>(skin);
        lineRenderer.setItems(lineRenderers);
        lineRenderer.setWidth(textwidth * 3f);
        lineRenderer.setSelected(lineRenderers[GlobalConf.scene.LINE_RENDERER]);

        // BLOOM
        bloomBak = GlobalConf.postprocess.POSTPROCESS_BLOOM_INTENSITY;
        OwnLabel bloomLabel = new OwnLabel(txt("gui.bloom"), skin, "default");
        OwnLabel bloom = new OwnLabel(Integer.toString((int) (GlobalConf.postprocess.POSTPROCESS_BLOOM_INTENSITY * 10)), skin);
        Slider bloomEffect = new OwnSlider(Constants.MIN_SLIDER, Constants.MAX_SLIDER, 1, false, skin);
        bloomEffect.setName("bloom effect");
        bloomEffect.setWidth(sliderWidth);
        bloomEffect.setValue(GlobalConf.postprocess.POSTPROCESS_BLOOM_INTENSITY * 10f);
        bloomEffect.addListener(event -> {
            if (event instanceof ChangeEvent) {
                EventManager.instance.post(Events.BLOOM_CMD, bloomEffect.getValue() / 10f, true);
                bloom.setText(Integer.toString((int) bloomEffect.getValue()));
                return true;
            }
            return false;
        });

        // LABELS
        labels.addAll(gqualityLabel, aaLabel, orbitsLabel, lrLabel, bloomLabel);

        // LENS FLARE
        lensflareBak = GlobalConf.postprocess.POSTPROCESS_LENS_FLARE;
        CheckBox lensFlare = new CheckBox(" " + txt("gui.lensflare"), skin);
        lensFlare.setName("lens flare");
        lensFlare.addListener(event -> {
            if (event instanceof ChangeEvent) {
                EventManager.instance.post(Events.LENS_FLARE_CMD, lensFlare.isChecked(), true);
                return true;
            }
            return false;
        });
        lensFlare.setChecked(GlobalConf.postprocess.POSTPROCESS_LENS_FLARE);

        // LIGHT GLOW
        lightglowBak = GlobalConf.postprocess.POSTPROCESS_LIGHT_SCATTERING;
        CheckBox lightGlow = new CheckBox(" " + txt("gui.lightscattering"), skin);
        lightGlow.setName("light scattering");
        lightGlow.setChecked(GlobalConf.postprocess.POSTPROCESS_LIGHT_SCATTERING);
        lightGlow.addListener(event -> {
            if (event instanceof ChangeEvent) {
                EventManager.instance.post(Events.LIGHT_SCATTERING_CMD, lightGlow.isChecked(), true);
                return true;
            }
            return false;
        });

        // MOTION BLUR
        motionblurBak = GlobalConf.postprocess.POSTPROCESS_MOTION_BLUR;
        CheckBox motionBlur = new CheckBox(" " + txt("gui.motionblur"), skin);
        motionBlur.setName("motion blur");
        motionBlur.setChecked(GlobalConf.postprocess.POSTPROCESS_MOTION_BLUR != 0);
        motionBlur.addListener(event -> {
            if (event instanceof ChangeEvent) {
                EventManager.instance.post(Events.MOTION_BLUR_CMD, motionBlur.isChecked() ? Constants.MOTION_BLUR_VALUE : 0.0f, true);
                return true;
            }
            return false;
        });

        graphics.add(gqualityLabel).left().padRight(pad * 4).padBottom(pad);
        graphics.add(gquality).left().padRight(pad * 2).padBottom(pad);
        graphics.add(gqualityTooltip).left().padBottom(pad).row();
        final Cell<Actor> noticeGraphicsCell = graphics.add();
        noticeGraphicsCell.colspan(3).left().row();
        graphics.add(aaLabel).left().padRight(pad * 4).padBottom(pad);
        graphics.add(aa).left().padRight(pad * 2).padBottom(pad);
        graphics.add(aaTooltip).left().padBottom(pad).row();
        graphics.add(orbitsLabel).left().padRight(pad * 4).padBottom(pad);
        graphics.add(orbitRenderer).colspan(2).left().padBottom(pad).row();
        graphics.add(lrLabel).left().padRight(pad * 4).padBottom(pad);
        graphics.add(lineRenderer).colspan(2).left().padBottom(pad).row();
        graphics.add(bloomLabel).left().padRight(pad * 4).padBottom(pad);
        graphics.add(bloomEffect).left().padBottom(pad);
        graphics.add(bloom).left().padBottom(pad).row();
        graphics.add(lensFlare).colspan(3).left().padBottom(pad).row();
        graphics.add(lightGlow).colspan(3).left().padBottom(pad).row();
        graphics.add(motionBlur).colspan(3).left().padBottom(pad).row();

        EventListener graphicsChangeListener = new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeEvent) {
                    if (noticeGraphicsCell.getActor() == null) {
                        String nextinfostr = txt("gui.ui.info") + '\n';
                        int lines = GlobalResources.countOccurrences(nextinfostr, '\n');
                        TextArea nextTimeInfo = new OwnTextArea(nextinfostr, skin, "info");
                        nextTimeInfo.setDisabled(true);
                        nextTimeInfo.setPrefRows(lines + 1);
                        nextTimeInfo.setWidth(tawidth);
                        nextTimeInfo.clearListeners();
                        noticeGraphicsCell.setActor(nextTimeInfo);
                    }
                    return true;
                }
                return false;
            }
        };
        //gquality.addListener(graphicsChangeListener);

        // Add to content
        contentGraphicsTable.add(titleGraphics).left().padBottom(pad * 2).row();
        contentGraphicsTable.add(graphics).left().padBottom(pad * 4).row();

        // SHADOWS
        Label titleShadows = new OwnLabel(txt("gui.graphics.shadows"), skin, "help-title");
        Table shadows = new Table();

        // SHADOW MAP RESOLUTION
        OwnLabel smResolutionLabel = new OwnLabel(txt("gui.graphics.shadows.resolution"), skin);
        smResolutionLabel.setDisabled(!GlobalConf.scene.SHADOW_MAPPING);
        IntValidator smResValidator = new IntValidator(128, 2048);
        smResolution = new OwnTextField(Integer.toString(MathUtils.clamp(GlobalConf.scene.SHADOW_MAPPING_RESOLUTION, 128, 2048)), skin, smResValidator);
        smResolution.setWidth(textwidth * 3f);
        smResolution.setDisabled(!GlobalConf.scene.SHADOW_MAPPING);

        // N SHADOWS
        OwnLabel nShadowsLabel = new OwnLabel("#" + txt("gui.graphics.shadows"), skin);
        nShadowsLabel.setDisabled(!GlobalConf.scene.SHADOW_MAPPING);
        ComboBoxBean[] nsh = new ComboBoxBean[] { new ComboBoxBean("1", 1), new ComboBoxBean("2", 2), new ComboBoxBean("3", 3), new ComboBoxBean("4", 4) };
        nshadows = new OwnSelectBox<ComboBoxBean>(skin);
        nshadows.setItems(nsh);
        nshadows.setWidth(textwidth * 3f);
        nshadows.setSelected(nsh[GlobalConf.scene.SHADOW_MAPPING_N_SHADOWS - 1]);
        nshadows.setDisabled(!GlobalConf.scene.SHADOW_MAPPING);

        // ENABLE SHADOWS
        shadowsCb = new OwnCheckBox(txt("gui.graphics.shadows.enable"), skin, "default", pad);
        shadowsCb.setChecked(GlobalConf.scene.SHADOW_MAPPING);
        shadowsCb.addListener((event) -> {
            if (event instanceof ChangeEvent) {
                // Enable or disable resolution
                enableComponents(shadowsCb.isChecked(), smResolution, smResolutionLabel, nshadows, nShadowsLabel);
                return true;
            }
            return false;
        });

        // LABELS
        labels.add(smResolutionLabel);

        shadows.add(shadowsCb).left().padRight(pad * 2).padBottom(pad).row();
        shadows.add(smResolutionLabel).left().padRight(pad * 4).padBottom(pad);
        shadows.add(smResolution).left().padRight(pad * 2).padBottom(pad).row();
        shadows.add(nShadowsLabel).left().padRight(pad * 4).padBottom(pad);
        shadows.add(nshadows).left().padRight(pad * 2).padBottom(pad);

        // Add to content
        contentGraphicsTable.add(titleShadows).left().padBottom(pad * 2).row();
        contentGraphicsTable.add(shadows).left().padBottom(pad * 4).row();

        // DISPLAY SETTINGS
        Label titleDisplay = new OwnLabel(txt("gui.graphics.imglevels"), skin, "help-title");
        Table display = new Table();


        brightnessBak = GlobalConf.postprocess.POSTPROCESS_BRIGHTNESS;
        contrastBak = GlobalConf.postprocess.POSTPROCESS_CONTRAST;
        hueBak = GlobalConf.postprocess.POSTPROCESS_HUE;
        saturationBak = GlobalConf.postprocess.POSTPROCESS_SATURATION;
        gammaBak = GlobalConf.postprocess.POSTPROCESS_GAMMA;

        /** Brightness **/
        OwnLabel brightnessl = new OwnLabel(txt("gui.brightness"), skin, "default");
        Label brightnessLabel = new OwnLabel(Integer.toString((int) MathUtilsd.lint(GlobalConf.postprocess.POSTPROCESS_BRIGHTNESS, Constants.MIN_BRIGHTNESS, Constants.MAX_BRIGHTNESS, Constants.MIN_SLIDER, Constants.MAX_SLIDER)), skin);
        Slider brightness = new OwnSlider(Constants.MIN_SLIDER, Constants.MAX_SLIDER, 1, false, skin);
        brightness.setName("brightness");
        brightness.setWidth(sliderWidth);
        brightness.setValue(MathUtilsd.lint(GlobalConf.postprocess.POSTPROCESS_BRIGHTNESS, Constants.MIN_BRIGHTNESS, Constants.MAX_BRIGHTNESS, Constants.MIN_SLIDER, Constants.MAX_SLIDER));
        brightnessLabel.setText(Integer.toString((int) brightness.getValue()));
        brightness.addListener(event -> {
            if (event instanceof ChangeEvent) {
                EventManager.instance.post(Events.BRIGHTNESS_CMD, MathUtilsd.lint(brightness.getValue(), Constants.MIN_SLIDER, Constants.MAX_SLIDER, Constants.MIN_BRIGHTNESS, Constants.MAX_BRIGHTNESS), true);
                brightnessLabel.setText(Integer.toString((int) brightness.getValue()));
                return true;
            }
            return false;
        });

        display.add(brightnessl).left().padRight(pad * 4).padBottom(pad);
        display.add(brightness).left().padRight(pad * 2).padBottom(pad);
        display.add(brightnessLabel).row();

        /** Contrast **/
        OwnLabel contrastl = new OwnLabel(txt("gui.contrast"), skin, "default");
        Label contrastLabel = new OwnLabel(Integer.toString((int) MathUtilsd.lint(GlobalConf.postprocess.POSTPROCESS_CONTRAST, Constants.MIN_CONTRAST, Constants.MAX_CONTRAST, Constants.MIN_SLIDER, Constants.MAX_SLIDER)), skin);
        Slider contrast = new OwnSlider(Constants.MIN_SLIDER, Constants.MAX_SLIDER, 1, false, skin);
        contrast.setName("contrast");
        contrast.setWidth(sliderWidth);
        contrast.setValue(MathUtilsd.lint(GlobalConf.postprocess.POSTPROCESS_CONTRAST, Constants.MIN_CONTRAST, Constants.MAX_CONTRAST, Constants.MIN_SLIDER, Constants.MAX_SLIDER));
        contrastLabel.setText(Integer.toString((int) contrast.getValue()));
        contrast.addListener(event -> {
            if (event instanceof ChangeEvent) {
                EventManager.instance.post(Events.CONTRAST_CMD, MathUtilsd.lint(contrast.getValue(), Constants.MIN_SLIDER, Constants.MAX_SLIDER, Constants.MIN_CONTRAST, Constants.MAX_CONTRAST), true);
                contrastLabel.setText(Integer.toString((int) contrast.getValue()));
                return true;
            }
            return false;
        });

        display.add(contrastl).left().padRight(pad * 4).padBottom(pad);
        display.add(contrast).left().padRight(pad * 2).padBottom(pad);
        display.add(contrastLabel).row();

        /** Hue **/
        OwnLabel huel = new OwnLabel(txt("gui.hue"), skin, "default");
        Label hueLabel = new OwnLabel(Integer.toString((int) MathUtilsd.lint(GlobalConf.postprocess.POSTPROCESS_HUE, Constants.MIN_HUE, Constants.MAX_HUE, Constants.MIN_SLIDER, Constants.MAX_SLIDER)), skin);
        Slider hue = new OwnSlider(Constants.MIN_SLIDER, Constants.MAX_SLIDER, 1, false, skin);
        hue.setName("hue");
        hue.setWidth(sliderWidth);
        hue.setValue(MathUtilsd.lint(GlobalConf.postprocess.POSTPROCESS_HUE, Constants.MIN_HUE, Constants.MAX_HUE, Constants.MIN_SLIDER, Constants.MAX_SLIDER));
        hueLabel.setText(Integer.toString((int) hue.getValue()));
        hue.addListener(event -> {
            if (event instanceof ChangeEvent) {
                EventManager.instance.post(Events.HUE_CMD, MathUtilsd.lint(hue.getValue(), Constants.MIN_SLIDER, Constants.MAX_SLIDER, Constants.MIN_HUE, Constants.MAX_HUE), true);
                hueLabel.setText(Integer.toString((int) hue.getValue()));
                return true;
            }
            return false;
        });

        display.add(huel).left().padRight(pad * 4).padBottom(pad);
        display.add(hue).left().padRight(pad * 2).padBottom(pad);
        display.add(hueLabel).row();

        /** Saturation **/
        OwnLabel saturationl = new OwnLabel(txt("gui.saturation"), skin, "default");
        Label saturationLabel = new OwnLabel(Integer.toString((int) MathUtilsd.lint(GlobalConf.postprocess.POSTPROCESS_SATURATION, Constants.MIN_SATURATION, Constants.MAX_SATURATION, Constants.MIN_SLIDER, Constants.MAX_SLIDER)), skin);
        Slider saturation = new OwnSlider(Constants.MIN_SLIDER, Constants.MAX_SLIDER, 1, false, skin);
        saturation.setName("saturation");
        saturation.setWidth(sliderWidth);
        saturation.setValue(MathUtilsd.lint(GlobalConf.postprocess.POSTPROCESS_SATURATION, Constants.MIN_SATURATION, Constants.MAX_SATURATION, Constants.MIN_SLIDER, Constants.MAX_SLIDER));
        saturationLabel.setText(Integer.toString((int) saturation.getValue()));
        saturation.addListener(event -> {
            if (event instanceof ChangeEvent) {
                EventManager.instance.post(Events.SATURATION_CMD, MathUtilsd.lint(saturation.getValue(), Constants.MIN_SLIDER, Constants.MAX_SLIDER, Constants.MIN_SATURATION, Constants.MAX_SATURATION), true);
                saturationLabel.setText(Integer.toString((int) saturation.getValue()));
                return true;
            }
            return false;
        });

        display.add(saturationl).left().padRight(pad * 4).padBottom(pad);
        display.add(saturation).left().padRight(pad * 2).padBottom(pad);
        display.add(saturationLabel).row();

        /** Gamma **/
        OwnLabel gammal = new OwnLabel(txt("gui.gamma"), skin, "default");
        Label gammaLabel = new OwnLabel(nf1.format(GlobalConf.postprocess.POSTPROCESS_HUE), skin);
        Slider gamma = new OwnSlider(Constants.MIN_GAMMA, Constants.MAX_GAMMA, 0.1f, false, skin);
        gamma.setName("gamma");
        gamma.setWidth(sliderWidth);
        gamma.setValue(GlobalConf.postprocess.POSTPROCESS_GAMMA);
        gammaLabel.setText(nf1.format(gamma.getValue()));
        gamma.addListener(event -> {
            if (event instanceof ChangeEvent) {
                EventManager.instance.post(Events.GAMMA_CMD, gamma.getValue(), true);
                gammaLabel.setText(nf1.format(gamma.getValue()));
                return true;
            }
            return false;
        });

        display.add(gammal).left().padRight(pad * 4).padBottom(pad);
        display.add(gamma).left().padRight(pad * 2).padBottom(pad);
        display.add(gammaLabel).row();

        // LABELS
        labels.addAll(brightnessl, contrastl, huel, saturationl, gammal);

        // Add to content
        contentGraphicsTable.add(titleDisplay).left().padBottom(pad * 2).row();
        contentGraphicsTable.add(display).left();
        /**
         * ==== UI ====
         **/
        final Table contentUI = new Table(skin);
        contents.add(contentUI);
        contentUI.align(Align.top | Align.left);

        OwnLabel titleUI = new OwnLabel(txt("gui.ui.interfacesettings"), skin, "help-title");

        Table ui = new Table();

        // LANGUAGE
        OwnLabel langLabel = new OwnLabel(txt("gui.ui.language"), skin);
        File i18nfolder = new File((System.getProperty("assets.location") != null ? System.getProperty("assets.location") : "") + "i18n/");
        String i18nname = "gsbundle";
        String[] files = i18nfolder.list();
        LangComboBoxBean[] langs = new LangComboBoxBean[files.length];
        int i = 0;
        for (String file : files) {
            if (file.startsWith("gsbundle") && file.endsWith(".properties")) {
                String locale = file.substring(i18nname.length(), file.length() - ".properties".length());
                // Default locale
                if (locale == null || locale.isEmpty())
                    locale = "-en-GB";
                if (locale.length() != 0) {
                    // Remove underscore _
                    locale = locale.substring(1).replace("_", "-");
                    Locale loc = Locale.forLanguageTag(locale);
                    langs[i] = new LangComboBoxBean(loc);
                } else {
                    langs[i] = new LangComboBoxBean(I18n.bundle.getLocale());
                }
            }
            i++;
        }
        Arrays.sort(langs);

        lang = new OwnSelectBox<LangComboBoxBean>(skin);
        lang.setWidth(textwidth * 3f);
        lang.setItems(langs);
        lang.setSelected(langs[idxLang(GlobalConf.program.LOCALE, langs)]);

        // THEME
        OwnLabel themeLabel = new OwnLabel(txt("gui.ui.theme"), skin);
        String[] themes = new String[] { "dark-green", "dark-green-x2", "dark-blue", "dark-blue-x2", "dark-orange", "dark-orange-x2", "bright-green", "bright-green-x2", "night-red", "night-red-x2" };
        theme = new OwnSelectBox<String>(skin);
        theme.setWidth(textwidth * 3f);
        theme.setItems(themes);
        theme.setSelected(GlobalConf.program.UI_THEME);

        // POINTER COORDINATES
        pointerCoords = new OwnCheckBox(txt("gui.ui.pointercoordinates"), skin, "default", pad);
        pointerCoords.setChecked(GlobalConf.program.DISPLAY_POINTER_COORDS);

        // LABELS
        labels.addAll(langLabel, themeLabel);

        // Add to table
        ui.add(langLabel).left().padRight(pad * 4).padBottom(pad);
        ui.add(lang).left().padBottom(pad).row();
        ui.add(themeLabel).left().padRight(pad * 4).padBottom(pad);
        ui.add(theme).left().padBottom(pad).row();
        ui.add(pointerCoords).left().padRight(pad * 2).padBottom(pad).row();

        // Add to content
        contentUI.add(titleUI).left().padBottom(pad * 2).row();
        contentUI.add(ui).left();

        /**
         * ==== PERFORMANCE ====
         **/
        final Table contentPerformance = new Table(skin);
        contents.add(contentPerformance);
        contentPerformance.align(Align.top | Align.left);

        // MULTITHREADING
        OwnLabel titleMultithread = new OwnLabel(txt("gui.multithreading"), skin, "help-title");

        Table multithread = new Table(skin);

        OwnLabel numThreadsLabel = new OwnLabel(txt("gui.thread.number"), skin);
        int maxthreads = Runtime.getRuntime().availableProcessors();
        ComboBoxBean[] cbs = new ComboBoxBean[maxthreads + 1];
        cbs[0] = new ComboBoxBean(txt("gui.letdecide"), 0);
        for (i = 1; i <= maxthreads; i++) {
            cbs[i] = new ComboBoxBean(txt("gui.thread", i), i);
        }
        numThreads = new OwnSelectBox<ComboBoxBean>(skin);
        numThreads.setWidth(textwidth * 3f);
        numThreads.setItems(cbs);
        numThreads.setSelectedIndex(GlobalConf.performance.NUMBER_THREADS);

        multithreadCb = new OwnCheckBox(txt("gui.thread.enable"), skin, "default", pad);
        multithreadCb.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeEvent) {
                    numThreads.setDisabled(!multithreadCb.isChecked());
                    // Add notice
                    return true;
                }
                return false;
            }
        });
        multithreadCb.setChecked(GlobalConf.performance.MULTITHREADING);
        numThreads.setDisabled(!multithreadCb.isChecked());

        // Add to table
        multithread.add(multithreadCb).colspan(2).left().padBottom(pad).row();
        multithread.add(numThreadsLabel).left().padRight(pad * 4).padBottom(pad);
        multithread.add(numThreads).left().padBottom(pad).row();
        final Cell<Actor> noticeMulithreadCell = multithread.add();
        noticeMulithreadCell.colspan(2).left();

        multithreadCb.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeEvent) {
                    if (noticeMulithreadCell.getActor() == null) {
                        String nextinfostr = txt("gui.ui.info") + '\n';
                        int lines = GlobalResources.countOccurrences(nextinfostr, '\n');
                        TextArea nextTimeInfo = new OwnTextArea(nextinfostr, skin, "info");
                        nextTimeInfo.setDisabled(true);
                        nextTimeInfo.setPrefRows(lines + 1);
                        nextTimeInfo.setWidth(tawidth);
                        nextTimeInfo.clearListeners();
                        noticeMulithreadCell.setActor(nextTimeInfo);
                    }
                    return true;
                }
                return false;
            }
        });

        // Add to content
        contentPerformance.add(titleMultithread).left().padBottom(pad * 2).row();
        contentPerformance.add(multithread).left().padBottom(pad * 4).row();

        // DRAW DISTANCE
        OwnLabel titleLod = new OwnLabel(txt("gui.lod"), skin, "help-title");

        Table lod = new Table(skin);

        // Smooth transitions
        lodFadeCb = new OwnCheckBox(txt("gui.lod.fade"), skin, "default", pad);
        lodFadeCb.setChecked(GlobalConf.scene.OCTREE_PARTICLE_FADE);

        // Draw distance
        OwnLabel ddLabel = new OwnLabel(txt("gui.lod.thresholds"), skin);
        lodTransitions = new OwnSlider(Constants.MIN_SLIDER, Constants.MAX_SLIDER, 0.1f, false, skin);
        lodTransitions.setValue(Math.round(MathUtilsd.lint(GlobalConf.scene.OCTANT_THRESHOLD_0 * MathUtilsd.radDeg, Constants.MIN_LOD_TRANS_ANGLE_DEG, Constants.MAX_LOD_TRANS_ANGLE_DEG, Constants.MIN_SLIDER, Constants.MAX_SLIDER)));

        final OwnLabel lodValueLabel = new OwnLabel(nf3.format(GlobalConf.scene.OCTANT_THRESHOLD_0 * MathUtilsd.radDeg), skin);

        lodTransitions.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeEvent) {
                    OwnSlider slider = (OwnSlider) event.getListenerActor();
                    lodValueLabel.setText(nf3.format(MathUtilsd.lint(slider.getValue(), Constants.MIN_SLIDER, Constants.MAX_SLIDER, Constants.MIN_LOD_TRANS_ANGLE_DEG, Constants.MAX_LOD_TRANS_ANGLE_DEG)));
                    return true;
                }
                return false;
            }
        });

        OwnImageButton lodTooltip = new OwnImageButton(skin, "tooltip");
        lodTooltip.addListener(new TextTooltip(txt("gui.lod.thresholds.info"), skin));

        // LABELS
        labels.addAll(numThreadsLabel, ddLabel);

        // Add to table
        lod.add(lodFadeCb).colspan(4).left().padBottom(pad).row();
        lod.add(ddLabel).left().padRight(pad * 4).padBottom(pad);
        lod.add(lodTransitions).left().padRight(pad * 4).padBottom(pad);
        lod.add(lodValueLabel).left().padRight(pad * 4).padBottom(pad);
        lod.add(lodTooltip).left().padBottom(pad);

        // Add to content
        contentPerformance.add(titleLod).left().padBottom(pad * 2).row();
        contentPerformance.add(lod).left();

        /**
         * ==== CONTROLS ====
         **/
        final Table contentControls = new Table(skin);
        contents.add(contentControls);
        contentControls.align(Align.top | Align.left);

        OwnLabel titleController = new OwnLabel(txt("gui.controller"), skin, "help-title");

        // DETECTED CONTROLLER
        OwnLabel detectedLabel = new OwnLabel(txt("gui.controller.detected"), skin);
        Array<Controller> controllers = Controllers.getControllers();
        OwnLabel controllerName = new OwnLabel(controllers.size == 0 ? txt("gui.controller.nocontrollers") : controllers.get(0).getName(), skin);

        // CONTROLLER MAPPINGS
        OwnLabel mappingsLabel = new OwnLabel(txt("gui.controller.mappingsfile"), skin);
        Array<FileComboBoxBean> controllerMappingsFiles = new Array<FileComboBoxBean>();
        FileHandle externalfolder = Gdx.files.absolute(SysUtilsFactory.getSysUtils().getAssetsLocation() + "./mappings/");
        FileHandle homefolder = Gdx.files.absolute(SysUtilsFactory.getSysUtils().getDefaultMappingsDir().getPath());
        Array<FileHandle> mappingFiles = new Array<FileHandle>();
        GlobalResources.listRec(externalfolder, mappingFiles, ".controller");
        GlobalResources.listRec(homefolder, mappingFiles, ".controller");
        FileComboBoxBean selected = null;
        for (FileHandle fh : mappingFiles) {
            FileComboBoxBean fcbb = new FileComboBoxBean(fh);
            controllerMappingsFiles.add(fcbb);
            if (GlobalConf.controls.CONTROLLER_MAPPINGS_FILE.endsWith(fh.name())) {
                selected = fcbb;
            }
        }

        controllerMappings = new OwnSelectBox<FileComboBoxBean>(skin);
        controllerMappings.setItems(controllerMappingsFiles);
        controllerMappings.setSelected(selected);

        // INVERT Y
        inverty = new OwnCheckBox("Invert look y axis", skin, "default", pad);
        inverty.setChecked(GlobalConf.controls.INVERT_LOOK_Y_AXIS);

        // KEY BINDINGS
        OwnLabel titleKeybindings = new OwnLabel(txt("gui.keymappings"), skin, "help-title");

        Map<TreeSet<Integer>, ProgramAction> keyboardMappings = KeyBindings.instance.getSortedMappings();
        Set<TreeSet<Integer>> keymaps = keyboardMappings.keySet();
        String[][] data = new String[keyboardMappings.size()][2];

        i = 0;
        for (TreeSet<Integer> keys : keymaps) {
            ProgramAction action = keyboardMappings.get(keys);
            data[i][0] = action.actionName;
            data[i][1] = keysToString(keys);
            i++;
        }

        Table controls = new Table(skin);
        controls.align(Align.left | Align.top);
        // Header
        controls.add(new OwnLabel(txt("gui.keymappings.action"), skin, "header")).left();
        controls.add(new OwnLabel(txt("gui.keymappings.keys"), skin, "header")).left().row();

        controls.add(new OwnLabel(txt("action.forward"), skin)).left();
        controls.add(new OwnLabel(Keys.toString(Keys.UP).toUpperCase(), skin)).left().row();
        controls.add(new OwnLabel(txt("action.backward"), skin)).left();
        controls.add(new OwnLabel(Keys.toString(Keys.DOWN).toUpperCase(), skin)).left().row();
        controls.add(new OwnLabel(txt("action.left"), skin)).left();
        controls.add(new OwnLabel(Keys.toString(Keys.LEFT).toUpperCase(), skin)).left().row();
        controls.add(new OwnLabel(txt("action.right"), skin)).left();
        controls.add(new OwnLabel(Keys.toString(Keys.RIGHT).toUpperCase(), skin)).left().row();

        // Controls
        for (String[] pair : data) {
            controls.add(new OwnLabel(pair[0], skin)).left();
            controls.add(new OwnLabel(pair[1], skin)).left().row();
        }

        OwnScrollPane controlsScroll = new OwnScrollPane(controls, skin, "default-nobg");
        controlsScroll.setWidth(controlsscrollw);
        controlsScroll.setHeight(controllsscrollh);
        controlsScroll.setForceScroll(false, true);
        controlsScroll.setSmoothScrolling(true);
        controlsScroll.setFadeScrollBars(false);
        scrolls.add(controlsScroll);

        // Add to content
        contentControls.add(titleController).colspan(2).left().padBottom(pad * 2).row();
        contentControls.add(detectedLabel).left().padBottom(pad * 2);
        contentControls.add(controllerName).left().padBottom(pad * 2).row();
        contentControls.add(mappingsLabel).left().padBottom(pad * 2);
        contentControls.add(controllerMappings).left().padBottom(pad * 2).row();
        contentControls.add(inverty).left().colspan(2).padBottom(pad * 2).row();
        contentControls.add(titleKeybindings).colspan(2).left().padBottom(pad * 2).row();
        contentControls.add(controlsScroll).colspan(2).left();

        /**
         * ==== SCREENSHOTS ====
         **/
        final Table contentScreenshots = new Table(skin);
        contents.add(contentScreenshots);
        contentScreenshots.align(Align.top | Align.left);

        // SCREEN CAPTURE
        OwnLabel titleScreenshots = new OwnLabel(txt("gui.screencapture"), skin, "help-title");

        Table screenshots = new Table(skin);

        // Info
        String ssinfostr = txt("gui.screencapture.info") + '\n';
        int lines = GlobalResources.countOccurrences(ssinfostr, '\n');
        TextArea screenshotsInfo = new OwnTextArea(ssinfostr, skin, "info");
        screenshotsInfo.setDisabled(true);
        screenshotsInfo.setPrefRows(lines + 1);
        screenshotsInfo.setWidth(tawidth);
        screenshotsInfo.clearListeners();

        // Save location
        OwnLabel screenshotsLocationLabel = new OwnLabel(txt("gui.screenshots.save"), skin);
        screenshotsLocationLabel.pack();
        screenshotsLocation = new OwnTextButton(GlobalConf.screenshot.SCREENSHOT_FOLDER, skin);
        screenshotsLocation.pad(pad);
        screenshotsLocation.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeEvent) {
                    FileChooser fc = FileChooser.createPickDialog(txt("gui.screenshots.directory.choose"), skin, Gdx.files.absolute(GlobalConf.screenshot.SCREENSHOT_FOLDER));
                    fc.setResultListener(new ResultListener() {
                        @Override
                        public boolean result(boolean success, FileHandle result) {
                            if (success) {
                                // do stuff with result
                                screenshotsLocation.setText(result.path());
                            }
                            return true;
                        }
                    });
                    fc.setOkButtonText(txt("gui.ok"));
                    fc.setCancelButtonText(txt("gui.cancel"));
                    fc.setFilter(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            return pathname.isDirectory();
                        }
                    });
                    fc.show(stage);

                    return true;
                }
                return false;
            }
        });

        // Size
        final OwnLabel screenshotsSizeLabel = new OwnLabel(txt("gui.screenshots.size"), skin);
        screenshotsSizeLabel.setDisabled(GlobalConf.screenshot.isSimpleMode());
        final OwnLabel xLabel = new OwnLabel("x", skin);
        screenshotsSizeValidator = new IntValidator(GlobalConf.ScreenshotConf.MIN_SCREENSHOT_SIZE, GlobalConf.ScreenshotConf.MAX_SCREENSHOT_SIZE);
        sswidthField = new OwnTextField(Integer.toString(MathUtils.clamp(GlobalConf.screenshot.SCREENSHOT_WIDTH, GlobalConf.ScreenshotConf.MIN_SCREENSHOT_SIZE, GlobalConf.ScreenshotConf.MAX_SCREENSHOT_SIZE)), skin, screenshotsSizeValidator);
        sswidthField.setWidth(textwidth);
        sswidthField.setDisabled(GlobalConf.screenshot.isSimpleMode());
        ssheightField = new OwnTextField(Integer.toString(MathUtils.clamp(GlobalConf.screenshot.SCREENSHOT_HEIGHT, GlobalConf.ScreenshotConf.MIN_SCREENSHOT_SIZE, GlobalConf.ScreenshotConf.MAX_SCREENSHOT_SIZE)), skin, screenshotsSizeValidator);
        ssheightField.setWidth(textwidth);
        ssheightField.setDisabled(GlobalConf.screenshot.isSimpleMode());
        HorizontalGroup ssSizeGroup = new HorizontalGroup();
        ssSizeGroup.space(pad * 2);
        ssSizeGroup.addActor(sswidthField);
        ssSizeGroup.addActor(xLabel);
        ssSizeGroup.addActor(ssheightField);

        // Mode
        OwnLabel ssModeLabel = new OwnLabel(txt("gui.screenshots.mode"), skin);
        ComboBoxBean[] screenshotModes = new ComboBoxBean[] { new ComboBoxBean(txt("gui.screenshots.mode.simple"), 0), new ComboBoxBean(txt("gui.screenshots.mode.redraw"), 1) };
        screenshotMode = new OwnSelectBox<ComboBoxBean>(skin);
        screenshotMode.setItems(screenshotModes);
        screenshotMode.setWidth(textwidth * 3f);
        screenshotMode.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeEvent) {
                    if (screenshotMode.getSelected().value == 0) {
                        // Simple
                        enableComponents(false, sswidthField, ssheightField, screenshotsSizeLabel, xLabel);
                    } else {
                        // Redraw
                        enableComponents(true, sswidthField, ssheightField, screenshotsSizeLabel, xLabel);
                    }
                    return true;
                }
                return false;
            }
        });
        screenshotMode.setSelected(screenshotModes[GlobalConf.screenshot.SCREENSHOT_MODE.ordinal()]);
        screenshotMode.addListener(new TextTooltip(txt("gui.tooltip.screenshotmode"), skin));

        OwnImageButton screenshotsModeTooltip = new OwnImageButton(skin, "tooltip");
        screenshotsModeTooltip.addListener(new TextTooltip(txt("gui.tooltip.screenshotmode"), skin));

        HorizontalGroup ssModeGroup = new HorizontalGroup();
        ssModeGroup.space(pad);
        ssModeGroup.addActor(screenshotMode);
        ssModeGroup.addActor(screenshotsModeTooltip);

        // LABELS
        labels.addAll(screenshotsLocationLabel, ssModeLabel, screenshotsSizeLabel);

        // Add to table
        screenshots.add(screenshotsInfo).colspan(2).left().padBottom(pad).row();
        screenshots.add(screenshotsLocationLabel).left().padRight(pad * 4).padBottom(pad);
        screenshots.add(screenshotsLocation).left().expandX().padBottom(pad).row();
        screenshots.add(ssModeLabel).left().padRight(pad * 4).padBottom(pad);
        screenshots.add(ssModeGroup).left().expandX().padBottom(pad).row();
        screenshots.add(screenshotsSizeLabel).left().padRight(pad * 4).padBottom(pad);
        screenshots.add(ssSizeGroup).left().expandX().padBottom(pad).row();

        // Add to content
        contentScreenshots.add(titleScreenshots).left().padBottom(pad * 2).row();
        contentScreenshots.add(screenshots).left();

        /**
         * ==== FRAME OUTPUT ====
         **/
        final Table contentFrames = new Table(skin);
        contents.add(contentFrames);
        contentFrames.align(Align.top | Align.left);

        // FRAME OUTPUT CONFIG
        OwnLabel titleFrameoutput = new OwnLabel(txt("gui.frameoutput"), skin, "help-title");

        Table frameoutput = new Table(skin);

        // Info
        String foinfostr = txt("gui.frameoutput.info") + '\n';
        lines = GlobalResources.countOccurrences(foinfostr, '\n');
        TextArea frameoutputInfo = new OwnTextArea(foinfostr, skin, "info");
        frameoutputInfo.setDisabled(true);
        frameoutputInfo.setPrefRows(lines + 1);
        frameoutputInfo.setWidth(tawidth);
        frameoutputInfo.clearListeners();

        // Save location
        OwnLabel frameoutputLocationLabel = new OwnLabel(txt("gui.frameoutput.location"), skin);
        frameoutputLocation = new OwnTextButton(GlobalConf.frame.RENDER_FOLDER, skin);
        frameoutputLocation.pad(pad);
        frameoutputLocation.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeEvent) {
                    FileChooser fc = FileChooser.createPickDialog(txt("gui.frameoutput.directory.choose"), skin, Gdx.files.absolute(GlobalConf.frame.RENDER_FOLDER));
                    fc.setResultListener(new ResultListener() {
                        @Override
                        public boolean result(boolean success, FileHandle result) {
                            if (success) {
                                // do stuff with result
                                frameoutputLocation.setText(result.path());
                            }
                            return true;
                        }
                    });
                    fc.setOkButtonText(txt("gui.ok"));
                    fc.setCancelButtonText(txt("gui.cancel"));
                    fc.setFilter(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            return pathname.isDirectory();
                        }
                    });
                    fc.show(stage);

                    return true;
                }
                return false;
            }
        });

        // Prefix
        OwnLabel prefixLabel = new OwnLabel(txt("gui.frameoutput.prefix"), skin);
        frameoutputPrefix = new OwnTextField(GlobalConf.frame.RENDER_FILE_NAME, skin, new RegexpValidator("^\\w+$"));
        frameoutputPrefix.setWidth(textwidth * 3f);

        // FPS
        OwnLabel fpsLabel = new OwnLabel(txt("gui.frameoutput.fps"), skin);
        frameoutputFps = new OwnTextField(Integer.toString(GlobalConf.frame.RENDER_TARGET_FPS), skin, new IntValidator(1, 200));
        frameoutputFps.setWidth(textwidth * 3f);

        // Size
        final OwnLabel frameoutputSizeLabel = new OwnLabel(txt("gui.frameoutput.size"), skin);
        frameoutputSizeLabel.setDisabled(GlobalConf.frame.isSimpleMode());
        final OwnLabel xLabelfo = new OwnLabel("x", skin);
        frameoutputSizeValidator = new IntValidator(GlobalConf.FrameConf.MIN_FRAME_SIZE, GlobalConf.FrameConf.MAX_FRAME_SIZE);
        fowidthField = new OwnTextField(Integer.toString(MathUtils.clamp(GlobalConf.frame.RENDER_WIDTH, GlobalConf.FrameConf.MIN_FRAME_SIZE, GlobalConf.FrameConf.MAX_FRAME_SIZE)), skin, frameoutputSizeValidator);
        fowidthField.setWidth(textwidth);
        fowidthField.setDisabled(GlobalConf.frame.isSimpleMode());
        foheightField = new OwnTextField(Integer.toString(MathUtils.clamp(GlobalConf.frame.RENDER_HEIGHT, GlobalConf.FrameConf.MIN_FRAME_SIZE, GlobalConf.FrameConf.MAX_FRAME_SIZE)), skin, frameoutputSizeValidator);
        foheightField.setWidth(textwidth);
        foheightField.setDisabled(GlobalConf.frame.isSimpleMode());
        HorizontalGroup foSizeGroup = new HorizontalGroup();
        foSizeGroup.space(pad * 2);
        foSizeGroup.addActor(fowidthField);
        foSizeGroup.addActor(xLabelfo);
        foSizeGroup.addActor(foheightField);

        // Mode
        OwnLabel fomodeLabel = new OwnLabel(txt("gui.screenshots.mode"), skin);
        ComboBoxBean[] frameoutputModes = new ComboBoxBean[] { new ComboBoxBean(txt("gui.screenshots.mode.simple"), 0), new ComboBoxBean(txt("gui.screenshots.mode.redraw"), 1) };
        frameoutputMode = new OwnSelectBox<ComboBoxBean>(skin);
        frameoutputMode.setItems(frameoutputModes);
        frameoutputMode.setWidth(textwidth * 3f);
        frameoutputMode.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeEvent) {
                    if (frameoutputMode.getSelected().value == 0) {
                        // Simple
                        enableComponents(false, fowidthField, foheightField, frameoutputSizeLabel, xLabelfo);
                    } else {
                        // Redraw
                        enableComponents(true, fowidthField, foheightField, frameoutputSizeLabel, xLabelfo);
                    }
                    return true;
                }
                return false;
            }
        });
        frameoutputMode.setSelected(frameoutputModes[GlobalConf.frame.FRAME_MODE.ordinal()]);
        frameoutputMode.addListener(new TextTooltip(txt("gui.tooltip.screenshotmode"), skin));

        OwnImageButton frameoutputModeTooltip = new OwnImageButton(skin, "tooltip");
        frameoutputModeTooltip.addListener(new TextTooltip(txt("gui.tooltip.screenshotmode"), skin));

        HorizontalGroup foModeGroup = new HorizontalGroup();
        foModeGroup.space(pad);
        foModeGroup.addActor(frameoutputMode);
        foModeGroup.addActor(frameoutputModeTooltip);

        // LABELS
        labels.addAll(frameoutputLocationLabel, prefixLabel, fpsLabel, fomodeLabel, frameoutputSizeLabel);

        // Add to table
        frameoutput.add(frameoutputInfo).colspan(2).left().padBottom(pad).row();
        frameoutput.add(frameoutputLocationLabel).left().padRight(pad * 4).padBottom(pad);
        frameoutput.add(frameoutputLocation).left().expandX().padBottom(pad).row();
        frameoutput.add(prefixLabel).left().padRight(pad * 4).padBottom(pad);
        frameoutput.add(frameoutputPrefix).left().padBottom(pad).row();
        frameoutput.add(fpsLabel).left().padRight(pad * 4).padBottom(pad);
        frameoutput.add(frameoutputFps).left().padBottom(pad).row();
        frameoutput.add(fomodeLabel).left().padRight(pad * 4).padBottom(pad);
        frameoutput.add(foModeGroup).left().expandX().padBottom(pad).row();
        frameoutput.add(frameoutputSizeLabel).left().padRight(pad * 4).padBottom(pad);
        frameoutput.add(foSizeGroup).left().expandX().padBottom(pad).row();

        // Add to content
        contentFrames.add(titleFrameoutput).left().padBottom(pad * 2).row();
        contentFrames.add(frameoutput).left();

        /**
         * ==== CAMERA ====
         **/
        final Table contentCamera = new Table(skin);
        contents.add(contentCamera);
        contentCamera.align(Align.top | Align.left);

        // CAMERA RECORDING
        Table camrec = new Table(skin);

        OwnLabel titleCamrec = new OwnLabel(txt("gui.camerarec.title"), skin, "help-title");

        // fps
        OwnLabel camfpsLabel = new OwnLabel(txt("gui.camerarec.fps"), skin);
        camrecFps = new OwnTextField(Integer.toString(GlobalConf.frame.CAMERA_REC_TARGET_FPS), skin, new IntValidator(1, 200));
        camrecFps.setWidth(textwidth * 3f);

        // Activate automatically
        cbAutoCamrec = new OwnCheckBox(txt("gui.camerarec.frameoutput"), skin, "default", pad);
        cbAutoCamrec.setChecked(GlobalConf.frame.AUTO_FRAME_OUTPUT_CAMERA_PLAY);
        cbAutoCamrec.addListener(new TextTooltip(txt("gui.tooltip.playcamera.frameoutput"), skin));
        OwnImageButton camrecTooltip = new OwnImageButton(skin, "tooltip");
        camrecTooltip.addListener(new TextTooltip(txt("gui.tooltip.playcamera.frameoutput"), skin));

        HorizontalGroup cbGroup = new HorizontalGroup();
        cbGroup.space(pad);
        cbGroup.addActor(cbAutoCamrec);
        cbGroup.addActor(camrecTooltip);

        // LABELS
        labels.add(camfpsLabel);

        // Add to table
        camrec.add(camfpsLabel).left().padRight(pad * 4).padBottom(pad);
        camrec.add(camrecFps).left().expandX().padBottom(pad).row();
        camrec.add(cbGroup).colspan(2).left().padBottom(pad).row();

        // Add to content
        contentCamera.add(titleCamrec).left().padBottom(pad * 2).row();
        contentCamera.add(camrec).left();

        /**
         * ==== 360 ====
         **/
        final Table content360 = new Table(skin);
        contents.add(content360);
        content360.align(Align.top | Align.left);

        // CUBEMAP
        OwnLabel titleCubemap = new OwnLabel(txt("gui.360"), skin, "help-title");
        Table cubemap = new Table(skin);

        // Info
        String cminfostr = txt("gui.360.info") + '\n';
        lines = GlobalResources.countOccurrences(cminfostr, '\n');
        TextArea cmInfo = new OwnTextArea(cminfostr, skin, "info");
        cmInfo.setDisabled(true);
        cmInfo.setPrefRows(lines + 1);
        cmInfo.setWidth(tawidth);
        cmInfo.clearListeners();

        // Resolution
        OwnLabel cmResolutionLabel = new OwnLabel(txt("gui.360.resolution"), skin);
        cmResolution = new OwnTextField(Integer.toString(GlobalConf.scene.CUBEMAP_FACE_RESOLUTION), skin, new IntValidator(20, 15000));
        cmResolution.setWidth(textwidth * 3f);

        // LABELS
        labels.add(cmResolutionLabel);

        // Add to table
        cubemap.add(cmInfo).colspan(2).left().padBottom(pad).row();
        cubemap.add(cmResolutionLabel).left().padRight(pad * 4).padBottom(pad);
        cubemap.add(cmResolution).left().expandX().padBottom(pad).row();

        // Add to content
        content360.add(titleCubemap).left().padBottom(pad * 2).row();
        content360.add(cubemap).left();

        /**
         * ==== DATA ====
         **/
        final Table contentDataTable = new Table(skin);
        contentDataTable.align(Align.top | Align.left);
        final OwnScrollPane contentData = new OwnScrollPane(contentDataTable, skin, "minimalist-nobg");
        contentData.setHeight(scrollh);
        contentData.setScrollingDisabled(true, false);
        contentData.setFadeScrollBars(false);
        contents.add(contentData);

        // GENERAL OPTIONS
        OwnLabel titleGeneralData = new OwnLabel(txt("gui.data.options"), skin, "help-title");
        highAccuracyPositions = new OwnCheckBox(txt("gui.data.highaccuracy"), skin, pad);
        highAccuracyPositions.setChecked(GlobalConf.data.HIGH_ACCURACY_POSITIONS);
        highAccuracyPositions.addListener(new TextTooltip(txt("gui.data.highaccuracy.tooltip"), skin));
        OwnImageButton highAccTooltip = new OwnImageButton(skin, "tooltip");
        highAccTooltip.addListener(new TextTooltip(txt("gui.data.highaccuracy.tooltip"), skin));

        HorizontalGroup haGroup = new HorizontalGroup();
        haGroup.space(pad);
        haGroup.addActor(highAccuracyPositions);
        haGroup.addActor(highAccTooltip);

        // DATA SOURCE
        OwnLabel titleData = new OwnLabel(txt("gui.data.source"), skin, "help-title");
        Table datasource = new Table(skin);

        String assetsLoc = System.getProperty("assets.location") != null ? System.getProperty("assets.location") : "";
        FileHandle dataFolder = Gdx.files.absolute(assetsLoc + File.separatorChar + "data");
        FileHandle[] catalogFiles = dataFolder.list(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().startsWith("catalog-dr2-") && pathname.getName().endsWith(".json");
            }
        });
        JsonReader reader = new JsonReader();

        // Sort by name
        Comparator<FileHandle> byName = (FileHandle a, FileHandle b) -> a.name().compareTo(b.name());
        Arrays.sort(catalogFiles, byName);
        candidates = new HashMap<Button, String>();
        catalogs = new OwnTextButton[catalogFiles.length];
        i = 0;
        float taheight = GlobalConf.SCALE_FACTOR > 1 ? 50 : 35;
        String[] currentSetting = GlobalConf.data.CATALOG_JSON_FILES.split("\\s*,\\s*");
        Table datasets = new Table();
        for (FileHandle catalogFile : catalogFiles) {
            String candidate = catalogFile.path().substring(assetsLoc.length(), catalogFile.path().length());

            String name = null;
            String desc = null;
            try {
                JsonValue val = reader.parse(catalogFile);
                if (val.has("description"))
                    desc = val.get("description").asString();
                if (val.has("name"))
                    name = val.get("name").asString();
            } catch (Exception e) {
            }
            if (desc == null)
                desc = candidate;
            if (name == null)
                name = catalogFile.nameWithoutExtension();

            OwnTextButton cb = new OwnTextButton(name, skin, "toggle-big");

            cb.setChecked(contains(catalogFile.name(), currentSetting));
            cb.addListener(new TextTooltip(candidate, skin));
            datasets.add(cb).left().top().padRight(pad);

            // Description
            TextArea description = new OwnTextArea(desc, skin.get("regular", TextFieldStyle.class));
            description.setDisabled(true);
            description.setPrefRows(2);
            description.setWidth(tawidth);
            description.setHeight(taheight);
            datasets.add(description).left().top().padTop(pad / 2).padLeft(pad).row();

            candidates.put(cb, candidate);

            catalogs[i++] = cb;
        }
        datasource.add(datasets).colspan(2).row();
        ButtonGroup<OwnTextButton> bg = new ButtonGroup<OwnTextButton>();
        bg.setMinCheckCount(0);
        bg.setMaxCheckCount(1);
        bg.add(catalogs);
        float maxw = 0;
        for (Button b : catalogs) {
            if (b.getWidth() > maxw)
                maxw = b.getWidth();
        }
        for (Button b : catalogs)
            b.setWidth(maxw + 10 * GlobalConf.SCALE_FACTOR);

        final Cell<Actor> noticeDataCell = datasource.add();
        noticeDataCell.colspan(2).left().row();

        EventListener dataNoticeListener = new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeEvent) {
                    if (noticeDataCell.getActor() == null) {
                        String nextinfostr = txt("gui.ui.info") + '\n';
                        int lines = GlobalResources.countOccurrences(nextinfostr, '\n');
                        TextArea nextTimeInfo = new OwnTextArea(nextinfostr, skin, "info");
                        nextTimeInfo.setDisabled(true);
                        nextTimeInfo.setPrefRows(lines + 1);
                        nextTimeInfo.setWidth(tawidth);
                        nextTimeInfo.clearListeners();
                        noticeDataCell.setActor(nextTimeInfo);
                    }
                    return true;
                }
                return false;
            }
        };
        for (OwnTextButton cb : catalogs)
            cb.addListener(dataNoticeListener);

        datasetChooser = new OwnCheckBox(txt("gui.data.dschooser"), skin, pad);
        datasetChooser.setChecked(GlobalConf.program.DISPLAY_DATASET_DIALOG);

        // Add to content
        contentDataTable.add(titleGeneralData).left().padBottom(pad * 2).row();
        contentDataTable.add(haGroup).left().padBottom(pad * 2).row();
        contentDataTable.add(titleData).left().padBottom(pad * 2).row();
        contentDataTable.add(datasource).left().padBottom(pad * 2).row();
        contentDataTable.add(datasetChooser).left();

        /**
         * ==== GAIA ====
         **/
        final Table contentGaia = new Table(skin);
        contents.add(contentGaia);
        contentGaia.align(Align.top | Align.left);

        // ATTITUDE
        OwnLabel titleAttitude = new OwnLabel(txt("gui.gaia.attitude"), skin, "help-title");
        Table attitude = new Table(skin);

        real = new OwnCheckBox(txt("gui.gaia.real"), skin, "radio", pad);
        real.setChecked(GlobalConf.data.REAL_GAIA_ATTITUDE);
        nsl = new OwnCheckBox(txt("gui.gaia.nsl"), skin, "radio", pad);
        nsl.setChecked(!GlobalConf.data.REAL_GAIA_ATTITUDE);

        new ButtonGroup<CheckBox>(real, nsl);

        // Add to table
        attitude.add(nsl).left().padBottom(pad).row();
        attitude.add(real).left().padBottom(pad).row();
        final Cell<Actor> noticeAttCell = attitude.add();
        noticeAttCell.colspan(2).left();

        EventListener attNoticeListener = new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeEvent) {
                    if (noticeAttCell.getActor() == null) {
                        String nextinfostr = txt("gui.ui.info") + '\n';
                        int lines = GlobalResources.countOccurrences(nextinfostr, '\n');
                        TextArea nextTimeInfo = new OwnTextArea(nextinfostr, skin, "info");
                        nextTimeInfo.setDisabled(true);
                        nextTimeInfo.setPrefRows(lines + 1);
                        nextTimeInfo.setWidth(tawidth);
                        nextTimeInfo.clearListeners();
                        noticeAttCell.setActor(nextTimeInfo);
                    }
                    return true;
                }
                return false;
            }
        };
        real.addListener(attNoticeListener);
        nsl.addListener(attNoticeListener);

        // Add to content
        contentGaia.add(titleAttitude).left().padBottom(pad * 2).row();
        contentGaia.add(attitude).left();

        /**
         * ==== SYSTEM ====
         **/
        final Table contentSystem = new Table(skin);
        contents.add(contentSystem);
        contentSystem.align(Align.top | Align.left);

        // STATS
        OwnLabel titleStats = new OwnLabel(txt("gui.system.reporting"), skin, "help-title");
        Table stats = new Table(skin);

        report = new OwnCheckBox(txt("gui.system.allowreporting"), skin, pad);
        report.setChecked(GlobalConf.program.ANALYTICS_ENABLED);

        // RELOAD DEFAULTS
        OwnTextButton reloadDefaults = new OwnTextButton(txt("gui.system.reloaddefaults"), skin);
        reloadDefaults.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeEvent) {
                    reloadDefaultPreferences();
                    me.hide();
                    // Prevent saving current state
                    GaiaSky.instance.savestate = false;
                    Gdx.app.exit();
                    return true;
                }

                return false;
            }

        });
        reloadDefaults.setWidth(180 * GlobalConf.SCALE_FACTOR);

        OwnLabel warningLabel = new OwnLabel(txt("gui.system.reloaddefaults.warn"), skin, "default-red");

        // Add to table
        stats.add(report).left().padBottom(pad * 5).row();
        stats.add(warningLabel).left().padBottom(pad).row();
        stats.add(reloadDefaults).left();

        // Add to content
        contentSystem.add(titleStats).left().padBottom(pad * 2).row();
        contentSystem.add(stats).left();

        /** COMPUTE LABEL WIDTH **/
        float maxLabelWidth = 0;
        for (OwnLabel l : labels) {
            l.pack();
            if (l.getWidth() > maxLabelWidth)
                maxLabelWidth = l.getWidth();
        }
        maxLabelWidth = Math.max(textwidth * 2, maxLabelWidth);
        for (OwnLabel l : labels)
            l.setWidth(maxLabelWidth);

        /** ADD ALL CONTENT **/
        tabContent.addActor(contentGraphics);
        tabContent.addActor(contentUI);
        tabContent.addActor(contentPerformance);
        tabContent.addActor(contentControls);
        tabContent.addActor(contentScreenshots);
        tabContent.addActor(contentFrames);
        tabContent.addActor(contentCamera);
        tabContent.addActor(content360);
        tabContent.addActor(contentData);
        tabContent.addActor(contentGaia);
        tabContent.addActor(contentSystem);

        /** ADD TO MAIN TABLE **/
        content.add(tabContent).left().padLeft(10).expand().fill();

        // Listen to changes in the tab button checked states
        // Set visibility of the tab content to match the checked state
        ChangeListener tab_listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                contentGraphics.setVisible(tabGraphics.isChecked());
                contentUI.setVisible(tabUI.isChecked());
                contentPerformance.setVisible(tabPerformance.isChecked());
                contentControls.setVisible(tabControls.isChecked());
                contentScreenshots.setVisible(tabScreenshots.isChecked());
                contentFrames.setVisible(tabFrames.isChecked());
                contentCamera.setVisible(tabCamera.isChecked());
                content360.setVisible(tab360.isChecked());
                contentData.setVisible(tabData.isChecked());
                contentGaia.setVisible(tabGaia.isChecked());
                contentSystem.setVisible(tabSystem.isChecked());
            }
        };
        tabGraphics.addListener(tab_listener);
        tabUI.addListener(tab_listener);
        tabPerformance.addListener(tab_listener);
        tabControls.addListener(tab_listener);
        tabScreenshots.addListener(tab_listener);
        tabFrames.addListener(tab_listener);
        tabCamera.addListener(tab_listener);
        tab360.addListener(tab_listener);
        tabData.addListener(tab_listener);
        tabGaia.addListener(tab_listener);
        tabSystem.addListener(tab_listener);

        // Let only one tab button be checked at a time
        ButtonGroup<Button> tabs = new ButtonGroup<Button>();
        tabs.setMinCheckCount(1);
        tabs.setMaxCheckCount(1);
        tabs.add(tabGraphics);
        tabs.add(tabUI);
        tabs.add(tabPerformance);
        tabs.add(tabControls);
        tabs.add(tabScreenshots);
        tabs.add(tabFrames);
        tabs.add(tabCamera);
        tabs.add(tab360);
        tabs.add(tabData);
        tabs.add(tabGaia);
        tabs.add(tabSystem);

    }

    private boolean contains(String name, String[] list) {
        for (String candidate : list)
            if (candidate.contains(name))
                return true;
        return false;
    }

    @Override
    protected void accept() {
        saveCurrentPreferences();
    }

    @Override
    protected void cancel() {
        revertLivePreferences();
    }

    private void reloadDefaultPreferences() {
        // User config file
        File userFolder = SysUtilsFactory.getSysUtils().getGSHomeDir();
        File userFolderConfFile = new File(userFolder, "global.properties");

        // Internal config
        File confFolder = new File("conf" + File.separator);
        File internalFolderConfFile = new File(confFolder, "global.properties");

        // Delete current conf
        if (userFolderConfFile.exists()) {
            userFolderConfFile.delete();
        }

        // Copy file
        try {
            if (confFolder.exists() && confFolder.isDirectory()) {
                // Running released package
                copyFile(internalFolderConfFile, userFolderConfFile, true);
            } else {
                // Running from code?
                if (!new File("../android/assets/conf" + File.separator).exists()) {
                    throw new IOException("File ../android/assets/conf does not exist!");
                }
                copyFile(new File("../android/assets/conf" + File.separator + "global.properties"), userFolderConfFile, true);
            }

        } catch (Exception e) {
            Logger.error(e, "Error copying default preferences file to user folder: " + userFolderConfFile.getAbsolutePath());
        }

    }

    private static void copyFile(File sourceFile, File destFile, boolean ow) throws IOException {
        if (destFile.exists()) {
            if (ow) {
                // Overwrite, delete file
                destFile.delete();
            } else {
                return;
            }
        }
        // Create new
        destFile.createNewFile();

        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    private void saveCurrentPreferences() {
        // Add all properties to GlobalConf.instance

        final boolean reloadFullscreenMode = fullscreen.isChecked() != GlobalConf.screen.FULLSCREEN;
        final boolean reloadScreenMode = reloadFullscreenMode || (GlobalConf.screen.FULLSCREEN && (GlobalConf.screen.FULLSCREEN_WIDTH != fullscreenResolutions.getSelected().width || GlobalConf.screen.FULLSCREEN_HEIGHT != fullscreenResolutions.getSelected().height)) || (!GlobalConf.screen.FULLSCREEN && (GlobalConf.screen.SCREEN_WIDTH != Integer.parseInt(widthField.getText())) || GlobalConf.screen.SCREEN_HEIGHT != Integer.parseInt(heightField.getText()));

        GlobalConf.screen.FULLSCREEN = fullscreen.isChecked();

        // Fullscreen options
        GlobalConf.screen.FULLSCREEN_WIDTH = fullscreenResolutions.getSelected().width;
        GlobalConf.screen.FULLSCREEN_HEIGHT = fullscreenResolutions.getSelected().height;

        // Windowed options
        GlobalConf.screen.SCREEN_WIDTH = Integer.parseInt(widthField.getText());
        GlobalConf.screen.SCREEN_HEIGHT = Integer.parseInt(heightField.getText());

        // Graphics
        ComboBoxBean bean = gquality.getSelected();
        if (GlobalConf.scene.GRAPHICS_QUALITY != bean.value) {
            GlobalConf.scene.GRAPHICS_QUALITY = bean.value;
            EventManager.instance.post(Events.GRAPHICS_QUALITY_UPDATED, bean.value);
        }

        bean = aa.getSelected();
        Antialias newaa = GlobalConf.postprocess.getAntialias(bean.value);
        if (GlobalConf.postprocess.POSTPROCESS_ANTIALIAS != newaa) {
            GlobalConf.postprocess.POSTPROCESS_ANTIALIAS = GlobalConf.postprocess.getAntialias(bean.value);
            EventManager.instance.post(Events.ANTIALIASING_CMD, GlobalConf.postprocess.POSTPROCESS_ANTIALIAS);
        }

        GlobalConf.screen.VSYNC = vsync.isChecked();
        try {
            // Windows backend crashes for some reason
            Gdx.graphics.setVSync(GlobalConf.screen.VSYNC);
        } catch (Exception e) {
            Logger.error(e, this.getClass().getSimpleName());
        }

        if (limitfpsCb.isChecked()) {
            GlobalConf.screen.LIMIT_FPS = Integer.parseInt(limitFps.getText());
        } else {
            GlobalConf.screen.LIMIT_FPS = 0;
        }

        // Orbit renderer
        GlobalConf.scene.ORBIT_RENDERER = orbitRenderer.getSelected().value;

        // Line renderer
        boolean reloadLineRenderer = GlobalConf.scene.LINE_RENDERER != lineRenderer.getSelected().value;
        bean = lineRenderer.getSelected();
        GlobalConf.scene.LINE_RENDERER = bean.value;

        // Shadow mapping
        GlobalConf.scene.SHADOW_MAPPING = shadowsCb.isChecked();
        int newshadowres = Integer.parseInt(smResolution.getText());
        int newnshadows = nshadows.getSelected().value;
        final boolean reloadShadows = shadowsCb.isChecked() && (GlobalConf.scene.SHADOW_MAPPING_RESOLUTION != newshadowres || GlobalConf.scene.SHADOW_MAPPING_N_SHADOWS != newnshadows);

        // Interface
        LangComboBoxBean lbean = lang.getSelected();
        boolean reloadUI = GlobalConf.program.UI_THEME != theme.getSelected() || !lbean.locale.toLanguageTag().equals(GlobalConf.program.LOCALE);
        GlobalConf.program.LOCALE = lbean.locale.toLanguageTag();
        I18n.forceinit(Gdx.files.internal("i18n/gsbundle"));
        GlobalConf.program.UI_THEME = theme.getSelected();
        boolean previousPointerCoords = GlobalConf.program.DISPLAY_POINTER_COORDS;
        GlobalConf.program.DISPLAY_POINTER_COORDS = pointerCoords.isChecked();
        if (previousPointerCoords != GlobalConf.program.DISPLAY_POINTER_COORDS) {
            EventManager.instance.post(Events.DISPLAY_POINTER_COORDS_CMD, GlobalConf.program.DISPLAY_POINTER_COORDS);
        }
        // Update scale factor according to theme - for HiDPI screens
        GlobalConf.updateScaleFactor(GlobalConf.program.UI_THEME.endsWith("x2") ? 2f : 1f);

        // Performance
        bean = numThreads.getSelected();
        GlobalConf.performance.NUMBER_THREADS = bean.value;
        GlobalConf.performance.MULTITHREADING = multithreadCb.isChecked();

        GlobalConf.scene.OCTREE_PARTICLE_FADE = lodFadeCb.isChecked();
        GlobalConf.scene.OCTANT_THRESHOLD_0 = MathUtilsd.lint(lodTransitions.getValue(), Constants.MIN_SLIDER, Constants.MAX_SLIDER, Constants.MIN_LOD_TRANS_ANGLE_DEG, Constants.MAX_LOD_TRANS_ANGLE_DEG) * (float) MathUtilsd.degRad;
        // Here we use a 0.4 rad between the thresholds
        GlobalConf.scene.OCTANT_THRESHOLD_1 = GlobalConf.scene.OCTREE_PARTICLE_FADE ? GlobalConf.scene.OCTANT_THRESHOLD_0 + 0.4f : GlobalConf.scene.OCTANT_THRESHOLD_0;

        // Data
        boolean hapos = GlobalConf.data.HIGH_ACCURACY_POSITIONS;
        GlobalConf.data.HIGH_ACCURACY_POSITIONS = highAccuracyPositions.isChecked();

        if (hapos != GlobalConf.data.HIGH_ACCURACY_POSITIONS) {
            // Event
            EventManager.instance.post(Events.HIGH_ACCURACY_CMD, GlobalConf.data.HIGH_ACCURACY_POSITIONS);
        }
        GlobalConf.data.CATALOG_JSON_FILES = "";
        boolean first = true;
        for (Button b : catalogs) {
            if (b.isChecked()) {
                // Add all selected to list
                if (!first) {
                    GlobalConf.data.CATALOG_JSON_FILES += "," + candidates.get(b);
                } else {
                    GlobalConf.data.CATALOG_JSON_FILES += candidates.get(b);
                    first = false;
                }
            }
        }
        GlobalConf.program.DISPLAY_DATASET_DIALOG = datasetChooser.isChecked();

        // Screenshots
        File ssfile = new File(screenshotsLocation.getText().toString());
        if (ssfile.exists() && ssfile.isDirectory())
            GlobalConf.screenshot.SCREENSHOT_FOLDER = ssfile.getAbsolutePath();
        ScreenshotMode prev = GlobalConf.screenshot.SCREENSHOT_MODE;
        GlobalConf.screenshot.SCREENSHOT_MODE = GlobalConf.ScreenshotMode.values()[screenshotMode.getSelectedIndex()];
        int ssw = Integer.parseInt(sswidthField.getText());
        int ssh = Integer.parseInt(ssheightField.getText());
        boolean ssupdate = ssw != GlobalConf.screenshot.SCREENSHOT_WIDTH || ssh != GlobalConf.screenshot.SCREENSHOT_HEIGHT || !prev.equals(GlobalConf.screenshot.SCREENSHOT_MODE);
        GlobalConf.screenshot.SCREENSHOT_WIDTH = ssw;
        GlobalConf.screenshot.SCREENSHOT_HEIGHT = ssh;
        if (ssupdate)
            EventManager.instance.post(Events.SCREENSHOT_SIZE_UDPATE, GlobalConf.screenshot.SCREENSHOT_WIDTH, GlobalConf.screenshot.SCREENSHOT_HEIGHT);

        // Frame output
        File fofile = new File(frameoutputLocation.getText().toString());
        if (fofile.exists() && fofile.isDirectory())
            GlobalConf.frame.RENDER_FOLDER = fofile.getAbsolutePath();
        String text = frameoutputPrefix.getText();
        if (text.matches("^\\w+$")) {
            GlobalConf.frame.RENDER_FILE_NAME = text;
        }
        prev = GlobalConf.frame.FRAME_MODE;
        GlobalConf.frame.FRAME_MODE = GlobalConf.ScreenshotMode.values()[frameoutputMode.getSelectedIndex()];
        int fow = Integer.parseInt(fowidthField.getText());
        int foh = Integer.parseInt(foheightField.getText());
        boolean foupdate = fow != GlobalConf.frame.RENDER_WIDTH || foh != GlobalConf.frame.RENDER_HEIGHT || !prev.equals(GlobalConf.frame.FRAME_MODE);
        GlobalConf.frame.RENDER_WIDTH = fow;
        GlobalConf.frame.RENDER_HEIGHT = foh;
        GlobalConf.frame.RENDER_TARGET_FPS = Integer.parseInt(frameoutputFps.getText());
        if (foupdate)
            EventManager.instance.post(Events.FRAME_SIZE_UDPATE, GlobalConf.frame.RENDER_WIDTH, GlobalConf.frame.RENDER_HEIGHT);

        // Camera recording
        GlobalConf.frame.CAMERA_REC_TARGET_FPS = Integer.parseInt(camrecFps.getText());
        GlobalConf.frame.AUTO_FRAME_OUTPUT_CAMERA_PLAY = (Boolean) cbAutoCamrec.isChecked();

        // Cube map resolution
        int newres = Integer.parseInt(cmResolution.getText());
        if (newres != GlobalConf.scene.CUBEMAP_FACE_RESOLUTION)
            EventManager.instance.post(Events.CUBEMAP_RESOLUTION_CMD, newres);

        // Controllers
        String mappingsFile = controllerMappings.getSelected().file;
        if (mappingsFile != GlobalConf.controls.CONTROLLER_MAPPINGS_FILE) {
            GlobalConf.controls.CONTROLLER_MAPPINGS_FILE = mappingsFile;
            EventManager.instance.post(Events.RELOAD_CONTROLLER_MAPPINGS, mappingsFile);
        }
        GlobalConf.controls.INVERT_LOOK_Y_AXIS = inverty.isChecked();

        // Gaia attitude
        GlobalConf.data.REAL_GAIA_ATTITUDE = real.isChecked();

        // System
        GlobalConf.program.ANALYTICS_ENABLED = report.isChecked();

        // Save configuration
        ConfInit.instance.persistGlobalConf(new File(System.getProperty("properties.file")));

        EventManager.instance.post(Events.PROPERTIES_WRITTEN);

        if (reloadScreenMode) {
            Gdx.app.postRunnable(() -> {
                EventManager.instance.post(Events.SCREEN_MODE_CMD);
            });
        }

        if (reloadLineRenderer) {
            Gdx.app.postRunnable(() -> {
                EventManager.instance.post(Events.LINE_RENDERER_UPDATE);
            });
        }

        if (reloadShadows) {
            Gdx.app.postRunnable(() -> {
                GlobalConf.scene.SHADOW_MAPPING_RESOLUTION = newshadowres;
                GlobalConf.scene.SHADOW_MAPPING_N_SHADOWS = newnshadows;

                EventManager.instance.post(Events.REBUILD_SHADOW_MAP_DATA_CMD);
            });
        }

        if (reloadUI) {
            reloadUI();
        }

    }

    /**
     * Reverts preferences which have been modified live. It needs backup values.
     */
    private void revertLivePreferences() {
        EventManager.instance.post(Events.BRIGHTNESS_CMD, brightnessBak, true);
        EventManager.instance.post(Events.CONTRAST_CMD, contrastBak, true);
        EventManager.instance.post(Events.HUE_CMD, hueBak, true);
        EventManager.instance.post(Events.SATURATION_CMD, saturationBak, true);
        EventManager.instance.post(Events.GAMMA_CMD, gammaBak, true);
        EventManager.instance.post(Events.MOTION_BLUR_CMD, motionblurBak, true);
        EventManager.instance.post(Events.LENS_FLARE_CMD, lensflareBak, true);
        EventManager.instance.post(Events.LIGHT_SCATTERING_CMD, lightglowBak, true);
        EventManager.instance.post(Events.BLOOM_CMD, bloomBak, true);
    }

    private void reloadUI() {
        // Reinitialise user interface
        Gdx.app.postRunnable(() -> {
            // Reinitialise GUI system
            GlobalResources.updateSkin();
            GaiaSky.instance.reinitialiseGUI1();
            EventManager.instance.post(Events.SPACECRAFT_LOADED, GaiaSky.instance.sg.getNode("Spacecraft"));
            GaiaSky.instance.reinitialiseGUI2();
            // Time init
            EventManager.instance.post(Events.TIME_CHANGE_INFO, GaiaSky.instance.time.getTime());
            if (GaiaSky.instance.cam.mode == CameraManager.CameraMode.Focus)
                // Refocus
                EventManager.instance.post(Events.FOCUS_CHANGE_CMD, GaiaSky.instance.cam.getFocus());
            // Update names with new language
            GaiaSky.instance.sg.getRoot().updateNamesRec();
            // UI theme reload broadcast
            EventManager.instance.post(Events.UI_THEME_RELOAD_INFO, GlobalResources.skin);
        });
    }

    private void selectFullscreen(boolean fullscreen, OwnTextField widthField, OwnTextField heightField, SelectBox<DisplayMode> fullScreenResolutions, OwnLabel widthLabel, OwnLabel heightLabel) {
        if (fullscreen) {
            GlobalConf.screen.SCREEN_WIDTH = fullScreenResolutions.getSelected().width;
            GlobalConf.screen.SCREEN_HEIGHT = fullScreenResolutions.getSelected().height;
        } else {
            GlobalConf.screen.SCREEN_WIDTH = Integer.parseInt(widthField.getText());
            GlobalConf.screen.SCREEN_HEIGHT = Integer.parseInt(heightField.getText());
        }

        enableComponents(!fullscreen, widthField, heightField, widthLabel, heightLabel);
        enableComponents(fullscreen, fullScreenResolutions);
    }

    private int idxAa(int base, Antialias x) {
        if (x.getAACode() == -1)
            return 1;
        if (x.getAACode() == -2)
            return 2;
        if (x.getAACode() == 0)
            return 0;
        return (int) (Math.log(x.getAACode()) / Math.log(2) + 1e-10) + 2;
    }

    private int idxLang(String code, LangComboBoxBean[] langs) {
        if (code.isEmpty()) {
            code = I18n.bundle.getLocale().toLanguageTag();
        }
        for (int i = 0; i < langs.length; i++) {
            if (langs[i].locale.toLanguageTag().equals(code)) {
                return i;
            }
        }
        return -1;
    }

    private String keysToString(TreeSet<Integer> keys) {
        String s = "";

        int i = 0;
        int n = keys.size();
        for (Integer key : keys) {
            s += Keys.toString(key).toUpperCase();
            if (i < n - 1) {
                s += " + ";
            }

            i++;
        }

        return s;
    }

}
