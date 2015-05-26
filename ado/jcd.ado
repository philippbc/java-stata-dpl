capture program drop jcd
program jcd
	version 13
	syntax anything [if] [in] [, args(string asis)]
	
	gettoken class anything : anything
	
	local r_all : r(macros)
	foreach r_elt of loc r_all {
		local r_`r_elt' = r(`r_elt')
	}
	
	local e_all : e(macros)
	foreach e_elt of loc e_all {
		local e_`e_elt' = e(`e_elt')
	}
	
	javacall uk.ac.ucl.msi.stata.PluginLoader start `anything' `if' `in', args(`class' `args')
end
