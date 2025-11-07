package newbank.server;

import java.util.ArrayList;

public class Customer {
	
	private ArrayList<Account> accounts;
	
	public Customer() {
		accounts = new ArrayList<>();
	}
	
	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString();
		}
		return s;
	}

	public void addAccount(Account account) {
		accounts.add(account);
	}

	public boolean hasAccount(String accountName) {
		for(Account a : accounts) {
			if(a.getAccountName().equalsIgnoreCase(accountName)) {
				return true;
			}
		}
		return false;
	}
}
