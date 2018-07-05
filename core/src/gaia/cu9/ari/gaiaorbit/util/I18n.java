package gaia.cu9.ari.gaiaorbit.util;

import java.util.Locale;
import java.util.MissingResourceException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;

/**
 * Manages the i18n system.
 * @author Toni Sagrista
 *
 */
public class I18n {

    public static I18NBundle bundle;
    public static Locale locale;

    /**
     * Initialises the i18n system.
     */
    public static void initialize() {
        if (bundle == null) {
            forceinit(Gdx.files.internal("i18n/gsbundle"));
        }
    }

    public static void initialize(FileHandle fh) {
        forceinit(fh);
    }

    public static boolean forceinit(FileHandle baseFileHandle) {
        if (GlobalConf.program == null || GlobalConf.program.LOCALE.isEmpty()) {
            // Use system default
            locale = Locale.getDefault();
        } else {
            locale = forLanguageTag(GlobalConf.program.LOCALE);
        }
        try {
            bundle = I18NBundle.createBundle(baseFileHandle, locale);
            return true;
        } catch (MissingResourceException e) {
            Logger.info(I18n.class.getSimpleName(), e.getLocalizedMessage());
            // Use default locale - en_GB
            locale = new Locale("en", "GB");
            try {
                bundle = I18NBundle.createBundle(baseFileHandle, locale);
            } catch (Exception e2) {
                Logger.error(e, I18n.class.getSimpleName());
            }
            return false;
        }

    }

    public static Locale forLanguageTag(String languageTag) {
        String[] tags = languageTag.split("-");
        if (tags.length > 1) {
            return new Locale(tags[0], tags[1]);
        } else {
            return new Locale(languageTag);
        }
    }

}
