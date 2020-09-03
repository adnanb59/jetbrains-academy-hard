package banking;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class BackendService {
    private Connection c;

    /**
    * BackendService constructor.
    * Store connection to DB.
    * 
    * @param conn - JDBC connection
    */
    private BackendService(Connection conn) {
        this.c = conn;
    }

    /**
    * Initialize connection to DB and store it to a BackendService object for use by Bank.
    * 
    * @param url - Location of DB, used for connection
    * @return new BackendService object with Connection stored
    */
    public static BackendService initConnection(String url) {
        try {
            Connection potentialConn = DriverManager.getConnection("jdbc:mysql://" + url);
            // After getting connection, create CARD table if it doesn't exist
            try (Statement st = potentialConn.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS card(id INTEGER, number TEXT, pin TEXT, balance INTEGER DEFAULT 0)");
            }
            return new BackendService(potentialConn);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }
        return null; // If connection initialization failed, return null
    }

    /**
    * Find account in DB with passed in account # and/or PIN.
    * 
    * @param acct - Account number to search for
    * @param pin - Account's associated PIN (or null if not being checked)
    * @return Account balance if account exists or -1
    */
    public long findAccount(String acct, String pin) {
        String query = String.format("SELECT * FROM card WHERE number='%s'", acct);
        if (pin != null) query = query + String.format(" AND pin='%s'", pin);
        Map<String, Object> res = doQuery(query);
        return res.size() == 0 ? -1 : (long) res.get("balance");
    }

    /**
    * Run the SQL query against stored DB and return resultant row (as map)
    * 
    * @param query - SQL query to run 
    * @return Map of attribute-value pairs to represent a row
    */
    private Map<String, Object> doQuery(String query) {
        Map<String, Object> res = null;
        try (Statement st = c.createStatement()) {
            // Execute SQL query, take the resultset and put it into map
            // if no results, map will be empty
            ResultSet query_results = st.executeQuery(query);
            res = new HashMap<>();
            while (query_results.next()) {
                res.put("balance", query_results.getLong("balance"));
                res.put("number", query_results.getString("number"));
                res.put("pin", query_results.getString("pin"));
            }
            query_results.close();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }
        return res;
    }

    /**
    * Run the SQL update passed in and return the number of rows affected
    * 
    * @param update - SQL update to run
    * @return number of rows affected
    */
    private int doUpdate(String update) {
        int rows = 0;
        try (Statement st = c.createStatement()) {
            rows = st.executeUpdate(update);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }
        return rows;
    }

    /** Close DB connection */
    public void closeConnection() {
        try {
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
    * Add account (with passed in information) to DB
    * 
    * @param acctNum - core account number
    * @param accountNumber - IIN + account number + checksum (as string representation)
    * @param pin - account PIN
    * @return Account object if created or null if failed
    */
    public Account addAccount(long acctNum, String accountNumber, String pin) {
        String update = String.format("INSERT INTO card(id, number, pin) VALUES (%d, '%s', '%s')", acctNum, accountNumber, pin);
        // if there has been affected rows, return new Account object
        return doUpdate(update) == 1 ? new Account(accountNumber, pin) : null;
    }

    /**
    * Update the funds in <cardNumber>'s account by added <income> (income can be -ve, therefore reduced).
    * If account doesn't exist, nothing will happen.
    * 
    * @param account - account number
    * @param income - amount to modify balance by (can be -ve to decrease amount)
    * @return Whether or not account's funds have been modified
    */
    public boolean updateFundsToAccount(String account, long income) {
        String update = String.format("UPDATE card SET balance=balance+%d WHERE number='%s'", income, account);
        return doUpdate(update) != 0;
    }

    /**
    * Transfer funds (amount) from account <sender> to <receiver> and return if transfer was successful.
    * 
    * @param sender - Account sending funds
    * @param receiver - Account receiving funds
    * @param amount - Amount to transfer
    * @return Whether or not transfer was successful
    */
    public boolean transferFunds(String sender, String receiver, long amount) {
        // First update holder account's funds, then receiver account's funds
        // Not the best solution in terms of atomic instructions (and dealing with an error in between),
        // however it's due to the simplicity of the project. If first update fails, second one doesn't happen.
        boolean result = updateFundsToAccount(sender, -amount);
        if (result) result = updateFundsToAccount(receiver, amount);
        return result;
    }

    /**
    * Delete account passed in from DB.
    * Both account number and pin are checked before deleting account.
    * 
    * @param account - account number
    * @param pin - account pin
    * @return Whether or not deletion was successful
    */
    public boolean deleteAccount(String account, String pin) {
        return doUpdate(String.format("DELETE FROM card WHERE number='%s' AND pin='%s'", account, pin)) != 0;
    }
}