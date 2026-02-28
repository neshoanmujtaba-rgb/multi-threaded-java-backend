// Define the package for this class - core banking logic
package newbank.server;

// Import HashMap for storing customers by their username keys
import java.util.HashMap;
// Import LocalDateTime for getting current date/time for transactions
import java.time.LocalDateTime;
// Import DateTimeFormatter for formatting timestamps in consistent format
import java.time.format.DateTimeFormatter;

/**
 * NewBank is the central banking system class that manages all operations.
 * Implements Singleton pattern: only one instance of NewBank exists through the program
 * All customer data, authentication, and transactions are managed here.
 * Processes all banking commands sent by clients through their handlers.
 */
public class NewBank {

    // Private static final instance of this class - the Singleton pattern ensures only one NewBank exists
    private static final NewBank bank = new NewBank();
    // HashMap to store all customers, with username as key and Customer object as value
    private HashMap<String, Customer> customers;
    // Reference to UserAccountManager for handling password authentication
    private UserAccountManager authManager;
    // Reference to TransactionLog for saving/loading transaction history to/from files
    private TransactionLog transactionLog;

    /**
     * Private constructor: Prevents creation of multiple instances
     * Can only be called once to create the singleton instance
     * Initializes all data structures and loads test data
     */
    private NewBank() {
        // Create HashMap to store customers (starts empty, test data added below)
        customers = new HashMap<>();
        // Create new UserAccountManager for handling authentication operations
        authManager = new UserAccountManager();
        // Create new TransactionLog for persistent transaction storage
        transactionLog = new TransactionLog();
        // Load test data (demo customers) into the system
        addTestData();
    }

    /**
     * Populates the banking system with test customer accounts and credentials
     * Used for demonstration and testing purposes
     */
    private void addTestData() {
        // Create a new Customer object for Bhagy
        Customer bhagy = new Customer();
        // Add a "Main" account with £1000.00 starting balance to Bhagy
        bhagy.addAccount(new Account("Main", 1000.0));
        // Store Bhagy in customers HashMap using "Bhagy" as the key
        customers.put("Bhagy", bhagy);
        // Register Bhagy's credentials in the authentication manager
        authManager.registerUser("Bhagy", "1234");

        // Create a new Customer object for Christina
        Customer christina = new Customer();
        // Add a "Savings" account with £1500.00 starting balance to Christina
        christina.addAccount(new Account("Savings", 1500.0));
        // Store Christina in customers HashMap using "Christina" as the key
        customers.put("Christina", christina);
        // Register Christina's credentials in the authentication manager
        authManager.registerUser("Christina", "5678");

        // Create a new Customer object for John
        Customer john = new Customer();
        // Add a "Checking" account with £250.00 starting balance to John
        john.addAccount(new Account("Checking", 250.0));
        // Store John in customers HashMap using "John" as the key
        customers.put("John", john);
        // Register John's credentials in the authentication manager
        authManager.registerUser("John", "abcd");

        // Create a new Customer object for Ada
        Customer ada = new Customer();
        // Add a "Checking" account with £1800.00 starting balance to Ada
        ada.addAccount(new Account("Checking", 1800.0));
        // Add a "Savings" account with £15.00 starting balance to Ada (multiple accounts)
        ada.addAccount(new Account("Savings", 15.0));
        // Store Ada in customers HashMap using "Ada" as the key
        customers.put("Ada", ada);
        // Register Ada's credentials in the authentication manager
        authManager.registerUser("Ada", "efgh");

        // Print decorative header for demo login information
        System.out.println("\n (<3 ========== DEMO LOGINS ==========");
        // Print demo login for Bhagy
        System.out.println("Username: Bhagy      Password: 1234");
        // Print demo login for Christina
        System.out.println("Username: Christina  Password: 5678");
        // Print demo login for John
        System.out.println("Username: John       Password: abcd");
        // Print demo login for Ada
        System.out.println("Username: Ada        Password: efgh");
        // Print decorative footer for demo login information
        System.out.println("==================================== :)\n");
    }

    /**
     * Gets the singleton instance of NewBank
     * This is the only way to access the NewBank instance throughout the application
     * @return NewBank - The single NewBank instance
     */
    public static NewBank getBank() {
        // Return the static singleton instance
        return bank;
    }

    /**
     * Authenticates a customer and returns their CustomerID if credentials are valid
     * Synchronized to ensure thread-safe access from multiple client handlers
     * @param userName - The customer's username
     * @param password - The customer's password
     * @return CustomerID - Valid CustomerID if authentication succeeds, null if fails
     */
    public synchronized CustomerID checkLogInDetails(String userName, String password) {
        // Call authManager to verify username and password match stored credentials
        if (authManager.authenticate(userName, password)) {
            // Check if this authenticated username actually exists in customers HashMap
            if (customers.containsKey(userName)) {
                // Create and return a new CustomerID token for this authenticated customer
                return new CustomerID(userName);
            }
        }
        // Return null if authentication failed or customer not found
        return null;
    }

    /**
     * Main request processor: Handles all banking commands from clients
     * Routes commands to appropriate handler methods (SHOWMYACCOUNTS, PAY, MOVE, etc.)
     * Synchronized to ensure thread-safe access from multiple client threads
     * @param customer - The authenticated CustomerID object making the request
     * @param request - The full command string sent by the customer
     * @return String - Response message with command result (SUCCESS or FAIL)
     */
    public synchronized String processRequest(CustomerID customer, String request) {
        // Check if request is null or contains only whitespace (invalid input)
        if(request == null || request.trim().isEmpty()) {
            // Return error message for invalid empty command
            return "FAIL: Invalid command format";
        }
        
        // Check if the customer exists in the customers HashMap
        if (customers.containsKey(customer.getKey())) {
            // Split the request string by spaces to separate command and arguments
            // Example: "PAY John 100" becomes ["PAY", "John", "100"]
            String[] parts = request.split(" ");
            // Extract the first part and convert to uppercase for case-insensitive comparison
            String command = parts[0].toUpperCase();

            // Switch statement to route command to appropriate handler method
            switch (command) {

                // SHOWMYACCOUNTS command: Display all accounts and their balances
                case "SHOWMYACCOUNTS":
                    return showMyAccounts(customer);

                // SHOWTRANSACTIONS command: Display transaction history for a specific account
                case "SHOWTRANSACTIONS":
                    // Validate that exactly 2 parts: command and account name
                    if(parts.length != 2) {
                        // Return error if wrong number of arguments
                        return "FAIL: Invalid command format";
                    }
                    // Call helper to show transactions for the specified account
                    return showTransactions(customer, parts[1]);

                // NEWACCOUNT command: Create a new account for the customer
                case "NEWACCOUNT":
                    // Validate that exactly 2 parts: command and account name
                    if (parts.length != 2) {
                        // Return error if wrong number of arguments
                        return "FAIL: Invalid command format";
                    }
                    // Call helper to create account with the specified name
                    return newAccount(customer, parts[1]);

                // PAY command: Transfer money to another customer
                case "PAY":
                    // Validate that exactly 3 parts: PAY, recipient, amount
                    if (parts.length != 3) {
                        // Return error if wrong number of arguments
                        return "FAIL: Invalid command format";
                    }

                    // Use try-catch to handle non-numeric amount input
                    try {
                        // Extract recipient name from second part
                        String recipientName = parts[1];
                        // Parse the third part as a double (amount in pounds)
                        double amount = Double.parseDouble(parts[2]);
                        // Call helper to process the payment
                        return pay(customer, recipientName, amount);
                    } catch (NumberFormatException e) {
                        // Return error if amount is not a valid number
                        return "FAIL: Invalid command format";
                    }

                // MOVE command: Transfer money between customer's own accounts
                case "MOVE":
                    // Validate that exactly 4 parts: MOVE, amount, from, to
                    if (parts.length != 4) {
                        // Return error if wrong number of arguments
                        return "FAIL: Invalid command format";
                    }
                    // Use try-catch to handle non-numeric amount input
                    try {
                        // Parse the amount from the second part
                        double amount = Double.parseDouble(parts[1]);
                        // Extract source account name from third part
                        String fromAccount = parts[2];
                        // Extract destination account name from fourth part
                        String toAccount = parts[3];
                        // Call helper to move money between accounts
                        return move(customer, amount, fromAccount, toAccount);
                    } catch (NumberFormatException e) {
                        // Return error if amount is not a valid number
                        return "FAIL: Invalid command format";
                    }

                // CLOSEACCOUNT command: Close a customer account if balance is zero
                case "CLOSEACCOUNT":
                    // Validate that exactly 2 parts: command and account name
                    if (parts.length != 2) {
                        // Return error if wrong number of arguments
                        return "FAIL: Invalid command format";
                    }
                    // Get the Customer object for this authenticated customer
                    Customer accountHolder = customers.get(customer.getKey());
                    // Extract the account name to close
                    String accountName = parts[1];
                    // Call Customer method to remove/close the account
                    String result = accountHolder.removeAccount(accountName);
                    // Check if account was successfully closed
                    if (result.startsWith("SUCCESS")) {
                        // Log the account closure as a transaction
                        logTransaction(accountHolder, customer.getKey(), "Closed account: " + accountName);
                    }
                    // Return the result of the close operation
                    return result;

                // LOGOUT command: End the customer's session
                case "LOGOUT":
                    // Return success message - client handler will see this and close connection
                    return "SUCCESS: Logged out";

                // HELP command: Display list of available commands
                case "HELP":
                    // Return formatted help text with all available commands
                    return getHelpText();

                // Unknown/invalid command
                default:
                    // Return error for unrecognized command
                    return "FAIL: Invalid command format";
            }
        }

        // Return error if customer cannot be found
        return "FAIL: Unknown customer";
    }

    /**
     * Creates a new account for the customer
     * Feature Request 1: Account creation functionality
     * @param customerId - The customer creating the account
     * @param accountName - The name for the new account
     * @return String - SUCCESS message if created, FAIL message if error
     */
    private String createNewAccount(CustomerID customerId, String accountName) {
        // Get the Customer object from HashMap using customer ID key
        Customer customer = customers.get(customerId.getKey());
        // Check if customer already has an account with this name (case-insensitive)
        if(customer.hasAccount(accountName)) {
            // Return generic failure if account already exists
            return "FAIL";
        }
        // Add new Account object to customer with £0.00 starting balance
        customer.addAccount(new Account(accountName, 0.0));
        // Log this account creation as a transaction
        logTransaction(customer, customerId.getKey(), "Created account: " + accountName);
        // Return success message
        return "SUCCESS";
    }

    /**
     * Moves money between two of the customer's own accounts
     * Feature Request: Move money between own accounts
     * @param customerId - The customer moving money
     * @param amountStr - The amount as a string
     * @param fromAccountName - Name of source account
     * @param toAccountName - Name of destination account
     * @return String - SUCCESS if moved, FAIL with reason if error
     */
    private String moveMoney(CustomerID customerId, String amountStr, String fromAccountName, String toAccountName) {
        // Get the Customer object from HashMap using customer ID key
        Customer customer = customers.get(customerId.getKey());
        // Parse the amount from string to double
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            // Return failure if amount string is not a valid number
            return "FAIL";
        }

        // Find the source account by name in customer's account list
        Account from = customer.getAccountByName(fromAccountName);
        // Find the destination account by name in customer's account list
        Account to = customer.getAccountByName(toAccountName);

        // Check if both accounts exist and source has sufficient balance
        if (from != null && to != null && from.getBalance() >= amount) {
            // Withdraw money from source account
            from.withdraw(amount);
            // Deposit money to destination account
            to.deposit(amount);
            // Log the transfer as a transaction with formatted message
            logTransaction(customer, customerId.getKey(), String.format("Moved %.2f from %s to %s", amount, fromAccountName, toAccountName));
            // Return success message
            return "SUCCESS";
        }
        // Return failure if accounts don't exist or insufficient funds
        return "FAIL";
    }

    /**
     * Transfers money from customer to another customer
     * Feature Request: Payment between customers
     * @param customerId - The customer making the payment
     * @param recipient - The name of the recipient customer
     * @param amountStr - The amount as a string
     * @return String - SUCCESS if paid, FAIL with reason if error
     */
    private String payMoney(CustomerID customerId, String recipient, String amountStr) {
        // Get the Customer object from HashMap using customer ID key
        Customer customer = customers.get(customerId.getKey());
        // Parse the amount from string to double
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            // Return failure if amount string is not a valid number
            return "FAIL";
        }

        // Get the customer's first account (default account for payments)
        Account main = customer.getFirstAccount();
        // Check if customer has an account and has sufficient balance
        if(main != null && main.getBalance() >= amount) {
            // Withdraw the amount from customer's main account
            main.withdraw(amount);
            // Log the payment as a transaction
            logTransaction(customer, customerId.getKey(), String.format("Paid %.2f to %s", amount, recipient));
            // Return success message
            return "SUCCESS";
        }
        // Return failure if no account found or insufficient funds
        return "FAIL";
    }

    /**
     * Records a transaction to customer's history and persistent storage
     * Updates both the Customer's in-memory history and saves to file
     * @param customer - The Customer object whose transaction to log
     * @param customerName - The customer's username (for file storage)
     * @param message - The transaction description message
     */
    private void logTransaction(Customer customer, String customerName, String message) {
        // Get the current date and time right now
        LocalDateTime now = LocalDateTime.now();
        // Create DateTimeFormatter to format the timestamp
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Format the current date/time as a timestamp string
        String timestamp = now.format(formatter);
        // Combine timestamp and message into a full transaction record
        String fullTransaction = timestamp + " | " + message;
        // Add the transaction to the customer's in-memory transaction history
        customer.addTransaction(fullTransaction);
        // Save the customer's transaction history to persistent file storage
        transactionLog.saveTransactions(customerName, customer.getTransactionHistoryList());
    }

    /**
     * Returns formatted string of all customer's accounts and balances
     * @param customer - The customer whose accounts to display
     * @return String - Formatted account list
     */
    private String showMyAccounts(CustomerID customer) {
        // Get the Customer object and call its formatting method
        return customers.get(customer.getKey()).accountsToString();
    }

    /**
     * Returns transaction history for a specific customer account
     * @param customer - The customer requesting transaction history
     * @param accountName - The name of the account
     * @return String - Formatted transaction list or error message
     */
    private String showTransactions(CustomerID customer, String accountName) {
        // Get the Customer object from HashMap
        Customer c = customers.get(customer.getKey());
        // Check if customer has an account with the requested name
        if (!c.hasAccount(accountName)) {
            // Return error if account doesn't exist or belongs to another customer
            return "FAIL: Account does not exist or belongs to another customer";
        }
        /// Get the Account object with the specified name
        Account account = c.getAccountByName(accountName);
        // Join all transactions with newlines and return as formatted string
        return String.join("\n", account.getTransactions());
    }

    /**
     * Validates account name according to Feature Request 1.4 requirements:
     * - Must not be null
     * - Must be 3-20 characters long
     * - Must contain only alphanumeric characters (letters and numbers)
     *
     * @param accountName The account name to validate
     * @return null if valid, error message string if invalid
     */
	private String validateAccountName(String accountName) {
		// Check if account name is null
		if (accountName == null) {
			// Return error message for null account name
			return "FAIL: Account name cannot be null";
		}

		// Check if account name length is NOT between 3 and 20 characters
		if (accountName.length() < 3 || accountName.length() > 20) {
			// Return error message for invalid length
			return "FAIL: Account name must be 3-20 characters";
		}

		// Use regex to check if account name contains only alphanumeric characters
		// [a-zA-Z0-9]+ means one or more alphanumeric characters
		if (!accountName.matches("[a-zA-Z0-9]+")) {
			// Return error message for non-alphanumeric characters
			return "FAIL: Account name must be alphanumeric only";
		}

		// Return null if account name passes all validations
		return null;
	}

	/**
	 * Feature Request 1: Account Creation
	 * Creates a new account for the customer with validation
	 * FR1.1: Accepts command in format NEWACCOUNT <AccountName>
	 * FR1.2: Creates account with default balance of £0.00
	 * FR1.3: Returns SUCCESS if created, or FAIL if account already exists
	 * FR1.4: Account names must be alphanumeric, 3-20 characters, case-insensitive
	 * @param customer - The customer creating the account
	 * @param accountName - The requested name for the new account
	 * @return String - SUCCESS message if created, FAIL message with reason if error
	 */
	private String newAccount(CustomerID customer, String accountName) {
		// Validate the account name using validation helper method
		String validationError = validateAccountName(accountName);
		// Check if validation returned an error message (null means valid)
		if (validationError != null) {
			// Return the validation error message to user
			return validationError;
		}

		// Get the Customer object from HashMap using customer ID key
		Customer c = customers.get(customer.getKey());

		// Check if customer already has an account with this name (case-insensitive)
        if(c.hasAccount(accountName)) {
            // Return error message indicating account already exists
            return "FAIL: Account with name '" + accountName + "' already exists";
		}

		// Create new Account with the specified name and £0.00 starting balance
		c.addAccount(new Account(accountName, 0.0));
		// Log the account creation as a transaction
		logTransaction(c, customer.getKey(), "Created account: " + accountName);
        // Return success message with account details
        return "SUCCESS: Account '" + accountName + "' created with balance £0.00";
    }

    /**
     * Common transfer method used by both Pay and Move commands
     * Performs debit from source account and credit to destination account
     * @param sourceAccount - The account to debit from
     * @param destinationAccount - The account to credit to
     * @param amount - The amount to transfer
     * @return DebitOutcome - The result of the debit operation
     */
    private Account.DebitOutcome transfer(Account sourceAccount, Account destinationAccount, double amount) {

        // Attempt to debit (withdraw) from the source account
        Account.DebitOutcome outcome = sourceAccount.debit(amount);

        // Check if debit was successful before crediting destination
        if (outcome == Account.DebitOutcome.SUCCESS){
            // Credit (deposit) the amount to the destination account
            destinationAccount.credit(amount);
        }

        // Return the outcome of the debit operation
        return outcome;
    }

    /**
     * Converts a transfer outcome to a human-readable response message
     * @param outcome - The DebitOutcome from a transfer operation
     * @param amount - The amount that was transferred
     * @param source - The source Account object
     * @param destination - The destination Account object
     * @return String - Formatted message describing the transfer result
     */
    private String transferOutcomeString(Account.DebitOutcome outcome, double amount, Account source, Account destination) {
        // Switch statement to handle different transfer outcomes
        switch(outcome){
            // Case: Transfer was successful
            case SUCCESS:
                // Return detailed success message with new balance
                return "SUCCESS: Transferred " + amount + " from " + source.getAccountName() + " to " + destination.getAccountName() + ". " + source.getAccountName() + "'s new balance is: " + source.getAccountBalance();

            // Case: Amount was zero or negative
            case NON_POSITIVE_AMOUNT:
                // Return error message for invalid amount
                return "FAIL: Amount must be positive";

            // Case: Source account has insufficient funds
            case INSUFFICIENT_FUNDS:
                // Return error message for insufficient balance
                return "FAIL: Insufficient funds";

            // Case: Unexpected error
            default:
                // Return generic error message
                return "FAIL: Unknown error";

        }
    }

    /**
     * Processes a payment from one customer to another
     * Feature Request: Customer-to-customer payments
     * @param customer - The customer making the payment
     * @param recipientName - The name of the recipient customer
     * @param amount - The amount in pounds to transfer
     * @return String - SUCCESS message if paid, FAIL message with reason if error
     */
    private String pay(CustomerID customer, String recipientName, double amount) {

        // Get sender customer object from HashMap
        Customer sender = customers.get(customer.getKey());
        // Check if sender exists and has at least one account
        if(sender == null || !sender.hasAccounts()) {
            // Return error if sender has no accounts
            return "FAIL: You do not have an account";
        }

        // Get recipient customer object from HashMap using recipient name
        Customer recipient = customers.get(recipientName);
        // Check if recipient exists and has at least one account
        if(recipient == null || !recipient.hasAccounts()) {
            // Return error if recipient doesn't exist or has no accounts
            return "FAIL: Recipient does not have an account";
        }

        // Get the sender's first (default) account for payment source
        Account senderAccount = sender.getFirstAccount();
        // Get the recipient's first account for payment destination
        Account recipientAccount = recipient.getFirstAccount();

        // Use common transfer method to handle the payment
        Account.DebitOutcome outcome = transfer(senderAccount, recipientAccount, amount);

        // Check if transfer was successful to log transactions
        if (outcome == Account.DebitOutcome.SUCCESS) {
            // Log payment from sender's perspective
            logTransaction(sender, customer.getKey(),
                String.format("Paid £%.2f to %s", amount, recipientName));
            // Log payment from recipient's perspective
            logTransaction(recipient, recipientName,
                String.format("Received £%.2f from %s", amount, customer.getKey()));
        }

        // Return formatted outcome message describing the payment result
        return transferOutcomeString(outcome, amount, senderAccount, recipientAccount);
    }

    /**
     * Moves money between two of the same customer's accounts
     * Feature Request: Intra-account transfers
     * @param customer - The customer moving money between their accounts
     * @param amount - The amount in pounds to transfer
     * @param fromAccount - Name of the source account
     * @param toAccount - Name of the destination account
     * @return String - SUCCESS message if moved, FAIL message with reason if error
     */
    private String move(CustomerID customer, double amount, String fromAccount, String toAccount) {
        // Get the Customer object from HashMap using customer ID key
        Customer c = customers.get(customer.getKey());
        // Get the source Account object by finding account with fromAccount name
        Account from = c.getAccountByName(fromAccount);
        // Get the destination Account object by finding account with toAccount name
        Account to = c.getAccountByName(toAccount);

        // Validate that both account names are not null
        if (fromAccount == null || toAccount == null) {
            // Return error if account names are null
            return "FAIL: To and From account names must not be null";
        }

        // Validate that customer has more than one account (can't move within single account)
        if (c.getNumberOfAccounts() < 2) {
            // Return error if customer doesn't have at least 2 accounts
            return "FAIL: You do not have more than one account";
        }

        // Use common transfer method to move money between accounts
        Account.DebitOutcome outcome = transfer(from, to, amount);

        // Check if transfer was successful to log transaction
        if (outcome == Account.DebitOutcome.SUCCESS) {
            // Log the money movement as a customer transaction
            logTransaction(c, customer.getKey(),
                String.format("Moved £%.2f from %s to %s", amount, fromAccount, toAccount));
        }

        // Return formatted outcome message describing the move result
        return transferOutcomeString(outcome, amount, from, to);
    }

    /**
     * Generates the help text displaying all available commands and their usage
     * Returned as a formatted string that can be sent to the customer
     * @return String - Formatted help text with all commands and descriptions
     */
    private String getHelpText() {
        // Build and return a multi-line string with all available commands
        return "AVAILABLE COMMANDS:\n"
        + "---------------------------------\n"
        + "SHOWMYACCOUNTS                          - View all your accounts\n"
        + "SHOWTRANSACTIONS <Account>               - View transactions for an account\n"
        + "NEWACCOUNT <AccountName>                 - Create a new account\n"
        + "PAY <RecipientName> <Amount>             - Send money to another customer\n"
        + "MOVE <Amount> <FromAccount> <ToAccount>  - Move money between your accounts\n"
        + "LOGOUT                                   - Log out of your session\n"
        + "HELP                                     - Show all available commands\n";
    }
}
