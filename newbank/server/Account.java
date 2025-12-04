package newbank.server;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Account {
    private String accountName;
    private double openingBalance;
    private List<String> transactions;
    private static final DateTimeFormatter TIMESTAMP_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Account(String accountName, double openingBalance) {
        this.accountName = accountName;
        this.openingBalance = openingBalance;
        this.transactions = new ArrayList<>();
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        transactions.add(timestamp + " | Account created with balance: £" + openingBalance);
    }

    @Override
    public String toString() {
        return accountName + ": £" + openingBalance;
    }

    public String getAccountName() {
        return accountName;
    }

    public double getAccountBalance(){
        return openingBalance;
    }

    public enum DebitOutcome {
        NON_POSITIVE_AMOUNT,
        INSUFFICIENT_FUNDS,
        SUCCESS
    }

    public DebitOutcome debit(double amount) {
        if(amount <= 0) {
            return DebitOutcome.NON_POSITIVE_AMOUNT;
        }
        if (openingBalance < amount) {
            return DebitOutcome.INSUFFICIENT_FUNDS;
        }
        openingBalance -= amount;
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        transactions.add(timestamp + " | Debited £" + amount + ", New Balance: £" + openingBalance);
        return DebitOutcome.SUCCESS;
    }

    public void credit(double amount) {
        if (amount > 0) {
            openingBalance += amount;
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            transactions.add(timestamp + " | Credited £" + amount + ", New Balance: £" + openingBalance);
        }
    }

    // FR10.1: New methods for explicit transfer operations with timestamps
    public void withdraw(double amount) {
        if (amount > 0 && openingBalance >= amount) {
            openingBalance -= amount;
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            transactions.add(timestamp + " | Withdrawn £" + amount + ", New Balance: £" + openingBalance);
        }
    }

    public void deposit(double amount) {
        if (amount > 0) {
            openingBalance += amount;
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            transactions.add(timestamp + " | Deposited £" + amount + ", New Balance: £" + openingBalance);
        }
    }

    public double getBalance() {
        return openingBalance;
    }

    public List<String> getTransactions() {
        return transactions;
    }
}