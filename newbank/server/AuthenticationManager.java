package newbank.server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationManager {
    
    // NM:14/11/25: I think a temporary workaround to making credentials stick around between sessions
    // is to do the following...
    // it will only hold data of the NewBank user and their own PC. 
    // I initially thought it might   
    private HashMap<String, String> credentials;  // stores username-password pairs
    private final String DATA_FILE = "user_credentials.dat"; //


    public AuthenticationManager() {
        //credentials = new HashMap<>(); // this is where the issue starts every new instance of authentication manager starts with a new Hashmap object instance.
    credentials = loadFromFile(); // load existing data on startup
    
    }
    
    // Register new user with username - password
    public void registerUser(String username, String password) {
        credentials.put(username, password);
        saveToFile(); // make the credentials persistent
    }
    
    // Verify username and password
    public boolean authenticate(String username, String password) {
        if (credentials.containsKey(username)) {
            String storedPassword = credentials.get(username);
            return storedPassword != null && storedPassword.equals(password);
        }
        return false;
    }

    // SAVE data to file
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))){
            oos.writeObject(credentials); // writes the hashmap onto file
            System.out.println("User credentials saved sucessfully");
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }

    // LOAD data from file 
    private HashMap<String, String> loadFromFile(){
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("No existing user credentials found. Starting fresh. "); 
            return new HashMap<>();
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            return (HashMap<String, String>) ois.readObject();
        } catch(IOException | ClassNotFoundException e) {
            System.err.println("Error loading user data: " + e.getMessage());
            return new HashMap<>();
        }
    }
}