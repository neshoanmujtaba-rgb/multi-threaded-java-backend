package newbank.server;

import java.util.HashMap;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	private AuthenticationManager authManager;  // Separate auth system
	
	private NewBank() {
		customers = new HashMap<>();
		authManager = new AuthenticationManager();
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
		System.out.println("Username: Ada		Password: efgh");
		System.out.println("==================================== :)\n");
	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		// run login through AuthenticationManager
		if(authManager.authenticate(userName, password)) {
			// Then verify customer
			if(customers.containsKey(userName)) {
				return new CustomerID(userName);
			}
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
		if(customers.containsKey(customer.getKey())) {
			String[] parts = request.split(" ");
			String command = parts[0];

			switch(command) {
			case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
			case "NEWACCOUNT" :
				if(parts.length == 2) {
					return newAccount(customer, parts[1]);
				}
				return "FAIL";
			case "PAY" :
				if(parts.length == 3) {
					try {
						String recipientName = parts[1];
						double amount = Double.parseDouble(parts[2]);
						return pay(customer, recipientName, amount);
					} catch (NumberFormatException e) {
						return "FAIL";
					}
				}
				return "FAIL";
			case "MOVE" :
				if(parts.length == 4) {
					try {
						double amount = Double.parseDouble(parts[1]);
						String fromAccount = parts[2];
						String toAccount = parts[3];
						return move(customer, amount, fromAccount, toAccount);
					} catch (NumberFormatException e) {
						return "FAIL";
					}
				}
				return "FAIL";
			default : return "FAIL";
			}
		}
		return "FAIL";
	}

	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	private int getNumberOfAccounts(CustomerID customer){
		return customers.get(customer.getKey()).getNumberOfAccounts();
	}

	private String newAccount(CustomerID customer, String accountName) {
		Customer c = customers.get(customer.getKey());
		if(c.hasAccount(accountName)) {
			return "FAIL";
		}
		c.addAccount(new Account(accountName, 0.0));
		return "SUCCESS";
	}

	private String pay(CustomerID customer, String recipientName, double amount) {
		// Validate amount is positive
		if(amount <= 0) {
			return "FAIL";
		}

		// Get sender customer
		Customer sender = customers.get(customer.getKey());
		if(sender == null || !sender.hasAccounts()) {
			return "FAIL";
		}

		// Get recipient customer
		Customer recipient = customers.get(recipientName);
		if(recipient == null || !recipient.hasAccounts()) {
			return "FAIL";
		}

		// Get accounts
		Account senderAccount = sender.getFirstAccount();
		Account recipientAccount = recipient.getFirstAccount();

		// Check sufficient funds and debit from sender
		if(!senderAccount.debit(amount)) {
			return "FAIL";
		}

		// Credit to recipient
		recipientAccount.credit(amount);

		return "SUCCESS";
	}
	
	private String move(CustomerID customer, double amount, String fromAccount, String toAccount) {
		
		// Validate customer exists
		Customer c = customers.get(customer.getKey());
		if (c == null) {
			return "FAIL: You do not exist";
		}

		// Validate account names
		if (fromAccount == null || toAccount == null) {
			return "FAIL: To and From account names cannot be null";
		}

		// Retrieve and validate accounts
		Account from = c.getAccount(fromAccount);
		if (from == null) {
			return "FAIL: fromAccount cannot be null";
		}

		Account to = c.getAccount(toAccount);
		if (to == null) {
			return "FAIL: toAccount cannot be null";
		}
		
		// Validate amount is positive
		if (amount <= 0) {
			return "FAIL: Amount must be positive";
		}

		// Validate customer has more than one account
		if (getNumberOfAccounts(customer) < 2) {
			return "FAIL: You do not have more than one account";
		}

		// Attempt to debit fromAccount
		if(!from.debit(amount)) {
			return "FAIL";
		}

		// Assuming none of the above have failed credit account
		to.credit(amount);

		return "SUCCESS"; 
	}

}