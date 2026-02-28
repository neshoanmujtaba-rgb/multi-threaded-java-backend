// Define the package for this class - part of the banking server
package newbank.server;

// Import ArrayList for storing transaction history
import java.util.ArrayList;
// Import List interface for flexibility in collections
import java.util.List;
// Import LocalDateTime for time-stamping transactions
import java.time.LocalDateTime;
// Import DateTimeFormatter for formatting timestamps in a readable way
import java.time.format.DateTimeFormatter;

/**
 * Account class represents a single bank account within a customer's profile.
 * Each account has a name, balance, and a transaction history.
 * Supports operations: debit (withdraw), credit (deposit), withdraw, deposit.
 * All transactions are logged with timestamps.
 */
public class Account {
    // String field to store the account's name (e.g., "Main", "Savings", "Checking")
    private String accountName;
    // Double field to store the current balance in the account (in pounds)
    private double openingBalance;
    // List of Strings to store all transactions for this account with timestamps
    private List<String> transactions;
    // Static final DateTimeFormatter object for consistent timestamp formatting across all accounts
    // Format: "yyyy-MM-dd HH:mm:ss" (e.g., "2025-02-24 14:30:45")
    private static final DateTimeFormatter TIMESTAMP_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructor: Creates a new Account with given name and initial balance
     * Automatically logs account creation as first transaction
     * @param accountName - The name of the account (e.g., "Main", "Savings")
     * @param openingBalance - The initial balance in pounds
     */
    public Account(String accountName, double openingBalance) {
        // Store the account name in this object's field
        this.accountName = accountName;
        // Store the opening balance in this object's field
        this.openingBalance = openingBalance;
        // Initialize the transactions list as an empty ArrayList to store transaction records
        this.transactions = new ArrayList<>();
        // Get the current date and time right now
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        // Add the account creation as the first transaction with timestamp and balance
        transactions.add(timestamp + " | Account created with balance: £" + openingBalance);
    }

    /**
     * Overrides the toString() method to return a readable string representation
     * This is called automatically when the account is printed or converted to string
     * @return String - The account name and balance in format "AccountName: £balance"
     */
    @Override
    public String toString() {
        // Return a formatted string showing account name and current balance
        return accountName + ": £" + openingBalance;
    }

    /**
     * Getter method: Returns the account's name
     * @return String - The name of this account
     */
    public String getAccountName() {
        // Return the stored account name
        return accountName;
    }

    /**
     * Getter method: Returns the current balance of the account
     * @return double - The current balance in pounds
     */
    public double getAccountBalance(){
        // Return the current balance stored in this account
        return openingBalance;
    }

    /**
     * Enum DebitOutcome defines the possible outcomes of a debit (withdrawal) operation
     * This allows methods to return specific status instead of just true/false
     */
    public enum DebitOutcome {
        // Outcome when user tries to debit zero or negative amount (invalid)
        NON_POSITIVE_AMOUNT,
        // Outcome when account balance is too low to complete the withdrawal
        INSUFFICIENT_FUNDS,
        // Outcome when debit operation completes successfully
        SUCCESS
    }

    /**
     * Debit method: Withdraws money from the account if sufficient funds exist
     * Returns status instead of throwing exception - allows caller to handle outcome
     * @param amount - The amount to withdraw in pounds
     * @return DebitOutcome - SUCCESS if debit completed, or appropriate error code
     */
    public DebitOutcome debit(double amount) {
        // Check if the amount is zero or negative - invalid for withdrawal
        if(amount <= 0) {
            // Return error status indicating non-positive amount
            return DebitOutcome.NON_POSITIVE_AMOUNT;
        }
        // Check if the account has enough funds to cover the withdrawal
        if (openingBalance < amount) {
            // Return error status indicating insufficient funds in account
            return DebitOutcome.INSUFFICIENT_FUNDS;
        }
        // Deduct the amount from the current balance
        openingBalance -= amount;
        // Get current timestamp for transaction record
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        // Add debit transaction to history with details about amount and new balance
        transactions.add(timestamp + " | Debited £" + amount + ", New Balance: £" + openingBalance);
        // Return success status to indicate operation completed successfully
        return DebitOutcome.SUCCESS;
    }

    /**
     * Credit method: Deposits money into the account
     * Only processes positive amounts (validates before crediting)
     * @param amount - The amount to deposit in pounds
     */
    public void credit(double amount) {
        // Check that amount is greater than zero (only accept positive deposits)
        if (amount > 0) {
            // Add the amount to the current balance
            openingBalance += amount;
            // Get current timestamp for transaction record
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            // Add credit transaction to history with details about amount and new balance
            transactions.add(timestamp + " | Credited £" + amount + ", New Balance: £" + openingBalance);
        }
    }

    /**
     * Withdraw method: Explicit withdrawal operation (alternative to debit)
     * Feature Request 10.1: Provides explicit transfer operations with timestamps
     * @param amount - The amount to withdraw in pounds
     */
    public void withdraw(double amount) {
        // Check that amount is positive AND account has sufficient balance
        if (amount > 0 && openingBalance >= amount) {
            // Deduct the amount from the current balance
            openingBalance -= amount;
            // Get current timestamp for transaction record
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            // Add withdrawal transaction to history with details about amount and new balance
            transactions.add(timestamp + " | Withdrawn £" + amount + ", New Balance: £" + openingBalance);
        }
    }

    /**
     * Deposit method: Explicit deposit operation (alternative to credit)
     * Feature Request 10.1: Provides explicit transfer operations with timestamps
     * @param amount - The amount to deposit in pounds
     */
    public void deposit(double amount) {
        // Check that amount is positive (only accept positive deposits)
        if (amount > 0) {
            // Add the amount to the current balance
            openingBalance += amount;
            // Get current timestamp for transaction record
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            // Add deposit transaction to history with details about amount and new balance
            transactions.add(timestamp + " | Deposited £" + amount + ", New Balance: £" + openingBalance);
        }
    }

    /**
     * Getter method: Returns the current balance
     * @return double - The current account balance in pounds
     */
    public double getBalance() {
        // Return the current balance
        return openingBalance;
    }

    /**
     * Getter method: Returns the complete transaction history for this account
     * @return List<String> - List containing all transaction records with timestamps
     */
    public List<String> getTransactions() {
        // Return the list of all transaction records
        return transactions;
    }
}