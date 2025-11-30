package newbank.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionRecordingTest {

    private Customer customer;

    @BeforeEach
    public void setUp() {
        customer = new Customer();
    }

    @Test
    public void testAddTransaction() {
        String transactionMsg = "2023-10-27 10:00:00 | Moved 100.0 from Main to Savings";
        customer.addTransaction(transactionMsg);

        String history = customer.getTransactionHistory();
        assertTrue(history.contains(transactionMsg));
    }

    @Test
    public void testHistoryPersistence() {
        customer.addTransaction("Event 1");
        customer.addTransaction("Event 2");

        String history = customer.getTransactionHistory();
        assertTrue(history.contains("Event 1"));
        assertTrue(history.contains("Event 2"));
    }

    @Test
    public void testTimestampPresence() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String datePart = now.format(formatter);

        customer.addTransaction(datePart + " HH:mm:ss | Action");

        String history = customer.getTransactionHistory();
        assertTrue(history.contains(datePart));
    }
}