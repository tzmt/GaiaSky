package gaia.cu9.ari.gaiaorbit.data;

import java.io.InputStream;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;

import gaia.cu9.ari.gaiaorbit.scenegraph.ISceneGraph;
import gaia.cu9.ari.gaiaorbit.scenegraph.SceneGraphNode;
import gaia.cu9.ari.gaiaorbit.scenegraph.StarGroup;
import gaia.cu9.ari.gaiaorbit.scenegraph.octreewrapper.AbstractOctreeWrapper;
import gaia.cu9.ari.gaiaorbit.util.Logger;
import gaia.cu9.ari.gaiaorbit.util.time.ITimeFrameProvider;

public class SceneGraphJsonLoader {

    public static ISceneGraph loadSceneGraph(FileHandle[] jsonFiles, ITimeFrameProvider time, boolean multithreading, int maxThreads) {
        ISceneGraph sg = null;
        try {
            Array<SceneGraphNode> nodes = new Array<SceneGraphNode>(false, 5000);

            for (FileHandle jsonFile : jsonFiles) {
                JsonReader jsonReader = new JsonReader();
                InputStream is = jsonFile.read();
                JsonValue model = jsonReader.parse(is);

                JsonValue child = model.get("data").child;
                while (child != null) {
                    String clazzName = child.getString("loader");
                    @SuppressWarnings("unchecked")
                    Class<Object> clazz = (Class<Object>) ClassReflection.forName(clazzName);

                    JsonValue filesJson = child.get("files");
                    if (filesJson != null) {
                        String[] files = filesJson.asStringArray();

                        Constructor c = ClassReflection.getConstructor(clazz);
                        ISceneGraphLoader loader = (ISceneGraphLoader) c.newInstance();

                        // Init loader
                        loader.initialize(files);

                        // Load data
                        Array<? extends SceneGraphNode> data = loader.loadData();
                        for (SceneGraphNode elem : data) {
                            nodes.add(elem);
                        }
                    }

                    child = child.next;
                }
                is.close();
            }

            // Initialize nodes and look for octrees
            boolean hasOctree = false;
            boolean hasStarGroup = false;
            for (SceneGraphNode node : nodes) {
                node.initialize();
                if (node instanceof AbstractOctreeWrapper) {
                    hasOctree = true;
                    AbstractOctreeWrapper aow = (AbstractOctreeWrapper) node;
                    for (SceneGraphNode n : aow.children) {
                        if (n instanceof StarGroup) {
                            hasStarGroup = true;
                            break;
                        }
                    }
                }

                if (node instanceof StarGroup)
                    hasStarGroup = true;
            }

            sg = SceneGraphImplementationProvider.provider.getImplementation(multithreading, hasOctree, hasStarGroup, maxThreads);

            sg.initialize(nodes, time, hasOctree, hasStarGroup);

        } catch (Exception e) {
            Logger.error(e);
        }
        return sg;
    }

}
