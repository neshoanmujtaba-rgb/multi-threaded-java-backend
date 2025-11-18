package newbank.server;

import java.util.HashMap;

public class NewBank {

<<<<<<< HEAD
    private static final NewBank bank = new NewBank();
    private HashMap<String, Customer> customers;
    private AccountManager authManager;
=======
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
>>>>>>> origin/main

    private NewBank() {
        customers = new HashMap<>();
        authManager = new AccountManager();
        addTestData();
    }

<<<<<<< HEAD
    private void addTestData() {
        Customer bhagy = new Customer();
        bhagy.addAccount(new Account("Main", 1000.0));
        customers.put("Bhagy", bhagy);
        authManager.registerUser("Bhagy", "1234");
=======
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
>>>>>>> origin/main

        Customer christina = new Customer();
        christina.addAccount(new Account("Savings", 1500.0));
        customers.put("Christina", christina);
        authManager.registerUser("Christina", "5678");

<<<<<<< HEAD
        Customer john = new Customer();
        john.addAccount(new Account("Checking", 250.0));
        customers.put("John", john);
        authManager.registerUser("John", "abcd");
=======
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
>>>>>>> origin/main

        System.out.println("\n (<3 ========== DEMO LOGINS ==========");
        System.out.println("Username: Bhagy      Password: 1234");
        System.out.println("Username: Christina  Password: 5678");
        System.out.println("Username: John       Password: abcd");
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
            String command = parts[0];

            switch (command) {
                case "SHOWMYACCOUNTS":
                    return showMyAccounts(customer);
                case "SHOWTRANSACTIONS":
                    if (parts.length == 2) {
                        return showTransactions(customer, parts[1]);
                    }
                    return "FAIL: Missing account name";
                case "NEWACCOUNT":
                    if (parts.length == 2) {
                        return newAccount(customer, parts[1]);
                    }
                    return "FAIL";
                case "PAY":
                    if (parts.length == 3) {
                        try {
                            String recipientName = parts[1];
                            double amount = Double.parseDouble(parts[2]);
                            return pay(customer, recipientName, amount);
                        } catch (NumberFormatException e) {
                            return "FAIL";
                        }
                    }
                    return "FAIL";
                default:
                    return "FAIL: Unknown command";
            }
        }
        return "FAIL";
    }

    private String showMyAccounts(CustomerID customer) {
        return customers.get(customer.getKey()).accountsToString();
    }

<<<<<<< HEAD
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
        if (c.hasAccount(accountName)) {
            return "FAIL";
        }
        c.addAccount(new Account(accountName, 0.0));
        return "SUCCESS";
    }

    private String pay(CustomerID customer, String recipientName, double amount) {
        if (amount <= 0) {
            return "FAIL";
        }

        Customer sender = customers.get(customer.getKey());
        if (sender == null || !sender.hasAccounts()) {
            return "FAIL";
        }

        Customer recipient = customers.get(recipientName);
        if (recipient == null || !recipient.hasAccounts()) {
            return "FAIL";
        }

        Account senderAccount = sender.getFirstAccount();
        Account recipientAccount = recipient.getFirstAccount();

        if (!senderAccount.debit(amount)) {
            return "FAIL";
        }

        recipientAccount.credit(amount);
        return "SUCCESS";
    }
=======
		return "SUCCESS";
	}
	
	private String move(CustomerID customer, double amount, String fromAccount, String toAccount) {
		
		Customer c = customers.get(customer.getKey());
		Account from = c.getAccount(fromAccount);
		Account to = c.getAccount(toAccount);
		
		// Validate amount is positive
		if (amount <= 0) {
			return "FAIL";
		}

		// Validate customer has more than one account
		if (getNumberOfAccounts(customer) < 2) {
			return "FAIL";
		}

		// Attempt to debit fromAccount
		if(!from.debit(amount)) {
			return "FAIL";
		}

		// Assuming none of the above have failed credit account
		to.credit(amount);

		return "SUCCESS"; 
	}

>>>>>>> origin/main
}