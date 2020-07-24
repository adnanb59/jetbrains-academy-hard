## linear-equations-solver

The program allows a user to provide a system of linear equations and provide a solution (if possible).
Coefficients for the variables in the system are complex numbers, so solutions can also be complex. 
You're essentially creating a matrix and a result and getting the solution vector (Ax = B).

The solution algorithm utilizes complete pivoting (so columns can be switched along with rows).
The architecture utilizes a Command pattern for individual actions involving row & column swaps and value updates.
If there are solutions, then they will be displayed, otherwise `No solutions` or `Infinitely many solutions`
will be given.

#### How it works
User is expected to provide an input file and output file.
The input file follows this specific format:
```
vars eqns
xii ... bi
... ... ...
... ... ...
```

Where `vars` and `eqns` are integer values that specify the number of coefficients per equation
as well as the number of equations. Each line contains `vars + 1` values for the coefficients and the solution.

#### Running Program
After compiling the files, you can run the program with
`java Runner -in <filename> -out <filename>`

##### URL: https://hyperskill.org/projects/40
