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

Once a maze is initialized, more options will be presented to the user. These will include saving & displaying the maze as well as its escape route.

### Extra Notes

> Graph Algorithms

> Maze Structure

> Maze Design

##### URL: https://hyperskill.org/projects/47
