##commandParserV1.0
##programName=<value="bgzip md5 <file>";description="when '-h' were passed in, would be show 'Usage: $value [options]'">
##debugMode=false
##offset=<value=1;description="skip the $value arguments before the command argument passed in">
##globalRule=<value=".";description="one of the following rules is supported: {'.','AT_MOST_ONE','AT_LEAST_ONE','REQUEST_ONE'}">
#commandName	request	default	convertTo	validateWith	arity	group	description	format	hidden	help	debug
--help,-help,-h	false	.	passedIn	.	0	Options	.	.	true	true	false
md5	true	.	string	EnsureFileExists;NotDirectory	1	Options	.	.	true	false	false