package newbank.server;

import java.util.HashMap;
import java.util.Scanner;

public class NewAccount {
 // A HashMap to store account names and their balances
    private static HashMap<String, Double> accounts = new HashMap<>();

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to the Bank System!");
        System.out.println("Enter a command (e.g. NEWACCOUNT <AccountName>):");

        while (true) {
            System.out.print("> ");
            String commandLine = input.nextLine().trim();

            // Exit command for convenience
            if (commandLine.equalsIgnoreCase("EXIT")) {
                System.out.println("Exiting system...");
                break;
            }

            // Split user input by spaces
            String[] parts = commandLine.split(" ");
            if (parts.length == 2 && parts[0].equalsIgnoreCase("NEWACCOUNT")) {
                String accountName = parts[1];
                String result = createAccount(accountName);
                System.out.println(result);
            } else {
                System.out.println("Invalid command. Try: NEWACCOUNT <AccountName>");
            }
        }
        input.close();
    }

    /**
     * FR1: The system shall provide a function to create new accounts.
     * FR1.1: The user shall enter a command in the format NEWACCOUNT <AccountName>.
     * FR1.2: The system shall create the account with a default balance of £0.00.
     * FR1.3: The system shall return SUCCESS if the account is created,
     *        or FAIL if an account with that name already exists.
     * FR1.4: Account names shall be alphanumeric, 3-20 characters, and case-insensitive.
     */
    public static String createAccount(String accountName) {
        // FR1.4: Validate account name format
        if (accountName == null) {
            return "FAIL: Account name cannot be null";
        }

        if (accountName.length() < 3 || accountName.length() > 20) {
            return "FAIL: Account name must be 3-20 characters";
        }

        if (!accountName.matches("[a-zA-Z0-9]+")) {
            return "FAIL: Account name must be alphanumeric only";
        }

        // FR1.4: Case-insensitive comparison - normalize to lowercase
        String normalizedName = accountName.toLowerCase();

        // FR1.3: Check if an account already exists (case-insensitive)
        if (accounts.containsKey(normalizedName)) {
            return "FAIL: Account with name '" + accountName + "' already exists";
        }

        // FR1.2: Create an account with £0.00 balance
        accounts.put(normalizedName, 0.00);
        return "SUCCESS: Account '" + accountName + "' created with balance £0.00";
    }
}
    /**
     * FR1: The system shall provide a function to create new accounts.
     * FR1.1: The user shall enter a command in the format NEWACCOUNT <AccountName>.
     * FR1.2: The system shall create the account with a default balance of £0.00.
     * FR1.3: The system shall return SUCCESS if the account is created,
     *        or FAIL if an account with that name already exists.
     */
    public static String createAccount(String accountName) {
        if (accounts.containsKey(accountName)) {
            return "FAIL: Account with name '" + accountName + "' already exists.";
        } else {
            accounts.put(accountName, 0.00);
            return "SUCCESS: Account '" + accountName + "' created with balance £0.00.";
        }
    }
}
