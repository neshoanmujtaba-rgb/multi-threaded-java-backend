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
        return DebitOutcome.SUCCESS;
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