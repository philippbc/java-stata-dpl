package de.pbc.stata;

import java.nio.file.Paths;

import de.pbc.utils.properties.Property;

/**
 * DPL properties.
 * 
 * @author Philipp B. Cornelius
 * @version 2015-05-20
 */
public enum Properties implements Property {

	/**
	 * Semicolon ({@code ;}) separated paths to all folders containing compiled
	 * Java {@code .class} files. The paths have to satisfy
	 * {@link Paths#get(String, String...)} naming conventions. The paths may be
	 * relative to {@code user.dir}.
	 */
	CLASS_PATH("src/"),
	/**
	 * Semicolon ({@code ;}) separated paths to to all folders containing JAR
	 * files. The paths have to satisfy {@link Paths#get(String, String...)}
	 * naming conventions. The paths may be relative to {@code user.dir}.
	 */
	JAR_PATH("lib/");

	// CONSTANTS ----------------------------------------------------- //

	private final String def;

	// CONSTRUCTOR --------------------------------------------------- //

	private Properties(String def) {
		this.def = def;
	}

	// PUBLIC -------------------------------------------------------- //

	@Override
	public String getDefault() {
		return def;
	}

}