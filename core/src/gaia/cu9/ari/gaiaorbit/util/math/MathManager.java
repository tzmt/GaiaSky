package gaia.cu9.ari.gaiaorbit.util.math;

import gaia.cu9.ari.gaiaorbit.event.EventManager;
import gaia.cu9.ari.gaiaorbit.event.Events;
import gaia.cu9.ari.gaiaorbit.event.IObserver;
import gaia.cu9.ari.gaiaorbit.util.GlobalConf;

public class MathManager implements IObserver {

    public static MathManager instance;

    public static void initialize() {
        if (instance == null)
            instance = new MathManager();
    }

    public ITrigonometry trigo;

    private Trigonometry trigonometry;
    private FastTrigonometry fastTrigonometry;

    MathManager() {
        trigonometry = new Trigonometry();
        fastTrigonometry = new FastTrigonometry();

        trigo = GlobalConf.data.HIGH_ACCURACY_POSITIONS ? trigonometry : fastTrigonometry;

        EventManager.instance.subscribe(this, Events.HIGH_ACCURACY_CMD);
    }

    @Override
    public void notify(Events event, Object... data) {
        switch (event) {
        case HIGH_ACCURACY_CMD:
            boolean highAcc = (Boolean) data[0];
            trigo = highAcc ? trigonometry : fastTrigonometry;
            break;
        default:
            break;
        }

    }

}
