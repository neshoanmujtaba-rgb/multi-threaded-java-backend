package tests.server;

import newbank.server.NewBank;
import newbank.server.CustomerID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CLOSEACCOUNT command and Customer.removeAccount tests")
public class CloseAccountTest {

    @Test
    @DisplayName("Create a zero-balance account and close it via processRequest")
    public void testCloseAccountViaProcessRequest() {
        NewBank bank = NewBank.getBank();

        // login using demo user from addTestData()
        CustomerID cid = bank.checkLogInDetails("Bhagy", "1234");
        assertNotNull(cid, "Login should succeed for demo user Bhagy");

        // create a new zero-balance account via NEWACCOUNT
        String createResult = bank.processRequest(cid, "NEWACCOUNT Zero");
        assertTrue(createResult.startsWith("SUCCESS"), "NEWACCOUNT should succeed for a fresh name");

        // now close that account
        String closeResult = bank.processRequest(cid, "CLOSEACCOUNT Zero");
        assertEquals("SUCCESS", closeResult, "CLOSEACCOUNT should return SUCCESS when balance is zero and account exists");

        // verify the account no longer appears in SHOWMYACCOUNTS
        String accounts = bank.processRequest(cid, "SHOWMYACCOUNTS");
        assertFalse(accounts.contains("Zero"), "Closed account should not appear in account listing");
    }
}
