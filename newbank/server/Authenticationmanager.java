package newbank.server;

import java.util.HashMap;

public class AuthenticationManager {
    
    private HashMap<String, String> credentials;  // stores username-password pairs
    
    public AuthenticationManager() {
        credentials = new HashMap<>();
    }
    
    // Register new user with username - password
    public void registerUser(String username, String password) {
        credentials.put(username, password);
    }
    
    // Verify username and password
    public boolean authenticate(String username, String password) {
        if (credentials.containsKey(username)) {
            String storedPassword = credentials.get(username);
            return storedPassword != null && storedPassword.equals(password);
        }
        return false;
    }
}