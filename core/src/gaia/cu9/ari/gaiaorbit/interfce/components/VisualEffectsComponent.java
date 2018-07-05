package gaia.cu9.ari.gaiaorbit.interfce.components;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;

import gaia.cu9.ari.gaiaorbit.event.EventManager;
import gaia.cu9.ari.gaiaorbit.event.Events;
import gaia.cu9.ari.gaiaorbit.event.IObserver;
import gaia.cu9.ari.gaiaorbit.util.Constants;
import gaia.cu9.ari.gaiaorbit.util.GlobalConf;
import gaia.cu9.ari.gaiaorbit.util.math.MathUtilsd;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnLabel;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnSlider;
import gaia.cu9.ari.gaiaorbit.util.scene2d.Separator;

public class VisualEffectsComponent extends GuiComponent implements IObserver {

    protected Slider starBrightness, starSize, starOpacity, ambientLight, labelSize;
    protected OwnLabel starbrightnessl, size, opacity, ambient, bloomLabel, labels;

    boolean flag = true;

    boolean hackProgrammaticChangeEvents = true;

    public VisualEffectsComponent(Skin skin, Stage stage) {
        super(skin, stage);
    }

    public void initialize() {
        float space3 = 3 * GlobalConf.SCALE_FACTOR;
        float space2 = 2 * GlobalConf.SCALE_FACTOR;
        float sliderWidth = 140 * GlobalConf.SCALE_FACTOR;
        /** Star brightness **/
        Label sbrightnessLabel = new Label(txt("gui.starbrightness"), skin, "default");
        starbrightnessl = new OwnLabel(Integer.toString((int) (MathUtilsd.lint(GlobalConf.scene.STAR_BRIGHTNESS, Constants.MIN_STAR_BRIGHT, Constants.MAX_STAR_BRIGHT, Constants.MIN_SLIDER, Constants.MAX_SLIDER))), skin);
        starBrightness = new OwnSlider(Constants.MIN_SLIDER, Constants.MAX_SLIDER, 1, false, skin);
        starBrightness.setName("star brightness");
        starBrightness.setWidth(sliderWidth);
        starBrightness.setValue((float) MathUtilsd.lint(GlobalConf.scene.STAR_BRIGHTNESS, Constants.MIN_STAR_BRIGHT, Constants.MAX_STAR_BRIGHT, Constants.MIN_SLIDER, Constants.MAX_SLIDER));
        starBrightness.addListener(event -> {
            if (event instanceof ChangeEvent && hackProgrammaticChangeEvents) {
                EventManager.instance.post(Events.STAR_BRIGHTNESS_CMD, MathUtilsd.lint(starBrightness.getValue(), Constants.MIN_SLIDER, Constants.MAX_SLIDER, Constants.MIN_STAR_BRIGHT, Constants.MAX_STAR_BRIGHT), true);
                starbrightnessl.setText(Integer.toString((int) starBrightness.getValue()));
                return true;
            }
            return false;
        });
        HorizontalGroup sbrightnessGroup = new HorizontalGroup();
        sbrightnessGroup.space(space3);
        sbrightnessGroup.addActor(starBrightness);
        sbrightnessGroup.addActor(starbrightnessl);

        /** Star size **/
        Label sizeLabel = new Label(txt("gui.star.size"), skin, "default");
        size = new OwnLabel(Integer.toString((int) (MathUtilsd.lint(GlobalConf.scene.STAR_POINT_SIZE, Constants.MIN_STAR_POINT_SIZE, Constants.MAX_STAR_POINT_SIZE, Constants.MIN_SLIDER, Constants.MAX_SLIDER))), skin);
        starSize = new OwnSlider(Constants.MIN_SLIDER, Constants.MAX_SLIDER, 1, false, skin);
        starSize.setName("star size");
        starSize.setWidth(sliderWidth);
        starSize.setValue(MathUtilsd.lint(GlobalConf.scene.STAR_POINT_SIZE, Constants.MIN_STAR_POINT_SIZE, Constants.MAX_STAR_POINT_SIZE, Constants.MIN_SLIDER, Constants.MAX_SLIDER));
        starSize.addListener(event -> {
            if (flag && event instanceof ChangeEvent) {
                EventManager.instance.post(Events.STAR_POINT_SIZE_CMD, MathUtilsd.lint(starSize.getValue(), Constants.MIN_SLIDER, Constants.MAX_SLIDER, Constants.MIN_STAR_POINT_SIZE, Constants.MAX_STAR_POINT_SIZE), true);
                size.setText(Integer.toString((int) starSize.getValue()));
                return true;
            }
            return false;
        });
        HorizontalGroup sizeGroup = new HorizontalGroup();
        sizeGroup.space(space3);
        sizeGroup.addActor(starSize);
        sizeGroup.addActor(size);

        /** Star opacity **/
        Label opacityLabel = new Label(txt("gui.star.opacity"), skin, "default");
        opacity = new OwnLabel(Integer.toString((int) (MathUtilsd.lint(GlobalConf.scene.POINT_ALPHA_MIN, Constants.MIN_STAR_MIN_OPACITY, Constants.MAX_STAR_MIN_OPACITY, Constants.MIN_SLIDER, Constants.MAX_SLIDER))), skin);
        starOpacity = new OwnSlider(Constants.MIN_SLIDER, Constants.MAX_SLIDER, 1, false, skin);
        starOpacity.setName("star opacity");
        starOpacity.setWidth(sliderWidth);
        starOpacity.setValue(MathUtilsd.lint(GlobalConf.scene.POINT_ALPHA_MIN, Constants.MIN_STAR_MIN_OPACITY, Constants.MAX_STAR_MIN_OPACITY, Constants.MIN_SLIDER, Constants.MAX_SLIDER));
        starOpacity.addListener(event -> {
            if (event instanceof ChangeEvent && hackProgrammaticChangeEvents) {
                EventManager.instance.post(Events.STAR_MIN_OPACITY_CMD, MathUtilsd.lint(starOpacity.getValue(), Constants.MIN_SLIDER, Constants.MAX_SLIDER, Constants.MIN_STAR_MIN_OPACITY, Constants.MAX_STAR_MIN_OPACITY), true);
                opacity.setText(Integer.toString((int) starOpacity.getValue()));
                return true;
            }
            return false;
        });
        HorizontalGroup opacityGroup = new HorizontalGroup();
        opacityGroup.space(space3);
        opacityGroup.addActor(starOpacity);
        opacityGroup.addActor(opacity);

        /** Ambient light **/
        Label ambientLightLabel = new Label(txt("gui.light.ambient"), skin, "default");
        ambient = new OwnLabel(Integer.toString((int) (GlobalConf.scene.AMBIENT_LIGHT * 100)), skin);
        ambientLight = new OwnSlider(Constants.MIN_SLIDER, Constants.MAX_SLIDER, 1, false, skin);
        ambientLight.setName("ambient light");
        ambientLight.setWidth(sliderWidth);
        ambientLight.setValue((float) GlobalConf.scene.AMBIENT_LIGHT * 100);
        ambientLight.addListener(event -> {
            if (event instanceof ChangeEvent) {
                EventManager.instance.post(Events.AMBIENT_LIGHT_CMD, ambientLight.getValue() / 100f);
                ambient.setText(Integer.toString((int) ambientLight.getValue()));
                return true;
            }
            return false;
        });
        HorizontalGroup ambientGroup = new HorizontalGroup();
        ambientGroup.space(space3);
        ambientGroup.addActor(ambientLight);
        ambientGroup.addActor(ambient);

        /** Label size **/
        Label labelSizeLabel = new Label(txt("gui.label.size"), skin, "default");
        labels = new OwnLabel(Integer.toString((int) MathUtilsd.lint(GlobalConf.scene.LABEL_SIZE_FACTOR, Constants.MIN_LABEL_SIZE, Constants.MAX_LABEL_SIZE, Constants.MIN_SLIDER, Constants.MAX_SLIDER)), skin);
        labelSize = new OwnSlider(Constants.MIN_SLIDER, Constants.MAX_SLIDER, 1, false, skin);
        labelSize.setName("label size");
        labelSize.setWidth(sliderWidth);
        labelSize.setValue(MathUtilsd.lint(GlobalConf.scene.LABEL_SIZE_FACTOR, Constants.MIN_LABEL_SIZE, Constants.MAX_LABEL_SIZE, Constants.MIN_SLIDER, Constants.MAX_SLIDER));
        labelSize.addListener(event -> {
            if (event instanceof ChangeEvent) {
                float val = MathUtilsd.lint(labelSize.getValue(), Constants.MIN_SLIDER, Constants.MAX_SLIDER, Constants.MIN_LABEL_SIZE, Constants.MAX_LABEL_SIZE);
                EventManager.instance.post(Events.LABEL_SIZE_CMD, val, true);
                labels.setText(Integer.toString((int) labelSize.getValue()));
                return true;
            }
            return false;
        });
        HorizontalGroup labelSizeGroup = new HorizontalGroup();
        labelSizeGroup.space(space3);
        labelSizeGroup.addActor(labelSize);
        labelSizeGroup.addActor(labels);

        VerticalGroup lightingGroup = new VerticalGroup().align(Align.left).columnAlign(Align.left);
        lightingGroup.space(space2);
        lightingGroup.addActor(sbrightnessLabel);
        lightingGroup.addActor(sbrightnessGroup);
        lightingGroup.addActor(new Separator(skin));
        lightingGroup.addActor(sizeLabel);
        lightingGroup.addActor(sizeGroup);
        lightingGroup.addActor(new Separator(skin));
        lightingGroup.addActor(opacityLabel);
        lightingGroup.addActor(opacityGroup);
        lightingGroup.addActor(new Separator(skin));
        lightingGroup.addActor(ambientLightLabel);
        lightingGroup.addActor(ambientGroup);
        lightingGroup.addActor(new Separator(skin));
        lightingGroup.addActor(labelSizeLabel);
        lightingGroup.addActor(labelSizeGroup);

        component = lightingGroup;

        EventManager.instance.subscribe(this, Events.STAR_POINT_SIZE_CMD, Events.STAR_BRIGHTNESS_CMD, Events.LIGHT_SCATTERING_CMD, Events.STAR_MIN_OPACITY_CMD);
    }

    @Override
    public void notify(Events event, Object... data) {
        switch (event) {
        case STAR_POINT_SIZE_CMD:
            if (!(boolean) data[1]) {
                flag = false;
                float newsize = MathUtilsd.lint((float) data[0], Constants.MIN_STAR_POINT_SIZE, Constants.MAX_STAR_POINT_SIZE, Constants.MIN_SLIDER, Constants.MAX_SLIDER);
                starSize.setValue(newsize);
                size.setText(Integer.toString((int) starSize.getValue()));
                flag = true;
            }
            break;
        case STAR_BRIGHTNESS_CMD:
            if (!(boolean) data[1]) {
                Float brightness = (Float) data[0];
                float sliderValue = MathUtilsd.lint(brightness, Constants.MIN_STAR_BRIGHT, Constants.MAX_STAR_BRIGHT, Constants.MIN_SLIDER, Constants.MAX_SLIDER);
                hackProgrammaticChangeEvents = false;
                starBrightness.setValue(sliderValue);
                starbrightnessl.setText(Integer.toString((int) sliderValue));
                hackProgrammaticChangeEvents = true;
            }
            break;
        case STAR_MIN_OPACITY_CMD:
            if (!(boolean) data[1]) {
                Float minopacity = (Float) data[0];
                float sliderValue = MathUtilsd.lint(minopacity, Constants.MIN_STAR_MIN_OPACITY, Constants.MAX_STAR_MIN_OPACITY, Constants.MIN_SLIDER, Constants.MAX_SLIDER);
                hackProgrammaticChangeEvents = false;
                starOpacity.setValue(sliderValue);
                opacity.setText(Integer.toString((int) sliderValue));
                hackProgrammaticChangeEvents = true;
            }
            break;
        default:
            break;
        }

    }

}
