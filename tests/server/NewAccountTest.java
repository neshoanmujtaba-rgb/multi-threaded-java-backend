package tests.server;

import newbank.server.NewAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Field;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NewAccount NEWACCOUNT Function Tests")
public class NewAccountTest {

    @BeforeEach
    public void setUp() throws Exception {
        // Clear the accounts HashMap before each test using reflection
        Field accountsField = NewAccount.class.getDeclaredField("accounts");
        accountsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        HashMap<String, Double> accounts = (HashMap<String, Double>) accountsField.get(null);
        accounts.clear();
    }

    @Test
    @DisplayName("FR1.2: Should create account with default balance £0.00")
    public void testCreateNewAccount_Success() {
        // Arrange
        String accountName = "Savings";

        // Act
        String result = NewAccount.createAccount(accountName);

        // Assert
        assertTrue(result.startsWith("SUCCESS"), "Should return SUCCESS message");
        assertTrue(result.contains(accountName), "Success message should contain account name");
        assertTrue(result.contains("£0.00"), "Success message should confirm £0.00 balance");
    }

    @Test
    @DisplayName("FR1.3: Should return FAIL when account already exists")
    public void testCreateDuplicateAccount_Fail() {
        // Arrange
        String accountName = "Checking";
        NewAccount.createAccount(accountName);

        // Act
        String result = NewAccount.createAccount(accountName);

        // Assert
        assertTrue(result.startsWith("FAIL"), "Should return FAIL message");
        assertTrue(result.contains(accountName), "Fail message should contain account name");
        assertTrue(result.contains("already exists"), "Fail message should indicate account exists");
    }

    @Test
    @DisplayName("FR1.4: Should treat account names as case-insensitive (duplicate check)")
    public void testCreateAccount_CaseInsensitive() {
        // Arrange
        NewAccount.createAccount("savings");

        // Act - Try to create with a different case
        String result = NewAccount.createAccount("SAVINGS");

        // Assert
        assertTrue(result.startsWith("FAIL"), "Should reject duplicate with different case (case-insensitive)");
        assertTrue(result.contains("already exists"), "Should indicate account already exists");
    }

    @Test
    @DisplayName("FR1.4: Should accept valid alphanumeric names 3-20 chars")
    public void testCreateAccount_ValidAlphanumeric() {
        // Act & Assert
        String result1 = NewAccount.createAccount("Main");
        String result2 = NewAccount.createAccount("Savings123");
        String result3 = NewAccount.createAccount("ISA2024");

        assertTrue(result1.startsWith("SUCCESS"), "Should accept 4-char alphanumeric name");
        assertTrue(result2.startsWith("SUCCESS"), "Should accept mixed alphanumeric name");
        assertTrue(result3.startsWith("SUCCESS"), "Should accept alphanumeric with numbers");
    }

    @Test
    @DisplayName("FR1.4: Should accept name with exactly 3 characters")
    public void testCreateAccount_MinLength() {
        // Act
        String result = NewAccount.createAccount("ABC");

        // Assert
        assertTrue(result.startsWith("SUCCESS"), "Should accept 3-character name");
    }

    @Test
    @DisplayName("FR1.4: Should accept name with exactly 20 characters")
    public void testCreateAccount_MaxLength() {
        // Act
        String result = NewAccount.createAccount("ABCDEFGHIJ1234567890");

        // Assert
        assertTrue(result.startsWith("SUCCESS"), "Should accept 20-character name");
    }

    @Test
    @DisplayName("FR1.4: Should reject name shorter than 3 characters")
    public void testCreateAccount_TooShort() {
        // Act
        String result = NewAccount.createAccount("AB");

        // Assert
        assertTrue(result.startsWith("FAIL"), "Should reject 2-character name");
        assertTrue(result.contains("3-20 characters"), "Should mention character limit");
    }

    @Test
    @DisplayName("FR1.4: Should reject name longer than 20 characters")
    public void testCreateAccount_TooLong() {
        // Act
        String result = NewAccount.createAccount("ABCDEFGHIJ12345678901");

        // Assert
        assertTrue(result.startsWith("FAIL"), "Should reject 21-character name");
        assertTrue(result.contains("3-20 characters"), "Should mention character limit");
    }

    @Test
    @DisplayName("FR1.4: Should reject names with spaces")
    public void testCreateAccount_NameWithSpaces() {
        // Act
        String result = NewAccount.createAccount("Joint Account");

        // Assert
        assertTrue(result.startsWith("FAIL"), "Should reject name with spaces per FR1.4");
        assertTrue(result.contains("alphanumeric"), "Should mention alphanumeric requirement");
    }

    @Test
    @DisplayName("FR1.4: Should reject names with special characters")
    public void testCreateAccount_SpecialCharacters() {
        // Act
        String result1 = NewAccount.createAccount("ISA-2024");
        String result2 = NewAccount.createAccount("Account_1");
        String result3 = NewAccount.createAccount("Savings!");

        // Assert
        assertTrue(result1.startsWith("FAIL"), "Should reject name with hyphen per FR1.4");
        assertTrue(result2.startsWith("FAIL"), "Should reject name with underscore per FR1.4");
        assertTrue(result3.startsWith("FAIL"), "Should reject name with exclamation per FR1.4");
    }

    @Test
    @DisplayName("FR1.4: Should reject null account name")
    public void testCreateAccount_NullName() {
        // Act
        String result = NewAccount.createAccount(null);

        // Assert
        assertTrue(result.startsWith("FAIL"), "Should return FAIL for null account name");
    }

    @Test
    @DisplayName("FR1.4: Should reject empty string account name")
    public void testCreateAccount_EmptyName() {
        // Act
        String result = NewAccount.createAccount("");

        // Assert
        assertTrue(result.startsWith("FAIL"), "Should reject empty string");
        assertTrue(result.contains("3-20 characters"), "Should mention length requirement");
    }

    @Test
    @DisplayName("FR1.4: Should reject whitespace-only account names")
    public void testCreateAccount_WhitespaceOnly() {
        // Act
        String result = NewAccount.createAccount("   ");

        // Assert
        assertTrue(result.startsWith("FAIL"), "Should reject whitespace-only names");
        assertTrue(result.contains("alphanumeric"), "Should mention alphanumeric requirement");
    }

    @Test
    @DisplayName("Should verify exact message format for success")
    public void testCreateAccount_MessageFormat() {
        // Act
        String result = NewAccount.createAccount("Test");

        // Assert
        assertEquals("SUCCESS: Account 'Test' created with balance £0.00", result,
                "Should match exact expected format");
    }

    @Test
    @DisplayName("Should create multiple different accounts successfully")
    public void testCreateMultipleAccounts_Success() {
        // Act & Assert
        String result1 = NewAccount.createAccount("Main");
        String result2 = NewAccount.createAccount("Savings");
        String result3 = NewAccount.createAccount("ISA");

        assertTrue(result1.startsWith("SUCCESS"), "First account should be created");
        assertTrue(result2.startsWith("SUCCESS"), "Second account should be created");
        assertTrue(result3.startsWith("SUCCESS"), "Third account should be created");
    }
}