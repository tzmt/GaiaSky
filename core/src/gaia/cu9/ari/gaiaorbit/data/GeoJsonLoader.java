package gaia.cu9.ari.gaiaorbit.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import gaia.cu9.ari.gaiaorbit.scenegraph.Area;
import gaia.cu9.ari.gaiaorbit.scenegraph.SceneGraphNode;
import gaia.cu9.ari.gaiaorbit.util.I18n;
import gaia.cu9.ari.gaiaorbit.util.Logger;

/**
 * Loads GeoJson files to Area objects
 * @author Toni Sagrista
 *
 * @param <T>
 */
public class GeoJsonLoader<T extends SceneGraphNode> implements ISceneGraphLoader {

    /** Contains all the files to be loaded by this loader **/
    private String[] filePaths;

    @Override
    public void initialize(String[] files) {
        filePaths = files;
    }

    @Override
    public Array<? extends SceneGraphNode> loadData() {
        Array<T> bodies = new Array<T>();

        try {
            JsonReader json = new JsonReader();
            for (String filePath : filePaths) {
                FileHandle file = Gdx.files.internal(filePath);
                JsonValue model = json.parse(file.read());
                JsonValue child = model.get("features").child;
                int size = 0;
                while (child != null) {
                    size++;

                    // Convert to object and add to list
                    T object = (T) convertJsonToArea(child);

                    bodies.add(object);

                    child = child.next;
                }
                Logger.info(this.getClass().getSimpleName(), I18n.bundle.format("notif.nodeloader", size, filePath));
            }

        } catch (Exception e) {
            Logger.error(e);
        }

        return bodies;
    }

    private Area convertJsonToArea(JsonValue json) throws ReflectionException {
        Area instance = new Area();
        instance.setParent("Earth");
        instance.setCt("Countries");
        instance.setName(json.get("properties").getString("name"));

        JsonValue jsonArray = json.get("geometry").get("coordinates");

        JsonValue firstelem;
        int size;
        int d;

        int depth = depth(jsonArray);

        if (depth == 4) {
            firstelem = jsonArray.child;
            size = jsonArray.size;
            d = 1;
        } else {
            firstelem = jsonArray.child;
            size = jsonArray.size;
            d = 2;
        }

        instance.setPerimeter(convertToDoubleArray(firstelem, size, d));

        return instance;
    }

    public double[][][] convertToDoubleArray(JsonValue json, int size, int d) {
        double[][][] result = new double[size][][];
        int i = 0;
        JsonValue current = json;
        if (d > 1)
            current = json.child;
        do {
            double[][] l1 = new double[current.size][];
            // Fill in last level

            JsonValue child = current.child;
            int j = 0;
            do {
                double[] l2 = child.asDoubleArray();
                l1[j] = l2;

                child = child.next();
                j++;
            } while (child != null);

            result[i] = l1;

            if (d == 1) {
                current = current.next();
            } else {
                current = json.next() != null ? json.next().child : null;
                json = json.next();
            }
            i++;
        } while (current != null);

        return result;
    }

    private int depth(JsonValue v) {
        if (v.isArray()) {
            return depth(v.child) + 1;
        } else {
            return 1;
        }
    }
}