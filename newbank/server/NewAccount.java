// Define the package for this class - part of the banking server
package newbank.server;

// Import HashMap for storing account names and balances
import java.util.HashMap;
// Import Scanner for reading user input from the console
import java.util.Scanner;

/**
 * NewAccount is a standalone utility program for creating bank accounts.
 * Demonstrates Feature Request 1: The system shall provide a function to create new accounts.
 * This class provides a command-line interface for account creation with validation.
 */
public class NewAccount {
    // A static HashMap to store account names (keys) and their balances (values) as Double
    // Static means this variable is shared across all instances (if any)
    private static HashMap<String, Double> accounts = new HashMap<>();

    /**
     * Main method: Entry point for running this program from the command line
     * Creates an interactive command-line interface for account management
     * @param args - Command line arguments (not used in this program)
     */
    public static void main(String[] args) {
        // Create a Scanner object to read user input from the keyboard (System.in)
        Scanner input = new Scanner(System.in);
        // Print welcome message to the user
        System.out.println("Welcome to the Bank System!");
        // Print instruction on what command to enter
        System.out.println("Enter a command (e.g. NEWACCOUNT <AccountName>):");

        // Start an infinite loop to continuously accept user commands
        while (true) {
            // Print the command prompt ">" 
            System.out.print("> ");
            // Read the entire line entered by the user from input
            String commandLine = input.nextLine().trim();

            // Check if user entered "EXIT" command (case-insensitive check)
            if (commandLine.equalsIgnoreCase("EXIT")) {
                // Print exit message to user
                System.out.println("Exiting system...");
                // Break out of the infinite loop to exit the program
                break;
            }

            // Split the commandLine string by spaces to separate command and arguments
            // Result is a String array where each element is one word
            String[] parts = commandLine.split(" ");
            // Check if input has exactly 2 parts AND first part is "NEWACCOUNT" (case-insensitive)
            if (parts.length == 2 && parts[0].equalsIgnoreCase("NEWACCOUNT")) {
                // Extract the account name from the second part (index 1)
                String accountName = parts[1];
                // Call createAccount method to try creating the account
                String result = createAccount(accountName);
                // Print the result (SUCCESS or FAIL message) to the user
                System.out.println(result);
            } else {
                // If input format is incorrect, print error message with correct format example
                System.out.println("Invalid command. Try: NEWACCOUNT <AccountName>");
            }
        }
        // Close the Scanner resource (good practice to prevent resource leaks)
        input.close();
    }

    /**
     * Feature Request 1: Create a new account with validation
     * Feature Request 1.1: User enters command in format NEWACCOUNT <AccountName>
     * Feature Request 1.2: System creates account with default balance of £0.00
     * Feature Request 1.3: Returns SUCCESS if created, FAIL if account already exists
     * Feature Request 1.4: Account names must be 3-20 characters, alphanumeric only, case-insensitive
     * 
     * @param accountName - The requested name for the new account
     * @return String - "SUCCESS: Account created..." if successful, or "FAIL: ..." with reason if unsuccessful
     */
    public static String createAccount(String accountName) {
        // Feature Request 1.4: Validate that accountName is not null
        if (accountName == null) {
            // Return failure message if account name is null
            return "FAIL: Account name cannot be null";
        }

        // Feature Request 1.4: Validate that account name length is between 3 and 20 characters
        if (accountName.length() < 3 || accountName.length() > 20) {
            // Return failure message if account name is too short or too long
            return "FAIL: Account name must be 3-20 characters";
        }

        // Feature Request 1.4: Use regex to validate that account name contains only alphanumeric characters
        // [a-zA-Z0-9]+ means: one or more characters that are letters (a-z, A-Z) or digits (0-9)
        if (!accountName.matches("[a-zA-Z0-9]+")) {
            // Return failure message if account name contains non-alphanumeric characters
            return "FAIL: Account name must be alphanumeric only";
        }

        // Feature Request 1.4: Normalize account name to lowercase for case-insensitive comparison
        // This ensures "Main" and "main" are treated as the same account name
        String normalizedName = accountName.toLowerCase();

        // Feature Request 1.3: Check if an account with this name already exists (case-insensitive)
        if (accounts.containsKey(normalizedName)) {
            // Return failure message indicating account with this name already exists
            return "FAIL: Account with name '" + accountName + "' already exists";
        }

        // Feature Request 1.2: Create account by storing it in HashMap with £0.00 default balance
        // Put the normalized name as key and 0.00 (zero pounds) as the initial balance
        accounts.put(normalizedName, 0.00);
        // Return success message confirming account creation with balance information
        return "SUCCESS: Account '" + accountName + "' created with balance £0.00";
    }
}