package newbank.server;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class AccountManager {
    
    // NM:14/11/25: I think a temporary workaround to making credentials stick around between sessions
    // is to do the following...
    // it will only hold data of the NewBank user and their own PC. 
    private HashMap<String, String> credentials;  // stores username-password pairs
    private final String DATA_FILE = "user_credentials.dat";
    
    public AccountManager() {
        credentials = loadFromFile(); // load existing data on startup
    }
    
    // Register new user with username - password
    // TO - Password is hashed with a random salt
    public void registerUser(String username, String password) {
              try {
            // Generate random salt
            byte[] salt = generateSalt();
            
            // Hash password with salt
            String hash = hashPassword(password, salt);
            
            // Store as "salt:hash" format
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String storedValue = saltBase64 + ":" + hash;
            
            credentials.put(username, storedValue);
            saveToFile(); // make the credentials persistent
            
            System.out.println("[SECURITY] User '" + username + "' registered with hashed password");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
        }
    }
    
    // Verify username and password
    public boolean authenticate(String username, String password) {
        if (credentials.containsKey(username)) {
            String storedValue = credentials.get(username);
            
            try {
                // Split stored value into salt and hash
                String[] parts = storedValue.split(":");
                if (parts.length != 2) {
                    return false;
                }
                
                String saltBase64 = parts[0];
                String storedHash = parts[1];
                
                // Decode salt
                byte[] salt = Base64.getDecoder().decode(saltBase64);
                
                // Hash the entered password with the stored salt
                String enteredHash = hashPassword(password, salt);
                
                // Compare hashes
                return storedHash.equals(enteredHash);
                
            } catch (NoSuchAlgorithmException e) {
                System.err.println("Error verifying password: " + e.getMessage());
                return false;
            }
        }
        return false;
    }
    
    // Generate a random salt for password hashing and return Random salt bytes
    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 16 bytes = 128 bits
        random.nextBytes(salt);
        return salt;
    }
    
    // throws NoSuchAlgorithmException if SHA-256 is not available
    private String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        
        // Add salt to the digest
        md.update(salt);
        
        // Hash the password
        byte[] hashedPassword = md.digest(password.getBytes());
        
        // Return as Base64 string for easy storage
        return Base64.getEncoder().encodeToString(hashedPassword);
    }
    
    // SAVE data to file
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))){
            oos.writeObject(credentials);
            System.out.println("[SECURITY] User credentials saved successfully (passwords hashed)");
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }
    
    // LOAD data from file 
    private HashMap<String, String> loadFromFile(){
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("No existing user credentials found. Starting fresh."); 
            return new HashMap<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            HashMap<String, String> loadedCredentials = (HashMap<String, String>) ois.readObject();
            System.out.println("[SECURITY] Loaded " + loadedCredentials.size() + " user credentials from file");
            return loadedCredentials;
        } catch(IOException | ClassNotFoundException e) {
            System.err.println("Error loading user data: " + e.getMessage());
            return new HashMap<>();
        }
    }
}






















