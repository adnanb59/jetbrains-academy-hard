package minesweeper;

import java.util.*;

public class Game {
    private final int mines;
    private final int size;
    private final Piece[][] board;
    private final Piece[] numbers = {
            Piece.ONE, Piece.TWO,
            Piece.THREE, Piece.FOUR, Piece.FIVE,
            Piece.SIX, Piece.SEVEN, Piece.EIGHT
    };
    private final Set<Integer> mine_set;
    private final Set<Integer> flagged_set;
    private final Set<Integer> checked_set;
    private State state;
    boolean firstMove, hitMine;

    public Game(int N, int M) {
        this.size = N;
        this.mines = M;
        this.state = State.PLAYING;
        this.board = new Piece[N][N];
        this.mine_set = new HashSet<>();
        this.checked_set = new HashSet<>();
        this.flagged_set = new HashSet<>();
        this.firstMove = true;
        this.hitMine = false;
        for (Piece[] dp : board) Arrays.fill(dp, Piece.EMPTY);
    }

    /**
     * Mark the cell specified with a flag
     *
     * @param r - Row of cell to flag
     * @param c - Column of cell to flag
     * @return Whether or not the cell was flagged
     */
    private boolean flagCell(int r, int c) {
        int position = (r-1)*size + c;
        // If flagged, un-flag
        // Otherwise, if un-flagged and unchecked, flag
        if (flagged_set.contains(position)) {
            flagged_set.remove(position);
        } else if (!checked_set.contains(position)) {
            flagged_set.add(position);
        } else return false;

        return true;
    }

    /**
     * Free the specified cell (and if it is empty, free adjacent empty cells)
     *
     * @param r - Row of cell to free
     * @param c - Column of cell to free
     * @return Whether or not the cell (and possibly adjacent cells) was freed
     */
    private boolean freeCell(int r, int c) {
        int position = (r-1)*size + c;

        // If this is the first cell being freed, set up the mines and reveal the adjacent empty cells
        // Otherwise, free the cell (and more) if possible (meaning it hasn't been checked or flagged already)
        if (firstMove) {
            setUpMines(r-1, c-1);
            freeUpSpace(r-1, c-1);
            firstMove = false;
        } else if (!checked_set.contains(position) && !flagged_set.contains(position)) {
            // At the unchecked, un-flagged cell
            //  - if it's empty, free it (and more)
            //  - if it's a mine, BOOM (lose)
            //  - if it's a number, reveal only that
            if (board[r-1][c-1] != Piece.EMPTY) {
                checked_set.add(position);
                if (board[r-1][c-1] == Piece.MINE) hitMine = true;
            } else freeUpSpace(r-1, c-1);
        } else return false;

        return true;
    }

    /**
     * From the specified cell, reveal all the empty cells until the original cell
     * is covered by a "ring" of numbers (the counters adjacent to mines).
     *
     * @param row - Row of specified cell
     * @param col - Column of specified cell
     */
    private void freeUpSpace(int row, int col) {
        // This is essentially a modified BFS
        // It looks at adjacent cells for empty cells (but doesn't take mines) and reveals them
        List<Integer> stack = new LinkedList<>();
        stack.add(row*size + (col+1));
        while (!stack.isEmpty()) {
            int position = stack.remove(0);
            // Mark the cell as checked, and remove flags if they exist
            checked_set.add(position);
            flagged_set.remove(position);
            int c = (position-1) % size;
            int r = (position-1) / size;
            // If you are looking at an empty cell, then query adjacent cells
            if (board[r][c] != Piece.EMPTY) continue;
            for (int i = r-1; i <= r+1; i++) {
                if (i == -1 || i == size) continue;
                for (int j = c-1; j <= c+1; j++) {
                    if (j == -1 || j == size || (i == row && j == col)) continue;
                    // Add an unchecked cell, number or empty, to stack
                    int adj_pos = i*size + (j+1);
                    if (!checked_set.contains(adj_pos) && (board[i][j] != Piece.MINE)) stack.add(adj_pos);
                }
            }
        }
    }

    /**
     * Take the specified cell and create the mines around it.
     * Leave a one cell radius around the main cell free from mines
     *
     * @param r - Row of cell (first to be freed)
     * @param c - Column of cell (first to be freed)
     */
    private void setUpMines(int r, int c) {
        Random rand = new Random(System.currentTimeMillis());
        // Create the M mines
        for (int i = 0; i < mines; i++) {
            // Look for a free cell to place a mine
            boolean mine_placed = false;
            while (!mine_placed) {
                int mine_row = rand.nextInt(size);
                int mine_col = rand.nextInt(size);
                // A valid cell is not the reference cell and doesn't already contain a mine
                if (!(mine_row == r && mine_col == c) && board[mine_row][mine_col] != Piece.MINE) {
                    // Place mine on board
                    board[mine_row][mine_col] = Piece.MINE;
                    mine_set.add(mine_row*size + (mine_col+1));
                    for (int y = mine_row-1; y <= mine_row+1; y++) {
                        if (y == -1 || y == size) continue;
                        for (int x = mine_col-1; x <= mine_col+1; x++) {
                            if (x == -1 || x == size || (x == mine_col && y == mine_row) || board[y][x] == Piece.MINE) continue;
                            // Update the cells around with appropriate mine count
                            if (board[y][x] == Piece.EMPTY) board[y][x] = Piece.ONE;
                            else board[y][x] = numbers[Integer.parseInt(board[y][x].toString())];
                        }
                    }
                    mine_placed = true;
                }
            }
        }
    }

    /**
     * Make a move (flag or free) at the specified cell on the Minesweeper board.
     *
     * @param c - Column of cell
     * @param r - Row of cell
     * @param move_type - Move to make (valid: free, mine)
     * @return Whether or not the move is valid
     */
    public boolean makeMove(int c, int r, String move_type) {
        if (r < 1 || c < 1 || r > size || c > size) return false; // Invalid coordinate check

        // If valid move is entered, make it, otherwise exit
        boolean result;
        if (move_type.equals("free")) {
            result = freeCell(r, c);
        } else if (move_type.equals("mine")) {
            result = flagCell(r, c);
        } else result = false;

        if (!result) return false;

        // After move is made, check for conclusive state
        // Conclusive states:
        //  - WIN: all the mines have been flagged OR all the other cells have been freed
        //  - LOSE: A mine has been hit
        if (flagged_set.equals(mine_set)) state = State.WIN;
        else if (flagged_set.isEmpty() && Collections.disjoint(mine_set, checked_set) && checked_set.size() + mines == size*size) state = State.WIN;
        else if (hitMine) state = State.LOSE;
        return true;
    }

    /**
     * Return whether or not the game has finished by checking it's state for a completed state (Lose/Win).
     *
     * @return Whether or not the game has finished
     */
    public boolean isCompleted() {
        return this.state != State.PLAYING;
    }

    /**
     * Return whether or not the user has won the game (by checking game's state)
     *
     * @return Whether or not the game state is WIN
     */
    public boolean wonTheGame() {
        return this.state == State.WIN;
    }

    /**
     * String representation of Minesweeper board
     *
     * @return Minesweeper board
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size+2; i++) {
            sb.append("|");
            for (int j = 0; j < size; j++) {
                if (i == 0 || i == size+1) sb.append("-");
                else {
                    // Everything outside of this is formatting, this is the board
                    // Think of the game board as numbers as a grid from 1 to N*N (with N rows of N columns of #'s)
                    // This is a way of mapping the row and column to a specific number
                    int position = (i-1)*size + (j+1);
                    // What to print:
                    //  - Board value if it's been checked (or is a mine and one has been hit)
                    //  - FLAGGED, if cell was flagged for a mine
                    //  - UNREAD, cell hasn't been flagged or checked
                    if (checked_set.contains(position) || (board[i-1][j] == Piece.MINE && hitMine))
                        sb.append(board[i-1][j].toString());
                    else if (flagged_set.contains(position)) sb.append(Piece.FLAGGED.toString());
                    else sb.append(Piece.UNREAD.toString());
                }
            }
            sb.append("|\n");
        }
        return sb.toString();
    }
}