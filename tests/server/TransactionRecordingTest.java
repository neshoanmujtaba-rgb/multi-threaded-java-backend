import newbank.server.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;

@DisplayName("FR10: Transaction Recording Tests")
public class TransactionRecordingTest {

    private Customer customer;
    private NewBank bank;
    private TransactionLog transactionLog;

    @BeforeEach
    public void setUp() {
        customer = new Customer();
        bank = NewBank.getBank();
        transactionLog = new TransactionLog();
    }

    @Test
    @DisplayName("FR10.1: Should add transaction with timestamp to customer history")
    public void testAddTransaction() {
        String transactionMsg = "2023-10-27 10:00:00 | Moved 100.0 from Main to Savings";
        customer.addTransaction(transactionMsg);

        String history = customer.getTransactionHistory();
        assertTrue(history.contains(transactionMsg), 
            "Transaction should be in customer history");
    }

    @Test
    @DisplayName("FR10.2: Should persist multiple transactions in memory")
    public void testHistoryPersistence() {
        customer.addTransaction("2023-10-27 10:00:00 | Event 1");
        customer.addTransaction("2023-10-27 10:01:00 | Event 2");

        String history = customer.getTransactionHistory();
        assertTrue(history.contains("Event 1"), "First transaction should persist");
        assertTrue(history.contains("Event 2"), "Second transaction should persist");
    }

    @Test
    @DisplayName("FR10.1: Should include timestamp in required format (yyyy-MM-dd HH:mm:ss)")
    public void testTimestampPresence() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String datePart = now.format(formatter);

        customer.addTransaction(datePart + " 10:30:45 | Action");

        String history = customer.getTransactionHistory();
        assertTrue(history.contains(datePart), "Date portion should be in history");
    }

    @Test
    @DisplayName("FR10.1: Account debit operation should log transaction with timestamp")
    public void testAccountDebitLogsTransaction() {
        Account account = new Account("Main", 1000.0);
        Account.DebitOutcome outcome = account.debit(100.0);

        assertEquals(Account.DebitOutcome.SUCCESS, outcome, "Debit should succeed");
        assertTrue(!account.getTransactions().isEmpty(), "Transactions list should not be empty");
        
        String lastTransaction = account.getTransactions().get(1); // Skip creation message
        assertTrue(lastTransaction.contains("|"), "Transaction should contain timestamp separator");
        assertTrue(lastTransaction.contains("100"), "Transaction should contain amount");
    }

    @Test
    @DisplayName("FR10.1: Account credit operation should log transaction with timestamp")
    public void testAccountCreditLogsTransaction() {
        Account account = new Account("Main", 1000.0);
        account.credit(50.0);

        String lastTransaction = account.getTransactions().get(1); // Skip creation message
        assertTrue(lastTransaction.contains("|"), "Transaction should contain timestamp separator");
        assertTrue(lastTransaction.contains("50"), "Transaction should contain amount");
        assertTrue(lastTransaction.contains("1050"), "Transaction should show new balance");
    }

    @Test
    @DisplayName("FR10.1: Account withdraw operation should log transaction with timestamp")
    public void testAccountWithdrawLogsTransaction() {
        Account account = new Account("Main", 1000.0);
        account.withdraw(200.0);

        String lastTransaction = account.getTransactions().get(1); // Skip creation message
        assertTrue(lastTransaction.contains("Withdrawn"), "Should contain Withdrawn action");
        assertTrue(lastTransaction.contains("200"), "Should contain withdrawn amount");
        assertTrue(lastTransaction.contains("800"), "Should show new balance");
    }

    @Test
    @DisplayName("FR10.1: Account deposit operation should log transaction with timestamp")
    public void testAccountDepositLogsTransaction() {
        Account account = new Account("Main", 1000.0);
        account.deposit(150.0);

        String lastTransaction = account.getTransactions().get(1); // Skip creation message
        assertTrue(lastTransaction.contains("Deposited"), "Should contain Deposited action");
        assertTrue(lastTransaction.contains("150"), "Should contain deposit amount");
        assertTrue(lastTransaction.contains("1150"), "Should show new balance");
    }

    @Test
    @DisplayName("FR10.2: Should save transactions to persistent file storage")
    public void testSaveTransactionsToFile() {
        String customerName = "TestCustomer";
        java.util.List<String> transactions = new java.util.ArrayList<>();
        transactions.add("2023-10-27 10:00:00 | Transaction 1");
        transactions.add("2023-10-27 10:01:00 | Transaction 2");

        transactionLog.saveTransactions(customerName, transactions);
        
        File file = new File("transaction_logs/" + customerName + ".txn");
        assertTrue(file.exists(), "Transaction log file should be created");
        
        // Cleanup
        transactionLog.clearTransactions(customerName);
    }

    @Test
    @DisplayName("FR10.2: Should load transactions from persistent file storage")
    public void testLoadTransactionsFromFile() {
        String customerName = "TestCustomer2";
        java.util.List<String> savedTransactions = new java.util.ArrayList<>();
        savedTransactions.add("2023-10-27 10:00:00 | Test Transaction 1");
        savedTransactions.add("2023-10-27 10:01:00 | Test Transaction 2");

        // Save transactions
        transactionLog.saveTransactions(customerName, savedTransactions);

        // Load transactions
        java.util.List<String> loadedTransactions = transactionLog.loadTransactions(customerName);

        assertEquals(savedTransactions.size(), loadedTransactions.size(), 
            "Should load same number of transactions");
        assertTrue(loadedTransactions.contains("2023-10-27 10:00:00 | Test Transaction 1"),
            "Should load first transaction correctly");
        
        // Cleanup
        transactionLog.clearTransactions(customerName);
    }

    @Test
    @DisplayName("FR10.2: Transaction history should persist across customer sessions")
    public void testTransactionHistoryPersistsAcrossSessions() {
        NewBank bank = NewBank.getBank();
        
        // Login first customer
        CustomerID customerId = bank.checkLogInDetails("Bhagy", "1234");
        assertNotNull(customerId, "Login should succeed");
        
        // Create a new account
        String result = bank.processRequest(customerId, "NEWACCOUNT TestAcc");
        assertTrue(result.startsWith("SUCCESS"), "Account creation should succeed");
        
        // Move money (transaction)
        String moveResult = bank.processRequest(customerId, "MOVE 100 Main TestAcc");
        assertTrue(moveResult.startsWith("SUCCESS"), "Move should succeed");
        
        // Check transactions are recorded
        String transactions = bank.processRequest(customerId, "SHOWTRANSACTIONS Main");
        assertTrue(transactions.contains("Moved"), "Transaction should be recorded");
        assertTrue(transactions.contains("100"), "Amount should be logged");
    }

    @Test
    @DisplayName("FR10.1: Multiple operations should each have their own timestamps")
    public void testMultipleOperationsHaveUniqueTimestamps() {
        Account account = new Account("Test", 1000.0);
        
        account.credit(100);
        String transaction1 = account.getTransactions().get(1);
        
        // Small delay to ensure different timestamp
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        account.withdraw(50);
        String transaction2 = account.getTransactions().get(2);
        
        assertNotEquals(transaction1, transaction2, 
            "Different transactions should have different entries");
        assertTrue(transaction1.contains("|") && transaction2.contains("|"),
            "Both transactions should have timestamps");
    }
}