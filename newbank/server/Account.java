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

	public double getAccountBalance(){
        return openingBalance;
    }

    public enum DebitOutcome {
        NON_POSITIVE_AMOUNT,
        INSUFFICIENT_FUNDS,
        SUCCESS
    }

    public DebitOutcome debit(double amount) {
        if(amount <= 0) {
            return DebitOutcome.NON_POSITIVE_AMOUNT;
        }
        if (openingBalance < amount) {
            return DebitOutcome.INSUFFICIENT_FUNDS;
        }
        openingBalance -= amount;
        return DebitOutcome.SUCCESS;
    }

	public void credit(double amount) {
		if(amount > 0) {
			openingBalance += amount;
		}
	}

}
