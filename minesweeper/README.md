## minesweeper

This program is the Minesweeper game created using Java.

// MORE TEXT HERE


##### Running Program
After compiling the program (using `javac Runner.java`), run program with: `java Runner`.

You will then be prompted to enter the size of the square grid as follows:

`How large do you want the grid? (NxN cells, enter one number): ` 

Being a square grid, only one number should be entered, furthermore it must be greater than 0.

After entering the size of the grid, you will be asked to enter how many mines you would like to place on the board.

The prompt: 

`How many mines do you want on the field? `

Again, only one positive (greater than 0) number is required, however the maximum number of mines you can have on the field is `N*N - 1`. This may just be *MY* rule, but there should be at least one empty cell to free on the board during the first turn.

After these initialization steps, this is where the game begins.

You will be shown the board and be prompted to make a move:

`Set/unset mines marks or claim a cell as free:`

A valid move is made by entering the coordinates of a cell followed by the move type: `<column> <row> mine/free`.

You keep making moves until you reach an end state: either you win or you lose.

##### URL: https://hyperskill.org/projects/77