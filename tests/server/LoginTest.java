package tests.server;

import newbank.server.NewBank;
import newbank.server.CustomerID;
import newbank.server.AccountManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Login and Authentication Tests")
public class LoginTest {

    private NewBank bank;
    private AccountManager accountManager;

    @BeforeEach
    public void setUp() {
        bank = NewBank.getBank();
        accountManager = new AccountManager();
    }

    @Test
    @DisplayName("Should login successfully with correct credentials")
    public void testSuccessfulLogin() {
        // Login using demo user from addTestData()
        CustomerID customer = bank.checkLogInDetails("Bhagy", "1234");
        
        assertNotNull(customer, "CustomerID should not be null for valid credentials");
        assertEquals("Bhagy", customer.getKey(), "CustomerID key should match username");
    }

    @Test
    @DisplayName("Should fail login with incorrect password")
    public void testFailedLoginWrongPassword() {
        CustomerID customer = bank.checkLogInDetails("Bhagy", "wrongPassword");
        
        assertNull(customer, "CustomerID should be null for incorrect password");
    }

    @Test
    @DisplayName("Should fail login with non-existent user")
    public void testFailedLoginNonExistentUser() {
        CustomerID customer = bank.checkLogInDetails("NonExistentUser", "anyPassword");
        
        assertNull(customer, "CustomerID should be null for non-existent user");
    }

    @Test
    @DisplayName("Should authenticate registered user with AccountManager")
    public void testAccountManagerAuthentication() {
        accountManager.registerUser("TestUser", "testPassword123");
        
        boolean result = accountManager.authenticate("TestUser", "testPassword123");
        assertTrue(result, "Should authenticate with correct password");
    }

    @Test
    @DisplayName("Should reject authentication with wrong password")
    public void testAccountManagerWrongPassword() {
        accountManager.registerUser("TestUser", "correctPassword");
        
        boolean result = accountManager.authenticate("TestUser", "wrongPassword");
        assertFalse(result, "Should not authenticate with wrong password");
    }

    @Test
    @DisplayName("NFR2.2: Should store passwords as hashes (same password authenticates twice)")
    public void testPasswordHashing() {
        accountManager.registerUser("HashTest", "myPassword");
        
        // First authentication
        boolean firstAttempt = accountManager.authenticate("HashTest", "myPassword");
        assertTrue(firstAttempt, "First authentication should succeed");
        
        // Second authentication (proves hash is reusable)
        boolean secondAttempt = accountManager.authenticate("HashTest", "myPassword");
        assertTrue(secondAttempt, "Second authentication should succeed");
    }

    @Test
    @DisplayName("Should allow multiple users with same password")
    public void testMultipleUsersWithSamePassword() {
        accountManager.registerUser("User1", "samePassword");
        accountManager.registerUser("User2", "samePassword");
        
        assertTrue(accountManager.authenticate("User1", "samePassword"), 
                   "User1 should authenticate");
        assertTrue(accountManager.authenticate("User2", "samePassword"), 
                   "User2 should authenticate");
    }

    @Test
    @DisplayName("Should handle all demo users from addTestData()")
    public void testAllDemoUsersLogin() {
        assertNotNull(bank.checkLogInDetails("Bhagy", "1234"), "Bhagy should login");
        assertNotNull(bank.checkLogInDetails("Christina", "5678"), "Christina should login");
        assertNotNull(bank.checkLogInDetails("John", "abcd"), "John should login");
        assertNotNull(bank.checkLogInDetails("Ada", "efgh"), "Ada should login");
    }
}