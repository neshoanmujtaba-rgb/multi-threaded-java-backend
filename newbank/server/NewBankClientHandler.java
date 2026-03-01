// Empty line at start of original file - preserved for compatibility

// Define the package for this class - handles individual client connections
package newbank.server;

// Import BufferedReader for efficiently reading lines of text from socket input
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * NewBankClientHandler handles communication with a single connected client
 * Extends Thread because each client connection runs in its own separate thread
 * Manages login/authentication and processes banking commands from the client
 */
public class NewBankClientHandler extends Thread{
	
	// Private reference to the NewBank singleton instance (shared with all handlers)
	private NewBank bank;
	// Private BufferedReader to read command lines sent by the connected client
	private BufferedReader in;
	// Private PrintWriter to send response messages back to the connected client
	private PrintWriter out;

	// Track failed login attempts to implement security lockout mechanism
	private int failedAttempts = 0;
	// Static constant defining the maximum login attempts allowed before lockout
	private static final int MAX_ATTEMPTS = 3;
	// Static constant for lockout duration in milliseconds: 30 seconds (30 * 1000 ms)
	private static final long LOCKOUT_TIME = 30 * 1000;
	
	/**
	 * Constructor: Sets up communication streams for this client connection
	 * @param s - The Socket object representing the connected client
	 * @throws IOException - If unable to set up input/output streams
	 */
	public NewBankClientHandler(Socket s) throws IOException {
		// Get the singleton NewBank instance (same instance for all client handlers)
		bank = NewBank.getBank();
		// Create BufferedReader to read client input from socket's input stream
		// InputStreamReader converts raw bytes from socket to readable characters
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		// Create PrintWriter to send server responses back to client through socket
		// true parameter enables auto-flush (automatically sends data without buffering)
		out = new PrintWriter(s.getOutputStream(), true);
	}
	
	/**
	 * Run method: Main client handling loop - handles login and command processing
	 * This runs in its own thread, separate from other client handlers
	 */
	public void run() {
		// Infinite loop for client session (breaks when logout occurs)
		// Keep getting requests from the client and process all of them
		try {

			out.println("Enter Username");

			String userName = in.readLine();

			out.println("Enter Password");

			String password = in.readLine();

			out.println("Checking Details...");
			// Authenticate user and get customer ID token from bank for use in subsequent requests
			// checkLogInDetails returns a CustomerID if credentials valid, null if invalid
			CustomerID customer = bank.checkLogInDetails(userName, password);
			
			// Check if the user is authenticated (customer != null means valid login)
			if(customer != null) {
				// Log in successful - tell user we're ready for commands
				out.println("Log In Successful. What do you want to do?");
				// Process client commands in an infinite loop until logout
				while(true) {
					// Read one command line from the client
					String request = in.readLine();
					// Print to server console who sent this request (for debugging)
					System.out.println("Request from " + customer.getKey());
					// Send request to NewBank to process and get response
					String response = bank.processRequest(customer, request);
					// Send the response back to the client
					out.println(response);
				}
        
			}

			// (NOTE: Unreachable code below - appears to be duplicate from merge conflict)
			// Log in successful - ready to process commands
			out.println("Log In Successful. What do you want to do?");
			// Process commands in infinite loop
			while(true) {
				// Read the next command from client
				String request = in.readLine();
				// Check if client closed connection (readLine returns null when connection closes)
				if (request == null) break;
				
				// Print to server console who sent this request (for debugging)
				System.out.println("Request from " + customer.getKey());
				// Send request to NewBank to process and get response
				String response = bank.processRequest(customer, request);
				// Send the response back to the client
				out.println(response);
			
                // Check if response is logout command
                if ("SUCCESS: Logged out".equals(response)) {
                    // Print to server console that connection is closing
                    System.out.println("Closing connection for " + customer.getKey());
                    // Break out of the command processing loop to end this client session
                    break;
                }
            }
			
		} catch (IOException e) {
			// If IOException occurs (e.g., connection lost), print error trace for debugging
			e.printStackTrace();
		}
		finally {
			// Finally block executes when client handler ends, to clean up resources
			try {
				// Close the input stream from the client
				in.close();
				// Close the output stream to the client
				out.close();
			} catch (IOException e) {
				// If IOException occurs during closing, print error for debugging
				e.printStackTrace();
				// Interrupt the current client handler thread to ensure clean shutdown
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Displays available banking commands to the customer
	 * Currently shows help text but doesn't send it (may be incomplete implementation)
	 */
	private void showAvailableCommands() {
		// Send "Available Commands:" header to client
		out.println("Available Commands:");
		// Send separator line for formatting
		out.println("-------------------");
		// Describe the SHOWMYACCOUNTS command
		out.println("SHOWMYACCOUNTS - View all your accounts and balances");
		// Describe the NEWACCOUNT command with example
		out.println("NEWACCOUNT <Name> - Create a new account (e.g., NEWACCOUNT Savings)");
		// Describe the MOVE command with example
		out.println("MOVE <Amount> <From> <To> - Transfer money between your accounts");
		// Continue MOVE description
		out.println("                             (e.g., MOVE 100 Main Savings)");
		// Describe the PAY command with example
		out.println("PAY <Person> <Amount> - Pay money to another user");
		// Continue PAY description
		out.println("                        (e.g., PAY John 100)");
	}

	/**
	 * Login attempt handler with account lockout mechanism
	 * Implements security feature: lock account after MAX_ATTEMPTS failed login attempts
	 * Returns CustomerID if successful login, null if login fails
	 * Note: This method appears unused - login happens in run() method above
	 * @return CustomerID - Valid customer ID if login succeeds, null if login fails
	 * @throws IOException - If I/O error occurs during login process
	 */
	private CustomerID attemptLogin() throws IOException {
		// Infinite loop - keeps trying until:
		// - Successful login (returns CustomerID)
		// - Or continues after lockout period expires and resets
		while (true) {
			// Check if user has exceeded maximum login attempts
			if (failedAttempts >= MAX_ATTEMPTS) {
				
				// User is locked out - must wait before trying again
				try {
					// Tell user to wait
					out.println("Account locked due to too many failed attempts. Waiting 30 seconds...");
					// Sleep for lockout duration (30 seconds converted to milliseconds)
					Thread.sleep(LOCKOUT_TIME);
				} catch (InterruptedException e) {
					// If thread is interrupted during sleep, propagate the interruption
					Thread.currentThread().interrupt();
					// Return null to indicate login failed
					return null;
				}
				
				// After lockout expires, reset the counter and allow trying again
				failedAttempts = 0;
				// Tell user lockout period has ended and they can try again
				out.println("Lockout period expired. You may try again.");
			}

			// Ask user for their username
			out.println("Enter Username");
			// Read the username from client
			String userName = in.readLine();
			// Ask user for their password
			out.println("Enter Password");
			// Read the password from client
			String password = in.readLine();
			// Tell user credentials are being checked
			out.println("Checking Details...");
			// Call NewBank to authenticate user with provided credentials
			// Returns CustomerID if valid, null if invalid
			CustomerID customer = bank.checkLogInDetails(userName, password);
			
			// Check if authentication was successful
			if (customer != null) {
				// Reset failed attempts counter since login succeeded
				failedAttempts = 0;
				// Return the valid CustomerID for use in subsequent commands
				return customer;
			} else {
				// Login failed - increment number of failed attempts
				failedAttempts++;
				// Calculate remaining attempts before lockout
				int remaining = MAX_ATTEMPTS - failedAttempts;
				
				// Check if there are still attempts remaining
				if (remaining > 0) {
					// Tell user login failed and show attempts remaining
					out.println("FAIL: Invalid username or password.");
					out.println("Attempts remaining: " + remaining);
				} else {
					// User has exceeded maximum attempts - account is locked
					out.println("FAIL: Too many failed attempts. Account locked for 30 seconds.");
					out.println("Please wait...");
				}
			}
		}
	}
}
