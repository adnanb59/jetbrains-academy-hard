## budget-manager

This program serves as a budget managing application where a user can manage their income and spending.
On top of just adding purchases, the user can view their categories as well as see where they've spent the most.
They are also able to load and save transactions from a file, therefore the application data will not just live and die with the running program.

#### Running program
After compiling program, run with `java Runner`.

The user will be prompted with menu to manage purchases and budget.
```
Choose your action:
1) Add income
2) Add purchase
3) Show list of purchases
4) Balance
5) Save
6) Load
7) Analyze (Sort)
0) Exit
```

If user chooses to add a purchase, they will have a choice of the following categories to place it in: food, clothing, entertainment or other.
The categories are defined [here](budget-manager/src/budget/util/ItemType.java).

If user chooses to save or load purchases, they will be written to or read from `purchases.txt`.

If user chooses to sort their purchases, they will be prompted with another menu where they can choose to sort all purchases, purchases of one type or sort the categories of purchases.

#### URL: https://hyperskill.org/projects/76
