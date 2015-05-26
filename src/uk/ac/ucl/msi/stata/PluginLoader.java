package uk.ac.ucl.msi.stata;

import static uk.ac.ucl.msi.stata.Properties.CLASS_PATH;
import static uk.ac.ucl.msi.stata.Properties.JAR_PATH;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.stata.sfi.SFIToolkit;

import de.pbc.utils.properties.PropertiesWrapper;

/**
 * <p>
 * The Stata <i>Dynamic Plugin Loader</i> (DPL) is Stata Java (SFI) plugin,
 * which dynamically loads other SFI plugins for execution. It thus allows SFI
 * developers to quickly iterate and debug their own plugins, without having to
 * restart Stata every time. This also applies to the resources SFI plugins may
 * reference (e.g., 3rd party libraries).
 * </p>
 * <h2>Plugin Interface</h2>
 * <p>
 * In order for DPL to load your plugin dynamically, your plugin has to
 * implement the {@link Plugin} interface. {@link Plugin#execute(String[])} is
 * the instantiated entry point for your plugin. The execution environment is
 * identical to Stata invoking a static entry point in your plugin. That is,
 * everything you can access via the SFI API (e.g., varlist, if, in) and the
 * supplied arguments are identical in either execution environment. See
 * {@link Plugin} for more details.
 * </p>
 * <h2>Setup</h2>
 * <p>
 * In a first step, the <i>Dynamic Plugin Loader</i> (DPL) has to be configured.
 * Think of it as setting up your IDE by configuring the {@code CLASS_PATH} to a
 * 3rd party library. In fact, that is exactly what you have to do for DPL too:
 * You have to specify the directories containing your plugins and 3rd party
 * libraries. To do this DPL expects {@code config/dpl.xml} in the current
 * {@code user.dir}. In the case of Stata, this is always the working directory
 * ({@code pwd}) at the time the JVM was started.
 * </p>
 * <p>
 * {@code config/dpl.xml} is expected to be a standard XML file as it is used by
 * {@link java.util.Properties}. It should contain 2 properties:
 * <ul>
 * <li>{@code CLASS_PATH}: Semicolon ({@code ;}) separated paths to all folders
 * containing compiled Java {@code .class} files. The paths have to satisfy
 * {@link Paths#get(String, String...)} naming conventions. The paths may be
 * relative to {@code user.dir}.</li>
 * <li>{@code JAR_PATH}: Semicolon ({@code ;}) separated paths to all folders
 * containing JAR files. The paths have to satisfy
 * {@link Paths#get(String, String...)} naming conventions. The paths may be
 * relative to {@code user.dir}.</li>
 * </ul>
 * Conveniently, these paths can link directly to your IDE's workspace. For
 * example, {@code .../Eclipse/Your Project/bin} for all compiled {@code .class}
 * files in an Eclipse environment.
 * </p>
 * <p>
 * This is all the configuration you need to do. In order to use DPL, make sure
 * the DPL JAR is in one of Stata's ADO path directories.
 * <h2>Usage</h2>
 * <p>
 * DPL is used best in conjunction with the {@code jcd} command, which takes
 * care of addressing DPL correctly (see {@code help jcd} for further details).
 * All you need to do is invoke {@code jcd} with your plugin's name:<br>
 * <br>
 * {@code jcd your.company.your.Plugin} <br>
 * <br>
 * The full command syntax for {@code jcd} is:<br>
 * <br>
 * {@code jcd class [varlist] [if] [in] [, args(argument_list)]}<br>
 * <br>
 * Alternatively, you can also invoke DPL directly:<br>
 * <br>
 * {@code javacall uk.ac.ucl.msi.stata.PluginLoader start [varlist] [if] [in] , args(your.company.your.Plugin [argument_list])}
 * </p>
 * <h2>Notes</h2>
 * <p>
 * If your plugin class is on one of the ADO paths DPL will always use this
 * version; it will not load your class from the resource paths specified in the
 * configuration file. That is, versions of classes on ADO paths supersede all
 * versions on {@code CLASS_PATH}s and {@code JAR_PATH}s.
 * </p>
 * <p>
 * Any uncaught exception occurring during the execution of your plugin leads to
 * DPL returning with error code 44. The exception and its stack trace are
 * printed to the Stata console. This also applies to all steps in preparation
 * of your plugin's execution (e.g., reading {@code config/dpl.xml}).
 * </p>
 * 
 * @author Philipp B. Cornelius
 * @version 2015-05-20
 */
public class PluginLoader {
	
	// MAIN ---------------------------------------------------------- //
	
	/**
	 * The entry point for {@link PluginLoader}.
	 * 
	 * @param args
	 * @return
	 */
	public static int start(String args[]) {
		try {
			PropertiesWrapper properties = new PropertiesWrapper(Paths.get("config/dpl.xml")).load();
			
			List<URL> urls = new ArrayList<>();
			
			String[] classPaths = properties.get(CLASS_PATH).split(";");
			urls.addAll(Arrays.stream(classPaths).map(PluginLoader::createUrlQuietly).collect(Collectors.toList()));
			
			String[] jarPaths = properties.get(JAR_PATH).split(";");
			for (String jarPath : jarPaths) {
				urls.addAll(Files
						.find(Paths.get(jarPath),
								4,
								(f, a) -> a.isRegularFile() && f.getFileName().toString().endsWith(".jar"))
						.map(PluginLoader::createUrlQuietly).collect(Collectors.toList()));
			}
			
			String className = args[0];
			args = Arrays.copyOfRange(args, 1, args.length);
			
			try (URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[urls.size()]))) {
				return ((Plugin) loader.loadClass(className).newInstance()).execute(args);
			}
		} catch (Exception e) {
			SFIToolkit.error(SFIToolkit.stackTraceToString(e));
			return 44;
		}
	}
	
	// PRIVATE ------------------------------------------------------- //
	
	/**
	 * We know this is virtually never going to happen, hence we wrap the nasty
	 * {@link MalformedURLException} into an unchecked exception.
	 * 
	 * @param path a path ({@code not null})
	 * @return a URL
	 */
	private static URL createUrlQuietly(Path path) {
		try {
			return path.toUri().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * We know this is virtually never going to happen, hence we wrap the nasty
	 * {@link MalformedURLException} into an unchecked exception. Equivalent to
	 * {@code createUrlQuietly(Paths.get(path))}.
	 * 
	 * @param path a path ({@code not null})
	 * @return a URL
	 */
	private static URL createUrlQuietly(String path) {
		try {
			return Paths.get(path).toUri().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
}