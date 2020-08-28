package banking;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class BackendService {
    private Connection c;

    private BackendService(Connection conn) {
        this.c = conn;
    }

    public static BackendService initConnection(String url) {
        try {
            Connection potentialConn = DriverManager.getConnection("jdbc:mysql://" + url);
            try (Statement st = potentialConn.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS card(id INTEGER, number TEXT, pin TEXT, balance INTEGER DEFAULT 0)");
            }
            return new BackendService(potentialConn);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }
        return null;
    }

    public long findAccount(String acct, String pin) {
        String query = String.format("SELECT * FROM card WHERE number='%s'", acct);
        if (pin != null) query = query + String.format(" AND pin='%s'", pin);
        Map<String, Object> res = doQuery(query);
        return res.size() == 0 ? -1 : (long) res.get("balance");
    }

    private Map<String, Object> doQuery(String query) {
        Map<String, Object> res = null;
        try (Statement st = c.createStatement()) {
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

    public void closeConnection() {
        try {
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Account addAccount(long acctNum, String accountNumber, String pin) {
        String update = String.format("INSERT INTO card(id, number, pin) VALUES (%d, '%s', '%s')", acctNum, accountNumber, pin);
        return doUpdate(update) == 1 ? new Account(accountNumber, pin) : null;
    }

    public boolean updateFundsToAccount(String cardNumber, long income) {
        String update = String.format("UPDATE card SET balance=balance+%d WHERE number='%s'", income, cardNumber);
        return doUpdate(update) != 0;
    }

    public boolean transferFunds(String cardNumber, String card, long transfer) {
        boolean result = updateFundsToAccount(card, transfer);
        if (result) result = updateFundsToAccount(cardNumber, -transfer);
        return result;
    }

    public boolean deleteAccount(String cardNumber, String pin) {
        return doUpdate(String.format("DELETE FROM card WHERE number='%s' AND pin='%s'", cardNumber, pin)) != 0;
    }
}