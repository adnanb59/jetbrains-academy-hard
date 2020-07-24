package solver.commands;

import solver.elements.*;
import java.util.*;

public class RowSwapCommand implements Command {
    private List<LinearEquation> m;
    private int a, b;

    /** RowSwapCommand constructor
     *
     * @param eqns - Equations that will be modified by command
     */
    public RowSwapCommand(List<LinearEquation> eqns) {
        m = eqns;
    }

    /** Set the rows of the matrix that will be swapped by command
     *
     * @param a - first row that will be swapped
     * @param b - second row that will be swapped
     */
    public void setRows(int a, int b) {
        this.a = a;
        this.b = b;
    }

    /** Execute command (Swap the rows in the matrix) */
    @Override
    public void execute() {
        Collections.swap(m, a, b);
    }
}
