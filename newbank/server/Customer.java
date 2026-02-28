// Define the package for this class - part of the banking server
package newbank.server;

// Import ArrayList for storing customer's accounts and transaction history
import java.util.ArrayList;

/**
 * Customer class represents a bank customer who can own multiple accounts.
 * Each customer has a list of accounts and a transaction history.
 * Customers can perform operations like creating/closing accounts and viewing transactions.
 */
public class Customer {
    // ArrayList to store all Account objects belonging to this customer
    private ArrayList<Account> accounts;
    // ArrayList to store transaction history records for this customer
    private ArrayList<String> transactionHistory;

    /**
     * Constructor: Creates a new Customer with empty accounts and transaction history
     */
    public Customer() {
        // Initialize the accounts ArrayList (starts empty, accounts added later)
        accounts = new ArrayList<>();
        // Initialize the transactionHistory ArrayList (starts empty, transactions added later)
        transactionHistory = new ArrayList<>();
    }

    /**
     * Converts all customer's accounts to a readable string representation
     * Used to display all accounts and their balances at once
     * @return String - Formatted string with each account on a new line
     */
    public String accountsToString() {
        // Create a StringBuilder object for efficient string building
        StringBuilder s = new StringBuilder();
        // Loop through each account in the accounts list with index
        for (int i = 0; i < accounts.size(); i++) {
            // Append the current account's toString() representation to the StringBuilder
            s.append(accounts.get(i).toString());
            // If this is not the last account (check if i < size - 1), add a newline for formatting
            if (i < accounts.size() - 1) {
                // Add newline character to separate accounts
                s.append("\n");
            }
        }
        // Convert the StringBuilder to a String and return it
        return s.toString();
    }

    /**
     * Adds a new account to this customer's account list
     * @param account - The Account object to add to this customer
     */
    public void addAccount(Account account) {
        // Add the provided account object to the accounts ArrayList
        accounts.add(account);
    }

    /**
     * Feature Request 8: Close Account Functionality
     * Removes an account from this customer's account list if it exists and has zero balance
     * This ensures financial safety - can't close accounts with remaining funds
     * @param accountName - The name of the account to close/remove
     * @return String - SUCCESS message if account closed, FAIL message with reason if not
     */
    public String removeAccount(String accountName) {
        // Call getAccountByName to find the Account object with matching name
        Account accountInstance = getAccountByName(accountName);

        // Check if account was found (getAccountByName returns null if not found)
        if (accountInstance == null) {
            // Return failure message indicating account doesn't exist or belongs to another customer
            return "FAIL: Account does not exist or belongs to another customer";
        }

        // Check if account balance is exactly zero (can only close zero-balance accounts)
        if (accountInstance.getBalance() != 0.00) {
            // Return failure message indicating customer must empty account before closing
            return "FAIL: Account balance must be £0.00 to close account";
        }

        // Remove the specific account object from the accounts ArrayList
        accounts.remove(accountInstance);
        // Return success message confirming account was closed successfully
        return "SUCCESS: Account closed successfully";
    }

    /**
     * Adds a transaction record to this customer's transaction history
     * Note (NM 22/11/25): This may be a duplicate of getAccountByName below - review needed
     * @param transaction - The transaction description string to add to history
     */
    public void addTransaction(String transaction) {
        // Add the transaction string to the transactionHistory ArrayList
        transactionHistory.add(transaction);
    }

    /**
     * Checks if this customer has an account with the given name
     * Uses case-insensitive comparison so "Main" and "main" are treated the same
     * @param accountName - The name of the account to check for
     * @return boolean - true if account exists, false if not found
     */
    public boolean hasAccount(String accountName) {
        // Loop through each account in the accounts list
        for (Account a : accounts) {
            // Use case-insensitive comparison: convert both to same case and compare
            if (a.getAccountName().equalsIgnoreCase(accountName)) {
                // Return true immediately if matching account is found
                return true;
            }
        }
        // Return false if loop completes with no matching account found
        return false;
    }

    /**
     * Checks if this customer has at least one account
     * @return boolean - true if customer has one or more accounts, false if no accounts
     */
    public boolean hasAccounts() {
        // Return the negation of isEmpty() - if list is not empty, return true
        return !accounts.isEmpty();
    }

    /**
     * Gets the first account from this customer's account list
     * Useful for default account operations (e.g., most payments come from first account)
     * @return Account - The first account in the list, or null if customer has no accounts
     */
    public Account getFirstAccount() {
        // Check if accounts list is empty (has no accounts)
        if (accounts.isEmpty()) {
            // Return null to indicate no accounts exist
            return null;
        }
        // Return the first account in the list (index 0 is the first)
        return accounts.get(0);
    }

    /**
     * Gets an account by name (case-insensitive search)
     * Searches through all this customer's accounts for matching name
     * @param accountName - The name of the account to retrieve
     * @return Account - The Account object if found, null if no account with that name
     */
    public Account getAccountByName(String accountName) {
        // Loop through each account in the accounts list
        for (Account a : accounts) {
            // Use case-insensitive comparison: convert both to same case and compare
            if (a.getAccountName().equalsIgnoreCase(accountName)) {
                // Return the matching account immediately when found
                return a;
            }
        }
        // Return null if no matching account was found after checking all accounts
        return null;
    }

    /**
     * Gets the total number of accounts this customer owns
     * @return int - The count of accounts owned by this customer
     */
    public int getNumberOfAccounts() {
        // Return the size of the accounts ArrayList (number of accounts)
        return accounts.size();
    }

    /**
     * Gets this customer's complete transaction history as a formatted string
     * Each transaction appears on a new line
     * @return String - Formatted transaction history with header "Transaction History:"
     */
    public String getTransactionHistory() {
        // Create a string starting with the header "Transaction History:\n"
        String s = "Transaction History:\n";
        // Loop through each transaction string in the transactionHistory ArrayList
        for(String t : transactionHistory) {
            // Append the current transaction and a newline to the result string
            s += t + "\n";
        }
        // Return the complete formatted transaction history string
        return s;
    }

    /**
     * Gets this customer's transaction history as a ArrayList (mutable copy)
     * Returns a new copy so external code can't directly modify the internal list
     * @return List<String> - A new ArrayList containing all transaction records
     */
    public java.util.List<String> getTransactionHistoryList() {
        // Create and return a new ArrayList that's a copy of transactionHistory
        return new java.util.ArrayList<>(transactionHistory);
    }

}