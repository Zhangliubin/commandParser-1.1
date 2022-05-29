##commandParserV1.0
##programName=<value="bgzip extract <file> --range <start-end>";description="when '-h' were passed in, would be show 'Usage: $value [options]'">
##debugMode=false
##@syntax=true
##offset=<value=1;description="skip the $value arguments before the command argument passed in">
##globalRule=<value=".";description="one of the following rules is supported: {'.','AT_MOST_ONE','AT_LEAST_ONE','REQUEST_ONE'}">
#commandName	request	default	convertTo	validateWith	arity	group	description	format	hidden	help	debug
--help,-help,-h	false	.	passedIn	.	0	Options	.	.	true	true	false
extract	true	.	string	EnsureFileExists;NotDirectory	1	Options	.	.	true	false	false
--range,-r	true	.	<start>-<end> (long)	.	1	Options	Set the range of the file pointer (decompressed file).	-r <start>-<end>	false	false	false
--output,-o	false	.	string	.	1	Options	Set the output file.	-o <file>	false	false	false
--threads,-t	false	4	integer	RangeOf(1.0,10.0)	1	Options	Set the number of threads for bgzip compression.	-t <int, 1-10>	false	false	false
--level,-l	false	5	integer	RangeOf(0.0,9.0)	1	Options	Compression level to use for bgzip compression.	-l <int, 0-9>	false	false	false
--yes,-y	false	.	passedIn	.	0	Options	Overwrite output file without asking.	.	false	false	false