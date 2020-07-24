package solver.commands;

import solver.elements.*;

public class RowReduceCommand implements Command {
    private LinearEquation a, b;
    private int idx;
    private ComplexNumber factor;

    /** RowReduceCommand constructor */
    public RowReduceCommand() {
        factor = new ComplexNumber(0.0 ,0.0);
    }

    /** Set the equation that will be modified by a factor of another equation */
    public void setModifiedEqn(LinearEquation l) {
        a = l;
    }

    /** Set the equation that will modify the other equation */
    public void setModifierEqn(LinearEquation l) {
        b = l;
    }

    /** Set the position that will be used by the reference equation
     *  to modify the target equation
     *
     * @param p - position that will be used by reference equation
     */
    public void setPosition(int p) {
        idx = p;
    }

    /** Get factor that was used to reduce target equation's coefficients
     *
     * @return Complex Number representing factor used to reduce equation
     */
    public ComplexNumber getFactor() {
        return factor;
    }

    /** Execute command (reduce the target equation using the reference equation
     *  and the position it used)
     */
    @Override
    public void execute() {
        factor = a.modifyEquation(idx, b);
    }
}