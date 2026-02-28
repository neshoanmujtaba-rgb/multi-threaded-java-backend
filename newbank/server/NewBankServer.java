// Define the package for this class - central banking server
package newbank.server;

// Import IOException for handling network errors
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * NewBankServer is the main banking server that listens for client connections
 * Extends Thread because server runs in its own thread
 * For each incoming client connection, creates a NewBankClientHandler thread to serve that client
 * Multi-threaded design allows server to handle multiple concurrent client connections
 */
public class NewBankServer extends Thread{
	
	// Private ServerSocket that listens for incoming client connections on a specific port
	private ServerSocket server; //declaring a variable (a reference of type) ServerSocket not an actual object just empty container to hold ServerSpclet object
	
	/**
	 * Constructor: Creates a ServerSocket listening on the specified port
	 * @param port - The port number the server will listen on (e.g., 14002)
	 * @throws IOException - If the ServerSocket cannot be created on this port
	 */
	public NewBankServer(int port) throws IOException {
		// Create a ServerSocket that listens on the specified port for incoming connections
		server = new ServerSocket(port); //this is a LISTENING socket 
	}
	
	/*
	 * Run method: Main server loop - executed when server thread is started
	 * Continuously accepts client connections and spawns handler threads for each
	 */
	public void run() {
		// Print message to console indicating server is ready to accept connections
		System.out.println("New Bank Server listening on " + server.getLocalPort());
		
		// Use try-catch to handle potential IOException during server operations
		try {
			// Infinite loop - server runs continuously until interrupted or error occurs
			while(true) {
				// Accept (blocking call) - wait for a client to connect and return Socket for that client
				Socket s = server.accept();
				// Create a new NewBankClientHandler thread to handle this specific client's requests
				NewBankClientHandler clientHandler = new NewBankClientHandler(s);
				// Start the client handler thread which will handle all requests from this client
				clientHandler.start();
			}
		} catch (IOException e) {
			// If IOException occurs in the server loop, print error details for debugging
			e.printStackTrace();
		}
		finally {
			// Finally block executes regardless of whether exception occurred
			// Used to ensure proper cleanup of resources
			try {
				// Close the ServerSocket to stop accepting new connections
				server.close();
			} catch (IOException e) {
				// If IOException occurs during closing, print error for debugging
				e.printStackTrace();
				// Interrupt the current (server) thread to ensure clean shutdown
				Thread.currentThread().interrupt();
			}
		}
	}
	
	/**
	 * Main method: Entry point for starting the banking server
	 * Creates instance of NewBankServer on port 14002 and starts it
	 * @param args - Command line arguments (not used in this implementation)
	 * @throws IOException - If server cannot bind to the specified port
	 */
	public static void main(String[] args) throws IOException {
		// Create a new NewBankServer instance listening on port 14002 (standard for this bank)
		new NewBankServer(14002).start();
	}
}
