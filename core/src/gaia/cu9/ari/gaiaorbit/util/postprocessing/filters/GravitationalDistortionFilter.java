/*******************************************************************************
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package gaia.cu9.ari.gaiaorbit.util.postprocessing.filters;

import com.badlogic.gdx.math.Vector2;
import com.bitfire.postprocessing.filters.Filter;
import com.bitfire.utils.ShaderLoader;

/** Fast approximate anti-aliasing filter.
 * @author Toni Sagrista */
public final class GravitationalDistortionFilter extends Filter<GravitationalDistortionFilter> {
    private Vector2 viewport;
    private Vector2 massPosition;

    public enum Param implements Parameter {
        // @formatter:off
        Texture("u_texture0", 0), Viewport("u_viewport", 2), MassPosition("u_massPosition", 2);
        // @formatter:on

        private String mnemonic;
        private int elementSize;

        private Param(String mnemonic, int arrayElementSize) {
            this.mnemonic = mnemonic;
            this.elementSize = arrayElementSize;
        }

        @Override
        public String mnemonic() {
            return this.mnemonic;
        }

        @Override
        public int arrayElementSize() {
            return this.elementSize;
        }
    }

    public GravitationalDistortionFilter(Vector2 viewportSize) {
        super(ShaderLoader.fromFile("screenspace", "gravitydistortion"));

        this.viewport = viewportSize;

        this.massPosition = new Vector2();

        rebind();
    }

    public GravitationalDistortionFilter(int viewportWidth, int viewportHeight) {
        this(new Vector2(viewportWidth, viewportHeight));
    }


    public void setViewportSize(float width, float height) {
        this.viewport.set(width, height);
        setParam(Param.Viewport, this.viewport);
    }

    /**
     * The position of the mass that causes the distortion in pixels.
     * @param x
     * @param y
     */
    public void setMassPosition(float x, float y) {
        this.massPosition.set(x, y);
        setParam(Param.MassPosition, this.massPosition);
    }

    @Override
    public void rebind() {
        // reimplement super to batch every parameter
        setParams(Param.Texture, u_texture0);
        setParams(Param.Viewport, this.viewport);
        setParams(Param.MassPosition, this.massPosition);
        endParams();
    }

    @Override
    protected void onBeforeRender() {
        inputTexture.bind(u_texture0);
    }
}
