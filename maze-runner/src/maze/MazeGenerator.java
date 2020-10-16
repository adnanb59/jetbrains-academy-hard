package maze;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MazeGenerator {
    /**
    * Function to create a path for a maze with given dimensions (passed in)
    * The path is represented by a set of integers that represent free cells (the set of other cells would
    * therefore be the walls)
    *
    * @param height - height of maze (REQUIRED: >= 3)
    * @param width - width of maze (REQUIRED: >= 3)
    * @return Maze with given dimensions
    */
    public static Set<Integer> generate(int height, int width) {
        if (height < 3 || width < 3) return null; // 3x3 maze is smallest size of maze

        Random rand = new Random(System.currentTimeMillis());

        // Path represents final maze path
        // Frontier cells are the available (not yet freed) neighbours of cells in the path
        HashSet<Integer> path = new HashSet<>();
        HashSet<Integer> frontiers = new HashSet<>();

        // Start at random cell within outer wall (at this point, entrance and exit haven't been selected)
        frontiers.add((rand.nextInt(height-2)+1)*width + (rand.nextInt(width-2)+1));

        // Loop till all possible neighbour cells have been added to path
        while (!frontiers.isEmpty()) {
            // Pick a cell at random and add it to the path (while removing it from the set of frontiers)
            int current = frontiers.stream().skip(rand.nextInt(frontiers.size())).findFirst().orElse(-1);
            frontiers.remove(current);
            path.add(current);

            // Add frontier cells of currently accessed cell, these cells aren't in the path, as of yet
            // Cells are added in order: N, S, W, E (if they exist - not outside scope of board)
            if (current/width % height > 2 && !path.contains(current - 2*width)) frontiers.add(current - 2*width);
            if (current/width % height < height-3 && !path.contains(current + 2*width)) frontiers.add(current + 2*width);
            if ((current % width > 2) && !path.contains(current-2)) frontiers.add(current-2);
            if ((current % width < width-3) && !path.contains(current+2)) frontiers.add(current+2);

            // Find neighbours (path cells that are adjacent to current cell)
            int[] neighbours = new int[4]; // Max of 4
            int count = 0;
            if (path.contains(current-2)) neighbours[count++] = -1;
            if (path.contains(current+2)) neighbours[count++] = 1;
            if (path.contains(current + 2*width)) neighbours[count++] = width;
            if (path.contains(current - 2*width)) neighbours[count++] = -width;
            if (count > 0) { // If there are available neighbours, pick one at random
                current = current + neighbours[rand.nextInt(count)];
                path.add(current);
                frontiers.remove(current);
            }
        }

        boolean markedEnding = false, markedStart = false;
        // Add entrance to maze, find most top-left free cell and "create a hole" to the entrance.
        // Free adjacent cells to the left of found cell
        for (int j = 1; j < width-1 && !markedStart; j++) {
            for (int i = 1; i < height-1 && !markedStart; i++) {
                if (path.contains(i*width + j)) {
                    markedStart = true;
                    int position = i*width + j;
                    while (position % width != 0) path.add(--position);
                }
            }
        }

        // Similar to finding entrance, except you're looking for the bottom-right free cell
        for (int j = width-2; j > 0 && !markedEnding; j--) {
            for (int i = height-2; i > 0 && !markedEnding; i--) {
                if (path.contains(i*width + j)) {
                    markedEnding = true;
                    int position = i*width + j;
                    while (position % width != width-1) path.add(++position);
                }
            }
        }

        return path;
    }
}