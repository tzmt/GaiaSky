package gaia.cu9.ari.gaiaorbit.desktop.util;

import java.io.File;
import java.io.PrintWriter;

/**
 * Produces a plot of ColorXP (BP-RP) vs log(Teff))
 * 
 * @author tsagrista
 *
 */
public class TeffPlot {

    public static void main(String[] args) {
        File f = new File("/tmp/teffcxp.dat");

        double startxp = -1.5;
        double endxp = 9.0;
        int nsteps = 100;
        double step = (endxp - startxp) / (double) nsteps;

        try {
            PrintWriter writer = new PrintWriter(f, "UTF-8");
            writer.println("bp-rp,logteff,teff");

            double currxp = startxp;
            while (currxp < endxp) {

                double logteff = 3.999 - 0.654 * currxp + 0.709 * currxp * currxp - 0.316 * currxp * currxp * currxp;
                double teff = Math.pow(10, logteff);
                writer.println(currxp + "," + logteff + "," + teff);
                currxp += step;
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

}
