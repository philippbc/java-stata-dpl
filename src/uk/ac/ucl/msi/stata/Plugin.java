package uk.ac.ucl.msi.stata;

/**
 * <p>
 * In order for DPL to load your plugin dynamically, your plugin has to
 * implement the {@link Plugin} interface. {@link Plugin#execute(String[])} is
 * the instantiated entry point for your plugin. The execution environment is
 * identical to Stata invoking a static entry point in your module. That is,
 * everything you can access via the SFI API (e.g., varlist, if, in) as well as
 * the supplied arguments are identical in either execution environment.
 * </p>
 * <p>
 * For example:
 * 
 * <pre>
 * <code>public class YourModule implements Module {
 * 	private static int staticEntryPoint(String[] args) {
 * 		return new YourModule().execute(args);
 * 	}
 * 
 * 	{@literal @}Override
 * 	public int execute(String[] args) throws Exception {
 * 		// <i>do your work</i>
 * 		return 0;
 * 	}
 * }
 * </code>
 * </pre>
 * 
 * In this scenario, during development {@link PluginLoader} invokes
 * {@link #execute(String[])} directly. For productive use, your module is
 * invoked by Stata via {@code staticEntryPoint(String[])}. Once inside
 * {@link #execute(String[])} the difference is indistinguishable.
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