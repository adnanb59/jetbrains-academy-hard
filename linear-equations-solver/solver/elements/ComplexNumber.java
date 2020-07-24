package solver.elements;

import java.util.List;

public class ComplexNumber {
    private double real;
    private double imaginary;

    /** ComplexNumber copy constructor
     *
     * @param copy - The ComplexNumber instance to copy values from
     */
    public ComplexNumber(ComplexNumber copy) {
        real = copy.getReal();
        imaginary = copy.getImaginary();
    }

    /** ComplexNumber constructor for direct values
     *
     * @param r - value for real part of number
     * @param i - value for imaginary part of number
     */
    public ComplexNumber(Double r, Double i) {
        real = r;
        imaginary = i;
    }

    /** ComplexNumber constructor for direct values
     *
     * @param values - list of values for complex number (a tuple)
     */
    public ComplexNumber(List<Double> values) {
        real = values.get(0);
        imaginary = values.get(1);
    }

    /** Get real part of complex number
     *
     * @return Real portion of complex number
     */
    public Double getReal() { return real; }

    /** Get imaginary part of complex number
     *
     * @return Imaginary portion of complex number
     */
    public Double getImaginary() { return imaginary; }

    /** Add complex number passed in to current complex number
     *
     * @param c - Reference complex number to add values from
     */
    public void add(ComplexNumber c) {
        this.real += c.getReal();
        this.imaginary += c.getImaginary();
    }

    /** Take two complex numbers and return a new one that is their sum
     *
     * @param c1 - first complex number to add
     * @param c2 - second complex number to add
     * @return New complex number -> c1 + c2
     */
    public static ComplexNumber add(ComplexNumber c1, ComplexNumber c2) {
        return new ComplexNumber(c1.getReal() + c2.getReal(), c1.getImaginary() + c2.getImaginary());
    }

    /** Subtract complex number passed in to current complex number
     *
     * @param c - Reference complex number to subtract values from
     */
    public void subtract(ComplexNumber c) {
        this.real -= c.getReal();
        this.imaginary -= c.getImaginary();
    }

    /** Take two complex numbers and return a new one that is their difference
     *
     * @param c1 - first complex number to subtract
     * @param c2 - second complex number to subtract
     * @return New complex number -> c1 - c2
     */
    public static ComplexNumber subtract(ComplexNumber c1, ComplexNumber c2) {
        return new ComplexNumber(c1.getReal() - c2.getReal(), c1.getImaginary() - c2.getImaginary());
    }

    /** Multiply complex number passed in to current complex number
     *
     * @param c - Reference complex number to multiply values from
     */
    public void multiply(ComplexNumber c) {
        this.real = real*c.getReal() - imaginary*c.getImaginary();
        this.imaginary = real*c.getImaginary() + imaginary*c.getReal();
    }

    /** Take two complex numbers and return a new one that is their product
     *
     * @param c1 - first complex number to multiply
     * @param c2 - second complex number to multiply
     * @return New complex number -> c1 * c2
     */
    public static ComplexNumber multiply(ComplexNumber c1, ComplexNumber c2) {
        return new ComplexNumber(c1.getReal()*c2.getReal() - c1.getImaginary()*c2.getImaginary(),
                c1.getReal()*c2.getImaginary() + c1.getImaginary()*c2.getReal());
    }

    /** Divide complex number passed in to current complex number
     *
     * @param c - Reference complex number to divide values from
     */
    public void divide(ComplexNumber c) {
        double denominator = Math.pow(c.getReal(), 2) + Math.pow(c.getImaginary(), 2);
        double newImaginary = (imaginary*c.getReal() - real*c.getImaginary())/denominator;
        real = (real*c.getReal() + imaginary*c.getImaginary())/denominator;
        imaginary = newImaginary;
    }

    /** Take two complex numbers and return a new one that is their quotient
     *
     * @param c1 - first complex number to divide
     * @param c2 - second complex number to divide
     * @return New complex number -> c1 / c2
     */
    public static ComplexNumber divide(ComplexNumber c1, ComplexNumber c2) {
        double denominator = Math.pow(c2.getReal(), 2) + Math.pow(c2.getImaginary(), 2);
        double real = (c1.getReal()*c2.getReal() + c1.getImaginary()*c2.getImaginary())/denominator;
        double imaginary = (c1.getImaginary()*c2.getReal() - c1.getReal()*c2.getImaginary())/denominator;
        return new ComplexNumber(real, imaginary);
    }

    /** Check if complex number is zero
     *
     * @return True if both real & imaginary parts are zero, otherwise false
     */
    public boolean isZero() {
        return real == 0.0 && imaginary == 0.0;
    }

    /** Check if object passed in is equivalent to this complex number
     *
     * @param c - Complex Number to compare
     * @return Whether or not c is equivalent to this number
     */
    @Override
    public boolean equals(Object c) {
        if (this == c) return true;
        if (c == null || getClass() != c.getClass()) return false;
        ComplexNumber that = (ComplexNumber) c;
        return Double.compare(that.real, real) == 0 &&
                Double.compare(that.imaginary, imaginary) == 0;
    }

    /** ComplexNumber string representation */
    @Override
    public String toString() {
        return (real != 0.0 || imaginary == 0.0 ? real : "") +
                (real != 0.0 && imaginary > 0.0 ? "+" : "") +
                (imaginary != 0.0 ? imaginary : "") +
                (imaginary != 0.0 ? "i" : "");
    }
}