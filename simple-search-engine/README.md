## simple-search-engine

This program simulates a lookup engine for names and emails that allows users to search for people in a "directory".

Users populate the "directory" initially and then have the option to search through it.

The engine uses an inverted index data structure, which maps terms to the positions in which they appear in the collection, so rather than search the entire collection for matches, it is easier to check if a term exists.

> Search terms refer to any part of the person's name or email, one does not need to enter the complete name or email for it to show up as a result.

#### Running program
After compiling program, run program `java Runner [--data filename]`.
If the flag `--data` is passed with a file name, then the initial collection of people is added from the file, otherwise the user will be prompted to enter names as follows.

`Enter the number of people:`, where user must enter the number of people they'd like to keep in the collection.

This will then be followed by the prompt to enter N people: `Enter all people:`.

After the collection of people have been setup, then the search engine can do it's work.

In the engine's menu, the user can either print the entire collection or search for specific users.

If the user opts to search for people, they will be prompted to enter their search strategy followed by the terms they want to search for.

`Select a matching strategy: ALL, ANY, NONE`, where:
- ALL: all the search terms must be matched by people in the collection in order to be returned
- ANY: any of the search terms can be matched
- NONE: only return people that don't match any of the search terms

Followed by the prompt for search terms: `Enter a name or email to search all suitable people.`

##### URL: https://hyperskill.org/projects/66
