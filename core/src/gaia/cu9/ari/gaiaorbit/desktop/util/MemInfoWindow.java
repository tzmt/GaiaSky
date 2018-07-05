package gaia.cu9.ari.gaiaorbit.desktop.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.utils.Align;

import gaia.cu9.ari.gaiaorbit.interfce.GenericDialog;
import gaia.cu9.ari.gaiaorbit.util.GlobalConf;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnScrollPane;
import gaia.cu9.ari.gaiaorbit.util.scene2d.OwnTextArea;

public class MemInfoWindow extends GenericDialog {

    private OwnScrollPane meminfoscroll;

    public MemInfoWindow(Stage stg, Skin skin) {
        super(txt("gui.help.meminfo"), skin, stg);

        setCancelText(txt("gui.close"));

        // Build
        buildSuper();

    }

    @Override
    protected void build() {
        float pad = 5 * GlobalConf.SCALE_FACTOR;
        float tawidth = 500 * GlobalConf.SCALE_FACTOR;

        String meminfostr = "";
        for (MemoryPoolMXBean mpBean : ManagementFactory.getMemoryPoolMXBeans()) {
            meminfostr += txt("gui.help.name") + ": " + mpBean.getName() + ": " + mpBean.getUsage() + "\n";
        }

        TextArea meminfo = new OwnTextArea(meminfostr, skin, "no-disabled");
        meminfo.setDisabled(true);
        meminfo.setPrefRows(10);
        meminfo.setWidth(tawidth);
        meminfo.clearListeners();

        meminfoscroll = new OwnScrollPane(meminfo, skin, "minimalist-nobg");
        meminfoscroll.setWidth(tawidth);
        meminfoscroll.setForceScroll(false, true);
        meminfoscroll.setSmoothScrolling(true);
        meminfoscroll.setFadeScrollBars(false);

        add(meminfoscroll).align(Align.center).pad(pad);

    }

    @Override
    protected void accept() {
    }

    @Override
    protected void cancel() {
    }

}
