// Define the package for this class - handles user authentication and security
package newbank.server;

// Import File class for file I/O operations
import java.io.*;
// Import MessageDigest for cryptographic hashing of passwords
import java.security.MessageDigest;
// Import NoSuchAlgorithmException for when hash algorithm isn't available
import java.security.NoSuchAlgorithmException;
// Import SecureRandom for cryptographically secure random number generation
import java.security.SecureRandom;
// Import Base64 for encoding binary data as text for storage
import java.util.Base64;
// Import HashMap for storing username-password credential pairs
import java.util.HashMap;
// Import Map interface for collection types
import java.util.Map;

/**
 * UserAccountManager handles user authentication and password security.
 * Stores username-password pairs securely using:
 * - Random salt generation (prevents rainbow table attacks)
 * - SHA-256 hashing (industry-standard cryptographic hash)
 * - Base64 encoding (for storage in text files)
 * - Persistent file storage (credentials persist between sessions)
 * 
 * SECURITY NOTE: Passwords are NEVER stored in plain text - always hashed with salt
 */
public class UserAccountManager {
    
    // HashMap to store username-password pairs (actually stores salt:hash pairs)
    // Note (NM:14/11/25): Temporary workaround - credentials persist between sessions on same user's PC
    // This will only hold data of this NewBank user on their own PC
    private HashMap<String, String> credentials;
    // String constant for the filename where credentials are saved to disk
    private final String DATA_FILE = "user_credentials.dat";
    
    /**
     * Constructor: Initializes the UserAccountManager and loads existing credentials from file
     * Loads previously saved passwords (if they exist) so users don't need to re-register
     */
    public UserAccountManager() {
        // Call loadFromFile to read saved credentials from disk
        credentials = loadFromFile();
    }
    
    /**
     * Registers a new user with username and password
     * Password is securely hashed with random salt before storage
     * Uses SHA-256 hashing algorithm with random salt for maximum security
     * @param username - The username to register
     * @param password - The plain-text password to hash and store
     */
    public void registerUser(String username, String password) {
        // Use try-catch to handle NoSuchAlgorithmException from hash operations
        try {
            // Generate a random salt (unpredictable bytes to prevent rainbow table attacks)
            byte[] salt = generateSalt();
            
            // Hash the password using SHA-256 algorithm combined with the salt
            // This makes the hash unique even if two users have the same password
            String hash = hashPassword(password, salt);
            
            // Encode the salt to Base64 string (binary bytes can't be stored directly in HashMap String)
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            // Combine salt and hash as "salt:hash" string for storage
            // Format: each part is Base64-encoded, separated by colon
            String storedValue = saltBase64 + ":" + hash;
            
            // Store the "salt:hash" value in credentials HashMap under the username key
            credentials.put(username, storedValue);
            // Save the credentials to persistent file storage so they survive program restart
            saveToFile();
            
            // Print security confirmation message to console
            System.out.println("[SECURITY] User '" + username + "' registered with hashed password");
        } catch (NoSuchAlgorithmException e) {
            // If SHA-256 algorithm not available (shouldn't happen), print error
            System.err.println("Error hashing password: " + e.getMessage());
        }
    }
    
    /**
     * Authenticates a user by verifying username and password
     * Retrieves stored hash, re-hashes provided password with same salt, compares results
     * Uses constant-time comparison to prevent timing attacks
     * @param username - The username to authenticate
     * @param password - The plain-text password to verify
     * @return boolean - true if username exists and password matches, false otherwise
     */
    public boolean authenticate(String username, String password) {
        // Check if the username exists in the credentials HashMap
        if (credentials.containsKey(username)) {
            // Retrieve the stored "salt:hash" string for this username
            String storedValue = credentials.get(username);
            
            // Use try-catch to handle NoSuchAlgorithmException from hash operation
            try {
                // Split the stored value by colon to separate salt and hash parts
                String[] parts = storedValue.split(":");
                // Check if split resulted in exactly 2 parts (salt and hash)
                if (parts.length != 2) {
                    // Return false if format is corrupted (doesn't have salt:hash format)
                    return false;
                }
                
                // Extract the Base64-encoded salt (first part)
                String saltBase64 = parts[0];
                // Extract the stored hash (second part)
                String storedHash = parts[1];
                
                // Decode the Base64 salt back to raw bytes for use in hashing
                byte[] salt = Base64.getDecoder().decode(saltBase64);
                
                // Hash the user-provided password using the SAME salt from storage
                // If password matches, the computed hash will equal the stored hash
                String enteredHash = hashPassword(password, salt);
                
                // Compare the computed hash with the stored hash
                // Return true if they match (password is correct), false if different (password is wrong)
                return storedHash.equals(enteredHash);
                
            } catch (NoSuchAlgorithmException e) {
                // If SHA-256 algorithm not available (shouldn't happen), print error
                System.err.println("Error verifying password: " + e.getMessage());
                // Return false to deny authentication on error (secure fail)
                return false;
            }
        }
        // Return false if username not found in credentials HashMap
        return false;
    }
    
    /**
     * Generates a cryptographically secure random salt for password hashing
     * Salt is random bytes that are combined with password before hashing
     * This ensures same password produces different hashes for different users
     * Prevents attackers from using pre-computed rainbow tables
     * @return byte[] - Array of 16 random bytes (128 bits) for use as salt
     */
    private byte[] generateSalt() {
        // Create a SecureRandom instance for cryptographically secure randomness
        // SecureRandom uses system entropy, unlike regular Random which is weak
        SecureRandom random = new SecureRandom();
        // Create a byte array of size 16 (128 bits of entropy)
        byte[] salt = new byte[16];
        // Fill the byte array with random bytes from SecureRandom
        random.nextBytes(salt);
        // Return the random salt bytes
        return salt;
    }
    
    /**
     * Hashes a password using SHA-256 algorithm combined with salt
     * Salt is added to digest first, then password is digested
     * Output is Base64-encoded for storage in text files
     * @param password - The plain-text password to hash
     * @param salt - The salt bytes to combine with password
     * @return String - Base64-encoded SHA-256 hash of (salt + password)
     * @throws NoSuchAlgorithmException - If SHA-256 algorithm not available
     */
    private String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        // Get MessageDigest instance for SHA-256 hashing algorithm
        // SHA-256 produces 256-bit (32-byte) hash output
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        
        // Add the salt bytes to the digest
        // This must happen BEFORE adding password, as order matters for hashing
        md.update(salt);
        
        // Digest the password bytes (converts password string to bytes and hashes)
        // This returns the final 32-byte SHA-256 hash of (salt + password)
        byte[] hashedPassword = md.digest(password.getBytes());
        
        // Encode the binary hash bytes as Base64 text string for storage
        // Base64 uses only printable ASCII characters, suitable for text files
        return Base64.getEncoder().encodeToString(hashedPassword);
    }
    
    /**
     * Saves credentials to persistent file storage using Java serialization
     * Writes the entire credentials HashMap as a binary object to file
     * Allows credentials to survive program restart
     */
    private void saveToFile() {
        // Use try-with-resources to open ObjectOutputStream with auto-close
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))){
            // Serialize (write) the entire credentials HashMap to the file as a binary object
            oos.writeObject(credentials);
            // Print confirmation message that save succeeded
            System.out.println("[SECURITY] User credentials saved successfully (passwords hashed)");
        } catch (IOException e) {
            // If I/O error occurred during save, print error message to standard error
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }
    
    /**
     * Loads credentials from persistent file storage using Java deserialization
     * Reads the previously saved HashMap of credentials from disk file
     * If file doesn't exist, returns empty HashMap (starts fresh)
     * @return HashMap<String, String> - Loaded credentials HashMap, or empty if file doesn't exist
     */
    @SuppressWarnings("unchecked") // Suppress compiler warning about unsafe cast from Object to HashMap
    private HashMap<String, String> loadFromFile(){
        // Create File object representing the credentials data file
        File file = new File(DATA_FILE);
        // Check if the data file does not exist (new installation)
        if (!file.exists()) {
            // Print message that no existing credentials were found
            System.out.println("No existing user credentials found. Starting fresh."); 
            // Return new empty HashMap to start with no users
            return new HashMap<>();
        }
        // Use try-with-resources to open ObjectInputStream with auto-close
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            // Deserialize (read) the object from file - cast from Object to HashMap
            // @SuppressWarnings allows the unchecked cast without compiler warning
            HashMap<String, String> loadedCredentials = (HashMap<String, String>) ois.readObject();
            // Print confirmation message with number of credentials loaded
            System.out.println("[SECURITY] Loaded " + loadedCredentials.size() + " user credentials from file");
            // Return the loaded credentials HashMap
            return loadedCredentials;
        } catch(IOException | ClassNotFoundException e) {
            // If error occurs during read (corrupted file), print error message
            System.err.println("Error loading user data: " + e.getMessage());
            // Return new empty HashMap as fallback (secure fail)
            return new HashMap<>();
        }
    }
}






















