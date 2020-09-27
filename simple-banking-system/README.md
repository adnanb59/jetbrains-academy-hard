# simple-banking-system

This program simulates a simple banking system where users can create accounts and do simple transaction operations.
These include things like checking your balance, depositing & transferring money as well as the option to even close your account.

This program, however, has the added bonus of using a database to store accounts and maintain the right balances.
Therefore, once you exit the program and run it again, you can use credentials created before and expect that it still works (with accurate balances as well).

You could say that the simplicity of this banking system comes with the operations that can be done by a user. However, it's important to point out that the program is simple as well in terms of security. All data stored in the DB is stored as-is, no encryption/decryption.

The bank has a hard coded institution identification number (IIN) of 400000 (which can be verified on account creation, as all accounts start with it). This can be modified in the code, or you can add it as a command line argument, therefore allowing for more banks and various accounts. Account numbers are 16 digits, however the digits after the IIN aren't exactly any random numbers. The 9 numbers after (can refer to it as the core account number) are random, however the whole account number passes [Luhn's algorithm](https://en.wikipedia.org/wiki/Luhn_algorithm), therefore the last digit is a check digit.

#### Running program
This program contains a persistence layer where bank updates are stored in a DB. This program is set up to run with MySQL (but can be easily
modified to work with SQLite).
- To use SQLite instead of MySQL, remove the MySQL driver [addition](https://github.com/adnanizm/jetbrains-academy-hard/blob/a1565a81f07d91da9d331fe171c411fb06180dc3/simple-banking-system/src/Runner.java#L15) and change the [host url](https://github.com/adnanizm/jetbrains-academy-hard/blob/a1565a81f07d91da9d331fe171c411fb06180dc3/simple-banking-system/src/banking/BackendService.java#L28) to sqlite.

However, if you choose to use mySQL, you will need to download the MySQL JDBC library and compile it with the program.
There are a number of ways to do this, [here](https://stackoverflow.com/a/2840358) is an explanation that can help out.

After compiling program, run program with `java Runner [url]`, where url is the MySQL database url.

You are prompted with the bank's main menu:
```
1. Create an account
2. Log into account
0. Exit
```

Once you log in (assuming you've created an account), you will be prompted with the user menu with account options:
```
1. Balance
2. Add income
3. Do transfer
4. Close account
5. Log out
0. Exit
```
