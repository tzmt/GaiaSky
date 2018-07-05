package gaia.cu9.ari.gaiaorbit.interfce;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;

import gaia.cu9.ari.gaiaorbit.event.EventManager;
import gaia.cu9.ari.gaiaorbit.event.Events;
import gaia.cu9.ari.gaiaorbit.util.GlobalConf;
import gaia.cu9.ari.gaiaorbit.util.I18n;
import gaia.cu9.ari.gaiaorbit.util.scene2d.CollapsibleWindow;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnLabel;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnTextButton;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnTextField;

public class DateDialog extends CollapsibleWindow {
    private final Window me;
    private final Stage stage;

    private final TextField day, year, hour, min, sec;
    private final SelectBox<String> month;
    private final TextButton setNow;
    private final Color defaultColor;

    public DateDialog(Stage stage, Skin skin) {
        super(I18n.bundle.get("gui.pickdate"), skin);
        this.me = this;
        this.stage = stage;

        float pad = 5 * GlobalConf.SCALE_FACTOR;

        /** SET NOW **/
        setNow = new OwnTextButton("Set current time (UTC)", skin);
        setNow.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeEvent) {
                    updateTime(Instant.now(), ZoneOffset.UTC);
                    return true;
                }
                return false;
            }
        });
        setNow.setSize(150 * GlobalConf.SCALE_FACTOR, 20 * GlobalConf.SCALE_FACTOR);
        add(setNow).center().colspan(2).padTop(pad);
        row();

        /** DAY GROUP **/
        HorizontalGroup dayGroup = new HorizontalGroup();
        day = new OwnTextField("", skin);
        day.setMaxLength(2);
        day.setWidth(40 * GlobalConf.SCALE_FACTOR);
        day.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof InputEvent) {
                    InputEvent ie = (InputEvent) event;
                    if (ie.getType() == Type.keyTyped) {
                        checkField(day, 1, 31);
                        return true;
                    }
                }
                return false;
            }

        });

        month = new SelectBox<String>(skin);
        month.setItems("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        month.setWidth(40 * GlobalConf.SCALE_FACTOR);

        year = new OwnTextField("", skin);
        year.setMaxLength(5);
        year.setWidth(40 * GlobalConf.SCALE_FACTOR);
        year.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof InputEvent) {
                    InputEvent ie = (InputEvent) event;
                    if (ie.getType() == Type.keyTyped) {
                        checkField(year, -20000, 20000);
                        return true;
                    }
                }
                return false;
            }

        });

        dayGroup.addActor(day);
        dayGroup.addActor(new OwnLabel("/", skin));
        dayGroup.addActor(month);
        dayGroup.addActor(new OwnLabel("/", skin));
        dayGroup.addActor(year);

        add(new OwnLabel(I18n.bundle.get("gui.time.date") + " (dd/MM/yyyy):", skin)).pad(5, 5, 0, 5).right();
        add(dayGroup).pad(pad, 0, 0, pad);
        row();

        /** HOUR GROUP **/
        HorizontalGroup hourGroup = new HorizontalGroup();
        hour = new OwnTextField("", skin);
        hour.setMaxLength(2);
        hour.setWidth(40 * GlobalConf.SCALE_FACTOR);
        hour.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof InputEvent) {
                    InputEvent ie = (InputEvent) event;
                    if (ie.getType() == Type.keyTyped) {
                        checkField(hour, 0, 23);
                        return true;
                    }
                }
                return false;
            }

        });

        min = new OwnTextField("", skin);
        min.setMaxLength(2);
        min.setWidth(40 * GlobalConf.SCALE_FACTOR);
        min.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof InputEvent) {
                    InputEvent ie = (InputEvent) event;
                    if (ie.getType() == Type.keyTyped) {
                        checkField(min, 0, 59);
                        return true;
                    }
                }
                return false;
            }

        });

        sec = new OwnTextField("", skin);
        sec.setMaxLength(2);
        sec.setWidth(40 * GlobalConf.SCALE_FACTOR);
        sec.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof InputEvent) {
                    InputEvent ie = (InputEvent) event;
                    if (ie.getType() == Type.keyTyped) {
                        checkField(sec, 0, 59);
                        return true;
                    }
                }
                return false;
            }

        });

        hourGroup.addActor(hour);
        hourGroup.addActor(new OwnLabel(":", skin));
        hourGroup.addActor(min);
        hourGroup.addActor(new OwnLabel(":", skin));
        hourGroup.addActor(sec);

        add(new OwnLabel(I18n.bundle.get("gui.time.time") + " (hh:mm:ss):", skin)).pad(5, 5, 0, 5).right();
        add(hourGroup).pad(pad, 0, 0, pad);
        row();

        /** BUTTONS **/
        HorizontalGroup buttonGroup = new HorizontalGroup();
        TextButton ok = new OwnTextButton(I18n.bundle.get("gui.ok"), skin, "default");
        ok.setName("close");
        ok.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeEvent) {

                    boolean cool = checkField(day, 1, 31);
                    cool = checkField(year, -20000, 20000) && cool;
                    cool = checkField(hour, 0, 23) && cool;
                    cool = checkField(min, 0, 59) && cool;
                    cool = checkField(sec, 0, 59) && cool;

                    if (cool) {
                        // Set the date
                        LocalDateTime date = LocalDateTime.of(Integer.parseInt(year.getText()), month.getSelectedIndex() + 1, Integer.parseInt(day.getText()), Integer.parseInt(hour.getText()), Integer.parseInt(min.getText()), Integer.parseInt(sec.getText()));

                        // Send time change command
                        EventManager.instance.post(Events.TIME_CHANGE_CMD, date.toInstant(ZoneOffset.UTC));

                        me.remove();
                    }

                    return true;
                }

                return false;
            }

        });
        TextButton cancel = new OwnTextButton(I18n.bundle.get("gui.cancel"), skin, "default");
        cancel.setName("close");
        cancel.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ChangeEvent) {
                    me.remove();
                    return true;
                }

                return false;
            }

        });
        buttonGroup.addActor(ok);
        ok.setSize(70 * GlobalConf.SCALE_FACTOR, 20 * GlobalConf.SCALE_FACTOR);
        buttonGroup.addActor(cancel);
        cancel.setSize(70 * GlobalConf.SCALE_FACTOR, 20 * GlobalConf.SCALE_FACTOR);
        buttonGroup.align(Align.right).space(10);

        add(buttonGroup).colspan(2).pad(pad, 0, 0, 0).bottom().right();
        getTitleTable().align(Align.left);

        pack();

        defaultColor = day.getColor().cpy();

        this.setPosition(Math.round(stage.getWidth() / 2f - this.getWidth() / 2f), Math.round(stage.getHeight() / 2f - this.getHeight() / 2f));
    }

    /**
     * Returns true if all is good
     * 
     * @param f
     *            The text field
     * @param min
     *            The minimum value
     * @param max
     *            The maximum value
     * @return The boolean indicating whether the value in this field is between
     *         min and max
     */
    public boolean checkField(TextField f, int min, int max) {
        try {
            int val = Integer.parseInt(f.getText());
            if (val < min || val > max) {
                f.setColor(1, 0, 0, 1);
                return false;
            }
        } catch (Exception e) {
            f.setColor(1, 0, 0, 1);
            return false;
        }
        f.setColor(defaultColor);
        return true;
    }

    /** Updates the time **/
    public void updateTime(Instant instant, ZoneId zid) {
        LocalDateTime date = LocalDateTime.ofInstant(instant, zid);
        int year = date.get(ChronoField.YEAR_OF_ERA);
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        int hour = date.getHour();
        int min = date.getMinute();
        int sec = date.getSecond();

        this.day.setText(String.valueOf(day));
        this.month.setSelectedIndex(month - 1);
        this.year.setText(String.valueOf(year));
        this.hour.setText(String.valueOf(hour));
        this.min.setText(String.valueOf(min));
        this.sec.setText(String.valueOf(sec));
    }

    public void display() {
        if (!stage.getActors().contains(me, true))
            stage.addActor(me);
    }

}
