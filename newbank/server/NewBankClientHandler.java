
package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler extends Thread{
	
	private NewBank bank;
	private BufferedReader in;
	private PrintWriter out;

	// lockout tracking variables
	private int failedAttempts = 0;
	private static final int MAX_ATTEMPTS = 3;
	private static final long LOCKOUT_TIME = 30 * 1000; // should be 30 x 1000ms = 30 seconds
	
	
	public NewBankClientHandler(Socket s) throws IOException {
		bank = NewBank.getBank();
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);
	}
	
	public void run() {
		// keep getting requests from the client and processing them
		try {
<<<<<<< HEAD
			// ask for user name
			out.println("Enter Username");
			String userName = in.readLine();
			// ask for password
			out.println("Enter Password");
			String password = in.readLine();
			out.println("Checking Details...");
			// authenticate user and get customer ID token from bank for use in subsequent requests
			CustomerID customer = bank.checkLogInDetails(userName, password);
			// if the user is authenticated then get requests from the user and process them 
			if(customer != null) {
				out.println("Log In Successful. What do you want to do?");
				while(true) {
					String request = in.readLine();
					System.out.println("Request from " + customer.getKey());
					String response = bank.processRequest(customer, request);
					out.println(response);
				}
=======

			// session attempting login
			CustomerID customer = attemptLogin();
			
			// If client disconnected during login, exit 
			if (customer == null) {
				return;
>>>>>>> main
			}

			// Login successful - ready to process commands
			out.println("Log In Successful. What do you want to do?");
			while(true) {
				String request = in.readLine();
				if (request == null) break;
				
				System.out.println("Request from " + customer.getKey());
				String response = bank.processRequest(customer, request);
				out.println(response);
			
                if ("SUCCESS: Logged out".equals(response)) {
                    System.out.println("Closing connection for " + customer.getKey());
                    break;
                }
            }
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}

    /** login attempt handler with lockout mechanism
     Returns CustomerID if successful */
    private CustomerID attemptLogin() throws IOException {
        while (true) {
        // Check if locked out
        if (failedAttempts >= MAX_ATTEMPTS) {
            // Wait 30 seconds
            try {
                Thread.sleep(LOCKOUT_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            failedAttempts = 0;
            out.println("Lockout period expired. You may try again.");
        }
            // ask for user name
            out.println("Enter Username");
            String userName = in.readLine();
            // ask for password
            out.println("Enter Password");
            String password = in.readLine();
            out.println("Checking Details...");
            // authenticate user and get customer ID token from bank for use in subsequent requests
            CustomerID customer = bank.checkLogInDetails(userName, password);
            // if the user is authenticated then get requests from the user and process them
            if (customer != null) {
                // Success - reset counter
                failedAttempts = 0;
                return customer;
            } else {
                // Failed - increment counter
                failedAttempts++;
                int remaining = MAX_ATTEMPTS - failedAttempts;
                if (remaining > 0) {
                    out.println("FAIL: Invalid username or password.");
                    out.println("Attempts remaining: " + remaining);
                } else {
                    out.println("FAIL: Too many failed attempts. Account locked for 30 seconds.");
                    out.println("Please wait...");
                }
            }
        }
    }
}
