capture program drop jc
program jc
	version 13
	
	local r_all : r(macros)
	foreach r_elt of loc r_all {
		local r_`r_elt' = r(`r_elt')
	}
	
	local e_all : e(macros)
	foreach e_elt of loc e_all {
		local e_`e_elt' = e(`e_elt')
	}
	
	javacall `0'
end
