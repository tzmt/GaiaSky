package gaia.cu9.ari.gaiaorbit.interfce;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import gaia.cu9.ari.gaiaorbit.GaiaSky;
import gaia.cu9.ari.gaiaorbit.event.EventManager;
import gaia.cu9.ari.gaiaorbit.event.Events;
import gaia.cu9.ari.gaiaorbit.scenegraph.IFocus;
import gaia.cu9.ari.gaiaorbit.scenegraph.camera.NaturalCamera;
import gaia.cu9.ari.gaiaorbit.scenegraph.camera.CameraManager.CameraMode;
import gaia.cu9.ari.gaiaorbit.util.Constants;
import gaia.cu9.ari.gaiaorbit.util.GlobalConf;
import gaia.cu9.ari.gaiaorbit.util.comp.ViewAngleComparator;
import gaia.cu9.ari.gaiaorbit.util.math.Vector3d;

/**
 * Input listener for the natural camera.
 * 
 * @author tsagrista
 *
 */
public class NaturalInputListener extends GestureDetector {

    /**
     * The button for rotating the camera either around its center or around the
     * focus.
     */
    public int leftMouseButton = Buttons.LEFT;
    /** The button for panning the camera along the up/right plane */
    public int rightMouseButton = Buttons.RIGHT;
    /** The button for moving the camera along the direction axis */
    public int middleMouseButton = Buttons.MIDDLE;
    /**
     * Whether scrolling requires the activeKey to be pressed (false) or always
     * allow scrolling (true).
     */
    public boolean alwaysScroll = true;
    /** The weight for each scrolled amount. */
    public float scrollFactor = -0.1f;
    /** The key for rolling the camera **/
    public int rollKey = Keys.SHIFT_LEFT;
    /** The natural camera */
    public NaturalCamera camera;
    /** Focus comparator **/
    private Comparator<IFocus> comp;

    /** Holds the pressed keys at any moment **/
    public static Set<Integer> pressedKeys;

    /** The current (first) button being pressed. */
    protected int button = -1;

    private float startX, startY;
    /** Max pixel distance to be considered a click **/
    private float MOVE_PX_DIST;
    /** Max distance from the click to the actual selected star **/
    private int MIN_PIX_DIST;
    private Vector2 gesture = new Vector2();
    private Vector3d aux;

    /** dx(mouse pointer) since last time **/
    private double dragDx;
    /** dy(mouse pointer) since last time **/
    private double dragDy;
    /** Smoothing factor applied in the non-cinematic mode **/
    private double noAccelSmoothing;
    /** Scaling factor applied in the non-cinematic mode **/
    private double noAccelFactor;
    /** Drag vectors **/
    private Vector2 currentDrag, lastDrag;

    /** Save time of last click, in ms */
    private long lastClickTime = -1;
    /** Maximum double click time, in ms **/
    private static final long doubleClickTime = 400;

    protected static class GaiaGestureListener extends GestureAdapter {
        public NaturalInputListener controller;
        private float previousZoom;

        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            previousZoom = 0;
            return false;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {
            return false;
        }

        @Override
        public boolean longPress(float x, float y) {
            return false;
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
            return false;
        }

        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {
            return false;
        }

        @Override
        public boolean zoom(float initialDistance, float distance) {
            float newZoom = distance - initialDistance;
            float amount = newZoom - previousZoom;
            previousZoom = newZoom;
            float w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
            return controller.zoom(amount / ((w > h) ? h : w));
        }

        @Override
        public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
            return false;
        }
    };

    protected final GaiaGestureListener gestureListener;

    protected NaturalInputListener(final GaiaGestureListener gestureListener, NaturalCamera camera) {
        super(gestureListener);
        this.gestureListener = gestureListener;
        this.gestureListener.controller = this;
        this.camera = camera;
        this.aux = new Vector3d();
        this.comp = new ViewAngleComparator<IFocus>();
        // 1% of width
        this.MOVE_PX_DIST = !Constants.mobile ? (float) Math.max(5, Gdx.graphics.getWidth() * 0.01) : (float) Math.max(80, Gdx.graphics.getWidth() * 0.05);
        this.MIN_PIX_DIST = !Constants.mobile ? (int) (5 * GlobalConf.SCALE_FACTOR) : (int) (160 * GlobalConf.SCALE_FACTOR);

        this.dragDx = 0;
        this.dragDy = 0;
        this.noAccelSmoothing = 16.0;
        this.noAccelFactor = 10.0;

        this.currentDrag = new Vector2();
        this.lastDrag = new Vector2();

        pressedKeys = new HashSet<Integer>();
    }

    public NaturalInputListener(final NaturalCamera camera) {
        this(new GaiaGestureListener(), camera);
    }

    private int touched;
    private boolean multiTouch;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (GlobalConf.runtime.INPUT_ENABLED) {
            touched |= (1 << pointer);
            multiTouch = !MathUtils.isPowerOfTwo(touched);
            if (multiTouch)
                this.button = -1;
            else if (this.button < 0) {
                startX = screenX;
                startY = screenY;
                gesture.set(startX, startY);
                this.button = button;
            }
        }
        camera.setInputByController(false);
        return super.touchDown(screenX, screenY, pointer, button);
    }

    private Array<IFocus> getHits(int screenX, int screenY) {
        Array<IFocus> l = GaiaSky.instance.getFocusableEntities();

        Array<IFocus> hits = new Array<IFocus>();

        Iterator<IFocus> it = l.iterator();
        // Add all hits
        while (it.hasNext()) {
            IFocus s = it.next();
            s.addHit(screenX, screenY, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), MIN_PIX_DIST, camera, hits);
        }

        return hits;
    }

    private IFocus getBestHit(int screenX, int screenY) {
        Array<IFocus> hits = getHits(screenX, screenY);
        if (hits.size != 0) {
            // Sort using distance
            hits.sort(comp);
            // Get closest
            return hits.get(hits.size - 1);
        }
        return null;
    }

    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        EventManager.instance.post(Events.INPUT_EVENT, button);
        if (GlobalConf.runtime.INPUT_ENABLED) {
            touched &= -1 ^ (1 << pointer);
            multiTouch = !MathUtils.isPowerOfTwo(touched);
            if (button == this.button && button == Input.Buttons.LEFT) {
                final long currentTime = TimeUtils.millis();
                final long lastLeftTime = lastClickTime;

                Gdx.app.postRunnable(() -> {
                    // 5% of width pixels distance
                    if (!GlobalConf.scene.CINEMATIC_CAMERA || (GlobalConf.scene.CINEMATIC_CAMERA && gesture.dst(screenX, screenY) < MOVE_PX_DIST)) {
                        boolean stopped = camera.stopMovement();
                        boolean focusRemoved = GaiaSky.instance.mainGui != null && GaiaSky.instance.mainGui.cancelTouchFocus();
                        boolean doubleClick = currentTime - lastLeftTime < doubleClickTime;
                        gesture.set(0, 0);

                        if (doubleClick && !stopped && !focusRemoved) {
                            // Select star, if any
                            IFocus hit = getBestHit(screenX, screenY);
                            if (hit != null) {
                                EventManager.instance.post(Events.FOCUS_CHANGE_CMD, hit);
                                EventManager.instance.post(Events.CAMERA_MODE_CMD, CameraMode.Focus);
                            }
                        }
                    }
                });
                dragDx = 0;
                dragDy = 0;
                lastClickTime = currentTime;
            } else if (button == this.button && button == Input.Buttons.RIGHT) {
                // Ensure Octants observed property is computed
                Gdx.app.postRunnable(() -> {
                    // 5% of width pixels distance
                    if (gesture.dst(screenX, screenY) < MOVE_PX_DIST) {
                        // Stop
                        camera.setYaw(0);
                        camera.setPitch(0);

                        // Right click, context menu
                        IFocus hit = getBestHit(screenX, screenY);
                        EventManager.instance.post(Events.POPUP_MENU_FOCUS, hit, screenX, screenY);
                    }
                });
            }

            // Remove keyboard focus from GUI elements
            EventManager.instance.notify(Events.REMOVE_KEYBOARD_FOCUS);

            this.button = -1;
        }
        camera.setInputByController(false);
        return super.touchUp(screenX, screenY, pointer, button);
    }

    protected boolean processDrag(double deltaX, double deltaY, int button) {
        boolean accel = GlobalConf.scene.CINEMATIC_CAMERA;
        if (accel) {
            dragDx = deltaX;
            dragDy = deltaY;
        } else {
            currentDrag.set((float) deltaX, (float) deltaY);
            // Check orientation of last vs current
            if (Math.abs(currentDrag.angle(lastDrag)) > 90) {
                // Reset
                dragDx = 0;
                dragDy = 0;
            }

            dragDx = lowPass(dragDx, deltaX * noAccelFactor, noAccelSmoothing);
            dragDy = lowPass(dragDy, deltaY * noAccelFactor, noAccelSmoothing);
            // Update last drag
            lastDrag.set(currentDrag);
        }

        if (button == leftMouseButton) {
            if (isKeyPressed(rollKey)) {
                // camera.rotate(camera.direction, deltaX * rotateAngle);
                if (dragDx != 0)
                    camera.addRoll(dragDx, accel);
            } else {
                camera.addRotateMovement(dragDx, dragDy, false, accel);
            }
        } else if (button == rightMouseButton) {
            // cam.naturalCamera.addPanMovement(deltaX, deltaY);
            camera.addRotateMovement(dragDx, dragDy, true, accel);
        } else if (button == middleMouseButton) {
            if (dragDx != 0)
                camera.addForwardForce(dragDx);
        }
        camera.setInputByController(false);
        return false;
    }

    private double lowPass(double smoothedValue, double newValue, double smoothing) {
        return smoothedValue + (newValue - smoothedValue) / smoothing;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (GlobalConf.runtime.INPUT_ENABLED) {
            boolean result = super.touchDragged(screenX, screenY, pointer);
            if (result || this.button < 0)
                return result;
            final double deltaX = (screenX - startX) / Gdx.graphics.getWidth();
            final double deltaY = (startY - screenY) / Gdx.graphics.getHeight();
            startX = screenX;
            startY = screenY;
            return processDrag(deltaX, deltaY, button);
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if (GlobalConf.runtime.INPUT_ENABLED) {
            return zoom(amount * scrollFactor);
        }
        return false;
    }

    public boolean zoom(float amount) {
        if (alwaysScroll)
            camera.addForwardForce(amount);
        camera.setInputByController(false);
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (GlobalConf.runtime.INPUT_ENABLED) {
            pressedKeys.add(keycode);
            camera.setInputByController(false);
        }
        return false;

    }

    @Override
    public boolean keyUp(int keycode) {
        pressedKeys.remove(keycode);
        camera.setInputByController(false);
        return false;

    }

    public void updateKeys() {
        if (isKeyPressed(Keys.UP)) {
            camera.addForwardForce(1.0f);
        }
        if (isKeyPressed(Keys.DOWN)) {
            camera.addForwardForce(-1.0f);
        }
        if (isKeyPressed(Keys.RIGHT)) {
            if (camera.getMode().isFocus())
                camera.addHorizontalRotation(-1.0f, true);
            else
                camera.addYaw(1.0f, true);
        }
        if (isKeyPressed(Keys.LEFT)) {
            if (camera.getMode().isFocus())
                camera.addHorizontalRotation(1.0f, true);
            else
                camera.addYaw(-1.0f, true);
        }
    }

    public boolean isKeyPressed(int keycode) {
        return pressedKeys.contains(keycode);
    }

}
