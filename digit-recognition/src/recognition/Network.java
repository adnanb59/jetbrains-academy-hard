package recognition;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class Network implements Serializable {
    private static final long serialVersionUID = 7L;
    private static int[][] idealActivations;
    private double[][] weights;
    private int[] biases;
    private double[] outputs;
    private double LEARNING_COEFFICIENT = 0.5;

    public Network() {
        // Math.random() to create number between 0 and 1
        idealActivations = new int[][]{
                { 1,  1,  1,  1,  0,  1,  1,  0,  1,  1,  0,  1,  1,  1,  1},
                { 0,  1,  0,  0,  1,  0,  0,  1,  0,  0,  1,  0,  0,  1,  0},
                { 1,  1,  1,  0,  0,  1,  1,  1,  1,  1,  0,  0,  1,  1,  1},
                { 1,  1,  1,  0,  0,  1,  1,  1,  1,  0,  0,  1,  1,  1,  1},
                { 1,  0,  1,  1,  0,  1,  1,  1,  1,  0,  0,  1,  0,  0,  1},
                { 1,  1,  1,  1,  0,  0,  1,  1,  1,  0,  0,  1,  1,  1,  1},
                { 1,  1,  1,  1,  0,  0,  1,  1,  1,  1,  0,  1,  1,  1,  1},
                { 1,  1,  1,  0,  0,  1,  0,  0,  1,  0,  0,  1,  0,  0,  1},
                { 1,  1,  1,  1,  0,  1,  1,  1,  1,  1,  0,  1,  1,  1,  1},
                { 1,  1,  1,  1,  0,  1,  1,  1,  1,  0,  0,  1,  1,  1,  1}
        };
        biases = new int[]{-1, 6, 1, 0, 2, 0, -1, 3, -2, -1};
        weights = new double[10][15];
        outputs = new double[10];
        Random r = new Random();
        for (int i = 0; i < weights.length; i++) {
            Arrays.fill(weights[i], r.nextGaussian());
        }
    }

    public double sigmoid(double value) {
        return 1/(1 + Math.exp(-value));
    }

    /*private void writeObject(ObjectOutputStream oos) throws Exception {
        // write the custom serialization code here
    }

    private void readObject(ObjectInputStream ois) throws Exception {
        // write the custom deserialization code here
    }*/
}
