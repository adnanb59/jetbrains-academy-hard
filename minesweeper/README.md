## minesweeper

This program is the Minesweeper game created using Java.

The aim of the game is to avoid the mines placed on the board as you explore it's cells.

In order to complete your task, you are able to free cells to gain information about the board. Empty cells don't tell you much (except that you're safe for now) but with the numbered cells, you can determine how many mines may be around.

As with the standard Minesweeper, if an empty cell is freed then adjacent empty cells are freed.
Once you have an estimation of the location of a mine, you can flag it.

You lose if you free a cell with a mine (it might be obvious but I felt I should lay everything out).
You win if you can either flag all the cells with mines (and only those cells, extra flags need to be removed). Additionally, there is another way to win where you are able to free every cell that doesn't contain a mine. In a sense, it is another way to detect mines. So if you have no cells flagged and all the non-mine cells freed, you win.

#### Running Program
After compiling the program (using `javac Runner.java`), run program with: `java Runner`.

You will then be prompted to enter the size of the square grid as follows:

`How large do you want the grid? (NxN cells, enter one number):` 

Being a square grid, only one number should be entered, furthermore it must be greater than 0.

After entering the size of the grid, you will be asked to enter how many mines you would like to place on the board.

The prompt: 

`How many mines do you want on the field?`

Again, only one positive (greater than 0) number is required, however the maximum number of mines you can have on the field is `N*N - 1`. This may just be *MY* rule, but there should be at least one empty cell to free on the board during the first turn.

After these initialization steps, this is where the game begins.

You will be shown the board and be prompted to make a move:

`Set/unset mines marks or claim a cell as free:`

A valid move is made by entering the coordinates of a cell followed by the move type: `<column> <row> mine/free`.

You keep making moves until you reach an end state: either you win or you lose.

The board that the user sees looks like this (a 3x3 board with unread cells):

```
|---|
|...|
|...|
|...|
|---|
```

#### Extra comments

> ENUMS

This program uses an enum for the various pieces on the Minesweeper board. Currently it's set to:
- `*`: Flagged cell
- `X`: Mine
- `.`: Unread
- `1`-`9`- Counters
Therefore, if you want to use different values, you can change them [here](minesweeper/src/minesweeper/Piece.java).

> DESIGN DECISION

The big "issue" I had with this project was determining how I wanted to represent the board with respect to what the player sees vs. the value on the board.

One option was to employ the use of two boards: one that is visible to the user and one that represents the actual board. Therefore, when cells are freed, the user board is updated based on the actual board. Additionally, when cells are flagged or unread, it is only reflected in the user board. 

My main reservations about this method was that if the board was significantly larger then you have 2 sets of large boards in use. It doesn't seem all that efficient to have to go between boards to set and retrieve values (considering the traversals that need to be done).

My other method (and the one I ended up using) was to have one board which contains the values of the board and while creating the String display for the board, printing mask values for parts that are unknown to the user (the unread and flagged cells) while printing the true board otherwise. This made me use several bookkeeping data structures to keep track of freed and flagged cells (as well as cells containing mines). Therefore, in having to check info on a cell, the program will refer to a specific set. Another plus is it is easier to check for win conditions using set operations (equality between flagged and mine sets or disjoint between freed and mine sets).

> FREEING CELLS

After freeing an empty cell in the game, adjacent empty cells are also freed. In order to do so, I used a modified Breadth-First Search (BFS). Cells that contained a number or were empty were added to the stack of cells to be visited later. Furthermore, only from an empty cell will adjacent cells be looked at. Additionally, the checked cells set serves as the "visited" set, so cells aren't visited multiple times. In thinking of a graph analogy where cells are vertices, empty nodes are only adjacent to other empty nodes as well as numbered nodes, so only those nodes are considered during the traversal.

> POSITION

I use a variable called `position` multiple times to map the row and column of the board to a number. This is my workaround to storing coordinates in the data structures. Think of the board as a grid of numbers from 1 to `N*N`, the position maps the coordinates to the specific number.

##### URL: https://hyperskill.org/projects/77
