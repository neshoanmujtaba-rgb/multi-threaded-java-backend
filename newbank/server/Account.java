package newbank.server;

public class Account {
	
	private String accountName;
	private double openingBalance;

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
	}
	
	public String toString() {
		return (accountName + ": " + openingBalance);
	}

	public String getAccountName() {
		return accountName;
	}

	public boolean debit(double amount) {
		if(amount <= 0 || openingBalance < amount) {
			return false;
		}
		openingBalance -= amount;
		return true;
	}

	public void credit(double amount) {
		if(amount > 0) {
			openingBalance += amount;
		}
	}

}
