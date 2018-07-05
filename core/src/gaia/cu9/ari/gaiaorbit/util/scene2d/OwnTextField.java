package gaia.cu9.ari.gaiaorbit.util.scene2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent;

import gaia.cu9.ari.gaiaorbit.util.validator.IValidator;

/**
 * TextButton in which the cursor changes when the mouse rolls over.
 * It also fixes the size issue.
 * @author Toni Sagrista
 *
 */
public class OwnTextField extends TextField {

    private float ownwidth = 0f, ownheight = 0f;
    private IValidator validator = null;
    private String lastCorrectText = "";
    private Color regularColor;
    private Color errorColor;

    public OwnTextField(String text, Skin skin) {
        super(text, skin);
    }

    public OwnTextField(String text, Skin skin, IValidator validator) {
        this(text, skin);
        this.validator = validator;
        initValidator();
    }

    public OwnTextField(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public OwnTextField(String text, Skin skin, String styleName, IValidator validator) {
        this(text, skin, styleName);
        this.validator = validator;
        initValidator();
    }

    public OwnTextField(String text, TextFieldStyle style) {
        super(text, style);
    }

    public OwnTextField(String text, TextFieldStyle style, IValidator validator) {
        this(text, style);
        this.validator = validator;
        initValidator();
    }

    public void setErrorColor(Color errorColor) {
        this.errorColor = errorColor;
    }

    private void initValidator() {
        if (validator != null) {
            errorColor = new Color(0xff6666ff);
            regularColor = getColor().cpy();
            addListener(new EventListener() {
                @Override
                public boolean handle(Event event) {
                    if (event instanceof ChangeEvent) {
                        String str = getText();
                        if (validator.validate(str)) {
                            setColor(regularColor);
                            lastCorrectText = str;
                        } else {
                            setColor(errorColor);
                        }
                        return true;
                    } else if (event instanceof FocusEvent) {
                        if (!((FocusEvent) event).isFocused()) {
                            // We lost focus, return to last correct text if current not valid
                            String str = getText();
                            if (!validator.validate(str)) {
                                setText(lastCorrectText);
                                setColor(regularColor);
                            }

                        }
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void setWidth(float width) {
        ownwidth = width;
        super.setWidth(width);
    }

    @Override
    public void setHeight(float height) {
        ownheight = height;
        super.setHeight(height);
    }

    @Override
    public void setSize(float width, float height) {
        ownwidth = width;
        ownheight = height;
        super.setSize(width, height);
    }

    @Override
    public float getPrefWidth() {
        if (ownwidth != 0) {
            return ownwidth;
        } else {
            return super.getPrefWidth();
        }
    }

    @Override
    public float getPrefHeight() {
        if (ownheight != 0) {
            return ownheight;
        } else {
            return super.getPrefHeight();
        }
    }

}