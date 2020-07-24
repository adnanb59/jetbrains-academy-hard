package calculator;

import java.util.*;
import java.util.regex.*;
import java.math.BigInteger;

class Operation {
    // Map of operations and their priorities relative to each other
    private static HashMap<String, Integer> priorities = new HashMap<>(Map.of("+", 0, "-", 0,
            "*", 1, "/", 1,
            "^", 2));

    /**
    * Check whether first operation passed in has a higher priority than the second operation
    *
    * @param s - First operator
    * @param s1 - Second operator
    * @return True if first operator has a higher priority than the second in an expression
    */
    public static boolean hasHigherPriorityThan(String s, String s1) {
        return priorities.get(s) > priorities.get(s1);
    }

    /**
    * Take the operator and operands and calculate result
    *
    * @param op - Operation
    * @param vA - First operand
    * @param vB - Second operand
    * @return Result of operation between both values
    */
    public static BigInteger operate(String op, String vA, String vB) {
        BigInteger res = BigInteger.ZERO, v1 = new BigInteger(vA), v2 = new BigInteger(vB);
        switch(op) {
            case "+":
                res = v1.add(v2);
                break;
            case "-":
                res = v2.subtract(v1);
                break;
            case "*":
                res = v2.multiply(v1);
                break;
            case "/":
                res = v2.divide(v1);
                break;
            case "^":
                res = v2.pow(v1.intValue());
                break;
            default:
                break;
        }
        return res;
    }
}

public class SmartCalculator {
    private String operation = "(\\++|-+|\\*|/|\\^)";
    private Map<String, BigInteger> variables;
    private Pattern invId, opPattern, varPattern;

    /** SmartCalculator constructor */
    public SmartCalculator() {
        variables = new HashMap<>();
        invId = Pattern.compile("([0-9][a-zA-Z]|[a-zA-Z][0-9])");
        opPattern = Pattern.compile(operation);
        varPattern = Pattern.compile("(-?[a-zA-Z]+|-?\\d+)");
    }

    /**
    * Evaluate expression passed in and return result (or null if equation is invalid)
    *
    * @param equation - Expression to be evaluated
    * @return Result of equation
    */
    private BigInteger evaluateExpression(String equation) {
        Deque<String> first = new LinkedList<>();
        Deque<String> second = new LinkedList<>();

        boolean isValid = true, checkForValue = true;
        Matcher operationMatcher = opPattern.matcher(equation), varOrValMatcher = varPattern.matcher(equation);

        // Go through characters of equation to parse brackets, values, variables and operators
        // Parse information and store into different stacks
        for (int i = 0; i < equation.length() && isValid; i++) {
            if (equation.charAt(i) == ' ') continue;
            else if (equation.charAt(i) == '(') {
                first.addFirst("("); // If left parenthesis, push onto first stack
            } else if (equation.charAt(i) == ')') {
                // Go through stack until you find the left parenthesis or the stack is emptied
                while (!first.isEmpty() && !first.peekFirst().equals("(")) {
                    second.addLast(first.pollFirst());
                }
                // Remove the left parenthesis or terminate algo (set flag for exit)
                if (first.isEmpty()) isValid = false;
                else first.pollFirst();
            } else if (checkForValue && varOrValMatcher.find(i)) {
                // Add value or variable to second stack and flip type checking indicator to check for operator
                second.addLast(varOrValMatcher.group());
                checkForValue = false;
                i = varOrValMatcher.end() - 1;
            } else if (!checkForValue && operationMatcher.find(i)) {
                String o = operationMatcher.group();
                // If operation is +, only use +, if it's -..., convert to + if operator length is even, o/w -
                if (o.charAt(0) == '+' && o.length() > 1) o = "+";
                else if (o.charAt(0) == '-' && o.length() > 1) o = o.length() % 2 == 0 ? "+" : "-";
                // If incoming operator is less than or equal to that on the stack,
                // or you reach the end of the stack or a left parenthesis,
                // pop the operator off the first stack and push it onto the second
                while (!first.isEmpty() && !first.peekFirst().equals("(") &&
                        !Operation.hasHigherPriorityThan(o, first.peekFirst())) {
                    second.addLast(first.pollFirst());
                }
                first.addFirst(o);
                checkForValue = true;
                i = operationMatcher.end() - 1;
            }
        }

        // Empty out operators in the first stack into the second stack
        while (!first.isEmpty() && isValid) {
            isValid = !first.peekFirst().equals("(");
            if (isValid) second.addLast(first.pollFirst());
        }

        // Go through second stack and re-insert values into first stack,
        // evaluating operations as operators are read in the second stack
        while (!second.isEmpty() && isValid) {
            String s = second.pollFirst();
            if (s.matches("-?\\d+")) {
                first.addFirst(s);
            } else if (s.matches("-?[a-zA-Z]+")) {
                // Get value from variable and store it back on stack (if exists)
                BigInteger v = variables.get(s);
                if (v != null) first.addFirst(v.toString());
                else isValid = false;
            } else if (s.matches(operation)) {
                // If an operation is passed in, only calculate it if there's enough values on stack
                isValid = first.size() >= 2;
                if (isValid) {
                    BigInteger v = Operation.operate(s, first.pollFirst(), first.pollFirst());
                    first.addFirst(v.toString());
                }
            }
        }

        // If everything is fine, pop first element on first stack and return
        return !isValid || first.size() != 1 ? null : new BigInteger(first.pollFirst());
    }

    /**
    * Take an expression and evaluate it (either by storing a result to a variable or displaying it)
    *
    * @param prompt - Expression passed in from user (that isn't a command)
    */
    public void process(String prompt) {
        if (!prompt.isEmpty()) {
            boolean isLeftEqualRight = prompt.replaceAll("\\(", "").length() ==
                    prompt.replaceAll("\\)", "").length();
            int equalsCount = prompt.length() - prompt.replaceAll("=", "").length();
            Matcher mInvalidId = invId.matcher(prompt);
            if (mInvalidId.find() || equalsCount > 1 || !isLeftEqualRight) {
                System.out.println("Invalid expression");
            } else if (equalsCount == 1) {
                String[] var = prompt.split("\\s*=\\s*");
                BigInteger res = evaluateExpression(var[1]);
                if (res != null) variables.put(var[0], res);
                else System.out.println("Invalid expression");
            } else {
                BigInteger res = evaluateExpression(prompt);
                System.out.println(res != null ? res : "Invalid expression");
            }
        }
    }
}