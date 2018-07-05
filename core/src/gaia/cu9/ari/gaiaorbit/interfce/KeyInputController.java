package gaia.cu9.ari.gaiaorbit.interfce;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

import gaia.cu9.ari.gaiaorbit.event.EventManager;
import gaia.cu9.ari.gaiaorbit.event.Events;
import gaia.cu9.ari.gaiaorbit.interfce.KeyBindings.ProgramAction;
import gaia.cu9.ari.gaiaorbit.util.GlobalConf;

/**
 * This input controller connects the input events with the key binding actions
 * 
 * @author tsagrista
 *
 */
public class KeyInputController extends InputAdapter {

    public KeyBindings mappings;
    /** Holds the pressed keys at any moment **/
    public static Set<Integer> pressedKeys;

    public KeyInputController() {
        super();
        pressedKeys = new HashSet<Integer>();
        KeyBindings.initialize();
        mappings = KeyBindings.instance;
    }

    @Override
    public boolean keyDown(int keycode) {
        // Fix leftovers
        if (!Gdx.input.isKeyPressed(KeyBindings.SPECIAL1))
            pressedKeys.remove(KeyBindings.SPECIAL1);

        if (GlobalConf.runtime.INPUT_ENABLED) {
            pressedKeys.add(keycode);
        }
        return false;

    }

    @Override
    public boolean keyUp(int keycode) {
        EventManager.instance.post(Events.INPUT_EVENT, keycode);

        // Fix leftovers
        if (!Gdx.input.isKeyPressed(KeyBindings.SPECIAL1))
            pressedKeys.remove(KeyBindings.SPECIAL1);

        if (GlobalConf.runtime.INPUT_ENABLED) {
            // Use key mappings
            ProgramAction action = mappings.getMappings().get(pressedKeys);
            if (action != null) {
                action.run();
            }
        } else if (keycode == Keys.ESCAPE) {
            // If input is not enabled, only escape works
            Gdx.app.exit();
        }
        pressedKeys.remove(keycode);
        return false;

    }

}
