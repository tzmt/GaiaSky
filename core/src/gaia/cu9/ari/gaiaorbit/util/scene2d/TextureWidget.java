package gaia.cu9.ari.gaiaorbit.util.scene2d;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class TextureWidget extends Widget {

    private FrameBuffer fb;
    private float width, height;
    public TextureWidget(FrameBuffer fb) {
        super();
        this.fb = fb;
        this.width = fb.getWidth();
        this.height = fb.getHeight();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (fb != null) {
            batch.draw(fb.getColorBufferTexture(), getX(), getY(), width, height);
        }
    }

    @Override
    public float getMinWidth() {
        return width;
    }

    @Override
    public float getMinHeight() {
        return height;
    }

    @Override
    public float getPrefWidth() {
        return width;
    }

    @Override
    public float getPrefHeight() {
        return height;
    }

    @Override
    public float getMaxWidth() {
        return width;
    }

    @Override
    public float getMaxHeight() {
        return height;
    }

}
