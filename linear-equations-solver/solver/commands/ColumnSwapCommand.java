package solver.commands;

import solver.elements.*;
import java.util.List;

public class ColumnSwapCommand implements Command {
    private List<LinearEquation> m;
    private int a, b;

    /** ColumnSwapCommand constructor
     *
     * @param eqns - Equations that will be modified by command
     */
    public ColumnSwapCommand(List<LinearEquation> eqns) {
        m = eqns;
    }

    /** Set columns that will be swapped
     *
     * @param a - first column to swap
     * @param b - second column to swap
     */
    public void setColumns(int a, int b) {
        this.a = a;
        this.b = b;
    }

    /** Execute column swap (go through equations and swap columns) */
    @Override
    public void execute() {
        for (LinearEquation e : m) {
            ComplexNumber temp = new ComplexNumber(e.getCoefficient(a));
            e.setCoefficient(a, e.getCoefficient(b));
            e.setCoefficient(b, temp);
        }
    }
}
