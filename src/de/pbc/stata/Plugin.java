package de.pbc.stata;

/**
 * <p>
 * In order for DPL to load a plugin dynamically, the plugin has to implement
 * this interface. {@link Plugin#execute(String[])} is the <i>instantiated</i>
 * entry point: <b>All work to be done in the plugin should start in
 * {@link Plugin#execute(String[])}.</b>
 * </p>
 * <p>
 * The execution environment in {@link Plugin#execute(String[])} is identical to
 * Stata invoking a static entry point. That is, everything you can access via
 * the SFI API (e.g., varlist, if, in) and the supplied arguments are identical.
 * </p>
 * <p>
 * Consider the following example:
 * 
 * <pre>
 * <code>public class YourPlugin implements Plugin {
 * 	// This is the static entry point as you <i>may</i> want to use it for productive scenarios
 * 	// All it does is instantiate your plugin and call execute(...).
 * 	private static int staticEntryPoint(String[] args) {
 * 		return new YourPlugin().execute(args);
 * 	}
 * 
 * 	// This is where all the action happens.
 * 	{@literal @}Override
 * 	public int execute(String[] args) throws Exception {
 * 		// <i>do your work here</i>
 * 		return 0;
 * 	}
 * }
 * </code>
 * </pre>
 * 
 * During development {@link PluginLoader} invokes {@link #execute(String[])}
 * directly. For productive use, your module is invoked by Stata via
 * {@code staticEntryPoint(String[])}. Once inside {@link #execute(String[])}
 * the difference is indistinguishable.
 * </p>
 * 
 * @author Philipp B. Cornelius
 * @version 2015-05-20
 */
public interface Plugin {
	
	// PUBLIC -------------------------------------------------------- //
	
	/**
	 * This is where the plugin should do its work. Please refer to the Stata
	 * manual as to what APIs are available and what are acceptable return
	 * codes.
	 * 
	 * @param args a {@code String} array of arguments supplied to your module
	 *            by Stata
	 * @return see Stata's manual for acceptable return codes
	 * @throws Exception any uncaught exception will lead to
	 *             {@link PluginLoader} returning with 44 and printing the
	 *             exception and its stack trace to the Stata console
	 */
	public int execute(String[] args) throws Exception;
	
}