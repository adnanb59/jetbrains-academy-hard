## sorting-tool

This program functions as a tool that a user can use to parse a selection of data and gain information on it.
This includes sorting the data (by some order, more later) as well as looking at different terms' frequencies.

The three relevant data types that can be processed are: word (words in a selection), line (lines ..), long (integer numbers ..)

These data types can be processed by either the frequency they appear in the selection or by their "natural" order (lexicographic, numeric)
> The line data type gets sorted by their length, rather than the lexographic order of their first words. That order is only used for tie breaks when displaying results.

**If you attempt to sort a data type that doesn't exist in the selection, an error will be thrown (i.e. sorting numbers in a selection with words)**

#### Running program
After compiling program, run using `java Runner [-sortingType natural|byCount] [-dataType word|long|line] [-inputFile <filename>] [-outputFile <filename>]`
- input and output files are self-explanatory
- the sortingType flag has 2 options:
  - natural: sort by numeric (long), lexographic (word), length (or lexographic, in case of ties) (line)
  - byCount: sort by frequency that data exists in selection
- the dataType flag has 3 options:
  - word: sort words
  - long: sort numbers
  - line: sort sentences

##### URL: https://hyperskill.org/projects/45
