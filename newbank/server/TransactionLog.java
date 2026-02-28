// Define the package for this class - part of the banking server
package newbank.server;

// Import File class for file system operations
import java.io.*;
// Import utility classes like List, ArrayList, Arrays for collection operations
import java.util.*;

/**
 * TransactionLog class manages persistent storage of customer transaction histories.
 * Saves transactions to individual files per customer and can load them back on startup.
 * Feature Request 10.2: Persistent file storage for transaction history
 */
public class TransactionLog {
    // String constant for the directory name where all transaction files will be stored
    private static final String TRANSACTION_LOG_DIR = "transaction_logs";
    // String constant for the file extension used for all transaction log files (.txn format)
    private static final String LOG_FILE_EXTENSION = ".txn";
    
    /**
     * Constructor: Creates the transaction logs directory if it doesn't exist
     * This ensures the directory is ready for saving transaction files
     */
    public TransactionLog() {
        // Create a File object representing the transaction logs directory
        File dir = new File(TRANSACTION_LOG_DIR);
        // Check if the directory doesn't already exist
        if (!dir.exists()) {
            // Create the directory (mkdir creates the directory)
            dir.mkdir();
        }
    }
    
    /**
     * Feature Request 10.2: Save customer transaction history to persistent file storage
     * Writes all transactions for a customer to a file in the transaction_logs directory
     * @param customerName - The name of the customer (used as filename)
     * @param transactions - List of transaction strings to save to file
     */
    public void saveTransactions(String customerName, List<String> transactions) {
        // Use try-catch to handle any IOException that might occur during file operations
        try {
            // Construct the full filename path: "transaction_logs/customerName.txn"
            String filename = TRANSACTION_LOG_DIR + File.separator + customerName + LOG_FILE_EXTENSION;
            // Create FileWriter to write character data to the file
            // Try-with-resources ensures FileWriter is closed automatically
            try (FileWriter fw = new FileWriter(filename);
                 // Wrap FileWriter with BufferedWriter for better performance (buffered writing)
                 BufferedWriter bw = new BufferedWriter(fw)) {
                // Loop through each transaction string in the transactions list
                for (String transaction : transactions) {
                    // Write the transaction string to the file
                    bw.write(transaction);
                    // Write a newline character after each transaction for readability
                    bw.newLine();
                }
            }
            // After successful save, print confirmation message to console
            System.out.println("[TRANSACTION LOG] Saved " + transactions.size() + 
                             " transactions for " + customerName);
        } catch (IOException e) {
            // If an IOException occurred during file write, print error message to standard error
            System.err.println("Error saving transaction log: " + e.getMessage());
        }
    }
    
    /**
     * Feature Request 10.2: Load customer transaction history from persistent file storage
     * Reads all transactions for a customer from their file (if file exists)
     * @param customerName - The name of the customer (used to find their transaction file)
     * @return List<String> - List of transaction strings loaded from file, or empty list if no file exists
     */
    public List<String> loadTransactions(String customerName) {
        // Create an empty ArrayList to store the loaded transactions
        List<String> transactions = new ArrayList<>();
        // Use try-catch to handle any IOException that might occur during file operations
        try {
            // Construct the full filename path: "transaction_logs/customerName.txn"
            String filename = TRANSACTION_LOG_DIR + File.separator + customerName + LOG_FILE_EXTENSION;
            // Create a File object representing the transaction file for this customer
            File file = new File(filename);
            // Check if the transaction file does not exist
            if (!file.exists()) {
                // Return empty list - customer may be new with no previous transactions
                return transactions;
            }
            
            // Create FileReader to read character data from the file
            // Try-with-resources ensures FileReader/BufferedReader are closed automatically
            try (FileReader fr = new FileReader(filename);
                 // Wrap FileReader with BufferedReader for better performance (buffered reading)
                 BufferedReader br = new BufferedReader(fr)) {
                // Read lines from the file in a loop
                String line;
                // Read the next line from file (readLine() returns null at end of file)
                while ((line = br.readLine()) != null) {
                    // Check if the current line is not empty (trim removes whitespace)
                    if (!line.trim().isEmpty()) {
                        // Add the non-empty transaction line to the transactions list
                        transactions.add(line);
                    }
                }
            }
            // After successful load, print confirmation message to console
            System.out.println("[TRANSACTION LOG] Loaded " + transactions.size() + 
                             " transactions for " + customerName);
        } catch (IOException e) {
            // If an IOException occurred during file read, print error message to standard error
            System.err.println("Error loading transaction log: " + e.getMessage());
        }
        // Return the list of loaded transactions (empty if no file existed or error occurred)
        return transactions;
    }
    
    /**
     * Retrieves all transaction log files currently stored in the system
     * Useful for administrative operations (e.g., viewing all customer transaction files)
     * @return List<File> - List of File objects for all .txn files in transaction_logs directory
     */
    public List<File> getAllTransactionFiles() {
        // Create an empty ArrayList to store all transaction File objects found
        List<File> files = new ArrayList<>();
        // Create a File object representing the transaction logs directory
        File dir = new File(TRANSACTION_LOG_DIR);
        // Check if the directory exists and is actually a directory (not a file)
        if (dir.exists() && dir.isDirectory()) {
            // List all files in the directory that end with the LOG_FILE_EXTENSION (.txn)
            // The lambda (d, name) -> name.endsWith(LOG_FILE_EXTENSION) filters files
            File[] fileList = dir.listFiles((d, name) -> name.endsWith(LOG_FILE_EXTENSION));
            // Check if fileList is not null (listFiles can return null if directory access denied)
            if (fileList != null) {
                // Convert the File[] array to a List and add all files to the files ArrayList
                files.addAll(Arrays.asList(fileList));
            }
        }
        // Return the list of all transaction log files found
        return files;
    }
    
    /**
     * Clears/deletes the transaction log file for a specific customer
     * Useful for testing purposes or removing customer data
     * @param customerName - The name of the customer whose transaction file should be deleted
     */
    public void clearTransactions(String customerName) {
        // Construct the full filename path: "transaction_logs/customerName.txn"
        String filename = TRANSACTION_LOG_DIR + File.separator + customerName + LOG_FILE_EXTENSION;
        // Create a File object representing the transaction file for this customer
        File file = new File(filename);
        // Check if the file exists before attempting to delete it
        if (file.exists()) {
            // Delete the file (removes it from the file system)
            file.delete();
        }
    }
}