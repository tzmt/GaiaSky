package gaia.cu9.ari.gaiaorbit.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import gaia.cu9.ari.gaiaorbit.desktop.format.DesktopDateFormatFactory;
import gaia.cu9.ari.gaiaorbit.desktop.format.DesktopNumberFormatFactory;
import gaia.cu9.ari.gaiaorbit.desktop.util.DesktopConfInit;
import gaia.cu9.ari.gaiaorbit.event.EventManager;
import gaia.cu9.ari.gaiaorbit.event.Events;
import gaia.cu9.ari.gaiaorbit.event.IObserver;
import gaia.cu9.ari.gaiaorbit.util.ConfInit;
import gaia.cu9.ari.gaiaorbit.util.I18n;
import gaia.cu9.ari.gaiaorbit.util.Logger;
import gaia.cu9.ari.gaiaorbit.util.NotificationsListener;
import gaia.cu9.ari.gaiaorbit.util.format.DateFormatFactory;
import gaia.cu9.ari.gaiaorbit.util.format.NumberFormatFactory;
import gaia.cu9.ari.gaiaorbit.util.math.StdRandom;
import gaia.cu9.ari.gaiaorbit.util.math.Vector3d;

public class OortGenerator implements IObserver {

    /** Whether to write the results to disk **/
    private static final boolean writeFile = true;

    /** Inner radius in AU **/
    private static double inner_radius = 2000;

    /** Outer radius in AU **/
    private static float outer_radius = 15000;

    /** Number of particles **/
    private static int N = 10000;

    public static void main(String[] args) {
        try {
            Gdx.files = new LwjglFiles();

            // Initialize number format
            NumberFormatFactory.initialize(new DesktopNumberFormatFactory());

            // Initialize date format
            DateFormatFactory.initialize(new DesktopDateFormatFactory());

            ConfInit.initialize(new DesktopConfInit(new FileInputStream(new File("../android/assets/conf/global.properties")), new FileInputStream(new File("../android/assets/data/dummyversion"))));

            I18n.initialize(new FileHandle("/home/tsagrista/git/gaiasky/android/assets/i18n/gsbundle"));

            // Add notif watch
            EventManager.instance.subscribe(new NotificationsListener(), Events.POST_NOTIFICATION, Events.JAVA_EXCEPTION);

            Array<double[]> oort = null;

            oort = generateOort();

            if (writeFile) {
                writeToDisk(oort, "/tmp/");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates random Oort cloud particles
     * 
     * @throws IOException
     */
    private static Array<double[]> generateOort() throws IOException, RuntimeException {
        StdRandom.setSeed(100l);

        Array<double[]> particles = new Array<double[]>(N);

        Vector3d yAxis = new Vector3d(0, 1, 0);
        Vector3d xAxis = new Vector3d(1, 0, 0);
        double thickness = outer_radius - inner_radius;
        Vector3d particle = new Vector3d();
        int n = 0;
        // Generate only in z, we'll randomly rotate later
        while (n < N) {
            double x = (StdRandom.gaussian()) * outer_radius * 2;
            double y = (StdRandom.gaussian()) * outer_radius * 2;
            double z = (StdRandom.gaussian()) * outer_radius * 2;

            particle.set(x, y, z);

            // if (particle.len() <= outer_radius) {

            // // Rotation around X
            // double xAngle = StdRandom.uniform() * 360;
            // particle.rotate(xAxis, xAngle);
            //
            // // Rotation around Y
            // double yAngle = StdRandom.uniform() * 180 - 90;
            // particle.rotate(yAxis, yAngle);

            particles.add(new double[] { particle.x, particle.y, particle.z });
            n++;
            // }
        }

        return particles;
    }

    private static void writeToDisk(Array<double[]> oort, String dir) throws IOException {
        String filePath = dir + "oort_";
        filePath += N + "particles.dat";

        FileHandle fh = new FileHandle(filePath);
        File f = fh.file();
        if (fh.exists() && f.isFile()) {
            fh.delete();
        }

        if (fh.isDirectory()) {
            throw new RuntimeException("File is directory: " + filePath);
        }
        f.createNewFile();

        FileWriter fw = new FileWriter(filePath);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("#X Y Z");
        bw.newLine();

        for (int i = 0; i < oort.size; i++) {
            double[] particle = oort.get(i);
            bw.write(particle[0] + " " + particle[1] + " " + particle[2]);
            bw.newLine();
        }

        bw.close();

        Logger.info("File written to " + filePath);
    }

    @Override
    public void notify(Events event, Object... data) {
        switch (event) {
        case POST_NOTIFICATION:
            String message = "";
            for (int i = 0; i < data.length; i++) {
                if (i == data.length - 1 && data[i] instanceof Boolean) {
                } else {
                    message += (String) data[i];
                    if (i < data.length - 1 && !(i == data.length - 2 && data[data.length - 1] instanceof Boolean)) {
                        message += " - ";
                    }
                }
            }
            System.out.println(message);
            break;
        case JAVA_EXCEPTION:
            Exception e = (Exception) data[0];
            e.printStackTrace(System.err);
            break;
        default:
            break;
        }

    }

}
