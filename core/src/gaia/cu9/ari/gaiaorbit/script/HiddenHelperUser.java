package gaia.cu9.ari.gaiaorbit.script;

import java.util.concurrent.atomic.AtomicBoolean;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import gaia.cu9.ari.gaiaorbit.GaiaSky;
import gaia.cu9.ari.gaiaorbit.event.EventManager;
import gaia.cu9.ari.gaiaorbit.event.Events;
import gaia.cu9.ari.gaiaorbit.event.IObserver;
import gaia.cu9.ari.gaiaorbit.scenegraph.CelestialBody;
import gaia.cu9.ari.gaiaorbit.scenegraph.IFocus;

/**
 * This guy implements high level operations which run concurrently to the main
 * thread by starting new threads
 * 
 * @author tsagrista
 *
 */
public class HiddenHelperUser implements IObserver {

    private static HiddenHelperUser hhu;

    public static HiddenHelperUser instance() {
        if (hhu == null)
            hhu = new HiddenHelperUser();
        return hhu;
    }

    public static void initialize() {
        instance();
    }

    private Array<HelperTask> currentTasks;

    private long lastCommandTime;

    private HiddenHelperUser() {
        super();
        currentTasks = new Array<HelperTask>(5);
        lastCommandTime = -1;
        EventManager.instance.subscribe(this, Events.NAVIGATE_TO_OBJECT, Events.LAND_ON_OBJECT, Events.LAND_AT_LOCATION_OF_OBJECT, Events.INPUT_EVENT);
    }

    @Override
    public void notify(Events event, Object... data) {
        switch (event) {
        case NAVIGATE_TO_OBJECT:
            IFocus body;
            if (data[0] instanceof String)
                body = GaiaSky.instance.sg.findFocus((String) data[0]);
            else
                body = ((IFocus) data[0]);

            // final double angle = body.getRadius() * 10 * Constants.U_TO_KM;

            GoToObjectTask gotoTask = new GoToObjectTask(body, currentTasks);
            Thread gotoT = new Thread(gotoTask);
            gotoT.start();
            currentTasks.add(gotoTask);
            lastCommandTime = TimeUtils.millis();
            break;
        case LAND_ON_OBJECT:
            if (data[0] instanceof String)
                body = GaiaSky.instance.sg.findFocus((String) data[0]);
            else
                body = ((CelestialBody) data[0]);

            LandOnObjectTask landOnTask = new LandOnObjectTask(body, currentTasks);
            Thread landonT = new Thread(landOnTask);
            landonT.start();
            currentTasks.add(landOnTask);
            lastCommandTime = TimeUtils.millis();

            break;
        case LAND_AT_LOCATION_OF_OBJECT:
            if (data[0] instanceof String)
                body = GaiaSky.instance.sg.findFocus((String) data[0]);
            else
                body = ((CelestialBody) data[0]);

            HelperTask landAtTask = null;
            if (data[1] instanceof String) {
                String locname = (String) data[1];
                landAtTask = new LandOnLocationTask(body, locname, currentTasks);
            } else {
                Double lon = (Double) data[1];
                Double lat = (Double) data[2];
                landAtTask = new LandOnLocationTask(body, lon, lat, currentTasks);

            }
            Thread landAtLoc = new Thread(landAtTask);
            landAtLoc.start();
            currentTasks.add(landAtTask);
            lastCommandTime = TimeUtils.millis();
            break;
        case INPUT_EVENT:
            // More than one second after the command is given to be able to
            // stop
            if (TimeUtils.millis() - lastCommandTime > 1000) {

                // Stop all current threads
                for (HelperTask tsk : currentTasks) {
                    tsk.stop();
                }
                currentTasks.clear();
            }
            break;
        default:
            break;
        }

    }

    private abstract class HelperTask implements Runnable {
        protected AtomicBoolean stop;
        protected Array<HelperTask> currentTasks;

        protected HelperTask(Array<HelperTask> currentTasks) {
            this.stop = new AtomicBoolean(false);
            this.currentTasks = currentTasks;
        }

        public void stop() {
            this.stop.set(true);
        }
    }

    private class GoToObjectTask extends HelperTask {
        IFocus body;

        public GoToObjectTask(IFocus body, Array<HelperTask> currentTasks) {
            super(currentTasks);
            this.body = body;

        }

        @Override
        public void run() {
            EventScriptingInterface.instance().goToObject(body, 20, 1, stop);
            currentTasks.removeValue(this, true);
        }

    }

    private class LandOnObjectTask extends HelperTask {
        IFocus body;

        public LandOnObjectTask(IFocus body, Array<HelperTask> currentTasks) {
            super(currentTasks);
            this.body = body;
        }

        @Override
        public void run() {
            EventScriptingInterface.instance().landOnObject(body, stop);
            currentTasks.removeValue(this, true);
        }

    }

    private class LandOnLocationTask extends HelperTask {
        IFocus body;
        String locName;
        Double lon, lat;

        public LandOnLocationTask(IFocus body, String locName, Array<HelperTask> currentTasks) {
            super(currentTasks);
            this.body = body;
            this.locName = locName;
        }

        public LandOnLocationTask(IFocus body, double lon, double lat, Array<HelperTask> currentTasks) {
            super(currentTasks);
            this.body = body;
            this.lon = lon;
            this.lat = lat;
        }

        @Override
        public void run() {
            if (locName == null)
                EventScriptingInterface.instance().landOnObjectLocation(body, lon, lat, stop);
            else
                EventScriptingInterface.instance().landOnObjectLocation(body, locName, stop);
            currentTasks.removeValue(this, true);
        }

    }

}
