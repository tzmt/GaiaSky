package gaia.cu9.ari.gaiaorbit.interfce;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import gaia.cu9.ari.gaiaorbit.event.EventManager;
import gaia.cu9.ari.gaiaorbit.event.Events;
import gaia.cu9.ari.gaiaorbit.event.IObserver;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnLabel;

/**
 * Widget that displays big messages on screen.
 * 
 * @author Toni Sagrista
 *
 */
public class MessagesInterface extends Table implements IObserver, IGuiInterface {
    Label headline, subhead;
    boolean displaying = false;
    /** Lock object for synchronization **/
    private Object lock;

    Map<Integer, Widget> customElements;

    public MessagesInterface(Skin skin, Object lock) {
        super(skin);
        customElements = new HashMap<Integer, Widget>();

        headline = new OwnLabel("", skin, "headline");
        headline.setColor(1, 1, 0, 1);
        subhead = new OwnLabel("", skin, "subhead");
        this.add(headline).left();
        this.row();
        this.add(subhead).left();
        this.lock = lock;
        EventManager.instance.subscribe(this, Events.POST_HEADLINE_MESSAGE, Events.CLEAR_HEADLINE_MESSAGE, Events.POST_SUBHEAD_MESSAGE, Events.CLEAR_SUBHEAD_MESSAGE, Events.CLEAR_MESSAGES);
    }

    private void unsubscribe() {
        EventManager.instance.unsubscribe(this, Events.POST_HEADLINE_MESSAGE, Events.CLEAR_HEADLINE_MESSAGE, Events.POST_SUBHEAD_MESSAGE, Events.CLEAR_SUBHEAD_MESSAGE, Events.CLEAR_MESSAGES);
    }

    @Override
    public void notify(Events event, Object... data) {
        synchronized (lock) {
            switch (event) {
            case POST_HEADLINE_MESSAGE:
                headline.setText((String) data[0]);
                break;
            case CLEAR_HEADLINE_MESSAGE:
                headline.setText("");
                break;
            case POST_SUBHEAD_MESSAGE:
                subhead.setText((String) data[0]);
                break;
            case CLEAR_SUBHEAD_MESSAGE:
                subhead.setText("");
                break;
            case CLEAR_MESSAGES:
                headline.setText("");
                subhead.setText("");
                break;
            default:
                break;
            }
        }
    }

    @Override
    public void dispose() {
        unsubscribe();
    }

}
