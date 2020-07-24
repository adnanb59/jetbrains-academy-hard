package solver.elements;

import solver.commands.*;
import java.util.*;
import java.util.stream.Collectors;

public class Matrix {
    private List<LinearEquation> eqns;
    private int numSolutions;
    private SolvedState state;
    // Commands (attempt at using Command pattern)
    private RowSwapCommand rsc;
    private RowFactorCommand rfc;
    private RowReduceCommand rrc;
    private ColumnSwapCommand csc;

    /** Matrix Constructor */
    public Matrix() {
        eqns = new ArrayList<>();
        numSolutions = 0;
        state = SolvedState.NOT_SOLVED;
        rsc = new RowSwapCommand(eqns);
        rfc = new RowFactorCommand();
        rrc = new RowReduceCommand();
        csc = new ColumnSwapCommand(eqns);
    }

    /**
     * Add the linear equation specified to the matrix.
     *
     * @param l - LinearEquation to add to Matrix
     */
    public void addEquation(LinearEquation l) {
        eqns.add(l);
    }

    /**
     * Get current state of matrix.
     *
     * @return State of matrix (in terms of being processed)
     */
    public SolvedState getSolutionState() {
        return state;
    }

    /**
     * Return the solutions of the solved matrix.
     *
     * @return Solutions of the matrix
     */
    public List<ComplexNumber> getSolutions() {
        if (state != SolvedState.ONE_SOLUTION) return state == SolvedState.INFINITE_SOLUTIONS ? null : List.of();
        return eqns.subList(0, numSolutions).stream().map(LinearEquation::getSolution)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Find row or column that can be swapped with current (row, col) coordinate to allow
     * solving algorithm to continue.
     *
     * @param start - starting (row, col) pair in matrix that is currently 0
     * @param max - Maximum (currently of rows) for algorithm to search through
     * @param isRow - Note to algorithm on whether to search the row or the column
     * @return Index of row/column (non-zero) that can be swapped with specified row/column (zero)
     */
    private int foundSwappable(int start, int max, boolean isRow) {
        int tmp = start;
        while (tmp < max && eqns.get(isRow ? tmp : start).getCoefficient(isRow ? start : tmp).isZero()) {
            tmp++;
        }
        return tmp < max ? tmp : -1;
    }

    /**
     * Search the lower left triangular portion of the matrix, starting from specified (row, col)
     * and see if there exists a coordinate that it can be swapped with (the nearest non-zero (row, col)).
     *
     * @param start - starting (row, col) pair in matrix that is currently 0 (matrix iterated through main diagonal)
     * @param max - Maximum (currently of rows) for algorithm to search through
     * @return The non-zero (row, col) pair that can be swapped with specified starting point
     */
    private List<Integer> foundSwappableLowerLeft(int start, int max) {
        List<Integer> l = new ArrayList<>();
        for (int i = start+1; i < max && l.size() == 0; i++) {
            for (int j = start; j <= i && l.size() == 0; j++) {
                if (!eqns.get(i).getCoefficient(j).isZero()) {
                    l.add(i);
                    l.add(j);
                }
            }
        }
        return l;
    }

    /** Main method to solve matrix */
    public void solve() {
        boolean canContinue = true;
        int N = eqns.size(), M = N > 0 ? eqns.get(0).getNumOfVariables() : 0, sigEqns = 0;
        ComplexNumber temp_factor;
        List<Integer> columnSwaps = new ArrayList<>();

        System.out.println("Row manipulation:");
        // Go down matrix to make matrix row-reduced echelon
        for (int i = 0; i < N; i++) {
            sigEqns = i;
            // There is no i'th variable in the i'th equation, exit algorithm
            if (eqns.get(i).getCoefficient(i) == null) {
                canContinue = false;
                break;
            }

            // If current (row, col) of matrix is 0, try to find position to
            // swap, if you can't find one then exit
            if (eqns.get(i).getCoefficient(i).isZero()) {
                // First, search down along column for non-zero value
                // and if found, swap rows
                int found = foundSwappable(i, N, true);
                if (found != -1) {
                    rsc.setRows(i, found);
                    rsc.execute();
                    System.out.println("R" + (i+1) + " <-> R" + (found+1));
                } else {
                    // If column couldn't find non-zero value, search along row for swapping partner
                    found = foundSwappable(i, M, false);
                    if (found != -1) {
                        csc.setColumns(i, found);
                        csc.execute();
                        System.out.println("C" + (i+1) + " <-> C" + (found+1));
                    }
                }

                // Adjacent row and column do not provide non-zero value for swapping
                // Need to search along the lower left triangle of the matrix for value
                if (found == -1) {
                    // Found non-zero value, get their coordinates and swap specified rows and columns
                    List<Integer> res = foundSwappableLowerLeft(i+1, N);
                    if (res.size() == 2) {
                        rsc.setRows(i, res.get(0));
                        rsc.execute();
                        System.out.println("R" + (i+1) + " <-> R" + (res.get(0) + 1));
                        csc.setColumns(i, res.get(1));
                        csc.execute();
                        System.out.println("C" + (i+1) + " <-> C" + (res.get(1) + 1));
                        columnSwaps.addAll(res);
                        found = 0;
                    }
                }

                // Still no row or column found, exit algorithm
                if (found == -1) {
                    canContinue = false;
                    break;
                }
            }

            // If coefficient along main diagonal is not 1 then factor row by coefficient before continuing
            if (eqns.get(i).getCoefficient(i).getReal() != 1.0 || eqns.get(i).getCoefficient(i).getImaginary() != 0.0) {
                rfc.setModifiedIndex(i);
                rfc.setModifiedEqn(eqns.get(i));
                rfc.execute();
                if (!(temp_factor = rfc.getFactor()).isZero()) {
                    System.out.println("R" + (i+1) + " / " + temp_factor.toString() + " -> R" + (i+1));
                    System.out.println(eqns.get(i));
                }
            }

            // From current row, go down remaining rows and reduce coefficients below to 0 (or skip,
            // if already is). While reducing coefficients below, adjust coefficients of entire equation.
            rrc.setModifierEqn(eqns.get(i));
            rrc.setPosition(i);
            for (int j = i+1; j < N; j++) {
                if (eqns.get(j).getCoefficient(i).isZero()) continue;
                rrc.setModifiedEqn(eqns.get(j));
                rrc.execute();
                if (!(temp_factor = rrc.getFactor()).isZero()) {
                    temp_factor.multiply(new ComplexNumber(-1.0, 0.0));
                    System.out.println(temp_factor.toString() + " * R" + (i+1) + " + R" + (j+1) + " -> R" + (j+1));
                }
            }
        }

        // Go through remaining rows (if any & check for invalid rows)
        if (!canContinue) {
            for (int i = N-1; i >= sigEqns; i--) {
                canContinue = eqns.get(i).isEquationPossible();
                if (!canContinue) break;
            }
            if (canContinue) sigEqns--; // there was an over counting of significant equations, roll back up
        }

        // If a conclusion has been reached (None or infinite), set their state
        // If there can be a solution (not fully solved), mark their state and complete solving
        if (!canContinue) state = SolvedState.NO_SOLUTION;
        else if (sigEqns+1 != M) state = SolvedState.INFINITE_SOLUTIONS;
        else {
            // Go back up matrix to complete row-echelon form
            for (int i = N - 1; i >= 0; i--) {
                rrc.setModifierEqn(eqns.get(i));
                for (int j = i - 1; j >= 0; j--) {
                    if (!eqns.get(j).isEquationPossible()) continue;
                    rrc.setPosition(i);
                    rrc.setModifiedEqn(eqns.get(j));
                    rrc.execute();
                    ComplexNumber t = rrc.getFactor();
                    t.multiply(new ComplexNumber(-1.0, 0.0));
                    if (!t.isZero()) {
                        System.out.println(t.toString() + " * R" + (i+1) + " + R" + (j+1) + " -> R" + (j+1));
                    }
                }
            }

            // If there were any columns that were swapped, go through them
            // and swap columns back (and swap rows to maintain identity matrix)
            for (int k = 0; k < columnSwaps.size(); k+=2) {
                csc.setColumns(columnSwaps.get(k), columnSwaps.get(k+1));
                csc.execute();
                rsc.setRows(columnSwaps.get(k), columnSwaps.get(k+1));
                rsc.execute();
            }
            state = SolvedState.ONE_SOLUTION;
            numSolutions = M;
        }
    }

    /** String representation of Matrix */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (LinearEquation a : eqns) {
            sb.append(a.toString()).append("\n");
        }
        return sb.toString();
    }
}