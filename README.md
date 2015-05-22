### Stata *Dynamic Plugin Loader* (DPL)

The Stata *Dynamic Plugin Loader* (DPL) is Stata Java (SFI) plugin, which dynamically loads other SFI plugins upon execution. It thus allows SFI developers to quickly iterate and debug their own plugins, without having to restart Stata every time. This also applies to the resources SFI plugins may reference (e.g., 3rd party libraries).

#####Plugin Interface

In order for DPL to load your plugin dynamically, your plugin has to implement the `Plugin` interface. `Plugin.execute(String[])` is the instantiated entry point for your plugin. The execution environment is identical to Stata invoking a static entry point in your module. That is, everything you can access via the SFI API (e.g., varlist, if, in) and the supplied arguments are identical in either execution environment. See `Plugin`'s documentation for more details.

#####Setup

In a first step, the *Dynamic Plugin Loader* (DPL) has to be configured. Think of it as setting up your IDE by configuring the `CLASS_PATH` to a 3rd party library. In fact, that is exactly what you have to do for DPL too: You have to specify the directories containing your plugins and 3rd party libraries. To do this DPL expects `config/dpl.xml` in the current `user.dir`. In the case of Stata, this is always the working directory (`pwd`) at the time the JVM was started.

`config/dpl.xml` is expected to be a standard XML file as it is used by `java.util.Properties`. It should contain 2 properties:
* `CLASS_PATH`: Semicolon (`;`) separated paths to all folders containing compiled Java `.class` files. The paths have to satisfy `Paths.get(String, String...)` naming conventions. The paths may be relative to `user.dir`.
* `JAR_PATH`: Semicolon (`;`) separated paths to all folders containing JAR files. The paths have to satisfy `Paths.get(String, String...)` naming conventions. The paths may be relative to `user.dir`.

Conveniently, these paths can link directly to your IDE's workspace. For example, `.../Eclipse/Your Project/bin` for all compiled `.class` files in an Eclipse environment.

This is all the configuration you need to do. In order to use DPL, make sure the DPL JAR is in one of Stata's ADO path directories.

#####Usage

DPL is used best in conjunction with the `jcd` command, which takes care of addressing DPL correctly (see `help jcd` for further details). All you need to do is invoke `jcd` with your module's name:

`jcd your.company.your.Module`

The full command syntax for `jcd` is:

`jcd class [varlist] [if] [in] [, args(argument_list)]`

Alternatively, you can also invoke DPL directly:

`javacall uk.ac.ucl.msi.stata.PluginLoader start [varlist] [if] [in] [, args(argument_list)]`

#####Notes

If your plugin class is on one of the ADO paths DPL will always use this version; it will not load your class from the resource paths specified in the configuration file. That is, versions of classes on ADO paths supersede all versions on `CLASS_PATH`s and `JAR_PATH`s.

Any uncaught exception occurring during the execution of your module leads to DPL returning with error code 44. The exception and its stack trace are printed to the Stata console. This also applies to all steps in preparation of your module's execution (e.g., reading `config/dpl.xml`).
