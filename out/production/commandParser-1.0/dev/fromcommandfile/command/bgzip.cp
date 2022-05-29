##commandParserV1.0
##programName=<value="bgzip <mode>";description="when '-h' were passed in, would be show 'Usage: $value [options]'">
##debugMode=false
##offset=<value=1;description="skip the $value arguments before the command argument passed in">
##globalRule=<value="REQUEST_ONE";description="one of the following rules is supported: {'.','AT_MOST_ONE','AT_LEAST_ONE','REQUEST_ONE'}">
#commandName	request	default	convertTo	validateWith	arity	group	description	format	hidden	help	debug
--help,-help,-h	false	.	passedIn	.	0	Options	.	.	true	true	false
compress	false	.	string-array	.	-1	Options	Compression using parallel-bgzip (supported by CLM algorithm).	compress <file>	false	false	false
convert	false	.	string-array	.	-1	Options	Convert *.gz format to *.bgz format.	convert <file>	false	false	false
decompress	false	.	string-array	.	-1	Options	Decompression.	decompress <file>	false	false	false
extract	false	.	string-array	.	-1	Options	Cut the bgzip file by pointer range (decompressed file).	extract <file> -r <start>-<end>	false	false	false
concat	false	.	string-array	.	-1	Options	Concatenate multiple files.	concat <file> <file> ...	false	false	false
md5	false	.	string	.	1	Options	Calculate a message-digest fingerprint (checksum) for decompressed file.	md5 <file>	false	false	false