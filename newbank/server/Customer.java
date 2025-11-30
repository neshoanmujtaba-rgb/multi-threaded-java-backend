package newbank.server;

import java.util.ArrayList;

public class Customer {
    private ArrayList<Account> accounts;
    private ArrayList<String> transactionHistory;

    public Customer() {
        accounts = new ArrayList<>();
        transactionHistory = new ArrayList<>();
    }

    public String accountsToString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < accounts.size(); i++) {
            s.append(accounts.get(i).toString());
            if (i < accounts.size() - 1) {
                s.append("\n");
            }
        }
        return s.toString();
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    // FR8 Close Account Functionality
    // Removes an account belonging to this Customer if it exists and balance is zero.
    public String removeAccount(String accountName) {
        Account accountInstance = getAccountByName(accountName);

        if (accountInstance == null) {
            return "FAIL: Account does not exist or belongs to another customer";
        }

        if (accountInstance.getBalance() != 0.00) {
            return "FAIL: Account balance must be £0.00 to close account";
        }

        accounts.remove(accountInstance);
        return "SUCCESS: Account closed successfully";
    }


    // NM 22/11/25: is this a duplicate of getAccountByName below?   
    public void addTransaction(String transaction) {
        transactionHistory.add(transaction);
    }

    public boolean hasAccount(String accountName) {
        for (Account a : accounts) {
            if (a.getAccountName().equalsIgnoreCase(accountName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAccounts() {
        return !accounts.isEmpty();
    }

    public Account getFirstAccount() {
        if (accounts.isEmpty()) {
            return null;
        }
        return accounts.get(0);
    }

    // Case-insensitive version from your branch
    public Account getAccountByName(String accountName) {
        for (Account a : accounts) {
            if (a.getAccountName().equalsIgnoreCase(accountName)) {
                return a;
            }
        }
        return null;
    }

    // Extra method from main branch
    public int getNumberOfAccounts() {
        return accounts.size();
    }

    public String getTransactionHistory() {
        String s = "Transaction History:\n";
        for(String t : transactionHistory) {
            s += t + "\n";
        }
        return s;
    }

}