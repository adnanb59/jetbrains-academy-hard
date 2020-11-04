## maze-runner

This is a fun program that uses Graph algorithms to allow users to create and solve (well, show the solution to) mazes.

Users can't solve mazes interactively with the program but this program is neat if you are looking for mazes to solve.

> I know that the project name maze-runner suggests that there's an interactive aspect where one would solve the maze but I'm just using the Jetbrains project name.

On top of just simply creating mazes, you can also export them to a text file and load them later (to view the maze and/or it's solution).

The mazes created by this program have openings on the left and right (one on either side) and none on the top or bottom. It should look like there's a wall (aside from the openings) that seems to contain the inner maze structure.

### Running Program

After compiling program using `javac src/Runner.java`, run program with `java src/Runner`.

Once the program starts, the user will be prompted with a menu with options to initialize a maze.
This can be done in two ways:
- Create a new maze
- Load a maze

If the user decides to create a new maze, they will be prompted to enter a size for the maze. Because the maze is square, only one number is required.
Additionally, the smallest maze possible with respect to the maze design mentioned above is 3x3. Therefore, the user must enter a number greater than or equal to 3.

If a user decides to load a maze, they will be prompted to enter the text file containing the maze encoding, which will then be loaded. If the file does not exist, or contains an incorrect format (more on that later), then an error will be thrown.

Once a maze is initialized, more options will be presented to the user. These will include saving & displaying the maze as well as displaying its escape route.

### Extra Notes

> Graph Algorithms

This program uses a modified Prim's algorithm to create the maze.
Choosing a vertex as well as a neighbour to connect to is random, so if you try to connect with the graph analogy, you should consider edges to be unweighted.
I was having trouble with making the logic work with the implementation, [this](https://stackoverflow.com/a/29758926) link helped me fix those issues (using every other element vs. the adjacent element)

This program uses a bidirectional Breadth-First Search to find the escape route of the maze.
Because its easy to determine the entrance and exit (mentioned later), the algorithm searches until there is an intersection is found (the first element that was visited by both directions of the BFS). Once found, the algorithm traces back through the parent elements visited until it reaches the entrance & exit, adding the elements to a set containing the escape path positions.

> Maze Structure

As you can see [here](maze-runner/src/maze/MazeGenerator.java), the maze is not a 2D-array but the final structure is influenced by one. It is a set consisting of positions on the maze containing free spaces (the path). These positions are akin to indices in a 2D-array except rather than being coordinates, they are values between `[0, N^2 - 1]; N = maze length`. Because the maze really consists of two values: a free space or a block/wall, there is no need to keep track of both pieces of information because one existing in a data structure implies that it doesn't exist in the other (if there was another). Meaning, if the value exists in the path set, it is implied that it is not part of the wall.

Also, since Java set lookup is O(1) on average, it is quicker to check if an element is part of the path vs. having to traverse to a specific coordinate in an array.
This, along with simple math makes maze creation much more efficient because all you're doing is adding or subtracting values from a given value to find its neighbours (and doing checks).

> File Format

When saving the file, the maze stored into a text file is not in the most obvious format since its not simply the String representation of the maze being plopped into a text file.
The file format is as follows:
```
<length of maze>
<#> <#> <#> ... <#>
```
where the hashes represent the positions in the path set.

While loading the file, there are a bunch of checks done on the values as follows:
- maze length >= 3
- maze positions >= 0 and < length^2
- existence of **exactly** one opening on the left and right side (representing entrance & exit)

A value denoting an entrance is such that `v % length = 0`, likewise a value denoting an exit is a value where `v % length = length-1`.

##### URL: https://hyperskill.org/projects/47
