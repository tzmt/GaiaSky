package gaia.cu9.ari.gaiaorbit.interfce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import gaia.cu9.ari.gaiaorbit.event.EventManager;
import gaia.cu9.ari.gaiaorbit.event.Events;
import gaia.cu9.ari.gaiaorbit.util.GlobalConf;
import gaia.cu9.ari.gaiaorbit.util.GlobalResources;
import gaia.cu9.ari.gaiaorbit.util.I18n;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnTextIconButton;

/**
 * Displays the loading screen.
 * 
 * @author Toni Sagrista
 *
 */
public class LoadingGui extends AbstractGui {
    protected Table center, bottom;
    protected Container<Button> screenMode;

    public NotificationsInterface notificationsInterface;

    public LoadingGui() {
        super();
    }

    @Override
    public void initialize(AssetManager assetManager) {
        interfaces = new Array<IGuiInterface>();
        float pad30 = 30 * GlobalConf.SCALE_FACTOR;
        float pad10 = 10 * GlobalConf.SCALE_FACTOR;
        // User interface
        ui = new Stage(new ScreenViewport(), GlobalResources.spriteBatch);
        skin = GlobalResources.skin;

        center = new Table();
        center.setFillParent(true);
        center.center();

        FileHandle gslogo = Gdx.files.internal("img/gaiasky-logo.png");
        Texture logotex = new Texture(gslogo);
        logotex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        Image logoimg = new Image(logotex);
        float scl = GlobalConf.SCALE_FACTOR / 2.5f;
        logoimg.setScale(scl);
        logoimg.setOrigin(Align.center);

        center.add(logoimg).center();
        center.row().padBottom(pad30);
        center.add(new Label(I18n.bundle.get("notif.loading.wait"), skin, "header"));
        center.row();

        // SCREEN MODE BUTTON - TOP RIGHT
        screenMode = new Container<Button>();
        screenMode.setFillParent(true);
        screenMode.top().right();
        screenMode.pad(pad10);
        Image smImg = new Image(skin.getDrawable("screen-mode"));
        OwnTextIconButton screenModeButton = new OwnTextIconButton("", smImg, skin);
        screenModeButton.setCursor(GlobalResources.linkCursor);
        screenModeButton.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeEvent) {
                    GlobalConf.screen.FULLSCREEN = !GlobalConf.screen.FULLSCREEN;
                    EventManager.instance.post(Events.SCREEN_MODE_CMD);
                    return true;
                }
                return false;
            }
        });
        screenMode.setActor(screenModeButton);

        // MESSAGE INTERFACE - BOTTOM
        notificationsInterface = new NotificationsInterface(skin, lock, false, false, false, false);
        center.add(notificationsInterface);
        interfaces.add(notificationsInterface);

        rebuildGui();

    }

    @Override
    public void doneLoading(AssetManager assetManager) {
    }

    public void rebuildGui() {
        if (ui != null) {
            ui.clear();
            ui.addActor(screenMode);
            ui.addActor(center);
        }
    }

}
