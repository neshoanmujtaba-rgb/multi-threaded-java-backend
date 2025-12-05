package newbank.server;

import java.io.*;
import java.util.*;

public class TransactionLog {
    private static final String TRANSACTION_LOG_DIR = "transaction_logs";
    private static final String LOG_FILE_EXTENSION = ".txn";
    
    public TransactionLog() {
        // Create transaction logs directory if it doesn't exist
        File dir = new File(TRANSACTION_LOG_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }
    
    /**
     * FR10.2: Save customer transaction history to persistent file storage
     */
    public void saveTransactions(String customerName, List<String> transactions) {
        try {
            String filename = TRANSACTION_LOG_DIR + File.separator + customerName + LOG_FILE_EXTENSION;
            try (FileWriter fw = new FileWriter(filename);
                 BufferedWriter bw = new BufferedWriter(fw)) {
                for (String transaction : transactions) {
                    bw.write(transaction);
                    bw.newLine();
                }
            }
            System.out.println("[TRANSACTION LOG] Saved " + transactions.size() + 
                             " transactions for " + customerName);
        } catch (IOException e) {
            System.err.println("Error saving transaction log: " + e.getMessage());
        }
    }
    
    /**
     * FR10.2: Load customer transaction history from persistent file storage
     */
    public List<String> loadTransactions(String customerName) {
        List<String> transactions = new ArrayList<>();
        try {
            String filename = TRANSACTION_LOG_DIR + File.separator + customerName + LOG_FILE_EXTENSION;
            File file = new File(filename);
            if (!file.exists()) {
                return transactions; // Return empty list if no file exists yet
            }
            
            try (FileReader fr = new FileReader(filename);
                 BufferedReader br = new BufferedReader(fr)) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        transactions.add(line);
                    }
                }
            }
            System.out.println("[TRANSACTION LOG] Loaded " + transactions.size() + 
                             " transactions for " + customerName);
        } catch (IOException e) {
            System.err.println("Error loading transaction log: " + e.getMessage());
        }
        return transactions;
    }
    
    /**
     * Get all transaction log files in the system
     */
    public List<File> getAllTransactionFiles() {
        List<File> files = new ArrayList<>();
        File dir = new File(TRANSACTION_LOG_DIR);
        if (dir.exists() && dir.isDirectory()) {
            File[] fileList = dir.listFiles((d, name) -> name.endsWith(LOG_FILE_EXTENSION));
            if (fileList != null) {
                files.addAll(Arrays.asList(fileList));
            }
        }
        return files;
    }
    
    /**
     * Clear transaction log for a specific customer (useful for testing)
     */
    public void clearTransactions(String customerName) {
        String filename = TRANSACTION_LOG_DIR + File.separator + customerName + LOG_FILE_EXTENSION;
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }
}