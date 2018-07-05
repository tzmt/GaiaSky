package gaia.cu9.ari.gaiaorbit.util.ucd;

/**
 * Very naive class that represents and UCD and does the parsing.
 * @author tsagrista
 *
 */
public class UCD {

    enum UCDType {
        POS, PHOT, STAT, PHYS, META, ARITH, EM, OBS, SPECT, SRC, TIME, INSTR, UNKNOWN, MISSING
    }

    public String originalucd, converted, colname, unit;
    public String[][] ucd;
    public String[] ucdstrings;
    public UCDType type;
    public int index;

    public UCD(String originalucd, String colname, String unit, int index) {
        super();

        this.index = index;
        this.colname = colname;
        this.unit = unit;
        if (originalucd != null && !originalucd.isEmpty()) {
            this.originalucd = originalucd;
            // Convert UCD1 to 
            this.converted = originalucd.toLowerCase().replace("_", ".");

            this.ucdstrings = this.converted.split(";");

            this.ucd = new String[ucdstrings.length][];

            for (int i = 0; i < ucdstrings.length; i++) {
                String singleucd = ucdstrings[i];
                String[] ssplit = singleucd.split("\\.");
                this.ucd[i] = ssplit;
            }

            // Type
            String currtype = this.ucd[0][0];
            try {
                this.type = UCDType.valueOf(currtype.toUpperCase());
            } catch (Exception e) {
                this.type = UCDType.UNKNOWN;
            }
        } else {
            this.type = UCDType.MISSING;
        }

    }

    @Override
    public String toString() {
        return colname + (this.originalucd == null ? "" : " - " + this.originalucd);
    }

}
