package gaia.cu9.ari.gaiaorbit.interfce.beans;

import com.badlogic.gdx.files.FileHandle;

public class FileComboBoxBean {
    public String name;
    public String file;

    public FileComboBoxBean(FileHandle file) {
	super();
	this.name = file.name();
	this.file = file.path();
    }

    @Override
    public String toString() {
	return name;
    }

    static String stripExtension(String str) {
	// Handle null case specially.
	if (str == null)
	    return null;

	// Get position of last '.'.
	int pos = str.lastIndexOf(".");

	// If there wasn't any '.' just return the string as is.
	if (pos == -1)
	    return str;

	// Otherwise return the string, up to the dot.
	return str.substring(0, pos);
    }

}