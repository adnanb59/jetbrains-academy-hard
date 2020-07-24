package solver.commands;

import solver.elements.*;

public class RowFactorCommand implements Command {
    private LinearEquation a;
    private int index;
    private ComplexNumber factor;

    /** RowFactorCommand constructor */
    public RowFactorCommand() {
        factor = new ComplexNumber(0.0 ,0.0);
    }

    /** Set Linear equation that will be modified
     *
     * @param l - Equation that will be modified
     */
    public void setModifiedEqn(LinearEquation l) {
        a = l;
    }

    /** Set index that will be the position that will be referenced in swap
     *
     * @param idx - position in row that will be basis for factor
     */
    public void setModifiedIndex(int idx) {
        index = idx;
    }

    /** Return factor of row reduction
     *
     * @return Complex Number that represents factor used to divide row
     */
    public ComplexNumber getFactor() {
        return factor;
    }

    /** Execute command (reduce the row by position passed in) */
    @Override
    public void execute() {
        factor = a.factorEquation(index);
    }
}
