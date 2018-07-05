package gaia.cu9.ari.gaiaorbit.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g3d.environment.ShadowMap;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.math.Matrix4;

public class ShadowMapImpl implements ShadowMap {

    private Matrix4 trans;
    private TextureDescriptor<Texture> td;

    public ShadowMapImpl(Matrix4 trans, Texture tex) {
        super();
        this.trans = trans;
        this.td = new TextureDescriptor<Texture>(tex);
    }

    @Override
    public Matrix4 getProjViewTrans() {
        return trans;
    }

    @Override
    public TextureDescriptor<Texture> getDepthMap() {
        return td;
    }

    public void setProjViewTrans(Matrix4 mat) {
        this.trans.set(mat);
    }

    public void setDepthMap(Texture tex) {
        this.td.set(tex, TextureFilter.Nearest, TextureFilter.Nearest, TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
    }

}
