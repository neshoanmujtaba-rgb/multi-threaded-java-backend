import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Field;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BankSystem NEWACCOUNT Function Tests")
public class BankSystemTest {

    @BeforeEach
    public void setUp() throws Exception {
        // Clear the accounts HashMap before each test using reflection
        Field accountsField = BankSystem.class.getDeclaredField("accounts");
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
        String result = BankSystem.createAccount(accountName);

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
        BankSystem.createAccount(accountName); // Create the first account

        // Act
        String result = BankSystem.createAccount(accountName); // Try to create a duplicate

        // Assert
        assertTrue(result.startsWith("FAIL"), "Should return FAIL message");
        assertTrue(result.contains(accountName), "Fail message should contain account name");
        assertTrue(result.contains("already exists"), "Fail message should indicate account exists");
    }

    @Test
    @DisplayName("Should create multiple different accounts successfully")
    public void testCreateMultipleAccounts_Success() {
        // Act & Assert
        String result1 = BankSystem.createAccount("Main");
        String result2 = BankSystem.createAccount("Savings");
        String result3 = BankSystem.createAccount("ISA");

        assertTrue(result1.startsWith("SUCCESS"), "First account should be created successfully");
        assertTrue(result2.startsWith("SUCCESS"), "Second account should be created successfully");
        assertTrue(result3.startsWith("SUCCESS"), "Third account should be created successfully");
    }

    @Test
    @DisplayName("Should handle account names with different cases")
    public void testCreateAccount_CaseSensitivity() {
        // Arrange & Act
        String result1 = BankSystem.createAccount("savings");
        String result2 = BankSystem.createAccount("SAVINGS");

        // Assert
        assertTrue(result1.startsWith("SUCCESS"), "Lowercase account should be created");
        assertTrue(result2.startsWith("SUCCESS"), "Uppercase account should be created separately");
    }

    @Test
    @DisplayName("Should handle empty string account name")
    public void testCreateAccount_EmptyName() {
        // Act
        String result = BankSystem.createAccount("");

        // Assert
        assertTrue(result.startsWith("SUCCESS") || result.startsWith("FAIL"),
                "Should return a valid response for empty string");
    }

    @Test
    @DisplayName("Should handle account names with spaces")
    public void testCreateAccount_NameWithSpaces() {
        // Act
        String result = BankSystem.createAccount("Joint Account");

        // Assert
        assertTrue(result.startsWith("SUCCESS"), "Should create account with spaces in name");
        assertTrue(result.contains("Joint Account"), "Should preserve spaces in account name");
    }

    @Test
    @DisplayName("Should handle special characters in account names")
    public void testCreateAccount_SpecialCharacters() {
        // Act
        String result = BankSystem.createAccount("ISA-2024");

        // Assert
        assertTrue(result.startsWith("SUCCESS"), "Should create account with special characters");
    }


    @Test
    @DisplayName("Should handle null account name appropriately")
    public void testCreateAccount_NullName() {
        String result = BankSystem.createAccount(null);
        assertTrue(result.startsWith("SUCCESS"),
                "Current implementation allows null as account name");
    }

    @Test
    @DisplayName("Should accept whitespace-only account names")
    public void testCreateAccount_WhitespaceOnly() {
        String result = BankSystem.createAccount("   ");
        assertTrue(result.startsWith("SUCCESS"),
                "Current implementation allows whitespace-only names");
    }

    @Test
    @DisplayName("Should verify exact message format for success")
    public void testCreateAccount_MessageFormat() {
        String result = BankSystem.createAccount("Test");
        assertEquals("SUCCESS: Account 'Test' created with balance £0.00.", result,
                "Message includes trailing period");
    }
}