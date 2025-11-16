package newbank.server;

import java.util.ArrayList;

public class Customer {
	
	private ArrayList<Account> accounts;
	
	public Customer() {
		accounts = new ArrayList<>();
	}
	
	public String accountsToString() {
		String s = "";
		for(int i = 0; i < accounts.size(); i++) {
			s += accounts.get(i).toString();
			if(i < accounts.size() - 1) {
				s += "\n";
			}
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

	public boolean hasAccounts() {
		return !accounts.isEmpty();
	}

	public Account getFirstAccount() {
		if(accounts.isEmpty()) {
			return null;
		}
		return accounts.get(0);
	}

	public Account getAccount(String accountName) {
		for (Account a : accounts) {
			if (a.getAccountName().equals(accountName)){
				return a;
			}
		}
		return null;
	}

	public int getNumberOfAccounts() {
		return accounts.size();
	}
}
