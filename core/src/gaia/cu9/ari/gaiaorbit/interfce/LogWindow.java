package gaia.cu9.ari.gaiaorbit.interfce;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.Instant;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;

import gaia.cu9.ari.gaiaorbit.util.GlobalConf;
import gaia.cu9.ari.gaiaorbit.util.I18n;
import gaia.cu9.ari.gaiaorbit.util.ISysUtils;
import gaia.cu9.ari.gaiaorbit.util.Logger;
import gaia.cu9.ari.gaiaorbit.util.SysUtilsFactory;
import gaia.cu9.ari.gaiaorbit.util.format.DateFormatFactory;
import gaia.cu9.ari.gaiaorbit.util.format.DateFormatFactory.DateType;
import gaia.cu9.ari.gaiaorbit.util.format.IDateFormat;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnLabel;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnScrollPane;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnTextButton;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnTextIconButton;

public class LogWindow extends GenericDialog {

    private IDateFormat format;
    private Table logs;
    private ScrollPane scroll;

    // Current number of messages in window
    private int nmsg = 0;

    private float w, h, pad;

    public LogWindow(Stage stage, Skin skin) {
        super(txt("gui.log.title"), skin, stage);

        this.format = DateFormatFactory.getFormatter(I18n.locale, DateType.DATETIME);

        setCancelText(txt("gui.close"));

        // Build
        buildSuper();

    }

    @Override
    protected void build() {
        w = 500 * GlobalConf.SCALE_FACTOR;
        h = 500 * GlobalConf.SCALE_FACTOR;
        pad = 10 * GlobalConf.SCALE_FACTOR;

        logs = new Table(skin);
        List<MessageBean> list = NotificationsInterface.historical;
        for (MessageBean mb : list) {
            addMessage(mb);
        }

        scroll = new OwnScrollPane(logs, skin, "minimalist-nobg");
        scroll.setFadeScrollBars(true);
        scroll.setScrollingDisabled(false, false);
        scroll.setSmoothScrolling(true);
        scroll.setHeight(h);
        scroll.setWidth(w);
        scroll.pack();
        updateScroll();

        HorizontalGroup buttons = new HorizontalGroup();
        buttons.pad(pad);
        buttons.space(pad);

        Image reloadImg = new Image(skin.getDrawable("reload"));
        Button reload = new OwnTextIconButton("", reloadImg, skin);
        reload.setName("update log");
        reload.addListener(new TextTooltip(txt("gui.log.update"), skin));
        reload.addListener((event) -> {
            if (event instanceof ChangeEvent) {
                update();
            }
            return false;
        });
        buttons.addActor(reload);

        Button export = new OwnTextButton(txt("gui.log.export"), skin);
        export.setName("export log");
        export.addListener((event) -> {
            if (event instanceof ChangeEvent) {
                export();
            }
            return false;
        });
        buttons.addActor(export);

        content.add(scroll).padBottom(pad).row();
        content.add(buttons).align(Align.center);
    }

    public void update() {
        if (logs != null) {
            List<MessageBean> list = NotificationsInterface.historical;
            if (list.size() > nmsg) {
                for (int i = nmsg; i < list.size(); i++) {
                    addMessage(list.get(i));
                }
            }
            updateScroll();
        }
    }

    public void export() {
        String filename = Instant.now().toString() + "_gaiasky.log";
        ISysUtils su = SysUtilsFactory.getSysUtils();
        File gshome = su.getGSHomeDir();
        File log = new File(gshome, filename);

        try {
            FileWriter fw = new FileWriter(log);
            BufferedWriter bw = new BufferedWriter(fw);
            for (MessageBean mb : NotificationsInterface.historical) {
                fw.write(format.format(mb.date) + " - " + mb.msg + '\n');
            }
            bw.flush();
            bw.close();
            Logger.info("Log file written to " + log.getAbsolutePath());
        } catch (Exception e) {
            Logger.error(e, this.getClass().getSimpleName());
        }

    }

    public void updateScroll() {
        scroll.setScrollPercentX(0);
        scroll.setScrollPercentY(1);
        scroll.invalidate();
    }

    private void addMessage(MessageBean mb) {
        Label date = new OwnLabel(format.format(mb.date), skin);
        Label msg = new OwnLabel(mb.msg, skin);
        logs.add(date).left().padRight(pad);
        logs.add(msg).left().row();
        nmsg++;
    }

    @Override
    protected void accept() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void cancel() {
        // TODO Auto-generated method stub

    }

}
