package maze;

import java.util.*;

public class Maze {
    private final int height, width;
    private final Set<Integer> path;
    private Set<Integer> escape;

    /**
    * Maze constructor (randomly generated maze of given dimensions)
    *
    * @param height - height of maze
    * @param width - width of maze
    */
    public Maze(int height, int width) {
        this.height = height;
        this.width = width;
        this.path = MazeGenerator.generate(height, width);
        this.escape = null;
    }

    /**
     * Maze constructor (with maze path passed in)
     *
     * @param height - height of maze
     * @param width - width of maze
     * @param path - Maze path passed in
     */
    public Maze(int height, int width, Set<Integer> path) {
        this.height = height;
        this.width = width;
        this.path = path;
    }

    /**
    * Get width of maze
    *
    * @return width of maze
    */
    public int getWidth() {
        return width;
    }

    /**
    * Get height of maze
    *
    * @return height of maze
    */
    public int getHeight() {
        return height;
    }

    /**
    * Get set of cells representing the path of the maze
    *
    * @return path cell set
    */
    public Set<Integer> getPath() {
        return path;
    }

    /**
     * Display path from entrance to exit (the escape) in the maze
     *
     * @return String representation of the maze with the escape labelled
     */
    public String showEscape() {
        // The escape calculation can be time-consuming, therefore, if it's been calculated once, it's stored
        if (escape == null) {
            // Finding the escape involves the use of 2 breadth-first searches (one starting from the start,
            // one from the end) (it's a bidirectional search)
            int intersection = -1, pos_forward = 0, pos_backward = 0,
                    s = path.stream().filter(e -> e % width == 0).findFirst().orElse(-1),
                    t = path.stream().filter(e -> e % width == width-1).findFirst().orElse(-1);
            if (s == -1 || t == -1) return toString(); // catch whether or not there is an entrance or exit

            List<Integer> queue_fwd = new ArrayList<>(), queue_bwd = new ArrayList<>();
            Map<Integer, Integer> parents_fwd = new HashMap<>(), parents_bwd = new HashMap<>();
            int[] directions = {-width, 1, width, -1}; // offsets to calculate adjacent cells
            queue_fwd.add(s);
            parents_fwd.put(s, null);
            queue_bwd.add(t);
            parents_bwd.put(t, null);

            // Go until you've found the intersecting cell or you have no more cells to view in the either queues
            // Because deleting the first element (and unshifting list) is costly, indices are used to keep
            // track of queue positions
            while ((pos_forward < queue_fwd.size() || pos_backward < queue_bwd.size()) && intersection == -1) {
                // If forward queue has elements
                if (pos_forward < queue_fwd.size()) {
                    // Get element from queue
                    // If it has been accessed in other search then the intersection has been found
                    int v = queue_fwd.get(pos_forward++);
                    if (parents_bwd.containsKey(v)) intersection = v;
                    else {
                        // Go through possible directions for cells in path that haven't been visited
                        for (int d : directions) {
                            if (path.contains(v+d) && !parents_fwd.containsKey(v+d)) {
                                queue_fwd.add(v+d);
                                parents_fwd.put(v+d, v);
                            }
                        }
                    }
                }

                if (pos_backward < queue_bwd.size() && intersection == -1) {
                    // Get element from queue
                    // If it has been accessed in other search then the intersection has been found
                    int v = queue_bwd.get(pos_backward++);
                    if (parents_fwd.containsKey(v)) intersection = v;
                    else {
                        // Go through possible directions for cells in path that haven't been visited
                        for (int d : directions) {
                            if (path.contains(v-d) && !parents_bwd.containsKey(v-d)) {
                                queue_bwd.add(v-d);
                                parents_bwd.put(v-d, v);
                            }
                        }
                    }
                }

                System.out.println();
            }

            // If intersection has been found, create escape
            if (intersection != -1) {
                escape = new HashSet<>();
                Integer b = intersection, f = intersection;
                // From the intersection, go through parents (in both directions) till you
                // reach the starts (entrance and exit)
                while (f != null || b != null) {
                    if (f != null) {
                        escape.add(f);
                        f = parents_fwd.get(f);
                    }
                    if (b != null) {
                        escape.add(b);
                        b = parents_bwd.get(b);
                    }
                }
            }
        }

        return display(true);
    }

    /**
    * Return a string representation of the maze (with escape highlighted if wanted by user)
    *
    * @param display_escape - Whether or not the escape path should be printed
    * @return String representation of Maze
    */
    public String display(boolean display_escape) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!path.contains(i*width + j)) sb.append("\u2588\u2588");
                else if (display_escape && escape != null && escape.contains(i*width + j)) sb.append("//");
                else sb.append("  ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
    * @return String representation of Maze
    */
    @Override
    public String toString() {
        return display(false);
    }
}