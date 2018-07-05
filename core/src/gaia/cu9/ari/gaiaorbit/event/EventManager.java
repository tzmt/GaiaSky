package gaia.cu9.ari.gaiaorbit.event;

import java.util.LinkedHashSet;
import java.util.Set;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Keys;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

import gaia.cu9.ari.gaiaorbit.GaiaSky;

/**
 * Event manager that allows for subscription of observers to events (identified
 * by strings), and also for the creation of event objects by anyone.
 * 
 * @author Toni Sagrista
 *
 */
public class EventManager implements IObserver {

    /** Time frame options **/
    public enum TimeFrame {
        /** Real time from the user's perspective **/
        REAL_TIME,
        /** Simulation time in the simulation clock **/
        SIMULATION_TIME;

        public long getCurrentTimeMs() {
            if (this.equals(REAL_TIME)) {
                return TimeUtils.millis();
            } else if (this.equals(SIMULATION_TIME)) {
                return GaiaSky.instance.time.getTime().toEpochMilli();
            }
            return -1;
        }
    }

    /** Singleton pattern **/
    public static final EventManager instance = new EventManager();

    private static final long START = TimeUtils.millis();

    /** Holds a priority queue for each time frame **/
    private ObjectMap<TimeFrame, PriorityQueue<Telegram>> queues;

    /** Telegram pool **/
    private final Pool<Telegram> pool;

    /** Subscriptions Event-Observers **/
    private IntMap<Set<IObserver>> subscriptions = new IntMap<Set<IObserver>>();

    /** The time frame to use if none is specified **/
    private TimeFrame defaultTimeFrame;

    public EventManager() {
        this.pool = new Pool<Telegram>(20) {
            protected Telegram newObject() {
                return new Telegram();
            }
        };
        // Initialize queues, one for each time frame.
        queues = new ObjectMap<TimeFrame, PriorityQueue<Telegram>>(TimeFrame.values().length);
        for (TimeFrame tf : TimeFrame.values()) {
            PriorityQueue<Telegram> pq = new PriorityQueue<Telegram>();
            queues.put(tf, pq);
        }
        defaultTimeFrame = TimeFrame.REAL_TIME;
        subscribe(this, Events.EVENT_TIME_FRAME_CMD);
    }

    /**
     * Subscribes the given observer to the given event types.
     * 
     * @param observer
     *            The observer to subscribe.
     * @param events
     *            The event types to subscribe to.
     */
    public void subscribe(IObserver observer, Events... events) {
        for (Events event : events) {
            subscribe(observer, event);
        }
    }

    /**
     * Registers a listener for the specified message code. Messages without an
     * explicit receiver are broadcasted to all its registered listeners.
     * 
     * @param msg
     *            the message code
     * @param listener
     *            the listener to add
     */
    public void subscribe(IObserver listener, Events msg) {
        synchronized (subscriptions) {
            Set<IObserver> listeners = subscriptions.get(msg.ordinal());
            if (listeners == null) {
                // Associate an empty ordered array with the message code. Sometimes the order matters
                listeners = new LinkedHashSet<IObserver>();
                subscriptions.put(msg.ordinal(), listeners);
            }
            listeners.add(listener);
        }
    }

    public void unsubscribe(IObserver listener, Events... events) {
        for (Events event : events) {
            unsubscribe(listener, event);
        }
    }

    /**
     * Unregister the specified listener for the specified message code.
     * 
     * @param events
     *            The message code.
     * @param listener
     *            The listener to remove.
     **/
    public void unsubscribe(IObserver listener, Events events) {
        synchronized (subscriptions) {
            Set<IObserver> listeners = subscriptions.get(events.ordinal());
            if (listeners != null) {
                listeners.remove(listener);
            }
        }
    }

    /**
     * Unregisters all the subscriptions of the given listeners.
     * 
     * @param listeners
     *            The listeners to remove.
     */
    public void removeAllSubscriptions(IObserver... listeners) {
        synchronized (subscriptions) {
            Keys km = subscriptions.keys();
            while (km.hasNext) {
                int key = km.next();
                for (IObserver listener : listeners) {
                    subscriptions.get(key).remove(listener);
                }
            }
        }
    }

    /**
     * Unregisters all the listeners for the specified message code.
     * 
     * @param msg
     *            the message code
     */
    public void clearSubscriptions(Events msg) {
        synchronized (subscriptions) {
            subscriptions.remove(msg.ordinal());
        }
    }

    /**
     * Posts or registers a new event type with the given data.
     * 
     * @param event
     *            The event type.
     * @param data
     *            The event data.
     */
    public void post(Events event, Object... data) {
        synchronized (subscriptions) {
            Set<IObserver> observers = subscriptions.get(event.ordinal());
            if (observers != null && observers.size() > 0) {
                for (IObserver observer : observers) {
                    observer.notify(event, data);
                }
            }
        }
    }

    /**
     * Posts or registers a new event type with the given data and the default
     * time frame. The default time frame can be changed using the event
     * {@link Events#EVENT_TIME_FRAME_CMD}. The event will be passed along after
     * the specified delay time [ms] in the given time frame has passed.
     * 
     * @param event
     *            The event type.
     * @param delayMs
     *            Milliseconds of delay in the given time frame.
     * @param data
     *            The event data.
     */
    public void postDelayed(Events event, long delayMs, Object... data) {
        if (delayMs <= 0) {
            post(event, data);
        } else {
            Telegram t = pool.obtain();
            t.event = event;
            t.data = data;
            t.timestamp = defaultTimeFrame.getCurrentTimeMs() + delayMs;

            // Add to queue
            queues.get(defaultTimeFrame).add(t);
        }
    }

    /**
     * Posts or registers a new event type with the given data. The event will
     * be passed along after the specified delay time [ms] in the given time
     * frame has passed.
     * 
     * @param event
     *            The event type.
     * @param delayMs
     *            Milliseconds of delay in the given time frame.
     * @param frame
     *            The time frame, either real time (user) or simulation time
     *            (simulation clock time).
     * @param data
     *            The event data.
     */
    public void postDelayed(Events event, long delayMs, TimeFrame frame, Object... data) {
        if (delayMs <= 0) {
            post(event, data);
        } else {
            Telegram t = pool.obtain();
            t.event = event;
            t.data = data;
            t.timestamp = frame.getCurrentTimeMs() + delayMs;

            // Add to queue
            queues.get(frame).add(t);
        }
    }

    /**
     * Returns the current time in milliseconds.
     */
    public static long getCurrentTime() {
        return TimeUtils.millis() - START;
    }

    /**
     * Dispatches any telegrams with a timestamp that has expired. Any
     * dispatched telegrams are removed from the queue.
     * <p>
     * This method must be called each time through the main loop.
     */
    public void dispatchDelayedMessages() {
        for (TimeFrame tf : queues.keys()) {
            dispatch(queues.get(tf), tf.getCurrentTimeMs());
        }
    }

    private void dispatch(PriorityQueue<Telegram> queue, long currentTime) {
        if (queue.size() == 0)
            return;

        // Now peek at the queue to see if any telegrams need dispatching.
        // Remove all telegrams from the front of the queue that have gone
        // past their time stamp.
        do {
            // Read the telegram from the front of the queue
            final Telegram telegram = queue.peek();
            if (telegram.timestamp > currentTime)
                break;

            // Send the telegram to the recipient
            discharge(telegram);

            // Remove it from the queue
            queue.poll();
        } while (queue.size() > 0);
    }

    private void discharge(Telegram telegram) {
        post(telegram.event, telegram.data);
        // Release the telegram to the pool
        pool.free(telegram);
    }

    public boolean hasSubscriptors(Events event) {
        return !subscriptions.get(event.ordinal()).isEmpty();
    }

    @Override
    public void notify(Events event, Object... data) {
        switch (event) {
        case EVENT_TIME_FRAME_CMD:
            defaultTimeFrame = (TimeFrame) data[0];
            break;
        default:
            break;
        }

    }
}
