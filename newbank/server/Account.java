package newbank.server;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private String accountName;
    private double openingBalance;
    private List<String> transactions;

    public Account(String accountName, double openingBalance) {
        this.accountName = accountName;
        this.openingBalance = openingBalance;
        this.transactions = new ArrayList<>();
        transactions.add("Account created with balance: £" + openingBalance);
    }

    @Override
    public String toString() {
        return accountName + ": £" + openingBalance;
    }

    public String getAccountName() {
        return accountName;
    }

    public boolean debit(double amount) {
        if (amount <= 0 || openingBalance < amount) {
            return false;
        }
        openingBalance -= amount;
        transactions.add("Debited £" + amount + ", New Balance: £" + openingBalance);
        return true;
    }

    public void credit(double amount) {
        if (amount > 0) {
            openingBalance += amount;
            transactions.add("Credited £" + amount + ", New Balance: £" + openingBalance);
        }
    }

    public double getBalance() {
        return openingBalance;
    }

    public List<String> getTransactions() {
        return transactions;
    }
}