package solver.elements;

import java.util.*;

public class LinearEquation {
    private int size;
    private ComplexNumber sum;
    private List<ComplexNumber> coefficients;
    private ComplexNumber solution;

    /**
     * LinearEquation constructor
     *
     * @param size - specified size of linear equation
     * @param equation - array of coefficients
     */
    public LinearEquation(int size, List<ComplexNumber> equation) {
        this.size = size;
        coefficients = new ArrayList<>(equation.subList(0, size));
        solution = equation.remove(size);
        sum = new ComplexNumber(coefficients.get(0));
        for (int i = 1; i < size; i++) sum.add(coefficients.get(i));
    }

    /**
     * Set the value of the specified coefficient in the linear equation.
     *
     * @param idx - Coefficient to update
     * @param v - New value of coefficient
     */
    public void setCoefficient(int idx, ComplexNumber v) {
        if (idx < size) {
            ComplexNumber temp = new ComplexNumber(coefficients.get(idx));
            coefficients.set(idx, v);
            sum.add(ComplexNumber.subtract(v, temp));
        }
    }

    /**
     * Get value of i'th coefficient.
     *
     * @param position - position to get coefficient from
     * @return position'th coefficient or null (if it doesn't exist)
     */
    public ComplexNumber getCoefficient(int position) {
        return position < coefficients.size() ? coefficients.get(position) : null;
    }

    /**
     * Get the number of the variables in the equation.
     *
     * @return Get number of variables in the equation
     */
    public int getNumOfVariables() { return size; }

    /**
     * Get the solution for the linear equation.
     *
     * @return Solution for equation (or null if none specified)
     */
    public ComplexNumber getSolution() { return solution; }

    /**
     * Figure out if this equation is not contradictory, in that
     * the sum of the coefficients is not zero or the solution is zero.
     *
     * @return Whether or not the current equation is contradictory or makes sense
     */
    public boolean isEquationPossible() { return solution.isZero() || !sum.isZero(); }

    /** Reduce row by the inverse of the coefficient specified in parameter.
     *
     * @param idx - specific coefficient to reduce equation by
     * @return factor that reduces coefficients & solution of equation
     */
    public ComplexNumber factorEquation(int idx) {
        ComplexNumber factor = new ComplexNumber(coefficients.get(idx));
        for (int i = idx; i < size; i++) {
            coefficients.get(i).divide(factor);
        }
        solution.divide(factor);
        sum.divide(factor);
        return factor;
    }

    /** Reduce row by factor of the specified linear equation's coefficient (specified by position).
     *
     * @param position - Specific coefficient to get
     * @param b - Equation to get coefficient from
     * @return factor reducing equation (coefficients & solution)
     */
    public ComplexNumber modifyEquation(int position, LinearEquation b) {
        ComplexNumber factor = position < size ? new ComplexNumber(coefficients.get(position)) : new ComplexNumber(0.0, 0.0);
        if (!factor.isZero()) {
            factor.divide(b.getCoefficient(position));
            solution.subtract(ComplexNumber.multiply(factor, b.getSolution()));
            for (int i = position; i < size; i++) {
                coefficients.get(i).subtract(ComplexNumber.multiply(factor, b.getCoefficient(i)));
                sum.subtract(ComplexNumber.multiply(factor, b.getCoefficient(i)));
            }
        }
        return factor;
    }

    /** String representation of LinearEquation */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coefficients.size(); i++) {
            if (i != 0) {
                if (coefficients.get(i).getReal() > 0 ||
                        (coefficients.get(i).getReal()== 0 && coefficients.get(i).getImaginary() > 0)) {
                    sb.append("+ ");
                }
            }
            sb.append(coefficients.get(i).toString()).append(" ");
        }
        sb.append("= ").append(solution.toString());
        return sb.toString();
    }
}