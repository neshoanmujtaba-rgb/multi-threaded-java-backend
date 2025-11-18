package newbank.server;

import java.util.HashMap;

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
}