package gaia.cu9.ari.gaiaorbit.util.override;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;

public class RelativisticEffectFloatAttribute extends FloatAttribute {

    public RelativisticEffectFloatAttribute(long type) {
        super(type);
    }

    public RelativisticEffectFloatAttribute(long type, float value) {
        super(type, value);
    }

    public static final String VcAlias = "vc";
    public static final long Vc = register(VcAlias);

    public static final String TsAlias = "ts";
    public static final long Ts = register(TsAlias);

    public static final String OmgwAlias = "omgw";
    public static final long Omgw = register(OmgwAlias);

    @Override
    public Attribute copy() {
        return new RelativisticEffectFloatAttribute(type, value);
    }


}
