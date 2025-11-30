package newbank.server;

import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
    private HashMap<String, Customer> customers;
    private AccountManager authManager;

    private NewBank() {
        customers = new HashMap<>();
        authManager = new AccountManager();
        addTestData();
    }

    private void addTestData() {
        Customer bhagy = new Customer();
        bhagy.addAccount(new Account("Main", 1000.0));
        customers.put("Bhagy", bhagy);
        authManager.registerUser("Bhagy", "1234");

        Customer christina = new Customer();
        christina.addAccount(new Account("Savings", 1500.0));
        customers.put("Christina", christina);
        authManager.registerUser("Christina", "5678");

        Customer john = new Customer();
        john.addAccount(new Account("Checking", 250.0));
        customers.put("John", john);
        authManager.registerUser("John", "abcd");

        Customer ada = new Customer();
        ada.addAccount(new Account("Checking", 1800.0));
        ada.addAccount(new Account("Savings", 15.0));
        customers.put("Ada", ada);
        authManager.registerUser("Ada", "efgh");

        // Print demo login when server connection starts
        System.out.println("\n (<3 ========== DEMO LOGINS ==========");
        System.out.println("Username: Bhagy      Password: 1234");
        System.out.println("Username: Christina  Password: 5678");
        System.out.println("Username: John       Password: abcd");
        System.out.println("Username: Ada        Password: efgh");
        System.out.println("==================================== :)\n");
    }

    public static NewBank getBank() {
        return bank;
    }

    public synchronized CustomerID checkLogInDetails(String userName, String password) {
        if (authManager.authenticate(userName, password)) {
            if (customers.containsKey(userName)) {
                return new CustomerID(userName);
            }
        }
        return null;
    }

    public synchronized String processRequest(CustomerID customer, String request) {
        if (customers.containsKey(customer.getKey())) {
            String[] parts = request.split(" ");
            String command = parts[0].toUpperCase();

            switch (command) {
                
                case "SHOWMYACCOUNTS":
                    return showMyAccounts(customer);

                case "SHOWTRANSACTIONS":
                   if(parts.length < 2) {
                    return "FAIL: Missing account name";}    
                if (parts.length > 2) {
                    return "FAIL: Too many arguments entered. SHOWTRANSACTIONS must be run as SHOWTRANSACTIONS <Account Name>";
                }
                    return showTransactions(customer, parts[1]);     

                case "NEWACCOUNT":
                    if (parts.length < 2) {
                        return "FAIL: Missing account name";
                    } 
                    if (parts.length > 2) {
                        return "FAIL: Too many arguments entered. NEWACCOUNT must be run as NEWACCOUNT <Account Type>";
                    }
                    return newAccount(customer, parts[1]);

                case "PAY":
                    if (parts.length < 3) {
                        return "FAIL: Missing arguments. PAY must be run as PAY <Recipient Name> <Amount>";
                    }
                    if (parts.length > 3) {
                        return "FAIL: Too many arguments entered. PAY must be run as PAY <Recipient Name> <Amount>";
                    }

                    try {
                        String recipientName = parts[1];
                        double amount = Double.parseDouble(parts[2]);
                        return pay(customer, recipientName, amount);
                    } catch (NumberFormatException e) {
                        return "FAIL: Amount must be a valid number";
                    }
                
                case "MOVE":
                    if (parts.length == 4) {
                        try {
                            double amount = Double.parseDouble(parts[1]);
                            String fromAccount = parts[2];
                            String toAccount = parts[3];
                            return move(customer, amount, fromAccount, toAccount);
                        } catch (NumberFormatException e) {
                            return "FAIL: Amount must be a valid number";
                        }
                    }
                    return "FAIL: Insufficient arguments entered. MOVE must be run as MOVE <Amount> <Source Account Name> <Destination Account Name> ";
                
                case "CLOSEACCOUNT":
                    if (parts.length == 1) {
                        return "FAIL: Please specify an account name to close. e.g. CLOSEACCOUNT <AccountName>";
                    }
                    if (parts.length == 2) {
                        Customer accountHolder = customers.get(customer.getKey());
                        String accountName = parts[1];
                        return accountHolder.removeAccount(accountName);
                    }
                        
                case "LOGOUT":
                return "SUCCESS: Logged out";
                
                case "HELP":
                return getHelpText();
                
              default:
                    return "FAIL: Unknown command. Type HELP for a list of commands.";
            }
        }

        return "FAIL: Unknown customer";
    }

	private String createNewAccount(CustomerID customerId, String accountName) {
		Customer customer = customers.get(customerId.getKey());
		if(customer.hasAccount(accountName)) {
			return "FAIL";
		}
		customer.addAccount(new Account(accountName, 0.0));
		logTransaction(customer, "Created account: " + accountName);
		return "SUCCESS";
	}

	private String moveMoney(CustomerID customerId, String amountStr, String fromAccountName, String toAccountName) {
		Customer customer = customers.get(customerId.getKey());
		double amount;
		try {
			amount = Double.parseDouble(amountStr);
		} catch (NumberFormatException e) {
			return "FAIL";
		}

		Account from = customer.getAccountByName(fromAccountName);
		Account to = customer.getAccountByName(toAccountName);

		if (from != null && to != null && from.getBalance() >= amount) {
			from.withdraw(amount);
			to.deposit(amount);
			logTransaction(customer, String.format("Moved %.2f from %s to %s", amount, fromAccountName, toAccountName));
			return "SUCCESS";
		}
		return "FAIL";
	}

	private String payMoney(CustomerID customerId, String recipient, String amountStr) {
		Customer customer = customers.get(customerId.getKey());
		double amount;
		try {
			amount = Double.parseDouble(amountStr);
		} catch (NumberFormatException e) {
			return "FAIL";
		}

		Account main = customer.getFirstAccount();
		if(main != null && main.getBalance() >= amount) {
			main.withdraw(amount);
			// In a complete implementation, we would credit the recipient here.
			logTransaction(customer, String.format("Paid %.2f to %s", amount, recipient));
			return "SUCCESS";
		}
		return "FAIL";
	}

	private void logTransaction(Customer customer, String message) {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String timestamp = now.format(formatter);
		customer.addTransaction(timestamp + " | " + message);
	}
	
	private String showMyAccounts(CustomerID customer) {
        return customers.get(customer.getKey()).accountsToString();
    }

    private String showTransactions(CustomerID customer, String accountName) {
        Customer c = customers.get(customer.getKey());
        if (!c.hasAccount(accountName)) {
            return "FAIL: Account does not exist or belongs to another customer";
        }
        Account account = c.getAccountByName(accountName);
        return String.join("\n", account.getTransactions());
    }

	private String newAccount(CustomerID customer, String accountName) {
		Customer c = customers.get(customer.getKey());
		if(c.hasAccount(accountName)) {
			return "FAIL";
		}
		c.addAccount(new Account(accountName, 0.0));
		return "SUCCESS";
	}

	// Transfer method to be used by both Pay and Move methods
	// You need to be determining the accounts in the Pay and Move methods before calling them here
	private DebitOutcome transfer(Account sourceAccount, Account destinationAccount, double amount) {

		DebitOutcome outcome = sourceAccount.debit(amount);

		if (outcome == DebitOutcome.SUCCESS){
			destinationAccount.credit(amount);
		}
		
		return outcome;
	}


	private String transferOutcomeString(DebitOutcome outcome, double amount, Account source, Account destination) {
		switch(outcome){
			case SUCCESS:
				return "SUCCESS: Transferred " + amount + " from " + source.getAccountName() + " to " + destination.getAccountName() + ". " + source.getAccountName() + "'s new balance is: " + source.getAccountBalance();
			
			case NON_POSITIVE_AMOUNT:
				return "FAIL: Amount must be positive";

			case INSUFFICIENT_FUNDS:
				return "FAIL: Insufficient funds";
			
			default:
				return "FAIL: Unknown error";

		}
	}

	private String pay(CustomerID customer, String recipientName, double amount) {

		// Get sender customer
		Customer sender = customers.get(customer.getKey());
		if(sender == null || !sender.hasAccounts()) {
			return "FAIL: You do not have an account";
		}

		// Get recipient customer
		Customer recipient = customers.get(recipientName);
		if(recipient == null || !recipient.hasAccounts()) {
			return "FAIL: Recipient does not have an account";
		}

        Account senderAccount = sender.getFirstAccount();
        Account recipientAccount = recipient.getFirstAccount();

		// UPDATED TO CALL COMMON METHOD TRANSFER
		DebitOutcome outcome = transfer(senderAccount, recipientAccount, amount);

		return transferOutcomeString(outcome, amount, senderAccount, recipientAccount);
	}
	
	private String move(CustomerID customer, double amount, String fromAccount, String toAccount) {
		Customer c = customers.get(customer.getKey());
        Account from = c.getAccountByName(fromAccount);
        Account to = c.getAccountByName(toAccount);

		// Validate account names
		if (fromAccount == null || toAccount == null) {
			return "FAIL: To and From account names must not be null";
		}

		// MOVE SPECIFIC
		// Validate customer has more than one account
		if (c.getNumberOfAccounts() < 2) {
			return "FAIL: You do not have more than one account";
		}

		// Attempt to debit fromAccount - CALL TRANSFER INSTEAD
		DebitOutcome outcome = transfer(from, to, amount);
		

		return transferOutcomeString(outcome, amount, from, to);
	}

    
    private String getHelpText() {
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
