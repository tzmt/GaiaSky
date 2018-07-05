package gaia.cu9.ari.gaiaorbit.interfce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import gaia.cu9.ari.gaiaorbit.event.EventManager;
import gaia.cu9.ari.gaiaorbit.event.Events;
import gaia.cu9.ari.gaiaorbit.event.IObserver;
import gaia.cu9.ari.gaiaorbit.render.ComponentType;
import gaia.cu9.ari.gaiaorbit.scenegraph.ISceneGraph;
import gaia.cu9.ari.gaiaorbit.util.ComponentTypes;
import gaia.cu9.ari.gaiaorbit.util.I18n;

/**
 * Provides general methods and attributes that all GUIs should have
 * 
 * @author tsagrista
 *
 */
public abstract class AbstractGui implements IObserver, IGui {

    /**
     * The user interface stage
     */
    protected Stage ui;
    /**
     * The skin to use
     */
    protected Skin skin;
    /**
     * The GUI interfaces, if any
     */
    protected Array<IGuiInterface> interfaces;

    /**
     * The name of this GUI
     */
    protected String name;

    /** Lock for sync **/
    protected Object lock;

    public AbstractGui() {
        lock = new Object();
        name = this.getClass().getSimpleName();
    }

    @Override
    public void update(double dt) {
        ui.act((float) dt);
    }

    @Override
    public void render(int rw, int rh) {
        synchronized (lock) {
            ui.draw();
        }
    }

    @Override
    public Stage getGuiStage() {
        return ui;
    }

    public String getName() {
        return name;
    }

    @Override
    public void dispose() {
        if (interfaces != null)
            for (IGuiInterface iface : interfaces)
                iface.dispose();

        if (ui != null)
            ui.dispose();
        EventManager.instance.removeAllSubscriptions(this);
    }

    @Override
    public void resize(final int width, final int height) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                resizeImmediate(width, height);
            }
        });
    }

    @Override
    public void resizeImmediate(final int width, final int height) {
        ui.getViewport().update(width, height, true);
        rebuildGui();
    }

    /**
     * Adds the already created GUI objects to the stage.
     */
    protected abstract void rebuildGui();

    @Override
    public boolean cancelTouchFocus() {
        if (ui.getKeyboardFocus() != null || ui.getScrollFocus() != null) {
            ui.setScrollFocus(null);
            ui.setKeyboardFocus(null);
            return true;
        }
        return false;
    }

    @Override
    public Actor findActor(String name) {
        return ui.getRoot().findActor(name);
    }

    protected String txt(String key) {
        return I18n.bundle.get(key);
    }

    protected String txt(String key, Object... params) {
        return I18n.bundle.format(key, params);
    }

    @Override
    public void setVisibilityToggles(ComponentType[] entities, ComponentTypes visible) {
        // Empty by default
    }

    @Override
    public void setSceneGraph(ISceneGraph sg) {
        // Empty by default
    }

    @Override
    public void notify(Events event, Object... data) {
        // Empty by default
    }
}
