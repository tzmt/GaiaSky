package gaia.cu9.ari.gaiaorbit.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import gaia.cu9.ari.gaiaorbit.util.override.RelativisticShaderProvider;

public class RelativisticShaderProviderLoader<T extends RelativisticShaderProviderLoader.RelativisticShaderProviderParameter> extends AsynchronousAssetLoader<RelativisticShaderProvider, T> {

    RelativisticShaderProvider shaderProvider;

    public RelativisticShaderProviderLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, T parameter) {
        shaderProvider = new RelativisticShaderProvider(Gdx.files.internal(parameter.vertexShader), Gdx.files.internal(parameter.fragmentShader));
    }

    @Override
    public RelativisticShaderProvider loadSync(AssetManager manager, String fileName, FileHandle file, RelativisticShaderProviderParameter parameter) {
        return shaderProvider;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, RelativisticShaderProviderParameter parameter) {
        return null;
    }

    public static class RelativisticShaderProviderParameter extends AssetLoaderParameters<RelativisticShaderProvider> {
        String vertexShader;
        String fragmentShader;

        public RelativisticShaderProviderParameter(String vertexShader, String fragmentShader) {
            super();
            this.vertexShader = vertexShader;
            this.fragmentShader = fragmentShader;
        }

    }

}
