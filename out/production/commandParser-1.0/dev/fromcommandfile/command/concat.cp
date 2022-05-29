##commandParserV1.0
##programName=<value="bgzip concat <file1> <file2> ...";description="when '-h' were passed in, would be show 'Usage: $value [options]'">
##debugMode=true
##offset=<value=1;description="skip the $value arguments before the command argument passed in">
##globalRule=<value=".";description="one of the following rules is supported: {'.','AT_MOST_ONE','AT_LEAST_ONE','REQUEST_ONE'}">
#commandName	request	default	convertTo	validateWith	arity	group	description	format	hidden	help	debug
--help,-help,-h	false	.	passedIn	.	0	Options	.	.	true	true	false
concat	true	string-array	string-array	EnsureFileExists;NotDirectory	-1	Options	.	.	true	false	false
--output,-o	false	.	string	.	1	Options	Set the output file.	-o <file>	false	false	false
--yes,-y	false	.	passedIn	.	0	Options	Overwrite output file without asking.	.	false	false	false