## flashcards

This program allows a user to create a set of flashcards that they can use to study and revise information.  
A flashcard, in this case, is defined by a term and it's definition.  

In the program, the user can do a variety of actions (when prompted).  
The user will be prompted with 
`Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):`

- _add_: A user can add a card (will be further prompted for the term & definition) to the collection. If the term or
definition has not been entered before then the card will be added to the collection.
If not, there will be an error message.

- _remove_: A user can remove a card (will be further prompted for the term) from the collection. If the card does
exist, it will be removed, otherwise there will be an error message.

- _import_: A user can import a collection from a file (which will be prompted for) if not done through
the command line -- more on that later. If the file exists and is formatted correctly (format defined later)
then they will be added, otherwise there will be an error.

- _export_: A user can export a collection to a file (which will be prompted for) if not done through 
the command line -- more on that later. If the file exists and is formatted correctly (format defined later)
then the cards will be exported (for use later), otherwise there will be an error.

- _ask_: To test themselves, the user can prompt the program to ask them about random flash cards (the specific number
will be prompted for). As the user is right or wrong, the program collects information about the incorrect attempts.

- _log_: A user can log all the output they've encountered from the program to an output file (which will be prompted
for).

- _hardest card_: A user can find out which cards they have been struggling with by finding out which cards they have
failed the most (along with the number of failures).

- _reset stats_: A user can reset the failure statistics being maintained.

- _exit_: Once the user is done with the program, safely exit with this prompt. If the user passed an -export flag,
then the card collection will be exported to a file.

#### *Format For Importing/Exporting Collections*
`term`  
`definition`  
`attempts`
- and you can keep adding cards, following this format (no extra blank lines between cards) 

#### *Running Program*
The user can specify an input file (to import card collection from) using the flag **-import** as 
well as specifying the output file (to export card collection to) using the flag **-export**.

After compiling the files, you can run program with:  
`java Runner [-import <filename>] [-export <filename>]`  

##### URL: https://hyperskill.org/projects/44
